package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;
import static uk.ac.manchester.cs.jfact.kernel.DagTag.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import conformance.Original;
import conformance.PortedFrom;
import gnu.trove.map.hash.TIntObjectHashMap;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.ArrayIntMap;
import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Reference;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.kernel.state.DLCompletionTreeSaveState;
import uk.ac.manchester.cs.jfact.kernel.state.SaveList;

/** completion tree */
@PortedFrom(file = "dlCompletionTree.h", name = "DlCompletionTree")
public class DlCompletionTree implements Comparable<DlCompletionTree>, Serializable {

    /** restore blocked node */
    static class UnBlock extends Restorer {

        private final DlCompletionTree p;
        private final DlCompletionTree unblockBlocker;
        private final DepSet dep;
        private final boolean pBlocked;
        private final boolean dBlocked;

        public UnBlock(DlCompletionTree q) {
            p = q;
            unblockBlocker = q.blocker;
            dep = DepSet.create(q.pDep);
            pBlocked = q.pBlocked;
            dBlocked = q.dBlocked;
        }

        @Override
        public void restore() {
            p.setBlocker(unblockBlocker);
            p.pDep = DepSet.create(dep);
            p.pBlocked = pBlocked;
            p.dBlocked = dBlocked;
        }
    }

    /** restore (un)cached node */
    static class CacheRestorer extends Restorer {

        private final DlCompletionTree p;
        private final boolean isCached;

        public CacheRestorer(DlCompletionTree q) {
            p = q;
            isCached = q.cached;
        }

        @Override
        public void restore() {
            p.cached = isCached;
        }
    }

    /** restore node after IR set change */
    class IRRestorer extends Restorer {

        private final int n;

        public IRRestorer() {
            n = inequalityRelation.size();
        }

        @Override
        public void restore() {
            resize(inequalityRelation, n, null);
            inequalityRelationHelper.clear();
            // TODO check performances of this
            inequalityRelation.stream().filter(p -> p != null).forEach(p -> inequalityRelationHelper.put(p.getConcept(),
                p));
        }
    }

    /** label of a node */
    private final CGLabel label;
    // TODO check for better access
    /** inequality relation information respecting current node */
    protected final List<ConceptWDep> inequalityRelation = new ArrayList<>();
    protected final TIntObjectHashMap<ConceptWDep> inequalityRelationHelper = new TIntObjectHashMap<>();
    // TODO check whether access should be improved
    /** Neighbours information */
    private final List<DlCompletionTreeArc> neighbour = new ArrayList<>();
    /** pointer to last saved node */
    private final SaveList saves = new SaveList();
    /** ID of node (used in print) */
    private final int id;
    /** concept that init the newly created node */
    private int init;
    /** blocker of a node */
    protected DlCompletionTree blocker;
    /** dep-set for Purge op */
    protected DepSet pDep = DepSet.create();
    // save state information
    protected int curLevel;
    /** is given node a data node */
    private boolean flagDataNode = false;
    /** flag if node is Cached */
    protected boolean cached = true;
    /** flag whether node is permanently/temporarily blocked */
    protected boolean pBlocked = true;
    /** flag whether node is directly/indirectly blocked */
    protected boolean dBlocked = true;
    /**
     * Whether node is affected by change of some potential blocker. This flag
     * may be viewed as a cache for a 'blocked' status
     */
    private boolean affected = true;
    /** level of a nominal node; 0 means blockable one */
    private int nominalLevel;
    @Original private final JFactReasonerConfiguration options;
    /** default level for the Blockable node */
    public static final int BLOCKABLE_LEVEL = Integer.MAX_VALUE;

    /**
     * @param newId
     *        newId
     * @param c
     *        c
     */
    public DlCompletionTree(int newId, JFactReasonerConfiguration c) {
        id = newId;
        options = c;
        label = new CGLabel(c);
    }

