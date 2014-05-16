package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;
import static uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry.resolveSynonym;
import static uk.ac.manchester.cs.jfact.kernel.DagTag.*;
import static uk.ac.manchester.cs.jfact.kernel.KBStatus.*;
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeEntry;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeExpression;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.datatypes.LiteralEntry;
import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.FastSet;
import uk.ac.manchester.cs.jfact.helpers.FastSetFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Pair;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.helpers.Timer;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheConst;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheSingleton;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheState;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.split.SplitVarEntry;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.split.TSplitRules;
import uk.ac.manchester.cs.jfact.split.TSplitVar;
import uk.ac.manchester.cs.jfact.split.TSplitVars;
import conformance.Original;
import conformance.PortedFrom;

/** tbox */
@PortedFrom(file = "dlTBox.h", name = "TBox")
public class TBox implements Serializable {

    private static final long serialVersionUID = 11000L;
    @PortedFrom(file = "dlTBox.h", name = "relevance")
    private long relevance = 1;
    @PortedFrom(file = "dlTBox.h", name = "DLHeap")
    private final DLDag dlHeap;
    /** reasoner for TBox-related queries w/o nominals */
    @PortedFrom(file = "dlTBox.h", name = "stdReasoner")
    private DlSatTester stdReasoner = null;
    /** use this macro to do the same action with all available reasoners */
    /** reasoner for TBox-related queries with nominals */
    @PortedFrom(file = "dlTBox.h", name = "nomReasoner")
    private NominalReasoner nomReasoner;
    /** taxonomy structure of a TBox */
    @PortedFrom(file = "dlTBox.h", name = "pTax")
    private Taxonomy pTax;
    /** classifier */
    @PortedFrom(file = "dlTBox.h", name = "pTaxCreator")
    private DLConceptTaxonomy pTaxCreator;
    /** name-signature map */
    @PortedFrom(file = "dlTBox.h", name = "pName2Sig")
    private Map<NamedEntity, TSignature> pName2Sig;
    /** set of reasoning options */
    @Original
    private final JFactReasonerConfiguration config;
    /** status of the KB */
    @PortedFrom(file = "dlTBox.h", name = "Status")
    private KBStatus kbStatus;
    /** global KB features */
    @PortedFrom(file = "dlTBox.h", name = "KBFeatures")
    private final LogicFeatures KBFeatures = new LogicFeatures();
    /** GCI features */
    @PortedFrom(file = "dlTBox.h", name = "GCIFeatures")
    private final LogicFeatures GCIFeatures = new LogicFeatures();
    /** nominal cloud features */
    @PortedFrom(file = "dlTBox.h", name = "NCFeatures")
    private LogicFeatures nominalCloudFeatures = new LogicFeatures();
    /** aux features */
    @PortedFrom(file = "dlTBox.h", name = "auxFeatures")
    private LogicFeatures auxFeatures = new LogicFeatures();
    /** pointer to current feature (in case of local ones) */
    @PortedFrom(file = "dlTBox.h", name = "curFeature")
    private LogicFeatures curFeature = null;
    /**
     * concept representing temporary one that can not be used anywhere in the
     * ontology
     */
    @PortedFrom(file = "dlTBox.h", name = "pTemp")
    private Concept pTemp;
    /** temporary concept that represents query */
    @PortedFrom(file = "dlTBox.h", name = "pQuery")
    private Concept pQuery;
    /** all named concepts */
    @PortedFrom(file = "dlTBox.h", name = "concepts")
    private final NamedEntryCollection<Concept> concepts;
    /** all named individuals/nominals */
    @PortedFrom(file = "dlTBox.h", name = "individuals")
    private final NamedEntryCollection<Individual> individuals;
    /** "normal" (object) roles */
    @PortedFrom(file = "dlTBox.h", name = "ORM")
    private final RoleMaster objectRoleMaster;
    /** data roles */
    @PortedFrom(file = "dlTBox.h", name = "DRM")
    private final RoleMaster dataRoleMaster;
    /** set of GCIs */
    @PortedFrom(file = "dlTBox.h", name = "Axioms")
    private AxiomSet axioms;
    /** given individual-individual relations */
    @PortedFrom(file = "dlTBox.h", name = "RelatedI")
    private final List<Related> relatedIndividuals = new ArrayList<Related>();
    /** known disjoint sets of individuals */
    @PortedFrom(file = "dlTBox.h", name = "DifferentIndividuals")
    private final List<List<Individual>> differentIndividuals = new ArrayList<List<Individual>>();
    /** all simple rules in KB */
    @PortedFrom(file = "dlTBox.h", name = "SimpleRules")
    private final List<SimpleRule> simpleRules = new ArrayList<SimpleRule>();
    /** split rules */
    @PortedFrom(file = "dlTBox.h", name = "SplitRules")
    private final TSplitRules SplitRules;
    /** internalisation of a general axioms */
    @PortedFrom(file = "dlTBox.h", name = "T_G")
    private int internalisedGeneralAxiom;
    /** KB flags about GCIs */
    @PortedFrom(file = "dlTBox.h", name = "GCIs")
    private final KBFlags GCIs = new KBFlags();
    /** cache for the \forall R.C replacements during absorption */
    @PortedFrom(file = "dlTBox.h", name = "RCCache")
    private final Map<DLTree, Concept> forall_R_C_Cache = new HashMap<DLTree, Concept>();
    /** current aux concept's ID */
    @PortedFrom(file = "dlTBox.h", name = "auxConceptID")
    private int auxConceptID = 0;
    /**
     * how many times nominals were found during translation to DAG; local to
     * BuildDAG
     */
    @PortedFrom(file = "dlTBox.h", name = "nNominalReferences")
    private int nNominalReferences;
    /** searchable stack for the told subsumers */
    @PortedFrom(file = "dlTBox.h", name = "CInProcess")
    private final Set<Concept> conceptInProcess = new HashSet<Concept>();
    /** fairness constraints */
    @PortedFrom(file = "dlTBox.h", name = "Fairness")
    private final List<Concept> fairness = new ArrayList<Concept>();
    // Reasoner's members: there are many reasoner classes, some members are
    // shared
    /** let reasoner know that we are in the classificaton (for splits) */
    @PortedFrom(file = "dlTBox.h", name = "duringClassification")
    private boolean duringClassification;
    /** how many nodes skip before block; work only with FAIRNESS */
    @PortedFrom(file = "dlTBox.h", name = "nSkipBeforeBlock")
    private int nSkipBeforeBlock;
    // Internally defined flags
    /** flag whether TBox is GALEN-like */
    @PortedFrom(file = "dlTBox.h", name = "isLikeGALEN")
    private boolean isLikeGALEN;
    /** flag whether TBox is WINE-like */
    @PortedFrom(file = "dlTBox.h", name = "isLikeWINE")
    private boolean isLikeWINE;
    /** whether KB is consistent */
    @PortedFrom(file = "dlTBox.h", name = "consistent")
    private boolean consistent;
    /** time spend for preprocessing */
    @PortedFrom(file = "dlTBox.h", name = "preprocTime")
    private long preprocTime;
    /** time spend for consistency checking */
    @PortedFrom(file = "dlTBox.h", name = "consistTime")
    private long consistTime;
    /** number of concepts and individuals; used to set index for modelCache */
    @PortedFrom(file = "dlTBox.h", name = "nC")
    protected int nC = 0;
    /** number of all distinct roles; used to set index for modelCache */
    @PortedFrom(file = "dlTBox.h", name = "nR")
    protected int nR = 0;
    /** maps from concept index to concept itself */
    @PortedFrom(file = "dlTBox.h", name = "ConceptMap")
    private final List<Concept> ConceptMap = new ArrayList<Concept>();
    /** map to show the possible equivalence between individuals */
    @PortedFrom(file = "dlTBox.h", name = "SameI")
    private final Map<Concept, Pair> sameIndividuals = new HashMap<Concept, Pair>();
    /** all the synonyms in the told subsumers' cycle */
    @PortedFrom(file = "dlTBox.h", name = "ToldSynonyms")
    private final Set<Concept> toldSynonyms = new HashSet<Concept>();
    /** set of split vars */
    @PortedFrom(file = "dlTBox.h", name = "Splits")
    private TSplitVars Splits;
    /** status of the KB */
    @PortedFrom(file = "dlTBox.h", name = "Status")
    private KBStatus status;

    /**
     * @param s
     *        split vars
     */
    @PortedFrom(file = "dlTBox.h", name = "setSplitVars")
    public void setSplitVars(TSplitVars s) {
        Splits = s;
    }

    /** @return split vars */
    @PortedFrom(file = "dlTBox.h", name = "getSplitVars")
    public TSplitVars getSplits() {
        return Splits;
    }

    /** @return individuals */
    @PortedFrom(file = "dlTBox.h", name = "i_begin")
    public List<Individual> i_begin() {
        return individuals.getList();
    }

    /** @return list of concepts */
    @PortedFrom(file = "dlTBox.h", name = "c_begin")
    public List<Concept> getConcepts() {
        return concepts.getList();
    }

    /** @return reasoner configuration */
    @Original
    public JFactReasonerConfiguration getOptions() {
        return config;
    }

    /**
     * @param bp
     *        bp
     * @return concept by it's BP (non- version)
     */
    @PortedFrom(file = "dlTBox.h", name = "getDataEntryByBP")
    public String getDataEntryByBP(int bp) {
        NamedEntry p = dlHeap.get(bp).getConcept();
        if (p instanceof DatatypeEntry) {
            return ((DatatypeEntry) p).getFacet().toString();
        }
        if (p instanceof LiteralEntry) {
            return ((LiteralEntry) p).getFacet().toString();
        }
        return "";
    }

    /**
     * add description to a concept;
     * 
     * @param p
     *        p
     * @param desc
     *        desc
     * @return true in case of error
     */
    @PortedFrom(file = "dlTBox.h", name = "initNonPrimitive")
    public boolean initNonPrimitive(Concept p, DLTree desc) {
        if (!p.canInitNonPrim(desc)) {
            return true;
        }
        makeNonPrimitive(p, desc);
        return false;
    }

    /**
     * make concept non-primitive;
     * 
     * @param p
     *        p
     * @param desc
     *        desc
     * @return it's old description
     */
    @PortedFrom(file = "dlTBox.h", name = "makeNonPrimitive")
    public DLTree makeNonPrimitive(Concept p, DLTree desc) {
        DLTree ret = p.makeNonPrimitive(desc);
        checkEarlySynonym(p);
        return ret;
    }

    /**
     * checks if C is defined as C=D and set Synonyms accordingly
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "dlTBox.h", name = "checkEarlySynonym")
    public void checkEarlySynonym(Concept p) {
        if (p.isSynonym()) {
            // nothing to do
            return;
        }
        if (p.isPrimitive()) {
            // couldn't be a synonym
            return;
        }
        if (!p.getDescription().isCN()) {
            // complex expression -- not a synonym(imm.)
            return;
        }
        p.setSynonym(getCI(p.getDescription()));
        p.initToldSubsumers();
    }

    /**
     * process a disjoint set [beg,end) in a usual manner
     * 
     * @param beg
     *        beg
     */
    @PortedFrom(file = "dlTBox.h", name = "processDisjoint")
    public void processDisjoint(List<DLTree> beg) {
        while (!beg.isEmpty()) {
            DLTree r = beg.remove(0);
            this.addSubsumeAxiom(r, DLTreeFactory.buildDisjAux(beg));
        }
    }

    /**
     * @param R
     *        R
     * @return create REFLEXIVE node
     */
    @PortedFrom(file = "dlTBox.h", name = "reflexive2dag")
    public int reflexive2dag(Role R) {
        // input check: only simple roles are allowed in the reflexivity
        // construction
        if (!R.isSimple()) {
            throw new ReasonerInternalException(
                    "Non simple role used as simple: " + R.getName());
        }
        return -dlHeap.add(new DLVertex(dtIrr, 0, R, bpINVALID, null));
    }

    /**
     * @param R
     *        R
     * @param C
     *        C
     * @return create forall node for data role
     */
    @PortedFrom(file = "dlTBox.h", name = "dataForall2dag")
    public int dataForall2dag(Role R, int C) {
        return dlHeap.add(new DLVertex(dtForall, 0, R, C, null));
    }

    /**
     * @param n
     *        n
     * @param R
     *        R
     * @param C
     *        C
     * @return create atmost node for data role
     */
    @PortedFrom(file = "dlTBox.h", name = "dataAtMost2dag")
    public int dataAtMost2dag(int n, Role R, int C) {
        return dlHeap.add(new DLVertex(dtLE, n, R, C, null));
    }

    /**
     * @param p
     *        p
     * @return a pointer to concept representation
     */
    @PortedFrom(file = "dlTBox.h", name = "concept2dag")
    public int concept2dag(Concept p) {
        if (p == null) {
            return bpINVALID;
        }
        if (!isValid(p.getpName())) {
            addConceptToHeap(p);
        }
        return p.resolveId();
    }

    /**
     * try to absorb GCI C[=D; if not possible, just record this GCI
     * 
     * @param C
     *        C
     * @param D
     *        D
     */
    @PortedFrom(file = "dlTBox.h", name = "processGCI")
    public void processGCI(DLTree C, DLTree D) {
        axioms.addAxiom(C, D);
    }

