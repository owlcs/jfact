package uk.ac.manchester.cs.jfact.split;

import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.*;

import javax.annotation.Nullable;

import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;

/** @author ignazio */
public class ExpressionFromAxiomBuilder implements DLAxiomVisitorEx<ConceptExpression> {

    private ConceptExpression a;

    /**
     * @param a
     *        a
     */
    public ExpressionFromAxiomBuilder(@Nullable ConceptExpression a) {
        this.a = a;
    }

    @Override
    public ConceptExpression doDefault(AxiomInterface axiom) {
        return a;
    }

    @Override
    public ConceptExpression visit(AxiomRelatedTo axiom) {
        return value(axiom.getRelation(), axiom.getRelatedIndividual());
    }

    @Override
    public ConceptExpression visit(AxiomValueOf axiom) {
        return value(axiom.getAttribute(), axiom.getValue());
    }

    @Override
    public ConceptExpression visit(AxiomORoleDomain axiom) {
        return exists(axiom.getRole(), top());
    }

    @Override
    public ConceptExpression visit(AxiomORoleRange axiom) {
        return exists(axiom.getRole(), not(axiom.getRange()));
    }

    @Override
    public ConceptExpression visit(AxiomDRoleDomain axiom) {
        return exists(axiom.getRole(), dataTop());
    }

    @Override
    public ConceptExpression visit(AxiomDRoleRange axiom) {
        return exists(axiom.getRole(), dataNot(axiom.getRange()));
    }

    @Override
    public ConceptExpression visit(AxiomRelatedToNot axiom) {
        return not(value(axiom.getRelation(), axiom.getRelatedIndividual()));
    }

    @Override
    public ConceptExpression visit(AxiomValueOfNot axiom) {
        return not(value(axiom.getAttribute(), axiom.getValue()));
    }
}
