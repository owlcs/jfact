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

    /** statistics */
    public static final String S_ABS_N_ATTEMPT = "SAbsNAttempt";
    /** statistics */
    public static final String S_ABS_N_APPLY = "SAbsNApply";
    /** statistics */
    public static final String S_ABS_ACTION = "SAbsAction";
    /** statistics */
    public static final String S_ABS_INPUT = "SAbsInput";
    /** statistics */
    public static final String S_ABS_R_ATTEMPT = "SAbsRAttempt";
    /** statistics */
    public static final String S_ABS_R_APPLY = "SAbsRApply";
    /** statistics */
    public static final String S_ABS_C_ATTEMPT = "SAbsCAttempt";
    /** statistics */
    public static final String S_ABS_C_APPLY = "SAbsCApply";
    /** statistics */
    public static final String S_ABS_T_APPLY = "SAbsTApply";
    /** statistics */
    public static final String S_ABS_SPLIT = "SAbsSplit";
    /** statistics */
    public static final String S_ABS_B_APPLY = "SAbsBApply";
    /** statistics */
    public static final String S_ABS_REP_FORALL = "SAbsRepForall";
    /** statistics */
    public static final String S_ABS_REP_CN = "SAbsRepCN";
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
        return isForall(p) && !Role.resolveRole(p.getChild().getLeft()).isDataRole();
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

    private final Map<String, AtomicInteger> created = new HashMap<>();

    /**
     * @param s
     *        s
     */
    private void add(String s) {
        created.computeIfAbsent(s, (x) -> new AtomicInteger()).incrementAndGet();
    }

    /**
     * @param s
     *        s
     * @return index for s
     */
    private int get(String s) {
        return created.getOrDefault(s, ZERO).intValue();
    }

    /** init SAbsRepCN */
    public void SAbsRepCN() {
        add(S_ABS_REP_CN);
    }

    /** init SAbsRepForall */
    public void SAbsRepForall() {
        add(S_ABS_REP_FORALL);
    }

    /** init SAbsBApply */
    public void SAbsBApply() {
        add(S_ABS_B_APPLY);
    }

    /** init SAbsSplit */
    public void SAbsSplit() {
        add(S_ABS_SPLIT);
    }

    /** init SAbsTApply */
    public void SAbsTApply() {
        add(S_ABS_T_APPLY);
    }

    /** init SAbsCApply */
    public void SAbsCApply() {
        add(S_ABS_C_APPLY);
    }

    /** init SAbsCAttempt */
    public void SAbsCAttempt() {
        add(S_ABS_C_ATTEMPT);
    }

    /** init SAbsRApply */
    public void SAbsRApply() {
        add(S_ABS_R_APPLY);
    }

    /** init SAbsRAttempt */
    public void SAbsRAttempt() {
        add(S_ABS_R_ATTEMPT);
    }

    /** init SAbsInput */
    public void SAbsInput() {
        add(S_ABS_INPUT);
    }

    /** init SAbsAction */
    public void SAbsAction() {
        add(S_ABS_ACTION);
    }

    /** init SAbsNApply */
    public void SAbsNApply() {
        add(S_ABS_N_APPLY);
    }

    /** init SAbsNAttempt */
    public void SAbsNAttempt() {
        add(S_ABS_N_ATTEMPT);
    }

    /**
     * @return true if map contains SAbsRepCN
     */
    public boolean containsSAbsRepCN() {
        return created.containsKey(S_ABS_REP_CN);
    }

    /**
     * @return true if map contains SAbsRepForall
     */
    public boolean containsSAbsRepForall() {
        return created.containsKey(S_ABS_REP_FORALL);
    }

    /**
     * @return true if map contains SAbsBApply
     */
    public boolean containsSAbsBApply() {
        return created.containsKey(S_ABS_B_APPLY);
    }

    /**
     * @return true if map contains SAbsSplit
     */
    public boolean containsSAbsSplit() {
        return created.containsKey(S_ABS_SPLIT);
    }

    /**
     * @return true if map contains SAbsTApply
     */
    public boolean containsSAbsTApply() {
        return created.containsKey(S_ABS_T_APPLY);
    }

    /**
     * @return true if map contains SAbsCApply
     */
    public boolean containsSAbsCApply() {
        return created.containsKey(S_ABS_C_APPLY);
    }

    /**
     * @return true if map contains SAbsCAttempt
     */
    public boolean containsSAbsCAttempt() {
        return created.containsKey(S_ABS_C_ATTEMPT);
    }

    /**
     * @return true if map contains SAbsRApply
     */
    public boolean containsSAbsRApply() {
        return created.containsKey(S_ABS_R_APPLY);
    }

    /**
     * @return true if map contains SAbsRAttempt
     */
    public boolean containsSAbsRAttempt() {
        return created.containsKey(S_ABS_R_ATTEMPT);
    }

    /**
     * @return true if map contains SAbsInput
     */
    public boolean containsSAbsInput() {
        return created.containsKey(S_ABS_INPUT);
    }

    /**
     * @return true if map contains SAbsAction
     */
    public boolean containsSAbsAction() {
        return created.containsKey(S_ABS_ACTION);
    }

    /**
     * @return true if map contains SAbsNApply
     */
    public boolean containsSAbsNApply() {
        return created.containsKey(S_ABS_N_APPLY);
    }

    /**
     * @return true if map contains SAbsNAttempt
     */
    public boolean containsSAbsNAttempt() {
        return created.containsKey(S_ABS_N_ATTEMPT);
    }

    /**
     * @return value for SAbsRepCN
     */
    public int getSAbsRepCN() {
        return get(S_ABS_REP_CN);
    }

    /**
     * @return value for SAbsRepForall
     */
    public int getSAbsRepForall() {
        return get(S_ABS_REP_FORALL);
    }

    /**
     * @return value for SAbsBApply
     */
    public int getSAbsBApply() {
        return get(S_ABS_B_APPLY);
    }

    /**
     * @return value for SAbsSplit
     */
    public int getSAbsSplit() {
        return get(S_ABS_SPLIT);
    }

    /**
     * @return value for SAbsTApply
     */
    public int getSAbsTApply() {
        return get(S_ABS_T_APPLY);
    }

    /**
     * @return value for SAbsCApply
     */
    public int getSAbsCApply() {
        return get(S_ABS_C_APPLY);
    }

    /**
     * @return value for SAbsCAttempt
     */
    public int getSAbsCAttempt() {
        return get(S_ABS_C_ATTEMPT);
    }

    /**
     * @return value for SAbsRApply
     */
    public int getSAbsRApply() {
        return get(S_ABS_R_APPLY);
    }

    /**
     * @return value for SAbsRAttempt
     */
    public int getSAbsRAttempt() {
        return get(S_ABS_R_ATTEMPT);
    }

    /**
     * @return value for SAbsInput
     */
    public int getSAbsInput() {
        return get(S_ABS_INPUT);
    }

    /**
     * @return value for SAbsAction
     */
    public int getSAbsAction() {
        return get(S_ABS_ACTION);
    }

    /**
     * @return value for SAbsNApply
     */
    public int getSAbsNApply() {
        return get(S_ABS_N_APPLY);
    }

    /**
     * @return value for SAbsNAttempt
     */
    public int getSAbsNAttempt() {
        return get(S_ABS_N_ATTEMPT);
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
        return C.isName() && getConcept(C).getDescription() == null;
    }
}
