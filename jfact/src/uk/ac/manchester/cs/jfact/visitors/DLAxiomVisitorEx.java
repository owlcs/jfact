package uk.ac.manchester.cs.jfact.visitors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;

@SuppressWarnings("javadoc")
public interface DLAxiomVisitorEx<O> {
    O visit(AxiomDeclaration axiom);

    O visit(AxiomEquivalentConcepts axiom);

    O visit(AxiomDisjointConcepts axiom);

    O visit(AxiomEquivalentORoles axiom);

    O visit(AxiomEquivalentDRoles axiom);

    O visit(AxiomDisjointUnion axiom);

    O visit(AxiomDisjointORoles axiom);

    O visit(AxiomDisjointDRoles axiom);

    O visit(AxiomSameIndividuals axiom);

    O visit(AxiomDifferentIndividuals axiom);

    O visit(AxiomFairnessConstraint axiom);

    O visit(AxiomRoleInverse axiom);

    O visit(AxiomORoleSubsumption axiom);

    O visit(AxiomDRoleSubsumption axiom);

    O visit(AxiomORoleDomain axiom);

    O visit(AxiomDRoleDomain axiom);

    O visit(AxiomORoleRange axiom);

    O visit(AxiomDRoleRange axiom);

    O visit(AxiomRoleTransitive axiom);

    O visit(AxiomRoleReflexive axiom);

    O visit(AxiomRoleIrreflexive axiom);

    O visit(AxiomRoleSymmetric axiom);

    O visit(AxiomRoleAsymmetric axiom);

    O visit(AxiomORoleFunctional axiom);

    O visit(AxiomDRoleFunctional axiom);

    O visit(AxiomRoleInverseFunctional axiom);

    O visit(AxiomConceptInclusion axiom);

    O visit(AxiomInstanceOf axiom);

    O visit(AxiomRelatedTo axiom);

    O visit(AxiomRelatedToNot axiom);

    O visit(AxiomValueOf axiom);

    O visit(AxiomValueOfNot axiom);
}
