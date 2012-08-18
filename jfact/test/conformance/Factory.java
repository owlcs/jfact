package conformance;

import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.jfact.JFactFactory;

@SuppressWarnings("javadoc")
public class Factory {
    public static final OWLReasonerFactory factory() {
        return new JFactFactory();
    }
}
