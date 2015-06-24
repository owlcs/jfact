package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static java.util.stream.Collectors.toList;
import static uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry.resolveSynonym;
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

import uk.ac.manchester.cs.jfact.kernel.*;

/** dl tree factory */
public class DLTreeFactory implements Serializable {

    private static final long serialVersionUID = 11000L;
    private static final EnumSet<Token> snfCalls = EnumSet.of(TOP, BOTTOM, CNAME, INAME, RNAME, DNAME, DATAEXPR, NOT,
        INV, AND, FORALL, LE, SELF, RCOMPOSITION, PROJFROM, PROJINTO);

    /**
     * @return BOTTOM element
     */
    @Nonnull
    public static DLTree createBottom() {
        return new LEAFDLTree(new Lexeme(BOTTOM));
    }

    /**
     * @param R
     *        R
     * @return inverse
     */
    @Nonnull
    public static DLTree createInverse(DLTree R) {
        assert R != null;
        if (R.token() == INV) {
            return R.getChild().copy();
        }
        if (R.token() == RNAME) {
            if (isTopRole(R) || isBotRole(R)) {
                // top/bottom roles are inverses of themselves
                return R;
            }
            return new ONEDLTree(new Lexeme(INV), R);
        }
        throw new UnreachableSituationException();
    }

    // Semantic Locality checking support. DO NOT used in usual reasoning
    /**
     * @param dr
     *        dr
     * @return true iff a data range DR is semantically equivalent to TOP.
     *         FIXME!! good approximation for now
     */
    public static boolean isSemanticallyDataTop(DLTree dr) {
        return dr.elem().getToken() == TOP;
    }

    /**
     * @param dr
     *        dr
     * @return true iff a data range DR is semantically equivalent to BOTTOM.
     *         FIXME!! good approximation for now
     */
    public static boolean isSemanticallyDataBottom(DLTree dr) {
        return dr.elem().getToken() == BOTTOM;
    }

    /**
     * @param dr
     *        dr
     * @param n
     *        n
     * @return true iff the cardinality of a given data range DR is greater than
     *         N. FIXME!! good approximation for now
     */
    @SuppressWarnings("unused")
    public static boolean isDataRangeBigEnough(DLTree dr, int n) {
        // XXX bug
        return true;
    }

    /**
     * simplify universal restriction with top data role
     * 
     * @param dr
     *        dr
     * @return simplified tree
     */
    @Nonnull
    public static DLTree simplifyDataTopForall(DLTree dr) {
        // if the filler (dr) is TOP (syntactically or semantically), then the
        // forall is top
        if (isSemanticallyDataTop(dr)) {
            return createTop();
        }
        // in any other case the attempt to restrict the data domain will fail
        return createBottom();
    }

    /**
     * simplify minimal cardinality restriction with top data role
     * 
     * @param n
     *        n
     * @param dr
     *        dr
     * @return simplified tree
     */
    @Nonnull
    public static DLTree simplifyDataTopLE(int n, DLTree dr) {
        // if the filler (dr) is BOTTOM (syntactically or semantically), then
        // the LE is top
        if (isSemanticallyDataBottom(dr)) {
            return createTop();
        }
        // if the size of a filler is smaller than the cardinality, then it's
        // always possible to make a restriction
        if (!isDataRangeBigEnough(dr, n)) {
            return createTop();
        }
        // in any other case the attempt to restrict the data domain will fail
        return createBottom();
    }

    /**
     * @param arguments
     *        arguments to AND
     * @return a construction in the form AND (\neg q_i)
     */
    @Nonnull
    public static DLTree buildDisjAux(List<DLTree> arguments) {
        return DLTreeFactory.createSNFAnd(arguments.stream().map(i -> DLTreeFactory.createSNFNot(i.copy())).collect(
            toList()));
    }

    /**
     * @param C
     *        C
     * @param D
     *        D
     * @return and
     */
    @Nonnull
    public static DLTree createSNFAnd(DLTree C, DLTree D) {
        if (C == null) {
            assert D != null;
            return D;
        }
        if (D == null) {
            assert C != null;
            return C;
        }
        if (C.isTOP() || D.isBOTTOM()) {
            return D;
        }
        if (D.isTOP() || C.isBOTTOM()) {
            return C;
        }
        return new NDLTree(new Lexeme(AND), C, D);
    }

