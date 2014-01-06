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
import conformance.PortedFrom;

/** axiom visitor */
@PortedFrom(file = "tDLAxiom.h", name = "DLAxiomVisitor")
public interface DLAxiomVisitor {
    /** @param axiom
     *            AxiomDeclaration axiom to visit */
    void visit(AxiomDeclaration axiom);

    /** @param axiom
     *            AxiomEquivalentConcepts axiom to visit */
    void visit(AxiomEquivalentConcepts axiom);

    /** @param axiom
     *            AxiomDisjointConcepts axiom to visit */
    void visit(AxiomDisjointConcepts axiom);

    /** @param axiom
     *            AxiomEquivalentORoles axiom to visit */
    void visit(AxiomEquivalentORoles axiom);

    /** @param axiom
     *            AxiomEquivalentDRoles axiom to visit */
    void visit(AxiomEquivalentDRoles axiom);

    /** @param axiom
     *            AxiomDisjointORoles axiom to visit */
    void visit(AxiomDisjointORoles axiom);

    /** @param axiom
     *            AxiomDisjointDRoles axiom to visit */
    void visit(AxiomDisjointDRoles axiom);

    /** @param axiom
     *            AxiomSameIndividuals axiom to visit */
    void visit(AxiomSameIndividuals axiom);

    /** @param axiom
     *            AxiomDifferentIndividuals axiom to visit */
    void visit(AxiomDifferentIndividuals axiom);

    /** @param axiom
     *            AxiomFairnessConstraint axiom to visit */
    void visit(AxiomFairnessConstraint axiom);

    /** @param axiom
     *            AxiomRoleInverse axiom to visit */
    void visit(AxiomRoleInverse axiom);

    /** @param axiom
     *            AxiomORoleSubsumption axiom to visit */
    void visit(AxiomORoleSubsumption axiom);

    /** @param axiom
     *            AxiomDRoleSubsumption axiom to visit */
    void visit(AxiomDRoleSubsumption axiom);

    /** @param axiom
     *            AxiomORoleDomain axiom to visit */
    void visit(AxiomORoleDomain axiom);

    /** @param axiom
     *            AxiomDRoleDomain axiom to visit */
    void visit(AxiomDRoleDomain axiom);

    /** @param axiom
     *            AxiomORoleRange axiom to visit */
    void visit(AxiomORoleRange axiom);

    /** @param axiom
     *            AxiomDRoleRange axiom to visit */
    void visit(AxiomDRoleRange axiom);

    /** @param axiom
     *            AxiomRoleTransitive axiom to visit */
    void visit(AxiomRoleTransitive axiom);

    /** @param axiom
     *            AxiomRoleReflexive axiom to visit */
    void visit(AxiomRoleReflexive axiom);

    /** @param axiom
     *            AxiomRoleIrreflexive axiom to visit */
    void visit(AxiomRoleIrreflexive axiom);

    /** @param axiom
     *            AxiomRoleSymmetric axiom to visit */
    void visit(AxiomRoleSymmetric axiom);

    /** @param axiom
     *            AxiomRoleAsymmetric axiom to visit */
    void visit(AxiomRoleAsymmetric axiom);

    /** @param axiom
     *            AxiomORoleFunctional axiom to visit */
    void visit(AxiomORoleFunctional axiom);

    /** @param axiom
     *            AxiomDRoleFunctional axiom to visit */
    void visit(AxiomDRoleFunctional axiom);

    /** @param axiom
     *            AxiomRoleInverseFunctional axiom to visit */
    void visit(AxiomRoleInverseFunctional axiom);

    /** @param axiom
     *            AxiomConceptInclusion axiom to visit */
    void visit(AxiomConceptInclusion axiom);

    /** @param axiom
     *            AxiomInstanceOf axiom to visit */
    void visit(AxiomInstanceOf axiom);

    /** @param axiom
     *            AxiomRelatedTo axiom to visit */
    void visit(AxiomRelatedTo axiom);

    /** @param axiom
     *            AxiomRelatedToNot axiom to visit */
    void visit(AxiomRelatedToNot axiom);

    /** @param axiom
     *            AxiomValueOf axiom to visit */
    void visit(AxiomValueOf axiom);

    /** @param axiom
     *            AxiomValueOfNot axiom to visit */
    void visit(AxiomValueOfNot axiom);

    /** @param axiom
     *            AxiomDisjointUnion axiom to visit */
    void visit(AxiomDisjointUnion axiom);
}
