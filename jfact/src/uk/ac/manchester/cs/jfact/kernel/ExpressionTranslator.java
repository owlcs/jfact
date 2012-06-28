package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeEntry;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.datatypes.LiteralEntry;
import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataExactCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataForall;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataMaxCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataMinCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataValue;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptNot;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExactCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectForall;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectMaxCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectMinCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectSelf;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectValue;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptOneOf;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptOr;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptTop;
import uk.ac.manchester.cs.jfact.kernel.dl.DataAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.DataBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.DataNot;
import uk.ac.manchester.cs.jfact.kernel.dl.DataOneOf;
import uk.ac.manchester.cs.jfact.kernel.dl.DataOr;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.DataTop;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionInto;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NAryExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

public final class ExpressionTranslator implements DLExpressionVisitorEx<DLTree> {
    /** TBox to get access to the named entities */
    private final TBox tbox;
    /// signature of non-trivial entities; used in semantic locality checkers only
    TSignature sig;

    /// @return true iff ENTRY is not in signature
    boolean nc(final NamedEntity entity) {
        return sig != null && !sig.containsNamedEntity(entity);
    }

    /// set internal signature to a given signature S
    void setSignature(final TSignature s) {
        sig = s;
    }

    ExpressionTranslator(final TBox kb) {
        tbox = kb;
    }

    // concept expressions
    public DLTree visit(final ConceptTop expr) {
        return DLTreeFactory.createTop();
    }

    public DLTree visit(final ConceptBottom expr) {
        return DLTreeFactory.createBottom();
    }

    public DLTree visit(final ConceptName expr) {
        if (nc(expr)) {
            return sig.topCLocal() ? DLTreeFactory.createTop() : DLTreeFactory
                    .createBottom();
        } else {
            NamedEntry entry = expr.getEntry();
            if (entry == null) {
                entry = matchEntry(tbox.getConcept(expr.getName()), expr);
            }
            //tree = createEntry(CNAME,entry);
            return DLTreeFactory.buildTree(new Lexeme(CNAME, entry));
        }
    }

    /// create DLTree of given TAG and named ENTRY; set the entry's ENTITY if necessary
    NamedEntry matchEntry(final NamedEntry entry, final NamedEntity entity) {
        entry.setEntity(entity);
        entity.setEntry(entry);
        return entry;
    }

    //	private Lexeme setEntry(Token cname, NamedEntry expr, NamedEntity e) {
    //		if (expr.getEntity() == null) {
    //			expr.setEntity(e);
    //		}
    //		return new Lexeme(cname, expr);
    //	}
    public DLTree visit(final ConceptNot expr) {
        return DLTreeFactory.createSNFNot(expr.getConcept().accept(this));
    }

    private List<DLTree> visitArgs(final NAryExpression<? extends Expression> expr) {
        List<DLTree> args = new ArrayList<DLTree>();
        List<? extends Expression> list = expr.getArguments();
        for (int i = 0; i < list.size(); i++) {
            args.add(list.get(i).accept(this));
        }
        return args;
    }

    public DLTree visit(final ConceptAnd expr) {
        return DLTreeFactory.createSNFAnd(visitArgs(expr));
    }

    public DLTree visit(final ConceptOr expr) {
        return DLTreeFactory.createSNFOr(visitArgs(expr));
    }

    public DLTree visit(final ConceptOneOf expr) {
        return DLTreeFactory.createSNFOr(visitArgs(expr));
    }

    public DLTree visit(final ConceptObjectSelf expr) {
        DLTree r = expr.getOR().accept(this);
        if (r.elem().getNE().isBottom()) {
            return DLTreeFactory.createBottom();
        }
        DLTree toReturn = DLTreeFactory.createSNFSelf(DLTreeFactory.buildTree(new Lexeme(
                Token.SELF), r));
        return toReturn;
    }

    public DLTree visit(final ConceptObjectValue expr) {
        return DLTreeFactory.createSNFExists(expr.getOR().accept(this), expr.getI()
                .accept(this));
    }

    public DLTree visit(final ConceptObjectExists expr) {
        return DLTreeFactory.createSNFExists(expr.getOR().accept(this), expr.getConcept()
                .accept(this));
    }

    public DLTree visit(final ConceptObjectForall expr) {
        return DLTreeFactory.createSNFForall(expr.getOR().accept(this), expr.getConcept()
                .accept(this));
    }

    public DLTree visit(final ConceptObjectMinCardinality expr) {
        return DLTreeFactory.createSNFGE(expr.getCardinality(),
                expr.getOR().accept(this), expr.getConcept().accept(this));
    }

    public DLTree visit(final ConceptObjectMaxCardinality expr) {
        return DLTreeFactory.createSNFLE(expr.getCardinality(),
                expr.getOR().accept(this), expr.getConcept().accept(this));
    }

    public DLTree visit(final ConceptObjectExactCardinality expr) {
        DLTree le = DLTreeFactory.createSNFLE(expr.getCardinality(),
                expr.getOR().accept(this).copy(), expr.getConcept().accept(this).copy());
        DLTree ge = DLTreeFactory.createSNFGE(expr.getCardinality(),
                expr.getOR().accept(this).copy(), expr.getConcept().accept(this).copy());
        return DLTreeFactory.createSNFAnd(ge, le);
    }

    public DLTree visit(final ConceptDataValue expr) {
        return DLTreeFactory.createSNFExists(expr.getDataRoleExpression().accept(this),
                expr.getExpr().accept(this));
    }

    public DLTree visit(final ConceptDataExists expr) {
        return DLTreeFactory.createSNFExists(expr.getDataRoleExpression().accept(this),
                expr.getExpr().accept(this));
    }

