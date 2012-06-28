package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class WebOnt_miscellaneous_204 {
    @Test
    public void testWebOnt_miscellaneous_204() {
        String premise = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns=\"http://www.w3.org/1999/xhtml\"\n"
                + "    xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/inconsistent204#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/inconsistent204\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:FunctionalProperty rdf:ID=\"fp\"/>\n"
                + "   <owl:DatatypeProperty rdf:about=\"#fp\"/>\n" + "   <owl:Thing>\n"
                + "     <first:fp rdf:parseType=\"Literal\"\n"
                + "     ><span xml:lang='en'>\n" + "        <b>Good!</b></span\n"
                + "     ></first:fp>\n" + "     <first:fp rdf:parseType=\"Literal\"\n"
                + "     ><span xml:lang='en'>\n" + "        <b>Bad!</b></span\n"
                + "     ></first:fp>\n" + "   </owl:Thing>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_miscellaneous_204";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "This shows a simple inconsistency depending on the datatype\n"
                + "rdf:XMLLiteral.\n"
                + "This file is inconsistent with a datatype map which supports rdf:XMLLiteral,\n"
                + "and consistent otherwise.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
