package uk.ac.manchester.cs.jfact.visitors;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomConceptInclusion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleDomain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleRange;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleSubsumption;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDeclaration;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDifferentIndividuals;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointDRoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointORoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointUnion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentDRoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentORoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomFairnessConstraint;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomInstanceOf;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleDomain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleRange;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleSubsumption;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRelatedTo;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRelatedToNot;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleAsymmetric;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleInverseFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleIrreflexive;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleReflexive;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleSymmetric;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleTransitive;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomSameIndividuals;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomValueOf;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomValueOfNot;

@SuppressWarnings("unused")
public class DLAxiomVisitorExAdapter<A> implements DLAxiomVisitorEx<A> {
	public A visit(final AxiomDeclaration axiom) {
		return null;
	}

	public A visit(final AxiomEquivalentConcepts axiom) {
		return null;
	}

	public A visit(final AxiomDisjointConcepts axiom) {
		return null;
	}

	public A visit(final AxiomEquivalentORoles axiom) {
		return null;
	}

	public A visit(final AxiomEquivalentDRoles axiom) {
		return null;
	}

	public A visit(final AxiomDisjointUnion axiom) {
		return null;
	}

	public A visit(final AxiomDisjointORoles axiom) {
		return null;
	}

	public A visit(final AxiomDisjointDRoles axiom) {
		return null;
	}

	public A visit(final AxiomSameIndividuals axiom) {
		return null;
	}

	public A visit(final AxiomDifferentIndividuals axiom) {
		return null;
	}

	public A visit(final AxiomFairnessConstraint axiom) {
		return null;
	}

	public A visit(final AxiomRoleInverse axiom) {
		return null;
	}

	public A visit(final AxiomORoleSubsumption axiom) {
		return null;
	}

	public A visit(final AxiomDRoleSubsumption axiom) {
		return null;
	}

	public A visit(final AxiomORoleDomain axiom) {
		return null;
	}

	public A visit(final AxiomDRoleDomain axiom) {
		return null;
	}

	public A visit(final AxiomORoleRange axiom) {
		return null;
	}

	public A visit(final AxiomDRoleRange axiom) {
		return null;
	}

	public A visit(final AxiomRoleTransitive axiom) {
		return null;
	}

	public A visit(final AxiomRoleReflexive axiom) {
		return null;
	}

	public A visit(final AxiomRoleIrreflexive axiom) {
		return null;
	}

	public A visit(final AxiomRoleSymmetric axiom) {
		return null;
	}

	public A visit(final AxiomRoleAsymmetric axiom) {
		return null;
	}

	public A visit(final AxiomORoleFunctional axiom) {
		return null;
	}

	public A visit(final AxiomDRoleFunctional axiom) {
		return null;
	}

	public A visit(final AxiomRoleInverseFunctional axiom) {
		return null;
	}

	public A visit(final AxiomConceptInclusion axiom) {
		return null;
	}

	public A visit(final AxiomInstanceOf axiom) {
		return null;
	}

	public A visit(final AxiomRelatedTo axiom) {
		return null;
	}

	public A visit(final AxiomRelatedToNot axiom) {
		return null;
	}

	public A visit(final AxiomValueOf axiom) {
		return null;
	}

	public A visit(final AxiomValueOfNot axiom) {
		return null;
	}

	public A visitOntology(final Ontology ontology) {
		return null;
	}
}
