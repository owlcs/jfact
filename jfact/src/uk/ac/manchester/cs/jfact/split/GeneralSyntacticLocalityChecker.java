package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import conformance.Original;
import conformance.PortedFrom;

/** syntactic locality checker for DL axioms */
@PortedFrom(file = "GeneralSyntacticLocalityChecker.h", name = "GeneralSyntacticLocalityChecker")
public abstract class GeneralSyntacticLocalityChecker extends SigAccessor implements
        DLAxiomVisitor, LocalityChecker {
    /** top evaluator */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "TopEval")
    protected TopEquivalenceEvaluator TopEval;
    /** bottom evaluator */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "BotEval")
    protected BotEquivalenceEvaluator BotEval;
    /** remember the axiom locality value here */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isLocal")
    protected boolean isLocal;

    /** @return true iff EXPR is top equivalent */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isTopEquivalent")
    protected boolean isTopEquivalent(Expression expr) {
        return TopEval.isTopEquivalent(expr);
    }

    /** @return true iff EXPR is bottom equivalent */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isBotEquivalent")
    protected boolean isBotEquivalent(Expression expr) {
        return BotEval.isBotEquivalent(expr);
    }

    /** @return true iff role expression in equivalent to const wrt locality */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isREquivalent")
    protected boolean isREquivalent(Expression expr) {
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

    /** init c'tor */
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

    /** load ontology to a given KB */
    @Override
    @Original
    public void visitOntology(Ontology ontology) {
        for (AxiomInterface p : ontology.getAxioms()) {
            if (p.isUsed()) {
                p.accept(this);
            }
        }
    }

    @Override
    public void visit(AxiomDeclaration axiom) {
        isLocal = true;
    }

    @Override
    public void visit(AxiomEquivalentConcepts axiom) {
        // 1 element => local
        if (axiom.size() == 1) {
            isLocal = true;
            return;
        }
        // axiom is local iff all the classes are either top- or bot-local
        isLocal = false;
        List<ConceptExpression> args = axiom.getArguments();
        if (args.size() > 0) {
            if (isBotEquivalent(args.get(0))) {
                for (int i = 1; i < args.size(); i++) {
                    if (!isBotEquivalent(args.get(i))) {
                        return;
                    }
                }
            } else {
                if (!isTopEquivalent(args.get(0))) {
                    return;
                }
                for (int i = 1; i < args.size(); i++) {
                    if (!isTopEquivalent(args.get(i))) {
                        return;
                    }
                }
            }
        }
        isLocal = true;
    }

    @Override
    public void visit(AxiomDisjointConcepts axiom) {
        // local iff at most 1 concept is not bot-equiv
        boolean hasNBE = false;
        isLocal = true;
        for (ConceptExpression p : axiom.getArguments()) {
            if (!isBotEquivalent(p)) {
                if (hasNBE) {
                    isLocal = false;
                    break;
                } else {
                    hasNBE = true;
                }
            }
        }
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
        isLocal = true;
        if (axiom.size() <= 1) {
            return;
        }
        for (ObjectRoleExpression p : axiom.getArguments()) {
            if (!isREquivalent(p)) {
                isLocal = false;
                break;
            }
        }
    }

    @Override
    public void visit(AxiomEquivalentDRoles axiom) {
        isLocal = true;
        if (axiom.size() <= 1) {
            return;
        }
        for (DataRoleExpression p : axiom.getArguments()) {
            if (!isREquivalent(p)) {
                isLocal = false;
                break;
            }
        }
    }

    @Override
    public void visit(AxiomDisjointORoles axiom) {
        isLocal = false;
        if (sig.topRLocal()) {
            return;
        }
        boolean hasNBE = false;
        for (ObjectRoleExpression p : axiom.getArguments()) {
            if (!isREquivalent(p)) {
                if (hasNBE) {
                    return;
                    // false here
                } else {
                    hasNBE = true;
                }
            }
        }
        isLocal = true;
    }

    @Override
    public void visit(AxiomDisjointDRoles axiom) {
        isLocal = false;
        if (sig.topRLocal()) {
            return;
        }
        boolean hasNBE = false;
        for (DataRoleExpression p : axiom.getArguments()) {
            if (!isREquivalent(p)) {
                if (hasNBE) {
                    return;
                    // false here
                } else {
                    hasNBE = true;
                }
            }
        }
        isLocal = true;
    }

    @Override
    public void visit(AxiomSameIndividuals axiom) {
        isLocal = false;
    }

    @Override
    public void visit(AxiomDifferentIndividuals axiom) {
        isLocal = false;
    }

    /** there is no such axiom in OWL API, but I hope nobody would use Fairness
     * here */
    @Override
    public void visit(AxiomFairnessConstraint axiom) {
        isLocal = true;
    }

    @Override
    public void visit(AxiomRoleInverse axiom) {
        isLocal = isREquivalent(axiom.getRole()) && isREquivalent(axiom.getInvRole());
    }

    @Override
    public void visit(AxiomORoleSubsumption axiom) {
        isLocal = isTopEquivalent(axiom.getRole()) || isBotEquivalent(axiom.getSubRole());
    }

    @Override
    public void visit(AxiomDRoleSubsumption axiom) {
        isLocal = isTopEquivalent(axiom.getRole()) || isBotEquivalent(axiom.getSubRole());
    }

    @Override
    public void visit(AxiomORoleDomain axiom) {
        isLocal = isTopEquivalent(axiom.getDomain()) || isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomDRoleDomain axiom) {
        isLocal = isTopEquivalent(axiom.getDomain()) || isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomORoleRange axiom) {
        isLocal = isTopEquivalent(axiom.getRange()) || isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomDRoleRange axiom) {
        isLocal = isTopEquivalent(axiom.getRange()) || isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleTransitive axiom) {
        isLocal = isREquivalent(axiom.getRole());
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
        isLocal = isREquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleAsymmetric axiom) {
        isLocal = isBotEquivalent(axiom.getRole());
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
