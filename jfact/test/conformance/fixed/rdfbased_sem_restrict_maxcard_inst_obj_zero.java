package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_restrict_maxcard_inst_obj_zero {
	@Test
	public void testrdfbased_sem_restrict_maxcard_inst_obj_zero() {
		//XXX test modified because of ontology not compliant with OWL 2
		String premise = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+ "    xmlns:ex=\"http://www.example.org#\"\n"
				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
				// added
				+ "<owl:Class rdf:about=\"http://www.example.org#z\"/>"
				+ "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>"
				// end add
				+ "  <ex:z rdf:about=\"http://www.example.org#w\">\n"
				+ "    <ex:p rdf:resource=\"http://www.example.org#x\"/>\n"
				+ "  </ex:z>\n"
				+ "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
				//added
				+ "<rdfs:subClassOf><rdf:Description>"
				+ "<rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Restriction\"/>"
				// end add
				+ "    <owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\"\n"
				+ "    >0</owl:maxCardinality>\n"
				+ "    <owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
				+ "  </rdf:Description>\n</rdfs:subClassOf></rdf:Description>\n"
				+ "</rdf:RDF>";
		String conclusion = "";
		String id = "rdfbased_sem_restrict_maxcard_inst_obj_zero";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "If an individual w is an instance of the max-0-cardinality restriction on property p, then there cannot be any triple w p x.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
