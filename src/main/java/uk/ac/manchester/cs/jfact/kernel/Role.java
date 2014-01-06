package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.DLTree.equalTrees;
import static uk.ac.manchester.cs.jfact.helpers.Helper.bpINVALID;
import static uk.ac.manchester.cs.jfact.kernel.Token.RCOMPOSITION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.FastSet;
import uk.ac.manchester.cs.jfact.helpers.FastSetFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.kernel.actors.AddRoleActor;
import conformance.Original;
import conformance.PortedFrom;

/** Role */
@PortedFrom(file = "tRole.h", name = "Role")
public class Role extends ClassifiableEntry {
    private static final long serialVersionUID = 11000L;

    static class KnownValue implements Serializable {
        private static final long serialVersionUID = 11000L;
        /** flag value */
        protected boolean value;
        /** whether flag set or not */
        protected boolean known;

        public KnownValue(boolean val) {
            value = val;
            known = false;
        }

        public KnownValue() {
            this(false);
        }

        /** @return true iff the value is known to be set */
        protected boolean isKnown() {
            return known;
        }

        /** @return the value */
        protected boolean getValue() {
            return value;
        }

        /** set the value; it is now known
         * 
         * @param val
         *            val */
        protected void setValue(boolean val) {
            value = val;
            known = true;
        }
    }

    /** role that are inverse of given one */
    @PortedFrom(file = "tRole.h", name = "Inverse")
    private Role inverse;
    /** Domain of role as a concept description; default null */
    @PortedFrom(file = "tRole.h", name = "pDomain")
    private DLTree pDomain;
    /** Domain of role as a concept description; default NULL */
    @PortedFrom(file = "tRole.h", name = "pSpecialDomain")
    private DLTree pSpecialDomain;
    /** domain in the form AR.Range for the complex roles */
    @PortedFrom(file = "tRole.h", name = "bpSpecialDomain")
    private int bpSpecialDomain;
    /** Domain of role as a pointer to DAG entry */
    @PortedFrom(file = "tRole.h", name = "bpDomain")
    private int bpDomain;
    /** pointer to role's functional definition DAG entry (or just TOP) */
    @PortedFrom(file = "tRole.h", name = "Functional")
    private int functional;
    /** is role relevant to current query */
    @PortedFrom(file = "tRole.h", name = "rel")
    private long rel;
    /** label of a domain (inverse role is used for a range label) */
    @PortedFrom(file = "tRole.h", name = "domLabel")
    private final MergableLabel domLabel = new MergableLabel();
    // for later filling
    @PortedFrom(file = "tRole.h", name = "Ancestor")
    private final List<Role> ancestorRoles = new ArrayList<Role>();
    @PortedFrom(file = "tRole.h", name = "Descendant")
    private final List<Role> descendantRoles = new ArrayList<Role>();
    /** set of the most functional super-roles */
    @PortedFrom(file = "tRole.h", name = "TopFunc")
    private final List<Role> topFunctionalRoles = new ArrayList<Role>();
    /** set of the roles that are disjoint with a given one */
    @PortedFrom(file = "tRole.h", name = "Disjoint")
    private final Set<Role> disjointRoles = new HashSet<Role>();
    /** all compositions in the form R1*R2*\ldots*Rn [= R */
    @PortedFrom(file = "tRole.h", name = "subCompositions")
    private final List<List<Role>> subCompositions = new ArrayList<List<Role>>();
    /** bit-vector of all parents */
    @PortedFrom(file = "tRole.h", name = "AncMap")
    private final FastSet ancestorMap = FastSetFactory.create();
    /** bit-vector of all roles disjoint with current */
    @PortedFrom(file = "tRole.h", name = "DJRoles")
    private final FastSet disjointRolesIndex = FastSetFactory.create();
    /** automaton for role */
    @PortedFrom(file = "tRole.h", name = "automaton")
    private final RoleAutomaton automaton = new RoleAutomaton();
    /** value for functionality */
    @PortedFrom(file = "tRole.h", name = "Functionality")
    private final KnownValue functionality = new KnownValue();
    /** value for symmetry */
    @PortedFrom(file = "tRole.h", name = "Symmetry")
    private final KnownValue symmetry = new KnownValue();
    /** value for asymmetricity */
    @PortedFrom(file = "tRole.h", name = "Asymmetry")
    private final KnownValue asymmetry = new KnownValue();
    /** value for transitivity */
    @PortedFrom(file = "tRole.h", name = "Transitivity")
    private final KnownValue transitivity = new KnownValue();
    /** value for reflexivity */
    @PortedFrom(file = "tRole.h", name = "Reflexivity")
    private final KnownValue reflexivity = new KnownValue();
    /** value for reflexivity */
    @PortedFrom(file = "tRole.h", name = "Irreflexivity")
    private final KnownValue irreflexivity = new KnownValue();
    /** flag to show that this role needs special R and D processing */
    @PortedFrom(file = "tRole.h", name = "SpecialDomain")
    private boolean specialDomain;

