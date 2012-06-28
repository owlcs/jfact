package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_eqdis_disprop_eqprop {
    @Test
    public void testrdfbased_sem_eqdis_disprop_eqprop() {
        //XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                //added
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                //end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#s2\">\n"
                + "    <ex:p2 rdf:resource=\"http://www.example.org#o2\"/>\n"
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s1\">\n"
                + "    <ex:p1 rdf:resource=\"http://www.example.org#o1\"/>\n"
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "    <owl:equivalentProperty rdf:resource=\"http://www.example.org#p2\"/>\n"
                + "    <owl:propertyDisjointWith rdf:resource=\"http://www.example.org#p2\"/>\n"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_disprop_eqprop";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Two non-empty properties cannot both be equivalent and disjoint.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
