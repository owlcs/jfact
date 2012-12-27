package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.DLTree.*;
import static uk.ac.manchester.cs.jfact.kernel.CacheStatus.*;
import static uk.ac.manchester.cs.jfact.kernel.KBStatus.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.datatypes.LiteralEntry;
import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.actors.Actor;
import uk.ac.manchester.cs.jfact.kernel.actors.RIActor;
import uk.ac.manchester.cs.jfact.kernel.actors.SupConceptActor;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptTop;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.split.*;
import conformance.PortedFrom;

@PortedFrom(file = "Kernel.h", name = "ReasoningKernel")
public class ReasoningKernel {
    /** options for the kernel and all related substructures */
    private JFactReasonerConfiguration kernelOptions;
    /** local TBox (to be created) */
    private TBox pTBox;
    /** set of axioms */
    private Ontology ontology = new Ontology();
    /** expression translator to work with queries */
    private ExpressionTranslator pET;
    // Top/Bottom role names: if set, they will appear in all hierarchy-related
    // output
    /** top object role name */
    private String topORoleName;
    /** bottom object role name */
    private String botORoleName;
    /** top data role name */
    private String topDRoleName;
    /** bottom data role name */
    private String botDRoleName;
    // values to propagate to the new KB in case of clearance
    private AtomicBoolean interrupted;

    public void setInterruptedSwitch(AtomicBoolean b) {
        interrupted = b;
    }

    // reasoning cache
    /** cache level */
    private CacheStatus cacheLevel;
    /** cached query concept description */
    private DLTree cachedQueryTree;
    /** cached concept (either defConcept or existing one) */
    private Concept cachedConcept;
    /** cached query result (taxonomy position) */
    private TaxonomyVertex cachedVertex;
    // internal flags
    /** set if TBox throws an exception during preprocessing/classification */
    private boolean reasoningFailed;
    /** trace vector for the last operation (set from the TBox trace-sets) */
    private List<Axiom> traceVec = new ArrayList<Axiom>();
    /** flag to gather trace information for the next reasoner's call */
    private boolean needTracing;
    private DatatypeFactory datatypeFactory;
    // types for knowledge exploration
    /** dag-2-interface translator used in knowledge exploration */
    // TDag2Interface D2I;
    /** knowledge exploration support */
    KnowledgeExplorer KE;
    /** atomic decomposer */
    AtomicDecomposer AD;
    /** syntactic locality based module extractor */
    TModularizer ModSyn = null;
    /** semantic locality based module extractor */
    TModularizer ModSem = null;
    /** set to return by the locality checking procedure */
    Set<Axiom> Result = new HashSet<Axiom>();
    /** cached query input description */
    ConceptExpression cachedQuery;
    private boolean useAxiomSplitting;
    /** ignore cache for the TExpr* (useful for semantic AD) */
    boolean ignoreExprCache = false;

