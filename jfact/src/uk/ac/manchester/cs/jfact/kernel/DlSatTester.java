package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.DagTag.*;
import static uk.ac.manchester.cs.jfact.kernel.Redo.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.reasoner.TimeOutException;

import uk.ac.manchester.cs.jfact.datatypes.DataTypeReasoner;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.FastSet;
import uk.ac.manchester.cs.jfact.helpers.FastSetFactory;
import uk.ac.manchester.cs.jfact.helpers.FastSetSimple;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Reference;
import uk.ac.manchester.cs.jfact.helpers.SaveStack;
import uk.ac.manchester.cs.jfact.helpers.Stats;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.helpers.Timer;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.TBox.SimpleRule;
import uk.ac.manchester.cs.jfact.kernel.ToDoList.ToDoEntry;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomConceptInclusion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheConst;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheIan;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheState;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.split.ModuleType;
import uk.ac.manchester.cs.jfact.split.SyntacticLocalityChecker;
import uk.ac.manchester.cs.jfact.split.TModularizer;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.split.TSplitRules.TSplitRule;
import uk.ac.manchester.cs.jfact.split.TSplitVar;

public class DlSatTester {
    private final class LocalFastSet implements FastSet {
        final BitSet pos = new BitSet();

        public LocalFastSet() {}

        public int[] toIntArray() {
            throw new UnsupportedOperationException();
        }

        public int size() {
            throw new UnsupportedOperationException();
        }

        public void removeAt(final int o) {
            throw new UnsupportedOperationException();
        }

        public void removeAllValues(final int... values) {
            throw new UnsupportedOperationException();
        }

        public void removeAll(final int i, final int end) {
            throw new UnsupportedOperationException();
        }

        public void remove(final int o) {
            throw new UnsupportedOperationException();
        }

        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        public boolean intersect(final FastSet f) {
            throw new UnsupportedOperationException();
        }

        public int get(final int i) {
            throw new UnsupportedOperationException();
        }

        public boolean containsAny(final FastSet c) {
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(final FastSet c) {
            throw new UnsupportedOperationException();
        }

        private final int asPositive(final int p) {
            return p >= 0 ? 2 * p : 1 - 2 * p;
        }

        public boolean contains(final int o) {
            return pos.get(asPositive(o));
        }

        public void clear() {
            pos.clear();
        }

        public void addAll(final FastSet c) {
            throw new UnsupportedOperationException();
        }

        public void add(final int e) {
            pos.set(asPositive(e));
        }

        public void completeSet(final int value) {
            for (int i = 0; i <= value; i++) {
                pos.set(i);
            }
        }
    }

    /** Enum for usage the Tactics to a ToDoEntry */
    abstract class BranchingContext {
        /** currently processed node */
        protected DlCompletionTree node;
        /** currently processed concept */
        protected ConceptWDep concept = null;
        /** dependences for branching clashes */
        protected DepSet branchDep = DepSet.create();
        /// size of a session GCIs vector
        int SGsize;

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

    abstract class BCChoose extends BranchingContext {}

    /** stack to keep BContext */
    final class BCStack extends SaveStack<BranchingContext> {
        /** single entry for the barrier (good for nominal reasoner) */
        private final BCBarrier bcBarrier;

        /** push method to use */
        @Override
        public void push(final BranchingContext p) {
            p.init();
            initBC(p);
            super.push(p);
        }

        BCStack() {
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
        protected BranchingContext pushLE() {
            BCLE e = new BCLE();
            push(e);
            return e;
        }

        /** get BC for Choose-rule */
        protected BranchingContext pushCh() {
            BCChoose c = new BCChoose() {
                @Override
                public void nextOption() {}

                @Override
                public void init() {}
            };
            push(c);
            return c;
        }

        /** get BC for the barrier */
        protected BranchingContext pushBarrier() {
            push(bcBarrier);
            return bcBarrier;
        }
    }

    final class BCBarrier extends BranchingContext {
        @Override
        public void init() {}

        @Override
        public void nextOption() {}
    }

    final class BCLE extends BranchingContext {
        /** current branching index; used in several branching rules */
        private int branchIndex;
        /** index of a merge-candidate (in LE concept) */
        private int mergeCandIndex;
        /** vector of edges to be merged */
        private List<DlCompletionTreeArc> edges = new ArrayList<DlCompletionTreeArc>();

        /** init tag and indeces */
        @Override
        public void init() {
            branchIndex = 0;
            mergeCandIndex = 0;
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
        protected DlCompletionTreeArc getFrom() {
            return edges.get(mergeCandIndex);
        }

        /** get FROM pointer to merge */
        protected DlCompletionTreeArc getTo() {
            return edges.get(branchIndex);
        }

        /** check if the LE has no option to process */
        protected boolean noMoreLEOptions() {
            return mergeCandIndex <= branchIndex;
        }

        protected List<DlCompletionTreeArc> getEdgesToMerge() {
            return edges;
        }

        protected void setEdgesToMerge(final List<DlCompletionTreeArc> edgesToMerge) {
            edges = edgesToMerge;
        }
    }

    final class BCNN extends BranchingContext {
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
        protected boolean noMoreNNOptions(final int n) {
            return branchIndex > n;
        }

        protected int getBranchIndex() {
            return branchIndex;
        }

        public void setBranchIndex(final int branchIndex) {
            this.branchIndex = branchIndex;
        }
    }

    final class BCOr extends BranchingContext {
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

        /** 1st element of OrIndex */
        //		protected List<ConceptWDep> getApplicableOrEntries() {
        //			return applicableOrEntries;
        //		}
        protected int[] getApplicableOrEntriesConcepts() {
            int[] toReturn = new int[branchIndex];
            for (int i = 0; i < toReturn.length; i++) {
                toReturn[i] = applicableOrEntries.get(i).getConcept();
            }
            return toReturn;
        }

