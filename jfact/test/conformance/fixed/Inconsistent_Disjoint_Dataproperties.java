package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class Inconsistent_Disjoint_Dataproperties {
	@Test
	public void testInconsistent_Disjoint_Dataproperties() {
		String premise = "Prefix(:=<http://example.org/>)\n"
				+ "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
				+ "  Declaration(NamedIndividual(:a))\n"
				+ "  Declaration(DataProperty(:dp1))\n"
				+ "  Declaration(DataProperty(:dp2))\n" + "  Declaration(Class(:A))\n"
				+ "  DisjointDataProperties(:dp1 :dp2) \n"
				+ "  DataPropertyAssertion(:dp1 :a \"10\"^^xsd:integer)\n"
				+ "  SubClassOf(:A \n"
				+ "    DataSomeValuesFrom(:dp2 DatatypeRestriction(\n"
				+ "      xsd:integer \n"
				+ "      xsd:minInclusive \"10\"^^xsd:integer \n"
				+ "      xsd:maxInclusive \"10\"^^xsd:integer)\n" + "    )\n" + "  )\n"
				+ "  ClassAssertion(:A :a)\n" + ")";
		String conclusion = "";
		String id = "Inconsistent_Disjoint_Dataproperties";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "The data properties dp1 and dp2 are disjoint, but the individual a must have 10 as dp1 filler and 10 as dp2 filler (since 10 is the only integer satisfying >= 10 and <= 10), which causes the inconsistency.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
