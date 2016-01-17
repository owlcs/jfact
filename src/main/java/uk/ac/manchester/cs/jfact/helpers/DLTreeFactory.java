package uk.ac.manchester.cs.jfact.helpers;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry.resolveSynonym;
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import uk.ac.manchester.cs.jfact.kernel.*;

/** dl tree factory */
public class DLTreeFactory implements Serializable {

    private static final EnumSet<Token> snfCalls = EnumSet.of(TOP, BOTTOM, CNAME, INAME, RNAME, DNAME, DATAEXPR, NOT,
        INV, AND, FORALL, LE, SELF, RCOMPOSITION, PROJFROM, PROJINTO);

    private DLTreeFactory() {}

    /**
     * @return BOTTOM element
     */
    public static DLTree createBottom() {
        return new LEAFDLTree(new Lexeme(BOTTOM));
    }

    /**
     * @param r
     *        R
     * @return inverse
     */
    public static DLTree createInverse(DLTree r) {
        assert r != null;
        if (r.token() == INV) {
            return r.getChild().copy();
        }
        if (r.token() == RNAME) {
            if (isTopRole(r) || isBotRole(r)) {
                // top/bottom roles are inverses of themselves
                return r;
            }
            return new ONEDLTree(new Lexeme(INV), r);
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
    public static DLTree buildDisjAux(List<DLTree> arguments) {
        return DLTreeFactory.createSNFAnd(asList(arguments.stream().map(DLTree::copy).map(
            DLTreeFactory::createSNFNot)));
    }

    /**
     * @param c
     *        C
     * @param d
     *        D
     * @return and
     */
    public static DLTree createSNFAnd(@Nullable DLTree c, @Nullable DLTree d) {
        if (c == null) {
            assert d != null;
            return d;
        }
        if (d == null) {
            assert c != null;
            return c;
        }
        if (c.isTOP() || d.isBOTTOM()) {
            return d;
        }
        if (d.isTOP() || c.isBOTTOM()) {
            return c;
        }
        return new NDLTree(new Lexeme(AND), c, d);
    }

    /**
     * @param collection
     *        collection
     * @return and
     */
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
    public static DLTree createSNFAnd(Collection<DLTree> collection, DLTree ancestor) {
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
        if (l.size() == 1) {
            return l.get(0);
        }
        if (l.size() == collection.size()) {
            // no changes, return the ancestor
            return ancestor;
        }
        return new NDLTree(new Lexeme(AND), l);
    }

    /**
     * @param c
     *        tree to check
     * @param d
     *        contained tree
     * @return true if C contains D
     */
    public static boolean containsC(DLTree c, DLTree d) {
        if (c.isCName()) {
            return DLTree.equalTrees(c, d);
        }
        if (c.isAND()) {
            return c.getChildren().stream().anyMatch(p -> containsC(p, d));
        }
        return false;
    }

    /**
     * @param c
     *        c
     * @param d
     *        d
     * @return C and D
     */
    public static DLTree createSNFReducedAnd(@Nullable DLTree c, @Nullable DLTree d) {
        if (c == null || d == null) {
            return createSNFAnd(c, d);
        }
        if (d.isCName() && containsC(c, d)) {
            return c;
        } else if (d.isAND()) {
            for (DLTree t : d.getChildren()) {
                c = createSNFReducedAnd(c, t.copy());
            }
            return c;
        } else {
            return createSNFAnd(c, d);
        }
    }

    /**
     * create existential restriction of given formulas (\ER.C)
     * 
     * @param r
     *        R
     * @param c
     *        C
     * @return exist R C
     */
    public static DLTree createSNFExists(DLTree r, DLTree c) {
        // \ER.C . \not\AR.\not C
        return createSNFNot(createSNFForall(r, createSNFNot(c)));
    }

    /**
     * @param r
     *        R
     * @param c
     *        C
     * @return for all
     */
    public static DLTree createSNFForall(DLTree r, DLTree c) {
        if (c.isTOP()) {
            return c;
        } else if (isBotRole(r)) {
            return createTop();
        }
        if (isTopRole(r) && Role.resolveRole(r).isDataRole()) {
            return simplifyDataTopForall(c);
        }
        return new TWODLTree(new Lexeme(FORALL), r, c);
    }

    /**
     * @param r
     *        R
     * @return role
     */
    public static DLTree createRole(Role r) {
        return createEntry(r.isDataRole() ? DNAME : RNAME, r);
    }

    /**
     * @param tag
     *        tag
     * @param entry
     *        entry
     * @return entry
     */
    public static DLTree createEntry(Token tag, NamedEntry entry) {
        return new LEAFDLTree(new Lexeme(tag, entry));
    }

    /**
     * create at-most (LE) restriction of given formulas (max n R.C)
     * 
     * @param n
     *        n
     * @param r
     *        R
     * @param c
     *        C
     * @return at most
     */
    public static DLTree createSNFLE(int n, DLTree r, DLTree c) {
        if (c.isBOTTOM()) {
            // <= n R.F -> T
            return createTop();
        }
        if (n == 0) {
            return createSNFForall(r, createSNFNot(c));
        }
        if (isBotRole(r)) {
            return createTop();
        }
        if (isTopRole(r) && Role.resolveRole(r).isDataRole()) {
            return simplifyDataTopLE(n, c);
        }
        return new TWODLTree(new Lexeme(LE, n), r, c);
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
     * @param r
     *        R
     * @return self
     */
    public static DLTree createSNFSelf(DLTree r) {
        if (isBotRole(r)) {
            return createBottom();
            // loop on bottom role is always unsat
        }
        if (isTopRole(r)) {
            return createTop();
            // top role is reflexive
        }
        return new ONEDLTree(new Lexeme(SELF), r);
    }

    /**
     * @param n
     *        n
     * @param r
     *        R
     * @param c
     *        C
     * @return at least
     */
    public static DLTree createSNFGE(int n, DLTree r, DLTree c) {
        if (n == 0) {
            return createTop();
        }
        if (c.isBOTTOM()) {
            return c;
        }
        return createSNFNot(createSNFLE(n - 1, r, c));
    }

    /**
     * @param c
     *        C
     * @return not
     */
    public static DLTree createSNFNot(DLTree c) {
        assert c != null;
        if (c.isBOTTOM()) {
            // \not F = T
            return createTop();
        }
        if (c.isTOP()) {
            // \not T = F
            return createBottom();
        }
        if (c.token() == NOT) {
            // \not\not C = C
            return c.getChild().copy();
        }
        // general case
        return new ONEDLTree(new Lexeme(NOT), c);
    }

    /**
     * @param c
     *        C
     * @param ancestor
     *        ancestor
     * @return not
     */
    public static DLTree createSNFNot(DLTree c, DLTree ancestor) {
        assert c != null;
        if (c.isBOTTOM()) {
            // \not F = T
            return createTop();
        }
        if (c.isTOP()) {
            // \not T = F
            return createBottom();
        }
        if (c.token() == NOT) {
            // \not\not C = C
            return c.getChild().copy();
        }
        // general case
        return ancestor;
    }

    /**
     * create disjunction of given formulas
     * 
     * @param c
     *        C
     * @return OR C
     */
    public static DLTree createSNFOr(Collection<DLTree> c) {
        // C\or D . \not(\not C\and\not D)
        return createSNFNot(createSNFAnd(asList(c.stream().map(DLTreeFactory::createSNFNot))));
    }

    /**
     * @return TOP element
     */
    public static DLTree createTop() {
        return new LEAFDLTree(new Lexeme(TOP));
    }

    /**
     * @param tree
     *        tree
     * @return inverse
     */
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
    public static DLTree buildTree(Lexeme t, DLTree t1) {
        return new ONEDLTree(t, t1);
    }

    /**
     * @param t
     *        t
     * @return leaf tree
     */
    public static DLTree buildTree(Lexeme t) {
        return new LEAFDLTree(t);
    }

    // check if DL tree is a (data)role name
    private static boolean isRName(@Nullable DLTree t) {
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
     * @param r
     *        R
     * @return true if functional
     */
    public static boolean isFunctionalExpr(@Nullable DLTree t, NamedEntry r) {
        return t != null && t.token() == LE && r.equals(t.getLeft().elem().getNE()) && t.elem().getData() == 1 && t
            .getRight().isTOP();
    }

    /**
     * @param t
     *        t
     * @return true is SNF
     */
    public static boolean isSNF(@Nullable DLTree t) {
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
    public static boolean isSubTree(@Nullable DLTree t1, @Nullable DLTree t2) {
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
    public static boolean replaceSynonymsFromTree(@Nullable DLTree desc) {
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
            AtomicBoolean b = new AtomicBoolean(false);
            desc.children().forEach(d -> b.set(replaceSynonymsFromTree(d) || b.get()));
            return b.get();
        }
    }
}
