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
public class DLAxiomVisitorAdapter implements DLAxiomVisitor {
    public void visit(final AxiomDeclaration axiom) {}

    public void visit(final AxiomEquivalentConcepts axiom) {}

    public void visit(final AxiomDisjointConcepts axiom) {}

    public void visit(final AxiomEquivalentORoles axiom) {}

    public void visit(final AxiomEquivalentDRoles axiom) {}

    public void visit(final AxiomDisjointORoles axiom) {}

    public void visit(final AxiomDisjointDRoles axiom) {}

    public void visit(final AxiomSameIndividuals axiom) {}

    public void visit(final AxiomDifferentIndividuals axiom) {}

    public void visit(final AxiomFairnessConstraint axiom) {}

    public void visit(final AxiomRoleInverse axiom) {}

    public void visit(final AxiomORoleSubsumption axiom) {}

    public void visit(final AxiomDRoleSubsumption axiom) {}

    public void visit(final AxiomORoleDomain axiom) {}

    public void visit(final AxiomDRoleDomain axiom) {}

    public void visit(final AxiomORoleRange axiom) {}

    public void visit(final AxiomDRoleRange axiom) {}

    public void visit(final AxiomRoleTransitive axiom) {}

    public void visit(final AxiomRoleReflexive axiom) {}

    public void visit(final AxiomRoleIrreflexive axiom) {}

    public void visit(final AxiomRoleSymmetric axiom) {}

    public void visit(final AxiomRoleAsymmetric axiom) {}

    public void visit(final AxiomORoleFunctional axiom) {}

    public void visit(final AxiomDRoleFunctional axiom) {}

    public void visit(final AxiomRoleInverseFunctional axiom) {}

    public void visit(final AxiomConceptInclusion axiom) {}

    public void visit(final AxiomInstanceOf axiom) {}

    public void visit(final AxiomRelatedTo axiom) {}

    public void visit(final AxiomRelatedToNot axiom) {}

    public void visit(final AxiomValueOf axiom) {}

    public void visit(final AxiomValueOfNot axiom) {}

    public void visitOntology(final Ontology ontology) {}

    public void visit(final AxiomDisjointUnion axiom) {}
}
