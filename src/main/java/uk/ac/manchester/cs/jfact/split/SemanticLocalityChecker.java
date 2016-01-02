package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.or;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;

/** semantic locality checker for DL axioms */
@PortedFrom(file = "SemanticLocalityChecker.h", name = "SemanticLocalityChecker")
public class SemanticLocalityChecker implements DLAxiomVisitor, LocalityChecker, Serializable {

    /** Reasoner to detect the tautology */
    @PortedFrom(file = "SemanticLocalityChecker.h", name = "Kernel") private final ReasoningKernel kernel;
    /** Expression manager of a kernel */
    @PortedFrom(file = "SemanticLocalityChecker.h", name = "pEM") private final ExpressionCache pEM;
    /** map between axioms and concept expressions */
    @PortedFrom(file = "SemanticLocalityChecker.h", name = "ExprMap") private final Map<AxiomInterface, ConceptExpression> exprMap = new HashMap<>();
    /** signature to keep */
    @PortedFrom(file = "LocalityChecker.h", name = "sig") private TSignature sig;
    /** remember the axiom locality value here */
    @PortedFrom(file = "SemanticLocalityChecker.h", name = "isLocal") private boolean isLocal;

    /**
     * init c'tor
     * 
     * @param k
     *        k
     */
    public SemanticLocalityChecker(ReasoningKernel k) {
        kernel = k;
        isLocal = true;
        pEM = kernel.getExpressionManager();
    }

    /**
     * @param axiom
     *        axiom
     * @return expression necessary to build query for a given type of an
     *         axiom; @return NULL if none necessary
     */
    @Nullable
    @PortedFrom(file = "SemanticLocalityChecker.h", name = "getExpr")
    protected ConceptExpression getExpr(AxiomInterface axiom) {
        // everything else doesn't require expression to be build
        return axiom.accept(new ExpressionFromAxiomBuilder(null));
    }

    @Override
    @Original
    public TSignature getSignature() {
        return sig;
    }

    /**
     * set a new value of a signature (without changing a locality parameters)
     */
    @Override
    @Original
    public void setSignatureValue(TSignature sig) {
        this.sig = sig;
        kernel.setSignature(sig);
    }

    // set fields
    /** @return true iff an AXIOM is local wrt defined policy */
    @Override
    @Original
    public boolean local(AxiomInterface axiom) {
        axiom.accept(this);
        return isLocal;
    }

    /** init kernel with the ontology signature */
    @Override
    @PortedFrom(file = "SemanticLocalityChecker.h", name = "preprocessOntology")
    public void preprocessOntology(Collection<AxiomInterface> axioms) {
        TSignature s = new TSignature();
        exprMap.clear();
        axioms.forEach(q -> {
            exprMap.put(q, getExpr(q));
            s.add(q.getSignature());
        });
        kernel.clearKB();
        // register all the objects in the ontology signature
        s.begin().forEach(p -> kernel.getOntology().add(new AxiomDeclaration(null, (Expression) p)));
        // prepare the reasoner to check tautologies
        kernel.realiseKB();
        // after TBox appears there, set signature to translate
        kernel.setSignature(getSignature());
        // disallow usage of the expression cache as same expressions will lead
        // to different translations
        kernel.setIgnoreExprCache(true);
    }

    @Override
    public void visit(AxiomDeclaration axiom) {
        isLocal = true;
    }

    @Override
    public void visit(AxiomEquivalentConcepts axiom) {
        isLocal = axiom.allMatchWithFirst((a, b) -> kernel.isEquivalent(a, b));
    }

    @Override
    public void visit(AxiomDisjointConcepts axiom) {
        isLocal = axiom.allMatch((a, b) -> kernel.isDisjoint(a, b));
    }

    @Override
    public void visit(AxiomDisjointUnion axiom) {
        isLocal = false;
        // check A = (or C1... Cn)
        List<ConceptExpression> arguments = axiom.getArguments();
        if (!kernel.isEquivalent(axiom.getConcept(), or(arguments))) {
            return;
        }
        // check disjoint(C1...Cn)
        isLocal = axiom.allMatch((a, b) -> kernel.isDisjoint(a, b));
    }

    @Override
    public void visit(AxiomEquivalentORoles axiom) {
        isLocal = axiom.allMatchWithFirst((a, b) -> kernel.isSubRoles(a, b) && kernel.isSubRoles(b, a));
    }

    // tautology if all the subsumptions Ri [= Rj holds
    @Override
    public void visit(AxiomEquivalentDRoles axiom) {
        isLocal = axiom.allMatchWithFirst((a, b) -> kernel.isSubRoles(a, b) && kernel.isSubRoles(b, a));
    }

