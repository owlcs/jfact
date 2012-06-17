package conformance.broken;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class Plus_and_Minus_Zero_are_Distinct {
	@Test
	public void testPlus_and_Minus_Zero_are_Distinct() {
		String premise = "Prefix(:=<http://example.org/>)\n"
				+ "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
				+ "Ontology(\n"
				+ "  Declaration(NamedIndividual(:Meg))\n"
				+ "  Declaration(DataProperty(:numberOfChildren))\n"
				+ "  DataPropertyAssertion(:numberOfChildren :Meg \"+0.0\"^^xsd:float) \n"
				+ "  DataPropertyAssertion(:numberOfChildren :Meg \"-0.0\"^^xsd:float) \n"
				+ "  FunctionalDataProperty(:numberOfChildren)\n" + ")";
		String conclusion = "";
		String id = "Plus_and_Minus_Zero_are_Distinct";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "For floats and double, +0.0 and -0.0 are distinct values, which contradicts the functionality for numberOfChildren.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