    /**
     * @param collection
     *        collection
     * @return and
     */
    @Nonnull
    public static DLTree createSNFAnd(Collection<DLTree> collection) {
        if (collection.isEmpty()) {
            return createTop();
        }
        if (collection.size() == 1) {
            DLTree next = collection.iterator().next();
            assert next != null;
            return next;
        }
        List<DLTree> l = new ArrayList<>();
        for (DLTree d : collection) {
            if (d == null) {
                continue;
            }
            if (d.isBOTTOM()) {
                return createBottom();
            }
            if (d.isAND()) {
                l.addAll(d.getChildren());
            } else {
                l.add(d);
            }
        }
        if (l.isEmpty()) {
            return createTop();
        }
        if (l.size() == 1) {
            DLTree dlTree = l.get(0);
            assert dlTree != null;
            return dlTree;
        }
        return new NDLTree(new Lexeme(AND), l);
    }

    /**
     * @param collection
     *        collection
     * @param ancestor
     *        ancestor
     * @return and
     */
    @Nonnull
    public static DLTree createSNFAnd(Collection<DLTree> collection, @Nonnull DLTree ancestor) {
        if (collection.size() == 1) {
            return collection.iterator().next();
        }
        boolean hasTop = false;
        List<DLTree> l = new ArrayList<>();
        for (DLTree d : collection) {
            if (d.isTOP()) {
                hasTop = true;
            }
            if (d.isBOTTOM()) {
                return createBottom();
            }
            if (d.isAND()) {
                l.addAll(d.getChildren());
            } else {
                l.add(d);
            }
        }
        if (hasTop && l.isEmpty()) {
            return createTop();
        }
        // XXX port to 4
        if (l.size() == 1) {
            return l.get(0);
        }
        if (l.size() == collection.size()) {
            // no changes, return the ancestor
            return ancestor;
        }
        return new NDLTree(new Lexeme(AND), l);
    }

    public static boolean containsC(DLTree C, DLTree D) {
        if (C.isCName()) {
            return DLTree.equalTrees(C, D);
        }
        if (C.isAND()) {
            return C.getChildren().stream().anyMatch(p -> containsC(p, D));
        }
        return false;
    }

    /**
     * @param C
     *        c
     * @param D
     *        d
     * @return C and D
     */
    @Nonnull
    public static DLTree createSNFReducedAnd(DLTree C, DLTree D) {
        if (C == null || D == null) {
            return createSNFAnd(C, D);
        }
        if (D.isCName() && containsC(C, D)) {
            return C;
        } else if (D.isAND()) {
            for (DLTree d : D.getChildren()) {
                C = createSNFReducedAnd(C, d.copy());
            }
            return C;
        } else {
            return createSNFAnd(C, D);
        }
    }

    /**
     * create existential restriction of given formulas (\ER.C)
     * 
     * @param R
     *        R
     * @param C
     *        C
     * @return exist R C
     */
    @Nonnull
    public static DLTree createSNFExists(DLTree R, DLTree C) {
        // \ER.C . \not\AR.\not C
        return createSNFNot(createSNFForall(R, createSNFNot(C)));
    }

    /**
     * @param R
     *        R
     * @param C
     *        C
     * @return for all
     */
    @Nonnull
    public static DLTree createSNFForall(DLTree R, DLTree C) {
        if (C.isTOP()) {
            return C;
        } else if (isBotRole(R)) {
            return createTop();
        }
        if (isTopRole(R) && Role.resolveRole(R).isDataRole()) {
            return simplifyDataTopForall(C);
        }
        return new TWODLTree(new Lexeme(FORALL), R, C);
    }

    /**
     * @param R
     *        R
     * @return role
     */
    @Nonnull
    public static DLTree createRole(Role R) {
        return createEntry(R.isDataRole() ? DNAME : RNAME, R);
    }

    /**
     * @param tag
     *        tag
     * @param entry
     *        entry
     * @return entry
     */
    @Nonnull
    public static DLTree createEntry(Token tag, NamedEntry entry) {
        return new LEAFDLTree(new Lexeme(tag, entry));
    }

