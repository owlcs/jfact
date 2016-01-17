package uk.ac.manchester.cs.jfact;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.*;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;

import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;

/** class expression translator */
public class ClassExpressionTranslator extends OWLEntityTranslator<OWLClass, ConceptExpression> implements
    OWLClassExpressionVisitorEx<ConceptExpression> {

    /**
     * @param em
     *        em
     * @param df
     *        df
     * @param tr
     *        tr
     */
    public ClassExpressionTranslator(ExpressionCache em, OWLDataFactory df, TranslationMachinery tr) {
        super(em, df, tr);
    }

    @Nullable
    @Override
    protected ConceptExpression getTopEntityPointer() {
        return top();
    }

    @Nullable
    @Override
    protected ConceptExpression getBottomEntityPointer() {
        return bottom();
    }

    @Nullable
    @Override
    protected OWLClass getTopEntity() {
        return df.getOWLThing();
    }

    @Nullable
    @Override
    protected OWLClass getBottomEntity() {
        return df.getOWLNothing();
    }

    @Override
    protected ConceptExpression createPointerForEntity(OWLClass entity) {
        // XXX many entities would cause a lot of wasted memory
        return em.concept(entity);
    }

    @Override
    protected DefaultNode<OWLClass> createDefaultNode(Stream<OWLClass> stream) {
        return new OWLClassNode(stream);
    }

    @Override
    protected DefaultNodeSet<OWLClass> createDefaultNodeSet(Stream<Node<OWLClass>> stream) {
        return new OWLClassNodeSet(stream);
    }

    @Override
    public ConceptExpression visit(OWLClass desc) {
        return getPointerFromEntity(desc);
    }

    @Override
    public ConceptExpression visit(OWLObjectIntersectionOf desc) {
        return and(translateClassExpressionSet(desc.operands()));
    }

    private List<ConceptExpression> translateClassExpressionSet(Stream<? extends OWLClassExpression> classExpressions) {
        return asList(classExpressions.map(c -> c.accept(this)));
    }

    @Override
    public ConceptExpression visit(OWLObjectUnionOf desc) {
        return or(translateClassExpressionSet(desc.operands()));
    }

    @Override
    public ConceptExpression visit(OWLObjectComplementOf desc) {
        return not(desc.getOperand().accept(this));
    }

    @Override
    public ConceptExpression visit(OWLObjectSomeValuesFrom desc) {
        return exists(tr.pointer(desc.getProperty()), desc.getFiller().accept(this));
    }

    @Override
    public ConceptExpression visit(OWLObjectAllValuesFrom desc) {
        return forall(tr.pointer(desc.getProperty()), desc.getFiller().accept(this));
    }

    @Override
    public ConceptExpression visit(OWLObjectHasValue desc) {
        return value(tr.pointer(desc.getProperty()), tr.pointer(desc.getFiller()));
    }

    @Override
    public ConceptExpression visit(OWLObjectMinCardinality desc) {
        return minCardinality(desc.getCardinality(), tr.pointer(desc.getProperty()), desc.getFiller().accept(this));
    }

    @Override
    public ConceptExpression visit(OWLObjectExactCardinality desc) {
        return cardinality(desc.getCardinality(), tr.pointer(desc.getProperty()), desc.getFiller().accept(this));
    }

    @Override
    public ConceptExpression visit(OWLObjectMaxCardinality desc) {
        return maxCardinality(desc.getCardinality(), tr.pointer(desc.getProperty()), desc.getFiller().accept(this));
    }

    @Override
    public ConceptExpression visit(OWLObjectHasSelf desc) {
        return selfReference(tr.pointer(desc.getProperty()));
    }

    @Override
    public ConceptExpression visit(OWLObjectOneOf desc) {
        return em.oneOf(tr.translate(desc.individuals()));
    }

    @Override
    public ConceptExpression visit(OWLDataSomeValuesFrom desc) {
        return exists(tr.pointer(desc.getProperty()), tr.pointer(desc.getFiller()));
    }

    @Override
    public ConceptExpression visit(OWLDataAllValuesFrom desc) {
        return forall(tr.pointer(desc.getProperty()), tr.pointer(desc.getFiller()));
    }

    @Override
    public ConceptExpression visit(OWLDataHasValue desc) {
        return value(tr.pointer(desc.getProperty()), tr.pointer(desc.getFiller()));
    }

    @Override
    public ConceptExpression visit(OWLDataMinCardinality desc) {
        return minCardinality(desc.getCardinality(), tr.pointer(desc.getProperty()), tr.pointer(desc.getFiller()));
    }

    @Override
    public ConceptExpression visit(OWLDataExactCardinality desc) {
        return cardinality(desc.getCardinality(), tr.pointer(desc.getProperty()), tr.pointer(desc.getFiller()));
    }

    @Override
    public ConceptExpression visit(OWLDataMaxCardinality desc) {
        return maxCardinality(desc.getCardinality(), tr.pointer(desc.getProperty()), tr.pointer(desc.getFiller()));
    }
}