    public DLTree visit(final ConceptDataForall expr) {
        return DLTreeFactory.createSNFForall(expr.getDataRoleExpression().accept(this),
                expr.getExpr().accept(this));
    }

    public DLTree visit(final ConceptDataMinCardinality expr) {
        return DLTreeFactory.createSNFGE(expr.getCardinality(), expr
                .getDataRoleExpression().accept(this), expr.getExpr().accept(this));
    }

    public DLTree visit(final ConceptDataMaxCardinality expr) {
        return DLTreeFactory.createSNFLE(expr.getCardinality(), expr
                .getDataRoleExpression().accept(this), expr.getExpr().accept(this));
    }

    public DLTree visit(final ConceptDataExactCardinality expr) {
        DLTree le = DLTreeFactory.createSNFLE(expr.getCardinality(), expr
                .getDataRoleExpression().accept(this).copy(), expr.getExpr().accept(this)
                .copy());
        DLTree ge = DLTreeFactory.createSNFGE(expr.getCardinality(), expr
                .getDataRoleExpression().accept(this).copy(), expr.getExpr().accept(this)
                .copy());
        return DLTreeFactory.createSNFAnd(ge, le);
    }

    // individual expressions
    public DLTree visit(final IndividualName expr) {
        NamedEntry entry = expr.getEntry();
        if (entry == null) {
            entry = matchEntry(tbox.getIndividual(expr.getName()), expr);
        }
        //tree = createEntry(INAME,entry);
        return DLTreeFactory.buildTree(new Lexeme(INAME, entry));
    }

    // object role expressions
    public DLTree visit(final ObjectRoleTop expr) {
        throw new ReasonerInternalException(
                "Unsupported expression 'top object role' in transformation");
    }

    public DLTree visit(final ObjectRoleBottom expr) {
        throw new ReasonerInternalException(
                "Unsupported expression 'bottom object role' in transformation");
    }

    public DLTree visit(final ObjectRoleName expr) {
        RoleMaster RM = tbox.getORM();
        NamedEntry role;
        if (nc(expr)) {
            role = sig.topRLocal() ? RM.getTopRole() : RM.getBotRole();
        } else {
            role = expr.getEntry();
            if (role == null) {
                role = matchEntry(RM.ensureRoleName(expr.getName()), expr);
            }
        }
        //	tree = createEntry(RNAME,role);
        return DLTreeFactory.buildTree(new Lexeme(RNAME, role));
    }

    public DLTree visit(final ObjectRoleInverse expr) {
        return DLTreeFactory.createInverse(expr.getOR().accept(this));
    }

    public DLTree visit(final ObjectRoleChain expr) {
        List<ObjectRoleExpression> l = new ArrayList<ObjectRoleExpression>(
                expr.getArguments());
        if (l.size() == 0) {
            throw new ReasonerInternalException(
                    "Unsupported expression 'empty role chain' in transformation");
        }
        DLTree acc = l.get(0).accept(this);
        for (int i = 1; i < l.size(); i++) {
            //TODO this is still a binary tree while it should be n-ary with enforced order
            acc = DLTreeFactory.buildTree(new Lexeme(RCOMPOSITION), acc,
                    l.get(i).accept(this));
        }
        return acc;
    }

    public DLTree visit(final ObjectRoleProjectionFrom expr) {
        return DLTreeFactory.buildTree(new Lexeme(PROJFROM), expr.getOR().accept(this),
                expr.getConcept().accept(this));
    }

    public DLTree visit(final ObjectRoleProjectionInto expr) {
        return DLTreeFactory.buildTree(new Lexeme(PROJINTO), expr.getOR().accept(this),
                expr.getConcept().accept(this));
    }

    // data role expressions
    public DLTree visit(final DataRoleTop expr) {
        throw new ReasonerInternalException(
                "Unsupported expression 'top data role' in transformation");
    }

    public DLTree visit(final DataRoleBottom expr) {
        throw new ReasonerInternalException(
                "Unsupported expression 'bottom data role' in transformation");
    }

    public DLTree visit(final DataRoleName expr) {
        RoleMaster RM = tbox.getDRM();
        NamedEntry role;
        if (nc(expr)) {
            role = sig.topRLocal() ? RM.getTopRole() : RM.getBotRole();
        } else
        //role = RM.ensureRoleName(expr.getName());
        {
            role = expr.getEntry();
            if (role == null) {
                role = matchEntry(RM.ensureRoleName(expr.getName()), expr);
            }
        }
        //tree = createEntry(DNAME,role);
        return DLTreeFactory.buildTree(new Lexeme(DNAME, role));
    }

    // data expressions
    public DLTree visit(final DataTop expr) {
        return DLTreeFactory.createTop();
    }

    public DLTree visit(final DataBottom expr) {
        return DLTreeFactory.createBottom();
    }

    public DLTree visit(final Datatype<?> expr) {
        DatatypeEntry entry = new DatatypeEntry(expr);
        return DLTreeFactory.wrap(entry);
    }

    public DLTree visit(final Literal<?> expr) {
        // process type
        LiteralEntry entry = new LiteralEntry(expr.value());
        entry.setLiteral(expr);
        return DLTreeFactory.wrap(entry);
    }

    public DLTree visit(final DataNot expr) {
        return DLTreeFactory.createSNFNot(expr.getExpr().accept(this));
    }

    public DLTree visit(final DataAnd expr) {
        return DLTreeFactory.createSNFAnd(visitArgs(expr));
    }

    public DLTree visit(final DataOr expr) {
        return DLTreeFactory.createSNFOr(visitArgs(expr));
    }

    public DLTree visit(final DataOneOf expr) {
        return DLTreeFactory.createSNFOr(visitArgs(expr));
    }
}