    /**
     * check if B2 holds for given DL vertex with C=V
     * 
     * @param v
     *        v
     * @param c
     *        C
     * @return true if b2 holds
     */
    @PortedFrom(file = "dlCompletionTree.h", name = "B2")
    private boolean b2(DLVertex v, int c) {
        assert hasParent();// safety
        RAStateTransitions rst = v.getRole().getAutomaton().get(v.getState());
        if (v.getRole().isSimple()) {
            return b2Simple(rst, v.getConceptIndex());
        } else {
            if (rst.empty()) {
                // no possible applications
                return true;
            }
            // pointer to current forall
            int bp = c - v.getState();
            if (rst.isSingleton()) {
                return b2Simple(rst, bp + rst.getTransitionEnd());
            }
            return b2Complex(rst, bp);
        }
    }

    /**
     * @param c
     *        C
     * @return check whether a node can block another one with init concept C
     */
    public boolean canBlockInit(int c) {
        if (c == BP_BOTTOM) {
            return false;
        }
        if (c == BP_TOP) {
            return true;
        }
        return label.contains(c);
    }

    /**
     * log saving/restoring node
     * 
     * @param action
     *        action
     */
    private void logSRNode(String action) {
        options.getLog().printTemplateMixInt(Templates.LOG_SR_NODE, action, id, neighbour.size(), curLevel);
    }

    /** @return letter corresponding to the blocking mode */
    private String getBlockingStatusName() {
        return isPBlocked() ? "p" : isDBlocked() ? "d" : isIBlocked() ? "i" : "u";
    }

    /** @return log node status (d-,i-,p-blocked or cached) */
    private String logNodeBStatus() {
        StringBuilder toReturn = new StringBuilder();
        // blocking status information
        if (blocker != null) {
            toReturn.append(getBlockingStatusName()).append(blocker.id);
        }
        if (isCached()) {
            toReturn.append('c');
        }
        return toReturn.toString();
    }

    /**
     * add given arc P as a neighbour
     * 
     * @param p
     *        p
     */
    public void addNeighbour(DlCompletionTreeArc p) {
        neighbour.add(p);
    }

    /** @return Node's id */
    public int getId() {
        return id;
    }

    /** @return check if the node is cached (IE need not to be expanded) */
    public boolean isCached() {
        return cached;
    }

    /**
     * set cached status of given node
     * 
     * @param val
     *        val
     * @return restorer
     */
    @Nullable
    public Restorer setCached(boolean val) {
        if (cached == val) {
            return null;
        }
        Restorer ret = new CacheRestorer(this);
        cached = val;
        return ret;
    }

    // data node methods
    /** @return true if datanode */
    public boolean isDataNode() {
        return flagDataNode;
    }

    /** set data node to true */
    public void setDataNode() {
        flagDataNode = true;
    }

    // nominal node methods
    /** @return true if blockable */
    public boolean isBlockableNode() {
        return nominalLevel == BLOCKABLE_LEVEL;
    }

    /** @return true if nominal */
    public boolean isNominalNode() {
        return nominalLevel != BLOCKABLE_LEVEL;
    }

    /** set nominal level to 0 */
    public void setNominalLevel() {
        setNominalLevel(0);
    }

    /**
     * @param newLevel
     *        newLevel
     */
    public void setNominalLevel(int newLevel) {
        nominalLevel = newLevel;
    }

    /** @return nominal level */
    public int getNominalLevel() {
        return nominalLevel;
    }

    /**
     * adds concept P to a label, defined by TAG; update blocked status if
     * necessary
     * 
     * @param p
     *        p
     * @param complex
     *        true if complex concepts sought
     */
    public void addConcept(ConceptWDep p, boolean complex) {
        label.add(complex, p);
    }

    /**
     * set the Init concept
     * 
     * @param p
     *        p
     */
    public void setInit(int p) {
        init = p;
    }

    /** @return init value */
    public int getInit() {
        return init;
    }

    /** @return neighbour list */
    public List<DlCompletionTreeArc> getNeighbour() {
        return neighbour;
    }

    /** @return true if node is a non-root; works for reflexive roles */
    public boolean hasParent() {
        if (neighbour.isEmpty()) {
            return false;
        }
        return neighbour.get(0).isPredEdge();
    }

