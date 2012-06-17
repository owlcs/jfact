package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_ReflexiveProperty_001 {
	@Test
	public void testNew_Feature_ReflexiveProperty_001() {
		String premise = "<?xml version=\"1.0\"?>\n" + "<rdf:RDF\n"
				+ "  xml:base  = \"http://example.org/\"\n"
				+ "  xmlns     = \"http://example.org/\"\n"
				+ "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
				+ "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
				+ "\n" + "<owl:Ontology/>\n" + "\n"
				+ "<owl:ObjectProperty rdf:about=\"knows\" />\n"
				+ "<owl:NamedIndividual rdf:about=\"Peter\" />\n" + "\n"
				+ "<owl:ReflexiveProperty rdf:about=\"knows\" />\n" + "\n" + "</rdf:RDF>";
		String conclusion = "<?xml version=\"1.0\"?>\n" + "<rdf:RDF\n"
				+ "  xml:base  = \"http://example.org/\"\n"
				+ "  xmlns     = \"http://example.org/\"\n"
				+ "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
				+ "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
				+ "\n" + "<owl:Ontology/>\n" + "\n"
				+ "<owl:ObjectProperty rdf:about=\"knows\" />\n" + "\n"
				+ "<rdf:Description rdf:about=\"Peter\">\n"
				+ "  <knows rdf:resource=\"Peter\" />\n" + "</rdf:Description>\n" + "\n"
				+ "</rdf:RDF>";
		String id = "New_Feature_ReflexiveProperty_001";
		TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
		String d = "Uses a reflexive object property axiom to infer a property value based on the example in the Structural Specification and Functional-Style Syntax document.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
