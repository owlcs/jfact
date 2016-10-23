package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.INITBRANCHINGLEVELVALUE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import conformance.PortedFrom;
import uk.ac.manchester.cs.chainsaw.FastSet;
import uk.ac.manchester.cs.chainsaw.FastSetFactory;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Reference;
import uk.ac.manchester.cs.jfact.helpers.SaveStack;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.kernel.state.DLCompletionGraphSaveState;

/** completion graph */
@PortedFrom(file = "dlCompletionGraph.h", name = "DlCompletionGraph")
public class DlCompletionGraph implements Serializable {

    /** initial value of IR level */
    @PortedFrom(file = "dlCompletionGraph.h", name = "initIRLevel") private static final int INIT_IR_LEVEL = 0;
    // XXX is this actually used?
    /** allocator for edges */
    @PortedFrom(file = "dlCompletionGraph.h", name = "CTEdgeHeap") private final List<DlCompletionTreeArc> ctEdgeHeap = new ArrayList<>();
    /** heap itself */
    @PortedFrom(file = "dlCompletionGraph.h", name = "NodeBase") private final List<DlCompletionTree> nodeBase;
    /** nodes, saved on current branching level */
    @PortedFrom(file = "dlCompletionGraph.h", name = "SavedNodes") private final List<DlCompletionTree> savedNodes = new ArrayList<>();
    /** host reasoner */
    @PortedFrom(file = "dlCompletionGraph.h", name = "pReasoner") private final DlSatTester pReasoner;
    /** remember the last generated ID for the node */
    @PortedFrom(file = "dlCompletionGraph.h", name = "nodeId") private int nodeId = 0;
    /** index of the next unallocated entry */
    @PortedFrom(file = "dlCompletionGraph.h", name = "endUsed") private int endUsed;
    /** current branching level (synchronised with resoner's one) */
    @PortedFrom(file = "dlCompletionGraph.h", name = "branchingLevel") private int branchingLevel;
    /** current IR level (should be valid BP) */
    @PortedFrom(file = "dlCompletionGraph.h", name = "IRLevel") private int irLevel;
    /** stack for rarely changed information */
    @PortedFrom(file = "dlCompletionGraph.h", name = "RareStack") private final SaveStackRare rareStack = new SaveStackRare();
    /** stack for usual saving/restoring */
    @PortedFrom(file = "dlCompletionGraph.h", name = "Stack") private final SaveStack<DLCompletionGraphSaveState> stack = new SaveStack<>();
    // helpers for the output
    /** bitmap to remember which node was printed */
    // TODO change to regular
    @PortedFrom(file = "dlCompletionGraph.h", name = "CPGFlag") private final FastSet cgpFlag = FastSetFactory.create();
    /** indent to print CGraph nodes */
    @PortedFrom(file = "dlCompletionGraph.h", name = "CPGIndent") private int cgpIndent;
    // statistical members
    /** number of node' saves */
    @PortedFrom(file = "dlCompletionGraph.h", name = "nNodeSaves") private int nNodeSaves;
    /** number of node' saves */
    @PortedFrom(file = "dlCompletionGraph.h", name = "nNodeRestores") private int nNodeRestores;
    /** maximal size of the graph */
    @PortedFrom(file = "dlCompletionGraph.h", name = "maxGraphSize") private int maxGraphSize = 0;
    // flags
    /** how many nodes skip before block; work only with FAIRNESS */
    @PortedFrom(file = "dlCompletionGraph.h", name = "nSkipBeforeBlock") private int nSkipBeforeBlock = 0;
    /** use or not lazy blocking (ie test blocking only expanding exists) */
    @PortedFrom(file = "dlCompletionGraph.h", name = "useLazyBlocking") private boolean useLazyBlocking;
    /** whether to use Anywhere blocking as opposed to an ancestor one */
    @PortedFrom(file = "dlCompletionGraph.h", name = "useAnywhereBlocking") private boolean useAnywhereBlocking;
    /** check if session has inverse roles */
    @PortedFrom(file = "dlCompletionGraph.h", name = "sessionHasInverseRoles") private boolean sessionHasInverseRoles;
    /** check if session has number restrictions */
    @PortedFrom(file = "dlCompletionGraph.h", name = "sessionHasNumberRestrictions") private boolean sessionHasNumberRestrictions;

