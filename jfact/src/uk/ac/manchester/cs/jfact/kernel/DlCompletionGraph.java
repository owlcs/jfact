package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.*;
import uk.ac.manchester.cs.jfact.kernel.state.DLCompletionGraphSaveState;

public class DlCompletionGraph {
    /** initial value of IR level */
    private static final int initIRLevel = 0;
    // XXX is this actually used?
    /** allocator for edges */
    private List<DlCompletionTreeArc> ctEdgeHeap = new ArrayList<DlCompletionTreeArc>();
    /** heap itself */
    private List<DlCompletionTree> nodeBase;
    /** nodes, saved on current branching level */
    private List<DlCompletionTree> savedNodes = new ArrayList<DlCompletionTree>();
    /** host reasoner */
    private DlSatTester pReasoner;
    /** remember the last generated ID for the node */
    private int nodeId = 0;
    /** index of the next unallocated entry */
    private int endUsed;
    /** current branching level (synchronised with resoner's one) */
    private int branchingLevel;
    /** current IR level (should be valid BP) */
    private int irLevel;
    /** stack for rarely changed information */
    private SaveStackRare rareStack = new SaveStackRare();
    /** stack for usual saving/restoring */
    private SaveStack<DLCompletionGraphSaveState> stack = new SaveStack<DLCompletionGraphSaveState>();
    // helpers for the output
    /** bitmap to remember which node was printed */
    // TODO change to regular
    private FastSet cgpFlag = FastSetFactory.create();
    /** indent to print CGraph nodes */
    private int cgpIndent;
    // statistical members
    /** number of node' saves */
    private int nNodeSaves;
    /** number of node' saves */
    private int nNodeRestores;
    // / maximal size of the graph
    int maxGraphSize = 0;
    // flags
    /** how many nodes skip before block; work only with FAIRNESS */
    private int nSkipBeforeBlock = 0;
    /** use or not lazy blocking (ie test blocking only expanding exists) */
    private boolean useLazyBlocking;
    /** whether to use Anywhere blocking as opposed to an ancestor one */
    private boolean useAnywhereBlocking;
    /** check if session has inverse roles */
    private boolean sessionHasInverseRoles;
    /** check if session has number restrictions */
    private boolean sessionHasNumberRestrictions;

    /** init vector [B,E) with new objects T */
    private void initNodeArray(List<DlCompletionTree> l, int b, int e) {
        for (int p = b; p < e; ++p) {
            l.set(p, new DlCompletionTree(nodeId++, pReasoner.getOptions()));
        }
    }

    /** increase heap size */
    private void grow() {
        int size = nodeBase.size();
        Helper.resize(nodeBase, size * 2);
        initNodeArray(nodeBase, size, nodeBase.size());
    }

    /** init root node */
    private void initRoot() {
        assert endUsed == 0;
        getNewNode();
    }

    /** invalidate EDGE, save restoring info */
    private void invalidateEdge(DlCompletionTreeArc edge) {
        saveRareCond(edge.save());
    }

    /** check if d-blocked node is still d-blocked */
    private boolean isStillDBlocked(DlCompletionTree node) {
        return node.isDBlocked() && isBlockedBy(node, node.blocker);
    }

    /** try to find d-blocker for a node */
    private void findDBlocker(DlCompletionTree node) {
        saveNode(node, branchingLevel);
        node.clearAffected();
        if (node.isBlocked()) {
            saveRareCond(node.setUBlocked());
        }
        if (useAnywhereBlocking) {
            findDAnywhereBlocker(node);
        } else {
            findDAncestorBlocker(node);
        }
    }

    /** unblock all the children of the node */
    private void unblockNodeChildren(DlCompletionTree node) {
        List<DlCompletionTreeArc> neighbour = node.getNeighbour();
        int size = neighbour.size();
        for (int i = 0; i < size; i++) {
            DlCompletionTreeArc q = neighbour.get(i);
            if (q.isSuccEdge() && !q.isIBlocked() && !q.isReflexiveEdge()) {
                unblockNode(q.getArcEnd(), false);
            }
        }
    }

