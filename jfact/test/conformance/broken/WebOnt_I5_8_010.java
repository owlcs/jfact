package conformance.broken;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class WebOnt_I5_8_010 {
	@Test
	public void testWebOnt_I5_8_010() {
		//		String premise = "<rdf:RDF\n"
		//				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
		//				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
		//				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
		//				+ "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/premises010\" >\n"
		//				+ "  <owl:Ontology/>\n"
		//				+ "  <owl:DatatypeProperty rdf:ID=\"p\">\n"
		//				+ "    <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\" />\n"
		//				+ "  </owl:DatatypeProperty>\n"
		//				+ "  <rdf:Description rdf:ID=\"john\">\n"
		//				+ "    <rdf:type>\n"
		//				+ "      <owl:Restriction>\n"
		//				+ "        <owl:onProperty rdf:resource=\"#p\"/>\n"
		//				+ "        <owl:someValuesFrom rdf:resource=\"http://www.w3.org/2001/XMLSchema#nonPositiveInteger\" />\n"
		//				+ "      </owl:Restriction>\n"
		//				+ "   </rdf:type>\n"
		//				+ "  </rdf:Description>\n</rdf:RDF>";
		//		String conclusion = "<rdf:RDF\n"
		//				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
		//				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
		//				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
		//				+ "    xmlns:first=\"http://www.w3.org/2002/03owlt/I5.8/premises010#\"\n"
		//				+ "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions010\" >\n"
		//				+ "  <owl:Ontology/>\n"
		//				+ "  <owl:DatatypeProperty rdf:about=\"premises010#p\"/>\n"
		//				+ "  <owl:Thing rdf:about=\"premises010#john\">\n"
		//				+ "    <first:p rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">0</first:p>\n"
		//				+ "  </owl:Thing></rdf:RDF>";
		String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
				+ "Ontology(\n"
				+ "Declaration(DataProperty(<urn:t#p>))\n"
				+ "DataPropertyRange(<urn:t#p> xsd:nonNegativeInteger)\n"
				+ "ClassAssertion(DataSomeValuesFrom(<urn:t#p> xsd:nonPositiveInteger) <urn:t#john>)\n)";
		String conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
				+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
				+ "Ontology(\nDeclaration(DataProperty(<urn:t#p>))\n"
				+ "ClassAssertion(owl:Thing <urn:t#john>)\n"
				+ "DataPropertyAssertion(<urn:t#p> <urn:t#john> \"0\"^^xsd:int)\n)";
		String id = "WebOnt_I5_8_010";
		TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
		String d = "0 is the only <code>xsd:nonNegativeInteger</code> which is\n"
				+ "also an <code>xsd:nonPositiveInteger</code>.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		//r.getConfiguration().setLoggingActive(true);
		r.run();
	}
}