    /** add automaton of a sub-role to a given one
     * 
     * @param R
     *            R */
    @PortedFrom(file = "tRole.h", name = "addSubRoleAutomaton")
    private void addSubRoleAutomaton(Role R) {
        if (!equals(R)) {
            automaton.addRA(R.getAutomaton());
        }
    }

    @PortedFrom(file = "tRole.h", name = "addTrivialTransition")
    private void addTrivialTransition(Role r) {
        automaton.addTransitionSafe(RoleAutomaton.initial, new RATransition(
                RoleAutomaton.final_state, r));
    }

    /** @return get an automaton by a (possibly synonymical) role
     * @param R
     *            R
     * @param RInProcess
     *            RInProcess */
    @PortedFrom(file = "tRole.h", name = "completeAutomatonByRole")
    private RoleAutomaton completeAutomatonByRole(Role R, Set<Role> RInProcess) {
        // no synonyms here
        assert !R.isSynonym();
        // no case ...*S*... [= S
        assert R != this;
        R.completeAutomaton(RInProcess);
        return R.automaton;
    }

    /** merge domains */
    @PortedFrom(file = "tRole.h", name = "mergeSupersDomain")
    public void mergeSupersDomain() {
        for (int i = 0; i < ancestorRoles.size(); i++) {
            domLabel.merge(ancestorRoles.get(i).domLabel);
        }
        // for reflexive role -- merge domain and range labels
        if (isReflexive()) {
            domLabel.merge(getRangeLabel());
        }
        // for R1*R2*...*Rn [= R, merge dom(R) with dom(R1) and ran(R) with
        // ran(Rn)
        for (List<Role> q : subCompositions) {
            if (!q.isEmpty()) {
                domLabel.merge(q.get(0).domLabel);
                getRangeLabel().merge(q.get(q.size() - 1).getRangeLabel());
            }
        }
    }

    /** @return inverse of given role (non- version) */
    @PortedFrom(file = "tRole.h", name = "inverse")
    public Role inverse() {
        assert inverse != null;
        return resolveSynonym(inverse);
    }

    /** @return real inverse of a role (RO) */
    @PortedFrom(file = "tRole.h", name = "realInverse")
    public Role realInverse() {
        assert inverse != null;
        return inverse;
    }

    /** set inverse to given role
     * 
     * @param p
     *            p */
    @PortedFrom(file = "tRole.h", name = "setInverse")
    public void setInverse(Role p) {
        assert inverse == null;
        inverse = p;
    }

    /** @return Simple flag (not simple if role or any of its sub-roles is
     *         transitive) */
    @PortedFrom(file = "tRole.h", name = "isSimple")
    public boolean isSimple() {
        return automaton.isSimple();
    }

    /** @return special domain */
    @PortedFrom(file = "tRole.h", name = "getTSpecialDomain")
    public DLTree getTSpecialDomain() {
        return pSpecialDomain;
    }

    /** @return true iff role has a special domain */
    @PortedFrom(file = "tRole.h", name = "hasSpecialDomain")
    public boolean hasSpecialDomain() {
        return specialDomain;
    }

    /** init special domain; call this only after *ALL* the domains are known */
    @PortedFrom(file = "tRole.h", name = "initSpecialDomain")
    public void initSpecialDomain() {
        if (!hasSpecialDomain() || getTRange() == null) {
            pSpecialDomain = DLTreeFactory.createTop();
        } else {
            pSpecialDomain = DLTreeFactory.createSNFForall(
                    DLTreeFactory.createRole(this), getTRange().copy());
            pSpecialDomain = DLTreeFactory.createSNFForall(DLTreeFactory
                    .buildTree(new Lexeme(Token.RNAME, this)), getTRange().copy());
        }
    }

