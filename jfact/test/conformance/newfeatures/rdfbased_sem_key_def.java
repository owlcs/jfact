package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_key_def {
    @Test
    public void testrdfbased_sem_key_def() {
        //XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                //added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:DatatypeProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                //end added
                + "  <ex:c rdf:about=\"http://www.example.org#x\">\n"
                + "    <ex:p1 rdf:resource=\"http://www.example.org#z\"/>\n"
                + "    <ex:p2>data</ex:p2>\n" + "  </ex:c>\n"
                + "  <ex:c rdf:about=\"http://www.example.org#y\">\n"
                + "    <ex:p1 rdf:resource=\"http://www.example.org#z\"/>\n"
                + "    <ex:p2>data</ex:p2>\n" + "  </ex:c>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                + "    <owl:hasKey rdf:parseType=\"Collection\">\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p1\"/>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p2\"/>\n"
                + "    </owl:hasKey>\n" + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\">\n"
                + "    <owl:sameAs rdf:resource=\"http://www.example.org#y\"/>\n"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_key_def";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For two triples matching the conditions of a key axiom the subjects are identified.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
