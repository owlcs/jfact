package uk.ac.manchester.cs.jfact.visitors;

import org.semanticweb.owlapitools.decomposition.AxiomWrapper;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;

/**
 * axiom visitor
 * 
 * @param <O>
 *        return type
 */
@FunctionalInterface
public interface DLAxiomVisitorEx<O> {

    /**
     * @param a
     *        parameter to visit
     * @return default return value
     */
    O doDefault(AxiomWrapper a);

    /**
     * @param axiom
     *        AxiomDeclaration axiom to visit
     * @return visitor value
     */
    default O visit(AxiomDeclaration axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomEquivalentConcepts axiom to visit
     * @return visitor value
     */
    default O visit(AxiomEquivalentConcepts axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDisjointConcepts axiom to visit
     * @return visitor value
     */
    default O visit(AxiomDisjointConcepts axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomEquivalentORoles axiom to visit
     * @return visitor value
     */
    default O visit(AxiomEquivalentORoles axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomEquivalentDRoles axiom to visit
     * @return visitor value
     */
    default O visit(AxiomEquivalentDRoles axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDisjointUnion axiom to visit
     * @return visitor value
     */
    default O visit(AxiomDisjointUnion axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDisjointORoles axiom to visit
     * @return visitor value
     */
    default O visit(AxiomDisjointORoles axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDisjointDRoles axiom to visit
     * @return visitor value
     */
    default O visit(AxiomDisjointDRoles axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomSameIndividuals axiom to visit
     * @return visitor value
     */
    default O visit(AxiomSameIndividuals axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDifferentIndividuals axiom to visit
     * @return visitor value
     */
    default O visit(AxiomDifferentIndividuals axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomFairnessConstraint axiom to visit
     * @return visitor value
     */
    default O visit(AxiomFairnessConstraint axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleInverse axiom to visit
     * @return visitor value
     */
    default O visit(AxiomRoleInverse axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomORoleSubsumption axiom to visit
     * @return visitor value
     */
    default O visit(AxiomORoleSubsumption axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDRoleSubsumption axiom to visit
     * @return visitor value
     */
    default O visit(AxiomDRoleSubsumption axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomORoleDomain axiom to visit
     * @return visitor value
     */
    default O visit(AxiomORoleDomain axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDRoleDomain axiom to visit
     * @return visitor value
     */
    default O visit(AxiomDRoleDomain axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomORoleRange axiom to visit
     * @return visitor value
     */
    default O visit(AxiomORoleRange axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDRoleRange axiom to visit
     * @return visitor value
     */
    default O visit(AxiomDRoleRange axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleTransitive axiom to visit
     * @return visitor value
     */
    default O visit(AxiomRoleTransitive axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleReflexive axiom to visit
     * @return visitor value
     */
    default O visit(AxiomRoleReflexive axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleIrreflexive axiom to visit
     * @return visitor value
     */
    default O visit(AxiomRoleIrreflexive axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleSymmetric axiom to visit
     * @return visitor value
     */
    default O visit(AxiomRoleSymmetric axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleAsymmetric axiom to visit
     * @return visitor value
     */
    default O visit(AxiomRoleAsymmetric axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomORoleFunctional axiom to visit
     * @return visitor value
     */
    default O visit(AxiomORoleFunctional axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomDRoleFunctional axiom to visit
     * @return visitor value
     */
    default O visit(AxiomDRoleFunctional axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRoleInverseFunctional axiom to visit
     * @return visitor value
     */
    default O visit(AxiomRoleInverseFunctional axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomConceptInclusion axiom to visit
     * @return visitor value
     */
    default O visit(AxiomConceptInclusion axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomInstanceOf axiom to visit
     * @return visitor value
     */
    default O visit(AxiomInstanceOf axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRelatedTo axiom to visit
     * @return visitor value
     */
    default O visit(AxiomRelatedTo axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomRelatedToNot axiom to visit
     * @return visitor value
     */
    default O visit(AxiomRelatedToNot axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomValueOf axiom to visit
     * @return visitor value
     */
    default O visit(AxiomValueOf axiom) {
        return doDefault(axiom);
    }

    /**
     * @param axiom
     *        AxiomValueOfNot axiom to visit
     * @return visitor value
     */
    default O visit(AxiomValueOfNot axiom) {
        return doDefault(axiom);
    }
}
