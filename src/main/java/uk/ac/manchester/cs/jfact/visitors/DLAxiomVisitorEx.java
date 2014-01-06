package uk.ac.manchester.cs.jfact.visitors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
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

/** axiom visitor
 * 
 * @param <O>
 *            return type */
public interface DLAxiomVisitorEx<O> {
    /** @param axiom
     *            AxiomDeclaration axiom to visit
     * @return visitor value */
    O visit(AxiomDeclaration axiom);

    /** @param axiom
     *            AxiomEquivalentConcepts axiom to visit
     * @return visitor value */
    O visit(AxiomEquivalentConcepts axiom);

    /** @param axiom
     *            AxiomDisjointConcepts axiom to visit
     * @return visitor value */
    O visit(AxiomDisjointConcepts axiom);

    /** @param axiom
     *            AxiomEquivalentORoles axiom to visit
     * @return visitor value */
    O visit(AxiomEquivalentORoles axiom);

    /** @param axiom
     *            AxiomEquivalentDRoles axiom to visit
     * @return visitor value */
    O visit(AxiomEquivalentDRoles axiom);

    /** @param axiom
     *            AxiomDisjointUnion axiom to visit
     * @return visitor value */
    O visit(AxiomDisjointUnion axiom);

    /** @param axiom
     *            AxiomDisjointORoles axiom to visit
     * @return visitor value */
    O visit(AxiomDisjointORoles axiom);

    /** @param axiom
     *            AxiomDisjointDRoles axiom to visit
     * @return visitor value */
    O visit(AxiomDisjointDRoles axiom);

    /** @param axiom
     *            AxiomSameIndividuals axiom to visit
     * @return visitor value */
    O visit(AxiomSameIndividuals axiom);

    /** @param axiom
     *            AxiomDifferentIndividuals axiom to visit
     * @return visitor value */
    O visit(AxiomDifferentIndividuals axiom);

    /** @param axiom
     *            AxiomFairnessConstraint axiom to visit
     * @return visitor value */
    O visit(AxiomFairnessConstraint axiom);

    /** @param axiom
     *            AxiomRoleInverse axiom to visit
     * @return visitor value */
    O visit(AxiomRoleInverse axiom);

    /** @param axiom
     *            AxiomORoleSubsumption axiom to visit
     * @return visitor value */
    O visit(AxiomORoleSubsumption axiom);

    /** @param axiom
     *            AxiomDRoleSubsumption axiom to visit
     * @return visitor value */
    O visit(AxiomDRoleSubsumption axiom);

    /** @param axiom
     *            AxiomORoleDomain axiom to visit
     * @return visitor value */
    O visit(AxiomORoleDomain axiom);

    /** @param axiom
     *            AxiomDRoleDomain axiom to visit
     * @return visitor value */
    O visit(AxiomDRoleDomain axiom);

    /** @param axiom
     *            AxiomORoleRange axiom to visit
     * @return visitor value */
    O visit(AxiomORoleRange axiom);

    /** @param axiom
     *            AxiomDRoleRange axiom to visit
     * @return visitor value */
    O visit(AxiomDRoleRange axiom);

    /** @param axiom
     *            AxiomRoleTransitive axiom to visit
     * @return visitor value */
    O visit(AxiomRoleTransitive axiom);

    /** @param axiom
     *            AxiomRoleReflexive axiom to visit
     * @return visitor value */
    O visit(AxiomRoleReflexive axiom);

    /** @param axiom
     *            AxiomRoleIrreflexive axiom to visit
     * @return visitor value */
    O visit(AxiomRoleIrreflexive axiom);

    /** @param axiom
     *            AxiomRoleSymmetric axiom to visit
     * @return visitor value */
    O visit(AxiomRoleSymmetric axiom);

    /** @param axiom
     *            AxiomRoleAsymmetric axiom to visit
     * @return visitor value */
    O visit(AxiomRoleAsymmetric axiom);

    /** @param axiom
     *            AxiomORoleFunctional axiom to visit
     * @return visitor value */
    O visit(AxiomORoleFunctional axiom);

    /** @param axiom
     *            AxiomDRoleFunctional axiom to visit
     * @return visitor value */
    O visit(AxiomDRoleFunctional axiom);

    /** @param axiom
     *            AxiomRoleInverseFunctional axiom to visit
     * @return visitor value */
    O visit(AxiomRoleInverseFunctional axiom);

    /** @param axiom
     *            AxiomConceptInclusion axiom to visit
     * @return visitor value */
    O visit(AxiomConceptInclusion axiom);

    /** @param axiom
     *            AxiomInstanceOf axiom to visit
     * @return visitor value */
    O visit(AxiomInstanceOf axiom);

    /** @param axiom
     *            AxiomRelatedTo axiom to visit
     * @return visitor value */
    O visit(AxiomRelatedTo axiom);

    /** @param axiom
     *            AxiomRelatedToNot axiom to visit
     * @return visitor value */
    O visit(AxiomRelatedToNot axiom);

    /** @param axiom
     *            AxiomValueOf axiom to visit
     * @return visitor value */
    O visit(AxiomValueOf axiom);

    /** @param axiom
     *            AxiomValueOfNot axiom to visit
     * @return visitor value */
    O visit(AxiomValueOfNot axiom);
}