    /** absorb all axioms */
    @PortedFrom(file = "dlTBox.h", name = "AbsorbAxioms")
    public void absorbAxioms() {
        int nSynonyms = countSynonyms();
        axioms.absorb();
        if (countSynonyms() > nSynonyms) {
            replaceAllSynonyms();
        }
        if (axioms.wasRoleAbsorptionApplied()) {
            initToldSubsumers();
        }
    }

    /** set told TOP concept whether necessary */
    @PortedFrom(file = "dlTBox.h", name = "initToldSubsumers")
    public void initToldSubsumers() {
        for (Concept pc : concepts.getList()) {
            if (!pc.isSynonym()) {
                pc.initToldSubsumers();
            }
        }
        for (Individual pi : individuals.getList()) {
            if (!pi.isSynonym()) {
                pi.initToldSubsumers();
            }
        }
    }

    /** set told TOP concept whether necessary */
    @PortedFrom(file = "dlTBox.h", name = "setToldTop")
    public void setToldTop() {
        for (Concept pc : concepts.getList()) {
            pc.setToldTop(top);
        }
        for (Individual pi : individuals.getList()) {
            pi.setToldTop(top);
        }
    }

    /** calculate TS depth for all concepts */
    @PortedFrom(file = "dlTBox.h", name = "calculateTSDepth")
    public void calculateTSDepth() {
        for (Concept pc : concepts.getList()) {
            pc.calculateTSDepth();
        }
        for (Individual pi : individuals.getList()) {
            pi.calculateTSDepth();
        }
    }

    /** @return number of synonyms in the KB */
    @PortedFrom(file = "dlTBox.h", name = "countSynonyms")
    public int countSynonyms() {
        int nSynonyms = 0;
        for (Concept pc : concepts.getList()) {
            if (pc.isSynonym()) {
                ++nSynonyms;
            }
        }
        for (Individual pi : individuals.getList()) {
            if (pi.isSynonym()) {
                ++nSynonyms;
            }
        }
        return nSynonyms;
    }

    /**
     * init Extra Rule field in concepts given by a vector V with a given INDEX
     * 
     * @param v
     *        v
     * @param index
     *        index
     */
    @PortedFrom(file = "dlTBox.h", name = "initRuleFields")
    public void initRuleFields(List<Concept> v, int index) {
        for (Concept q : v) {
            q.addExtraRule(index);
        }
    }

    /** mark all concepts wrt their classification tag */
    @PortedFrom(file = "dlTBox.h", name = "fillsClassificationTag")
    public void fillsClassificationTag() {
        for (Concept pc : concepts.getList()) {
            pc.getClassTag();
        }
        for (Individual pi : individuals.getList()) {
            pi.getClassTag();
        }
    }

    /**
     * set new concept index for given C wrt existing nC
     * 
     * @param C
     *        C
     */
    @PortedFrom(file = "dlTBox.h", name = "setConceptIndex")
    public void setConceptIndex(Concept C) {
        C.setIndex(nC);
        ConceptMap.add(C);
        ++nC;
    }

    /** @return true iff reasoners were initialised */
    @PortedFrom(file = "dlTBox.h", name = "reasonersInited")
    private boolean reasonersInited() {
        return stdReasoner != null;
    }

    /** @return reasoner wrt nominal case */
    @PortedFrom(file = "dlTBox.h", name = "getReasoner")
    public DlSatTester getReasoner() {
        assert curFeature != null;
        if (curFeature.hasSingletons()) {
            return nomReasoner;
        } else {
            return stdReasoner;
        }
    }

    /**
     * print all registered concepts
     * 
     * @param o
     *        o
     */
    @PortedFrom(file = "dlTBox.h", name = "PrintConcepts")
    public void printConcepts(LogAdapter o) {
        if (concepts.size() == 0) {
            return;
        }
        o.print("Concepts (", concepts.size(), "):\n");
        for (Concept pc : concepts.getList()) {
            printConcept(o, pc);
        }
    }

    /**
     * print all registered individuals
     * 
     * @param o
     *        o
     */
    @PortedFrom(file = "dlTBox.h", name = "PrintIndividuals")
    public void printIndividuals(LogAdapter o) {
        if (individuals.size() == 0) {
            return;
        }
        o.print("Individuals (", individuals.size(), "):\n");
        for (Individual pi : individuals.getList()) {
            printConcept(o, pi);
        }
    }

    /**
     * @param o
     *        o
     */
    @PortedFrom(file = "dlTBox.h", name = "PrintSimpleRules")
    public void printSimpleRules(LogAdapter o) {
        if (simpleRules.isEmpty()) {
            return;
        }
        o.print("Simple rules (", simpleRules.size(), "):\n");
        for (SimpleRule p : simpleRules) {
            o.print("(");
            for (int i = 0; i < p.getBody().size(); i++) {
                if (i > 0) {
                    o.print(", ");
                }
                o.print(p.getBody().get(i).getName());
            }
            o.print(") => ", p.tHead, "\n");
        }
    }

    /**
     * @param o
     *        o
     */
    @PortedFrom(file = "dlTBox.h", name = "PrintAxioms")
    public void printAxioms(LogAdapter o) {
        if (internalisedGeneralAxiom == bpTOP) {
            return;
        }
        o.print("Axioms:\nT [=");
        printDagEntry(o, internalisedGeneralAxiom);
    }

    /**
     * @param R
     *        R
     * @return check if the role R is irreflexive
     */
    @PortedFrom(file = "dlTBox.h", name = "isIrreflexive")
    public boolean isIrreflexive(Role R) {
        assert R != null;
        // data roles are irreflexive
        if (R.isDataRole()) {
            return true;
        }
        // prepare feature that are KB features
        // FIXME!! overkill, but fine for now as it is sound
        curFeature = KBFeatures;
        getReasoner().setBlockingMethod(isIRinQuery(), isNRinQuery());
        boolean result = getReasoner().checkIrreflexivity(R);
        clearFeatures();
        return result;
    }

    /**
     * gather information about logical features of relevant concept
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "dlTBox.h", name = "collectLogicFeature")
    private void collectLogicFeature(Concept p) {
        if (curFeature != null) {
            curFeature.fillConceptData(p);
        }
    }

    /**
     * gather information about logical features of relevant role
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "dlTBox.h", name = "collectLogicFeature")
    private void collectLogicFeature(Role p) {
        if (curFeature != null) {
            curFeature.fillRoleData(p, p.inverse().isRelevant(relevance));
        }
    }

    /**
     * gather information about logical features of relevant DAG entry
     * 
     * @param v
     *        v
     * @param pos
     *        pos
     */
    @PortedFrom(file = "dlTBox.h", name = "collectLogicFeature")
    private void collectLogicFeature(DLVertex v, boolean pos) {
        if (curFeature != null) {
            curFeature.fillDAGData(v, pos);
        }
    }

    /** mark all active GCIs relevant */
    @PortedFrom(file = "dlTBox.h", name = "markGCIsRelevant")
    private void markGCIsRelevant() {
        setRelevant(internalisedGeneralAxiom);
    }

    /** set all TBox content (namely, concepts and GCIs) relevant */
    @PortedFrom(file = "dlTBox.h", name = "markAllRelevant")
    private void markAllRelevant() {
        for (Concept pc : concepts.getList()) {
            if (!pc.isRelevant(relevance)) {
                ++nRelevantCCalls;
                pc.setRelevant(relevance);
                this.collectLogicFeature(pc);
                setRelevant(pc.getpBody());
            }
        }
        for (Individual pi : individuals.getList()) {
            if (!pi.isRelevant(relevance)) {
                ++nRelevantCCalls;
                pi.setRelevant(relevance);
                this.collectLogicFeature(pi);
                setRelevant(pi.getpBody());
            }
        }
        markGCIsRelevant();
    }

    /** clear all relevance info */
    @PortedFrom(file = "dlTBox.h", name = "clearRelevanceInfo")
    public void clearRelevanceInfo() {
        relevance++;
    }

    /** @return fresh concept */
    @PortedFrom(file = "dlTBox.h", name = "getFreshConcept")
    public DLTree getFreshConcept() {
        return DLTreeFactory.buildTree(new Lexeme(CNAME, pTemp));
    }

    /**
     * put relevance information to a concept's data
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "dlTBox.h", name = "setConceptRelevant")
    private void setConceptRelevant(Concept p) {
        curFeature = p.getPosFeatures();
        setRelevant(p.getpBody());
        KBFeatures.or(p.getPosFeatures());
        this.collectLogicFeature(p);
        clearRelevanceInfo();
        // nothing to do for neg-prim concepts
        if (p.isPrimitive()) {
            return;
        }
        curFeature = p.getNegFeatures();
        setRelevant(-p.getpBody());
        KBFeatures.or(p.getNegFeatures());
        clearRelevanceInfo();
    }

    /**
     * update AUX features with the given one; update roles if necessary
     * 
     * @param lf
     *        lf
     */
    @PortedFrom(file = "dlTBox.h", name = "updateAuxFeatures")
    private void updateAuxFeatures(LogicFeatures lf) {
        if (!lf.isEmpty()) {
            auxFeatures.or(lf);
            auxFeatures.mergeRoles();
        }
    }

    /** clear current features */
    @PortedFrom(file = "dlTBox.h", name = "clearFeatures")
    public void clearFeatures() {
        curFeature = null;
    }

    /** @return object Role Master */
    @PortedFrom(file = "dlTBox.h", name = "getORM")
    public RoleMaster getORM() {
        return objectRoleMaster;
    }

    /** @return Data Role Master */
    @PortedFrom(file = "dlTBox.h", name = "getDRM")
    public RoleMaster getDRM() {
        return dataRoleMaster;
    }

    /**
     * @param R
     *        R
     * @return RoleMaster depending of the R
     */
    @PortedFrom(file = "dlTBox.h", name = "getRM")
    public RoleMaster getRM(Role R) {
        return R.isDataRole() ? dataRoleMaster : objectRoleMaster;
    }

    /** @return DAG (needed for KE) */
    @PortedFrom(file = "dlTBox.h", name = "getDag")
    public DLDag getDag() {
        return dlHeap;
    }

    /**
     * return registered concept by given NAME;
     * 
     * @param name
     *        name
     * @return null if can't register
     */
    @PortedFrom(file = "dlTBox.h", name = "getConcept")
    public Concept getConcept(IRI name) {
        return concepts.get(name);
    }

    /**
     * return registered individual by given NAME;
     * 
     * @param name
     *        name
     * @return null if can't register
     */
    @PortedFrom(file = "dlTBox.h", name = "getIndividual")
    public Individual getIndividual(IRI name) {
        return individuals.get(name);
    }

    /**
     * @param name
     *        name
     * @return true iff given NAME is a name of a registered individual
     */
    @PortedFrom(file = "dlTBox.h", name = "isIndividual")
    private boolean isIndividual(IRI name) {
        return individuals.isRegistered(name);
    }

    /**
     * @param tree
     *        tree
     * @return true iff given TREE represents a registered individual
     */
    @PortedFrom(file = "dlTBox.h", name = "isIndividual")
    public boolean isIndividual(DLTree tree) {
        return tree.token() == INAME
                && this.isIndividual(tree.elem().getNE().getName());
    }

    // TODO move
    /**
     * @param name
     *        name
     * @return TOP/BOTTOM/CN/IN by the DLTree entry
     */
    @PortedFrom(file = "dlTBox.h", name = "getCI")
    public Concept getCI(DLTree name) {
        if (name.isTOP()) {
            return top;
        }
        if (name.isBOTTOM()) {
            return bottom;
        }
        if (!name.isName()) {
            return null;
        }
        if (name.token() == CNAME) {
            return (Concept) name.elem().getNE();
        } else {
            return (Individual) name.elem().getNE();
        }
    }

    /**
     * @param C
     *        C
     * @return a DL tree by a given concept-like C
     */
    @PortedFrom(file = "dlTBox.h", name = "getTree")
    public DLTree getTree(Concept C) {
        if (C == null) {
            return null;
        }
        if (C.isTop()) {
            return DLTreeFactory.createTop();
        }
        if (C.isBottom()) {
            return DLTreeFactory.createBottom();
        }
        return DLTreeFactory.buildTree(new Lexeme(
                this.isIndividual(C.getName()) ? INAME : CNAME, C));
    }

    /**
     * set the flag that forbid usage of undefined names for concepts/roles;
     * 
     * @param val
     *        val
     * @return old value
     */
    @PortedFrom(file = "dlTBox.h", name = "setForbidUndefinedNames")
    public boolean setForbidUndefinedNames(boolean val) {
        objectRoleMaster.setUndefinedNames(!val);
        dataRoleMaster.setUndefinedNames(!val);
        individuals.setLocked(val);
        return concepts.setLocked(val);
    }

    /**
     * individual relation (a,b):R
     * 
     * @param a
     *        a
     * @param R
     *        R
     * @param b
     *        b
     */
    @PortedFrom(file = "dlTBox.h", name = "RegisterIndividualRelation")
    public void registerIndividualRelation(NamedEntry a, NamedEntry R,
            NamedEntry b) {
        if (!this.isIndividual(a.getName()) || !this.isIndividual(b.getName())) {
            throw new ReasonerInternalException(
                    "Individual expected in related()");
        }
        relatedIndividuals.add(new Related((Individual) a, (Individual) b,
                (Role) R));
        relatedIndividuals.add(new Related((Individual) b, (Individual) a,
                ((Role) R).inverse()));
    }

