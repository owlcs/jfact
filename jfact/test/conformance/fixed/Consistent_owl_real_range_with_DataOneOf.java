package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class Consistent_owl_real_range_with_DataOneOf {
	@Test
	public void testConsistent_owl_real_range_with_DataOneOf() {
		String premise = "Prefix(:=<http://example.org/>)\n"
				+ "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
				+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
				+ "Ontology(\n"
				+ "  Declaration(NamedIndividual(:a))\n"
				+ "  Declaration(DataProperty(:dp))\n"
				+ "  Declaration(Class(:A))\n"
				+ "  SubClassOf(:A DataAllValuesFrom(:dp owl:real)) \n"
				+ "  SubClassOf(:A \n"
				+ "    DataSomeValuesFrom(:dp DataOneOf(\"-INF\"^^xsd:float \"-0\"^^xsd:integer))\n"
				+ "  )\n" + "  ClassAssertion(:A :a)\n" + ")";
		String conclusion = "";
		String id = "Consistent_owl_real_range_with_DataOneOf";
		TestClasses tc = TestClasses.valueOf("CONSISTENCY");
		String d = "The individual a must have either negative Infinity or 0 (-0 as integer is 0) as dp fillers and all dp successors must be from owl:real, which excludes negative infinity, but allows 0.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