    /** mark NODE as a d-blocked by a BLOCKER */
    private void setNodeDBlocked(DlCompletionTree node, DlCompletionTree blocker) {
        saveRareCond(node.setDBlocked(blocker));
        propagateIBlockedStatus(node, node);
    }

    /** mark NODE as an i-blocked by a BLOCKER */
    private void setNodeIBlocked(DlCompletionTree node, DlCompletionTree blocker) {
        // nominal nodes can't be blocked
        if (node.isPBlocked() || node.isNominalNode()) {
            return;
        }
        node.clearAffected();
        // already iBlocked -- nothing changes
        if (node.isIBlocked() && node.blocker.equals(blocker)) {
            return;
        }
        // prevent node to be IBlocked due to reflexivity
        if (node.equals(blocker)) {
            return;
        }
        saveRareCond(node.setIBlocked(blocker));
        propagateIBlockedStatus(node, blocker);
    }

    /** propagate i-blocked status to all children of NODE */
    private void propagateIBlockedStatus(DlCompletionTree node, DlCompletionTree blocker) {
        List<DlCompletionTreeArc> neighbour = node.getNeighbour();
        int size = neighbour.size();
        for (int i = 0; i < size; i++) {
            DlCompletionTreeArc q = neighbour.get(i);
            if (q.isSuccEdge() && !q.isIBlocked()) {
                setNodeIBlocked(q.getArcEnd(), blocker);
            }
        }
    }

    /** @return true iff node might became unblocked */
    private boolean canBeUnBlocked(DlCompletionTree node) {
        // in presence of inverse roles it is not enough
        // to check the affected flag for both node and its blocker
        // see tModal* for example
        if (sessionHasInverseRoles) {
            return true;
        }
        // if node is affected -- it can be unblocked;
        // if blocker became blocked itself -- the same
        return node.isAffected() || node.isIllegallyDBlocked();
    }

    /** print proper indentation */
    private void printIndent(LogAdapter o) {
        o.print("\n|");
        for (int i = 1; i < cgpIndent; ++i) {
            o.print(" |");
        }
    }

    /** c'tor: make INIT_SIZE objects */
    public DlCompletionGraph(int initSize, DlSatTester p) {
        nodeBase = new ArrayList<DlCompletionTree>();
        Helper.resize(nodeBase, initSize);
        pReasoner = p;
        nodeId = 0;
        endUsed = 0;
        branchingLevel = InitBranchingLevelValue;
        irLevel = initIRLevel;
        initNodeArray(nodeBase, 0, nodeBase.size());
        clearStatistics();
        initRoot();
    }

    // flag setting
    /** set flags for blocking */
    public void initContext(int nSkip, boolean useLB, boolean useAB) {
        nSkipBeforeBlock = nSkip;
        useLazyBlocking = useLB;
        useAnywhereBlocking = useAB;
    }

    /** set blocking method for a session */
    public void setBlockingMethod(boolean hasInverse, boolean hasQCR) {
        sessionHasInverseRoles = hasInverse;
        sessionHasNumberRestrictions = hasQCR;
    }

    /** add concept C of a type TAG to NODE; call blocking check if appropriate */
    public void addConceptToNode(DlCompletionTree node, ConceptWDep c, DagTag tag) {
        node.addConcept(c, tag);
        if (useLazyBlocking) {
            node.setAffected();
        } else {
            detectBlockedStatus(node);
        }
    }

    // access to nodes
    /** get a root node (non-const) */
    public DlCompletionTree getRoot() {
        return nodeBase.get(0).resolvePBlocker();
    }

    public DlCompletionTree getNode(int i) {
        if (i >= endUsed) {
            return null;
        }
        return nodeBase.get(i);
    }

    /** get new node (with internal level) */
    public DlCompletionTree getNewNode() {
        if (endUsed >= nodeBase.size()) {
            grow();
        }
        DlCompletionTree ret = nodeBase.get(endUsed++);
        ret.init(branchingLevel);
        return ret;
    }

