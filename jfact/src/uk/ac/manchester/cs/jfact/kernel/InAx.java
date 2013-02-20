package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.HashMap;
import java.util.Map;

import uk.ac.manchester.cs.jfact.helpers.DLTree;
import conformance.PortedFrom;

/** DLTREE utils */
@PortedFrom(file = "tAxiom.h", name = "InAx")
public class InAx {
    /** build an RW concept from a given [C|I]NAME-rooted DLTree */
    static Concept getConcept(DLTree p) {
        return (Concept) p.elem().getNE();
    }

    /** @return true iff P is a TOP */
    static boolean isTop(DLTree p) {
        return p.isBOTTOM();
    }

    /** @return true iff P is a BOTTOM */
    static boolean isBot(DLTree p) {
        return p.isTOP();
    }

    /** @return true iff P is a positive concept name */
    static boolean isPosCN(DLTree p) {
        return p.isNOT() && p.getChild().isName();
    }

    /** @return true iff P is a positive non-primitive CN */
    static boolean isPosNP(DLTree p) {
        return isPosCN(p) && !getConcept(p.getChild()).isPrimitive();
    }

    /** @return true iff P is a positive primitive CN */
    static boolean isPosPC(DLTree p) {
        return isPosCN(p) && getConcept(p.getChild()).isPrimitive();
    }

    /** @return true iff P is a negative concept name */
    static boolean isNegCN(DLTree p) {
        return p.isName();
    }

    /** @return true iff P is a negative non-primitive CN */
    static boolean isNegNP(DLTree p) {
        return isNegCN(p) && !getConcept(p).isPrimitive();
    }

    /** @return true iff P is a negative primitive CN */
    static boolean isNegPC(DLTree p) {
        return isNegCN(p) && getConcept(p).isPrimitive();
    }

    /** check whether P is in the form (and C D) */
    static boolean isAnd(DLTree p) {
        return p.isNOT() && p.getChild().isAND();
    }

    /** @return true iff P is an OR expression */
    static boolean isOr(DLTree p) {
        return p.isAND();
    }

    /** @return true iff P is a general FORALL expression */
    static boolean isForall(DLTree p) {
        return p.isNOT() && p.getChild().token() == Token.FORALL;
    }

    /** @return true iff P is an object FORALL expression */
    static boolean isOForall(DLTree p) {
        return isForall(p) && !Role.resolveRole(p.getChild().getLeft()).isDataRole();
    }

    /** @return true iff P is a FORALL expression suitable for absorption */
    static boolean isAbsForall(DLTree p) {
        if (!isOForall(p)) {
            return false;
        }
        DLTree C = p.getChild().getRight();
        if (isTop(C)) {
            return false;
        }
        return !C.isName() || !getConcept(C).isSystem();
    }

    static Map<String, Integer> created = new HashMap<String, Integer>();

    static void add(String s) {
        if (created.containsKey(s)) {
            created.put(s, created.get(s) + 1);
        } else {
            created.put(s, 1);
        }
    }

    static int get(String s) {
        return created.containsKey(s) ? created.get(s) : 0;
    }

    static void SAbsRepCN() {
        add("SAbsRepCN");
    }

    static void SAbsRepForall() {
        add("SAbsRepForall");
    }

    static void SAbsBApply() {
        add("SAbsBApply");
    }

    static void SAbsSplit() {
        add("SAbsSplit");
    }

    static void SAbsTApply() {
        add("SAbsTApply");
    }

    static void SAbsCApply() {
        add("SAbsCApply");
    }

    static void SAbsCAttempt() {
        add("SAbsCAttempt");
    }

    static void SAbsRApply() {
        add("SAbsRApply");
    }

    static void SAbsRAttempt() {
        add("SAbsRAttempt");
    }

    static void SAbsInput() {
        add("SAbsInput");
    }

    static void SAbsAction() {
        add("SAbsAction");
    }

    static void SAbsNApply() {
        add("SAbsNApply");
    }

    static void SAbsNAttempt() {
        add("SAbsNAttempt");
    }
}
