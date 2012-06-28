package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class Minus_Infinity_is_not_in_owl_real {
    @Test
    public void testMinus_Infinity_is_not_in_owl_real() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp owl:real)) \n"
                + "  SubClassOf(:A \n"
                + "    DataSomeValuesFrom(:dp DataOneOf(\"-INF\"^^xsd:float \"-0\"^^xsd:integer))\n"
                + "  )\n" + "  ClassAssertion(:A :a) \n"
                + "  NegativeDataPropertyAssertion(:dp :a \"0\"^^xsd:unsignedInt)\n"
                + ")";
        String conclusion = "";
        String id = "Minus_Infinity_is_not_in_owl_real";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a must have either negative Infinity or 0 (-0 as integer is 0) as dp fillers and all dp successors must be from owl:real, which excludes negative infinity. Since 0 is excluded by the negative property assertion, the ontology is inconsistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
