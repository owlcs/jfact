package uk.ac.manchester.cs.jfact.visitors;

import org.semanticweb.owlapitools.decomposition.AxiomWrapper;

import conformance.PortedFrom;
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;

/** axiom visitor */
@PortedFrom(file = "tDLAxiom.h", name = "DLAxiomVisitor")
public interface DLAxiomVisitor {

    /**
     * @param a
     *        parameter
     */
    default void doDefault(@SuppressWarnings("unused") AxiomWrapper a) {}

    /**
     * @param axiom
     *        AxiomDeclaration axiom to visit
     */
    default void visit(AxiomDeclaration axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomEquivalentConcepts axiom to visit
     */
    default void visit(AxiomEquivalentConcepts axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDisjointConcepts axiom to visit
     */
    default void visit(AxiomDisjointConcepts axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomEquivalentORoles axiom to visit
     */
    default void visit(AxiomEquivalentORoles axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomEquivalentDRoles axiom to visit
     */
    default void visit(AxiomEquivalentDRoles axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDisjointORoles axiom to visit
     */
    default void visit(AxiomDisjointORoles axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDisjointDRoles axiom to visit
     */
    default void visit(AxiomDisjointDRoles axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomSameIndividuals axiom to visit
     */
    default void visit(AxiomSameIndividuals axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDifferentIndividuals axiom to visit
     */
    default void visit(AxiomDifferentIndividuals axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomFairnessConstraint axiom to visit
     */
    default void visit(AxiomFairnessConstraint axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleInverse axiom to visit
     */
    default void visit(AxiomRoleInverse axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomORoleSubsumption axiom to visit
     */
    default void visit(AxiomORoleSubsumption axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDRoleSubsumption axiom to visit
     */
    default void visit(AxiomDRoleSubsumption axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomORoleDomain axiom to visit
     */
    default void visit(AxiomORoleDomain axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDRoleDomain axiom to visit
     */
    default void visit(AxiomDRoleDomain axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomORoleRange axiom to visit
     */
    default void visit(AxiomORoleRange axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDRoleRange axiom to visit
     */
    default void visit(AxiomDRoleRange axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleTransitive axiom to visit
     */
    default void visit(AxiomRoleTransitive axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleReflexive axiom to visit
     */
    default void visit(AxiomRoleReflexive axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleIrreflexive axiom to visit
     */
    default void visit(AxiomRoleIrreflexive axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleSymmetric axiom to visit
     */
    default void visit(AxiomRoleSymmetric axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleAsymmetric axiom to visit
     */
    default void visit(AxiomRoleAsymmetric axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomORoleFunctional axiom to visit
     */
    default void visit(AxiomORoleFunctional axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDRoleFunctional axiom to visit
     */
    default void visit(AxiomDRoleFunctional axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleInverseFunctional axiom to visit
     */
    default void visit(AxiomRoleInverseFunctional axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomConceptInclusion axiom to visit
     */
    default void visit(AxiomConceptInclusion axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomInstanceOf axiom to visit
     */
    default void visit(AxiomInstanceOf axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRelatedTo axiom to visit
     */
    default void visit(AxiomRelatedTo axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRelatedToNot axiom to visit
     */
    default void visit(AxiomRelatedToNot axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomValueOf axiom to visit
     */
    default void visit(AxiomValueOf axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomValueOfNot axiom to visit
     */
    default void visit(AxiomValueOfNot axiom) {
        doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDisjointUnion axiom to visit
     */
    default void visit(AxiomDisjointUnion axiom) {
        doDefault(axiom);
    }
}
