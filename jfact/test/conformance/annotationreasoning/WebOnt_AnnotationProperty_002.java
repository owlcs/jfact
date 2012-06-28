package conformance.annotationreasoning;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class WebOnt_AnnotationProperty_002 {
    @Test
    public void testWebOnt_AnnotationProperty_002() {
        String premise = "<rdf:RDF\n"
                + "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "xmlns:first=\"http://www.w3.org/2002/03owlt/AnnotationProperty/premises002#\"\n"
                + "xml:base=\"http://www.w3.org/2002/03owlt/AnnotationProperty/premises002\" >\n"
                + "<owl:Ontology/>\n" + "<owl:Class rdf:ID=\"A\">" + "  <first:ap>"
                + "     <owl:Class rdf:ID=\"B\"/>" + "  </first:ap>" + "</owl:Class>"
                + "<owl:AnnotationProperty rdf:ID=\"ap\"/>" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF\n"
                + "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\""
                + "    xmlns:first=\"http://www.w3.org/2002/03owlt/AnnotationProperty/premises002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/AnnotationProperty/conclusions002\" >\n"
                + " <owl:Ontology/>\n" + " <owl:Class rdf:about=\"premises002#A\">"
                + "    <first:ap>" + "       <owl:Thing />" + "    </first:ap>"
                + "  </owl:Class>"
                + "  <owl:AnnotationProperty rdf:about=\"premises002#ap\"/>"
                + "</rdf:RDF>";
        String id = "WebOnt_AnnotationProperty_002";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "In OWL 1, this test was used to expose differences between the RDF Based and Direct semantics.  In OWL 2, the entailment ontology holds under both semantics.  Under the OWL 2 Direct Semantics, annotations in the conclusion ontology are ignored, so the only axiom evaluated in ClassAssertion(owl:Thing _:x).  Under the OWL 2 RDF Based semantics, annotations are relevant, and in this test, the entailment holds.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
