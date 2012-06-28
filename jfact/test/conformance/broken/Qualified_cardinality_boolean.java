package conformance.broken;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class Qualified_cardinality_boolean {
    @Test
    public void testQualified_cardinality_boolean() {
        String premise = "Prefix( : = <http://example.org/test#> )\n"
                + "Prefix( xsd: = <http://www.w3.org/2001/XMLSchema#> )\n"
                + "\n"
                + "Ontology(<http://owl.semanticweb.org/page/Special:GetOntology/Qualified-cardinality-boolean?m=p>\n"
                + "  Declaration(NamedIndividual(:a))\n" + "  Declaration(Class(:A))\n"
                + "  Declaration(DataProperty(:dp))\n" + "\n"
                + "  SubClassOf(:A DataExactCardinality(2 :dp xsd:boolean))\n" + "\n"
                + "  ClassAssertion(:A :a)\n" + ")";
        String conclusion = "Prefix( : = <http://example.org/test#> )\n"
                + "Prefix( xsd: = <http://www.w3.org/2001/XMLSchema#> )\n"
                + "\n"
                + "Ontology(<http://owl.semanticweb.org/page/Special:GetOntology/Qualified-cardinality-boolean?m=c>\n"
                + "  Declaration(DataProperty(:dp))\n" + "\n"
                + "  DataPropertyAssertion(:dp :a \"true\"^^xsd:boolean)\n"
                + "  DataPropertyAssertion(:dp :a \"false\"^^xsd:boolean)\n" + ")";
        String id = "Qualified_cardinality_boolean";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "According to qualified cardinality restriction individual a should have two boolean values. Since there are only two boolean values, the data property assertions can be entailed.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
