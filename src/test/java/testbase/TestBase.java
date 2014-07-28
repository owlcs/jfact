package testbase;

import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.jfact.JFactFactory;

public class TestBase {

    protected OWLReasonerFactory factory() {
        return new JFactFactory();
    }
}
