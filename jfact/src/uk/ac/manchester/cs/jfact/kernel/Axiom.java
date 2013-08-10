package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.InAx.*;
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;
import conformance.Original;
import conformance.PortedFrom;

/** inner axiom class */
@PortedFrom(file = "tAxiom.h", name = "TAxiom")
public class Axiom implements Serializable {
    private static final long serialVersionUID = 11000L;
    private LogAdapter absorptionLog;

    /** @param l
     *            absorption log */
    public Axiom(LogAdapter l) {
        absorptionLog = l;
    }

    // NS for different DLTree matchers for trees in axiom
    /** absorb into negation of a concept;
     * 
     * @param KB
     * @return true if absorption is performed */
    @PortedFrom(file = "tAxiom.h", name = "absorbIntoNegConcept")
    public boolean absorbIntoNegConcept(TBox KB) {
        List<DLTree> Cons = new ArrayList<DLTree>();
        Concept Concept;
        DLTree bestConcept = null;
        // finds all primitive negated concept names without description
        for (DLTree p : disjuncts) {
            if (p.token() == NOT && p.getChild().isName()
                    && (Concept = getConcept(p.getChild())).isPrimitive()
                    && !Concept.isSingleton() && Concept.getDescription() == null) {
                SAbsNAttempt();
                Cons.add(p);
            }
        }
        // if no concept names -- return;
        if (Cons.isEmpty()) {
            return false;
        }
        SAbsNApply();
        // FIXME!! as for now: just take the 1st concept name
        if (bestConcept == null) {
            bestConcept = Cons.get(0);
        }
        // normal concept absorption
        Concept = InAx.getConcept(bestConcept.getChild());
        JFactReasonerConfiguration options = KB.getOptions();
        if (options.isAbsorptionLoggingActive()) {
            absorptionLog.print(" N-Absorb GCI to concept ", Concept.getName());
            if (Cons.size() > 1) {
                absorptionLog.print(" (other options are");
                for (int j = 1; j < Cons.size(); ++j) {
                    absorptionLog.print(" ", InAx.getConcept(Cons.get(j).getChild())
                            .getName());
                }
                absorptionLog.print(")");
            }
        }
        // replace ~C [= D with C=~notC, notC [= D:
        // make notC [= D
        Concept nC = KB.getAuxConcept(createAnAxiom(bestConcept));
        // define C = ~notC; C had an empty desc, so it's safe not to delete it
        KB.makeNonPrimitive(Concept, DLTreeFactory.createSNFNot(KB.getTree(nC)));
        return true;
    }

    /** GCI is presented in the form (or Disjuncts); */
    @PortedFrom(file = "tAxiom.h", name = "Disjuncts")
    private Set<DLTree> disjuncts = new LinkedHashSet<DLTree>();
    @Original
    private TOntologyAtom atom;

    /** create a copy of a given GCI; ignore SKIP entry */
    @PortedFrom(file = "tAxiom.h", name = "copy")
    private Axiom copy(DLTree skip) {
        Axiom ret = new Axiom(absorptionLog);
        for (DLTree i : disjuncts) {
            if (!i.equals(skip)) {
                ret.disjuncts.add(i.copy());
            }
        }
        return ret;
    }

    /** simplify (OR C ...) for a non-primitive C in a given position */
    @PortedFrom(file = "tAxiom.h", name = "simplifyPosNP")
    private Axiom simplifyPosNP(DLTree pos) {
        SAbsRepCN();
        Axiom ret = copy(pos);
        ret.add(DLTreeFactory.createSNFNot(InAx.getConcept(pos.getChild())
                .getDescription().copy()));
        absorptionLog.print(" simplify CN expression for ", pos.getChild());
        return ret;
    }

    /** simplify (OR ~C ...) for a non-primitive C in a given position */
    @PortedFrom(file = "tAxiom.h", name = "simplifyNegNP")
    private Axiom simplifyNegNP(DLTree pos) {
        SAbsRepCN();
        Axiom ret = copy(pos);
        ret.add(InAx.getConcept(pos).getDescription().copy());
        absorptionLog.print(" simplify ~CN expression for ", pos);
        return ret;
    }

    /** split (OR (AND...) ...) in a given position */
    @PortedFrom(file = "tAxiom.h", name = "split")
    private List<Axiom> split(List<Axiom> acc, DLTree pos, DLTree pAnd) {
        if (pAnd.isAND()) {
            // split the AND
            List<DLTree> children = new ArrayList<DLTree>(pAnd.getChildren());
            acc = this.split(acc, pos, children.remove(0));
            if (children.size() > 0) {
                acc = this.split(acc, pos, DLTreeFactory.createSNFAnd(children));
            }
        } else {
            Axiom ret = copy(pos);
            ret.add(DLTreeFactory.createSNFNot(pAnd.copy()));
            acc.add(ret);
        }
        return acc;
    }

