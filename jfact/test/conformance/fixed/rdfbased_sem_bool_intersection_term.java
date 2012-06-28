package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_bool_intersection_term {
    @Test
    public void testrdfbased_sem_bool_intersection_term() {
        //XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                //added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                //end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                //added
                + "<rdfs:subClassOf><owl:Class>"
                //end added
                + "    <owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#x\"/>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#y\"/>\n"
                + "    </owl:intersectionOf>\n"
                //added
                + "</owl:Class></rdfs:subClassOf>"
                //end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                //added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                //end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                + "    <rdfs:subClassOf rdf:resource=\"http://www.example.org#x\"/>\n"
                + "    <rdfs:subClassOf rdf:resource=\"http://www.example.org#y\"/>\n"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_bool_intersection_term";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If a class is an intersection of other classes, then the original class is a subclass of each of the other classes.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
