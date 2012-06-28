package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_char_inversefunc_data {
    @Test
    public void testrdfbased_sem_char_inversefunc_data() {
        String premise = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <owl:InverseFunctionalProperty rdf:about=\"http://www.example.org#p\">\n"
                + "    <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#DatatypeProperty\"/>\n"
                + "  </owl:InverseFunctionalProperty>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                + "    <ex:p>data</ex:p>\n" + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                + "    <ex:p>data</ex:p>\n" + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                + "    <owl:sameAs rdf:resource=\"http://www.example.org#x2\"/>\n"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_char_inversefunc_data";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For two triples with the same inverse functional data property as their predicates and with the same data object, the subjects are the same. This test shows that the OWL 2 RDF-Based Semantics allows for IFDPs.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