    /**
     * check if SOME rule is applicable; includes transitive SOME support
     * 
     * @param r
     *        R
     * @param c
     *        C
     * @return completion tree
     */
    @Nullable
    public DlCompletionTree isSomeApplicable(Role r, int c) {
        return r.isTransitive() ? isTSomeApplicable(r, c) : isNSomeApplicable(r, c);
    }

    /** @return label */
    public CGLabel label() {
        return label;
    }

    // label iterators
    /** @return simple concepts */
    public List<ConceptWDep> simpleConcepts() {
        return label.getSimpleConcepts();
    }

    /** @return complex concepts */
    public List<ConceptWDep> complexConcepts() {
        return label.getComplexConcepts();
    }

    /** @return map for label with simple concepts */
    public ArrayIntMap simpleConceptsMap() {
        return label.getSimpleConceptsMap();
    }

    /** @return map for a label with complex concepts */
    public ArrayIntMap complexConceptsMap() {
        return label.getComplexConceptsMap();
    }

    /**
     * @param p
     *        p
     * @return check whether node's label contains P
     */
    public boolean isLabelledBy(int p) {
        return label.contains(p);
    }

    // Blocked-By methods for different logics
    /**
     * @param p
     *        p
     * @return check blocking condition for SH logic
     */
    public boolean isBlockedBySH(DlCompletionTree p) {
        return label.lesserequal(p.label);
    }

    /**
     * @param dag
     *        dag
     * @param p
     *        p
     * @return check blocking condition for SHI logic
     */
    public boolean isBlockedBySHI(DLDag dag, DlCompletionTree p) {
        return isCommonlyBlockedBy(dag, p);
    }

    /**
     * @param dag
     *        dag
     * @param p
     *        p
     * @return check blocking condition for SHIQ logic using optimised blocking
     */
    public boolean isBlockedBySHIQ(DLDag dag, DlCompletionTree p) {
        return isCommonlyBlockedBy(dag, p) && (isCBlockedBy(dag, p) || isABlockedBy(dag, p));
    }

    // WARNING!! works only for blockable nodes
    // every non-root node will have first upcoming edge pointed to a parent
    /**
     * @return RW pointer to the parent node; WARNING: correct only for nodes
     *         with hasParent()==TRUE
     */
    public DlCompletionTree getParentNode() {
        return neighbour.get(0).getArcEnd();
    }

    // managing AFFECTED flag
    /** @return check whether node is affected by blocking-related changes */
    public boolean isAffected() {
        return affected;
    }

    /** set node (and all subnodes) affected */
    public void setAffected() {
        // don't mark already affected, nominal or p-blocked nodes
        if (isAffected() || isNominalNode() || isPBlocked()) {
            return;
        }
        affected = true;
        neighbour.stream().filter(q -> q.isSuccEdge()).forEach(q -> q.getArcEnd().setAffected());
    }

    /** clear affected flag */
    public void clearAffected() {
        affected = false;
    }

    // just returns calculated values
    /** @return check if node is directly blocked */
    public boolean isDBlocked() {
        return blocker != null && !pBlocked && dBlocked;
    }

    /** @return check if node is indirectly blocked */
    public boolean isIBlocked() {
        return blocker != null && !pBlocked && !dBlocked;
    }

    /** @return check if node is purged (and so indirectly blocked) */
    public boolean isPBlocked() {
        return blocker != null && pBlocked && !dBlocked;
    }

    /** @return true if node is blocked */
    public boolean isBlockedPBlockedNominalNodeCached() {
        return cached || isNominalNode() || isBlocked() || isPBlocked();
    }

    /** @return check if node is blocked (d/i) */
    public boolean isBlocked() {
        return blocker != null && !pBlocked;
    }

    /** @return check the legality of the direct block */
    public boolean isIllegallyDBlocked() {
        return isDBlocked() && blocker.isBlocked();
    }

    /** @return blocker */
    public DlCompletionTree getBlocker() {
        return blocker;
    }

