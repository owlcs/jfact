package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class consistent_dataproperty_disjointness {
    @Test
    public void testconsistent_dataproperty_disjointness() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp1))\n"
                + "  Declaration(DataProperty(:dp2))\n" + "  Declaration(Class(:A))\n"
                + "  DisjointDataProperties(:dp1 :dp2) \n"
                + "  DataPropertyAssertion(:dp1 :a \"10\"^^xsd:integer)\n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp2 \n"
                + "    DatatypeRestriction(xsd:integer \n"
                + "      xsd:minInclusive \"18\"^^xsd:integer \n"
                + "      xsd:maxInclusive \"18\"^^xsd:integer)\n" + "    )\n" + "  )\n"
                + "  ClassAssertion(:A :a)\n" + ")";
        String conclusion = "";
        String id = "consistent_dataproperty_disjointness";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "The datatype properties dp1 and dp2 are disjoint, but the individual a can have 10 as a filler for dp1 and 18 as filler for dp2, which satisfies the disjointness.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
