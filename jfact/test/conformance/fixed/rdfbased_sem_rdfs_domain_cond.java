package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_rdfs_domain_cond {
    @Test
    public void testrdfbased_sem_rdfs_domain_cond() {
        //XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                //added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                //end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#u\">\n"
                + "    <ex:p rdf:resource=\"http://www.example.org#v\"/>\n"
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\">\n"
                + "    <rdfs:domain rdf:resource=\"http://www.example.org#c\"/>\n"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                //added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                //end added
                + "  <ex:c rdf:about=\"http://www.example.org#u\"/>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_rdfs_domain_cond";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The left hand side individual in a given triple is entailed to be an instance of the domain of the predicate.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
