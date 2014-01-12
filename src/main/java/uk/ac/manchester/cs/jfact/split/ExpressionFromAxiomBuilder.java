package uk.ac.manchester.cs.jfact.split;

import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
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
    private final ExpressionManager pEM;

    /** @param a
     *            a
     * @param e
     *            e */
    public ExpressionFromAxiomBuilder(ConceptExpression a, ExpressionManager e) {
        super(a);
        pEM = e;
    }

    @Override
    public ConceptExpression visit(AxiomRelatedTo axiom) {
        return pEM.value(axiom.getRelation(), axiom.getRelatedIndividual());
    }

    @Override
    public ConceptExpression visit(AxiomValueOf axiom) {
        return pEM.value(axiom.getAttribute(), axiom.getValue());
    }

    @Override
    public ConceptExpression visit(AxiomORoleDomain axiom) {
        return pEM.exists(axiom.getRole(), pEM.top());
    }

    @Override
    public ConceptExpression visit(AxiomORoleRange axiom) {
        return pEM.exists(axiom.getRole(), pEM.not(axiom.getRange()));
    }

    @Override
    public ConceptExpression visit(AxiomDRoleDomain axiom) {
        return pEM.exists(axiom.getRole(), pEM.dataTop());
    }

    @Override
    public ConceptExpression visit(AxiomDRoleRange axiom) {
        return pEM.exists(axiom.getRole(), pEM.dataNot(axiom.getRange()));
    }

    @Override
    public ConceptExpression visit(AxiomRelatedToNot axiom) {
        return pEM.not(pEM.value(axiom.getRelation(), axiom.getRelatedIndividual()));
    }

    @Override
    public ConceptExpression visit(AxiomValueOfNot axiom) {
        return pEM.not(pEM.value(axiom.getAttribute(), axiom.getValue()));
    }
}
