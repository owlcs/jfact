package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_DataQCR_001 {
    @Test
    public void testNew_Feature_DataQCR_001() {
        String premise = "<?xml version=\"1.0\"?>\n" + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\"\n"
                + "  xmlns     = \"http://example.org/\"\n"
                + "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
                + "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + "\n" + "<owl:Ontology/>\n" + "\n"
                + "<owl:DatatypeProperty rdf:about=\"hasName\" />\n" + "\n"
                + "<rdf:Description rdf:about=\"Meg\">\n"
                + "  <hasName>Meg Griffin</hasName>\n"
                + "  <hasName>Megan Griffin</hasName>\n" + "</rdf:Description>\n" + "\n"
                + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\"\n"
                + "  xmlns     = \"http://example.org/\"\n"
                + "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
                + "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + "\n"
                + "<owl:Ontology/>\n"
                + "\n"
                + "<owl:DatatypeProperty rdf:about=\"hasName\" />\n"
                + "\n"
                + "<rdf:Description rdf:about=\"Meg\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"hasName\" />\n"
                + "      <owl:minQualifiedCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minQualifiedCardinality>\n"
                + "      <owl:onDataRange rdf:resource=\"http://www.w3.org/2001/XMLSchema#string\" />\n"
                + "    </owl:Restriction>\n" + "  </rdf:type>\n" + "</rdf:Description>\n"
                + "\n" + "</rdf:RDF>";
        String id = "New_Feature_DataQCR_001";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates a qualified minimum cardinality data property restriction based on example in the Structural Specification and Functional-Style Syntax document.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
