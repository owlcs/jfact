package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_Rational_001 {
	@Test
	public void testNew_Feature_Rational_001() {
		String premise = "<?xml version=\"1.0\"?>\n"
				+ "<rdf:RDF\n"
				+ "  xml:base  = \"http://example.org/\"\n"
				+ "  xmlns     = \"http://example.org/\"\n"
				+ "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
				+ "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
				+ "\n"
				+ "<owl:Ontology/>\n"
				+ "\n"
				+ "<owl:DatatypeProperty rdf:about=\"dp\" />\n"
				+ "\n"
				+ "<rdf:Description rdf:about=\"a\">\n"
				+ "  <rdf:type>\n"
				+ "    <owl:Restriction>\n"
				+ "      <owl:onProperty rdf:resource=\"dp\" />\n"
				+ "      <owl:allValuesFrom rdf:resource=\"http://www.w3.org/2002/07/owl#rational\" />\n"
				+ "    </owl:Restriction>\n"
				+ "  </rdf:type>\n"
				+ "</rdf:Description>\n"
				+ "\n"
				+ "<rdf:Description rdf:about=\"a\">\n"
				+ "  <rdf:type>\n"
				+ "    <owl:Restriction>\n"
				+ "      <owl:onProperty rdf:resource=\"dp\" />\n"
				+ "      <owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality>\n"
				+ "    </owl:Restriction>\n" + "  </rdf:type>\n" + "</rdf:Description>\n"
				+ "\n" + "</rdf:RDF>";
		String conclusion = "";
		String id = "New_Feature_Rational_001";
		TestClasses tc = TestClasses.valueOf("CONSISTENCY");
		String d = "A consistent ontology using owl:rational";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
