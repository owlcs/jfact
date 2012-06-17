package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class WebOnt_allValuesFrom_002 {
	@Test
	public void testWebOnt_allValuesFrom_002() {
		String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
				+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
				+ "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
				+ "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
				+ "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
				+ "Ontology(\n"
				+ "Declaration(Class(<urn:t:t#c>))\n"
				+ "Declaration(Class(<urn:t:t#r>))\n"
				+ "Declaration(ObjectProperty(<urn:t:t#p>))\n"
				+ "SubClassOf(<urn:t:t#r> ObjectAllValuesFrom(<urn:t:t#p> <urn:t:t#c>))\n"
				+ "ClassAssertion(<urn:t:t#r> <urn:t:t#i>)\n"
				+ "ClassAssertion(owl:Thing <urn:t:t#i>)\n"
				+ "ClassAssertion(owl:Thing <urn:t:t#newind>))";
		String conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
				+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
				+ "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
				+ "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
				+ "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
				+ "Ontology(\n"
				+ "Declaration(Class(<urn:t:t#c>))\n"
				+ "Declaration(Class(<urn:t:t#r>))\n"
				+ "Declaration(ObjectProperty(<urn:t:t#p>))\n"
				+
				//"ClassAssertion(owl:Thing <urn:t:t#i>)\n"+
				//"ClassAssertion(owl:Thing <urn:t:t#newind>)\n"+
				"ObjectPropertyAssertion(<urn:t:t#p> <urn:t:t#i> <urn:t:t#newind>)\n"
				+ "ClassAssertion(<urn:t:t#c> <urn:t:t#newind>)\n" +
				//"ClassAssertion(owl:Thing <urn:t:t#newind>)" +
				")";
		String id = "WebOnt_allValuesFrom_002";
		TestClasses tc = TestClasses.valueOf("NEGATIVE_IMPL");
		String d = "Another simple example; contrast with <code>owl:someValuesFrom</code>.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
