package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeEntry;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeExpression;
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
import conformance.Original;
import conformance.PortedFrom;

/** expression translator */
@PortedFrom(file = "tExpressionTranslator.h", name = "TExpressionTranslator")
public class ExpressionTranslator implements DLExpressionVisitorEx<DLTree>,
        Serializable {

    private static final long serialVersionUID = 11000L;
    /** TBox to get access to the named entities */
    @PortedFrom(file = "tExpressionTranslator.h", name = "kb")
    private final TBox tbox;
    /**
     * signature of non-trivial entities; used in semantic locality checkers
     * only
     */
    @PortedFrom(file = "tExpressionTranslator.h", name = "sig")
    private TSignature sig;

    /**
     * @param entity
     *        entity
     * @return true iff ENTRY is not in signature
     */
    @PortedFrom(file = "tExpressionTranslator.h", name = "nc")
    private boolean nc(NamedEntity entity) {
        return sig != null && !sig.containsNamedEntity(entity);
    }

    /**
     * set internal signature to a given signature S
     * 
     * @param s
     *        signature
     */
    @PortedFrom(file = "tExpressionTranslator.h", name = "setSignature")
    public void setSignature(TSignature s) {
        sig = s;
    }

    /**
     * @param kb
     *        kb
     */
    public ExpressionTranslator(TBox kb) {
        tbox = kb;
    }

    // concept expressions
    @Override
    public DLTree visit(ConceptTop expr) {
        return DLTreeFactory.createTop();
    }

    @Override
    public DLTree visit(ConceptBottom expr) {
        return DLTreeFactory.createBottom();
    }

    @Override
    public DLTree visit(ConceptName expr) {
        if (nc(expr)) {
            return sig.topCLocal() ? DLTreeFactory.createTop() : DLTreeFactory
                    .createBottom();
        } else {
            NamedEntry entry = expr.getEntry();
            if (entry == null) {
                entry = matchEntry(tbox.getConcept(expr.getName()), expr);
            }
            return DLTreeFactory.buildTree(new Lexeme(CNAME, entry));
        }
    }

    /**
     * create DLTree of given TAG and named ENTRY; set the entry's ENTITY if
     * 
     * @param entry
     *        entry
     * @param entity
     *        entity
     * @return updated named entry
     */
    // necessary
    @PortedFrom(file = "tExpressionTranslator.h", name = "matchEntry")
    private NamedEntry matchEntry(NamedEntry entry, NamedEntity entity) {
        entry.setEntity(entity);
        entity.setEntry(entry);
        return entry;
    }

    @Override
    public DLTree visit(ConceptNot expr) {
        return DLTreeFactory.createSNFNot(expr.getConcept().accept(this));
    }

    @Original
    private List<DLTree> visitArgs(NAryExpression<? extends Expression> expr) {
        List<DLTree> args = new ArrayList<DLTree>();
        List<? extends Expression> list = expr.getArguments();
        for (int i = 0; i < list.size(); i++) {
            args.add(list.get(i).accept(this));
        }
        return args;
    }

    @Override
    public DLTree visit(ConceptAnd expr) {
        return DLTreeFactory.createSNFAnd(visitArgs(expr));
    }

    @Override
    public DLTree visit(ConceptOr expr) {
        return DLTreeFactory.createSNFOr(visitArgs(expr));
    }

    @Override
    public DLTree visit(ConceptOneOf<?> expr) {
        return DLTreeFactory.createSNFOr(visitArgs(expr));
    }

    @Override
    public DLTree visit(ConceptObjectSelf expr) {
        DLTree r = expr.getOR().accept(this);
        return DLTreeFactory.createSNFSelf(r);
    }

    @Override
    public DLTree visit(ConceptObjectValue expr) {
        return DLTreeFactory.createSNFExists(expr.getOR().accept(this), expr
                .getIndividual().accept(this));
    }

    @Override
    public DLTree visit(ConceptObjectExists expr) {
        return DLTreeFactory.createSNFExists(expr.getOR().accept(this), expr
                .getConcept().accept(this));
    }

    @Override
    public DLTree visit(ConceptObjectForall expr) {
        return DLTreeFactory.createSNFForall(expr.getOR().accept(this), expr
                .getConcept().accept(this));
    }

    @Override
    public DLTree visit(ConceptObjectMinCardinality expr) {
        return DLTreeFactory.createSNFGE(expr.getCardinality(), expr.getOR()
                .accept(this), expr.getConcept().accept(this));
    }

    @Override
    public DLTree visit(ConceptObjectMaxCardinality expr) {
        return DLTreeFactory.createSNFLE(expr.getCardinality(), expr.getOR()
                .accept(this), expr.getConcept().accept(this));
    }

    @Override
    public DLTree visit(ConceptObjectExactCardinality expr) {
        DLTree le = DLTreeFactory.createSNFLE(expr.getCardinality(), expr
                .getOR().accept(this).copy(), expr.getConcept().accept(this)
                .copy());
        DLTree ge = DLTreeFactory.createSNFGE(expr.getCardinality(), expr
                .getOR().accept(this).copy(), expr.getConcept().accept(this)
                .copy());
        return DLTreeFactory.createSNFAnd(ge, le);
    }

    @Override
    public DLTree visit(ConceptDataValue expr) {
        return DLTreeFactory.createSNFExists(expr.getDataRoleExpression()
                .accept(this), expr.getExpr().accept(this));
    }

    @Override
    public DLTree visit(ConceptDataExists expr) {
        return DLTreeFactory.createSNFExists(expr.getDataRoleExpression()
                .accept(this), expr.getExpr().accept(this));
    }

    @Override
    public DLTree visit(ConceptDataForall expr) {
        return DLTreeFactory.createSNFForall(expr.getDataRoleExpression()
                .accept(this), expr.getExpr().accept(this));
    }

    @Override
    public DLTree visit(ConceptDataMinCardinality expr) {
        return DLTreeFactory.createSNFGE(expr.getCardinality(), expr
                .getDataRoleExpression().accept(this),
                expr.getExpr().accept(this));
    }

    @Override
    public DLTree visit(ConceptDataMaxCardinality expr) {
        return DLTreeFactory.createSNFLE(expr.getCardinality(), expr
                .getDataRoleExpression().accept(this),
                expr.getExpr().accept(this));
    }

    @Override
    public DLTree visit(ConceptDataExactCardinality expr) {
        DLTree le = DLTreeFactory.createSNFLE(expr.getCardinality(), expr
                .getDataRoleExpression().accept(this).copy(), expr.getExpr()
                .accept(this).copy());
        DLTree ge = DLTreeFactory.createSNFGE(expr.getCardinality(), expr
                .getDataRoleExpression().accept(this).copy(), expr.getExpr()
                .accept(this).copy());
        return DLTreeFactory.createSNFAnd(ge, le);
    }

    // individual expressions
    @Override
    public DLTree visit(IndividualName expr) {
        NamedEntry entry = expr.getEntry();
        if (entry == null) {
            entry = matchEntry(tbox.getIndividual(expr.getName()), expr);
        }
        return DLTreeFactory.buildTree(new Lexeme(INAME, entry));
    }

    // object role expressions
    @Override
    public DLTree visit(ObjectRoleTop expr) {
        throw new ReasonerInternalException(
                "Unsupported expression 'top object role' in transformation");
    }

    @Override
    public DLTree visit(ObjectRoleBottom expr) {
        throw new ReasonerInternalException(
                "Unsupported expression 'bottom object role' in transformation");
    }

    @Override
    public DLTree visit(ObjectRoleName expr) {
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
        return DLTreeFactory.buildTree(new Lexeme(RNAME, role));
    }

    @Override
    public DLTree visit(ObjectRoleInverse expr) {
        return DLTreeFactory.createInverse(expr.getOR().accept(this));
    }

    @Override
    public DLTree visit(ObjectRoleChain expr) {
        List<ObjectRoleExpression> l = new ArrayList<ObjectRoleExpression>(
                expr.getArguments());
        if (l.isEmpty()) {
            throw new ReasonerInternalException(
                    "Unsupported expression 'empty role chain' in transformation");
        }
        DLTree acc = l.get(0).accept(this);
        for (int i = 1; i < l.size(); i++) {
            // TODO this is still a binary tree while it should be n-ary with
            // enforced order
            acc = DLTreeFactory.buildTree(new Lexeme(RCOMPOSITION), acc,
                    l.get(i).accept(this));
        }
        return acc;
    }

    @Override
    public DLTree visit(ObjectRoleProjectionFrom expr) {
        return DLTreeFactory.buildTree(new Lexeme(PROJFROM), expr.getOR()
                .accept(this), expr.getConcept().accept(this));
    }

    @Override
    public DLTree visit(ObjectRoleProjectionInto expr) {
        return DLTreeFactory.buildTree(new Lexeme(PROJINTO), expr.getOR()
                .accept(this), expr.getConcept().accept(this));
    }

    // data role expressions
    @Override
    public DLTree visit(DataRoleTop expr) {
        throw new ReasonerInternalException(
                "Unsupported expression 'top data role' in transformation");
    }

    @Override
    public DLTree visit(DataRoleBottom expr) {
        throw new ReasonerInternalException(
                "Unsupported expression 'bottom data role' in transformation");
    }

    @Override
    public DLTree visit(DataRoleName expr) {
        RoleMaster RM = tbox.getDRM();
        NamedEntry role;
        if (nc(expr)) {
            role = sig.topRLocal() ? RM.getTopRole() : RM.getBotRole();
        } else {
            role = expr.getEntry();
            if (role == null) {
                role = matchEntry(RM.ensureRoleName(expr.getName()), expr);
            }
        }
        return DLTreeFactory.buildTree(new Lexeme(DNAME, role));
    }

    // data expressions
    @Override
    public DLTree visit(DataTop expr) {
        return DLTreeFactory.createTop();
    }

    @Override
    public DLTree visit(DataBottom expr) {
        return DLTreeFactory.createBottom();
    }

    @Override
    public DLTree visit(Datatype<?> expr) {
        DatatypeEntry entry = new DatatypeEntry(expr);
        return DLTreeFactory.wrap(entry);
    }

    @Override
    public DLTree visit(DatatypeExpression<?> expr) {
        DatatypeEntry entry = new DatatypeEntry(expr);
        return DLTreeFactory.wrap(entry);
    }

    @Override
    public DLTree visit(Literal<?> expr) {
        // process type
        LiteralEntry entry = new LiteralEntry(expr.value());
        entry.setLiteral(expr);
        return DLTreeFactory.wrap(entry);
    }

    @Override
    public DLTree visit(DataNot expr) {
        return DLTreeFactory.createSNFNot(expr.getExpr().accept(this));
    }

    @Override
    public DLTree visit(DataAnd expr) {
        return DLTreeFactory.createSNFAnd(visitArgs(expr));
    }

    @Override
    public DLTree visit(DataOr expr) {
        return DLTreeFactory.createSNFOr(visitArgs(expr));
    }

    @Override
    public DLTree visit(DataOneOf expr) {
        return DLTreeFactory.createSNFOr(visitArgs(expr));
    }
}
