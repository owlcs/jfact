package uk.ac.manchester.cs.jfact.visitors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;

/** adapter for null visitor */
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