    /**
     * add axiom CN [= D for concept CN
     * 
     * @param C
     *        C
     * @param D
     *        D
     */
    @PortedFrom(file = "dlTBox.h", name = "addSubsumeAxiom")
    public void addSubsumeAxiom(Concept C, DLTree D) {
        this.addSubsumeAxiom(getTree(C), D);
    }

    /**
     * add simple rule RULE to the TBox' rules
     * 
     * @param Rule
     *        Rule
     */
    @PortedFrom(file = "dlTBox.h", name = "addSimpleRule")
    private void addSimpleRule(SimpleRule Rule) {
        initRuleFields(Rule.getBody(), simpleRules.size());
        simpleRules.add(Rule);
    }

    /** let TBox know that the whole ontology is loaded */
    @PortedFrom(file = "dlTBox.h", name = "finishLoading")
    public void finishLoading() {
        setForbidUndefinedNames(true);
    }

    /** @return true if KB contains fairness constraints */
    @PortedFrom(file = "dlTBox.h", name = "hasFC")
    public boolean hasFC() {
        return !fairness.isEmpty();
    }

    @PortedFrom(file = "dlTBox.h", name = "setFairnessConstraint")
    private void setFairnessConstraint(Collection<DLTree> c) {
        for (DLTree beg : c) {
            if (beg.isName()) {
                fairness.add(getCI(beg));
            } else {
                // build a flag for a FC
                Concept fc = getAuxConcept(null);
                fairness.add(fc);
                // make an axiom: FC = C
                addEqualityAxiom(getTree(fc), beg);
            }
        }
        // in presence of fairness constraints use ancestor blocking
        if (config.getuseAnywhereBlocking() && hasFC()) {
            config.setuseAnywhereBlocking(false);
            config.getLog().print(
                    "\nFairness constraints: set useAnywhereBlocking = 0");
        }
    }

    /**
     * @param l
     *        l
     */
    @PortedFrom(file = "dlTBox.h", name = "setFairnessConstraint")
    public void setFairnessConstraintDLTrees(List<DLTree> l) {
        for (int i = 0; i < l.size(); i++) {
            // build a flag for a FC
            Concept fc = getAuxConcept(null);
            fairness.add(fc);
            // make an axiom: C [= FC
            this.addSubsumeAxiom(l.get(i), getTree(fc));
        }
    }

    /** @return GCI Axioms */
    @PortedFrom(file = "dlTBox.h", name = "getTG")
    public int getTG() {
        return internalisedGeneralAxiom;
    }

    /**
     * @param index
     *        index
     * @return simple rule by its INDEX
     */
    @PortedFrom(file = "dlTBox.h", name = "getSimpleRule")
    public SimpleRule getSimpleRule(int index) {
        return simpleRules.get(index);
    }

    /** @return check if the relevant part of KB contains inverse roles. */
    @PortedFrom(file = "dlTBox.h", name = "isIRinQuery")
    public boolean isIRinQuery() {
        if (curFeature != null) {
            return curFeature.hasInverseRole();
        } else {
            return KBFeatures.hasInverseRole();
        }
    }

    /** @return check if the relevant part of KB contains number restrictions. */
    @PortedFrom(file = "dlTBox.h", name = "isNRinQuery")
    public boolean isNRinQuery() {
        LogicFeatures p = curFeature != null ? curFeature : KBFeatures;
        return p.hasFunctionalRestriction() || p.hasNumberRestriction()
                || p.hasQNumberRestriction();
    }

    /** @return check if the relevant part of KB contains singletons */
    @PortedFrom(file = "dlTBox.h", name = "testHasNominals")
    public boolean testHasNominals() {
        if (curFeature != null) {
            return curFeature.hasSingletons();
        } else {
            return KBFeatures.hasSingletons();
        }
    }

    /** @return check if the relevant part of KB contains top role */
    @PortedFrom(file = "dlTBox.h", name = "testHasTopRole")
    public boolean testHasTopRole() {
        if (curFeature != null) {
            return curFeature.hasTopRole();
        } else {
            return KBFeatures.hasTopRole();
        }
    }

    /** @return check if Sorted Reasoning is applicable */
    @PortedFrom(file = "dlTBox.h", name = "canUseSortedReasoning")
    public boolean canUseSortedReasoning() {
        return config.isUseSortedReasoning() && !GCIs.isGCI()
                && !GCIs.isReflexive();
    }

    /** perform classification (assuming KB is consistent) */
    @PortedFrom(file = "dlTBox.h", name = "performClassification")
    public void performClassification() {
        createTaxonomy(false);
    }

    /** perform realisation (assuming KB is consistent) */
    @PortedFrom(file = "dlTBox.h", name = "performRealisation")
    public void performRealisation() {
        createTaxonomy(true);
    }

    /** @return internal Taxonomy of concepts */
    @PortedFrom(file = "dlTBox.h", name = "getTaxonomy")
    public Taxonomy getTaxonomy() {
        return pTax;
    }

    /** @return status flag */
    @PortedFrom(file = "dlTBox.h", name = "getStatus")
    public KBStatus getStatus() {
        return kbStatus;
    }

    /**
     * set consistency flag
     * 
     * @param val
     *        val
     */
    @PortedFrom(file = "dlTBox.h", name = "setConsistency")
    public void setConsistency(boolean val) {
        kbStatus = kbCChecked;
        consistent = val;
    }

    /** @return check if the ontology is consistent */
    @PortedFrom(file = "dlTBox.h", name = "isConsistent")
    public boolean isConsistent() {
        if (kbStatus.ordinal() < kbCChecked.ordinal()) {
            prepareReasoning();
            if (kbStatus.ordinal() < kbCChecked.ordinal() && consistent) {
                setConsistency(performConsistencyCheck());
            }
        }
        return consistent;
    }

    /**
     * @param p
     *        p
     * @param q
     *        q
     * @return test if 2 concept non-subsumption can be determined by sorts
     *         checking
     */
    @PortedFrom(file = "dlTBox.h", name = "testSortedNonSubsumption")
    public boolean testSortedNonSubsumption(Concept p, Concept q) {
        // sorted reasoning doesn't work in presence of GCIs
        if (!canUseSortedReasoning()) {
            return false;
        }
        // doesn't work for the SAT tests
        if (q == null) {
            return false;
        }
        return !dlHeap.haveSameSort(p.getpName(), q.getpName());
    }

    /** print TBox as a whole */
    @PortedFrom(file = "dlTBox.h", name = "print")
    public void print() {
        dlHeap.printStat(config.getLog());
        objectRoleMaster.print(config.getLog(), "Object");
        dataRoleMaster.print(config.getLog(), "Data");
        printConcepts(config.getLog());
        printIndividuals(config.getLog());
        printSimpleRules(config.getLog());
        printAxioms(config.getLog());
        config.getLog().print(dlHeap);
    }

    /** build dag */
    @PortedFrom(file = "dlTBox.h", name = "buildDAG")
    public void buildDAG() {
        nNominalReferences = 0;
        // init concept indexing
        // start with 1 to make index 0 an indicator of "not processed"
        nC = 1;
        ConceptMap.add(null);
        // make fresh concept and datatype
        concept2dag(pTemp);
        for (Concept pc : concepts.getList()) {
            concept2dag(pc);
        }
        for (Individual pi : individuals.getList()) {
            concept2dag(pi);
        }
        for (SimpleRule q : simpleRules) {
            q.setBpHead(tree2dag(q.tHead));
        }
        // builds Roles range and domain
        initRangeDomain(objectRoleMaster);
        initRangeDomain(dataRoleMaster);
        // build all splits
        for (TSplitVar s : getSplits().getEntries()) {
            split2dag(s);
        }
        DLTree GCI = axioms.getGCI();
        // add special domains to the GCIs
        List<DLTree> list = new ArrayList<DLTree>();
        if (config.isUseSpecialDomains()) {
            for (Role p : objectRoleMaster.getRoles()) {
                if (!p.isSynonym() && p.hasSpecialDomain()) {
                    list.add(p.getTSpecialDomain().copy());
                }
            }
        }
        // take chains that lead to Bot role into account
        if (!objectRoleMaster.getBotRole().isSimple()) {
            list.add(DLTreeFactory.createSNFForall(
                    DLTreeFactory.createRole(objectRoleMaster.getBotRole()),
                    DLTreeFactory.createBottom()));
        }
        if (!list.isEmpty()) {
            list.add(GCI);
            GCI = DLTreeFactory.createSNFAnd(list);
        }
        internalisedGeneralAxiom = tree2dag(GCI);
        GCI = null;
        // mark GCI flags
        GCIs.setGCI(internalisedGeneralAxiom != bpTOP);
        GCIs.setReflexive(objectRoleMaster.hasReflexiveRoles());
        for (Role p : objectRoleMaster.getRoles()) {
            if (!p.isSynonym() && p.isTopFunc()) {
                p.setFunctional(atmost2dag(1, p, bpTOP));
            }
        }
        for (Role p : dataRoleMaster.getRoles()) {
            if (!p.isSynonym() && p.isTopFunc()) {
                p.setFunctional(atmost2dag(1, p, bpTOP));
            }
        }
        // concept2dag(pTemp);
        if (nNominalReferences > 0) {
            int nInd = individuals.getList().size();
            if (nInd > 100 && nNominalReferences > nInd) {
                isLikeWINE = true;
            }
        }
        // here DAG is complete; set its size
        dlHeap.setFinalSize();
    }

    /**
     * @param RM
     *        RM
     */
    @PortedFrom(file = "dlTBox.h", name = "initRangeDomain")
    public void initRangeDomain(RoleMaster RM) {
        for (Role p : RM.getRoles()) {
            if (!p.isSynonym()) {
                Role R = p;
                if (config.isRKG_UPDATE_RND_FROM_SUPERROLES()) {
                    // add R&D from super-roles (do it AFTER axioms are
                    // transformed into R&D)
                    R.collectDomainFromSupers();
                }
                DLTree dom = R.getTDomain();
                int bp = bpTOP;
                if (dom != null) {
                    bp = tree2dag(dom);
                    GCIs.setRnD(true);
                }
                R.setBPDomain(bp);
                // special domain for R is AR.Range
                R.initSpecialDomain();
                if (R.hasSpecialDomain()) {
                    R.setSpecialDomain(tree2dag(R.getTSpecialDomain()));
                }
            }
        }
    }

    /** build up split rules for reasoners; create them after DAG is build */
    @PortedFrom(file = "dlTBox.h", name = "buildSplitRules")
    private void buildSplitRules() {
        if (!getSplits().empty()) {
            getSplitRules().createSplitRules(getSplits());
            getSplitRules().initEntityMap(dlHeap);
        }
    }

    /**
     * @param p
     *        p
     * @return index of new element
     */
    @PortedFrom(file = "dlTBox.h", name = "addDataExprToHeap")
    public int addDataExprToHeap(LiteralEntry p) {
        int toReturn = 0;
        if (isValid(p.getIndex())) {
            toReturn = p.getIndex();
        } else {
            DagTag dt = dtDataValue;
            int hostBP = bpTOP;
            if (p.getType() != null) {
                hostBP = addDatatypeExpressionToHeap(p.getType());
            }
            DLVertex ver = new DLVertex(dt, 0, null, hostBP, null);
            ver.setConcept(p);
            p.setIndex(dlHeap.directAdd(ver));
            toReturn = p.getIndex();
        }
        return toReturn;
    }

    /**
     * @param p
     *        p
     * @return index of new element
     */
    @PortedFrom(file = "dlTBox.h", name = "addDataExprToHeap")
    public int addDataExprToHeap(DatatypeEntry p) {
        int toReturn = 0;
        if (isValid(p.getIndex())) {
            toReturn = p.getIndex();
        } else {
            DagTag dt = p.isBasicDataType() ? dtDataType : dtDataExpr;
            int hostBP = bpTOP;
            if (!p.isBasicDataType()) {
                Datatype<?> baseType = ((DatatypeExpression<?>) p.getDatatype())
                        .getHostType();
                hostBP = addDatatypeExpressionToHeap(baseType);
            }
            DLVertex ver = new DLVertex(dt, 0, null, hostBP, null);
            ver.setConcept(p);
            p.setIndex(dlHeap.directAdd(ver));
            toReturn = p.getIndex();
        }
        return toReturn;
    }

    /**
     * @param p
     *        p
     * @return index of new element
     */
    @Original
    public int addDatatypeExpressionToHeap(Datatype<?> p) {
        int hostBP = 0;
        DatatypeEntry concept = new DatatypeEntry(p);
        int index = dlHeap.index(concept);
        if (index != bpINVALID) {
            hostBP = index;
        } else {
            // else, create a new vertex and add it
            DLVertex ver = new DLVertex(dtDataType, 0, null, bpTOP, null);
            ver.setConcept(concept);
            int directAdd = dlHeap.directAdd(ver);
            hostBP = directAdd;
        }
        return hostBP;
    }

    /**
     * @param pConcept
     *        pConcept
     */
    @PortedFrom(file = "dlTBox.h", name = "addConceptToHeap")
    public void addConceptToHeap(Concept pConcept) {
        // choose proper tag by concept
        DagTag tag = pConcept.isPrimitive() ? pConcept.isSingleton() ? dtPSingleton
                : dtPConcept
                : pConcept.isSingleton() ? dtNSingleton : dtNConcept;
        // NSingleton is a nominal
        if (tag == dtNSingleton && !pConcept.isSynonym()) {
            ((Individual) pConcept).setNominal(true);
        }
        // new concept's addition
        DLVertex ver = new DLVertex(tag);
        ver.setConcept(pConcept);
        pConcept.setpName(dlHeap.directAdd(ver));
        int desc = bpTOP;
        // translate body of a concept
        if (pConcept.getDescription() != null) {
            desc = tree2dag(pConcept.getDescription());
        } else {
            assert pConcept.isPrimitive();
        }
        // update concept's entry
        pConcept.setpBody(desc);
        ver.setChild(desc);
        if (!pConcept.isSynonym() && pConcept.getIndex() == 0) {
            setConceptIndex(pConcept);
        }
    }

