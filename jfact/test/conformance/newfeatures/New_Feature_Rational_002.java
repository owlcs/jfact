package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_Rational_002 {
	@Test
	public void testNew_Feature_Rational_002() {
		String premise = "Prefix( : = <http://example.org/> )\n"
				+ "Prefix( owl: = <http://www.w3.org/2002/07/owl#> )\n"
				+ "Prefix( xsd: = <http://www.w3.org/2001/XMLSchema#> )\n"
				+ "\n"
				+ "Ontology(\n"
				+ " Declaration( DataProperty( :dp ) )\n"
				+ " ClassAssertion( DataAllValuesFrom( :dp DataOneOf( \"0.5\"^^xsd:decimal \"1/2\"^^owl:rational ) ) :a )\n"
				+ " ClassAssertion( DataMinCardinality( 2 :dp ) :a )\n" + ")";
		String conclusion = "";
		String id = "New_Feature_Rational_002";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "An inconsistent ontology using owl:rational";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
