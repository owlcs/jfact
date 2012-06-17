package conformance.broken;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class WebOnt_someValuesFrom_003 {
	@Test
	public void testWebOnt_someValuesFrom_003() {
		String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
				+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
				+ "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
				+ "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
				+ "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
				+ "Ontology(\n"
				+ "Declaration(Class(<urn:person>))\n"
				+ "EquivalentClasses(<urn:person> ObjectSomeValuesFrom(<urn:parent> <urn:person>))\n"
				+ "Declaration(ObjectProperty(<urn:parent>))\n"
				+ "ClassAssertion(<urn:person> <urn:fred>))";
		//				"<rdf:RDF\n"
		//				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
		//				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
		//				+ "    xmlns:first=\"http://www.w3.org/2002/03owlt/someValuesFrom/premises003#\"\n"
		//				+ "    xml:base=\"http://www.w3.org/2002/03owlt/someValuesFrom/premises003\" >\n"
		//				+ "   <owl:Ontology/>\n"
		//				+ "   <owl:Class rdf:ID=\"person\">\n"
		//				+ "     <owl:equivalentClass><owl:Restriction>\n"
		//				+ "         <owl:onProperty><owl:ObjectProperty rdf:ID=\"parent\"/></owl:onProperty>\n"
		//				+ "         <owl:someValuesFrom rdf:resource=\"#person\" />\n"
		//				+ "       </owl:Restriction></owl:equivalentClass>\n"
		//				+ "    </owl:Class>\n"
		//				+ "    <first:person rdf:ID=\"fred\" />\n" + "\n"
		//				+ "</rdf:RDF>";
		String conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
				+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
				+ "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
				+ "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
				+ "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
				+ "Ontology(\n"
				+ "Declaration(ObjectProperty(<urn:parent>))\n"
				+
				//"Declaration(ObjectProperty(<urn:parent>))\n"+
				//"ClassAssertion(owl:Thing <urn:fred>)\n"+
				"ObjectPropertyAssertion(<urn:parent> <urn:fred> _:genid2)\n"
				+ "ClassAssertion(owl:Thing _:genid3)\n"
				+ "ClassAssertion(owl:Thing _:genid2)\n"
				+ "ObjectPropertyAssertion(<urn:parent> _:genid2 _:genid3))";
		//				"<rdf:RDF\n"
		//				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
		//				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
		//				+ "    xmlns:first=\"http://www.w3.org/2002/03owlt/someValuesFrom/premises003#\"\n"
		//				+ "    xml:base=\"http://www.w3.org/2002/03owlt/someValuesFrom/conclusions003\" >\n"
		//				+ "   <owl:Ontology/>\n"
		//				+ "   <owl:ObjectProperty rdf:about=\"premises003#parent\"/>\n"
		//				+ "   <owl:Thing rdf:about=\"premises003#fred\">"
		//				+ "<first:parent><owl:Thing><first:parent><owl:Thing/></first:parent></owl:Thing>\n"
		//				+ "     </first:parent>\n" + "   </owl:Thing>\n" + "</rdf:RDF>";
		String id = "WebOnt_someValuesFrom_003";
		TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
		String d = "A simple infinite loop for implementors to avoid.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		//r.printPremise();System.out.println("WebOnt_someValuesFrom_003.testWebOnt_someValuesFrom_003()");r.printConsequence();
		r.run();
	}
}
