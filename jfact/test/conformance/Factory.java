package conformance;

import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.jfact.JFactFactory;

public class Factory {
	public static final OWLReasonerFactory factory() {
		return new JFactFactory();
	}
}