    /**
     * c'tor: make INIT_SIZE objects
     * 
     * @param initSize
     *        initSize
     * @param p
     *        p
     */
    public DlCompletionGraph(int initSize, DlSatTester p) {
        pReasoner = p;
        nodeId = 0;
        endUsed = 0;
        branchingLevel = INITBRANCHINGLEVELVALUE;
        irLevel = INIT_IR_LEVEL;
        nodeBase = new ArrayList<>(initSize);
        for (int i = 0; i < initSize; i++) {
            nodeBase.add(new DlCompletionTree(nodeId++, pReasoner.getOptions()));
        }
        clearStatistics();
        initRoot();
    }

    /** increase heap size */
    @PortedFrom(file = "dlCompletionGraph.h", name = "grow")
    private void grow() {
        int size = nodeBase.size();
        for (int i = 0; i < size; i++) {
            nodeBase.add(new DlCompletionTree(nodeId++, pReasoner.getOptions()));
        }
    }

    /** init root node */
    @PortedFrom(file = "dlCompletionGraph.h", name = "initRoot")
    private void initRoot() {
        assert endUsed == 0;
        getNewNode();
    }

    /**
     * invalidate EDGE, save restoring info
     * 
     * @param edge
     *        edge
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "invalidateEdge")
    private void invalidateEdge(DlCompletionTreeArc edge) {
        saveRareCond(edge.save());
    }

    /**
     * check if d-blocked node is still d-blocked
     * 
     * @param node
     *        node
     * @return true if still blocked
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "isStillDBlocked")
    private boolean isStillDBlocked(DlCompletionTree node) {
        return node.isDBlocked() && isBlockedBy(node, node.blocker);
    }

    /**
     * try to find d-blocker for a node
     * 
     * @param node
     *        node
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "findDBlocker")
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

    /**
     * unblock all the children of the node
     * 
     * @param node
     *        node
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "unblockNodeChildren")
    private void unblockNodeChildren(DlCompletionTree node) {
        node.getNeighbour().stream().filter(q -> q.unblockable())
            // all of them are i-blocked
            .forEach(q -> unblockNode(q.getArcEnd(), false));
    }

    /**
     * mark NODE as a d-blocked by a BLOCKER
     * 
     * @param node
     *        node
     * @param blocker
     *        blocker
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "setNodeDBlocked")
    private void setNodeDBlocked(DlCompletionTree node, DlCompletionTree blocker) {
        saveRareCond(node.setDBlocked(blocker));
        propagateIBlockedStatus(node, node);
    }

    /**
     * mark NODE as an i-blocked by a BLOCKER
     * 
     * @param node
     *        node
     * @param blocker
     *        blocker
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "setNodeIBlocked")
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

    /**
     * propagate i-blocked status to all children of NODE
     * 
     * @param node
     *        node
     * @param blocker
     *        blocker
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "propagateIBlockedStatus")
    private void propagateIBlockedStatus(DlCompletionTree node, DlCompletionTree blocker) {
        node.getNeighbour().stream().filter(q -> q.isSuccEdge() && !q.isIBlocked()).forEach(q -> setNodeIBlocked(q
            .getArcEnd(), blocker));
    }

    /**
     * @param node
     *        node
     * @return true iff node might became unblocked
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "canBeUnBlocked")
    private boolean canBeUnBlocked(DlCompletionTree node) {
        // in presence of inverse roles it is not enough
        // to check the affected flag for both node and its blocker
        // see tModal* for example
        if (sessionHasInverseRoles) {
            return true;
        }
        // if node is affected -- it can be unblocked.
        // if blocker became blocked itself -- the same
        return node.isAffected() || node.isIllegallyDBlocked();
    }

    /**
     * print proper indentation
     * 
     * @param o
     *        o
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "PrintIndent")
    private void printIndent(LogAdapter o) {
        o.print("\n|");
        for (int i = 1; i < cgpIndent; ++i) {
            o.print(" |");
        }
    }

    // flag setting
    /**
     * set flags for blocking
     * 
     * @param nSkip
     *        nSkip
     * @param useLB
     *        useLB
     * @param useAB
     *        useAB
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "initContext")
    public void initContext(int nSkip, boolean useLB, boolean useAB) {
        nSkipBeforeBlock = nSkip;
        useLazyBlocking = useLB;
        useAnywhereBlocking = useAB;
    }

    /**
     * set blocking method for a session
     * 
     * @param hasInverse
     *        hasInverse
     * @param hasQCR
     *        hasQCR
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "setBlockingMethod")
    public void setBlockingMethod(boolean hasInverse, boolean hasQCR) {
        sessionHasInverseRoles = hasInverse;
        sessionHasNumberRestrictions = hasQCR;
    }

    /**
     * add concept C of a type TAG to NODE; call blocking check if appropriate
     * 
     * @param node
     *        node
     * @param c
     *        c
     * @param complex
     *        true if complex concepts sought
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "addConceptToNode")
    public void addConceptToNode(DlCompletionTree node, ConceptWDep c, boolean complex) {
        node.addConcept(c, complex);
        if (useLazyBlocking) {
            node.setAffected();
        } else {
            detectBlockedStatus(node);
        }
    }

    // access to nodes
    /** @return root node (non-const) */
    @PortedFrom(file = "dlCompletionGraph.h", name = "getRoot")
    public DlCompletionTree getRoot() {
        return nodeBase.get(0).resolvePBlocker();
    }

