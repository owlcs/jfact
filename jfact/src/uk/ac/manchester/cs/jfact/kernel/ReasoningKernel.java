package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.DLTree.*;
import static uk.ac.manchester.cs.jfact.kernel.CacheStatus.*;
import static uk.ac.manchester.cs.jfact.kernel.KBStatus.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.datatypes.LiteralEntry;
import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.actors.Actor;
import uk.ac.manchester.cs.jfact.kernel.actors.ActorImpl;
import uk.ac.manchester.cs.jfact.kernel.actors.RIActor;
import uk.ac.manchester.cs.jfact.kernel.actors.SupConceptActor;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptTop;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.split.*;
import conformance.Original;
import conformance.PortedFrom;

/** Reasoning kernel */
@PortedFrom(file = "Kernel.h", name = "ReasoningKernel")
public class ReasoningKernel {
    /** options for the kernel and all related substructures */
    @PortedFrom(file = "Kernel.h", name = "KernelOptions")
    private JFactReasonerConfiguration kernelOptions;
    /** local TBox (to be created) */
    @PortedFrom(file = "Kernel.h", name = "pTBox")
    private TBox pTBox;
    /** set of axioms */
    @PortedFrom(file = "Kernel.h", name = "Ontology")
    private Ontology ontology = new Ontology();
    /** expression translator to work with queries */
    @PortedFrom(file = "Kernel.h", name = "pET")
    private ExpressionTranslator pET;
    @PortedFrom(file = "Kernel.h", name = "Name2Sig")
    private Map<ClassifiableEntry, TSignature> Name2Sig = new HashMap<ClassifiableEntry, TSignature>();
    // Top/Bottom role names: if set, they will appear in all hierarchy-related
    // output
    /** top object role name */
    @PortedFrom(file = "Kernel.h", name = "topORoleName")
    private String topORoleName;
    /** bottom object role name */
    @PortedFrom(file = "Kernel.h", name = "botORoleName")
    private String botORoleName;
    /** top data role name */
    @PortedFrom(file = "Kernel.h", name = "topDRoleName")
    private String topDRoleName;
    /** bottom data role name */
    @PortedFrom(file = "Kernel.h", name = "botDRoleName")
    private String botDRoleName;
    // values to propagate to the new KB in case of clearance
    @Original
    private AtomicBoolean interrupted;

    /** @param b */
    @Original
    public void setInterruptedSwitch(AtomicBoolean b) {
        interrupted = b;
    }

    // reasoning cache
    /** cache level */
    @PortedFrom(file = "Kernel.h", name = "cacheLevel")
    private CacheStatus cacheLevel;
    /** cached query concept description */
    @PortedFrom(file = "Kernel.h", name = "cachedQueryTree")
    private DLTree cachedQueryTree;
    /** cached concept (either defConcept or existing one) */
    @PortedFrom(file = "Kernel.h", name = "cachedConcept")
    private Concept cachedConcept;
    /** cached query result (taxonomy position) */
    @PortedFrom(file = "Kernel.h", name = "cachedVertex")
    private TaxonomyVertex cachedVertex;
    // internal flags
    /** set if TBox throws an exception during preprocessing/classification */
    @PortedFrom(file = "Kernel.h", name = "reasoningFailed")
    private boolean reasoningFailed;
    /** trace vector for the last operation (set from the TBox trace-sets) */
    @PortedFrom(file = "Kernel.h", name = "TraceVec")
    private List<AxiomInterface> traceVec = new ArrayList<AxiomInterface>();
    /** flag to gather trace information for the next reasoner's call */
    @PortedFrom(file = "Kernel.h", name = "NeedTracing")
    private boolean needTracing;
    @Original
    private DatatypeFactory datatypeFactory;
    // types for knowledge exploration
    /** knowledge exploration support */
    @PortedFrom(file = "Kernel.h", name = "KE")
    KnowledgeExplorer KE;
    /** atomic decomposer */
    @PortedFrom(file = "Kernel.h", name = "AD")
    AtomicDecomposer AD;
    /** syntactic locality based module extractor */
    @PortedFrom(file = "Kernel.h", name = "ModSyn")
    OntologyBasedModularizer ModSyn = null;
    /** semantic locality based module extractor */
    @PortedFrom(file = "Kernel.h", name = "ModSem")
    OntologyBasedModularizer ModSem = null;
    /** set to return by the locality checking procedure */
    @PortedFrom(file = "Kernel.h", name = "Result")
    Set<AxiomInterface> Result = new HashSet<AxiomInterface>();
    /** cached query input description */
    @PortedFrom(file = "Kernel.h", name = "cachedQuery")
    ConceptExpression cachedQuery;
    @PortedFrom(file = "Kernel.h", name = "useAxiomSplitting")
    private boolean useAxiomSplitting;
    /** ignore cache for the TExpr* (useful for semantic AD) */
    @PortedFrom(file = "Kernel.h", name = "ignoreExprCache")
    boolean ignoreExprCache = false;

    // -- internal query cache manipulation
    /** clear query cache */
    @PortedFrom(file = "Kernel.h", name = "clearQueryCache")
    void clearQueryCache() {
        cachedQuery = null;
        cachedQueryTree = null;
    }

    /** set query cache value to QUERY */
    @PortedFrom(file = "Kernel.h", name = "setQueryCache")
    void setQueryCache(ConceptExpression query) {
        clearQueryCache();
        cachedQuery = query;
    }

    /** set query cache value to QUERY */
    @PortedFrom(file = "Kernel.h", name = "setQueryCache")
    void setQueryCache(DLTree query) {
        clearQueryCache();
        cachedQueryTree = query;
    }

    /** choose whether TExpr cache should be ignored
     * 
     * @param value */
    @PortedFrom(file = "Kernel.h", name = "setIgnoreExprCache")
    public void setIgnoreExprCache(boolean value) {
        ignoreExprCache = value;
    }

    /** check whether query cache is the same as QUERY */
    @PortedFrom(file = "Kernel.h", name = "checkQueryCache")
    boolean checkQueryCache(ConceptExpression query) {
        return ignoreExprCache ? false : cachedQuery == query;
    }

    /** check whether query cache is the same as QUERY */
    @PortedFrom(file = "Kernel.h", name = "checkQueryCache")
    boolean checkQueryCache(DLTree query) {
        return equalTrees(cachedQueryTree, query);
    }

    /** get status of the KB */
    @PortedFrom(file = "Kernel.h", name = "getStatus")
    private KBStatus getStatus() {
        if (pTBox == null) {
            return kbEmpty;
        }
        // if the ontology is changed, it needs to be reclassified
        if (ontology.isChanged()) {
            return kbLoading;
        }
        return pTBox.getStatus();
    }

    /** get DLTree corresponding to an expression EXPR */
    @PortedFrom(file = "Kernel.h", name = "e")
    public DLTree e(Expression expr) {
        return expr.accept(pET);
    }

    /** get fresh filled depending of a type of R */
    @PortedFrom(file = "Kernel.h", name = "getFreshFiller")
    private DLTree getFreshFiller(Role R) {
        if (R.isDataRole()) {
            LiteralEntry t = new LiteralEntry("freshliteral");
            t.setLiteral(DatatypeFactory.LITERAL.buildLiteral("freshliteral"));
            return DLTreeFactory.wrap(t);
        } else {
            return getTBox().getFreshConcept();
        }
    }

    /** get role expression based on the R */
    @PortedFrom(file = "Kernel.h", name = "Role")
    RoleExpression Role(Role R) {
        if (R.isDataRole()) {
            return getExpressionManager().dataRole(R.getName());
        } else {
            return getExpressionManager().objectRole(R.getName());
        }
    }

    /** clear cache and flags */
    @PortedFrom(file = "Kernel.h", name = "initCacheAndFlags")
    private void initCacheAndFlags() {
        cacheLevel = csEmpty;
        clearQueryCache();
        cachedConcept = null;
        cachedVertex = null;
        reasoningFailed = false;
        needTracing = false;
    }

    /** set need tracing to true */
    @PortedFrom(file = "Kernel.h", name = "needTracing")
    public void needTracing() {
        needTracing = true;
    }

    /** @return the trace-set of the last reasoning operation */
    @PortedFrom(file = "Kernel.h", name = "getTrace")
    public List<AxiomInterface> getTrace() {
        List<AxiomInterface> toReturn = new ArrayList<AxiomInterface>(traceVec);
        traceVec.clear();
        return toReturn;
    }

    /** set the signature of the expression translator
     * 
     * @param sig */
    @PortedFrom(file = "Kernel.h", name = "setSignature")
    public void setSignature(TSignature sig) {
        if (pET != null) {
            pET.setSignature(sig);
        }
    }

    /** @return the ontology */
    @PortedFrom(file = "Kernel.h", name = "getOntology")
    public Ontology getOntology() {
        return ontology;
    }

    /** @param ax
     * @param C
     * @param l
     * @return axiom C = C1 or ... or Cn; C1 != ... != Cn */
    @PortedFrom(file = "Kernel.h", name = "disjointUnion")
    public AxiomInterface disjointUnion(OWLAxiom ax, ConceptExpression C,
            List<ConceptExpression> l) {
        return ontology.add(new AxiomDisjointUnion(ax, C, l));
    }

