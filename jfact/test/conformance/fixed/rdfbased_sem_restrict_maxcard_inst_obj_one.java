package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_restrict_maxcard_inst_obj_one {
	@Test
	public void testrdfbased_sem_restrict_maxcard_inst_obj_one() {
		//XXX test modified because of ontology not compliant with OWL 2
		String premise = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+ "    xmlns:ex=\"http://www.example.org#\"\n"
				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
				//added
				+ "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
				+ "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
				//end added
				+ "  <ex:z rdf:about=\"http://www.example.org#w\">\n"
				+ "    <ex:p rdf:resource=\"http://www.example.org#x1\"/>\n"
				+ "    <ex:p rdf:resource=\"http://www.example.org#x2\"/>\n"
				+ "  </ex:z>\n"
				+ "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
				//added
				+ "  <rdfs:subClassOf>\n"
				+ "  <owl:Restriction>\n"
				//end added
				+ "    <owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\"\n"
				+ "    >1</owl:maxCardinality>\n"
				+ "    <owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
				//added
				+ "  </owl:Restriction>\n" + "  </rdfs:subClassOf>\n"
				//end added
				+ "  </rdf:Description>\n" + "</rdf:RDF>";
		String conclusion = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+ "    xmlns:ex=\"http://www.example.org#\"\n"
				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
				+ "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
				+ "    <owl:sameAs rdf:resource=\"http://www.example.org#x2\"/>\n"
				+ "  </rdf:Description>\n" + "</rdf:RDF>";
		String id = "rdfbased_sem_restrict_maxcard_inst_obj_one";
		TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
		String d = "If an individual w is an instance of the max-1-cardinality restriction on property p, and if there are triples w p x1 and w p x2, then x1 equals x2.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
