package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class WebOnt_miscellaneous_202 {
    @Test
    public void testWebOnt_miscellaneous_202() {
        String premise =
        //				"<rdf:RDF\n"
        //				+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
        //				+ "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
        //				+ "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
        //				+ "    xmlns=\"http://www.w3.org/1999/xhtml\"\n"
        //				+ "    xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent202#\"\n"
        //				+ "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent202\" >\n"
        //				+ "\n"
        //				+ "  <owl:Ontology/>\n"
        //				+ "  <owl:DatatypeProperty rdf:ID=\"fp\" />\n"
        //				+ "  <owl:FunctionalProperty\n"
        //				+ "           rdf:about=\"#fp\" />\n"
        //				+ "  <owl:Thing>\n"
        //				+ "     <first:fp rdf:parseType=\"Literal\"><br />\n"
        //				+ "<img src=\"vn.png\" alt=\"Venn diagram\" longdesc=\"vn.html\" title=\"Venn\"></img></first:fp>\n"
        //				+ "     <first:fp rdf:parseType=\"Literal\"><br \n"
        //				+ "></br>\n" + "<img \n" + "src=\"vn.png\" title=\n"
        //				+ "\"Venn\" alt\n" + "=\"Venn diagram\" longdesc=\n"
        //				+ "\"vn.html\" /></first:fp>\n" + "   </owl:Thing>\n"
        //				+ "</rdf:RDF>";
        "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(\n" + "Declaration(DataProperty(<urn:fp>))\n"
                + "FunctionalDataProperty(<urn:fp>)\n"
                + "ClassAssertion(owl:Thing <urn:id2>)\n"
                + "DataPropertyAssertion(<urn:fp> <urn:id2> \"<br></br>\n\n"
                + "<img></img>\"^^rdf:XMLLiteral)\n"
                + "DataPropertyAssertion(<urn:fp> <urn:id2> \"<br></br>\n"
                + "<img></img>\"^^rdf:XMLLiteral)\n" + ")";
        //TODO this is silly, to pass this test the reasoner needs a lot of extra processing for literals... maybe the OWL API should do this, it can be useful for users in general and it's actually down to the equals method for XML Literals.
        String conclusion = "";
        String id = "WebOnt_miscellaneous_202";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "This shows that insignificant whitespace in an rdf:XMLLiteral is not significant within OWL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        //r.getConfiguration().setLoggingActive(true);
        r.run();
    }
}