    /** get related cache for an individual I */
    @PortedFrom(file = "Kernel.h", name = "getRelated")
    private List<Individual> getRelated(Individual I, Role R) {
        if (!I.hasRelatedCache(R)) {
            I.setRelatedCache(R, buildRelatedCache(I, R));
        }
        return I.getRelatedCache(R);
    }

    // -- internal reasoning methods
    /** @return true iff C is satisfiable */
    @PortedFrom(file = "Kernel.h", name = "checkSatTree")
    private boolean checkSatTree(DLTree C) {
        if (C.isTOP()) {
            return true;
        }
        if (C.isBOTTOM()) {
            return false;
        }
        this.setUpCache(C, csSat);
        return getTBox().isSatisfiable(cachedConcept);
    }

    /** @return true iff C is satisfiable */
    @PortedFrom(file = "Kernel.h", name = "checkSat")
    boolean checkSat(ConceptExpression C) {
        this.setUpCache(C, csSat);
        return getTBox().isSatisfiable(cachedConcept);
    }

    /** helper;
     * 
     * @return true iff C is either named concept of Top/Bot */
    @PortedFrom(file = "Kernel.h", name = "isNameOrConst")
    boolean isNameOrConst(ConceptExpression C) {
        return C instanceof ConceptName || C instanceof ConceptTop
                || C instanceof ConceptBottom;
    }

    @PortedFrom(file = "Kernel.h", name = "isNameOrConst")
    boolean isNameOrConst(DLTree C) {
        return C.isBOTTOM() || C.isTOP() || C.isName();
    }

    /** @return true iff C [= D holds */
    @PortedFrom(file = "Kernel.h", name = "checkSub")
    boolean checkSub(ConceptExpression C, ConceptExpression D) {
        if (this.isNameOrConst(D) && this.isNameOrConst(C)) {
            return this.checkSub(getTBox().getCI(e(C)), getTBox().getCI(e(D)));
        }
        return !checkSat(getExpressionManager().and(C, getExpressionManager().not(D)));
    }

    /** @param useSemantic
     * @return module extractor */
    @PortedFrom(file = "Kernel.h", name = "getModExtractor")
    public OntologyBasedModularizer getModExtractor(boolean useSemantic) {
        if (useSemantic) {
            if (ModSem == null) {
                ModSem = new OntologyBasedModularizer(ontology,
                        OntologyBasedModularizer.buildTModularizer(useSemantic, this));
            }
            return ModSem;
        }
        if (ModSyn == null) {
            ModSyn = new OntologyBasedModularizer(ontology,
                    OntologyBasedModularizer.buildTModularizer(useSemantic, this));
        }
        return ModSyn;
    }

    /** @param signature
     * @param useSemantic
     * @param type
     * @return a set of axioms that corresponds to the atom with the id INDEX */
    @PortedFrom(file = "Kernel.h", name = "getModule")
    public List<AxiomInterface> getModule(List<Expression> signature,
            boolean useSemantic, ModuleType type) {
        // init signature
        TSignature Sig = new TSignature();
        Sig.setLocality(false);
        for (Expression q : signature) {
            if (q instanceof NamedEntity) {
                Sig.add((NamedEntity) q);
            }
        }
        return getModExtractor(useSemantic).getModule(Sig, type);
    }

    /** @param signature
     * @param useSemantic
     * @param type
     * @return a set of axioms that corresponds to the atom with the id INDEX */
    @PortedFrom(file = "Kernel.h", name = "getNonLocal")
    public Set<AxiomInterface> getNonLocal(List<Expression> signature,
            boolean useSemantic, ModuleType type) {
        // init signature
        TSignature Sig = new TSignature();
        Sig.setLocality(type == ModuleType.M_TOP);
        for (Expression q : signature) {
            if (q instanceof NamedEntity) {
                Sig.add((NamedEntity) q);
            }
        }
        // do check
        LocalityChecker LC = getModExtractor(useSemantic).getModularizer()
                .getLocalityChecker();
        LC.setSignatureValue(Sig);
        Result.clear();
        for (AxiomInterface p : getOntology().getAxioms()) {
            if (!LC.local(p)) {
                Result.add(p);
            }
        }
        return Result;
    }

    /** @return true iff C [= D holds */
    @PortedFrom(file = "Kernel.h", name = "checkSub")
    private boolean checkSub(Concept C, Concept D) {
        // check whether a concept is fresh
        if (D.getpName() == 0) // D is fresh
        {
            if (C.getpName() == 0) {
                return C == D;
                // 2 fresh concepts subsumes one another iff they are the same
            } else {
                // C is known
                return !getTBox().isSatisfiable(C);
                // C [= D iff C=\bottom
            }
        } else {
            // D is known
            if (C.getpName() == 0) {
                // C [= D iff D = \top, or ~D = \bottom
                return !checkSatTree(DLTreeFactory.createSNFNot(getTBox().getTree(C)));
            }
        }
        // here C and D are known (not fresh)
        // check the obvious ones
        if (D.isTop() || C.isBottom()) {
            return true;
        }
        if (getStatus().ordinal() < kbClassified.ordinal()) {
            // unclassified => do via SAT test
            return getTBox().isSubHolds(C, D);
        }
        // classified => do the taxonomy traversal
        SupConceptActor actor = new SupConceptActor(D);
        Taxonomy tax = getCTaxonomy();
        if (tax.getRelativesInfo(C.getTaxVertex(), actor, true, false, true)) {
            return false;
        } else {
            tax.clearVisited();
            return true;
        }
    }

    /** @throw an exception if no TBox found */
    @PortedFrom(file = "Kernel.h", name = "checkTBox")
    private void checkTBox() {
        if (pTBox == null) {
            throw new ReasonerInternalException("KB Not Initialised");
        }
    }

    /** get RW access to TBox */
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

    /** get RW access to Object RoleMaster from TBox */
    @PortedFrom(file = "Kernel.h", name = "getORM")
    private RoleMaster getORM() {
        return getTBox().getORM();
    }

    /** get RW access to Data RoleMaster from TBox */
    @PortedFrom(file = "Kernel.h", name = "getDRM")
    private RoleMaster getDRM() {
        return getTBox().getDRM();
    }

    /** get access to the concept hierarchy */
    @PortedFrom(file = "Kernel.h", name = "getCTaxonomy")
    private Taxonomy getCTaxonomy() {
        if (!isKBClassified()) {
            throw new ReasonerInternalException(
                    "No access to concept taxonomy: ontology not classified");
        }
        return getTBox().getTaxonomy();
    }

    /** get access to the object role hierarchy */
    @PortedFrom(file = "Kernel.h", name = "getORTaxonomy")
    private Taxonomy getORTaxonomy() {
        if (!isKBPreprocessed()) {
            throw new ReasonerInternalException(
                    "No access to the object role taxonomy: ontology not preprocessed");
        }
        return getORM().getTaxonomy();
    }

    /** get access to the data role hierarchy */
    @PortedFrom(file = "Kernel.h", name = "getDRTaxonomy")
    private Taxonomy getDRTaxonomy() {
        if (!isKBPreprocessed()) {
            throw new ReasonerInternalException(
                    "No access to the data role taxonomy: ontology not preprocessed");
        }
        return getDRM().getTaxonomy();
    }

    // transformation methods
    /** get individual by the TIndividualExpr */
    @PortedFrom(file = "Kernel.h", name = "getIndividual")
    private Individual getIndividual(IndividualExpression i, String reason) {
        DLTree I = e(i);
        if (I == null) {
            throw new ReasonerInternalException(reason);
        }
        return (Individual) getTBox().getCI(I);
    }

    /** get role by the TRoleExpr */
    @PortedFrom(file = "Kernel.h", name = "getRole")
    private Role getRole(RoleExpression r, String reason) {
        return Role.resolveRole(e(r));
    }

    /** get taxonomy of the property wrt it's name */
    @PortedFrom(file = "Kernel.h", name = "getTaxonomy")
    private Taxonomy getTaxonomy(Role R) {
        return R.isDataRole() ? getDRTaxonomy() : getORTaxonomy();
    }

    /** get taxonomy vertext of the property wrt it's name */
    @PortedFrom(file = "Kernel.h", name = "getTaxVertex")
    private TaxonomyVertex getTaxVertex(Role R) {
        return R.getTaxVertex();
    }

    @PortedFrom(file = "Kernel.h", name = "getOptions")
    public JFactReasonerConfiguration getOptions() {
        return kernelOptions;
    }

    /** @return classification status of KB */
    @PortedFrom(file = "Kernel.h", name = "isKBPreprocessed")
    public boolean isKBPreprocessed() {
        return getStatus().ordinal() >= kbCChecked.ordinal();
    }

    /** @return classification status of KB */
    @PortedFrom(file = "Kernel.h", name = "isKBClassified")
    public boolean isKBClassified() {
        return getStatus().ordinal() >= kbClassified.ordinal();
    }