    /**
     * @param t
     *        t
     * @return index of new element
     */
    @PortedFrom(file = "dlTBox.h", name = "tree2dag")
    public int tree2dag(DLTree t) {
        if (t == null) {
            return bpINVALID;
        }
        Lexeme cur = t.elem();
        int ret = bpINVALID;
        switch (cur.getToken()) {
            case BOTTOM:
                ret = bpBOTTOM;
                break;
            case TOP:
                ret = bpTOP;
                break;
            case DATAEXPR:
                if (cur.getNE() instanceof DatatypeEntry) {
                    ret = this.addDataExprToHeap((DatatypeEntry) cur.getNE());
                } else {
                    ret = this.addDataExprToHeap((LiteralEntry) cur.getNE());
                }
                break;
            case CNAME:
                ret = concept2dag((Concept) cur.getNE());
                break;
            case INAME:
                ++nNominalReferences;
                // definitely a nominal
                Individual ind = (Individual) cur.getNE();
                ind.setNominal(true);
                ret = concept2dag(ind);
                break;
            case NOT:
                ret = -tree2dag(t.getChild());
                break;
            case AND:
                ret = and2dag(new DLVertex(dtAnd), t);
                break;
            case FORALL:
                ret = forall2dag(Role.resolveRole(t.getLeft()),
                        tree2dag(t.getRight()));
                break;
            case SELF:
                ret = reflexive2dag(Role.resolveRole(t.getChild()));
                break;
            case LE:
                ret = atmost2dag(cur.getData(), Role.resolveRole(t.getLeft()),
                        tree2dag(t.getRight()));
                break;
            case PROJFROM:
                ret = dlHeap
                        .directAdd(new DLVertex(DagTag.dtProj, 0, Role
                                .resolveRole(t.getLeft()), tree2dag(t
                                .getRight().getRight()), Role.resolveRole(t
                                .getRight().getLeft())));
                break;
            default:
                assert DLTreeFactory.isSNF(t);
                throw new UnreachableSituationException();
        }
        return ret;
    }

    /**
     * fills AND-like vertex V with an AND-like expression T; process result
     * 
     * @param v
     *        v
     * @param t
     *        t
     * @return heap size
     */
    @PortedFrom(file = "dlTBox.h", name = "and2dag")
    public int and2dag(DLVertex v, DLTree t) {
        int ret = bpBOTTOM;
        if (!fillANDVertex(v, t)) {
            int value = v.getAndToDagValue();
            if (value != bpINVALID) {
                return value;
            }
            return dlHeap.add(v);
        }
        return ret;
    }

    /**
     * @param R
     *        R
     * @param C
     *        C
     * @return index of new element
     */
    @PortedFrom(file = "dlTBox.h", name = "forall2dag")
    public int forall2dag(Role R, int C) {
        if (R.isDataRole()) {
            return dataForall2dag(R, C);
        }
        int ret = dlHeap.add(new DLVertex(dtForall, 0, R, C, null));
        if (R.isSimple()) {
            return ret;
        }
        if (!dlHeap.isLast(ret)) {
            return ret;
        }
        for (int i = 1; i < R.getAutomaton().size(); ++i) {
            dlHeap.directAddAndCache(new DLVertex(dtForall, i, R, C, null));
        }
        return ret;
    }

    /**
     * @param n
     *        n
     * @param R
     *        R
     * @param C
     *        C
     * @return index of new element
     */
    @PortedFrom(file = "dlTBox.h", name = "atmost2dag")
    public int atmost2dag(int n, Role R, int C) {
        if (!R.isSimple()) {
            throw new ReasonerInternalException(
                    "Non simple role used as simple: " + R.getName());
        }
        if (R.isDataRole()) {
            return dataAtMost2dag(n, R, C);
        }
        if (C == bpBOTTOM) {
            // can happen as A & ~A
            return bpTOP;
        }
        int ret = dlHeap.add(new DLVertex(dtLE, n, R, C, null));
        if (!dlHeap.isLast(ret)) {
            return ret;
        }
        for (int m = n - 1; m > 0; --m) {
            dlHeap.directAddAndCache(new DLVertex(dtLE, m, R, C, null));
        }
        dlHeap.directAddAndCache(new DLVertex(dtNN));
        return ret;
    }

    /**
     * transform splitted concept registered in SPLIT to a dag representation
     * 
     * @param split
     *        split
     */
    @PortedFrom(file = "dlTBox.h", name = "split2dag")
    private void split2dag(TSplitVar split) {
        DLVertex v = new DLVertex(dtSplitConcept);
        for (SplitVarEntry p : split.getEntries()) {
            v.addChild(p.concept.getpName());
        }
        split.getC().setpBody(dlHeap.directAdd(v));
        split.getC().setPrimitive(false);
        dlHeap.replaceVertex(split.getC().getpName(), new DLVertex(dtNConcept,
                0, null, split.getC().getpBody(), null), split.getC());
        dlHeap.directAdd(new DLVertex(dtChoose, 0, null, split.getC()
                .getpName(), null));
    }

    @PortedFrom(file = "dlTBox.h", name = "fillANDVertex")
    private boolean fillANDVertex(DLVertex v, DLTree t) {
        if (t.isAND()) {
            boolean ret = false;
            List<DLTree> children = t.getChildren();
            int size = children.size();
            for (int i = 0; i < size; i++) {
                ret |= fillANDVertex(v, children.get(i));
            }
            return ret;
        } else {
            return v.addChild(tree2dag(t));
        }
    }

    @PortedFrom(file = "dlTBox.h", name = "arrayCD")
    private final List<Concept> arrayCD = new ArrayList<Concept>();
    @PortedFrom(file = "dlTBox.h", name = "arrayNoCD")
    private final List<Concept> arrayNoCD = new ArrayList<Concept>();
    @PortedFrom(file = "dlTBox.h", name = "arrayNP")
    private final List<Concept> arrayNP = new ArrayList<Concept>();

    /**
     * @param begin
     *        begin
     * @param <T>
     *        concept type
     * @return number of elements
     */
    @PortedFrom(file = "dlTBox.h", name = "fillArrays")
    public <T extends Concept> int fillArrays(List<T> begin) {
        int n = 0;
        for (T p : begin) {
            if (p.isNonClassifiable()) {
                continue;
            }
            ++n;
            switch (p.getClassTag()) {
                case cttTrueCompletelyDefined:
                    arrayCD.add(p);
                    break;
                case cttNonPrimitive:
                case cttHasNonPrimitiveTS:
                    arrayNP.add(p);
                    break;
                default:
                    arrayNoCD.add(p);
                    break;
            }
        }
        return n;
    }

    @Original
    private int nItems = 0;
    @Original
    private final AtomicBoolean interrupted;
    @Original
    private final DatatypeFactory datatypeFactory;

    /** @return n items */
    @Original
    public int getNItems() {
        return nItems;
    }

    /**
     * @param needIndividual
     *        needIndividual
     */
    @PortedFrom(file = "dlTBox.h", name = "createTaxonomy")
    public void createTaxonomy(boolean needIndividual) {
        boolean needConcept = !needIndividual;
        // if there were SAT queries before -- the query concept is in there.
        // Delete it
        clearQueryConcept();
        // here we sure that ontology is consistent
        // FIXME!! distinguish later between the 1st run and the following runs
        dlHeap.setSubOrder();
        // initTaxonomy();
        pTaxCreator.setBottomUp(GCIs);
        needConcept |= needIndividual;
        if (config.getverboseOutput()) {
            config.getLog().print("Processing query...\n");
        }
        Timer locTimer = new Timer();
        locTimer.start();
        nItems = 0;
        arrayCD.clear();
        arrayNoCD.clear();
        arrayNP.clear();
        nItems += this.fillArrays(concepts.getList());
        nItems += this.fillArrays(individuals.getList());
        if (config.getProgressMonitor() != null) {
            config.getProgressMonitor().reasonerTaskStarted(
                    ReasonerProgressMonitor.CLASSIFYING);
        }
        duringClassification = true;
        classifyConcepts(arrayCD, true, "completely defined");
        classifyConcepts(arrayNoCD, false, "regular");
        classifyConcepts(arrayNP, false, "non-primitive");
        duringClassification = false;
        pTaxCreator.processSplits();
        if (config.getProgressMonitor() != null) {
            config.getProgressMonitor().reasonerTaskStopped();
        }
        pTax.finalise();
        locTimer.stop();
        if (config.getverboseOutput()) {
            config.getLog().print(" done in ").print(locTimer.calcDelta())
                    .print(" seconds\n\n");
        }
        if (needConcept && kbStatus.ordinal() < kbClassified.ordinal()) {
            kbStatus = kbClassified;
        }
        if (needIndividual) {
            kbStatus = kbRealised;
        }
        if (config.getverboseOutput()) {
            config.getLog().print(pTax);
        }
    }

    /**
     * @param collection
     *        collection
     * @param curCompletelyDefined
     *        curCompletelyDefined
     * @param type
     *        type
     */
    @PortedFrom(file = "dlTBox.h", name = "classifyConcepts")
    public void classifyConcepts(List<Concept> collection,
            boolean curCompletelyDefined, String type) {
        pTaxCreator.setCompletelyDefined(curCompletelyDefined);
        config.getLog().printTemplate(Templates.CLASSIFY_CONCEPTS, type);
        int n = 0;
        for (Concept q : collection) {
            if (!interrupted.get() && !q.isClassified()) {
                // need to classify concept
                classifyEntry(q);
                if (q.isClassified()) {
                    ++n;
                }
            }
        }
        config.getLog().printTemplate(Templates.CLASSIFY_CONCEPTS2, n, type);
    }

    /**
     * classify single concept
     * 
     * @param entry
     *        entry
     */
    @PortedFrom(file = "dlTBox.h", name = "classifyEntry")
    private void classifyEntry(Concept entry) {
        if (isBlockedInd(entry)) {
            classifyEntry(getBlockingInd(entry));
            // make sure that the possible synonym is already classified
        }
        if (!entry.isClassified()) {
            pTaxCreator.classifyEntry(entry);
        }
    }

    /**
     * @param datatypeFactory
     *        datatypeFactory
     * @param configuration
     *        configuration
     * @param topObjectRoleName
     *        topObjectRoleName
     * @param botObjectRoleName
     *        botObjectRoleName
     * @param topDataRoleName
     *        topDataRoleName
     * @param botDataRoleName
     *        botDataRoleName
     * @param interrupted
     *        interrupted
     */
    public TBox(DatatypeFactory datatypeFactory,
            JFactReasonerConfiguration configuration, IRI topObjectRoleName,
            IRI botObjectRoleName, IRI topDataRoleName, IRI botDataRoleName,
            AtomicBoolean interrupted) {
        this.datatypeFactory = datatypeFactory;
        this.interrupted = interrupted;
        config = configuration;
        SplitRules = new TSplitRules(configuration);
        axioms = new AxiomSet(this);
        dlHeap = new DLDag(configuration);
        kbStatus = kbLoading;
        pQuery = null;
        concepts = new NamedEntryCollection<Concept>("concept",
                new ConceptCreator(), config);
        individuals = new NamedEntryCollection<Individual>("individual",
                new IndividualCreator(), config);
        objectRoleMaster = new RoleMaster(false, topObjectRoleName,
                botObjectRoleName, config);
        dataRoleMaster = new RoleMaster(true, topDataRoleName, botDataRoleName,
                config);
        axioms = new AxiomSet(this);
        internalisedGeneralAxiom = bpTOP;
        duringClassification = false;
        isLikeGALEN = false;
        isLikeWINE = false;
        consistent = true;
        preprocTime = 0;
        consistTime = 0;
        config.getLog().printTemplate(Templates.READ_CONFIG,
                config.getuseCompletelyDefined(), "useRelevantOnly(obsolete)",
                config.getdumpQuery(), config.getalwaysPreferEquals());
        axioms.initAbsorptionFlags(config.getabsorptionFlags());
        initTopBottom();
        setForbidUndefinedNames(false);
        initTaxonomy();
    }

    /**
     * @param desc
     *        desc
     * @return concept for desc
     */
    @PortedFrom(file = "dlTBox.h", name = "getAuxConcept")
    public Concept getAuxConcept(DLTree desc) {
        boolean old = setForbidUndefinedNames(false);
        Concept C = getConcept(IRI.create("urn:aux" + ++auxConceptID));
        setForbidUndefinedNames(old);
        C.setSystem();
        C.setNonClassifiable(true);
        C.setPrimitive(true);
        C.addDesc(desc);
        // it is created after this is done centrally
        C.initToldSubsumers();
        return C;
    }

    @PortedFrom(file = "dlTBox.h", name = "top")
    private Concept top;
    @PortedFrom(file = "dlTBox.h", name = "bottom")
    private Concept bottom;

    @PortedFrom(file = "dlTBox.h", name = "initTopBottom")
    private void initTopBottom() {
        top = Concept.getTOP();
        bottom = Concept.getBOTTOM();
        pTemp = Concept.getTEMP();
        // query concept
        pQuery = Concept.getQuery();
    }