    /** split an axiom;
     * 
     * @return new axiom and/or NULL */
    @PortedFrom(file = "tAxiom.h", name = "split")
    public List<Axiom> split() {
        List<Axiom> acc = new ArrayList<Axiom>();
        for (DLTree p : disjuncts) {
            if (InAx.isAnd(p)) {
                SAbsSplit();
                absorptionLog.print(" split AND espression ", p.getChild());
                acc = this.split(acc, p, p.getChildren().iterator().next());
                /** no need to split more than once: every extra splits would be
                 * together with unsplitted parts like: (A or B) and (C or D)
                 * would be transform into A and (C or D), B and (C or D), (A or
                 * B) and C, (A or B) and D so just return here */
                return acc;
            }
        }
        return acc;
    }

    /** add DLTree to an axiom
     * 
     * @param p
     *            tree to add */
    @PortedFrom(file = "tAxiom.h", name = "add")
    public void add(DLTree p) {
        if (InAx.isBot(p)) {
            // nothing to do
            return;
        }
        // flatten the disjunctions on the fly
        if (InAx.isOr(p)) {
            for (DLTree d : p.getChildren()) {
                add(d);
            }
            return;
        }
        disjuncts.add(p);
    }

    /** dump GCI for debug purposes */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(" (neg-and");
        for (DLTree p : disjuncts) {
            b.append(p);
        }
        b.append(")");
        return b.toString();
    }

    /** replace a defined concept with its description
     * 
     * @return rewritten axiom */
    @PortedFrom(file = "tAxiom.h", name = "simplifyCN")
    public Axiom simplifyCN() {
        for (DLTree p : disjuncts) {
            if (InAx.isPosNP(p)) {
                return simplifyPosNP(p);
            } else if (InAx.isNegNP(p)) {
                return simplifyNegNP(p);
            }
        }
        return null;
    }

    /** replace a universal restriction with a fresh concept
     * 
     * @param KB
     *            tbox
     * @return rewritten axiom */
    @PortedFrom(file = "tAxiom.h", name = "simplifyForall")
    public Axiom simplifyForall(TBox KB) {
        for (DLTree i : disjuncts) {
            if (InAx.isAbsForall(i)) {
                return this.simplifyForall(i, KB);
            }
        }
        return null;
    }

    @PortedFrom(file = "tAxiom.h", name = "simplifyForall")
    private Axiom simplifyForall(DLTree pos, TBox KB) {
        SAbsRepForall();
        // (all R ~C)
        DLTree pAll = pos.getChild();
        absorptionLog.print(" simplify ALL expression", pAll);
        Axiom ret = copy(pos);
        ret.add(KB.getTree(KB.replaceForall(pAll.copy())));
        return ret;
    }

    /** create a concept expression corresponding to a given GCI; ignore SKIP
     * entry
     * 
     * @param replaced
     *            ignored entity
     * @return rewritten DLTree */
    @PortedFrom(file = "tAxiom.h", name = "createAnAxiom")
    public DLTree createAnAxiom(DLTree replaced) {
        // XXX check if this is correct
        if (disjuncts.isEmpty()) {
            return DLTreeFactory.createBottom();
        }
        // assert !disjuncts.isEmpty();
        List<DLTree> leaves = new ArrayList<DLTree>();
        for (DLTree d : disjuncts) {
            if (!d.equals(replaced)) {
                leaves.add(d.copy());
            }
        }
        DLTree result = DLTreeFactory.createSNFAnd(leaves);
        return DLTreeFactory.createSNFNot(result);
    }

    /** absorb into BOTTOM;
     * 
     * @return true if absorption is performed */
    @PortedFrom(file = "tAxiom.h", name = "absorbIntoBottom")
    public boolean absorbIntoBottom() {
        List<DLTree> Pos = new ArrayList<DLTree>(), Neg = new ArrayList<DLTree>();
        for (DLTree p : disjuncts) {
            switch (p.token()) {
                case BOTTOM: // axiom in the form T [= T or ...; nothing to do
                    SAbsBApply();
                    absorptionLog.print(" Absorb into BOTTOM");
                    return true;
                case TOP: // skip it here
                    break;
                case NOT: // something negated: put it into NEG
                    Neg.add(p.getChild());
                    break;
                default: // something positive: save in POS
                    Pos.add(p);
                    break;
            }
        }
        // now check whether there is a concept in both POS and NEG
        for (DLTree q : Neg) {
            for (DLTree s : Pos) {
                if (q.equals(s)) {
                    SAbsBApply();
                    absorptionLog.print(" Absorb into BOTTOM due to (not", q, ") and", s);
                    return true;
                }
            }
        }
        return false;
    }

    /** @param KB
     * @return false if there are no absorptions */
    @PortedFrom(file = "tAxiom.h", name = "absorbIntoConcept")
    public boolean absorbIntoConcept(TBox KB) {
        List<DLTree> Cons = new ArrayList<DLTree>();
        DLTree bestConcept = null;
        for (DLTree p : disjuncts) {
            if (InAx.isNegPC(p)) {
                SAbsCAttempt();
                Cons.add(p);
                if (getConcept(p).isSystem()) {
                    bestConcept = p;
                }
            }
        }
        if (Cons.isEmpty()) {
            return false;
        }
        SAbsCApply();
        if (bestConcept == null) {
            bestConcept = Cons.get(0);
        }
        // normal concept absorption
        Concept Concept = InAx.getConcept(bestConcept);
        if (KB.getOptions().isAbsorptionLoggingActive()) {
            absorptionLog.print(" C-Absorb GCI to concept ", Concept.getName());
            if (Cons.size() > 1) {
                absorptionLog.print(" (other options are");
                for (int j = 1; j < Cons.size(); ++j) {
                    absorptionLog.print(" ", InAx.getConcept(Cons.get(j)).getName());
                }
                absorptionLog.print(")");
            }
        }
        Concept.addDesc(createAnAxiom(bestConcept));
        Concept.removeSelfFromDescription();
        KB.clearRelevanceInfo();
        KB.checkToldCycle(Concept);
        KB.clearRelevanceInfo();
        return true;
    }

    /** @param KB
     * @return false if there are no absorptions */
    @PortedFrom(file = "tAxiom.h", name = "absorbIntoDomain")
    public boolean absorbIntoDomain(TBox KB) {
        List<DLTree> Cons = new ArrayList<DLTree>();
        DLTree bestSome = null;
        for (DLTree p : disjuncts) {
            if (p.token() == NOT
                    && (p.getChild().token() == FORALL || p.getChild().token() == LE)) {
                SAbsRAttempt();
                Cons.add(p);
                if (p.getChild().getRight().isBOTTOM()) {
                    bestSome = p;
                    break;
                }
            }
        }
        if (Cons.isEmpty()) {
            return false;
        }
        SAbsRApply();
        Role role;
        if (bestSome != null) {
            role = Role.resolveRole(bestSome.getChild().getLeft());
        } else {
            role = Role.resolveRole(Cons.get(0).getChild().getLeft());
        }
        if (KB.getOptions().isAbsorptionLoggingActive()) {
            absorptionLog.print(" R-Absorb GCI to the domain of role ", role.getName());
            if (Cons.size() > 1) {
                absorptionLog.print(" (other options are");
                for (int j = 1; j < Cons.size(); ++j) {
                    absorptionLog.print(" ",
                            Role.resolveRole(Cons.get(j).getChild().getLeft()).getName());
                }
                absorptionLog.print(")");
            }
        }
        role.setDomain(createAnAxiom(bestSome));
        return true;
    }

    /** absorb into TOP;
     * 
     * @param KB
     * @return true if any absorption is performed */
    @PortedFrom(file = "tAxiom.h", name = "absorbIntoTop")
    public boolean absorbIntoTop(TBox KB) {
        Concept C = null;
        // check whether the axiom is Top [= C
        for (DLTree p : disjuncts) {
            if (InAx.isBot(p)) {
                continue;
            } else if (InAx.isPosCN(p)) {
                // C found
                if (C != null) {
                    return false;
                }
                C = InAx.getConcept(p.getChild());
                if (C.isSingleton()) {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (C == null) {
            return false;
        }
        SAbsTApply();
        // make an absorption
        DLTree desc = KB.makeNonPrimitive(C, DLTreeFactory.createTop());
        if (KB.getOptions().isAbsorptionLoggingActive()) {
            absorptionLog.print("TAxiom.absorbIntoTop() T-Absorb GCI to axiom\n");
            if (desc != null) {
                absorptionLog.print("s *TOP* [=", desc, " and\n");
            }
            absorptionLog.print(" ", C.getName(), " = *TOP*\n");
        }
        if (desc != null) {
            KB.addSubsumeAxiom(DLTreeFactory.createTop(), desc);
        }
        return true;
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 == null) {
            return false;
        }
        if (this == arg0) {
            return true;
        }
        if (arg0 instanceof Axiom) {
            Axiom ax = (Axiom) arg0;
            return disjuncts.equals(ax.disjuncts);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return disjuncts.hashCode();
    }

    /** @return atom for this axiom */
    @Original
    public TOntologyAtom getAtom() {
        return atom;
    }

    /** @param atom */
    @Original
    public void setAtom(TOntologyAtom atom) {
        this.atom = atom;
    }
}
