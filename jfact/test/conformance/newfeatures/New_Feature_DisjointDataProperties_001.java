package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_DisjointDataProperties_001 {
	@Test
	public void testNew_Feature_DisjointDataProperties_001() {
		String premise = "<?xml version=\"1.0\"?>\n" + "<rdf:RDF\n"
				+ "  xml:base  = \"http://example.org/\"\n"
				+ "  xmlns     = \"http://example.org/\"\n"
				+ "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
				+ "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
				+ "\n" + "<owl:Ontology/>\n" + "\n"
				+ "<owl:DatatypeProperty rdf:about=\"hasName\" />\n"
				+ "<owl:DatatypeProperty rdf:about=\"hasAddress\" />\n" + "\n"
				+ "<rdf:Description rdf:about=\"hasName\">\n"
				+ "  <owl:propertyDisjointWith rdf:resource=\"hasAddress\" />\n"
				+ "</rdf:Description>\n" + "\n"
				+ "<rdf:Description rdf:about=\"Peter\">\n"
				+ "  <hasName>Peter Griffin</hasName>\n"
				+ "  <hasAddress>Peter Griffin</hasAddress>\n" + "</rdf:Description>\n"
				+ "\n" + "</rdf:RDF>";
		String conclusion = "";
		String id = "New_Feature_DisjointDataProperties_001";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "Demonstrates use of a disjoint data properties axiom to create a trivial inconsistency based on the example in the Structural Specification and Functional-Style Syntax document.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
