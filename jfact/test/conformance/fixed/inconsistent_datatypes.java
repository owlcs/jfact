package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class inconsistent_datatypes {
    @Test
    public void testinconsistent_datatypes() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n" + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp xsd:string)) \n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp xsd:integer)) \n"
                + "  ClassAssertion(:A :a)\n" + ")";
        String conclusion = "";
        String id = "inconsistent_datatypes";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a is in the extension of the class A and is thus required to have a dp-successor that is an integer and at the same time all dp-successors are required to be strings, which causes the inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