    /**
     * create at-most (LE) restriction of given formulas (max n R.C)
     * 
     * @param n
     *        n
     * @param R
     *        R
     * @param C
     *        C
     * @return at most
     */
    @Nonnull
    public static DLTree createSNFLE(int n, DLTree R, DLTree C) {
        if (C.isBOTTOM()) {
            // <= n R.F -> T;
            return createTop();
        }
        if (n == 0) {
            return createSNFForall(R, createSNFNot(C));
        }
        if (isBotRole(R)) {
            return createTop();
        }
        if (isTopRole(R) && Role.resolveRole(R).isDataRole()) {
            return simplifyDataTopLE(n, C);
        }
        return new TWODLTree(new Lexeme(LE, n), R, C);
    }

    /**
     * check whether T is a bottom (empty) role
     * 
     * @param t
     *        tree
     * @return true if bottom
     */
    public static boolean isBotRole(DLTree t) {
        return isRName(t) && t.elem().getNE().isBottom();
    }

    /**
     * check whether T is a top (universal) role
     * 
     * @param t
     *        tree
     * @return true if top role
     */
    public static boolean isTopRole(DLTree t) {
        return isRName(t) && t.elem().getNE().isTop();
    }

    /**
     * create SELF restriction for role R
     * 
     * @param R
     *        R
     * @return self
     */
    @Nonnull
    public static DLTree createSNFSelf(DLTree R) {
        if (isBotRole(R)) {
            return createBottom();
            // loop on bottom role is always unsat
        }
        if (isTopRole(R)) {
            return createTop();
            // top role is reflexive
        }
        return new ONEDLTree(new Lexeme(SELF), R);
    }

    /**
     * @param n
     *        n
     * @param R
     *        R
     * @param C
     *        C
     * @return at least
     */
    public static @Nonnull DLTree createSNFGE(int n, DLTree R, DLTree C) {
        if (n == 0) {
            return createTop();
        }
        if (C.isBOTTOM()) {
            return C;
        } else {
            return createSNFNot(createSNFLE(n - 1, R, C));
        }
    }

    /**
     * @param C
     *        C
     * @return not
     */
    @Nonnull
    public static DLTree createSNFNot(DLTree C) {
        assert C != null;
        if (C.isBOTTOM()) {
            // \not F = T
            return createTop();
        }
        if (C.isTOP()) {
            // \not T = F
            return createBottom();
        }
        if (C.token() == NOT) {
            // \not\not C = C
            return C.getChild().copy();
        }
        // general case
        return new ONEDLTree(new Lexeme(NOT), C);
    }

    /**
     * @param C
     *        C
     * @param ancestor
     *        ancestor
     * @return not
     */
    @Nonnull
    public static DLTree createSNFNot(@Nonnull DLTree C, @Nonnull DLTree ancestor) {
        assert C != null;
        if (C.isBOTTOM()) {
            // \not F = T
            return createTop();
        }
        if (C.isTOP()) {
            // \not T = F
            return createBottom();
        }
        if (C.token() == NOT) {
            // \not\not C = C
            return C.getChild().copy();
        }
        // general case
        return ancestor;
    }

    /**
     * create disjunction of given formulas
     * 
     * @param C
     *        C
     * @return OR C
     */
    @Nonnull
    public static DLTree createSNFOr(Collection<DLTree> C) {
        // C\or D . \not(\not C\and\not D)
        return createSNFNot(createSNFAnd(C.stream().map(d -> createSNFNot(d)).collect(toList())));
    }

    /**
     * @return TOP element
     */
    @Nonnull
    public static DLTree createTop() {
        return new LEAFDLTree(new Lexeme(TOP));
    }

    /**
     * @param tree
     *        tree
     * @return inverse
     */
    @Nonnull
    public static DLTree inverseComposition(DLTree tree) {
        // XXX this needs to be checked with a proper test
        // see rolemaster.cpp, inverseComposition
        if (tree.token() == RCOMPOSITION) {
            return tree.accept(new ReverseCloningVisitor());
        } else {
            return new LEAFDLTree(new Lexeme(RNAME, Role.resolveRole(tree).inverse()));
        }
    }

