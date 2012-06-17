package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class WebOnt_I5_8_006 {
	@Test
	public void testWebOnt_I5_8_006() {
		String premise = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns:first=\"http://www.w3.org/2002/03owlt/I5.8/premises006#\"\n"
				+ "    xmlns:second=\"http://www.w3.org/2002/03owlt/I5.8/conclusions006#\"\n"
				+ "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/premises006\" >\n"
				+ "  <owl:Ontology/>\n" + "  <owl:DatatypeProperty rdf:ID=\"p\">\n"
				+ "    <rdfs:range rdf:resource=\n"
				+ "  \"http://www.w3.org/2001/XMLSchema#byte\" />\n"
				+ "  </owl:DatatypeProperty>\n" + "</rdf:RDF>";
		String conclusion = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns:first=\"http://www.w3.org/2002/03owlt/I5.8/premises006#\"\n"
				+ "    xmlns:second=\"http://www.w3.org/2002/03owlt/I5.8/conclusions006#\"\n"
				+ "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions006\" >\n"
				+ "  <owl:Ontology/>\n"
				+ "  <owl:DatatypeProperty rdf:about=\"premises006#p\">\n"
				+ "    <rdfs:range rdf:resource=\n"
				+ "  \"http://www.w3.org/2001/XMLSchema#short\" />\n"
				+ "  </owl:DatatypeProperty>\n" + "\n" + "</rdf:RDF>";
		String id = "WebOnt_I5_8_006";
		TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
		String d = "All <code>xsd:byte</code>\n" + "are <code>xsd:short</code>.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
