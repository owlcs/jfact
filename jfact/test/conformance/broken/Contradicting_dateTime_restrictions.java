package conformance.broken;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class Contradicting_dateTime_restrictions {
	@Test
	public void testContradicting_dateTime_restrictions() {
		String premise = "Prefix(:=<http://example.org/>)\n"
				+ "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
				+ "Ontology(\n"
				+ "  Declaration(NamedIndividual(:a))\n"
				+ "  Declaration(DataProperty(:dp))\n"
				+ "  Declaration(Class(:A))\n"
				+ "  SubClassOf(:A \n"
				+ "    DataHasValue(:dp \"2007-10-08T20:44:11.656+01:00\"^^xsd:dateTime)) \n"
				+ "  SubClassOf(:A \n"
				+ "    DataAllValuesFrom(:dp DatatypeRestriction(\n"
				+ "      xsd:dateTime \n"
				+ "      xsd:minInclusive \"2008-07-08T20:44:11.656+01:00\"^^xsd:dateTime \n"
				+ "      xsd:maxInclusive \"2008-10-08T20:44:11.656+01:00\"^^xsd:dateTime)\n"
				+ "    )\n" + "  ) \n" + "  ClassAssertion(:A :a)\n" + ")";
		String conclusion = "";
		String id = "Contradicting_dateTime_restrictions";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "The individual a must have a dp filler that is a date from 2007, but the restrictions on dp allow only values from 2008, which makes the ontology inconsistent.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
