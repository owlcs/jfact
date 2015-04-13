package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.DLTree;

/** DLTREE utils */
@PortedFrom(file = "tAxiom.h", name = "InAx")
public class InAx implements Serializable {

    private static final long serialVersionUID = 11000L;
    private static final AtomicInteger ZERO = new AtomicInteger(0);

    /**
     * @return an RW concept from a given [C|I]NAME-rooted DLTree
     * @param p
     *        the tree
     */
    public static Concept getConcept(DLTree p) {
        return (Concept) p.elem().getNE();
    }

    /**
     * @param C
     *        concept
     * @param t
     *        tbox
     * @return true if a concept C is a concept is non-primitive
     */
    @PortedFrom(file = "tAxiom.cpp", name = "isNP")
    public static boolean isNP(Concept C, TBox t) {
        return C.isNonPrimitive() && !hasDefCycle(C);
    }

    @PortedFrom(file = "tAxiom.cpp", name = "hasDefCycle")
    static boolean hasDefCycle(Concept C) {
        if (C.isPrimitive()) {
            return false;
        }
        return hasDefCycle(C, new HashSet<Concept>());
    }

    @PortedFrom(file = "tAxiom.cpp", name = "hasDefCycle")
    static boolean hasDefCycle(Concept C, Set<Concept> visited) {
        // interested in non-primitive
        if (C.isPrimitive()) {
            return false;
        }
        // already seen -- cycle
        if (visited.contains(C)) {
            return true;
        }
        // check the structure: looking for the \exists R.C
        DLTree p = C.getDescription();
        if (!p.isNOT()) {
            return false;
        }
        p = p.getChild();
        if (p.token() != Token.FORALL) {
            return false;
        }
        p = p.getRight();
        if (!p.isNOT()) {
            return false;
        }
        p = p.getChild();
        if (!p.isName()) {
            return false;
        }
        // here P is a concept
        // remember C
        visited.add(C);
        // check p
        return hasDefCycle(getConcept(p), visited);
    }

    /**
     * @return true iff P is a TOP
     * @param p
     *        the tree
     */
    public static boolean isTop(DLTree p) {
        return p.isBOTTOM();
    }

    /**
     * @return true iff P is a BOTTOM
     * @param p
     *        the tree
     */
    public static boolean isBot(DLTree p) {
        return p.isTOP();
    }

    /**
     * @return true iff P is a positive concept name
     * @param p
     *        the tree
     */
    public static boolean isPosCN(DLTree p) {
        return p.isNOT() && p.getChild().isName();
    }

    /**
     * @param p
     *        the tree
     * @param t
     *        tbox
     * @return true iff P is a positive non-primitive CN
     */
    public static boolean isPosNP(DLTree p, TBox t) {
        return isPosCN(p) && isNP(getConcept(p.getChild()), t);
    }

    /**
     * @return true iff P is a positive primitive CN
     * @param p
     *        the tree
     */
    public static boolean isPosPC(DLTree p) {
        return isPosCN(p) && getConcept(p.getChild()).isPrimitive();
    }

    /**
     * @return true iff P is a negative concept name
     * @param p
     *        the tree
     */
    public static boolean isNegCN(DLTree p) {
        return p.isName();
    }

    /**
     * @param p
     *        the tree
     * @param t
     *        tbox
     * @return true iff P is a negative non-primitive CN
     */
    public static boolean isNegNP(DLTree p, TBox t) {
        return isNegCN(p) && isNP(getConcept(p), t);
    }

    /**
     * @return true iff P is a negative primitive CN
     * @param p
     *        the tree
     */
    public static boolean isNegPC(DLTree p) {
        return isNegCN(p) && getConcept(p).isPrimitive();
    }

    /**
     * @return check whether P is in the form (and C D)
     * @param p
     *        the tree
     */
    public static boolean isAnd(DLTree p) {
        return p.isNOT() && p.getChild().isAND();
    }

    /**
     * @return true iff P is an OR expression
     * @param p
     *        the tree
     */
    public static boolean isOr(DLTree p) {
        return p.isAND();
    }

    /**
     * @return true iff P is a general FORALL expression
     * @param p
     *        the tree
     */
    public static boolean isForall(DLTree p) {
        return p.isNOT() && p.getChild().token() == Token.FORALL;
    }

    /**
     * @return true iff P is an object FORALL expression
     * @param p
     *        the tree
     */
    public static boolean isOForall(DLTree p) {
        return isForall(p)
                && !Role.resolveRole(p.getChild().getLeft()).isDataRole();
    }

    /**
     * @return true iff P is a FORALL expression suitable for absorption
     * @param p
     *        the tree
     */
    public static boolean isAbsForall(DLTree p) {
        if (!isOForall(p)) {
            return false;
        }
        DLTree C = p.getChild().getRight();
        if (isTop(C)) {
            return false;
        }
        return !C.isName() || !getConcept(C).isSystem();
    }

    private static final Map<String, AtomicInteger> created = new HashMap<>();

    /**
     * @param s
     *        s
     */
    private static void add(String s) {
        AtomicInteger i = created.get(s);
        if (i == null) {
            i = new AtomicInteger(1);
            created.put(s, i);
        } else {
            i.incrementAndGet();
        }
    }

    /**
     * @param s
     *        s
     * @return index for s
     */
    private static int get(String s) {
        AtomicInteger i = created.get(s);
        return i==null?0:i.get();
    }

    /** init SAbsRepCN */
    public static void SAbsRepCN() {
        add("SAbsRepCN");
    }

    /** init SAbsRepForall */
    public static void SAbsRepForall() {
        add("SAbsRepForall");
    }

