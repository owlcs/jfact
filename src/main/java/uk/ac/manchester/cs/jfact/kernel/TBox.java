package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static java.util.stream.Collectors.joining;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static uk.ac.manchester.cs.jfact.helpers.DLTreeFactory.*;
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;
import static uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry.resolveSynonym;
import static uk.ac.manchester.cs.jfact.kernel.DagTag.*;
import static uk.ac.manchester.cs.jfact.kernel.KBStatus.*;
import static uk.ac.manchester.cs.jfact.kernel.Role.resolveRole;
import static uk.ac.manchester.cs.jfact.kernel.RoleMaster.addRoleSynonym;
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapitools.decomposition.Signature;

import conformance.Original;
import conformance.PortedFrom;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.linked.TIntLinkedList;
import uk.ac.manchester.cs.chainsaw.FastSet;
import uk.ac.manchester.cs.chainsaw.FastSetFactory;
import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeEntry;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeExpression;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.datatypes.LiteralEntry;
import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Pair;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.helpers.Timer;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleName;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheConst;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheSingleton;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheState;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/** tbox */
@PortedFrom(file = "dlTBox.h", name = "TBox")
public class TBox implements Serializable {

    private static final String DONE_IN = " done in ";
    private static final String SECONDS = " seconds\n\n";
    @PortedFrom(file = "dlTBox.h", name = "relevance") private long relevance = 1;
    @PortedFrom(file = "dlTBox.h", name = "DLHeap") private final DLDag dlHeap;
    /** reasoner for TBox-related queries w/o nominals */
    @PortedFrom(file = "dlTBox.h", name = "stdReasoner") private DlSatTester stdReasoner = null;
    /** use this macro to do the same action with all available reasoners */
    /** reasoner for TBox-related queries with nominals */
    @PortedFrom(file = "dlTBox.h", name = "nomReasoner") private NominalReasoner nomReasoner;
    /** taxonomy structure of a TBox */
    @PortedFrom(file = "dlTBox.h", name = "pTax") private Taxonomy pTax;
    /** classifier */
    @PortedFrom(file = "dlTBox.h", name = "pTaxCreator") private DLConceptTaxonomy pTaxCreator;
    /** name-signature map */
    @PortedFrom(file = "dlTBox.h", name = "pName2Sig") private Map<OWLEntity, Signature> pName2Sig;
    /** set of reasoning options */
    @Original private final JFactReasonerConfiguration config;
    /** status of the KB */
    @PortedFrom(file = "dlTBox.h", name = "Status") private KBStatus kbStatus;
    /** global KB features */
    @PortedFrom(file = "dlTBox.h", name = "KBFeatures") private final LogicFeatures kbFeatures = new LogicFeatures();
    /** GCI features */
    @PortedFrom(file = "dlTBox.h", name = "GCIFeatures") private final LogicFeatures gciFeatures = new LogicFeatures();
    /** nominal cloud features */
    @PortedFrom(file = "dlTBox.h", name = "NCFeatures") private LogicFeatures nominalCloudFeatures = new LogicFeatures();
    /** aux features */
    @PortedFrom(file = "dlTBox.h", name = "auxFeatures") private LogicFeatures auxFeatures = new LogicFeatures();
    /** pointer to current feature (in case of local ones) */
    @PortedFrom(file = "dlTBox.h", name = "curFeature") private LogicFeatures curFeature = null;
    /**
     * concept representing temporary one that can not be used anywhere in the
     * ontology
     */
    @PortedFrom(file = "dlTBox.h", name = "pTemp") private Concept pTemp;
    /** temporary concept that represents query */
    @PortedFrom(file = "dlTBox.h", name = "pQuery") private Concept pQuery;
    /** all named concepts */
    @PortedFrom(file = "dlTBox.h", name = "concepts") private NamedEntryCollection<Concept> concepts;
    /** all named individuals/nominals */
    @PortedFrom(file = "dlTBox.h", name = "individuals") private NamedEntryCollection<Individual> individuals;
    /** "normal" (object) roles */
    @PortedFrom(file = "dlTBox.h", name = "ORM") private final RoleMaster objectRoleMaster;
    /** data roles */
    @PortedFrom(file = "dlTBox.h", name = "DRM") private final RoleMaster dataRoleMaster;
    /** set of GCIs */
    @PortedFrom(file = "dlTBox.h", name = "Axioms") private AxiomSet axioms;
    /** given individual-individual relations */
    @PortedFrom(file = "dlTBox.h", name = "RelatedI") private final List<Related> relatedIndividuals = new ArrayList<>();
    /** known disjoint sets of individuals */
    @PortedFrom(file = "dlTBox.h", name = "DifferentIndividuals") private final List<List<Individual>> differentIndividuals = new ArrayList<>();
    /** all simple rules in KB */
    @PortedFrom(file = "dlTBox.h", name = "SimpleRules") private final List<SimpleRule> simpleRules = new ArrayList<>();
    /** internalisation of a general axioms */
    @PortedFrom(file = "dlTBox.h", name = "T_G") private int internalisedGeneralAxiom;
    /** KB flags about GCIs */
    @PortedFrom(file = "dlTBox.h", name = "GCIs") private final KBFlags gcis = new KBFlags();
    /** cache for the \forall R.C replacements during absorption */
    @PortedFrom(file = "dlTBox.h", name = "RCCache") private final Map<DLTree, Concept> forallRCCache = new HashMap<>();
    /** current aux concept's ID */
    @PortedFrom(file = "dlTBox.h", name = "auxConceptID") private int auxConceptID = 0;
    /**
     * how many times nominals were found during translation to DAG; local to
     * BuildDAG
     */
    @PortedFrom(file = "dlTBox.h", name = "nNominalReferences") private int nNominalReferences;
    /** searchable stack for the told subsumers */
    @PortedFrom(file = "dlTBox.h", name = "CInProcess") private final Set<Concept> conceptInProcess = new HashSet<>();
    /** fairness constraints */
    @PortedFrom(file = "dlTBox.h", name = "Fairness") private final List<Concept> fairness = new ArrayList<>();
    // Reasoner's members: there are many reasoner classes, some members are
    // shared
    /** let reasoner know that we are in the classificaton (for splits) */
    @PortedFrom(file = "dlTBox.h", name = "duringClassification") private boolean duringClassification;
    // Internally defined flags
    /** flag whether TBox is GALEN-like */
    @PortedFrom(file = "dlTBox.h", name = "isLikeGALEN") private boolean isLikeGALEN;
    /** flag whether TBox is WINE-like */
    @PortedFrom(file = "dlTBox.h", name = "isLikeWINE") private boolean isLikeWINE;
    /** whether KB is consistent */
    @PortedFrom(file = "dlTBox.h", name = "consistent") private boolean consistent;
    /** time spend for preprocessing */
    @PortedFrom(file = "dlTBox.h", name = "preprocTime") private long preprocTime;
    /** time spend for consistency checking */
    @PortedFrom(file = "dlTBox.h", name = "consistTime") private long consistTime;
    /** number of concepts and individuals; used to set index for modelCache */
    @PortedFrom(file = "dlTBox.h", name = "nC") protected int nC = 0;
    /** number of all distinct roles; used to set index for modelCache */
    @PortedFrom(file = "dlTBox.h", name = "nR") protected AtomicInteger nR = new AtomicInteger(0);
    /** maps from concept index to concept itself */
    @PortedFrom(file = "dlTBox.h", name = "ConceptMap") private final List<Concept> conceptMap = new ArrayList<>();
    /** map to show the possible equivalence between individuals */
    @PortedFrom(file = "dlTBox.h", name = "SameI") private final Map<Concept, Pair> sameIndividuals = new HashMap<>();
    /** all the synonyms in the told subsumers' cycle */
    @PortedFrom(file = "dlTBox.h", name = "ToldSynonyms") private final Set<Concept> toldSynonyms = new HashSet<>();
    /** status of the KB */
    @PortedFrom(file = "dlTBox.h", name = "Status") private KBStatus status;
    private Map<Concept, DLTree> extraConceptDefs = new HashMap<>();
    private InAx statistics = new InAx();
    @PortedFrom(file = "dlTBox.h", name = "arrayCD") private final List<Concept> arrayCD = new ArrayList<>();
    @PortedFrom(file = "dlTBox.h", name = "arrayNoCD") private final List<Concept> arrayNoCD = new ArrayList<>();
    @PortedFrom(file = "dlTBox.h", name = "arrayNP") private final List<Concept> arrayNP = new ArrayList<>();
    @Original private int nItems = 0;
    @Original private final AtomicBoolean interrupted;
    @Original private final DatatypeFactory datatypeFactory;
    @PortedFrom(file = "dlTBox.h", name = "top") private Concept top;
    @PortedFrom(file = "dlTBox.h", name = "bottom") private Concept bottom;
    @PortedFrom(file = "dlTBox.h", name = "nRelevantCCalls") private long nRelevantCCalls;
    @PortedFrom(file = "dlTBox.h", name = "nRelevantBCalls") private long nRelevantBCalls;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "IV") private final IterableVec<Individual> iv = new IterableVec<>();
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "concepts") private final TIntArrayList conceptsForQueryAnswering = new TIntArrayList();
    private OWLDataFactory df;

    /**
     * @param datatypeFactory
     *        datatypeFactory
     * @param configuration
     *        configuration
     * @param interrupted
     *        interrupted
     * @param df
     *        data factory
     */
    public TBox(DatatypeFactory datatypeFactory, JFactReasonerConfiguration configuration, AtomicBoolean interrupted,
        OWLDataFactory df) {
        this.df = df;
        this.datatypeFactory = datatypeFactory;
        this.interrupted = interrupted;
        config = configuration;
        axioms = new AxiomSet(this);
        dlHeap = new DLDag(configuration);
        kbStatus = KBLOADING;
        pQuery = null;
        concepts = new NamedEntryCollection<>("concept", config);
        individuals = new NamedEntryCollection<>("individual", config);
        objectRoleMaster = new RoleMaster(false, new ObjectRoleName(df.getOWLTopObjectProperty()), new ObjectRoleName(df
            .getOWLBottomObjectProperty()), config);
        dataRoleMaster = new RoleMaster(true, new DataRoleName(df.getOWLTopDataProperty()), new DataRoleName(df
            .getOWLBottomDataProperty()), config);
        axioms = new AxiomSet(this);
        internalisedGeneralAxiom = BP_TOP;
        duringClassification = false;
        isLikeGALEN = false;
        isLikeWINE = false;
        consistent = true;
        preprocTime = 0;
        consistTime = 0;
        config.getLog().printTemplate(Templates.READ_CONFIG, Boolean.valueOf(config.getuseCompletelyDefined()),
            "useRelevantOnly(obsolete)", Boolean.valueOf(config.getdumpQuery()), Boolean.valueOf(config
                .getalwaysPreferEquals()));
        axioms.initAbsorptionFlags(config.getabsorptionFlags());
        initTopBottom();
        setForbidUndefinedNames(false);
        initTaxonomy();
    }

    /**
     * @return statistics
     */
    public InAx getStatistics() {
        return statistics;
    }

    /**
     * @return individuals
     */
    @PortedFrom(file = "dlTBox.h", name = "i_begin")
    public Stream<Individual> individuals() {
        return individuals.getConcepts();
    }

    /**
     * @return list of concepts
     */
    @PortedFrom(file = "dlTBox.h", name = "c_begin")
    public Stream<Concept> getConcepts() {
        return concepts.getConcepts();
    }

    /**
     * @return reasoner configuration
     */
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
            this.addSubsumeAxiom(r, buildDisjAux(beg));
        }
    }

    /**
     * @param r
     *        R
     * @return create REFLEXIVE node
     */
    @PortedFrom(file = "dlTBox.h", name = "reflexive2dag")
    public int reflexive2dag(Role r) {
        // input check: only simple roles are allowed in the reflexivity
        // construction
        if (!r.isSimple()) {
            throw new ReasonerInternalException("Non simple role used as simple: " + r.getIRI());
        }
        return -dlHeap.add(new DLVertex(IRR, 0, r, BP_INVALID, null, dlHeap));
    }

    /**
     * @param r
     *        R
     * @param c
     *        C
     * @return create forall node for data role
     */
    @PortedFrom(file = "dlTBox.h", name = "dataForall2dag")
    public int dataForall2dag(Role r, int c) {
        return dlHeap.add(new DLVertex(DagTag.FORALL, 0, r, c, null, dlHeap));
    }

    /**
     * @param n
     *        n
     * @param r
     *        R
     * @param c
     *        C
     * @return create atmost node for data role
     */
    @PortedFrom(file = "dlTBox.h", name = "dataAtMost2dag")
    public int dataAtMost2dag(int n, Role r, int c) {
        return dlHeap.add(new DLVertex(DagTag.LE, n, r, c, null, dlHeap));
    }

    /**
     * @param p
     *        p
     * @return a pointer to concept representation
     */
    @PortedFrom(file = "dlTBox.h", name = "concept2dag")
    public int concept2dag(@Nullable Concept p) {
        if (p == null) {
            return BP_INVALID;
        }
        if (!isValid(p.getpName())) {
            addConceptToHeap(p);
        }
        return p.resolveId();
    }

    /**
     * try to absorb GCI C[=D; if not possible, just record this GCI
     * 
     * @param c
     *        C
     * @param d
     *        D
     */
    @PortedFrom(file = "dlTBox.h", name = "processGCI")
    public void processGCI(DLTree c, DLTree d) {
        axioms.addAxiom(c, d);
    }

    /** absorb all axioms */
    @PortedFrom(file = "dlTBox.h", name = "AbsorbAxioms")
    public void absorbAxioms() {
        long nSynonyms = countSynonyms();
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
        concepts.getConcepts().filter(pc -> !pc.isSynonym()).forEach(pc -> pc.initToldSubsumers());
        individuals.getConcepts().filter(pi -> !pi.isSynonym()).forEach(pi -> pi.initToldSubsumers());
    }

    /** set told TOP concept whether necessary */
    @PortedFrom(file = "dlTBox.h", name = "setToldTop")
    public void setToldTop() {
        concepts.getConcepts().forEach(pc -> pc.setToldTop(top));
        individuals.getConcepts().forEach(pi -> pi.setToldTop(top));
    }

    /** calculate TS depth for all concepts */
    @PortedFrom(file = "dlTBox.h", name = "calculateTSDepth")
    public void calculateTSDepth() {
        concepts.getConcepts().forEach(pc -> pc.calculateTSDepth());
        individuals.getConcepts().forEach(pi -> pi.calculateTSDepth());
    }

    /**
     * @return number of synonyms in the KB
     */
    @PortedFrom(file = "dlTBox.h", name = "countSynonyms")
    public int countSynonyms() {
        long nSynonyms = concepts.getConcepts().filter(p -> p.isSynonym()).count();
        nSynonyms += individuals.getConcepts().filter(p -> p.isSynonym()).count();
        return (int) nSynonyms;
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
        v.forEach(q -> q.addExtraRule(index));
    }

    /** mark all concepts wrt their classification tag */
    @PortedFrom(file = "dlTBox.h", name = "fillsClassificationTag")
    public void fillsClassificationTag() {
        concepts.getConcepts().forEach(p -> p.getClassTag());
        individuals.getConcepts().forEach(p -> p.getClassTag());
    }

    /**
     * set new concept index for given C wrt existing nC
     * 
     * @param c
     *        C
     */
    @PortedFrom(file = "dlTBox.h", name = "setConceptIndex")
    public void setConceptIndex(Concept c) {
        c.setIndex(nC);
        conceptMap.add(c);
        ++nC;
    }

    /**
     * @return true iff reasoners were initialised
     */
    @PortedFrom(file = "dlTBox.h", name = "reasonersInited")
    private boolean reasonersInited() {
        return stdReasoner != null;
    }

    /**
     * @return reasoner wrt nominal case
     */
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
        o.print("Concepts (").print(concepts.size()).print("):\n");
        concepts.getConcepts().forEach(pc -> printConcept(o, pc));
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
        o.print("Individuals (").print(individuals.size()).print("):\n");
        individuals.getConcepts().forEach(pi -> printConcept(o, pi));
    }

    /**
     * @param o
     *        o
     */
    @PortedFrom(file = "dlTBox.h", name = "PrintSimpleRules")
    public void printSimpleRules(LogAdapter o) {
        if (simpleRules.isEmpty() || !o.isEnabled()) {
            return;
        }
        o.print("Simple rules (").print(simpleRules.size()).print("):\n");
        for (SimpleRule p : simpleRules) {
            o.print(p.getBody().stream().map(Concept::getIRI).collect(joining(", ", "(", ")")));
            o.print(" => ", p.tHead, "\n");
        }
    }

    /**
     * @param o
     *        o
     */
    @PortedFrom(file = "dlTBox.h", name = "PrintAxioms")
    public void printAxioms(LogAdapter o) {
        if (internalisedGeneralAxiom == BP_TOP) {
            return;
        }
        o.print("Axioms:\nT [=");
        printDagEntry(o, internalisedGeneralAxiom);
    }

    /**
     * @param r
     *        R
     * @return check if the role R is irreflexive
     */
    @PortedFrom(file = "dlTBox.h", name = "isIrreflexive")
    public boolean isIrreflexive(Role r) {
        assert r != null;
        // data roles are irreflexive
        if (r.isDataRole()) {
            return true;
        }
        // prepare feature that are KB features
        // FIXME!! overkill, but fine for now as it is sound
        curFeature = kbFeatures;
        getReasoner().setBlockingMethod(isIRinQuery(), isNRinQuery());
        boolean result = getReasoner().checkIrreflexivity(r);
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
        concepts.getConcepts().forEach(pc -> {
            if (!pc.isRelevant(relevance)) {
                ++nRelevantCCalls;
                pc.setRelevant(relevance);
                this.collectLogicFeature(pc);
                setRelevant(pc.getpBody());
            }
        });
        individuals.getConcepts().forEach(pi -> {
            if (!pi.isRelevant(relevance)) {
                ++nRelevantCCalls;
                pi.setRelevant(relevance);
                this.collectLogicFeature(pi);
                setRelevant(pi.getpBody());
            }
        });
        markGCIsRelevant();
    }

    /** clear all relevance info */
    @PortedFrom(file = "dlTBox.h", name = "clearRelevanceInfo")
    public void clearRelevanceInfo() {
        relevance++;
    }

    /**
     * @return fresh concept
     */
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
        kbFeatures.or(p.getPosFeatures());
        this.collectLogicFeature(p);
        clearRelevanceInfo();
        // nothing to do for neg-prim concepts
        if (p.isPrimitive()) {
            return;
        }
        curFeature = p.getNegFeatures();
        setRelevant(-p.getpBody());
        kbFeatures.or(p.getNegFeatures());
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

    /**
     * @return object Role Master
     */
    @PortedFrom(file = "dlTBox.h", name = "getORM")
    public RoleMaster getORM() {
        return objectRoleMaster;
    }

    /**
     * @return Data Role Master
     */
    @PortedFrom(file = "dlTBox.h", name = "getDRM")
    public RoleMaster getDRM() {
        return dataRoleMaster;
    }

    /**
     * @param r
     *        R
     * @return RoleMaster depending of the R
     */
    @PortedFrom(file = "dlTBox.h", name = "getRM")
    public RoleMaster getRM(Role r) {
        return r.isDataRole() ? dataRoleMaster : objectRoleMaster;
    }

    /**
     * @return DAG (needed for KE)
     */
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
        return concepts.get(name, Concept::new);
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
        return individuals.get(name, Individual::new);
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
        return tree.token() == INAME && this.isIndividual(tree.elem().getNE().getIRI());
    }

    /**
     * @param name
     *        name
     * @return TOP/BOTTOM/CN/IN by the DLTree entry
     */
    @Nullable
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
        return (Concept) name.elem().getNE();
    }

    /**
     * @param c
     *        C
     * @return a DL tree by a given concept-like C
     */
    @Nullable
    @PortedFrom(file = "dlTBox.h", name = "getTree")
    public DLTree getTree(@Nullable Concept c) {
        if (c == null) {
            return null;
        }
        if (c.isTop()) {
            return DLTreeFactory.createTop();
        }
        if (c.isBottom()) {
            return DLTreeFactory.createBottom();
        }
        return DLTreeFactory.buildTree(new Lexeme(this.isIndividual(c.getIRI()) ? INAME : CNAME, c));
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
     * @param r
     *        R
     * @param b
     *        b
     */
    @PortedFrom(file = "dlTBox.h", name = "RegisterIndividualRelation")
    public void registerIndividualRelation(NamedEntry a, NamedEntry r, NamedEntry b) {
        if (!this.isIndividual(a.getIRI()) || !this.isIndividual(b.getIRI())) {
            throw new ReasonerInternalException("Individual expected in related()");
        }
        relatedIndividuals.add(new Related((Individual) a, (Individual) b, (Role) r));
        relatedIndividuals.add(new Related((Individual) b, (Individual) a, ((Role) r).inverse()));
    }

    /**
     * add axiom CN [= D for concept CN
     * 
     * @param c
     *        C
     * @param d
     *        D
     */
    @PortedFrom(file = "dlTBox.h", name = "addSubsumeAxiom")
    public void addSubsumeAxiom(Concept c, DLTree d) {
        this.addSubsumeAxiom(getTree(c), d);
    }

    /**
     * add simple rule RULE to the TBox' rules
     * 
     * @param rule
     *        Rule
     */
    @PortedFrom(file = "dlTBox.h", name = "addSimpleRule")
    private void addSimpleRule(SimpleRule rule) {
        initRuleFields(rule.getBody(), simpleRules.size());
        simpleRules.add(rule);
    }

    /** let TBox know that the whole ontology is loaded */
    @PortedFrom(file = "dlTBox.h", name = "finishLoading")
    public void finishLoading() {
        setForbidUndefinedNames(true);
    }

    /**
     * @return true if KB contains fairness constraints
     */
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
        if (config.getUseAnywhereBlocking() && hasFC()) {
            config.setUseAnywhereBlocking(false);
            config.getLog().print("\nFairness constraints: set useAnywhereBlocking = 0");
        }
    }

    /**
     * @param l
     *        l
     */
    @PortedFrom(file = "dlTBox.h", name = "setFairnessConstraint")
    public void setFairnessConstraintDLTrees(List<DLTree> l) {
        l.forEach(this::setFairnessConstraintDLTrees);
    }

    protected void setFairnessConstraintDLTrees(DLTree d) {
        // build a flag for a FC
        Concept fc = getAuxConcept(null);
        fairness.add(fc);
        // make an axiom: C [= FC
        addSubsumeAxiom(d, getTree(fc));
    }

    /**
     * @return GCI Axioms
     */
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

    /**
     * @return check if the relevant part of KB contains inverse roles.
     */
    @PortedFrom(file = "dlTBox.h", name = "isIRinQuery")
    public boolean isIRinQuery() {
        if (curFeature != null) {
            return curFeature.hasInverseRole();
        } else {
            return kbFeatures.hasInverseRole();
        }
    }

    /**
     * @return check if the relevant part of KB contains number restrictions.
     */
    @PortedFrom(file = "dlTBox.h", name = "isNRinQuery")
    public boolean isNRinQuery() {
        LogicFeatures p = curFeature != null ? curFeature : kbFeatures;
        return p.hasFunctionalRestriction() || p.hasNumberRestriction() || p.hasQNumberRestriction();
    }

    /**
     * @return check if the relevant part of KB contains singletons
     */
    @PortedFrom(file = "dlTBox.h", name = "testHasNominals")
    public boolean testHasNominals() {
        if (curFeature != null) {
            return curFeature.hasSingletons();
        } else {
            return kbFeatures.hasSingletons();
        }
    }

    /**
     * @return check if the relevant part of KB contains top role
     */
    @PortedFrom(file = "dlTBox.h", name = "testHasTopRole")
    public boolean testHasTopRole() {
        if (curFeature != null) {
            return curFeature.hasTopRole();
        } else {
            return kbFeatures.hasTopRole();
        }
    }

    /**
     * @return check if Sorted Reasoning is applicable
     */
    @PortedFrom(file = "dlTBox.h", name = "canUseSortedReasoning")
    public boolean canUseSortedReasoning() {
        return config.isUseSortedReasoning() && !gcis.isGCI() && !gcis.isReflexive();
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

    /**
     * @return internal Taxonomy of concepts
     */
    @PortedFrom(file = "dlTBox.h", name = "getTaxonomy")
    public Taxonomy getTaxonomy() {
        return pTax;
    }

    /**
     * @return status flag
     */
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
        kbStatus = KBCHECKED;
        consistent = val;
    }

    /**
     * @return check if the ontology is consistent
     */
    @PortedFrom(file = "dlTBox.h", name = "isConsistent")
    public boolean isConsistent() {
        if (kbStatus.ordinal() < KBCHECKED.ordinal()) {
            prepareReasoning();
            if (kbStatus.ordinal() < KBCHECKED.ordinal() && consistent) {
                // we can detect inconsistency during preprocessing
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
    public boolean testSortedNonSubsumption(Concept p, @Nullable Concept q) {
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
        conceptMap.add(null);
        // make fresh concept and datatype
        concept2dag(pTemp);
        concepts.getConcepts().forEach(this::concept2dag);
        individuals.getConcepts().forEach(this::concept2dag);
        simpleRules.forEach(q -> q.setBpHead(tree2dag(q.tHead)));
        // builds Roles range and domain
        initRangeDomain(objectRoleMaster);
        initRangeDomain(dataRoleMaster);
        DLTree gci = axioms.getGCI();
        // add special domains to the GCIs
        List<DLTree> list = new ArrayList<>();
        if (config.isUseSpecialDomains()) {
            objectRoleMaster.getRoles().stream().filter(p -> !p.isSynonym() && p.hasSpecialDomain()).forEach(p -> list
                .add(p.getTSpecialDomain().copy()));
        }
        // take chains that lead to Bot role into account
        if (!objectRoleMaster.getBotRole().isSimple()) {
            list.add(DLTreeFactory.createSNFForall(DLTreeFactory.createRole(objectRoleMaster.getBotRole()),
                DLTreeFactory.createBottom()));
        }
        if (!list.isEmpty()) {
            list.add(gci);
            gci = DLTreeFactory.createSNFAnd(list);
        }
        internalisedGeneralAxiom = tree2dag(gci);
        // mark GCI flags
        gcis.setGCI(internalisedGeneralAxiom != BP_TOP);
        gcis.setReflexive(objectRoleMaster.hasReflexiveRoles());
        // builds functional labels for roles
        Stream.concat(objectRoleMaster.getRoles().stream(), dataRoleMaster.getRoles().stream()).filter(p -> !p
            .isSynonym() && p.isTopFunc()).forEach(p -> p.setFunctional(atmost2dag(1, p, BP_TOP)));
        // check the type of the ontology
        if (nNominalReferences > 0) {
            int nInd = individuals.size();
            if (nInd > 100 && nNominalReferences > nInd) {
                isLikeWINE = true;
            }
        }
        // here DAG is complete; set its final size
        dlHeap.setFinalSize();
    }

    /**
     * @param rm
     *        RM
     */
    @PortedFrom(file = "dlTBox.h", name = "initRangeDomain")
    public void initRangeDomain(RoleMaster rm) {
        rm.getRoles().stream().filter(r -> !r.isSynonym()).forEach(this::initRangeDomain);
    }

    protected void initRangeDomain(Role r) {
        if (config.isUpdaterndFromSuperRoles()) {
            // add R&D from super-roles (do it AFTER axioms are
            // transformed into R&D)
            r.collectDomainFromSupers();
        }
        DLTree dom = r.getTDomain();
        int bp = BP_TOP;
        if (dom != null) {
            bp = tree2dag(dom);
            gcis.setRnD(true);
        }
        r.setBPDomain(bp);
        // special domain for R is AR.Range
        r.initSpecialDomain();
        if (r.hasSpecialDomain()) {
            r.setSpecialDomain(tree2dag(r.getTSpecialDomain()));
        }
    }

    /**
     * @param p
     *        p
     * @return index of new element
     */
    @PortedFrom(file = "dlTBox.h", name = "addDataExprToHeap")
    public int addDataExprToHeap(LiteralEntry p) {
        if (isValid(p.getIndex())) {
            return p.getIndex();
        }
        DagTag dt = DATAVALUE;
        int hostBP = addDatatypeExpressionToHeap(p.getType());
        DLVertex ver = new DLVertex(dt, 0, null, hostBP, null, dlHeap);
        ver.setConcept(p);
        p.setIndex(dlHeap.directAdd(ver, false));
        return p.getIndex();
    }

    /**
     * @param p
     *        p
     * @return index of new element
     */
    @PortedFrom(file = "dlTBox.h", name = "addDataExprToHeap")
    public int addDataExprToHeap(DatatypeEntry p) {
        if (isValid(p.getIndex())) {
            return p.getIndex();
        }
        DagTag dt = p.isBasicDataType() ? DATATYPE : DagTag.DATAEXPR;
        int hostBP = BP_TOP;
        if (!p.isBasicDataType()) {
            Datatype<?> baseType = ((DatatypeExpression<?>) p.getDatatype()).getHostType();
            hostBP = addDatatypeExpressionToHeap(baseType);
        }
        DLVertex ver = new DLVertex(dt, 0, null, hostBP, null, dlHeap);
        ver.setConcept(p);
        p.setIndex(dlHeap.directAdd(ver, false));
        return p.getIndex();
    }

    /**
     * @param p
     *        p
     * @return index of new element
     */
    @Original
    public int addDatatypeExpressionToHeap(Datatype<?> p) {
        DatatypeEntry concept = new DatatypeEntry(p);
        int index = dlHeap.index(concept);
        if (index != BP_INVALID) {
            return index;
        }
        // else, create a new vertex and add it
        DLVertex ver = new DLVertex(DATATYPE, 0, null, BP_TOP, null, dlHeap);
        ver.setConcept(concept);
        return dlHeap.directAdd(ver, true);
    }

    /**
     * @param pConcept
     *        pConcept
     */
    @PortedFrom(file = "dlTBox.h", name = "addConceptToHeap")
    public void addConceptToHeap(Concept pConcept) {
        // choose proper tag by concept
        DagTag tag = pConcept.isPrimitive() ? pConcept.isSingleton() ? PSINGLETON : PCONCEPT
            : pConcept.isSingleton() ? NSINGLETON : NCONCEPT;
        // NSingleton is a nominal
        if (tag == NSINGLETON && !pConcept.isSynonym()) {
            pConcept.setNominal(true);
        }
        // new concept's addition
        DLVertex ver = new DLVertex(tag, dlHeap);
        ver.setConcept(pConcept);
        pConcept.setpName(dlHeap.directAdd(ver, false));
        int desc = BP_TOP;
        // translate body of a concept
        if (pConcept.getDescription() != null) {
            // complex concept
            desc = tree2dag(pConcept.getDescription());
        } else {
            // only primivive concepts here
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
    @SuppressWarnings("incomplete-switch")
    public int tree2dag(@Nullable DLTree t) {
        if (t == null) {
            return BP_INVALID;
        }
        Lexeme cur = t.elem();
        int ret;
        switch (cur.getToken()) {
            case BOTTOM:
                ret = BP_BOTTOM;
                break;
            case TOP:
                ret = BP_TOP;
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
                Concept ind = (Concept) cur.getNE();
                ind.setNominal(true);
                ret = concept2dag(ind);
                break;
            case NOT:
                ret = -tree2dag(t.getChild());
                break;
            case AND:
                ret = and2dag(new DLVertex(DagTag.AND, dlHeap), t);
                break;
            case FORALL:
                ret = forall2dag(resolveRole(t.getLeft()), tree2dag(t.getRight()));
                break;
            case SELF:
                ret = reflexive2dag(resolveRole(t.getChild()));
                break;
            case LE:
                ret = atmost2dag(cur.getData(), resolveRole(t.getLeft()), tree2dag(t.getRight()));
                break;
            case PROJFROM:
                // XXX verify if new object necessary?
                ret = dlHeap.directAdd(new DLVertex(DagTag.PROJ, 0, Role.resolveRole(t.getLeft()), tree2dag(t.getRight()
                    .getRight()), resolveRole(t.getRight().getLeft()), dlHeap), false);
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
        int ret = BP_BOTTOM;
        if (!fillANDVertex(v, t)) {
            // no clash found: AND vertex
            int value = v.getAndToDagValue();
            if (value != BP_INVALID) {
                return value;
            }
            return dlHeap.add(v);
        }
        return ret;
    }

    /**
     * @param r
     *        R
     * @param c
     *        C
     * @return index of new element
     */
    @PortedFrom(file = "dlTBox.h", name = "forall2dag")
    public int forall2dag(Role r, int c) {
        if (r.isDataRole()) {
            return dataForall2dag(r, c);
        }
        // create \all R.C == \all R{0}.C
        int ret = dlHeap.add(new DLVertex(DagTag.FORALL, 0, r, c, null, dlHeap));
        if (r.isSimple()) {
            // don't care about the rest
            return ret;
        }
        // check if the concept is not last
        if (!dlHeap.isLast(ret)) {
            // all sub-roles were added before
            return ret;
        }
        // have appropriate concepts for all the automata states
        for (int i = 1; i < r.getAutomaton().size(); ++i) {
            dlHeap.directAddAndCache(new DLVertex(DagTag.FORALL, i, r, c, null, dlHeap));
        }
        return ret;
    }

    /**
     * @param n
     *        n
     * @param r
     *        R
     * @param c
     *        C
     * @return index of new element
     */
    @PortedFrom(file = "dlTBox.h", name = "atmost2dag")
    public int atmost2dag(int n, Role r, int c) {
        // input check: only simple roles are allowed in the (non-trivial) NR
        if (!r.isSimple()) {
            throw new ReasonerInternalException("Non simple role used as simple: " + r.getIRI());
        }
        if (r.isDataRole()) {
            return dataAtMost2dag(n, r, c);
        }
        if (c == BP_BOTTOM) {
            // can happen as A & ~A
            return BP_TOP;
        }
        int ret = dlHeap.add(new DLVertex(DagTag.LE, n, r, c, null, dlHeap));
        // check if the concept is not last
        if (!dlHeap.isLast(ret)) {
            // all elements were added before
            return ret;
        }
        // create entries for the transitive sub-roles
        for (int m = n - 1; m > 0; --m) {
            dlHeap.directAddAndCache(new DLVertex(DagTag.LE, m, r, c, null, dlHeap));
        }
        // create a blocker for the NN-rule
        dlHeap.directAddAndCache(new DLVertex(DagTag.NN, dlHeap));
        return ret;
    }

    @PortedFrom(file = "dlTBox.h", name = "fillANDVertex")
    private boolean fillANDVertex(DLVertex v, DLTree t) {
        if (t.isAND()) {
            return t.getChildren().stream().anyMatch(i -> fillANDVertex(v, i));
        }
        return v.addChild(tree2dag(t));
    }

    /**
     * @param begin
     *        begin
     * @param <T>
     *        concept type
     * @return number of elements
     */
    @PortedFrom(file = "dlTBox.h", name = "fillArrays")
    @SuppressWarnings("incomplete-switch")
    public <T extends Concept> int fillArrays(Stream<T> begin) {
        AtomicInteger n = new AtomicInteger(0);
        begin.filter(p -> !p.isNonClassifiable()).forEach(p -> {
            n.incrementAndGet();
            switch (p.getClassTag()) {
                case COMPLETELYDEFINED:
                    arrayCD.add(p);
                    break;
                case NONPRIMITIVE:
                case HASNONPRIMITIVETS:
                    arrayNP.add(p);
                    break;
                default:
                    arrayNoCD.add(p);
                    break;
            }
        });
        return n.get();
    }

    /**
     * @return n items
     */
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
        pTaxCreator.setBottomUp(gcis);
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
        nItems += fillArrays(concepts.getConcepts());
        nItems += fillArrays(individuals.getConcepts());
        config.getProgressMonitor().reasonerTaskStarted(ReasonerProgressMonitor.CLASSIFYING);
        duringClassification = true;
        classifyConcepts(arrayCD, true, "completely defined");
        classifyConcepts(arrayNoCD, false, "regular");
        classifyConcepts(arrayNP, false, "non-primitive");
        duringClassification = false;
        config.getProgressMonitor().reasonerTaskStopped();
        pTax.finalise();
        locTimer.stop();
        if (config.getverboseOutput()) {
            config.getLog().print(DONE_IN).print(locTimer.calcDelta()).print(SECONDS);
        }
        if (needConcept && kbStatus.ordinal() < KBCLASSIFIED.ordinal()) {
            kbStatus = KBCLASSIFIED;
        }
        if (needIndividual || nNominalReferences > 0) {
            kbStatus = KBREALISED;
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
    public void classifyConcepts(List<Concept> collection, boolean curCompletelyDefined, String type) {
        // set CD for taxonomy
        pTaxCreator.setCompletelyDefined(curCompletelyDefined);
        config.getLog().printTemplate(Templates.CLASSIFY_CONCEPTS, type);
        // check if concept is already classified
        // classify and count otherwise
        int n = (int) collection.stream().filter(q -> !interrupted.get() && !q.isClassified()).map(this::classifyEntry)
            .filter(Concept::isClassified).count();
        config.getLog().printTemplateMixInt(Templates.CLASSIFY_CONCEPTS2, type, n);
    }

    /**
     * classify single concept
     * 
     * @param entry
     *        entry
     * @return modified input
     */
    @PortedFrom(file = "dlTBox.h", name = "classifyEntry")
    private Concept classifyEntry(Concept entry) {
        if (isBlockedInd(entry)) {
            classifyEntry(getBlockingInd(entry));
            // make sure that the possible synonym is already classified
        }
        if (!entry.isClassified()) {
            pTaxCreator.classifyEntry(entry);
        }
        return entry;
    }

    /**
     * @param desc
     *        desc
     * @return concept for desc
     */
    @PortedFrom(file = "dlTBox.h", name = "getAuxConcept")
    public Concept getAuxConcept(@Nullable DLTree desc) {
        boolean old = setForbidUndefinedNames(false);
        Concept c = getConcept(IRI.create("urn:aux" + ++auxConceptID));
        setForbidUndefinedNames(old);
        c.setSystem();
        c.setNonClassifiable(true);
        c.setPrimitive(true);
        c.addDesc(desc);
        // it is created after this is done centrally
        c.initToldSubsumers();
        return c;
    }

    @PortedFrom(file = "dlTBox.h", name = "initTopBottom")
    private void initTopBottom() {
        top = Concept.getTOP(df);
        bottom = Concept.getBOTTOM(df);
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
    public void prepareFeatures(@Nullable Concept pConcept, @Nullable Concept qConcept) {
        auxFeatures = new LogicFeatures(gciFeatures);
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
        // set blocking method for the current reasoning session
        getReasoner().setBlockingMethod(isIRinQuery(), isNRinQuery());
    }

    /** build simple cache */
    @PortedFrom(file = "dlTBox.h", name = "buildSimpleCache")
    public void buildSimpleCache() {
        // set cache for BOTTOM entry
        initConstCache(BP_BOTTOM);
        // set all the caches for the temp concept
        initSingletonCache(pTemp, true);
        initSingletonCache(pTemp, false);
        // inapplicable if KB contains CGIs in any form
        if (gcis.isGCI() || gcis.isReflexive()) {
            return;
        }
        // it is now safe to make a TOP cache
        initConstCache(BP_TOP);
        concepts.getConcepts().filter(pc -> pc.isPrimitive()).forEach(pc -> initSingletonCache(pc, false));
        individuals.getConcepts().filter(pc -> pc.isPrimitive()).forEach(pc -> initSingletonCache(pc, false));
    }

    /**
     * @return true if consistent
     */
    @PortedFrom(file = "dlTBox.h", name = "performConsistencyCheck")
    public boolean performConsistencyCheck() {
        if (config.getverboseOutput()) {
            config.getLog().print("Consistency checking...\n");
        }
        Timer pt = new Timer();
        pt.start();
        buildSimpleCache();
        Concept test = nominalCloudFeatures.hasSingletons() ? individuals.first() : null;
        prepareFeatures(test, null);
        boolean ret;
        if (test != null) {
            // make a cache for TOP if it is not there
            if (dlHeap.getCache(BP_TOP) == null) {
                initConstCache(BP_TOP);
            }
            ret = nomReasoner.consistentNominalCloud();
        } else {
            ret = isSatisfiable(top);
        }
        // setup cache for GCI
        if (gcis.isGCI()) {
            // there is no much win to have it together with
            // special-domains-as-GCIs ATM.
            dlHeap.setCache(-internalisedGeneralAxiom, new ModelCacheConst(false));
        }
        pt.stop();
        consistTime = pt.calcDelta();
        if (config.getverboseOutput()) {
            config.getLog().print(DONE_IN).print(consistTime).print(SECONDS);
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
        // check whether we already did the test
        ModelCacheInterface cache = dlHeap.getCache(pConcept.getpName());
        if (cache != null) {
            return cache.getState() != ModelCacheState.INVALID;
        }
        // logging the startpoint
        config.getLog().printTemplate(Templates.IS_SATISFIABLE, pConcept.getIRI());
        // perform reasoning with a proper logical features
        prepareFeatures(pConcept, null);
        // XXX next two lines not in FaCT++, see what they do
        int resolveId = pConcept.resolveId();
        if (resolveId == BP_INVALID) {
            config.getLog().print("query concept still invalid after prepareFeatures()");
            return false;
        }
        boolean result = getReasoner().runSat(resolveId, BP_TOP);
        cache = getReasoner().buildCacheByCGraph(result);
        // save cache
        dlHeap.setCache(pConcept.getpName(), cache);
        clearFeatures();
        config.getLog().printTemplate(Templates.IS_SATISFIABLE1, pConcept.getIRI(), !result ? "un" : "");
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
        config.getLog().printTemplate(Templates.ISSUBHOLDS1, pConcept.getIRI(), qConcept.getIRI());
        // perform reasoning with a proper logical features
        prepareFeatures(pConcept, qConcept);
        boolean result = !getReasoner().runSat(pConcept.resolveId(), -qConcept.resolveId());
        clearFeatures();
        config.getLog().printTemplate(Templates.ISSUBHOLDS2, pConcept.getIRI(), qConcept.getIRI(), !result ? " NOT"
            : "");
        return result;
    }

    /**
     * @param a
     *        _a
     * @param b
     *        _b
     * @return true if same individual
     */
    @PortedFrom(file = "dlTBox.h", name = "isSameIndividuals")
    public boolean isSameIndividuals(Individual a, Individual b) {
        if (a.equals(b)) {
            return true;
        }
        Individual inda = resolveSynonym(a);
        Individual indb = resolveSynonym(b);
        if (inda.equals(indb)) {
            // known synonyms
            return true;
        }
        if (!this.isIndividual(inda.getIRI()) || !this.isIndividual(indb.getIRI())) {
            throw new ReasonerInternalException("Individuals are expected in the isSameIndividuals() query");
        }
        if (inda.isFresh() || indb.isFresh()) {
            if (inda.isSynonym()) {
                return isSameIndividuals((Individual) inda.getSynonym(), indb);
            }
            if (indb.isSynonym()) {
                return isSameIndividuals(inda, (Individual) indb.getSynonym());
            }
            // here this means that one of the individuals is a fresh name
            return false;
        }
        // TODO equals for TaxonomyVertex
        return inda.getTaxVertex().equals(indb.getTaxVertex());
    }

    /**
     * @param r
     *        R
     * @param s
     *        S
     * @return true if disjoint roles
     */
    @PortedFrom(file = "dlTBox.h", name = "isDisjointRoles")
    public boolean isDisjointRoles(Role r, Role s) {
        assert r != null && s != null;
        if (r.isDataRole() != s.isDataRole()) {
            return true;
        }
        // prepare feature that are KB features
        // FIXME!! overkill, but fine for now as it is sound
        curFeature = kbFeatures;
        getReasoner().setBlockingMethod(isIRinQuery(), isNRinQuery());
        boolean result = getReasoner().checkDisjointRoles(r, s);
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
        // prepare told subsumers for classification; as it is non-primitive, it
        // is not CD
        pQuery.initToldSubsumers();
        // setup taxonomy behaviour flags
        assert pTax != null;
        // non-primitive concept
        pTaxCreator.setCompletelyDefined(false);
        // classify the concept
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
        if (getReasoner().runSat(pConcept.resolveId(), BP_TOP)) {
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
        assert kbStatus.ordinal() >= KBCHECKED.ordinal();
        if (consistent) {
            o.print("Required");
        } else {
            o.print("KB is inconsistent. Query is NOT processed\nConsistency");
        }
        long sum = preprocTime + consistTime;
        o.print(" check done in ").print(time).print(" seconds\nof which:\nPreproc. takes ").print(preprocTime).print(
            " seconds\nConsist. takes ").print(consistTime).print(" seconds");
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
        if (p == BP_TOP) {
            o.print(" *TOP*");
            return;
        } else if (p == BP_BOTTOM) {
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
        DagTag dagtag = v.getType();
        switch (dagtag) {
            case TOP:
                o.print(" *TOP*");
                return;
            case PCONCEPT:
            case NCONCEPT:
            case PSINGLETON:
            case NSINGLETON:
            case DATATYPE:
            case DATAVALUE:
                o.print(" ");
                o.print(v.getConcept().getIRI());
                return;
            case DATAEXPR:
                o.print(" ");
                o.print(getDataEntryByBP(p));
                return;
            case IRR:
                o.print(" (", dagtag.getName(), " ", v.getRole().getIRI(), ")");
                return;
            case COLLECTION:
            case AND:
                o.print(" (");
                o.print(dagtag.getName());
                for (int q : v.begin()) {
                    printDagEntry(o, q);
                }
                o.print(")");
                return;
            case FORALL:
            case LE:
                o.print(" (");
                o.print(dagtag.getName());
                if (dagtag == DagTag.LE) {
                    o.print(" ");
                    o.print(v.getNumberLE());
                }
                o.print(" ");
                o.print(v.getRole().getIRI());
                printDagEntry(o, v.getConceptIndex());
                o.print(")");
                return;
            case PROJ:
                o.print(" (", dagtag.getName(), " ", v.getRole().getIRI(), ")");
                printDagEntry(o, v.getConceptIndex());
                o.print(" => ", v.getProjRole().getIRI(), ")");
                return;
            case NN:
            case CHOOSE:
                throw new UnreachableSituationException();
            case BAD:
                o.print("WRONG: printing a badtag dtBad!\n");
                break;
            default:
                throw new ReasonerInternalException("Error printing vertex of type " + dagtag.getName() + '(' + dagtag
                    + ')');
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
            o.print(".", p.getIRI(), " [").print(p.getTsDepth()).print("] ", p.isPrimitive() ? "[=" : "=");
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
        concepts.getConcepts().filter(p -> p.isRelevant(relevance)).forEach(p -> dumpConcept(dump, p));
        individuals.getConcepts().filter(p -> p.isRelevant(relevance)).forEach(p -> dumpConcept(dump, p));
        if (internalisedGeneralAxiom != BP_TOP) {
            dump.startAx(DIOp.IMPLIESC);
            dump.dumpTop();
            dump.contAx(DIOp.IMPLIESC);
            dumpExpression(dump, internalisedGeneralAxiom);
            dump.finishAx(DIOp.IMPLIESC);
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
        // dump defConcept
        dump.startAx(DIOp.DEFINEC);
        dump.dumpConcept(p);
        dump.finishAx(DIOp.DEFINEC);
        // dump "p [= def"
        if (p.getpBody() != BP_TOP) {
            DIOp ax = p.isPrimitive() ? DIOp.IMPLIESC : DIOp.EQUALSC;
            dump.startAx(ax);
            dump.dumpConcept(p);
            dump.contAx(ax);
            dumpExpression(dump, p.getpBody());
            dump.finishAx(ax);
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
            dump.startAx(DIOp.DEFINER);
            dump.dumpRole(q);
            dump.finishAx(DIOp.DEFINER);
            if (q.hasToldSubsumers()) {
                q.getToldSubsumers().forEach(i -> {
                    dump.startAx(DIOp.IMPLIESR);
                    dump.dumpRole(q);
                    dump.contAx(DIOp.IMPLIESR);
                    dump.dumpRole((Role) i);
                    dump.finishAx(DIOp.IMPLIESR);
                });
            }
        }
        if (p.isTransitive()) {
            dump.startAx(DIOp.TRANSITIVER);
            dump.dumpRole(p);
            dump.finishAx(DIOp.TRANSITIVER);
        }
        if (p.isTopFunc()) {
            dump.startAx(DIOp.FUNCTIONALR);
            dump.dumpRole(p);
            dump.finishAx(DIOp.FUNCTIONALR);
        }
        if (p.getBPDomain() != BP_TOP) {
            dump.startAx(DIOp.DOMAINR);
            dump.dumpRole(p);
            dump.contAx(DIOp.DOMAINR);
            dumpExpression(dump, p.getBPDomain());
            dump.finishAx(DIOp.DOMAINR);
        }
        if (p.getBPRange() != BP_TOP) {
            dump.startAx(DIOp.RANGER);
            dump.dumpRole(p);
            dump.contAx(DIOp.RANGER);
            dumpExpression(dump, p.getBPRange());
            dump.finishAx(DIOp.RANGER);
        }
    }

    /**
     * @param dump
     *        dump
     * @param p
     *        p
     */
    @PortedFrom(file = "dlTBox.h", name = "dumpExpression")
    @SuppressWarnings("incomplete-switch")
    public void dumpExpression(DumpInterface dump, int p) {
        assert isValid(p);
        if (p == BP_TOP) {
            dump.dumpTop();
            return;
        }
        if (p == BP_BOTTOM) {
            dump.dumpBottom();
            return;
        }
        if (p < 0) {
            dump.startOp(DIOp.NOT);
            dumpExpression(dump, -p);
            dump.finishOp(DIOp.NOT);
            return;
        }
        DLVertex v = dlHeap.get(Math.abs(p));
        DagTag dagtag = v.getType();
        switch (dagtag) {
            case TOP:
                dump.dumpTop();
                return;
            case PCONCEPT:
            case NCONCEPT:
            case PSINGLETON:
            case NSINGLETON:
                dump.dumpConcept((Concept) v.getConcept());
                return;
            case AND:
                dump.startOp(DIOp.AND);
                int[] begin = v.begin();
                for (int q : begin) {
                    if (q != begin[0]) {
                        dump.contOp(DIOp.AND);
                    }
                    dumpExpression(dump, q);
                }
                dump.finishOp(DIOp.AND);
                return;
            case FORALL:
                dump.startOp(DIOp.FORALL);
                dump.dumpRole(v.getRole());
                dump.contOp(DIOp.FORALL);
                dumpExpression(dump, v.getConceptIndex());
                dump.finishOp(DIOp.FORALL);
                return;
            case LE:
                dump.startOp(DIOp.LE, v.getNumberLE());
                dump.dumpRole(v.getRole());
                dump.contOp(DIOp.LE);
                dumpExpression(dump, v.getConceptIndex());
                dump.finishOp(DIOp.LE);
                return;
            default:
                throw new ReasonerInternalException("Error dumping vertex of type " + dagtag.getName() + '(' + dagtag
                    + ')');
        }
    }

    /**
     * @param dump
     *        dump
     */
    @PortedFrom(file = "dlTBox.h", name = "dumpAllRoles")
    public void dumpAllRoles(DumpInterface dump) {
        Stream.concat(objectRoleMaster.getRoles().stream(), dataRoleMaster.getRoles().stream()).filter(p -> p
            .isRelevant(relevance)).forEach(p -> dumpRole(dump, p));
    }

    /**
     * @param sub
     *        sub
     * @param sup
     *        sup
     */
    @PortedFrom(file = "dlTBox.h", name = "addSubsumeAxiom")
    public void addSubsumeAxiom(@Nullable DLTree sub, @Nullable DLTree sup) {
        // for C [= C: nothing to do
        if (DLTree.equalTrees(sub, sup)) {
            return;
        }
        // try to apply C [= CN
        if (sup.isCN()) {
            sup = applyAxiomCToCN(sub, sup);
            if (sup == null) {
                return;
            }
        }
        // try to apply CN [= C
        if (sub.isCN()) {
            sub = applyAxiomCNToC(sub, sup);
            if (sub == null) {
                return;
            }
        }
        // check if an axiom looks like T [= \AR.C
        // if not, treat as a GCI
        if (!axiomToRangeDomain(sub, sup)) {
            processGCI(sub, sup);
        }
    }

    /**
     * @param d
     *        D
     * @param cn
     *        CN
     * @return merged dltree
     */
    @Nullable
    @PortedFrom(file = "dlTBox.h", name = "applyAxiomCToCN")
    public DLTree applyAxiomCToCN(DLTree d, DLTree cn) {
        Concept ci = getCI(cn);
        assert ci != null;
        Concept c = resolveSynonym(ci);
        // lie: this will never be reached
        if (c.isBottom()) {
            return DLTreeFactory.createBottom();
        }
        if (c.isTop()) {
            return null;
        }
        if (!(c.isSingleton() && d.isName()) && DLTree.equalTrees(c.getDescription(), d)) {
            makeNonPrimitive(c, d);
        } else {
            return cn;
        }
        return null;
    }

    /**
     * @param cn
     *        CN
     * @param d
     *        D
     * @return merged dltree
     */
    @Nullable
    @PortedFrom(file = "dlTBox.h", name = "applyAxiomCNToC")
    public DLTree applyAxiomCNToC(DLTree cn, DLTree d) {
        Concept ci = getCI(cn);
        assert ci != null;
        Concept c = resolveSynonym(ci);
        if (c.isTop()) {
            return DLTreeFactory.createTop();
        }
        if (c.isBottom()) {
            return null;
        }
        if (c.isPrimitive()) {
            c.addDesc(d);
        } else {
            // XXX no return from here?
            addSubsumeForDefined(c, d);
        }
        return null;
    }

    /**
     * add an axiom CN [= E for defined CN (CN=D already in base)
     * 
     * @param c
     *        C
     * @param e
     *        superclass
     */
    @PortedFrom(file = "dlTBox.h", name = "addSubsumeForDefined")
    public void addSubsumeForDefined(Concept c, DLTree e) {
        // if E is a syntactic sub-class of D, then nothing to do
        if (DLTreeFactory.isSubTree(e, c.getDescription())) {
            return;
        }
        // try to see whether C contains a reference to itself at the top level
        if (c.hasSelfInDesc()) {
            // remember the old description value
            DLTree d = c.getDescription().copy();
            // remove C from the description
            c.removeSelfFromDescription();
            // the trees should differ here
            assert !DLTree.equalTrees(d, c.getDescription());
            // note that we don't know exact semantics of C for now.
            // we need to split its definition and work via GCIs
            makeDefinitionPrimitive(c, e, d);
        } else {
            // here we have the definition of C = D, and subsumption C [= E
            // XXX this disables the new strategy
            if (true) {
                // for now: it's not clear of what's going wrong
                processGCI(getTree(c), e);
            } else {
                // here we leave the definition of C = D, and delay the
                // processing of C [= E
                DLTree p = extraConceptDefs.get(c);
                if (p == null) {
                    // no such entry
                    // save C [= E
                    extraConceptDefs.put(c, e);
                } else {
                    // we have C [= X; change to C [= (X and E)
                    extraConceptDefs.put(c, DLTreeFactory.createSNFAnd(p, e));
                }
            }
            return;
        }
    }

    /**
     * @param sub
     *        sub
     * @param sup
     *        sup
     * @return true if range can be set
     */
    @PortedFrom(file = "dlTBox.h", name = "axiomToRangeDomain")
    public static boolean axiomToRangeDomain(DLTree sub, DLTree sup) {
        if (sub.isTOP() && sup.token() == Token.FORALL) {
            resolveRole(sup.getLeft()).setRange(sup.getRight().copy());
            return true;
        }
        if (sub.token() == NOT && sub.getChild().token() == Token.FORALL && sub.getChild().getRight().isBOTTOM()) {
            resolveRole(sub.getChild().getLeft()).setDomain(sup);
            return true;
        }
        return false;
    }

    @PortedFrom(file = "dlTBox.h", name = "addEqualityAxiom")
    private void addEqualityAxiom(DLTree left, DLTree right) {
        // check whether LHS is a named concept
        Concept leftCI = getCI(left);
        Concept rightCI = getCI(right);
        Concept c = null;
        if (leftCI != null) {
            c = resolveSynonym(leftCI);
        }
        boolean isNamedLHS = c != null && !c.isTop() && !c.isBottom();
        // check whether LHS is a named concept
        Concept d = null;
        if (rightCI != null) {
            d = resolveSynonym(rightCI);
        }
        boolean isNamedRHS = d != null && !d.isTop() && !d.isBottom();
        // try to make a definition C = RHS for C with no definition
        if (isNamedLHS && addNonprimitiveDefinition(c, d, right)) {
            return;
        }
        // try to make a definition RHS = LHS for RHS = C with no definition
        if (isNamedRHS && addNonprimitiveDefinition(d, c, left)) {
            return;
        }
        // try to make a definition C = RHS for C [= D
        if (isNamedLHS && switchToNonprimitive(left, right)) {
            return;
        }
        // try to make a definition RHS = LHS for RHS = C with C [= D
        if (isNamedRHS && switchToNonprimitive(right, left)) {
            return;
        }
        // fail to make a concept definition; separate the definition
        this.addSubsumeAxiom(left.copy(), right.copy());
        this.addSubsumeAxiom(right, left);
    }

    /**
     * @param left
     *        left
     * @param right
     *        right
     * @param rightOrigin
     *        original right tree
     * @return true if definition is added
     */
    @PortedFrom(file = "dlTBox.h", name = "addNonprimitiveDefinition")
    public boolean addNonprimitiveDefinition(Concept left, @Nullable Concept right, DLTree rightOrigin) {
        // nothing to do for the case C := D for named concepts C,D with D = C
        // already
        if (right != null) {
            if (resolveSynonym(right).equals(left)) {
                return true;
            }
            // can't have C=D where C is a nominal and D is a concept
            if (left.isSingleton() && !right.isSingleton()) {
                return false;
            }
        }
        // check the case whether C=RHS or C [= \top
        if (left.canInitNonPrim(rightOrigin)) {
            makeNonPrimitive(left, rightOrigin);
            return true;
        }
        // can't make definition
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
        Concept ci = getCI(left);
        if (ci == null) {
            return false;
        }
        Concept c = resolveSynonym(ci);
        if (c.isTop() || c.isBottom()) {
            return false;
        }
        Concept ci2 = getCI(right);
        if (c.isSingleton() && ci2 != null) {
            if (!resolveSynonym(ci2).isSingleton()) {
                return false;
            }
        }
        if (config.getalwaysPreferEquals() && c.isPrimitive()) {
            addSubsumeForDefined(c, makeNonPrimitive(c, right));
            return true;
        }
        return false;
    }

    /**
     * transform definition C=D' with C [= E into C [= (D' and E) with D [= C.
     * <br>
     * D is usually D', but see addSubsumeForDefined()
     * 
     * @param c
     *        primitive
     * @param e
     *        superclass
     * @param d
     *        replacement
     */
    @PortedFrom(file = "dlTBox.h", name = "makeDefinitionPrimitive")
    void makeDefinitionPrimitive(Concept c, DLTree e, DLTree d) {
        // now we have C [= D'
        c.setPrimitive(true);
        // here C [= (D' and E)
        c.addDesc(e);
        c.initToldSubsumers();
        // all we need is to add (old C's desc)D [= C
        addSubsumeAxiom(d, getTree(c));
    }

    /**
     * @param beg
     *        beg
     */
    @PortedFrom(file = "dlTBox.h", name = "processDisjointC")
    public void processDisjointC(Collection<DLTree> beg) {
        List<DLTree> prim = new ArrayList<>();
        List<DLTree> rest = new ArrayList<>();
        for (DLTree d : beg) {
            if (d.isName() && ((Concept) d.elem().getNE()).isPrimitive()) {
                prim.add(d);
            } else {
                rest.add(d);
            }
        }
        // both primitive concept and others are in DISJ statement
        if (!prim.isEmpty() && !rest.isEmpty()) {
            DLTree nrest = DLTreeFactory.buildDisjAux(rest);
            prim.forEach(q -> this.addSubsumeAxiom(q, nrest));
        }
        // no primitive concepts between DJ elements
        if (!rest.isEmpty()) {
            processDisjoint(rest);
        }
        // all non-PC are done; prim is non-empty
        // FIXME!! do it in more optimal way later
        if (!prim.isEmpty()) {
            processDisjoint(prim);
        }
    }

    /**
     * @param l
     *        argument list
     */
    @PortedFrom(file = "dlTBox.h", name = "processEquivalentC")
    public void processEquivalentC(List<DLTree> l) {
        pairs(l, (a, b) -> addEqualityAxiom(a, b));
    }

    /**
     * @param l
     *        l
     */
    @PortedFrom(file = "dlTBox.h", name = "processDifferent")
    public void processDifferent(List<DLTree> l) {
        if (l.stream().anyMatch(i -> !isIndividual(i))) {
            // only nominals in DIFFERENT command
            throw new ReasonerInternalException("Only individuals allowed in processDifferent()");
        }
        List<Individual> acc = asList(l.stream().map(i -> (Individual) i.elem().getNE()));
        l.clear();
        // register vector of disjoint nominals in proper place
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
        if (l.stream().anyMatch(i -> !isIndividual(i))) {
            // only nominals in SAME command
            throw new ReasonerInternalException("Only individuals allowed in processSame()");
        }
        pairs(l, (a, b) -> addEqualityAxiom(a, b));
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
        // check that all ids are correct role names
        if (l.stream().anyMatch(DLTreeFactory::isTopRole)) {
            throw new ReasonerInternalException("Universal role in the disjoint roles axiom");
        }
        // make a disjoint roles
        List<Role> roles = asList(l.stream().map(Role::resolveRole));
        RoleMaster rm = getRM(roles.get(0));
        allPairs(roles, (a, b) -> rm.addDisjointRoles(a, b));
    }

    /**
     * @param l
     *        l
     */
    @PortedFrom(file = "dlTBox.h", name = "processEquivalentR")
    public void processEquivalentR(List<DLTree> l) {
        pairs(l, (a, b) -> addRoleSynonym(resolveRole(a), resolveRole(b)));
        l.clear();
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
        // FIXME!! find a proper place for this
        transformExtraSubsumptions();
        initToldSubsumers();
        transformToldCycles();
        transformSingletonHierarchy();
        absorbAxioms();
        setToldTop();
        buildDAG();
        fillsClassificationTag();
        calculateTSDepth();
        // set indexes for model caching
        setAllIndexes();
        determineSorts();
        gatherRelevanceInfo();
        // here it is safe to print KB features (all are known; the last one was
        // in Relevance)
        printFeatures();
        dlHeap.setOrderDefaults(isLikeGALEN ? "Fdn" : isLikeWINE ? "Sdp" : "Sap", isLikeGALEN ? "Ban"
            : isLikeWINE ? "Fdn" : "Dap");
        dlHeap.gatherStatistic();
        calculateStatistic();
        removeExtraDescriptions();
        pt.stop();
        preprocTime = pt.calcDelta();
        if (config.getverboseOutput()) {
            config.getLog().print(DONE_IN).print(pt.calcDelta()).print(SECONDS);
        }
    }

    /**
     * @param c
     *        concept
     * @param tree
     *        superclass
     * @param processed
     *        processed set
     * @return true if C is referenced in TREE; use PROCESSED to record explored
     *         names
     */
    @PortedFrom(file = "Preprocess.cpp", name = "isReferenced")
    boolean isReferenced(Concept c, DLTree tree, Set<Concept> processed) {
        assert tree != null;
        // names
        if (tree.isName()) {
            Concept d = (Concept) tree.elem().getNE();
            // check whether we found cycle
            if (c.equals(d)) {
                return true;
            }
            // check if we already processed D
            if (!processed.isEmpty()) {
                return false;
            }
            // recurse here
            return isReferenced(c, d, processed);
        }
        // operations with a single concept
        if (tree.getChildren().size() == 1) {
            return isReferenced(c, tree.getChild(), processed);
        }
        // operations w/o concept
        if (tree.token() == SELF || tree.isTOP() || tree.isBOTTOM()) {
            return false;
        }
        if (tree.isAND() || tree.token() == OR) {
            return tree.getChildren().stream().anyMatch(child -> isReferenced(c, child, processed));
        }
        // non-concept expressions: should not be here
        throw new UnreachableSituationException("cannot match the tree type");
    }

    /**
     * @param c
     *        concept
     * @param d
     *        superclass
     * @param processed
     *        processed
     * @return true if C is referenced in the definition of concept D; use
     *         PROCESSED to record explored names
     */
    boolean isReferenced(Concept c, Concept d, Set<Concept> processed) {
        // mark D as processed
        processed.add(d);
        // check the description of D
        DLTree description = d.getDescription();
        if (description == null) {
            return false;
        }
        if (isReferenced(c, description, processed)) {
            return true;
        }
        // we are done for primitive concepts
        if (d.isPrimitive()) {
            return false;
        }
        // check if D has an extra description
        DLTree p = extraConceptDefs.get(d);
        if (p != null) {
            return isReferenced(c, p, processed);
        }
        return false;
    }

    /** transform C [= E with C = D into GCIs */
    @PortedFrom(file = "Preprocess.cpp", name = "TransformExtraSubsumptions")
    void transformExtraSubsumptions() {
        Iterator<Map.Entry<Concept, DLTree>> it = extraConceptDefs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Concept, DLTree> p = it.next();
            Concept c = p.getKey();
            DLTree e = p.getValue();
            // for every C here we have C = D in KB and C [= E in ExtraDefs
            // if there is a cycle for C
            if (isCyclic(c)) {
                DLTree d = c.getDescription().copy();
                // then we should make C [= (D and E) and go with GCI D [= C
                makeDefinitionPrimitive(c, e, d);
            } else {
                // it is safe to keep definition C = D and go with GCI D [= E
                processGCI(getTree(c), e);
            }
            it.remove();
        }
        extraConceptDefs.clear();
    }

    /**
     * @param c
     *        concept
     * @return true iff C has a cyclic definition, ie is referenced in its own
     *         description
     */
    @PortedFrom(file = "Preprocess.cpp", name = "isCyclic")
    public boolean isCyclic(Concept c) {
        return isReferenced(c, c, new HashSet<Concept>());
    }

    @PortedFrom(file = "dlTBox.h", name = "setAllIndexes")
    private void setAllIndexes() {
        // place for the query concept
        ++nC;
        // start with 1 to make index 0 an indicator of "not processed"
        nR.set(1);
        Stream.concat(objectRoleMaster.getRoles().stream(), dataRoleMaster.getRoles().stream()).filter(r -> !r
            .isSynonym()).forEach(r -> r.setIndex(nR.getAndIncrement()));
    }

    @PortedFrom(file = "dlTBox.h", name = "replaceAllSynonyms")
    private void replaceAllSynonyms() {
        objectRoleMaster.getRoles().stream().filter(r -> !r.isSynonym()).forEach(r -> replaceSynonymsFromTree(r
            .getTDomain()));
        dataRoleMaster.getRoles().stream().filter(r -> !r.isSynonym()).forEach(r -> replaceSynonymsFromTree(r
            .getTDomain()));
        concepts.getConcepts().filter(p -> replaceSynonymsFromTree(p.getDescription())).forEach(p -> p
            .initToldSubsumers());
        individuals.getConcepts().filter(p -> replaceSynonymsFromTree(p.getDescription())).forEach(p -> p
            .initToldSubsumers());
    }

    /** preprocess related individuals */
    @PortedFrom(file = "dlTBox.h", name = "preprocessRelated")
    public void preprocessRelated() {
        relatedIndividuals.forEach(q -> q.simplify());
    }

    /** transform cycles */
    @PortedFrom(file = "dlTBox.h", name = "transformToldCycles")
    public void transformToldCycles() {
        // remember number of synonyms appeared in KB
        int nSynonyms = countSynonyms();
        clearRelevanceInfo();
        concepts.getConcepts().filter(p -> !p.isSynonym()).forEach(this::checkToldCycle);
        individuals.getConcepts().filter(p -> !p.isSynonym()).forEach(this::checkToldCycle);
        clearRelevanceInfo();
        // update nymber of synonyms
        nSynonyms = countSynonyms() - nSynonyms;
        if (nSynonyms > 0) {
            config.getLog().printTemplateInt(Templates.TRANSFORM_TOLD_CYCLES, nSynonyms);
            replaceAllSynonyms();
        }
    }

    /**
     * @param p
     *        _p
     * @return concpet with told cycle rewritten
     */
    @Nullable
    @PortedFrom(file = "dlTBox.h", name = "checkToldCycle")
    public Concept checkToldCycle(Concept p) {
        assert p != null;
        // resolve synonym (if happens) to prevent cases like A[=B[=C[=A,
        // A[=D[=B
        Concept cp = resolveSynonym(p);
        // no reason to process TOP here
        if (cp.isTop()) {
            return null;
        }
        // if we found a cycle...
        if (conceptInProcess.contains(cp)) {
            return cp;
        }
        if (cp.isRelevant(relevance)) {
            return null;
        }
        Concept ret = null;
        // add concept in processing
        conceptInProcess.add(cp);
        boolean redo = false;
        while (!redo) {
            redo = true;
            if (cp.hasToldSubsumers()) {
                for (ClassifiableEntry r : cp.getToldSubsumers()) {
                    // if cycle was detected
                    if ((ret = checkToldCycle((Concept) r)) != null) {
                        if (ret.equals(cp)) {
                            toldSynonyms.add(cp);
                            // find a representative for the cycle; nominal is
                            // preferable
                            for (Concept q : toldSynonyms) {
                                if (q.isSingleton()) {
                                    cp = q;
                                }
                            }
                            // now p is a representative for all the synonyms
                            // fill the description
                            Set<DLTree> leaves = new HashSet<>();
                            for (Concept q : toldSynonyms) {
                                if (!q.equals(cp)) {
                                    // make it a synonym of RET, save old desc
                                    DLTree d = makeNonPrimitive(q, getTree(cp));
                                    if (d.isBOTTOM()) {
                                        leaves.clear();
                                        leaves.add(d);
                                        break;
                                    } else {
                                        leaves.add(d);
                                    }
                                    // check whether we had an extra definition
                                    // for Q
                                    DLTree extra = extraConceptDefs.get(q);
                                    if (extra != null) {
                                        if (extra.isBOTTOM()) {
                                            leaves.clear();
                                            leaves.add(extra);
                                            break;
                                        } else {
                                            leaves.add(d);
                                        }
                                        extraConceptDefs.remove(q);
                                    }
                                }
                            }
                            toldSynonyms.clear();
                            // mark the returned concept primitive (to allow
                            // addDesc
                            // to work)
                            cp.setPrimitive(true);
                            cp.addLeaves(leaves);
                            // replace all synonyms with TOP
                            cp.removeSelfFromDescription();
                            // re-run the search starting from new sample
                            if (!ret.equals(cp)) {
                                // need to fix the stack
                                conceptInProcess.remove(ret);
                                conceptInProcess.add(cp);
                                ret.setRelevant(relevance);
                                cp.dropRelevant();
                            }
                            ret = null;
                            redo = false;
                            break;
                        } else {
                            toldSynonyms.add(cp);
                            redo = true;
                            // no need to continue; finish with this cycle first
                            break;
                        }
                    }
                }
            }
        }
        // remove processed concept from set
        conceptInProcess.remove(cp);
        cp.setRelevant(relevance);
        return ret;
    }

    /** transform singleton hierarchy */
    @PortedFrom(file = "dlTBox.h", name = "transformSingletonHierarchy")
    public void transformSingletonHierarchy() {
        // remember number of synonyms appeared in KB
        long nSynonyms = countSynonyms();
        // cycle until no new synonyms are created
        AtomicBoolean changed = new AtomicBoolean(false);
        do {
            changed.set(false);
            individuals.getConcepts().filter(pi -> !pi.isSynonym() && pi.isHasSP()).forEach(pi -> {
                transformSingletonWithSP(pi).removeSelfFromDescription();
                changed.set(true);
            });
        } while (changed.get());
        // update nymber of synonyms
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
        if (p.hasToldSubsumers()) {
            for (ClassifiableEntry r : p.getToldSubsumers()) {
                Concept i = (Concept) r;
                if (i.isSingleton()) {
                    // found the end of the chain
                    return (Individual) i;
                }
                if (i.isHasSP()) {
                    // found the continuation of the chain
                    return transformSingletonWithSP(i);
                }
            }
        }
        // will always have found the entry
        throw new UnreachableSituationException();
    }

    @PortedFrom(file = "dlTBox.h", name = "transformSingletonWithSP")
    private Individual transformSingletonWithSP(Concept p) {
        Individual i = getSPForConcept(p);
        // make p a synonym of i
        if (p.isSingleton()) {
            i.addRelated((Individual) p);
        }
        this.addSubsumeAxiom(i, makeNonPrimitive(p, getTree(i)));
        return i;
    }

    /** determine sorts */
    @PortedFrom(file = "dlTBox.h", name = "determineSorts")
    public void determineSorts() {
        if (config.isUseSortedReasoning()) {
            // Related individuals does not appears in DLHeap,
            // so their sorts shall be determined explicitely
            relatedIndividuals.forEach(p -> dlHeap.updateSorts(p.getA().getpName(), p.getRole(), p.getB().getpName()));
            // simple rules needs the same treatement
            simpleRules.forEach(q -> q.simpleRuleBody.forEach(r -> dlHeap.merge(dlHeap.get(q.bpHead).getSort(), r
                .getpName())));
            // create sorts for concept and/or roles
            dlHeap.determineSorts(objectRoleMaster, dataRoleMaster);
        }
    }

    /** calculate statistics */
 // Told stuff is used here, so run this AFTER fillTold*()
    @PortedFrom(file = "dlTBox.h", name = "CalculateStatistic")
    public void calculateStatistic() {
        AtomicInteger npFull = new AtomicInteger(), nsFull = new AtomicInteger();
        AtomicInteger nPC = new AtomicInteger(), nNC = new AtomicInteger(), nSing = new AtomicInteger();
        AtomicInteger nNoTold = new AtomicInteger();
        concepts.getConcepts().filter(p -> isValid(p.getpName())).forEach(n -> {
            if(n.isSingleton()) {
                nSing.incrementAndGet();
            }
            if (n.isPrimitive()) {
                nPC.incrementAndGet();
            } else {
                nNC.incrementAndGet();
            }
            if (n.isSynonym()) {
                nsFull.incrementAndGet();
            }
            if (n.isCompletelyDefined()) {
                if (n.isPrimitive()) {
                    npFull.incrementAndGet();
                }
            } else if (!n.hasToldSubsumers()) {
                nNoTold.incrementAndGet();
            }
        });
        individuals.getConcepts().filter(p -> isValid(p.getpName())).forEach(n -> {
            nSing.incrementAndGet();
            if (n.isPrimitive()) {
                nPC.incrementAndGet();
            } else {
                nNC.incrementAndGet();
            }
            if (n.isSynonym()) {
                nsFull.incrementAndGet();
            }
            if (n.isCompletelyDefined()) {
                if (n.isPrimitive()) {
                    npFull.incrementAndGet();
                }
            } else if (!n.hasToldSubsumers()) {
                nNoTold.incrementAndGet();
            }
        });
        config.getLog().print("There are ").print(nPC).print(" primitive concepts used\n of which ").print(npFull)
            .print(" completely defined\n      and ").print(nNoTold).print(" has no told subsumers\nThere are ").print(
                nNC).print(" non-primitive concepts used\n of which ").print(nsFull).print(" synonyms\nThere are ")
            .print(nSing).print(" individuals or nominals used\n");
    }

    /** remove extra descritpions */
    @PortedFrom(file = "dlTBox.h", name = "RemoveExtraDescriptions")
    public void removeExtraDescriptions() {
        concepts.getConcepts().forEach(pc -> pc.removeDescription());
        individuals.getConcepts().forEach(pi -> pi.removeDescription());
    }

    /** set To Do priorities using local OPTIONS */
    @Original
    public void setToDoPriorities() {
        stdReasoner.initToDoPriorities();
        if (nomReasoner != null) {
            nomReasoner.initToDoPriorities();
        }
    }

    /**
     * @param c
     *        C
     * @return true iff individual C is known to be p-blocked by another one
     */
    @PortedFrom(file = "dlTBox.h", name = "isBlockedInd")
    public boolean isBlockedInd(Concept c) {
        return sameIndividuals.containsKey(c);
    }

    /**
     * @param c
     *        C
     * @return individual that blocks C; works only for blocked individuals C
     */
    @PortedFrom(file = "dlTBox.h", name = "getBlockingInd")
    public Individual getBlockingInd(Concept c) {
        return sameIndividuals.get(c).first;
    }

    /**
     * @param c
     *        C
     * @return true iff an individual blocks C deterministically
     */
    @PortedFrom(file = "dlTBox.h", name = "isBlockingDet")
    public boolean isBlockingDet(Concept c) {
        return sameIndividuals.get(c).second;
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
        dlHeap.setCache(createBiPointer(p.getpName(), pos), new ModelCacheSingleton(createBiPointer(p.getIndex(),
            pos)));
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
        stdReasoner = new DlSatTester(this, config);
        if (nominalCloudFeatures.hasSingletons()) {
            nomReasoner = new NominalReasoner(this, config);
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
    public void setNameSigMap(Map<OWLEntity, Signature> p) {
        pName2Sig = p;
    }

    /**
     * @param c
     *        c
     * @return signature
     */
    @Nullable
    @Original
    public Signature getSignature(ClassifiableEntry c) {
        if (pName2Sig == null) {
            return null;
        }
        if (c.getEntity() == null) {
            return null;
        }
        return pName2Sig.get(c.getEntity());
    }

    /**
     * set relevance for a DLVertex
     * 
     * @param p
     *        _p
     */
    @PortedFrom(file = "dlTBox.h", name = "setRelevant")
    @SuppressWarnings("incomplete-switch")
    private void setRelevant(int p) {
        FastSet done = FastSetFactory.create();
        TIntLinkedList queue = new TIntLinkedList();
        queue.add(p);
        while (!queue.isEmpty()) {
            int nextP = queue.removeAt(0);
            if (done.contains(nextP)) {
                // skip cycles
                continue;
            }
            done.add(nextP);
            assert isValid(nextP);
            if (nextP == BP_TOP || nextP == BP_BOTTOM) {
                continue;
            }
            DLVertex v = realSetRelevant(nextP);
            DagTag dagtag = v.getType();
            Role vRole;
            switch (dagtag) {
                case DATATYPE:
                    // types and values are not relevant
                case DATAVALUE:
                case DATAEXPR:
                case NN:
                    // not appear in any expression => not relevant
                    break;
                case PCONCEPT:
                    // negated primitive entries -- does nothing
                case PSINGLETON:
                case NCONCEPT:
                    // named concepts
                case NSINGLETON:
                    Concept concept = (Concept) v.getConcept();
                    if (!concept.isRelevant(relevance)) {
                        ++nRelevantCCalls;
                        concept.setRelevant(relevance);
                        this.collectLogicFeature(concept);
                        queue.add(concept.getpBody());
                    }
                    break;
                case FORALL:
                case LE:
                    vRole = v.getRole();
                    List<Role> rolesToExplore = new LinkedList<>();
                    rolesToExplore.add(vRole);
                    while (!rolesToExplore.isEmpty()) {
                        Role roleToExplore = rolesToExplore.remove(0);
                        if ((roleToExplore.getId() != 0 || roleToExplore.isTop()) && !roleToExplore.isRelevant(
                            relevance)) {
                            roleToExplore.setRelevant(relevance);
                            this.collectLogicFeature(roleToExplore);
                            queue.add(roleToExplore.getBPDomain());
                            queue.add(roleToExplore.getBPRange());
                            rolesToExplore.addAll(roleToExplore.getAncestor());
                        }
                    }
                    queue.add(v.getConceptIndex());
                    break;
                case PROJ:
                    // no need to set (inverse) roles as it doesn't really
                    // matter
                case CHOOSE:
                    queue.add(v.getConceptIndex());
                    break;
                case IRR:
                    vRole = v.getRole();
                    List<Role> vRolesToExplore = new LinkedList<>();
                    vRolesToExplore.add(vRole);
                    while (!vRolesToExplore.isEmpty()) {
                        Role roleToExplore = vRolesToExplore.remove(0);
                        if (roleToExplore.getId() != 0 && !roleToExplore.isRelevant(relevance)) {
                            roleToExplore.setRelevant(relevance);
                            this.collectLogicFeature(roleToExplore);
                            queue.add(roleToExplore.getBPDomain());
                            queue.add(roleToExplore.getBPRange());
                            vRolesToExplore.addAll(roleToExplore.getAncestor());
                        }
                    }
                    break;
                case AND:
                case COLLECTION:
                    for (int q : v.begin()) {
                        queue.add(q);
                    }
                    break;
                default:
                    throw new ReasonerInternalException("Error setting relevant vertex of type " + dagtag);
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
        // gather GCIs features
        curFeature = gciFeatures;
        markGCIsRelevant();
        clearRelevanceInfo();
        kbFeatures.or(gciFeatures);
        // fills in nominal cloud relevance info
        nominalCloudFeatures = new LogicFeatures(gciFeatures);
        // set up relevance info
        individuals.getConcepts().forEach(pi -> {
            setConceptRelevant(pi);
            nominalCloudFeatures.or(pi.getPosFeatures());
        });
        // correct NC inverse role information
        if (nominalCloudFeatures.hasSomeAll() && !relatedIndividuals.isEmpty()) {
            nominalCloudFeatures.setInverseRoles();
        }
        concepts.getConcepts().forEach(this::setConceptRelevant);
        int bSize = dlHeap.size() - 2;
        curFeature = null;
        double bRatio = 0;//
        double sqBSize = 1;
        if (bSize > 20) {
            bRatio = (float) nRelevantBCalls / bSize;
            sqBSize = Math.sqrt(bSize);
        }
        // set up GALEN-like flag; based on r/n^{3/2}, add r/n^2<1
        isLikeGALEN = bRatio > sqBSize * 20 && bRatio < bSize;
        // switch off sorted reasoning iff top role appears
        if (kbFeatures.hasTopRole()) {
            config.setUseSortedReasoning(false);
        }
    }

    /** print features */
    @PortedFrom(file = "dlTBox.h", name = "printFeatures")
    public void printFeatures() {
        kbFeatures.writeState(config.getLog());
        config.getLog().print("KB contains ", gcis.isGCI() ? "" : "NO ", "GCIs\nKB contains ", gcis.isReflexive() ? ""
            : "NO ", "reflexive roles\nKB contains ", gcis.isRnD() ? "" : "NO ", "range and domain restrictions\n");
    }

    /**
     * @return list of list of different individuals
     */
    @Original
    public List<List<Individual>> getDifferent() {
        return differentIndividuals;
    }

    /**
     * @return list of relted individuals
     */
    @Original
    public List<Related> getRelatedI() {
        return relatedIndividuals;
    }

    /**
     * @return the dl heap
     */
    @Original
    public DLDag getDLHeap() {
        return dlHeap;
    }

    /**
     * @return the GCIs
     */
    @Original
    public KBFlags getGCIs() {
        return gcis;
    }

    /**
     * replace (AR:C) with X such that C [= AR^-:X for fresh X.
     * 
     * @param rc
     *        RC
     * @return X
     */
    @PortedFrom(file = "dlTBox.h", name = "replaceForall")
    public Concept replaceForall(DLTree rc) {
        // check whether we already did this before for given R,C
        if (forallRCCache.containsKey(rc)) {
            return forallRCCache.get(rc);
        }
        // see R and C at the first time
        Concept x = getAuxConcept(null);
        DLTree c = createSNFNot(rc.getRight().copy());
        // create ax axiom C [= AR^-.X
        DLTree forAll = createSNFForall(createInverse(rc.getLeft().copy()), getTree(x));
        config.getAbsorptionLog().print("\nReplaceForall: add").print(c).print(" [=").print(forAll);
        this.addSubsumeAxiom(c, forAll);
        // save cache for R,C
        forallRCCache.put(rc, x);
        return x;
    }

    /**
     * @return interrupted switch
     */
    @PortedFrom(file = "dlTBox.h", name = "isCancelled")
    public AtomicBoolean isCancelled() {
        return interrupted;
    }

    /**
     * @return list of fairness concepts
     */
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

    static class IterableElem<E> implements Serializable {

        private final List<E> elems;
        private final int pBeg;
        private final int pEnd;
        private int pCur;

        // / init c'tor
        IterableElem(List<E> init) {
            elems = init;
            pBeg = 0;
            pEnd = elems.size();
            pCur = 0;
            if (elems.isEmpty()) {
                throw new IllegalArgumentException("no empties allowed");
            }
        }

        public E getCur() {
            return elems.get(pCur);
        }

        public boolean next() {
            if (++pCur >= pEnd) {
                pCur = pBeg;
                return true;
            }
            return false;
        }
    }

    static class IterableVec<E> implements Serializable {

        private final List<IterableElem<E>> base = new ArrayList<>();

        IterableVec() {}

        public void clear() {
            base.clear();
        }

        /**
         * move I'th iterable forward; deal with end-case
         * 
         * @param i
         *        index
         * @return true if there is a next
         */
        public boolean next(int i) {
            if (base.get(i).next()) {
                return i == 0 ? true : next(i - 1);
            }
            return false;
        }

        /**
         * add a new iterable to a vec
         * 
         * @param it
         *        iterable to add
         */
        void add(IterableElem<E> it) {
            base.add(it);
        }

        /** @return next position */
        boolean next() {
            return next(base.size() - 1);
        }

        int size() {
            return base.size();
        }

        E get(int i) {
            return base.get(i).getCur();
        }
    }

    /**
     * @param mPlus
     *        MPlus
     * @param mMinus
     *        MMinus
     */
    public void reclassify(Set<OWLEntity> mPlus, Set<OWLEntity> mMinus) {
        pTaxCreator.reclassify(mPlus, mMinus);
        status = KBREALISED;
        // FIXME!! check whether it is classified/realised
    }

    /**
     * @return list of concept index
     */
    public TIntList getConceptsForQueryAnswering() {
        return conceptsForQueryAnswering;
    }

    /**
     * @return true if in classification
     */
    public boolean isDuringClassification() {
        return duringClassification;
    }

    /**
     * @return individuals
     */
    public IterableVec<Individual> getIV() {
        return iv;
    }

    /**
     * @return skip before block
     */
    public int getnSkipBeforeBlock() {
        return config.getnSkipBeforeBlock();
    }
}