    /** @return get purge dep-set of a given node */
    public DepSet getPurgeDep() {
        return pDep;
    }

    /** @return get node to which current one was merged */
    public DlCompletionTree resolvePBlocker() {
        if (isPBlocked()) {
            return blocker.resolvePBlocker();
        } else {
            return this;
        }
    }

    /**
     * @param dep
     *        dep
     * @return get node to which current one was merged; fills DEP from pDep's
     */
    public DlCompletionTree resolvePBlocker(DepSet dep) {
        if (!isPBlocked()) {
            return this;
        }
        dep.add(pDep);
        return blocker.resolvePBlocker(dep);
    }

    /**
     * @param c
     *        c
     * @return check whether the loop between a DBlocked NODE and it's parent
     *         blocked contains C
     */
    public boolean isLoopLabelled(int c) {
        assert isDBlocked();
        if (blocker.isLabelledBy(c)) {
            return true;
        }
        // Blocker is the 1st node in the loop
        int n = 1;
        for (DlCompletionTree p = getParentNode(); p.hasParent() && !p.equals(blocker); p = p.getParentNode()) {
            if (p.isLabelledBy(c)) {
                return true;
            } else {
                ++n;
            }
        }
        options.getLog().print(" loop(").print(n).print(")");
        return false;
    }

    // re-building blocking hierarchy
    /**
     * set node blocked
     * 
     * @param blocker
     *        blocker
     * @param permanently
     *        permanently
     * @param directly
     *        directly
     * @return restorer
     */
    private Restorer setBlocked(@Nullable DlCompletionTree blocker, boolean permanently, boolean directly) {
        Restorer ret = new UnBlock(this);
        setBlocker(blocker);
        pBlocked = permanently;
        dBlocked = directly;
        options.getLog().printTemplate(Templates.LOG_NODE_BLOCKED, getBlockingStatusName(), Integer.toString(id),
            blocker == null ? "" : ",", blocker == null ? "" : Integer.toString(blocker.id));
        return ret;
    }

    /**
     * mark node d-blocked
     * 
     * @param blocker
     *        blocker
     * @return restorer
     */
    public Restorer setDBlocked(DlCompletionTree blocker) {
        return setBlocked(blocker, false, true);
    }

    /**
     * mark node i-blocked
     * 
     * @param blocker
     *        blocker
     * @return restorer
     */
    public Restorer setIBlocked(DlCompletionTree blocker) {
        return setBlocked(blocker, false, false);
    }

    /**
     * mark node unblocked
     * 
     * @return restorer
     */
    public Restorer setUBlocked() {
        return setBlocked(null, true, true);
    }

    /**
     * mark node purged
     * 
     * @param blocker
     *        blocker
     * @param dep
     *        dep
     * @return restorer
     */
    public Restorer setPBlocked(@Nullable DlCompletionTree blocker, DepSet dep) {
        Restorer ret = new UnBlock(this);
        setBlocker(blocker);
        if (isNominalNode()) {
            pDep = DepSet.create(dep);
        }
        pBlocked = true;
        dBlocked = false;
        options.getLog().printTemplate(Templates.LOG_NODE_BLOCKED, getBlockingStatusName(), Integer.toString(id),
            blocker == null ? "" : ",", blocker == null ? "" : Integer.toString(blocker.id));
        return ret;
    }

    // checking edge labelling
    /**
     * check if edge to NODE is labelled by R;
     * 
     * @param r
     *        R
     * @param node
     *        node
     * @return null if does not
     */
    @Nullable
    public DlCompletionTreeArc getEdgeLabelled(Role r, DlCompletionTree node) {
        return neighbour.stream().filter(p -> p.getArcEnd().equals(node) && p.isNeighbour(r)).findAny().orElse(null);
    }

    /**
     * check if parent arc is labelled by R; works only for blockable nodes
     * 
     * @param r
     *        R
     * @return true if parent labelled
     */
    private boolean isParentArcLabelled(Role r) {
        return getEdgeLabelled(r, getParentNode()) != null;
    }

