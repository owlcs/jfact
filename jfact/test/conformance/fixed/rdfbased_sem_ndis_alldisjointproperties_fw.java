package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_ndis_alldisjointproperties_fw {
	@Test
	public void testrdfbased_sem_ndis_alldisjointproperties_fw() {
		//XXX test modified because of ontology not compliant with OWL 2
		//XXX without declarations, some properties default to datatype properties and some to annotation properties
		String premise = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+ "    xmlns:ex=\"http://www.example.org#\"\n"
				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
				//added
				+ "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
				+ "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
				+ "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p3\"/>\n"
				//end added
				+ "  <owl:AllDisjointProperties rdf:about=\"http://www.example.org#z\">\n"
				+ "    <owl:members rdf:parseType=\"Collection\">\n"
				+ "      <rdf:Description rdf:about=\"http://www.example.org#p1\"/>\n"
				+ "      <rdf:Description rdf:about=\"http://www.example.org#p2\"/>\n"
				+ "      <rdf:Description rdf:about=\"http://www.example.org#p3\"/>\n"
				+ "    </owl:members>\n" + "  </owl:AllDisjointProperties>\n"
				+ "  <rdf:Description rdf:about=\"http://www.example.org#s\">\n"
				+ "    <ex:p1 rdf:resource=\"http://www.example.org#o\"/>\n"
				+ "    <ex:p2 rdf:resource=\"http://www.example.org#o\"/>\n"
				+ "  </rdf:Description>\n" + "</rdf:RDF>";
		String conclusion = "";
		String id = "rdfbased_sem_ndis_alldisjointproperties_fw";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "All the members of an owl:AllDisjointProperties construct are mutually disjoint properties.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
