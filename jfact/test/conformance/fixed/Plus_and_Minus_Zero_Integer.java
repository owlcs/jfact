package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class Plus_and_Minus_Zero_Integer {
    @Test
    public void testPlus_and_Minus_Zero_Integer() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n" + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp \n"
                + "    DataOneOf(\"0\"^^xsd:integer))\n" + "  ) \n"
                + "  ClassAssertion(:A :a)\n" + "  ClassAssertion(\n"
                + "    DataSomeValuesFrom(:dp DataOneOf(\"-0\"^^xsd:integer)) :a\n"
                + "  )\n" + ")";
        String conclusion = "";
        String id = "Plus_and_Minus_Zero_Integer";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "For integers 0 and -0 are the same value, so the ontology is consistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        //r.getConfiguration().setLoggingActive(true);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
