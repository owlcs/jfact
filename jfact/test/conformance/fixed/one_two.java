package conformance.fixed;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class one_two {
	@Test
	public void testone_two() {
		//TODO changed to fix it
		String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
				+ "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
				+ "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
				+ "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
				+ "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
				+ "Ontology(<http://example.com/>\n"
				+ "Declaration(Class(<http://example.com/2a>))\n"
				+ "Declaration(Class(<http://example.com/a>))\n"
				+ "Declaration(Class(<http://example.com/b>))\n"
				+ "Declaration(Class(<http://example.com/b_and_c>))\n"
				+ "Declaration(Class(<http://example.com/c>))\n"
				+ "SubClassOf(<http://example.com/2a> ObjectSomeValuesFrom(<http://example.com/2a_to_a> <http://example.com/a>))\n"
				+ "SubClassOf(<http://example.com/2a> ObjectSomeValuesFrom(<http://example.com/2a_to_b_and_c> <http://example.com/b_and_c>))\n"
				+ "DisjointClasses(<http://example.com/2a> <http://example.com/a>)\n"
				+ "DisjointClasses(<http://example.com/2a> <http://example.com/b>)\n"
				+ "DisjointClasses(<http://example.com/2a> <http://example.com/b_and_c>)\n"
				+ "DisjointClasses(<http://example.com/2a> <http://example.com/c>)\n"
				+ "Declaration(ObjectProperty(<http://example.com/2a_to_a>))\n"
				+ "Declaration(ObjectProperty(<http://example.com/2a_to_b_and_c>))\n"
				+ "Declaration(ObjectProperty(<http://example.com/a_to_2a_prime>))\n"
				+ "Declaration(ObjectProperty(<http://example.com/a_to_b>))\n"
				+ "Declaration(ObjectProperty(<http://example.com/b_and_c_to_2a_prime>))\n"
				+ "Declaration(ObjectProperty(<http://example.com/b_to_a_prime>))\n"
				+ "Declaration(ObjectProperty(<http://example.com/b_to_c>))\n"
				+ "Declaration(ObjectProperty(<http://example.com/c_to_b_prime>))\n"
				+ "EquivalentClasses(<http://example.com/a> ObjectOneOf(<http://example.com/j> <http://example.com/i> <http://example.com/k>))\n"
				+ "SubClassOf(<http://example.com/a> ObjectSomeValuesFrom(<http://example.com/a_to_2a_prime> <http://example.com/2a>))\n"
				+ "SubClassOf(<http://example.com/a> ObjectSomeValuesFrom(<http://example.com/a_to_b> <http://example.com/b>))\n"
				+ "DisjointClasses(<http://example.com/a> <http://example.com/2a>)\n"
				+ "DisjointClasses(<http://example.com/a> <http://example.com/b>)\n"
				+ "DisjointClasses(<http://example.com/a> <http://example.com/c>)\n"
				+ "SubClassOf(<http://example.com/b> ObjectSomeValuesFrom(<http://example.com/b_to_a_prime> <http://example.com/a>))\n"
				+ "SubClassOf(<http://example.com/b> ObjectSomeValuesFrom(<http://example.com/b_to_c> <http://example.com/c>))\n"
				+ "DisjointClasses(<http://example.com/b> <http://example.com/2a>)\n"
				+ "DisjointClasses(<http://example.com/b> <http://example.com/a>)\n"
				+ "DisjointClasses(<http://example.com/b> <http://example.com/c>)\n"
				+ "EquivalentClasses(<http://example.com/b_and_c> ObjectUnionOf(<http://example.com/c> <http://example.com/b>))\n"
				+ "SubClassOf(<http://example.com/b_and_c> ObjectSomeValuesFrom(<http://example.com/b_and_c_to_2a_prime> <http://example.com/2a>))\n"
				+ "DisjointClasses(<http://example.com/b_and_c> <http://example.com/2a>)\n"
				+ "SubClassOf(<http://example.com/c> ObjectSomeValuesFrom(<http://example.com/c_to_b_prime> <http://example.com/b>))\n"
				+ "DisjointClasses(<http://example.com/c> <http://example.com/2a>)\n"
				+ "DisjointClasses(<http://example.com/c> <http://example.com/a>)\n"
				+ "DisjointClasses(<http://example.com/c> <http://example.com/b>)\n"
				+ "InverseObjectProperties(<http://example.com/a_to_2a_prime> <http://example.com/2a_to_a>)\n"
				+ "FunctionalObjectProperty(<http://example.com/2a_to_a>)\n"
				+ "InverseFunctionalObjectProperty(<http://example.com/2a_to_a>)\n"
				+ "InverseObjectProperties(<http://example.com/b_and_c_to_2a_prime> <http://example.com/2a_to_b_and_c>)\n"
				+ "FunctionalObjectProperty(<http://example.com/2a_to_b_and_c>)\n"
				+ "InverseFunctionalObjectProperty(<http://example.com/2a_to_b_and_c>)\n"
				+ "InverseObjectProperties(<http://example.com/a_to_2a_prime> <http://example.com/2a_to_a>)\n"
				+ "FunctionalObjectProperty(<http://example.com/a_to_2a_prime>)\n"
				+ "InverseFunctionalObjectProperty(<http://example.com/a_to_2a_prime>)\n"
				+ "InverseObjectProperties(<http://example.com/b_to_a_prime> <http://example.com/a_to_b>)\n"
				+ "FunctionalObjectProperty(<http://example.com/a_to_b>)\n"
				+ "InverseFunctionalObjectProperty(<http://example.com/a_to_b>)\n"
				+ "InverseObjectProperties(<http://example.com/b_and_c_to_2a_prime> <http://example.com/2a_to_b_and_c>)\n"
				+ "FunctionalObjectProperty(<http://example.com/b_and_c_to_2a_prime>)\n"
				+ "InverseFunctionalObjectProperty(<http://example.com/b_and_c_to_2a_prime>)\n"
				+ "InverseObjectProperties(<http://example.com/b_to_a_prime> <http://example.com/a_to_b>)\n"
				+ "FunctionalObjectProperty(<http://example.com/b_to_a_prime>)\n"
				+ "InverseFunctionalObjectProperty(<http://example.com/b_to_a_prime>)\n"
				+ "InverseObjectProperties(<http://example.com/c_to_b_prime> <http://example.com/b_to_c>)\n"
				+ "FunctionalObjectProperty(<http://example.com/b_to_c>)\n"
				+ "InverseFunctionalObjectProperty(<http://example.com/b_to_c>)\n"
				+ "InverseObjectProperties(<http://example.com/c_to_b_prime> <http://example.com/b_to_c>)\n"
				+ "FunctionalObjectProperty(<http://example.com/c_to_b_prime>)\n"
				+ "InverseFunctionalObjectProperty(<http://example.com/c_to_b_prime>)\n"
				+ "DifferentIndividuals(<http://example.com/i> <http://example.com/j> <http://example.com/k>))";
		String conclusion = "";
		String id = "one_two";
		TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
		String d = "Start with 3 classes, a,b,c and relate them so instances have to be in a 1:1 relationship with each other.\n"
				+ "\n"
				+ "The class b-and-c is the union of b and c. Therefore there have to be 2 instances of b-and-c for every instance of a.\n"
				+ "\n"
				+ "Relate the class 2a to b-and-c so that *their* instances are in 1:1 relationship.\n"
				+ "\n"
				+ "Now relate 2a to a so that *their* instances are in a 1:1 relationship. This should lead to a situation in which every instance\n"
				+ "of 2a is 1:1 with an instance of a, and at the same time 2:1 with an instance of a.\n"
				+ "\n"
				+ "Unless all the classes have an infinite number of members or are empty this doesn't work. This example has a is the enumerated class {i,j,k} (i,j,k all different individuals). So it should be inconsistent.";
		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
		r.setReasonerFactory(Factory.factory());
		r.run();
	}
}
