package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_restrict_maxqcr_inst_obj_zero {
	@Test
	public void testrdfbased_sem_restrict_maxqcr_inst_obj_zero() {
		//XXX test modified because of ontology not compliant with OWL 2
		String premise = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+ "    xmlns:ex=\"http://www.example.org#\"\n"
				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
				//added
				+ "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
				+ "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
				+ "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
				//end added
				+ "  <ex:z rdf:about=\"http://www.example.org#w\">\n"
				+ "    <ex:p>\n"
				+ "      <ex:c rdf:about=\"http://www.example.org#x\"/>\n"
				+ "    </ex:p>\n"
				+ "  </ex:z>\n"
				+ "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
				//added
				+ "<owl:equivalentClass><owl:Restriction>"
				//end added
				+ "    <owl:maxQualifiedCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\"\n"
				+ "    >0</owl:maxQualifiedCardinality>\n"
				+ "    <owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
				+ "    <owl:onClass rdf:resource=\"http://www.example.org#c\"/>\n"
				//added
				+ "</owl:Restriction></owl:equivalentClass>"
				//end added
				+ "  </rdf:Description>\n" + "</rdf:RDF>";
		String conclusion = "";
		String id = "rdfbased_sem_restrict_maxqcr_inst_obj_zero";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "If an individual w is an instance of the max-0-QCR on property p to class c, then there cannot be any triple w p x with x in c.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