        protected List<ConceptWDep> setApplicableOrEntries(final List<ConceptWDep> list) {
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

    //	typedef std::set<const TNamedEntity*> SigSet;
    //	typedef std::vector<const TNamedEntity*> SigVec;
    /// class to check whether there is a need to unsplit splitted var
    class SingleSplit {
        /// signature of equivalent part of the split
        Set<NamedEntity> eqSig;
        /// signature of subsumption part of the split
        Set<NamedEntity> impSig;
        /// pointer to split vertex to activate
        int bp;

        SingleSplit() {}

        SingleSplit(final Set<NamedEntity> es, final Set<NamedEntity> is, final int p) {
            eqSig = new HashSet<NamedEntity>(es);
            impSig = new HashSet<NamedEntity>(is);
            bp = p;
        }
    }

    private final List<SingleSplit> SplitRules = new ArrayList<SingleSplit>();
    /// GCIs local to session
    private final List<Integer> SessionGCIs = new ArrayList<Integer>();
    /// set of active splits
    private final FastSet ActiveSplits = FastSetFactory.create();
    /// concept signature of current CGraph
    private final Set<NamedEntity> SessionSignature = new HashSet<NamedEntity>();
    /// signature to dep-set map for current session
    private final Map<NamedEntity, DepSet> SessionSigDepSet = new HashMap<NamedEntity, DepSet>();
    private final Set<NamedEntity> ActiveSignature = new HashSet<NamedEntity>();
    /// signature related to a split
    private final Set<NamedEntity> PossibleSignature = new HashSet<NamedEntity>();
    /// map between BP and TNamedEntities
    private final List<NamedEntity> EntityMap = new ArrayList<NamedEntity>();
    /// flag for using active signature
    boolean useActiveSignature;
    /// let reasoner know that we are in the classificaton (for splits)
    boolean duringClassification;

    // split rules support
    //	/// update active signature wrt given entity
    boolean updateActiveSignature(final NamedEntity entity, final DepSet dep) {
        if (entity == null || // not a named one
                ActiveSignature.contains(entity)) {
            return false;
        }
        return updateActiveSignature1(entity, dep);
    }

    /// add new split rule
    void addSplitRule(final Set<NamedEntity> eqSig, final Set<NamedEntity> impSig,
            final int bp) {
        SplitRules.add(new SingleSplit(eqSig, impSig, bp));
    }

    /// build a set out of signature SIG w/o given ENTITY
    Set<NamedEntity> buildSet(final TSignature sig, final NamedEntity entity) {
        Set<NamedEntity> set = new HashSet<NamedEntity>();
        //		std::cout << "Building set for " << entity.getName() << "\n";
        for (NamedEntity p : sig.begin()) {
            if (!p.equals(entity) && p instanceof ConceptName) {
                //				std::cout << "In the set: " << (*p).getName() << "\n";
                set.add(p);
            }
        }
        //		std::cout << "done\n";
        // register all elements in the set in PossibleSignature
        PossibleSignature.addAll(set);
        return set;
    }

    /// init split as a set-of-sets
    void initSplit(final TSplitVar split) {
        //		std::cout << "Processing split for " << split.oldName.getName() << ":\n";
        //TSplitVar::iterator p = split.begin(), p_end = split.end();
        List<TSplitVar.Entry> vars = split.getEntries();
        TSplitVar.Entry p = null;
        if (vars.size() > 0) {
            p = vars.get(0);
            Set<NamedEntity> impSet = buildSet(p.sig, p.name);
            int bp = split.C.getpBody() + 1; // choose-rule stays next to a split-definition of C
            for (int i = 1; i < vars.size(); i++) {
                p = vars.get(i);
                if (p.Module.size() == 1) {
                    addSplitRule(buildSet(p.sig, p.name), impSet, bp);
                } else {
                    // make set of all the seed signatures of for p.Module
                    Set<TSignature> Out = new HashSet<TSignature>();
                    // prepare vector of available entities
                    List<NamedEntity> Allowed = new ArrayList<NamedEntity>();
                    //				std::cout << "\n\n\nMaking split for module with " << p.name.getName();
                    List<uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom> Module = new ArrayList<uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom>(
                            p.Module);
                    // prepare signature for the process
                    TSignature sig = p.sig;
                    prepareStartSig(Module, sig, Allowed);
                    // build all the seed sigs for p.sig
                    BuildAllSeedSigs(Allowed, sig, Module, Out);
                    for (TSignature q : Out) {
                        addSplitRule(buildSet(q, p.name), impSet, bp);
                    }
                }
            }
        }
    }

    /// init splits
    void initSplits() {
        for (TSplitVar p : tBox.getSplits().getEntries()) {
            initSplit(p);
        }
        // now mark all the entities not in PossibleSignatures NULLs
        for (int i = 1; i < EntityMap.size(); ++i) {
            if (!PossibleSignature.contains(EntityMap.get(i))) {
                EntityMap.set(i, null);
            }
        }
    }

    /// check whether split-set S contains in the active set
    boolean containsInActive(final Set<NamedEntity> S) {
        return ActiveSignature.containsAll(S);
    }// includes ( ActiveSignature, S ); }

    /// check whether split-set S intersects with the active set
    boolean intersectsWithActive(final Collection<? extends NamedEntity> S) {
        for (NamedEntity e : S) {
            if (ActiveSignature.contains(e)) {
                return true;
            }
        }
        return false;
    }

    /// @return named entity corresponding to a given bp
    NamedEntity getEntity(final int bp) {
        return EntityMap.get(bp > 0 ? bp : -bp);
    }

    /// put TODO entry for either BP or inverse(BP) in NODE's label
    void updateName(final DlCompletionTree node, final int bp) {
        CGLabel lab = node.label();
        //CGLabel::const_iterator p;
        ConceptWDep c = lab.getConceptWithBP(bp);
        if (c == null) {
            c = lab.getConceptWithBP(-bp);
        }
        if (c != null) {
            addExistingToDoEntry(node, c, "sp");
        }
    }

    /// re-do every BP or inverse(BP) in labels of CGraph
    void updateName(final int bp) {
        int n = 0;
        DlCompletionTree node = null;
        while ((node = cGraph.getNode(n++)) != null) {
            if (!node.isDataNode()) {
                this.updateName(node, bp);
            }
        }
    }

    /// prepare start signature
    void prepareStartSig(
            final List<uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom> Module,
            final TSignature sig, final List<NamedEntity> Allowed) {
        // remove all defined concepts from signature
        for (uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom p : Module) {
            //			AxiomEquivalentConcepts ax = dynamic_cast<const TDLAxiomEquivalentConcepts*>(*p);
            if (p instanceof AxiomEquivalentConcepts) {
                for (ConceptExpression q : ((AxiomEquivalentConcepts) p).getArguments()) {
                    // FIXME!! check for the case A=B for named classes
                    if (q instanceof ConceptName) {
                        sig.remove((ConceptName) q);
                    }
                }
            } else {
                if (p instanceof AxiomConceptInclusion) {
                    AxiomConceptInclusion ci = (AxiomConceptInclusion) p;
                    // don't need the left-hand part either if it is a name
                    if (ci.getSubConcept() instanceof ConceptName) {
                        sig.remove((ConceptName) ci.getSubConcept());
                    }
                }
            }
        }
        // now put every concept name into Allowed
        for (NamedEntity r : sig.begin()) {
            if (r instanceof ConceptName) {
                Allowed.add(r);
            }
        }
    }

    /// build all the seed signatures
    void BuildAllSeedSigs(final List<NamedEntity> Allowed, final TSignature StartSig,
            final List<uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom> Module,
            final Set<TSignature> Out) {
        // copy the signature
        TSignature sig = new TSignature(StartSig);
        //		std::cout << "\nBuilding seed signatures:";
        // create a set of allowed entities for the next round
        List<NamedEntity> RecAllowed = new ArrayList<NamedEntity>(), Keepers = new ArrayList<NamedEntity>();
        Set<uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom> outModule = new HashSet<uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom>();
        //TODO add configuration options
        TModularizer mod = new TModularizer(new SyntacticLocalityChecker(sig));
        for (NamedEntity p : Allowed) {
            if (sig.containsNamedEntity(p)) {
                sig.remove(p);
                outModule.addAll(mod.extractModule(Module, sig, ModuleType.M_STAR));
                if (outModule.size() == Module.size()) { // possible to remove one
                    //					std::cout << "remove";
                    RecAllowed.add(p);
                } else {
                    //					std::cout << "keep";
                    Keepers.add(p);
                }
                sig.add(p);
            }
        }
        if (RecAllowed.isEmpty()) // minimal seed signature
        {
            Out.add(StartSig);
            return;
        }
        if (!Keepers.isEmpty()) {
            for (NamedEntity p : RecAllowed) {
                sig.remove(p);
            }
            outModule.addAll(mod.extractModule(Module, sig, ModuleType.M_STAR));
            if (outModule.size() == Module.size()) {
                Out.add(sig);
                return;
            }
        }
        // need to try smaller sigs
        sig = StartSig;
        for (NamedEntity p : RecAllowed) {
            sig.remove(p);
            BuildAllSeedSigs(RecAllowed, sig, Module, Out);
            sig.add(p);
        }
    }

    /** host TBox */
    protected final TBox tBox;
    /** link to dag from TBox */
    protected final DLDag dlHeap;
    /** all the reflexive roles */
    private final List<Role> reflexiveRoles = new ArrayList<Role>();
    /** Completion Graph of tested concept(s) */
    protected final DlCompletionGraph cGraph;
    /** Todo list */
    private final ToDoList TODO;
    private final FastSet used = new LocalFastSet();
    /** GCI-related KB flags */
    private final KBFlags gcis;
    /** record nodes that were processed during Cascaded Cache construction */
    private final FastSet inProcess = FastSetFactory.create();
    /** timer for the SAT tests (ie, cache creation) */
    private final Timer satTimer = new Timer();
    /** timer for the SUB tests (ie, general subsumption) */
    private final Timer subTimer = new Timer();
    /** timer for a single test; use it as a timeout checker */
    private final Timer testTimer = new Timer();
    // save/restore option
    /** stack for the local reasoner's state */
    protected final BCStack stack = new BCStack();
    /** context from the restored branching rule */
    protected BranchingContext bContext;
    /** index of last non-det situation */
    private int tryLevel;
    /** shift in order to determine the 1st non-det application */
    protected int nonDetShift;
    /// last level when split rules were applied
    private int splitRuleLevel;
    // current values
    /** currently processed CTree node */
    protected DlCompletionTree curNode;
    /** currently processed Concept */
    private DepSet curConceptDepSet;
    private int curConceptConcept;
    /** last processed d-blocked node */
    //private DlCompletionTree dBlocked;
    /** size of the DAG with some extra space */
    private int dagSize;
    /** temporary array used in OR operation */
    private List<ConceptWDep> orConceptsToTest = new ArrayList<ConceptWDep>();
    /** temporary array used in <= operations */
    private List<DlCompletionTreeArc> edgesToMerge = new ArrayList<DlCompletionTreeArc>();
    /** contains clash set if clash is encountered in a node label */
    private DepSet clashSet = DepSet.create();
    protected final JFactReasonerConfiguration options;
    ///** flag for switching semantic branching */
    //	private boolean useSemanticBranching;
    //	/** flag for switching backjumping */
    //	private boolean useBackjumping;
    //	/** whether or not check blocking status as late as possible */
    //	private boolean useLazyBlocking;
    //	/** flag for switching between Anywhere and Ancestor blockings */
    //	private boolean useAnywhereBlocking;
    // session status flags:
    /** true if nominal-related expansion rule was fired during reasoning */
    private boolean encounterNominal;
    /** flag to show if it is necessary to produce DT reasoning immediately */
    private boolean checkDataNode;
    /** cache for testing whether it's possible to non-expand newly created node */
    ModelCacheIan newNodeCache;
    /** auxilliary cache that is built from the edges of newly created node */
    ModelCacheIan newNodeEdges;
    Stats stats = new Stats();
    protected final DatatypeFactory datatypeFactory;

    /**
     * Adds ToDo entry which already exists in label of NODE. There is no need
     * to add entry to label, but it is necessary to provide offset of existing
     * concept. This is done by providing OFFSET of the concept in NODE's label
     */
    private void addExistingToDoEntry(final DlCompletionTree node, final ConceptWDep C,
            final String reason /* = null */) {
        int bp = C.getConcept();
        TODO.addEntry(node, dlHeap.get(bp).getType(), C);
        logNCEntry(node, C.getConcept(), C.getDep(), "+", reason);
    }

    /** add all elements from NODE label into Todo list */
    private void redoNodeLabel(final DlCompletionTree node, final String reason) {
        final CGLabel lab = node.label();
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
    private void ensureDAGSize() {
        if (dagSize < dlHeap.size()) {
            dagSize = dlHeap.maxSize();
            Helper.resize(EntityMap, dagSize);
            tBox.SplitRules.ensureDagSize(dagSize);
        }
    }

    //--		internal cache support
    /** return cache of given completion tree (implementation) */
    protected final ModelCacheInterface createModelCache(final DlCompletionTree p) {
        return new ModelCacheIan(dlHeap, p, encounterNominal, tBox.nC, tBox.nR,
                options.isRKG_USE_SIMPLE_RULES());
    }

    /** check whether node may be (un)cached; save node if something is changed */
    private ModelCacheState tryCacheNode(final DlCompletionTree node) {
        //TODO verify
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

    private boolean applyExtraRulesIf(final Concept p) {
        if (!p.hasExtraRules()) {
            return false;
        }
        assert p.isPrimitive();
        return applyExtraRules(p);
    }

    //--		internal nominal reasoning interface
    /** check whether reasoning with nominals is performed */
    public boolean hasNominals() {
        return false;
        // !Nominals.isEmpty();
    }

    /** @return true iff current node is i-blocked (ie, no expansion necessary) */
    private boolean isIBlocked() {
        return curNode.isIBlocked();
    }

    /** @return true iff there is R-neighbour labelled with C */
    private boolean isSomeExists(final Role R, final int C) {
        //TODO verify whether a cache is worth the effort
        if (!used.contains(C)) {
            return false;
        }
        final DlCompletionTree where = curNode.isSomeApplicable(R, C);
        if (where != null) {
            options.getLog().printTemplate(Templates.E, R.getName(), where.getId(), C);
        }
        return where != null;
    }

    /**
     * apply AR.C in and <= nR (if needed) in NODE's label where R is label of
     * arcSample. Set of applicable concepts is defined by redoForallFlags
     * value.
     */
    /** check if branching rule was called for the 1st time */
    private boolean isFirstBranchCall() {
        return bContext == null;
    }

    /** init branching context with given rule type */
    protected void initBC(final BranchingContext c) {
        //XXX move to BranchingContext
        // save reasoning context
        c.node = curNode;
        c.concept = new ConceptWDep(curConceptConcept, curConceptDepSet);
        c.branchDep = DepSet.create(curConceptDepSet);
        //TODO check why these commented lines do not appear
        //		bContext.pUsedIndex = pUsed.size();
        //		bContext.nUsedIndex = nUsed.size();
        c.SGsize = SessionGCIs.size();
    }

    /** create BC for Or rule */
    private void createBCOr() {
        bContext = stack.pushOr();
    }

    /** create BC for NN-rule */
    private void createBCNN() {
        bContext = stack.pushNN();
    }

    /** create BC for LE-rule */
    private void createBCLE() {
        bContext = stack.pushLE();
    }

    /** create BC for Choose-rule */
    private void createBCCh() {
        bContext = stack.pushCh();
    }

    /** check whether a node represents a functional one */
    private static boolean isFunctionalVertex(final DLVertex v) {
        return v.getType() == DagTag.dtLE && v.getNumberLE() == 1
                && v.getConceptIndex() == Helper.bpTOP;
    }

    /**
     * check if ATLEAST and ATMOST entries are in clash. Both vertex MUST have
     * dtLE type.
     */
    private boolean checkNRclash(final DLVertex atleast, final DLVertex atmost) { // >= n R.C clash with <= m S.D iff...
        return (atmost.getConceptIndex() == Helper.bpTOP || atleast.getConceptIndex() == atmost
                .getConceptIndex()) && // either D is TOP or C == D...
                atleast.getNumberGE() > atmost.getNumberLE() && // and n is greater than m...
                atleast.getRole().lesserequal(atmost.getRole()); // and R [= S
    }

    /** quick check whether CURNODE has a clash with a given ATMOST restriction */
    private boolean isQuickClashLE(final DLVertex atmost) {
        List<ConceptWDep> list = curNode.beginl_cc();
        for (int i = 0; i < list.size(); i++) {
            ConceptWDep q = list.get(i);
            if (q.getConcept() < 0 // need at-least restriction
                    && isNRClash(dlHeap.get(q.getConcept()), atmost, q)) {
                return true;
            }
        }
        return false;
    }

    /** quick check whether CURNODE has a clash with a given ATLEAST restriction */
    private boolean isQuickClashGE(final DLVertex atleast) {
        List<ConceptWDep> list = curNode.beginl_cc();
        for (int i = 0; i < list.size(); i++) {
            ConceptWDep q = list.get(i);
            if (q.getConcept() > 0 // need at-most restriction
                    && isNRClash(atleast, dlHeap.get(q.getConcept()), q)) {
                return true;
            }
        }
        return false;
    }

    /**
     * aux method that fills the dep-set for either C or ~C found in the label;
     * 
     * @param d
     *            depset to be changed if a clash is found
     * @return whether C was found
     */
    private boolean findChooseRuleConcept(final CWDArray label, final int C,
            final DepSet d) {
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
        //return false;
    }

    /** check whether clash occures EDGE to TO labelled with S disjoint with R */
    private boolean checkDisjointRoleClash(final DlCompletionTreeArc edge,
            final DlCompletionTree to, final Role R, final DepSet dep) { // clash found
        if (edge.getArcEnd().equals(to) && edge.getRole().isDisjoint(R)) {
            this.setClashSet(dep);
            updateClashSet(edge.getDep());
            return true;
        }
        return false;
    }

    // support for FORALL expansion
    /** Perform expansion of (\neg \ER.Self).DEP to an EDGE */
    private boolean checkIrreflexivity(final DlCompletionTreeArc edge, final Role R,
            final DepSet dep) {
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
    private void logNCEntry(final DlCompletionTree n, final int bp, final DepSet dep,
            final String action, final String reason) {
        if (options.isLoggingActive()) {
            final LogAdapter logAdapter = options.getLog();
            logAdapter.print(" ", action);
            logAdapter.print("(");
            logAdapter.print(n.logNode());
            logAdapter.print(",", bp, dep, ")");
            if (reason != null) {
                logAdapter.print(reason);
            }
        }
    }

    /** use this method in ALL dependency stuff (never use tryLevel directly) */
    private int getCurLevel() {
        return tryLevel;
    }

    /** set new branching level (never use tryLevel directly) */
    private void setCurLevel(final int level) {
        tryLevel = level;
    }

    /**
     * @return true if no branching ops were applied during reasoners; FIXME!!
     *         doesn't work properly with a nominal cloud
     */
    protected boolean noBranchingOps() {
        return tryLevel == Helper.InitBranchingLevelValue + nonDetShift;
    }

    /** Get save/restore level based on either current- or DS level */
    private int getSaveRestoreLevel(final DepSet ds) {
        // FIXME!!! see more precise it later
        if (options.isRKG_IMPROVE_SAVE_RESTORE_DEPSET()) {
            return ds.level() + 1;
        } else {
            return getCurLevel();
        }
    }

    /** restore reasoning state to the latest saved position */
    private void restore() {
        this.restore(getCurLevel() - 1);
    }

    /** update level in N node and save it's state (if necessary) */
    private void updateLevel(final DlCompletionTree n, final DepSet ds) {
        cGraph.saveNode(n, getSaveRestoreLevel(ds));
    }

    /** finalize branching OP processing making deterministic op */
    private void determiniseBranchingOp() {
        bContext = null; // clear context for the next branching op
        stack.pop(); // remove unnecessary context from the stack
    }

    /** set value of global dep-set to D */
    private void setClashSet(final DepSet d) {
        clashSet = d;
    }

    private void setClashSet(final List<DepSet> d) {
        DepSet dep = DepSet.create();
        for (int i = 0; i < d.size(); i++) {
            dep.add(d.get(i));
        }
        clashSet = dep;
    }

    /** add D to global dep-set */
    private void updateClashSet(final DepSet d) {
        clashSet.add(d);
    }

    /** get dep-set wrt current level */
    private DepSet getCurDepSet() {
        return DepSet.create(getCurLevel() - 1);
    }

    /** get RW access to current branching dep-set */
    private DepSet getBranchDep() {
        return bContext.branchDep;
    }

    /** update cumulative branch-dep with current clash-set */
    private void updateBranchDep() {
        getBranchDep().add(clashSet);
    }

    /** prepare cumulative dep-set to usage */
    private void prepareBranchDep() {
        getBranchDep().restrict(getCurLevel());
    }

    /** prepare cumulative dep-set and copy itto general clash-set */
    private void useBranchDep() {
        prepareBranchDep();
        this.setClashSet(getBranchDep());
    }

    /** re-apply all the relevant expantion rules to a given unblocked NODE */
    public void repeatUnblockedNode(final DlCompletionTree node, final boolean direct) {
        if (direct) {
            applyAllGeneratingRules(node); // re-apply all the generating rules
        } else {
            redoNodeLabel(node, "ubi");
        }
    }

    /**
     * get access to the DAG associated with it (necessary for the blocking
     * support)
     */
    public final DLDag getDAG() {
        return tBox.getDLHeap();
    }

    /** get the ROOT node of the completion graph */
    public final DlCompletionTree getRootNode() {
        return cGraph.getRoot();
    }

    /** init Todo list priority for classification */
    public void initToDoPriorities() {
        final String iaoeflg = options.getIAOEFLG();
        // inform about used rules order
        options.getLog().print("\nInit IAOEFLG = ", iaoeflg);
        TODO.initPriorities(iaoeflg);
    }

    /** set blocking method for a session */
    public void setBlockingMethod(final boolean hasInverse, final boolean hasQCR) {
        cGraph.setBlockingMethod(hasInverse, hasQCR);
    }

    /// set the in-classification flag
    public void setDuringClassification(final boolean value) {
        duringClassification = value;
    }

    /** create model cache for the just-classified entry */
    public final ModelCacheInterface buildCacheByCGraph(final boolean sat) {
        if (sat) {
            return createModelCache(getRootNode());
        } else {
            // unsat => cache is just bottom
            return ModelCacheConst.createConstCache(Helper.bpBOTTOM);
        }
    }

    public void writeTotalStatistic(final LogAdapter o) {
        if (options.isUSE_REASONING_STATISTICS()) {
            stats.accumulate(); // ensure that the last reasoning results are in
            stats.logStatisticData(o, false, cGraph, options);
        }
        o.print("\n");
    }

    public ModelCacheInterface createCache(final int p, final FastSet f) {
        assert Helper.isValid(p);
        ModelCacheInterface cache;
        if ((cache = dlHeap.getCache(p)) != null) {
            return cache;
        }
        prepareCascadedCache(p, f);
        if ((cache = dlHeap.getCache(p)) != null) {
            return cache;
        }
        cache = buildCache(p);
        dlHeap.setCache(p, cache);
        return cache;
    }

    private final static EnumSet<DagTag> handlecollection = EnumSet.of(dtAnd,
            dtCollection);
    private final static EnumSet<DagTag> handleforallle = EnumSet.of(dtForall, dtLE);
    private final static EnumSet<DagTag> handlesingleton = EnumSet.of(dtPSingleton,
            dtNSingleton, dtNConcept, dtPConcept);

    private void prepareCascadedCache(final int p, final FastSet f) {
        if (inProcess.contains(p)) {
            return;
        }
        if (f.contains(p)) {
            return;
        }
        final DLVertex v = dlHeap.get(p);
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
            final Role R = v.getRole();
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
        //dttop, dtsplit, etc: do nothing
        f.add(p);
    }

    private final ModelCacheInterface buildCache(final int p) {
        final LogAdapter logAdapter = options.getLog();
        if (options.isLoggingActive()) {
            logAdapter.print("\nChecking satisfiability of DAG entry ", p);
            tBox.printDagEntry(logAdapter, p);
            logAdapter.print(":\n");
        }
        boolean sat = this.runSat(p, Helper.bpTOP);
        if (!sat) {
            logAdapter.printTemplate(Templates.BUILD_CACHE_UNSAT, p);
        }
        return buildCacheByCGraph(sat);
    }

    //-----------------------------------------------------------------------------
    // flags section
    //-----------------------------------------------------------------------------
    /// @return true iff semantic branching is used
    boolean useSemanticBranching() {
        return tBox.useSemanticBranching;
    }

    /// @return true iff lazy blocking is used
    boolean useLazyBlocking() {
        return tBox.useLazyBlocking;
    }

    /// @return true iff active signature is in use
    boolean useActiveSignature() {
        return !tBox.getSplits().empty();
    }

    protected final void resetSessionFlags() {
        // reflect possible change of DAG size
        ensureDAGSize();
        used.add(Helper.bpTOP);
        used.add(Helper.bpBOTTOM);
        encounterNominal = false;
        checkDataNode = true;
        splitRuleLevel = 0;
    }

    protected boolean initNewNode(final DlCompletionTree node, final DepSet dep,
            final int C) {
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

    public boolean runSat(final int p, final int q) {
        prepareReasoner();
        // use general method to init node with P and add Q then
        if (initNewNode(cGraph.getRoot(), DepSet.create(), p)
                || addToDoEntry(cGraph.getRoot(), q, DepSet.create(), null)) {
            return false; // concept[s] unsatisfiable
        }
        // check satisfiability explicitly
        Timer timer = q == Helper.bpTOP ? satTimer : subTimer;
        timer.start();
        boolean result = this.runSat();
        timer.stop();
        return result;
    }

    public boolean checkDisjointRoles(final Role R, final Role S) {
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
        // init new nodes/edges. No need to apply restrictions, as no reasoning have been done yet.
        if (initNewNode(edgeR.getArcEnd(), dummy, Helper.bpTOP)
                || initNewNode(edgeS.getArcEnd(), dummy, Helper.bpTOP)
                || setupEdge(edgeR, dummy, /* flags= */
                        0) || setupEdge(edgeS, dummy, /* flags= */
                        0) || merge(edgeS.getArcEnd(), edgeR.getArcEnd(), dummy)) {
            return true;
        }
        // 2 roles are disjoint if current setting is unsatisfiable
        curNode = null;
        return !this.runSat();
    }

    public boolean checkIrreflexivity(final Role R) {
        prepareReasoner();
        // use general method to init node...
        DepSet dummy = DepSet.create();
        if (initNewNode(cGraph.getRoot(), dummy, Helper.bpTOP)) {
            return true;
        }
        // ... add an R-loop
        curNode = cGraph.getRoot();
        DlCompletionTreeArc edgeR = this.createOneNeighbour(R, dummy);
        // init new nodes/edges. No need to apply restrictions, as no reasoning have been done yet.
        if (initNewNode(edgeR.getArcEnd(), dummy, Helper.bpTOP)
                || setupEdge(edgeR, dummy, /* flags= */
                        0) || merge(edgeR.getArcEnd(), cGraph.getRoot(), dummy)) {
            return true;
        }
        // R is irreflexive if current setting is unsatisfiable
        curNode = null;
        return !this.runSat();
    }

    // restore implementation
    private boolean backJumpedRestore() {
        // if empty clash dep-set -- concept is unsatisfiable
        if (clashSet == null || clashSet.isEmpty()) {
            return true;
        }
        // some non-deterministic choices were done
        this.restore(clashSet.level());
        return false;
    }

    private boolean straightforwardRestore() {
        if (noBranchingOps()) {
            return true; // ... the concept is unsatisfiable
        } else { // restoring the state
            this.restore();
            return false;
        }
    }

    private boolean tunedRestore() {
        if (options.getuseBackjumping()) {
            return backJumpedRestore();
        } else {
            return straightforwardRestore();
        }
    }

    private boolean commonTacticBodyAll(final DLVertex cur) {
        assert curConceptConcept > 0 && cur.getType() == dtForall;
        if (cur.getRole().isTop()) {
            //			if(cur.getRole().isDataRole()) {
            //				return false;
            //			}
            stats.getnAllCalls().inc();
            return addSessionGCI(cur.getConceptIndex(), curConceptDepSet);
        }
        //
        // can't skip singleton models for complex roles due to empty transitions
        if (cur.getRole().isSimple()) {
            return commonTacticBodyAllSimple(cur);
        } else {
            return commonTacticBodyAllComplex(cur);
        }
    }

    /// add C to a set of session GCIs; init all nodes with (C,dep)
    private boolean addSessionGCI(final int C, final DepSet dep) {
        SessionGCIs.add(C);
        int n = 0;
        DlCompletionTree node = null;
        while ((node = cGraph.getNode(n++)) != null) {
            if (!node.isDataNode() && addToDoEntry(node, C, dep, "sg")) {
                return true;
            }
        }
        return false;
    }

    protected DlSatTester(final TBox tbox, final JFactReasonerConfiguration Options,
            final DatatypeFactory datatypeFactory) {
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
        duringClassification = false;
        options.getLog().printTemplate(Templates.READCONFIG,
                options.getuseSemanticBranching(), options.getuseBackjumping(),
                options.getuseLazyBlocking(), options.getuseAnywhereBlocking());
        if (tBox.hasFC() && options.getuseAnywhereBlocking()) {
            options.setuseAnywhereBlocking(false);
            options.getLog().print(
                    "Fairness constraints: set useAnywhereBlocking = false\n");
        }
        cGraph.initContext(tbox.nSkipBeforeBlock, options.getuseLazyBlocking(),
                options.getuseAnywhereBlocking());
        tbox.getORM().fillReflexiveRoles(reflexiveRoles);
        //		useActiveSignature = !tBox.getSplits().empty();
        //		if (useActiveSignature) {
        //			initSplits();
        //			// make entity map
        //			int size = dlHeap.size();
        //			Helper.resize(EntityMap, size);
        //			EntityMap.set(0, null);
        //			EntityMap.set(1, null);
        //			for (int i = 2; i < size - 1; ++i) {
        //				if (dlHeap.get(i).getConcept() != null) {
        //					EntityMap.set(i, dlHeap.get(i).getConcept().getEntity());
        //				} else {
        //					EntityMap.set(i, null);
        //				}
        //			}
        //			EntityMap.set(size - 1, null); // query concept
        //		}
        resetSessionFlags();
    }

    public JFactReasonerConfiguration getOptions() {
        return options;
    }

    protected void prepareReasoner() {
        cGraph.clear();
        stack.clear();
        TODO.clear();
        //		if (!TODO.isSaveStateGenerationStarted()) {
        //			TODO.startSaveStateGeneration();
        //		}
        used.clear();
        SessionGCIs.clear();
        //		ActiveSplits.clear();
        //		ActiveSignature.clear();
        curNode = null;
        bContext = null;
        tryLevel = Helper.InitBranchingLevelValue;
        // clear last session information
        resetSessionFlags();
    }

    /// try to add a concept to a label given by TAG; ~C can't appear in the label
    public boolean findConcept(final CWDArray lab, final int p) {
        assert Helper.isCorrect(p); // sanity checking
        // constants are not allowed here
        assert p != Helper.bpTOP;
        assert p != Helper.bpBOTTOM;
        stats.getnLookups().inc();
        return lab.contains(p);
    }

    private AddConceptResult checkAddedConcept(final CWDArray lab, final int p,
            final DepSet dep) {
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

    /// try to add a concept to a label given by TAG; ~C can't appear in the label; setup clash-set if found
    private boolean findConceptClash(final CWDArray lab, final int bp, final DepSet dep) {
        stats.getnLookups().inc();
        DepSet depset = lab.get(bp);
        if (depset != null) {
            clashSet = DepSet.plus(depset, dep);
            return true;
        }
        return false;
    }

    private AddConceptResult tryAddConcept(final CWDArray lab, final int bp,
            final DepSet dep) {
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

    private boolean addToDoEntry(final DlCompletionTree n, final int bp,
            final DepSet dep, final String reason) {
        if (bp == Helper.bpTOP) {
            return false;
        }
        if (bp == Helper.bpBOTTOM) {
            this.setClashSet(dep);
            logNCEntry(n, bp, dep, "x", dlHeap.get(bp).getType().getName());
            return true;
        }
        final DLVertex v = dlHeap.get(bp);
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

    private boolean insertToDoEntry(final DlCompletionTree n, final int bp,
            final DepSet dep, final DagTag tag, final String reason) {
        ConceptWDep p = new ConceptWDep(bp, dep);
        updateLevel(n, dep);
        cGraph.addConceptToNode(n, p, tag);
        used.add(bp);
        //		if (useActiveSignature) {
        //			if (updateActiveSignature(getEntity(bp), dep)) {
        //				return true;
        //			}
        //		}
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

    private boolean canBeCached(final DlCompletionTree node) {
        boolean shallow = true;
        int size = 0;
        //		if (node.isNominalNode()) {
        //			return false;
        //		}
        // check whether node cache is allowed
        if (!tBox.useNodeCache) {
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

    /**
     * build cache of the node (it is known that caching is possible) in
     * newNodeCache
     */
    private void doCacheNode(final DlCompletionTree node) {
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

    private ModelCacheState reportNodeCached(final DlCompletionTree node) {
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

    private boolean correctCachedEntry(final DlCompletionTree n) {
        assert n.isCached();
        ModelCacheState status = tryCacheNode(n);
        if (status == ModelCacheState.csFailed) {
            redoNodeLabel(n, "uc");
        }
        return status.usageByState();
    }

    int shortcuts = 0;

    private boolean hasDataClash(final DlCompletionTree node) {
        assert node != null && node.isDataNode();
        List<ConceptWDep> concepts = node.beginl_sc();
        final int size = concepts.size();
        // shortcut: if only one non-and argument is passed and it's Literal, return false without bothering
        if (size == 1) {
            return false;
        }
        //			NamedEntry dataEntry = this.dlHeap.get(concepts.get(0).getConcept())
        //					.getConcept();
        //			if (dataEntry instanceof DatatypeEntry
        //					&& ((DatatypeEntry) dataEntry).getDatatype().equals(
        //							DatatypeFactory.LITERAL)) {
        //				this.shortcuts++;
        //				if (this.shortcuts % 100 == 0) {
        //					System.out.println("DlSatTester.hasDataClash() " + this.shortcuts);
        //				}
        //				return false;
        //			}
        //		}
        DataTypeReasoner datatypeReasoner = new DataTypeReasoner(options);
        for (int i = 0; i < size; i++) {
            ConceptWDep r = concepts.get(i);
            final DLVertex v = dlHeap.get(r.getConcept());
            NamedEntry dataEntry = dlHeap.get(r.getConcept()).getConcept();
            boolean positive = r.getConcept() > 0;
            if (datatypeReasoner.addDataEntry(positive, v.getType(), dataEntry,
                    r.getDep())) {
                this.setClashSet(datatypeReasoner.getClashSet());
                return true;
            }
        }
        final boolean checkClash = datatypeReasoner.checkClash();
        if (checkClash) {
            this.setClashSet(datatypeReasoner.getClashSet());
        }
        return checkClash;
    }

    protected boolean runSat() {
        testTimer.start();
        boolean result = checkSatisfiability();
        testTimer.stop();
        options.getLog().print("\nChecking time was ", testTimer.getResultTime(),
                " milliseconds");
        testTimer.reset();
        finaliseStatistic();
        if (result) {
            cGraph.print(options.getLog());
        }
        return result;
    }

    private void finaliseStatistic() {
        cGraph.clearStatistics();
    }

    private boolean applyReflexiveRoles(final DlCompletionTree node, final DepSet dep) {
        for (Role p : reflexiveRoles) {
            DlCompletionTreeArc pA = cGraph.addRoleLabel(node, node, false, p, dep);
            if (setupEdge(pA, dep, 0)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkSatisfiability() {
        //this.tBox.print();
        int loop = 0;
        for (;;) {
            if (curNode == null) {
                //				if (TODO.isEmpty()) {
                //					if (options.isLoggingActive()) {
                //						logIndentation();
                //						//CGraph.Print(LL);
                //						options.getLog().print("[*ub:");
                //					}
                //					cGraph.retestCGBlockedStatus();
                //					options.getLog().print("]");
                if (TODO.isEmpty()) // no applicable rules
                { // do run-once things
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
                final ToDoEntry curTDE = TODO.getNextEntry();
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

    /// perform all the actions that should be done once, after all normal rules are not applicable. @return true if the concept is unsat
    boolean performAfterReasoning() {
        // make sure all blocked nodes are still blocked
        logIndentation();
        options.getLog().print("[*ub:");
        cGraph.retestCGBlockedStatus();
        options.getLog().print("]");
        if (!TODO.isEmpty()) {
            return false;
        }
        // check if any split expansion rule could be fired
        if (!tBox.getSplits().empty()) {
            logIndentation();
            options.getLog().print("[*split:");
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

    //-----------------------------------------------------------------------------
    //				split code implementation
    //-----------------------------------------------------------------------------
    /// apply split rule RULE to a reasoner. @return true if clash was found
    boolean applySplitRule(final TSplitRule rule) {
        DepSet dep = rule.fireDep(SessionSignature, SessionSigDepSet);
        int bp = rule.bp() - 1; // p.bp points to Choose(C) node, p.bp-1 -- to the split node
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

    /// check whether any split rules should be run and do it. @return true iff clash was found
    boolean checkSplitRules() {
        if (splitRuleLevel == 0) // 1st application OR return was made before previous set
        {
            ActiveSplits.clear();
            SessionSignature.clear();
            SessionSigDepSet.clear();
            splitRuleLevel = getCurLevel();
        }
        // fills in session signature for current CGraph. combine dep-sets for the same entities
        this.updateSessionSignature();
        // now for every split expansion rule check whether it can be fired
        for (TSplitRule p : tBox.SplitRules.getRules()) {
            if (!ActiveSplits.contains(p.bp() - 1) && p.canFire(SessionSignature)) {
                if (applySplitRule(p)) {
                    return true;
                }
            }
        }
        return false;
    }

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

    protected void restore(final int newTryLevel) {
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

    private void logIndentation() {
        final LogAdapter logAdapter = options.getLog();
        logAdapter.print("\n");
        for (int i = 1; i < getCurLevel(); i++) {
            logAdapter.print(' ');
        }
    }

    private void logStartEntry() {
        if (options.isLoggingActive()) {
            logIndentation();
            final LogAdapter logAdapter = options.getLog();
            logAdapter.print("[*(");
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

    private void logFinishEntry(final boolean res) {
        if (options.isLoggingActive()) {
            options.getLog().print("]");
            if (res) {
                options.getLog().printTemplate(Templates.LOG_FINISH_ENTRY, clashSet);
            }
        }
    }

    public float printReasoningTime(final LogAdapter o) {
        o.print("\n     SAT takes ", satTimer, " seconds\n     SUB takes ", subTimer,
                " seconds");
        return satTimer.calcDelta() + subTimer.calcDelta();
    }

    /**
     * Tactics section;
     * 
     * Each Tactic should have a (small) Usability function <name> and a Real
     * tactic function <name>Body
     * 
     * Each tactic returns: - true - if expansion of CUR lead to clash - false -
     * overwise
     * 
     */
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

    private boolean commonTacticBody(final DLVertex cur) {
        stats.getnTacticCalls().inc();
        switch (cur.getType()) {
            case dtTop:
                throw new UnreachableSituationException();
                //				return tacticUsage.utDone;
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

    private boolean commonTacticBodyId(final DLVertex cur) {
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

    /// add entity.dep to a session structures
    void updateSessionSignature(final NamedEntity entity, final DepSet dep) {
        if (entity != null) {
            SessionSignature.add(entity);
            SessionSigDepSet.get(entity).add(dep);
        }
    }

    /// update session signature with all names from a given node
    void updateSignatureByNode(final DlCompletionTree node) {
        CGLabel lab = node.label();
        for (ConceptWDep p : lab.get_sc()) {
            this.updateSessionSignature(tBox.SplitRules.getEntity(p.getConcept()),
                    p.getDep());
        }
    }

    /// update session signature for all non-data nodes
    void updateSessionSignature() {
        int n = 0;
        DlCompletionTree node = null;
        while ((node = cGraph.getNode(n++)) != null) {
            if (!node.isDataNode()) {
                updateSignatureByNode(node);
            }
        }
    }

    boolean updateActiveSignature1(final NamedEntity entity, final DepSet dep) {
        ActiveSignature.add(entity);
        // check whether some of the split rules require unsplitting
        for (SingleSplit p : SplitRules) {
            if (!ActiveSplits.contains(p.bp - 1) && containsInActive(p.eqSig)
                    && intersectsWithActive(p.impSig)) {
                // here p.bp points to Choose(C) node, p.bp-1 -- to the split node
                ActiveSplits.add(p.bp - 1);
                if (addSessionGCI(p.bp, dep)) {
                    return true;
                }
                // make sure that all existing splits will be re-applied
                this.updateName(p.bp - 1);
            }
        }
        return false;
    }

    protected boolean applicable(final SimpleRule rule) {
        final CWDArray lab = curNode.label().getLabel(DagTag.dtPConcept);
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

    private boolean applyExtraRules(final Concept C) {
        FastSet er_begin = C.getExtraRules();
        for (int i = 0; i < er_begin.size(); i++) {
            SimpleRule rule = tBox.getSimpleRule(er_begin.get(i));
            stats.getnSRuleAdd().inc();
            if (rule.applicable(this)) // apply the rule's head
            {
                stats.getnSRuleFire().inc();
                if (addToDoEntry(curNode, rule.getBpHead(), clashSet, null)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean commonTacticBodySingleton(final DLVertex cur) {
        assert cur.getType() == dtPSingleton || cur.getType() == dtNSingleton; // safety check
        stats.getnSingletonCalls().inc();
        assert hasNominals();
        encounterNominal = true;
        final Individual C = (Individual) cur.getConcept();
        assert C.getNode() != null;
        DepSet dep = DepSet.create(curConceptDepSet);
        DlCompletionTree realNode = C.getNode().resolvePBlocker(dep);
        if (!realNode.equals(curNode)) {
            return merge(curNode, realNode, dep);
        }
        return commonTacticBodyId(cur);
    }

    private boolean commonTacticBodyAnd(final DLVertex cur) {
        assert curConceptConcept > 0 && cur.getType() == dtAnd; // safety check
        stats.getnAndCalls().inc();
        // FIXME!! I don't know why, but performance is usually BETTER if using r-iters.
        // It's their only usage, so after investigation they can be dropped
        int[] begin = cur.begin();
        for (int q : begin) {
            if (addToDoEntry(curNode, q, curConceptDepSet, null)) {
                return true;
            }
        }
        //		for (int i = begin.length - 1; i >= 0; i--) {
        //			int q = begin[i];
        //			if (addToDoEntry(curNode, q, dep, null)) {
        //				return true;
        //			}
        //		}
        //		for ( DLVertex::const_reverse_iterator q = cur.rbegin(); q != cur.rend(); ++q )
        //			if ( addToDoEntry ( curNode, *q, dep ) )return true;
        return false;
    }

    private boolean commonTacticBodyOr(final DLVertex cur) {
        assert curConceptConcept < 0 && cur.getType() == dtAnd; // safety check
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

    private boolean planOrProcessing(final DLVertex cur, final Reference<DepSet> dep) {
        orConceptsToTest.clear();
        dep.setReference(DepSet.create(curConceptDepSet));
        // check all OR components for the clash
        CGLabel lab = curNode.label();
        for (int q : cur.begin()) {
            int inverse = -q;
            switch (tryAddConcept(lab.getLabel(dlHeap.get(inverse).getType()), inverse,
                    null)) {
                case acrClash: // clash found -- OK
                    dep.getReference().add(clashSet);
                    continue;
                case acrExist: // already have such concept -- save it to the 1st position
                    orConceptsToTest.clear();
                    orConceptsToTest.add(new ConceptWDep(-q));
                    return true;
                case acrDone:
                    orConceptsToTest.add(new ConceptWDep(-q));
                    continue;
                default: // safety check
                    throw new UnreachableSituationException();
            }
        }
        return false;
    }

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
                    throw new UnreachableSituationException(curNode.toString());
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

    private boolean commonTacticBodyAllComplex(final DLVertex cur) {
        int state = cur.getState();
        int C = curConceptConcept - state; // corresponds to AR{0}.X
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

    private boolean commonTacticBodyAllSimple(final DLVertex cur) {
        RAStateTransitions RST = cur.getRole().getAutomaton().getBase().get(0);
        int C = cur.getConceptIndex();
        // check whether automaton applicable to any edges
        stats.getnAllCalls().inc();
        // check all neighbours; as the role is simple then recognise() == applicable()
        List<DlCompletionTreeArc> neighbour = curNode.getNeighbour();
        final int size = neighbour.size();
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

    private boolean applyTransitions(final DlCompletionTreeArc edge,
            final RAStateTransitions RST, final int C, final DepSet dep,
            final String reason) {
        Role R = edge.getRole();
        DlCompletionTree node = edge.getArcEnd();
        // fast lane: the single transition which is applicable
        if (RST.isSingleton()) {
            return addToDoEntry(node, C + RST.getTransitionEnd(), dep, reason);
        }
        // try to apply all transitions to edge
        List<RATransition> begin = RST.begin();
        final int size = begin.size();
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

    /**
     * Perform expansion of (\AR.C).DEP to an EDGE for simple R with a given
     * reason
     */
    private boolean applyUniversalNR(final DlCompletionTree Node,
            final DlCompletionTreeArc arcSample, final DepSet dep_, final int flags) {
        // check whether a flag is set
        if (flags == 0) {
            return false;
        }
        Role R = arcSample.getRole();
        DepSet dep = DepSet.plus(dep_, arcSample.getDep());
        List<ConceptWDep> base = Node.beginl_cc();
        final int size = base.size();
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
                        // R is recognised so just add the final state!
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

    private boolean commonTacticBodySome(final DLVertex cur) {
        Role R = cur.getRole();
        int C = -cur.getConceptIndex();
        if (R.isTop()) {
            return commonTacticBodySomeUniv(cur);
        }
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
            final DLVertex nom = dlHeap.get(C);
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
            final DLVertex ver = dlHeap.get(LC.getConcept());
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
                if (R.isDisjoint()
                        && this.checkDisjointRoleClash(curNode, succ, R, newDep)) {
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
                    // add Range and Domain of a new role; this includes functional, so remove it from the latter
                    if (initHeadOfNewEdge(curNode, R, newDep, "RD")) {
                        return true;
                    }
                    if (initHeadOfNewEdge(succ, R.inverse(), newDep, "RR")) {
                        return true;
                    }
                    // check AR.C in both sides of functionalArc
                    // FIXME!! for simplicity, check the functionality here (see bEx017). It seems
                    // only necessary when R has several functional super-roles, so the condition
                    // can be simplified
                    if (applyUniversalNR(curNode, functionalArc, newDep,
                            redoForall.getValue() | redoFunc.getValue())) {
                        return true;
                    }
                    // if new role label was added to a functionalArc, some functional restrictions
                    // in the SUCC node might became applicable. See bFunctional1x test
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

    private boolean commonTacticBodyValue(final Role R, final Individual nom) {
        DepSet dep = DepSet.create(curConceptDepSet);
        if (isCurNodeBlocked()) {
            return false;
        }
        stats.getnSomeCalls().inc();
        assert nom.getNode() != null;
        DlCompletionTree realNode = nom.getNode().resolvePBlocker(dep);
        if (R.isDisjoint() && this.checkDisjointRoleClash(curNode, realNode, R, dep)) {
            return true;
        }
        encounterNominal = true;
        DlCompletionTreeArc edge = cGraph.addRoleLabel(curNode, realNode, false, R, dep);
        // add all necessary concepts to both ends of the edge
        return setupEdge(edge, dep, redoForall.getValue() | redoFunc.getValue()
                | redoAtMost.getValue() | redoIrr.getValue());
    }

    /// expansion rule for the existential quantifier with universal role
    boolean commonTacticBodySomeUniv(final DLVertex cur) {
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
            if (node.label().contains(C)) {
                return false;
            }
        }
        // make new node labelled with C
        node = cGraph.getNewNode();
        return initNewNode(node, curConceptDepSet, C);
    }

    private boolean createNewEdge(final Role R, final int C, final int flags) {
        if (isCurNodeBlocked()) {
            stats.getnUseless().inc();
            return false;
        }
        DlCompletionTreeArc pA = this.createOneNeighbour(R, curConceptDepSet);
        // add necessary label
        return initNewNode(pA.getArcEnd(), curConceptDepSet, C)
                || setupEdge(pA, curConceptDepSet, flags);
    }

    private DlCompletionTreeArc createOneNeighbour(final Role R, final DepSet dep) {
        return this.createOneNeighbour(R, dep, DlCompletionTree.BLOCKABLE_LEVEL);
    }

    private DlCompletionTreeArc createOneNeighbour(final Role R, final DepSet dep,
            final int level) {
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

    /// check whether current node is blocked
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

    private void applyAllGeneratingRules(final DlCompletionTree node) {
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

    public boolean setupEdge(final DlCompletionTreeArc pA, final DepSet dep,
            final int flags) {
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
    private boolean initHeadOfNewEdge(final DlCompletionTree node, final Role R,
            final DepSet dep, final String reason) {
        // if R is functional, then add FR with given DEP-set to NODE
        if (R.isFunctional()) {
            List<Role> begin_topfunc = R.begin_topfunc();
            final int size = begin_topfunc.size();
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

    private boolean commonTacticBodyFunc(final DLVertex cur) {
        assert curConceptConcept > 0 && isFunctionalVertex(cur);
        if (isNNApplicable(cur.getRole(), Helper.bpTOP, curConceptConcept + 1)) {
            return commonTacticBodyNN(cur);
        }
        stats.getnFuncCalls().inc();
        if (isQuickClashLE(cur)) {
            return true;
        }
        findNeighbours(cur.getRole(), Helper.bpTOP, null);
        if (edgesToMerge.size() < 2) {
            return false;
        }
        DlCompletionTreeArc q = edgesToMerge.get(0);
        DlCompletionTree sample = q.getArcEnd();
        DepSet depF = DepSet.create(curConceptDepSet);
        depF.add(q.getDep());
        for (int i = 1; i < edgesToMerge.size(); i++) {
            q = edgesToMerge.get(i);
            if (!q.getArcEnd().isPBlocked()) {
                if (merge(q.getArcEnd(), sample, DepSet.plus(depF, q.getDep()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean applyCh(final DLVertex cur) {
        int C = cur.getConceptIndex();
        Role R = cur.getRole();
        BCLE bcLE = null;
        //applyCh:
        // check if we have Qualified NR
        if (C != Helper.bpTOP) {
            if (commonTacticBodyChoose(R, C)) {
                return true;
            }
        }
        // check whether we need to apply NN rule first
        if (isNNApplicable(R, C, /* stopper= */
                curConceptConcept + cur.getNumberLE())) {
            //applyNN:
            return commonTacticBodyNN(cur); // after application <=-rule would be checked again
        }
        // if we are here that it IS first LE call
        if (isQuickClashLE(cur)) {
            return true;
        }
        // we need to repeat merge until there will be necessary amount of edges
        while (true) {
            if (isFirstBranchCall()) {
                DepSet dep = DepSet.create();
                // check the amount of neighbours we have
                findNeighbours(R, C, dep);
                // if the number of R-neighbours satisfies condition -- nothing to do
                if (edgesToMerge.size() <= cur.getNumberLE()) {
                    return false;
                }
                // init context
                createBCLE();
                bContext.branchDep.add(dep);
                // setup BCLE
                bcLE = (BCLE) bContext;
                List<DlCompletionTreeArc> temp = edgesToMerge;
                edgesToMerge = bcLE.getEdgesToMerge();
                bcLE.setEdgesToMerge(temp);
                bcLE.resetMCI();
            }
            {
                DlCompletionTreeArc from = null;
                DlCompletionTreeArc to = null;
                boolean applyLE = true;
                while (applyLE) {
                    applyLE = false;
                    // skip init, because here we are after restoring
                    bcLE = (BCLE) bContext;
                    if (bcLE.noMoreLEOptions()) { // set global clashset to cummulative one from previous branch failures
                        useBranchDep();
                        return true;
                    }
                    // get from- and to-arcs using corresponding indexes in Edges
                    from = bcLE.getFrom();
                    to = bcLE.getTo();
                    Reference<DepSet> dep = new Reference<DepSet>(DepSet.create()); // empty dep-set
                    // fast check for from.end() and to.end() are in \neq
                    if (cGraph.nonMergable(from.getArcEnd(), to.getArcEnd(), dep)) {
                        // need this for merging two nominal nodes
                        dep.getReference().add(from.getDep());
                        dep.getReference().add(to.getDep());
                        // add dep-set from labels
                        if (C == Helper.bpTOP) {
                            this.setClashSet(dep.getReference());
                        } else {
                            // QCR: update dep-set wrt C
                            // here we know that C is in both labels; set a proper clash-set
                            DagTag tag = dlHeap.get(C).getType();
                            boolean test;
                            // here dep contains the clash-set
                            test = findConceptClash(from.getArcEnd().label()
                                    .getLabel(tag), C, dep.getReference());
                            assert test;
                            dep.setReference(DepSet.plus(dep.getReference(), clashSet));
                            // save new dep-set
                            test = findConceptClash(to.getArcEnd().label().getLabel(tag),
                                    C, dep.getReference());
                            assert test;
                            // both clash-sets are now in common clash-set
                        }
                        updateBranchDep();
                        bContext.nextOption();
                        applyLE = true;
                    }
                }
                save();
                // add depset from current level and FROM arc and to current dep.set
                DepSet curDep = getCurDepSet();
                curDep.add(from.getDep());
                if (merge(from.getArcEnd(), to.getArcEnd(), curDep)) {
                    return true;
                }
                // it might be the case (see bIssue28) that after the merge there is an R-neigbour
                // that have neither C or ~C in its label (it was far in the nominal cloud)
                if (C != Helper.bpTOP) {
                    if (commonTacticBodyChoose(R, C)) {
                        return true;
                    }
                }
            }
            //		return false;
        }
    }

    private boolean commonTacticBodyLE(final DLVertex cur) {
        assert curConceptConcept > 0 && cur.getType() == dtLE;
        stats.getnLeCalls().inc();
        int C = cur.getConceptIndex();
        Role R = cur.getRole();
        BCLE bcLE = null;
        if (!isFirstBranchCall()) {
            if (bContext instanceof BCNN) {
                //break applyNN; // clash in NN-rule: skip choose-rule
                return commonTacticBodyNN(cur); // after application <=-rule would be checked again
            }
            if (bContext instanceof BCLE)
            //break applyLE; // clash in LE-rule: skip all the rest
            {
                // we need to repeate merge until there will be necessary amount of edges
                while (true) {
                    {
                        DlCompletionTreeArc from = null;
                        DlCompletionTreeArc to = null;
                        boolean applyLE = true;
                        while (applyLE) {
                            applyLE = false;
                            // skip init, because here we are after restoring
                            bcLE = (BCLE) bContext;
                            if (bcLE.noMoreLEOptions()) { // set global clashset to cummulative one from previous branch failures
                                useBranchDep();
                                return true;
                            }
                            // get from- and to-arcs using corresponding indexes in Edges
                            from = bcLE.getFrom();
                            to = bcLE.getTo();
                            Reference<DepSet> dep = new Reference<DepSet>(DepSet.create()); // empty dep-set
                            // fast check for from.end() and to.end() are in \neq
                            if (cGraph.nonMergable(from.getArcEnd(), to.getArcEnd(), dep)) {
                                dep.getReference().add(from.getDep());
                                dep.getReference().add(to.getDep());
                                // add dep-set from labels
                                if (C == Helper.bpTOP) {
                                    this.setClashSet(dep.getReference());
                                } else // QCR: update dep-set wrt C
                                {
                                    // here we know that C is in both labels; set a proper clash-set
                                    DagTag tag = dlHeap.get(C).getType();
                                    boolean test;
                                    // here dep contains the clash-set
                                    test = findConceptClash(from.getArcEnd().label()
                                            .getLabel(tag), C, dep.getReference());
                                    assert test;
                                    dep.setReference(DepSet.plus(dep.getReference(),
                                            clashSet)); // save new dep-set
                                    test = findConceptClash(to.getArcEnd().label()
                                            .getLabel(tag), C, dep.getReference());
                                    assert test;
                                    // both clash-sets are now in common clash-set
                                }
                                updateBranchDep();
                                bContext.nextOption();
                                applyLE = true;
                            }
                        }
                        save();
                        // add depset from current level and FROM arc and to current dep.set
                        DepSet curDep = getCurDepSet();
                        curDep.add(from.getDep());
                        if (merge(from.getArcEnd(), to.getArcEnd(), curDep)) {
                            return true;
                        }
                        // it might be the case (see bIssue28) that after the merge there is an R-neigbour
                        // that have neither C or ~C in its label (it was far in the nominal cloud)
                        if (C != Helper.bpTOP) {
                            if (commonTacticBodyChoose(R, C)) {
                                return true;
                            }
                        }
                    }
                    if (isFirstBranchCall()) {
                        DepSet dep = DepSet.create();
                        // check the amount of neighbours we have
                        findNeighbours(R, C, dep);
                        // if the number of R-neighbours satisfies condition -- nothing to do
                        if (edgesToMerge.size() <= cur.getNumberLE()) {
                            return false;
                        }
                        // init context
                        createBCLE();
                        bContext.branchDep.add(dep);
                        // setup BCLE
                        bcLE = (BCLE) bContext;
                        List<DlCompletionTreeArc> temp = edgesToMerge;
                        edgesToMerge = bcLE.getEdgesToMerge();
                        bcLE.setEdgesToMerge(temp);
                        bcLE.resetMCI();
                    }
                }
            }
            assert bContext instanceof BCChoose;
            //break applyCh; // clash in choose-rule: redo all
            return applyCh(cur);
        }
        return applyCh(cur);
    }

    private boolean commonTacticBodyGE(final DLVertex cur) {
        if (isCurNodeBlocked()) {
            return false;
        }
        stats.getnGeCalls().inc();
        if (isQuickClashGE(cur)) {
            return true;
        }
        // create N new different edges
        return createDifferentNeighbours(cur.getRole(), cur.getConceptIndex(),
                curConceptDepSet, cur.getNumberGE(), DlCompletionTree.BLOCKABLE_LEVEL);
    }

    private boolean createDifferentNeighbours(final Role R, final int C,
            final DepSet dep, final int n, final int level) {
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
        // re-apply all <= NR in curNode; do it only once for all created nodes; no need for Irr
        return applyUniversalNR(curNode, pA, dep,
                redoFunc.getValue() | redoAtMost.getValue());
    }

    private boolean isNRClash(final DLVertex atleast, final DLVertex atmost,
            final ConceptWDep reason) {
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

    private boolean checkMergeClash(final CGLabel from, final CGLabel to,
            final DepSet dep, final int nodeId) {
        DepSet clashDep = DepSet.create(dep);
        boolean clash = false;
        List<ConceptWDep> list = from.get_sc();
        final int size = list.size();
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
        final int ccsize = list.size();
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

    private boolean mergeLabels(final CGLabel from, final DlCompletionTree to,
            final DepSet dep) {
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

    private boolean merge(final DlCompletionTree from, final DlCompletionTree to,
            final DepSet depF) {
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
                    && this.checkDisjointRoleClash(q.getReverse().getArcEnd(),
                            q.getArcEnd(), q.getRole(), depF)) {
                //XXX dubious
                {
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

    protected boolean checkDisjointRoleClash(final DlCompletionTree from,
            final DlCompletionTree to, final Role R, final DepSet dep) {
        for (DlCompletionTreeArc p : from.getNeighbour()) {
            if (this.checkDisjointRoleClash(p, to, R, dep)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNewEdge(final DlCompletionTree node,
            final List<DlCompletionTreeArc> e) {
        final int size = e.size();
        for (int i = 0; i < size; i++) {
            if (e.get(i).getArcEnd().equals(node)) {
                return false;
            }
        }
        return true;
    }

    private void findNeighbours(final Role Role, final int c, final DepSet Dep) {
        edgesToMerge.clear();
        DagTag tag = dlHeap.get(c).getType();
        List<DlCompletionTreeArc> neighbour = curNode.getNeighbour();
        final int size = neighbour.size();
        for (int i = 0; i < size; i++) {
            DlCompletionTreeArc p = neighbour.get(i);
            if (p.isNeighbour(Role) && isNewEdge(p.getArcEnd(), edgesToMerge)
                    && findChooseRuleConcept(p.getArcEnd().label().getLabel(tag), c, Dep)) {
                edgesToMerge.add(p);
            }
        }
        Collections.sort(edgesToMerge, new EdgeCompare());
    }

    private boolean commonTacticBodyChoose(final Role R, final int C) {
        List<DlCompletionTreeArc> neighbour = curNode.getNeighbour();
        final int size = neighbour.size();
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

    private boolean applyChooseRule(final DlCompletionTree node, final int C) {
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

    private boolean commonTacticBodyNN(final DLVertex cur) {
        stats.getnNNCalls().inc();
        if (isFirstBranchCall()) {
            createBCNN();
        }
        final BCNN bcNN = (BCNN) bContext;
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
        // now remember NR we just created: it is (<= curNN R), so have to find it
        return addToDoEntry(curNode, curConceptConcept + cur.getNumberLE() - NN, curDep,
                "NN");
    }

    protected boolean isNNApplicable(final Role r, final int C, final int stopper) {
        // NN rule is only applicable to nominal nodes
        if (!curNode.isNominalNode()) {
            return false;
        }
        // check whether the NN-rule was already applied here for a given concept
        if (used.contains(stopper) && curNode.isLabelledBy(stopper)) {
            return false;
        }
        // check for the real applicability of the NN-rule here
        for (DlCompletionTreeArc p : curNode.getNeighbour()) {
            DlCompletionTree suspect = p.getArcEnd();
            // if there is an edge that require to run the rule, then we need it
            if (p.isPredEdge() && suspect.isBlockableNode() && p.isNeighbour(r)
                    && suspect.isLabelledBy(C)) {
                options.getLog().print(" NN(", suspect.getId(), ")");
                return true;
            }
        }
        return false;
    }

    private boolean commonTacticBodySomeSelf(final Role R) {
        if (isCurNodeBlocked()) {
            return false;
        }
        for (DlCompletionTreeArc p : curNode.getNeighbour()) {
            if (p.getArcEnd().equals(curNode) && p.isNeighbour(R)) {
                return false;
            }
        }
        final DepSet dep = DepSet.create(curConceptDepSet);
        DlCompletionTreeArc pA = cGraph.createLoop(curNode, R, dep);
        return setupEdge(pA, dep, redoForall.getValue() | redoFunc.getValue()
                | redoAtMost.getValue() | redoIrr.getValue());
    }

    private boolean commonTacticBodyIrrefl(final Role R) {
        for (DlCompletionTreeArc p : curNode.getNeighbour()) {
            if (this.checkIrreflexivity(p, R, curConceptDepSet)) {
                return true;
            }
        }
        return false;
    }

    private boolean commonTacticBodyProj(final Role R, final int C, final Role ProjR) {
        if (curNode.isLabelledBy(-C)) {
            return false;
        }
        // checkProjection() might change curNode's edge vector and thusly invalidate iterators
        for (int i = 0; i < curNode.getNeighbour().size(); i++) {
            if (curNode.getNeighbour().get(i).isNeighbour(R)) {
                if (checkProjection(curNode.getNeighbour().get(i), C, ProjR)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkProjection(final DlCompletionTreeArc pA, final int C,
            final Role ProjR) {
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

    /// expansion rule for split
    private boolean commonTacticBodySplit(final DLVertex cur) {
        if (duringClassification
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
}

enum AddConceptResult {
    acrClash, acrExist, acrDone
}

class EdgeCompare implements Comparator<DlCompletionTreeArc>, Serializable {
    public int compare(final DlCompletionTreeArc o1, final DlCompletionTreeArc o2) {
        return o1.getArcEnd().compareTo(o2.getArcEnd());
    }
}

/** possible flags of re-checking ALL-like expressions in new nodes */
enum Redo {
    redoForall(1), redoFunc(2), redoAtMost(4), redoIrr(8);
    private final int value;

    Redo(final int i) {
        value = i;
    }

    protected int getValue() {
        return value;
    }
}