    /** set the special domain value
     * 
     * @param bp
     *            bp */
    @PortedFrom(file = "tRole.h", name = "setSpecialDomain")
    public void setSpecialDomain(int bp) {
        bpSpecialDomain = bp;
    }

    @Original
    private boolean dataRole;

    /** @return distinguish data- and non-data role */
    @Original
    public boolean isDataRole() {
        return dataRole;
    }

    /** @param action
     *            action */
    @Original
    public void setDataRole(boolean action) {
        dataRole = action;
    }

    /** @return test if role is functional (ie, have some functional ancestors) */
    @PortedFrom(file = "tRole.h", name = "isFunctional")
    public boolean isFunctional() {
        return functionality.getValue();
    }

    /** @return check whether the functionality of a role is known */
    @PortedFrom(file = "tRole.h", name = "isFunctionalityKnown")
    public boolean isFunctionalityKnown() {
        return functionality.isKnown();
    }

    /** set role functionality value
     * 
     * @param value
     *            value */
    @PortedFrom(file = "tRole.h", name = "setFunctional")
    public void setFunctional(boolean value) {
        functionality.setValue(value);
    }

    /** mark role (topmost) functional */
    @PortedFrom(file = "tRole.h", name = "setFunctional")
    public void setFunctional() {
        if (topFunctionalRoles.isEmpty()) {
            topFunctionalRoles.add(this);
        }
        this.setFunctional(true);
    }

    // transitivity
    /** @return check whether the role is transitive */
    @PortedFrom(file = "tRole.h", name = "isTransitive")
    public boolean isTransitive() {
        return transitivity.getValue();
    }

    /** @return check whether the transitivity of a role is known */
    @PortedFrom(file = "tRole.h", name = "isTransitivityKnown")
    public boolean isTransitivityKnown() {
        return transitivity.isKnown();
    }

    /** set the transitivity of both role and it's inverse
     * 
     * @param value
     *            value */
    @PortedFrom(file = "tRole.h", name = "setTransitive")
    public void setTransitive(boolean value) {
        transitivity.setValue(value);
        inverse().transitivity.setValue(value);
    }

    // symmetry
    /** @return check whether the role is symmetric */
    @PortedFrom(file = "tRole.h", name = "isSymmetric")
    public boolean isSymmetric() {
        return symmetry.getValue();
    }

    /** @return check whether the symmetry of a role is known */
    @PortedFrom(file = "tRole.h", name = "isSymmetryKnown")
    public boolean isSymmetryKnown() {
        return symmetry.isKnown();
    }

    /** set the symmetry of both role and it's inverse
     * 
     * @param value
     *            value */
    @PortedFrom(file = "tRole.h", name = "setSymmetric")
    public void setSymmetric(boolean value) {
        symmetry.setValue(value);
        inverse().symmetry.setValue(value);
    }

    // asymmetry
    /** @return check whether the role is asymmetric */
    @PortedFrom(file = "tRole.h", name = "isAsymmetric")
    public boolean isAsymmetric() {
        return asymmetry.getValue();
    }

    /** @return check whether the asymmetry of a role is known */
    @PortedFrom(file = "tRole.h", name = "isAsymmetryKnown")
    public boolean isAsymmetryKnown() {
        return asymmetry.isKnown();
    }

    /** set the asymmetry of both role and it's inverse
     * 
     * @param value
     *            value */
    @PortedFrom(file = "tRole.h", name = "setAsymmetric")
    public void setAsymmetric(boolean value) {
        asymmetry.setValue(value);
        inverse().asymmetry.setValue(value);
    }

    /** @return check whether the role is reflexive */
    @PortedFrom(file = "tRole.h", name = "isReflexive")
    public boolean isReflexive() {
        return reflexivity.getValue();
    }

    /** @return check whether the reflexivity of a role is known */
    @PortedFrom(file = "tRole.h", name = "isReflexivityKnown")
    public boolean isReflexivityKnown() {
        return reflexivity.isKnown();
    }

    /** set the reflexivity of both role and it's inverse
     * 
     * @param value
     *            value */
    @PortedFrom(file = "tRole.h", name = "setReflexive")
    public void setReflexive(boolean value) {
        reflexivity.setValue(value);
        inverse().reflexivity.setValue(value);
    }

