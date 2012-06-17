package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.DLTree.equalTrees;
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
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomConceptInclusion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleDomain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleRange;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleSubsumption;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDeclaration;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDifferentIndividuals;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointDRoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointORoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointUnion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentDRoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentORoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomFairnessConstraint;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomInstanceOf;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleDomain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleRange;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleSubsumption;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRelatedTo;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRelatedToNot;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleAsymmetric;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleInverseFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleIrreflexive;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleReflexive;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleSymmetric;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleTransitive;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomSameIndividuals;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomValueOf;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomValueOfNot;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleComplexExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.RoleExpression;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.split.AtomicDecomposer;
import uk.ac.manchester.cs.jfact.split.KnowledgeExplorer;
import uk.ac.manchester.cs.jfact.split.LocalityChecker;
import uk.ac.manchester.cs.jfact.split.ModuleType;
import uk.ac.manchester.cs.jfact.split.SemanticLocalityChecker;
import uk.ac.manchester.cs.jfact.split.SigIndex;
import uk.ac.manchester.cs.jfact.split.SyntacticLocalityChecker;
import uk.ac.manchester.cs.jfact.split.TAxiomSplitter;
import uk.ac.manchester.cs.jfact.split.TModularizer;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;
import uk.ac.manchester.cs.jfact.split.TSignature;

public final class ReasoningKernel {
	/** options for the kernel and all related substructures */
	private final JFactReasonerConfiguration kernelOptions;
	/** local TBox (to be created) */
	private TBox pTBox;
	/** set of axioms */
	private final Ontology ontology = new Ontology();
	/** expression translator to work with queries */
	private ExpressionTranslator pET;
	// Top/Bottom role names: if set, they will appear in all hierarchy-related output
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