    /** @return realistion status of KB */
    @PortedFrom(file = "Kernel.h", name = "isKBRealised")
    public boolean isKBRealised() {
        return getStatus().ordinal() >= kbRealised.ordinal();
    }

    /** set top/bottom role names to use them in the related output
     * 
     * @param topO
     * @param botO
     * @param topD
     * @param botD */
    @PortedFrom(file = "Kernel.h", name = "setTopBottomRoleNames")
    public void setTopBottomRoleNames(String topO, String botO, String topD, String botD) {
        topORoleName = topO;
        botORoleName = botO;
        topDRoleName = topD;
        botDRoleName = botD;
        ontology.getExpressionManager().setTopBottomRoles(topORoleName, botORoleName,
                topDRoleName, botDRoleName);
    }

    /** dump query processing TIME, reasoning statistics and a (preprocessed)
     * TBox
     * 
     * @param o
     * @param time */
    @PortedFrom(file = "Kernel.h", name = "writeReasoningResult")
    public void writeReasoningResult(LogAdapter o, long time) {
        // get rid of the query leftovers
        getTBox().clearQueryConcept();
        getTBox().writeReasoningResult(time);
    }

    // helper methods to query properties of roles
    /** @return true if R is functional wrt ontology */
    @PortedFrom(file = "Kernel.h", name = "checkFunctionality")
    private boolean checkFunctionality(Role R) {
        // R is transitive iff \ER.C and \ER.\not C is unsatisfiable
        DLTree tmp = DLTreeFactory.createSNFExists(DLTreeFactory.createRole(R).copy(),
                DLTreeFactory.createSNFNot(getFreshFiller(R)));
        tmp = DLTreeFactory.createSNFAnd(tmp, DLTreeFactory.createSNFExists(
                DLTreeFactory.createRole(R), getFreshFiller(R)));
        return !checkSatTree(tmp);
    }

    /** @return true if R is functional; set the value for R if necessary */
    @PortedFrom(file = "Kernel.h", name = "getFunctionality")
    private boolean getFunctionality(Role R) {
        if (!R.isFunctionalityKnown()) {
            R.setFunctional(checkFunctionality(R));
        }
        return R.isFunctional();
    }

    /** @return true if R is transitive wrt ontology */
    @PortedFrom(file = "Kernel.h", name = "checkTransitivity")
    private boolean checkTransitivity(DLTree R) {
        // R is transitive iff \ER.\ER.C and \AR.\not C is unsatisfiable
        DLTree tmp = DLTreeFactory.createSNFExists(R.copy(),
                DLTreeFactory.createSNFNot(getTBox().getFreshConcept()));
        tmp = DLTreeFactory.createSNFExists(R.copy(), tmp);
        tmp = DLTreeFactory.createSNFAnd(tmp,
                DLTreeFactory.createSNFForall(R, getTBox().getFreshConcept()));
        return !checkSatTree(tmp);
    }

    /** @return true if R is symmetric wrt ontology */
    @PortedFrom(file = "Kernel.h", name = "checkSymmetry")
    private boolean checkSymmetry(DLTree R) {
        // R is symmetric iff C and \ER.\AR.(not C) is unsatisfiable
        DLTree tmp = DLTreeFactory.createSNFForall(R.copy(),
                DLTreeFactory.createSNFNot(getTBox().getFreshConcept()));
        tmp = DLTreeFactory.createSNFAnd(getTBox().getFreshConcept(),
                DLTreeFactory.createSNFExists(R, tmp));
        return !checkSatTree(tmp);
    }

    /** @return true if R is reflexive wrt ontology */
    @PortedFrom(file = "Kernel.h", name = "checkReflexivity")
    private boolean checkReflexivity(DLTree R) {
        // R is reflexive iff C and \AR.(not C) is unsatisfiable
        DLTree tmp = DLTreeFactory.createSNFForall(R,
                DLTreeFactory.createSNFNot(getTBox().getFreshConcept()));
        tmp = DLTreeFactory.createSNFAnd(getTBox().getFreshConcept(), tmp);
        return !checkSatTree(tmp);
    }

    @PortedFrom(file = "Kernel.h", name = "checkRoleSubsumption")
    private boolean checkRoleSubsumption(Role R, Role S) {
        if (R.isDataRole() != S.isDataRole()) {
            return false;
        }
        // R [= S iff \ER.C and \AS.(not C) is unsatisfiable
        DLTree tmp = DLTreeFactory.createSNFForall(DLTreeFactory.createRole(S),
                DLTreeFactory.createSNFNot(getFreshFiller(S)));
        tmp = DLTreeFactory.createSNFAnd(DLTreeFactory.createSNFExists(
                DLTreeFactory.createRole(R), getFreshFiller(R)), tmp);
        return !checkSatTree(tmp);
    }

    /** @return expression manager */
    @PortedFrom(file = "Kernel.h", name = "getExpressionManager")
    public ExpressionManager getExpressionManager() {
        return ontology.getExpressionManager();
    }

    /** create new KB */
    @PortedFrom(file = "Kernel.h", name = "newKB")
    private boolean newKB() {
        if (pTBox != null) {
            return true;
        }
        pTBox = new TBox(datatypeFactory, getOptions(), topORoleName, botORoleName,
                topDRoleName, botDRoleName, interrupted);
        pET = new ExpressionTranslator(pTBox);
        initCacheAndFlags();
        return false;
    }

    /** delete existed KB */
    @PortedFrom(file = "Kernel.h", name = "releaseKB")
    private boolean releaseKB() {
        clearTBox();
        ontology.clear();
        return false;
    }

    /** reset current KB
     * 
     * @return true if no new tbox is created */
    @PortedFrom(file = "Kernel.h", name = "clearKB")
    public boolean clearKB() {
        if (pTBox == null) {
            return true;
        }
        return releaseKB() || newKB();
    }

    // TELLS interface
    // Declaration axioms
    /** @param ax
     * @param C
     * @return axiom declare(x) */
    @PortedFrom(file = "Kernel.h", name = "declare")
    public AxiomInterface declare(OWLAxiom ax, Expression C) {
        return ontology.add(new AxiomDeclaration(ax, C));
    }

    // Concept axioms
    /** @param ax
     * @param C
     * @param D
     * @return axiom C [= D */
    @PortedFrom(file = "Kernel.h", name = "impliesConcepts")
    public AxiomInterface impliesConcepts(OWLAxiom ax, ConceptExpression C,
            ConceptExpression D) {
        return ontology.add(new AxiomConceptInclusion(ax, C, D));
    }

    /** @param ax
     * @param l
     * @return axiom C1 = ... = Cn */
    @PortedFrom(file = "Kernel.h", name = "equalConcepts")
    public AxiomInterface equalConcepts(OWLAxiom ax, List<ConceptExpression> l) {
        return ontology.add(new AxiomEquivalentConcepts(ax, l));
    }

    /** @param ax
     * @param l
     * @return axiom C1 != ... != Cn */
    @PortedFrom(file = "Kernel.h", name = "disjointConcepts")
    public AxiomInterface disjointConcepts(OWLAxiom ax, List<ConceptExpression> l) {
        return ontology.add(new AxiomDisjointConcepts(ax, l));
    }

    // Role axioms
    /** @param ax
     * @param R
     * @param S
     * @return R = Inverse(S) */
    @PortedFrom(file = "Kernel.h", name = "setInverseRoles")
    public AxiomInterface setInverseRoles(OWLAxiom ax, ObjectRoleExpression R,
            ObjectRoleExpression S) {
        return ontology.add(new AxiomRoleInverse(ax, R, S));
    }

    /** @param ax
     * @param R
     * @param S
     * @return axiom (R [= S) */
    @PortedFrom(file = "Kernel.h", name = "impliesORoles")
    public AxiomInterface impliesORoles(OWLAxiom ax, ObjectRoleComplexExpression R,
            ObjectRoleExpression S) {
        return ontology.add(new AxiomORoleSubsumption(ax, R, S));
    }

    /** @param ax
     * @param R
     * @param S
     * @return axiom (R [= S) */
    @PortedFrom(file = "Kernel.h", name = "impliesDRoles")
    public AxiomInterface impliesDRoles(OWLAxiom ax, DataRoleExpression R,
            DataRoleExpression S) {
        return ontology.add(new AxiomDRoleSubsumption(ax, R, S));
    }

    /** @param ax
     * @param l
     * @return axiom R1 = R2 = ... */
    @PortedFrom(file = "Kernel.h", name = "equalORoles")
    public AxiomInterface equalORoles(OWLAxiom ax, List<ObjectRoleExpression> l) {
        return ontology.add(new AxiomEquivalentORoles(ax, l));
    }

    /** @param ax
     * @param l
     * @return axiom R1 = R2 = ... */
    @PortedFrom(file = "Kernel.h", name = "equalDRoles")
    public AxiomInterface equalDRoles(OWLAxiom ax, List<DataRoleExpression> l) {
        return ontology.add(new AxiomEquivalentDRoles(ax, l));
    }

