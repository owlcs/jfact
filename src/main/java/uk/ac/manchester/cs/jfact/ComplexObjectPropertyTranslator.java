package uk.ac.manchester.cs.jfact;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNodeSet;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleComplexExpression;

/** property expression translator */
public class ComplexObjectPropertyTranslator extends
    OWLEntityTranslator<OWLObjectPropertyExpression, ObjectRoleComplexExpression> {

    /**
     * @param em
     *        em
     * @param df
     *        df
     * @param tr
     *        tr
     */
    public ComplexObjectPropertyTranslator(ExpressionCache em, OWLDataFactory df, TranslationMachinery tr) {
        super(em, df, tr);
    }

    @Nullable
    @Override
    protected ObjectRoleComplexExpression getTopEntityPointer() {
        return em.objectRole(df.getOWLObjectProperty(OWLRDFVocabulary.OWL_TOP_OBJECT_PROPERTY));
    }

    @Nullable
    @Override
    protected ObjectRoleComplexExpression getBottomEntityPointer() {
        return em.objectRole(df.getOWLObjectProperty(OWLRDFVocabulary.OWL_BOTTOM_OBJECT_PROPERTY));
    }

    @Override
    protected ObjectRoleComplexExpression registerNewEntity(OWLObjectPropertyExpression entity) {
        ObjectRoleComplexExpression pointer = createPointerForEntity(entity);
        fillMaps(entity, pointer);
        OWLObjectPropertyExpression inverseentity = entity.getInverseProperty().getSimplified();
        fillMaps(inverseentity, createPointerForEntity(inverseentity));
        return pointer;
    }

    @Override
    protected ObjectRoleComplexExpression createPointerForEntity(OWLObjectPropertyExpression entity) {
        return em.objectRole(entity.getNamedProperty());
    }

    @Nullable
    @Override
    protected OWLObjectProperty getTopEntity() {
        return df.getOWLTopObjectProperty();
    }

    @Nullable
    @Override
    protected OWLObjectProperty getBottomEntity() {
        return df.getOWLBottomObjectProperty();
    }

    @Override
    protected DefaultNode<OWLObjectPropertyExpression> createDefaultNode(Stream<OWLObjectPropertyExpression> stream) {
        return new OWLObjectPropertyNode(stream);
    }

    @Override
    protected DefaultNodeSet<OWLObjectPropertyExpression> createDefaultNodeSet(
        Stream<Node<OWLObjectPropertyExpression>> stream) {
        return new OWLObjectPropertyNodeSet(stream);
    }
}
