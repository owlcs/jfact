package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class WebOnt_allValuesFrom_001 {
    @Test
    public void testWebOnt_allValuesFrom_001() {
        String premise = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:first=\"http://www.w3.org/2002/03owlt/allValuesFrom/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/allValuesFrom/premises001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"r\">\n"
                + "      <rdfs:subClassOf>\n"
                + "        <owl:Restriction>\n"
                + "            <owl:onProperty rdf:resource=\"#p\"/>\n"
                + "            <owl:allValuesFrom rdf:resource=\"#c\"/>\n"
                + "        </owl:Restriction>\n"
                + "      </rdfs:subClassOf>\n"
                + "    </owl:Class>\n"
                + "    <owl:ObjectProperty rdf:ID=\"p\"/>\n"
                + "    <owl:Class rdf:ID=\"c\"/>\n"
                + "    <first:r rdf:ID=\"i\">\n"
                + "       <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/>\n"
                + "       <first:p>\n" + "         <owl:Thing rdf:ID=\"o\" />\n"
                + "       </first:p>\n" + "    </first:r>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:first=\"http://www.w3.org/2002/03owlt/allValuesFrom/premises001#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/allValuesFrom/conclusions001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <first:c rdf:about=\"premises001#o\">\n"
                + "       <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/>\n"
                + "    </first:c>\n" + "    <owl:Class rdf:about=\"premises001#c\"/>\n"
                + "</rdf:RDF>";
        String id = "WebOnt_allValuesFrom_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A simple example.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
