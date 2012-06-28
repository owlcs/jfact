package conformance.broken;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class Inconsistent_Byte_Filler {
    @Test
    public void testInconsistent_Byte_Filler() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n" + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp xsd:byte))  \n"
                + "  ClassAssertion(:A :a)\n" + "  ClassAssertion(\n"
                + "    DataSomeValuesFrom(:dp DataOneOf(\"6542145\"^^xsd:integer)) :a\n"
                + "  )\n" + ")";
        String conclusion = "";
        String id = "Inconsistent_Byte_Filler";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a must have the integer 6542145 as dp filler, but all fillers must also be bytes. Since 6542145 is not byte, the ontology is inconsistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
