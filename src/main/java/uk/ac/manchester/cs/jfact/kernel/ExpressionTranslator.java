package uk.ac.manchester.cs.jfact.kernel;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.io.Serializable;
import java.util.List;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapitools.decomposition.Signature;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeEntry;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeExpression;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.datatypes.LiteralEntry;
import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NAryExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

/** expression translator */
@PortedFrom(file = "tExpressionTranslator.h", name = "TExpressionTranslator")
public class ExpressionTranslator implements DLExpressionVisitorEx<DLTree>, Serializable {

    /** TBox to get access to the named entities */
    @PortedFrom(file = "tExpressionTranslator.h", name = "kb") private final TBox tbox;
    /**
     * signature of non-trivial entities; used in semantic locality checkers
     * only
     */
    @PortedFrom(file = "tExpressionTranslator.h", name = "sig") private Signature sig;

    /**
     * @param kb
     *        kb
     */
    public ExpressionTranslator(TBox kb) {
        tbox = kb;
    }

    /**
     * @param entity
     *        entity
     * @return true iff ENTRY is not in signature
     */
    @PortedFrom(file = "tExpressionTranslator.h", name = "nc")
    private boolean nc(NamedEntity entity) {
        return sig != null && !sig.contains(entity.getEntity());
    }

    /**
     * set internal signature to a given signature S
     * 
     * @param s
     *        signature
     */
    @PortedFrom(file = "tExpressionTranslator.h", name = "setSignature")
    public void setSignature(Signature s) {
        sig = s;
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
            return sig.topCLocal() ? DLTreeFactory.createTop() : DLTreeFactory.createBottom();
        } else {
            NamedEntry entry = expr.getEntry();
            if (entry == null) {
                entry = matchEntry(tbox.getConcept(expr.getIRI()), expr);
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
    private static NamedEntry matchEntry(NamedEntry entry, NamedEntity entity) {
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
        return asList(expr.getArguments().stream().map(a -> a.accept(this)));
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
        return DLTreeFactory.createSNFExists(expr.getOR().accept(this), expr.getIndividual().accept(this));
    }

    @Override
    public DLTree visit(ConceptObjectExists expr) {
        return DLTreeFactory.createSNFExists(expr.getOR().accept(this), expr.getConcept().accept(this));
    }

    @Override
    public DLTree visit(ConceptObjectForall expr) {
        return DLTreeFactory.createSNFForall(expr.getOR().accept(this), expr.getConcept().accept(this));
    }

    @Override
    public DLTree visit(ConceptObjectMinCardinality expr) {
        return DLTreeFactory.createSNFGE(expr.getCardinality(), expr.getOR().accept(this), expr.getConcept().accept(
            this));
    }

    @Override
    public DLTree visit(ConceptObjectMaxCardinality expr) {
        return DLTreeFactory.createSNFLE(expr.getCardinality(), expr.getOR().accept(this), expr.getConcept().accept(
            this));
    }

    @Override
    public DLTree visit(ConceptObjectExactCardinality expr) {
        DLTree le = DLTreeFactory.createSNFLE(expr.getCardinality(), expr.getOR().accept(this).copy(), expr.getConcept()
            .accept(this).copy());
        DLTree ge = DLTreeFactory.createSNFGE(expr.getCardinality(), expr.getOR().accept(this).copy(), expr.getConcept()
            .accept(this).copy());
        return DLTreeFactory.createSNFAnd(ge, le);
    }

    @Override
    public DLTree visit(ConceptDataValue expr) {
        return DLTreeFactory.createSNFExists(expr.getDataRoleExpression().accept(this), expr.getExpr().accept(this));
    }

    @Override
    public DLTree visit(ConceptDataExists expr) {
        return DLTreeFactory.createSNFExists(expr.getDataRoleExpression().accept(this), expr.getExpr().accept(this));
    }

    @Override
    public DLTree visit(ConceptDataForall expr) {
        return DLTreeFactory.createSNFForall(expr.getDataRoleExpression().accept(this), expr.getExpr().accept(this));
    }

    @Override
    public DLTree visit(ConceptDataMinCardinality expr) {
        DLTree role = expr.getDataRoleExpression().accept(this);
        DLTree filler = expr.getExpr().accept(this);
        assert role != null;
        assert filler != null;
        return DLTreeFactory.createSNFGE(expr.getCardinality(), role, filler);
    }

    @Override
    public DLTree visit(ConceptDataMaxCardinality expr) {
        return DLTreeFactory.createSNFLE(expr.getCardinality(), expr.getDataRoleExpression().accept(this), expr
            .getExpr().accept(this));
    }

    @Override
    public DLTree visit(ConceptDataExactCardinality expr) {
        DLTree le = DLTreeFactory.createSNFLE(expr.getCardinality(), expr.getDataRoleExpression().accept(this).copy(),
            expr.getExpr().accept(this).copy());
        DLTree ge = DLTreeFactory.createSNFGE(expr.getCardinality(), expr.getDataRoleExpression().accept(this).copy(),
            expr.getExpr().accept(this).copy());
        return DLTreeFactory.createSNFAnd(ge, le);
    }

    // individual expressions
    @Override
    public DLTree visit(IndividualName expr) {
        NamedEntry entry = expr.getEntry();
        if (entry == null) {
            entry = matchEntry(tbox.getIndividual(expr.getIRI()), expr);
        }
        return DLTreeFactory.buildTree(new Lexeme(INAME, entry));
    }

    // object role expressions
    @Override
    public DLTree visit(ObjectRoleTop expr) {
        throw new ReasonerInternalException("Unsupported expression 'top object role' in transformation");
    }

    @Override
    public DLTree visit(ObjectRoleBottom expr) {
        throw new ReasonerInternalException("Unsupported expression 'bottom object role' in transformation");
    }

    @Override
    public DLTree visit(ObjectRoleName expr) {
        RoleMaster rm = tbox.getORM();
        NamedEntry role = getRoleEntry(expr, rm);
        return DLTreeFactory.buildTree(new Lexeme(RNAME, role));
    }

    protected NamedEntry getRoleEntry(ObjectRoleName expr, RoleMaster rm) {
        NamedEntry role;
        if (nc(expr)) {
            role = sig.topRLocal() ? rm.getTopRole() : rm.getBotRole();
        } else {
            role = expr.getEntry();
            if (role == null) {
                role = matchEntry(rm.ensureRoleName(expr.getIRI()), expr);
            }
        }
        return role;
    }

    @Override
    public DLTree visit(ObjectRoleInverse expr) {
        return DLTreeFactory.createInverse(expr.getOR().accept(this));
    }

    @Override
    public DLTree visit(ObjectRoleChain expr) {
        List<ObjectRoleExpression> arguments = expr.getArguments();
        if (arguments.isEmpty()) {
            throw new ReasonerInternalException("Unsupported expression 'empty role chain' in transformation");
        }
        List<DLTree> l = asList(arguments.stream().map(p -> p.accept(this)));
        return DLTreeFactory.buildTree(new Lexeme(RCOMPOSITION), l);
    }

    @Override
    public DLTree visit(ObjectRoleProjectionFrom expr) {
        return DLTreeFactory.buildTree(new Lexeme(PROJFROM), expr.getOR().accept(this), expr.getConcept().accept(this));
    }

    @Override
    public DLTree visit(ObjectRoleProjectionInto expr) {
        return DLTreeFactory.buildTree(new Lexeme(PROJINTO), expr.getOR().accept(this), expr.getConcept().accept(this));
    }

    // data role expressions
    @Override
    public DLTree visit(DataRoleTop expr) {
        throw new ReasonerInternalException("Unsupported expression 'top data role' in transformation");
    }

    @Override
    public DLTree visit(DataRoleBottom expr) {
        throw new ReasonerInternalException("Unsupported expression 'bottom data role' in transformation");
    }

    @Override
    public DLTree visit(DataRoleName expr) {
        RoleMaster rm = tbox.getDRM();
        NamedEntry role;
        if (nc(expr)) {
            role = sig.topRLocal() ? rm.getTopRole() : rm.getBotRole();
        } else {
            role = expr.getEntry();
            if (role == null) {
                role = matchEntry(rm.ensureRoleName(expr.getIRI()), expr);
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
        return DLTreeFactory.wrap(new DatatypeEntry(expr));
    }

    @Override
    public DLTree visit(DatatypeExpression<?> expr) {
        return DLTreeFactory.wrap(new DatatypeEntry(expr));
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
