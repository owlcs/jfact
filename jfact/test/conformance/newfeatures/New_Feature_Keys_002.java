package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_Keys_002 {
	@Test
	public void testNew_Feature_Keys_002() {
		String premise = "<?xml version=\"1.0\"?>\n" + "<rdf:RDF\n"
				+ "  xml:base  = \"http://example.org/\"\n"
				+ "  xmlns     = \"http://example.org/\"\n"
				+ "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
				+ "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
				+ "\n" + "<owl:Ontology/>\n" + "\n"
				+ "<owl:DatatypeProperty rdf:about=\"hasSSN\" />\n" + "\n"
				+ "<rdf:Description rdf:about=\"http://www.w3.org/2002/07/owl#Thing\">\n"
				+ "  <owl:hasKey rdf:parseType=\"Collection\">\n"
				+ "    <rdf:Description rdf:about=\"hasSSN\" />\n" + "  </owl:hasKey>\n"
				+ "</rdf:Description>\n" + "\n"
				+ "<rdf:Description rdf:about=\"Peter\">\n"
				+ "  <hasSSN>123-45-6789</hasSSN>\n" + "</rdf:Description>\n" + "\n"
				+ "<rdf:Description rdf:about=\"Peter_Griffin\">\n"
				+ "  <hasSSN>123-45-6789</hasSSN>\n" + "</rdf:Description>\n" + "\n"
				+ "<rdf:Description rdf:about=\"Peter\">\n"
				+ "  <owl:differentFrom rdf:resource=\"Peter_Griffin\" />\n"
				+ "</rdf:Description>\n" + "\n" + "</rdf:RDF>";
		String conclusion = "";
		String id = "New_Feature_Keys_002";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "Demonstrates use of a key axiom to cause a trivial inconsistency based on an example in the Structural Specification and Functional-Style Syntax document.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