    // blocking
    /** update blocked status for d-blocked node */
    public void updateDBlockedStatus(DlCompletionTree node) {
        if (!canBeUnBlocked(node)) {
            return;
        }
        if (isStillDBlocked(node)) {
            // FIXME!! clear affected in all children
            node.clearAffected();
        } else {
            detectBlockedStatus(node);
        }
        assert !node.isAffected();
    }

    /** retest every d-blocked node in the CG. Use it after the CG was build */
    public void retestCGBlockedStatus() {
        boolean repeat = false;
        do {
            for (int i = 0; i < endUsed; i++) {
                DlCompletionTree p = nodeBase.get(i);
                if (p.isDBlocked()) {
                    updateDBlockedStatus(p);
                }
            }
            // we need to repeat the thing if something became unblocked and
            // then blocked again,
            // in case one of the blockers became blocked itself; see tModal3
            // for such an example
            repeat = false;
            for (int i = 0; i < endUsed; i++) {
                DlCompletionTree p = nodeBase.get(i);
                if (p.isIllegallyDBlocked()) {
                    repeat = true;
                    break;
                }
            }
        } while (repeat);
    }

    /** @return true if a fairness constraint C is violated in one of the */
    // loops in the CGraph
    DlCompletionTree getFCViolator(int C) {
        for (DlCompletionTree p : nodeBase) {
            if (p.isDBlocked() && !p.isLoopLabelled(C)) {
                return p.blocker;
            }
        }
        return null;
    }

    /** clear all the session statistics */
    public void clearStatistics() {
        nNodeSaves = 0;
        nNodeRestores = 0;
        if (maxGraphSize < endUsed) {
            maxGraphSize = endUsed;
        }
    }

    /** get number of nodes in the CGraph */
    public int maxSize() {
        return maxGraphSize;
    }

    /** mark all heap elements as unused */
    public void clear() {
        ctEdgeHeap.clear();
        endUsed = 0;
        branchingLevel = InitBranchingLevelValue;
        irLevel = initIRLevel;
        rareStack.clear();
        stack.clear();
        savedNodes.clear();
        initRoot();
    }

    /** save rarely appeared info if P is non-null */
    public void saveRareCond(Restorer p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        rareStack.push(p);
    }

    public void saveRareCond(List<Restorer> p) {
        for (int i = 0; i < p.size(); i++) {
            rareStack.push(p.get(i));
        }
    }

    // role/node
    /** add role R with dep-set DEP to the label of the TO arc */
    public DlCompletionTreeArc addRoleLabel(DlCompletionTree from, DlCompletionTree to,
            boolean isPredEdge, Role R, // name of role (arc label)
            DepSet dep) // dep-set of the arc label
    {
        // check if GCraph already has FROM.TO edge labelled with RNAME
        DlCompletionTreeArc ret = from.getEdgeLabelled(R, to);
        if (ret == null) {
            ret = createEdge(from, to, isPredEdge, R, dep);
        } else {
            if (!dep.isEmpty()) {
                saveRareCond(ret.addDep(dep));
            }
        }
        return ret;
    }

    /** Create an empty R-neighbour of FROM; @return an edge to created node */
    public DlCompletionTreeArc createNeighbour(DlCompletionTree from, boolean isPredEdge,
            Role r, // name of role (arc label)
            DepSet dep) // dep-set of the arc label
    {
        if (pReasoner.getOptions().isRKG_IMPROVE_SAVE_RESTORE_DEPSET()) {
            assert branchingLevel == dep.level() + 1;
        }
        return createEdge(from, getNewNode(), isPredEdge, r, dep);
    }

    /** Create an R-loop of NODE wrt dep-set DEP; @return a loop edge */
    public DlCompletionTreeArc createLoop(DlCompletionTree node, Role r, DepSet dep) {
        return addRoleLabel(node, node, /* isPredEdge= */
                false, r, dep);
    }

    /** save given node wrt level */
    public void saveNode(DlCompletionTree node, int level) {
        if (node.needSave(level)) {
            node.save(level);
            savedNodes.add(node);
            ++nNodeSaves;
        }
    }

