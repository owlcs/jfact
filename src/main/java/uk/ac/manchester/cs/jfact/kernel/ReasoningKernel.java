package uk.ac.manchester.cs.jfact.kernel;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.verifyNotNull;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.add;
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.DLTree.equalTrees;
import static uk.ac.manchester.cs.jfact.helpers.Helper.anyMatchOnAllPairs;
import static uk.ac.manchester.cs.jfact.kernel.CacheStatus.*;
import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.*;
import static uk.ac.manchester.cs.jfact.kernel.KBStatus.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapitools.decomposition.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposition;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecompositionImpl;
import uk.ac.manchester.cs.jfact.KnowledgeExplorer;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.datatypes.LiteralEntry;
import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.Timer;
import uk.ac.manchester.cs.jfact.kernel.actors.Actor;
import uk.ac.manchester.cs.jfact.kernel.actors.RIActor;
import uk.ac.manchester.cs.jfact.kernel.actors.SupConceptActor;
import uk.ac.manchester.cs.jfact.kernel.actors.TaxonomyActor;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptTop;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

/** Reasoning kernel */
@PortedFrom(file = "Kernel.h", name = "ReasoningKernel")
public class ReasoningKernel implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReasoningKernel.class);
    private static final String ROLE_EXPRESSION_EXPECTED = "Role expression expected in isDisjointRoles()";
    private static final String ROLE_EXPECTED = "Role expression expected in getNeighbours() method";
    /** options for the kernel and all related substructures */
    @PortedFrom(file = "Kernel.h", name = "KernelOptions") private final JFactReasonerConfiguration kernelOptions;
    /** local TBox (to be created) */
    @PortedFrom(file = "Kernel.h", name = "pTBox") protected TBox pTBox;
    /** set of axioms */
    @PortedFrom(file = "Kernel.h", name = "Ontology") private final Ontology ontology = new Ontology();
    /** expression translator to work with queries */
    @PortedFrom(file = "Kernel.h", name = "pET") private ExpressionTranslator pET;
    @PortedFrom(file = "Kernel.h", name = "Name2Sig") private final Map<OWLEntity, Signature> name2Sig = new HashMap<>();
    /** ontology signature (used in incremental) */
    @PortedFrom(file = "Kernel.h", name = "OntoSig") private Collection<NamedEntity> ontoSig;
    // values to propagate to the new KB in case of clearance
    @Original private AtomicBoolean interrupted;
    // reasoning cache
    /** cache level */
    @PortedFrom(file = "Kernel.h", name = "cacheLevel") private CacheStatus cacheLevel;
    /** cached query concept description */
    @PortedFrom(file = "Kernel.h", name = "cachedQueryTree") private DLTree cachedQueryTree;
    /** cached concept (either defConcept or existing one) */
    @PortedFrom(file = "Kernel.h", name = "cachedConcept") private Concept cachedConcept;
    /** cached query result (taxonomy position) */
    @PortedFrom(file = "Kernel.h", name = "cachedVertex") private TaxonomyVertex cachedVertex;
    // internal flags
    /** set if TBox throws an exception during preprocessing/classification */
    @PortedFrom(file = "Kernel.h", name = "reasoningFailed") private boolean reasoningFailed = false;
    /** trace vector for the last operation (set from the TBox trace-sets) */
    @PortedFrom(file = "Kernel.h", name = "TraceVec") private final List<AxiomWrapper> traceVec = new ArrayList<>();
    /** flag to gather trace information for the next reasoner's call */
    @PortedFrom(file = "Kernel.h", name = "NeedTracing") private boolean needTracing = false;
    @Original private final DatatypeFactory datatypeFactory;
    // types for knowledge exploration
    /** knowledge exploration support */
    @PortedFrom(file = "Kernel.h", name = "KE") private KnowledgeExplorer ke;
    /** atomic decomposer */
    @PortedFrom(file = "Kernel.h", name = "AD") private AtomicDecomposition ad;
    /** syntactic locality based module extractor */
    @PortedFrom(file = "Kernel.h", name = "ModSyn") private Decomposer modSyn = null;
    /** semantic locality based module extractor */
    @PortedFrom(file = "Kernel.h", name = "ModSem") private Decomposer modSem = null;
    /** set to return by the locality checking procedure */
    @PortedFrom(file = "Kernel.h", name = "Result") private final Set<AxiomWrapper> result = new HashSet<>();
    /** cached query input description */
    @PortedFrom(file = "Kernel.h", name = "cachedQuery") private ConceptExpression cachedQuery;
    /** ignore cache for the TExpr* (useful for semantic AD) */
    @PortedFrom(file = "Kernel.h", name = "ignoreExprCache") private boolean ignoreExprCache = false;
    private final Timer moduleTimer = new Timer();
    private OWLDataFactory df;

    /**
     * @param conf
     *        conf
     * @param factory
     *        factory
     * @param df
     *        data factory
     */
    public ReasoningKernel(JFactReasonerConfiguration conf, DatatypeFactory factory, OWLDataFactory df) {
        // should be commented
        cachedQuery = null;
        cachedQueryTree = null;
        kernelOptions = conf;
        datatypeFactory = factory;
        pTBox = null;
        pET = null;
        cachedQuery = null;
        this.df = df;
        initCacheAndFlags();
    }

    /**
     * @param b
     *        b
     */
    @Original
    public void setInterruptedSwitch(AtomicBoolean b) {
        interrupted = b;
    }

    // -- internal query cache manipulation
    /** clear query cache */
    @PortedFrom(file = "Kernel.h", name = "clearQueryCache")
    private void clearQueryCache() {
        cachedQuery = null;
        cachedQueryTree = null;
    }

    /**
     * set query cache value to QUERY
     * 
     * @param query
     *        query
     */
    @PortedFrom(file = "Kernel.h", name = "setQueryCache")
    private void setQueryCache(ConceptExpression query) {
        clearQueryCache();
        cachedQuery = query;
    }

    /**
     * set query cache value to QUERY
     * 
     * @param query
     *        query
     */
    @PortedFrom(file = "Kernel.h", name = "setQueryCache")
    private void setQueryCache(DLTree query) {
        clearQueryCache();
        cachedQueryTree = query;
    }

    /**
     * choose whether TExpr cache should be ignored
     * 
     * @param value
     *        value
     */
    @PortedFrom(file = "Kernel.h", name = "setIgnoreExprCache")
    public void setIgnoreExprCache(boolean value) {
        ignoreExprCache = value;
    }

    /**
     * check whether query cache is the same as QUERY
     * 
     * @param query
     *        query
     * @return true if same
     */
    @PortedFrom(file = "Kernel.h", name = "checkQueryCache")
    private boolean checkQueryCache(ConceptExpression query) {
        return ignoreExprCache ? false : query.equals(cachedQuery);
    }

    /**
     * check whether query cache is the same as QUERY
     * 
     * @param query
     *        query
     * @return true if same
     */
    @PortedFrom(file = "Kernel.h", name = "checkQueryCache")
    private boolean checkQueryCache(DLTree query) {
        return equalTrees(cachedQueryTree, query);
    }

    /** @return status of the KB */
    @PortedFrom(file = "Kernel.h", name = "getStatus")
    private KBStatus getStatus() {
        if (pTBox == null) {
            return KBEMPTY;
        }
        // if the ontology is changed, it needs to be reclassified
        if (ontology.isChanged()) {
            return KBLOADING;
        }
        return pTBox.getStatus();
    }

    /**
     * @param expr
     *        expr
     * @return get DLTree corresponding to an expression EXPR
     */
    @PortedFrom(file = "Kernel.h", name = "e")
    public DLTree e(Expression expr) {
        return expr.accept(pET);
    }

    /**
     * @return fresh filled depending of a type of R
     * @param r
     *        R
     */
    @PortedFrom(file = "Kernel.h", name = "getFreshFiller")
    private DLTree getFreshFiller(Role r) {
        if (r.isDataRole()) {
            LiteralEntry t = new LiteralEntry("freshliteral");
            t.setLiteral(DatatypeFactory.LITERAL.buildLiteral("freshliteral"));
            return DLTreeFactory.wrap(t);
        } else {
            return getTBox().getFreshConcept();
        }
    }

    /**
     * @return role expression based on the R
     * @param r
     *        R
     */
    @PortedFrom(file = "Kernel.h", name = "Role")
    private RoleExpression role(Role r) {
        if (r.isDataRole()) {
            return getExpressionManager().dataRole(r.getEntity().getEntity());
        } else {
            return getExpressionManager().objectRole(r.getEntity().getEntity());
        }
    }

    /** clear cache and flags */
    @PortedFrom(file = "Kernel.h", name = "initCacheAndFlags")
    private void initCacheAndFlags() {
        cacheLevel = EMPTY;
        clearQueryCache();
        cachedConcept = null;
        cachedVertex = null;
        needTracing = false;
    }

    /** set need tracing to true */
    @PortedFrom(file = "Kernel.h", name = "needTracing")
    public void needTracing() {
        needTracing = true;
    }

    /** @return the trace-set of the last reasoning operation */
    @PortedFrom(file = "Kernel.h", name = "getTrace")
    public Stream<AxiomWrapper> getTrace() {
        List<AxiomWrapper> toReturn = new ArrayList<>(traceVec);
        traceVec.clear();
        return toReturn.stream();
    }

    /**
     * set the signature of the expression translator
     * 
     * @param sig
     *        sig
     */
    @PortedFrom(file = "Kernel.h", name = "setSignature")
    public void setSignature(Signature sig) {
        if (pET != null) {
            pET.setSignature(sig);
        }
    }

    /** @return the ontology */
    @PortedFrom(file = "Kernel.h", name = "getOntology")
    public Ontology getOntology() {
        return ontology;
    }

    /**
     * @return related cache for an individual I
     * @param i
     *        I
     * @param r
     *        R
     */
    @PortedFrom(file = "Kernel.h", name = "getRelated")
    private List<Individual> getRelated(Individual i, Role r) {
        if (!i.hasRelatedCache(r)) {
            i.setRelatedCache(r, buildRelatedCache(i, r));
        }
        return i.getRelatedCache(r);
    }

    // -- internal reasoning methods
    /**
     * @param c
     *        C
     * @return true iff C is satisfiable
     */
    @PortedFrom(file = "Kernel.h", name = "checkSatTree")
    private boolean checkSatTree(DLTree c) {
        if (c.isTOP()) {
            return true;
        }
        if (c.isBOTTOM()) {
            return false;
        }
        this.setUpCache(c, SAT);
        return getTBox().isSatisfiable(cachedConcept);
    }

    /**
     * @param c
     *        C
     * @return true iff C is satisfiable
     */
    @PortedFrom(file = "Kernel.h", name = "checkSat")
    private boolean checkSat(ConceptExpression c) {
        setUpCache(c, SAT);
        return getTBox().isSatisfiable(cachedConcept);
    }

    /**
     * helper;
     * 
     * @param c
     *        C
     * @return true iff C is either named concept of Top/Bot
     */
    @PortedFrom(file = "Kernel.h", name = "isNameOrConst")
    private static boolean isNameOrConst(ConceptExpression c) {
        return c instanceof ConceptName || c instanceof ConceptTop || c instanceof ConceptBottom;
    }

    @PortedFrom(file = "Kernel.h", name = "isNameOrConst")
    private static boolean isNameOrConst(DLTree c) {
        return c.isBOTTOM() || c.isTOP() || c.isName();
    }

    /**
     * @param c
     *        C
     * @param d
     *        D
     * @return true iff C [= D holds
     */
    @PortedFrom(file = "Kernel.h", name = "checkSub")
    private boolean checkSub(ConceptExpression c, ConceptExpression d) {
        if (isNameOrConst(d) && isNameOrConst(c)) {
            return this.checkSub(getTBox().getCI(e(c)), getTBox().getCI(e(d)));
        }
        return !checkSat(and(c, not(d)));
    }

    /**
     * @param useSemantic
     *        useSemantic
     * @param r
     *        reasoner
     * @return module extractor
     */
    @PortedFrom(file = "Kernel.h", name = "getModExtractor")
    public Decomposer getModExtractor(boolean useSemantic, OWLReasoner r) {
        if (useSemantic) {
            if (modSem == null) {
                modSem = new Decomposer(ontology.getAxioms(), new SemanticLocalityChecker(r));
            }
            return modSem;
        }
        if (modSyn == null) {
            modSyn = new Decomposer(ontology.getAxioms(), new SyntacticLocalityChecker());
        }
        return modSyn;
    }

    /**
     * @param signature
     *        signature
     * @param useSemantic
     *        useSemantic
     * @param type
     *        type
     * @param r
     *        reasoner
     * @return a set of axioms that corresponds to the atom with the id INDEX
     */
    @PortedFrom(file = "Kernel.h", name = "getModule")
    public Collection<AxiomWrapper> getModule(List<Expression> signature, boolean useSemantic, ModuleType type,
        OWLReasoner r) {
        // init signature
        Signature sig = new Signature();
        sig.setLocality(false);
        signature.stream().filter(p -> p instanceof NamedEntity).map(p -> ((NamedEntity) p).getEntity()).forEach(
            sig::add);
        return getModExtractor(useSemantic, r).getModule(sig.getSignature().stream(), useSemantic, type);
    }

    /**
     * @param signature
     *        signature
     * @param useSemantic
     *        useSemantic
     * @param type
     *        type
     * @param r
     *        reasoner
     * @return a set of axioms that corresponds to the atom with the id INDEX
     */
    @PortedFrom(file = "Kernel.h", name = "getNonLocal")
    public Set<AxiomWrapper> getNonLocal(List<Expression> signature, boolean useSemantic, ModuleType type,
        OWLReasoner r) {
        // init signature
        Signature sig = new Signature();
        sig.setLocality(type == ModuleType.TOP);
        signature.stream().filter(p -> p instanceof NamedEntity).map(p -> ((NamedEntity) p).getEntity()).forEach(
            sig::add);
        // do check
        LocalityChecker lc = getModExtractor(useSemantic, r).getModularizer().getLocalityChecker();
        lc.setSignatureValue(sig);
        result.clear();
        add(result, getOntology().getAxioms().stream().filter(p -> !lc.local(p.getAxiom())));
        return result;
    }

    /**
     * @param c
     *        C
     * @param d
     *        D
     * @return true iff C [= D holds
     */
    @PortedFrom(file = "Kernel.h", name = "checkSub")
    private boolean checkSub(Concept c, Concept d) {
        // check whether a concept is fresh
        if (d.getpName() == 0) // D is fresh
        {
            if (c.getpName() == 0) {
                return c.equals(d);
                // 2 fresh concepts subsumes one another iff they are the same
            } else {
                // C is known
                return !getTBox().isSatisfiable(c);
                // C [= D iff C=\bottom
            }
        } else {
            // D is known
            if (c.getpName() == 0) {
                // C [= D iff D = \top, or ~D = \bottom
                return !checkSatTree(DLTreeFactory.createSNFNot(getTBox().getTree(c)));
            }
        }
        // here C and D are known (not fresh)
        // check the obvious ones
        if (d.isTop() || c.isBottom()) {
            return true;
        }
        if (getStatus().ordinal() < KBCLASSIFIED.ordinal()) {
            // unclassified => do via SAT test
            return getTBox().isSubHolds(c, d);
        }
        // classified => do the taxonomy traversal
        SupConceptActor actor = new SupConceptActor(d);
        Taxonomy tax = getCTaxonomy();
        return tax.getRelativesInfo(c.getTaxVertex(), actor, true, false, true);
    }

    /**
     * @throws ReasonerInternalException
     *         exception if no TBox found
     */
    @PortedFrom(file = "Kernel.h", name = "checkTBox")
    private void checkTBox() {
        if (pTBox == null) {
            throw new ReasonerInternalException("KB Not Initialised");
        }
    }

    /** @return get RW access to TBox */
    @PortedFrom(file = "Kernel.h", name = "getTBox")
    public TBox getTBox() {
        checkTBox();
        return pTBox;
    }

    /** clear TBox and related structures; keep ontology in place */
    @PortedFrom(file = "Kernel.h", name = "clearTBox")
    private void clearTBox() {
        pTBox = null;
        pET = null;
        getExpressionManager().clearNameCache();
    }

    /** @return Object RoleMaster from TBox */
    @PortedFrom(file = "Kernel.h", name = "getORM")
    private RoleMaster getORM() {
        return getTBox().getORM();
    }

    /** @return Data RoleMaster from TBox */
    @PortedFrom(file = "Kernel.h", name = "getDRM")
    private RoleMaster getDRM() {
        return getTBox().getDRM();
    }

    /** @return concept hierarchy */
    @PortedFrom(file = "Kernel.h", name = "getCTaxonomy")
    private Taxonomy getCTaxonomy() {
        return getTBox().getTaxonomy();
    }

    /** @return object role hierarchy */
    @PortedFrom(file = "Kernel.h", name = "getORTaxonomy")
    private Taxonomy getORTaxonomy() {
        if (!isKBPreprocessed()) {
            throw new ReasonerInternalException("No access to the object role taxonomy: ontology not preprocessed");
        }
        return getORM().getTaxonomy();
    }

    /** @return data role hierarchy */
    @PortedFrom(file = "Kernel.h", name = "getDRTaxonomy")
    private Taxonomy getDRTaxonomy() {
        if (!isKBPreprocessed()) {
            throw new ReasonerInternalException("No access to the data role taxonomy: ontology not preprocessed");
        }
        return getDRM().getTaxonomy();
    }

    // transformation methods
    /**
     * @return individual by the TIndividualExpr
     * @param i
     *        i
     * @param reason
     *        reason
     */
    @PortedFrom(file = "Kernel.h", name = "getIndividual")
    private Individual getIndividual(IndividualExpression i, String reason) {
        return (Individual) verifyNotNull(getTBox().getCI(verifyNotNull(e(i), reason)));
    }

    /**
     * @return role by the TRoleExpr
     * @param r
     *        r
     * @param reason
     *        reason
     */
    @PortedFrom(file = "Kernel.h", name = "getRole")
    private Role getRole(RoleExpression r, String reason) {
        return Role.resolveRole(e(r), reason);
    }

    /**
     * @return taxonomy of the property wrt it's name
     * @param r
     *        R
     */
    @PortedFrom(file = "Kernel.h", name = "getTaxonomy")
    private Taxonomy getTaxonomy(Role r) {
        return r.isDataRole() ? getDRTaxonomy() : getORTaxonomy();
    }

    /**
     * @return taxonomy vertext of the property wrt it's name
     * @param r
     *        r
     */
    @PortedFrom(file = "Kernel.h", name = "getTaxVertex")
    private static TaxonomyVertex getTaxVertex(Role r) {
        return r.getTaxVertex();
    }

    /** @return kernel configuration */
    @PortedFrom(file = "Kernel.h", name = "getOptions")
    public JFactReasonerConfiguration getOptions() {
        return kernelOptions;
    }

    /** @return classification status of KB */
    @PortedFrom(file = "Kernel.h", name = "isKBPreprocessed")
    public boolean isKBPreprocessed() {
        return getStatus().ordinal() >= KBCHECKED.ordinal();
    }

    /** @return classification status of KB */
    @PortedFrom(file = "Kernel.h", name = "isKBClassified")
    public boolean isKBClassified() {
        return getStatus().ordinal() >= KBCLASSIFIED.ordinal();
    }

    /** @return realistion status of KB */
    @PortedFrom(file = "Kernel.h", name = "isKBRealised")
    public boolean isKBRealised() {
        return getStatus().ordinal() >= KBREALISED.ordinal();
    }

    /**
     * dump query processing TIME, reasoning statistics and a (preprocessed)
     * TBox
     * 
     * @param time
     *        time
     */
    @PortedFrom(file = "Kernel.h", name = "writeReasoningResult")
    public void writeReasoningResult(long time) {
        // get rid of the query leftovers
        getTBox().clearQueryConcept();
        getTBox().writeReasoningResult(time);
    }

    // helper methods to query properties of roles
    /**
     * @param r
     *        r
     * @return true if r is functional wrt ontology
     */
    @PortedFrom(file = "Kernel.h", name = "checkFunctionality")
    private boolean checkFunctionality(Role r) {
        // r is transitive iff \ER.C and \ER.\not C is unsatisfiable
        DLTree tmp = DLTreeFactory.createSNFExists(DLTreeFactory.createRole(r).copy(), DLTreeFactory.createSNFNot(
            getFreshFiller(r)));
        tmp = DLTreeFactory.createSNFAnd(tmp, DLTreeFactory.createSNFExists(DLTreeFactory.createRole(r), getFreshFiller(
            r)));
        return !checkSatTree(tmp);
    }

    /**
     * @param r
     *        r
     * @return true if r is functional; set the value for r if necessary
     */
    @PortedFrom(file = "Kernel.h", name = "getFunctionality")
    private boolean getFunctionality(Role r) {
        if (!r.isFunctionalityKnown()) {
            r.setFunctional(checkFunctionality(r));
        }
        return r.isFunctional();
    }

    /**
     * @param r
     *        R
     * @return true if R is transitive wrt ontology
     */
    @PortedFrom(file = "Kernel.h", name = "checkTransitivity")
    private boolean checkTransitivity(DLTree r) {
        // R is transitive iff \ER.\ER.C and \AR.\not C is unsatisfiable
        DLTree tmp = DLTreeFactory.createSNFExists(r.copy(), DLTreeFactory.createSNFNot(getTBox().getFreshConcept()));
        tmp = DLTreeFactory.createSNFExists(r.copy(), tmp);
        tmp = DLTreeFactory.createSNFAnd(tmp, DLTreeFactory.createSNFForall(r, getTBox().getFreshConcept()));
        return !checkSatTree(tmp);
    }

    /**
     * @param r
     *        R
     * @return true if R is symmetric wrt ontology
     */
    @PortedFrom(file = "Kernel.h", name = "checkSymmetry")
    private boolean checkSymmetry(DLTree r) {
        // R is symmetric iff C and \ER.\AR.(not C) is unsatisfiable
        DLTree tmp = DLTreeFactory.createSNFForall(r.copy(), DLTreeFactory.createSNFNot(getTBox().getFreshConcept()));
        tmp = DLTreeFactory.createSNFAnd(getTBox().getFreshConcept(), DLTreeFactory.createSNFExists(r, tmp));
        return !checkSatTree(tmp);
    }

    /**
     * @param r
     *        R
     * @return true if R is reflexive wrt ontology
     */
    @PortedFrom(file = "Kernel.h", name = "checkReflexivity")
    private boolean checkReflexivity(DLTree r) {
        // R is reflexive iff C and \AR.(not C) is unsatisfiable
        DLTree tmp = DLTreeFactory.createSNFForall(r, DLTreeFactory.createSNFNot(getTBox().getFreshConcept()));
        tmp = DLTreeFactory.createSNFAnd(getTBox().getFreshConcept(), tmp);
        return !checkSatTree(tmp);
    }

    @PortedFrom(file = "Kernel.h", name = "checkRoleSubsumption")
    private boolean checkRoleSubsumption(Role r, Role s) {
        if (r.isDataRole() != s.isDataRole()) {
            return false;
        }
        // R [= S iff \ER.C and \AS.(not C) is unsatisfiable
        DLTree tmp = DLTreeFactory.createSNFForall(DLTreeFactory.createRole(s), DLTreeFactory.createSNFNot(
            getFreshFiller(s)));
        tmp = DLTreeFactory.createSNFAnd(DLTreeFactory.createSNFExists(DLTreeFactory.createRole(r), getFreshFiller(r)),
            tmp);
        return !checkSatTree(tmp);
    }

    /** @return expression manager */
    @PortedFrom(file = "Kernel.h", name = "getExpressionManager")
    public ExpressionCache getExpressionManager() {
        return ontology.getExpressionManager();
    }

    /**
     * create new KB
     * 
     * @return false if new tbox was created
     */
    @PortedFrom(file = "Kernel.h", name = "newKB")
    private boolean newKB() {
        if (pTBox != null) {
            return true;
        }
        pTBox = new TBox(datatypeFactory, getOptions(), interrupted, df);
        pET = new ExpressionTranslator(pTBox);
        initCacheAndFlags();
        return false;
    }

    /**
     * delete existing KB
     * 
     * @return false
     */
    @PortedFrom(file = "Kernel.h", name = "releaseKB")
    private boolean releaseKB() {
        clearTBox();
        ontology.clear();
        // the new KB is coming so the failures of the previous one doesn't
        // matter
        reasoningFailed = false;
        return false;
    }

    /**
     * reset current KB
     * 
     * @return true if no new tbox is created
     */
    @PortedFrom(file = "Kernel.h", name = "clearKB")
    public boolean clearKB() {
        if (pTBox == null) {
            return true;
        }
        return releaseKB() || newKB();
    }

    // * ASK part
    /*
     * Before execution of any query the Kernel make sure that the KB is in an
     * appropriate state: Preprocessed, Classified or Realised. If the ontology
     * was changed between asks, incremental classification is performed and the
     * corrected result is returned.
     */
    /** @return consistency status of KB */
    @PortedFrom(file = "Kernel.h", name = "isKBConsistent")
    public boolean isKBConsistent() {
        if (getStatus().ordinal() <= KBLOADING.ordinal()) {
            processKB(KBCHECKED);
        }
        return getTBox().isConsistent();
    }

    /** ensure that KB is preprocessed/consistence checked */
    @PortedFrom(file = "Kernel.h", name = "preprocessKB")
    private void preprocessKB() {
        isKBConsistent();
    }

    /** ensure that KB is classified */
    @PortedFrom(file = "Kernel.h", name = "classifyKB")
    public void classifyKB() {
        if (!isKBClassified()) {
            processKB(KBCLASSIFIED);
        }
        isKBConsistent();
    }

    /** ensure that KB is realised */
    @PortedFrom(file = "Kernel.h", name = "realiseKB")
    public void realiseKB() {
        if (!isKBRealised()) {
            processKB(KBREALISED);
        }
        if (!isKBConsistent()) {
            throw new InconsistentOntologyException("Ontology is inconsistent");
        }
    }

    // role info retrieval
    /**
     * @param r
     *        R
     * @return true iff object role is functional
     */
    @PortedFrom(file = "Kernel.h", name = "isFunctional")
    public boolean isFunctional(ObjectRoleExpression r) {
        // ensure KB is ready to answer the query
        preprocessKB();
        Role role = getRole(r, "Role expression expected in isFunctional()");
        if (role.isTop()) {
            // universal role is not functional
            return false;
        }
        if (role.isBottom()) {
            // empty role is functional
            return true;
        }
        return getFunctionality(role);
    }

    /**
     * @param r
     *        R
     * @return true iff data role is functional
     */
    @PortedFrom(file = "Kernel.h", name = "isFunctional")
    public boolean isFunctional(DataRoleExpression r) {
        // ensure KB is ready to answer the query
        preprocessKB();
        Role role = getRole(r, "Role expression expected in isFunctional()");
        if (role.isTop()) {
            // universal role is not functional
            return false;
        }
        if (role.isBottom()) {
            // empty role is functional
            return true;
        }
        return getFunctionality(role);
    }

    /**
     * @param r
     *        R
     * @return true iff role is inverse-functional
     */
    @PortedFrom(file = "Kernel.h", name = "isInverseFunctional")
    public boolean isInverseFunctional(ObjectRoleExpression r) {
        // ensure KB is ready to answer the query
        preprocessKB();
        Role role = getRole(r, "Role expression expected in isInverseFunctional()").inverse();
        if (role.isTop()) {
            // universal role is not functional
            return false;
        }
        if (role.isBottom()) {
            // empty role is functional
            return true;
        }
        return getFunctionality(role);
    }

    /**
     * @param r
     *        R
     * @return true iff role is transitive
     */
    @PortedFrom(file = "Kernel.h", name = "isTransitive")
    public boolean isTransitive(ObjectRoleExpression r) {
        // ensure KB is ready to answer the query
        preprocessKB();
        Role role = getRole(r, "Role expression expected in isTransitive()");
        if (role.isTop()) {
            // universal role is transitive
            return true;
        }
        // empty role is transitive
        if (role.isBottom()) {
            return true;
        }
        if (!role.isTransitivityKnown()) {
            // calculate transitivity
            role.setTransitive(checkTransitivity(e(r)));
        }
        return role.isTransitive();
    }

    /**
     * @param r
     *        R
     * @return true iff role is symmetric
     */
    @PortedFrom(file = "Kernel.h", name = "isSymmetric")
    public boolean isSymmetric(ObjectRoleExpression r) {
        preprocessKB();
        Role role = getRole(r, "Role expression expected in isSymmetric()");
        if (role.isTop()) {
            return true;
        }
        if (role.isBottom()) {
            return true;
        }
        if (!role.isSymmetryKnown()) {
            role.setSymmetric(checkSymmetry(e(r)));
        }
        return role.isSymmetric();
    }

    /**
     * @param r
     *        R
     * @return true iff role is asymmetric
     */
    @PortedFrom(file = "Kernel.h", name = "isAsymmetric")
    public boolean isAsymmetric(ObjectRoleExpression r) {
        // ensure KB is ready to answer the query
        preprocessKB();
        Role role = getRole(r, "Role expression expected in isAsymmetric()");
        if (role.isTop()) {
            // universal role is not asymmetric
            return false;
        }
        if (role.isBottom()) {
            // empty role is asymmetric
            return true;
        }
        // calculate asymmetry
        if (!role.isAsymmetryKnown()) {
            role.setAsymmetric(getTBox().isDisjointRoles(role, role.inverse()));
        }
        return role.isAsymmetric();
    }

    /**
     * @param r
     *        R
     * @return true iff role is reflexive
     */
    @PortedFrom(file = "Kernel.h", name = "isReflexive")
    public boolean isReflexive(ObjectRoleExpression r) {
        preprocessKB();
        Role role = getRole(r, "Role expression expected in isReflexive()");
        if (role.isTop()) {
            // universal role is reflexive
            return true;
        }
        if (role.isBottom()) {
            // empty role is not reflexive
            return false;
        }
        if (!role.isReflexivityKnown()) {
            // calculate reflexivity
            role.setReflexive(checkReflexivity(e(r)));
        }
        return role.isReflexive();
    }

    /**
     * @param r
     *        R
     * @return true iff role is irreflexive
     */
    @PortedFrom(file = "Kernel.h", name = "isIrreflexive")
    public boolean isIrreflexive(ObjectRoleExpression r) {
        preprocessKB();
        Role role = getRole(r, "Role expression expected in isIrreflexive()");
        if (role.isTop()) {
            // universal role is not irreflexive
            return false;
        }
        if (role.isBottom()) {
            // empty role is irreflexive
            return true;
        }
        if (!role.isIrreflexivityKnown()) {
            role.setIrreflexive(getTBox().isIrreflexive(role));
        }
        return role.isIrreflexive();
    }

    // all-disjoint query implementation
    /**
     * @param l
     *        l
     * @return true if disjoint
     */
    @PortedFrom(file = "Kernel.h", name = "isDisjointRoles")
    public boolean isDisjointRoles(List<? extends RoleExpression> l) {
        // grab all roles from the arg-list
        int nTopRoles = 0;
        List<Role> roles = new ArrayList<>(l.size());
        for (RoleExpression p : l) {
            uk.ac.manchester.cs.jfact.kernel.Role role = getRole(p, ROLE_EXPRESSION_EXPECTED);
            // empty role is disjoint with everything
            if (!role.isBottom()) {
                if (role.isTop()) {
                    // count universal roles
                    ++nTopRoles;
                } else {
                    roles.add(role);
                }
            }
        }
        // deal with top-roles
        if (nTopRoles > 0) {
            // universal role is not disjoint with anything but the bottom role
            return !(nTopRoles > 1 || !roles.isEmpty());
        }
        // test pair-wise disjointness
        return !anyMatchOnAllPairs(roles, v -> !getTBox().isDisjointRoles(v.i, v.j));
    }

    /**
     * @param or
     *        R
     * @param os
     *        S
     * @return true iff two roles are disjoint
     */
    @PortedFrom(file = "Kernel.h", name = "isDisjointRoles")
    public boolean isDisjointRoles(ObjectRoleExpression or, ObjectRoleExpression os) {
        preprocessKB();
        Role r = getRole(or, ROLE_EXPRESSION_EXPECTED);
        Role s = getRole(os, ROLE_EXPRESSION_EXPECTED);
        if (r.isTop() || s.isTop()) {
            // universal role is not disjoint with anything
            return false;
        }
        // empty role is disjoint with everything
        if (r.isBottom() || s.isBottom()) {
            return true;
        }
        return getTBox().isDisjointRoles(r, s);
    }

    /**
     * @param or
     *        R
     * @param os
     *        S
     * @return true iff two roles are disjoint
     */
    @PortedFrom(file = "Kernel.h", name = "isDisjointRoles")
    public boolean isDisjointRoles(DataRoleExpression or, DataRoleExpression os) {
        preprocessKB();
        Role r = getRole(or, ROLE_EXPRESSION_EXPECTED);
        Role s = getRole(os, ROLE_EXPRESSION_EXPECTED);
        if (r.isTop() || s.isTop()) {
            // universal role is not disjoint with anything
            return false;
        }
        if (r.isBottom() || s.isBottom()) {
            // empty role is disjoint with everything
            return true;
        }
        return getTBox().isDisjointRoles(r, s);
    }

    /**
     * @param or
     *        R
     * @param os
     *        S
     * @return true if R is a sub-role of S
     */
    @PortedFrom(file = "Kernel.h", name = "isSubRoles")
    public boolean isSubRoles(RoleExpression or, RoleExpression os) {
        preprocessKB();
        Role r = getRole(or, "Role expression expected in isSubRoles()");
        Role s = getRole(os, "Role expression expected in isSubRoles()");
        if (r.isBottom() || s.isTop()) {
            // \bot [= X [= \top
            return true;
        }
        if (r.isTop() && s.isBottom()) {
            // as \top [= \bot leads to inconsistent ontology
            return false;
        }
        if (isEmptyRole(or) || isUniversalRole(os)) {
            // \bot [= X [= \top
            return true;
        }
        if (isUniversalRole(or) && isEmptyRole(os)) {
            // as \top [= \bot leads to inconsistent ontology
            return false;
        }
        // told case first
        if (!r.isTop() && !s.isBottom() && r.lesserequal(s)) {
            return true;
        }
        // check the general case
        // FIXME!! cache it later
        return checkRoleSubsumption(r, s);
    }

    // single satisfiability
    /**
     * @param c
     *        C
     * @return true iff C is satisfiable
     */
    @PortedFrom(file = "Kernel.h", name = "isSatisfiable")
    public boolean isSatisfiable(ConceptExpression c) {
        preprocessKB();
        try {
            return checkSat(c);
        } catch (OWLRuntimeException crn) {
            if (c instanceof ConceptName) {
                // this is an unknown concept
                return true;
            }
            // complex expression, involving unknown names
            throw crn;
        }
    }

    /**
     * @param c
     *        C
     * @param d
     *        D
     * @return true iff C [= D holds
     */
    @PortedFrom(file = "Kernel.h", name = "isSubsumedBy")
    public boolean isSubsumedBy(ConceptExpression c, ConceptExpression d) {
        preprocessKB();
        if (ReasoningKernel.isNameOrConst(d) && ReasoningKernel.isNameOrConst(c)) {
            return this.checkSub(getTBox().getCI(e(c)), getTBox().getCI(e(d)));
        }
        DLTree nD = DLTreeFactory.createSNFNot(e(d));
        return !checkSatTree(DLTreeFactory.createSNFAnd(e(c), nD));
    }

    /**
     * @param c
     *        C
     * @param d
     *        D
     * @return true iff C is disjoint with D; that is, C [= \not D holds
     */
    @PortedFrom(file = "Kernel.h", name = "isDisjoint")
    public boolean isDisjoint(ConceptExpression c, ConceptExpression d) {
        return !isSatisfiable(and(c, d));
    }

    /**
     * @param c
     *        C
     * @param d
     *        D
     * @return true iff C is equivalent to D
     */
    @PortedFrom(file = "Kernel.h", name = "isEquivalent")
    public boolean isEquivalent(ConceptExpression c, ConceptExpression d) {
        if (c.equals(d)) {
            return true;
        }
        preprocessKB();
        // try to detect C=D wrt named concepts
        if (isKBClassified() && ReasoningKernel.isNameOrConst(d) && ReasoningKernel.isNameOrConst(c)) {
            TaxonomyVertex cV = getTBox().getCI(e(c)).getTaxVertex();
            TaxonomyVertex dV = getTBox().getCI(e(d)).getTaxVertex();
            if (cV == null && dV == null) {
                // 2 different fresh names
                return false;
            }
            if (cV == null || dV == null) {
                // not the same
                return false;
            }
            return cV.equals(dV);
        }
        // not classified or not named constants
        return isSubsumedBy(c, d) && isSubsumedBy(d, c);
    }

    // concept hierarchy
    /**
     * apply actor__apply() to all super/sub-concepts of [complex] C
     * 
     * @param c
     *        C
     * @param direct
     *        direct
     * @param actor
     *        actor
     * @param supDirection
     *        true for superClasses, false for subclasses
     * @param <T>
     *        type
     * @return actor
     */
    @PortedFrom(file = "Kernel.h", name = "getSupConcepts")
    public <T extends Expression> TaxonomyActor<T> getConcepts(
        // refactored getSupConcepts and getSubConcepts
        ConceptExpression c, boolean direct, TaxonomyActor<T> actor, boolean supDirection) {
        classifyKB();
        setUpCache(c, CLASSIFIED);
        actor.clear();
        getCTaxonomy().getRelativesInfo(cachedVertex, actor, false, direct, supDirection);
        return actor;
    }

    /**
     * apply actor__apply() to all DIRECT sub-concepts of [complex] C
     * 
     * @param c
     *        C
     * @param direct
     *        direct
     * @param actor
     *        actor
     * @param supDirection
     *        true for super direction
     * @return actor
     */
    @PortedFrom(file = "Kernel.h", name = "getSubConcepts")
    public Actor getConcepts(ConceptExpression c, boolean direct, Actor actor, boolean supDirection) {
        classifyKB();
        this.setUpCache(c, CLASSIFIED);
        actor.clear();
        Taxonomy tax = getCTaxonomy();
        tax.getRelativesInfo(cachedVertex, actor, false, direct, supDirection);
        return actor;
    }

    /**
     * apply actor__apply() to all synonyms of [complex] C
     * 
     * @param c
     *        C
     * @param actor
     *        actor
     * @param <T>
     *        type
     * @return actor
     */
    @PortedFrom(file = "Kernel.h", name = "getEquivalentConcepts")
    public <T extends Expression> TaxonomyActor<T> getEquivalentConcepts(ConceptExpression c, TaxonomyActor<T> actor) {
        classifyKB();
        setUpCache(c, CLASSIFIED);
        actor.clear();
        actor.apply(cachedVertex);
        return actor;
    }

    /**
     * apply actor::apply() to all named concepts disjoint with [complex] C
     * 
     * @param c
     *        C
     * @param actor
     *        actor
     * @param <T>
     *        type
     * @return actor
     */
    @PortedFrom(file = "Kernel.h", name = "getDisjointConcepts")
    public <T extends Expression> TaxonomyActor<T> getDisjointConcepts(ConceptExpression c, TaxonomyActor<T> actor) {
        classifyKB();
        setUpCache(not(c), CLASSIFIED);
        actor.clear();
        // we are looking for all sub-concepts of (not C) (including synonyms)
        getCTaxonomy().getRelativesInfo(cachedVertex, actor, true, false, false);
        return actor;
    }

    // role hierarchy
    /**
     * apply actor__apply() to all DIRECT super-roles of [complex] R
     * 
     * @param r
     *        r
     * @param direct
     *        direct
     * @param actor
     *        actor
     * @param supDirection
     *        true for super direction
     * @param <T>
     *        type
     * @return actor
     */
    @PortedFrom(file = "Kernel.h", name = "getSupRoles")
    public <T extends RoleExpression> TaxonomyActor<T> getRoles(RoleExpression r, boolean direct,
        TaxonomyActor<T> actor, boolean supDirection) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role role = getRole(r, "Role expression expected in getRoles()");
        actor.clear();
        getTaxonomy(role).getRelativesInfo(getTaxVertex(role), actor, false, direct, supDirection);
        return actor;
    }

    /**
     * apply actor__apply() to all synonyms of [complex] R
     * 
     * @param r
     *        r
     * @param actor
     *        actor
     * @param <T>
     *        type
     * @return actor
     */
    @PortedFrom(file = "Kernel.h", name = "getEquivalentRoles")
    public <T extends RoleExpression> TaxonomyActor<T> getEquivalentRoles(RoleExpression r, TaxonomyActor<T> actor) {
        preprocessKB();
        Role role = getRole(r, "Role expression expected in getEquivalentRoles()");
        actor.clear();
        actor.apply(getTaxVertex(role));
        return actor;
    }

    // domain and range as a set of named concepts
    /**
     * apply actor__apply() to all DIRECT NC that are in the domain of [complex]
     * R
     * 
     * @param r
     *        r
     * @param direct
     *        direct
     * @param actor
     *        actor
     * @param <T>
     *        type
     * @return modified actor
     */
    @PortedFrom(file = "Kernel.h", name = "getORoleDomain")
    public <T extends ConceptExpression> TaxonomyActor<T> getORoleDomain(ObjectRoleExpression r, boolean direct,
        TaxonomyActor<T> actor) {
        classifyKB();
        setUpCache(exists(r, top()), CLASSIFIED);
        actor.clear();
        // if direct, gets an exact domain is named concept; otherwise, set of
        // the most specific concepts
        // else gets all named classes that are in the domain of a role
        getCTaxonomy().getRelativesInfo(cachedVertex, actor, true, direct, true);
        return actor;
    }

    /**
     * apply actor::apply() to all DIRECT NC that are in the domain of data role
     * R
     * 
     * @param r
     *        r
     * @param direct
     *        direct
     * @param actor
     *        actor
     * @param <T>
     *        type
     * @return modified actor
     */
    @PortedFrom(file = "Kernel.h", name = "getDRoleDomain")
    public <T extends ConceptExpression> TaxonomyActor<T> getDRoleDomain(DataRoleExpression r, boolean direct,
        TaxonomyActor<T> actor) {
        classifyKB();
        setUpCache(exists(r, dataTop()), CLASSIFIED);
        actor.clear();
        // if direct, gets an exact domain is named concept; otherwise, set of
        // the most specific concepts
        // else gets all named classes that are in the domain of a role
        getCTaxonomy().getRelativesInfo(cachedVertex, actor, true, direct, true);
        return actor;
    }

    /**
     * apply actor__apply() to all DIRECT NC that are in the range of [complex]
     * R
     * 
     * @param r
     *        r
     * @param direct
     *        direct
     * @param actor
     *        actor
     * @param <T>
     *        type
     */
    @PortedFrom(file = "Kernel.h", name = "getRoleRange")
    private <T extends ConceptExpression> void getRoleRange(ObjectRoleExpression r, boolean direct,
        TaxonomyActor<T> actor) {
        getORoleDomain(getExpressionManager().inverse(r), direct, actor);
    }

    // instances
    /**
     * @param c
     *        C
     * @param actor
     *        actor
     * @param direct
     *        direct
     * @return actor
     */
    @PortedFrom(file = "Kernel.h", name = "getInstances")
    public TaxonomyActor<IndividualExpression> getInstances(ConceptExpression c,
        TaxonomyActor<IndividualExpression> actor, boolean direct) {
        if (direct) {
            getDirectInstances(c, actor);
        } else {
            this.getInstances(c, actor);
        }
        return actor;
    }

    /**
     * apply actor__apply() to all direct instances of given [complex] C
     * 
     * @param c
     *        C
     * @param actor
     *        actor
     */
    @PortedFrom(file = "Kernel.h", name = "getDirectInstances")
    public void getDirectInstances(ConceptExpression c, Actor actor) {
        realiseKB();
        setUpCache(c, CLASSIFIED);
        actor.clear();
        // implement 1-level check by hand
        // if the root vertex contains individuals -- we are done
        if (actor.apply(cachedVertex)) {
            return;
        }
        // if not, just go 1 level down and apply the actor regardless of what's
        // found
        // FIXME!! check again after bucket-method will be implemented
        cachedVertex.neigh(false).forEach(actor::apply);
    }

    /**
     * apply actor__apply() to all instances of given [complex] C
     * 
     * @param c
     *        C
     * @param actor
     *        actor
     */
    @PortedFrom(file = "Kernel.h", name = "getInstances")
    public void getInstances(ConceptExpression c, Actor actor) {
        // FIXME!!
        // check for Racer's/IS approach
        realiseKB();
        setUpCache(c, CLASSIFIED);
        actor.clear();
        getCTaxonomy().getRelativesInfo(cachedVertex, actor, true, false, false);
    }

    /**
     * apply actor__apply() to all DIRECT concepts that are types of an
     * individual I
     * 
     * @param i
     *        I
     * @param direct
     *        direct
     * @param actor
     *        actor
     * @param <T>
     *        type
     * @return actor
     */
    @PortedFrom(file = "Kernel.h", name = "getTypes")
    public <T extends Expression> TaxonomyActor<T> getTypes(IndividualName i, boolean direct, TaxonomyActor<T> actor) {
        realiseKB();
        setUpCache(getExpressionManager().oneOf(i), CLASSIFIED);
        actor.clear();
        getCTaxonomy().getRelativesInfo(cachedVertex, actor, true, direct, true);
        return actor;
    }

    /**
     * apply actor__apply() to all synonyms of an individual I
     * 
     * @param i
     *        I
     * @param actor
     *        actor
     * @param <T>
     *        type
     * @return actor
     */
    @PortedFrom(file = "Kernel.h", name = "getSameAs")
    public <T extends Expression> TaxonomyActor<T> getSameAs(IndividualName i, TaxonomyActor<T> actor) {
        realiseKB();
        return getEquivalentConcepts(getExpressionManager().oneOf(i), actor);
    }

    /**
     * @param ie
     *        I
     * @param je
     *        J
     * @return true iff I and J refer to the same individual
     */
    @PortedFrom(file = "Kernel.h", name = "isSameIndividuals")
    public boolean isSameIndividuals(IndividualExpression ie, IndividualExpression je) {
        realiseKB();
        Individual i = getIndividual(ie, "Only known individuals are allowed in the isSameAs()");
        Individual j = getIndividual(je, "Only known individuals are allowed in the isSameAs()");
        return getTBox().isSameIndividuals(i, j);
    }

    // ----------------------------------------------------------------------------------
    // knowledge exploration queries
    // ----------------------------------------------------------------------------------
    /**
     * build a completion tree for a concept expression C (no caching as it
     * breaks the idea of KE).
     * 
     * @param c
     *        C
     * @return the root node
     */
    @PortedFrom(file = "Kernel.h", name = "buildCompletionTree")
    public DlCompletionTree buildCompletionTree(ConceptExpression c) {
        preprocessKB();
        setUpCache(c, SAT);
        DlCompletionTree ret = getTBox().buildCompletionTree(cachedConcept);
        // init KB after the sat test to reduce the number of DAG adjustments
        if (ke == null) {
            ke = new KnowledgeExplorer(getTBox(), getExpressionManager());
        }
        return ret;
    }

    /** @return knowledge explorer */
    @Original
    private KnowledgeExplorer getKnowledgeExplorer() {
        return ke;
    }

    /**
     * build the set of data neighbours of a NODE, put the set of data roles
     * into the RESULT variable
     * 
     * @param node
     *        node
     * @param onlyDet
     *        onlyDet
     * @return set of data roles
     */
    @PortedFrom(file = "Kernel.h", name = "getDataRoles")
    public Set<DataRoleExpression> getDataRoles(DlCompletionTree node, boolean onlyDet) {
        return ke.getDataRoles(node, onlyDet);
    }

    /**
     * build the set of object neighbours of a NODE, put the set of object roles
     * and inverses into the RESULT variable
     * 
     * @param node
     *        node
     * @param onlyDet
     *        onlyDet
     * @param needIncoming
     *        needIncoming
     * @return set of object roles
     */
    @PortedFrom(file = "Kernel.h", name = "getObjectRoles")
    public Set<ObjectRoleExpression> getObjectRoles(DlCompletionTree node, boolean onlyDet, boolean needIncoming) {
        return ke.getObjectRoles(node, onlyDet, needIncoming);
    }

    /**
     * build the set of neighbours of a NODE via role ROLE; put the resulting
     * list into RESULT
     * 
     * @param node
     *        node
     * @param role
     *        role
     * @return neighbors for KE
     */
    @PortedFrom(file = "Kernel.h", name = "getNeighbours")
    public List<DlCompletionTree> getNeighbours(DlCompletionTree node, RoleExpression role) {
        return ke.getNeighbours(node, getRole(role, ROLE_EXPECTED));
    }

    /**
     * put into RESULT all the expressions from the NODE label; if ONLYDET is
     * true, return only deterministic elements
     * 
     * @param node
     *        node
     * @param onlyDet
     *        onlyDet
     * @return object labels for KE
     */
    @PortedFrom(file = "Kernel.h", name = "getLabel")
    public List<ConceptExpression> getObjectLabel(DlCompletionTree node, boolean onlyDet) {
        return ke.getObjectLabel(node, onlyDet);
    }

    /**
     * @param node
     *        node
     * @param onlyDet
     *        onlyDet
     * @return data labels for KE
     */
    @PortedFrom(file = "Kernel.h", name = "getLabel")
    public List<DataExpression> getDataLabel(DlCompletionTree node, boolean onlyDet) {
        return ke.getDataLabel(node, onlyDet);
    }

    /**
     * @param node
     *        node
     * @return blocker of a blocked node NODE or NULL if node is not blocked
     */
    @PortedFrom(file = "Kernel.h", name = "getBlocker")
    public DlCompletionTree getBlocker(DlCompletionTree node) {
        return ke.getBlocker(node);
    }

    // atomic decomposition queries
    /**
     * @param i
     *        I
     * @param c
     *        C
     * @return true iff individual I is instance of given [complex] C
     */
    @PortedFrom(file = "Kernel.h", name = "isInstance")
    public boolean isInstance(IndividualExpression i, ConceptExpression c) {
        realiseKB();
        getIndividual(i, "individual name expected in the isInstance()");
        // FIXME!! this way a new concept is created; could be done more optimal
        return isSubsumedBy(getExpressionManager().oneOf(i), c);
    }

    /**
     * check whether it is necessary to reload the ontology
     * 
     * @return true if reload must happen
     */
    @PortedFrom(file = "Kernel.h", name = "needForceReload")
    private boolean needForceReload() {
        if (pTBox == null) {
            return true;
        }
        if (!ontology.isChanged()) {
            return false;
        }
        // no incremental required -- nothing to do
        if (!kernelOptions.isUseIncrementalReasoning()) {
            return true;
        }
        return false;
    }

    /** incrementally classify changes */
    @PortedFrom(file = "Incremental.cpp", name = "doIncremental")
    public void doIncremental() {
        // re-set the modularizer to use updated ontology
        modSyn = null;
        Set<OWLEntity> mPlus = new HashSet<>();
        Set<OWLEntity> mMinus = new HashSet<>();
        // detect new- and old- signature elements
        Collection<NamedEntity> newSig = ontology.getSignature();
        Collection<NamedEntity> removedEntities = new HashSet<>(ontoSig);
        removedEntities.removeAll(newSig);
        Collection<NamedEntity> addedEntities = new HashSet<>(newSig);
        addedEntities.removeAll(ontoSig);
        Taxonomy tax = getCTaxonomy();
        // deal with removed concepts
        for (NamedEntity e : removedEntities) {
            if (e.getEntry() instanceof Concept) {
                Concept c = (Concept) e.getEntry();
                // remove all links
                c.getTaxVertex().remove();
                // update Name2Sig
                name2Sig.remove(c.getEntity());
            }
        }
        // deal with added concepts
        tax.deFinalise();
        for (NamedEntity e : addedEntities) {
            if (e instanceof ConceptName) {
                ConceptName cName = (ConceptName) e;
                // register the name in TBox
                e(cName);
                // create sig for it
                Concept c = (Concept) cName.getEntry();
                setupSig(c.getEntity().getEntity());
                // init the taxonomy element
                TaxonomyVertex cur = tax.getCurrent();
                cur.clear();
                cur.setSample(c, true);
                cur.addNeighbour(true, tax.getTopVertex());
                tax.finishCurrentNode();
            }
        }
        ontoSig = newSig;
        // fill in M^+ and M^- sets
        Timer t = new Timer();
        t.start();
        LocalityChecker lc = getModExtractor(false, null).getModularizer().getLocalityChecker();
        for (Map.Entry<OWLEntity, Signature> p : name2Sig.entrySet()) {
            lc.setSignatureValue(p.getValue());
            for (AxiomWrapper notProcessed : ontology.getAxioms()) {
                if (!lc.local(notProcessed.getAxiom())) {
                    mPlus.add(p.getKey());
                    break;
                }
            }
            for (AxiomWrapper retracted : ontology.getRetracted()) {
                if (!lc.local(retracted.getAxiom())) {
                    mMinus.add(p.getKey());
                    // FIXME!! only concepts for now
                    TaxonomyVertex v = ((ClassifiableEntry) p.getKey()).getTaxVertex();
                    if (v.noNeighbours(true)) {
                        v.addNeighbour(true, tax.getTopVertex());
                        tax.getTopVertex().addNeighbour(false, v);
                    }
                    break;
                }
            }
        }
        t.stop();
        // build changed modules
        Set<OWLEntity> toProcess = new HashSet<>(mPlus);
        toProcess.addAll(mMinus);
        // process all entries recursively
        while (!toProcess.isEmpty()) {
            buildSignature(toProcess.iterator().next(), ontology.getAxioms(), toProcess);
        }
        tax.finalise();
        // save the taxonomy
        byte[] saved = save(pTBox);
        // do actual change
        kernelOptions.setUseIncrementalReasoning(false);
        forceReload();
        pTBox.setNameSigMap(name2Sig);
        pTBox.isConsistent();
        kernelOptions.setUseIncrementalReasoning(true);
        // load the taxonomy
        pTBox = load(saved);
        pTBox.reclassify(mPlus, mMinus);
        getOntology().setProcessed();
    }

    @Nullable
    private static byte[] save(TBox tbox) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oout;
        try {
            oout = new ObjectOutputStream(out);
            // save taxonomy
            oout.writeObject(tbox);
        } catch (IOException e) {
            LOGGER.error("Serialization problem", e);
            return null;
        }
        return out.toByteArray();
    }

    @Nullable
    private static TBox load(@Nullable byte[] tbox) {
        try {
            return (TBox) new ObjectInputStream(new ByteArrayInputStream(tbox)).readObject();
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.error("Deserialization problem", e);
        }
        return null;
    }

    /** force the re-classification of the changed ontology */
    @PortedFrom(file = "Kernel.h", name = "forceReload")
    private void forceReload() {
        clearTBox();
        newKB();
        ontology.getSignature().forEach(p -> p.setEntry(null));
        // (re)load ontology
        OntologyLoader ontologyLoader = new OntologyLoader(getTBox());
        ontologyLoader.visitOntology(ontology);
        if (kernelOptions.isUseIncrementalReasoning()) {
            initIncremental();
        }
        // after loading ontology became processed completely
        ontology.setProcessed();
    }

    /**
     * setup Name2Sig for a given entity;
     * 
     * @param entity
     *        entity
     */
    @PortedFrom(file = "Incremental.cpp", name = "setupSig")
    public void setupSig(@Nullable OWLEntity entity) {
        moduleTimer.start();
        // do nothing if entity doesn't exist
        if (entity == null) {
            return;
        }
        moduleTimer.start();
        // prepare a place to update
        Signature sig = new Signature();
        // calculate a module
        sig.add(entity);
        getModExtractor(false, null).getModule(sig.getSignature().stream(), false, ModuleType.BOT);
        // perform update
        name2Sig.put(entity, new Signature(getModExtractor(false, null).getModularizer().getSignature().getSignature()
            .stream()));
        moduleTimer.stop();
    }

    /**
     * build signature for ENTITY and all dependent entities from toProcess;
     * look for modules in Module;
     * 
     * @param entity
     *        entity
     * @param module
     *        Module
     * @param toProcess
     *        toProcess
     */
    @PortedFrom(file = "Incremental.cpp", name = "buildSignature")
    public void buildSignature(OWLEntity entity, Collection<AxiomWrapper> module, Set<OWLEntity> toProcess) {
        toProcess.remove(entity);
        setupSig(entity);
        Collection<AxiomWrapper> newModule = getModExtractor(false, null).getModularizer().getModule();
        if (module.size() == newModule.size()) {
            return;
        }
        // smaller module: recurse
        Signature modSig = getModExtractor(false, null).getModularizer().getSignature();
        modSig.getSignature().stream().filter(toProcess::contains).forEach(p -> buildSignature(p, newModule,
            toProcess));
    }

    /** initialise the incremental bits on full reload */
    @PortedFrom(file = "Incremental.cpp", name = "initIncremental")
    public void initIncremental() {
        name2Sig.clear();
        // found all entities
        Set<OWLEntity> toProcess = new HashSet<>();
        getModExtractor(false, null);
        // fill the module signatures of the concepts
        getTBox().getConcepts().forEach(p -> toProcess.add(p.getEntity().getEntity()));
        // process all entries recursively
        while (!toProcess.isEmpty()) {
            buildSignature(toProcess.iterator().next(), ontology.getAxioms(), toProcess);
        }
        getTBox().setNameSigMap(name2Sig);
        ontoSig = ontology.getSignature();
    }

    // knowledge exploration queries
    @PortedFrom(file = "Kernel.h", name = "processKB")
    private void processKB(KBStatus status) {
        assert status.ordinal() >= KBCHECKED.ordinal();
        // check whether reasoning was failed
        if (reasoningFailed) {
            throw new ReasonerInternalException("Can't answer queries due to previous errors");
        }
        // check if something has to be done
        KBStatus curStatus = getStatus();
        if (curStatus.ordinal() >= status.ordinal()) {
            // nothing to do; but make sure that we are consistent
            if (!isKBConsistent()) {
                throw new InconsistentOntologyException("Ontology being processed is inconsistent");
            }
            return;
        }
        // here curStatus < kbRealised, and status >= kbChecked
        if (curStatus == KBEMPTY || curStatus == KBLOADING) {
            // load and preprocess KB -- here might be failures
            reasoningFailed = true;
            // load the axioms from the ontology to the TBox
            if (needForceReload()) {
                forceReload();
            } else {
                // just do incremental classification and exit
                doIncremental();
                reasoningFailed = false;
                return;
            }
            // do the preprocessing and consistency check
            pTBox.isConsistent();
            // if there were no exception thrown -- clear the failure status
            reasoningFailed = false;
            // if the consistency check is all we need -- return
            if (status == KBCHECKED) {
                return;
            }
        }
        // here we need to do classification or realisation
        if (!pTBox.isConsistent()) {
            // nothing to do for inconsistent ontologies
            return;
        }
        if (status == KBREALISED) {
            pTBox.performRealisation();
        } else {
            pTBox.performClassification();
        }
    }

    // do classification
    @PortedFrom(file = "Kernel.h", name = "classify")
    private void classify(KBStatus status) {
        // don't do classification twice
        if (status != KBREALISED) {
            if (!pTBox.isConsistent()) {
                return;
            }
            pTBox.performClassification();
            return;
        }
        realise();
    }

    // do realisation
    @PortedFrom(file = "Kernel.h", name = "realiseKB")
    private void realise() {
        if (!pTBox.isConsistent()) {
            return;
        }
        pTBox.performRealisation();
    }

    // -- query caching support
    /**
     * classify query; cache is ready at the point. NAMED means whether concept
     * is just a name
     * 
     * @param named
     *        named
     */
    @PortedFrom(file = "Kernel.h", name = "classifyQuery")
    private void classifyQuery(boolean named) {
        // make sure KB is classified
        classifyKB();
        if (!named) {
            // general expression: classify query concept
            getTBox().classifyQueryConcept();
        }
        cachedVertex = cachedConcept.getTaxVertex();
        if (cachedVertex == null) {
            // fresh concept
            cachedVertex = getCTaxonomy().getFreshVertex(cachedConcept);
        }
    }

    @PortedFrom(file = "Kernel.h", name = "setUpCache")
    private void setUpCache(DLTree query, CacheStatus level) {
        // if KB was changed since it was classified,
        // we should catch it before
        assert !ontology.isChanged();
        // check if the query is already cached
        if (this.checkQueryCache(query)) {
            // ... with the same level -- nothing to do
            if (level.ordinal() <= cacheLevel.ordinal()) {
                return;
            } else {
                // concept was defined but not classified yet
                assert level == CLASSIFIED && cacheLevel != CLASSIFIED;
                if (cacheLevel == SAT) {
                    // already check satisfiability
                    classifyQuery(cachedQueryTree.isCN());
                    return;
                }
            }
        } else {
            // change current query
            this.setQueryCache(query);
        }
        // clean cached info
        cachedVertex = null;
        cacheLevel = level;
        // check if concept-to-cache is defined in ontology
        if (cachedQueryTree.isCN()) {
            cachedConcept = getTBox().getCI(cachedQueryTree);
        } else {
            // case of complex query
            cachedConcept = getTBox().createQueryConcept(cachedQueryTree);
        }
        assert cachedConcept != null;
        // preprocess concept is necessary (fresh concept in query or complex
        // one)
        if (cachedConcept.getpName() == 0) {
            getTBox().preprocessQueryConcept(cachedConcept);
        }
        if (level == CLASSIFIED) {
            classifyQuery(cachedQueryTree.isCN());
        }
    }

    @PortedFrom(file = "Kernel.h", name = "setUpCache")
    private void setUpCache(ConceptExpression query, CacheStatus level) {
        // if KB was changed since it was classified,
        // we should catch it before
        assert !ontology.isChanged();
        // check if the query is already cached
        if (this.checkQueryCache(query)) {
            // ... with the same level -- nothing to do
            if (level.ordinal() <= cacheLevel.ordinal()) {
                return;
            } else {
                // concept was defined but not classified yet
                assert level == CLASSIFIED && cacheLevel != CLASSIFIED;
                if (cacheLevel == SAT) {
                    // already check satisfiability
                    classifyQuery(ReasoningKernel.isNameOrConst(cachedQuery));
                    return;
                }
            }
        } else {
            // change current query
            this.setQueryCache(query);
        }
        // clean cached info
        cachedVertex = null;
        cacheLevel = level;
        // check if concept-to-cache is defined in ontology
        if (ReasoningKernel.isNameOrConst(cachedQuery)) {
            cachedConcept = getTBox().getCI(e(cachedQuery));
        } else {
            // case of complex query
            // need to clear the query before transform it into DLTree
            // ... as if fresh names appears there, they would be cleaned up
            cachedConcept = getTBox().createQueryConcept(e(cachedQuery));
        }
        assert cachedConcept != null;
        // preprocess concept is necessary (fresh concept in query or complex
        // one)
        if (cachedConcept.getpName() == 0) {
            getTBox().preprocessQueryConcept(cachedConcept);
        }
        if (level == CLASSIFIED) {
            classifyQuery(ReasoningKernel.isNameOrConst(cachedQuery));
        }
    }

    @SuppressWarnings("unused")
    @PortedFrom(file = "Kernel.cpp", name = "isEq")
    protected boolean isEq(DlCompletionTree p, DlCompletionTree q) {
        return false;
    }

    @SuppressWarnings("unused")
    @PortedFrom(file = "Kernel.cpp", name = "isLt")
    protected boolean isLt(DlCompletionTree p, DlCompletionTree q) {
        return false;
    }

    @PortedFrom(file = "Kernel.cpp", name = "checkDataRelation")
    private boolean checkDataRelation(DlCompletionTree vR, DlCompletionTree vS, int op) {
        switch (op) {
            case 0: // =
                return isEq(vR, vS);
            case 1: // !=
                return !isEq(vR, vS);
            case 2: // <
                return isLt(vR, vS);
            case 3: // <=
                return isLt(vR, vS) || isEq(vR, vS);
            case 4: // >
                return isLt(vS, vR);
            case 5: // >=
                return isLt(vS, vR) || isEq(vR, vS);
            default:
                throw new ReasonerInternalException("Illegal operation in checkIndividualValues()");
        }
    }

    /**
     * set RESULT into set of instances of A such that they do have data roles R
     * and S
     * 
     * @param or
     *        R
     * @param os
     *        S
     * @param op
     *        op
     * @param individuals
     *        individuals
     * @return related individuals
     */
    @PortedFrom(file = "Kernel.cpp", name = "getDataRelatedIndividuals")
    public Collection<IndividualName> getDataRelatedIndividuals(RoleExpression or, RoleExpression os, int op,
        Collection<IndividualExpression> individuals) {
        realiseKB();    // ensure KB is ready to answer the query
        List<IndividualName> toReturn = new ArrayList<>();
        Role r = getRole(or, "Role expression expected in the getIndividualsWith()");
        Role s = getRole(os, "Role expression expected in the getIndividualsWith()");
        // vector of individuals
        for (IndividualExpression q : individuals) {
            Individual ind = getIndividual(q, "individual name expected in getDataRelatedIndividuals()");
            DlCompletionTree vR = null;
            DlCompletionTree vS = null;
            for (DlCompletionTreeArc edge : ind.getNode().getNeighbour()) {
                if (edge.isNeighbour(r)) {
                    vR = edge.getArcEnd();
                } else if (edge.isNeighbour(s)) {
                    vS = edge.getArcEnd();
                }
                if (vR != null && vS != null && checkDataRelation(vR, vS, op)) {
                    if (q instanceof IndividualName) {
                        toReturn.add((IndividualName) q);
                    }
                    break;
                }
            }
        }
        return toReturn;
    }

    // atomic decomposition queries
    /**
     * create new atomic decomposition of the loaded ontology using TYPE.
     * 
     * @param o
     *        ontology
     * @param useSemantics
     *        useSemantics
     * @param type
     *        type
     * @return size of the AD
     */
    @PortedFrom(file = "Kernel.h", name = "getAtomicDecompositionSize")
    public int getAtomicDecompositionSize(OWLOntology o, boolean useSemantics, ModuleType type) {
        // init AD field
        if (ad == null) {
            ad = new AtomicDecompositionImpl(o);
        }
        return ad.getAtoms().size();
    }

    /**
     * get a set of axioms that corresponds to the atom with the id INDEX
     * 
     * @param index
     *        index
     * @return list of axioms for atom
     */
    @PortedFrom(file = "Kernel.h", name = "getAtomAxioms")
    public List<AxiomWrapper> getAtomAxioms(int index) {
        return ad.getAtomList().get(index).getAtomAxioms();
    }

    /** @return list of tautologies */
    @Original
    public Set<OWLAxiom> getTautologies() {
        return ad.getTautologies();
    }

    /**
     * get a set of axioms that corresponds to the module of the atom with the
     * id INDEX
     * 
     * @param index
     *        index
     * @return module for atom
     */
    @PortedFrom(file = "Kernel.h", name = "getAtomModule")
    public List<AxiomWrapper> getAtomModule(int index) {
        return ad.getAtomList().get(index).getModule();
    }

    /**
     * get a set of atoms on which atom with index INDEX depends
     * 
     * @param index
     *        index
     * @return dependent atoms for atom
     */
    @PortedFrom(file = "Kernel.h", name = "getAtomDependents")
    public Set<OntologyAtom> getAtomDependents(int index) {
        return ad.getAtomList().get(index).getDependencies();
    }

    // knowledge exploration queries
    /**
     * @param r
     *        R
     * @param l
     *        l
     * @return true iff the chain contained in the arg-list is a sub-property of
     *         R
     */
    @PortedFrom(file = "Kernel.h", name = "checkSubChain")
    private boolean checkSubChain(Role r, List<ObjectRoleExpression> l) {
        // retrieve a role chain
        // R1 o ... o Rn [= R iff \ER1.\ER2....\ERn.(notC) and AR.C is
        // unsatisfiable
        DLTree tmp = DLTreeFactory.createSNFNot(getTBox().getFreshConcept());
        for (int i = l.size() - 1; i > -1; i--) {
            ObjectRoleExpression p = l.get(i);
            Role s = getRole(p, "Role expression expected in chain of isSubChain()");
            if (s.isBottom()) {
                // bottom in a chain makes it super of any role
                return true;
            }
            tmp = DLTreeFactory.createSNFExists(DLTreeFactory.createRole(s), tmp);
        }
        tmp = DLTreeFactory.createSNFAnd(tmp, DLTreeFactory.createSNFForall(DLTreeFactory.buildTree(new Lexeme(
            Token.RNAME, r)), getTBox().getFreshConcept()));
        return !checkSatTree(tmp);
    }

    /**
     * @param r
     *        R
     * @param l
     *        l
     * @return true if R is a super-role of a chain holding in the args
     */
    @PortedFrom(file = "Kernel.h", name = "isSubChain")
    public boolean isSubChain(ObjectRoleComplexExpression r, List<ObjectRoleExpression> l) {
        preprocessKB();
        Role role = getRole(r, "Role expression expected in isSubChain()");
        if (role.isTop()) {
            // universal role is a super of any chain
            return true;
        }
        return checkSubChain(role, l);
    }

    @PortedFrom(file = "Kernel.h", name = "buildRelatedCache")
    private List<Individual> buildRelatedCache(Individual i, Role r) {
        // for synonyms: use the representative's cache
        if (r.isSynonym()) {
            return getRelated(i, ClassifiableEntry.resolveSynonym(r));
        }
        // FIXME!! return an empty set for data roles
        // empty role has no fillers
        if (r.isDataRole() || r.isBottom()) {
            return new ArrayList<>();
        }
        // now fills the query
        RIActor actor = new RIActor();
        // ask for instances of \exists R^-.{i}
        ObjectRoleExpression invR = r.getId() > 0 ? getExpressionManager().inverse(getExpressionManager().objectRole(r
            .getEntity().getEntity())) : getExpressionManager().objectRole(r.inverse().getEntity().getEntity());
        ConceptExpression query;
        if (r.isTop()) {
            // universal role has all the named individuals as a filler
            query = top();
        } else {
            query = value(invR, getExpressionManager().individual(i.getEntity().getEntity()));
        }
        this.getInstances(query, actor);
        return actor.getAcc();
    }

    /**
     * @param i
     *        I
     * @param r
     *        R
     * @return individual role fillers for object role and individual
     */
    @PortedFrom(file = "Kernel.h", name = "getRoleFillers")
    public List<Individual> getRoleFillers(IndividualExpression i, ObjectRoleExpression r) {
        realiseKB();
        return getRelated(getIndividual(i, "Individual name expected in the getRoleFillers()"), getRole(r,
            "Role expression expected in the getRoleFillers()"));
    }

    /**
     * @param i
     *        I
     * @param r
     *        R
     * @param j
     *        J
     * @return true if individuals related through R
     */
    @PortedFrom(file = "Kernel.h", name = "isRelated")
    private boolean isRelated(IndividualExpression i, ObjectRoleExpression r, IndividualExpression j) {
        realiseKB();
        Individual ind = getIndividual(i, "Individual name expected in the isRelated()");
        Role role = getRole(r, "Role expression expected in the isRelated()");
        if (role.isDataRole()) {
            return false;
        }
        Individual jind = getIndividual(j, "Individual name expected in the isRelated()");
        // set instead of list?
        return getRelated(ind, role).stream().anyMatch(jind::equals);
    }
}
