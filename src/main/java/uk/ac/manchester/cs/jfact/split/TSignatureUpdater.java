package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.Collection;

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
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import conformance.PortedFrom;

/** update signature by adding the signature of a given axiom to it */
@PortedFrom(file = "tSignatureUpdater.h", name = "TSignatureUpdater")
public class TSignatureUpdater implements DLAxiomVisitor, Serializable {

    private static final long serialVersionUID = 11000L;
    /** helper with expressions */
    @PortedFrom(file = "tSignatureUpdater.h", name = "Updater")
    private final TExpressionSignatureUpdater Updater;

    /**
     * helper for the expression processing
     * 
     * @param E
     *        E
     */
    @PortedFrom(file = "tSignatureUpdater.h", name = "v")
    private void v(Expression E) {
        E.accept(Updater);
    }

    /**
     * helper for the [begin,end) interval
     * 
     * @param arg
     *        arg
     */
    @PortedFrom(file = "tSignatureUpdater.h", name = "v")
    private void v(Collection<? extends Expression> arg) {
        arg.forEach(e -> v(e));
    }

    @Override
    public void visit(AxiomDeclaration axiom) {
        v(axiom.getDeclaration());
    }

    @Override
    public void visit(AxiomEquivalentConcepts axiom) {
        v(axiom.getArguments());
    }

    @Override
    public void visit(AxiomDisjointConcepts axiom) {
        v(axiom.getArguments());
    }

    @Override
    public void visit(AxiomDisjointUnion axiom) {
        v(axiom.getConcept());
        v(axiom.getArguments());
    }

    @Override
    public void visit(AxiomEquivalentORoles axiom) {
        v(axiom.getArguments());
    }

    @Override
    public void visit(AxiomEquivalentDRoles axiom) {
        v(axiom.getArguments());
    }

    @Override
    public void visit(AxiomDisjointORoles axiom) {
        v(axiom.getArguments());
    }

    @Override
    public void visit(AxiomDisjointDRoles axiom) {
        v(axiom.getArguments());
    }

    @Override
    public void visit(AxiomSameIndividuals axiom) {
        v(axiom.getArguments());
    }

    @Override
    public void visit(AxiomDifferentIndividuals axiom) {
        v(axiom.getArguments());
    }

    @Override
    public void visit(AxiomFairnessConstraint axiom) {
        v(axiom.getArguments());
    }

    @Override
    public void visit(AxiomRoleInverse axiom) {
        v(axiom.getRole());
        v(axiom.getInvRole());
    }

    @Override
    public void visit(AxiomORoleSubsumption axiom) {
        v(axiom.getRole());
        v(axiom.getSubRole());
    }

    @Override
    public void visit(AxiomDRoleSubsumption axiom) {
        v(axiom.getRole());
        v(axiom.getSubRole());
    }

    @Override
    public void visit(AxiomORoleDomain axiom) {
        v(axiom.getRole());
        v(axiom.getDomain());
    }

    @Override
    public void visit(AxiomDRoleDomain axiom) {
        v(axiom.getRole());
        v(axiom.getDomain());
    }

    @Override
    public void visit(AxiomORoleRange axiom) {
        v(axiom.getRole());
        v(axiom.getRange());
    }

    @Override
    public void visit(AxiomDRoleRange axiom) {
        v(axiom.getRole());
        v(axiom.getRange());
    }

    @Override
    public void visit(AxiomRoleTransitive axiom) {
        v(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleReflexive axiom) {
        v(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleIrreflexive axiom) {
        v(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleSymmetric axiom) {
        v(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleAsymmetric axiom) {
        v(axiom.getRole());
    }

    @Override
    public void visit(AxiomORoleFunctional axiom) {
        v(axiom.getRole());
    }

    @Override
    public void visit(AxiomDRoleFunctional axiom) {
        v(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleInverseFunctional axiom) {
        v(axiom.getRole());
    }

    @Override
    public void visit(AxiomConceptInclusion axiom) {
        v(axiom.getSubConcept());
        v(axiom.getSupConcept());
    }

    @Override
    public void visit(AxiomInstanceOf axiom) {
        v(axiom.getIndividual());
        v(axiom.getC());
    }

    @Override
    public void visit(AxiomRelatedTo axiom) {
        v(axiom.getIndividual());
        v(axiom.getRelation());
        v(axiom.getRelatedIndividual());
    }

    @Override
    public void visit(AxiomRelatedToNot axiom) {
        v(axiom.getIndividual());
        v(axiom.getRelation());
        v(axiom.getRelatedIndividual());
    }

    @Override
    public void visit(AxiomValueOf axiom) {
        v(axiom.getIndividual());
        v(axiom.getAttribute());
    }

    @Override
    public void visit(AxiomValueOfNot axiom) {
        v(axiom.getIndividual());
        v(axiom.getAttribute());
    }

    /**
     * @param sig
     *        sig
     */
    public TSignatureUpdater(TSignature sig) {
        Updater = new TExpressionSignatureUpdater(sig);
    }
}