    /**
     * get DLTree by a given TDE
     * 
     * @param t
     *        t
     * @return wrapped entry
     */
    @Nonnull
    public static DLTree wrap(NamedEntry t) {
        return new LEAFDLTree(new Lexeme(DATAEXPR, t));
    }

    /**
     * get TDE by a given DLTree
     * 
     * @param t
     *        t
     * @return unwrapped entry
     */
    @Nonnull
    public static NamedEntry unwrap(DLTree t) {
        return t.elem().getNE();
    }

    /**
     * @param t
     *        t
     * @param t1
     *        t1
     * @param t2
     *        t2
     * @return tree with two children
     */
    @Nonnull
    public static DLTree buildTree(Lexeme t, DLTree t1, DLTree t2) {
        return new TWODLTree(t, t1, t2);
    }

    /**
     * @param t
     *        t
     * @param l
     *        list
     * @return tree with multiple children
     */
    @Nonnull
    public static DLTree buildTree(Lexeme t, Collection<DLTree> l) {
        return new NDLTree(t, l);
    }

    /**
     * @param t
     *        t
     * @param t1
     *        t1
     * @return single child tree
     */
    @Nonnull
    public static DLTree buildTree(Lexeme t, DLTree t1) {
        return new ONEDLTree(t, t1);
    }

    /**
     * @param t
     *        t
     * @return leaf tree
     */
    @Nonnull
    public static DLTree buildTree(Lexeme t) {
        return new LEAFDLTree(t);
    }

    // check if DL tree is a (data)role name
    private static boolean isRName(DLTree t) {
        if (t == null) {
            return false;
        }
        if (t.token() == RNAME || t.token() == DNAME) {
            return true;
        }
        return false;
    }

    /**
     * check whether T is an expression in the form (atmost 1 RNAME)
     * 
     * @param t
     *        t
     * @param R
     *        R
     * @return true if functional
     */
    public static boolean isFunctionalExpr(DLTree t, NamedEntry R) {
        return t != null && t.token() == LE && R.equals(t.getLeft().elem().getNE()) && t.elem().getData() == 1 && t
            .getRight().isTOP();
    }

    /**
     * @param t
     *        t
     * @return true is SNF
     */
    public static boolean isSNF(DLTree t) {
        if (t == null) {
            return true;
        }
        if (snfCalls.contains(t.token())) {
            return isSNF(t.getLeft()) && isSNF(t.getRight());
        }
        return false;
    }

    /**
     * @param t1
     *        t1
     * @param t2
     *        t2
     * @return true if t2 is a subtree
     */
    public static boolean isSubTree(DLTree t1, DLTree t2) {
        if (t1 == null || t1.isTOP()) {
            return true;
        }
        if (t2 == null) {
            return false;
        }
        if (t1.isAND()) {
            return t1.getChildren().stream().allMatch(t -> isSubTree(t, t2));
        }
        if (t2.isAND()) {
            return t2.getChildren().stream().anyMatch(t -> isSubTree(t1, t));
        }
        return t1.equals(t2);
    }

    /**
     * check whether T is U-Role
     * 
     * @param t
     *        t
     * @return true if universal
     */
    public static boolean isUniversalRole(DLTree t) {
        return isRName(t) && t.elem().getNE().isTop();
    }

    /**
     * @param desc
     *        desc
     * @return true if changes happen
     */
    public static boolean replaceSynonymsFromTree(DLTree desc) {
        if (desc == null) {
            return false;
        }
        if (desc.isName()) {
            ClassifiableEntry entry = (ClassifiableEntry) desc.elem.getNE();
            if (entry.isSynonym()) {
                entry = resolveSynonym(entry);
                // check for TOP/BOTTOM
                if (entry.isTop()) {
                    desc.elem = new Lexeme(TOP);
                } else if (entry.isBottom()) {
                    desc.elem = new Lexeme(BOTTOM);
                } else {
                    desc.elem = new Lexeme(((Concept) entry).isSingleton() ? INAME : CNAME, entry);
                }
                return true;
            } else {
                return false;
            }
        } else {
            // XXX port to 4
            AtomicBoolean b = new AtomicBoolean(false);
            desc.children().forEach(d -> b.set( replaceSynonymsFromTree(d)||b.get()));
            return b.get();
        }
    }
}
