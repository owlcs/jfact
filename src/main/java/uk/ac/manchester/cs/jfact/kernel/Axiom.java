package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static java.util.stream.Collectors.*;
import static uk.ac.manchester.cs.jfact.kernel.InAx.*;
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.IRI;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;

/** inner axiom class */
@PortedFrom(file = "tAxiom.h", name = "TAxiom")
public class Axiom implements Serializable {

    private static final long serialVersionUID = 11000L;
    private static LogAdapter absorptionLog;
    private Axiom origin;

    /**
     * @param l
     *        absorption log
     */
    public static void setLogAdapter(LogAdapter l) {
        absorptionLog = l;
    }

    /**
     * @param parent
     *        parent axiom
     */
    public Axiom(Axiom parent) {
        origin = parent;
    }

    // NS for different DLTree matchers for trees in axiom
    /**
     * absorb single concept into negation of a concept;
     * 
     * @param KB
     *        KB
     * @return true if absorption is performed
     */
    @PortedFrom(file = "tAxiom.h", name = "absorbIntoNegConcept")
    public boolean absorbIntoNegConcept(TBox KB) {
        // finds all primitive negated concept names without description
        List<DLTree> Cons = disjuncts.stream().filter(p -> primitiveNegatedConceptNamesWithoutDescription(p)).peek(
            p -> SAbsNAttempt()).collect(toList());
        // if no concept names -- return;
        if (Cons.isEmpty()) {
            return false;
        }
        SAbsNApply();
        // FIXME!! as for now: just take the 1st concept name
        DLTree bestConcept = Cons.get(0);
        // normal concept absorption
        Concept Concept = InAx.getConcept(bestConcept.getChild());
        JFactReasonerConfiguration options = KB.getOptions();
        logOptions(" N-Absorb GCI to concept ", Cons, Concept.getName(), options);
        // replace ~C [= D with C=~notC, notC [= D:
        // make notC [= D
        Concept nC = KB.getAuxConcept(createAnAxiom(bestConcept));
        // define C = ~notC; C had an empty desc, so it's safe not to delete it
        KB.makeNonPrimitive(Concept, DLTreeFactory.createSNFNot(KB.getTree(nC)));
        return true;
    }

    protected boolean primitiveNegatedConceptNamesWithoutDescription(DLTree p) {
        boolean b = p.token() == NOT && p.getChild().isName();
        if (!b) {
            return b;
        }
        Concept Concept = getConcept(p.getChild());
        return Concept.isPrimitive() && !Concept.isSingleton() && Concept.getDescription() == null;
    }

    /** GCI is presented in the form (or Disjuncts); */
    @PortedFrom(file = "tAxiom.h", name = "Disjuncts")
    private final Set<DLTree> disjuncts = new LinkedHashSet<>();
    @Original
    private TOntologyAtom atom;

    /**
     * create a copy of a given GCI; ignore SKIP entry
     * 
     * @param skip
     *        skip
     * @return copy
     */
    @PortedFrom(file = "tAxiom.h", name = "copy")
    private Axiom copy(DLTree skip) {
        Axiom ret = new Axiom(this);
        disjuncts.stream().filter(i -> !i.equals(skip)).forEach(i -> ret.disjuncts.add(i.copy()));
        return ret;
    }

    /**
     * @return true iff an axiom is the same as one of its ancestors
     */
    @PortedFrom(file = "tAxiom.h", name = "isCyclic")
    boolean isCyclic() {
        Axiom p = origin;
        while (p != null) {
            if (p.equals(this)) {
                absorptionLog.print(" same as ancestor");
                return true;
            }
            p = p.origin;
        }
        return false;
    }

    /**
     * simplify (OR C ...) for a non-primitive C in a given position
     * 
     * @param pos
     *        pos
     * @return simplified axiom
     */
    @PortedFrom(file = "tAxiom.h", name = "simplifyPosNP")
    private Axiom simplifyPosNP(DLTree pos) {
        SAbsRepCN();
        Axiom ret = copy(pos);
        ret.add(DLTreeFactory.createSNFNot(InAx.getConcept(pos.getChild()).getDescription().copy()));
        absorptionLog.print(" simplify CN expression for ", pos.getChild());
        return ret;
    }

