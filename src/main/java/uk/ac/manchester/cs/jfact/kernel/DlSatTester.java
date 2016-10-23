package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;
import static uk.ac.manchester.cs.jfact.kernel.DagTag.*;
import static uk.ac.manchester.cs.jfact.kernel.Redo.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.reasoner.TimeOutException;

import conformance.Original;
import conformance.PortedFrom;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import uk.ac.manchester.cs.chainsaw.FastSet;
import uk.ac.manchester.cs.chainsaw.FastSetFactory;
import uk.ac.manchester.cs.chainsaw.LocalFastSet;
import uk.ac.manchester.cs.jfact.datatypes.DataTypeReasoner;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeEntry;
import uk.ac.manchester.cs.jfact.datatypes.LiteralEntry;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Reference;
import uk.ac.manchester.cs.jfact.helpers.SaveStack;
import uk.ac.manchester.cs.jfact.helpers.Stats;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.helpers.Timer;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheConst;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheIan;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheState;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.kernel.todolist.ToDoEntry;
import uk.ac.manchester.cs.jfact.kernel.todolist.ToDoList;

/** sat tester */
@PortedFrom(file = "Reasoner.h", name = "DlSatTester")
public class DlSatTester implements Serializable {

    /** Enum for usage the Tactics to a ToDoEntry */
    class BranchingContext implements Serializable {

        /** currently processed node */
        protected DlCompletionTree node;
        /** currently processed concept */
        protected ConceptWDep concept = null;
        /** dependences for branching clashes */
        protected DepSet branchDep = DepSet.create();
        /** size of a session GCIs vector */
        protected int sgSize;

        /** empty c'tor */
        public BranchingContext() {
            node = null;
        }

        /** init indeces (if necessary) */
        public void init() {
            // default implementation empty
        }

        /** give the next branching alternative */
        public void nextOption() {
            // default implementation empty
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + " dep '" + branchDep + "' curconcept '" + (concept == null
                ? new ConceptWDep(BP_INVALID) : concept) + "' curnode '" + node + '\'';
        }
    }

    class BCChoose extends BranchingContext {}

    /** stack to keep BContext */
    class BCStack extends SaveStack<BranchingContext> {

        /** single entry for the barrier (good for nominal reasoner) */
        private final BCBarrier bcBarrier;

        protected BCStack() {
            bcBarrier = new BCBarrier();
        }

        /** push method to use */
        @Override
        public void push(BranchingContext p) {
            p.init();
            initBC(p);
            super.push(p);
        }

        /**
         * get BC for Or-rule
         * 
         * @return or
         */
        protected BranchingContext pushOr() {
            BCOr o = new BCOr();
            push(o);
            return o;
        }

        /**
         * get BC for NN-rule
         * 
         * @return nn
         */
        protected BranchingContext pushNN() {
            BCNN n = new BCNN();
            push(n);
            return n;
        }

        /**
         * get BC for LE-rule
         * 
         * @return le
         */
        protected BCLE<DlCompletionTreeArc> pushLE() {
            BCLE<DlCompletionTreeArc> e = new BCLE<>();
            push(e);
            return e;
        }

        /**
         * get BC for TopLE-rule
         * 
         * @return le
         */
        protected BCLE<DlCompletionTree> pushTopLE() {
            // XXX verify if this is correct
            BCLE<DlCompletionTree> e = new BCLE<>();
            push(e);
            return e;
        }

        /**
         * get BC for Choose-rule
         * 
         * @return choose
         */
        protected BCChoose pushCh() {
            BCChoose c = new BCChoose();
            push(c);
            return c;
        }

        /**
         * get BC for the barrier
         * 
         * @return barrier
         */
        protected BCBarrier pushBarrier() {
            push(bcBarrier);
            return bcBarrier;
        }
    }

    class BCBarrier extends BranchingContext {}

    class BCLE<I> extends BranchingContext {

        /** current branching index; used in several branching rules */
        private int branchIndex;
        /** index of a merge-candidate (in LE concept) */
        private int mergeCandIndex;
        /** vector of edges to be merged */
        private List<I> edges = new ArrayList<>();

        /** init tag and indeces */
        @Override
        public void init() {
            branchIndex = 0;
            mergeCandIndex = 0;
        }

        public List<I> swap(List<I> values) {
            List<I> temp = edges;
            edges = values;
            return temp;
        }

        /** correct mergeCandIndex after changing */
        protected void resetMCI() {
            mergeCandIndex = edges.size() - 1;
        }

        /** give the next branching alternative */
        @Override
        public void nextOption() {
            --mergeCandIndex;
            // get new merge candidate
            if (mergeCandIndex == branchIndex) {
                // nothing more can be mergeable to BI node
                ++branchIndex;
                // change the candidate to merge to
                resetMCI();
            }
        }

        // access to the fields
        /**
         * get FROM pointer to merge
         * 
         * @return from
         */
        protected I getFrom() {
            return edges.get(mergeCandIndex);
        }

        /**
         * get FROM pointer to merge
         * 
         * @return to
         */
        protected I getTo() {
            return edges.get(branchIndex);
        }

        /**
         * check if the LE has no option to process
         * 
         * @return true if more
         */
        protected boolean noMoreLEOptions() {
            return mergeCandIndex <= branchIndex;
        }

        protected List<I> getEdgesToMerge() {
            return edges;
        }

        protected void setEdgesToMerge(List<I> edgesToMerge) {
            edges = edgesToMerge;
        }
    }

    class BCNN extends BranchingContext {

        /** current branching index; used in several branching rules */
        private int branchIndex;

        /** init tag and indeces */
        @Override
        public void init() {
            branchIndex = 1;
        }

        /** give the next branching alternative */
        @Override
        public void nextOption() {
            ++branchIndex;
        }

        // access to the fields
        /**
         * check if the NN has no option to process
         * 
         * @param n
         *        n
         * @return true if no more options
         */
        protected boolean noMoreNNOptions(int n) {
            return branchIndex > n;
        }

        protected int getBranchIndex() {
            return branchIndex;
        }

        public void setBranchIndex(int branchIndex) {
            this.branchIndex = branchIndex;
        }
    }

    class BCOr extends BranchingContext {

        /** current branching index; used in several branching rules */
        private int branchIndex;
        private int size = 0;
        /** useful disjuncts (ready to add) in case of OR */
        private List<ConceptWDep> applicableOrEntries = new ArrayList<>();

        /** init tag and indeces */
        @Override
        public void init() {
            branchIndex = 0;
        }

        /** give the next branching alternative */
        @Override
        public void nextOption() {
            ++branchIndex;
        }

        // access to the fields
        /**
         * check if the current processing OR entry is the last one
         * 
         * @return true if last
         */
        protected boolean isLastOrEntry() {
            return size == branchIndex + 1;
        }

        /**
         * current element of OrIndex
         * 
         * @return current or
         */
        protected ConceptWDep orCur() {
            return applicableOrEntries.get(branchIndex);
        }

        protected int getBranchIndex() {
            return branchIndex;
        }

        protected int[] getApplicableOrEntriesConcepts() {
            int[] toReturn = new int[branchIndex];
            for (int i = 0; i < toReturn.length; i++) {
                toReturn[i] = applicableOrEntries.get(i).getConcept();
            }
            return toReturn;
        }

        protected List<ConceptWDep> setApplicableOrEntries(List<ConceptWDep> list) {
            List<ConceptWDep> toReturn = applicableOrEntries;
            applicableOrEntries = list;
            size = applicableOrEntries.size();
            return toReturn;
        }

        @Override
        public String toString() {
            StringBuilder o = new StringBuilder();
            o.append("BCOR ");
            o.append(branchIndex);
            o.append(" dep ");
            o.append(branchDep);
            o.append(" curconcept ");
            o.append(concept == null ? new ConceptWDep(BP_INVALID) : concept);
            o.append(" curnode ");
            o.append(node);
            o.append(" orentries [");
            o.append(applicableOrEntries);
            o.append(']');
            return o.toString();
        }
    }

    /** GCIs local to session */
    @PortedFrom(file = "Reasoner.h", name = "SessionGCIs") private final TIntArrayList sessionGCIs = new TIntArrayList();
    /** set of active splits */
    @PortedFrom(file = "Reasoner.h", name = "ActiveSplits") private final FastSet activeSplits = FastSetFactory
        .create();
    /** concept signature of current CGraph */
    @PortedFrom(file = "Reasoner.h", name = "SessionSignature") private final Set<NamedEntity> sessionSignature = new HashSet<>();
    /** signature to dep-set map for current session */
    @PortedFrom(file = "Reasoner.h", name = "SessionSigDepSet") private final Map<NamedEntity, DepSet> sessionSigDepSet = new HashMap<>();
    /** nodes to merge in the TopRole-LE rules */
    @PortedFrom(file = "Reasoner.h", name = "NodesToMerge") private List<DlCompletionTree> nodesToMerge = new ArrayList<>();
    @PortedFrom(file = "Reasoner.h", name = "EdgesToMerge") private List<DlCompletionTreeArc> edgesToMerge = new ArrayList<>();
    /** host TBox */
    @PortedFrom(file = "Reasoner.h", name = "tBox") protected final TBox tBox;
    /** link to dag from TBox */
    @PortedFrom(file = "Reasoner.h", name = "DLHeap") protected final DLDag dlHeap;
    /** all the reflexive roles */
    @PortedFrom(file = "Reasoner.h", name = "ReflexiveRoles") private final List<Role> reflexiveRoles = new ArrayList<>();
    /** Completion Graph of tested concept(s) */
    @PortedFrom(file = "Reasoner.h", name = "CGraph") protected final DlCompletionGraph cGraph;
    /** Todo list */
    @PortedFrom(file = "Reasoner.h", name = "TODO") private final ToDoList todo;
    @Original private final FastSet used = new LocalFastSet();
    /** GCI-related KB flags */
    @PortedFrom(file = "Reasoner.h", name = "GCIs") private final KBFlags gcis;
    /** record nodes that were processed during Cascaded Cache construction */
    @PortedFrom(file = "Reasoner.h", name = "inProcess") private final FastSet inProcess = FastSetFactory.create();
    /** timer for the SAT tests (ie, cache creation) */
    @PortedFrom(file = "Reasoner.h", name = "satTimer") private final Timer satTimer = new Timer();
    /** timer for the SUB tests (ie, general subsumption) */
    @PortedFrom(file = "Reasoner.h", name = "subTimer") private final Timer subTimer = new Timer();
    /** timer for a single test; use it as a timeout checker */
    @PortedFrom(file = "Reasoner.h", name = "testTimer") private final Timer testTimer = new Timer();
    // save/restore option
    /** stack for the local reasoner's state */
    @PortedFrom(file = "Reasoner.h", name = "Stack") protected final BCStack stack = new BCStack();
    /** context from the restored branching rule */
    @PortedFrom(file = "Reasoner.h", name = "bContext") protected BranchingContext bContext;
    /** index of last non-det situation */
    @PortedFrom(file = "Reasoner.h", name = "tryLevel") private int tryLevel;
    /** shift in order to determine the 1st non-det application */
    @PortedFrom(file = "Reasoner.h", name = "nonDetShift") protected int nonDetShift;
    // current values
    /** currently processed CTree node */
    @PortedFrom(file = "Reasoner.h", name = "curNode") protected DlCompletionTree curNode;
    /** currently processed Concept */
    @Original private DepSet curConceptDepSet;
    @Original private int curConceptConcept;
    /** size of the DAG with some extra space */
    @PortedFrom(file = "Reasoner.h", name = "dagSize") private int dagSize;
    /** temporary array used in OR operation */
    @PortedFrom(file = "Reasoner.h", name = "OrConceptsToTest") private List<ConceptWDep> orConceptsToTest = new ArrayList<>();
    /** contains clash set if clash is encountered in a node label */
    @PortedFrom(file = "Reasoner.h", name = "clashSet") private DepSet clashSet = DepSet.create();
    @Original protected final JFactReasonerConfiguration options;
    // session status flags:
    /** true if nominal-related expansion rule was fired during reasoning */
    @PortedFrom(file = "Reasoner.h", name = "encounterNominal") private boolean encounterNominal;
    /** flag to show if it is necessary to produce DT reasoning immediately */
    @PortedFrom(file = "Reasoner.h", name = "checkDataNode") private boolean checkDataNode;
    /**
     * cache for testing whether it's possible to non-expand newly created node
     */
    @PortedFrom(file = "Reasoner.h", name = "newNodeCache") private final ModelCacheIan newNodeCache;
    /** auxilliary cache that is built from the edges of newly created node */
    @PortedFrom(file = "Reasoner.h", name = "newNodeEdges") private final ModelCacheIan newNodeEdges;
    @Original private final Stats stats = new Stats();
    @Original private static final EnumSet<DagTag> handlecollection = EnumSet.of(AND, COLLECTION);
    @Original private static final EnumSet<DagTag> handleforallle = EnumSet.of(FORALL, LE);
    @Original private static final EnumSet<DagTag> handlesingleton = EnumSet.of(PSINGLETON, NSINGLETON, NCONCEPT,
        PCONCEPT);
    private static final Comparator<DlCompletionTree> nodeCompare = Comparator.comparing(
        DlCompletionTree::getNominalLevel).thenComparing(DlCompletionTree::getId);

    protected DlSatTester(TBox tbox, JFactReasonerConfiguration options) {
        this.options = options;
        tBox = tbox;
        dlHeap = tbox.getDLHeap();
        cGraph = new DlCompletionGraph(1, this);
        todo = new ToDoList(cGraph.getRareStack());
        newNodeCache = new ModelCacheIan(true, tbox.nC, tbox.nR.get(), options);
        newNodeEdges = new ModelCacheIan(false, tbox.nC, tbox.nR.get(), options);
        gcis = tbox.getGCIs();
        bContext = null;
        tryLevel = INITBRANCHINGLEVELVALUE;
        nonDetShift = 0;
        curNode = null;
        dagSize = 0;
        options.getLog().printTemplate(Templates.READCONFIG, Boolean.valueOf(options.getuseSemanticBranching()), Boolean
            .valueOf(options.getuseBackjumping()), Boolean.valueOf(options.getuseLazyBlocking()), Boolean.valueOf(
                options.getUseAnywhereBlocking()));
        if (tBox.hasFC() && options.getUseAnywhereBlocking()) {
            options.setUseAnywhereBlocking(false);
            options.getLog().print("Fairness constraints: set useAnywhereBlocking = false\n");
        }
        cGraph.initContext(tbox.getnSkipBeforeBlock(), options.getuseLazyBlocking(), options.getUseAnywhereBlocking());
        tbox.getORM().fillReflexiveRoles(reflexiveRoles);
        resetSessionFlags();
    }

