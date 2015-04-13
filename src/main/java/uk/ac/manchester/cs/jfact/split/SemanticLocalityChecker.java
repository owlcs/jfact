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

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
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
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;

/** semantic locality checker for DL axioms */
@PortedFrom(file = "SemanticLocalityChecker.h", name = "SemanticLocalityChecker")
public class SemanticLocalityChecker implements DLAxiomVisitor,
        LocalityChecker, Serializable {

    private static final long serialVersionUID = 11000L;
    /** Reasoner to detect the tautology */
    @PortedFrom(file = "SemanticLocalityChecker.h", name = "Kernel")
    private final ReasoningKernel Kernel;
    /** Expression manager of a kernel */
    @PortedFrom(file = "SemanticLocalityChecker.h", name = "pEM")
    private final ExpressionCache pEM;
    /** map between axioms and concept expressions */
    @PortedFrom(file = "SemanticLocalityChecker.h", name = "ExprMap")
    private final Map<AxiomInterface, ConceptExpression> ExprMap = new HashMap<>();

    /**
     * @param axiom
     *        axiom
     * @return expression necessary to build query for a given type of an axiom; @return
     *         NULL if none necessary
     */
    @PortedFrom(file = "SemanticLocalityChecker.h", name = "getExpr")
    protected ConceptExpression getExpr(AxiomInterface axiom) {
        // everything else doesn't require expression to be build
        return axiom.accept(new ExpressionFromAxiomBuilder(null));
    }

    /** signature to keep */
    @PortedFrom(file = "LocalityChecker.h", name = "sig")
    private TSignature sig;

    @Override
    @Original
    public TSignature getSignature() {
        return sig;
    }

    /** set a new value of a signature (without changing a locality parameters) */
    @Override
    @Original
    public void setSignatureValue(TSignature Sig) {
        sig = Sig;
        Kernel.setSignature(sig);
    }

    /** remember the axiom locality value here */
    @PortedFrom(file = "SemanticLocalityChecker.h", name = "isLocal")
    private boolean isLocal;

    /**
     * init c'tor
     * 
     * @param k
     *        k
     */
    public SemanticLocalityChecker(ReasoningKernel k) {
        Kernel = k;
        isLocal = true;
        pEM = Kernel.getExpressionManager();
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
    public
            void preprocessOntology(Collection<AxiomInterface> axioms) {
        TSignature s = new TSignature();
        ExprMap.clear();
        for (AxiomInterface q : axioms) {
            ExprMap.put(q, getExpr(q));
            s.add(q.getSignature());
        }
        Kernel.clearKB();
        // register all the objects in the ontology signature
        for (NamedEntity p : s.begin()) {
            Kernel.getOntology()
                    .add(new AxiomDeclaration(null, (Expression) p));
        }
        // prepare the reasoner to check tautologies
        Kernel.realiseKB();
        // after TBox appears there, set signature to translate
        Kernel.setSignature(getSignature());
        // disallow usage of the expression cache as same expressions will lead
        // to different translations
        Kernel.setIgnoreExprCache(true);
    }

    @Override
    public void visit(AxiomDeclaration axiom) {
        isLocal = true;
    }

    @Override
    public void visit(AxiomEquivalentConcepts axiom) {
        isLocal = false;
        List<ConceptExpression> arguments = axiom.getArguments();
        int size = arguments.size();
        ConceptExpression C = arguments.get(0);
        for (int i = 1; i < size; i++) {
            ConceptExpression p = arguments.get(i);
            if (!Kernel.isEquivalent(C, p)) {
                return;
            }
        }
        isLocal = true;
    }

    @Override
    public void visit(AxiomDisjointConcepts axiom) {
        isLocal = false;
        List<ConceptExpression> arguments = axiom.getArguments();
        int size = arguments.size();
        for (int i = 0; i < size; i++) {
            ConceptExpression p = arguments.get(i);
            for (int j = i + 1; j < size; j++) {
                ConceptExpression q = arguments.get(j);
                if (!Kernel.isDisjoint(p, q)) {
                    return;
                }
            }
        }
        isLocal = true;
    }

    @Override
    public void visit(AxiomDisjointUnion axiom) {
        isLocal = false;
        // check A = (or C1... Cn)
        List<ConceptExpression> arguments = axiom.getArguments();
        if (!Kernel.isEquivalent(axiom.getConcept(), or(arguments))) {
            return;
        }
        // check disjoint(C1...Cn)
        int size = arguments.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (!Kernel.isDisjoint(arguments.get(i), arguments.get(j))) {
                    return;
                }
            }
        }
        isLocal = true;
    }

    @Override
    public void visit(AxiomEquivalentORoles axiom) {
        isLocal = false;
        List<ObjectRoleExpression> arguments = axiom.getArguments();
        int size = arguments.size();
        ObjectRoleExpression R = arguments.get(0);
        for (int i = 1; i < size; i++) {
            if (!(Kernel.isSubRoles(R, arguments.get(i)) && Kernel.isSubRoles(
                    arguments.get(i), R))) {
                return;
            }
        }
        isLocal = true;
    }

    // tautology if all the subsumptions Ri [= Rj holds
    @Override
    public void visit(AxiomEquivalentDRoles axiom) {
        isLocal = false;
        List<DataRoleExpression> arguments = axiom.getArguments();
        DataRoleExpression R = arguments.get(0);
        for (int i = 1; i < arguments.size(); i++) {
            if (!(Kernel.isSubRoles(R, arguments.get(i)) && Kernel.isSubRoles(
                    arguments.get(i), R))) {
                return;
            }
        }
        isLocal = true;
    }

    @Override
    public void visit(AxiomDisjointORoles axiom) {
        isLocal = Kernel.isDisjointRoles(axiom.getArguments());
    }

    @Override
    public void visit(AxiomDisjointDRoles axiom) {
        isLocal = Kernel.isDisjointRoles(axiom.getArguments());
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
        isLocal = Kernel.isSubRoles(axiom.getRole(),
                pEM.inverse(axiom.getInvRole()))
                && Kernel.isSubRoles(axiom.getInvRole(),
                        pEM.inverse(axiom.getRole()));
    }

    @Override
    public void visit(AxiomORoleSubsumption axiom) {
        // check whether the LHS is a role chain
        if (axiom.getSubRole() instanceof ObjectRoleChain) {
            isLocal = Kernel.isSubChain(axiom.getRole(),
                    ((ObjectRoleChain) axiom.getSubRole()).getArguments());
            return;
        }
        // check whether the LHS is a plain rle or inverse
        if (axiom.getSubRole() instanceof ObjectRoleExpression) {
            isLocal = Kernel.isSubRoles(axiom.getSubRole(), axiom.getRole());
            return;
        }
        // here we have a projection expression. FIXME!! for now
        isLocal = true;
    }

    @Override
    public void visit(AxiomDRoleSubsumption axiom) {
        isLocal = Kernel.isSubRoles(axiom.getSubRole(), axiom.getRole());
    }

    // Domain(R) = C is tautology iff ER.Top [= C
    @Override
    public void visit(AxiomORoleDomain axiom) {
        isLocal = Kernel.isSubsumedBy(ExprMap.get(axiom), axiom.getDomain());
    }

    @Override
    public void visit(AxiomDRoleDomain axiom) {
        isLocal = Kernel.isSubsumedBy(ExprMap.get(axiom), axiom.getDomain());
    }

    // Range(R) = C is tautology iff ER.~C is unsatisfiable
    @Override
    public void visit(AxiomORoleRange axiom) {
        isLocal = !Kernel.isSatisfiable(ExprMap.get(axiom));
    }

    @Override
    public void visit(AxiomDRoleRange axiom) {
        isLocal = !Kernel.isSatisfiable(ExprMap.get(axiom));
    }

    @Override
    public void visit(AxiomRoleTransitive axiom) {
        isLocal = Kernel.isTransitive(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleReflexive axiom) {
        isLocal = Kernel.isReflexive(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleIrreflexive axiom) {
        isLocal = Kernel.isIrreflexive(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleSymmetric axiom) {
        isLocal = Kernel.isSymmetric(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleAsymmetric axiom) {
        isLocal = Kernel.isAsymmetric(axiom.getRole());
    }

    @Override
    public void visit(AxiomORoleFunctional axiom) {
        isLocal = Kernel.isFunctional(axiom.getRole());
    }

    @Override
    public void visit(AxiomDRoleFunctional axiom) {
        isLocal = Kernel.isFunctional(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleInverseFunctional axiom) {
        isLocal = Kernel.isInverseFunctional(axiom.getRole());
    }

    @Override
    public void visit(AxiomConceptInclusion axiom) {
        isLocal = Kernel.isSubsumedBy(axiom.getSubConcept(),
                axiom.getSupConcept());
    }

    // for top locality, this might be local
    @Override
    public void visit(AxiomInstanceOf axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), axiom.getC());
    }

    @Override
    public void visit(AxiomRelatedTo axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), ExprMap.get(axiom));
    }

    @Override
    public void visit(AxiomRelatedToNot axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), ExprMap.get(axiom));
    }

    @Override
    public void visit(AxiomValueOf axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), ExprMap.get(axiom));
    }

    @Override
    public void visit(AxiomValueOfNot axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), ExprMap.get(axiom));
    }
}
