package uk.ac.manchester.cs.jfact.visitors;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;

public class DLAxiomVisitorAdapter implements DLAxiomVisitor {
    @Override
    public void visit(AxiomDeclaration axiom) {}

    @Override
    public void visit(AxiomEquivalentConcepts axiom) {}

    @Override
    public void visit(AxiomDisjointConcepts axiom) {}

    @Override
    public void visit(AxiomEquivalentORoles axiom) {}

    @Override
    public void visit(AxiomEquivalentDRoles axiom) {}

    @Override
    public void visit(AxiomDisjointORoles axiom) {}

    @Override
    public void visit(AxiomDisjointDRoles axiom) {}

    @Override
    public void visit(AxiomSameIndividuals axiom) {}

    @Override
    public void visit(AxiomDifferentIndividuals axiom) {}

    @Override
    public void visit(AxiomFairnessConstraint axiom) {}

    @Override
    public void visit(AxiomRoleInverse axiom) {}

    @Override
    public void visit(AxiomORoleSubsumption axiom) {}

    @Override
    public void visit(AxiomDRoleSubsumption axiom) {}

    @Override
    public void visit(AxiomORoleDomain axiom) {}

    @Override
    public void visit(AxiomDRoleDomain axiom) {}

    @Override
    public void visit(AxiomORoleRange axiom) {}

    @Override
    public void visit(AxiomDRoleRange axiom) {}

    @Override
    public void visit(AxiomRoleTransitive axiom) {}

    @Override
    public void visit(AxiomRoleReflexive axiom) {}

    @Override
    public void visit(AxiomRoleIrreflexive axiom) {}

    @Override
    public void visit(AxiomRoleSymmetric axiom) {}

    @Override
    public void visit(AxiomRoleAsymmetric axiom) {}

    @Override
    public void visit(AxiomORoleFunctional axiom) {}

    @Override
    public void visit(AxiomDRoleFunctional axiom) {}

    @Override
    public void visit(AxiomRoleInverseFunctional axiom) {}

    @Override
    public void visit(AxiomConceptInclusion axiom) {}

    @Override
    public void visit(AxiomInstanceOf axiom) {}

    @Override
    public void visit(AxiomRelatedTo axiom) {}

    @Override
    public void visit(AxiomRelatedToNot axiom) {}

    @Override
    public void visit(AxiomValueOf axiom) {}

    @Override
    public void visit(AxiomValueOfNot axiom) {}

    @Override
    public void visitOntology(Ontology ontology) {}

    @Override
    public void visit(AxiomDisjointUnion axiom) {}
}
