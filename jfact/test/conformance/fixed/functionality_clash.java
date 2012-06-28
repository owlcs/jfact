package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class functionality_clash {
    @Test
    public void testfunctionality_clash() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:hasAge))\n"
                + "  FunctionalDataProperty(:hasAge) \n"
                + "  ClassAssertion(DataHasValue(:hasAge \"18\"^^xsd:integer) :a) \n"
                + "  ClassAssertion(DataHasValue(:hasAge \"19\"^^xsd:integer) :a)\n"
                + ")";
        String conclusion = "";
        String id = "functionality_clash";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The property hasAge is functional, but the individual a has two distinct hasAge fillers.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