    /** restore given node wrt level */
    private void restoreNode(DlCompletionTree node, int level) {
        if (node.needRestore(level)) {
            node.restore(level);
            ++nNodeRestores;
        }
    }

    private boolean isBlockedBy(DlCompletionTree node, DlCompletionTree blocker) {
        assert !node.isNominalNode();
        assert !blocker.isNominalNode();
        if (blocker.isBlocked()) {
            return false;
        }
        if (!blocker.canBlockInit(node.getInit())) {
            return false;
        }
        boolean ret;
        if (sessionHasInverseRoles) {
            DLDag dag = pReasoner.getDAG();
            if (sessionHasNumberRestrictions) {
                ret = node.isBlockedBy_SHIQ(dag, blocker);
            } else {
                ret = node.isBlockedBy_SHI(dag, blocker);
            }
        } else {
            ret = node.isBlockedBy_SH(blocker);
        }
        if (pReasoner.getOptions().isUSE_BLOCKING_STATISTICS() && !ret) {
            pReasoner
                    .getOptions()
                    .getLog()
                    .printTemplate(Templates.IS_BLOCKED_FAILURE_BY, node.getId(),
                            blocker.getId());
        }
        return ret;
    }

    public void detectBlockedStatus(DlCompletionTree node) {
        DlCompletionTree p = node;
        boolean wasBlocked = node.isBlocked();
        boolean wasDBlocked = node.isDBlocked();
        node.setAffected();
        while (p.hasParent() && p.isBlockableNode() && p.isAffected()) {
            findDBlocker(p);
            if (p.isBlocked()) {
                // this.print(LL);
                return;
            }
            p = p.getParentNode();
        }
        p.clearAffected();
        if (wasBlocked && !node.isBlocked()) {
            unblockNode(node, wasDBlocked);
        }
    }

    private void unblockNode(DlCompletionTree node, boolean wasDBlocked) {
        if (node.isPBlocked() || !node.isBlockableNode()) {
            return;
        }
        if (!wasDBlocked) {
            saveRareCond(node.setUBlocked());
        }
        pReasoner.repeatUnblockedNode(node, wasDBlocked);
        unblockNodeChildren(node);
    }

    private void findDAncestorBlocker(DlCompletionTree node) {
        DlCompletionTree p = node;
        if (pReasoner.getOptions().isRKG_USE_FAIRNESS()) {
            if (nSkipBeforeBlock > 0) {
                for (int n = nSkipBeforeBlock - 1; n >= 0 && p.hasParent()
                        && p.isBlockableNode(); --n) {
                    p = p.getParentNode();
                }
            }
        }
        while (p.hasParent()) {
            p = p.getParentNode();
            if (!p.isBlockableNode()) {
                return;
            }
            if (isBlockedBy(node, p)) {
                setNodeDBlocked(node, p);
                return;
            }
        }
    }

    private void findDAnywhereBlocker(DlCompletionTree node) {
        for (int i = 0; i < endUsed && i != node.getId(); i++) {
            DlCompletionTree p = nodeBase.get(i);
            if (!p.isBlockedPBlockedNominalNodeCached()) {
                if (isBlockedBy(node, p)) {
                    setNodeDBlocked(node, p);
                    return;
                }
            }
        }
    }

    /** Class for maintaining graph of CT nodes. Behaves like deleteless
     * allocator for nodes, plus some obvious features */
    public boolean nonMergable(DlCompletionTree p, DlCompletionTree q,
            Reference<DepSet> dep) {
        return p.nonMergable(q, dep);
    }

    private void updateIR(DlCompletionTree p, DlCompletionTree q, DepSet toAdd) {
        if (!q.inequalityRelation.isEmpty()) {
            saveRareCond(p.updateIR(q, toAdd));
        }
    }

    public void initIR() {
        ++irLevel;
    }

    public boolean setCurIR(DlCompletionTree node, DepSet ds) {
        return node.initIR(irLevel, ds);
    }

