package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.DLTree.*;
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.io.Serializable;
import java.util.*;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.helpers.*;
import uk.ac.manchester.cs.jfact.kernel.actors.AddRoleActor;

public class Role extends ClassifiableEntry {
    static class KnownValue {
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

        /** set the value; it is now known */
        protected void setValue(boolean val) {
            value = val;
            known = true;
        }
    }

    /** role that are inverse of given one */
    private Role inverse;
    /** Domain of role as a concept description; default null */
    private DLTree pDomain;
    /** Domain of role as a concept description; default NULL */
    private DLTree pSpecialDomain;
    /** domain in the form AR.Range for the complex roles */
    private int bpSpecialDomain;
    /** Domain of role as a pointer to DAG entry */
    private int bpDomain;
    /** pointer to role's functional definition DAG entry (or just TOP) */
    private int functional;
    /** is role relevant to current query */
    private long rel;
    /** label of a domain (inverse role is used for a range label) */
    private MergableLabel domLabel = new MergableLabel();
    // for later filling
    private List<Role> ancestorRoles = new ArrayList<Role>();
    private List<Role> descendantRoles = new ArrayList<Role>();
    /** set of the most functional super-roles */
    private List<Role> topFunctionalRoles = new ArrayList<Role>();
    /** set of the roles that are disjoint with a given one */
    private Set<Role> disjointRoles = new HashSet<Role>();
    /** all compositions in the form R1*R2*\ldots*Rn [= R */
    private List<List<Role>> subCompositions = new ArrayList<List<Role>>();
    /** bit-vector of all parents */
    private FastSet ancestorMap = FastSetFactory.create();
    /** bit-vector of all roles disjoint with current */
    private FastSet disjointRolesIndex = FastSetFactory.create();
    /** automaton for role */
    private RoleAutomaton automaton = new RoleAutomaton();
    /** value for functionality */
    private KnownValue functionality = new KnownValue();
    /** value for symmetry */
    private KnownValue symmetry = new KnownValue();
    /** value for asymmetricity */
    private KnownValue asymmetry = new KnownValue();
    /** value for transitivity */
    private KnownValue transitivity = new KnownValue();
    /** value for reflexivity */
    private KnownValue reflexivity = new KnownValue();
    /** value for reflexivity */
    private KnownValue irreflexivity = new KnownValue();
    /** flag to show that this role needs special R&D processing */
    private boolean specialDomain;

    /** add automaton of a sub-role to a given one */
    private void addSubRoleAutomaton(Role R) {
        if (!equals(R)) {
            automaton.addRA(R.getAutomaton());
        }
    }

    private void addTrivialTransition(Role r) {
        automaton.addTransitionSafe(RoleAutomaton.initial, new RATransition(
                RoleAutomaton.final_state, r));
    }

    /** get an automaton by a (possibly synonymical) role */
    private RoleAutomaton completeAutomatonByRole(Role R, Set<Role> RInProcess) {
        assert !R.isSynonym(); // no synonyms here
        assert R != this; // no case ...*S*... [= S
        R.completeAutomaton(RInProcess);
        return R.automaton;
    }

