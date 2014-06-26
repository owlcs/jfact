package jfact.plugin.protege;

import org.protege.editor.owl.model.inference.AbstractProtegeOWLReasonerInfo;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.jfact.JFactFactory;

/**
 * @author ignazio
 */
public class JFactProtegeFactory extends AbstractProtegeOWLReasonerInfo {

    private JFactFactory fac;

    @Override
    public void initialise() {
        fac = new JFactFactory();
    }

    @Override
    public void dispose() {
        fac = null;
    }

    @Override
    public BufferingMode getRecommendedBuffering() {
        return BufferingMode.BUFFERING;
    }

    @Override
    public OWLReasonerFactory getReasonerFactory() {
        return fac;
    }
}
