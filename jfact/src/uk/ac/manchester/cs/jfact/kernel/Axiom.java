package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.InAx.*;
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

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

@PortedFrom(file = "tAxiom.h", name = "TAxiom")
public class Axiom {
    // NS for different DLTree matchers for trees in axiom
    /** absorb into negation of a concept; @return true if absorption is */
    // performed
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
            LogAdapter logAbsorptionAdapter = options.getAbsorptionLog();
            logAbsorptionAdapter.print(" N-Absorb GCI to concept ", Concept.getName());
            if (Cons.size() > 1) {
                logAbsorptionAdapter.print(" (other options are");
                for (int j = 1; j < Cons.size(); ++j) {
                    logAbsorptionAdapter.print(" ",
                            InAx.getConcept(Cons.get(j).getChild()).getName());
                }
                logAbsorptionAdapter.print(")");
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
    private Set<DLTree> disjuncts = new LinkedHashSet<DLTree>();
    private TOntologyAtom atom;

    /** create a copy of a given GCI; ignore SKIP entry */
    @PortedFrom(file = "tAxiom.h", name = "copy")
    private Axiom copy(DLTree skip) {
        Axiom ret = new Axiom();
        for (DLTree i : disjuncts) {
            if (!i.equals(skip)) {
                ret.disjuncts.add(i.copy());
            }
        }
        return ret;
    }

    /** simplify (OR C ...) for a non-primitive C in a given position */
    @PortedFrom(file = "tAxiom.h", name = "simplifyPosNP")
    private Axiom simplifyPosNP(DLTree pos, TBox KB) {
        SAbsRepCN();
        Axiom ret = copy(pos);
        ret.add(DLTreeFactory.createSNFNot(InAx.getConcept(pos.getChild())
                .getDescription().copy()));
        KB.getOptions().getAbsorptionLog()
                .print(" simplify CN expression for ", pos.getChild());
        return ret;
    }

    /** simplify (OR ~C ...) for a non-primitive C in a given position */
    @PortedFrom(file = "tAxiom.h", name = "simplifyNegNP")
    private Axiom simplifyNegNP(DLTree pos, TBox KB) {
        SAbsRepCN();
        Axiom ret = copy(pos);
        ret.add(InAx.getConcept(pos).getDescription().copy());
        KB.getOptions().getAbsorptionLog().print(" simplify ~CN expression for ", pos);
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

    /** split an axiom; @return new axiom and/or NULL */
    @PortedFrom(file = "tAxiom.h", name = "split")
    public List<Axiom> split(TBox KB) {
        List<Axiom> acc = new ArrayList<Axiom>();
        for (DLTree p : disjuncts) {
            if (InAx.isAnd(p)) {
                SAbsSplit();
                KB.getOptions().getAbsorptionLog()
                        .print(" split AND espression ", p.getChild());
                acc = this.split(acc, p, p.getChildren().iterator().next());
                // no need to split more than once:
                // every extra splits would be together with unsplitted parts
                // like: (A or B) and (C or D) would be transform into
                // A and (C or D), B and (C or D), (A or B) and C, (A or B) and
                // D
                // so just return here
                return acc;
            }
        }
        return acc;
    }

    /** add DLTree to an axiom */
    @PortedFrom(file = "tAxiom.h", name = "add")
    public void add(DLTree p) {
        if (InAx.isBot(p)) {
            return; // nothing to do
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

    /** replace a defined concept with its description */
    @PortedFrom(file = "tAxiom.h", name = "simplifyCN")
    public Axiom simplifyCN(TBox tbox) {
        for (DLTree p : disjuncts) {
            if (InAx.isPosNP(p)) {
                return simplifyPosNP(p, tbox);
            } else if (InAx.isNegNP(p)) {
                return simplifyNegNP(p, tbox);
            }
        }
        return null;
    }

    /** replace a universal restriction with a fresh concept */
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
        DLTree pAll = pos.getChild(); // (all R ~C)
        KB.getOptions().getAbsorptionLog().print(" simplify ALL expression", pAll);
        Axiom ret = copy(pos);
        ret.add(KB.getTree(KB.replaceForall(pAll.copy())));
        return ret;
    }

    /** create a concept expression corresponding to a given GCI; ignore SKIP */
    // entry
    @PortedFrom(file = "tAxiom.h", name = "createAnAxiom")
    public DLTree createAnAxiom(DLTree replaced) {
        // XXX check if this is correct
        if (disjuncts.isEmpty()) {
            return DLTreeFactory.createBottom();
        }
        // assert !this.disjuncts.isEmpty();
        List<DLTree> leaves = new ArrayList<DLTree>();
        for (DLTree d : disjuncts) {
            if (!d.equals(replaced)) {
                leaves.add(d.copy());
            }
        }
        DLTree result = DLTreeFactory.createSNFAnd(leaves);
        return DLTreeFactory.createSNFNot(result);
    }

    /** absorb into BOTTOM; @return true if absorption is performed */
    @PortedFrom(file = "tAxiom.h", name = "absorbIntoBottom")
    public boolean absorbIntoBottom(TBox KB) {
        List<DLTree> Pos = new ArrayList<DLTree>(), Neg = new ArrayList<DLTree>();
        for (DLTree p : disjuncts) {
            switch (p.token()) {
                case BOTTOM: // axiom in the form T [= T or ...; nothing to do
                    SAbsBApply();
                    KB.getOptions().getAbsorptionLog().print(" Absorb into BOTTOM");
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
                    KB.getOptions().getAbsorptionLog()
                            .print(" Absorb into BOTTOM due to (not", q, ") and", s);
                    return true;
                }
            }
        }
        return false;
    }

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
            LogAdapter logAbsorptionAdapter = KB.getOptions().getAbsorptionLog();
            logAbsorptionAdapter.print(" C-Absorb GCI to concept ", Concept.getName());
            if (Cons.size() > 1) {
                logAbsorptionAdapter.print(" (other options are");
                for (int j = 1; j < Cons.size(); ++j) {
                    logAbsorptionAdapter.print(" ", InAx.getConcept(Cons.get(j))
                            .getName());
                }
                logAbsorptionAdapter.print(")");
            }
        }
        Concept.addDesc(createAnAxiom(bestConcept));
        Concept.removeSelfFromDescription();
        KB.clearRelevanceInfo();
        KB.checkToldCycle(Concept);
        KB.clearRelevanceInfo();
        return true;
    }

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
            LogAdapter logAbsorptionAdapter = KB.getOptions().getAbsorptionLog();
            logAbsorptionAdapter.print(" R-Absorb GCI to the domain of role ",
                    role.getName());
            if (Cons.size() > 1) {
                logAbsorptionAdapter.print(" (other options are");
                for (int j = 1; j < Cons.size(); ++j) {
                    logAbsorptionAdapter.print(" ",
                            Role.resolveRole(Cons.get(j).getChild().getLeft()).getName());
                }
                logAbsorptionAdapter.print(")");
            }
        }
        role.setDomain(createAnAxiom(bestSome));
        return true;
    }

    /** absorb into TOP; @return true if absorption performs */
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
            LogAdapter logAbsorptionAdapter = KB.getOptions().getAbsorptionLog();
            logAbsorptionAdapter.print("TAxiom.absorbIntoTop() T-Absorb GCI to axiom\n");
            if (desc != null) {
                logAbsorptionAdapter.print("s *TOP* [=", desc, " and\n");
            }
            logAbsorptionAdapter.print(" ", C.getName(), " = *TOP*\n");
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

    @Original
    public TOntologyAtom getAtom() {
        return atom;
    }

    @Original
    public void setAtom(TOntologyAtom atom) {
        this.atom = atom;
    }
}