    // irreflexivity
    /** @return check whether the role is irreflexive */
    @PortedFrom(file = "tRole.h", name = "isIrreflexive")
    public boolean isIrreflexive() {
        return irreflexivity.getValue();
    }

    /** @return check whether the irreflexivity of a role is known */
    @PortedFrom(file = "tRole.h", name = "isIrreflexivityKnown")
    public boolean isIrreflexivityKnown() {
        return irreflexivity.isKnown();
    }

    /** set the irreflexivity of both role and it's inverse
     * 
     * @param value
     *            value */
    @PortedFrom(file = "tRole.h", name = "setIrreflexive")
    public void setIrreflexive(boolean value) {
        irreflexivity.setValue(value);
        inverse().irreflexivity.setValue(value);
    }

    /** @return check if the role is topmost-functional (ie, has no functional
     *         ancestors) */
    @PortedFrom(file = "tRole.h", name = "isTopFunc")
    public boolean isTopFunc() {
        // check for emptyness is here due to case where a role is determined to
        // be a functional
        return !topFunctionalRoles.isEmpty() && topFunctionalRoles.get(0).equals(this);
    }

    /** set functional attribute to given value (functional DAG vertex)
     * 
     * @param fNode
     *            fNode */
    @PortedFrom(file = "tRole.h", name = "setFunctional")
    public void setFunctional(int fNode) {
        functional = fNode;
    }

    /** @return get the Functional DAG vertex */
    @PortedFrom(file = "tRole.h", name = "getFunctional")
    public int getFunctional() {
        return functional;
    }

    // relevance
    /** @param lab
     *            lab
     * @return is given role relevant to given Labeller's state */
    @PortedFrom(file = "tRole.h", name = "isRelevant")
    public boolean isRelevant(long lab) {
        return lab == rel;
    }

    /** make given role relevant to given Labeller's state
     * 
     * @param lab
     *            lab */
    @PortedFrom(file = "tRole.h", name = "setRelevant")
    public void setRelevant(long lab) {
        rel = lab;
    }

    // Sorted reasoning interface
    /** @return label of the role's domain */
    @PortedFrom(file = "tRole.h", name = "getDomainLabel")
    public MergableLabel getDomainLabel() {
        return domLabel;
    }

    /** @return label of the role's range */
    @PortedFrom(file = "tRole.h", name = "getRangeLabel")
    public MergableLabel getRangeLabel() {
        return inverse().domLabel;
    }

    /** add p to domain of the role
     * 
     * @param p
     *            p */
    @PortedFrom(file = "tRole.h", name = "setDomain")
    public void setDomain(DLTree p) {
        if (equalTrees(pDomain, p)) {
            // usual case when you have a name for inverse role
        } else if (DLTreeFactory.isFunctionalExpr(p, this)) {
            this.setFunctional();
            // functional restriction in the role domain means the role is
            // functional
        } else {
            pDomain = DLTreeFactory.createSNFAnd(Arrays.asList(pDomain, p));
        }
    }

    /** add p to range of the role
     * 
     * @param p
     *            p */
    @PortedFrom(file = "tRole.h", name = "setRange")
    public void setRange(DLTree p) {
        inverse().setDomain(p);
    }

    /** @return domain-as-a-tree of the role */
    @PortedFrom(file = "tRole.h", name = "getTDomain")
    public DLTree getTDomain() {
        return pDomain;
    }

    /** @return range-as-a-tree of the role */
    @PortedFrom(file = "tRole.h", name = "getTRange")
    private DLTree getTRange() {
        return inverse().pDomain;
    }

    /** merge to Domain all domains from super-roles */
    @PortedFrom(file = "tRole.h", name = "collectDomainFromSupers")
    public void collectDomainFromSupers() {
        for (int i = 0; i < ancestorRoles.size(); i++) {
            setDomain(ancestorRoles.get(i).pDomain.copy());
        }
    }

    /** set domain-as-a-bipointer to a role
     * 
     * @param p
     *            p */
    @PortedFrom(file = "tRole.h", name = "setBPDomain")
    public void setBPDomain(int p) {
        bpDomain = p;
    }

    /** @return domain-as-a-bipointer of the role */
    @PortedFrom(file = "tRole.h", name = "getBPDomain")
    public int getBPDomain() {
        return bpDomain;
    }