    /**
     * simplify (OR ~C ...) for a non-primitive C in a given position
     * 
     * @param pos
     *        pos
     * @return simplified axiom
     */
    @PortedFrom(file = "tAxiom.h", name = "simplifyNegNP")
    private Axiom simplifyNegNP(DLTree pos) {
        SAbsRepCN();
        Axiom ret = copy(pos);
        ret.add(InAx.getConcept(pos).getDescription().copy());
        absorptionLog.print(" simplify ~CN expression for ", pos);
        return ret;
    }

    /**
     * split (OR (AND...) ...) in a given position
     * 
     * @param acc
     *        acc
     * @param pos
     *        pos
     * @param pAnd
     *        pAnd
     * @return split axioms
     */
    @PortedFrom(file = "tAxiom.h", name = "split")
    private List<Axiom> split(List<Axiom> acc, DLTree pos, DLTree pAnd) {
        if (pAnd.isAND()) {
            // split the AND
            List<DLTree> children = new ArrayList<>(pAnd.getChildren());
            acc = this.split(acc, pos, children.remove(0));
            if (!children.isEmpty()) {
                acc = this.split(acc, pos, DLTreeFactory.createSNFAnd(children));
            }
        } else {
            Axiom ret = copy(pos);
            ret.add(DLTreeFactory.createSNFNot(pAnd.copy()));
            acc.add(ret);
        }
        return acc;
    }

    /**
     * split an axiom;
     * 
     * @return new axiom and/or NULL
     */
    @PortedFrom(file = "tAxiom.h", name = "split")
    public List<Axiom> split() {
        Optional<DLTree> findAny = disjuncts.stream().filter(p -> InAx.isAnd(p)).peek(p -> SAbsSplit()).peek(
            p -> absorptionLog.print(" split AND expression ", p.getChild())).findAny();
        // no need to split more than once:
        // every extra splits would be together with unsplitted parts
        // like: (A or B) and (C or D) would be transform into
        // A and (C or D), B and (C or D), (A or B) and C, (A or B) and
        // D, so just return here
        // XXX doublecheck here because in FaCT++ these are binary trees
        // but here they are n-ary
        if (findAny.isPresent()) {
            return this.split(new ArrayList<>(), findAny.get(), findAny.get().getChildren().iterator().next());
        }
        return new ArrayList<>();
    }

    /**
     * add DLTree to an axiom
     * 
     * @param p
     *        tree to add
     */
    @PortedFrom(file = "tAxiom.h", name = "add")
    public void add(DLTree p) {
        if (InAx.isBot(p)) {
            // BOT or X == X
            // nothing to do
            return;
        }
        // flatten the disjunctions on the fly
        if (InAx.isOr(p)) {
            p.getChildren().forEach(d -> add(d));
            return;
        }
        disjuncts.add(p);
    }

    /** dump GCI for debug purposes */
    @Override
    public String toString() {
        return disjuncts.stream().map(p -> p.toString()).collect(joining("", " (neg-and ", ")"));
    }

    /**
     * replace a defined concept with its description
     * 
     * @param t
     *        tbox
     * @return rewritten axiom
     */
    @PortedFrom(file = "tAxiom.h", name = "simplifyCN")
    public Axiom simplifyCN(TBox t) {
        for (DLTree p : disjuncts) {
            if (InAx.isPosNP(p, t)) {
                return simplifyPosNP(p);
            } else if (InAx.isNegNP(p, t)) {
                return simplifyNegNP(p);
            }
        }
        return null;
    }

