package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;
import java.util.List;

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
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NAryExpression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import conformance.Original;
import conformance.PortedFrom;

/** syntactic locality checker for DL axioms */
@PortedFrom(file = "GeneralSyntacticLocalityChecker.h", name = "GeneralSyntacticLocalityChecker")
public abstract class GeneralSyntacticLocalityChecker extends SigAccessor
        implements DLAxiomVisitor, LocalityChecker {

    private static final long serialVersionUID = 11000L;
    /** top evaluator */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "TopEval")
    protected final TopEquivalenceEvaluator TopEval;
    /** bottom evaluator */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "BotEval")
    protected final BotEquivalenceEvaluator BotEval;
    /** remember the axiom locality value here */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isLocal")
    protected boolean isLocal;

    /**
     * processing method for all Equivalent axioms
     * 
     * @param axiom
     *        axiom to process
     * @return true if axiom is local
     */
    @PortedFrom(file = "GeneralSyntacticLocalityChecker.h", name = "processEquivalentAxiom")
            boolean processEquivalentAxiom(
                    NAryExpression<? extends Expression> axiom) {
        // 1 element => local
        if (axiom.size() <= 1) {
            return true;
        }
        // axiom is local iff all the elements are either top- or bot-local
        List<? extends Expression> list = axiom.getArguments();
        if (isBotEquivalent(list.get(0))) {
            // all should be \bot-eq
            for (int i = 1; i < list.size(); i++) {
                if (!isBotEquivalent(list.get(i))) {
                    return false;
                }
            }
        } else if (isTopEquivalent(list.get(0))) {
            // all should be \top-eq
            for (int i = 1; i < list.size(); i++) {
                if (!isTopEquivalent(list.get(i))) {
                    return false;
                }
            }
        } else {
            // neither \bot- no \top-eq: non-local
            return false;
        }
        // all elements have the same locality
        return true;
    }

    /**
     * processing method for all Disjoint axioms
     * 
     * @param axiom
     *        axiom to process
     * @return true if axiom is local
     */
    @PortedFrom(file = "GeneralSyntacticLocalityChecker.h", name = "processDisjointAxiom")
            boolean processDisjointAxiom(
                    NAryExpression<? extends Expression> axiom) {
        // local iff at most 1 element is not bot-equiv
        boolean hasNBE = false;
        List<? extends Expression> list = axiom.getArguments();
        for (int i = 0; i < list.size(); i++) {
            if (!isBotEquivalent(list.get(i))) {
                if (hasNBE) {
                    // already seen one non-bot-eq element
                    // non-local
                    return false;
                } else {
                    // record that 1 non-bot-eq element was found
                    hasNBE = true;
                }
            }
        }
        return true;
    }

    /**
     * @param expr
     *        expr
     * @return true iff EXPR is top equivalent
     */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isTopEquivalent")
    protected boolean isTopEquivalent(Expression expr) {
        return TopEval.isTopEquivalent(expr);
    }

    /**
     * @param expr
     *        expr
     * @return true iff EXPR is bottom equivalent
     */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isBotEquivalent")
    protected boolean isBotEquivalent(Expression expr) {
        return BotEval.isBotEquivalent(expr);
    }

    /**
     * @param expr
     *        expr
     * @return true iff role expression in equivalent to const wrt locality
     */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isREquivalent")
    private boolean isREquivalent(Expression expr) {
        return sig.topRLocal() ? isTopEquivalent(expr) : isBotEquivalent(expr);
    }

    /** @return true iff an AXIOM is local wrt defined policy */
    @Override
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "local")
    public boolean local(AxiomInterface axiom) {
        axiom.accept(this);
        return isLocal;
    }

    /** set a new value of a signature (without changing a locality parameters) */
    @Override
    @Original
    public void setSignatureValue(TSignature Sig) {
        sig = Sig;
        TopEval.sig = sig;
        BotEval.sig = sig;
    }

    // set fields
    @Override
    @Original
    public void preprocessOntology(Collection<AxiomInterface> s) {
        sig = new TSignature();
        for (AxiomInterface ax : s) {
            sig.add(ax.getSignature());
        }
    }

    /**
     * init c'tor
     * 
     * @param sig
     *        sig
     */
    public GeneralSyntacticLocalityChecker(TSignature sig) {
        this.sig = sig;
        TopEval = new TopEquivalenceEvaluator();
        BotEval = new BotEquivalenceEvaluator();
        TopEval.setBotEval(BotEval);
        BotEval.setTopEval(TopEval);
    }

    /** init c'tor */
    public GeneralSyntacticLocalityChecker() {
        this(null);
    }

    @Override
    @Original
    public TSignature getSignature() {
        return sig;
    }

    @Override
    public void visit(AxiomDeclaration axiom) {
        isLocal = true;
    }

    @Override
    public void visit(AxiomEquivalentConcepts axiom) {
        isLocal = processEquivalentAxiom(axiom);
    }

    @Override
    public void visit(AxiomDisjointConcepts axiom) {
        isLocal = processDisjointAxiom(axiom);
    }

    @Override
    public void visit(AxiomDisjointUnion axiom) {
        // DisjointUnion(A, C1,..., Cn) is local if
        // (1) A and all of Ci are bot-equivalent,
        // or (2) A and one Ci are top-equivalent and the remaining Cj are
        // bot-equivalent
        isLocal = false;
        boolean lhsIsTopEq;
        if (isTopEquivalent(axiom.getConcept())) {
            lhsIsTopEq = true;
        } else if (isBotEquivalent(axiom.getConcept())) {
            lhsIsTopEq = false;
        } else {
            return;
        }             // neither (1) nor (2)
        boolean topEqDesc = false;
        for (ConceptExpression p : axiom.getArguments()) {
            if (!isBotEquivalent(p)) {
                if (lhsIsTopEq && isTopEquivalent(p)) {
                    if (topEqDesc) {
                        return;
                    } else {
                        topEqDesc = true;
                    }
                } else {
                    return;
                }
            }
        }
        isLocal = true;
    }

    @Override
    public void visit(AxiomEquivalentORoles axiom) {
        isLocal = processEquivalentAxiom(axiom);
    }

    @Override
    public void visit(AxiomEquivalentDRoles axiom) {
        isLocal = processEquivalentAxiom(axiom);
    }

    @Override
    public void visit(AxiomDisjointORoles axiom) {
        isLocal = processDisjointAxiom(axiom);
    }

    @Override
    public void visit(AxiomDisjointDRoles axiom) {
        isLocal = processDisjointAxiom(axiom);
    }

    @Override
    public void visit(AxiomSameIndividuals axiom) {
        isLocal = false;
    }

    @Override
    public void visit(AxiomDifferentIndividuals axiom) {
        isLocal = false;
    }

    /**
     * FaCT++ extension: there is no such axiom in OWL API, but I hope nobody
     * would use Fairness here
     */
    @Override
    public void visit(AxiomFairnessConstraint axiom) {
        isLocal = true;
    }

    @Override
    public void visit(AxiomRoleInverse axiom) {
        isLocal = isBotEquivalent(axiom.getRole())
                && isBotEquivalent(axiom.getInvRole())
                || isTopEquivalent(axiom.getRole())
                && isTopEquivalent(axiom.getInvRole());
    }

    @Override
    public void visit(AxiomORoleSubsumption axiom) {
        isLocal = isTopEquivalent(axiom.getRole())
                || isBotEquivalent(axiom.getSubRole());
    }

    @Override
    public void visit(AxiomDRoleSubsumption axiom) {
        isLocal = isTopEquivalent(axiom.getRole())
                || isBotEquivalent(axiom.getSubRole());
    }

    @Override
    public void visit(AxiomORoleDomain axiom) {
        isLocal = isTopEquivalent(axiom.getDomain())
                || isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomDRoleDomain axiom) {
        isLocal = isTopEquivalent(axiom.getDomain())
                || isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomORoleRange axiom) {
        isLocal = isTopEquivalent(axiom.getRange())
                || isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomDRoleRange axiom) {
        isLocal = isTopEquivalent(axiom.getRange())
                || isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleTransitive axiom) {
        isLocal = isBotEquivalent(axiom.getRole())
                || isTopEquivalent(axiom.getRole());
    }

    /** as BotRole is irreflexive, the only local axiom is topEquivalent(R) */
    @Override
    public void visit(AxiomRoleReflexive axiom) {
        isLocal = isTopEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleIrreflexive axiom) {
        isLocal = isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleSymmetric axiom) {
        isLocal = isBotEquivalent(axiom.getRole())
                || isTopEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleAsymmetric axiom) {
        // XXX why is this necessarily non local?
        isLocal = false;
    }

    @Override
    public void visit(AxiomORoleFunctional axiom) {
        isLocal = isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomDRoleFunctional axiom) {
        isLocal = isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleInverseFunctional axiom) {
        isLocal = isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomConceptInclusion axiom) {
        isLocal = isBotEquivalent(axiom.getSubConcept())
                || isTopEquivalent(axiom.getSupConcept());
    }

    @Override
    public void visit(AxiomInstanceOf axiom) {
        isLocal = isTopEquivalent(axiom.getC());
    }

    @Override
    public void visit(AxiomRelatedTo axiom) {
        isLocal = isTopEquivalent(axiom.getRelation());
    }

    @Override
    public void visit(AxiomRelatedToNot axiom) {
        isLocal = isBotEquivalent(axiom.getRelation());
    }

    @Override
    public void visit(AxiomValueOf axiom) {
        isLocal = isTopEquivalent(axiom.getAttribute());
    }

    @Override
    public void visit(AxiomValueOfNot axiom) {
        isLocal = isBotEquivalent(axiom.getAttribute());
    }
}
