package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_DisjointDataProperties_002 {
    @Test
    public void testNew_Feature_DisjointDataProperties_002() {
        String premise = "<?xml version=\"1.0\"?>\n" + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\"\n"
                + "  xmlns     = \"http://example.org/\"\n"
                + "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
                + "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + "\n" + "<owl:Ontology/>\n" + "\n"
                + "<owl:DatatypeProperty rdf:about=\"hasName\" />\n"
                + "<owl:DatatypeProperty rdf:about=\"hasAddress\" />\n"
                + "<owl:DatatypeProperty rdf:about=\"hasZip\" />\n" + "\n"
                + "<owl:AllDisjointProperties>\n"
                + "  <owl:members rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"hasName\" />\n"
                + "    <rdf:Description rdf:about=\"hasAddress\" />\n"
                + "    <rdf:Description rdf:about=\"hasZip\" />\n" + "  </owl:members>\n"
                + "</owl:AllDisjointProperties>\n" + "\n"
                + "<rdf:Description rdf:about=\"Peter\">\n"
                + "  <hasName>Peter Griffin</hasName>\n" + "</rdf:Description>\n" + "\n"
                + "<rdf:Description rdf:about=\"Peter_Griffin\">\n"
                + "  <hasAddress>Peter Griffin</hasAddress>\n" + "</rdf:Description>\n"
                + "\n" + "<rdf:Description rdf:about=\"Petre\">\n"
                + "  <hasZip>Peter Griffin</hasZip>\n" + "</rdf:Description>\n" + "\n"
                + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n" + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\"\n"
                + "  xmlns     = \"http://example.org/\"\n"
                + "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
                + "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + "\n" + "<owl:Ontology/>\n" + "\n" + "<owl:AllDifferent>\n"
                + "  <owl:distinctMembers rdf:parseType=\"Collection\">\n"
                + "    <rdf:Description rdf:about=\"Peter\" />\n"
                + "    <rdf:Description rdf:about=\"Peter_Griffin\" />\n"
                + "    <rdf:Description rdf:about=\"Petre\" />\n"
                + "  </owl:distinctMembers>\n" + "</owl:AllDifferent>\n" + "\n"
                + "</rdf:RDF>";
        String id = "New_Feature_DisjointDataProperties_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Demonstrates use of a ternary disjoint data properties axiom to infer different individuals.  Adapted from test New-Feature-DisjointDataProperties-001.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