    /** @param ax
     * @param l
     * @return axiom R1 != R2 != ... */
    @PortedFrom(file = "Kernel.h", name = "disjointORoles")
    public AxiomInterface disjointORoles(OWLAxiom ax, List<ObjectRoleExpression> l) {
        return ontology.add(new AxiomDisjointORoles(ax, l));
    }

    /** @param ax
     * @param l
     * @return axiom R1 != R2 != ... */
    @PortedFrom(file = "Kernel.h", name = "disjointDRoles")
    public AxiomInterface disjointDRoles(OWLAxiom ax, List<DataRoleExpression> l) {
        return ontology.add(new AxiomDisjointDRoles(ax, l));
    }

    /** @param ax
     * @param R
     * @param C
     * @return Domain (R C) */
    @PortedFrom(file = "Kernel.h", name = "setODomain")
    public AxiomInterface setODomain(OWLAxiom ax, ObjectRoleExpression R,
            ConceptExpression C) {
        return ontology.add(new AxiomORoleDomain(ax, R, C));
    }

    /** @param ax
     * @param R
     * @param C
     * @return Domain (R C) */
    @PortedFrom(file = "Kernel.h", name = "setDDomain")
    public AxiomInterface setDDomain(OWLAxiom ax, DataRoleExpression R,
            ConceptExpression C) {
        return ontology.add(new AxiomDRoleDomain(ax, R, C));
    }

    /** @param ax
     * @param R
     * @param C
     * @return Range (R C) */
    @PortedFrom(file = "Kernel.h", name = "setORange")
    public AxiomInterface setORange(OWLAxiom ax, ObjectRoleExpression R,
            ConceptExpression C) {
        return ontology.add(new AxiomORoleRange(ax, R, C));
    }

    /** @param ax
     * @param R
     * @param E
     * @return Range (R E) */
    @PortedFrom(file = "Kernel.h", name = "setDRange")
    public AxiomInterface setDRange(OWLAxiom ax, DataRoleExpression R, DataExpression E) {
        return ontology.add(new AxiomDRoleRange(ax, R, E));
    }