    @Override
    public void visit(AxiomDisjointORoles axiom) {
        isLocal = kernel.isDisjointRoles(axiom.getArguments());
    }

    @Override
    public void visit(AxiomDisjointDRoles axiom) {
        isLocal = kernel.isDisjointRoles(axiom.getArguments());
    }

    // never local
    @Override
    public void visit(AxiomSameIndividuals axiom) {
        isLocal = false;
    }

    // never local
    @Override
    public void visit(AxiomDifferentIndividuals axiom) {
        isLocal = false;
    }

    /**
     * there is no such axiom in OWL API, but I hope nobody would use Fairness
     * here
     */
    @Override
    public void visit(AxiomFairnessConstraint axiom) {
        isLocal = true;
    }

    // R = inverse(S) is tautology iff R [= S- and S [= R-
    @Override
    public void visit(AxiomRoleInverse axiom) {
        isLocal = kernel.isSubRoles(axiom.getRole(), pEM.inverse(axiom.getInvRole()))
            && kernel.isSubRoles(axiom.getInvRole(), pEM.inverse(axiom.getRole()));
    }

    @Override
    public void visit(AxiomORoleSubsumption axiom) {
        // check whether the LHS is a role chain
        if (axiom.getSubRole() instanceof ObjectRoleChain) {
            isLocal = kernel.isSubChain(axiom.getRole(), ((ObjectRoleChain) axiom.getSubRole()).getArguments());
            return;
        }
        // check whether the LHS is a plain rle or inverse
        if (axiom.getSubRole() instanceof ObjectRoleExpression) {
            isLocal = kernel.isSubRoles(axiom.getSubRole(), axiom.getRole());
            return;
        }
        // here we have a projection expression. FIXME!! for now
        isLocal = true;
    }

    @Override
    public void visit(AxiomDRoleSubsumption axiom) {
        isLocal = kernel.isSubRoles(axiom.getSubRole(), axiom.getRole());
    }

    // Domain(R) = C is tautology iff ER.Top [= C
    @Override
    public void visit(AxiomORoleDomain axiom) {
        isLocal = kernel.isSubsumedBy(exprMap.get(axiom), axiom.getDomain());
    }

    @Override
    public void visit(AxiomDRoleDomain axiom) {
        isLocal = kernel.isSubsumedBy(exprMap.get(axiom), axiom.getDomain());
    }

    // Range(R) = C is tautology iff ER.~C is unsatisfiable
    @Override
    public void visit(AxiomORoleRange axiom) {
        isLocal = !kernel.isSatisfiable(exprMap.get(axiom));
    }

    @Override
    public void visit(AxiomDRoleRange axiom) {
        isLocal = !kernel.isSatisfiable(exprMap.get(axiom));
    }

    @Override
    public void visit(AxiomRoleTransitive axiom) {
        isLocal = kernel.isTransitive(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleReflexive axiom) {
        isLocal = kernel.isReflexive(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleIrreflexive axiom) {
        isLocal = kernel.isIrreflexive(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleSymmetric axiom) {
        isLocal = kernel.isSymmetric(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleAsymmetric axiom) {
        isLocal = kernel.isAsymmetric(axiom.getRole());
    }

    @Override
    public void visit(AxiomORoleFunctional axiom) {
        isLocal = kernel.isFunctional(axiom.getRole());
    }

    @Override
    public void visit(AxiomDRoleFunctional axiom) {
        isLocal = kernel.isFunctional(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleInverseFunctional axiom) {
        isLocal = kernel.isInverseFunctional(axiom.getRole());
    }

    @Override
    public void visit(AxiomConceptInclusion axiom) {
        isLocal = kernel.isSubsumedBy(axiom.getSubConcept(), axiom.getSupConcept());
    }

    // for top locality, this might be local
    @Override
    public void visit(AxiomInstanceOf axiom) {
        isLocal = kernel.isInstance(axiom.getIndividual(), axiom.getC());
    }

    @Override
    public void visit(AxiomRelatedTo axiom) {
        isLocal = kernel.isInstance(axiom.getIndividual(), exprMap.get(axiom));
    }

    @Override
    public void visit(AxiomRelatedToNot axiom) {
        isLocal = kernel.isInstance(axiom.getIndividual(), exprMap.get(axiom));
    }

    @Override
    public void visit(AxiomValueOf axiom) {
        isLocal = kernel.isInstance(axiom.getIndividual(), exprMap.get(axiom));
    }

    @Override
    public void visit(AxiomValueOfNot axiom) {
        isLocal = kernel.isInstance(axiom.getIndividual(), exprMap.get(axiom));
    }
}