    void mergeSupersDomain() {
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

    /** get inverse of given role (non- version) */
    public Role inverse() {
        assert inverse != null;
        return resolveSynonym(inverse);
    }

    /** get real inverse of a role (RO) */
    public Role realInverse() {
        assert inverse != null;
        return inverse;
    }

    /** set inverse to given role */
    public void setInverse(Role p) {
        assert inverse == null;
        inverse = p;
    }

    /** a Simple flag (not simple if role or any of its sub-roles is transitive) */
    public boolean isSimple() {
        return automaton.isSimple();
    }

    public DLTree getTSpecialDomain() {
        return pSpecialDomain;
    }

    /** @return true iff role has a special domain */
    public boolean hasSpecialDomain() {
        return specialDomain;
    }

    /** init special domain; call this only after *ALL* the domains are known */
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

    /** set the special domain value */
    public void setSpecialDomain(int bp) {
        bpSpecialDomain = bp;
    }

    private boolean dataRole;

    /** distinguish data- and non-data role */
    public boolean isDataRole() {
        return dataRole;
    }

    public void setDataRole() {
        dataRole = true;
    }

    public void clearDataRole() {
        dataRole = false;
    }

    public void setDataRole(boolean action) {
        dataRole = action;
    }

    /** test if role is functional (ie, have some functional ancestors) */
    public boolean isFunctional() {
        return functionality.getValue();
    }

    /** check whether the functionality of a role is known */
    public boolean isFunctionalityKnown() {
        return functionality.isKnown();
    }

    /** set role functionality value */
    public void setFunctional(boolean value) {
        functionality.setValue(value);
    }

    /** mark role (topmost) functional */
    public void setFunctional() {
        if (topFunctionalRoles.isEmpty()) {
            topFunctionalRoles.add(this);
        }
        this.setFunctional(true);
    }

    // transitivity
    /** check whether the role is transitive */
    public boolean isTransitive() {
        return transitivity.getValue();
    }

    /** check whether the transitivity of a role is known */
    public boolean isTransitivityKnown() {
        return transitivity.isKnown();
    }

    /** set the transitivity of both role and it's inverse */
    public void setTransitive(boolean value) {
        transitivity.setValue(value);
        inverse().transitivity.setValue(value);
    }

    public void setTransitive() {
        this.setTransitive(true);
    }

    // symmetry
    /** check whether the role is symmetric */
    public boolean isSymmetric() {
        return symmetry.getValue();
    }

    /** check whether the symmetry of a role is known */
    public boolean isSymmetryKnown() {
        return symmetry.isKnown();
    }

    /** set the symmetry of both role and it's inverse */
    public void setSymmetric(boolean value) {
        symmetry.setValue(value);
        inverse().symmetry.setValue(value);
    }

    public void setSymmetric() {
        this.setSymmetric(true);
    }

    // asymmetry
    /** check whether the role is asymmetric */
    public boolean isAsymmetric() {
        return asymmetry.getValue();
    }

    /** check whether the asymmetry of a role is known */
    public boolean isAsymmetryKnown() {
        return asymmetry.isKnown();
    }

    /** set the asymmetry of both role and it's inverse */
    public void setAsymmetric(boolean value) {
        asymmetry.setValue(value);
        inverse().asymmetry.setValue(value);
    }

    /** check whether the role is reflexive */
    public boolean isReflexive() {
        return reflexivity.getValue();
    }

    /** check whether the reflexivity of a role is known */
    public boolean isReflexivityKnown() {
        return reflexivity.isKnown();
    }

    /** set the reflexivity of both role and it's inverse */
    public void setReflexive(boolean value) {
        reflexivity.setValue(value);
        inverse().reflexivity.setValue(value);
    }

    public void setReflexive() {
        this.setReflexive(true);
    }

    // irreflexivity
    /** check whether the role is irreflexive */
    public boolean isIrreflexive() {
        return irreflexivity.getValue();
    }

    /** check whether the irreflexivity of a role is known */
    public boolean isIrreflexivityKnown() {
        return irreflexivity.isKnown();
    }

    /** set the irreflexivity of both role and it's inverse */
    public void setIrreflexive(boolean value) {
        irreflexivity.setValue(value);
        inverse().irreflexivity.setValue(value);
    }

    public void setIrreflexive() {
        this.setIrreflexive(true);
    }

    /** check if the role is topmost-functional (ie, has no functional ancestors) */
    public boolean isTopFunc() {
        // check for emptyness is here due to case where a role is determined to
        // be a functional
        return !topFunctionalRoles.isEmpty() && topFunctionalRoles.get(0).equals(this);
    }

    /** set functional attribute to given value (functional DAG vertex) */
    public void setFunctional(int fNode) {
        functional = fNode;
    }

    /** get the Functional DAG vertex */
    public int getFunctional() {
        return functional;
    }

    // relevance
    /** is given role relevant to given Labeller's state */
    public boolean isRelevant(long lab) {
        return lab == rel;
    }

    /** make given role relevant to given Labeller's state */
    public void setRelevant(long lab) {
        rel = lab;
    }

    // Sorted reasoning interface
    /** get label of the role's domain */
    public MergableLabel getDomainLabel() {
        return domLabel;
    }

    /** get label of the role's range */
    public MergableLabel getRangeLabel() {
        return inverse().domLabel;
    }

    /** add p to domain of the role */
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

    /** add p to range of the role */
    public void setRange(DLTree p) {
        inverse().setDomain(p);
    }

    /** get domain-as-a-tree of the role */
    public DLTree getTDomain() {
        return pDomain;
    }

    /** get range-as-a-tree of the role */
    private DLTree getTRange() {
        return inverse().pDomain;
    }

    /** merge to Domain all domains from super-roles */
    public void collectDomainFromSupers() {
        for (int i = 0; i < ancestorRoles.size(); i++) {
            setDomain(ancestorRoles.get(i).pDomain.copy());
        }
    }

    /** set domain-as-a-bipointer to a role */
    public void setBPDomain(int p) {
        bpDomain = p;
    }

    /** get domain-as-a-bipointer of the role */
    public int getBPDomain() {
        return bpDomain;
    }

    /** get range-as-a-bipointer of the role */
    public int getBPRange() {
        return inverse().bpDomain;
    }

    @Override
    public int getIndex() {
        // TODO Auto-generated method stub
        return super.getIndex();
    }

    // disjoint roles
    /** set R and THIS as a disjoint; use it after Anc/Desc are determined */
    public void addDisjointRole(Role R) {
        disjointRoles.add(R);
        for (Role p : R.descendantRoles) {
            disjointRoles.add(p);
            p.disjointRoles.add(this);
        }
    }

    /** check (and correct) case whether R != S for R [= S */
    public void checkHierarchicalDisjoint() {
        this.checkHierarchicalDisjoint(this);
        if (isReflexive()) {
            this.checkHierarchicalDisjoint(inverse());
        }
    }

    /** check whether a role is disjoint with anything */
    public boolean isDisjoint() {
        return !disjointRoles.isEmpty();
    }

    /** check whether a role is disjoint with R */
    public boolean isDisjoint(Role r) {
        return disjointRolesIndex.contains(r.getAbsoluteIndex());
    }

    /** check if role is a non-strict sub-role of R */
    private boolean lesser(Role r) {
        return isDataRole() == r.isDataRole()
                && ancestorMap.contains(r.getAbsoluteIndex());
    }

    public boolean lesserequal(Role r) {
        return equals(r) || lesser(r);
    }

    public List<Role> getAncestor() {
        return ancestorRoles;
    }

    /** get access to the func super-roles w/o func parents via iterator */
    public List<Role> begin_topfunc() {
        return topFunctionalRoles;
    }

    /** fills BITMAP with the role's ancestors */
    private void addAncestorsToBitMap(FastSet bitmap) {
        for (int i = 0; i < ancestorRoles.size(); i++) {
            bitmap.add(ancestorRoles.get(i).getAbsoluteIndex());
        }
    }

    /** add composition to a role */
    public void addComposition(DLTree tree) {
        List<Role> RS = new ArrayList<Role>();
        fillsComposition(RS, tree);
        subCompositions.add(RS);
    }

    /** get access to a RA for the role */
    public RoleAutomaton getAutomaton() {
        return automaton;
    }

    // completing internal constructions
    /** eliminate told role cycle */
    public Role eliminateToldCycles() {
        Set<Role> RInProcess = new HashSet<Role>();
        List<Role> ToldSynonyms = new ArrayList<Role>();
        return this.eliminateToldCycles(RInProcess, ToldSynonyms);
    }

    /** complete role automaton */
    public void completeAutomaton(int nRoles) {
        Set<Role> RInProcess = new HashSet<Role>();
        this.completeAutomaton(RInProcess);
        automaton.setup(nRoles, isDataRole());
    }

    /** check whether role description is consistent */
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

    private static Role resolveRoleHelper(DLTree t) {
        if (t == null) {
            throw new ReasonerInternalException("Role expression expected");
        }
        switch (t.token()) {
            case RNAME: // role name
            case DNAME: // data role name
                return (Role) t.elem().getNE();
            case INV: // inversion
                return resolveRoleHelper(t.getChild()).inverse();
            default: // error
                throw new ReasonerInternalException("Invalid role expression");
        }
    }

    /** @return R or -R for T in the form (inv ... (inv R)...); remove synonyms */
    public static Role resolveRole(DLTree t) {
        return resolveSynonym(resolveRoleHelper(t));
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

    /** get (unsigned) unique index of the role */
    public int getAbsoluteIndex() {
        int i = 2 * extId;
        return i > 0 ? i : 1 - i;
    }

    private int buildIndex() {
        int i = 2 * extId;
        return i > 0 ? i : 1 - i;
    }

    private void fillsComposition(List<Role> Composition, DLTree tree) {
        if (tree.token() == RCOMPOSITION) {
            fillsComposition(Composition, tree.getLeft());
            fillsComposition(Composition, tree.getRight());
        } else {
            Composition.add(resolveRole(tree));
        }
    }

    public void addFeaturesToSynonym() {
        if (!isSynonym()) {
            return;
        }
        Role syn = resolveSynonym(this);
        if (isFunctional() && !syn.isFunctional()) {
            syn.setFunctional();
        }
        if (isTransitive()) {
            syn.setTransitive();
        }
        if (isReflexive()) {
            syn.setReflexive();
        }
        if (isDataRole()) {
            syn.setDataRole();
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

    public void print(LogAdapter o) {
        o.print("Role \"", getName(), "\"(", getId(), ")", isTransitive() ? "T" : "",
                isReflexive() ? "R" : "", isTopFunc() ? "t" : "", isFunctional() ? "F"
                        : "", isDataRole() ? "D" : "");
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
            o.print(" Domain=(", bpDomain, ")=", pDomain);
        }
        if (getTRange() != null) {
            o.print(" Range=(", getBPRange(), ")=", getTRange());
        }
        o.print("\nAutomaton (size ", automaton.size(), "): ", automaton.isISafe() ? "I"
                : "i", automaton.isOSafe() ? "O" : "o");
        automaton.print(o);
        o.print("\n");
    }

    public void initADbyTaxonomy(Taxonomy pTax, int nRoles) {
        assert isClassified(); // safety check
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

    public void postProcess() {
        initTopFunc();
        if (this.isDisjoint()) {
            initDJMap();
        }
    }

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

    private void initDJMap() {
        for (Role q : disjointRoles) {
            disjointRolesIndex.add(q.getAbsoluteIndex());
        }
    }

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
                        this.setTransitive();
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
        for (ClassifiableEntry p : toldSubsumers) {
            Role R = (Role) resolveSynonym(p);
            R.addSubRoleAutomaton(this);
            if (hasSpecialDomain()) {
                R.specialDomain = true;
            }
        }
        // finish processing role
        RInProcess.remove(this);
    }

    /** add automaton for a role composition */
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
        boolean oSafe = false; // we couldn't assume that the current role
                               // automaton is i- or o-safe
        automaton.initChain(from);
        for (; p != p_last; ++p) {
            oSafe = automaton.addToChain(completeAutomatonByRole(RS.get(p), RInProcess),
                    oSafe);
        }
        // add the last automaton to chain
        automaton.addToChain(completeAutomatonByRole(RS.get(p), RInProcess), oSafe, to);
    }

    public Role getInverse() {
        return inverse;
    }
}

class RoleCompare implements Comparator<Role>, Serializable {
    @Override
    public int compare(Role p, Role q) {
        int n = p.getId();
        int m = q.getId();
        if (n > 0 && m < 0) {
            return -1;
        }
        if (n < 0 && m > 0) {
            return 1;
        }
        return 0;
    }
}