    /** @return range-as-a-bipointer of the role */
    @PortedFrom(file = "tRole.h", name = "getBPRange")
    public int getBPRange() {
        return inverse().bpDomain;
    }

    // disjoint roles
    /** set R and THIS as a disjoint; use it after Anc/Desc are determined
     * 
     * @param R
     *            R */
    @PortedFrom(file = "tRole.h", name = "addDisjointRole")
    public void addDisjointRole(Role R) {
        disjointRoles.add(R);
        for (Role p : R.descendantRoles) {
            disjointRoles.add(p);
            p.disjointRoles.add(this);
        }
    }

    /** check (and correct) case whether R != S for R [= S */
    @PortedFrom(file = "tRole.h", name = "checkHierarchicalDisjoint")
    public void checkHierarchicalDisjoint() {
        this.checkHierarchicalDisjoint(this);
        if (isReflexive()) {
            this.checkHierarchicalDisjoint(inverse());
        }
    }

    /** @return check whether a role is disjoint with anything */
    @PortedFrom(file = "tRole.h", name = "isDisjoint")
    public boolean isDisjoint() {
        return !disjointRoles.isEmpty();
    }

    /** @param r
     *            r
     * @return check whether a role is disjoint with R */
    @PortedFrom(file = "tRole.h", name = "isDisjoint")
    public boolean isDisjoint(Role r) {
        return disjointRolesIndex.contains(r.getAbsoluteIndex());
    }

    /** @return check if role is a non-strict sub-role of R
     * @param r
     *            r */
    @PortedFrom(file = "tRole.h", name = "<")
    private boolean lesser(Role r) {
        return isDataRole() == r.isDataRole()
                && ancestorMap.contains(r.getAbsoluteIndex());
    }

    /** @param r
     *            r
     * @return true if lesser or equal to r */
    @PortedFrom(file = "tRole.h", name = "<=")
    public boolean lesserequal(Role r) {
        return equals(r) || lesser(r);
    }

    /** @return list of ancestor roles */
    @PortedFrom(file = "tRole.h", name = "begin_anc")
    public List<Role> getAncestor() {
        return ancestorRoles;
    }

    /** @return func super-roles w/o func parents via iterator */
    @PortedFrom(file = "tRole.h", name = "begin_topfunc")
    public List<Role> begin_topfunc() {
        return topFunctionalRoles;
    }

    /** fills BITMAP with the role's ancestors
     * 
     * @param bitmap
     *            bitmap */
    @PortedFrom(file = "tRole.h", name = "addAncestorsToBitMap")
    private void addAncestorsToBitMap(FastSet bitmap) {
        for (int i = 0; i < ancestorRoles.size(); i++) {
            bitmap.add(ancestorRoles.get(i).getAbsoluteIndex());
        }
    }

    /** add composition to a role
     * 
     * @param tree
     *            tree */
    @PortedFrom(file = "tRole.h", name = "addComposition")
    public void addComposition(DLTree tree) {
        List<Role> RS = new ArrayList<Role>();
        fillsComposition(RS, tree);
        subCompositions.add(RS);
    }

    /** @return RA for the role */
    @PortedFrom(file = "tRole.h", name = "getAutomaton")
    public RoleAutomaton getAutomaton() {
        return automaton;
    }

    // completing internal constructions
    /** eliminate told role cycle
     * 
     * @return rewritten role */
    @PortedFrom(file = "tRole.h", name = "eliminateToldCycles")
    public Role eliminateToldCycles() {
        Set<Role> RInProcess = new HashSet<Role>();
        List<Role> ToldSynonyms = new ArrayList<Role>();
        return this.eliminateToldCycles(RInProcess, ToldSynonyms);
    }

    /** complete role automaton
     * 
     * @param nRoles
     *            nRoles */
    @PortedFrom(file = "tRole.h", name = "completeAutomaton")
    public void completeAutomaton(int nRoles) {
        Set<Role> RInProcess = new HashSet<Role>();
        this.completeAutomaton(RInProcess);
        automaton.setup(nRoles, isDataRole());
    }