    // inequality relation interface
    /**
     * init IR with given entry and dep-set;
     * 
     * @param level
     *        level
     * @param ds
     *        ds
     * @return true if IR already has this label
     */
    @PortedFrom(file = "dlCompletionTree.cpp", name = "initIR")
    public boolean initIR(int level, DepSet ds) {
        Reference<DepSet> dummy = new Reference<>(DepSet.create());
        // we don't need a clash-set here
        if (inIRwithC(level, ds, dummy)) {
            return true;
        }
        ConceptWDep conceptWDep = new ConceptWDep(level, ds);
        inequalityRelation.add(conceptWDep);
        inequalityRelationHelper.put(level, conceptWDep);
        return false;
    }

    /**
     * check if IR for the node contains C
     * 
     * @param level
     *        level
     * @param ds
     *        ds
     * @param dep
     *        dep
     * @return true if C contained
     */
    @PortedFrom(file = "dlCompletionTree.cpp", name = "inIRwithC")
    private boolean inIRwithC(int level, DepSet ds, Reference<DepSet> dep) {
        if (inequalityRelation.isEmpty()) {
            return false;
        }
        ConceptWDep p = inequalityRelationHelper.get(level);
        if (p != null) {
            dep.getReference().add(p.getDep());
            dep.getReference().add(ds);
            return true;
        }
        return false;
    }

    // saving/restoring
    /**
     * @param newLevel
     *        newLevel
     * @return check if node needs to be saved
     */
    public boolean needSave(int newLevel) {
        return curLevel < newLevel;
    }

    /**
     * save node using internal stack
     * 
     * @param level
     *        level
     */
    public void save(int level) {
        DLCompletionTreeSaveState node = new DLCompletionTreeSaveState();
        saves.push(node);
        save(node);
        curLevel = level;
    }

    /**
     * @param restLevel
     *        restLevel
     * @return check if node needs to be restored
     */
    public boolean needRestore(int restLevel) {
        return curLevel > restLevel;
    }

    /**
     * @param level
     *        level number restore node to given level
     */
    public void restore(int level) {
        restore(saves.pop(level));
    }

    // output
    /** @return log node information (number, i/d blockers, cached) */
    public String logNode() {
        return id + logNodeBStatus();
    }

