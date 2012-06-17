package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class WebOnt_miscellaneous_203 {
	@Test
	public void testWebOnt_miscellaneous_203() {
		String premise = "<rdf:RDF\n"
				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "    xmlns=\"http://www.w3.org/1999/xhtml\"\n"
				+ "    xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/inconsistent203#\"\n"
				+ "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/inconsistent203\" >\n"
				+ "\n"
				+ "  <owl:Ontology/>\n"
				+ "\n"
				+ "  <owl:DatatypeProperty\n"
				+ "           rdf:ID=\"fp\" />\n"
				+ "  <owl:FunctionalProperty\n"
				+ "           rdf:about=\"#fp\" />\n"
				+ "  <owl:Thing>\n"
				+ "     <first:fp rdf:parseType=\"Literal\">\n"
				+ "<br />\n"
				+ "<img src=\"vn.png\" alt=\"Venn diagram\" longdesc=\"vn.html\" title=\"Venn\"></img>\n"
				+ "\n" + "</first:fp>\n"
				+ "     <first:fp rdf:parseType=\"Literal\"><br \n" + "></br>\n"
				+ "<img \n" + "src=\"vn.png\" title=\n" + "\"Venn\" alt\n"
				+ "=\"Venn diagram\" longdesc=\n" + "\"vn.html\" />\n" + "</first:fp>\n"
				+ "   </owl:Thing>\n" + "</rdf:RDF>";
		String conclusion = "";
		String id = "WebOnt_miscellaneous_203";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "This shows that initial whitespace in an rdf:XMLLiteral (http://www.w3.org/TR/rdf-concepts/#section-XMLLiteral) is significant within OWL.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
