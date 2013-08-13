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
import java.util.*;

import org.semanticweb.owlapi.reasoner.TimeOutException;

import uk.ac.manchester.cs.jfact.datatypes.DataTypeReasoner;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.*;
import uk.ac.manchester.cs.jfact.helpers.Timer;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheConst;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheIan;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheState;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.kernel.todolist.ToDoEntry;
import uk.ac.manchester.cs.jfact.kernel.todolist.ToDoList;
import uk.ac.manchester.cs.jfact.split.TSplitRules.TSplitRule;
import conformance.Original;
import conformance.PortedFrom;

/** sat tester */
@PortedFrom(file = "Reasoner.h", name = "DlSatTester")
public class DlSatTester implements Serializable {
    private static final long serialVersionUID = 11000L;

    private class LocalFastSet implements FastSet, Serializable {
        private static final long serialVersionUID = 11000L;
        BitSet pos = new BitSet();

        public LocalFastSet() {}

        @Override
        public int[] toIntArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeAt(int o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeAllValues(int... values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeAll(int i, int end) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove(int o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean intersect(FastSet f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int get(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAny(FastSet c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(FastSet c) {
            throw new UnsupportedOperationException();
        }

        private int asPositive(int p) {
            return p >= 0 ? 2 * p : 1 - 2 * p;
        }

        @Override
        public boolean contains(int o) {
            return pos.get(asPositive(o));
        }

        @Override
        public void clear() {
            pos.clear();
        }

        @Override
        public void addAll(FastSet c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int e) {
            pos.set(asPositive(e));
        }

        @Override
        public void completeSet(int value) {
            for (int i = 0; i <= value; i++) {
                pos.set(i);
            }
        }
    }

    /** Enum for usage the Tactics to a ToDoEntry */
    abstract class BranchingContext implements Serializable {
        private static final long serialVersionUID = 11000L;
        /** currently processed node */
        protected DlCompletionTree node;
        /** currently processed concept */
        protected ConceptWDep concept = null;
        /** dependences for branching clashes */
        protected DepSet branchDep = DepSet.create();
        /** size of a session GCIs vector */
        protected int SGsize;

        /** empty c'tor */
        public BranchingContext() {
            node = null;
        }

        /** init indeces (if necessary) */
        public abstract void init();

        /** give the next branching alternative */
        public abstract void nextOption();

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + " dep '" + branchDep
                    + "' curconcept '"
                    + (concept == null ? new ConceptWDep(Helper.bpINVALID) : concept)
                    + "' curnode '" + node + "'";
        }
    }

    abstract class BCChoose extends BranchingContext {
        private static final long serialVersionUID = 11000L;
    }

    /** stack to keep BContext */
    class BCStack extends SaveStack<BranchingContext> {
        private static final long serialVersionUID = 11000L;
        /** single entry for the barrier (good for nominal reasoner) */
        private final BCBarrier bcBarrier;

        /** push method to use */
        @Override
        public void push(BranchingContext p) {
            p.init();
            initBC(p);
            super.push(p);
        }

        protected BCStack() {
            bcBarrier = new BCBarrier();
        }

        /** get BC for Or-rule */
        protected BranchingContext pushOr() {
            BCOr o = new BCOr();
            push(o);
            return o;
        }

        /** get BC for NN-rule */
        protected BranchingContext pushNN() {
            BCNN n = new BCNN();
            push(n);
            return n;
        }

        /** get BC for LE-rule */
        protected BCLE<DlCompletionTreeArc> pushLE() {
            BCLE<DlCompletionTreeArc> e = new BCLE<DlCompletionTreeArc>();
            push(e);
            return e;
        }

        /** get BC for TopLE-rule */
        BCLE<DlCompletionTree> pushTopLE() {
            // XXX verify if this is correct
            BCLE<DlCompletionTree> e = new BCLE<DlCompletionTree>();
            push(e);
            return e;
        }

        /** get BC for Choose-rule */
        protected BCChoose pushCh() {
            BCChoose c = new BCChoose() {
                private static final long serialVersionUID = 11000L;

                @Override
                public void nextOption() {}

                @Override
                public void init() {}
            };
            push(c);
            return c;
        }

        /** get BC for the barrier */
        protected BCBarrier pushBarrier() {
            push(bcBarrier);
            return bcBarrier;
        }
    }

    class BCBarrier extends BranchingContext {
        private static final long serialVersionUID = 11000L;

        @Override
        public void init() {}

        @Override
        public void nextOption() {}
    }

    class BCLE<I> extends BranchingContext {
        private static final long serialVersionUID = 11000L;
        /** current branching index; used in several branching rules */
        private int branchIndex;
        /** index of a merge-candidate (in LE concept) */
        private int mergeCandIndex;
        /** vector of edges to be merged */
        private List<I> edges = new ArrayList<I>();

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
        /** get FROM pointer to merge */
        protected I getFrom() {
            return edges.get(mergeCandIndex);
        }

        /** get FROM pointer to merge */
        protected I getTo() {
            return edges.get(branchIndex);
        }

        /** check if the LE has no option to process */
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
        private static final long serialVersionUID = 11000L;
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
        /** check if the NN has no option to process */
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
        private static final long serialVersionUID = 11000L;
        /** current branching index; used in several branching rules */
        private int branchIndex;
        private int size = 0;
        /** useful disjuncts (ready to add) in case of OR */
        private List<ConceptWDep> applicableOrEntries = new ArrayList<ConceptWDep>();

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
        /** check if the current processing OR entry is the last one */
        protected boolean isLastOrEntry() {
            return size == branchIndex + 1;
        }

        /** current element of OrIndex */
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
            o.append(concept == null ? new ConceptWDep(Helper.bpINVALID) : concept);
            o.append(" curnode ");
            o.append(node);
            o.append(" orentries [");
            o.append(applicableOrEntries);
            o.append("]");
            return o.toString();
        }
    }

    /** class to check whether there is a need to unsplit splitted var */
    class SingleSplit implements Serializable {
        private static final long serialVersionUID = 11000L;
        /** signature of equivalent part of the split */
        private final Set<NamedEntity> eqSig;
        /** signature of subsumption part of the split */
        private final Set<NamedEntity> impSig;
        /** pointer to split vertex to activate */
        private final int bp;

        protected SingleSplit(Set<NamedEntity> es, Set<NamedEntity> is, int p) {
            eqSig = new HashSet<NamedEntity>(es);
            impSig = new HashSet<NamedEntity>(is);
            bp = p;
        }
    }

    /** GCIs local to session */
    @PortedFrom(file = "Reasoner.h", name = "SessionGCIs")
    private final List<Integer> SessionGCIs = new ArrayList<Integer>();
    /** set of active splits */
    @PortedFrom(file = "Reasoner.h", name = "ActiveSplits")
    private final FastSet ActiveSplits = FastSetFactory.create();
    /** concept signature of current CGraph */
    @PortedFrom(file = "Reasoner.h", name = "SessionSignature")
    private final Set<NamedEntity> SessionSignature = new HashSet<NamedEntity>();
    /** signature to dep-set map for current session */
    @PortedFrom(file = "Reasoner.h", name = "SessionSigDepSet")
    private final Map<NamedEntity, DepSet> SessionSigDepSet = new HashMap<NamedEntity, DepSet>();
    /** nodes to merge in the TopRole-LE rules */
    @PortedFrom(file = "Reasoner.h", name = "NodesToMerge")
    private List<DlCompletionTree> NodesToMerge = new ArrayList<DlCompletionTree>();
    @PortedFrom(file = "Reasoner.h", name = "EdgesToMerge")
    private List<DlCompletionTreeArc> EdgesToMerge = new ArrayList<DlCompletionTreeArc>();

    // CGraph-wide rules support
    /** @return true if node is valid for the reasoning */
    @PortedFrom(file = "Reasoner.h", name = "isNodeGloballyUsed")
    private boolean isNodeGloballyUsed(DlCompletionTree node) {
        return !(node.isDataNode() || node.isIBlocked() || node.isPBlocked());
    }

    /** @return true if node is valid for the reasoning */
    @PortedFrom(file = "Reasoner.h", name = "isObjectNodeUnblocked")
    private boolean isObjectNodeUnblocked(DlCompletionTree node) {
        return isNodeGloballyUsed(node) && !node.isDBlocked();
    }

    /** put TODO entry for either BP or inverse(BP) in NODE's label */
    @PortedFrom(file = "Reasoner.h", name = "updateName")
    private void updateName(DlCompletionTree node, int bp) {
        CGLabel lab = node.label();
        ConceptWDep c = lab.getConceptWithBP(bp);
        if (c == null) {
            c = lab.getConceptWithBP(-bp);
        }
        if (c != null) {
            addExistingToDoEntry(node, c, "sp");
        }
    }

    /** re-do every BP or inverse(BP) in labels of CGraph */
    @PortedFrom(file = "Reasoner.h", name = "updateName")
    private void updateName(int bp) {
        int n = 0;
        DlCompletionTree node = null;
        while ((node = cGraph.getNode(n++)) != null) {
            if (isNodeGloballyUsed(node)) {
                this.updateName(node, bp);
            }
        }
    }

    /** host TBox */
    @PortedFrom(file = "Reasoner.h", name = "tBox")
    protected final TBox tBox;
    /** link to dag from TBox */
    @PortedFrom(file = "Reasoner.h", name = "DLHeap")
    protected final DLDag dlHeap;
    /** all the reflexive roles */
    @PortedFrom(file = "Reasoner.h", name = "ReflexiveRoles")
    private final List<Role> reflexiveRoles = new ArrayList<Role>();
    /** Completion Graph of tested concept(s) */
    @PortedFrom(file = "Reasoner.h", name = "CGraph")
    protected final DlCompletionGraph cGraph;
    /** Todo list */
    @PortedFrom(file = "Reasoner.h", name = "TODO")
    private final ToDoList TODO;
    @Original
    private final FastSet used = new LocalFastSet();
    /** GCI-related KB flags */
    @PortedFrom(file = "Reasoner.h", name = "GCIs")
    private final KBFlags gcis;
    /** record nodes that were processed during Cascaded Cache construction */
    @PortedFrom(file = "Reasoner.h", name = "inProcess")
    private final FastSet inProcess = FastSetFactory.create();
    /** timer for the SAT tests (ie, cache creation) */
    @PortedFrom(file = "Reasoner.h", name = "satTimer")
    private final Timer satTimer = new Timer();
    /** timer for the SUB tests (ie, general subsumption) */
    @PortedFrom(file = "Reasoner.h", name = "subTimer")
    private final Timer subTimer = new Timer();
    /** timer for a single test; use it as a timeout checker */
    @PortedFrom(file = "Reasoner.h", name = "testTimer")
    private final Timer testTimer = new Timer();
    // save/restore option
    /** stack for the local reasoner's state */
    @PortedFrom(file = "Reasoner.h", name = "Stack")
    protected final BCStack stack = new BCStack();
    /** context from the restored branching rule */
    @PortedFrom(file = "Reasoner.h", name = "bContext")
    protected BranchingContext bContext;
    /** index of last non-det situation */
    @PortedFrom(file = "Reasoner.h", name = "tryLevel")
    private int tryLevel;
    /** shift in order to determine the 1st non-det application */
    @PortedFrom(file = "Reasoner.h", name = "nonDetShift")
    protected int nonDetShift;
    /** last level when split rules were applied */
    @PortedFrom(file = "Reasoner.h", name = "splitRuleLevel")
    private int splitRuleLevel;
    // current values
    /** currently processed CTree node */
    @PortedFrom(file = "Reasoner.h", name = "curNode")
    protected DlCompletionTree curNode;
    /** currently processed Concept */
    @Original
    private DepSet curConceptDepSet;
    @Original
    private int curConceptConcept;
    /** size of the DAG with some extra space */
    @PortedFrom(file = "Reasoner.h", name = "dagSize")
    private int dagSize;
    /** temporary array used in OR operation */
    @PortedFrom(file = "Reasoner.h", name = "OrConceptsToTest")
    private List<ConceptWDep> orConceptsToTest = new ArrayList<ConceptWDep>();
    /** contains clash set if clash is encountered in a node label */
    @PortedFrom(file = "Reasoner.h", name = "clashSet")
    private DepSet clashSet = DepSet.create();
    @Original
    protected final JFactReasonerConfiguration options;
    // session status flags:
    /** true if nominal-related expansion rule was fired during reasoning */
    @PortedFrom(file = "Reasoner.h", name = "encounterNominal")
    private boolean encounterNominal;
    /** flag to show if it is necessary to produce DT reasoning immediately */
    @PortedFrom(file = "Reasoner.h", name = "checkDataNode")
    private boolean checkDataNode;
    /** cache for testing whether it's possible to non-expand newly created node */
    @PortedFrom(file = "Reasoner.h", name = "newNodeCache")
    private final ModelCacheIan newNodeCache;
    /** auxilliary cache that is built from the edges of newly created node */
    @PortedFrom(file = "Reasoner.h", name = "newNodeEdges")
    private final ModelCacheIan newNodeEdges;
    @Original
    private final Stats stats = new Stats();
    @Original
    protected final DatatypeFactory datatypeFactory;

    /** Adds ToDo entry which already exists in label of NODE. There is no need
     * to add entry to label, but it is necessary to provide offset of existing
     * concept. This is done by providing OFFSET of the concept in NODE's label */
    @PortedFrom(file = "Reasoner.h", name = "addExistingToDoEntry")
    private void
            addExistingToDoEntry(DlCompletionTree node, ConceptWDep C, String reason /*
                                                                                      * =
                                                                                      * null
                                                                                      */) {
        int bp = C.getConcept();
        TODO.addEntry(node, dlHeap.get(bp).getType(), C);
        logNCEntry(node, C.getConcept(), C.getDep(), "+", reason);
    }

    /** add all elements from NODE label into Todo list */
    @PortedFrom(file = "Reasoner.h", name = "redoNodeLabel")
    private void redoNodeLabel(DlCompletionTree node, String reason) {
        CGLabel lab = node.label();
        List<ConceptWDep> l = lab.get_sc();
        for (int i = 0; i < l.size(); i++) {
            addExistingToDoEntry(node, l.get(i), reason);
        }
        l = lab.get_cc();
        for (int i = 0; i < l.size(); i++) {
            addExistingToDoEntry(node, l.get(i), reason);
        }
    }

    /** make sure that the DAG does not grow larger than that was recorded */
    @PortedFrom(file = "Reasoner.h", name = "ensureDAGSize")
    private void ensureDAGSize() {
        if (dagSize < dlHeap.size()) {
            dagSize = dlHeap.maxSize();
            tBox.getSplitRules().ensureDagSize(dagSize);
        }
    }

    // -- internal cache support
    /** return cache of given completion tree (implementation) */
    @PortedFrom(file = "Reasoner.h", name = "createModelCache")
    protected ModelCacheInterface createModelCache(DlCompletionTree p) {
        return new ModelCacheIan(dlHeap, p, encounterNominal, tBox.nC, tBox.nR,
                options.isRKG_USE_SIMPLE_RULES());
    }

    /** check whether node may be (un)cached; save node if something is changed */
    @PortedFrom(file = "Reasoner.h", name = "tryCacheNode")
    private ModelCacheState tryCacheNode(DlCompletionTree node) {
        // TODO verify
        ModelCacheState ret = canBeCached(node) ? reportNodeCached(node)
                : ModelCacheState.csFailed;
        // node is cached if RET is csvalid
        boolean val = ret == ModelCacheState.csValid;
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

    /** @return true iff current node is i-blocked (ie, no expansion necessary) */
    @PortedFrom(file = "Reasoner.h", name = "isIBlocked")
    private boolean isIBlocked() {
        return curNode.isIBlocked();
    }

    /** @return true iff there is R-neighbour labelled with C */
    @PortedFrom(file = "Reasoner.h", name = "isSomeExists")
    private boolean isSomeExists(Role R, int C) {
        // TODO verify whether a cache is worth the effort
        if (!used.contains(C)) {
            return false;
        }
        DlCompletionTree where = curNode.isSomeApplicable(R, C);
        if (where != null) {
            options.getLog().printTemplate(Templates.E, R.getName(), where.getId(), C);
        }
        return where != null;
    }

    /** apply AR.C in and <= nR (if needed) in NODE's label where R is label of
     * arcSample. Set of applicable concepts is defined by redoForallFlags
     * value. */
    /** check if branching rule was called for the 1st time */
    @PortedFrom(file = "Reasoner.h", name = "isFirstBranchCall")
    private boolean isFirstBranchCall() {
        return bContext == null;
    }

    /** init branching context with given rule type */
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
        c.SGsize = SessionGCIs.size();
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

    /** check whether a node represents a functional one */
    @PortedFrom(file = "Reasoner.h", name = "isFunctionalVertex")
    private static boolean isFunctionalVertex(DLVertex v) {
        return v.getType() == DagTag.dtLE && v.getNumberLE() == 1
                && v.getConceptIndex() == Helper.bpTOP;
    }

    /** check if ATLEAST and ATMOST entries are in clash. Both vertex MUST have
     * dtLE type. */
    @PortedFrom(file = "Reasoner.h", name = "checkNRclash")
    private boolean checkNRclash(DLVertex atleast, DLVertex atmost) {
        // >= n R.C clash with <= m S.D iff...
        return (atmost.getConceptIndex() == Helper.bpTOP ||
        // either D is TOP or C == D...
                atleast.getConceptIndex() == atmost.getConceptIndex())
                &&
                // and n is greater than m...
                atleast.getNumberGE() > atmost.getNumberLE() &&
                // and R [= S
                atleast.getRole().lesserequal(atmost.getRole());
    }

    /** quick check whether CURNODE has a clash with a given ATMOST restriction */
    @PortedFrom(file = "Reasoner.h", name = "isQuickClashLE")
    private boolean isQuickClashLE(DLVertex atmost) {
        List<ConceptWDep> list = curNode.beginl_cc();
        for (int i = 0; i < list.size(); i++) {
            ConceptWDep q = list.get(i);
            // need at-least restriction
            if (q.getConcept() < 0 && isNRClash(dlHeap.get(q.getConcept()), atmost, q)) {
                return true;
            }
        }
        return false;
    }

    /** quick check whether CURNODE has a clash with a given ATLEAST restriction */
    @PortedFrom(file = "Reasoner.h", name = "isQuickClashGE")
    private boolean isQuickClashGE(DLVertex atleast) {
        List<ConceptWDep> list = curNode.beginl_cc();
        for (int i = 0; i < list.size(); i++) {
            ConceptWDep q = list.get(i);
            // need at-most restriction
            if (q.getConcept() > 0 && isNRClash(atleast, dlHeap.get(q.getConcept()), q)) {
                return true;
            }
        }
        return false;
    }

    /** aux method that fills the dep-set for either C or ~C found in the label;
     * 
     * @param d
     *            depset to be changed if a clash is found
     * @return whether C was found */
    @PortedFrom(file = "Reasoner.h", name = "findChooseRuleConcept")
    private boolean findChooseRuleConcept(CWDArray label, int C, DepSet d) {
        if (C == Helper.bpTOP) {
            return true;
        }
        if (findConceptClash(label, C, d)) {
            if (d != null) {
                d.add(clashSet);
            }
            return true;
        } else if (findConceptClash(label, -C, d)) {
            if (d != null) {
                d.add(clashSet);
            }
            return false;
        } else {
            throw new UnreachableSituationException();
        }
    }

    /** check whether clash occures EDGE to TO labelled with S disjoint with R */
    @PortedFrom(file = "Reasoner.h", name = "checkDisjointRoleClash")
    private boolean checkDisjointRoleClash(DlCompletionTreeArc edge, DlCompletionTree to,
            Role R, DepSet dep) {
        // clash found
        if (edge.getArcEnd().equals(to) && edge.getRole().isDisjoint(R)) {
            this.setClashSet(dep);
            updateClashSet(edge.getDep());
            return true;
        }
        return false;
    }

    // support for FORALL expansion
    /** Perform expansion of (\neg \ER.Self).DEP to an EDGE */
    @PortedFrom(file = "Reasoner.h", name = "checkIrreflexivity")
    private boolean checkIrreflexivity(DlCompletionTreeArc edge, Role R, DepSet dep) {
        // only loops counts here...
        if (!edge.getArcEnd().equals(edge.getReverse().getArcEnd())) {
            return false;
        }
        // which are labelled either with R or with R-
        if (!edge.isNeighbour(R) && !edge.isNeighbour(R.inverse())) {
            return false;
        }
        // set up clash
        this.setClashSet(dep);
        updateClashSet(edge.getDep());
        return true;
    }

    /** log the result of processing ACTION with entry (N,C{DEP})/REASON */
    @PortedFrom(file = "Reasoner.h", name = "logNCEntry")
    private void logNCEntry(DlCompletionTree n, int bp, DepSet dep, String action,
            String reason) {
        if (options.isLoggingActive()) {
            LogAdapter logAdapter = options.getLog();
            logAdapter.print(" ").print(action).print("(").print(n.logNode()).print(",")
                    .print(bp).print(dep, ")");
            if (reason != null) {
                logAdapter.print(reason);
            }
        }
    }

    /** use this method in ALL dependency stuff (never use tryLevel directly) */
    @PortedFrom(file = "Reasoner.h", name = "getCurLevel")
    private int getCurLevel() {
        return tryLevel;
    }

    /** set new branching level (never use tryLevel directly) */
    @PortedFrom(file = "Reasoner.h", name = "setCurLevel")
    private void setCurLevel(int level) {
        tryLevel = level;
    }

    /** @return true if no branching ops were applied during reasoners; FIXME!!
     *         doesn't work properly with a nominal cloud */
    @PortedFrom(file = "Reasoner.h", name = "noBranchingOps")
    protected boolean noBranchingOps() {
        return tryLevel == Helper.InitBranchingLevelValue + nonDetShift;
    }

    /** Get save/restore level based on either current- or DS level */
    @PortedFrom(file = "Reasoner.h", name = "getSaveRestoreLevel")
    private int getSaveRestoreLevel(DepSet ds) {
        // FIXME!!! see more precise it later
        if (options.isRKG_IMPROVE_SAVE_RESTORE_DEPSET()) {
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

    /** update level in N node and save it's state (if necessary) */
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

    /** set value of global dep-set to D */
    @PortedFrom(file = "Reasoner.h", name = "setClashSet")
    private void setClashSet(DepSet d) {
        clashSet = d;
    }

    @PortedFrom(file = "Reasoner.h", name = "setClashSet")
    private void setClashSet(List<DepSet> d) {
        DepSet dep = DepSet.create();
        for (int i = 0; i < d.size(); i++) {
            dep.add(d.get(i));
        }
        clashSet = dep;
    }

    /** add D to global dep-set */
    @PortedFrom(file = "Reasoner.h", name = "updateClashSet")
    private void updateClashSet(DepSet d) {
        clashSet.add(d);
    }

    /** get dep-set wrt current level */
    @PortedFrom(file = "Reasoner.h", name = "getCurDepSet")
    private DepSet getCurDepSet() {
        return DepSet.create(getCurLevel() - 1);
    }

    /** get RW access to current branching dep-set */
    @PortedFrom(file = "Reasoner.h", name = "getBranchDep")
    private DepSet getBranchDep() {
        return bContext.branchDep;
    }

    /** update cumulative branch-dep with current clash-set */
    @PortedFrom(file = "Reasoner.h", name = "updateBranchDep")
    private void updateBranchDep() {
        getBranchDep().add(clashSet);
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

    /** re-apply all the relevant expantion rules to a given unblocked NODE
     * 
     * @param node
     * @param direct */
    @PortedFrom(file = "Reasoner.h", name = "repeatUnblockedNode")
    public void repeatUnblockedNode(DlCompletionTree node, boolean direct) {
        if (direct) {
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
        TODO.initPriorities(iaoeflg);
    }

    /** set blocking method for a session
     * 
     * @param hasInverse
     * @param hasQCR */
    @PortedFrom(file = "Reasoner.h", name = "setBlockingMethod")
    public void setBlockingMethod(boolean hasInverse, boolean hasQCR) {
        cGraph.setBlockingMethod(hasInverse, hasQCR);
    }

    /** @param sat
     * @return create model cache for the just-classified entry */
    @PortedFrom(file = "Reasoner.h", name = "buildCacheByCGraph")
    public ModelCacheInterface buildCacheByCGraph(boolean sat) {
        if (sat) {
            return createModelCache(getRootNode());
        } else {
            // unsat => cache is just bottom
            return ModelCacheConst.createConstCache(Helper.bpBOTTOM);
        }
    }

    /** @param o */
    @PortedFrom(file = "Reasoner.h", name = "writeTotalStatistic")
    public void writeTotalStatistic(LogAdapter o) {
        if (options.isUSE_REASONING_STATISTICS()) {
            // ensure that the last reasoning results are in
            stats.accumulate();
            stats.logStatisticData(o, false, cGraph, options);
        }
        o.print("\n");
    }

    /** @param p
     * @param f
     * @return new cache */
    @PortedFrom(file = "Reasoner.h", name = "createCache")
    public ModelCacheInterface createCache(int p, FastSet f) {
        assert Helper.isValid(p);
        ModelCacheInterface cache;
        if ((cache = dlHeap.getCache(p)) != null) {
            return cache;
        }
        if (!tBox.testHasTopRole()) {
            prepareCascadedCache(p, f);
        }
        if ((cache = dlHeap.getCache(p)) != null) {
            return cache;
        }
        cache = buildCache(p);
        dlHeap.setCache(p, cache);
        return cache;
    }

    @Original
    private final static EnumSet<DagTag> handlecollection = EnumSet.of(dtAnd,
            dtCollection);
    @Original
    private final static EnumSet<DagTag> handleforallle = EnumSet.of(dtForall, dtLE);
    @Original
    private final static EnumSet<DagTag> handlesingleton = EnumSet.of(dtPSingleton,
            dtNSingleton, dtNConcept, dtPConcept);

    @PortedFrom(file = "Reasoner.h", name = "prepareCascadedCache")
    private void prepareCascadedCache(int p, FastSet f) {
        if (inProcess.contains(p)) {
            return;
        }
        if (f.contains(p)) {
            return;
        }
        DLVertex v = dlHeap.get(p);
        boolean pos = p > 0;
        if (v.getCache(pos) != null) {
            return;
        }
        DagTag type = v.getType();
        if (handlecollection.contains(type)) {
            for (int q : v.begin()) {
                prepareCascadedCache(pos ? q : -q, f);
            }
        } else if (handlesingleton.contains(type)) {
            if (!pos && type.isPNameTag()) {
                return;
            }
            inProcess.add(p);
            prepareCascadedCache(pos ? v.getConceptIndex() : -v.getConceptIndex(), f);
            inProcess.remove(p);
        } else if (handleforallle.contains(type)) {
            Role R = v.getRole();
            if (!R.isDataRole() && !R.isTop()) {
                int x = pos ? v.getConceptIndex() : -v.getConceptIndex();
                if (x != Helper.bpTOP) {
                    inProcess.add(x);
                    createCache(x, f);
                    inProcess.remove(x);
                }
                x = R.getBPRange();
                if (x != Helper.bpTOP) {
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
        boolean sat = this.runSat(p, Helper.bpTOP);
        if (!sat) {
            logAdapter.printTemplate(Templates.BUILD_CACHE_UNSAT, p);
        }
        return buildCacheByCGraph(sat);
    }

    // flags section
    /** @return true iff semantic branching is used */
    @PortedFrom(file = "Reasoner.h", name = "useSemanticBranching")
    private boolean useSemanticBranching() {
        return tBox.isUseSemanticBranching();
    }

    /** @return true iff lazy blocking is used */
    @PortedFrom(file = "Reasoner.h", name = "useLazyBlocking")
    private boolean useLazyBlocking() {
        return tBox.isUseLazyBlocking();
    }

    /** @return true iff active signature is in use */
    @PortedFrom(file = "Reasoner.h", name = "useActiveSignature")
    private boolean useActiveSignature() {
        return !tBox.getSplits().empty();
    }

    @PortedFrom(file = "Reasoner.h", name = "resetSessionFlags")
    protected void resetSessionFlags() {
        // reflect possible change of DAG size
        ensureDAGSize();
        used.add(Helper.bpTOP);
        used.add(Helper.bpBOTTOM);
        encounterNominal = false;
        checkDataNode = true;
        splitRuleLevel = 0;
    }

    @PortedFrom(file = "Reasoner.h", name = "initNewNode")
    protected boolean initNewNode(DlCompletionTree node, DepSet dep, int C) {
        if (node.isDataNode()) {
            checkDataNode = false;
        }
        node.setInit(C);
        if (addToDoEntry(node, C, dep, null)) {
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
        if (!SessionGCIs.isEmpty()) {
            for (int i : SessionGCIs) {
                if (addToDoEntry(node, i, dep, "sg")) {
                    return true;
                }
            }
        }
        return false;
    }

    /** @param p
     * @param q
     * @return true if satisfiable */
    @PortedFrom(file = "Reasoner.h", name = "runSat")
    public boolean runSat(int p, int q) {
        prepareReasoner();
        // use general method to init node with P and add Q then
        if (initNewNode(cGraph.getRoot(), DepSet.create(), p)
                || addToDoEntry(cGraph.getRoot(), q, DepSet.create(), null)) {
            // concept[s] unsatisfiable
            return false;
        }
        // check satisfiability explicitly
        Timer timer = q == Helper.bpTOP ? satTimer : subTimer;
        timer.start();
        boolean result = this.runSat();
        timer.stop();
        return result;
    }

    /** @param R
     * @param S
     * @return true if not satisfiable */
    @PortedFrom(file = "Reasoner.h", name = "checkDisjointRoles")
    public boolean checkDisjointRoles(Role R, Role S) {
        prepareReasoner();
        // use general method to init node...
        DepSet dummy = DepSet.create();
        if (initNewNode(cGraph.getRoot(), dummy, Helper.bpTOP)) {
            return true;
        }
        // ... add edges with R and S...
        curNode = cGraph.getRoot();
        DlCompletionTreeArc edgeR = this.createOneNeighbour(R, dummy);
        DlCompletionTreeArc edgeS = this.createOneNeighbour(S, dummy);
        // init new nodes/edges. No need to apply restrictions, as no reasoning
        // have been done yet.
        if (initNewNode(edgeR.getArcEnd(), dummy, Helper.bpTOP)
                || initNewNode(edgeS.getArcEnd(), dummy, Helper.bpTOP)
                || setupEdge(edgeR, dummy, 0) || setupEdge(edgeS, dummy, 0)
                || merge(edgeS.getArcEnd(), edgeR.getArcEnd(), dummy)) {
            return true;
        }
        // 2 roles are disjoint if current setting is unsatisfiable
        curNode = null;
        return !this.runSat();
    }

    /** @param R
     * @return true if not satisfiable */
    @PortedFrom(file = "Reasoner.h", name = "checkIrreflexivity")
    public boolean checkIrreflexivity(Role R) {
        prepareReasoner();
        // use general method to init node...
        DepSet dummy = DepSet.create();
        if (initNewNode(cGraph.getRoot(), dummy, Helper.bpTOP)) {
            return true;
        }
        // ... add an R-loop
        curNode = cGraph.getRoot();
        DlCompletionTreeArc edgeR = this.createOneNeighbour(R, dummy);
        // init new nodes/edges. No need to apply restrictions, as no reasoning
        // have been done yet.
        if (initNewNode(edgeR.getArcEnd(), dummy, Helper.bpTOP)
                || setupEdge(edgeR, dummy, 0)
                || merge(edgeR.getArcEnd(), cGraph.getRoot(), dummy)) {
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
        assert curConceptConcept > 0 && cur.getType() == dtForall;
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

    /** add C to a set of session GCIs; init all nodes with (C,dep) */
    @PortedFrom(file = "Reasoner.h", name = "addSessionGCI")
    private boolean addSessionGCI(int C, DepSet dep) {
        SessionGCIs.add(C);
        int n = 0;
        DlCompletionTree node = null;
        while ((node = cGraph.getNode(n++)) != null) {
            if (isNodeGloballyUsed(node) && addToDoEntry(node, C, dep, "sg")) {
                return true;
            }
        }
        return false;
    }

    protected DlSatTester(TBox tbox, JFactReasonerConfiguration Options,
            DatatypeFactory datatypeFactory) {
        options = Options;
        this.datatypeFactory = datatypeFactory;
        tBox = tbox;
        dlHeap = tbox.getDLHeap();
        cGraph = new DlCompletionGraph(1, this);
        TODO = new ToDoList();
        newNodeCache = new ModelCacheIan(true, tbox.nC, tbox.nR,
                options.isRKG_USE_SIMPLE_RULES());
        newNodeEdges = new ModelCacheIan(false, tbox.nC, tbox.nR,
                options.isRKG_USE_SIMPLE_RULES());
        gcis = tbox.getGCIs();
        bContext = null;
        tryLevel = Helper.InitBranchingLevelValue;
        nonDetShift = 0;
        curNode = null;
        dagSize = 0;
        options.getLog().printTemplate(Templates.READCONFIG,
                options.getuseSemanticBranching(), options.getuseBackjumping(),
                options.getuseLazyBlocking(), options.getuseAnywhereBlocking());
        if (tBox.hasFC() && options.getuseAnywhereBlocking()) {
            options.setuseAnywhereBlocking(false);
            options.getLog().print(
                    "Fairness constraints: set useAnywhereBlocking = false\n");
        }
        cGraph.initContext(tbox.getnSkipBeforeBlock(), options.getuseLazyBlocking(),
                options.getuseAnywhereBlocking());
        tbox.getORM().fillReflexiveRoles(reflexiveRoles);
        resetSessionFlags();
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
        TODO.clear();
        used.clear();
        SessionGCIs.clear();
        curNode = null;
        bContext = null;
        tryLevel = Helper.InitBranchingLevelValue;
        // clear last session information
        resetSessionFlags();
    }

    /** try to add a concept to a label given by TAG; ~C can't appear in the
     * label
     * 
     * @param label
     * @param p
     * @return true if label contains p */
    @PortedFrom(file = "Reasoner.h", name = "findConcept")
    public boolean findConcept(CWDArray label, int p) {
        assert Helper.isCorrect(p); // sanity checking
        // constants are not allowed here
        assert p != Helper.bpTOP;
        assert p != Helper.bpBOTTOM;
        stats.getnLookups().inc();
        return label.contains(p);
    }

    @PortedFrom(file = "Reasoner.h", name = "checkAddedConcept")
    private AddConceptResult checkAddedConcept(CWDArray lab, int p, DepSet dep) {
        assert Helper.isCorrect(p); // sanity checking
        // constants are not allowed here
        assert p != Helper.bpTOP;
        assert p != Helper.bpBOTTOM;
        stats.getnLookups().inc();
        stats.getnLookups().inc();
        if (lab.contains(p)) {
            return AddConceptResult.acrExist;
        }
        int inv_p = -p;
        DepSet depset = lab.get(inv_p);
        if (depset != null) {
            clashSet = DepSet.plus(depset, dep);
            return AddConceptResult.acrClash;
        }
        return AddConceptResult.acrDone;
    }

    /** try to add a concept to a label given by TAG; ~C can't appear in the
     * label; setup clash-set if found */
    @PortedFrom(file = "Reasoner.h", name = "findConceptClash")
    private boolean findConceptClash(CWDArray lab, int bp, DepSet dep) {
        stats.getnLookups().inc();
        DepSet depset = lab.get(bp);
        if (depset != null) {
            clashSet = DepSet.plus(depset, dep);
            return true;
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "tryAddConcept")
    private AddConceptResult tryAddConcept(CWDArray lab, int bp, DepSet dep) {
        boolean canC = used.contains(bp);
        boolean canNegC = used.contains(-bp);
        if (canC) {
            if (canNegC) {
                return checkAddedConcept(lab, bp, dep);
            } else {
                stats.getnLookups().inc();
                return findConcept(lab, bp) ? AddConceptResult.acrExist
                        : AddConceptResult.acrDone;
            }
        } else {
            if (canNegC) {
                return findConceptClash(lab, -bp, dep) ? AddConceptResult.acrClash
                        : AddConceptResult.acrDone;
            } else {
                return AddConceptResult.acrDone;
            }
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "addToDoEntry")
    protected boolean addToDoEntry(DlCompletionTree n, int bp, DepSet dep, String reason) {
        if (bp == Helper.bpTOP) {
            return false;
        }
        if (bp == Helper.bpBOTTOM) {
            this.setClashSet(dep);
            logNCEntry(n, bp, dep, "x", dlHeap.get(bp).getType().getName());
            return true;
        }
        DLVertex v = dlHeap.get(bp);
        DagTag tag = v.getType();
        if (tag == DagTag.dtCollection) {
            if (bp < 0) {
                return false;
            }
            stats.getnTacticCalls().inc();
            DlCompletionTree oldNode = curNode;
            int oldConceptConcept = curConceptConcept;
            FastSetSimple oldConceptDepSetDelegate = curConceptDepSet.getDelegate();
            curNode = n;
            curConceptConcept = bp;
            curConceptDepSet = DepSet.create(curConceptDepSet);
            boolean ret = commonTacticBodyAnd(v);
            curNode = oldNode;
            curConceptConcept = oldConceptConcept;
            curConceptDepSet = DepSet.create(oldConceptDepSetDelegate);
            return ret;
        }
        switch (tryAddConcept(n.label().getLabel(tag), bp, dep)) {
            case acrClash:
                logNCEntry(n, bp, dep, "x", dlHeap.get(bp).getType().getName());
                return true;
            case acrExist:
                return false;
            case acrDone:
                return insertToDoEntry(n, bp, dep, tag, reason);
            default:
                throw new UnreachableSituationException();
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "insertToDoEntry")
    private boolean insertToDoEntry(DlCompletionTree n, int bp, DepSet dep, DagTag tag,
            String reason) {
        ConceptWDep p = new ConceptWDep(bp, dep);
        updateLevel(n, dep);
        cGraph.addConceptToNode(n, p, tag);
        used.add(bp);
        if (n.isCached()) {
            return correctCachedEntry(n);
        }
        TODO.addEntry(n, tag, p);
        if (n.isDataNode()) {
            return checkDataNode ? hasDataClash(n) : false;
        }
        logNCEntry(n, bp, dep, "+", reason);
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "canBeCached")
    private boolean canBeCached(DlCompletionTree node) {
        boolean shallow = true;
        int size = 0;
        // check whether node cache is allowed
        if (!tBox.isUseNodeCache()) {
            return false;
        }
        // nominal nodes can not be cached
        if (node.isNominalNode()) {
            return false;
        }
        stats.getnCacheTry().inc();
        List<ConceptWDep> list = node.beginl_sc();
        for (int i = 0; i < list.size(); i++) {
            ConceptWDep p = list.get(i);
            if (dlHeap.getCache(p.getConcept()) == null) {
                stats.getnCacheFailedNoCache().inc();
                options.getLog().printTemplate(Templates.CAN_BE_CACHED, p.getConcept());
                return false;
            }
            shallow &= dlHeap.getCache(p.getConcept()).shallowCache();
            ++size;
        }
        list = node.beginl_cc();
        for (int i = 0; i < list.size(); i++) {
            ConceptWDep p = list.get(i);
            if (dlHeap.getCache(p.getConcept()) == null) {
                stats.getnCacheFailedNoCache().inc();
                options.getLog().printTemplate(Templates.CAN_BE_CACHED, p.getConcept());
                return false;
            }
            shallow &= dlHeap.getCache(p.getConcept()).shallowCache();
            ++size;
        }
        if (shallow && size != 0) {
            stats.getnCacheFailedShallow().inc();
            options.getLog().print(" cf(s)");
            return false;
        }
        return true;
    }

    /** build cache of the node (it is known that caching is possible) in
     * newNodeCache */
    @PortedFrom(file = "Reasoner.h", name = "doCacheNode")
    private void doCacheNode(DlCompletionTree node) {
        List<DepSet> deps = new ArrayList<DepSet>();
        newNodeCache.clear();
        List<ConceptWDep> beginl_sc = node.beginl_sc();
        for (int i = 0; i < beginl_sc.size(); i++) {
            ConceptWDep p = beginl_sc.get(i);
            deps.add(p.getDep());
            ModelCacheState merge = newNodeCache.merge(dlHeap.getCache(p.getConcept()));
            if (merge != ModelCacheState.csValid) {
                if (merge == ModelCacheState.csInvalid) {
                    this.setClashSet(deps);
                }
                return;
            }
        }
        List<ConceptWDep> list = node.beginl_cc();
        for (int i = 0; i < list.size(); i++) {
            ConceptWDep p = list.get(i);
            deps.add(p.getDep());
            ModelCacheState merge = newNodeCache.merge(dlHeap.getCache(p.getConcept()));
            if (merge != ModelCacheState.csValid) {
                if (merge == ModelCacheState.csInvalid) {
                    this.setClashSet(deps);
                }
                return;
            }
        }
        // all concepts in label are mergable; now try to add input arc
        newNodeEdges.clear();
        newNodeEdges.initRolesFromArcs(node);
        newNodeCache.merge(newNodeEdges);
    }

    @PortedFrom(file = "Reasoner.h", name = "reportNodeCached")
    private ModelCacheState reportNodeCached(DlCompletionTree node) {
        doCacheNode(node);
        ModelCacheState status = newNodeCache.getState();
        switch (status) {
            case csValid:
                stats.getnCachedSat().inc();
                options.getLog().printTemplate(Templates.REPORT1, node.getId());
                break;
            case csInvalid:
                stats.getnCachedUnsat().inc();
                break;
            case csFailed:
            case csUnknown:
                stats.getnCacheFailed().inc();
                options.getLog().print(" cf(c)");
                status = ModelCacheState.csFailed;
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
        if (status == ModelCacheState.csFailed) {
            redoNodeLabel(n, "uc");
        }
        return status.usageByState();
    }

    @PortedFrom(file = "Reasoner.h", name = "hasDataClash")
    private boolean hasDataClash(DlCompletionTree node) {
        assert node != null && node.isDataNode();
        List<ConceptWDep> concepts = node.beginl_sc();
        int size = concepts.size();
        // shortcut: if only one non-and argument is passed and it's Literal,
        // return false without bothering
        DataTypeReasoner datatypeReasoner = new DataTypeReasoner(options);
        for (int i = 0; i < size; i++) {
            ConceptWDep r = concepts.get(i);
            DLVertex v = dlHeap.get(r.getConcept());
            NamedEntry dataEntry = dlHeap.get(r.getConcept()).getConcept();
            boolean positive = r.getConcept() > 0;
            if (datatypeReasoner.addDataEntry(positive, v.getType(), dataEntry,
                    r.getDep())) {
                this.setClashSet(datatypeReasoner.getClashSet());
                return true;
            }
        }
        boolean checkClash = datatypeReasoner.checkClash();
        if (checkClash) {
            this.setClashSet(datatypeReasoner.getClashSet());
        }
        return checkClash;
        // XXX not sure about this change
        // return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "runSat")
    protected boolean runSat() {
        testTimer.start();
        boolean result = checkSatisfiability();
        testTimer.stop();
        options.getLog().print("\nChecking time was ").print(testTimer.getResultTime())
                .print(" milliseconds");
        testTimer.reset();
        finaliseStatistic();
        if (result) {
            cGraph.print(options.getLog());
        }
        return result;
    }

    @PortedFrom(file = "Reasoner.h", name = "finaliseStatistic")
    private void finaliseStatistic() {
        cGraph.clearStatistics();
    }

    @PortedFrom(file = "Reasoner.h", name = "applyReflexiveRoles")
    private boolean applyReflexiveRoles(DlCompletionTree node, DepSet dep) {
        for (Role p : reflexiveRoles) {
            DlCompletionTreeArc pA = cGraph.addRoleLabel(node, node, false, p, dep);
            if (setupEdge(pA, dep, 0)) {
                return true;
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "checkSatisfiability")
    protected boolean checkSatisfiability() {
        int loop = 0;
        for (;;) {
            if (curNode == null) {
                // no applicable rules
                if (TODO.isEmpty()) {
                    // do run-once things
                    if (performAfterReasoning()) {
                        if (tunedRestore()) {
                            return false;
                        }
                    }
                    // if nothing added -- that's it
                    if (TODO.isEmpty()) {
                        return true;
                    }
                }
                ToDoEntry curTDE = TODO.getNextEntry();
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
            if (commonTactic()) {
                if (tunedRestore()) {
                    return false;
                }
            } else {
                curNode = null;
            }
        }
    }

    /** perform all the actions that should be done once, after all normal rules
     * are not applicable.
     * 
     * @return true if the concept is unsat */
    @PortedFrom(file = "Reasoner.h", name = "performAfterReasoning")
    private boolean performAfterReasoning() {
        // make sure all blocked nodes are still blocked
        logIndentation();
        options.getLog().print("ub:");
        cGraph.retestCGBlockedStatus();
        options.getLog().print("]");
        if (!TODO.isEmpty()) {
            return false;
        }
        // check if any split expansion rule could be fired
        if (!tBox.getSplits().empty()) {
            logIndentation();
            options.getLog().print("split:");
            boolean clash = checkSplitRules();
            options.getLog().print("]");
            if (clash) {
                return true;
            }
            if (!TODO.isEmpty()) {
                return false;
            }
        }
        if (options.isRKG_USE_FAIRNESS()) {
            // check fairness constraints
            if (tBox.hasFC()) {
                DlCompletionTree violator = null;
                // for every given FC, if it is violated, reject current model
                for (Concept p : tBox.getFairness()) {
                    violator = cGraph.getFCViolator(p.getpName());
                    if (violator != null) {
                        stats.getnFairnessViolations().inc();
                        // try to fix violators
                        if (addToDoEntry(violator, p.getpName(), getCurDepSet(), "fair")) {
                            return true;
                        }
                    }
                }
                if (!TODO.isEmpty()) {
                    return false;
                }
            }
        }
        return false;
    }

    // split code implementation
    /** apply split rule RULE to a reasoner.
     * 
     * @return true if clash was found */
    @PortedFrom(file = "Reasoner.h", name = "applySplitRule")
    private boolean applySplitRule(TSplitRule rule) {
        DepSet dep = rule.fireDep(SessionSignature, SessionSigDepSet);
        int bp = rule.bp() - 1;
        // p.bp points to Choose(C) node, p.bp-1 -- to the split node
        // split became active
        ActiveSplits.add(bp);
        // add corresponding choose to all
        if (addSessionGCI(bp + 1, dep)) {
            return true;
        }
        // make sure that all existing splits will be re-applied
        this.updateName(bp);
        return false;
    }

    /** check whether any split rules should be run and do it.
     * 
     * @return true iff clash was found */
    @PortedFrom(file = "Reasoner.h", name = "checkSplitRules")
    private boolean checkSplitRules() {
        if (splitRuleLevel == 0) {
            // 1st application OR return was made before previous set
            ActiveSplits.clear();
            SessionSignature.clear();
            SessionSigDepSet.clear();
            splitRuleLevel = getCurLevel();
        }
        // fills in session signature for current CGraph. combine dep-sets for
        // the same entities
        this.updateSessionSignature();
        // now for every split expansion rule check whether it can be fired
        for (TSplitRule p : tBox.getSplitRules().getRules()) {
            if (!ActiveSplits.contains(p.bp() - 1) && p.canFire(SessionSignature)) {
                if (applySplitRule(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "restoreBC")
    private void restoreBC() {
        curNode = bContext.node;
        if (bContext.concept == null) {
            curConceptConcept = Helper.bpINVALID;
            curConceptDepSet = DepSet.create();
        } else {
            curConceptConcept = bContext.concept.getConcept();
            curConceptDepSet = DepSet.create(bContext.concept.getDep());
        }
        if (!SessionGCIs.isEmpty()) {
            Helper.resize(SessionGCIs, bContext.SGsize);
        }
        updateBranchDep();
        bContext.nextOption();
    }

    @PortedFrom(file = "Reasoner.h", name = "save")
    protected void save() {
        cGraph.save();
        TODO.save();
        ++tryLevel;
        bContext = null;
        stats.getnStateSaves().inc();
        options.getLog().printTemplate(Templates.SAVE, getCurLevel() - 1);
        if (options.isDEBUG_SAVE_RESTORE()) {
            cGraph.print(options.getLog());
            options.getLog().print(TODO);
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "restore")
    protected void restore(int newTryLevel) {
        assert !stack.isEmpty();
        assert newTryLevel > 0;
        setCurLevel(newTryLevel);
        // update split level
        if (getCurLevel() < splitRuleLevel) {
            splitRuleLevel = 0;
        }
        bContext = stack.top(getCurLevel());
        restoreBC();
        cGraph.restore(getCurLevel());
        TODO.restore(getCurLevel());
        stats.getnStateRestores().inc();
        options.getLog().printTemplate(Templates.RESTORE, getCurLevel());
        if (options.isDEBUG_SAVE_RESTORE()) {
            cGraph.print(options.getLog());
            options.getLog().print(TODO);
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "logIndentation")
    private void logIndentation() {
        LogAdapter logAdapter = options.getLog();
        logAdapter.print("\n");
        for (int i = 1; i < getCurLevel(); i++) {
            logAdapter.print(' ');
        }
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
            logAdapter.print(v.getType().name());
            if (v.getConcept() != null) {
                logAdapter.print("(", v.getConcept().getName(), ")");
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

    /** @param o
     * @return reasoning time */
    @PortedFrom(file = "Reasoner.h", name = "printReasoningTime")
    public float printReasoningTime(LogAdapter o) {
        o.print("\n     SAT takes ", satTimer, " seconds\n     SUB takes ", subTimer,
                " seconds");
        return satTimer.calcDelta() + subTimer.calcDelta();
    }

    /** Tactics section; Each Tactic should have a (small) Usability function
     * <name> and a Real tactic function <name>Body Each tactic returns: - true
     * - if expansion of CUR lead to clash - false - overwise */
    @PortedFrom(file = "Reasoner.h", name = "commonTactic")
    private boolean commonTactic() {
        if (curNode.isCached() || curNode.isPBlocked()) {
            return false;
        }
        logStartEntry();
        boolean ret = false;
        if (!isIBlocked()) {
            ret = commonTacticBody(dlHeap.get(curConceptConcept));
        }
        logFinishEntry(ret);
        return ret;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBody")
    private boolean commonTacticBody(DLVertex cur) {
        stats.getnTacticCalls().inc();
        switch (cur.getType()) {
            case dtTop:
                throw new UnreachableSituationException();
            case dtDataType:
            case dtDataValue:
                stats.getnUseless().inc();
                return false;
            case dtPSingleton:
            case dtNSingleton:
                if (curConceptConcept > 0) {
                    return commonTacticBodySingleton(cur);
                } else {
                    return commonTacticBodyId(cur);
                }
            case dtNConcept:
            case dtPConcept:
                return commonTacticBodyId(cur);
            case dtAnd:
                if (curConceptConcept > 0) {
                    return commonTacticBodyAnd(cur);
                } else {
                    return commonTacticBodyOr(cur);
                }
            case dtForall:
                if (curConceptConcept < 0) {
                    return commonTacticBodySome(cur);
                }
                return commonTacticBodyAll(cur);
            case dtIrr:
                if (curConceptConcept < 0) {
                    return commonTacticBodySomeSelf(cur.getRole());
                } else {
                    return commonTacticBodyIrrefl(cur.getRole());
                }
            case dtLE:
                if (curConceptConcept < 0) {
                    return commonTacticBodyGE(cur);
                }
                if (isFunctionalVertex(cur)) {
                    return commonTacticBodyFunc(cur);
                } else {
                    return commonTacticBodyLE(cur);
                }
            case dtProj:
                assert curConceptConcept > 0;
                return commonTacticBodyProj(cur.getRole(), cur.getConceptIndex(),
                        cur.getProjRole());
            case dtSplitConcept:
                return commonTacticBodySplit(cur);
            case dtChoose:
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
        if (options.isRKG_USE_SIMPLE_RULES()) {
            // check if we have some simple rules
            if (curConceptConcept > 0 && applyExtraRulesIf((Concept) cur.getConcept())) {
                return true;
            }
        }
        // get either body(p) or inverse(body(p)), depends on sign of current ID
        int C = curConceptConcept > 0 ? cur.getConceptIndex() : -cur.getConceptIndex();
        return addToDoEntry(curNode, C, curConceptDepSet, null);
    }

    /** add entity.dep to a session structures */
    @PortedFrom(file = "Reasoner.h", name = "updateSessionSignature")
    private void updateSessionSignature(NamedEntity entity, DepSet dep) {
        if (entity != null) {
            SessionSignature.add(entity);
            SessionSigDepSet.get(entity).add(dep);
        }
    }

    /** update session signature with all names from a given node */
    @PortedFrom(file = "Reasoner.h", name = "updateSignatureByNode")
    private void updateSignatureByNode(DlCompletionTree node) {
        CGLabel lab = node.label();
        for (ConceptWDep p : lab.get_sc()) {
            this.updateSessionSignature(tBox.getSplitRules().getEntity(p.getConcept()),
                    p.getDep());
        }
    }

    /** update session signature for all non-data nodes */
    @PortedFrom(file = "Reasoner.h", name = "updateSessionSignature")
    private void updateSessionSignature() {
        int n = 0;
        DlCompletionTree node = null;
        while ((node = cGraph.getNode(n++)) != null) {
            if (isObjectNodeUnblocked(node)) {
                updateSignatureByNode(node);
            }
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "applicable")
    protected boolean applicable(SimpleRule rule) {
        CWDArray lab = curNode.label().getLabel(DagTag.dtPConcept);
        DepSet loc = DepSet.create(curConceptDepSet);
        for (Concept p : rule.getBody()) {
            if (p.getpName() != curConceptConcept) {
                if (findConceptClash(lab, p.getpName(), loc)) {
                    loc.add(clashSet);
                } else {
                    return false;
                }
            }
        }
        this.setClashSet(loc);
        return true;
    }

    @PortedFrom(file = "Reasoner.h", name = "applyExtraRules")
    private boolean applyExtraRules(Concept C) {
        FastSet er_begin = C.getExtraRules();
        for (int i = 0; i < er_begin.size(); i++) {
            SimpleRule rule = tBox.getSimpleRule(er_begin.get(i));
            stats.getnSRuleAdd().inc();
            if (rule.applicable(this)) {
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
        assert cur.getType() == dtPSingleton || cur.getType() == dtNSingleton;
        stats.getnSingletonCalls().inc();
        assert hasNominals();
        encounterNominal = true;
        Individual C = (Individual) cur.getConcept();
        assert C.getNode() != null;
        DepSet dep = DepSet.create(curConceptDepSet);
        // blank nodes are set to be non classifiable and not initialized in
        // initNominalCloud
        // XXX not sure how it should be fixed, just trying to avoid null
        // pointer here
        if (C.isNonClassifiable()) {
            return true;
        }
        DlCompletionTree realNode = C.getNode().resolvePBlocker(dep);
        if (!realNode.equals(curNode)) {
            return merge(curNode, realNode, dep);
        }
        return commonTacticBodyId(cur);
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyAnd")
    private boolean commonTacticBodyAnd(DLVertex cur) {
        assert curConceptConcept > 0 && cur.getType() == dtAnd; // safety check
        stats.getnAndCalls().inc();
        int[] begin = cur.begin();
        for (int q : begin) {
            if (addToDoEntry(curNode, q, curConceptDepSet, null)) {
                return true;
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyOr")
    private boolean commonTacticBodyOr(DLVertex cur) {
        // safety check
        assert curConceptConcept < 0 && cur.getType() == dtAnd;
        stats.getnOrCalls().inc();
        if (isFirstBranchCall()) {
            Reference<DepSet> dep = new Reference<DepSet>(DepSet.create());
            if (planOrProcessing(cur, dep)) {
                options.getLog().printTemplate(Templates.COMMON_TACTIC_BODY_OR,
                        orConceptsToTest.get(orConceptsToTest.size() - 1));
                return false;
            }
            if (orConceptsToTest.isEmpty()) {
                this.setClashSet(dep.getReference());
                return true;
            }
            if (orConceptsToTest.size() == 1) {
                ConceptWDep C = orConceptsToTest.get(0);
                return insertToDoEntry(curNode, C.getConcept(), dep.getReference(),
                        dlHeap.get(C.getConcept()).getType(), "bcp");
            }
            createBCOr();
            bContext.branchDep = DepSet.create(dep.getReference());
            orConceptsToTest = ((BCOr) bContext).setApplicableOrEntries(orConceptsToTest);
        }
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
            switch (tryAddConcept(lab.getLabel(dlHeap.get(inverse).getType()), inverse,
                    null)) {
                case acrClash:
                    // clash found -- OK
                    dep.getReference().add(clashSet);
                    continue;
                case acrExist:
                    // already have such concept -- save it to the 1st position
                    orConceptsToTest.clear();
                    orConceptsToTest.add(new ConceptWDep(-q));
                    return true;
                case acrDone:
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
                    // XXX should throw an exception only when debugging, but
                    // this is hard to figure out
                    // throw new
                    // UnreachableSituationException(curNode.toString());
                    // Both Exists and Clash are errors
                }
            }
        }
        // add new entry to current node; we know the result would be DONE
        if (options.isRKG_USE_DYNAMIC_BACKJUMPING()) {
            return addToDoEntry(curNode, bcOr.orCur().getConcept(), dep, reason);
        } else {
            return insertToDoEntry(curNode, bcOr.orCur().getConcept(), dep,
                    dlHeap.get(bcOr.orCur().getConcept()).getType(), reason);
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyAllComplex")
    private boolean commonTacticBodyAllComplex(DLVertex cur) {
        int state = cur.getState();
        // corresponds to AR{0}.X
        int C = curConceptConcept - state;
        RAStateTransitions RST = cur.getRole().getAutomaton().getBase().get(state);
        // apply all empty transitions
        if (RST.hasEmptyTransition()) {
            List<RATransition> list = RST.begin();
            for (int i = 0; i < list.size(); i++) {
                RATransition q = list.get(i);
                stats.getnAutoEmptyLookups().inc();
                if (q.isEmpty()) {
                    if (addToDoEntry(curNode, C + q.final_state(), curConceptDepSet, "e")) {
                        return true;
                    }
                }
            }
        }
        // apply all top-role transitions
        if (RST.hasTopTransition()) {
            List<RATransition> list = RST.begin();
            for (int i = 0; i < list.size(); i++) {
                RATransition q = list.get(i);
                if (q.isTop()) {
                    if (addSessionGCI(C + q.final_state(), curConceptDepSet)) {
                        return true;
                    }
                }
            }
        }
        // apply final-state rule
        if (state == 1) {
            if (addToDoEntry(curNode, cur.getConceptIndex(), curConceptDepSet, null)) {
                return true;
            }
        }
        // check whether automaton applicable to any edges
        stats.getnAllCalls().inc();
        // check all neighbours
        List<DlCompletionTreeArc> list = curNode.getNeighbour();
        for (int i = 0; i < list.size(); i++) {
            DlCompletionTreeArc p = list.get(i);
            if (RST.recognise(p.getRole())) {
                if (applyTransitions(p, RST, C,
                        DepSet.plus(curConceptDepSet, p.getDep()), null)) {
                    return true;
                }
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyAllSimple")
    private boolean commonTacticBodyAllSimple(DLVertex cur) {
        RAStateTransitions RST = cur.getRole().getAutomaton().getBase().get(0);
        int C = cur.getConceptIndex();
        // check whether automaton applicable to any edges
        stats.getnAllCalls().inc();
        // check all neighbours; as the role is simple then recognise() ==
        // applicable()
        List<DlCompletionTreeArc> neighbour = curNode.getNeighbour();
        int size = neighbour.size();
        for (int i = 0; i < size; i++) {
            DlCompletionTreeArc p = neighbour.get(i);
            if (RST.recognise(p.getRole())) {
                if (addToDoEntry(p.getArcEnd(), C,
                        DepSet.plus(curConceptDepSet, p.getDep()), null)) {
                    return true;
                }
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "applyTransitions")
    private boolean applyTransitions(DlCompletionTreeArc edge, RAStateTransitions RST,
            int C, DepSet dep, String reason) {
        Role R = edge.getRole();
        DlCompletionTree node = edge.getArcEnd();
        // fast lane: the single transition which is applicable
        if (RST.isSingleton()) {
            return addToDoEntry(node, C + RST.getTransitionEnd(), dep, reason);
        }
        // try to apply all transitions to edge
        List<RATransition> begin = RST.begin();
        int size = begin.size();
        for (int i = 0; i < size; i++) {
            RATransition q = begin.get(i);
            stats.getnAutoTransLookups().inc();
            if (q.applicable(R)) {
                if (addToDoEntry(node, C + q.final_state(), dep, reason)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Perform expansion of (\AR.C).DEP to an EDGE for simple R with a given
     * reason */
    @PortedFrom(file = "Reasoner.h", name = "applyUniversalNR")
    private boolean applyUniversalNR(DlCompletionTree Node,
            DlCompletionTreeArc arcSample, DepSet dep_, int flags) {
        // check whether a flag is set
        if (flags == 0) {
            return false;
        }
        Role R = arcSample.getRole();
        DepSet dep = DepSet.plus(dep_, arcSample.getDep());
        List<ConceptWDep> base = Node.beginl_cc();
        int size = base.size();
        for (int i = 0; i < size; i++) {
            ConceptWDep p = base.get(i);
            // need only AR.C concepts where ARC is labelled with R
            if (p.getConcept() < 0) {
                continue;
            }
            DLVertex v = dlHeap.get(p.getConcept());
            Role vR = v.getRole();
            switch (v.getType()) {
                case dtIrr:
                    if ((flags & redoIrr.getValue()) > 0) {
                        if (this.checkIrreflexivity(arcSample, vR, dep)) {
                            return true;
                        }
                    }
                    break;
                case dtForall: {
                    if ((flags & redoForall.getValue()) == 0) {
                        break;
                    }
                    if (vR.isTop()) {
                        break;
                    }
                    /** check whether transition is possible */
                    RAStateTransitions RST = vR.getAutomaton().getBase()
                            .get(v.getState());
                    if (!RST.recognise(R)) {
                        break;
                    }
                    if (vR.isSimple()) {
                        // R is recognised so just add the state!
                        if (addToDoEntry(arcSample.getArcEnd(), v.getConceptIndex(),
                                DepSet.plus(dep, p.getDep()), "ae")) {
                            return true;
                        }
                    } else {
                        if (applyTransitions(arcSample, RST,
                                p.getConcept() - v.getState(),
                                DepSet.plus(dep, p.getDep()), "ae")) {
                            return true;
                        }
                    }
                    break;
                }
                case dtLE:
                    if (isFunctionalVertex(v)) {
                        if ((flags & redoFunc.getValue()) > 0 && R.lesserequal(vR)) {
                            addExistingToDoEntry(Node, p, "f");
                        }
                    } else if ((flags & redoAtMost.getValue()) > 0 && R.lesserequal(vR)) {
                        addExistingToDoEntry(Node, p, "le");
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodySome")
    private boolean commonTacticBodySome(DLVertex cur) {
        Role R = cur.getRole();
        int C = -cur.getConceptIndex();
        if (R.isTop()) {
            return commonTacticBodySomeUniv(cur);
        }
        if (isSomeExists(R, C)) {
            return false;
        }
        if (C < 0 && dlHeap.get(C).getType() == dtAnd) {
            for (int q : dlHeap.get(C).begin()) {
                if (isSomeExists(R, -q)) {
                    return false;
                }
            }
        }
        if (C > 0 && tBox.testHasNominals()) {
            DLVertex nom = dlHeap.get(C);
            if (nom.getType() == dtPSingleton || nom.getType() == dtNSingleton) {
                return commonTacticBodyValue(R, (Individual) nom.getConcept());
            }
        }
        stats.getnSomeCalls().inc();
        if (R.isFunctional()) {
            List<Role> list = R.begin_topfunc();
            for (int i = 0; i < list.size(); i++) {
                int functional = list.get(i).getFunctional();
                switch (tryAddConcept(curNode.label().getLabel(DagTag.dtLE), functional,
                        curConceptDepSet)) {
                    case acrClash:
                        return true;
                    case acrDone: {
                        updateLevel(curNode, curConceptDepSet);
                        ConceptWDep rFuncRestriction1 = new ConceptWDep(functional,
                                curConceptDepSet);
                        cGraph.addConceptToNode(curNode, rFuncRestriction1, DagTag.dtLE);
                        used.add(rFuncRestriction1.getConcept());
                        options.getLog().printTemplate(Templates.COMMON_TACTIC_BODY_SOME,
                                rFuncRestriction1);
                    }
                        break;
                    case acrExist:
                        break;
                    default:
                        throw new UnreachableSituationException();
                }
            }
        }
        boolean rFunc = false;
        Role RF = R;
        ConceptWDep rFuncRestriction = null;
        List<ConceptWDep> list = curNode.beginl_cc();
        for (int i = 0; i < list.size(); i++) {
            ConceptWDep LC = list.get(i);
            DLVertex ver = dlHeap.get(LC.getConcept());
            if (LC.getConcept() > 0 && isFunctionalVertex(ver)
                    && R.lesserequal(ver.getRole())) {
                if (!rFunc || RF.lesserequal(ver.getRole())) {
                    rFunc = true;
                    RF = ver.getRole();
                    rFuncRestriction = LC;
                }
            }
        }
        if (rFunc) {
            DlCompletionTreeArc functionalArc = null;
            DepSet newDep = DepSet.create();
            for (int i = 0; i < curNode.getNeighbour().size() && functionalArc == null; i++) {
                DlCompletionTreeArc pr = curNode.getNeighbour().get(i);
                if (pr.isNeighbour(RF, newDep)) {
                    functionalArc = pr;
                }
            }
            if (functionalArc != null) {
                options.getLog().printTemplate(Templates.COMMON_TACTIC_BODY_SOME2,
                        rFuncRestriction);
                DlCompletionTree succ = functionalArc.getArcEnd();
                newDep.add(curConceptDepSet);
                if (R.isDisjoint() && checkDisjointRoleClash(curNode, succ, R, newDep)) {
                    return true;
                }
                functionalArc = cGraph.addRoleLabel(curNode, succ,
                        functionalArc.isPredEdge(), R, newDep);
                // adds concept to the end of arc
                if (addToDoEntry(succ, C, newDep, null)) {
                    return true;
                }
                // if new role label was added...
                if (!RF.equals(R)) {
                    // add Range and Domain of a new role; this includes
                    // functional, so remove it from the latter
                    if (initHeadOfNewEdge(curNode, R, newDep, "RD")) {
                        return true;
                    }
                    if (initHeadOfNewEdge(succ, R.inverse(), newDep, "RR")) {
                        return true;
                    }
                    /** check AR.C in both sides of functionalArc FIXME!! for
                     * simplicity, check the functionality here (see bEx017). It
                     * seems only necessary when R has several functional
                     * super-roles, so the condition can be simplified */
                    if (applyUniversalNR(curNode, functionalArc, newDep,
                            redoForall.getValue() | redoFunc.getValue())) {
                        return true;
                    }
                    /** if new role label was added to a functionalArc, some
                     * functional restrictions in the SUCC node might became
                     * applicable. See bFunctional1x test */
                    if (applyUniversalNR(
                            succ,
                            functionalArc.getReverse(),
                            newDep,
                            redoForall.getValue() | redoFunc.getValue()
                                    | redoAtMost.getValue())) {
                        return true;
                    }
                }
                return false;
            }
        }
        return createNewEdge(cur.getRole(), C, Redo.redoForall.getValue()
                | Redo.redoAtMost.getValue());
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyValue")
    private boolean commonTacticBodyValue(Role R, Individual nom) {
        DepSet dep = DepSet.create(curConceptDepSet);
        if (isCurNodeBlocked()) {
            return false;
        }
        stats.getnSomeCalls().inc();
        assert nom.getNode() != null;
        DlCompletionTree realNode = nom.getNode().resolvePBlocker(dep);
        if (R.isDisjoint() && checkDisjointRoleClash(curNode, realNode, R, dep)) {
            return true;
        }
        encounterNominal = true;
        DlCompletionTreeArc edge = cGraph.addRoleLabel(curNode, realNode, false, R, dep);
        // add all necessary concepts to both ends of the edge
        return setupEdge(edge, dep, redoForall.getValue() | redoFunc.getValue()
                | redoAtMost.getValue() | redoIrr.getValue());
    }

    /** expansion rule for the existential quantifier with universal role */
    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodySomeUniv")
    private boolean commonTacticBodySomeUniv(DLVertex cur) {
        // check blocking conditions
        if (isCurNodeBlocked()) {
            return false;
        }
        stats.getnSomeCalls().inc();
        int C = -cur.getConceptIndex();
        // check whether C is already in CGraph
        int i = 0;
        DlCompletionTree node;
        while ((node = cGraph.getNode(i++)) != null) {
            if (isObjectNodeUnblocked(node) && node.label().contains(C)) {
                return false;
            }
        }
        // make new node labelled with C
        node = cGraph.getNewNode();
        return initNewNode(node, curConceptDepSet, C);
    }

    @PortedFrom(file = "Reasoner.h", name = "createNewEdge")
    private boolean createNewEdge(Role R, int C, int flags) {
        if (isCurNodeBlocked()) {
            stats.getnUseless().inc();
            return false;
        }
        DlCompletionTreeArc pA = this.createOneNeighbour(R, curConceptDepSet);
        // add necessary label
        return initNewNode(pA.getArcEnd(), curConceptDepSet, C)
                || setupEdge(pA, curConceptDepSet, flags);
    }

    @PortedFrom(file = "Reasoner.h", name = "createOneNeighbour")
    private DlCompletionTreeArc createOneNeighbour(Role R, DepSet dep) {
        return this.createOneNeighbour(R, dep, DlCompletionTree.BLOCKABLE_LEVEL);
    }

    @PortedFrom(file = "Reasoner.h", name = "createOneNeighbour")
    private DlCompletionTreeArc createOneNeighbour(Role R, DepSet dep, int level) {
        boolean forNN = level != DlCompletionTree.BLOCKABLE_LEVEL;
        DlCompletionTreeArc pA = cGraph.createNeighbour(curNode, forNN, R, dep);
        DlCompletionTree node = pA.getArcEnd();
        if (forNN) {
            node.setNominalLevel(level);
        }
        if (R.isDataRole()) {
            node.setDataNode();
        }
        options.getLog().printTemplate(R.isDataRole() ? Templates.DN : Templates.CN,
                node.getId(), dep);
        return pA;
    }

    /** check whether current node is blocked */
    @PortedFrom(file = "Reasoner.h", name = "isCurNodeBlocked")
    private boolean isCurNodeBlocked() {
        if (!options.getuseLazyBlocking()) {
            return curNode.isBlocked();
        }
        if (!curNode.isBlocked() && curNode.isAffected()) {
            updateLevel(curNode, curConceptDepSet);
            cGraph.detectBlockedStatus(curNode);
        }
        return curNode.isBlocked();
    }

    @PortedFrom(file = "Reasoner.h", name = "applyAllGeneratingRules")
    private void applyAllGeneratingRules(DlCompletionTree node) {
        List<ConceptWDep> base = node.label().get_cc();
        for (int i = 0; i < base.size(); i++) {
            ConceptWDep p = base.get(i);
            if (p.getConcept() <= 0) {
                DLVertex v = dlHeap.get(p.getConcept());
                if (v.getType() == dtLE || v.getType() == dtForall) {
                    addExistingToDoEntry(node, p, "ubd");
                }
            }
        }
    }

    /** @param pA
     * @param dep
     * @param flags
     * @return false if all done */
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

    /** add necessary concepts to the head of the new EDGE */
    @PortedFrom(file = "Reasoner.h", name = "initHeadOfNewEdge")
    private boolean initHeadOfNewEdge(DlCompletionTree node, Role R, DepSet dep,
            String reason) {
        // if R is functional, then add FR with given DEP-set to NODE
        if (R.isFunctional()) {
            List<Role> begin_topfunc = R.begin_topfunc();
            int size = begin_topfunc.size();
            for (int i = 0; i < size; i++) {
                if (addToDoEntry(node, begin_topfunc.get(i).getFunctional(), dep, "fr")) {
                    return true;
                }
            }
        }
        // setup Domain for R
        if (addToDoEntry(node, R.getBPDomain(), dep, reason)) {
            return true;
        }
        if (!options.isRKG_UPDATE_RND_FROM_SUPERROLES()) {
            List<Role> list = R.getAncestor();
            for (int i = 0; i < list.size(); i++) {
                Role q = list.get(i);
                if (addToDoEntry(node, q.getBPDomain(), dep, reason)) {
                    return true;
                }
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
        if (isNNApplicable(cur.getRole(), Helper.bpTOP, curConceptConcept + 1)) {
            return commonTacticBodyNN(cur);
        }
        stats.getnFuncCalls().inc();
        if (isQuickClashLE(cur)) {
            return true;
        }
        findNeighbours(cur.getRole(), Helper.bpTOP, null);
        if (EdgesToMerge.size() < 2) {
            return false;
        }
        DlCompletionTreeArc q = EdgesToMerge.get(0);
        DlCompletionTree sample = q.getArcEnd();
        DepSet depF = DepSet.create(curConceptDepSet);
        depF.add(q.getDep());
        for (int i = 1; i < EdgesToMerge.size(); i++) {
            q = EdgesToMerge.get(i);
            if (!q.getArcEnd().isPBlocked()) {
                if (merge(q.getArcEnd(), sample, DepSet.plus(depF, q.getDep()))) {
                    return true;
                }
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyLE")
    private boolean commonTacticBodyLE(DLVertex cur) // for <=nR.C concepts
    {
        assert curConceptConcept > 0 && cur.getType() == dtLE;
        stats.getnLeCalls().inc();
        Role R = cur.getRole();
        if (R.isTop()) {
            return processTopRoleLE(cur);
        }
        int C = cur.getConceptIndex();
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
            if (C != bpTOP) {
                if (commonTacticBodyChoose(R, C)) {
                    return true;
                }
            }
            // check whether we need to apply NN rule first
            if (isNNApplicable(R, C, curConceptConcept + cur.getNumberLE())) {
                return commonTacticBodyNN(cur);
                // after application <=-rule would be checked again
            }
        }
        // we need to repeat merge until there will be necessary amount of edges
        while (true) {
            if (isFirstBranchCall()) {
                if (initLEProcessing(cur)) {
                    return false;
                }
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
            Reference<DepSet> dep = new Reference<DepSet>(DepSet.create());
            // empty dep-set
            // fast check for FROM =/= TO
            if (cGraph.nonMergable(from, to, dep)) {
                // need this for merging two nominal nodes
                dep.getReference().add(fromArc.getDep());
                dep.getReference().add(toArc.getDep());
                // add dep-set from labels
                if (C == bpTOP) {
                    setClashSet(dep.getReference());
                } else {
                    // QCR: update dep-set wrt C
                    // here we know that C is in both labels; set a proper
                    // clash-set
                    DagTag tag = dlHeap.get(C).getType();
                    boolean test;
                    // here dep contains the clash-set
                    test = findConceptClash(from.label().getLabel(tag), C,
                            dep.getReference());
                    assert test;
                    // save new dep-set
                    dep.getReference().add(clashSet);
                    test = findConceptClash(to.label().getLabel(tag), C,
                            dep.getReference());
                    assert test;
                    // both clash-sets are now in common clash-set
                }
                updateBranchDep();
                bContext.nextOption();
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
            /** it might be the case (see bIssue28) that after the merge there is
             * an R-neigbour that have neither C or ~C in its label (it was far
             * in the nominal cloud) */
            if (C != bpTOP) {
                if (commonTacticBodyChoose(R, C)) {
                    return true;
                }
            }
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "initLEProcessing")
    private boolean initLEProcessing(DLVertex cur) {
        DepSet dep = DepSet.create();
        // check the amount of neighbours we have
        findNeighbours(cur.getRole(), cur.getConceptIndex(), dep);
        // if the number of R-neighbours satisfies condition -- nothing to do
        if (EdgesToMerge.size() <= cur.getNumberLE()) {
            return true;
        }
        // init context
        createBCLE();
        bContext.branchDep.add(dep);
        // setup BCLE
        BCLE bcLE = (BCLE<DlCompletionTreeArc>) bContext;
        EdgesToMerge = bcLE.swap(EdgesToMerge);
        bcLE.resetMCI();
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyGE")
    private boolean commonTacticBodyGE(DLVertex cur) {
        // for >=nR.C concepts
        assert curConceptConcept < 0 && cur.getType() == dtLE;
        // check blocking conditions
        if (isCurNodeBlocked()) {
            return false;
        }
        Role R = cur.getRole();
        if (R.isTop()) {
            return processTopRoleGE(cur);
        }
        stats.getnGeCalls().inc();
        if (isQuickClashGE(cur)) {
            return true;
        }
        // create N new different edges
        return createDifferentNeighbours(cur.getRole(), cur.getConceptIndex(),
                curConceptDepSet, cur.getNumberGE(), DlCompletionTree.BLOCKABLE_LEVEL);
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
        DepSet dummy = DepSet.create();
        findCLabelledNodes(bpTOP, dummy);
        // check if we have nodes to merge
        if (NodesToMerge.size() < 2) {
            return false;
        }
        // merge all nodes to the first (the least wrt nominal hierarchy) found
        // node
        DlCompletionTree sample = NodesToMerge.get(0);
        DepSet dep = DepSet.create(curConceptDepSet);   // dep-set for merging
        // merge all elements to sample (sample wouldn't be merge)
        for (int i = 0; i < NodesToMerge.size(); i++) {
            // during merge EdgesToMerge may became purged (see Nasty4) => check
            // this
            if (!NodesToMerge.get(i).isPBlocked()) {
                if (merge(NodesToMerge.get(i), sample, dep)) {
                    return true;
                }
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "processTopRoleLE")
    private boolean processTopRoleLE(DLVertex cur) {
        // for <=nR.C concepts
        assert curConceptConcept > 0 && cur.getType() == dtLE;
        int C = cur.getConceptIndex();
        boolean needInit = true;
        if (!isFirstBranchCall()) {
            if (bContext instanceof BCLE) {
                // clash in LE-rule: skip the initial checks
                needInit = false;
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
            if (C != bpTOP) {
                if (applyChooseRuleGlobally(C)) {
                    return true;
                }
            }
        }
        // we need to repeat merge until there will be necessary amount of edges
        while (true) {
            if (isFirstBranchCall()) {
                if (initTopLEProcessing(cur)) {
                    return false;
                }
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
            Reference<DepSet> dep = new Reference<DepSet>(DepSet.create());
            // fast check for FROM =/= TO
            if (cGraph.nonMergable(from, to, dep)) {
                // add dep-set from labels
                if (C == bpTOP) {
                    setClashSet(dep.getReference());
                } else {
                    // QCR: update dep-set wrt C
                    // here we know that C is in both labels; set a proper
                    // clash-set
                    DagTag tag = dlHeap.get(C).getType();
                    boolean test;
                    // here dep contains the clash-set
                    test = findConceptClash(from.label().getLabel(tag), C,
                            dep.getReference());
                    assert test;
                    // save new dep-set
                    dep.getReference().add(clashSet);
                    test = findConceptClash(to.label().getLabel(tag), C,
                            dep.getReference());
                    assert test;
                    // both clash-sets are now in common clash-set
                }
                updateBranchDep();
                bContext.nextOption();
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
        assert curConceptConcept < 0 && cur.getType() == dtLE;
        assert !isCurNodeBlocked();
        stats.getnGeCalls().inc();
        if (isQuickClashGE(cur)) {
            return true;
        }
        // create N new different edges
        // FIXME!! for now
        return createDifferentNeighbours(cur.getRole(), cur.getConceptIndex(),
                curConceptDepSet, cur.getNumberGE(), DlCompletionTree.BLOCKABLE_LEVEL);
    }

    @PortedFrom(file = "Reasoner.h", name = "initTopLEProcessing")
    private boolean initTopLEProcessing(DLVertex cur) {
        DepSet dep = DepSet.create();
        // check the amount of neighbours we have
        findCLabelledNodes(cur.getConceptIndex(), dep);
        // if the number of R-neighbours satisfies condition -- nothing to do
        if (NodesToMerge.size() <= cur.getNumberLE()) {
            return true;
        }
        // init context
        createBCTopLE();
        bContext.branchDep.add(dep);
        // setup BCLE
        BCLE<DlCompletionTree> bcLE = (BCLE<DlCompletionTree>) bContext;
        NodesToMerge = bcLE.swap(NodesToMerge);
        bcLE.resetMCI();
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "createDifferentNeighbours")
    private boolean
            createDifferentNeighbours(Role R, int C, DepSet dep, int n, int level) {
        DlCompletionTreeArc pA = null;
        cGraph.initIR();
        for (int i = 0; i < n; ++i) {
            pA = this.createOneNeighbour(R, dep, level);
            DlCompletionTree child = pA.getArcEnd();
            cGraph.setCurIR(child, dep);
            // add necessary new node labels and setup new edge
            if (initNewNode(child, dep, C)) {
                return true;
            }
            if (setupEdge(pA, dep, redoForall.getValue())) {
                return true;
            }
        }
        cGraph.finiIR();
        // re-apply all <= NR in curNode; do it only once for all created nodes;
        // no need for Irr
        return applyUniversalNR(curNode, pA, dep,
                redoFunc.getValue() | redoAtMost.getValue());
    }

    @PortedFrom(file = "Reasoner.h", name = "isNRClash")
    private boolean isNRClash(DLVertex atleast, DLVertex atmost, ConceptWDep reason) {
        if (atmost.getType() != DagTag.dtLE || atleast.getType() != DagTag.dtLE) {
            return false;
        }
        if (!checkNRclash(atleast, atmost)) {
            return false;
        }
        this.setClashSet(DepSet.plus(curConceptDepSet, reason.getDep()));
        logNCEntry(curNode, reason.getConcept(), reason.getDep(), "x",
                dlHeap.get(reason.getConcept()).getType().getName());
        return true;
    }

    @PortedFrom(file = "Reasoner.h", name = "checkMergeClash")
    private boolean checkMergeClash(CGLabel from, CGLabel to, DepSet dep, int nodeId) {
        DepSet clashDep = DepSet.create(dep);
        boolean clash = false;
        List<ConceptWDep> list = from.get_sc();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ConceptWDep p = list.get(i);
            int inverse = -p.getConcept();
            if (used.contains(inverse)
                    && findConceptClash(to.getLabel(dtPConcept), inverse, p.getDep())) {
                clash = true;
                clashDep.add(clashSet);
                options.getLog().printTemplate(Templates.CHECK_MERGE_CLASH, nodeId,
                        p.getConcept(), DepSet.plus(clashSet, dep));
            }
        }
        list = from.get_cc();
        int ccsize = list.size();
        for (int i = 0; i < ccsize; i++) {
            ConceptWDep p = list.get(i);
            int inverse = -p.getConcept();
            if (used.contains(inverse)
                    && findConceptClash(to.getLabel(dtForall), inverse, p.getDep())) {
                clash = true;
                clashDep.add(clashSet);
                options.getLog().printTemplate(Templates.CHECK_MERGE_CLASH, nodeId,
                        p.getConcept(), DepSet.plus(clashSet, dep));
            }
        }
        if (clash) {
            this.setClashSet(clashDep);
        }
        return clash;
    }

    @PortedFrom(file = "Reasoner.h", name = "mergeLabels")
    private boolean mergeLabels(CGLabel from, DlCompletionTree to, DepSet dep) {
        CGLabel lab = to.label();
        CWDArray sc = lab.getLabel(dtPConcept);
        CWDArray cc = lab.getLabel(dtForall);
        if (!dep.isEmpty()) {
            cGraph.saveRareCond(sc.updateDepSet(dep));
            cGraph.saveRareCond(cc.updateDepSet(dep));
        }
        List<ConceptWDep> list = from.get_sc();
        for (int i = 0; i < list.size(); i++) {
            ConceptWDep p = list.get(i);
            int bp = p.getConcept();
            stats.getnLookups().inc();
            int index = sc.index(bp);
            if (index > -1) {
                if (!p.getDep().isEmpty()) {
                    cGraph.saveRareCond(sc.updateDepSet(index, p.getDep()));
                }
            } else {
                if (insertToDoEntry(to, bp, DepSet.plus(dep, p.getDep()), dlHeap.get(bp)
                        .getType(), "M")) {
                    return true;
                }
            }
        }
        list = from.get_cc();
        for (int i = 0; i < list.size(); i++) {
            ConceptWDep p = list.get(i);
            int bp = p.getConcept();
            stats.getnLookups().inc();
            int index = cc.index(bp);
            if (index > -1) {
                if (!p.getDep().isEmpty()) {
                    cGraph.saveRareCond(cc.updateDepSet(index, p.getDep()));
                }
            } else {
                if (insertToDoEntry(to, bp, DepSet.plus(dep, p.getDep()), dlHeap.get(bp)
                        .getType(), "M")) {
                    return true;
                }
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "merge")
    private boolean merge(DlCompletionTree from, DlCompletionTree to, DepSet depF) {
        assert !from.isPBlocked();
        assert !from.equals(to);
        assert to.getNominalLevel() <= from.getNominalLevel();
        options.getLog().printTemplate(Templates.MERGE, from.getId(), to.getId());
        stats.getnMergeCalls().inc();
        DepSet dep = DepSet.create(depF);
        Reference<DepSet> ref = new Reference<DepSet>(dep);
        if (cGraph.nonMergable(from, to, ref)) {
            this.setClashSet(ref.getReference());
            return true;
        }
        if (checkMergeClash(from.label(), to.label(), depF, to.getId())) {
            return true;
        }
        // copy all node labels
        if (mergeLabels(from.label(), to, depF)) {
            return true;
        }
        List<DlCompletionTreeArc> edges = new ArrayList<DlCompletionTreeArc>();
        cGraph.merge(from, to, depF, edges);
        int size = edges.size();
        for (int i = 0; i < size; i++) {
            DlCompletionTreeArc q = edges.get(i);
            if (q.getRole().isDisjoint()
                    && checkDisjointRoleClash(q.getReverse().getArcEnd(), q.getArcEnd(),
                            q.getRole(), depF)) {
                {
                    // XXX dubious
                    return true;
                }
            }
        }
        if (to.isDataNode()) {
            return hasDataClash(to);
        }
        for (DlCompletionTreeArc q : edges) {
            if (applyUniversalNR(to, q, depF, redoForall.getValue() | redoFunc.getValue()
                    | redoAtMost.getValue() | redoIrr.getValue())) {
                return true;
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "checkDisjointRoleClash")
    protected boolean checkDisjointRoleClash(DlCompletionTree from, DlCompletionTree to,
            Role R, DepSet dep) {
        for (DlCompletionTreeArc p : from.getNeighbour()) {
            if (checkDisjointRoleClash(p, to, R, dep)) {
                return true;
            }
        }
        return false;
    }

    @PortedFrom(file = "Tactic.cpp", name = "isNewEdge")
    private boolean isNewEdge(DlCompletionTree node, List<DlCompletionTreeArc> e) {
        int size = e.size();
        for (int i = 0; i < size; i++) {
            if (e.get(i).getArcEnd().equals(node)) {
                return false;
            }
        }
        return true;
    }

    @PortedFrom(file = "Reasoner.h", name = "findNeighbours")
    private void findNeighbours(Role Role, int c, DepSet Dep) {
        EdgesToMerge.clear();
        DagTag tag = dlHeap.get(c).getType();
        List<DlCompletionTreeArc> neighbour = curNode.getNeighbour();
        int size = neighbour.size();
        for (int i = 0; i < size; i++) {
            DlCompletionTreeArc p = neighbour.get(i);
            if (p.isNeighbour(Role) && isNewEdge(p.getArcEnd(), EdgesToMerge)
                    && findChooseRuleConcept(p.getArcEnd().label().getLabel(tag), c, Dep)) {
                EdgesToMerge.add(p);
            }
        }
        Collections.sort(EdgesToMerge, new EdgeCompare());
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyChoose")
    private boolean commonTacticBodyChoose(Role R, int C) {
        List<DlCompletionTreeArc> neighbour = curNode.getNeighbour();
        int size = neighbour.size();
        for (int i = 0; i < size; i++) {
            DlCompletionTreeArc p = neighbour.get(i);
            if (p.isNeighbour(R)) {
                if (applyChooseRule(p.getArcEnd(), C)) {
                    return true;
                }
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "applyChooseRule")
    private boolean applyChooseRule(DlCompletionTree node, int C) {
        if (node.isLabelledBy(C) || node.isLabelledBy(-C)) {
            return false;
        }
        if (isFirstBranchCall()) {
            createBCCh();
            save();
            return addToDoEntry(node, -C, getCurDepSet(), "cr0");
        } else {
            prepareBranchDep();
            DepSet dep = DepSet.create(getBranchDep());
            determiniseBranchingOp();
            return addToDoEntry(node, C, dep, "cr1");
        }
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyNN")
    private boolean commonTacticBodyNN(DLVertex cur) {
        stats.getnNNCalls().inc();
        if (isFirstBranchCall()) {
            createBCNN();
        }
        BCNN bcNN = (BCNN) bContext;
        if (bcNN.noMoreNNOptions(cur.getNumberLE())) {
            useBranchDep();
            return true;
        }
        int NN = bcNN.getBranchIndex();
        save();
        // new (just branched) dep-set
        DepSet curDep = getCurDepSet();
        // make a stopper to mark that NN-rule is applied
        if (addToDoEntry(curNode, curConceptConcept + cur.getNumberLE(), DepSet.create(),
                "NNs")) {
            return true;
        }
        // create curNN new different edges
        if (createDifferentNeighbours(cur.getRole(), cur.getConceptIndex(), curDep, NN,
                curNode.getNominalLevel() + 1)) {
            return true;
        }
        // now remember NR we just created: it is (<= curNN R), so have to find
        // it
        return addToDoEntry(curNode, curConceptConcept + cur.getNumberLE() - NN, curDep,
                "NN");
    }

    @PortedFrom(file = "Reasoner.h", name = "isNNApplicable")
    protected boolean isNNApplicable(Role r, int C, int stopper) {
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
        for (DlCompletionTreeArc p : curNode.getNeighbour()) {
            DlCompletionTree suspect = p.getArcEnd();
            // if there is an edge that require to run the rule, then we need it
            if (p.isPredEdge() && suspect.isBlockableNode() && p.isNeighbour(r)
                    && suspect.isLabelledBy(C)) {
                options.getLog().print(" NN(").print(suspect.getId()).print(")");
                return true;
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodySomeSelf")
    private boolean commonTacticBodySomeSelf(Role R) {
        if (isCurNodeBlocked()) {
            return false;
        }
        for (DlCompletionTreeArc p : curNode.getNeighbour()) {
            if (p.getArcEnd().equals(curNode) && p.isNeighbour(R)) {
                return false;
            }
        }
        DepSet dep = DepSet.create(curConceptDepSet);
        DlCompletionTreeArc pA = cGraph.createLoop(curNode, R, dep);
        return setupEdge(pA, dep, redoForall.getValue() | redoFunc.getValue()
                | redoAtMost.getValue() | redoIrr.getValue());
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyIrrefl")
    private boolean commonTacticBodyIrrefl(Role R) {
        for (DlCompletionTreeArc p : curNode.getNeighbour()) {
            if (this.checkIrreflexivity(p, R, curConceptDepSet)) {
                return true;
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodyProj")
    private boolean commonTacticBodyProj(Role R, int C, Role ProjR) {
        if (curNode.isLabelledBy(-C)) {
            return false;
        }
        // checkProjection() might change curNode's edge vector and thusly
        // invalidate iterators
        for (int i = 0; i < curNode.getNeighbour().size(); i++) {
            if (curNode.getNeighbour().get(i).isNeighbour(R)) {
                if (checkProjection(curNode.getNeighbour().get(i), C, ProjR)) {
                    return true;
                }
            }
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "checkProjection")
    private boolean checkProjection(DlCompletionTreeArc pA, int C, Role ProjR) {
        if (pA.isNeighbour(ProjR)) {
            return false;
        }
        if (curNode.isLabelledBy(-C)) {
            return false;
        }
        DepSet dep = DepSet.create(curConceptDepSet);
        dep.add(pA.getDep());
        if (!curNode.isLabelledBy(C)) {
            if (isFirstBranchCall()) {
                createBCCh();
                save();
                return addToDoEntry(curNode, -C, getCurDepSet(), "cr0");
            } else {
                prepareBranchDep();
                dep.add(getBranchDep());
                determiniseBranchingOp();
                if (addToDoEntry(curNode, C, dep, "cr1")) {
                    return true;
                }
            }
        }
        DlCompletionTree child = pA.getArcEnd();
        return setupEdge(
                cGraph.addRoleLabel(curNode, child, pA.isPredEdge(), ProjR, dep), dep,
                redoForall.getValue() | redoFunc.getValue() | redoAtMost.getValue()
                        | redoIrr.getValue());
    }

    /** create BC for LE-rule */
    @PortedFrom(file = "Reasoner.h", name = "createBCTopLE")
    public void createBCTopLE() {
        bContext = stack.pushTopLE();
        initBC(bContext);
    }

    /** expansion rule for split */
    @PortedFrom(file = "Reasoner.h", name = "commonTacticBodySplit")
    private boolean commonTacticBodySplit(DLVertex cur) {
        if (tBox.isDuringClassification()
                && !ActiveSplits.contains(curConceptConcept > 0 ? curConceptConcept
                        : -curConceptConcept)) {
            return false;
        }
        DepSet dep = curConceptDepSet;
        boolean pos = curConceptConcept > 0;
        for (int q : cur.begin()) {
            if (addToDoEntry(curNode, Helper.createBiPointer(q, pos), dep, null)) {
                return true;
            }
        }
        return false;
    }

    /** apply choose-rule for all vertices (necessary for Top role in QCR) */
    @PortedFrom(file = "Reasoner.h", name = "applyChooseRuleGlobally")
    private boolean applyChooseRuleGlobally(int C) {
        int i = 0;
        DlCompletionTree p = cGraph.getNode(i++);
        while (p != null) {
            if (isObjectNodeUnblocked(p)) {
                if (applyChooseRule(p, C)) {
                    return true;
                }
            }
            p = cGraph.getNode(i++);
        }
        return false;
    }

    @PortedFrom(file = "Reasoner.h", name = "findCLabelledNodes")
    private void findCLabelledNodes(int C, DepSet Dep) {
        NodesToMerge.clear();
        DagTag tag = dlHeap.get(C).getType();
        // FIXME!! do we need this for d-blocked nodes?
        int i = 0;
        DlCompletionTree arc = cGraph.getNode(i++);
        while (arc != null) {
            if (isNodeGloballyUsed(arc)
                    && findChooseRuleConcept(arc.label().getLabel(tag), C, Dep)) {
                NodesToMerge.add(arc);
            }
            arc = cGraph.getNode(i++);
        }
        // sort EdgesToMerge: From named nominals to generated nominals
        // to blockable nodes
        Collections.sort(NodesToMerge, new NodeCompare());
    }
}
