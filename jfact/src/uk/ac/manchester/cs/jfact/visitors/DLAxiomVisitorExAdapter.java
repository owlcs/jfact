package uk.ac.manchester.cs.jfact.visitors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;

/** adapter for null visitor
 * 
 * @param <A> */
public class DLAxiomVisitorExAdapter<A> implements DLAxiomVisitorEx<A>, Serializable {
    private static final long serialVersionUID = 11000L;
    @Override
    public A visit(AxiomDeclaration axiom) {
        return null;
    }

    @Override
    public A visit(AxiomEquivalentConcepts axiom) {
        return null;
    }

    @Override
    public A visit(AxiomDisjointConcepts axiom) {
        return null;
    }

    @Override
    public A visit(AxiomEquivalentORoles axiom) {
        return null;
    }

    @Override
    public A visit(AxiomEquivalentDRoles axiom) {
        return null;
    }

    @Override
    public A visit(AxiomDisjointUnion axiom) {
        return null;
    }

    @Override
    public A visit(AxiomDisjointORoles axiom) {
        return null;
    }

    @Override
    public A visit(AxiomDisjointDRoles axiom) {
        return null;
    }

    @Override
    public A visit(AxiomSameIndividuals axiom) {
        return null;
    }

    @Override
    public A visit(AxiomDifferentIndividuals axiom) {
        return null;
    }

    @Override
    public A visit(AxiomFairnessConstraint axiom) {
        return null;
    }

    @Override
    public A visit(AxiomRoleInverse axiom) {
        return null;
    }

    @Override
    public A visit(AxiomORoleSubsumption axiom) {
        return null;
    }

    @Override
    public A visit(AxiomDRoleSubsumption axiom) {
        return null;
    }

    @Override
    public A visit(AxiomORoleDomain axiom) {
        return null;
    }

    @Override
    public A visit(AxiomDRoleDomain axiom) {
        return null;
    }

    @Override
    public A visit(AxiomORoleRange axiom) {
        return null;
    }

    @Override
    public A visit(AxiomDRoleRange axiom) {
        return null;
    }

    @Override
    public A visit(AxiomRoleTransitive axiom) {
        return null;
    }

    @Override
    public A visit(AxiomRoleReflexive axiom) {
        return null;
    }

    @Override
    public A visit(AxiomRoleIrreflexive axiom) {
        return null;
    }

    @Override
    public A visit(AxiomRoleSymmetric axiom) {
        return null;
    }

    @Override
    public A visit(AxiomRoleAsymmetric axiom) {
        return null;
    }

    @Override
    public A visit(AxiomORoleFunctional axiom) {
        return null;
    }

    @Override
    public A visit(AxiomDRoleFunctional axiom) {
        return null;
    }

    @Override
    public A visit(AxiomRoleInverseFunctional axiom) {
        return null;
    }

    @Override
    public A visit(AxiomConceptInclusion axiom) {
        return null;
    }

    @Override
    public A visit(AxiomInstanceOf axiom) {
        return null;
    }

    @Override
    public A visit(AxiomRelatedTo axiom) {
        return null;
    }

    @Override
    public A visit(AxiomRelatedToNot axiom) {
        return null;
    }

    @Override
    public A visit(AxiomValueOf axiom) {
        return null;
    }

    @Override
    public A visit(AxiomValueOfNot axiom) {
        return null;
    }

    @Override
    public A visitOntology(Ontology ontology) {
        return null;
    }
}