    // -----------------------------------------------------------------------------
    // -- internal query cache manipulation
    // -----------------------------------------------------------------------------
    /** clear query cache */
    @PortedFrom(file = "Kernel.h", name = "clearQueryCache")
    void clearQueryCache() {
        cachedQuery = null; // deleteTree(cachedQueryTree);
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

    /** choose whether TExpr cache should be ignored */
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
    private DLTree e(Expression expr) {
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

    @PortedFrom(file = "Kernel.h", name = "needTracing")
    public void needTracing() {
        needTracing = true;
    }

    /** @return the trace-set of the last reasoning operation */
    @PortedFrom(file = "Kernel.h", name = "getTrace")
    public List<Axiom> getTrace() {
        List<Axiom> toReturn = new ArrayList<Axiom>(traceVec);
        traceVec.clear();
        return toReturn;
    }

    /** set the signature of the expression translator */
    @PortedFrom(file = "Kernel.h", name = "setSignature")
    public void setSignature(TSignature sig) {
        if (pET != null) {
            pET.setSignature(sig);
        }
    }

    /** get RW access to the ontology */
    @PortedFrom(file = "Kernel.h", name = "getOntology")
    Ontology getOntology() {
        return ontology;
    }

    /** axiom C = C1 or ... or Cn; C1 != ... != Cn */
    @PortedFrom(file = "Kernel.h", name = "disjointUnion")
    public Axiom disjointUnion(OWLAxiom ax, ConceptExpression C, List<Expression> l) {
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

    // -----------------------------------------------------------------------------
    // -- internal reasoning methods
    // -----------------------------------------------------------------------------
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

    /** helper; @return true iff C is either named concept of Top/Bot */
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

    @PortedFrom(file = "Kernel.h", name = "getModExtractor")
    public TModularizer getModExtractor(boolean useSemantic) {
        if (useSemantic) {
            if (ModSem == null) {
                TModularizer Mod = new TModularizer(kernelOptions,
                        new SemanticLocalityChecker(this));
                Mod.preprocessOntology(getOntology().getAxioms());
                ModSem = Mod;
            }
            return ModSem;
        }
        if (ModSyn == null) {
            TModularizer Mod = new TModularizer(kernelOptions,
                    new SyntacticLocalityChecker());
            Mod.preprocessOntology(getOntology().getAxioms());
            ModSyn = Mod;
        }
        return ModSyn;
    }

    /** get a set of axioms that corresponds to the atom with the id INDEX */
    @PortedFrom(file = "Kernel.h", name = "getModule")
    public List<Axiom> getModule(List<Expression> signature, boolean useSemantic,
            ModuleType type) {
        // init signature
        TSignature Sig = new TSignature();
        Sig.setLocality(false);
        for (Expression q : signature) {
            if (q instanceof NamedEntity) {
                Sig.add((NamedEntity) q);
            }
        }
        TModularizer Mod = getModExtractor(useSemantic);
        Mod.extract(getOntology().getAxioms(), Sig, type);
        return Mod.getModule();
    }

    /** get a set of axioms that corresponds to the atom with the id INDEX */
    @PortedFrom(file = "Kernel.h", name = "getNonLocal")
    public Set<Axiom> getNonLocal(List<Expression> signature, boolean useSemantic,
            ModuleType type) {
        // init signature
        TSignature Sig = new TSignature();
        Sig.setLocality(type == ModuleType.M_TOP);
        for (Expression q : signature) {
            if (q instanceof NamedEntity) {
                Sig.add((NamedEntity) q);
            }
        }
        // do check
        LocalityChecker LC = getModExtractor(useSemantic).getLocalityChecker();
        LC.setSignatureValue(Sig);
        Result.clear();
        for (Axiom p : getOntology().getAxioms()) {
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
                return C == D; // 2 fresh concepts subsumes one another iff they
                               // are the same
            } else {
                // C is known
                return !getTBox().isSatisfiable(C); // C [= D iff C=\bottom
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
            tax.clearCheckedLabel();
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
    private TBox getTBox() {
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
    private JFactReasonerConfiguration getOptions() {
        return kernelOptions;
    }

    /** return classification status of KB */
    @PortedFrom(file = "Kernel.h", name = "isKBPreprocessed")
    public boolean isKBPreprocessed() {
        return getStatus().ordinal() >= kbCChecked.ordinal();
    }

    /** return classification status of KB */
    @PortedFrom(file = "Kernel.h", name = "isKBClassified")
    public boolean isKBClassified() {
        return getStatus().ordinal() >= kbClassified.ordinal();
    }

    /** return realistion status of KB */
    @PortedFrom(file = "Kernel.h", name = "isKBRealised")
    public boolean isKBRealised() {
        return getStatus().ordinal() >= kbRealised.ordinal();
    }

    /** set top/bottom role names to use them in the related output */
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
     * TBox */
    @PortedFrom(file = "Kernel.h", name = "writeReasoningResult")
    public void writeReasoningResult(LogAdapter o, long time) {
        getTBox().clearQueryConcept(); // get rid of the query leftovers
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
            // DLTreeFactory.buildTree(new Lexeme(R
            // .isDataRole() ? Token.DNAME : Token.RNAME,
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

    /** @return true if R [= S wrt ontology */
    // bool checkRoleSubsumption ( TRole* R, TRole* S )
    // {
    // if ( unlikely ( R->isDataRole() != S->isDataRole() ) )
    // return false;
    /** / R [= S iff \ER.C and \AS.(not C) is unsatisfiable */
    // DLTree* tmp = createSNFForall ( createRole(S),
    // createSNFNot(getFreshFiller(S)) );
    // tmp = createSNFAnd ( createSNFExists ( createRole(R), getFreshFiller(R)
    // ), tmp );
    // return !checkSatTree(tmp);
    // }
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

    /** get access to an expression manager */
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

    /** reset current KB */
    @PortedFrom(file = "Kernel.h", name = "clearKB")
    public boolean clearKB() {
        if (pTBox == null) {
            return true;
        }
        return releaseKB() || newKB();
    }

    // TELLS interface
    // Declaration axioms
    /** axiom declare(x) */
    @PortedFrom(file = "Kernel.h", name = "declare")
    public Axiom declare(OWLAxiom ax, Expression C) {
        return ontology.add(new AxiomDeclaration(ax, C));
    }

    // Concept axioms
    /** axiom C [= D */
    @PortedFrom(file = "Kernel.h", name = "impliesConcepts")
    public Axiom impliesConcepts(OWLAxiom ax, ConceptExpression C, ConceptExpression D) {
        return ontology.add(new AxiomConceptInclusion(ax, C, D));
    }

    /** axiom C1 = ... = Cn */
    @PortedFrom(file = "Kernel.h", name = "equalConcepts")
    public Axiom equalConcepts(OWLAxiom ax, List<Expression> l) {
        return ontology.add(new AxiomEquivalentConcepts(ax, l));
    }

    /** axiom C1 != ... != Cn */
    @PortedFrom(file = "Kernel.h", name = "disjointConcepts")
    public Axiom disjointConcepts(OWLAxiom ax, List<Expression> l) {
        return ontology.add(new AxiomDisjointConcepts(ax, l));
    }

    // Role axioms
    /** R = Inverse(S) */
    @PortedFrom(file = "Kernel.h", name = "setInverseRoles")
    public Axiom setInverseRoles(OWLAxiom ax, ObjectRoleExpression R,
            ObjectRoleExpression S) {
        return ontology.add(new AxiomRoleInverse(ax, R, S));
    }

    /** axiom (R [= S) */
    @PortedFrom(file = "Kernel.h", name = "impliesORoles")
    public Axiom impliesORoles(OWLAxiom ax, ObjectRoleComplexExpression R,
            ObjectRoleExpression S) {
        return ontology.add(new AxiomORoleSubsumption(ax, R, S));
    }

    /** axiom (R [= S) */
    @PortedFrom(file = "Kernel.h", name = "impliesDRoles")
    public Axiom impliesDRoles(OWLAxiom ax, DataRoleExpression R, DataRoleExpression S) {
        return ontology.add(new AxiomDRoleSubsumption(ax, R, S));
    }

    /** axiom R1 = R2 = ... */
    @PortedFrom(file = "Kernel.h", name = "equalORoles")
    public Axiom equalORoles(OWLAxiom ax, List<Expression> l) {
        return ontology.add(new AxiomEquivalentORoles(ax, l));
    }

    /** axiom R1 = R2 = ... */
    @PortedFrom(file = "Kernel.h", name = "equalDRoles")
    public Axiom equalDRoles(OWLAxiom ax, List<Expression> l) {
        return ontology.add(new AxiomEquivalentDRoles(ax, l));
    }

    /** axiom R1 != R2 != ... */
    @PortedFrom(file = "Kernel.h", name = "disjointORoles")
    public Axiom disjointORoles(OWLAxiom ax, List<Expression> l) {
        return ontology.add(new AxiomDisjointORoles(ax, l));
    }

    /** axiom R1 != R2 != ... */
    @PortedFrom(file = "Kernel.h", name = "disjointDRoles")
    public Axiom disjointDRoles(OWLAxiom ax, List<Expression> l) {
        return ontology.add(new AxiomDisjointDRoles(ax, l));
    }

    /** Domain (R C) */
    @PortedFrom(file = "Kernel.h", name = "setODomain")
    public Axiom setODomain(OWLAxiom ax, ObjectRoleExpression R, ConceptExpression C) {
        return ontology.add(new AxiomORoleDomain(ax, R, C));
    }

    /** Domain (R C) */
    @PortedFrom(file = "Kernel.h", name = "setDDomain")
    public Axiom setDDomain(OWLAxiom ax, DataRoleExpression R, ConceptExpression C) {
        return ontology.add(new AxiomDRoleDomain(ax, R, C));
    }

    /** Range (R C) */
    @PortedFrom(file = "Kernel.h", name = "setORange")
    public Axiom setORange(OWLAxiom ax, ObjectRoleExpression R, ConceptExpression C) {
        return ontology.add(new AxiomORoleRange(ax, R, C));
    }

    /** Range (R E) */
    @PortedFrom(file = "Kernel.h", name = "setDRange")
    public Axiom setDRange(OWLAxiom ax, DataRoleExpression R, DataExpression E) {
        return ontology.add(new AxiomDRoleRange(ax, R, E));
    }

    /** Transitive (R) */
    @PortedFrom(file = "Kernel.h", name = "setTransitive")
    public Axiom setTransitive(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomRoleTransitive(ax, R));
    }

    /** Reflexive (R) */
    @PortedFrom(file = "Kernel.h", name = "setReflexive")
    public Axiom setReflexive(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomRoleReflexive(ax, R));
    }

    /** Irreflexive (R): Domain(R) = \neg ER.Self */
    @PortedFrom(file = "Kernel.h", name = "setIrreflexive")
    public Axiom setIrreflexive(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomRoleIrreflexive(ax, R));
    }

    /** Symmetric (R): R [= R^- */
    @PortedFrom(file = "Kernel.h", name = "setSymmetric")
    public Axiom setSymmetric(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomRoleSymmetric(ax, R));
    }

    /** AntySymmetric (R): disjoint(R,R^-) */
    @PortedFrom(file = "Kernel.h", name = "setAsymmetric")
    public Axiom setAsymmetric(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomRoleAsymmetric(ax, R));
    }

    /** Functional (R) */
    @PortedFrom(file = "Kernel.h", name = "setOFunctional")
    public Axiom setOFunctional(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomORoleFunctional(ax, R));
    }

    /** Functional (R) */
    @PortedFrom(file = "Kernel.h", name = "setDFunctional")
    public Axiom setDFunctional(OWLAxiom ax, DataRoleExpression R) {
        return ontology.add(new AxiomDRoleFunctional(ax, R));
    }

    /** InverseFunctional (R) */
    @PortedFrom(file = "Kernel.h", name = "setInverseFunctional")
    public Axiom setInverseFunctional(OWLAxiom ax, ObjectRoleExpression R) {
        return ontology.add(new AxiomRoleInverseFunctional(ax, R));
    }

    // Individual axioms
    /** axiom I e C */
    @PortedFrom(file = "Kernel.h", name = "instanceOf")
    public Axiom instanceOf(OWLAxiom ax, IndividualExpression I, ConceptExpression C) {
        return ontology.add(new AxiomInstanceOf(ax, I, C));
    }

    /** axiom <I,J>:R */
    @PortedFrom(file = "Kernel.h", name = "relatedTo")
    public Axiom relatedTo(OWLAxiom ax, IndividualExpression I, ObjectRoleExpression R,
            IndividualExpression J) {
        return ontology.add(new AxiomRelatedTo(ax, I, R, J));
    }

    /** axiom <I,J>:\neg R */
    @PortedFrom(file = "Kernel.h", name = "relatedToNot")
    public Axiom relatedToNot(OWLAxiom ax, IndividualExpression I,
            ObjectRoleExpression R, IndividualExpression J) {
        return ontology.add(new AxiomRelatedToNot(ax, I, R, J));
    }

    /** axiom (value I A V) */
    @PortedFrom(file = "Kernel.h", name = "valueOf")
    public Axiom valueOf(OWLAxiom ax, IndividualExpression I, DataRoleExpression A,
            Literal<?> V) {
        return ontology.add(new AxiomValueOf(ax, I, A, V));
    }

    /** axiom <I,V>:\neg A */
    @PortedFrom(file = "Kernel.h", name = "valueOfNot")
    public Axiom valueOfNot(OWLAxiom ax, IndividualExpression I, DataRoleExpression A,
            Literal<?> V) {
        return ontology.add(new AxiomValueOfNot(ax, I, A, V));
    }

    /** same individuals */
    @PortedFrom(file = "Kernel.h", name = "processSame")
    public Axiom processSame(OWLAxiom ax, List<Expression> l) {
        return ontology.add(new AxiomSameIndividuals(ax, l));
    }

    /** different individuals */
    @PortedFrom(file = "Kernel.h", name = "processDifferent")
    public Axiom processDifferent(OWLAxiom ax, List<Expression> l) {
        return ontology.add(new AxiomDifferentIndividuals(ax, l));
    }

    /** let all concept expressions in the ArgQueue to be fairness constraints */
    @PortedFrom(file = "Kernel.h", name = "setFairnessConstraint")
    public Axiom setFairnessConstraint(OWLAxiom ax, List<Expression> l) {
        return ontology.add(new AxiomFairnessConstraint(ax, l));
    }

    /** retract an axiom */
    @PortedFrom(file = "Kernel.h", name = "retract")
    public void retract(Axiom axiom) {
        ontology.retract(axiom);
    }

    // * ASK part
    /*
     * Before execution of any query the Kernel make sure that the KB is in an
     * appropriate state: Preprocessed, Classified or Realised. If the ontology
     * was changed between asks, incremental classification is performed and the
     * corrected result is returned.
     */
    /** return consistency status of KB */
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
    /** @return true iff object role is functional */
    @PortedFrom(file = "Kernel.h", name = "isFunctional")
    public boolean isFunctional(ObjectRoleExpression R) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role r = getRole(R, "Role expression expected in isFunctional()");
        if (r.isTop()) {
            return true; // universal role is symmetric
        }
        if (r.isBottom()) {
            return true; // empty role is symmetric
        }
        return getFunctionality(r);
    }