    public void finiIR() {}

    private DlCompletionTreeArc createEdge(DlCompletionTree from, DlCompletionTree to,
            boolean isPredEdge, Role roleName, DepSet dep) {
        DlCompletionTreeArc forward = new DlCompletionTreeArc(roleName, dep, to);
        ctEdgeHeap.add(forward);
        forward.setSuccEdge(!isPredEdge);
        DlCompletionTreeArc backward = new DlCompletionTreeArc(roleName.inverse(), dep,
                from);
        ctEdgeHeap.add(backward);
        backward.setSuccEdge(isPredEdge);
        forward.setReverse(backward);
        saveNode(from, branchingLevel);
        saveNode(to, branchingLevel);
        from.addNeighbour(forward);
        to.addNeighbour(backward);
        if (pReasoner.getOptions().isLoggingActive()) {
            pReasoner
                    .getOptions()
                    .getLog()
                    .printTemplate(Templates.CREATE_EDGE,
                            isPredEdge ? to.getId() : from.getId(),
                            isPredEdge ? "<-" : "->",
                            isPredEdge ? from.getId() : to.getId(), roleName.getName());
        }
        return forward;
    }

    private DlCompletionTreeArc moveEdge(DlCompletionTree node, DlCompletionTreeArc edge,
            boolean isPredEdge, DepSet dep) {
        if (edge.isIBlocked()) {
            return null;
        }
        if (!isPredEdge && !edge.getArcEnd().isNominalNode()) {
            return null;
        }
        Role R = edge.getRole();
        if (edge.isReflexiveEdge()) {
            return createLoop(node, R, dep);
        }
        DlCompletionTree to = edge.getArcEnd();
        if (R != null) {
            invalidateEdge(edge);
        }
        for (DlCompletionTreeArc p : node.getNeighbour()) {
            if (p.getArcEnd().equals(to) && p.isPredEdge() != isPredEdge) {
                return addRoleLabel(node, to, !isPredEdge, R, dep);
            }
        }
        return addRoleLabel(node, to, isPredEdge, R, dep);
    }

    public void merge(DlCompletionTree from, DlCompletionTree to, DepSet dep,
            List<DlCompletionTreeArc> edges) {
        edges.clear();
        List<DlCompletionTreeArc> neighbour = from.getNeighbour();
        int size = neighbour.size();
        for (int i = 0; i < size; i++) {
            DlCompletionTreeArc p = neighbour.get(i);
            if (p.isPredEdge() || p.getArcEnd().isNominalNode()) {
                DlCompletionTreeArc temp = moveEdge(to, p, p.isPredEdge(), dep);
                if (temp != null) {
                    edges.add(temp);
                }
            }
            if (p.isSuccEdge()) {
                purgeEdge(p, to, dep);
            }
        }
        updateIR(to, from, dep);
        purgeNode(from, to, dep);
    }

    private void purgeNode(DlCompletionTree p, DlCompletionTree root, DepSet dep) {
        if (p.isPBlocked()) {
            return;
        }
        saveRareCond(p.setPBlocked(root, dep));
        List<DlCompletionTreeArc> neighbour = p.getNeighbour();
        int size = neighbour.size();
        for (int i = 0; i < size; i++) {
            DlCompletionTreeArc q = neighbour.get(i);
            if (q.isSuccEdge() && !q.isIBlocked()) {
                purgeEdge(q, root, dep);
            }
        }
    }

    private void purgeEdge(DlCompletionTreeArc e, DlCompletionTree root, DepSet dep) {
        if (e.getRole() != null) {
            invalidateEdge(e);
        }
        if (e.getArcEnd().isBlockableNode()) {
            purgeNode(e.getArcEnd(), root, dep);
        }
    }

    public void save() {
        DLCompletionGraphSaveState s = new DLCompletionGraphSaveState();
        stack.push(s);
        s.setnNodes(endUsed);
        s.setsNodes(savedNodes.size());
        s.setnEdges(ctEdgeHeap.size());
        rareStack.incLevel();
        ++branchingLevel;
    }