    /** @param ax
     * @param R
     * @return Transitive (R) */
    @PortedFrom(file = "Kernel.h", name = "setTransitive")
    public AxiomInterface setTransitive(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomRoleTransitive(ax, R));
    }

    /** @param ax
     * @param R
     * @return Reflexive (R) */
    @PortedFrom(file = "Kernel.h", name = "setReflexive")
    public AxiomInterface setReflexive(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomRoleReflexive(ax, R));
    }

    /** @param ax
     * @param R
     * @return Irreflexive (R): Domain(R) = \neg ER.Self */
    @PortedFrom(file = "Kernel.h", name = "setIrreflexive")
    public AxiomInterface setIrreflexive(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomRoleIrreflexive(ax, R));
    }

    /** @param ax
     * @param R
     * @return Symmetric (R): R [= R^- */
    @PortedFrom(file = "Kernel.h", name = "setSymmetric")
    public AxiomInterface setSymmetric(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomRoleSymmetric(ax, R));
    }

    /** @param ax
     * @param R
     * @return AntySymmetric (R): disjoint(R,R^-) */
    @PortedFrom(file = "Kernel.h", name = "setAsymmetric")
    public AxiomInterface setAsymmetric(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomRoleAsymmetric(ax, R));
    }

    /** @param ax
     * @param R
     * @return Functional (R) */
    @PortedFrom(file = "Kernel.h", name = "setOFunctional")
    public AxiomInterface setOFunctional(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomORoleFunctional(ax, R));
    }

    /** @param ax
     * @param R
     * @return Functional (R) */
    @PortedFrom(file = "Kernel.h", name = "setDFunctional")
    public AxiomInterface setDFunctional(OWLAxiom ax, DataRoleExpression R) {
        return ontology.add(new AxiomDRoleFunctional(ax, R));
    }

    /** @param ax
     * @param R
     * @return InverseFunctional (R) */
    @PortedFrom(file = "Kernel.h", name = "setInverseFunctional")
    public AxiomInterface setInverseFunctional(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomRoleInverseFunctional(ax, R));
    }

    // Individual axioms
    /** @param ax
     * @param I
     * @param C
     * @return axiom I e C */
    @PortedFrom(file = "Kernel.h", name = "instanceOf")
    public AxiomInterface instanceOf(OWLAxiom ax, IndividualExpression I,
            ConceptExpression C) {
        return ontology.add(new AxiomInstanceOf(ax, I, C));
    }

    /** @param ax
     * @param I
     * @param R
     * @param J
     * @return axiom <I,J>:R */
    @PortedFrom(file = "Kernel.h", name = "relatedTo")
    public AxiomInterface relatedTo(OWLAxiom ax, IndividualExpression I,
            ObjectRoleExpression R, IndividualExpression J) {
        return ontology.add(new AxiomRelatedTo(ax, I, R, J));
    }

    /** @param ax
     * @param I
     * @param R
     * @param J
     * @return axiom <I,J>:\neg R */
    @PortedFrom(file = "Kernel.h", name = "relatedToNot")
    public AxiomInterface relatedToNot(OWLAxiom ax, IndividualExpression I,
            ObjectRoleExpression R, IndividualExpression J) {
        return ontology.add(new AxiomRelatedToNot(ax, I, R, J));
    }

    /** @param ax
     * @param I
     * @param A
     * @param V
     * @return axiom (value I A V) */
    @PortedFrom(file = "Kernel.h", name = "valueOf")
    public AxiomInterface valueOf(OWLAxiom ax, IndividualExpression I,
            DataRoleExpression A, Literal<?> V) {
        return ontology.add(new AxiomValueOf(ax, I, A, V));
    }

    /** @param ax
     * @param I
     * @param A
     * @param V
     * @return axiom <I,V>:\neg A */
    @PortedFrom(file = "Kernel.h", name = "valueOfNot")
    public AxiomInterface valueOfNot(OWLAxiom ax, IndividualExpression I,
            DataRoleExpression A, Literal<?> V) {
        return ontology.add(new AxiomValueOfNot(ax, I, A, V));
    }

    /** @param ax
     * @param l
     * @return same individuals */
    @PortedFrom(file = "Kernel.h", name = "processSame")
    public AxiomInterface processSame(OWLAxiom ax, List<IndividualExpression> l) {
        return ontology.add(new AxiomSameIndividuals(ax, l));
    }

    /** @param ax
     * @param l
     * @return different individuals */
    @PortedFrom(file = "Kernel.h", name = "processDifferent")
    public AxiomInterface processDifferent(OWLAxiom ax, List<IndividualExpression> l) {
        return ontology.add(new AxiomDifferentIndividuals(ax, l));
    }

    /** @param ax
     * @param l
     * @return let all concept expressions in the ArgQueue to be fairness
     *         constraints */
    @PortedFrom(file = "Kernel.h", name = "setFairnessConstraint")
    public AxiomInterface setFairnessConstraint(OWLAxiom ax, List<ConceptExpression> l) {
        return ontology.add(new AxiomFairnessConstraint(ax, l));
    }

    /** retract an axiom
     * 
     * @param axiom */
    @PortedFrom(file = "Kernel.h", name = "retract")
    public void retract(AxiomInterface axiom) {
        ontology.retract(axiom);
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
        try {
            if (getStatus().ordinal() <= kbLoading.ordinal()) {
                processKB(kbCChecked);
            }
            return getTBox().isConsistent();
        } catch (InconsistentOntologyException e) {
            return false;
        }
    }

    /** ensure that KB is preprocessed/consistence checked */
    @PortedFrom(file = "Kernel.h", name = "preprocessKB")
    private void preprocessKB() {
        if (!isKBConsistent()) {
            throw new InconsistentOntologyException();
        }
    }

    /** ensure that KB is classified */
    @PortedFrom(file = "Kernel.h", name = "classifyKB")
    public void classifyKB() {
        if (!isKBClassified()) {
            processKB(kbClassified);
        }
        if (!isKBConsistent()) {
            throw new InconsistentOntologyException();
        }
    }

    /** ensure that KB is realised */
    @PortedFrom(file = "Kernel.h", name = "realiseKB")
    public void realiseKB() {
        if (!isKBRealised()) {
            processKB(kbRealised);
        }
        if (!isKBConsistent()) {
            throw new InconsistentOntologyException();
        }
    }

    // role info retrieval
    /** @param R
     * @return true iff object role is functional */
    @PortedFrom(file = "Kernel.h", name = "isFunctional")
    public boolean isFunctional(ObjectRoleExpression R) {
        // ensure KB is ready to answer the query
        preprocessKB();
        Role r = getRole(R, "Role expression expected in isFunctional()");
        if (r.isTop()) {
            // universal role is symmetric
            return true;
        }
        if (r.isBottom()) {
            // empty role is symmetric
            return true;
        }
        return getFunctionality(r);
    }

    /** @param R
     * @return true iff data role is functional */
    @PortedFrom(file = "Kernel.h", name = "isFunctional")
    public boolean isFunctional(DataRoleExpression R) {
        // ensure KB is ready to answer the query
        preprocessKB();
        Role r = getRole(R, "Role expression expected in isFunctional()");
        if (r.isTop()) {
            // universal role is symmetric
            return true;
        }
        if (r.isBottom()) {
            // empty role is symmetric
            return true;
        }
        return getFunctionality(r);
    }

    /** @param R
     * @return true iff role is inverse-functional */
    @PortedFrom(file = "Kernel.h", name = "isInverseFunctional")
    public boolean isInverseFunctional(ObjectRoleExpression R) {
        // ensure KB is ready to answer the query
        preprocessKB();
        Role r = getRole(R, "Role expression expected in isInverseFunctional()")
                .inverse();
        if (r.isTop()) {
            // universal role is symmetric
            return true;
        }
        if (r.isBottom()) {
            // empty role is symmetric
            return true;
        }
        return getFunctionality(r);
    }

    /** @param R
     * @return true iff role is transitive */
    @PortedFrom(file = "Kernel.h", name = "isTransitive")
    public boolean isTransitive(ObjectRoleExpression R) {
        // ensure KB is ready to answer the query
        preprocessKB();
        Role r = getRole(R, "Role expression expected in isTransitive()");
        if (r.isTop()) {
            // universal role is symmetric
            return true;
        }
        // empty role is symmetric
        if (r.isBottom()) {
            return true;
        }
        if (!r.isTransitivityKnown()) {
            r.setTransitive(checkTransitivity(e(R)));
        }
        return r.isTransitive();
    }

    /** @param R
     * @return true iff role is symmetric */
    @PortedFrom(file = "Kernel.h", name = "isSymmetric")
    public boolean isSymmetric(ObjectRoleExpression R) {
        preprocessKB();
        Role r = getRole(R, "Role expression expected in isSymmetric()");
        if (r.isTop()) {
            return true;
        }
        if (r.isBottom()) {
            return true;
        }
        if (!r.isSymmetryKnown()) {
            r.setSymmetric(checkSymmetry(e(R)));
        }
        return r.isSymmetric();
    }

    /** @param R
     * @return true iff role is asymmetric */
    @PortedFrom(file = "Kernel.h", name = "isAsymmetric")
    public boolean isAsymmetric(ObjectRoleExpression R) {
        preprocessKB();
        Role r = getRole(R, "Role expression expected in isAsymmetric()");
        if (r.isTop()) {
            return true;
        }
        if (r.isBottom()) {
            return true;
        }
        if (!r.isAsymmetryKnown()) {
            r.setAsymmetric(getTBox().isDisjointRoles(r, r.inverse()));
        }
        return r.isAsymmetric();
    }

    /** @param R
     * @return true iff role is reflexive */
    @PortedFrom(file = "Kernel.h", name = "isReflexive")
    public boolean isReflexive(ObjectRoleExpression R) {
        preprocessKB();
        Role r = getRole(R, "Role expression expected in isReflexive()");
        if (r.isTop()) {
            return true;
        }
        if (r.isBottom()) {
            return true;
        }
        if (!r.isReflexivityKnown()) {
            r.setReflexive(checkReflexivity(e(R)));
        }
        return r.isReflexive();
    }

    /** @param R
     * @return true iff role is irreflexive */
    @PortedFrom(file = "Kernel.h", name = "isIrreflexive")
    public boolean isIrreflexive(ObjectRoleExpression R) {
        preprocessKB();
        Role r = getRole(R, "Role expression expected in isIrreflexive()");
        if (r.isTop()) {
            return true;
        }
        if (r.isBottom()) {
            return true;
        }
        if (!r.isIrreflexivityKnown()) {
            r.setIrreflexive(getTBox().isIrreflexive(r));
        }
        return r.isIrreflexive();
    }

    // all-disjoint query implementation
    /** @param l
     * @return true if disjoint */
    @PortedFrom(file = "Kernel.h", name = "isDisjointRoles")
    public boolean isDisjointRoles(List<? extends RoleExpression> l) {
        int nTopRoles = 0;
        List<Role> Roles = new ArrayList<Role>(l.size());
        for (RoleExpression p : l) {
            uk.ac.manchester.cs.jfact.kernel.Role role = getRole(p,
                    "Role expression expected in isDisjointRoles()");
            if (role.isTop()) {
                // universal role is not disjoint with anything
                ++nTopRoles;
            }
            if (role.isBottom()) {
                // empty role is disjoint with everything
                continue;
            }
            Roles.add(role);
        }
        // deal with top-roles
        if (nTopRoles > 0) {
            if (nTopRoles > 1 || !Roles.isEmpty()) {
                return false;
                // universal role is not disjoint with anything but the bottom
                // role
            } else {
                return true;
            }
        }
        // test pair-wise disjointness
        for (int i = 0; i < Roles.size() - 1; i++) {
            for (int j = i + 1; j < Roles.size(); j++) {
                if (!getTBox().isDisjointRoles(Roles.get(i), Roles.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /** @param R
     * @param S
     * @return true iff two roles are disjoint */
    @PortedFrom(file = "Kernel.h", name = "isDisjointRoles")
    public boolean isDisjointRoles(ObjectRoleExpression R, ObjectRoleExpression S) {
        preprocessKB();
        Role r = getRole(R, "Role expression expected in isDisjointRoles()");
        Role s = getRole(S, "Role expression expected in isDisjointRoles()");
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

    /** @param R
     * @param S
     * @return true iff two roles are disjoint */
    @PortedFrom(file = "Kernel.h", name = "isDisjointRoles")
    public boolean isDisjointRoles(DataRoleExpression R, DataRoleExpression S) {
        preprocessKB();
        Role r = getRole(R, "Role expression expected in isDisjointRoles()");
        Role s = getRole(S, "Role expression expected in isDisjointRoles()");
        if (r.isTop() || s.isTop()) {
            return false;
        }
        if (r.isBottom() || s.isBottom()) {
            return true;
        }
        return getTBox().isDisjointRoles(r, s);
    }

    /** @param R
     * @param S
     * @return true if R is a sub-role of S */
    @PortedFrom(file = "Kernel.h", name = "isSubRoles")
    public boolean isSubRoles(ObjectRoleComplexExpression R, ObjectRoleExpression S) {
        preprocessKB();
        Role r = getRole(R, "Role expression expected in isSubRoles()");
        Role s = getRole(S, "Role expression expected in isSubRoles()");
        if (r.isBottom() || s.isTop()) {
            // \bot [= X [= \top
            return true;
        }
        if (r.isTop() && s.isBottom()) {
            // as \top [= \bot leads to inconsistent ontology
            return false;
        }
        if (getExpressionManager().isEmptyRole(R)
                || getExpressionManager().isUniversalRole(S)) {
            // \bot [= X [= \top
            return true;
        }
        if (getExpressionManager().isUniversalRole(R)
                && getExpressionManager().isEmptyRole(S)) {
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
    /** @param C
     * @return true iff C is satisfiable */
    @PortedFrom(file = "Kernel.h", name = "isSatisfiable")
    public boolean isSatisfiable(ConceptExpression C) {
        preprocessKB();
        try {
            return checkSat(C);
        } catch (OWLRuntimeException crn) {
            if (C instanceof ConceptName) {
                // this is an unknown concept
                return true;
            }
            // complex expression, involving unknown names
            throw crn;
        }
    }

    /** @param C
     * @param D
     * @return true iff C [= D holds */
    @PortedFrom(file = "Kernel.h", name = "isSubsumedBy")
    public boolean isSubsumedBy(ConceptExpression C, ConceptExpression D) {
        preprocessKB();
        if (this.isNameOrConst(D) && this.isNameOrConst(C)) {
            return this.checkSub(getTBox().getCI(e(C)), getTBox().getCI(e(D)));
        }
        DLTree nD = DLTreeFactory.createSNFNot(e(D));
        return !checkSatTree(DLTreeFactory.createSNFAnd(e(C), nD));
    }

    /** @param C
     * @param D
     * @return true iff C is disjoint with D; that is, C [= \not D holds */
    @PortedFrom(file = "Kernel.h", name = "isDisjoint")
    public boolean isDisjoint(ConceptExpression C, ConceptExpression D) {
        return isSubsumedBy(C, getExpressionManager().not(D));
    }

    /** @param C
     * @param D
     * @return true iff C is equivalent to D */
    @PortedFrom(file = "Kernel.h", name = "isEquivalent")
    public boolean isEquivalent(ConceptExpression C, ConceptExpression D) {
        if (C == D) {
            return true;
        }
        preprocessKB();
        if (isKBClassified()) {
            // try to detect C=D wrt named concepts
            if (this.isNameOrConst(D) && this.isNameOrConst(C)) {
                TaxonomyVertex cV = getTBox().getCI(e(C)).getTaxVertex();
                TaxonomyVertex dV = getTBox().getCI(e(D)).getTaxVertex();
                if (cV == null && dV == null) {
                    // 2 different fresh names
                    return false;
                }
                return cV == dV;
            }
        }
        // not classified or not named constants
        return isSubsumedBy(C, D) && isSubsumedBy(D, C);
    }

    // concept hierarchy
    /** apply actor__apply() to all DIRECT super-concepts of [complex] C
     * 
     * @param C
     * @param direct
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getSupConcepts")
    public void getSupConcepts(ConceptExpression C, boolean direct, Actor actor) {
        classifyKB();
        this.setUpCache(C, csClassified);
        Taxonomy tax = getCTaxonomy();
        if (direct) {
            tax.getRelativesInfo(cachedVertex, actor, false, true, true);
        } else {
            tax.getRelativesInfo(cachedVertex, actor, false, false, true);
        }
    }

    /** apply actor__apply() to all DIRECT sub-concepts of [complex] C
     * 
     * @param C
     * @param direct
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getSubConcepts")
    public void getSubConcepts(ConceptExpression C, boolean direct, Actor actor) {
        classifyKB();
        this.setUpCache(C, csClassified);
        Taxonomy tax = getCTaxonomy();
        tax.getRelativesInfo(cachedVertex, actor, false, direct, false);
    }

    /** apply actor__apply() to all synonyms of [complex] C
     * 
     * @param C
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getEquivalentConcepts")
    public void getEquivalentConcepts(ConceptExpression C, Actor actor) {
        classifyKB();
        this.setUpCache(C, csClassified);
        actor.apply(cachedVertex);
    }

    /** apply actor::apply() to all named concepts disjoint with [complex] C
     * 
     * @param C
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getDisjointConcepts")
    public void getDisjointConcepts(ConceptExpression C, Actor actor) {
        classifyKB();
        this.setUpCache(getExpressionManager().not(C), csClassified);
        Taxonomy tax = getCTaxonomy();
        // we are looking for all sub-concepts of (not C) (including synonyms)
        tax.getRelativesInfo(cachedVertex, actor, true, false, false);
    }

    // role hierarchy
    /** apply actor__apply() to all DIRECT super-roles of [complex] R
     * 
     * @param r
     * @param direct
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getSupRoles")
    public void getSupRoles(RoleExpression r, boolean direct, Actor actor) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role R = getRole(r, "Role expression expected in getSupRoles()");
        Taxonomy tax = getTaxonomy(R);
        tax.getRelativesInfo(getTaxVertex(R), actor, false, direct, true);
    }

    /** apply actor__apply() to all DIRECT sub-roles of [complex] R
     * 
     * @param r
     * @param direct
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getSubRoles")
    public void getSubRoles(RoleExpression r, boolean direct, Actor actor) {
        preprocessKB();
        Role R = getRole(r, "Role expression expected in getSubRoles()");
        Taxonomy tax = getTaxonomy(R);
        tax.getRelativesInfo(getTaxVertex(R), actor, false, direct, false);
    }

    /** apply actor__apply() to all synonyms of [complex] R
     * 
     * @param r
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getEquivalentRoles")
    public void getEquivalentRoles(RoleExpression r, Actor actor) {
        preprocessKB();
        Role R = getRole(r, "Role expression expected in getEquivalentRoles()");
        actor.apply(getTaxVertex(R));
    }

    // domain and range as a set of named concepts
    /** apply actor__apply() to all DIRECT NC that are in the domain of [complex]
     * R
     * 
     * @param r
     * @param direct
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getORoleDomain")
    public void getORoleDomain(ObjectRoleExpression r, boolean direct, Actor actor) {
        classifyKB();
        this.setUpCache(getExpressionManager().exists(r, getExpressionManager().top()),
                csClassified);
        Taxonomy tax = getCTaxonomy();
        tax.getRelativesInfo(cachedVertex, actor, true, direct, true);
    }

    /** apply actor::apply() to all DIRECT NC that are in the domain of data role
     * R */
    @PortedFrom(file = "Kernel.h", name = "getDRoleDomain")
    void getDRoleDomain(DataRoleExpression r, boolean direct, Actor actor) {
        classifyKB();
        this.setUpCache(getExpressionManager()
                .exists(r, getExpressionManager().dataTop()), csClassified);
        Taxonomy tax = getCTaxonomy();
        if (direct) {
            tax.getRelativesInfo(cachedVertex, actor, true, true, true);
        } else {
            // gets all named classes that are in the domain of a role
            tax.getRelativesInfo(cachedVertex, actor, true, false, true);
        }
    }

    /** apply actor__apply() to all DIRECT NC that are in the range of [complex]
     * R
     * 
     * @param r
     * @param direct
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getRoleRange")
    public void getRoleRange(ObjectRoleExpression r, boolean direct, Actor actor) {
        getORoleDomain(getExpressionManager().inverse(r), direct, actor);
    }

    // instances
    /** @param C
     * @param actor
     * @param direct */
    @PortedFrom(file = "Kernel.h", name = "getInstances")
    public void getInstances(ConceptExpression C, Actor actor, boolean direct) {
        if (direct) {
            getDirectInstances(C, actor);
        } else {
            this.getInstances(C, actor);
        }
    }

    /** apply actor__apply() to all direct instances of given [complex] C
     * 
     * @param C
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getDirectInstances")
    public void getDirectInstances(ConceptExpression C, Actor actor) {
        realiseKB();
        this.setUpCache(C, csClassified);
        // implement 1-level check by hand
        // if the root vertex contains individuals -- we are done
        if (actor.apply(cachedVertex)) {
            return;
        }
        // if not, just go 1 level down and apply the actor regardless of what's
        // found
        // FIXME!! check again after bucket-method will be implemented
        for (TaxonomyVertex p : cachedVertex.neigh(/* upDirection= */false)) {
            actor.apply(p);
        }
    }

    /** apply actor__apply() to all instances of given [complex] C
     * 
     * @param C
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getInstances")
    public void getInstances(ConceptExpression C, Actor actor) {
        // FIXME!!
        // check for Racer's/IS approach
        realiseKB();
        this.setUpCache(C, csClassified);
        Taxonomy tax = getCTaxonomy();
        tax.getRelativesInfo(cachedVertex, actor, true, false, false);
    }

    /** apply actor__apply() to all DIRECT concepts that are types of an
     * individual I
     * 
     * @param I
     * @param direct
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getTypes")
    public void getTypes(IndividualName I, boolean direct, Actor actor) {
        realiseKB();
        this.setUpCache(getExpressionManager().oneOf(I), csClassified);
        Taxonomy tax = getCTaxonomy();
        tax.getRelativesInfo(cachedVertex, actor, true, direct, true);
    }

    /** apply actor__apply() to all synonyms of an individual I
     * 
     * @param I
     * @param actor */
    @PortedFrom(file = "Kernel.h", name = "getSameAs")
    public void getSameAs(IndividualName I, Actor actor) {
        realiseKB();
        getEquivalentConcepts(getExpressionManager().oneOf(I), actor);
    }

    /** @param I
     * @param J
     * @return true iff I and J refer to the same individual */
    @PortedFrom(file = "Kernel.h", name = "isSameIndividuals")
    public boolean isSameIndividuals(IndividualExpression I, IndividualExpression J) {
        realiseKB();
        Individual i = getIndividual(I,
                "Only known individuals are allowed in the isSameAs()");
        Individual j = getIndividual(J,
                "Only known individuals are allowed in the isSameAs()");
        return getTBox().isSameIndividuals(i, j);
    }

    // ----------------------------------------------------------------------------------
    // knowledge exploration queries
    // ----------------------------------------------------------------------------------
    /** build a completion tree for a concept expression C (no caching as it
     * breaks the idea of KE).
     * 
     * @param C
     * @return the root node */
    @PortedFrom(file = "Kernel.h", name = "buildCompletionTree")
    public DlCompletionTree buildCompletionTree(ConceptExpression C) {
        preprocessKB();
        this.setUpCache(C, csSat);
        DlCompletionTree ret = getTBox().buildCompletionTree(cachedConcept);
        // init KB after the sat test to reduce the number of DAG adjustments
        if (KE == null) {
            KE = new KnowledgeExplorer(getTBox(), getExpressionManager());
        }
        return ret;
    }

    /** @return knowledge explorer */
    @Original
    public KnowledgeExplorer getKnowledgeExplorer() {
        return KE;
    }

    /** build the set of data neighbours of a NODE, put the set of data roles
     * into the RESULT variable
     * 
     * @param node
     * @param onlyDet
     * @return set of data roles */
    @PortedFrom(file = "Kernel.h", name = "getDataRoles")
    public Set<RoleExpression> getDataRoles(DlCompletionTree node, boolean onlyDet) {
        return KE.getDataRoles(node, onlyDet);
    }

    /** build the set of object neighbours of a NODE, put the set of object roles
     * and inverses into the RESULT variable
     * 
     * @param node
     * @param onlyDet
     * @param needIncoming
     * @return set of object roles */
    @PortedFrom(file = "Kernel.h", name = "getObjectRoles")
    public Set<RoleExpression> getObjectRoles(DlCompletionTree node, boolean onlyDet,
            boolean needIncoming) {
        return KE.getObjectRoles(node, onlyDet, needIncoming);
    }

    /** build the set of neighbours of a NODE via role ROLE; put the resulting
     * list into RESULT
     * 
     * @param node
     * @param role
     * @return neighbors for KE */
    @PortedFrom(file = "Kernel.h", name = "getNeighbours")
    public List<DlCompletionTree>
            getNeighbours(DlCompletionTree node, RoleExpression role) {
        return KE.getNeighbours(node,
                getRole(role, "Role expression expected in getNeighbours() method"));
    }

    /** put into RESULT all the expressions from the NODE label; if ONLYDET is
     * true, return only deterministic elements
     * 
     * @param node
     * @param onlyDet
     * @return object labels for KE */
    @PortedFrom(file = "Kernel.h", name = "getLabel")
    public List<ConceptExpression> getObjectLabel(DlCompletionTree node, boolean onlyDet) {
        return KE.getObjectLabel(node, onlyDet);
    }

    /** @param node
     * @param onlyDet
     * @return data labels for KE */
    @PortedFrom(file = "Kernel.h", name = "getLabel")
    public List<DataExpression> getDataLabel(DlCompletionTree node, boolean onlyDet) {
        return KE.getDataLabel(node, onlyDet);
    }

    /** @param node
     * @return blocker of a blocked node NODE or NULL if node is not blocked */
    @PortedFrom(file = "Kernel.h", name = "getBlocker")
    public DlCompletionTree getBlocker(DlCompletionTree node) {
        return KE.getBlocker(node);
    }

    // atomic decomposition queries
    /** @param I
     * @param C
     * @return true iff individual I is instance of given [complex] C */
    @PortedFrom(file = "Kernel.h", name = "isInstance")
    public boolean isInstance(IndividualExpression I, ConceptExpression C) {
        realiseKB();
        getIndividual(I, "individual name expected in the isInstance()");
        // FIXME!! this way a new concept is created; could be done more optimal
        return isSubsumedBy(getExpressionManager().oneOf(I), C);
    }

    /** @param conf
     * @param factory */
    public ReasoningKernel(JFactReasonerConfiguration conf, DatatypeFactory factory) {
        // should be commented
        cachedQuery = null;
        cachedQueryTree = null;
        kernelOptions = conf;
        datatypeFactory = factory;
        pTBox = null;
        pET = null;
        cachedQuery = null;
        initCacheAndFlags();
        useAxiomSplitting = false;
    }

    /** check whether it is necessary to reload the ontology */
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
        ModSyn = null;
        // fill in M^+ and M^- sets
        LocalityChecker lc = getModExtractor(false).getModularizer().getLocalityChecker();
        // TODO: add new sig here
        List<ClassifiableEntry> MPlus = new ArrayList<ClassifiableEntry>();
        List<ClassifiableEntry> MMinus = new ArrayList<ClassifiableEntry>();
        for (Map.Entry<ClassifiableEntry, TSignature> p : Name2Sig.entrySet()) {
            lc.setSignatureValue(p.getValue());
            for (AxiomInterface notProcessed : ontology.getAxioms()) {
                if (!lc.local(notProcessed)) {
                    MPlus.add(p.getKey());
                    break;
                }
            }
            for (AxiomInterface retracted : ontology.getRetracted()) {
                if (!lc.local(retracted)) {
                    MMinus.add(p.getKey());
                    break;
                }
            }
        }
        // fill in an order to
        List<TaxonomyVertex> queue=new ArrayList<TaxonomyVertex>();
        List<TaxonomyVertex> toProcess=new ArrayList<TaxonomyVertex>();
        Taxonomy tax = getCTaxonomy();
        queue.add(tax.getTopVertex());
        while ( !queue.isEmpty() )
        {
            TaxonomyVertex cur = queue.remove(0);
            if ( tax.isVisited(cur) ) {
                continue;
            }
            tax.setVisited(cur);
ClassifiableEntry entry = cur.getPrimer();
            if ( MPlus.contains(entry)  || MMinus.contains(entry) ) {
                toProcess.add(cur);
            }
            for ( TaxonomyVertex p : cur.neigh(/*upDirection=*/false) ) {
                queue.add(p);
            }
        }
        tax.clearVisited();

  
    for ( TaxonomyVertex p : toProcess)
    {
         ClassifiableEntry entry = p.getPrimer();
        reclassifyNode ( p, MPlus.contains(entry), MMinus.contains(entry));
    }
//  forceReload();
}

    class ConceptActor extends ActorImpl<ClassifiableEntry>
{
        ConceptActor() {
            needConcepts();
        }

        List<List<ClassifiableEntry>> getNodes() {
            return acc;
        }

        @Override
        public boolean apply(TaxonomyVertex v) {
            // TODO Auto-generated method stub
            return false;
        }
}
/** reclassify (incrementally) NODE wrt ADDED or REMOVED flags*/
    @PortedFrom(file = "Incremental.cpp", name = "reclassifyNode")
public void
 reclassifyNode(TaxonomyVertex node, boolean added, boolean removed)
{
        ClassifiableEntry entry = node.getPrimer();
        NamedEntity entity = entry.getEntity();
        TSignature sig = new TSignature();
    sig.add(entity);
        List<AxiomInterface> Module = getModExtractor(false).getModule(sig,
                ModuleType.M_BOT);
    // update Name2Sig
        Name2Sig.put(entry, new TSignature(getModExtractor(false).getModularizer()
                .getSignature()));
        for (AxiomInterface p : Module) {
        getOntology().add(p);
    }
    // update top links
        node.clearLinks(/* upDirection= */true);
        ConceptActor actor = new ConceptActor();
        getSupConcepts((ConceptName) entity, /* direct= */true, actor);
        for (List<ClassifiableEntry> q : actor.getNodes()) {
            node.addNeighbour( /* upDirection= */true, q.get(0).getTaxVertex());
        }    // clear an ontology FIXME!! later
        getOntology().safeClear();
}


    
    /** force the re-classification of the changed ontology */
    @PortedFrom(file = "Kernel.h", name = "forceReload")
    private void forceReload() {
        clearTBox();
        newKB();
        // split ontological axioms
        if (kernelOptions.isSplits()) {
            TAxiomSplitter AxiomSplitter = new TAxiomSplitter(kernelOptions, ontology);
            AxiomSplitter.buildSplit();
        }
        OntologyLoader OntologyLoader = new OntologyLoader(getTBox());
        OntologyLoader.visitOntology(ontology);
        if (kernelOptions.isUseIncrementalReasoning()) {
            OntologyBasedModularizer ModExtractor = getModExtractor(false);
            // fill the module signatures of the concepts
            for (Concept p : getTBox().getConcepts()) {
                NamedEntity entity = p.getEntity();
                if (entity == null) {
                    continue;
                }
                TSignature sig = new TSignature();
                sig.add(entity);
                ModExtractor.getModule(sig, ModuleType.M_BOT);
                Name2Sig.put(p, new TSignature(ModExtractor.getModularizer()
                        .getSignature()));
            }
            getTBox().setNameSigMap(Name2Sig);
        }
        // after loading ontology became processed completely
        ontology.setProcessed();
    }

    // knowledge exploration queries
    @PortedFrom(file = "Kernel.h", name = "processKB")
    private void processKB(KBStatus status) {
        assert status.ordinal() >= kbCChecked.ordinal();
        // check whether reasoning was failed
        if (reasoningFailed) {
            throw new ReasonerInternalException(
                    "Can't classify KB because of previous errors");
        }
        // check if something have to be done
        if (getStatus().ordinal() >= status.ordinal()) {
            // nothing to do;
            // but make sure
            // that we are
            // consistent
            if (!isKBConsistent()) {
                throw new InconsistentOntologyException();
            }
            return;
        }
        // here we have to do something: let's decide what to do
        boolean stillGo = true;
        switch (getStatus()) {
            case kbEmpty:
            case kbLoading:
                break;
            // need to do the whole cycle -- just after the switch
            case kbCChecked: {
                classify(status);
                stillGo = false;
                break;
                // do classification
            }
            case kbClassified: {
                realise();
                stillGo = false;
                break;
                // do realisation
            }
            default:
                // nothing should be here
                throw new UnreachableSituationException();
        }
        if (stillGo) {
            // start with loading and preprocessing -- here might be a failures
            reasoningFailed = true;
            // load the axioms from the ontology to the TBox
            if (needForceReload()) {
                forceReload();
            } else {
                doIncremental();
            }
            // do the consistency check
            pTBox.isConsistent();
            // if there were no exception thrown -- clear the failure status
            reasoningFailed = false;
            if (status == kbCChecked) {
                return;
            }
            classify(status);
        }
    }

    // do classification
    @PortedFrom(file = "Kernel.h", name = "classify")
    private void classify(KBStatus status) {
        // don't do classification twice
        if (status != kbRealised) {
            // goto Realise;
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
    /** classify query; cache is ready at the point. NAMED means whether concept
     * is just a name */
    @PortedFrom(file = "Kernel.h", name = "classifyQuery")
    void classifyQuery(boolean named) {
        // make sure KB is classified
        classifyKB();
        if (!named) {
            getTBox().classifyQueryConcept();
        }
        cachedVertex = cachedConcept.getTaxVertex();
        if (cachedVertex == null) {
            cachedVertex = getCTaxonomy().getFreshVertex(cachedConcept);
        }
    }

    @PortedFrom(file = "Kernel.h", name = "setUpCache")
    void setUpCache(DLTree query, CacheStatus level) {
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
                assert level == csClassified && cacheLevel != csClassified;
                if (cacheLevel == csSat) {
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
        if (level == csClassified) {
            classifyQuery(cachedQueryTree.isCN());
        }
    }

    @PortedFrom(file = "Kernel.h", name = "setUpCache")
    void setUpCache(ConceptExpression query, CacheStatus level) {
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
                assert level == csClassified && cacheLevel != csClassified;
                if (cacheLevel == csSat) {
                    // already check satisfiability
                    classifyQuery(this.isNameOrConst(cachedQuery));
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
        if (this.isNameOrConst(cachedQuery)) {
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
        if (level == csClassified) {
            classifyQuery(this.isNameOrConst(cachedQuery));
        }
    }

    @PortedFrom(file = "Kernel.cpp", name = "isEq")
    boolean isEq(DlCompletionTree p, DlCompletionTree q) {
        return false;
    }

    @PortedFrom(file = "Kernel.cpp", name = "isLt")
    boolean isLt(DlCompletionTree p, DlCompletionTree q) {
        return false;
    }

    @PortedFrom(file = "Kernel.cpp", name = "checkDataRelation")
    boolean checkDataRelation(DlCompletionTree vR, DlCompletionTree vS, int op) {
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
                throw new ReasonerInternalException(
                        "Illegal operation in checkIndividualValues()");
        }
    }

    /** set RESULT into set of instances of A such that they do have data roles R
     * and S */
    @PortedFrom(file = "Kernel.cpp", name = "getDataRelatedIndividuals")
    public Collection<IndividualName> getDataRelatedIndividuals(RoleExpression R,
            RoleExpression S, int op, Collection<IndividualExpression> individuals) {
        realiseKB();    // ensure KB is ready to answer the query
        List<IndividualName> toReturn = new ArrayList<IndividualName>();
        Role r = getRole(R, "Role expression expected in the getIndividualsWith()");
        Role s = getRole(S, "Role expression expected in the getIndividualsWith()");
        // vector of individuals
        for (IndividualExpression q : individuals) {
            Individual ind = getIndividual(q,
                    "individual name expected in getDataRelatedIndividuals()");
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
    /** create new atomic decomposition of the loaded ontology using TYPE.
     * 
     * @param useSemantics
     * @param type
     * @return size of the AD */
    @PortedFrom(file = "Kernel.h", name = "getAtomicDecompositionSize")
    public int getAtomicDecompositionSize(boolean useSemantics, ModuleType type) {
        // init AD field
        if (AD == null) {
            AD = new AtomicDecomposer(getModExtractor(useSemantics).getModularizer());
        }
        return AD.getAOS(ontology, type).size();
    }

    /** get a set of axioms that corresponds to the atom with the id INDEX
     * 
     * @param index
     * @return list of axioms for atom */
    @PortedFrom(file = "Kernel.h", name = "getAtomAxioms")
    public Set<AxiomInterface> getAtomAxioms(int index) {
        return AD.getAOS().get(index).getAtomAxioms();
    }

    /** @return list of tautologies */
    @Original
    public List<AxiomInterface> getTautologies() {
        return AD.getTautologies();
    }

    /** get a set of axioms that corresponds to the module of the atom with the
     * id INDEX
     * 
     * @param index
     * @return module for atom */
    @PortedFrom(file = "Kernel.h", name = "getAtomModule")
    public Set<AxiomInterface> getAtomModule(int index) {
        return AD.getAOS().get(index).getModule();
    }

    /** get a set of atoms on which atom with index INDEX depends
     * 
     * @param index
     * @return dependent atoms for atom */
    @PortedFrom(file = "Kernel.h", name = "getAtomDependents")
    public Set<TOntologyAtom> getAtomDependents(int index) {
        return AD.getAOS().get(index).getDepAtoms();
    }

    /** get a number of locality checks performed for creating an AD */
    @PortedFrom(file = "Kernel.cpp", name = "getLocCheckNumber")
    public long getLocCheckNumber() {
        return AD.getLocChekNumber();
    }

    // knowledge exploration queries
    /** @return true iff the chain contained in the arg-list is a sub-property of
     *         R */
    @PortedFrom(file = "Kernel.h", name = "checkSubChain")
    private boolean checkSubChain(Role R, List<ObjectRoleExpression> l) {
        // retrieve a role chain
        // R1 o ... o Rn [= R iff \ER1.\ER2....\ERn.(notC) and AR.C is
        // unsatisfiable
        DLTree tmp = DLTreeFactory.createSNFNot(getTBox().getFreshConcept());
        for (int i = l.size() - 1; i > -1; i--) {
            ObjectRoleExpression p = l.get(i);
            Role S = getRole(p, "Role expression expected in chain of isSubChain()");
            if (S.isBottom()) {
                return true;
            }
            tmp = DLTreeFactory.createSNFExists(DLTreeFactory.createRole(S), tmp);
        }
        tmp = DLTreeFactory.createSNFAnd(tmp, DLTreeFactory.createSNFForall(DLTreeFactory
                .buildTree(new Lexeme(Token.RNAME, R)), getTBox().getFreshConcept()));
        return !checkSatTree(tmp);
    }

    /** @param R
     * @param l
     * @return true if R is a super-role of a chain holding in the args */
    @PortedFrom(file = "Kernel.h", name = "isSubChain")
    public boolean
            isSubChain(ObjectRoleComplexExpression R, List<ObjectRoleExpression> l) {
        preprocessKB();
        Role r = getRole(R, "Role expression expected in isSubChain()");
        if (r.isTop()) {
            // universal role is a super of any chain
            return true;
        }
        return checkSubChain(r, l);
    }

    /** @param R
     * @param S
     * @return true if R is a sub-role of S */
    @PortedFrom(file = "Kernel.h", name = "isSubRoles")
    public boolean isSubRoles(DataRoleExpression R, DataRoleExpression S) {
        preprocessKB();
        if (getExpressionManager().isEmptyRole(R)
                || getExpressionManager().isUniversalRole(S)) {
            // \bot [= X [= \top
            return true;
        }
        if (getExpressionManager().isUniversalRole(R)
                && getExpressionManager().isEmptyRole(S)) {
            // as \top [= \bot leads to inconsistent ontology
            return false;
        }
        // told case first
        uk.ac.manchester.cs.jfact.kernel.Role r = getRole(R,
                "Role expression expected in isSubRoles()");
        uk.ac.manchester.cs.jfact.kernel.Role s = getRole(S,
                "Role expression expected in isSubRoles()");
        if (!r.isTop() && !s.isBottom() && r.lesserequal(s)) {
            return true;
        }
        // check the general case
        // FIXME!! cache it later
        return checkRoleSubsumption(r, s);
    }

    @PortedFrom(file = "Kernel.h", name = "buildRelatedCache")
    private List<Individual> buildRelatedCache(Individual I, Role R) {
        if (R.isSynonym()) {
            return getRelated(I, ClassifiableEntry.resolveSynonym(R));
        }
        if (R.isDataRole() || R.isBottom()) {
            return new ArrayList<Individual>();
        }
        RIActor actor = new RIActor();
        ObjectRoleExpression InvR = R.getId() > 0 ? getExpressionManager().inverse(
                getExpressionManager().objectRole(R.getName())) : getExpressionManager()
                .objectRole(R.inverse().getName());
        ConceptExpression query;
        if (R.isTop()) {
            query = getExpressionManager().top();
        } else {
            query = getExpressionManager().value(InvR,
                    getExpressionManager().individual(I.getName()));
        }
        this.getInstances(query, actor);
        return actor.getAcc();
    }

    /** @param I
     * @param R
     * @return individual role fillers for object role and individual */
    @PortedFrom(file = "Kernel.h", name = "getRoleFillers")
    public List<Individual>
            getRoleFillers(IndividualExpression I, ObjectRoleExpression R) {
        realiseKB();
        return getRelated(
                getIndividual(I, "Individual name expected in the getRoleFillers()"),
                getRole(R, "Role expression expected in the getRoleFillers()"));
    }

    /** @param I
     * @param R
     * @param J
     * @return true if individuals related through R */
    @PortedFrom(file = "Kernel.h", name = "isRelated")
    public boolean isRelated(IndividualExpression I, ObjectRoleExpression R,
            IndividualExpression J) {
        realiseKB();
        Individual i = getIndividual(I, "Individual name expected in the isRelated()");
        Role r = getRole(R, "Role expression expected in the isRelated()");
        if (r.isDataRole()) {
            return false;
        }
        Individual j = getIndividual(J, "Individual name expected in the isRelated()");
        List<Individual> vec = getRelated(i, r);
        for (Individual p : vec) {
            if (j.equals(p)) {
                return true;
            }
        }
        return false;
    }

    private ConjunctiveQueryFolding conjunctiveQueryFolding = new ConjunctiveQueryFolding();

    /** call to underlying conjunctive query folding */
    @Original
    public void evaluateQuery(MultiMap<String, ConceptExpression> query) {
        conjunctiveQueryFolding.evaluateQuery(query, this);
    }
}
