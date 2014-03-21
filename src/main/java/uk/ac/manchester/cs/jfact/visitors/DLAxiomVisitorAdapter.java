package uk.ac.manchester.cs.jfact.visitors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;

/** adapter for null visitor */
public class DLAxiomVisitorAdapter implements DLAxiomVisitor, Serializable {

    private static final long serialVersionUID = 11000L;

    protected void doDefault(@SuppressWarnings("unused") AxiomInterface a) {}

    @Override
    public void visit(AxiomDeclaration axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomEquivalentConcepts axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomDisjointConcepts axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomEquivalentORoles axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomEquivalentDRoles axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomDisjointORoles axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomDisjointDRoles axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomSameIndividuals axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomDifferentIndividuals axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomFairnessConstraint axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomRoleInverse axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomORoleSubsumption axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomDRoleSubsumption axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomORoleDomain axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomDRoleDomain axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomORoleRange axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomDRoleRange axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomRoleTransitive axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomRoleReflexive axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomRoleIrreflexive axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomRoleSymmetric axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomRoleAsymmetric axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomORoleFunctional axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomDRoleFunctional axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomRoleInverseFunctional axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomConceptInclusion axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomInstanceOf axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomRelatedTo axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomRelatedToNot axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomValueOf axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomValueOfNot axiom) {
        doDefault(axiom);
    }

    @Override
    public void visit(AxiomDisjointUnion axiom) {
        doDefault(axiom);
    }
}
