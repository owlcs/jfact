package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class datatype_restriction_min_max_inconsistency {
    @Test
    public void testdatatype_restriction_min_max_inconsistency() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp \n"
                + "    DatatypeRestriction(xsd:integer xsd:minInclusive \"18\"^^xsd:integer))\n"
                + "  ) \n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp \n"
                + "    DatatypeRestriction(xsd:integer xsd:maxInclusive \"10\"^^xsd:integer))\n"
                + "  )\n" + "  ClassAssertion(:A :a)\n" + ")";
        String conclusion = "";
        String id = "datatype_restriction_min_max_inconsistency";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a is supposed to have an integer dp-successor >= 18, but all dp-successors must be <= 10, which is impossible.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        //r.getConfiguration().setLoggingActive(true);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