    private boolean isCommonlyBlockedBy(DLDag dag, DlCompletionTree p) {
        assert hasParent();
        if (!label.lesserequal(p.label)) {
            return false;
        }
        ArrayIntMap list = p.complexConceptsMap();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            int bp = list.keySet(i);
            if (bp > 0) {
                DLVertex v = dag.get(bp);
                if (v.getType() == FORALL && !b2(v, bp)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isABlockedBy(DLDag dag, DlCompletionTree p) {
        // current = w; p = w'; parent = v
        // there exists v
        assert hasParent();
        // B3,B4
        ArrayIntMap list = p.complexConceptsMap();
        for (int i = 0; i < list.size(); i++) {
            int bp = list.keySet(i);
            DLVertex v = dag.get(bp);
            if (v.getType() == FORALL && bp < 0) {
                // (some T E) \in L(w')
                if (!b4(1, v.getRole(), -v.getConceptIndex())) {
                    return false;
                }
            } else if (v.getType() == LE) {
                if (bp > 0) {
                    // (<= n S C) \in L(w')
                    if (!b3(p, v.getNumberLE(), v.getRole(), v.getConceptIndex())) {
                        return false;
                    }
                } else {
                    // (>= m T E) \in L(w')
                    if (!b4(v.getNumberGE(), v.getRole(), v.getConceptIndex())) {
                        return false;
                    }
                }
            }
        }
        // all other is OK -- done
        return true;
    }

    private boolean isCBlockedBy(DLDag dag, DlCompletionTree t) {
        Stream<ConceptWDep> list = t.complexConcepts().stream();
        if (list.anyMatch(p -> {
            if (p.getConcept() > 0) {
                DLVertex v = dag.get(p.getConcept());
                // (<= n T E) \in L(w')
                return v.getType() == LE && !b5(v.getRole(), v.getConceptIndex());
            }
            return false;
        })) {
            return false;
        }
        list = getParentNode().complexConcepts().stream();
        if (list.anyMatch(p -> {
            if (p.getConcept() < 0) {
                DLVertex v = dag.get(p.getConcept());
                // (<= n T E) \in L(w')
                return v.getType() == LE && !b6(v.getRole(), v.getConceptIndex());
            }
            return false;
        })) {
            return false;
        }
        return true;
    }

    @PortedFrom(file = "Blocking.cpp", name = "B2Simple")
    private boolean b2Simple(RAStateTransitions rst, int c) {
        DlCompletionTree parent = getParentNode();
        if (neighbour.stream().anyMatch(p -> recognise(rst, parent, p))) {
            return parent.label().contains(c);
        }
        return true;
    }

    @PortedFrom(file = "Blocking.cpp", name = "B2Complex")
    private boolean b2Complex(RAStateTransitions rst, int c) {
        DlCompletionTree parent = getParentNode();
        CGLabel parLab = parent.label();
        return !neighbour.stream().filter(p -> recognise(rst, parent, p)).anyMatch(p -> rst.stream().anyMatch(q -> q
            .applicable(p.getRole()) && !parLab.containsCC(c + q.finalState())));
    }

    protected boolean recognise(RAStateTransitions rst, DlCompletionTree parent, DlCompletionTreeArc p) {
        return !p.isIBlocked() && p.getArcEnd().equals(parent) && rst.recognise(p.getRole());
    }

    /**
     * check if B3 holds for (<= n S.C)\in w' (p is a candidate for blocker)
     * 
     * @param p
     *        completion tree
     * @param n
     *        level
     * @param t
     *        role
     * @param c
     *        filler
     * @return true if candidate
     */
    @PortedFrom(file = "Blocking.cpp", name = "B3")
    private boolean b3(DlCompletionTree p, int n, Role t, int c) {
        assert hasParent();
        // XXX here FaCT++ has blocking stats, are they useful?
        boolean ret;
        // when (<= n S C) \in L(w') then
        // a)w is an inv(S)-succ of v or
        if (!isParentArcLabelled(t)) {
            ret = true;
        } else if (getParentNode().isLabelledBy(-c)) {
            // b)w is an inv(S)succ of v and ~C\in L(v)or
            ret = true;
        } else if (!getParentNode().isLabelledBy(c)) {
            // c)w is an inv(S)succ of v and C\in L(v)...
            ret = false;
        } else {
            // ...and <=n-1 S-succ. z with C\in L(z)
            long m = p.neighbour.stream().filter(q -> q.isSuccEdge() && q.isNeighbour(t) && q.getArcEnd().isLabelledBy(
                c)).count();
            ret = m < n;
        }
        return ret;
    }

    /**
     * check if B4 holds for(>= m T.E)\in w'(p is a candidate for blocker)
     * 
     * @param m
     *        level
     * @param t
     *        role
     * @param e
     *        filler
     * @return true if candidate
     */
    @PortedFrom(file = "Blocking.cpp", name = "B4")
    private boolean b4(int m, Role t, int e) {
        assert hasParent();
        // when (>= m T E) \in L(w') then
        // b)w is an inv(T)succ of v and E\in L(v)and m == 1 or
        if (isParentArcLabelled(t) && m == 1 && getParentNode().isLabelledBy(e)) {
            return true;
        }
        // a)w' has at least m T-succ z with E\in L(z)
        // check all sons
        AtomicInteger n = new AtomicInteger(0);
        return neighbour.stream().anyMatch(q ->
        // check if node has enough successors
        q.isSuccEdge() && q.isNeighbour(t) && q.getArcEnd().isLabelledBy(e) && n.incrementAndGet() >= m);
    }

    /**
     * check if B5 holds for(<= n T.E)\in w'
     * 
     * @param t
     *        role
     * @param e
     *        filler
     * @return true if candidate
     */
    @PortedFrom(file = "Blocking.cpp", name = "B5")
    private boolean b5(Role t, int e) {
        assert hasParent();
        // when (<= n T E) \in L(w'), then
        // either w is not an inv(T)-successor of v...
        if (!isParentArcLabelled(t)) {
            return true;
        }
        // or ~E \in L(v)
        if (getParentNode().isLabelledBy(-e)) {
            return true;
        }
        return false;
    }

    /**
     * check if B6 holds for (>= m U.F)\in v
     * 
     * @param u
     *        role
     * @param f
     *        filler
     * @return true if candidate
     */
    @PortedFrom(file = "Blocking.cpp", name = "B6")
    private boolean b6(Role u, int f) {
        assert hasParent();
        // if >= m U F in L(v), and w is U-successor of v...
        if (!isParentArcLabelled(u.inverse())) {
            return true;
        }
        // then ~F\in L(w)
        if (isLabelledBy(-f)) {
            return true;
        }
        return false;
    }

    /**
     * @param level
     *        level
     */
    public void init(int level) {
        flagDataNode = false;
        nominalLevel = BLOCKABLE_LEVEL;
        curLevel = level;
        cached = false;
        // every (newly created) node can be blocked
        affected = true;
        dBlocked = true;
        // unused flag combination
        pBlocked = true;
        // cleans the cache where Label is involved
        label.init();
        init = BP_TOP;
        // node was used -- clear all previous content
        saves.clear();
        inequalityRelation.clear();
        inequalityRelationHelper.clear();
        neighbour.clear();
        setBlocker(null);
        pDep.clear();
    }

    @Nullable
    private DlCompletionTree isTSuccLabelled(Role r, int c) {
        if (isLabelledBy(c)) {
            return this;
        }
        // don't check nominal nodes (prevent cycles)
        if (isNominalNode()) {
            return null;
        }
        // check all other successors
        for (DlCompletionTreeArc p : neighbour) {
            if (p.isSuccEdge() && p.isNeighbour(r) && !p.isReflexiveEdge()) {
                DlCompletionTree ret = p.getArcEnd().isTSuccLabelled(r, c);
                if (ret != null) {
                    return ret;
                }
            }
        }
        return null;
    }

    @Nullable
    private DlCompletionTree isTPredLabelled(Role r, int c, DlCompletionTree from) {
        if (isLabelledBy(c)) {
            return this;
        }
        // don't check nominal nodes (prevent cycles)
        if (isNominalNode()) {
            return null;
        }
        // check all other successors
        for (DlCompletionTreeArc p : neighbour) {
            if (p.isSuccEdge() && p.isNeighbour(r) && !p.getArcEnd().equals(from)) {
                DlCompletionTree ret = p.getArcEnd().isTSuccLabelled(r, c);
                if (ret != null) {
                    return ret;
                }
            }
        }
        // check predecessor
        if (hasParent() && isParentArcLabelled(r)) {
            return getParentNode().isTPredLabelled(r, c, this);
        }
        return null;
    }

    @Nullable
    private DlCompletionTree isNSomeApplicable(Role r, int c) {
        Optional<DlCompletionTreeArc> findAny = neighbour.stream().filter(p -> p.isNeighbour(r) && p.getArcEnd()
            .isLabelledBy(c)).findAny();
        if (findAny.isPresent()) {
            return findAny.get().getArcEnd();
        }
        return null;
    }

    @Nullable
    private DlCompletionTree isTSomeApplicable(Role r, int c) {
        for (DlCompletionTreeArc p : neighbour) {
            if (p.isNeighbour(r)) {
                DlCompletionTree ret;
                if (p.isPredEdge()) {
                    ret = p.getArcEnd().isTPredLabelled(r, c, this);
                } else {
                    ret = p.getArcEnd().isTSuccLabelled(r, c);
                }
                if (ret != null) {
                    // already contained such a label
                    return ret;
                }
            }
        }
        return null;
    }

    /**
     * saving/restoring
     * 
     * @param nss
     *        save state
     */
    private void save(DLCompletionTreeSaveState nss) {
        nss.setCurLevel(curLevel);
        nss.setnNeighbours(neighbour.size());
        label.save(nss.getLab());
        logSRNode("SaveNode");
    }

    private void restore(@Nullable DLCompletionTreeSaveState nss) {
        if (nss == null) {
            return;
        }
        // level restore
        curLevel = nss.getCurLevel();
        // label restore
        label.restore(nss.getLab(), curLevel);
        // remove new neighbours
        if (!options.isUseDynamicBackjumping()) {
            resize(neighbour, nss.getnNeighbours(), null);
        } else {
            for (int j = neighbour.size() - 1; j >= 0; --j) {
                if (neighbour.get(j).getArcEnd().curLevel <= curLevel) {
                    Helper.resize(neighbour, j + 1, null);
                    break;
                }
            }
        }
        // it's cheaper to dirty affected flag than to consistently save nodes
        affected = true;
        logSRNode("RestNode");
    }

    /**
     * @param o
     *        o
     */
    public void printBody(LogAdapter o) {
        o.print(id);
        if (isNominalNode()) {
            o.print("o").print(nominalLevel);
        }
        o.print("(").print(curLevel).print(")");
        if (isDataNode()) {
            o.print("d");
        }
        o.print(label).print(logNodeBStatus());
    }

    @Override
    public String toString() {
        StringBuilder o = new StringBuilder();
        o.append(id);
        if (isNominalNode()) {
            o.append('o').append(nominalLevel);
        }
        o.append('(').append(curLevel).append(')');
        if (isDataNode()) {
            o.append('d');
        }
        o.append(label).append(logNodeBStatus());
        return o.toString();
    }

    /**
     * check if the NODE's and current node's IR are labelled with the same
     * level
     * 
     * @param node
     *        node to check
     * @param dep
     *        depset reference
     * @return true if mergeable
     */
    @PortedFrom(file = "dlCompletionTree.cpp", name = "nonMergable")
    public boolean nonMergable(DlCompletionTree node, Reference<DepSet> dep) {
        if (inequalityRelation.isEmpty() || node.inequalityRelation.isEmpty()) {
            return false;
        }
        for (ConceptWDep p : node.inequalityRelation) {
            if (inIRwithC(p.getConcept(), p.getDep(), dep)) {
                return true;
            }
        }
        return false;
    }

    /**
     * update IR of the current node with IR from NODE and additional clash-set;
     * 
     * @param node
     *        completion tree
     * @param toAdd
     *        depset
     * @return restorer
     */
    @Nullable
    @PortedFrom(file = "dlCompletionTree.cpp", name = "updateIR")
    public Restorer updateIR(DlCompletionTree node, DepSet toAdd) {
        if (node.inequalityRelation.isEmpty()) {
            return null;    // nothing to do
        }
        // save current state
        Restorer ret = new IRRestorer();
        // copy all elements from NODE's IR to current node.
        // FIXME!! do not check if some of them are already in there
        for (ConceptWDep p : node.inequalityRelation) {
            // not adding those already there, they would be ignored anyway
            if (!inequalityRelationHelper.containsKey(p.getConcept())) {
                ConceptWDep conceptWDep = new ConceptWDep(p.getConcept(), toAdd);
                inequalityRelation.add(conceptWDep);
                inequalityRelationHelper.put(p.getConcept(), conceptWDep);
            }
        }
        return ret;
    }

    @Override
    public int compareTo(@Nullable DlCompletionTree o) {
        if (nominalLevel == o.nominalLevel) {
            return id - o.id;
        }
        return nominalLevel - o.nominalLevel;
    }

    @Override
    public boolean equals(@Nullable Object arg0) {
        if (arg0 == null) {
            return false;
        }
        if (this == arg0) {
            return true;
        }
        if (arg0 instanceof DlCompletionTree) {
            DlCompletionTree arg02 = (DlCompletionTree) arg0;
            return nominalLevel == arg02.nominalLevel && id == arg02.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return nominalLevel * id;
    }

    /**
     * @param blocker
     *        blocker
     */
    public void setBlocker(@Nullable DlCompletionTree blocker) {
        this.blocker = blocker;
    }
}