	public void setInterruptedSwitch(final AtomicBoolean b) {
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
	private final List<Axiom> traceVec = new ArrayList<Axiom>();
	/** flag to gather trace information for the next reasoner's call */
	private boolean needTracing;
	private final DatatypeFactory datatypeFactory;
	// types for knowledge exploration
	/// dag-2-interface translator used in knowledge exploration
	//	TDag2Interface D2I;
	/// knowledge exploration support
	KnowledgeExplorer KE;
	/// atomic decomposer
	AtomicDecomposer AD;
	/// syntactic locality based module extractor
	TModularizer ModSyn;
	/// semantic locality based module extractor
	TModularizer ModSem;
	/// set to return by the locality checking procedure
	Set<Axiom> Result = new HashSet<Axiom>();
	/// cached query input description
	ConceptExpression cachedQuery;
	private final boolean useAxiomSplitting;
	/// ignore cache for the TExpr* (useful for semantic AD)
	boolean ignoreExprCache = false;

	//-----------------------------------------------------------------------------
	//--		internal query cache manipulation
	//-----------------------------------------------------------------------------
	/// clear query cache
	void clearQueryCache() {
		cachedQuery = null; //deleteTree(cachedQueryTree);
		cachedQueryTree = null;
	}

	/// set query cache value to QUERY
	void setQueryCache(final ConceptExpression query) {
		clearQueryCache();
		cachedQuery = query;
	}

	/// set query cache value to QUERY
	void setQueryCache(final DLTree query) {
		clearQueryCache();
		cachedQueryTree = query;
	}

	/// choose whether TExpr cache should be ignored
	public void setIgnoreExprCache(final boolean value) {
		ignoreExprCache = value;
	}

	/// check whether query cache is the same as QUERY
	boolean checkQueryCache(final ConceptExpression query) {
		return ignoreExprCache ? false : cachedQuery == query;
	}

	/// check whether query cache is the same as QUERY
	boolean checkQueryCache(final DLTree query) {
		return equalTrees(cachedQueryTree, query);
	}

	/** get status of the KB */
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
	private DLTree e(final Expression expr) {
		return expr.accept(pET);
	}

	/// get fresh filled depending of a type of R
	private DLTree getFreshFiller(final Role R) {
		if (R.isDataRole()) {
			final LiteralEntry t = new LiteralEntry("freshliteral");
			t.setLiteral(DatatypeFactory.LITERAL.buildLiteral("freshliteral"));
			return DLTreeFactory.wrap(t);
		} else {
			return getTBox().getFreshConcept();
		}
	}

	/// get role expression based on the R
	RoleExpression Role(final Role R) {
		if (R.isDataRole()) {
			return getExpressionManager().dataRole(R.getName());
		} else {
			return getExpressionManager().objectRole(R.getName());
		}
	}

	/** clear cache and flags */
	private void initCacheAndFlags() {
		cacheLevel = csEmpty;
		clearQueryCache();
		cachedConcept = null;
		cachedVertex = null;
		reasoningFailed = false;
		needTracing = false;
	}

	public void needTracing() {
		needTracing = true;
	}

	/** @return the trace-set of the last reasoning operation */
	public List<Axiom> getTrace() {
		List<Axiom> toReturn = new ArrayList<Axiom>(traceVec);
		traceVec.clear();
		return toReturn;
	}

	/// set the signature of the expression translator
	public void setSignature(final TSignature sig) {
		if (pET != null) {
			pET.setSignature(sig);
		}
	}

	/// get RW access to the ontology
	Ontology getOntology() {
		return ontology;
	}

	/** axiom C = C1 or ... or Cn; C1 != ... != Cn */
	public Axiom disjointUnion(final OWLAxiom ax, final ConceptExpression C,
			final List<Expression> l) {
		return ontology.add(new AxiomDisjointUnion(ax, C, l));
	}

	/** get related cache for an individual I */
	private List<Individual> getRelated(final Individual I, final Role R) {
		if (!I.hasRelatedCache(R)) {
			I.setRelatedCache(R, buildRelatedCache(I, R));
		}
		return I.getRelatedCache(R);
	}

	//-----------------------------------------------------------------------------
	//--		internal reasoning methods
	//-----------------------------------------------------------------------------
	/** @return true iff C is satisfiable */
	private boolean checkSatTree(final DLTree C) {
		if (C.isTOP()) {
			return true;
		}
		if (C.isBOTTOM()) {
			return false;
		}
		this.setUpCache(C, csSat);
		return getTBox().isSatisfiable(cachedConcept);
	}

	/// @return true iff C is satisfiable
	boolean checkSat(final ConceptExpression C) {
		this.setUpCache(C, csSat);
		return getTBox().isSatisfiable(cachedConcept);
	}

	/// helper; @return true iff C is either named concept of Top/Bot
	boolean isNameOrConst(final ConceptExpression C) {
		return C instanceof ConceptName || C instanceof ConceptTop
				|| C instanceof ConceptBottom;
	}

	boolean isNameOrConst(final DLTree C) {
		return C.isBOTTOM() || C.isTOP() || C.isName();
	}

	/// @return true iff C [= D holds
	boolean checkSub(final ConceptExpression C, final ConceptExpression D) {
		if (this.isNameOrConst(D) && this.isNameOrConst(C)) {
			return this.checkSub(getTBox().getCI(e(C)), getTBox().getCI(e(D)));
		}
		return !checkSat(getExpressionManager().and(C, getExpressionManager().not(D)));
	}

	public TModularizer getModExtractor(final boolean useSemantic) {
		boolean needInit = false;
		// check whether we need init
		if (useSemantic && ModSem == null) {
			ModSem = new TModularizer(new SemanticLocalityChecker(this, null));
			needInit = true;
		}
		if (!useSemantic && ModSyn == null) {
			ModSyn = new TModularizer(new SyntacticLocalityChecker(null));
			needInit = true;
		}
		// init if necessary
		TModularizer Mod = useSemantic ? ModSem : ModSyn;
		if (needInit) {
			SigIndex SI = new SigIndex();
			SI.processRange(getOntology().getAxioms());
			Mod.setSigIndex(SI);
			Mod.preprocessOntology(getOntology().getAxioms());
		}
		return Mod;
	}

	/// get a set of axioms that corresponds to the atom with the id INDEX
	public List<Axiom> getModule(final List<Expression> signature,
			final boolean useSemantic, final ModuleType type) {
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

	/// get a set of axioms that corresponds to the atom with the id INDEX
	public Set<Axiom> getNonLocal(final List<Expression> signature,
			final boolean useSemantic, final ModuleType type) {
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
	private boolean checkSub(final Concept C, final Concept D) {
		// check whether a concept is fresh
		if (D.getpName() == 0) // D is fresh
		{
			if (C.getpName() == 0) {
				return C == D; // 2 fresh concepts subsumes one another iff they are the same
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

	//	/** @return true iff C [= D holds */
	//	private boolean checkSub(DLTree C, DLTree D) {
	//		if (C.isCN() && D.isCN()) {
	//			return checkSub(getTBox().getCI(C), getTBox().getCI(D));
	//		}
	//		return !checkSatTree(DLTreeFactory.createSNFAnd(C, DLTreeFactory.createSNFNot(D)));
	//	}
	// get access to internal structures
	/** @throw an exception if no TBox found */
	private void checkTBox() {
		if (pTBox == null) {
			throw new ReasonerInternalException("KB Not Initialised");
		}
	}

	/** get RW access to TBox */
	private TBox getTBox() {
		checkTBox();
		return pTBox;
	}

	/** clear TBox and related structures; keep ontology in place */
	private void clearTBox() {
		pTBox = null;
		pET = null;
		//D2I = null;
	}

	/** get RW access to Object RoleMaster from TBox */
	private RoleMaster getORM() {
		return getTBox().getORM();
	}

	/** get RW access to Data RoleMaster from TBox */
	private RoleMaster getDRM() {
		return getTBox().getDRM();
	}

	/** get access to the concept hierarchy */
	private Taxonomy getCTaxonomy() {
		if (!isKBClassified()) {
			throw new ReasonerInternalException(
					"No access to concept taxonomy: ontology not classified");
		}
		return getTBox().getTaxonomy();
	}

	/** get access to the object role hierarchy */
	private Taxonomy getORTaxonomy() {
		if (!isKBPreprocessed()) {
			throw new ReasonerInternalException(
					"No access to the object role taxonomy: ontology not preprocessed");
		}
		return getORM().getTaxonomy();
	}

	/** get access to the data role hierarchy */
	private Taxonomy getDRTaxonomy() {
		if (!isKBPreprocessed()) {
			throw new ReasonerInternalException(
					"No access to the data role taxonomy: ontology not preprocessed");
		}
		return getDRM().getTaxonomy();
	}

	// transformation methods
	/** get individual by the TIndividualExpr */
	private Individual getIndividual(final IndividualExpression i, final String reason) {
		DLTree I = e(i);
		if (I == null) {
			throw new ReasonerInternalException(reason);
		}
		return (Individual) getTBox().getCI(I);
	}

	/** get role by the TRoleExpr */
	private Role getRole(final RoleExpression r, final String reason) {
		return Role.resolveRole(e(r));
	}

	/** get taxonomy of the property wrt it's name */
	private Taxonomy getTaxonomy(final Role R) {
		return R.isDataRole() ? getDRTaxonomy() : getORTaxonomy();
	}

	/** get taxonomy vertext of the property wrt it's name */
	private TaxonomyVertex getTaxVertex(final Role R) {
		return R.getTaxVertex();
	}

	private JFactReasonerConfiguration getOptions() {
		return kernelOptions;
	}

	/** return classification status of KB */
	public boolean isKBPreprocessed() {
		return getStatus().ordinal() >= kbCChecked.ordinal();
	}

	/** return classification status of KB */
	public boolean isKBClassified() {
		return getStatus().ordinal() >= kbClassified.ordinal();
	}

	/** return realistion status of KB */
	public boolean isKBRealised() {
		return getStatus().ordinal() >= kbRealised.ordinal();
	}

	/** set top/bottom role names to use them in the related output */
	public void setTopBottomRoleNames(final String topO, final String botO,
			final String topD, final String botD) {
		topORoleName = topO;
		botORoleName = botO;
		topDRoleName = topD;
		botDRoleName = botD;
		ontology.getExpressionManager().setTopBottomRoles(topORoleName, botORoleName,
				topDRoleName, botDRoleName);
	}

	/**
	 * dump query processing TIME, reasoning statistics and a (preprocessed)
	 * TBox
	 */
	public void writeReasoningResult(final LogAdapter o, final long time) {
		getTBox().clearQueryConcept(); // get rid of the query leftovers
		getTBox().writeReasoningResult(time);
	}

	// helper methods to query properties of roles
	/** @return true if R is functional wrt ontology */
	private boolean checkFunctionality(final Role R) {
		// R is transitive iff \ER.C and \ER.\not C is unsatisfiable
		DLTree tmp = DLTreeFactory.createSNFExists(DLTreeFactory.createRole(R).copy(),
				DLTreeFactory.createSNFNot(getFreshFiller(R)));
		tmp = DLTreeFactory.createSNFAnd(tmp, DLTreeFactory.createSNFExists(
				DLTreeFactory.createRole(R), getFreshFiller(R)));
		return !checkSatTree(tmp);
	}

	/** @return true if R is functional; set the value for R if necessary */
	private boolean getFunctionality(final Role R) {
		if (!R.isFunctionalityKnown()) {
			//			DLTreeFactory.buildTree(new Lexeme(R
			//					.isDataRole() ? Token.DNAME : Token.RNAME,
			R.setFunctional(checkFunctionality(R));
		}
		return R.isFunctional();
	}

	/** @return true if R is transitive wrt ontology */
	private boolean checkTransitivity(final DLTree R) {
		// R is transitive iff \ER.\ER.C and \AR.\not C is unsatisfiable
		DLTree tmp = DLTreeFactory.createSNFExists(R.copy(),
				DLTreeFactory.createSNFNot(getTBox().getFreshConcept()));
		tmp = DLTreeFactory.createSNFExists(R.copy(), tmp);
		tmp = DLTreeFactory.createSNFAnd(tmp,
				DLTreeFactory.createSNFForall(R, getTBox().getFreshConcept()));
		return !checkSatTree(tmp);
	}

	/** @return true if R is symmetric wrt ontology */
	private boolean checkSymmetry(final DLTree R) {
		// R is symmetric iff C and \ER.\AR.(not C) is unsatisfiable
		DLTree tmp = DLTreeFactory.createSNFForall(R.copy(),
				DLTreeFactory.createSNFNot(getTBox().getFreshConcept()));
		tmp = DLTreeFactory.createSNFAnd(getTBox().getFreshConcept(),
				DLTreeFactory.createSNFExists(R, tmp));
		return !checkSatTree(tmp);
	}

	/** @return true if R is reflexive wrt ontology */
	private boolean checkReflexivity(final DLTree R) {
		// R is reflexive iff C and \AR.(not C) is unsatisfiable
		DLTree tmp = DLTreeFactory.createSNFForall(R,
				DLTreeFactory.createSNFNot(getTBox().getFreshConcept()));
		tmp = DLTreeFactory.createSNFAnd(getTBox().getFreshConcept(), tmp);
		return !checkSatTree(tmp);
	}

	/** @return true if R [= S wrt ontology */
	//	bool checkRoleSubsumption ( TRole* R, TRole* S )
	//	{
	//		if ( unlikely ( R->isDataRole() != S->isDataRole() ) )
	//			return false;
	//		// R [= S iff \ER.C and \AS.(not C) is unsatisfiable
	//		DLTree* tmp = createSNFForall ( createRole(S), createSNFNot(getFreshFiller(S)) );
	//		tmp = createSNFAnd ( createSNFExists ( createRole(R), getFreshFiller(R) ), tmp );
	//		return !checkSatTree(tmp);
	//	}
	private boolean checkRoleSubsumption(final Role R, final Role S) {
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
	public ExpressionManager getExpressionManager() {
		return ontology.getExpressionManager();
	}

	/** create new KB */
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
	private boolean releaseKB() {
		clearTBox();
		ontology.clear();
		return false;
	}

	/** reset current KB */
	public boolean clearKB() {
		if (pTBox == null) {
			return true;
		}
		return releaseKB() || newKB();
	}

	//	TELLS interface
	// Declaration axioms
	/** axiom declare(x) */
	public Axiom declare(final OWLAxiom ax, final Expression C) {
		return ontology.add(new AxiomDeclaration(ax, C));
	}

	// Concept axioms
	/** axiom C [= D */
	public Axiom impliesConcepts(final OWLAxiom ax, final ConceptExpression C,
			final ConceptExpression D) {
		return ontology.add(new AxiomConceptInclusion(ax, C, D));
	}

	/** axiom C1 = ... = Cn */
	public Axiom equalConcepts(final OWLAxiom ax, final List<Expression> l) {
		return ontology.add(new AxiomEquivalentConcepts(ax, l));
	}

	/** axiom C1 != ... != Cn */
	public Axiom disjointConcepts(final OWLAxiom ax, final List<Expression> l) {
		return ontology.add(new AxiomDisjointConcepts(ax, l));
	}

	// Role axioms
	/** R = Inverse(S) */
	public Axiom setInverseRoles(final OWLAxiom ax, final ObjectRoleExpression R,
			final ObjectRoleExpression S) {
		return ontology.add(new AxiomRoleInverse(ax, R, S));
	}

	/** axiom (R [= S) */
	public Axiom impliesORoles(final OWLAxiom ax, final ObjectRoleComplexExpression R,
			final ObjectRoleExpression S) {
		return ontology.add(new AxiomORoleSubsumption(ax, R, S));
	}

	/** axiom (R [= S) */
	public Axiom impliesDRoles(final OWLAxiom ax, final DataRoleExpression R,
			final DataRoleExpression S) {
		return ontology.add(new AxiomDRoleSubsumption(ax, R, S));
	}

	/** axiom R1 = R2 = ... */
	public Axiom equalORoles(final OWLAxiom ax, final List<Expression> l) {
		return ontology.add(new AxiomEquivalentORoles(ax, l));
	}

	/** axiom R1 = R2 = ... */
	public Axiom equalDRoles(final OWLAxiom ax, final List<Expression> l) {
		return ontology.add(new AxiomEquivalentDRoles(ax, l));
	}

	/** axiom R1 != R2 != ... */
	public Axiom disjointORoles(final OWLAxiom ax, final List<Expression> l) {
		return ontology.add(new AxiomDisjointORoles(ax, l));
	}

	/** axiom R1 != R2 != ... */
	public Axiom disjointDRoles(final OWLAxiom ax, final List<Expression> l) {
		return ontology.add(new AxiomDisjointDRoles(ax, l));
	}

	/** Domain (R C) */
	public Axiom setODomain(final OWLAxiom ax, final ObjectRoleExpression R,
			final ConceptExpression C) {
		return ontology.add(new AxiomORoleDomain(ax, R, C));
	}

	/** Domain (R C) */
	public Axiom setDDomain(final OWLAxiom ax, final DataRoleExpression R,
			final ConceptExpression C) {
		return ontology.add(new AxiomDRoleDomain(ax, R, C));
	}

	/** Range (R C) */
	public Axiom setORange(final OWLAxiom ax, final ObjectRoleExpression R,
			final ConceptExpression C) {
		return ontology.add(new AxiomORoleRange(ax, R, C));
	}

	/** Range (R E) */
	public Axiom setDRange(final OWLAxiom ax, final DataRoleExpression R,
			final DataExpression E) {
		return ontology.add(new AxiomDRoleRange(ax, R, E));
	}

	/** Transitive (R) */
	public Axiom setTransitive(final OWLAxiom ax, final ObjectRoleExpression R) {
		return ontology.add(new AxiomRoleTransitive(ax, R));
	}

	/** Reflexive (R) */
	public Axiom setReflexive(final OWLAxiom ax, final ObjectRoleExpression R) {
		return ontology.add(new AxiomRoleReflexive(ax, R));
	}

	/** Irreflexive (R): Domain(R) = \neg ER.Self */
	public Axiom setIrreflexive(final OWLAxiom ax, final ObjectRoleExpression R) {
		return ontology.add(new AxiomRoleIrreflexive(ax, R));
	}

	/** Symmetric (R): R [= R^- */
	public Axiom setSymmetric(final OWLAxiom ax, final ObjectRoleExpression R) {
		return ontology.add(new AxiomRoleSymmetric(ax, R));
	}

	/** AntySymmetric (R): disjoint(R,R^-) */
	public Axiom setAsymmetric(final OWLAxiom ax, final ObjectRoleExpression R) {
		return ontology.add(new AxiomRoleAsymmetric(ax, R));
	}

	/** Functional (R) */
	public Axiom setOFunctional(final OWLAxiom ax, final ObjectRoleExpression R) {
		return ontology.add(new AxiomORoleFunctional(ax, R));
	}

	/** Functional (R) */
	public Axiom setDFunctional(final OWLAxiom ax, final DataRoleExpression R) {
		return ontology.add(new AxiomDRoleFunctional(ax, R));
	}

	/** InverseFunctional (R) */
	public Axiom setInverseFunctional(final OWLAxiom ax, final ObjectRoleExpression R) {
		return ontology.add(new AxiomRoleInverseFunctional(ax, R));
	}

	// Individual axioms
	/** axiom I e C */
	public Axiom instanceOf(final OWLAxiom ax, final IndividualExpression I,
			final ConceptExpression C) {
		return ontology.add(new AxiomInstanceOf(ax, I, C));
	}

	/** axiom <I,J>:R */
	public Axiom relatedTo(final OWLAxiom ax, final IndividualExpression I,
			final ObjectRoleExpression R, final IndividualExpression J) {
		return ontology.add(new AxiomRelatedTo(ax, I, R, J));
	}

	/** axiom <I,J>:\neg R */
	public Axiom relatedToNot(final OWLAxiom ax, final IndividualExpression I,
			final ObjectRoleExpression R, final IndividualExpression J) {
		return ontology.add(new AxiomRelatedToNot(ax, I, R, J));
	}

	/** axiom (value I A V) */
	public Axiom valueOf(final OWLAxiom ax, final IndividualExpression I,
			final DataRoleExpression A, final Literal<?> V) {
		return ontology.add(new AxiomValueOf(ax, I, A, V));
	}

	/** axiom <I,V>:\neg A */
	public Axiom valueOfNot(final OWLAxiom ax, final IndividualExpression I,
			final DataRoleExpression A, final Literal<?> V) {
		return ontology.add(new AxiomValueOfNot(ax, I, A, V));
	}

	/** same individuals */
	public Axiom processSame(final OWLAxiom ax, final List<Expression> l) {
		return ontology.add(new AxiomSameIndividuals(ax, l));
	}

	/** different individuals */
	public Axiom processDifferent(final OWLAxiom ax, final List<Expression> l) {
		return ontology.add(new AxiomDifferentIndividuals(ax, l));
	}

	/** let all concept expressions in the ArgQueue to be fairness constraints */
	public Axiom setFairnessConstraint(final OWLAxiom ax, final List<Expression> l) {
		return ontology.add(new AxiomFairnessConstraint(ax, l));
	}

	/** retract an axiom */
	public void retract(final Axiom axiom) {
		ontology.retract(axiom);
	}

	//* ASK part
	/*
	 * Before execution of any query the Kernel make sure that the KB is in an
	 * appropriate state: Preprocessed, Classified or Realised. If the ontology
	 * was changed between asks, incremental classification is performed and the
	 * corrected result is returned.
	 */
	/** return consistency status of KB */
	public boolean isKBConsistent() {
		if (getStatus().ordinal() <= kbLoading.ordinal()) {
			processKB(kbCChecked);
		}
		return getTBox().isConsistent();
	}

	/** ensure that KB is preprocessed/consistence checked */
	private void preprocessKB() {
		if (!isKBConsistent()) {
			throw new InconsistentOntologyException();
		}
	}

	/** ensure that KB is classified */
	public void classifyKB() {
		if (!isKBClassified()) {
			processKB(kbClassified);
		}
		if (!isKBConsistent()) {
			throw new InconsistentOntologyException();
		}
	}

	/** ensure that KB is realised */
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
	public boolean isFunctional(final ObjectRoleExpression R) {
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
	public boolean isFunctional(final DataRoleExpression R) {
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
	public boolean isInverseFunctional(final ObjectRoleExpression R) {
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
	public boolean isTransitive(final ObjectRoleExpression R) {
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
	public boolean isSymmetric(final ObjectRoleExpression R) {
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
	public boolean isAsymmetric(final ObjectRoleExpression R) {
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
	public boolean isReflexive(final ObjectRoleExpression R) {
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
	public boolean isIrreflexive(final ObjectRoleExpression R) {
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
	public boolean isDisjointRoles(final ObjectRoleExpression R,
			final ObjectRoleExpression S) {
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
	public boolean isDisjointRoles(final DataRoleExpression R, final DataRoleExpression S) {
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
	public boolean isSubRoles(final ObjectRoleComplexExpression R,
			final ObjectRoleExpression S) {
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
		//DLTree r = e(R), s = e(S);
		return checkRoleSubsumption(r, s);
	}

	// single satisfiability
	/** @return true iff C is satisfiable */
	public boolean isSatisfiable(final ConceptExpression C) {
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
	public boolean isSubsumedBy(final ConceptExpression C, final ConceptExpression D) {
		preprocessKB();
		if (this.isNameOrConst(D) && this.isNameOrConst(C)) {
			return this.checkSub(getTBox().getCI(e(C)), getTBox().getCI(e(D)));
		}
		DLTree nD = DLTreeFactory.createSNFNot(e(D));
		return !checkSatTree(DLTreeFactory.createSNFAnd(e(C), nD));
		//		return !this.checkSat(this.getExpressionManager().and(C,
		//				this.getExpressionManager().not(D)));
	}

	/** @return true iff C is disjoint with D; that is, C [= \not D holds */
	public boolean isDisjoint(final ConceptExpression C, final ConceptExpression D) {
		return isSubsumedBy(C, getExpressionManager().not(D));
	}

	/** @return true iff C is equivalent to D */
	public boolean isEquivalent(final ConceptExpression C, final ConceptExpression D) {
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
	public void getSupConcepts(final ConceptExpression C, final boolean direct,
			final Actor actor) {
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
	public void getSubConcepts(final ConceptExpression C, final boolean direct,
			final Actor actor) {
		classifyKB(); // ensure KB is ready to answer the query
		this.setUpCache(C, csClassified);
		Taxonomy tax = getCTaxonomy();
		tax.getRelativesInfo(cachedVertex, actor, false, direct, false);
	}

	/** apply actor__apply() to all synonyms of [complex] C */
	public void getEquivalentConcepts(final ConceptExpression C, final Actor actor) {
		classifyKB(); // ensure KB is ready to answer the query
		this.setUpCache(C, csClassified);
		actor.apply(cachedVertex);
	}

	/// apply actor::apply() to all named concepts disjoint with [complex] C
	public void getDisjointConcepts(final ConceptExpression C, final Actor actor) {
		classifyKB(); // ensure KB is ready to answer the query
		this.setUpCache(getExpressionManager().not(C), csClassified);
		Taxonomy tax = getCTaxonomy();
		// we are looking for all sub-concepts of (not C) (including synonyms to it)
		tax.getRelativesInfo(cachedVertex, actor, true, false, false);
	}

	// role hierarchy
	/** apply actor__apply() to all DIRECT super-roles of [complex] R */
	public void getSupRoles(final RoleExpression r, final boolean direct,
			final Actor actor) {
		preprocessKB(); // ensure KB is ready to answer the query
		Role R = getRole(r, "Role expression expected in getSupRoles()");
		Taxonomy tax = getTaxonomy(R);
		tax.getRelativesInfo(getTaxVertex(R), actor, false, direct, true);
	}

	/** apply actor__apply() to all DIRECT sub-roles of [complex] R */
	public void getSubRoles(final RoleExpression r, final boolean direct,
			final Actor actor) {
		preprocessKB(); // ensure KB is ready to answer the query
		Role R = getRole(r, "Role expression expected in getSubRoles()");
		Taxonomy tax = getTaxonomy(R);
		tax.getRelativesInfo(getTaxVertex(R), actor, false, direct, false);
	}

	/** apply actor__apply() to all synonyms of [complex] R */
	public void getEquivalentRoles(final RoleExpression r, final Actor actor) {
		preprocessKB(); // ensure KB is ready to answer the query
		Role R = getRole(r, "Role expression expected in getEquivalentRoles()");
		actor.apply(getTaxVertex(R));
	}

	// domain and range as a set of named concepts
	/**
	 * apply actor__apply() to all DIRECT NC that are in the domain of [complex]
	 * R
	 */
	public void getORoleDomain(final ObjectRoleExpression r, final boolean direct,
			final Actor actor) {
		classifyKB(); // ensure KB is ready to answer the query
		this.setUpCache(getExpressionManager().exists(r, getExpressionManager().top()),
				csClassified);
		Taxonomy tax = getCTaxonomy();
		tax.getRelativesInfo(cachedVertex, actor, true, direct, true);
	}

	/// apply actor::apply() to all DIRECT NC that are in the domain of data role R
	//template<class Actor>
	void getDRoleDomain(final DataRoleExpression r, final boolean direct,
			final Actor actor) {
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

	/**
	 * apply actor__apply() to all DIRECT NC that are in the range of [complex]
	 * R
	 */
	public void getRoleRange(final ObjectRoleExpression r, final boolean direct,
			final Actor actor) {
		getORoleDomain(getExpressionManager().inverse(r), direct, actor);
	}

	// instances
	public void getInstances(final ConceptExpression C, final Actor actor,
			final boolean direct) {
		if (direct) {
			getDirectInstances(C, actor);
		} else {
			this.getInstances(C, actor);
		}
	}

	/** apply actor__apply() to all direct instances of given [complex] C */
	public void getDirectInstances(final ConceptExpression C, final Actor actor) {
		realiseKB(); // ensure KB is ready to answer the query
		this.setUpCache(C, csClassified);
		// implement 1-level check by hand
		// if the root vertex contains individuals -- we are done
		if (actor.apply(cachedVertex)) {
			return;
		}
		// if not, just go 1 level down and apply the actor regardless of what's found
		// FIXME!! check again after bucket-method will be implemented
		for (TaxonomyVertex p : cachedVertex.neigh(/* upDirection= */false)) {
			actor.apply(p);
		}
	}

	/** apply actor__apply() to all instances of given [complex] C */
	public void getInstances(final ConceptExpression C, final Actor actor) { // FIXME!! check for Racer's/IS approach
		realiseKB(); // ensure KB is ready to answer the query
		this.setUpCache(C, csClassified);
		Taxonomy tax = getCTaxonomy();
		tax.getRelativesInfo(cachedVertex, actor, true, false, false);
	}

	/**
	 * apply actor__apply() to all DIRECT concepts that are types of an
	 * individual I
	 */
	public void getTypes(final IndividualExpression I, final boolean direct,
			final Actor actor) {
		realiseKB(); // ensure KB is ready to answer the query
		this.setUpCache(getExpressionManager().oneOf(I), csClassified);
		Taxonomy tax = getCTaxonomy();
		tax.getRelativesInfo(cachedVertex, actor, true, direct, true);
	}

	/** apply actor__apply() to all synonyms of an individual I */
	public void getSameAs(final IndividualExpression I, final Actor actor) {
		realiseKB(); // ensure KB is ready to answer the query
		getEquivalentConcepts(getExpressionManager().oneOf(I), actor);
	}

	/** @return true iff I and J refer to the same individual */
	public boolean isSameIndividuals(final IndividualExpression I,
			final IndividualExpression J) {
		realiseKB();
		Individual i = getIndividual(I,
				"Only known individuals are allowed in the isSameAs()");
		Individual j = getIndividual(J,
				"Only known individuals are allowed in the isSameAs()");
		return getTBox().isSameIndividuals(i, j);
	}

	//----------------------------------------------------------------------------------
	// knowledge exploration queries
	//----------------------------------------------------------------------------------
	/// build a completion tree for a concept expression C (no caching as it breaks the idea of KE). @return the root node
	public DlCompletionTree buildCompletionTree(final ConceptExpression C) {
		preprocessKB();
		this.setUpCache(C, csSat);
		DlCompletionTree ret = getTBox().buildCompletionTree(cachedConcept);
		// init KB after the sat test to reduce the number of DAG adjustments
		if (KE == null) {
			KE = new KnowledgeExplorer(getTBox(), getExpressionManager());
		}
		return ret;
		//	return getTBox().buildCompletionTree(cachedConcept);
	}

	public KnowledgeExplorer getKnowledgeExplorer() {
		return KE;
	}

	/// build the set of data neighbours of a NODE, put the set of data roles into the RESULT variable
	public Set<RoleExpression> getDataRoles(final DlCompletionTree node,
			final boolean onlyDet) {
		return KE.getDataRoles(node, onlyDet);
	}

	/// build the set of object neighbours of a NODE, put the set of object roles and inverses into the RESULT variable
	public Set<RoleExpression> getObjectRoles(final DlCompletionTree node,
			final boolean onlyDet, final boolean needIncoming) {
		return KE.getObjectRoles(node, onlyDet, needIncoming);
	}

	/// build the set of neighbours of a NODE via role ROLE; put the resulting list into RESULT
	public List<DlCompletionTree> getNeighbours(final DlCompletionTree node,
			final RoleExpression role) {
		return KE.getNeighbours(node,
				getRole(role, "Role expression expected in getNeighbours() method"));
	}

	/// put into RESULT all the expressions from the NODE label; if ONLYDET is true, return only deterministic elements
	public List<ConceptExpression> getObjectLabel(final DlCompletionTree node,
			final boolean onlyDet) {
		return KE.getObjectLabel(node, onlyDet);
	}

	public List<DataExpression> getDataLabel(final DlCompletionTree node,
			final boolean onlyDet) {
		return KE.getDataLabel(node, onlyDet);
	}

	//----------------------------------------------------------------------------------
	// atomic decomposition queries
	//----------------------------------------------------------------------------------
	/** @return true iff individual I is instance of given [complex] C */
	public boolean isInstance(final IndividualExpression I, final ConceptExpression C) {
		realiseKB(); // ensure KB is ready to answer the query
		getIndividual(I, "individual name expected in the isInstance()");
		// FIXME!! this way a new concept is created; could be done more optimal
		return isSubsumedBy(getExpressionManager().oneOf(I), C);
	}

	public ReasoningKernel(final JFactReasonerConfiguration conf,
			final DatatypeFactory factory) {
		// should be commented
		cachedQuery = null;
		cachedQueryTree = null;
		kernelOptions = conf;
		datatypeFactory = factory;
		pTBox = null;
		pET = null;
		ModSyn = null;
		ModSem = null;
		cachedQuery = null;
		initCacheAndFlags();
		useAxiomSplitting = false;
	}

	/// try to perform the incremental reasoning on the changed ontology
	private boolean tryIncremental() {
		if (pTBox == null) {
			return true;
		}
		if (!ontology.isChanged()) {
			return false;
		}
		return true;
	}

	/// force the re-classification of the changed ontology
	private void forceReload() {
		clearTBox();
		newKB();
		// Protege (as the only user of non-trivial monitors with reload) does not accept multiple usage of a monitor
		// so switch it off after the 1st usage
		//		if (kernelOptions.isUseELReasoner()) {
		//			ELFAxiomChecker ac = new ELFAxiomChecker();
		//			ac.visitOntology(ontology);
		//			if (ac.value()) {
		//				ELFNormalizer normalizer = new ELFNormalizer(getExpressionManager());
		//				normalizer.visitOntology(ontology);
		//				ELFReasoner reasoner = new ELFReasoner(ontology);
		//				reasoner.classify();
		//			}
		//		}
		// split ontological axioms
		if (kernelOptions.isSplits()) {
			TAxiomSplitter AxiomSplitter = new TAxiomSplitter(ontology);
			AxiomSplitter.buildSplit();
		}
		OntologyLoader OntologyLoader = new OntologyLoader(getTBox());
		OntologyLoader.visitOntology(ontology);
		ontology.setProcessed();
	}

	//----------------------------------------------------------------------------------
	// knowledge exploration queries
	//----------------------------------------------------------------------------------
	/// add the role R and all its supers to a set RESULT
	//void addRoleWithSupers (  Role R, TCGRoleSet Result );
	private void processKB(final KBStatus status) {
		assert status.ordinal() >= kbCChecked.ordinal();
		// check whether reasoning was failed
		if (reasoningFailed) {
			throw new ReasonerInternalException(
					"Can't classify KB because of previous errors");
		}
		// check if something have to be done
		if (getStatus().ordinal() >= status.ordinal()) { // nothing to do; but make sure that we are consistent
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
	private void classify(final KBStatus status) {
		// don't do classification twice
		if (status != kbRealised) {
			//goto Realise;
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

	//-----------------------------------------------------------------------------
	//--		query caching support
	//-----------------------------------------------------------------------------
	/// classify query; cache is ready at the point. NAMED means whether concept is just a name
	void classifyQuery(final boolean named) {
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

	void setUpCache(final DLTree query, final CacheStatus level) {
		// if KB was changed since it was classified,
		// we should catch it before
		assert !ontology.isChanged();
		// check if the query is already cached
		if (this.checkQueryCache(query)) { // ... with the same level -- nothing to do
											//deleteTree(query);
											//query=null;
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
			//getTBox().clearQueryConcept();
			cachedConcept = getTBox().createQueryConcept(cachedQueryTree);
		}
		assert cachedConcept != null;
		// preprocess concept is necessary (fresh concept in query or complex one)
		if (cachedConcept.getpName() == 0) {
			getTBox().preprocessQueryConcept(cachedConcept);
		}
		if (level == csClassified) {
			classifyQuery(cachedQueryTree.isCN());
		}
	}

	void setUpCache(final ConceptExpression query, final CacheStatus level) {
		// if KB was changed since it was classified,
		// we should catch it before
		assert !ontology.isChanged();
		// check if the query is already cached
		if (this.checkQueryCache(query)) { // ... with the same level -- nothing to do
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
			//getTBox().clearQueryConcept();
			// ... as if fresh names appears there, they would be cleaned up
			cachedConcept = getTBox().createQueryConcept(e(cachedQuery));
		}
		assert cachedConcept != null;
		// preprocess concept is necessary (fresh concept in query or complex one)
		if (cachedConcept.getpName() == 0) {
			getTBox().preprocessQueryConcept(cachedConcept);
		}
		if (level == csClassified) {
			classifyQuery(this.isNameOrConst(cachedQuery));
		}
	}

	//----------------------------------------------------------------------------------
	// atomic decomposition queries
	//----------------------------------------------------------------------------------
	/// create new atomic decomposition of the loaded ontology using TYPE. @return size of the AD
	public int getAtomicDecompositionSize(final boolean useSemantics,
			final ModuleType type) {
		// init AD field
		if (AD == null) {
			//XXX
			if (useSemantics) {
				AD = new AtomicDecomposer(new SemanticLocalityChecker(this,
						new TSignature()));
			} else {
				AD = new AtomicDecomposer(new SyntacticLocalityChecker(new TSignature()));
			}
		}
		return AD.getAOS(ontology, type).size();
	}

	/// get a set of axioms that corresponds to the atom with the id INDEX
	public Set<Axiom> getAtomAxioms(final int index) {
		return AD.getAOS().get(index).getAtomAxioms();
	}

	/// get a set of axioms that corresponds to the module of the atom with the id INDEX
	public Set<Axiom> getAtomModule(final int index) {
		return AD.getAOS().get(index).getModule();
	}

	/// get a set of atoms on which atom with index INDEX depends
	public Set<TOntologyAtom> getAtomDependents(final int index) {
		return AD.getAOS().get(index).getDepAtoms();
	}

	//----------------------------------------------------------------------------------
	// knowledge exploration queries
	//----------------------------------------------------------------------------------
	/// build the set of data neighbours of a NODE, put the set into the RESULT variable
	//	public Set<DataRoleExpression> getDataRoles(DlCompletionTree node, boolean onlyDet) {
	//		Set<DataRoleExpression> Result = new HashSet<DataRoleExpression>();
	//		for (DlCompletionTreeArc p : node.getNeighbour()) {
	//			if (!p.isIBlocked() && p.getArcEnd().isDataNode()
	//					&& (!onlyDet || p.getDep().isEmpty())) {
	//				// FIXME!! add also all supers
	//				Result.add(getExpressionManager().dataRole(p.getRole().getName()));
	//			}
	//		}
	//		return Result;
	//	}
	//
	//	/// build the set of object neighbours of a NODE; incoming edges are counted iff NEEDINCOMING is true
	//	public Collection<ObjectRoleExpression> getObjectRoles(DlCompletionTree node,
	//			boolean onlyDet, boolean needIncoming) {
	//		Collection<ObjectRoleExpression> Result = new HashSet<ObjectRoleExpression>();
	//		for (DlCompletionTreeArc p : node.getNeighbour()) {
	//			if (!p.isIBlocked() && !p.getArcEnd().isDataNode()
	//					&& (!onlyDet || p.getDep().isEmpty())
	//					&& (needIncoming || p.isSuccEdge())) {
	//				Result.add(getExpressionManager().objectRole(p.getRole().getName()));
	//			}
	//		}
	//		return Result;
	//	}
	//
	//	/// build the set of neighbours of a NODE via role ROLE; put the resulting list into RESULT
	//	public List<DlCompletionTree> getNeighbours(DlCompletionTree node, RoleExpression role) {
	//		List<DlCompletionTree> Result = new ArrayList<DlCompletionTree>();
	//		Role R = getRole(role, "Role expression expected in getNeighbours() method");
	//		for (DlCompletionTreeArc p : node.getNeighbour()) {
	//			if (!p.isIBlocked() && p.isNeighbour(R)) {
	//				Result.add(p.getArcEnd());
	//			}
	//		}
	//		return Result;
	//	}
	/// put into RESULT all the data expressions from the NODE label
	//	public List<ConceptExpression> getObjectLabel(DlCompletionTree node, boolean onlyDet) {
	//		List<ConceptExpression> Result = new ArrayList<ConceptExpression>();
	//		// prepare D2I translator
	//		if (D2I == null) {
	//			D2I = new TDag2Interface(getTBox().getDag(), getExpressionManager());
	//		} else {
	//			D2I.ensureDagSize();
	//		}
	//		//boolean data = node.isDataNode();
	//		Result.clear();
	//		for (ConceptWDep p : node.beginl_sc()) {
	//			if (!onlyDet || p.getDep().isEmpty()) {
	//				Result.add(D2I.getCExpr(p.getConcept()));
	//			}
	//		}
	//		for (ConceptWDep p : node.beginl_cc()) {
	//			if (!onlyDet || p.getDep().isEmpty()) {
	//				Result.add(D2I.getCExpr(p.getConcept()));
	//			}
	//		}
	//		return Result;
	//	}
	//	public List<DataExpression> getDataLabel(DlCompletionTree node, boolean onlyDet) {
	//		List<DataExpression> Result = new ArrayList<DataExpression>();
	//		// prepare D2I translator
	//		if (D2I == null) {
	//			D2I = new TDag2Interface(getTBox().getDag(), getExpressionManager());
	//		} else {
	//			D2I.ensureDagSize();
	//		}
	//		Result.clear();
	//
	//		for (ConceptWDep p : node.beginl_sc()) {
	//			if (!onlyDet || p.getDep().isEmpty()) {
	//				Result.add(D2I.getDExpr(p.getConcept()));
	//			}
	//		}
	//		for (ConceptWDep p : node.beginl_cc()) {
	//			if (!onlyDet || p.getDep().isEmpty()) {
	//				Result.add(D2I.getDExpr(p.getConcept()));
	//			}
	//		}
	//		return Result;
	//	}
	/**
	 * @return true iff the chain contained in the arg-list is a sub-property of
	 *         R
	 */
	private boolean checkSubChain(final Role R, final List<ObjectRoleExpression> l) {
		// retrieve a role chain
		// R1 o ... o Rn [= R iff \ER1.\ER2....\ERn.(notC) and AR.C is unsatisfiable
		DLTree tmp = DLTreeFactory.createSNFNot(getTBox().getFreshConcept());
		for (int i = l.size() - 1; i > -1; i--) {
			ObjectRoleExpression p = l.get(i);
			tmp = DLTreeFactory.createSNFExists(e(p), tmp);
		}
		tmp = DLTreeFactory.createSNFAnd(tmp, DLTreeFactory.createSNFForall(DLTreeFactory
				.buildTree(new Lexeme(Token.RNAME, R)), getTBox().getFreshConcept()));
		return !checkSatTree(tmp);
	}

	/** @return true if R is a super-role of a chain holding in the args */
	public boolean isSubChain(final ObjectRoleComplexExpression R,
			final List<ObjectRoleExpression> l) {
		preprocessKB(); // ensure KB is ready to answer the query
		Role r = getRole(R, "Role expression expected in isSubChain()");
		if (r.isTop()) {
			return true; // universal role is a super of any chain
		}
		if (r.isBottom()) {
			for (ObjectRoleExpression p : l) {
				Role S = getRole(p, "Role expression expected in chain of isSubChain()");
				if (S.isBottom()) {
					return true;
				}
			}
			return false; // empty role is not a super of any chain
		}
		return checkSubChain(r, l);
	}

	/** @return true if R is a sub-role of S */
	public boolean isSubRoles(final DataRoleExpression R, final DataRoleExpression S) {
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
		final uk.ac.manchester.cs.jfact.kernel.Role r = getRole(R,
				"Role expression expected in isSubRoles()");
		final uk.ac.manchester.cs.jfact.kernel.Role s = getRole(S,
				"Role expression expected in isSubRoles()");
		if (!r.isTop() && !s.isBottom() && r.lesserequal(s)) {
			return true;
		}
		// check the general case
		// FIXME!! cache it later
		return checkRoleSubsumption(r, s);
	}

	// all-disjoint query implementation
	public boolean isDisjointRoles(final List<Expression> l) {
		// grab all roles from the arg-list
		//List<TDLExpression> Disj = getExpressionManager().getArgList();
		List<Role> Roles = new ArrayList<Role>(l.size());
		for (Expression p : l) {
			if (p instanceof ObjectRoleExpression) {
				ObjectRoleExpression ORole = (ObjectRoleExpression) p;
				if (getExpressionManager().isUniversalRole(ORole)) {
					return false; // universal role is not disjoint with anything
				}
				if (getExpressionManager().isEmptyRole(ORole)) {
					continue; // empty role is disjoint with everything
				}
				Roles.add(getRole(ORole, "Role expression expected in isDisjointRoles()"));
			} else {
				if (!(p instanceof DataRoleExpression)) {
					throw new ReasonerInternalException(
							"Role expression expected in isDisjointRoles()");
				}
				DataRoleExpression DRole = (DataRoleExpression) p;
				if (getExpressionManager().isUniversalRole(DRole)) {
					return false; // universal role is not disjoint with anything
				}
				if (getExpressionManager().isEmptyRole(DRole)) {
					continue; // empty role is disjoint with everything
				}
				Roles.add(getRole(DRole, "Role expression expected in isDisjointRoles()"));
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

	public boolean isDisjointDataRoles(final List<DataRoleExpression> l) {
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

	public boolean isDisjointObjectRoles(final List<ObjectRoleExpression> l) {
		// grab all roles from the arg-list
		//List<TDLExpression> Disj = getExpressionManager().getArgList();
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

	private List<Individual> buildRelatedCache(final Individual I, final Role R) {
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

	public void getRoleFillers(final IndividualExpression I,
			final ObjectRoleExpression R, final List<NamedEntry> Result) {
		realiseKB();
		List<Individual> vec = getRelated(
				getIndividual(I, "Individual name expected in the getRoleFillers()"),
				getRole(R, "Role expression expected in the getRoleFillers()"));
		for (Individual p : vec) {
			Result.add(p);
		}
	}

	public boolean isRelated(final IndividualExpression I, final ObjectRoleExpression R,
			final IndividualExpression J) {
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

enum CacheStatus {
	csEmpty, csSat, csClassified
}
