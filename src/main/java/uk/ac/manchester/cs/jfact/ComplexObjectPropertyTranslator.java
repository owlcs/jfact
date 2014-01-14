package uk.ac.manchester.cs.jfact;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNodeSet;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleComplexExpression;

/** property expression translator */
public class ComplexObjectPropertyTranslator extends
        OWLEntityTranslator<OWLObjectPropertyExpression, ObjectRoleComplexExpression> {
    private static final long serialVersionUID = 11000L;

    /** @param em
     *            em
     * @param df
     *            df
     * @param tr
     *            tr */
    public ComplexObjectPropertyTranslator(ExpressionManager em, OWLDataFactory df,
            TranslationMachinery tr) {
        super(em, df, tr);
    }

    @Override
    protected ObjectRoleComplexExpression getTopEntityPointer() {
        return em.objectRole(OWLRDFVocabulary.OWL_TOP_OBJECT_PROPERTY.getIRI());
    }

    @Override
    protected ObjectRoleComplexExpression getBottomEntityPointer() {
        return em.objectRole(OWLRDFVocabulary.OWL_BOTTOM_OBJECT_PROPERTY.getIRI());
    }

    @Override
    protected ObjectRoleComplexExpression registerNewEntity(
            OWLObjectPropertyExpression entity) {
        ObjectRoleComplexExpression pointer = createPointerForEntity(entity);
        fillMaps(entity, pointer);
        OWLObjectPropertyExpression inverseentity = entity.getInverseProperty()
                .getSimplified();
        fillMaps(inverseentity, createPointerForEntity(inverseentity));
        return pointer;
    }

    @Override
    protected ObjectRoleComplexExpression createPointerForEntity(
            OWLObjectPropertyExpression entity) {
        ObjectRoleComplexExpression p = em.objectRole(entity.getNamedProperty().getIRI());
        return p;
    }

    @Override
    protected OWLObjectProperty getTopEntity() {
        return df.getOWLTopObjectProperty();
    }

    @Override
    protected OWLObjectProperty getBottomEntity() {
        return df.getOWLBottomObjectProperty();
    }

    @Override
    protected DefaultNode<OWLObjectPropertyExpression> createDefaultNode() {
        return new OWLObjectPropertyNode();
    }

    @Override
    protected DefaultNodeSet<OWLObjectPropertyExpression> createDefaultNodeSet() {
        return new OWLObjectPropertyNodeSet();
    }
}
