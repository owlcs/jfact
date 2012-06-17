package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_TopObjectProperty_001 {
	@Test
	public void testNew_Feature_TopObjectProperty_001() {
		String premise = "<?xml version=\"1.0\"?>\n"
				+ "<rdf:RDF\n"
				+ "  xml:base  = \"http://example.org/\"\n"
				+ "  xmlns     = \"http://example.org/\"\n"
				+ "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
				+ "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
				+ "\n"
				+ "<owl:Ontology/>\n"
				+ "\n"
				+ "<rdf:Description rdf:about=\"i\">\n"
				+ "  <rdf:type>\n"
				+ "    <owl:Class>\n"
				+ "      <owl:complementOf>\n"
				+ "        <owl:Restriction>\n"
				+ "          <owl:onProperty rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\" />\n"
				+ "          <owl:someValuesFrom rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" />\n"
				+ "        </owl:Restriction>\n" + "      </owl:complementOf>\n"
				+ "    </owl:Class>\n" + "  </rdf:type>\n" + "</rdf:Description>\n"
				+ "\n" + "</rdf:RDF>";
		String conclusion = "";
		String id = "New_Feature_TopObjectProperty_001";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "Demonstrates use of the top object property to create an inconsistency.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