    /** @return true iff data role is functional */
    @PortedFrom(file = "Kernel.h", name = "isFunctional")
    public boolean isFunctional(DataRoleExpression R) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role r = getRole(R, "Role expression expected in isFunctional()");
        if (r.isTop()) {
            return true; // universal role is symmetric
        }
        if (r.isBottom()) {
            return true; // empty role is symmetric
        }
        return getFunctionality(r);
    }

    /** @return true iff role is inverse-functional */
    @PortedFrom(file = "Kernel.h", name = "isInverseFunctional")
    public boolean isInverseFunctional(ObjectRoleExpression R) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role r = getRole(R, "Role expression expected in isInverseFunctional()")
                .inverse();
        if (r.isTop()) {
            return true; // universal role is symmetric
        }
        if (r.isBottom()) {
            return true; // empty role is symmetric
        }
        return getFunctionality(r);
    }

    /** @return true iff role is transitive */
    @PortedFrom(file = "Kernel.h", name = "isTransitive")
    public boolean isTransitive(ObjectRoleExpression R) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role r = getRole(R, "Role expression expected in isTransitive()");
        if (r.isTop()) {
            return true; // universal role is symmetric
        }
        if (r.isBottom()) {
            return true; // empty role is symmetric
        }
        if (!r.isTransitivityKnown()) {
            r.setTransitive(checkTransitivity(e(R)));
        }
        return r.isTransitive();
    }

    /** @return true iff role is symmetric */
    @PortedFrom(file = "Kernel.h", name = "isSymmetric")
    public boolean isSymmetric(ObjectRoleExpression R) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role r = getRole(R, "Role expression expected in isSymmetric()");
        if (r.isTop()) {
            return true; // universal role is symmetric
        }
        if (r.isBottom()) {
            return true; // empty role is symmetric
        }
        if (!r.isSymmetryKnown()) {
            r.setSymmetric(checkSymmetry(e(R)));
        }
        return r.isSymmetric();
    }

    /** @return true iff role is asymmetric */
    @PortedFrom(file = "Kernel.h", name = "isAsymmetric")
    public boolean isAsymmetric(ObjectRoleExpression R) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role r = getRole(R, "Role expression expected in isAsymmetric()");
        if (r.isTop()) {
            return true; // universal role is symmetric
        }
        if (r.isBottom()) {
            return true; // empty role is symmetric
        }
        if (!r.isAsymmetryKnown()) {
            r.setAsymmetric(getTBox().isDisjointRoles(r, r.inverse()));
        }
        return r.isAsymmetric();
    }

    /** @return true iff role is reflexive */
    @PortedFrom(file = "Kernel.h", name = "isReflexive")
    public boolean isReflexive(ObjectRoleExpression R) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role r = getRole(R, "Role expression expected in isReflexive()");
        if (r.isTop()) {
            return true; // universal role is symmetric
        }
        if (r.isBottom()) {
            return true; // empty role is symmetric
        }
        if (!r.isReflexivityKnown()) {
            r.setReflexive(checkReflexivity(e(R)));
        }
        return r.isReflexive();
    }

    /** @return true iff role is irreflexive */
    @PortedFrom(file = "Kernel.h", name = "isIrreflexive")
    public boolean isIrreflexive(ObjectRoleExpression R) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role r = getRole(R, "Role expression expected in isIrreflexive()");
        if (r.isTop()) {
            return true; // universal role is symmetric
        }
        if (r.isBottom()) {
            return true; // empty role is symmetric
        }
        if (!r.isIrreflexivityKnown()) {
            r.setIrreflexive(getTBox().isIrreflexive(r));
        }
        return r.isIrreflexive();
    }

    /** @return true iff two roles are disjoint */
    @PortedFrom(file = "Kernel.h", name = "isDisjointRoles")
    public boolean isDisjointRoles(ObjectRoleExpression R, ObjectRoleExpression S) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role r = getRole(R, "Role expression expected in isDisjointRoles()");
        Role s = getRole(S, "Role expression expected in isDisjointRoles()");
        if (r.isTop() || s.isTop()) {
            return false; // universal role is not disjoint with anything
        }
        if (r.isBottom() || s.isBottom()) {
            return true; // empty role is disjoint with everything
        }
        return getTBox().isDisjointRoles(r, s);
    }

    /** @return true iff two roles are disjoint */
    @PortedFrom(file = "Kernel.h", name = "isDisjointRoles")
    public boolean isDisjointRoles(DataRoleExpression R, DataRoleExpression S) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role r = getRole(R, "Role expression expected in isDisjointRoles()");
        Role s = getRole(S, "Role expression expected in isDisjointRoles()");
        if (r.isTop() || s.isTop()) {
            return false; // universal role is not disjoint with anything
        }
        if (r.isBottom() || s.isBottom()) {
            return true; // empty role is disjoint with everything
        }
        return getTBox().isDisjointRoles(r, s);
    }

    /** @return true if R is a sub-role of S */
    @PortedFrom(file = "Kernel.h", name = "isSubRoles")
    public boolean isSubRoles(ObjectRoleComplexExpression R, ObjectRoleExpression S) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role r = getRole(R, "Role expression expected in isSubRoles()");
        Role s = getRole(S, "Role expression expected in isSubRoles()");
        if (r.isBottom() || s.isTop()) {
            return true; // \bot [= X [= \top
        }
        if (r.isTop() && s.isBottom()) {
            return false; // as \top [= \bot leads to inconsistent ontology
        }
        if (getExpressionManager().isEmptyRole(R)
                || getExpressionManager().isUniversalRole(S)) {
            return true; // \bot [= X [= \top
        }
        if (getExpressionManager().isUniversalRole(R)
                && getExpressionManager().isEmptyRole(S)) {
            return false; // as \top [= \bot leads to inconsistent ontology
        }
        // told case first
        if (!r.isTop() && !s.isBottom() && r.lesserequal(s)) {
            return true;
        }
        // check the general case
        // FIXME!! cache it later
        // DLTree r = e(R), s = e(S);
        return checkRoleSubsumption(r, s);
    }

    // single satisfiability
    /** @return true iff C is satisfiable */
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

    /** @return true iff C [= D holds */
    @PortedFrom(file = "Kernel.h", name = "isSubsumedBy")
    public boolean isSubsumedBy(ConceptExpression C, ConceptExpression D) {
        preprocessKB();
        if (this.isNameOrConst(D) && this.isNameOrConst(C)) {
            return this.checkSub(getTBox().getCI(e(C)), getTBox().getCI(e(D)));
        }
        DLTree nD = DLTreeFactory.createSNFNot(e(D));
        return !checkSatTree(DLTreeFactory.createSNFAnd(e(C), nD));
        // return !this.checkSat(this.getExpressionManager().and(C,
        // this.getExpressionManager().not(D)));
    }

    /** @return true iff C is disjoint with D; that is, C [= \not D holds */
    @PortedFrom(file = "Kernel.h", name = "isDisjoint")
    public boolean isDisjoint(ConceptExpression C, ConceptExpression D) {
        return isSubsumedBy(C, getExpressionManager().not(D));
    }

    /** @return true iff C is equivalent to D */
    @PortedFrom(file = "Kernel.h", name = "isEquivalent")
    public boolean isEquivalent(ConceptExpression C, ConceptExpression D) {
        if (C == D) {
            return true;
        }
        preprocessKB();
        if (isKBClassified()) { // try to detect C=D wrt named concepts
            if (this.isNameOrConst(D) && this.isNameOrConst(C)) {
                TaxonomyVertex cV = getTBox().getCI(e(C)).getTaxVertex();
                TaxonomyVertex dV = getTBox().getCI(e(D)).getTaxVertex();
                if (cV == null && dV == null) {
                    return false; // 2 different fresh names
                }
                return cV == dV;
            }
        }
        // not classified or not named constants
        return isSubsumedBy(C, D) && isSubsumedBy(D, C);
    }

    // concept hierarchy
    /** apply actor__apply() to all DIRECT super-concepts of [complex] C */
    @PortedFrom(file = "Kernel.h", name = "getSupConcepts")
    public void getSupConcepts(ConceptExpression C, boolean direct, Actor actor) {
        classifyKB(); // ensure KB is ready to answer the query
        this.setUpCache(C, csClassified);
        Taxonomy tax = getCTaxonomy();
        if (direct) {
            tax.getRelativesInfo(cachedVertex, actor, false, true, true);
        } else {
            tax.getRelativesInfo(cachedVertex, actor, false, false, true);
        }
    }

    /** apply actor__apply() to all DIRECT sub-concepts of [complex] C */
    @PortedFrom(file = "Kernel.h", name = "getSubConcepts")
    public void getSubConcepts(ConceptExpression C, boolean direct, Actor actor) {
        classifyKB(); // ensure KB is ready to answer the query
        this.setUpCache(C, csClassified);
        Taxonomy tax = getCTaxonomy();
        tax.getRelativesInfo(cachedVertex, actor, false, direct, false);
    }

    /** apply actor__apply() to all synonyms of [complex] C */
    @PortedFrom(file = "Kernel.h", name = "getEquivalentConcepts")
    public void getEquivalentConcepts(ConceptExpression C, Actor actor) {
        classifyKB(); // ensure KB is ready to answer the query
        this.setUpCache(C, csClassified);
        actor.apply(cachedVertex);
    }

    /** apply actor::apply() to all named concepts disjoint with [complex] C */
    @PortedFrom(file = "Kernel.h", name = "getDisjointConcepts")
    public void getDisjointConcepts(ConceptExpression C, Actor actor) {
        classifyKB(); // ensure KB is ready to answer the query
        this.setUpCache(getExpressionManager().not(C), csClassified);
        Taxonomy tax = getCTaxonomy();
        // we are looking for all sub-concepts of (not C) (including synonyms to
        // it)
        tax.getRelativesInfo(cachedVertex, actor, true, false, false);
    }

    // role hierarchy
    /** apply actor__apply() to all DIRECT super-roles of [complex] R */
    @PortedFrom(file = "Kernel.h", name = "getSupRoles")
    public void getSupRoles(RoleExpression r, boolean direct, Actor actor) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role R = getRole(r, "Role expression expected in getSupRoles()");
        Taxonomy tax = getTaxonomy(R);
        tax.getRelativesInfo(getTaxVertex(R), actor, false, direct, true);
    }

    /** apply actor__apply() to all DIRECT sub-roles of [complex] R */
    @PortedFrom(file = "Kernel.h", name = "getSubRoles")
    public void getSubRoles(RoleExpression r, boolean direct, Actor actor) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role R = getRole(r, "Role expression expected in getSubRoles()");
        Taxonomy tax = getTaxonomy(R);
        tax.getRelativesInfo(getTaxVertex(R), actor, false, direct, false);
    }

    /** apply actor__apply() to all synonyms of [complex] R */
    @PortedFrom(file = "Kernel.h", name = "getEquivalentRoles")
    public void getEquivalentRoles(RoleExpression r, Actor actor) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role R = getRole(r, "Role expression expected in getEquivalentRoles()");
        actor.apply(getTaxVertex(R));
    }

    // domain and range as a set of named concepts
    /** apply actor__apply() to all DIRECT NC that are in the domain of [complex]
     * R */
    @PortedFrom(file = "Kernel.h", name = "getORoleDomain")
    public void getORoleDomain(ObjectRoleExpression r, boolean direct, Actor actor) {
        classifyKB(); // ensure KB is ready to answer the query
        this.setUpCache(getExpressionManager().exists(r, getExpressionManager().top()),
                csClassified);
        Taxonomy tax = getCTaxonomy();
        tax.getRelativesInfo(cachedVertex, actor, true, direct, true);
    }

    /** apply actor::apply() to all DIRECT NC that are in the domain of data */
    // role R
    // template<class Actor>
    @PortedFrom(file = "Kernel.h", name = "getDRoleDomain")
    void getDRoleDomain(DataRoleExpression r, boolean direct, Actor actor) {
        classifyKB(); // ensure KB is ready to answer the query
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
     * R */
    @PortedFrom(file = "Kernel.h", name = "getRoleRange")
    public void getRoleRange(ObjectRoleExpression r, boolean direct, Actor actor) {
        getORoleDomain(getExpressionManager().inverse(r), direct, actor);
    }

    // instances
    @PortedFrom(file = "Kernel.h", name = "getInstances")
    public void getInstances(ConceptExpression C, Actor actor, boolean direct) {
        if (direct) {
            getDirectInstances(C, actor);
        } else {
            this.getInstances(C, actor);
        }
    }

    /** apply actor__apply() to all direct instances of given [complex] C */
    @PortedFrom(file = "Kernel.h", name = "getDirectInstances")
    public void getDirectInstances(ConceptExpression C, Actor actor) {
        realiseKB(); // ensure KB is ready to answer the query
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

    /** apply actor__apply() to all instances of given [complex] C */
    @PortedFrom(file = "Kernel.h", name = "getInstances")
    public void getInstances(ConceptExpression C, Actor actor) { // FIXME!!
                                                                 // check
                                                                 // for
                                                                 // Racer's/IS
                                                                 // approach
        realiseKB(); // ensure KB is ready to answer the query
        this.setUpCache(C, csClassified);
        Taxonomy tax = getCTaxonomy();
        tax.getRelativesInfo(cachedVertex, actor, true, false, false);
    }

    /** apply actor__apply() to all DIRECT concepts that are types of an
     * individual I */
    @PortedFrom(file = "Kernel.h", name = "getTypes")
    public void getTypes(IndividualExpression I, boolean direct, Actor actor) {
        realiseKB(); // ensure KB is ready to answer the query
        this.setUpCache(getExpressionManager().oneOf(I), csClassified);
        Taxonomy tax = getCTaxonomy();
        tax.getRelativesInfo(cachedVertex, actor, true, direct, true);
    }

    /** apply actor__apply() to all synonyms of an individual I */
    @PortedFrom(file = "Kernel.h", name = "getSameAs")
    public void getSameAs(IndividualExpression I, Actor actor) {
        realiseKB(); // ensure KB is ready to answer the query
        getEquivalentConcepts(getExpressionManager().oneOf(I), actor);
    }

    /** @return true iff I and J refer to the same individual */
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
    /** build a completion tree for a concept expression C (no caching as it */
    // breaks the idea of KE). @return the root node
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
        // return getTBox().buildCompletionTree(cachedConcept);
    }

    public KnowledgeExplorer getKnowledgeExplorer() {
        return KE;
    }

    /** build the set of data neighbours of a NODE, put the set of data roles */
    // into the RESULT variable
    @PortedFrom(file = "Kernel.h", name = "getDataRoles")
    public Set<RoleExpression> getDataRoles(DlCompletionTree node, boolean onlyDet) {
        return KE.getDataRoles(node, onlyDet);
    }

    /** build the set of object neighbours of a NODE, put the set of object */
    // roles and inverses into the RESULT variable
    @PortedFrom(file = "Kernel.h", name = "getObjectRoles")
    public Set<RoleExpression> getObjectRoles(DlCompletionTree node, boolean onlyDet,
            boolean needIncoming) {
        return KE.getObjectRoles(node, onlyDet, needIncoming);
    }

    /** build the set of neighbours of a NODE via role ROLE; put the resulting
     * list into RESULT */
    @PortedFrom(file = "Kernel.h", name = "getNeighbours")
    public List<DlCompletionTree>
            getNeighbours(DlCompletionTree node, RoleExpression role) {
        return KE.getNeighbours(node,
                getRole(role, "Role expression expected in getNeighbours() method"));
    }

    /** put into RESULT all the expressions from the NODE label; if ONLYDET is */
    // true, return only deterministic elements
    public List<ConceptExpression> getObjectLabel(DlCompletionTree node, boolean onlyDet) {
        return KE.getObjectLabel(node, onlyDet);
    }

    public List<DataExpression> getDataLabel(DlCompletionTree node, boolean onlyDet) {
        return KE.getDataLabel(node, onlyDet);
    }

    // ----------------------------------------------------------------------------------
    // atomic decomposition queries
    // ----------------------------------------------------------------------------------
    /** @return true iff individual I is instance of given [complex] C */
    @PortedFrom(file = "Kernel.h", name = "isInstance")
    public boolean isInstance(IndividualExpression I, ConceptExpression C) {
        realiseKB(); // ensure KB is ready to answer the query
        getIndividual(I, "individual name expected in the isInstance()");
        // FIXME!! this way a new concept is created; could be done more optimal
        return isSubsumedBy(getExpressionManager().oneOf(I), C);
    }

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

    /** try to perform the incremental reasoning on the changed ontology */
    @PortedFrom(file = "Kernel.h", name = "tryIncremental")
    private boolean tryIncremental() {
        if (pTBox == null) {
            return true;
        }
        if (!ontology.isChanged()) {
            return false;
        }
        return true;
    }

    /** force the re-classification of the changed ontology */
    @PortedFrom(file = "Kernel.h", name = "forceReload")
    private void forceReload() {
        clearTBox();
        newKB();
        // Protege (as the only user of non-trivial monitors with reload) does
        // not accept multiple usage of a monitor
        // so switch it off after the 1st usage
        // if (kernelOptions.isUseELReasoner()) {
        // ELFAxiomChecker ac = new ELFAxiomChecker();
        // ac.visitOntology(ontology);
        // if (ac.value()) {
        // ELFNormalizer normalizer = new ELFNormalizer(getExpressionManager());
        // normalizer.visitOntology(ontology);
        // ELFReasoner reasoner = new ELFReasoner(ontology);
        // reasoner.classify();
        // }
        // }
        // split ontological axioms
        if (kernelOptions.isSplits()) {
            TAxiomSplitter AxiomSplitter = new TAxiomSplitter(kernelOptions, ontology);
            AxiomSplitter.buildSplit();
        }
        OntologyLoader OntologyLoader = new OntologyLoader(getTBox());
        OntologyLoader.visitOntology(ontology);
        ontology.setProcessed();
    }

    // ----------------------------------------------------------------------------------
    // knowledge exploration queries
    // ----------------------------------------------------------------------------------
    /** add the role R and all its supers to a set RESULT */
    // void addRoleWithSupers ( Role R, TCGRoleSet Result );
    @PortedFrom(file = "Kernel.h", name = "processKB")
    private void processKB(KBStatus status) {
        assert status.ordinal() >= kbCChecked.ordinal();
        // check whether reasoning was failed
        if (reasoningFailed) {
            throw new ReasonerInternalException(
                    "Can't classify KB because of previous errors");
        }
        // check if something have to be done
        if (getStatus().ordinal() >= status.ordinal()) { // nothing to do;
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
                break; // need to do the whole cycle -- just after the switch
            case kbCChecked: {
                classify(status);
                stillGo = false;
                break; // do classification
            }
            case kbClassified: {
                realise();
                stillGo = false;
                break;
            } // do realisation
            default: // nothing should be here
                throw new UnreachableSituationException();
        }
        if (stillGo) {
            // start with loading and preprocessing -- here might be a failures
            reasoningFailed = true;
            // load the axioms from the ontology to the TBox
            if (tryIncremental()) {
                forceReload();
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
    private void realise() {
        if (!pTBox.isConsistent()) {
            return;
        }
        pTBox.performRealisation();
    }

    // -----------------------------------------------------------------------------
    // -- query caching support
    // -----------------------------------------------------------------------------
    /** classify query; cache is ready at the point. NAMED means whether */
    // concept is just a name
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
        if (this.checkQueryCache(query)) { // ... with the same level -- nothing
                                           // to do
                                           // deleteTree(query);
                                           // query=null;
            if (level.ordinal() <= cacheLevel.ordinal()) {
                return;
            } else { // concept was defined but not classified yet
                assert level == csClassified && cacheLevel != csClassified;
                if (cacheLevel == csSat) // already check satisfiability
                {
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
        } else // case of complex query
        {
            // getTBox().clearQueryConcept();
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
        if (this.checkQueryCache(query)) { // ... with the same level -- nothing
                                           // to do
            if (level.ordinal() <= cacheLevel.ordinal()) {
                return;
            } else { // concept was defined but not classified yet
                assert level == csClassified && cacheLevel != csClassified;
                if (cacheLevel == csSat) // already check satisfiability
                {
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
        } else // case of complex query
        {
            // need to clear the query before transform it into DLTree
            // getTBox().clearQueryConcept();
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

    // ----------------------------------------------------------------------------------
    // atomic decomposition queries
    // ----------------------------------------------------------------------------------
    /** create new atomic decomposition of the loaded ontology using TYPE. */
    // @return size of the AD
    @PortedFrom(file = "Kernel.h", name = "getAtomicDecompositionSize")
    public int getAtomicDecompositionSize(boolean useSemantics, ModuleType type) {
        // init AD field
        if (AD == null) {
            AD = new AtomicDecomposer(getModExtractor(useSemantics));
        }
        return AD.getAOS(ontology, type).size();
    }

    /** get a set of axioms that corresponds to the atom with the id INDEX */
    @PortedFrom(file = "Kernel.h", name = "getAtomAxioms")
    public Set<Axiom> getAtomAxioms(int index) {
        return AD.getAOS().get(index).getAtomAxioms();
    }

    public List<Axiom> getTautologies() {
        return AD.getTautologies();
    }

    /** get a set of axioms that corresponds to the module of the atom with the */
    // id INDEX
    @PortedFrom(file = "Kernel.h", name = "getAtomModule")
    public Set<Axiom> getAtomModule(int index) {
        return AD.getAOS().get(index).getModule();
    }

    /** get a set of atoms on which atom with index INDEX depends */
    @PortedFrom(file = "Kernel.h", name = "getAtomDependents")
    public Set<TOntologyAtom> getAtomDependents(int index) {
        return AD.getAOS().get(index).getDepAtoms();
    }

    // ----------------------------------------------------------------------------------
    // knowledge exploration queries
    // ----------------------------------------------------------------------------------
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

    /** @return true if R is a super-role of a chain holding in the args */
    @PortedFrom(file = "Kernel.h", name = "isSubChain")
    public boolean
            isSubChain(ObjectRoleComplexExpression R, List<ObjectRoleExpression> l) {
        preprocessKB(); // ensure KB is ready to answer the query
        Role r = getRole(R, "Role expression expected in isSubChain()");
        if (r.isTop()) {
            return true; // universal role is a super of any chain
        }
        return checkSubChain(r, l);
    }

    /** @return true if R is a sub-role of S */
    @PortedFrom(file = "Kernel.h", name = "isSubRoles")
    public boolean isSubRoles(DataRoleExpression R, DataRoleExpression S) {
        preprocessKB(); // ensure KB is ready to answer the query
        if (getExpressionManager().isEmptyRole(R)
                || getExpressionManager().isUniversalRole(S)) {
            return true; // \bot [= X [= \top
        }
        if (getExpressionManager().isUniversalRole(R)
                && getExpressionManager().isEmptyRole(S)) {
            return false; // as \top [= \bot leads to inconsistent ontology
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

    // all-disjoint query implementation
    @PortedFrom(file = "Kernel.h", name = "isDisjointRoles")
    public boolean isDisjointRoles(List<Expression> l) {
        // grab all roles from the arg-list
        // List<TDLExpression> Disj = getExpressionManager().getArgList();
        List<Role> Roles = new ArrayList<Role>(l.size());
        int nTopRoles = 0;
        for (Expression p : l) {
            if (p instanceof ObjectRoleExpression) {
                Role R = getRole((RoleExpression) p,
                        "Role expression expected in isDisjointRoles()");
                if (R.isBottom()) {
                    continue;
                }
                if (R.isTop()) {
                    nTopRoles++;
                } else {
                    Roles.add(R);
                }
            } else {
                throw new ReasonerInternalException(
                        "Role expression expected in isDisjointRoles()");
            }
        }
        // deal with top-roles
        if (nTopRoles > 0) {
            if (nTopRoles > 1 || !Roles.isEmpty()) {
                return false;   // universal role is not disjoint with anything
                              // but the bottom role
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

    public boolean isDisjointDataRoles(List<DataRoleExpression> l) {
        // grab all roles from the arg-list
        List<Role> Roles = new ArrayList<Role>(l.size());
        for (DataRoleExpression p : l) {
            if (getExpressionManager().isUniversalRole(p)) {
                return false; // universal role is not disjoint with anything
            }
            if (getExpressionManager().isEmptyRole(p)) {
                continue; // empty role is disjoint with everything
            }
            Roles.add(getRole(p, "Role expression expected in isDisjointRoles()"));
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

    public boolean isDisjointObjectRoles(List<ObjectRoleExpression> l) {
        // grab all roles from the arg-list
        // List<TDLExpression> Disj = getExpressionManager().getArgList();
        List<Role> Roles = new ArrayList<Role>(l.size());
        for (ObjectRoleExpression p : l) {
            if (getExpressionManager().isUniversalRole(p)) {
                return false; // universal role is not disjoint with anything
            }
            if (getExpressionManager().isEmptyRole(p)) {
                continue; // empty role is disjoint with everything
            }
            Roles.add(getRole(p, "Role expression expected in isDisjointRoles()"));
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

    @PortedFrom(file = "Kernel.h", name = "getRoleFillers")
    public List<Individual>
            getRoleFillers(IndividualExpression I, ObjectRoleExpression R) {
        realiseKB();
        return getRelated(
                getIndividual(I, "Individual name expected in the getRoleFillers()"),
                getRole(R, "Role expression expected in the getRoleFillers()"));
    }

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
}

