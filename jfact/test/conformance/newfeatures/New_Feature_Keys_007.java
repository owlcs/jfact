package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_Keys_007 {
	@Test
	public void testNew_Feature_Keys_007() {
		String premise = "<?xml version=\"1.0\"?>\n" + "<rdf:RDF\n"
				+ "  xml:base  = \"http://example.org/\"\n"
				+ "  xmlns     = \"http://example.org/\"\n"
				+ "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
				+ "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
				+ "\n" + "<owl:Ontology/>\n" + "\n"
				+ "<owl:Class rdf:about=\"Person\" />\n" + "\n"
				+ "<owl:Class rdf:about=\"Man\" />\n" + "\n"
				+ "<owl:DatatypeProperty rdf:about=\"hasSSN\" />\n" + "\n"
				+ "<owl:ObjectProperty rdf:about=\"marriedTo\" />\n" + "\n"
				+ "<rdf:Description rdf:about=\"Person\">\n"
				+ "  <owl:hasKey rdf:parseType=\"Collection\">\n"
				+ "    <rdf:Description rdf:about=\"hasSSN\" />\n" + "  </owl:hasKey>\n"
				+ "</rdf:Description>\n" + "\n"
				+ "<rdf:Description rdf:about=\"Peter\">\n"
				+ "  <hasSSN>123-45-6789</hasSSN>\n"
				+ "  <rdf:type rdf:resource=\"Person\" />\n" + "</rdf:Description>\n"
				+ "\n" + "<rdf:Description rdf:about=\"Lois\">\n" + "  <rdf:type>\n"
				+ "    <owl:Restriction>\n"
				+ "      <owl:onProperty rdf:resource=\"marriedTo\" />\n"
				+ "      <owl:someValuesFrom>\n" + "        <owl:Class>\n"
				+ "          <owl:intersectionOf rdf:parseType=\"Collection\">\n"
				+ "            <rdf:Description rdf:about=\"Man\" />\n"
				+ "            <owl:Restriction>\n"
				+ "              <owl:onProperty rdf:resource=\"hasSSN\" />\n"
				+ "              <owl:hasValue>123-45-6789</owl:hasValue>\n"
				+ "            </owl:Restriction>\n"
				+ "          </owl:intersectionOf>\n" + "        </owl:Class>\n"
				+ "      </owl:someValuesFrom>\n" + "    </owl:Restriction>\n"
				+ "  </rdf:type>\n" + "</rdf:Description>\n" + "\n" + "</rdf:RDF>";
		String conclusion = "<?xml version=\"1.0\"?>\n" + "<rdf:RDF\n"
				+ "  xml:base  = \"http://example.org/\"\n"
				+ "  xmlns     = \"http://example.org/\"\n"
				+ "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
				+ "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
				+ "\n" + "<owl:Ontology/>\n" + "\n" + "<owl:Class rdf:about=\"Man\" />\n"
				+ "\n" + "<Man rdf:about=\"Peter\" />\n" + "\n" + "</rdf:RDF>";
		String id = "New_Feature_Keys_007";
		TestClasses tc = TestClasses.valueOf("NEGATIVE_IMPL");
		String d = "Demonstrates that a key axiom only applies to named individuals.  Based on an example in the Structural Specification and Functional-Style Syntax document.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
