package uk.ac.manchester.cs.jfact;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNodeSet;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;

/** data property translator */
public class DataPropertyTranslator extends
        OWLEntityTranslator<OWLDataProperty, DataRoleExpression> {
    private static final long serialVersionUID = 11000L;

    /** @param em
     *            em
     * @param df
     *            df
     * @param tr
     *            tr */
    public DataPropertyTranslator(ExpressionManager em, OWLDataFactory df,
            TranslationMachinery tr) {
        super(em, df, tr);
    }

    @Override
    protected DataRoleExpression getTopEntityPointer() {
        return em.dataRole(OWLRDFVocabulary.OWL_TOP_DATA_PROPERTY.getIRI());
    }

    @Override
    protected DataRoleExpression getBottomEntityPointer() {
        return em.dataRole(OWLRDFVocabulary.OWL_BOTTOM_DATA_PROPERTY.getIRI());
    }

    @Override
    protected DataRoleExpression createPointerForEntity(OWLDataProperty entity) {
        return em.dataRole(entity.getIRI());
    }

    @Override
    protected OWLDataProperty getTopEntity() {
        return df.getOWLTopDataProperty();
    }

    @Override
    protected OWLDataProperty getBottomEntity() {
        return df.getOWLBottomDataProperty();
    }

    @Override
    protected DefaultNode<OWLDataProperty> createDefaultNode() {
        return new OWLDataPropertyNode();
    }

    @Override
    protected DefaultNodeSet<OWLDataProperty> createDefaultNodeSet() {
        return new OWLDataPropertyNodeSet();
    }
}
