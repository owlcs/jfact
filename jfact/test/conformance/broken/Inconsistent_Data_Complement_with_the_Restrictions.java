package conformance.broken;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class Inconsistent_Data_Complement_with_the_Restrictions {
    @Test
    public void testInconsistent_Data_Complement_with_the_Restrictions() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n" + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp \n"
                + "    DataOneOf(\"3\"^^xsd:integer \"4\"^^xsd:integer))\n" + "  ) \n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp \n"
                + "    DataOneOf(\"2\"^^xsd:integer \"3\"^^xsd:integer))\n" + "  )\n"
                + "  ClassAssertion(:A :a)\n"
                + "  ClassAssertion(DataSomeValuesFrom(:dp\n"
                + "  DataComplementOf(DataOneOf(\"3\"^^xsd:integer))) :a)\n" + ")";
        String conclusion = "";
        String id = "Inconsistent_Data_Complement_with_the_Restrictions";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a must have dp fillers that are in the sets {3, 4} and {2, 3}, but at the same time 3 is not allowed as a dp filler for a, which causes the inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