    /** init SAbsBApply */
    public static void SAbsBApply() {
        add("SAbsBApply");
    }

    /** init SAbsSplit */
    public static void SAbsSplit() {
        add("SAbsSplit");
    }

    /** init SAbsTApply */
    public static void SAbsTApply() {
        add("SAbsTApply");
    }

    /** init SAbsCApply */
    public static void SAbsCApply() {
        add("SAbsCApply");
    }

    /** init SAbsCAttempt */
    public static void SAbsCAttempt() {
        add("SAbsCAttempt");
    }

    /** init SAbsRApply */
    public static void SAbsRApply() {
        add("SAbsRApply");
    }

    /** init SAbsRAttempt */
    public static void SAbsRAttempt() {
        add("SAbsRAttempt");
    }

    /** init SAbsInput */
    public static void SAbsInput() {
        add("SAbsInput");
    }

    /** init SAbsAction */
    public static void SAbsAction() {
        add("SAbsAction");
    }

    /** init SAbsNApply */
    public static void SAbsNApply() {
        add("SAbsNApply");
    }

    /** init SAbsNAttempt */
    public static void SAbsNAttempt() {
        add("SAbsNAttempt");
    }

    /**
     * @return true if map contains SAbsRepCN
     */
    public static boolean containsSAbsRepCN() {
        return created.containsKey("SAbsRepCN");
    }

    /**
     * @return true if map contains SAbsRepForall
     */
    public static boolean containsSAbsRepForall() {
        return created.containsKey("SAbsRepForall");
    }

    /**
     * @return true if map contains SAbsBApply
     */
    public static boolean containsSAbsBApply() {
        return created.containsKey("SAbsBApply");
    }

    /**
     * @return true if map contains SAbsSplit
     */
    public static boolean containsSAbsSplit() {
        return created.containsKey("SAbsSplit");
    }

    /**
     * @return true if map contains SAbsTApply
     */
    public static boolean containsSAbsTApply() {
        return created.containsKey("SAbsTApply");
    }

    /**
     * @return true if map contains SAbsCApply
     */
    public static boolean containsSAbsCApply() {
        return created.containsKey("SAbsCApply");
    }

    /**
     * @return true if map contains SAbsCAttempt
     */
    public static boolean containsSAbsCAttempt() {
        return created.containsKey("SAbsCAttempt");
    }

    /**
     * @return true if map contains SAbsRApply
     */
    public static boolean containsSAbsRApply() {
        return created.containsKey("SAbsRApply");
    }

    /**
     * @return true if map contains SAbsRAttempt
     */
    public static boolean containsSAbsRAttempt() {
        return created.containsKey("SAbsRAttempt");
    }

    /**
     * @return true if map contains SAbsInput
     */
    public static boolean containsSAbsInput() {
        return created.containsKey("SAbsInput");
    }

    /**
     * @return true if map contains SAbsAction
     */
    public static boolean containsSAbsAction() {
        return created.containsKey("SAbsAction");
    }

    /**
     * @return true if map contains SAbsNApply
     */
    public static boolean containsSAbsNApply() {
        return created.containsKey("SAbsNApply");
    }

    /**
     * @return true if map contains SAbsNAttempt
     */
    public static boolean containsSAbsNAttempt() {
        return created.containsKey("SAbsNAttempt");
    }

    /**
     * @return value for SAbsRepCN
     */
    public static int getSAbsRepCN() {
        return get("SAbsRepCN");
    }

    /**
     * @return value for SAbsRepForall
     */
    public static int getSAbsRepForall() {
        return get("SAbsRepForall");
    }

    /**
     * @return value for SAbsBApply
     */
    public static int getSAbsBApply() {
        return get("SAbsBApply");
    }

    /**
     * @return value for SAbsSplit
     */
    public static int getSAbsSplit() {
        return get("SAbsSplit");
    }

    /**
     * @return value for SAbsTApply
     */
    public static int getSAbsTApply() {
        return get("SAbsTApply");
    }

    /**
     * @return value for SAbsCApply
     */
    public static int getSAbsCApply() {
        return get("SAbsCApply");
    }

    /**
     * @return value for SAbsCAttempt
     */
    public static int getSAbsCAttempt() {
        return get("SAbsCAttempt");
    }

    /**
     * @return value for SAbsRApply
     */
    public static int getSAbsRApply() {
        return get("SAbsRApply");
    }

    /**
     * @return value for SAbsRAttempt
     */
    public static int getSAbsRAttempt() {
        return get("SAbsRAttempt");
    }

    /**
     * @return value for SAbsInput
     */
    public static int getSAbsInput() {
        return get("SAbsInput");
    }

    /**
     * @return value for SAbsAction
     */
    public static int getSAbsAction() {
        return get("SAbsAction");
    }

    /**
     * @return value for SAbsNApply
     */
    public static int getSAbsNApply() {
        return get("SAbsNApply");
    }

    /**
     * @return value for SAbsNAttempt
     */
    public static int getSAbsNAttempt() {
        return get("SAbsNAttempt");
    }

    /**
     * @param p
     *        dltree representing a forall
     * @return true iff P is a FORALL expression suitable for absorption with
     *         name at the end
     */
    @PortedFrom(file = "tAxiom.h", name = "isSimpleForall")
    public static boolean isSimpleForall(DLTree p) {
        if (!isAbsForall(p)) {
            return false;
        }
        DLTree C = p.getChild().getRight();
        // forall is simple if its filler is a name of a primitive concept
        // XXX check
        return C.isName() && getConcept(C).getDescription() == null;
    }
}