    /** check whether role description is consistent */
    @PortedFrom(file = "tRole.h", name = "consistent")
    public void consistent() {
        if (isSimple()) {
            return;
        }
        if (isFunctional()) {
            throw new ReasonerInternalException("Non simple role used as simple: "
                    + getName());
        }
        if (isDataRole()) {
            throw new ReasonerInternalException("Non simple role used as simple: "
                    + getName());
        }
        if (this.isDisjoint()) {
            throw new ReasonerInternalException("Non simple role used as simple: "
                    + getName());
        }
    }

    @Original
    private static Role resolveRoleHelper(DLTree t, String r) {
        if (t == null) {
            throw new ReasonerInternalException("Role expression expected: " + r);
        }
        switch (t.token()) {
            case RNAME: // role name
            case DNAME: // data role name
                return (Role) t.elem().getNE();
            case INV: // inversion
                return resolveRoleHelper(t.getChild(), r).inverse();
            default: // error
                throw new ReasonerInternalException("Invalid role expression: " + r
                        + " but got: " + t);
        }
    }

    /** @param t
     *            t
     * @param r
     *            r reason
     * @return R or -R for T in the form (inv ... (inv R)...); remove synonyms */
    @PortedFrom(file = "tRole.h", name = "resolveRole")
    public static Role resolveRole(DLTree t, String r) {
        return resolveSynonym(resolveRoleHelper(t, r));
    }

    /** @param t
     *            t
     * @return R or -R for T in the form (inv ... (inv R)...); remove synonyms */
    @PortedFrom(file = "tRole.h", name = "resolveRole")
    public static Role resolveRole(DLTree t) {
        return resolveSynonym(resolveRoleHelper(t, ""));
    }

    protected Role(String name) {
        super(name);
        inverse = null;
        pDomain = null;
        pSpecialDomain = null;
        bpDomain = bpINVALID;
        bpSpecialDomain = bpINVALID;
        functional = bpINVALID;
        rel = 0;
        specialDomain = false;
        setCompletelyDefined(true);
        // role hierarchy is completely defined by it's parents
        addTrivialTransition(this);
    }

    /** @return (unsigned) unique index of the role */
    @PortedFrom(file = "tRole.h", name = "getIndex")
    public int getAbsoluteIndex() {
        int i = 2 * extId;
        return i > 0 ? i : 1 - i;
    }

    @Original
    private int buildIndex() {
        int i = 2 * extId;
        return i > 0 ? i : 1 - i;
    }

    @PortedFrom(file = "tRole.h", name = "fillsComposition")
    private void fillsComposition(List<Role> Composition, DLTree tree) {
        if (tree.token() == RCOMPOSITION) {
            fillsComposition(Composition, tree.getLeft());
            fillsComposition(Composition, tree.getRight());
        } else {
            Composition.add(resolveRole(tree));
        }
    }

    /** add features */
    @PortedFrom(file = "tRole.h", name = "addFeaturesToSynonym")
    public void addFeaturesToSynonym() {
        if (!isSynonym()) {
            return;
        }
        Role syn = resolveSynonym(this);
        if (isFunctional() && !syn.isFunctional()) {
            syn.setFunctional();
        }
        if (isTransitive()) {
            syn.setTransitive(true);
        }
        if (isReflexive()) {
            syn.setReflexive(true);
        }
        if (isDataRole()) {
            syn.setDataRole(true);
        }
        if (pDomain != null) {
            syn.setDomain(pDomain.copy());
        }
        if (this.isDisjoint()) {
            syn.disjointRoles.addAll(disjointRoles);
        }
        syn.subCompositions.addAll(subCompositions);
        toldSubsumers.clear();
        addParent(syn);
    }

    @PortedFrom(file = "tRole.h", name = "eliminateToldCycles")
    private Role eliminateToldCycles(Set<Role> RInProcess, List<Role> ToldSynonyms) {
        if (isSynonym()) {
            return null;
        }
        if (RInProcess.contains(this)) {
            ToldSynonyms.add(this);
            return this;
        }
        Role ret = null;
        RInProcess.add(this);
        removeSynonymsFromParents();
        for (ClassifiableEntry r : toldSubsumers) {
            if ((ret = ((Role) r).eliminateToldCycles(RInProcess, ToldSynonyms)) != null) {
                if (ret.equals(this)) {
                    Collections.sort(ToldSynonyms, new RoleCompare());
                    ret = ToldSynonyms.get(0);
                    for (int i = 1; i < ToldSynonyms.size(); i++) {
                        Role p = ToldSynonyms.get(i);
                        p.setSynonym(ret);
                        ret.addParents(p.getToldSubsumers());
                    }
                    ToldSynonyms.clear();
                    RInProcess.remove(this);
                    return ret.eliminateToldCycles(RInProcess, ToldSynonyms);
                } else {
                    ToldSynonyms.add(this);
                    break;
                }
            }
        }
        RInProcess.remove(this);
        return ret;
    }

