package conformance.broken;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class Datatype_Float_Discrete_001 {
    @Test
    public void testDatatype_Float_Discrete_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/ontology/\"\n"
                + "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
                + "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "  xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "  xmlns:xsd = \"http://www.w3.org/2001/XMLSchema#\" >\n"
                + "\n"
                + "<owl:Ontology/>\n"
                + "\n"
                + "<owl:DatatypeProperty rdf:about=\"dp\" />\n"
                + "\n"
                + "<rdf:Description rdf:about=\"a\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"dp\" />\n"
                + "      <owl:someValuesFrom>\n"
                + "        <rdfs:Datatype>\n"
                + "          <owl:onDatatype rdf:resource=\"http://www.w3.org/2001/XMLSchema#float\" />\n"
                + "          <owl:withRestrictions rdf:parseType=\"Collection\">\n"
                + "            <rdf:Description>\n"
                + "              <xsd:minExclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">0.0</xsd:minExclusive>\n"
                + "            </rdf:Description>\n"
                + "            <rdf:Description>\n"
                + "              <xsd:maxExclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">1.401298464324817e-45</xsd:maxExclusive>\n"
                + "            </rdf:Description>\n"
                + "          </owl:withRestrictions>\n" + "        </rdfs:Datatype>\n"
                + "      </owl:someValuesFrom>\n" + "    </owl:Restriction>\n"
                + "  </rdf:type>\n" + "</rdf:Description>\n" + "\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "Datatype_Float_Discrete_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The value space of xsd:float is discrete, shown with range defined on 0x00000000 and 0x00000001";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
