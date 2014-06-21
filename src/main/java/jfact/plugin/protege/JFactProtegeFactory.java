package jfact.plugin.protege;

import org.protege.editor.owl.model.inference.AbstractProtegeOWLReasonerInfo;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.jfact.JFactFactory;

public class JFactProtegeFactory extends AbstractProtegeOWLReasonerInfo {

    private JFactFactory fac;

    @Override
    public void initialise() throws Exception {
        fac = new JFactFactory();
    }

    @Override
    public void dispose() throws Exception {
        fac = null;
    }

    public BufferingMode getRecommendedBuffering() {
        return BufferingMode.BUFFERING;
    }

    public OWLReasonerFactory getReasonerFactory() {
        return fac;
    }
}