    /**
     * @param i
     *        i
     * @return dl completion tree at index i
     */
    @Nullable
    @PortedFrom(file = "dlCompletionGraph.h", name = "getNode")
    public DlCompletionTree getNode(int i) {
        if (i >= endUsed) {
            return null;
        }
        return nodeBase.get(i);
    }

    /**
     * @return stream of nodes
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "getNode")
    public Stream<DlCompletionTree> nodes() {
        return nodeBase.stream().limit(endUsed);
    }

    /** @return new node (with internal level) */
    @PortedFrom(file = "dlCompletionGraph.h", name = "getNewNode")
    public DlCompletionTree getNewNode() {
        if (endUsed >= nodeBase.size()) {
            grow();
        }
        DlCompletionTree ret = nodeBase.get(endUsed++);
        ret.init(branchingLevel);
        return ret;
    }

    // blocking
    /**
     * update blocked status for d-blocked node
     * 
     * @param node
     *        node
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "updateDBlockedStatus")
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
    @PortedFrom(file = "dlCompletionGraph.h", name = "retestCGBlockedStatus")
    public void retestCGBlockedStatus() {
        do {
            nodeBase.stream().limit(endUsed).filter(DlCompletionTree::isDBlocked).forEach(this::updateDBlockedStatus);
            /**
             * we need to repeat the thing if something became unblocked and
             * then blocked again, in case one of the blockers became blocked
             * itself; see tModal3 for such an example
             */
        } while (nodeBase.stream().limit(endUsed).anyMatch(DlCompletionTree::isIllegallyDBlocked));
    }

    /**
     * @param c
     *        fairness constant
     * @return true if a fairness constraint C is violated in one of the loops
     *         in the CGraph
     */
    @Nullable
    @PortedFrom(file = "dlCompletionGraph.h", name = "getFCViolator")
    public DlCompletionTree getFCViolator(int c) {
        return nodeBase.stream().filter(p -> p.isDBlocked() && !p.isLoopLabelled(c)).findAny().orElse(null);
    }

    /** clear all the session statistics */
    @PortedFrom(file = "dlCompletionGraph.h", name = "clearStatistics")
    public void clearStatistics() {
        nNodeSaves = 0;
        nNodeRestores = 0;
        if (maxGraphSize < endUsed) {
            maxGraphSize = endUsed;
        }
    }

    /** @return get number of nodes in the CGraph */
    @PortedFrom(file = "dlCompletionGraph.h", name = "maxSize")
    public int maxSize() {
        return maxGraphSize;
    }

    /** mark all heap elements as unused */
    @PortedFrom(file = "dlCompletionGraph.h", name = "clear")
    public void clear() {
        ctEdgeHeap.clear();
        endUsed = 0;
        branchingLevel = INITBRANCHINGLEVELVALUE;
        irLevel = INIT_IR_LEVEL;
        rareStack.clear();
        stack.clear();
        savedNodes.clear();
        initRoot();
    }

    /**
     * save rarely appeared info if P is non-null
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "saveRareCond")
    public void saveRareCond(@Nullable Restorer p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        rareStack.push(p);
    }

    /**
     * @param l
     *        restorers to save
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "saveRareCond")
    public void saveRareCond(List<Restorer> l) {
        l.forEach(this::saveRareCond);
    }

    /**
     * get the rare stack
     * 
     * @return rare stack
     */
    public SaveStackRare getRareStack() {
        return rareStack;
    }

    // role/node
    /**
     * add role R with dep-set DEP to the label of the TO arc
     * 
     * @param from
     *        from
     * @param to
     *        to
     * @param isPredEdge
     *        isPredEdge
     * @param r
     *        name of role (arc label)
     * @param dep
     *        dep-set of the arc label
     * @return completion tree arc
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "addRoleLabel")
    public DlCompletionTreeArc addRoleLabel(DlCompletionTree from, DlCompletionTree to, boolean isPredEdge, Role r,
        DepSet dep) {
        // check if GCraph already has FROM.TO edge labelled with RNAME
        DlCompletionTreeArc ret = from.getEdgeLabelled(r, to);
        if (ret == null) {
            ret = createEdge(from, to, isPredEdge, r, dep);
        } else {
            if (!dep.isEmpty()) {
                saveRareCond(ret.addDep(dep));
            }
        }
        return ret;
    }

    /**
     * Create an empty R-neighbour of FROM;
     * 
     * @param from
     *        from
     * @param isPredEdge
     *        isPredEdge
     * @param r
     *        name of role (arc label)
     * @param dep
     *        dep-set of the arc label
     * @return an edge to created node
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "createNeighbour")
    public DlCompletionTreeArc createNeighbour(DlCompletionTree from, boolean isPredEdge, Role r, DepSet dep) {
        if (pReasoner.getOptions().isImproveSaveRestoreDepset()) {
            assert branchingLevel == dep.level() + 1;
        }
        return createEdge(from, getNewNode(), isPredEdge, r, dep);
    }

    /**
     * Create an R-loop of NODE wrt dep-set DEP;
     * 
     * @param node
     *        node
     * @param r
     *        r
     * @param dep
     *        dep
     * @return a loop edge
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "createLoop")
    public DlCompletionTreeArc createLoop(DlCompletionTree node, Role r, DepSet dep) {
        return addRoleLabel(node, node, /* isPredEdge= */
            false, r, dep);
    }

    /**
     * save given node wrt level
     * 
     * @param node
     *        node
     * @param level
     *        level
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "saveNode")
    public void saveNode(DlCompletionTree node, int level) {
        if (node.needSave(level)) {
            node.save(level);
            savedNodes.add(node);
            ++nNodeSaves;
        }
    }

    /**
     * restore given node wrt level
     * 
     * @param node
     *        node
     * @param level
     *        level
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "restoreNode")
    private void restoreNode(DlCompletionTree node, int level) {
        if (node.needRestore(level)) {
            node.restore(level);
            ++nNodeRestores;
        }
    }

    @PortedFrom(file = "dlCompletionGraph.h", name = "isBlockedBy")
    private boolean isBlockedBy(DlCompletionTree node, DlCompletionTree blocker) {
        assert !node.isNominalNode();
        assert !blocker.isNominalNode();
        // blocked node can't be blocked itself
        if (blocker.isBlocked()) {
            return false;
        }
        // easy check: Init is not in the label if a blocker
        if (!blocker.canBlockInit(node.getInit())) {
            return false;
        }
        boolean ret;
        if (sessionHasInverseRoles) {
            // subset blocking
            DLDag dag = pReasoner.getDAG();
            if (sessionHasNumberRestrictions) {
                // I+F -- optimised blocking
                ret = node.isBlockedBySHIQ(dag, blocker);
            } else {
                // just I -- equality blocking
                ret = node.isBlockedBySHI(dag, blocker);
            }
        } else {
            ret = node.isBlockedBySH(blocker);
        }
        if (pReasoner.getOptions().isUseBlockingStatistics() && !ret) {
            pReasoner.getOptions().getLog().printTemplateInt(Templates.IS_BLOCKED_FAILURE_BY, node.getId(), blocker
                .getId());
        }
        return ret;
    }

    /**
     * @param node
     *        node
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "detectBlockedStatus")
    public void detectBlockedStatus(DlCompletionTree node) {
        DlCompletionTree p = node;
        boolean wasBlocked = node.isBlocked();
        boolean wasDBlocked = node.isDBlocked();
        // if we are here, then node *need* to be checked
        // so this is to prevent from going out of the loop
        node.setAffected();
        while (p.hasParent() && p.isBlockableNode() && p.isAffected()) {
            findDBlocker(p);
            if (p.isBlocked()) {
                return;
            }
            p = p.getParentNode();
        }
        p.clearAffected();
        if (wasBlocked && !node.isBlocked()) {
            unblockNode(node, wasDBlocked);
        }
    }

    @PortedFrom(file = "dlCompletionGraph.h", name = "unblockNode")
    private void unblockNode(DlCompletionTree node, boolean wasDBlocked) {
        if (node.isPBlocked() || !node.isBlockableNode()) {
            return;
        }
        if (!wasDBlocked) {
            // if it was DBlocked -- findDBlocker() made it
            saveRareCond(node.setUBlocked());
        }
        pReasoner.repeatUnblockedNode(node, wasDBlocked);
        unblockNodeChildren(node);
    }

    @PortedFrom(file = "dlCompletionGraph.h", name = "findDAncestorBlocker")
    private void findDAncestorBlocker(DlCompletionTree node) {
        DlCompletionTree p = node;
        if (pReasoner.getOptions().isUseFairness() && nSkipBeforeBlock > 0) {
            for (int n = nSkipBeforeBlock - 1; n >= 0 && p.hasParent() && p.isBlockableNode(); --n) {
                p = p.getParentNode();
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

    @PortedFrom(file = "dlCompletionGraph.h", name = "findDAnywhereBlocker")
    private void findDAnywhereBlocker(DlCompletionTree node) {
        for (int i = 0; i < endUsed && i != node.getId(); i++) {
            DlCompletionTree p = nodeBase.get(i);
            if (!p.isBlockedPBlockedNominalNodeCached() && isBlockedBy(node, p)) {
                setNodeDBlocked(node, p);
                return;
            }
        }
    }

    /**
     * Class for maintaining graph of CT nodes. Behaves like deleteless
     * allocator for nodes, plus some obvious features
     */
    /**
     * @param p
     *        p
     * @param q
     *        q
     * @param dep
     *        dep
     * @return whether p and q can be merged
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "nonMergable")
    public boolean nonMergable(DlCompletionTree p, DlCompletionTree q, Reference<DepSet> dep) {
        return p.nonMergable(q, dep);
    }

    @PortedFrom(file = "dlCompletionGraph.h", name = "updateIR")
    private void updateIR(DlCompletionTree p, DlCompletionTree q, DepSet toAdd) {
        if (!q.inequalityRelation.isEmpty()) {
            saveRareCond(p.updateIR(q, toAdd));
        }
    }

    /** init IR */
    @PortedFrom(file = "dlCompletionGraph.h", name = "initIR")
    public void initIR() {
        ++irLevel;
    }

    /**
     * @param node
     *        node
     * @param ds
     *        ds
     * @return true if IR alreadh has the label
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "setCurIR")
    public boolean setCurIR(DlCompletionTree node, DepSet ds) {
        return node.initIR(irLevel, ds);
    }

    /** fini IR */
    @PortedFrom(file = "dlCompletionGraph.h", name = "finiIR")
    public void finiIR() {
        // nothing to do
    }

    @PortedFrom(file = "dlCompletionGraph.h", name = "createEdge")
    private DlCompletionTreeArc createEdge(DlCompletionTree from, DlCompletionTree to, boolean isPredEdge,
        Role roleName, DepSet dep) {
        DlCompletionTreeArc forward = new DlCompletionTreeArc(roleName, dep, to);
        ctEdgeHeap.add(forward);
        forward.setSuccEdge(!isPredEdge);
        DlCompletionTreeArc backward = new DlCompletionTreeArc(roleName.inverse(), dep, from);
        ctEdgeHeap.add(backward);
        backward.setSuccEdge(isPredEdge);
        forward.setReverse(backward);
        saveNode(from, branchingLevel);
        saveNode(to, branchingLevel);
        from.addNeighbour(forward);
        to.addNeighbour(backward);
        if (pReasoner.getOptions().isLoggingActive()) {
            pReasoner.getOptions().getLog().printTemplate(Templates.CREATE_EDGE, Integer.toString(isPredEdge ? to
                .getId() : from.getId()), isPredEdge ? "<-" : "->", Integer.toString(isPredEdge ? from.getId()
                    : to.getId()), roleName.getIRI());
        }
        return forward;
    }

    @Nullable
    @PortedFrom(file = "dlCompletionGraph.h", name = "moveEdge")
    private DlCompletionTreeArc moveEdge(DlCompletionTree node, DlCompletionTreeArc edge, boolean isPredEdge,
        DepSet dep) {
        // skip already purged edges
        if (edge.isIBlocked()) {
            return null;
        }
        // skip edges not leading to nominal nodes
        if (!isPredEdge && !edge.getArcEnd().isNominalNode()) {
            return null;
        }
        Role r = edge.getRole();
        // we shall copy reflexive edges in a specific way
        if (edge.isReflexiveEdge()) {
            return createLoop(node, r, dep);
        }
        DlCompletionTree to = edge.getArcEnd();
        // invalidate old edge
        if (r != null) {
            invalidateEdge(edge);
        }
        // try to find for NODE.TO (TO.NODE) whether we
        // have TO.NODE (NODE.TO) edge already
        Optional<DlCompletionTreeArc> findAny = node.getNeighbour().stream().filter(p -> p.getArcEnd() == to && p
            .isPredEdge() != isPredEdge).findAny();
        if (findAny.isPresent()) {
            return addRoleLabel(node, to, !isPredEdge, r, dep);
        }
        return addRoleLabel(node, to, isPredEdge, r, dep);
    }

    /**
     * merge labels; see SHOIN paper for detailed description
     * 
     * @param from
     *        from
     * @param to
     *        to
     * @param dep
     *        dep
     * @param edges
     *        edges
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "merge")
    public void merge(DlCompletionTree from, DlCompletionTree to, DepSet dep, List<DlCompletionTreeArc> edges) {
        edges.clear();
        // 1. For all x: x->FROM make x->TO
        // FIXME!! no optimisations (in case there exists an edge TO->x labelled
        // with R-)
        // 2. For all nominal x: FROM->x make TO->x
        // FIXME!! no optimisations (in case there exists an edge x->TO labelled
        // with R-)
        from.getNeighbour().forEach(p -> {
            if (p.isPredEdge() || p.getArcEnd().isNominalNode()) {
                DlCompletionTreeArc temp = moveEdge(to, p, p.isPredEdge(), dep);
                if (temp != null) {
                    edges.add(temp);
                }
            }
            if (p.isSuccEdge()) {
                purgeEdge(p, to, dep);
            }
        });
        // 4. For all x: FROM \neq x, add TO \neq x
        updateIR(to, from, dep);
        // 5. Purge FROM
        purgeNode(from, to, dep);
    }

    @PortedFrom(file = "dlCompletionGraph.h", name = "purgeNode")
    private void purgeNode(DlCompletionTree p, DlCompletionTree root, DepSet dep) {
        if (p.isPBlocked()) {
            return;
        }
        saveRareCond(p.setPBlocked(root, dep));
        // update successors
        p.getNeighbour().stream().filter(q -> q.isSuccEdge() && !q.isIBlocked()).forEach(q -> purgeEdge(q, root, dep));
    }

    @PortedFrom(file = "dlCompletionGraph.h", name = "purgeEdge")
    private void purgeEdge(DlCompletionTreeArc e, DlCompletionTree root, DepSet dep) {
        if (e.getRole() != null) {
            // invalidate given link
            invalidateEdge(e);
        }
        if (e.getArcEnd().isBlockableNode()) {
            // purge blockable successor
            purgeNode(e.getArcEnd(), root, dep);
        }
    }

    /** save dl completion graph */
    @PortedFrom(file = "dlCompletionGraph.h", name = "save")
    public void save() {
        DLCompletionGraphSaveState s = new DLCompletionGraphSaveState();
        stack.push(s);
        s.setnNodes(endUsed);
        s.setsNodes(savedNodes.size());
        s.setnEdges(ctEdgeHeap.size());
        rareStack.incLevel();
        ++branchingLevel;
    }

    /**
     * @param level
     *        level
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "restore")
    public void restore(int level) {
        assert level > 0;
        branchingLevel = level;
        rareStack.restore(level);
        DLCompletionGraphSaveState s = stack.pop(level);
        endUsed = s.getnNodes();
        int nSaved = s.getsNodes();
        if (endUsed < Math.abs(savedNodes.size() - nSaved)) {
            // it's cheaper to restore all nodes
            nodeBase.stream().limit(endUsed).forEach(p -> restoreNode(p, level));
        } else {
            for (int i = nSaved; i < savedNodes.size(); i++) {
                if (savedNodes.get(i).getId() < endUsed) {
                    // don't restore nodes that are dead anyway
                    restoreNode(savedNodes.get(i), level);
                }
            }
        }
        Helper.resize(savedNodes, nSaved, null);
        Helper.resize(ctEdgeHeap, s.getnEdges(), null);
    }

    /**
     * @param o
     *        o
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "print")
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

    @PortedFrom(file = "dlCompletionGraph.h", name = "PrintEdge")
    private void printEdge(List<DlCompletionTreeArc> l, int pos, DlCompletionTreeArc edge, DlCompletionTree parent,
        LogAdapter o) {
        DlCompletionTreeArc arc = edge;
        DlCompletionTree node = arc.getArcEnd();
        boolean succEdge = arc.isSuccEdge();
        printIndent(o);
        if (arc.getArcEnd().equals(node) && arc.isSuccEdge() == succEdge) {
            o.print(" ");
            arc.print(o);
        }
        for (; pos < l.size(); pos++) {
            arc = l.get(pos);
            if (arc.getArcEnd().equals(node) && arc.isSuccEdge() == succEdge) {
                o.print(" ");
                arc.print(o);
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

    @PortedFrom(file = "dlCompletionGraph.h", name = "PrintNode")
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
        node.getNeighbour().stream().filter(p -> p.isSuccEdge() || wantPred && p.getArcEnd().isNominalNode()).forEach(
            p -> printEdge(node.getNeighbour(), node.getNeighbour().indexOf(p), p, node, o));
        --cgpIndent;
    }
}
