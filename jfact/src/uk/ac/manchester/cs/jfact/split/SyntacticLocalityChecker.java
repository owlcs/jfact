package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;

/** syntactic locality checker for DL axioms */
public class SyntacticLocalityChecker extends SigAccessor implements DLAxiomVisitor,
        LocalityChecker {
    /** top evaluator */
    TopEquivalenceEvaluator TopEval;
    /** bottom evaluator */
    BotEquivalenceEvaluator BotEval;
    /** remember the axiom locality value here */
    boolean isLocal;

    /** @return true iff EXPR is top equivalent */
    boolean isTopEquivalent(Expression expr) {
        return TopEval.isTopEquivalent(expr);
    }

    /** @return true iff EXPR is bottom equivalent */
    boolean isBotEquivalent(Expression expr) {
        return BotEval.isBotEquivalent(expr);
    }

    /** @return true iff role expression in equivalent to const wrt locality */
    boolean isREquivalent(Expression expr) {
        return sig.topRLocal() ? isTopEquivalent(expr) : isBotEquivalent(expr);
    }

    /** init c'tor */
    public SyntacticLocalityChecker() {
        TopEval = new TopEquivalenceEvaluator();
        BotEval = new BotEquivalenceEvaluator();
        TopEval.setBotEval(BotEval);
        BotEval.setTopEval(TopEval);
    }

    @Override
    public TSignature getSignature() {
        return sig;
    }

    /** set a new value of a signature (without changing a locality parameters) */
    @Override
    public void setSignatureValue(TSignature Sig) {
        sig = Sig;
        TopEval.sig = sig;
        BotEval.sig = sig;
    }

    // set fields
    /** @return true iff an AXIOM is local wrt defined policy */
    @Override
    public boolean local(Axiom axiom) {
        axiom.accept(this);
        return isLocal;
    }

    /** load ontology to a given KB */
    @Override
    public void visitOntology(Ontology ontology) {
        for (Axiom p : ontology.getAxioms()) {
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
        isLocal = false;
        boolean topLoc = sig.topCLocal();
        if (!(topLoc ? isTopEquivalent(axiom.getC()) : isBotEquivalent(axiom.getC()))) {
            return;
        }
        boolean topEqDesc = false;
        for (ConceptExpression p : axiom.getArguments()) {
            if (!isBotEquivalent(p)) {
                if (!topLoc) {
                    return; // non-local straight away
                }
                if (isTopEquivalent(p)) {
                    if (topEqDesc) {
                        return; // 2nd top in there -- non-local
                    } else {
                        topEqDesc = true;
                    }
                } else {
                    return; // non-local
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
                    return; // false here
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
                    return; // false here
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

    /** there is no such axiom in OWL API, but I hope nobody would use Fairness */
    // here
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
        isLocal = isREquivalent(sig.topRLocal() ? axiom.getRole() : axiom.getSubRole());
    }

    @Override
    public void visit(AxiomDRoleSubsumption axiom) {
        isLocal = isREquivalent(sig.topRLocal() ? axiom.getRole() : axiom.getSubRole());
    }

    @Override
    public void visit(AxiomORoleDomain axiom) {
        isLocal = isTopEquivalent(axiom.getDomain());
        if (!sig.topRLocal()) {
            isLocal |= isBotEquivalent(axiom.getRole());
        }
    }

    @Override
    public void visit(AxiomDRoleDomain axiom) {
        isLocal = isTopEquivalent(axiom.getDomain());
        if (!sig.topRLocal()) {
            isLocal |= isBotEquivalent(axiom.getRole());
        }
    }

    @Override
    public void visit(AxiomORoleRange axiom) {
        isLocal = isTopEquivalent(axiom.getRange());
        if (!sig.topRLocal()) {
            isLocal |= isBotEquivalent(axiom.getRole());
        }
    }

    @Override
    public void visit(AxiomDRoleRange axiom) {
        isLocal = isTopDT(axiom.getRange());
        if (!sig.topRLocal()) {
            isLocal |= isBotEquivalent(axiom.getRole());
        }
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
        isLocal = !sig.topRLocal();
    }

    @Override
    public void visit(AxiomRoleSymmetric axiom) {
        isLocal = isREquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleAsymmetric axiom) {
        isLocal = !sig.topRLocal();
    }

    @Override
    public void visit(AxiomORoleFunctional axiom) {
        isLocal = !sig.topRLocal() && isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomDRoleFunctional axiom) {
        isLocal = !sig.topRLocal() && isBotEquivalent(axiom.getRole());
    }

    @Override
    public void visit(AxiomRoleInverseFunctional axiom) {
        isLocal = !sig.topRLocal() && isBotEquivalent(axiom.getRole());
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
        isLocal = sig.topRLocal() && isTopEquivalent(axiom.getRelation());
    }

    @Override
    public void visit(AxiomRelatedToNot axiom) {
        isLocal = !sig.topRLocal() && isBotEquivalent(axiom.getRelation());
    }

    @Override
    public void visit(AxiomValueOf axiom) {
        isLocal = sig.topRLocal() && isTopEquivalent(axiom.getAttribute());
    }

    @Override
    public void visit(AxiomValueOfNot axiom) {
        isLocal = !sig.topRLocal() && isBotEquivalent(axiom.getAttribute());
    }

    @Override
    public void preprocessOntology(Collection<Axiom> s) {
        sig = new TSignature();
        for (Axiom ax : s) {
            sig.add(ax.getSignature());
        }
    }
}