    @Override
    public String toString() {
        return extName + " " + extId;
    }

    /** @param o
     *            o */
    @PortedFrom(file = "tRole.h", name = "print")
    public void print(LogAdapter o) {
        o.print("Role \"", getName(), "\"(")
                .print(getId())
                .print(")", isTransitive() ? "T" : "", isReflexive() ? "R" : "",
                        isTopFunc() ? "t" : "", isFunctional() ? "F" : "",
                        isDataRole() ? "D" : "");
        if (isSynonym()) {
            o.print(" = \"", getSynonym().getName(), "\"\n");
            return;
        }
        if (!toldSubsumers.isEmpty()) {
            o.print(" parents={\"");
            List<ClassifiableEntry> l = new ArrayList<ClassifiableEntry>(toldSubsumers);
            for (int i = 0; i < l.size(); i++) {
                if (i > 0) {
                    o.print("\", \"");
                }
                o.print(l.get(i).getName());
            }
            o.print("\"}");
        }
        if (!disjointRoles.isEmpty()) {
            o.print(" disjoint with {\"");
            List<Role> l = new ArrayList<Role>(disjointRoles);
            for (int i = 0; i < disjointRoles.size(); i++) {
                if (i > 0) {
                    o.print("\", \"");
                }
                o.print(l.get(i).getName());
            }
            o.print("\"}");
        }
        if (pDomain != null) {
            o.print(" Domain=(").print(bpDomain).print(")=", pDomain);
        }
        if (getTRange() != null) {
            o.print(" Range=(").print(getBPRange()).print(")=", getTRange());
        }
        o.print("\nAutomaton (size ")
                .print(automaton.size())
                .print("): ", automaton.isISafe() ? "I" : "i",
                        automaton.isOSafe() ? "O" : "o");
        automaton.print(o);
        o.print("\n");
    }

    /** @param pTax
     *            pTax
     * @param nRoles
     *            nRoles */
    @PortedFrom(file = "tRole.h", name = "initADbyTaxonomy")
    public void initADbyTaxonomy(Taxonomy pTax, int nRoles) {
        assert isClassified();
        assert ancestorRoles.isEmpty() && descendantRoles.isEmpty();
        // Note that Top/Bottom are not connected to taxonomy yet.
        // fills ancestors by the taxonomy
        AddRoleActor anc = new AddRoleActor(ancestorRoles);
        pTax.getRelativesInfo(getTaxVertex(), anc, false, false, true);
        // fills descendants by the taxonomy
        AddRoleActor desc = new AddRoleActor(descendantRoles);
        pTax.getRelativesInfo(getTaxVertex(), desc, false, false, false);
        // init map for fast Anc/Desc access
        addAncestorsToBitMap(ancestorMap);
    }

    /** post process */
    @PortedFrom(file = "tRole.h", name = "postProcess")
    public void postProcess() {
        initTopFunc();
        if (this.isDisjoint()) {
            initDJMap();
        }
    }

    @PortedFrom(file = "tRole.h", name = "isRealTopFunc")
    private boolean isRealTopFunc() {
        if (!isFunctional()) {
            return false;
        }
        for (int i = 0; i < ancestorRoles.size(); i++) {
            if (ancestorRoles.get(i).isTopFunc()) {
                return false;
            }
        }
        return true;
    }

    @PortedFrom(file = "tRole.h", name = "initTopFunc")
    private void initTopFunc() {
        if (isRealTopFunc()) {
            return;
        }
        if (isTopFunc()) {
            topFunctionalRoles.clear();
        }
        for (int i = 0; i < ancestorRoles.size(); i++) {
            Role p = ancestorRoles.get(i);
            if (p.isRealTopFunc()) {
                topFunctionalRoles.add(p);
            }
        }
        if (!topFunctionalRoles.isEmpty()) {
            functionality.setValue(true);
        }
    }

