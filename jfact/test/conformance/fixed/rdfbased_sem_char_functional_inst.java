package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_char_functional_inst {
	@Test
	public void testrdfbased_sem_char_functional_inst() {
		//XXX test modified because of ontology not compliant with OWL 2
		String premise = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+ "    xmlns:ex=\"http://www.example.org#\"\n"
				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
				//added
				+ "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
				//end added
				+ "  <owl:FunctionalProperty rdf:about=\"http://www.example.org#p\"/>\n"
				+ "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
				+ "    <ex:p rdf:resource=\"http://www.example.org#y1\"/>\n"
				+ "    <ex:p rdf:resource=\"http://www.example.org#y2\"/>\n"
				+ "  </rdf:Description>\n" + "</rdf:RDF>";
		String conclusion = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+ "    xmlns:ex=\"http://www.example.org#\"\n"
				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
				+ "  <rdf:Description rdf:about=\"http://www.example.org#y1\">\n"
				+ "    <owl:sameAs rdf:resource=\"http://www.example.org#y2\"/>\n"
				+ "  </rdf:Description>\n" + "</rdf:RDF>";
		String id = "rdfbased_sem_char_functional_inst";
		TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
		String d = "For two triples with the same functional property as their predicates and with the same subject, the objects are the same.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