    public void restore(int level) {
        assert level > 0;
        branchingLevel = level;
        rareStack.restore(level);
        DLCompletionGraphSaveState s = stack.pop(level);
        endUsed = s.getnNodes();
        int nSaved = s.getsNodes();
        if (endUsed < Math.abs(savedNodes.size() - nSaved)) {
            for (int i = 0; i < endUsed; i++) {
                // XXX check: it was taking into account also empty nodes
                restoreNode(nodeBase.get(i), level);
            }
        } else {
            for (int i = nSaved; i < savedNodes.size(); i++) {
                if (savedNodes.get(i).getId() < endUsed) {
                    restoreNode(savedNodes.get(i), level);
                }
            }
        }
        Helper.resize(savedNodes, nSaved);
        Helper.resize(ctEdgeHeap, s.getnEdges());
    }

    public void print(LogAdapter o) {
        cgpIndent = 0;
        cgpFlag.clear();
        List<DlCompletionTree> l = nodeBase;
        for (int i = 1; i < endUsed && l.get(i).isNominalNode(); ++i) {
            cgpFlag.add(i);
        }
        printNode(l.get(0), o);
        for (int i = 1; i < endUsed && l.get(i).isNominalNode(); ++i) {
            cgpFlag.remove(l.get(i).getId());
            printNode(l.get(i), o);
        }
        o.print("\n");
    }

    private void printEdge(List<DlCompletionTreeArc> l, int pos,
            DlCompletionTreeArc _edge, DlCompletionTree parent, LogAdapter o) {
        DlCompletionTreeArc edge = _edge;
        DlCompletionTree node = edge.getArcEnd();
        boolean succEdge = edge.isSuccEdge();
        printIndent(o);
        if (edge.getArcEnd().equals(node) && edge.isSuccEdge() == succEdge) {
            o.print(" ");
            edge.print(o);
        }
        for (; pos < l.size(); pos++) {
            edge = l.get(pos);
            if (edge.getArcEnd().equals(node) && edge.isSuccEdge() == succEdge) {
                o.print(" ");
                edge.print(o);
            }
        }
        if (node.equals(parent)) {
            printIndent(o);
            o.print("-loop to node ");
            o.print(parent.getId());
        } else {
            printNode(node, o);
        }
    }

    private void printNode(DlCompletionTree node, LogAdapter o) {
        if (cgpIndent > 0) {
            printIndent(o);
            o.print("-");
        } else {
            o.print("\n");
        }
        node.printBody(o);
        if (cgpFlag.contains(node.getId())) {
            o.print("d");
            return;
        }
        cgpFlag.add(node.getId());
        boolean wantPred = node.isNominalNode();
        ++cgpIndent;
        List<DlCompletionTreeArc> l = node.getNeighbour();
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).isSuccEdge() || wantPred && l.get(i).getArcEnd().isNominalNode()) {
                printEdge(l, i + 1, l.get(i), node, o);
            }
        }
        --cgpIndent;
    }
}

class SaveStackRare {
    /** heap of saved objects */
    private LinkedList<Restorer> base = new LinkedList<Restorer>();
    /** current level */
    private int curLevel;

    public SaveStackRare() {
        curLevel = InitBranchingLevelValue;
    }

    /** inclrement current level */
    public void incLevel() {
        ++curLevel;
    }

    /** check that stack is empty */
    public boolean isEmpty() {
        return base.isEmpty();
    }

    /** add a new object to the stack */
    public void push(Restorer p) {
        p.setRaresavestackLevel(curLevel);
        base.addLast(p);
    }

    /** get all object from the top of the stack with levels >= LEVEL */
    public void restore(int level) {
        curLevel = level;
        while (base.size() > 0 && base.getLast().getRaresavestackLevel() > level) {
            // need to restore: restore last element, remove it from stack
            base.getLast().restore();
            base.removeLast();
        }
    }

    /** clear stack */
    public void clear() {
        base.clear();
        curLevel = InitBranchingLevelValue;
    }
}