    // CGraph-wide rules support
    /**
     * @param node
     *        node
     * @return true if node is valid for the reasoning
     */
    @PortedFrom(file = "Reasoner.h", name = "isNodeGloballyUsed")
    private static boolean isNodeGloballyUsed(DlCompletionTree node) {
        return !(node.isDataNode() || node.isIBlocked() || node.isPBlocked());
    }

    /**
     * @param node
     *        node
     * @return true if node is valid for the reasoning
     */
    @PortedFrom(file = "Reasoner.h", name = "isObjectNodeUnblocked")
    private static boolean isObjectNodeUnblocked(DlCompletionTree node) {
        return isNodeGloballyUsed(node) && !node.isDBlocked();
    }

    /**
     * put TODO entry for either BP or inverse(BP) in NODE's label
     * 
     * @param node
     *        node
     * @param bp
     *        bp
     */
    @PortedFrom(file = "Reasoner.h", name = "updateName")
    private void updateName(DlCompletionTree node, int bp) {
        CGLabel lab = node.label();
        ConceptWDep c = lab.getSCConceptWithBP(bp);
        if (c == null) {
            c = lab.getSCConceptWithBP(-bp);
        }
        if (c != null) {
            addExistingToDoEntry(node, c, "sp");
        }
    }

    /**
     * re-do every BP or inverse(BP) in labels of CGraph
     * 
     * @param bp
     *        bp
     */
    @PortedFrom(file = "Reasoner.h", name = "updateName")
    private void updateName(int bp) {
        cGraph.nodes().filter(DlSatTester::isNodeGloballyUsed).forEach(n -> updateName(n, bp));
    }

    /**
     * Adds ToDo entry which already exists in label of NODE. There is no need
     * to add entry to label, but it is necessary to provide offset of existing
     * concept. This is done by providing OFFSET of the concept in NODE's label
     * 
     * @param node
     *        node
     * @param c
     *        C
     * @param reason
     *        reason
     */
    @PortedFrom(file = "Reasoner.h", name = "addExistingToDoEntry")
    private void addExistingToDoEntry(DlCompletionTree node, ConceptWDep c, String reason) {
        int bp = c.getConcept();
        todo.addEntry(node, dlHeap.get(bp).getType(), c);
        logNCEntry(node, c.getConcept(), c.getDep(), "+", reason);
    }

    /**
     * add all elements from NODE label into Todo list
     * 
     * @param node
     *        node
     * @param reason
     *        reason
     */
    @PortedFrom(file = "Reasoner.h", name = "redoNodeLabel")
    private void redoNodeLabel(DlCompletionTree node, String reason) {
        CGLabel lab = node.label();
        lab.getSimpleConcepts().forEach(c -> addExistingToDoEntry(node, c, reason));
        lab.getComplexConcepts().forEach(c -> addExistingToDoEntry(node, c, reason));
    }

    /** make sure that the DAG does not grow larger than that was recorded */
    @PortedFrom(file = "Reasoner.h", name = "ensureDAGSize")
    private void ensureDAGSize() {
        if (dagSize < dlHeap.size()) {
            dagSize = dlHeap.maxSize();
        }
    }

    // -- internal cache support
    /**
     * @return cache of given completion tree (implementation)
     * @param p
     *        p
     */
    @PortedFrom(file = "Reasoner.h", name = "createModelCache")
    protected ModelCacheInterface createModelCache(DlCompletionTree p) {
        return new ModelCacheIan(dlHeap, p, encounterNominal, tBox.nC, tBox.nR.get(), options);
    }

    /**
     * check whether node may be (un)cached; save node if something is changed
     * 
     * @param node
     *        node
     * @return cahe state
     */
    @PortedFrom(file = "Reasoner.h", name = "tryCacheNode")
    private ModelCacheState tryCacheNode(DlCompletionTree node) {
        ModelCacheState ret = canBeCached(node) ? reportNodeCached(node) : ModelCacheState.FAILED;
        // node is cached if RET is csvalid
        boolean val = ret == ModelCacheState.VALID;
        if (node.isCached() != val) {
            Restorer setCached = node.setCached(val);
            cGraph.saveRareCond(setCached);
        }
        return ret;
    }

    @PortedFrom(file = "Reasoner.h", name = "applyExtraRulesIf")
    private boolean applyExtraRulesIf(Concept p) {
        if (!p.hasExtraRules()) {
            return false;
        }
        assert p.isPrimitive();
        return applyExtraRules(p);
    }

    // -- internal nominal reasoning interface
    /** @return check whether reasoning with nominals is performed */
    @PortedFrom(file = "Reasoner.h", name = "hasNominals")
    public boolean hasNominals() {
        return false;
    }

    /**
     * @return true iff current node is i-blocked (ie, no expansion necessary)
     */
    @PortedFrom(file = "Reasoner.h", name = "isIBlocked")
    private boolean isIBlocked() {
        return curNode.isIBlocked();
    }

    /**
     * @param r
     *        R
     * @param c
     *        C
     * @return true iff there is R-neighbour labelled with C
     */
    @PortedFrom(file = "Reasoner.h", name = "isSomeExists")
    private boolean isSomeExists(Role r, int c) {
        if (!used.contains(c)) {
            return false;
        }
        DlCompletionTree where = curNode.isSomeApplicable(r, c);
        if (where != null) {
            options.getLog().printTemplateMixInt(Templates.E, r.getIRI(), where.getId(), c);
        }
        return where != null;
    }

    /*
     * apply AR.C in and <= nR (if needed) in NODE's label where R is label of
     * arcSample. Set of applicable concepts is defined by redoForallFlags
     * value.
     */
    /**
     * check if branching rule was called for the 1st time
     * 
     * @return true if first call
     */
    @PortedFrom(file = "Reasoner.h", name = "isFirstBranchCall")
    private boolean isFirstBranchCall() {
        return bContext == null;
    }

    /**
     * init branching context with given rule type
     * 
     * @param c
     *        c
     */
    @PortedFrom(file = "Reasoner.h", name = "initBC")
    protected void initBC(BranchingContext c) {
        // XXX move to BranchingContext
        // save reasoning context
        c.node = curNode;
        c.concept = new ConceptWDep(curConceptConcept, curConceptDepSet);
        c.branchDep = DepSet.create(curConceptDepSet);
        // TODO check why these commented lines do not appear
        // bContext.pUsedIndex = pUsed.size();
        // bContext.nUsedIndex = nUsed.size();
        c.sgSize = sessionGCIs.size();
    }

    /** create BC for Or rule */
    @PortedFrom(file = "Reasoner.h", name = "createBCOr")
    private void createBCOr() {
        bContext = stack.pushOr();
    }

    /** create BC for NN-rule */
    @PortedFrom(file = "Reasoner.h", name = "createBCNN")
    private void createBCNN() {
        bContext = stack.pushNN();
    }

    /** create BC for LE-rule */
    @PortedFrom(file = "Reasoner.h", name = "createBCLE")
    private void createBCLE() {
        bContext = stack.pushLE();
    }

    /** create BC for Choose-rule */
    @PortedFrom(file = "Reasoner.h", name = "createBCCh")
    private void createBCCh() {
        bContext = stack.pushCh();
    }

    /**
     * check whether a node represents a functional one
     * 
     * @param v
     *        v
     * @return true if functional
     */
    @PortedFrom(file = "Reasoner.h", name = "isFunctionalVertex")
    private static boolean isFunctionalVertex(DLVertex v) {
        return v.getType() == DagTag.LE && v.getNumberLE() == 1 && v.getConceptIndex() == BP_TOP;
    }

    /**
     * check if ATLEAST and ATMOST entries are in clash. Both vertex MUST have
     * dtLE type.
     * 
     * @param atleast
     *        atleast
     * @param atmost
     *        atmost
     * @return true if clashing
     */
    @PortedFrom(file = "Reasoner.h", name = "checkNRclash")
    private static boolean checkNRclash(DLVertex atleast, DLVertex atmost) {
        // >= n R.C clash with <= m S.D iff...
        return (atmost.getConceptIndex() == BP_TOP ||
            // either D is TOP or C == D...
            atleast.getConceptIndex() == atmost.getConceptIndex()) &&
            // and n is greater than m...
            atleast.getNumberGE() > atmost.getNumberLE() &&
            // and R [= S
            atleast.getRole().lesserequal(atmost.getRole());
    }

    /**
     * quick check whether CURNODE has a clash with a given ATMOST restriction
     * 
     * @param atmost
     *        atmost
     * @return true if clash
     */
    @PortedFrom(file = "Reasoner.h", name = "isQuickClashLE")
    private boolean isQuickClashLE(DLVertex atmost) {
        // need at-least restriction
        return curNode.complexConcepts().stream().anyMatch(q -> q.getConcept() < 0 && isNRClash(dlHeap.get(q
            .getConcept()), atmost, q));
    }

    /**
     * quick check whether CURNODE has a clash with a given ATLEAST restriction
     * 
     * @param atleast
     *        atleast
     * @return true if clashing
     */
    @PortedFrom(file = "Reasoner.h", name = "isQuickClashGE")
    private boolean isQuickClashGE(DLVertex atleast) {
        // need at-most restriction
        return curNode.complexConcepts().stream().anyMatch(q -> q.getConcept() > 0 && isNRClash(atleast, dlHeap.get(q
            .getConcept()), q));
    }

    /**
     * aux method that fills the dep-set for either C or ~C found in the label;
     * 
     * @param label
     *        label
     * @param c
     *        C
     * @param d
     *        depset to be changed if a clash is found
     * @return whether C was found
     */
    @PortedFrom(file = "Reasoner.h", name = "findChooseRuleConcept")
    private boolean findChooseRuleConcept(CWDArray label, int c, @Nullable DepSet d) {
        if (c == BP_TOP) {
            return true;
        }
        if (findConceptClash(label, c, d)) {
            if (d != null) {
                d.add(clashSet);
            }
            return true;
        } else if (findConceptClash(label, -c, d)) {
            if (d != null) {
                d.add(clashSet);
            }
            return false;
        } else {
            throw new UnreachableSituationException();
        }
    }

    /**
     * check whether clash occures EDGE to TO labelled with S disjoint with R
     * 
     * @param edge
     *        edge
     * @param to
     *        to
     * @param r
     *        R
     * @param dep
     *        dep
     * @return true if clashing
     */
    @PortedFrom(file = "Reasoner.h", name = "checkDisjointRoleClash")
    private boolean checkDisjointRoleClash(DlCompletionTreeArc edge, DlCompletionTree to, Role r, DepSet dep) {
        // clash found
        if (edge.getArcEnd().equals(to) && edge.getRole().isDisjoint(r)) {
            this.setClashSet(dep);
            updateClashSet(edge.getDep());
            return true;
        }
        return false;
    }

    // support for FORALL expansion
    /**
     * Perform expansion of (\neg \ER.Self).DEP to an EDGE
     * 
     * @param edge
     *        edge
     * @param r
     *        R
     * @param dep
     *        dep
     * @return true if clashing
     */
    @PortedFrom(file = "Reasoner.h", name = "checkIrreflexivity")
    private boolean checkIrreflexivity(DlCompletionTreeArc edge, Role r, DepSet dep) {
        // only loops counts here...
        if (!edge.getArcEnd().equals(edge.getReverse().getArcEnd())) {
            return false;
        }
        // which are labelled either with R or with R-
        if (!edge.isNeighbour(r) && !edge.isNeighbour(r.inverse())) {
            return false;
        }
        // set up clash
        this.setClashSet(dep);
        updateClashSet(edge.getDep());
        return true;
    }

    /**
     * log the result of processing ACTION with entry (N,C{DEP})/REASON
     * 
     * @param n
     *        n
     * @param bp
     *        bp
     * @param dep
     *        dep
     * @param action
     *        action
     * @param reason
     *        reason
     */
    @PortedFrom(file = "Reasoner.h", name = "logNCEntry")
    private void logNCEntry(DlCompletionTree n, int bp, DepSet dep, String action, @Nullable String reason) {
        if (options.isLoggingActive()) {
            LogAdapter logAdapter = options.getLog();
            logAdapter.print(" ").print(action).print("(").print(n.logNode()).print(",").print(bp).print(dep, ")");
            if (reason != null) {
                logAdapter.print(reason);
            }
        }
    }

    /**
     * use this method in ALL dependency stuff (never use tryLevel directly)
     * 
     * @return try level
     */
    @PortedFrom(file = "Reasoner.h", name = "getCurLevel")
    private int getCurLevel() {
        return tryLevel;
    }

    /**
     * set new branching level (never use tryLevel directly)
     * 
     * @param level
     *        level
     */
    @PortedFrom(file = "Reasoner.h", name = "setCurLevel")
    private void setCurLevel(int level) {
        tryLevel = level;
    }

    /**
     * @return true if no branching ops were applied during reasoners; FIXME!!
     *         doesn't work properly with a nominal cloud
     */
    @PortedFrom(file = "Reasoner.h", name = "noBranchingOps")
    protected boolean noBranchingOps() {
        return tryLevel == INITBRANCHINGLEVELVALUE + nonDetShift;
    }

    /**
     * Get save/restore level based on either current- or DS level
     * 
     * @param ds
     *        ds
     * @return restore level
     */
    @PortedFrom(file = "Reasoner.h", name = "getSaveRestoreLevel")
    private int getSaveRestoreLevel(DepSet ds) {
        // FIXME!!! see more precise it later
        if (options.isImproveSaveRestoreDepset()) {
            return ds.level() + 1;
        } else {
            return getCurLevel();
        }
    }

    /** restore reasoning state to the latest saved position */
    @PortedFrom(file = "Reasoner.h", name = "restore")
    private void restore() {
        this.restore(getCurLevel() - 1);
    }

    /**
     * update level in N node and save it's state (if necessary)
     * 
     * @param n
     *        n
     * @param ds
     *        ds
     */
    @PortedFrom(file = "Reasoner.h", name = "updateLevel")
    private void updateLevel(DlCompletionTree n, DepSet ds) {
        cGraph.saveNode(n, getSaveRestoreLevel(ds));
    }

