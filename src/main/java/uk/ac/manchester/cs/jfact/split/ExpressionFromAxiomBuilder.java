package uk.ac.manchester.cs.jfact.split;

import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.*;

import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleDomain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleRange;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleDomain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleRange;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRelatedTo;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRelatedToNot;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomValueOf;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomValueOfNot;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorExAdapter;

/** @author ignazio */
public class ExpressionFromAxiomBuilder extends
        DLAxiomVisitorExAdapter<ConceptExpression> {

    private static final long serialVersionUID = 10201L;
    private final ExpressionCache em;
    /**
     * @param a
     *        a
     */
    public ExpressionFromAxiomBuilder(ConceptExpression a, ExpressionCache em) {
        super(a);
        this.em = em;
    }

    @Override
    public ConceptExpression visit(AxiomRelatedTo axiom) {
        return value(em, axiom.getRelation(), axiom.getRelatedIndividual());
    }

    @Override
    public ConceptExpression visit(AxiomValueOf axiom) {
        return value(em, axiom.getAttribute(), axiom.getValue());
    }

    @Override
    public ConceptExpression visit(AxiomORoleDomain axiom) {
        return exists(em, axiom.getRole(), top());
    }

    @Override
    public ConceptExpression visit(AxiomORoleRange axiom) {
        return exists(em, axiom.getRole(), not(em, axiom.getRange()));
    }

    @Override
    public ConceptExpression visit(AxiomDRoleDomain axiom) {
        return exists(em, axiom.getRole(), dataTop());
    }

    @Override
    public ConceptExpression visit(AxiomDRoleRange axiom) {
        return exists(em, axiom.getRole(), dataNot(em, axiom.getRange()));
    }

    @Override
    public ConceptExpression visit(AxiomRelatedToNot axiom) {
        return not(em, value(em, axiom.getRelation(), axiom.getRelatedIndividual()));
    }

    @Override
    public ConceptExpression visit(AxiomValueOfNot axiom) {
        return not(em, value(em, axiom.getAttribute(), axiom.getValue()));
    }
}
