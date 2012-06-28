package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class rdfbased_sem_enum_inst_included {
    @Test
    public void testrdfbased_sem_enum_inst_included() {
        //XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#e\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#e\">\n"
                + "    <owl:oneOf rdf:parseType=\"Collection\">\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#x\"/>\n"
                + "      <rdf:Description rdf:about=\"http://www.example.org#y\"/>\n"
                + "    </owl:oneOf>\n" + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF\n"
                + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "    xmlns:ex=\"http://www.example.org#\"\n"
                + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#e\"/>\n"
                // end added
                + "  <ex:e rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <ex:e rdf:about=\"http://www.example.org#y\"/>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_enum_inst_included";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If a class defines an enumeration class expression from two individuals, than both individuals are instances of the class.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
