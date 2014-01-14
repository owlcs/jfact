package uk.ac.manchester.cs.jfact;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;

import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;

/** individual translator */
public class IndividualTranslator extends
        OWLEntityTranslator<OWLNamedIndividual, IndividualName> {
    private static final long serialVersionUID = 11000L;

    /** @param em
     *            em
     * @param df
     *            df
     * @param tr
     *            tr */
    public IndividualTranslator(ExpressionManager em, OWLDataFactory df,
            TranslationMachinery tr) {
        super(em, df, tr);
    }

    @Override
    protected IndividualName getTopEntityPointer() {
        return null;
    }

    @Override
    protected IndividualName getBottomEntityPointer() {
        return null;
    }

    @Override
    protected IndividualName createPointerForEntity(OWLNamedIndividual entity) {
        return em.individual(entity.getIRI());
    }

    @Override
    protected OWLNamedIndividual getTopEntity() {
        return null;
    }

    @Override
    protected OWLNamedIndividual getBottomEntity() {
        return null;
    }

    @Override
    protected DefaultNode<OWLNamedIndividual> createDefaultNode() {
        return new OWLNamedIndividualNode();
    }

    @Override
    protected DefaultNodeSet<OWLNamedIndividual> createDefaultNodeSet() {
        return new OWLNamedIndividualNodeSet();
    }
}
