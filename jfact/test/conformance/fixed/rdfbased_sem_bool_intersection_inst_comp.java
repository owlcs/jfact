package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_bool_intersection_inst_comp {
	@Test
	public void testrdfbased_sem_bool_intersection_inst_comp() {
		//XXX test modified because of ontology not compliant with OWL 2
		String premise = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+ "    xmlns:ex=\"http://www.example.org#\"\n"
				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
				//added
				+ "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
				+ "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
				+ "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
				//end added
				+ "  <ex:x rdf:about=\"http://www.example.org#z\">\n"
				+ "    <rdf:type rdf:resource=\"http://www.example.org#y\"/>\n"
				+ "  </ex:x>\n"
				+ "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
				//added
				+ "<owl:equivalentClass><owl:Class>"
				//end added
				+ "    <owl:intersectionOf rdf:parseType=\"Collection\">\n"
				+ "      <rdf:Description rdf:about=\"http://www.example.org#x\"/>\n"
				+ "      <rdf:Description rdf:about=\"http://www.example.org#y\"/>\n"
				+ "    </owl:intersectionOf>\n"
				//added
				+ "</owl:Class></owl:equivalentClass>"
				//end added
				+ "  </rdf:Description>\n" + "</rdf:RDF>";
		String conclusion = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+ "    xmlns:ex=\"http://www.example.org#\"\n"
				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
				//added
				+ "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
				//end added
				+ "  <ex:c rdf:about=\"http://www.example.org#z\"/>\n" + "</rdf:RDF>";
		String id = "rdfbased_sem_bool_intersection_inst_comp";
		TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
		String d = "An individual, which is an instance of every component class of an intersection, is an instance of the intersection class expression.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