    /**
     * replace a universal restriction with a fresh concept
     * 
     * @param KB
     *        tbox
     * @return rewritten axiom
     */
    @PortedFrom(file = "tAxiom.h", name = "simplifyForall")
    public Axiom simplifyForall(TBox KB) {
        Optional<DLTree> findany = disjuncts.stream().filter(i -> InAx.isAbsForall(i)).findAny();
        if (findany.isPresent()) {
            return this.simplifyForall(findany.get(), KB);
        }
        return null;
    }

    /**
     * replace a simple universal restriction with a fresh concept
     * 
     * @param KB
     *        tbox
     * @return simplified axiom
     */
    public Axiom simplifySForall(TBox KB) {
        Optional<DLTree> findany = disjuncts.stream().filter(i -> InAx.isSimpleForall(i)).findAny();
        if (findany.isPresent()) {
            return simplifyForall(findany.get(), KB);
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

    /**
     * create a concept expression corresponding to a given GCI; ignore SKIP
     * entry
     * 
     * @param replaced
     *        ignored entity
     * @return rewritten DLTree
     */
    @PortedFrom(file = "tAxiom.h", name = "createAnAxiom")
    public DLTree createAnAxiom(DLTree replaced) {
        // XXX check if this is correct
        if (disjuncts.isEmpty()) {
            return DLTreeFactory.createBottom();
        }
        // assert !disjuncts.isEmpty();
        List<DLTree> leaves = disjuncts.stream().filter(d -> !d.equals(replaced)).map(d -> d.copy()).collect(toList());
        DLTree result = DLTreeFactory.createSNFAnd(leaves);
        return DLTreeFactory.createSNFNot(result);
    }

    /**
     * absorb into BOTTOM;
     * 
     * @return true if absorption is performed
     */
    @SuppressWarnings("incomplete-switch")
    @PortedFrom(file = "tAxiom.h", name = "absorbIntoBottom")
    public boolean absorbIntoBottom() {
        Set<DLTree> Pos = new HashSet<>();
        Set<DLTree> Neg = new HashSet<>();
        for (DLTree p : disjuncts) {
            switch (p.token()) {
            case BOTTOM: // axiom in the form T [= T or ...; nothing to do
                SAbsBApply();
                absorptionLog.print(" Absorb into BOTTOM");
                return true;
            case TOP: // skip it here
                break;
            case NOT:
                // something negated: put it into NEG
                if (Pos.contains(p)) {
                    SAbsBApply();
                    absorptionLog.print(" Absorb into BOTTOM due to (not", p, ") and", p);
                    return true;
                }
                Neg.add(p.getChild());
                break;
            default:
                // something positive: save in POS
                if (Neg.contains(p)) {
                    SAbsBApply();
                    absorptionLog.print(" Absorb into BOTTOM due to (not", p, ") and", p);
                    return true;
                }
                Pos.add(p);
                break;
            }
        }
        // now check whether there is a concept in both POS and NEG
        Optional<DLTree> findAny = Neg.stream().filter(q -> Pos.contains(q)).findAny();
        if (findAny.isPresent()) {
            SAbsBApply();
            absorptionLog.print(" Absorb into BOTTOM due to (not", findAny.get(), ") and", findAny.get());
            return true;
        }
        return false;
    }

    /**
     * absorb into concept; @return true if absorption is performed
     * 
     * @param KB
     *        KB
     * @return false if there are no absorptions
     */
    @PortedFrom(file = "tAxiom.h", name = "absorbIntoConcept")
    public boolean absorbIntoConcept(TBox KB) {
        List<DLTree> Cons = new ArrayList<>();
        DLTree bestConcept = null;
        // finds all primitive concept names
        for (DLTree p : disjuncts) {
            if (InAx.isNegPC(p)) {
                // FIXME!! review this during implementation of Nominal
                // Absorption
                SAbsCAttempt();
                Cons.add(p);
                if (getConcept(p).isSystem()) {
                    bestConcept = p;
                }
            }
        }
        // if no concept names -- return;
        if (Cons.isEmpty()) {
            return false;
        }
        SAbsCApply();
        // FIXME!! as for now: just take the 1st concept name
        if (bestConcept == null) {
            bestConcept = Cons.get(0);
        }
        // normal concept absorption
        Concept Concept = InAx.getConcept(bestConcept);
        logOptions(" C-Absorb GCI to concept ", Cons, Concept.getName(), KB.getOptions());
        // adds a new definition
        Concept.addDesc(createAnAxiom(bestConcept));
        Concept.removeSelfFromDescription();
        // in case T [= (A or \neg B) and (B and \neg A) there appears a cycle A
        // [= B [= A
        // so remove potential cycle
        // FIXME!! just because TConcept can't get rid of cycle by itself
        KB.clearRelevanceInfo();
        KB.checkToldCycle(Concept);
        KB.clearRelevanceInfo();
        return true;
    }

    protected void logOptions(String prefix, List<DLTree> Cons, IRI conceptName, JFactReasonerConfiguration options) {
        if (options.isAbsorptionLoggingActive()) {
            absorptionLog.print(prefix, conceptName);
            if (Cons.size() > 1) {
                Stream<DLTree> skip = Cons.stream().skip(1);
                List<DLTree> l = new ArrayList<>();
                skip.forEach(t -> {
                    if (t.getChildren().isEmpty()) {
                        l.add(t);
                    } else {
                        l.addAll(t.getChildren());
                    }
                });
                String collect = l.stream().map(p -> InAx.getConcept(p).getName()).collect(joining(" "));
                absorptionLog.print(" (other options are ").print(collect).print(")");
            }
        }
    }

    /**
     * absorb single axiom AX into role domain; @return true if succeed
     * 
     * @param KB
     *        KB
     * @return false if there are no absorptions
     */
    @PortedFrom(file = "tAxiom.h", name = "absorbIntoDomain")
    public boolean absorbIntoDomain(TBox KB) {
        List<DLTree> Cons = new ArrayList<>();
        DLTree bestSome = null;
        // find all forall concepts
        for (DLTree p : disjuncts) {
            // \neg ER.C and \neg >= n R.C
            if (p.token() == NOT && (p.getChild().token() == FORALL || p.getChild().token() == LE)) {
                SAbsRAttempt();
                Cons.add(p);
                // check for the direct domain case
                if (p.getChild().getRight().isBOTTOM()) {
                    // found proper absorption candidate
                    bestSome = p;
                    break;
                }
            }
        }
        // if there are no EXISTS concepts -- return;
        if (Cons.isEmpty()) {
            return false;
        }
        SAbsRApply();
        Role role;
        if (bestSome != null) {
            role = Role.resolveRole(bestSome.getChild().getLeft());
        } else {
            // FIXME!! as for now: just take the 1st role name
            role = Role.resolveRole(Cons.get(0).getChild().getLeft());
        }
        logOptions(" R-Absorb GCI to the domain of role ", Cons, role.getName(), KB.getOptions());
        // here bestSome is either actual domain, or END(); both cases are fine
        role.setDomain(createAnAxiom(bestSome));
        return true;
    }

    /**
     * absorb into TOP;
     * 
     * @param KB
     *        KB
     * @return true if any absorption is performed
     */
    @PortedFrom(file = "tAxiom.h", name = "absorbIntoTop")
    public boolean absorbIntoTop(TBox KB) {
        Concept C = null;
        // check whether the axiom is Top [= C
        for (DLTree p : disjuncts) {
            // BOTTOMS are fine
            if (!InAx.isBot(p)) {
                if (InAx.isPosCN(p)) {
                    // CN found
                    if (C != null) {
                        // more than one concept
                        return false;
                    }
                    C = InAx.getConcept(p.getChild());
                    if (C.isSingleton()) {
                        // doesn't work with nominals
                        return false;
                    }
                } else {
                    return false;
                }
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

    /**
     * @return atom for this axiom
     */
    @Original
    public TOntologyAtom getAtom() {
        return atom;
    }

    /**
     * @param atom
     *        atom
     */
    @Original
    public void setAtom(TOntologyAtom atom) {
        this.atom = atom;
    }
}
