package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class WebOnt_I4_6_004 {
	@Test
	public void testWebOnt_I4_6_004() {
		String premise = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xml:base=\"http://www.w3.org/2002/03owlt/I4.6/premises004\" >\n"
				+ "    <owl:Ontology/>\n"
				+ "    <owl:Class rdf:about=\"nonconclusions004#C1\">\n"
				+ "       <owl:equivalentClass>\n"
				+ "           <owl:Class rdf:about=\"nonconclusions004#C2\"/>\n"
				+ "       </owl:equivalentClass>\n" + "    </owl:Class>\n" + "</rdf:RDF>";
		String conclusion = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xml:base=\"http://www.w3.org/2002/03owlt/I4.6/nonconclusions004\" >\n"
				+ "    <owl:Ontology/>\n" + "    <owl:Class rdf:ID=\"C1\">\n"
				+ "       <owl:sameAs>\n" + "           <owl:Class rdf:ID=\"C2\"/>\n"
				+ "       </owl:sameAs>\n" + "    </owl:Class>\n" + "</rdf:RDF>";
		String id = "WebOnt_I4_6_004";
		TestClasses tc = TestClasses.valueOf("NEGATIVE_IMPL");
		String d = "<code>owl:sameAs</code> is stronger than <code>owl:equivalentClass</code>.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
