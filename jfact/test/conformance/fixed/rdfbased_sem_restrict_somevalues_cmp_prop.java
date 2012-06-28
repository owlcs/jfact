package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_restrict_somevalues_cmp_prop {
    @Test
    public void testrdfbased_sem_restrict_somevalues_cmp_prop() {
        //XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                //added
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                //end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                //added
                + "<owl:equivalentClass><owl:Restriction>"
                //end added
                + "    <owl:someValuesFrom rdf:resource=\"http://www.example.org#c\"/>\n"
                + "    <owl:onProperty>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#p1\">\n"
                + "        <rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p2\"/>\n"
                + "      </rdf:Description>\n"
                + "    </owl:onProperty>\n"
                //added
                + "</owl:Restriction></owl:equivalentClass>"
                //end added
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                //added
                + "<owl:equivalentClass><owl:Restriction>"
                //end added
                + "    <owl:someValuesFrom rdf:resource=\"http://www.example.org#c\"/>\n"
                + "    <owl:onProperty rdf:resource=\"http://www.example.org#p2\"/>\n"
                //added
                + "</owl:Restriction></owl:equivalentClass>"
                //end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                //added
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                //end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                + "    <rdfs:subClassOf rdf:resource=\"http://www.example.org#x2\"/>\n"
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_restrict_somevalues_cmp_prop";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An existential restriction on some property and some class is a sub class of another existential restriction on the same class but on a super property.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