    /** prepare reasoning */
    @PortedFrom(file = "dlTBox.h", name = "prepareReasoning")
    public void prepareReasoning() {
        preprocess();
        initReasoner();
        // check if it is necessary to dump relevant part TBox
        dumpQuery();
        dlHeap.setSatOrder();
    }

    @Original
    private void dumpQuery() {
        if (config.getdumpQuery()) {
            // TODO
            markAllRelevant();
            DumpLisp lDump = new DumpLisp(config.getLog());
            dump(lDump);
            clearRelevanceInfo();
        }
    }

    /**
     * @param pConcept
     *        pConcept
     * @param qConcept
     *        qConcept
     */
    @PortedFrom(file = "dlTBox.h", name = "prepareFeatures")
    public void prepareFeatures(Concept pConcept, Concept qConcept) {
        auxFeatures = new LogicFeatures(GCIFeatures);
        if (pConcept != null) {
            updateAuxFeatures(pConcept.getPosFeatures());
        }
        if (qConcept != null) {
            updateAuxFeatures(qConcept.getNegFeatures());
        }
        if (auxFeatures.hasSingletons()) {
            updateAuxFeatures(nominalCloudFeatures);
        }
        curFeature = auxFeatures;
        getReasoner().setBlockingMethod(isIRinQuery(), isNRinQuery());
    }

    /** build simple cache */
    @PortedFrom(file = "dlTBox.h", name = "buildSimpleCache")
    public void buildSimpleCache() {
        // set cache for BOTTOM entry
        initConstCache(bpBOTTOM);
        // set all the caches for the temp concept
        initSingletonCache(pTemp, true);
        initSingletonCache(pTemp, false);
        // inapplicable if KB contains CGIs in any form
        if (GCIs.isGCI() || GCIs.isReflexive()) {
            return;
        }
        // it is now safe to make a TOP cache
        initConstCache(bpTOP);
        for (Concept pc : concepts.getList()) {
            if (pc.isPrimitive()) {
                initSingletonCache(pc, false);
            }
        }
        for (Individual pi : individuals.getList()) {
            if (pi.isPrimitive()) {
                initSingletonCache(pi, false);
            }
        }
    }

    /** @return true if consistent */
    @PortedFrom(file = "dlTBox.h", name = "performConsistencyCheck")
    public boolean performConsistencyCheck() {
        if (config.getverboseOutput()) {
            config.getLog().print("Consistency checking...\n");
        }
        Timer pt = new Timer();
        pt.start();
        buildSimpleCache();
        Concept test = nominalCloudFeatures.hasSingletons()
                && individuals.getList().size() > 0 ? individuals.getList()
                .get(0) : null;
        prepareFeatures(test, null);
        boolean ret = false;
        if (test != null) {
            if (dlHeap.getCache(bpTOP) == null) {
                initConstCache(bpTOP);
            }
            ret = nomReasoner.consistentNominalCloud();
        } else {
            ret = isSatisfiable(top);
            // setup cache for GCI
            if (GCIs.isGCI()) {
                dlHeap.setCache(-internalisedGeneralAxiom, new ModelCacheConst(
                        false));
            }
        }
        pt.stop();
        consistTime = pt.calcDelta();
        if (config.getverboseOutput()) {
            config.getLog().print(" done in ").print(consistTime)
                    .print(" seconds\n\n");
        }
        return ret;
    }

    /**
     * @param pConcept
     *        pConcept
     * @return true if satisfiable
     */
    @PortedFrom(file = "dlTBox.h", name = "isSatisfiable")
    public boolean isSatisfiable(Concept pConcept) {
        assert pConcept != null;
        ModelCacheInterface cache = dlHeap.getCache(pConcept.getpName());
        if (cache != null) {
            return cache.getState() != ModelCacheState.csInvalid;
        }
        config.getLog().printTemplate(Templates.IS_SATISFIABLE,
                pConcept.getName());
        prepareFeatures(pConcept, null);
        int resolveId = pConcept.resolveId();
        if (resolveId == bpINVALID) {
            config.getLog().print(
                    "query concept still invalid after prepareFeatures()");
            return false;
        }
        boolean result = getReasoner().runSat(resolveId, bpTOP);
        cache = getReasoner().buildCacheByCGraph(result);
        dlHeap.setCache(pConcept.getpName(), cache);
        clearFeatures();
        config.getLog().printTemplate(Templates.IS_SATISFIABLE1,
                pConcept.getName(), !result ? "un" : "");
        return result;
    }

    /**
     * @param pConcept
     *        pConcept
     * @param qConcept
     *        qConcept
     * @return true if subsumption holds
     */
    @PortedFrom(file = "dlTBox.h", name = "isSubHolds")
    public boolean isSubHolds(Concept pConcept, Concept qConcept) {
        assert pConcept != null && qConcept != null;
        config.getLog().printTemplate(Templates.ISSUBHOLDS1,
                pConcept.getName(), qConcept.getName());
        prepareFeatures(pConcept, qConcept);
        boolean result = !getReasoner().runSat(pConcept.resolveId(),
                -qConcept.resolveId());
        clearFeatures();
        config.getLog().printTemplate(Templates.ISSUBHOLDS2,
                pConcept.getName(), qConcept.getName(), !result ? " NOT" : "");
        return result;
    }

    /**
     * @param _a
     *        _a
     * @param _b
     *        _b
     * @return true if same individual
     */
    @PortedFrom(file = "dlTBox.h", name = "isSameIndividuals")
    public boolean isSameIndividuals(Individual _a, Individual _b) {
        Individual a = resolveSynonym(_a);
        Individual b = resolveSynonym(_b);
        if (a.equals(b)) {
            return true;
        }
        if (!this.isIndividual(a.getName()) || !this.isIndividual(b.getName())) {
            throw new ReasonerInternalException(
                    "Individuals are expected in the isSameIndividuals() query");
        }
        if (a.getNode() == null || b.getNode() == null) {
            if (a.isSynonym()) {
                return isSameIndividuals((Individual) a.getSynonym(), b);
            }
            if (b.isSynonym()) {
                return isSameIndividuals(a, (Individual) b.getSynonym());
            }
            // here this means that one of the individuals is a fresh name
            return false;
        }
        // TODO equals for TaxonomyVertex
        return a.getTaxVertex().equals(b.getTaxVertex());
    }

    /**
     * @param R
     *        R
     * @param S
     *        S
     * @return true if disjoint roles
     */
    @PortedFrom(file = "dlTBox.h", name = "isDisjointRoles")
    public boolean isDisjointRoles(Role R, Role S) {
        assert R != null && S != null;
        if (R.isDataRole() != S.isDataRole()) {
            return true;
        }
        curFeature = KBFeatures;
        getReasoner().setBlockingMethod(isIRinQuery(), isNRinQuery());
        boolean result = getReasoner().checkDisjointRoles(R, S);
        clearFeatures();
        return result;
    }

    /**
     * @param desc
     *        desc
     * @return query concept
     */
    @PortedFrom(file = "dlTBox.h", name = "createQueryConcept")
    public Concept createQueryConcept(DLTree desc) {
        assert desc != null;
        // make sure that an old query is gone
        clearQueryConcept();
        // create description
        makeNonPrimitive(pQuery, desc.copy());
        pQuery.setIndex(nC - 1);
        return pQuery;
    }

    /**
     * preprocess query concept: put description into DAG
     * 
     * @param query
     *        query
     */
    @PortedFrom(file = "dlTBox.h", name = "preprocessQueryConcept")
    public void preprocessQueryConcept(Concept query) {
        // build DAG entries for the default concept
        addConceptToHeap(query);
        // gather statistics about the concept
        setConceptRelevant(query);
        // check satisfiability of the concept
        initCache(query, false);
    }

    /** delete all query-related stuff */
    @PortedFrom(file = "dlTBox.h", name = "clearQueryConcept")
    public void clearQueryConcept() {
        dlHeap.removeQuery();
    }

    /** classify query concept */
    @PortedFrom(file = "dlTBox.h", name = "classifyQueryConcept")
    public void classifyQueryConcept() {
        pQuery.initToldSubsumers();
        assert pTax != null;
        pTaxCreator.setCompletelyDefined(false);
        pTaxCreator.classifyEntry(pQuery);
    }

    /** knowledge exploration: build a model and return a link to the root */
    /**
     * build a completion tree for a concept C (no caching as it breaks the idea
     * of KE).
     * 
     * @param pConcept
     *        pConcept
     * @return the root node
     */
    @PortedFrom(file = "dlTBox.h", name = "buildCompletionTree")
    public DlCompletionTree buildCompletionTree(Concept pConcept) {
        DlCompletionTree ret = null;
        // perform reasoning with a proper logical features
        prepareFeatures(pConcept, null);
        // turn off caching of CT nodes during reasoning
        config.setUseNodeCache(false);
        // do the SAT test, save the CT if satisfiable
        if (getReasoner().runSat(pConcept.resolveId(), bpTOP)) {
            ret = getReasoner().getRootNode();
        }
        // turn on caching of CT nodes during reasoning
        config.setUseNodeCache(true);
        clearFeatures();
        return ret;
    }

    /**
     * @param time
     *        time
     */
    @PortedFrom(file = "dlTBox.h", name = "writeReasoningResult")
    public void writeReasoningResult(long time) {
        LogAdapter o = config.getLog();
        if (nomReasoner != null) {
            o.print("Query processing reasoning statistic: Nominals");
            nomReasoner.writeTotalStatistic(o);
        }
        o.print("Query processing reasoning statistic: Standard");
        stdReasoner.writeTotalStatistic(o);
        assert kbStatus.ordinal() >= kbCChecked.ordinal();
        if (consistent) {
            o.print("Required");
        } else {
            o.print("KB is inconsistent. Query is NOT processed\nConsistency");
        }
        long sum = preprocTime + consistTime;
        o.print(" check done in ").print(time)
                .print(" seconds\nof which:\nPreproc. takes ")
                .print(preprocTime).print(" seconds\nConsist. takes ")
                .print(consistTime).print(" seconds");
        if (nomReasoner != null) {
            o.print("\nReasoning NOM:");
            sum += nomReasoner.printReasoningTime(o);
        }
        o.print("\nReasoning STD:");
        sum += stdReasoner.printReasoningTime(o);
        o.print("\nThe rest takes ");
        long f = time - sum;
        if (f < 0) {
            f = 0;
        }
        o.print((float) f / 1000);
        o.print(" seconds\n");
        print();
    }

    /**
     * @param o
     *        o
     * @param p
     *        p
     */
    @PortedFrom(file = "dlTBox.h", name = "PrintDagEntry")
    public void printDagEntry(LogAdapter o, int p) {
        assert isValid(p);
        if (p == bpTOP) {
            o.print(" *TOP*");
            return;
        } else if (p == bpBOTTOM) {
            o.print(" *BOTTOM*");
            return;
        }
        if (p < 0) {
            o.print(" (not");
            printDagEntry(o, -p);
            o.print(")");
            return;
        }
        DLVertex v = dlHeap.get(Math.abs(p));
        DagTag type = v.getType();
        switch (type) {
            case dtTop:
                o.print(" *TOP*");
                return;
            case dtPConcept:
            case dtNConcept:
            case dtPSingleton:
            case dtNSingleton:
            case dtDataType:
            case dtDataValue:
                o.print(" ");
                o.print(v.getConcept().getName());
                return;
            case dtDataExpr:
                o.print(" ");
                o.print(getDataEntryByBP(p));
                return;
            case dtIrr:
                o.print(" (", type.getName(), " ", v.getRole().getName(), ")");
                return;
            case dtCollection:
            case dtAnd:
            case dtSplitConcept:
                o.print(" (");
                o.print(type.getName());
                for (int q : v.begin()) {
                    printDagEntry(o, q);
                }
                o.print(")");
                return;
            case dtForall:
            case dtLE:
                o.print(" (");
                o.print(type.getName());
                if (type == dtLE) {
                    o.print(" ");
                    o.print(v.getNumberLE());
                }
                o.print(" ");
                o.print(v.getRole().getName());
                printDagEntry(o, v.getConceptIndex());
                o.print(")");
                return;
            case dtProj:
                o.print(" (", type.getName(), " ", v.getRole().getName(), ")");
                printDagEntry(o, v.getConceptIndex());
                o.print(" => ", v.getProjRole().getName(), ")");
                return;
            case dtNN:
            case dtChoose:
                throw new UnreachableSituationException();
            case dtBad:
                o.print("WRONG: printing a badtag dtBad!\n");
                break;
            default:
                throw new ReasonerInternalException(
                        "Error printing vertex of type " + type.getName() + "("
                                + type + ")");
        }
    }

    /**
     * @param o
     *        o
     * @param p
     *        p
     */
    @PortedFrom(file = "dlTBox.h", name = "PrintConcept")
    public void printConcept(LogAdapter o, Concept p) {
        if (isValid(p.getpName())) {
            o.print(p.getClassTagPlain().getCTTagName());
            if (p.isSingleton()) {
                o.print(p.isNominal() ? 'o' : '!');
            }
            o.print(".", p.getName(), " [", p.getTsDepth(), "] ",
                    p.isPrimitive() ? "[=" : "=");
            if (isValid(p.getpBody())) {
                printDagEntry(o, p.getpBody());
            }
            if (p.getDescription() != null) {
                o.print(p.isPrimitive() ? "\n-[=" : "\n-=", p.getDescription());
            }
            o.print("\n");
        }
    }

