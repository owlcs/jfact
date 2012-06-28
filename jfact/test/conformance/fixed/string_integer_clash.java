package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class string_integer_clash {
    @Test
    public void teststring_integer_clash() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:hasAge))\n"
                + "  DataPropertyRange(:hasAge xsd:integer)\n"
                + "  ClassAssertion(DataHasValue(:hasAge \"aString\"^^xsd:string) :a)\n"
                + ")";
        String conclusion = "";
        String id = "string_integer_clash";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The range of hasAge is integer, but a has an asserted string hasAge filler.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        //r.getConfiguration().setLoggingActive(true);
        r.run();
    }
}
