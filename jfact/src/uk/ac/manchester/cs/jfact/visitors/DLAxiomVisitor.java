package uk.ac.manchester.cs.jfact.visitors;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import conformance.PortedFrom;

@SuppressWarnings("javadoc")
@PortedFrom(file = "tDLAxiom.h", name = "DLAxiomVisitor")
public interface DLAxiomVisitor {
    void visit(AxiomDeclaration axiom);

    void visit(AxiomEquivalentConcepts axiom);

    void visit(AxiomDisjointConcepts axiom);

    void visit(AxiomEquivalentORoles axiom);

    void visit(AxiomEquivalentDRoles axiom);

    void visit(AxiomDisjointORoles axiom);

    void visit(AxiomDisjointDRoles axiom);

    void visit(AxiomSameIndividuals axiom);

    void visit(AxiomDifferentIndividuals axiom);

    void visit(AxiomFairnessConstraint axiom);

    void visit(AxiomRoleInverse axiom);

    void visit(AxiomORoleSubsumption axiom);

    void visit(AxiomDRoleSubsumption axiom);

    void visit(AxiomORoleDomain axiom);

    void visit(AxiomDRoleDomain axiom);

    void visit(AxiomORoleRange axiom);

    void visit(AxiomDRoleRange axiom);

    void visit(AxiomRoleTransitive axiom);

    void visit(AxiomRoleReflexive axiom);

    void visit(AxiomRoleIrreflexive axiom);

    void visit(AxiomRoleSymmetric axiom);

    void visit(AxiomRoleAsymmetric axiom);

    void visit(AxiomORoleFunctional axiom);

    void visit(AxiomDRoleFunctional axiom);

    void visit(AxiomRoleInverseFunctional axiom);

    void visit(AxiomConceptInclusion axiom);

    void visit(AxiomInstanceOf axiom);

    void visit(AxiomRelatedTo axiom);

    void visit(AxiomRelatedToNot axiom);

    void visit(AxiomValueOf axiom);

    void visit(AxiomValueOfNot axiom);

    @PortedFrom(file = "tDLAxiom.h", name = "visitOntology")
    void visitOntology(Ontology ontology);

    void visit(AxiomDisjointUnion axiom);
}