    @PortedFrom(file = "dlTBox.h", name = "dump")
    private void dump(DumpInterface dump) {
        dump.prologue();
        dumpAllRoles(dump);
        for (Concept pc : concepts.getList()) {
            if (pc.isRelevant(relevance)) {
                dumpConcept(dump, pc);
            }
        }
        for (Individual pi : individuals.getList()) {
            if (pi.isRelevant(relevance)) {
                dumpConcept(dump, pi);
            }
        }
        if (internalisedGeneralAxiom != bpTOP) {
            dump.startAx(DIOp.diImpliesC);
            dump.dumpTop();
            dump.contAx(DIOp.diImpliesC);
            dumpExpression(dump, internalisedGeneralAxiom);
            dump.finishAx(DIOp.diImpliesC);
        }
        dump.epilogue();
    }

    /**
     * @param dump
     *        dump
     * @param p
     *        p
     */
    @PortedFrom(file = "dlTBox.h", name = "dumpConcept")
    public void dumpConcept(DumpInterface dump, Concept p) {
        dump.startAx(DIOp.diDefineC);
        dump.dumpConcept(p);
        dump.finishAx(DIOp.diDefineC);
        if (p.getpBody() != bpTOP) {
            DIOp Ax = p.isPrimitive() ? DIOp.diImpliesC : DIOp.diEqualsC;
            dump.startAx(Ax);
            dump.dumpConcept(p);
            dump.contAx(Ax);
            dumpExpression(dump, p.getpBody());
            dump.finishAx(Ax);
        }
    }

    /**
     * @param dump
     *        dump
     * @param p
     *        p
     */
    @PortedFrom(file = "dlTBox.h", name = "dumpRole")
    public void dumpRole(DumpInterface dump, Role p) {
        if (p.getId() > 0 || !p.inverse().isRelevant(relevance)) {
            Role q = p.getId() > 0 ? p : p.inverse();
            dump.startAx(DIOp.diDefineR);
            dump.dumpRole(q);
            dump.finishAx(DIOp.diDefineR);
            for (ClassifiableEntry i : q.getToldSubsumers()) {
                dump.startAx(DIOp.diImpliesR);
                dump.dumpRole(q);
                dump.contAx(DIOp.diImpliesR);
                dump.dumpRole((Role) i);
                dump.finishAx(DIOp.diImpliesR);
            }
        }
        if (p.isTransitive()) {
            dump.startAx(DIOp.diTransitiveR);
            dump.dumpRole(p);
            dump.finishAx(DIOp.diTransitiveR);
        }
        if (p.isTopFunc()) {
            dump.startAx(DIOp.diFunctionalR);
            dump.dumpRole(p);
            dump.finishAx(DIOp.diFunctionalR);
        }
        if (p.getBPDomain() != bpTOP) {
            dump.startAx(DIOp.diDomainR);
            dump.dumpRole(p);
            dump.contAx(DIOp.diDomainR);
            dumpExpression(dump, p.getBPDomain());
            dump.finishAx(DIOp.diDomainR);
        }
        if (p.getBPRange() != bpTOP) {
            dump.startAx(DIOp.diRangeR);
            dump.dumpRole(p);
            dump.contAx(DIOp.diRangeR);
            dumpExpression(dump, p.getBPRange());
            dump.finishAx(DIOp.diRangeR);
        }
    }

    /**
     * @param dump
     *        dump
     * @param p
     *        p
     */
    @PortedFrom(file = "dlTBox.h", name = "dumpExpression")
    public void dumpExpression(DumpInterface dump, int p) {
        assert isValid(p);
        if (p == bpTOP) {
            dump.dumpTop();
            return;
        }
        if (p == bpBOTTOM) {
            dump.dumpBottom();
            return;
        }
        if (p < 0) {
            dump.startOp(DIOp.diNot);
            dumpExpression(dump, -p);
            dump.finishOp(DIOp.diNot);
            return;
        }
        DLVertex v = dlHeap.get(Math.abs(p));
        DagTag type = v.getType();
        switch (type) {
            case dtTop:
                dump.dumpTop();
                return;
            case dtPConcept:
            case dtNConcept:
            case dtPSingleton:
            case dtNSingleton:
                dump.dumpConcept((Concept) v.getConcept());
                return;
            case dtAnd:
                dump.startOp(DIOp.diAnd);
                int[] begin = v.begin();
                for (int q : begin) {
                    if (q != begin[0]) {
                        dump.contOp(DIOp.diAnd);
                    }
                    dumpExpression(dump, q);
                }
                dump.finishOp(DIOp.diAnd);
                return;
            case dtForall:
                dump.startOp(DIOp.diForall);
                dump.dumpRole(v.getRole());
                dump.contOp(DIOp.diForall);
                dumpExpression(dump, v.getConceptIndex());
                dump.finishOp(DIOp.diForall);
                return;
            case dtLE:
                dump.startOp(DIOp.diLE, v.getNumberLE());
                dump.dumpRole(v.getRole());
                dump.contOp(DIOp.diLE);
                dumpExpression(dump, v.getConceptIndex());
                dump.finishOp(DIOp.diLE);
                return;
            default:
                throw new ReasonerInternalException(
                        "Error dumping vertex of type " + type.getName() + "("
                                + type + ")");
        }
    }

    /**
     * @param dump
     *        dump
     */
    @PortedFrom(file = "dlTBox.h", name = "dumpAllRoles")
    public void dumpAllRoles(DumpInterface dump) {
        for (Role p : objectRoleMaster.getRoles()) {
            if (p.isRelevant(relevance)) {
                assert !p.isSynonym();
                dumpRole(dump, p);
            }
        }
        for (Role p : dataRoleMaster.getRoles()) {
            if (p.isRelevant(relevance)) {
                assert !p.isSynonym();
                dumpRole(dump, p);
            }
        }
    }

    /**
     * @param sub
     *        sub
     * @param sup
     *        sup
     */
    @PortedFrom(file = "dlTBox.h", name = "addSubsumeAxiom")
    public void addSubsumeAxiom(DLTree sub, DLTree sup) {
        if (DLTree.equalTrees(sub, sup)) {
            return;
        }
        if (sup.isCN()) {
            sup = applyAxiomCToCN(sub, sup);
            if (sup == null) {
                return;
            }
        }
        if (sub.isCN()) {
            sub = applyAxiomCNToC(sub, sup);
            if (sub == null) {
                return;
            }
        }
        if (!axiomToRangeDomain(sub, sup)) {
            processGCI(sub, sup);
        }
    }

    /**
     * @param D
     *        D
     * @param CN
     *        CN
     * @return merged dltree
     */
    @PortedFrom(file = "dlTBox.h", name = "applyAxiomCToCN")
    public DLTree applyAxiomCToCN(DLTree D, DLTree CN) {
        Concept C = resolveSynonym(getCI(CN));
        assert C != null;
        // lie: this will never be reached
        if (C.isBottom()) {
            return DLTreeFactory.createBottom();
        }
        if (C.isTop()) {} else if (!(C.isSingleton() && D.isName())
                && DLTree.equalTrees(C.getDescription(), D)) {
            makeNonPrimitive(C, D);
        } else {
            return CN;
        }
        return null;
    }

    /**
     * @param CN
     *        CN
     * @param D
     *        D
     * @return merged dltree
     */
    @PortedFrom(file = "dlTBox.h", name = "applyAxiomCNToC")
    public DLTree applyAxiomCNToC(DLTree CN, DLTree D) {
        Concept C = resolveSynonym(getCI(CN));
        assert C != null;
        if (C.isTop()) {
            return DLTreeFactory.createTop();
        }
        if (C.isBottom()) {} else if (C.isPrimitive()) {
            C.addDesc(D);
        } else {
            addSubsumeForDefined(C, D);
        }
        return null;
    }

    /**
     * @param C
     *        C
     * @param D
     *        D
     */
    @PortedFrom(file = "dlTBox.h", name = "addSubsumeForDefined")
    public void addSubsumeForDefined(Concept C, DLTree E) {
        if (DLTreeFactory.isSubTree(E, C.getDescription())) {
            return;
        }
        DLTree D = C.getDescription().copy();
        C.removeSelfFromDescription();
        if (DLTree.equalTrees(D, C.getDescription())) {
            processGCI(D, E);
            return;
        }
        // note that we don't know exact semantics of C for now;
        // we need to split its definition and work via GCIs
        makeDefinitionPrimitive(C, E, D);
    }

    /**
     * @param sub
     *        sub
     * @param sup
     *        sup
     * @return true if range can be set
     */
    @PortedFrom(file = "dlTBox.h", name = "axiomToRangeDomain")
    public boolean axiomToRangeDomain(DLTree sub, DLTree sup) {
        if (sub.isTOP() && sup.token() == FORALL) {
            Role.resolveRole(sup.getLeft()).setRange(sup.getRight().copy());
            return true;
        }
        if (sub.token() == NOT && sub.getChild().token() == FORALL
                && sub.getChild().getRight().isBOTTOM()) {
            Role.resolveRole(sub.getChild().getLeft()).setDomain(sup);
            return true;
        }
        return false;
    }

    @PortedFrom(file = "dlTBox.h", name = "addEqualityAxiom")
    private void addEqualityAxiom(DLTree left, DLTree right) {
        if (addNonprimitiveDefinition(left, right)) {
            return;
        }
        if (addNonprimitiveDefinition(right, left)) {
            return;
        }
        if (switchToNonprimitive(left, right)) {
            return;
        }
        if (switchToNonprimitive(right, left)) {
            return;
        }
        this.addSubsumeAxiom(left.copy(), right.copy());
        this.addSubsumeAxiom(right, left);
    }

    /**
     * @param left
     *        left
     * @param right
     *        right
     * @return true if definition is added
     */
    @PortedFrom(file = "dlTBox.h", name = "addNonprimitiveDefinition")
    public boolean addNonprimitiveDefinition(DLTree left, DLTree right) {
        Concept C = resolveSynonym(getCI(left));
        if (C == null || C.isTop() || C.isBottom()) {
            return false;
        }
        Concept D = getCI(right);
        if (D != null && resolveSynonym(D).equals(C)) {
            return true;
        }
        if (C.isSingleton() && D != null && !D.isSingleton()) {
            return false;
        }
        if ((D == null || C.getDescription() == null || D.isPrimitive())
                && !initNonPrimitive(C, right)) {
            return true;
        }
        return false;
    }

    /**
     * @param left
     *        left
     * @param right
     *        right
     * @return true if concept is made non primitive
     */
    @PortedFrom(file = "dlTBox.h", name = "switchToNonprimitive")
    public boolean switchToNonprimitive(DLTree left, DLTree right) {
        Concept C = resolveSynonym(getCI(left));
        if (C == null || C.isTop() || C.isBottom()) {
            return false;
        }
        Concept D = resolveSynonym(getCI(right));
        if (C.isSingleton() && D != null && !D.isSingleton()) {
            return false;
        }
        if (config.getalwaysPreferEquals() && C.isPrimitive()) {
            addSubsumeForDefined(C, makeNonPrimitive(C, right));
            return true;
        }
        return false;
    }

    /**
     * transform definition C=D' with C [= E into C [= (D' and E) with D [= C.<br>
     * D is usually D', but see addSubsumeForDefined()
     * 
     * @param C
     *        primitive
     * @param E
     *        superclass
     * @param D
     *        replacement
     */
    @PortedFrom(file = "dlTBox.h", name = "makeDefinitionPrimitive")
    void makeDefinitionPrimitive(Concept C, DLTree E, DLTree D) {
        // now we have C [= D'
        C.setPrimitive(true);
        // here C [= (D' and E)
        C.addDesc(E);
        // all we need is to add (old C's desc)D [= C
        addSubsumeAxiom(D, getTree(C));
    }

    /**
     * @param beg
     *        beg
     */
    @PortedFrom(file = "dlTBox.h", name = "processDisjointC")
    public void processDisjointC(Collection<DLTree> beg) {
        List<DLTree> prim = new ArrayList<DLTree>();
        List<DLTree> rest = new ArrayList<DLTree>();
        for (DLTree d : beg) {
            if (d.isName() && ((Concept) d.elem().getNE()).isPrimitive()) {
                prim.add(d);
            } else {
                rest.add(d);
            }
        }
        if (!prim.isEmpty() && !rest.isEmpty()) {
            DLTree nrest = DLTreeFactory.buildDisjAux(rest);
            for (DLTree q : prim) {
                this.addSubsumeAxiom(q.copy(), nrest.copy());
            }
        }
        if (!rest.isEmpty()) {
            processDisjoint(rest);
        }
        if (!prim.isEmpty()) {
            processDisjoint(prim);
        }
    }

    /**
     * @param l
     *        l
     */
    @PortedFrom(file = "dlTBox.h", name = "processEquivalentC")
    public void processEquivalentC(List<DLTree> l) {
        // TODO check if this is taking into account all combinations
        for (int i = 0; i < l.size() - 1; i++) {
            addEqualityAxiom(l.get(i), l.get(i + 1).copy());
        }
    }

