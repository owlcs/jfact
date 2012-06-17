package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_eqdis_different_irrflxv {
	@Test
	public void testrdfbased_sem_eqdis_different_irrflxv() {
		//XXX test modified because of ontology not compliant with OWL 2
		String premise = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+ "    xmlns:ex=\"http://www.example.org#\"\n"
				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
				//added
				+ "<owl:Thing rdf:about=\"http://www.example.org#x\"/>\n"
				+ "<owl:Thing rdf:about=\"http://www.example.org#z\"/>\n"
				+ "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
				+ "    <owl:sameAs rdf:resource=\"http://www.example.org#x\"/>\n"
				+ "  </rdf:Description>\n"
				//end added
				+ "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
				+ "    <owl:differentFrom rdf:resource=\"http://www.example.org#z\"/>\n"
				+ "  </rdf:Description>\n" + "</rdf:RDF>";
		String conclusion = "";
		String id = "rdfbased_sem_eqdis_different_irrflxv";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "Diversity of two individuals is irreflexive.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