    @PortedFrom(file = "tRole.h", name = "checkHierarchicalDisjoint")
    private void checkHierarchicalDisjoint(Role R) {
        if (disjointRoles.contains(R)) {
            setDomain(DLTreeFactory.createBottom());
            disjointRoles.clear();
            return;
        }
        for (Role p : R.descendantRoles) {
            if (disjointRoles.contains(p)) {
                p.setDomain(DLTreeFactory.createBottom());
                disjointRoles.remove(p);
                p.disjointRoles.clear();
            }
        }
    }

    @PortedFrom(file = "tRole.h", name = "initDJMap")
    private void initDJMap() {
        for (Role q : disjointRoles) {
            disjointRolesIndex.add(q.getAbsoluteIndex());
        }
    }

    @PortedFrom(file = "tRole.h", name = "preprocessComposition")
    private void preprocessComposition(List<Role> RS) {
        boolean same = false;
        int last = RS.size() - 1;
        // TODO doublecheck, strange assignments to what is in the list
        for (int i = 0; i < RS.size(); i++) {
            Role p = RS.get(i);
            Role R = resolveSynonym(p);
            if (R.isBottom()) {
                RS.clear();
                return;
            }
            if (R.equals(this)) {
                if (i != 0 && i != last) {
                    throw new ReasonerInternalException("Cycle in RIA " + getName());
                }
                if (same) {
                    if (last == 1) {
                        RS.clear();
                        setTransitive(true);
                        return;
                    } else {
                        throw new ReasonerInternalException("Cycle in RIA " + getName());
                    }
                } else {
                    same = true;
                }
            }
            RS.set(i, R);
        }
    }

    @PortedFrom(file = "tRole.h", name = "completeAutomaton")
    private void completeAutomaton(Set<Role> RInProcess) {
        if (automaton.isCompleted()) {
            return;
            // if we found a cycle...
        }
        if (RInProcess.contains(this)) {
            throw new ReasonerInternalException("Cycle in RIA " + getName());
        }
        // start processing role
        RInProcess.add(this);
        // make sure that all sub-roles already have completed automata
        for (Role p : descendantRoles) {
            p.completeAutomaton(RInProcess);
        }
        // add automata for complex role inclusions
        for (List<Role> q : subCompositions) {
            addSubCompositionAutomaton(q, RInProcess);
        }
        // check for the transitivity
        if (isTransitive()) {
            automaton.addTransitionSafe(RoleAutomaton.final_state, new RATransition(
                    RoleAutomaton.initial));
        }
        // here automaton is complete
        automaton.setCompleted(true);
        if (!isBottom()) {
            for (ClassifiableEntry p : toldSubsumers) {
                Role R = (Role) resolveSynonym(p);
                R.addSubRoleAutomaton(this);
                if (hasSpecialDomain()) {
                    R.specialDomain = true;
                }
            }
        }
        // finish processing role
        RInProcess.remove(this);
    }

    /** add automaton for a role composition
     * 
     * @param RS
     *            RS
     * @param RInProcess
     *            RInProcess */
    @PortedFrom(file = "tRole.h", name = "addSubCompositionAutomaton")
    private void addSubCompositionAutomaton(List<Role> RS, Set<Role> RInProcess) {
        // first preprocess the role chain
        preprocessComposition(RS);
        if (RS.isEmpty()) {
            return;
        }
        // here we need a special treatment for R&D
        specialDomain = true;
        // tune iterators and states
        int p = 0;
        int p_last = RS.size() - 1;
        int from = RoleAutomaton.initial, to = RoleAutomaton.final_state;
        if (RS.get(0).equals(this)) {
            ++p;
            from = RoleAutomaton.final_state;
        } else if (RS.get(p_last).equals(this)) {
            --p_last;
            to = RoleAutomaton.initial;
        }
        // make sure the role chain contain at least one element
        assert p <= p_last;
        // create a chain
        boolean oSafe = false;
        // we couldn't assume that the current role
        // automaton is i- or o-safe
        automaton.initChain(from);
        for (; p != p_last; ++p) {
            oSafe = automaton.addToChain(completeAutomatonByRole(RS.get(p), RInProcess),
                    oSafe);
        }
        // add the last automaton to chain
        automaton.addToChain(completeAutomatonByRole(RS.get(p), RInProcess), oSafe, to);
    }

    /** @return inverse */
    @Original
    public Role getInverse() {
        return inverse;
    }
}