    /**
     * @param l
     *        l
     */
    @PortedFrom(file = "dlTBox.h", name = "processDifferent")
    public void processDifferent(List<DLTree> l) {
        List<Individual> acc = new ArrayList<Individual>();
        for (int i = 0; i < l.size(); i++) {
            if (this.isIndividual(l.get(i))) {
                acc.add((Individual) l.get(i).elem().getNE());
                l.set(i, null);
            } else {
                throw new ReasonerInternalException(
                        "Only individuals allowed in processDifferent()");
            }
        }
        if (acc.size() > 1) {
            differentIndividuals.add(acc);
        }
    }

    /**
     * @param l
     *        l
     */
    @PortedFrom(file = "dlTBox.h", name = "processSame")
    public void processSame(List<DLTree> l) {
        int size = l.size();
        for (int i = 0; i < size; i++) {
            if (!this.isIndividual(l.get(i))) {
                throw new ReasonerInternalException(
                        "Only individuals allowed in processSame()");
            }
        }
        for (int i = 0; i < size - 1; i++) {
            // TODO check if this is checking all combinations
            addEqualityAxiom(l.get(i), l.get(i + 1).copy());
        }
    }

    /**
     * @param l
     *        l
     */
    @PortedFrom(file = "dlTBox.h", name = "processDisjointR")
    public void processDisjointR(List<DLTree> l) {
        if (l.isEmpty()) {
            throw new ReasonerInternalException("Empty disjoint role axiom");
        }
        int size = l.size();
        for (int i = 0; i < size; i++) {
            if (DLTreeFactory.isTopRole(l.get(i))) {
                throw new ReasonerInternalException(
                        "Universal role in the disjoint roles axiom");
            }
        }
        List<Role> roles = new ArrayList<Role>(size);
        for (int i = 0; i < size; i++) {
            roles.add(Role.resolveRole(l.get(i)));
        }
        RoleMaster RM = getRM(roles.get(0));
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                RM.addDisjointRoles(roles.get(i), roles.get(j));
            }
        }
    }

    /**
     * @param l
     *        l
     */
    @PortedFrom(file = "dlTBox.h", name = "processEquivalentR")
    public void processEquivalentR(List<DLTree> l) {
        if (!l.isEmpty()) {
            RoleMaster RM = getRM(Role.resolveRole(l.get(0)));
            for (int i = 0; i < l.size() - 1; i++) {
                RM.addRoleSynonym(Role.resolveRole(l.get(i)),
                        Role.resolveRole(l.get(i + 1)));
            }
            l.clear();
        }
    }

    /** preprocess tbox */
    @PortedFrom(file = "dlTBox.h", name = "preprocess")
    public void preprocess() {
        if (config.getverboseOutput()) {
            config.getLog().print("\nPreprocessing...\n");
        }
        Timer pt = new Timer();
        pt.start();
        objectRoleMaster.initAncDesc();
        dataRoleMaster.initAncDesc();
        if (config.getverboseOutput()) {
            config.getLog().print(objectRoleMaster.getTaxonomy());
            config.getLog().print(dataRoleMaster.getTaxonomy());
        }
        if (countSynonyms() > 0) {
            replaceAllSynonyms();
        }
        preprocessRelated();
        initToldSubsumers();
        transformToldCycles();
        transformSingletonHierarchy();
        absorbAxioms();
        setToldTop();
        buildDAG();
        buildSplitRules();
        fillsClassificationTag();
        calculateTSDepth();
        // set indexes for model caching
        setAllIndexes();
        determineSorts();
        gatherRelevanceInfo();
        // here it is safe to print KB features (all are known; the last one was
        // in Relevance)
        printFeatures();
        dlHeap.setOrderDefaults(isLikeGALEN ? "Fdn" : isLikeWINE ? "Sdp"
                : "Sap", isLikeGALEN ? "Ban" : isLikeWINE ? "Fdn" : "Dap");
        dlHeap.gatherStatistic();
        calculateStatistic();
        removeExtraDescriptions();
        pt.stop();
        preprocTime = pt.calcDelta();
        if (config.getverboseOutput()) {
            config.getLog().print(" done in ").print(pt.calcDelta())
                    .print(" seconds\n\n");
        }
    }

    @PortedFrom(file = "dlTBox.h", name = "setAllIndexes")
    private void setAllIndexes() {
        // place for the query concept
        ++nC;
        // start with 1 to make index 0 an indicator of "not processed"
        nR = 1;
        for (Role r : objectRoleMaster.getRoles()) {
            if (!r.isSynonym()) {
                r.setIndex(nR++);
            }
        }
        for (Role r : dataRoleMaster.getRoles()) {
            if (!r.isSynonym()) {
                r.setIndex(nR++);
            }
        }
    }

    @PortedFrom(file = "dlTBox.h", name = "replaceAllSynonyms")
    private void replaceAllSynonyms() {
        for (Role r : objectRoleMaster.getRoles()) {
            if (!r.isSynonym()) {
                DLTreeFactory.replaceSynonymsFromTree(r.getTDomain());
            }
        }
        for (Role dr : dataRoleMaster.getRoles()) {
            if (!dr.isSynonym()) {
                DLTreeFactory.replaceSynonymsFromTree(dr.getTDomain());
            }
        }
        for (Concept pc : concepts.getList()) {
            if (DLTreeFactory.replaceSynonymsFromTree(pc.getDescription())) {
                pc.initToldSubsumers();
            }
        }
        for (Individual pi : individuals.getList()) {
            if (DLTreeFactory.replaceSynonymsFromTree(pi.getDescription())) {
                pi.initToldSubsumers();
            }
        }
    }

    /** preprocess related individuals */
    @PortedFrom(file = "dlTBox.h", name = "preprocessRelated")
    public void preprocessRelated() {
        for (Related q : relatedIndividuals) {
            q.simplify();
        }
    }

    /** transform cycles */
    @PortedFrom(file = "dlTBox.h", name = "transformToldCycles")
    public void transformToldCycles() {
        int nSynonyms = countSynonyms();
        clearRelevanceInfo();
        for (Concept pc : concepts.getList()) {
            if (!pc.isSynonym()) {
                checkToldCycle(pc);
            }
        }
        for (Individual pi : individuals.getList()) {
            if (!pi.isSynonym()) {
                checkToldCycle(pi);
            }
        }
        clearRelevanceInfo();
        nSynonyms = countSynonyms() - nSynonyms;
        if (nSynonyms > 0) {
            config.getLog().printTemplate(Templates.TRANSFORM_TOLD_CYCLES,
                    nSynonyms);
            replaceAllSynonyms();
        }
    }

    /**
     * @param _p
     *        _p
     * @return concpet with told cycle rewritten
     */
    @PortedFrom(file = "dlTBox.h", name = "checkToldCycle")
    public Concept checkToldCycle(Concept _p) {
        assert _p != null;
        Concept p = resolveSynonym(_p);
        if (p.isTop()) {
            return null;
        }
        if (conceptInProcess.contains(p)) {
            return p;
        }
        if (p.isRelevant(relevance)) {
            return null;
        }
        Concept ret = null;
        conceptInProcess.add(p);
        boolean redo = false;
        while (!redo) {
            redo = true;
            for (ClassifiableEntry r : p.getToldSubsumers()) {
                if ((ret = checkToldCycle((Concept) r)) != null) {
                    if (ret.equals(p)) {
                        toldSynonyms.add(p);
                        for (Concept q : toldSynonyms) {
                            if (q.isSingleton()) {
                                p = q;
                            }
                        }
                        Set<DLTree> leaves = new HashSet<DLTree>();
                        for (Concept q : toldSynonyms) {
                            if (q != p) {
                                DLTree d = makeNonPrimitive(q, getTree(p));
                                if (d.isBOTTOM()) {
                                    leaves.clear();
                                    leaves.add(d);
                                    break;
                                } else {
                                    leaves.add(d);
                                }
                            }
                        }
                        toldSynonyms.clear();
                        p.setPrimitive(true);
                        p.addLeaves(leaves);
                        p.removeSelfFromDescription();
                        if (!ret.equals(p)) {
                            conceptInProcess.remove(ret);
                            conceptInProcess.add(p);
                            ret.setRelevant(relevance);
                            p.dropRelevant();
                        }
                        ret = null;
                        redo = false;
                        break;
                    } else {
                        toldSynonyms.add(p);
                        redo = true;
                        break;
                    }
                }
            }
        }
        conceptInProcess.remove(p);
        p.setRelevant(relevance);
        return ret;
    }

    /** transform singleton hierarchy */
    @PortedFrom(file = "dlTBox.h", name = "transformSingletonHierarchy")
    public void transformSingletonHierarchy() {
        int nSynonyms = countSynonyms();
        boolean changed;
        do {
            changed = false;
            for (Individual pi : individuals.getList()) {
                if (!pi.isSynonym() && pi.isHasSP()) {
                    Individual i = transformSingletonWithSP(pi);
                    i.removeSelfFromDescription();
                    changed = true;
                }
            }
        } while (changed);
        nSynonyms = countSynonyms() - nSynonyms;
        if (nSynonyms > 0) {
            replaceAllSynonyms();
        }
    }

    /**
     * @param p
     *        p
     * @return individual for concept
     */
    @PortedFrom(file = "dlTBox.h", name = "getSPForConcept")
    public Individual getSPForConcept(Concept p) {
        for (ClassifiableEntry r : p.getToldSubsumers()) {
            Concept i = (Concept) r;
            if (i.isSingleton()) {
                return (Individual) i;
            }
            if (i.isHasSP()) {
                return transformSingletonWithSP(i);
            }
        }
        throw new UnreachableSituationException();
    }

    @PortedFrom(file = "dlTBox.h", name = "transformSingletonWithSP")
    private Individual transformSingletonWithSP(Concept p) {
        Individual i = getSPForConcept(p);
        if (p.isSingleton()) {
            i.addRelated((Individual) p);
        }
        this.addSubsumeAxiom(i, makeNonPrimitive(p, getTree(i)));
        return i;
    }

    /** determine sorts */
    @PortedFrom(file = "dlTBox.h", name = "determineSorts")
    public void determineSorts() {
        if (config.isRKG_USE_SORTED_REASONING()) {
            // Related individuals does not appears in DLHeap,
            // so their sorts shall be determined explicitely
            for (Related p : relatedIndividuals) {
                dlHeap.updateSorts(p.getA().getpName(), p.getRole(), p.getB()
                        .getpName());
            }
            // simple rules needs the same treatement
            for (SimpleRule q : simpleRules) {
                MergableLabel lab = dlHeap.get(q.bpHead).getSort();
                for (Concept r : q.simpleRuleBody) {
                    dlHeap.merge(lab, r.getpName());
                }
            }
            // create sorts for concept and/or roles
            dlHeap.determineSorts(objectRoleMaster, dataRoleMaster);
        }
    }

    /** calculate statistics */
    @PortedFrom(file = "dlTBox.h", name = "CalculateStatistic")
    public void calculateStatistic() {
        int npFull = 0, nsFull = 0;
        int nPC = 0, nNC = 0, nSing = 0;
        int nNoTold = 0;
        for (Concept pc : concepts.getList()) {
            Concept n = pc;
            if (!isValid(n.getpName())) {
                continue;
            }
            if (n.isPrimitive()) {
                ++nPC;
            } else {
                ++nNC;
            }
            if (n.isSynonym()) {
                ++nsFull;
            }
            if (n.isCompletelyDefined()) {
                if (n.isPrimitive()) {
                    ++npFull;
                }
            } else if (!n.hasToldSubsumers()) {
                ++nNoTold;
            }
        }
        for (Individual pi : individuals.getList()) {
            Concept n = pi;
            if (!isValid(n.getpName())) {
                continue;
            }
            ++nSing;
            if (n.isPrimitive()) {
                ++nPC;
            } else {
                ++nNC;
            }
            if (n.isSynonym()) {
                ++nsFull;
            }
            if (n.isCompletelyDefined()) {
                if (n.isPrimitive()) {
                    ++npFull;
                }
            } else if (!n.hasToldSubsumers()) {
                ++nNoTold;
            }
        }
        config.getLog().print("There are ").print(nPC)
                .print(" primitive concepts used\n of which ").print(npFull)
                .print(" completely defined\n      and ").print(nNoTold)
                .print(" has no told subsumers\nThere are ").print(nNC)
                .print(" non-primitive concepts used\n of which ")
                .print(nsFull).print(" synonyms\nThere are ").print(nSing)
                .print(" individuals or nominals used\n");
    }

    /** remove extra descritpions */
    @PortedFrom(file = "dlTBox.h", name = "RemoveExtraDescriptions")
    public void removeExtraDescriptions() {
        for (Concept pc : concepts.getList()) {
            pc.removeDescription();
        }
        for (Individual pi : individuals.getList()) {
            pi.removeDescription();
        }
    }

    /** set ToDo priorities using local OPTIONS */
    @Original
    public void setToDoPriorities() {
        stdReasoner.initToDoPriorities();
        if (nomReasoner != null) {
            nomReasoner.initToDoPriorities();
        }
    }

    /**
     * @param C
     *        C
     * @return true iff individual C is known to be p-blocked by another one
     */
    @PortedFrom(file = "dlTBox.h", name = "isBlockedInd")
    public boolean isBlockedInd(Concept C) {
        return sameIndividuals.containsKey(C);
    }

    /**
     * @param C
     *        C
     * @return individual that blocks C; works only for blocked individuals C
     */
    @PortedFrom(file = "dlTBox.h", name = "getBlockingInd")
    public Individual getBlockingInd(Concept C) {
        return sameIndividuals.get(C).first;
    }

    /**
     * @param C
     *        C
     * @return true iff an individual blocks C deterministically
     */
    @PortedFrom(file = "dlTBox.h", name = "isBlockingDet")
    public boolean isBlockingDet(Concept C) {
        return sameIndividuals.get(C).second;
    }

    /**
     * init const cache for either bpTOP or bpBOTTOM
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "dlTBox.h", name = "initConstCache")
    private void initConstCache(int p) {
        dlHeap.setCache(p, ModelCacheConst.createConstCache(p));
    }

    /**
     * init [singleton] cache for given concept and polarity
     * 
     * @param p
     *        p
     * @param pos
     *        pos
     */
    @PortedFrom(file = "dlTBox.h", name = "initSingletonCache")
    private void initSingletonCache(Concept p, boolean pos) {
        dlHeap.setCache(createBiPointer(p.getpName(), pos),
                new ModelCacheSingleton(createBiPointer(p.getIndex(), pos)));
    }

    /**
     * @param pConcept
     *        pConcept
     * @param sub
     *        sub
     * @return initialized cache
     */
    @PortedFrom(file = "dlTBox.h", name = "initCache")
    public ModelCacheInterface initCache(Concept pConcept, boolean sub) {
        int bp = sub ? -pConcept.getpName() : pConcept.getpName();
        ModelCacheInterface cache = dlHeap.getCache(bp);
        if (cache == null) {
            if (sub) {
                prepareFeatures(null, pConcept);
            } else {
                prepareFeatures(pConcept, null);
            }
            cache = getReasoner().createCache(bp, FastSetFactory.create());
            clearFeatures();
        }
        return cache;
    }

    /**
     * test if 2 concept non-subsumption can be determined by cache merging
     * 
     * @param p
     *        p
     * @param q
     *        q
     * @return cache state
     */
    @PortedFrom(file = "dlTBox.h", name = "testCachedNonSubsumption")
    public ModelCacheState testCachedNonSubsumption(Concept p, Concept q) {
        ModelCacheInterface pCache = initCache(p, /* sub= */false);
        ModelCacheInterface nCache = initCache(q, /* sub= */true);
        return pCache.canMerge(nCache);
    }

    /** init reasoner */
    @PortedFrom(file = "dlTBox.h", name = "initReasoner")
    public void initReasoner() {
        assert !reasonersInited();
        stdReasoner = new DlSatTester(this, config, datatypeFactory);
        if (nominalCloudFeatures.hasSingletons()) {
            nomReasoner = new NominalReasoner(this, config, datatypeFactory);
        }
        setToDoPriorities();
    }

    /** init taxonomy and classifier */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "initTaxonomy")
    public void initTaxonomy() {
        pTax = new Taxonomy(top, bottom, config);
        pTaxCreator = new DLConceptTaxonomy(pTax, this);
    }

    /**
     * set NameSigMap
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "dlTBox.h", name = "setNameSigMap")
    public void setNameSigMap(Map<NamedEntity, TSignature> p) {
        pName2Sig = p;
    }

    /**
     * @param c
     *        c
     * @return signature
     */
    @Original
    public TSignature getSignature(ClassifiableEntry c) {
        if (pName2Sig == null) {
            return null;
        }
        if (c.getEntity() == null) {
            return null;
        }
        return pName2Sig.get(c.getEntity());
    }

    @PortedFrom(file = "dlTBox.h", name = "nRelevantCCalls")
    private long nRelevantCCalls;
    @PortedFrom(file = "dlTBox.h", name = "nRelevantBCalls")
    private long nRelevantBCalls;

    /**
     * set relevance for a DLVertex
     * 
     * @param _p
     *        _p
     */
    @PortedFrom(file = "dlTBox.h", name = "setRelevant")
    private void setRelevant(int _p) {
        FastSet done = FastSetFactory.create();
        LinkedList<Integer> queue = new LinkedList<Integer>();
        queue.add(_p);
        while (!queue.isEmpty()) {
            int p = queue.remove(0);
            if (done.contains(p)) {
                // skip cycles
                continue;
            }
            done.add(p);
            assert isValid(p);
            if (p == bpTOP || p == bpBOTTOM) {
                continue;
            }
            DLVertex v = realSetRelevant(p);
            DagTag type = v.getType();
            switch (type) {
                case dtDataType:
                case dtDataValue:
                case dtDataExpr:
                case dtNN:
                    break;
                case dtPConcept:
                case dtPSingleton:
                case dtNConcept:
                case dtNSingleton:
                    Concept concept = (Concept) v.getConcept();
                    if (!concept.isRelevant(relevance)) {
                        ++nRelevantCCalls;
                        concept.setRelevant(relevance);
                        this.collectLogicFeature(concept);
                        queue.add(concept.getpBody());
                    }
                    break;
                case dtForall:
                case dtLE:
                    Role _role = v.getRole();
                    List<Role> rolesToExplore = new LinkedList<Role>();
                    rolesToExplore.add(_role);
                    while (!rolesToExplore.isEmpty()) {
                        Role roleToExplore = rolesToExplore.remove(0);
                        if ((roleToExplore.getId() != 0 || roleToExplore
                                .isTop())
                                && !roleToExplore.isRelevant(relevance)) {
                            roleToExplore.setRelevant(relevance);
                            this.collectLogicFeature(roleToExplore);
                            queue.add(roleToExplore.getBPDomain());
                            queue.add(roleToExplore.getBPRange());
                            rolesToExplore.addAll(roleToExplore.getAncestor());
                        }
                    }
                    queue.add(v.getConceptIndex());
                    break;
                case dtProj:
                case dtChoose:
                    queue.add(v.getConceptIndex());
                    break;
                case dtIrr:
                    Role __role = v.getRole();
                    List<Role> _rolesToExplore = new LinkedList<Role>();
                    _rolesToExplore.add(__role);
                    while (_rolesToExplore.size() > 0) {
                        Role roleToExplore = _rolesToExplore.remove(0);
                        if (roleToExplore.getId() != 0
                                && !roleToExplore.isRelevant(relevance)) {
                            roleToExplore.setRelevant(relevance);
                            this.collectLogicFeature(roleToExplore);
                            queue.add(roleToExplore.getBPDomain());
                            queue.add(roleToExplore.getBPRange());
                            _rolesToExplore.addAll(roleToExplore.getAncestor());
                        }
                    }
                    break;
                case dtAnd:
                case dtCollection:
                case dtSplitConcept:
                    for (int q : v.begin()) {
                        queue.add(q);
                    }
                    break;
                default:
                    throw new ReasonerInternalException(
                            "Error setting relevant vertex of type " + type);
            }
        }
    }

    @Original
    private DLVertex realSetRelevant(int p) {
        DLVertex v = dlHeap.get(p);
        boolean pos = p > 0;
        ++nRelevantBCalls;
        this.collectLogicFeature(v, pos);
        return v;
    }

    @PortedFrom(file = "dlTBox.h", name = "gatherRelevanceInfo")
    private void gatherRelevanceInfo() {
        nRelevantCCalls = 0;
        nRelevantBCalls = 0;
        int bSize = 0;
        curFeature = GCIFeatures;
        markGCIsRelevant();
        clearRelevanceInfo();
        KBFeatures.or(GCIFeatures);
        nominalCloudFeatures = new LogicFeatures(GCIFeatures);
        for (Individual pi : individuals.getList()) {
            setConceptRelevant(pi);
            nominalCloudFeatures.or(pi.getPosFeatures());
        }
        if (nominalCloudFeatures.hasSomeAll() && !relatedIndividuals.isEmpty()) {
            nominalCloudFeatures.setInverseRoles();
        }
        for (Concept pc : concepts.getList()) {
            setConceptRelevant(pc);
        }
        bSize = dlHeap.size() - 2;
        curFeature = null;
        double bRatio = 0; //
        double sqBSize = 1;
        if (bSize > 20) {
            bRatio = (float) nRelevantBCalls / bSize;
            sqBSize = Math.sqrt(bSize);
        }
        // set up GALEN-like flag; based on r/n^{3/2}, add r/n^2<1
        isLikeGALEN = bRatio > sqBSize * 20 && bRatio < bSize;
        // switch off sorted reasoning iff top role appears
        if (KBFeatures.hasTopRole()) {
            config.setUseSortedReasoning(false);
        }
    }

    /** print features */
    @PortedFrom(file = "dlTBox.h", name = "printFeatures")
    public void printFeatures() {
        KBFeatures.writeState(config.getLog());
        config.getLog().print("KB contains ", GCIs.isGCI() ? "" : "NO ",
                "GCIs\nKB contains ", GCIs.isReflexive() ? "" : "NO ",
                "reflexive roles\nKB contains ", GCIs.isRnD() ? "" : "NO ",
                "range and domain restrictions\n");
    }

    /** @return list of list of different individuals */
    @Original
    public List<List<Individual>> getDifferent() {
        return differentIndividuals;
    }

    /** @return list of relted individuals */
    @Original
    public List<Related> getRelatedI() {
        return relatedIndividuals;
    }

    /** @return the dl heap */
    @Original
    public DLDag getDLHeap() {
        return dlHeap;
    }

    /** @return the GCIs */
    @Original
    public KBFlags getGCIs() {
        return GCIs;
    }

    /**
     * replace (AR:C) with X such that C [= AR^-:X for fresh X.
     * 
     * @param RC
     *        RC
     * @return X
     */
    @PortedFrom(file = "dlTBox.h", name = "replaceForall")
    public Concept replaceForall(DLTree RC) {
        // check whether we already did this before for given R,C
        if (forall_R_C_Cache.containsKey(RC)) {
            return forall_R_C_Cache.get(RC);
        }
        Concept X = getAuxConcept(null);
        DLTree C = DLTreeFactory.createSNFNot(RC.getRight().copy());
        // create ax axiom C [= AR^-.X
        this.addSubsumeAxiom(C, DLTreeFactory.createSNFForall(
                DLTreeFactory.createInverse(RC.getLeft().copy()), getTree(X)));
        // save cache for R,C
        forall_R_C_Cache.put(RC, X);
        return X;
    }

    /** @return interrupted switch */
    @PortedFrom(file = "dlTBox.h", name = "isCancelled")
    public AtomicBoolean isCancelled() {
        return interrupted;
    }

    /** @return list of fairness concepts */
    @Original
    public List<Concept> getFairness() {
        return fairness;
    }

    /**
     * @param p
     *        p
     * @param pair
     *        pair
     */
    public void addSameIndividuals(Individual p, Pair pair) {
        sameIndividuals.put(p, pair);
    }

    static class IterableElem<Elem> implements Serializable {

        private static final long serialVersionUID = 11000L;
        private final List<Elem> Elems;
        private final int pBeg;
        private final int pEnd;
        private int pCur;

        // / init c'tor
        IterableElem(List<Elem> Init) {
            Elems = Init;
            pBeg = 0;
            pEnd = Elems.size();
            pCur = 0;
            // System.out.println("TBox.IterableElem.IterableElem() " +
            // Elems.size());
            if (Elems.isEmpty()) {
                throw new IllegalArgumentException("no empties allowed");
            }
        }

        public Elem getCur() {
            return Elems.get(pCur);
        }

        public boolean next() {
            if (++pCur >= pEnd) {
                pCur = pBeg;
                return true;
            }
            return false;
        }
    }

    static class IterableVec<Elem> implements Serializable {

        private static final long serialVersionUID = 11000L;

        public void clear() {
            Base.clear();
        }

        // / move I'th iterable forward; deal with end-case
        public boolean next(int i) {
            if (Base.get(i).next()) {
                return i == 0 ? true : next(i - 1);
            }
            return false;
        }

        private final List<IterableElem<Elem>> Base = new ArrayList<TBox.IterableElem<Elem>>();

        // / empty c'tor
        IterableVec() {}

        // / add a new iteralbe to a vec
        void add(IterableElem<Elem> It) {
            Base.add(It);
        }

        // / get next position
        boolean next() {
            return next(Base.size() - 1);
        }

        int size() {
            return Base.size();
        }

        Elem get(int i) {
            return Base.get(i).getCur();
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "IV")
    private final IterableVec<Individual> IV = new IterableVec<Individual>();
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "concepts")
    private final List<Integer> conceptsForQueryAnswering = new ArrayList<Integer>();

    /**
     * @param Cs
     *        Cs
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "answerQuery")
    public void answerQuery(List<DLTree> Cs) {
        dlHeap.removeQuery();
        // System.out.print("Transforming concepts...");
        // create BPs for all the concepts
        getConceptsForQueryAnswering().clear();
        for (DLTree q : Cs) {
            getConceptsForQueryAnswering().add(tree2dag(q));
        }
    }

    /**
     * @param MPlus
     *        MPlus
     * @param MMinus
     *        MMinus
     */
    public void reclassify(Set<NamedEntity> MPlus, Set<NamedEntity> MMinus) {
        pTaxCreator.reclassify(MPlus, MMinus);
        status = kbRealised;    // FIXME!! check whether it is classified/realised
    }

    /** @return list of concept index */
    public List<Integer> getConceptsForQueryAnswering() {
        return conceptsForQueryAnswering;
    }

    /** @return true if in classification */
    public boolean isDuringClassification() {
        return duringClassification;
    }

    /** @return split rules */
    public TSplitRules getSplitRules() {
        return SplitRules;
    }

    /** @return individuals */
    public IterableVec<Individual> getIV() {
        return IV;
    }

    /** @return skip before block */
    public int getnSkipBeforeBlock() {
        return nSkipBeforeBlock;
    }
}