    /** finalize branching OP processing making deterministic op */
    @PortedFrom(file = "Reasoner.h", name = "determiniseBranchingOp")
    private void determiniseBranchingOp() {
        // clear context for the next branching op
        bContext = null;
        // remove unnecessary context from the stack
        stack.pop();
    }

    /**
     * set value of global dep-set to D
     * 
     * @param d
     *        d
     */
    @PortedFrom(file = "Reasoner.h", name = "setClashSet")
    private void setClashSet(DepSet d) {
        clashSet = d;
    }

    @PortedFrom(file = "Reasoner.h", name = "setClashSet")
    private void setClashSet(List<DepSet> depsets) {
        DepSet dep = DepSet.create();
        depsets.forEach(dep::add);
        clashSet = dep;
    }

    /**
     * add D to global dep-set
     * 
     * @param d
     *        d
     */
    @PortedFrom(file = "Reasoner.h", name = "updateClashSet")
    private void updateClashSet(DepSet d) {
        clashSet.add(d);
    }

    /**
     * get dep-set wrt current level
     * 
     * @return current depset
     */
    @PortedFrom(file = "Reasoner.h", name = "getCurDepSet")
    private DepSet getCurDepSet() {
        return DepSet.create(getCurLevel() - 1);
    }

    /** @return current branching dep-set */
    @PortedFrom(file = "Reasoner.h", name = "getBranchDep")
    private DepSet getBranchDep() {
        return bContext.branchDep;
    }

    /** prepare cumulative dep-set to usage */
    @PortedFrom(file = "Reasoner.h", name = "prepareBranchDep")
    private void prepareBranchDep() {
        getBranchDep().restrict(getCurLevel());
    }

    /** prepare cumulative dep-set and copy itto general clash-set */
    @PortedFrom(file = "Reasoner.h", name = "useBranchDep")
    private void useBranchDep() {
        prepareBranchDep();
        this.setClashSet(getBranchDep());
    }

    /**
     * re-apply all the relevant expantion rules to a given unblocked NODE
     * 
     * @param node
     *        node
     * @param direct
     *        direct
     */
    @PortedFrom(file = "Reasoner.h", name = "repeatUnblockedNode")
    public void repeatUnblockedNode(DlCompletionTree node, boolean direct) {
        if (direct) {
            // not blocked -- clear blocked cache
            // re-apply all the generating rules
            applyAllGeneratingRules(node);
        } else {
            redoNodeLabel(node, "ubi");
        }
    }

    /** @return DAG associated with it (necessary for the blocking support) */
    @PortedFrom(file = "Reasoner.h", name = "getDAG")
    public DLDag getDAG() {
        return tBox.getDLHeap();
    }

    /** @return the ROOT node of the completion graph */
    @PortedFrom(file = "Reasoner.h", name = "getRootNode")
    public DlCompletionTree getRootNode() {
        return cGraph.getRoot();
    }

    /** init Todo list priority for classification */
    @Original
    public void initToDoPriorities() {
        String iaoeflg = options.getIAOEFLG();
        // inform about used rules order
        options.getLog().print("\nInit IAOEFLG = ", iaoeflg);
        todo.initPriorities(iaoeflg);
    }

    /**
     * set blocking method for a session
     * 
     * @param hasInverse
     *        hasInverse
     * @param hasQCR
     *        hasQCR
     */
    @PortedFrom(file = "Reasoner.h", name = "setBlockingMethod")
    public void setBlockingMethod(boolean hasInverse, boolean hasQCR) {
        cGraph.setBlockingMethod(hasInverse, hasQCR);
    }

    /**
     * @param sat
     *        sat
     * @return create model cache for the just-classified entry
     */
    @PortedFrom(file = "Reasoner.h", name = "buildCacheByCGraph")
    public ModelCacheInterface buildCacheByCGraph(boolean sat) {
        if (sat) {
            // here we need actual (not a p-blocked) root of the tree
            return createModelCache(getRootNode());
        } else {
            // unsat => cache is just bottom
            return ModelCacheConst.createConstCache(BP_BOTTOM);
        }
    }

    /**
     * @param o
     *        o
     */
    @PortedFrom(file = "Reasoner.h", name = "writeTotalStatistic")
    public void writeTotalStatistic(LogAdapter o) {
        if (options.isUseReasoningStatistics()) {
            // ensure that the last reasoning results are in
            stats.accumulate();
            stats.logStatisticData(o, false, cGraph, options);
        }
        o.print("\n");
    }

    /**
     * @param p
     *        p
     * @param f
     *        f
     * @return new cache
     */
    @PortedFrom(file = "Reasoner.h", name = "createCache")
    public ModelCacheInterface createCache(int p, FastSet f) {
        assert isValid(p);
        ModelCacheInterface cache = dlHeap.getCache(p);
        if (cache != null) {
            return cache;
        }
        if (!tBox.testHasTopRole()) {
            prepareCascadedCache(p, f);
        }
        cache = dlHeap.getCache(p);
        if (cache != null) {
            return cache;
        }
        cache = buildCache(p);
        dlHeap.setCache(p, cache);
        return cache;
    }

    @PortedFrom(file = "Reasoner.h", name = "prepareCascadedCache")
    private void prepareCascadedCache(int p, FastSet f) {
        // cycle found -- shall be processed without caching
        if (inProcess.contains(p)) {
            return;
        }
        // XXX check what's in f and when it's reset
        if (f.contains(p)) {
            return;
        }
        DLVertex v = dlHeap.get(p);
        boolean pos = p > 0;
        // check if a concept already cached
        if (v.getCache(pos) != null) {
            return;
        }
        DagTag type = v.getType();
        if (handlecollection.contains(type)) {
            for (int q : v.begin()) {
                int p2 = pos ? q : -q;
                inProcess.add(p2);
                prepareCascadedCache(p2, f);
                inProcess.remove(p2);
            }
        } else if (handlesingleton.contains(type)) {
            if (!pos && type.isPNameTag()) {
                return;
            }
            inProcess.add(p);
            prepareCascadedCache(pos ? v.getConceptIndex() : -v.getConceptIndex(), f);
            inProcess.remove(p);
        } else if (handleforallle.contains(type)) {
            Role r = v.getRole();
            // skip data-related stuff
            // no need to cache top-role stuff
            if (!r.isDataRole() && !r.isTop()) {
                int x = pos ? v.getConceptIndex() : -v.getConceptIndex();
                // build cache for C in \AR.C
                if (x != BP_TOP) {
                    inProcess.add(x);
                    createCache(x, f);
                    inProcess.remove(x);
                }
                x = r.getBPRange();
                if (x != BP_TOP) {
                    inProcess.add(x);
                    createCache(x, f);
                    inProcess.remove(x);
                }
            }
        }
        // dttop, dtsplit, etc: do nothing
        f.add(p);
    }

    @PortedFrom(file = "Reasoner.h", name = "buildCache")
    private ModelCacheInterface buildCache(int p) {
        LogAdapter logAdapter = options.getLog();
        if (options.isLoggingActive()) {
            logAdapter.print("\nChecking satisfiability of DAG entry ").print(p);
            tBox.printDagEntry(logAdapter, p);
            logAdapter.print(":\n");
        }
        boolean sat = this.runSat(p, BP_TOP);
        if (!sat) {
            logAdapter.printTemplateInt(Templates.BUILD_CACHE_UNSAT, p);
        }
        return buildCacheByCGraph(sat);
    }

    // flags section
    @PortedFrom(file = "Reasoner.h", name = "resetSessionFlags")
    protected void resetSessionFlags() {
        // reflect possible change of DAG size
        ensureDAGSize();
        used.add(BP_TOP);
        used.add(BP_BOTTOM);
        encounterNominal = false;
        checkDataNode = true;
    }

