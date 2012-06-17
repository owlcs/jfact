package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_Keys_006 {
	@Test
	public void testNew_Feature_Keys_006() {
		String premise = "<?xml version=\"1.0\"?>\n"
				+ "<rdf:RDF\n"
				+ "  xml:base  = \"http://example.org/\"\n"
				+ "  xmlns     = \"http://example.org/\"\n"
				+ "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
				+ "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
				+ "\n"
				+ "<owl:Ontology/>\n"
				+ "\n"
				+ "<owl:Class rdf:about=\"GriffinFamilyMember\" />\n"
				+ "\n"
				+ "<owl:DatatypeProperty rdf:about=\"hasName\" />\n"
				+ "\n"
				+ "<rdf:Description rdf:about=\"GriffinFamilyMember\">\n"
				+ "  <owl:hasKey rdf:parseType=\"Collection\">\n"
				+ "    <rdf:Description rdf:about=\"hasName\" />\n"
				+ "  </owl:hasKey>\n"
				+ "</rdf:Description>\n"
				+ "\n"
				+ "<rdf:Description rdf:about=\"Peter\">\n"
				+ "  <hasName>Peter</hasName>\n"
				+ "  <hasName>Kichwa-Tembo</hasName>\n"
				+ "  <rdf:type rdf:resource=\"GriffinFamilyMember\" />\n"
				+ "</rdf:Description>\n"
				+ "\n"
				+ "<rdf:Description rdf:about=\"hasName\">\n"
				+ "  <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#FunctionalProperty\" />\n"
				+ "</rdf:Description>\n" + "\n" + "</rdf:RDF>";
		String conclusion = "";
		String id = "New_Feature_Keys_006";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "Demonstrates that a key axiom does not make all properties used in it functional, but these properties may be made functional with other axioms. Based on an example in the Structural Specification and Functional-Style Syntax document.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
