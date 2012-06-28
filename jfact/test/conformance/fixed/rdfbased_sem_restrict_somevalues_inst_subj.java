package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_restrict_somevalues_inst_subj {
    @Test
    public void testrdfbased_sem_restrict_somevalues_inst_subj() {
        //XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                //added
                + "  <owl:Thing rdf:about=\"http://www.example.org#x\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                //end added
                + "    <owl:someValuesFrom rdf:resource=\"http://www.example.org#c\"/>\n"
                + "    <owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                //added
                + "</owl:Restriction></owl:equivalentClass>\n"
                // end added
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#w\">\n"
                + "    <ex:p>\n"
                + "      <ex:c rdf:about=\"http://www.example.org#x\"/>\n"
                + "    </ex:p>\n" + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Thing rdf:about=\"http://www.example.org#w\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                //end added
                + "  <ex:z rdf:about=\"http://www.example.org#w\"/>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_restrict_somevalues_inst_subj";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For a triple w p x, with x being an instance of a class c, the individual w is an instance of the existential restriction on p to c.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