    @PortedFrom(file = "Reasoner.h", name = "initNewNode")
    protected boolean initNewNode(DlCompletionTree node, DepSet dep, int c) {
        if (node.isDataNode()) {
            checkDataNode = false;
        }
        node.setInit(c);
        if (addToDoEntry(node, c, dep, null)) {
            return true;
        }
        if (node.isDataNode()) {
            return false;
        }
        if (addToDoEntry(node, tBox.getTG(), dep, null)) {
            return true;
        }
        if (gcis.isReflexive() && applyReflexiveRoles(node, dep)) {
            return true;
        }
        for (int i : sessionGCIs.toArray()) {
            if (addToDoEntry(node, i, dep, "sg")) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param p
     *        p
     * @param q
     *        q
     * @return true if satisfiable
     */
    @PortedFrom(file = "Reasoner.h", name = "runSat")
    public boolean runSat(int p, int q) {
        prepareReasoner();
        // use general method to init node with P and add Q then
        if (initNewNode(cGraph.getRoot(), DepSet.create(), p) || addToDoEntry(cGraph.getRoot(), q, DepSet.create(),
            null)) {
            // concept[s] unsatisfiable
            return false;
        }
        // check satisfiability explicitly
        Timer timer = q == BP_TOP ? satTimer : subTimer;
        timer.start();
        boolean result = this.runSat();
        timer.stop();
        return result;
    }

    /**
     * @param r
     *        R
     * @param s
     *        S
     * @return true if not satisfiable
     */
    @PortedFrom(file = "Reasoner.h", name = "checkDisjointRoles")
    public boolean checkDisjointRoles(Role r, Role s) {
        prepareReasoner();
        // use general method to init node...
        DepSet dummy = DepSet.create();
        if (initNewNode(cGraph.getRoot(), dummy, BP_TOP)) {
            return true;
        }
        // ... add edges with R and S...
        curNode = cGraph.getRoot();
        DlCompletionTreeArc edgeR = this.createOneNeighbour(r, dummy);
        DlCompletionTreeArc edgeS = this.createOneNeighbour(s, dummy);
        // init new nodes/edges. No need to apply restrictions, as no reasoning
        // have been done yet.
        if (initNewNode(edgeR.getArcEnd(), dummy, BP_TOP) || initNewNode(edgeS.getArcEnd(), dummy, BP_TOP) || setupEdge(
            edgeR, dummy, 0) || setupEdge(edgeS, dummy, 0) || merge(edgeS.getArcEnd(), edgeR.getArcEnd(), dummy)) {
            return true;
        }
        // 2 roles are disjoint if current setting is unsatisfiable
        curNode = null;
        return !this.runSat();
    }

    /**
     * @param r
     *        R
     * @return true if not satisfiable
     */
    @PortedFrom(file = "Reasoner.h", name = "checkIrreflexivity")
    public boolean checkIrreflexivity(Role r) {
        prepareReasoner();
        // use general method to init node...
        DepSet dummy = DepSet.create();
        if (initNewNode(cGraph.getRoot(), dummy, BP_TOP)) {
            return true;
        }
        // ... add an R-loop
        curNode = cGraph.getRoot();
        DlCompletionTreeArc edgeR = this.createOneNeighbour(r, dummy);
        // init new nodes/edges. No need to apply restrictions, as no reasoning
        // have been done yet.
        if (initNewNode(edgeR.getArcEnd(), dummy, BP_TOP) || setupEdge(edgeR, dummy, 0) || merge(edgeR.getArcEnd(),
            cGraph.getRoot(), dummy)) {
            return true;
        }
        // R is irreflexive if current setting is unsatisfiable
        curNode = null;
        return !this.runSat();
    }

    // restore implementation
    @PortedFrom(file = "Reasoner.h", name = "backJumpedRestore")
    private boolean backJumpedRestore() {
        // if empty clash dep-set -- concept is unsatisfiable
        if (clashSet == null || clashSet.isEmpty()) {
            return true;
        }
        // some non-deterministic choices were done
        this.restore(clashSet.level());
        return false;
    }

    /** update cumulative branch-dep with current clash-set and move options forward*/
    @PortedFrom(file = "Reasoner.h", name = "nextBranchingOption")
    private void nextBranchingOption () {
        getBranchDep().add(clashSet);
        bContext.nextOption();
    }

    @PortedFrom(file = "Reasoner.h", name = "straightforwardRestore")
    private boolean straightforwardRestore() {
        if (noBranchingOps()) {
            // ... the concept is unsatisfiable
            return true;
        } else {
            // restoring the state
            this.restore();
            return false;
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "tunedRestore")
    private boolean tunedRestore() {
        if (options.getuseBackjumping()) {
            return backJumpedRestore();
        } else {
            return straightforwardRestore();
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyAll")
    private boolean commonTacticBodyAll(DLVertex cur) {
        assert curConceptConcept > 0 && cur.getType() == FORALL;
        if (cur.getRole().isTop()) {
            stats.getnAllCalls().inc();
            return addSessionGCI(cur.getConceptIndex(), curConceptDepSet);
        }
        // can't skip singleton models for complex roles due to empty
        // transitions
        if (cur.getRole().isSimple()) {
            return commonTacticBodyAllSimple(cur);
        } else {
            return commonTacticBodyAllComplex(cur);
        }
    }

    /**
     * add C to a set of session GCIs; init all nodes with (C,dep)
     * 
     * @param c
     *        C
     * @param dep
     *        dep
     * @return true if node existed
     */
    @PortedFrom(file = "Tactic.cpp", name = "addSessionGCI")
    private boolean addSessionGCI(int c, DepSet dep) {
        sessionGCIs.add(c);
        return cGraph.nodes().anyMatch(node -> isNodeGloballyUsed(node) && addToDoEntry(node, c, dep, "sg"));
    }

    /** @return configuration options */
    @Original
    public JFactReasonerConfiguration getOptions() {
        return options;
    }

    @PortedFrom(file = "Reasoner.h", name = "prepareReasoner")
    protected void prepareReasoner() {
        cGraph.clear();
        stack.clear();
        todo.clear();
        used.clear();
        sessionGCIs.clear();
        curNode = null;
        bContext = null;
        tryLevel = INITBRANCHINGLEVELVALUE;
        // clear last session information
        resetSessionFlags();
    }

    /**
     * try to add a concept to a label given by TAG; ~C can't appear in the
     * label
     * 
     * @param label
     *        label
     * @param p
     *        p
     * @return true if label contains p
     */
    @PortedFrom(file = "Reasoner.h", name = "findConcept")
    public boolean findConcept(CWDArray label, int p) {
        assert isCorrect(p); // sanity checking
        // constants are not allowed here
        assert p != BP_TOP;
        assert p != BP_BOTTOM;
        stats.getnLookups().inc();
        return label.contains(p);
    }

    @PortedFrom(file = "Reasoner.h", name = "checkAddedConcept")
    private AddConceptResult checkAddedConcept(CWDArray lab, int p, @Nullable DepSet dep) {
        assert isCorrect(p); // sanity checking
        // constants are not allowed here
        if (findConcept(lab, p)) {
            return AddConceptResult.EXIST;
        }
        if (findConceptClash(lab, -p, dep)) {
            return AddConceptResult.CLASH;
        }
        return AddConceptResult.DONE;
    }

    /**
     * try to add a concept to a label given by TAG; ~C can't appear in the
     * label; setup clash-set if found
     * 
     * @param lab
     *        lab
     * @param bp
     *        bp
     * @param dep
     *        dep
     * @return true if clashing
     */
    @PortedFrom(file = "Reasoner.h", name = "findConceptClash")
    private boolean findConceptClash(CWDArray lab, int bp, @Nullable DepSet dep) {
        // sanity checking
        assert isCorrect(bp);
        // constants are not allowed here
        assert bp != BP_TOP;
        assert bp != BP_BOTTOM;
        stats.getnLookups().inc();
        DepSet depset = lab.get(bp);
        if (depset != null) {
            clashSet = DepSet.plus(depset, dep);
            return true;
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "tryAddConcept")
    private AddConceptResult tryAddConcept(CWDArray lab, int bp, @Nullable DepSet dep) {
        // check whether C or ~C can occurs in a node label
        boolean canC = used.contains(bp);
        boolean canNegC = used.contains(-bp);
        // if either C or ~C is used already, it's not new in a label
        if (canC) {
            if (canNegC) {
                // both C and ~C can be in the label
                return checkAddedConcept(lab, bp, dep);
            } else {
                // C but not ~C can be in the label
                stats.getnLookups().inc();
                return findConcept(lab, bp) ? AddConceptResult.EXIST : AddConceptResult.DONE;
            }
        } else {
            if (canNegC) {
                // ~C but not C can be in the label
                return findConceptClash(lab, -bp, dep) ? AddConceptResult.CLASH : AddConceptResult.DONE;
            } else {
                // neither C nor ~C can be in the label
                return AddConceptResult.DONE;
            }
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "addToDoEntry")
    protected boolean addToDoEntry(DlCompletionTree n, int bp, DepSet dep, @Nullable String reason) {
        if (bp == BP_TOP) {
            return false;
        }
        if (bp == BP_BOTTOM) {
            this.setClashSet(dep);
            logNCEntry(n, bp, dep, "x", dlHeap.get(bp).getType().getName());
            return true;
        }
        DLVertex v = dlHeap.get(bp);
        DagTag tag = v.getType();
        // try to add a concept to a node label
        switch (tryAddConcept(n.label().getLabel(tag.isComplexConcept()), bp, dep)) {
            case CLASH:
                // clash -- return
                logNCEntry(n, bp, dep, "x", dlHeap.get(bp).getType().getName());
                return true;
            case EXIST:
                // already exists -- nothing new
                return false;
            case DONE:
                // try was done
                return insertToDoEntry(n, bp, dep, tag, reason);
            default:
                // safety check
                throw new UnreachableSituationException();
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "insertToDoEntry")
    private boolean insertToDoEntry(DlCompletionTree n, int bp, DepSet dep, DagTag tag, @Nullable String reason) {
        ConceptWDep p = new ConceptWDep(bp, dep);
        // we will change current Node => save it if necessary
        updateLevel(n, dep);
        cGraph.addConceptToNode(n, p, tag.isComplexConcept());
        used.add(bp);
        if (n.isCached()) {
            return correctCachedEntry(n);
        }
        // add new info in TODO list
        todo.addEntry(n, tag, p);
        // data concept -- run data center for it
        if (n.isDataNode()) {
            return checkDataNode ? hasDataClash(n) : false;
        }
        logNCEntry(n, bp, dep, "+", reason);
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "canBeCached")
    private boolean canBeCached(DlCompletionTree node) {
        // check whether node cache is allowed
        if (!options.isUseNodeCache()) {
            return false;
        }
        // nominal nodes can not be cached
        if (node.isNominalNode()) {
            return false;
        }
        stats.getnCacheTry().inc();
        // check applicability of the caching
        AtomicBoolean shallow = new AtomicBoolean(true);
        AtomicInteger size = new AtomicInteger(0);
        if (node.simpleConcepts().stream().anyMatch(p -> canBeCachedCheck(shallow, size, p))) {
            return false;
        }
        if (node.complexConcepts().stream().anyMatch(p -> canBeCachedCheck(shallow, size, p))) {
            return false;
        }
        // it's useless to cache shallow nodes
        if (shallow.get() && size.get() > 0) {
            stats.getnCacheFailedShallow().inc();
            options.getLog().print(" cf(s)");
            return false;
        }
        return true;
    }

    protected boolean canBeCachedCheck(AtomicBoolean shallow, AtomicInteger size, ConceptWDep p) {
        if (dlHeap.getCache(p.getConcept()) == null) {
            stats.getnCacheFailedNoCache().inc();
            options.getLog().printTemplateInt(Templates.CAN_BE_CACHED, p.getConcept());
            return true;
        }
        shallow.compareAndSet(true, dlHeap.getCache(p.getConcept()).shallowCache());
        size.incrementAndGet();
        return false;
    }

    /**
     * build cache of the node (it is known that caching is possible) in
     * newNodeCache
     * 
     * @param node
     *        node
     */
    @PortedFrom(file = "Reasoner.h", name = "doCacheNode")
    private void doCacheNode(DlCompletionTree node) {
        List<DepSet> deps = new ArrayList<>();
        newNodeCache.clear();
        if (Stream.concat(node.simpleConcepts().stream(), node.complexConcepts().stream()).anyMatch(
            p -> doCacheNodeCheck(deps, p))) {
            // all concepts in label are mergable; now try to add input arc
            newNodeEdges.clear();
            newNodeEdges.initRolesFromArcs(node);
            newNodeCache.merge(newNodeEdges);
        }
    }

    protected boolean doCacheNodeCheck(List<DepSet> deps, ConceptWDep p) {
        deps.add(p.getDep());
        ModelCacheState merge = newNodeCache.merge(dlHeap.getCache(p.getConcept()));
        if (merge != ModelCacheState.VALID) {
            if (merge == ModelCacheState.INVALID) {
                this.setClashSet(deps);
            }
            return false;
        }
        return true;
    }

    @PortedFrom(file = "Reasoner.h", name = "reportNodeCached")
    private ModelCacheState reportNodeCached(DlCompletionTree node) {
        doCacheNode(node);
        ModelCacheState status = newNodeCache.getState();
        switch (status) {
            case VALID:
                stats.getnCachedSat().inc();
                options.getLog().printTemplateInt(Templates.REPORT1, node.getId());
                break;
            case INVALID:
                stats.getnCachedUnsat().inc();
                break;
            case FAILED:
            case UNKNOWN:
                stats.getnCacheFailed().inc();
                options.getLog().print(" cf(c)");
                status = ModelCacheState.FAILED;
                break;
            default:
                throw new UnreachableSituationException();
        }
        return status;
    }

    @PortedFrom(file = "Reasoner.h", name = "correctCachedEntry")
    private boolean correctCachedEntry(DlCompletionTree n) {
        assert n.isCached();
        ModelCacheState status = tryCacheNode(n);
        if (status == ModelCacheState.FAILED) {
            redoNodeLabel(n, "uc");
        }
        return status.usageByState();
    }

    static class DataCall {

        DagTag d;
        NamedEntry dataEntry;
        boolean positive;
        ConceptWDep r;

        @Override
        public int hashCode() {
            return ((positive ? 1 : 2) * 37 + d.hashCode()) * 37 + dataEntry.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == null) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof DataCall)) {
                return false;
            }
            DataCall o = (DataCall) obj;
            return positive == o.positive && d.equals(o.d) && dataEntry.equals(o.dataEntry);
        }

        @Override
        public String toString() {
            Object o;
            if (dataEntry instanceof DatatypeEntry) {
                o = ((DatatypeEntry) dataEntry).getDatatype();
            } else if (dataEntry instanceof LiteralEntry) {
                o = ((LiteralEntry) dataEntry).getLiteral();
            } else {
                o = dataEntry;
            }
            return positive + ", " + d + ", \"" + o.toString().replace("\"", "\\\"") + "\", " + r.getDep().toString()
                .replace("{", "").replace("}", "");
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "hasDataClash")
    private boolean hasDataClash(DlCompletionTree node) {
        assert node != null && node.isDataNode();
        DataTypeReasoner datatypeReasoner = new DataTypeReasoner(options);
        Set<DataCall> calls = new LinkedHashSet<>();
        node.simpleConcepts().forEach(r -> {
            DagTag d = dlHeap.get(r.getConcept()).getType();
            NamedEntry dataEntry = dlHeap.get(r.getConcept()).getConcept();
            boolean positive = r.getConcept() > 0;
            if (dataEntry != null) {
                DataCall dc = new DataCall();
                dc.d = d;
                dc.positive = positive;
                dc.dataEntry = dataEntry;
                dc.r = r;
                calls.add(dc);
            }
        });
        for (DataCall dc : calls) {
            if (datatypeReasoner.addDataEntry(dc.positive, dc.d, dc.dataEntry, dc.r.getDep())) {
                this.setClashSet(datatypeReasoner.getClashSet());
                return true;
            }
        }
        boolean checkClash = datatypeReasoner.checkClash();
        if (checkClash) {
            this.setClashSet(datatypeReasoner.getClashSet());
        }
        return checkClash;
    }

    @PortedFrom(file = "Reasoner.h", name = "runSat")
    protected boolean runSat() {
        testTimer.start();
        boolean result = checkSatisfiability();
        testTimer.stop();
        options.getLog().print("\nChecking time was ").print(testTimer.getResultTime()).print(" milliseconds");
        testTimer.reset();
        finaliseStatistic();
        if (result && options.getLog().isEnabled()) {
            cGraph.print(options.getLog());
        }
        return result;
    }

    @PortedFrom(file = "Reasoner.h", name = "finaliseStatistic")
    private void finaliseStatistic() {
        if (options.isUseReasoningStatistics()) {
            writeTotalStatistic(options.getLog());
        }
        cGraph.clearStatistics();
    }

    @PortedFrom(file = "Reasoner.h", name = "applyReflexiveRoles")
    private boolean applyReflexiveRoles(DlCompletionTree node, DepSet dep) {
        return reflexiveRoles.stream().map(r -> cGraph.addRoleLabel(node, node, false, r, dep)).anyMatch(p -> setupEdge(
            p, dep, 0));
    }

    @PortedFrom(file = "Reasoner.h", name = "checkSatisfiability")
    protected boolean checkSatisfiability() {
        int loop = 0;
        for (;;) {
            if (curNode == null) {
                // no applicable rules
                if (todo.isEmpty()) {
                    // do run-once things
                    if (performAfterReasoning() && tunedRestore()) {
                        // clash found
                        // no more alternatives
                        return false;
                    }
                    // if nothing added -- that's it
                    if (todo.isEmpty()) {
                        return true;
                    }
                }
                ToDoEntry curTDE = todo.getNextEntry();
                assert curTDE != null;
                curNode = curTDE.getNode();
                curConceptConcept = curTDE.getOffsetConcept();
                curConceptDepSet = DepSet.create(curTDE.getOffsetDepSet());
            }
            if (++loop == 50) {
                loop = 0;
                if (tBox.isCancelled().get()) {
                    return false;
                }
                if (testTimer.calcDelta() >= options.getTimeOut()) {
                    throw new TimeOutException();
                }
            }
            // here curNode/curConcept are set
            if (commonTactic()) {
                // clash found
                if (tunedRestore()) {
                    // the concept is unsatisfiable
                    return false;
                }
            } else {
                curNode = null;
            }
        }
    }

    /**
     * perform all the actions that should be done once, after all normal rules
     * are not applicable.
     * 
     * @return true if the concept is unsat
     */
    @PortedFrom(file = "Reasoner.h", name = "performAfterReasoning")
    private boolean performAfterReasoning() {
        // make sure all blocked nodes are still blocked
        logIndentation();
        options.getLog().print("ub:");
        cGraph.retestCGBlockedStatus();
        options.getLog().print("]");
        if (!todo.isEmpty()) {
            return false;
        }
        // check fairness constraints
        if (options.isUseFairness() && tBox.hasFC()) {
            // for every given FC, if it is violated, reject current model
            for (Concept p : tBox.getFairness()) {
                DlCompletionTree violator = cGraph.getFCViolator(p.getpName());
                if (violator != null) {
                    stats.getnFairnessViolations().inc();
                    // try to fix violators
                    if (addToDoEntry(violator, p.getpName(), getCurDepSet(), "fair")) {
                        return true;
                    }
                }
            }
            if (!todo.isEmpty()) {
                return false;
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "restoreBC")
    private void restoreBC() {
        curNode = bContext.node;
        if (bContext.concept == null) {
            curConceptConcept = BP_INVALID;
            curConceptDepSet = DepSet.create();
        } else {
            curConceptConcept = bContext.concept.getConcept();
            curConceptDepSet = DepSet.create(bContext.concept.getDep());
        }
        if (!sessionGCIs.isEmpty()) {
            resize(sessionGCIs, bContext.sgSize);
        }
        // we here after the clash so choose the next branching option
        nextBranchingOption();
    }

    private static void resize(TIntList l, int n) {
        if (l.size() > n) {
            while (l.size() > n) {
                l.remove(l.size() - 1);
            }
        } else {
            while (l.size() < n) {
                l.add(null);
            }
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "save")
    protected void save() {
        // save tree
        cGraph.save();
        // save ToDoList
        todo.save();
        // increase tryLevel
        ++tryLevel;
        // init BC
        bContext = null;
        stats.getnStateSaves().inc();
        options.getLog().printTemplateInt(Templates.SAVE, getCurLevel() - 1);
        if (options.isDebugSaveRestore()) {
            cGraph.print(options.getLog());
            options.getLog().print(todo);
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "restore")
    protected void restore(int newTryLevel) {
        assert !stack.isEmpty();
        assert newTryLevel > 0;
        // skip all intermediate restores
        setCurLevel(newTryLevel);
        // restore local
        bContext = stack.top(getCurLevel());
        restoreBC();
        // restore tree
        cGraph.restore(getCurLevel());
        // restore TO DO list
        todo.restore(getCurLevel());
        stats.getnStateRestores().inc();
        options.getLog().printTemplateInt(Templates.RESTORE, getCurLevel());
        if (options.isDebugSaveRestore()) {
            cGraph.print(options.getLog());
            options.getLog().print(todo);
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "logIndentation")
    private void logIndentation() {
        LogAdapter logAdapter = options.getLog();
        logAdapter.print("\n");
        IntStream.range(1, getCurLevel()).forEach(i -> logAdapter.print(' '));
        logAdapter.print('[');
    }

    @PortedFrom(file = "Reasoner.h", name = "logStartEntry")
    private void logStartEntry() {
        if (options.isLoggingActive()) {
            logIndentation();
            LogAdapter logAdapter = options.getLog();
            logAdapter.print("(");
            logAdapter.print(curNode.logNode());
            logAdapter.print(",");
            logAdapter.print(curConceptConcept);
            if (curConceptDepSet != null) {
                logAdapter.print(curConceptDepSet);
            }
            logAdapter.print("){");
            if (curConceptConcept < 0) {
                logAdapter.print("~");
            }
            DLVertex v = dlHeap.get(curConceptConcept);
            logAdapter.print(v.getType());
            if (v.getConcept() != null) {
                logAdapter.print("(", v.getConcept().getIRI(), ")");
            }
            logAdapter.print("}:");
            logAdapter.print("}:");
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "logFinishEntry")
    private void logFinishEntry(boolean res) {
        if (options.isLoggingActive()) {
            options.getLog().print("]");
            if (res) {
                options.getLog().printTemplate(Templates.LOG_FINISH_ENTRY, clashSet);
            }
        }
    }

    /**
     * @param o
     *        o
     * @return reasoning time
     */
    @PortedFrom(file = "Reasoner.h", name = "printReasoningTime")
    public float printReasoningTime(LogAdapter o) {
        o.print("\n     SAT takes ", satTimer, " seconds\n     SUB takes ", subTimer, " seconds");
        return satTimer.calcDelta() + subTimer.calcDelta();
    }

    /*
     * Tactics section; Each Tactic should have a (small) Usability function
     * <name> and a Real tactic function <name>Body Each tactic returns: - true
     * - if expansion of CUR lead to clash - false - overwise
     */
    @PortedFrom(file = "Reasoner.h", name = "commonTactic")
    private boolean commonTactic() {
        // check if Node is cached and we tries to expand existing result
        // also don't do anything for p-blocked nodes (can't be unblocked)
        if (curNode.isCached() || curNode.isPBlocked()) {
            return false;
        }
        // informs about starting calculations...
        logStartEntry();
        boolean ret = false;
        // apply tactic only if Node is not an i-blocked
        if (!isIBlocked()) {
            ret = commonTacticBody(dlHeap.get(curConceptConcept));
        }
        logFinishEntry(ret);
        return ret;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBody")
    @SuppressWarnings("incomplete-switch")
    private boolean commonTacticBody(DLVertex cur) {
        stats.getnTacticCalls().inc();
        switch (cur.getType()) {
            case TOP:
                // can't appear here; addToDoEntry deals with constants
                throw new UnreachableSituationException();
            case DATATYPE:
            case DATAVALUE:
                // data things are checked by data inferer
                stats.getnUseless().inc();
                return false;
            case PSINGLETON:
            case NSINGLETON:
                if (curConceptConcept > 0) {
                    // real singleton
                    return commonTacticBodySingleton(cur);
                }
                // negated singleton -- nothing to do with.
                return commonTacticBodyId(cur);
            case NCONCEPT:
            case PCONCEPT:
                return commonTacticBodyId(cur);
            case AND:
                if (curConceptConcept > 0) {
                    // this is AND vertex
                    return commonTacticBodyAnd(cur);
                }
                // this is OR vertex
                return commonTacticBodyOr(cur);
            case FORALL:
                if (curConceptConcept < 0) {
                    // SOME vertex
                    return commonTacticBodySome(cur);
                }
                // ALL vertex
                return commonTacticBodyAll(cur);
            case IRR:
                if (curConceptConcept < 0) {
                    // SOME R.Self vertex
                    return commonTacticBodySomeSelf(cur.getRole());
                }
                // don't need invalidate cache, as IRREFL can only lead to CLASH
                return commonTacticBodyIrrefl(cur.getRole());
            case LE:
                if (curConceptConcept < 0) {
                    // >= vertex
                    return commonTacticBodyGE(cur);
                }
                // <= vertex
                if (isFunctionalVertex(cur)) {
                    return commonTacticBodyFunc(cur);
                }
                return commonTacticBodyLE(cur);
            case PROJ:
                assert curConceptConcept > 0;
                return commonTacticBodyProj(cur.getRole(), cur.getConceptIndex(), cur.getProjRole());
            case CHOOSE:
                assert curConceptConcept > 0;
                return applyChooseRule(curNode, cur.getConceptIndex());
            default:
                throw new UnreachableSituationException();
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyId")
    private boolean commonTacticBodyId(DLVertex cur) {
        assert cur.getType().isCNameTag(); // safety check
        stats.getnIdCalls().inc();
        // check if we have some simple rules
        if (options.isUseSimpleRules() && curConceptConcept > 0 && applyExtraRulesIf((Concept) cur.getConcept())) {
            return true;
        }
        // get either body(p) or inverse(body(p)), depends on sign of current ID
        int c = curConceptConcept > 0 ? cur.getConceptIndex() : -cur.getConceptIndex();
        return addToDoEntry(curNode, c, curConceptDepSet, null);
    }

    /**
     * add entity.dep to a session structures
     * 
     * @param entity
     *        entity
     * @param dep
     *        dep
     */
    @PortedFrom(file = "Reasoner.h", name = "updateSessionSignature")
    private void updateSessionSignature(@Nullable NamedEntity entity, @Nullable DepSet dep) {
        if (entity != null) {
            sessionSignature.add(entity);
            sessionSigDepSet.get(entity).add(dep);
        }
    }

    /** update session signature for all non-data nodes */
    @PortedFrom(file = "Reasoner.h", name = "updateSessionSignature")
    private void updateSessionSignature() {
        int n = 0;
        DlCompletionTree node = cGraph.getNode(n++);
        while (node != null) {
            node = cGraph.getNode(n++);
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "applicable")
    protected boolean applicable(SimpleRule rule) {
        CWDArray lab = curNode.label().getLabel(false);
        // dep-set to keep track for all the concepts in a rule-head
        DepSet loc = null;
        for (Concept p : rule.getBody()) {
            if (p.getpName() != curConceptConcept) {
                // FIXME!! double check that's correct (no need to negate pName)
                if (findConceptClash(lab, p.getpName(), loc == null ? curConceptDepSet : loc)) {
                    // such a concept exists -- rememeber clash set
                    if (loc == null) {
                        loc = DepSet.create(clashSet);
                    } else {
                        loc.add(clashSet);
                    }
                } else {
                    // no such concept -- can not fire a rule
                    return false;
                }
            }
        }
        // rule will be fired -- set the dep-set
        this.setClashSet(loc == null ? curConceptDepSet : loc);
        return true;
    }

    @PortedFrom(file = "Tactic.cpp", name = "applyExtraRules")
    private boolean applyExtraRules(Concept c) {
        FastSet erBegin = c.getExtraRules();
        for (int i = 0; i < erBegin.size(); i++) {
            SimpleRule rule = tBox.getSimpleRule(erBegin.get(i));
            stats.getnSRuleAdd().inc();
            if (applicable(rule)) {
                // apply the rule's head
                stats.getnSRuleFire().inc();
                if (addToDoEntry(curNode, rule.getBpHead(), clashSet, null)) {
                    return true;
                }
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodySingleton")
    private boolean commonTacticBodySingleton(DLVertex cur) {
        // safety check
        assert cur.getType() == PSINGLETON || cur.getType() == NSINGLETON;
        stats.getnSingletonCalls().inc();
        // can use this rule only in the Nominal reasoner
        assert hasNominals();
        // if the test REALLY uses nominals, remember this
        encounterNominal = true;
        Individual c = (Individual) cur.getConcept();
        assert c != null && c.getNode() != null;
        // if node for C was purged due to merge -- find proper one
        DepSet dep = DepSet.create(curConceptDepSet);
        // blank nodes are set to be non classifiable and not initialized in
        // initNominalCloud
        if (c.isNonClassifiable()) {
            return true;
        }
        DlCompletionTree realNode = c.getNode().resolvePBlocker(dep);
        // check if o-rule is applicable
        if (!realNode.equals(curNode)) {
            // apply o-rule: merge 2 nodes
            // don't need to actually expand P: it was/will be done in C.node
            return merge(curNode, realNode, dep);
        }
        // singleton behaves as a general named concepts besides nominal cloud
        return commonTacticBodyId(cur);
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyAnd")
    private boolean commonTacticBodyAnd(DLVertex cur) {
        assert curConceptConcept > 0 && cur.getType() == AND; // safety check
        stats.getnAndCalls().inc();
        for (int q : cur.begin()) {
            if (addToDoEntry(curNode, q, curConceptDepSet, null)) {
                return true;
            }
        }
        return false;
    }

    /**
     * for C \or D concepts
     * 
     * @param cur
     *        current vertex true if clash happens
     * @return true if clash occurs
     */
    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyOr")
    private boolean commonTacticBodyOr(DLVertex cur) {
        // safety check
        assert curConceptConcept < 0 && cur.getType() == AND;
        stats.getnOrCalls().inc();
        if (isFirstBranchCall()) {
            // check the structure of OR operation (number of applicable
            // concepts)
            Reference<DepSet> dep = new Reference<>(DepSet.create());
            if (planOrProcessing(cur, dep)) {
                // found existing component
                options.getLog().printTemplate(Templates.COMMON_TACTIC_BODY_OR, orConceptsToTest.get(orConceptsToTest
                    .size() - 1));
                return false;
            }
            if (orConceptsToTest.isEmpty()) {
                // no more applicable concepts:
                // set global dep-set using accumulated deps
                this.setClashSet(dep.getReference());
                return true;
            }
            // not a branching: just add a single concept
            if (orConceptsToTest.size() == 1) {
                ConceptWDep c = orConceptsToTest.get(0);
                return insertToDoEntry(curNode, c.getConcept(), dep.getReference(), dlHeap.get(c.getConcept())
                    .getType(), "bcp");
            }
            // more than one alternative: use branching context
            createBCOr();
            bContext.branchDep = DepSet.create(dep.getReference());
            orConceptsToTest = ((BCOr) bContext).setApplicableOrEntries(orConceptsToTest);
        }
        // now it is OR case with 1 or more applicable concepts
        return processOrEntry();
    }

    @PortedFrom(file = "Reasoner.h", name = "planOrProcessing")
    private boolean planOrProcessing(DLVertex cur, Reference<DepSet> dep) {
        orConceptsToTest.clear();
        dep.setReference(DepSet.create(curConceptDepSet));
        // check all OR components for the clash
        CGLabel lab = curNode.label();
        for (int q : cur.begin()) {
            int inverse = -q;
            switch (tryAddConcept(lab.getLabel(dlHeap.get(inverse).getType().isComplexConcept()), inverse, null)) {
                case CLASH:
                    // clash found -- OK
                    dep.getReference().add(clashSet);
                    continue;
                case EXIST:
                    // already have such concept -- save it to the 1st position
                    orConceptsToTest.clear();
                    orConceptsToTest.add(new ConceptWDep(-q));
                    return true;
                case DONE:
                    orConceptsToTest.add(new ConceptWDep(-q));
                    continue;
                default:
                    // safety check
                    throw new UnreachableSituationException();
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "processOrEntry")
    private boolean processOrEntry() {
        // save the context here as after save() it would be lost
        BCOr bcOr = (BCOr) bContext;
        String reason = null;
        DepSet dep;
        if (bcOr.isLastOrEntry()) {
            // cumulative dep-set will be used
            prepareBranchDep();
            dep = getBranchDep();
            // no more branching decisions
            determiniseBranchingOp();
            reason = "bcp";
        } else {
            // save current state
            save();
            // new (just branched) dep-set
            dep = getCurDepSet();
            stats.getnOrBrCalls().inc();
        }
        // if semantic branching is in use -- add previous entries to the label
        if (options.getuseSemanticBranching()) {
            for (int i : bcOr.getApplicableOrEntriesConcepts()) {
                if (addToDoEntry(curNode, -i, dep, "sb")) {
                    return true;
                    // Both Exists and Clash are errors
                }
            }
        }
        // add new entry to current node; we know the result would be DONE
        if (options.isUseDynamicBackjumping()) {
            return addToDoEntry(curNode, bcOr.orCur().getConcept(), dep, reason);
        } else {
            return insertToDoEntry(curNode, bcOr.orCur().getConcept(), dep, dlHeap.get(bcOr.orCur().getConcept())
                .getType(), reason);
        }
    }

    /**
     * expansion rule for universal restriction with non-simple role using RA
     * 
     * @param cur
     *        vertex
     * @return true if clash occurs
     */
    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyAllComplex")
    private boolean commonTacticBodyAllComplex(DLVertex cur) {
        int state = cur.getState();
        // corresponds to AR{0}.X
        int c = curConceptConcept - state;
        RAStateTransitions rst = cur.getRole().getAutomaton().get(state);
        // apply all empty transitions
        if (rst.hasEmptyTransition() && rst.stream().anyMatch(q -> applyEmptyTransition(c, q))) {
            return true;
        }
        // apply all top-role transitions
        if (rst.hasTopTransition() && rst.stream().anyMatch(q -> q.isTop() && addSessionGCI(c + q.finalState(),
            curConceptDepSet))) {
            return true;
        }
        // apply final-state rule
        if (state == 1 && addToDoEntry(curNode, cur.getConceptIndex(), curConceptDepSet, null)) {
            return true;
        }
        // check whether automaton applicable to any edges
        stats.getnAllCalls().inc();
        // check all neighbours
        Stream<DlCompletionTreeArc> neighbors = curNode.getNeighbour().stream();
        return neighbors.anyMatch(p -> rst.recognise(p.getRole()) && applyTransitions(p, rst, c, DepSet.plus(
            curConceptDepSet, p.getDep()), null));
    }

    protected boolean applyEmptyTransition(int c, RATransition q) {
        stats.getnAutoEmptyLookups().inc();
        return q.isEmpty() && addToDoEntry(curNode, c + q.finalState(), curConceptDepSet, "e");
    }

    @PortedFrom(file = "Tactic.cpp", name = "commonTacticBodyAllSimple")
    private boolean commonTacticBodyAllSimple(DLVertex cur) {
        RAStateTransitions rst = cur.getRole().getAutomaton().get(0);
        int c = cur.getConceptIndex();
        // check whether automaton applicable to any edges
        stats.getnAllCalls().inc();
        // check all neighbours; as the role is simple then recognise() ==
        // applicable()
        Stream<DlCompletionTreeArc> neighbors = curNode.getNeighbour().stream();
        return neighbors.anyMatch(p -> rst.recognise(p.getRole()) && addToDoEntry(p.getArcEnd(), c, DepSet.plus(
            curConceptDepSet, p.getDep()), null));
    }

    @PortedFrom(file = "Tactic.cpp", name = "applyTransitions")
    private boolean applyTransitions(DlCompletionTreeArc edge, RAStateTransitions rst, int c, DepSet dep,
        @Nullable String reason) {
        DlCompletionTree node = edge.getArcEnd();
        // fast lane: the single transition which is applicable
        if (rst.isSingleton()) {
            return addToDoEntry(node, c + rst.getTransitionEnd(), dep, reason);
        }
        // try to apply all transitions to edge
        return rst.stream().anyMatch(q -> applyTransitionsCheck(edge, c, dep, reason, node, q));
    }

    protected boolean applyTransitionsCheck(DlCompletionTreeArc edge, int c, DepSet dep, @Nullable String reason,
        DlCompletionTree node, RATransition q) {
        stats.getnAutoTransLookups().inc();
        return q.applicable(edge.getRole()) && addToDoEntry(node, c + q.finalState(), dep, reason);
    }

    /**
     * Perform expansion of (\AR.C).DEP to an EDGE for simple R with a given
     * reason
     * 
     * @param node
     *        Node
     * @param arcSample
     *        arcSample
     * @param depIn
     *        dep_
     * @param flags
     *        flags
     * @return true if clashing
     */
    @PortedFrom(file = "Reasoner.h", name = "applyUniversalNR")
    private boolean applyUniversalNR(DlCompletionTree node, DlCompletionTreeArc arcSample, DepSet depIn, int flags) {
        // check whether a flag is set
        if (flags == 0) {
            return false;
        }
        DepSet dep = DepSet.plus(depIn, arcSample.getDep());
        // need only AR.C concepts where ARC is labelled with R
        return node.complexConcepts().stream().anyMatch(p -> p.getConcept() > 0 && universalNR(node, p, arcSample, dep,
            flags));
    }

    private boolean universalNR(DlCompletionTree node, ConceptWDep p, DlCompletionTreeArc arcSample, DepSet dep,
        int flags) {
        DLVertex v = dlHeap.get(p.getConcept());
        Role vR = v.getRole();
        switch (v.getType()) {
            case IRR:
                return REDOIRR.match(flags) && this.checkIrreflexivity(arcSample, vR, dep);
            case FORALL:
                if (!REDOFORALL.match(flags) || vR.isTop()) {
                    return false;
                }
                /** check whether transition is possible */
                RAStateTransitions rst = vR.getAutomaton().get(v.getState());
                if (!rst.recognise(arcSample.getRole())) {
                    break;
                }
                if (vR.isSimple()) {
                    // R is recognised so just add the state!
                    if (addToDoEntry(arcSample.getArcEnd(), v.getConceptIndex(), DepSet.plus(dep, p.getDep()), "ae")) {
                        return true;
                    }
                } else {
                    if (applyTransitions(arcSample, rst, p.getConcept() - v.getState(), DepSet.plus(dep, p.getDep()),
                        "ae")) {
                        return true;
                    }
                }
                break;
            case LE:
                if (isFunctionalVertex(v)) {
                    if (REDOFUNC.match(flags) && arcSample.getRole().lesserequal(vR)) {
                        addExistingToDoEntry(node, p, "f");
                    }
                } else if (REDOATMOST.match(flags) && arcSample.getRole().lesserequal(vR)) {
                    addExistingToDoEntry(node, p, "le");
                }
                break;
            case AND:
            case BAD:
            case CHOOSE:
            case COLLECTION:
            case DATAEXPR:
            case DATATYPE:
            case DATAVALUE:
            case NCONCEPT:
            case NN:
            case NSINGLETON:
            case PCONCEPT:
            case PSINGLETON:
            case PROJ:
            case TOP:
            default:
                break;
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodySome")
    private boolean commonTacticBodySome(DLVertex cur) {
        Role r = cur.getRole();
        if (r.isTop()) {
            return commonTacticBodySomeUniv(cur);
        }
        int c = -cur.getConceptIndex();
        // check if we already have R-neighbour labelled with C
        if (isSomeExists(r, c)) {
            return false;
        }
        // try to check the case (some R (or C D)), where C is in the label of
        // an R-neighbour
        if (c < 0 && dlHeap.get(c).getType() == AND) {
            for (int q : dlHeap.get(c).begin()) {
                if (isSomeExists(r, -q)) {
                    return false;
                }
            }
        }
        // check for the case \ER.{o}
        if (c > 0 && tBox.testHasNominals()) {
            DLVertex nom = dlHeap.get(c);
            if (nom.getType() == PSINGLETON || nom.getType() == NSINGLETON) {
                return commonTacticBodyValue(r, (Individual) nom.getConcept());
            }
        }
        stats.getnSomeCalls().inc();
        // check if we have functional role
        if (r.isFunctional()) {
            List<Role> list = r.beginTopfunc();
            for (int i = 0; i < list.size(); i++) {
                int functional = list.get(i).getFunctional();
                AddConceptResult tryAddConcept = tryAddConcept(curNode.label().getLabel(true), functional,
                    curConceptDepSet);
                if (tryAddConcept == AddConceptResult.CLASH) {
                    // addition leads to clash
                    return true;
                }
                if (tryAddConcept == AddConceptResult.DONE) {
                    // should be add to a label
                    // we are changing current Node => save it
                    updateLevel(curNode, curConceptDepSet);
                    ConceptWDep rFuncRestriction1 = new ConceptWDep(functional, curConceptDepSet);
                    // NOTE! not added into todo (because will be checked
                    // right now)
                    cGraph.addConceptToNode(curNode, rFuncRestriction1, true);
                    used.add(rFuncRestriction1.getConcept());
                    options.getLog().printTemplate(Templates.COMMON_TACTIC_BODY_SOME, rFuncRestriction1);
                }
                // only other possibility is acrExist. As the node already
                // exists, nothing to do
            }
        }
        // flag is true if we have functional restriction with this Role name
        AtomicBoolean rFunc = new AtomicBoolean(false);
        // most general functional super-role of given one
        Reference<Role> rf = new Reference<>(r);
        // role's functional restriction w/dep
        Reference<ConceptWDep> rFuncRestriction = new Reference<>(null);
        // set up rFunc; rfRole contains more generic functional superrole of
        // rName
        curNode.complexConcepts().stream().forEach(lc -> findRC(r, rFunc, rf, rFuncRestriction, lc));
        if (!rFunc.get()) {
            return createNewEdge(cur.getRole(), c, redoForallAtmost());
        }
        // functional role found => add new concept to existing node
        DlCompletionTreeArc functionalArc = null;
        DepSet newDep = null;
        for (int i = 0; i < curNode.getNeighbour().size() && functionalArc == null; i++) {
            DlCompletionTreeArc pr = curNode.getNeighbour().get(i);
            newDep = pr.neighbourDepSet(rf.getReference());
            if (newDep != null) {
                functionalArc = pr;
            }
        }
        if (functionalArc == null || newDep == null) {
            return createNewEdge(cur.getRole(), c, redoForallAtmost());
        }
        options.getLog().printTemplate(Templates.COMMON_TACTIC_BODY_SOME2, rFuncRestriction.getReference());
        DlCompletionTree succ = functionalArc.getArcEnd();
        newDep.add(curConceptDepSet);
        if (r.isDisjoint() && checkDisjointRoleClash(curNode, succ, r, newDep)) {
            return true;
        }
        functionalArc = cGraph.addRoleLabel(curNode, succ, functionalArc.isPredEdge(), r, newDep);
        // adds concept to the end of arc
        if (addToDoEntry(succ, c, newDep, null)) {
            return true;
        }
        // if new role label was added...
        if (!r.equals(rf.getReference())) {
            // add Range and Domain of a new role; this includes
            // functional, so remove it from the latter
            if (initHeadOfNewEdge(curNode, r, newDep, "RD") || initHeadOfNewEdge(succ, r.inverse(), newDep, "RR")) {
                return true;
            }
            /**
             * check AR.C in both sides of functionalArc FIXME!! for simplicity,
             * check the functionality here (see bEx017). It seems only
             * necessary when R has several functional super-roles, so the
             * condition can be simplified
             */
            if (applyUniversalNR(curNode, functionalArc, newDep, redoForallFunc())) {
                return true;
            }
            /**
             * if new role label was added to a functionalArc, some functional
             * restrictions in the SUCC node might became applicable. See
             * bFunctional1x test
             */
            if (applyUniversalNR(succ, functionalArc.getReverse(), newDep, redoForallFuncAtMost())) {
                return true;
            }
        }
        return false;
    }

    protected void findRC(Role r, AtomicBoolean rFunc, Reference<Role> rf, Reference<ConceptWDep> rFuncRestriction,
        ConceptWDep lc) {
        // found such vertex (<=1 R)
        DLVertex ver = dlHeap.get(lc.getConcept());
        if (lc.getConcept() > 0 && isFunctionalVertex(ver) && r.lesserequal(ver.getRole()) && (!rFunc.get() || rf
            .getReference().lesserequal(ver.getRole()))) {
            if (rFunc.compareAndSet(false, true)) {
                rf.setReference(ver.getRole());
                rFuncRestriction.setReference(lc);
            }
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyValue")
    private boolean commonTacticBodyValue(Role r, Individual nom) {
        DepSet dep = DepSet.create(curConceptDepSet);
        // check blocking conditions
        if (isCurNodeBlocked()) {
            return false;
        }
        stats.getnSomeCalls().inc();
        assert nom.getNode() != null;
        // if node for NOM was purged due to merge -- find proper one
        DlCompletionTree realNode = nom.getNode().resolvePBlocker(dep);
        // check if merging will lead to clash because of disjoint roles
        if (r.isDisjoint() && checkDisjointRoleClash(curNode, realNode, r, dep)) {
            return true;
        }
        // here we are sure that there is a nominal connected to a root node
        encounterNominal = true;
        // create new edge between curNode and the given nominal node
        DlCompletionTreeArc edge = cGraph.addRoleLabel(curNode, realNode, false, r, dep);
        // add all necessary concepts to both ends of the edge
        return setupEdge(edge, dep, redoForallFuncAtmostIrr());
    }

    /**
     * expansion rule for the existential quantifier with universal role
     * 
     * @param cur
     *        cur
     * @return true if clashing
     */
    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodySomeUniv")
    private boolean commonTacticBodySomeUniv(DLVertex cur) {
        // check blocking conditions
        if (isCurNodeBlocked()) {
            return false;
        }
        stats.getnSomeCalls().inc();
        int c = -cur.getConceptIndex();
        // check whether C is already in CGraph
        int i = 0;
        DlCompletionTree node;
        while ((node = cGraph.getNode(i++)) != null) {
            if (isObjectNodeUnblocked(node) && node.label().contains(c)) {
                return false;
            }
        }
        // make new node labelled with C
        node = cGraph.getNewNode();
        return initNewNode(node, curConceptDepSet, c);
    }

    @PortedFrom(file = "Reasoner.h", name = "createNewEdge")
    private boolean createNewEdge(Role r, int c, int flags) {
        if (isCurNodeBlocked()) {
            stats.getnUseless().inc();
            return false;
        }
        DlCompletionTreeArc pA = this.createOneNeighbour(r, curConceptDepSet);
        // add necessary label
        return initNewNode(pA.getArcEnd(), curConceptDepSet, c) || setupEdge(pA, curConceptDepSet, flags);
    }

    @PortedFrom(file = "Reasoner.h", name = "createOneNeighbour")
    private DlCompletionTreeArc createOneNeighbour(Role r, DepSet dep) {
        return this.createOneNeighbour(r, dep, DlCompletionTree.BLOCKABLE_LEVEL);
    }

    @PortedFrom(file = "Reasoner.h", name = "createOneNeighbour")
    private DlCompletionTreeArc createOneNeighbour(Role r, DepSet dep, int level) {
        boolean forNN = level != DlCompletionTree.BLOCKABLE_LEVEL;
        DlCompletionTreeArc pA = cGraph.createNeighbour(curNode, forNN, r, dep);
        DlCompletionTree node = pA.getArcEnd();
        if (forNN) {
            node.setNominalLevel(level);
        }
        if (r.isDataRole()) {
            node.setDataNode();
        }
        options.getLog().printTemplateMixInt(r.isDataRole() ? Templates.DN : Templates.CN, dep, node.getId());
        return pA;
    }

    /**
     * check whether current node is blocked
     * 
     * @return true if blocked
     */
    @PortedFrom(file = "Reasoner.h", name = "isCurNodeBlocked")
    private boolean isCurNodeBlocked() {
        if (!options.getuseLazyBlocking()) {
            return curNode.isBlocked();
        }
        // update blocked status
        if (!curNode.isBlocked() && curNode.isAffected()) {
            updateLevel(curNode, curConceptDepSet);
            cGraph.detectBlockedStatus(curNode);
        }
        return curNode.isBlocked();
    }

    @PortedFrom(file = "Reasoner.h", name = "applyAllGeneratingRules")
    private void applyAllGeneratingRules(DlCompletionTree node) {
        // need only ER.C or >=nR.C concepts
        node.label().getComplexConcepts().stream().filter(p -> p.getConcept() <= 0).forEach(p -> addLEForAll(node, p));
    }

    private void addLEForAll(DlCompletionTree node, ConceptWDep p) {
        DLVertex v = dlHeap.get(p.getConcept());
        if (v.getType() == LE || v.getType() == FORALL) {
            addExistingToDoEntry(node, p, "ubd");
        }
    }

    /**
     * @param pA
     *        pA
     * @param dep
     *        dep
     * @param flags
     *        flags
     * @return false if all done
     */
    @PortedFrom(file = "Reasoner.h", name = "setupEdge")
    public boolean setupEdge(DlCompletionTreeArc pA, DepSet dep, int flags) {
        DlCompletionTree child = pA.getArcEnd();
        DlCompletionTree from = pA.getReverse().getArcEnd();
        // adds Range and Domain
        if (initHeadOfNewEdge(from, pA.getRole(), dep, "RD")) {
            return true;
        }
        if (initHeadOfNewEdge(child, pA.getReverse().getRole(), dep, "RR")) {
            return true;
        }
        // check if we have any AR.X concepts in current node
        if (applyUniversalNR(from, pA, dep, flags)) {
            return true;
        }
        // for nominal children and loops -- just apply things for the inverses
        if (pA.isPredEdge() || child.isNominalNode() || child.equals(from)) {
            if (applyUniversalNR(child, pA.getReverse(), dep, flags)) {
                return true;
            }
        } else {
            if (child.isDataNode()) {
                checkDataNode = true;
                if (hasDataClash(child)) {
                    return true;
                }
            } else {
                // check if it is possible to use cache for new node
                if (tryCacheNode(child).usageByState()) {
                    return true;
                }
            }
        }
        // all done
        return false;
    }

    /**
     * add necessary concepts to the head of the new EDGE
     * 
     * @param node
     *        node
     * @param r
     *        R
     * @param dep
     *        dep
     * @param reason
     *        reason
     * @return true if clashing
     */
    @PortedFrom(file = "Reasoner.h", name = "initHeadOfNewEdge")
    private boolean initHeadOfNewEdge(DlCompletionTree node, Role r, DepSet dep, String reason) {
        // if R is functional, then add FR with given DEP-set to NODE
        if (r.isFunctional()) {
            Stream<Role> s = r.beginTopfunc().stream();
            if (s.anyMatch(p -> addToDoEntry(node, p.getFunctional(), dep, "fr"))) {
                return true;
            }
        }
        // setup Domain for R
        if (addToDoEntry(node, r.getBPDomain(), dep, reason)) {
            return true;
        }
        if (!options.isUpdaterndFromSuperRoles()) {
            Stream<Role> s = r.getAncestor().stream();
            if (s.anyMatch(q -> addToDoEntry(node, q.getBPDomain(), dep, reason))) {
                return true;
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyFunc")
    private boolean commonTacticBodyFunc(DLVertex cur) {
        assert curConceptConcept > 0 && isFunctionalVertex(cur);
        if (cur.getRole().isTop()) {
            return processTopRoleFunc(cur);
        }
        // check whether we need to apply NN rule first
        if (isNNApplicable(cur.getRole(), BP_TOP, curConceptConcept + 1)) {
            return commonTacticBodyNN(cur);
        }
        stats.getnFuncCalls().inc();
        if (isQuickClashLE(cur)) {
            return true;
        }
        // locate all R-neighbours of curNode
        findNeighbours(cur.getRole(), BP_TOP, null);
        // check if we have nodes to merge
        if (edgesToMerge.size() < 2) {
            return false;
        }
        // merge all nodes to the first (the least wrt nominal hierarchy) found
        // node
        DlCompletionTreeArc q = edgesToMerge.get(0);
        DlCompletionTree sample = q.getArcEnd();
        // dep-set for merging
        DepSet depF = DepSet.create(curConceptDepSet);
        depF.add(q.getDep());
        // merge all elements to sample (sample wouldn't be merge)
        // XXX during merge EdgesToMerge may became purged (see Nasty4) =>
        // check this
        for (int i = 1; i < edgesToMerge.size(); i++) {
            q = edgesToMerge.get(i);
            if (!q.getArcEnd().isPBlocked() && merge(q.getArcEnd(), sample, DepSet.plus(depF, q.getDep()))) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyLE")
    private boolean commonTacticBodyLE(DLVertex cur) // for <=nR.C concepts
    {
        assert curConceptConcept > 0 && cur.getType() == LE;
        stats.getnLeCalls().inc();
        Role r = cur.getRole();
        if (r.isTop()) {
            return processTopRoleLE(cur);
        }
        int c = cur.getConceptIndex();
        boolean needInit = true;
        if (!isFirstBranchCall()) {
            if (bContext instanceof BCNN) {
                return commonTacticBodyNN(cur);
                // after application <=-rule would be checked again
            }
            if (bContext instanceof BCLE) {
                needInit = false;
                // clash in LE-rule: skip the initial checks
            } else {
                assert bContext instanceof BCChoose;
            }
        } else {
            // if we are here that it IS first LE call
            if (isQuickClashLE(cur)) {
                return true;
            }
        }
        // initial phase: choose-rule, NN-rule
        if (needInit) {
            // check if we have Qualified NR
            if (c != BP_TOP && commonTacticBodyChoose(r, c)) {
                return true;
            }
            // check whether we need to apply NN rule first
            if (isNNApplicable(r, c, curConceptConcept + cur.getNumberLE())) {
                return commonTacticBodyNN(cur);
                // after application <=-rule would be checked again
            }
        }
        // we need to repeat merge until there will be necessary amount of edges
        while (true) {
            if (isFirstBranchCall() && initLEProcessing(cur)) {
                return false;
            }
            BCLE<DlCompletionTreeArc> bcLE = (BCLE<DlCompletionTreeArc>) bContext;
            if (bcLE.noMoreLEOptions()) {
                // set global clashset to cumulative
                // one from previous branch failures
                useBranchDep();
                return true;
            }
            // get from- and to-arcs using corresponding indexes in Edges
            DlCompletionTreeArc fromArc = bcLE.getFrom();
            DlCompletionTreeArc toArc = bcLE.getTo();
            DlCompletionTree from = fromArc.getArcEnd();
            DlCompletionTree to = toArc.getArcEnd();
            Reference<DepSet> dep = new Reference<>(DepSet.create());
            // empty dep-set
            // fast check for FROM =/= TO
            if (cGraph.nonMergable(from, to, dep)) {
                // need this for merging two nominal nodes
                dep.getReference().add(fromArc.getDep());
                dep.getReference().add(toArc.getDep());
                // add dep-set from labels
                if (c == BP_TOP) {
                    setClashSet(dep.getReference());
                } else {
                    // QCR: update dep-set wrt C
                    // here we know that C is in both labels; set a proper
                    // clash-set
                    DagTag tag = dlHeap.get(c).getType();
                    boolean test;
                    // here dep contains the clash-set
                    test = findConceptClash(from.label().getLabel(tag.isComplexConcept()), c, dep.getReference());
                    assert test;
                    // save new dep-set
                    dep.getReference().add(clashSet);
                    test = findConceptClash(to.label().getLabel(tag.isComplexConcept()), c, dep.getReference());
                    assert test;
                    // both clash-sets are now in common clash-set
                }
                nextBranchingOption();
                assert !isFirstBranchCall();
                continue;
            }
            save();
            // add depset from current level and FROM arc and to current dep.set
            DepSet curDep = DepSet.create(getCurDepSet());
            curDep.add(fromArc.getDep());
            if (merge(from, to, curDep)) {
                return true;
            }
            /**
             * it might be the case (see bIssue28) that after the merge there is
             * an R-neigbour that have neither C or ~C in its label (it was far
             * in the nominal cloud)
             */
            if (c != BP_TOP && commonTacticBodyChoose(r, c)) {
                return true;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @PortedFrom(file = "Reasoner.h", name = "initLEProcessing")
    private boolean initLEProcessing(DLVertex cur) {
        DepSet dep = DepSet.create();
        // check the amount of neighbours we have
        findNeighbours(cur.getRole(), cur.getConceptIndex(), dep);
        // if the number of R-neighbours satisfies condition -- nothing to do
        if (edgesToMerge.size() <= cur.getNumberLE()) {
            return true;
        }
        // init context
        createBCLE();
        bContext.branchDep.add(dep);
        // setup BCLE
        BCLE<DlCompletionTreeArc> bcLE = (BCLE<DlCompletionTreeArc>) bContext;
        edgesToMerge = bcLE.swap(edgesToMerge);
        bcLE.resetMCI();
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyGE")
    private boolean commonTacticBodyGE(DLVertex cur) {
        // for >=nR.C concepts
        assert curConceptConcept < 0 && cur.getType() == LE;
        // check blocking conditions
        if (isCurNodeBlocked()) {
            return false;
        }
        Role r = cur.getRole();
        if (r.isTop()) {
            return processTopRoleGE(cur);
        }
        stats.getnGeCalls().inc();
        if (isQuickClashGE(cur)) {
            return true;
        }
        // create N new different edges
        return createDifferentNeighbours(cur.getRole(), cur.getConceptIndex(), curConceptDepSet, cur.getNumberGE(),
            DlCompletionTree.BLOCKABLE_LEVEL);
    }

    // Func/LE/GE with top role processing
    @PortedFrom(file = "Reasoner.h", name = "processTopRoleFunc")
    private boolean processTopRoleFunc(DLVertex cur) {
        // for <=1 R concepts
        assert curConceptConcept > 0 && isFunctionalVertex(cur);
        stats.getnFuncCalls().inc();
        if (isQuickClashLE(cur)) {
            return true;
        }
        // locate all R-neighbours of curNode
        // not used
        findCLabelledNodes(BP_TOP, null);
        // check if we have nodes to merge
        if (nodesToMerge.size() < 2) {
            return false;
        }
        // merge all nodes to the first (the least wrt nominal hierarchy) found
        // node
        DlCompletionTree sample = nodesToMerge.get(0);
        DepSet dep = DepSet.create(curConceptDepSet);   // dep-set for merging
        // merge all elements to sample (sample wouldn't be merge)
        // during merge EdgesToMerge may became purged (see Nasty4) => check
        // this
        return nodesToMerge.stream().skip(1).anyMatch(p -> !p.isPBlocked() && merge(p, sample, dep));
    }

    @SuppressWarnings("unchecked")
    @PortedFrom(file = "Reasoner.h", name = "processTopRoleLE")
    private boolean processTopRoleLE(DLVertex cur) {
        // for <=nR.C concepts
        assert curConceptConcept > 0 && cur.getType() == LE;
        int c = cur.getConceptIndex();
        boolean needInit = true;
        if (!isFirstBranchCall()) {
            if (bContext instanceof BCLE) {
                // clash in LE-rule: skip the initial checks
                needInit = false;
            } else {
                // the only possible case is choose-rule; in this case just
                // continue
                assert bContext instanceof BCChoose;
            }
        } else {
            // if we are here that it IS first LE call
            if (isQuickClashLE(cur)) {
                return true;
            }
        }
        // initial phase: choose-rule, NN-rule
        // check if we have Qualified NR
        if (needInit && c != BP_TOP && applyChooseRuleGlobally(c)) {
            return true;
        }
        // we need to repeat merge until there will be necessary amount of edges
        while (true) {
            if (isFirstBranchCall() && initTopLEProcessing(cur)) {
                return false;
            }
            BCLE<DlCompletionTree> bcLE = (BCLE<DlCompletionTree>) bContext;
            if (bcLE.noMoreLEOptions()) {
                // set global clashset to cumulative
                // one from previous branch failures
                useBranchDep();
                return true;
            }
            // get from- and to-arcs using corresponding indexes in Edges
            DlCompletionTree from = bcLE.getFrom();
            DlCompletionTree to = bcLE.getTo();
            Reference<DepSet> dep = new Reference<>(DepSet.create());
            // fast check for FROM =/= TO
            if (cGraph.nonMergable(from, to, dep)) {
                // add dep-set from labels
                if (c == BP_TOP) {
                    setClashSet(dep.getReference());
                } else {
                    // QCR: update dep-set wrt C
                    // here we know that C is in both labels; set a proper
                    // clash-set
                    DagTag tag = dlHeap.get(c).getType();
                    boolean test;
                    // here dep contains the clash-set
                    test = findConceptClash(from.label().getLabel(tag.isComplexConcept()), c, dep.getReference());
                    assert test;
                    // save new dep-set
                    dep.getReference().add(clashSet);
                    test = findConceptClash(to.label().getLabel(tag.isComplexConcept()), c, dep.getReference());
                    assert test;
                    // both clash-sets are now in common clash-set
                }
                nextBranchingOption();
                assert !isFirstBranchCall();
                continue;
            }
            save();
            // add depset from current level and FROM arc and to current dep.set
            DepSet curDep = DepSet.create(getCurDepSet());
            if (merge(from, to, curDep)) {
                return true;
            }
        }
    }

    // for >=nR.C concepts
    @PortedFrom(file = "Reasoner.h", name = "processTopRoleGE")
    private boolean processTopRoleGE(DLVertex cur) {
        assert curConceptConcept < 0 && cur.getType() == LE;
        assert !isCurNodeBlocked();
        stats.getnGeCalls().inc();
        if (isQuickClashGE(cur)) {
            return true;
        }
        // create N new different edges
        // FIXME!! for now
        return createDifferentNeighbours(cur.getRole(), cur.getConceptIndex(), curConceptDepSet, cur.getNumberGE(),
            DlCompletionTree.BLOCKABLE_LEVEL);
    }

    @SuppressWarnings("unchecked")
    @PortedFrom(file = "Reasoner.h", name = "initTopLEProcessing")
    private boolean initTopLEProcessing(DLVertex cur) {
        DepSet dep = DepSet.create();
        // check the amount of neighbours we have
        findCLabelledNodes(cur.getConceptIndex(), dep);
        // if the number of R-neighbours satisfies condition -- nothing to do
        if (nodesToMerge.size() <= cur.getNumberLE()) {
            return true;
        }
        // init context
        createBCTopLE();
        bContext.branchDep.add(dep);
        // setup BCLE
        BCLE<DlCompletionTree> bcLE = (BCLE<DlCompletionTree>) bContext;
        nodesToMerge = bcLE.swap(nodesToMerge);
        bcLE.resetMCI();
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "createDifferentNeighbours")
    private boolean createDifferentNeighbours(Role r, int c, DepSet dep, int n, int level) {
        DlCompletionTreeArc pA = null;
        cGraph.initIR();
        for (int i = 0; i < n; ++i) {
            pA = this.createOneNeighbour(r, dep, level);
            DlCompletionTree child = pA.getArcEnd();
            cGraph.setCurIR(child, dep);
            // add necessary new node labels and setup new edge
            if (initNewNode(child, dep, c)) {
                return true;
            }
            if (setupEdge(pA, dep, REDOFORALL.getValue())) {
                return true;
            }
        }
        cGraph.finiIR();
        // re-apply all <= NR in curNode; do it only once for all created nodes.
        // No need for Irr
        return applyUniversalNR(curNode, pA, dep, redoFuncAtMost());
    }

    @PortedFrom(file = "Reasoner.h", name = "isNRClash")
    private boolean isNRClash(DLVertex atleast, DLVertex atmost, ConceptWDep reason) {
        if (atmost.getType() != DagTag.LE || atleast.getType() != DagTag.LE) {
            return false;
        }
        if (!checkNRclash(atleast, atmost)) {
            return false;
        }
        this.setClashSet(DepSet.plus(curConceptDepSet, reason.getDep()));
        logNCEntry(curNode, reason.getConcept(), reason.getDep(), "x", dlHeap.get(reason.getConcept()).getType()
            .getName());
        return true;
    }

    private boolean usedInverseAndClash(DagTag dt, ConceptWDep p, CGLabel to) {
        return used.contains(-p.getConcept()) && findConceptClash(to.getLabel(false), -p.getConcept(), p.getDep());
    }

    @PortedFrom(file = "Reasoner.h", name = "checkMergeClash")
    private boolean checkMergeClash(CGLabel from, CGLabel to, DepSet dep, int nodeId) {
        DepSet clashDep = DepSet.create(dep);
        Optional<ConceptWDep> clashConcept = from.getSimpleConcepts().stream().filter(p -> usedInverseAndClash(PCONCEPT,
            p, to)).findAny();
        boolean clash = false;
        if (clashConcept.isPresent()) {
            clashDep.add(clashSet);
            options.getLog().printTemplateMixInt(Templates.CHECK_MERGE_CLASH, clashDep, nodeId, clashConcept.get()
                .getConcept());
            clash = true;
        }
        clashConcept = from.getComplexConcepts().stream().filter(p -> usedInverseAndClash(FORALL, p, to)).findAny();
        if (clashConcept.isPresent()) {
            clashDep.add(clashSet);
            options.getLog().printTemplateMixInt(Templates.CHECK_MERGE_CLASH, clashDep, nodeId, clashConcept.get()
                .getConcept());
            clash = true;
        }
        if (clash) {
            this.setClashSet(clashDep);
        }
        return clash;
    }

    @PortedFrom(file = "Reasoner.h", name = "mergeLabels")
    private boolean mergeLabels(CGLabel from, DlCompletionTree to, DepSet dep) {
        // due to merging, all the concepts in the TO label
        // should be updated to the new dep-set DEP
        // TODO!! check whether this is really necessary
        if (!dep.isEmpty()) {
            cGraph.saveRareCond(to.label().getLabel(false).updateDepSet(dep));
            cGraph.saveRareCond(to.label().getLabel(true).updateDepSet(dep));
        }
        // if the concept is already exists in the node label --
        // we still need to update it with a new dep-set (due to merging)
        // note that DEP is already there
        return from.getSimpleConcepts().stream().anyMatch(p -> checkIndexAndSaveOrAddEntry(p, false, to, dep))
            || from.getComplexConcepts().stream().anyMatch(p -> checkIndexAndSaveOrAddEntry(p, true, to, dep));
    }

    private boolean checkIndexAndSaveOrAddEntry(ConceptWDep p, boolean dt, DlCompletionTree to, DepSet dep) {
        int bp = p.getConcept();
        stats.getnLookups().inc();
        int index = to.label().getLabel(dt).index(bp);
        if (index > -1) {
            if (!p.getDep().isEmpty()) {
                cGraph.saveRareCond(to.label().getLabel(dt).updateDepSet(index, p.getDep()));
            }
        } else {
            if (insertToDoEntry(to, bp, DepSet.plus(dep, p.getDep()), dlHeap.get(bp).getType(), "M")) {
                return true;
            }
        }
        return false;
    }

    @PortedFrom(file = "Tactic.cpp", name = "Merge")
    /** merge FROM node into TO node with additional dep-set DEPF */
    private boolean merge(DlCompletionTree from, DlCompletionTree to, DepSet depF) {
        // if node is already purged -- nothing to do
        assert !from.isPBlocked();
        // prevent node to be merged to itself
        assert !from.equals(to);
        // never merge nominal node to blockable one
        assert to.getNominalLevel() <= from.getNominalLevel();
        options.getLog().printTemplateInt(Templates.MERGE, from.getId(), to.getId());
        stats.getnMergeCalls().inc();
        // can't merge 2 nodes which are in inequality relation
        DepSet dep = DepSet.create(depF);
        Reference<DepSet> ref = new Reference<>(dep);
        if (cGraph.nonMergable(from, to, ref)) {
            this.setClashSet(ref.getReference());
            return true;
        }
        // check for the clash before doing anything else
        if (checkMergeClash(from.label(), to.label(), depF, to.getId())) {
            return true;
        }
        // copy all node labels
        if (mergeLabels(from.label(), to, depF)) {
            return true;
        }
        // correct graph structure
        List<DlCompletionTreeArc> edges = new ArrayList<>();
        cGraph.merge(from, to, depF, edges);
        // check whether a disjoint roles lead to clash
        if (edges.stream().anyMatch(q -> q.getRole().isDisjoint() && checkDisjointRoleClash(q.getReverse().getArcEnd(),
            q.getArcEnd(), q.getRole(), depF))) {
            return true;
        }
        // nothing more to do with data nodes
        if (to.isDataNode()) {
            // data concept -- run data center for it
            return hasDataClash(to);
        }
        // for every node added to TO, every ALL, Irr and <=-node should be
        // checked
        return edges.stream().anyMatch(q -> applyUniversalNR(to, q, depF, redoForallFuncAtmostIrr()));
    }

    @PortedFrom(file = "Reasoner.h", name = "checkDisjointRoleClash")
    protected boolean checkDisjointRoleClash(DlCompletionTree from, DlCompletionTree to, Role r, DepSet dep) {
        return from.getNeighbour().stream().anyMatch(p -> checkDisjointRoleClash(p, to, r, dep));
    }

    /**
     * aux method to check whether edge ended to NODE should be added to
     * EdgetoMerge
     * 
     * @param node
     *        completion tree node
     * @param e
     *        completion arcs
     * @return true if edge is new
     */
    @PortedFrom(file = "Tactic.cpp", name = "isNewEdge")
    private static boolean isNewEdge(DlCompletionTree node, List<DlCompletionTreeArc> e) {
        // skip edges to the same node
        return e.stream().noneMatch(p -> p.getArcEnd().equals(node));
    }

    @PortedFrom(file = "Reasoner.h", name = "findNeighbours")
    private void findNeighbours(Role role, int c, @Nullable DepSet dep) {
        edgesToMerge.clear();
        DagTag tag = dlHeap.get(c).getType();
        curNode.getNeighbour().stream().filter(p -> p.isNeighbour(role) && isNewEdge(p.getArcEnd(), edgesToMerge)
            && findChooseRuleConcept(p.getArcEnd().label().getLabel(tag.isComplexConcept()), c, dep)).forEach(edgesToMerge::add);
        // sort EdgesToMerge: From named nominals to generated nominals to
        // blockable nodes
        Collections.sort(edgesToMerge, new EdgeCompare());
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyChoose")
    private boolean commonTacticBodyChoose(Role r, int c) {
        return curNode.getNeighbour().stream().anyMatch(p -> p.isNeighbour(r) && applyChooseRule(p.getArcEnd(), c));
    }

    @PortedFrom(file = "Reasoner.h", name = "applyChooseRule")
    private boolean applyChooseRule(DlCompletionTree node, int c) {
        if (node.isLabelledBy(c) || node.isLabelledBy(-c)) {
            return false;
        }
        if (isFirstBranchCall()) {
            createBCCh();
            save();
            return addToDoEntry(node, -c, getCurDepSet(), "cr0");
        } else {
            prepareBranchDep();
            DepSet dep = DepSet.create(getBranchDep());
            determiniseBranchingOp();
            return addToDoEntry(node, c, dep, "cr1");
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyNN")
    private boolean commonTacticBodyNN(DLVertex cur) {
        // here we KNOW that NN-rule is applicable, so skip some tests
        stats.getnNNCalls().inc();
        if (isFirstBranchCall()) {
            createBCNN();
        }
        BCNN bcNN = (BCNN) bContext;
        // check whether we did all possible tries
        if (bcNN.noMoreNNOptions(cur.getNumberLE())) {
            // set global clashset to cummulative one from previous branch
            // failures
            useBranchDep();
            return true;
        }
        // take next NN number; save it as SAVE() will reset it to 0
        int nn = bcNN.getBranchIndex();
        // prepare to addition to the label
        save();
        // new (just branched) dep-set
        DepSet curDep = getCurDepSet();
        // make a stopper to mark that NN-rule is applied
        if (addToDoEntry(curNode, curConceptConcept + cur.getNumberLE(), DepSet.create(), "NNs")) {
            return true;
        }
        // create curNN new different edges
        if (createDifferentNeighbours(cur.getRole(), cur.getConceptIndex(), curDep, nn, curNode.getNominalLevel()
            + 1)) {
            return true;
        }
        // now remember NR we just created: it is (<= curNN R), so have to find
        // it
        return addToDoEntry(curNode, curConceptConcept + cur.getNumberLE() - nn, curDep, "NN");
    }

    /**
     * @param r
     *        role
     * @param c
     *        filler
     * @param stopper
     *        stopper
     * @return true iff NN-rule wrt (<= R) is applicable to the curNode
     */
    @PortedFrom(file = "Reasoner.h", name = "isNNApplicable")
    protected boolean isNNApplicable(Role r, int c, int stopper) {
        // NN rule is only applicable to nominal nodes
        if (!curNode.isNominalNode()) {
            return false;
        }
        // check whether the NN-rule was already applied here for a given
        // concept
        if (used.contains(stopper) && curNode.isLabelledBy(stopper)) {
            return false;
        }
        // check for the real applicability of the NN-rule here
        // if there is an edge that require to run the rule, then we need it
        // otherwise the rulle canot be applied
        return curNode.getNeighbour().stream().filter(p -> p.isPredEdge() && p.getArcEnd().isBlockableNode() && p
            .isNeighbour(r) && p.getArcEnd().isLabelledBy(c)).peek(p -> options.getLog().print(" NN(").print(p
                .getArcEnd().getId()).print(")")).findAny().isPresent();
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodySomeSelf")
    private boolean commonTacticBodySomeSelf(Role r) {
        // check blocking conditions
        if (isCurNodeBlocked()) {
            return false;
        }
        // nothing to do if R-loop already exists
        if (curNode.getNeighbour().stream().anyMatch(p -> p.getArcEnd().equals(curNode) && p.isNeighbour(r))) {
            return false;
        }
        // create an R-loop through curNode
        DepSet dep = DepSet.create(curConceptDepSet);
        DlCompletionTreeArc pA = cGraph.createLoop(curNode, r, dep);
        return setupEdge(pA, dep, redoForallFuncAtmostIrr());
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyIrrefl")
    private boolean commonTacticBodyIrrefl(Role r) {
        return curNode.getNeighbour().stream().anyMatch(p -> checkIrreflexivity(p, r, curConceptDepSet));
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyProj")
    private boolean commonTacticBodyProj(Role r, int c, Role projR) {
        if (curNode.isLabelledBy(-c)) {
            return false;
        }
        // checkProjection() might change curNode's edge vector and thusly
        // invalidate iterators
        return curNode.getNeighbour().stream().anyMatch(p -> p.isNeighbour(r) && checkProjection(p, c, projR));
    }

    @PortedFrom(file = "Reasoner.h", name = "checkProjection")
    private boolean checkProjection(DlCompletionTreeArc pA, int c, Role projR) {
        // nothing to do if pA is labelled by ProjR as well
        if (pA.isNeighbour(projR)) {
            return false;
        }
        // if ~C is in the label of curNode, do nothing
        if (curNode.isLabelledBy(-c)) {
            return false;
        }
        // neither C nor ~C are in the label: make a choice
        DepSet dep = DepSet.create(curConceptDepSet);
        dep.add(pA.getDep());
        if (!curNode.isLabelledBy(c)) {
            if (isFirstBranchCall()) {
                createBCCh();
                // save current state
                save();
                return addToDoEntry(curNode, -c, getCurDepSet(), "cr0");
            } else {
                prepareBranchDep();
                dep.add(getBranchDep());
                determiniseBranchingOp();
                if (addToDoEntry(curNode, c, dep, "cr1")) {
                    return true;
                }
            }
        }
        // here C is in the label of curNode: add ProjR to the edge if necessary
        DlCompletionTree child = pA.getArcEnd();
        return setupEdge(cGraph.addRoleLabel(curNode, child, pA.isPredEdge(), projR, dep), dep,
            redoForallFuncAtmostIrr());
    }

    /** create BC for LE-rule */
    @PortedFrom(file = "Reasoner.h", name = "createBCTopLE")
    public void createBCTopLE() {
        bContext = stack.pushTopLE();
        initBC(bContext);
    }

    /**
     * apply choose-rule for all vertices (necessary for Top role in QCR)
     * 
     * @param c
     *        C
     * @return true if clashing
     */
    @PortedFrom(file = "Reasoner.h", name = "applyChooseRuleGlobally")
    private boolean applyChooseRuleGlobally(int c) {
        return cGraph.nodes().anyMatch(p -> isObjectNodeUnblocked(p) && applyChooseRule(p, c));
    }

    @PortedFrom(file = "Reasoner.h", name = "findCLabelledNodes")
    private void findCLabelledNodes(int c, @Nullable DepSet dep) {
        nodesToMerge.clear();
        DagTag tag = dlHeap.get(c).getType();
        // FIXME!! do we need this for d-blocked nodes?
        int i = 0;
        DlCompletionTree arc = cGraph.getNode(i++);
        while (arc != null) {
            if (isNodeGloballyUsed(arc) && findChooseRuleConcept(arc.label().getLabel(tag.isComplexConcept()), c, dep)) {
                nodesToMerge.add(arc);
            }
            arc = cGraph.getNode(i++);
        }
        // sort EdgesToMerge: From named nominals to generated nominals
        // to blockable nodes
        Collections.sort(nodesToMerge, nodeCompare);
    }
}
