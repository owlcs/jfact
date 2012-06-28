package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_Rational_003 {
    @Test
    public void testNew_Feature_Rational_003() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/\"\n"
                + "  xmlns     = \"http://example.org/\"\n"
                + "  xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
                + "  xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "  xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "\n"
                + "<owl:Ontology/>\n"
                + "\n"
                + "<owl:DatatypeProperty rdf:about=\"dp\" />\n"
                + "\n"
                + "<rdf:Description rdf:about=\"a\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"dp\" />\n"
                + "      <owl:allValuesFrom>\n"
                + "        <rdfs:Datatype>\n"
                + "          <owl:oneOf>\n"
                + "            <rdf:Description>\n"
                + "              <rdf:first rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">0.3333333333333333</rdf:first>\n"
                + "              <rdf:rest>\n"
                + "                <rdf:Description>\n"
                + "                  <rdf:first rdf:datatype=\"http://www.w3.org/2002/07/owl#rational\">1/3</rdf:first>\n"
                + "                  <rdf:rest rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"/>\n"
                + "                </rdf:Description>\n"
                + "              </rdf:rest>\n"
                + "            </rdf:Description>\n"
                + "          </owl:oneOf>\n"
                + "        </rdfs:Datatype>\n"
                + "      </owl:allValuesFrom>\n"
                + "    </owl:Restriction>\n"
                + "  </rdf:type>\n"
                + "</rdf:Description>\n"
                + "\n"
                + "<rdf:Description rdf:about=\"a\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"dp\" />\n"
                + "      <owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality>\n"
                + "    </owl:Restriction>\n" + "  </rdf:type>\n" + "</rdf:Description>\n"
                + "\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "New_Feature_Rational_003";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "A consistent ontology demonstrating owl:rational is different from xsd:decimal.  The decimal literal requires 16 digits, the minimum required for conformance.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
