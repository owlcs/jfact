package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_Keys_004 {
	@Test
	public void testNew_Feature_Keys_004() {
		String premise = "<?xml version=\"1.0\"?>\n" + "<rdf:RDF\n"
				+ "  xml:base  = \"http://example.org/\"\n"
				+ "  xmlns     = \"http://example.org/\"\n"
				+ "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
				+ "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
				+ "\n" + "<owl:Ontology/>\n" + "\n"
				+ "<owl:Class rdf:about=\"GriffinFamilyMember\" />\n" + "\n"
				+ "<owl:DatatypeProperty rdf:about=\"hasName\" />\n" + "\n"
				+ "<rdf:Description rdf:about=\"GriffinFamilyMember\">\n"
				+ "  <owl:hasKey rdf:parseType=\"Collection\">\n"
				+ "    <rdf:Description rdf:about=\"hasName\" />\n" + "  </owl:hasKey>\n"
				+ "</rdf:Description>\n" + "\n"
				+ "<rdf:Description rdf:about=\"Peter\">\n"
				+ "  <hasName>Peter</hasName>\n"
				+ "  <rdf:type rdf:resource=\"GriffinFamilyMember\" />\n"
				+ "</rdf:Description>\n" + "\n"
				+ "<rdf:Description rdf:about=\"Peter_Griffin\">\n"
				+ "  <hasName>Peter</hasName>\n"
				+ "  <rdf:type rdf:resource=\"GriffinFamilyMember\" />\n"
				+ "</rdf:Description>\n" + "\n"
				+ "<rdf:Description rdf:about=\"StPeter\">\n"
				+ "  <hasName>Peter</hasName>\n" + "</rdf:Description>\n" + "\n"
				+ "</rdf:RDF>";
		String conclusion = "<?xml version=\"1.0\"?>\n" + "<rdf:RDF\n"
				+ "  xml:base  = \"http://example.org/\"\n"
				+ "  xmlns     = \"http://example.org/\"\n"
				+ "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
				+ "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
				+ "\n" + "<owl:Ontology/>\n" + "\n"
				+ "<rdf:Description rdf:about=\"Peter\">\n"
				+ "  <owl:sameAs rdf:resource=\"StPeter\" />\n" + "</rdf:Description>\n"
				+ "\n" + "</rdf:RDF>";
		String id = "New_Feature_Keys_004";
		TestClasses tc = TestClasses.valueOf("NEGATIVE_IMPL");
		String d = "Demonstrates that use of a \"localized\" key axiom only merges individuals that are instances of the given class expression.  Based on an example in the Structural Specification and Functional-Style Syntax document.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
