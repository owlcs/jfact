package uk.ac.manchester.cs.jfact;

import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;

import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;

/** class expression translator */
public class ClassExpressionTranslator extends
        OWLEntityTranslator<OWLClass, ConceptExpression> implements
        OWLClassExpressionVisitorEx<ConceptExpression> {

    private static final long serialVersionUID = 11000L;

    /**
     * @param em
     *        em
     * @param df
     *        df
     * @param tr
     *        tr
     */
    public ClassExpressionTranslator(ExpressionCache em, OWLDataFactory df,
            TranslationMachinery tr) {
        super(em, df, tr);
    }

    @Override
    protected ConceptExpression getTopEntityPointer() {
        return top();
    }

    @Override
    protected ConceptExpression getBottomEntityPointer() {
        return bottom();
    }

    @Override
    protected OWLClass getTopEntity() {
        return df.getOWLThing();
    }

    @Override
    protected OWLClass getBottomEntity() {
        return df.getOWLNothing();
    }

    @Override
    protected ConceptExpression createPointerForEntity(OWLClass entity) {
        // XXX many entities would cause a lot of wasted memory
        return em.concept(entity.getIRI());
    }

    @Override
    protected DefaultNode<OWLClass> createDefaultNode() {
        return new OWLClassNode();
    }

    @Override
    protected DefaultNodeSet<OWLClass> createDefaultNodeSet() {
        return new OWLClassNodeSet();
    }

    @Override
    public ConceptExpression visit(OWLClass desc) {
        return getPointerFromEntity(desc);
    }

    @Override
    public ConceptExpression visit(OWLObjectIntersectionOf desc) {
        return and(translateClassExpressionSet(desc.getOperands()));
    }

    private List<ConceptExpression> translateClassExpressionSet(
            Set<OWLClassExpression> classExpressions) {
        List<ConceptExpression> l = new ArrayList<>();
        for (OWLClassExpression ce : classExpressions) {
            l.add(ce.accept(this));
        }
        return l;
    }

    @Override
    public ConceptExpression visit(OWLObjectUnionOf desc) {
        return or(translateClassExpressionSet(desc.getOperands()));
    }

    @Override
    public ConceptExpression visit(OWLObjectComplementOf desc) {
        return not(desc.getOperand().accept(this));
    }

    @Override
    public ConceptExpression visit(OWLObjectSomeValuesFrom desc) {
        return exists(tr.pointer(desc.getProperty()),
                desc.getFiller().accept(this));
    }

    @Override
    public ConceptExpression visit(OWLObjectAllValuesFrom desc) {
        return forall(tr.pointer(desc.getProperty()),
                desc.getFiller().accept(this));
    }

    @Override
    public ConceptExpression visit(OWLObjectHasValue desc) {
        return value(tr.pointer(desc.getProperty()),
                tr.pointer(desc.getFiller()));
    }

    @Override
    public ConceptExpression visit(OWLObjectMinCardinality desc) {
        return minCardinality(desc.getCardinality(),
                tr.pointer(desc.getProperty()), desc.getFiller().accept(this));
    }

    @Override
    public ConceptExpression visit(OWLObjectExactCardinality desc) {
        return cardinality(desc.getCardinality(),
                tr.pointer(desc.getProperty()), desc.getFiller().accept(this));
    }

    @Override
    public ConceptExpression visit(OWLObjectMaxCardinality desc) {
        return maxCardinality(desc.getCardinality(),
                tr.pointer(desc.getProperty()), desc.getFiller().accept(this));
    }

    @Override
    public ConceptExpression visit(OWLObjectHasSelf desc) {
        return selfReference(tr.pointer(desc.getProperty()));
    }

    @Override
    public ConceptExpression visit(OWLObjectOneOf desc) {
        return em.oneOf(tr.translate(desc.getIndividuals()));
    }

    @Override
    public ConceptExpression visit(OWLDataSomeValuesFrom desc) {
        return exists(tr.pointer(desc.getProperty()),
                tr.pointer(desc.getFiller()));
    }

    @Override
    public ConceptExpression visit(OWLDataAllValuesFrom desc) {
        return forall(tr.pointer(desc.getProperty()),
                tr.pointer(desc.getFiller()));
    }

    @Override
    public ConceptExpression visit(OWLDataHasValue desc) {
        return value(tr.pointer(desc.getProperty()),
                tr.pointer(desc.getFiller()));
    }

    @Override
    public ConceptExpression visit(OWLDataMinCardinality desc) {
        return minCardinality(desc.getCardinality(),
                tr.pointer(desc.getProperty()), tr.pointer(desc.getFiller()));
    }

    @Override
    public ConceptExpression visit(OWLDataExactCardinality desc) {
        return cardinality(desc.getCardinality(),
                tr.pointer(desc.getProperty()), tr.pointer(desc.getFiller()));
    }

    @Override
    public ConceptExpression visit(OWLDataMaxCardinality desc) {
        return maxCardinality(desc.getCardinality(),
                tr.pointer(desc.getProperty()), tr.pointer(desc.getFiller()));
    }
}
