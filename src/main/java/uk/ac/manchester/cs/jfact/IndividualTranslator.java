package uk.ac.manchester.cs.jfact;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;

import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;

/** individual translator */
public class IndividualTranslator extends OWLEntityTranslator<OWLNamedIndividual, IndividualName> {

    /**
     * @param em
     *        em
     * @param df
     *        df
     * @param tr
     *        tr
     */
    public IndividualTranslator(ExpressionCache em, OWLDataFactory df, TranslationMachinery tr) {
        super(em, df, tr);
    }

    @Nullable
    @Override
    protected IndividualName getTopEntityPointer() {
        return null;
    }

    @Nullable
    @Override
    protected IndividualName getBottomEntityPointer() {
        return null;
    }

    @Override
    protected IndividualName createPointerForEntity(OWLNamedIndividual entity) {
        return em.individual(entity);
    }

    @Nullable
    @Override
    protected OWLNamedIndividual getTopEntity() {
        return null;
    }

    @Nullable
    @Override
    protected OWLNamedIndividual getBottomEntity() {
        return null;
    }

    @Override
    protected DefaultNode<OWLNamedIndividual> createDefaultNode(Stream<OWLNamedIndividual> stream) {
        return new OWLNamedIndividualNode(stream);
    }

    @Override
    protected DefaultNodeSet<OWLNamedIndividual> createDefaultNodeSet(Stream<Node<OWLNamedIndividual>> stream) {
        return new OWLNamedIndividualNodeSet(stream);
    }
}
