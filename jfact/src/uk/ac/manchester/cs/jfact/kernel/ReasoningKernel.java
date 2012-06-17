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
		this.interrupted = b;
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
		this.cachedQuery = null; //deleteTree(cachedQueryTree);
		this.cachedQueryTree = null;
	}

	/// set query cache value to QUERY
	void setQueryCache(final ConceptExpression query) {
		this.clearQueryCache();
		this.cachedQuery = query;
	}

	/// set query cache value to QUERY
	void setQueryCache(final DLTree query) {
		this.clearQueryCache();
		this.cachedQueryTree = query;
	}

	/// choose whether TExpr cache should be ignored
	public void setIgnoreExprCache(final boolean value) {
		this.ignoreExprCache = value;
	}

	/// check whether query cache is the same as QUERY
	boolean checkQueryCache(final ConceptExpression query) {
		return this.ignoreExprCache ? false : this.cachedQuery == query;
	}

	/// check whether query cache is the same as QUERY
	boolean checkQueryCache(final DLTree query) {
		return equalTrees(this.cachedQueryTree, query);
	}

	/** get status of the KB */
	private KBStatus getStatus() {
		if (this.pTBox == null) {
			return kbEmpty;
		}
		// if the ontology is changed, it needs to be reclassified
		if (this.ontology.isChanged()) {
			return kbLoading;
		}
		return this.pTBox.getStatus();
	}

	/** get DLTree corresponding to an expression EXPR */
	private DLTree e(final Expression expr) {
		return expr.accept(this.pET);
	}

	/// get fresh filled depending of a type of R
	private DLTree getFreshFiller(final Role R) {
		if (R.isDataRole()) {
			final LiteralEntry t = new LiteralEntry("freshliteral");
			t.setLiteral(DatatypeFactory.LITERAL.buildLiteral("freshliteral"));
			return DLTreeFactory.wrap(t);
		} else {
			return this.getTBox().getFreshConcept();
		}
	}

	/// get role expression based on the R
	RoleExpression Role(final Role R) {
		if (R.isDataRole()) {
			return this.getExpressionManager().dataRole(R.getName());
		} else {
			return this.getExpressionManager().objectRole(R.getName());
		}
	}

	/** clear cache and flags */
	private void initCacheAndFlags() {
		this.cacheLevel = csEmpty;
		this.clearQueryCache();
		this.cachedConcept = null;
		this.cachedVertex = null;
		this.reasoningFailed = false;
		this.needTracing = false;
	}

	public void needTracing() {
		this.needTracing = true;
	}

	/** @return the trace-set of the last reasoning operation */
	public List<Axiom> getTrace() {
		List<Axiom> toReturn = new ArrayList<Axiom>(this.traceVec);
		this.traceVec.clear();
		return toReturn;
	}

	/// set the signature of the expression translator
	public void setSignature(final TSignature sig) {
		if (this.pET != null) {
			this.pET.setSignature(sig);
		}
	}

	/// get RW access to the ontology
	Ontology getOntology() {
		return this.ontology;
	}

	/** axiom C = C1 or ... or Cn; C1 != ... != Cn */
	public Axiom disjointUnion(final OWLAxiom ax, final ConceptExpression C,
			final List<Expression> l) {
		return this.ontology.add(new AxiomDisjointUnion(ax, C, l));
	}

	/** get related cache for an individual I */
	private List<Individual> getRelated(final Individual I, final Role R) {
		if (!I.hasRelatedCache(R)) {
			I.setRelatedCache(R, this.buildRelatedCache(I, R));
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
		return this.getTBox().isSatisfiable(this.cachedConcept);
	}

	/// @return true iff C is satisfiable
	boolean checkSat(final ConceptExpression C) {
		this.setUpCache(C, csSat);
		return this.getTBox().isSatisfiable(this.cachedConcept);
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
			return this.checkSub(this.getTBox().getCI(this.e(C)),
					this.getTBox().getCI(this.e(D)));
		}
		return !this.checkSat(this.getExpressionManager().and(C,
				this.getExpressionManager().not(D)));
	}

	public TModularizer getModExtractor(final boolean useSemantic) {
		boolean needInit = false;
		// check whether we need init
		if (useSemantic && this.ModSem == null) {
			this.ModSem = new TModularizer(new SemanticLocalityChecker(this, null));
			needInit = true;
		}
		if (!useSemantic && this.ModSyn == null) {
			this.ModSyn = new TModularizer(new SyntacticLocalityChecker(null));
			needInit = true;
		}
		// init if necessary
		TModularizer Mod = useSemantic ? this.ModSem : this.ModSyn;
		if (needInit) {
			SigIndex SI = new SigIndex();
			SI.processRange(this.getOntology().getAxioms());
			Mod.setSigIndex(SI);
			Mod.preprocessOntology(this.getOntology().getAxioms());
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
		TModularizer Mod = this.getModExtractor(useSemantic);
		Mod.extract(this.getOntology().getAxioms(), Sig, type);
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
		LocalityChecker LC = this.getModExtractor(useSemantic).getLocalityChecker();
		LC.setSignatureValue(Sig);
		this.Result.clear();
		for (Axiom p : this.getOntology().getAxioms()) {
			if (!LC.local(p)) {
				this.Result.add(p);
			}
		}
		return this.Result;
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
				return !this.getTBox().isSatisfiable(C); // C [= D iff C=\bottom
			}
		} else {
			// D is known
			if (C.getpName() == 0) {
				// C [= D iff D = \top, or ~D = \bottom
				return !this.checkSatTree(DLTreeFactory.createSNFNot(this.getTBox()
						.getTree(C)));
			}
		}
		// here C and D are known (not fresh)
		// check the obvious ones
		if (D.isTop() || C.isBottom()) {
			return true;
		}
		if (this.getStatus().ordinal() < kbClassified.ordinal()) {
			// unclassified => do via SAT test
			return this.getTBox().isSubHolds(C, D);
		}
		// classified => do the taxonomy traversal
		SupConceptActor actor = new SupConceptActor(D);
		Taxonomy tax = this.getCTaxonomy();
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
		if (this.pTBox == null) {
			throw new ReasonerInternalException("KB Not Initialised");
		}
	}

	/** get RW access to TBox */
	private TBox getTBox() {
		this.checkTBox();
		return this.pTBox;
	}

	/** clear TBox and related structures; keep ontology in place */
	private void clearTBox() {
		this.pTBox = null;
		this.pET = null;
		//D2I = null;
	}

	/** get RW access to Object RoleMaster from TBox */
	private RoleMaster getORM() {
		return this.getTBox().getORM();
	}

	/** get RW access to Data RoleMaster from TBox */
	private RoleMaster getDRM() {
		return this.getTBox().getDRM();
	}

	/** get access to the concept hierarchy */
	private Taxonomy getCTaxonomy() {
		if (!this.isKBClassified()) {
			throw new ReasonerInternalException(
					"No access to concept taxonomy: ontology not classified");
		}
		return this.getTBox().getTaxonomy();
	}

	/** get access to the object role hierarchy */
	private Taxonomy getORTaxonomy() {
		if (!this.isKBPreprocessed()) {
			throw new ReasonerInternalException(
					"No access to the object role taxonomy: ontology not preprocessed");
		}
		return this.getORM().getTaxonomy();
	}

	/** get access to the data role hierarchy */
	private Taxonomy getDRTaxonomy() {
		if (!this.isKBPreprocessed()) {
			throw new ReasonerInternalException(
					"No access to the data role taxonomy: ontology not preprocessed");
		}
		return this.getDRM().getTaxonomy();
	}

	// transformation methods
	/** get individual by the TIndividualExpr */
	private Individual getIndividual(final IndividualExpression i, final String reason) {
		DLTree I = this.e(i);
		if (I == null) {
			throw new ReasonerInternalException(reason);
		}
		return (Individual) this.getTBox().getCI(I);
	}

	/** get role by the TRoleExpr */
	private Role getRole(final RoleExpression r, final String reason) {
		return Role.resolveRole(this.e(r));
	}

	/** get taxonomy of the property wrt it's name */
	private Taxonomy getTaxonomy(final Role R) {
		return R.isDataRole() ? this.getDRTaxonomy() : this.getORTaxonomy();
	}

	/** get taxonomy vertext of the property wrt it's name */
	private TaxonomyVertex getTaxVertex(final Role R) {
		return R.getTaxVertex();
	}

	private JFactReasonerConfiguration getOptions() {
		return this.kernelOptions;
	}

	/** return classification status of KB */
	public boolean isKBPreprocessed() {
		return this.getStatus().ordinal() >= kbCChecked.ordinal();
	}

	/** return classification status of KB */
	public boolean isKBClassified() {
		return this.getStatus().ordinal() >= kbClassified.ordinal();
	}

	/** return realistion status of KB */
	public boolean isKBRealised() {
		return this.getStatus().ordinal() >= kbRealised.ordinal();
	}

	/** set top/bottom role names to use them in the related output */
	public void setTopBottomRoleNames(final String topO, final String botO,
			final String topD, final String botD) {
		this.topORoleName = topO;
		this.botORoleName = botO;
		this.topDRoleName = topD;
		this.botDRoleName = botD;
		this.ontology.getExpressionManager().setTopBottomRoles(this.topORoleName,
				this.botORoleName, this.topDRoleName, this.botDRoleName);
	}

	/**
	 * dump query processing TIME, reasoning statistics and a (preprocessed)
	 * TBox
	 */
	public void writeReasoningResult(final LogAdapter o, final long time) {
		this.getTBox().clearQueryConcept(); // get rid of the query leftovers
		this.getTBox().writeReasoningResult(time);
	}

	// helper methods to query properties of roles
	/** @return true if R is functional wrt ontology */
	private boolean checkFunctionality(final Role R) {
		// R is transitive iff \ER.C and \ER.\not C is unsatisfiable
		DLTree tmp = DLTreeFactory.createSNFExists(DLTreeFactory.createRole(R).copy(),
				DLTreeFactory.createSNFNot(this.getFreshFiller(R)));
		tmp = DLTreeFactory.createSNFAnd(
				tmp,
				DLTreeFactory.createSNFExists(DLTreeFactory.createRole(R),
						this.getFreshFiller(R)));
		return !this.checkSatTree(tmp);
	}

	/** @return true if R is functional; set the value for R if necessary */
	private boolean getFunctionality(final Role R) {
		if (!R.isFunctionalityKnown()) {
			//			DLTreeFactory.buildTree(new Lexeme(R
			//					.isDataRole() ? Token.DNAME : Token.RNAME,
			R.setFunctional(this.checkFunctionality(R));
		}
		return R.isFunctional();
	}

	/** @return true if R is transitive wrt ontology */
	private boolean checkTransitivity(final DLTree R) {
		// R is transitive iff \ER.\ER.C and \AR.\not C is unsatisfiable
		DLTree tmp = DLTreeFactory.createSNFExists(R.copy(),
				DLTreeFactory.createSNFNot(this.getTBox().getFreshConcept()));
		tmp = DLTreeFactory.createSNFExists(R.copy(), tmp);
		tmp = DLTreeFactory.createSNFAnd(tmp,
				DLTreeFactory.createSNFForall(R, this.getTBox().getFreshConcept()));
		return !this.checkSatTree(tmp);
	}

	/** @return true if R is symmetric wrt ontology */
	private boolean checkSymmetry(final DLTree R) {
		// R is symmetric iff C and \ER.\AR.(not C) is unsatisfiable
		DLTree tmp = DLTreeFactory.createSNFForall(R.copy(),
				DLTreeFactory.createSNFNot(this.getTBox().getFreshConcept()));
		tmp = DLTreeFactory.createSNFAnd(this.getTBox().getFreshConcept(),
				DLTreeFactory.createSNFExists(R, tmp));
		return !this.checkSatTree(tmp);
	}

	/** @return true if R is reflexive wrt ontology */
	private boolean checkReflexivity(final DLTree R) {
		// R is reflexive iff C and \AR.(not C) is unsatisfiable
		DLTree tmp = DLTreeFactory.createSNFForall(R,
				DLTreeFactory.createSNFNot(this.getTBox().getFreshConcept()));
		tmp = DLTreeFactory.createSNFAnd(this.getTBox().getFreshConcept(), tmp);
		return !this.checkSatTree(tmp);
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
				DLTreeFactory.createSNFNot(this.getFreshFiller(S)));
		tmp = DLTreeFactory.createSNFAnd(
				DLTreeFactory.createSNFExists(DLTreeFactory.createRole(R),
						this.getFreshFiller(R)), tmp);
		return !this.checkSatTree(tmp);
	}

	/** get access to an expression manager */
	public ExpressionManager getExpressionManager() {
		return this.ontology.getExpressionManager();
	}

	/** create new KB */
	private boolean newKB() {
		if (this.pTBox != null) {
			return true;
		}
		this.pTBox = new TBox(this.datatypeFactory, this.getOptions(), this.topORoleName,
				this.botORoleName, this.topDRoleName, this.botDRoleName, this.interrupted);
		this.pET = new ExpressionTranslator(this.pTBox);
		this.initCacheAndFlags();
		return false;
	}

	/** delete existed KB */
	private boolean releaseKB() {
		this.clearTBox();
		this.ontology.clear();
		return false;
	}

	/** reset current KB */
	public boolean clearKB() {
		if (this.pTBox == null) {
			return true;
		}
		return this.releaseKB() || this.newKB();
	}

	//	TELLS interface
	// Declaration axioms
	/** axiom declare(x) */
	public Axiom declare(final OWLAxiom ax, final Expression C) {
		return this.ontology.add(new AxiomDeclaration(ax, C));
	}

	// Concept axioms
	/** axiom C [= D */
	public Axiom impliesConcepts(final OWLAxiom ax, final ConceptExpression C,
			final ConceptExpression D) {
		return this.ontology.add(new AxiomConceptInclusion(ax, C, D));
	}

	/** axiom C1 = ... = Cn */
	public Axiom equalConcepts(final OWLAxiom ax, final List<Expression> l) {
		return this.ontology.add(new AxiomEquivalentConcepts(ax, l));
	}

	/** axiom C1 != ... != Cn */
	public Axiom disjointConcepts(final OWLAxiom ax, final List<Expression> l) {
		return this.ontology.add(new AxiomDisjointConcepts(ax, l));
	}

	// Role axioms
	/** R = Inverse(S) */
	public Axiom setInverseRoles(final OWLAxiom ax, final ObjectRoleExpression R,
			final ObjectRoleExpression S) {
		return this.ontology.add(new AxiomRoleInverse(ax, R, S));
	}

	/** axiom (R [= S) */
	public Axiom impliesORoles(final OWLAxiom ax, final ObjectRoleComplexExpression R,
			final ObjectRoleExpression S) {
		return this.ontology.add(new AxiomORoleSubsumption(ax, R, S));
	}

	/** axiom (R [= S) */
	public Axiom impliesDRoles(final OWLAxiom ax, final DataRoleExpression R,
			final DataRoleExpression S) {
		return this.ontology.add(new AxiomDRoleSubsumption(ax, R, S));
	}

	/** axiom R1 = R2 = ... */
	public Axiom equalORoles(final OWLAxiom ax, final List<Expression> l) {
		return this.ontology.add(new AxiomEquivalentORoles(ax, l));
	}

	/** axiom R1 = R2 = ... */
	public Axiom equalDRoles(final OWLAxiom ax, final List<Expression> l) {
		return this.ontology.add(new AxiomEquivalentDRoles(ax, l));
	}

	/** axiom R1 != R2 != ... */
	public Axiom disjointORoles(final OWLAxiom ax, final List<Expression> l) {
		return this.ontology.add(new AxiomDisjointORoles(ax, l));
	}

	/** axiom R1 != R2 != ... */
	public Axiom disjointDRoles(final OWLAxiom ax, final List<Expression> l) {
		return this.ontology.add(new AxiomDisjointDRoles(ax, l));
	}

	/** Domain (R C) */
	public Axiom setODomain(final OWLAxiom ax, final ObjectRoleExpression R,
			final ConceptExpression C) {
		return this.ontology.add(new AxiomORoleDomain(ax, R, C));
	}

	/** Domain (R C) */
	public Axiom setDDomain(final OWLAxiom ax, final DataRoleExpression R,
			final ConceptExpression C) {
		return this.ontology.add(new AxiomDRoleDomain(ax, R, C));
	}

	/** Range (R C) */
	public Axiom setORange(final OWLAxiom ax, final ObjectRoleExpression R,
			final ConceptExpression C) {
		return this.ontology.add(new AxiomORoleRange(ax, R, C));
	}

	/** Range (R E) */
	public Axiom setDRange(final OWLAxiom ax, final DataRoleExpression R,
			final DataExpression E) {
		return this.ontology.add(new AxiomDRoleRange(ax, R, E));
	}

	/** Transitive (R) */
	public Axiom setTransitive(final OWLAxiom ax, final ObjectRoleExpression R) {
		return this.ontology.add(new AxiomRoleTransitive(ax, R));
	}

	/** Reflexive (R) */
	public Axiom setReflexive(final OWLAxiom ax, final ObjectRoleExpression R) {
		return this.ontology.add(new AxiomRoleReflexive(ax, R));
	}

	/** Irreflexive (R): Domain(R) = \neg ER.Self */
	public Axiom setIrreflexive(final OWLAxiom ax, final ObjectRoleExpression R) {
		return this.ontology.add(new AxiomRoleIrreflexive(ax, R));
	}

	/** Symmetric (R): R [= R^- */
	public Axiom setSymmetric(final OWLAxiom ax, final ObjectRoleExpression R) {
		return this.ontology.add(new AxiomRoleSymmetric(ax, R));
	}

	/** AntySymmetric (R): disjoint(R,R^-) */
	public Axiom setAsymmetric(final OWLAxiom ax, final ObjectRoleExpression R) {
		return this.ontology.add(new AxiomRoleAsymmetric(ax, R));
	}

	/** Functional (R) */
	public Axiom setOFunctional(final OWLAxiom ax, final ObjectRoleExpression R) {
		return this.ontology.add(new AxiomORoleFunctional(ax, R));
	}

	/** Functional (R) */
	public Axiom setDFunctional(final OWLAxiom ax, final DataRoleExpression R) {
		return this.ontology.add(new AxiomDRoleFunctional(ax, R));
	}

	/** InverseFunctional (R) */
	public Axiom setInverseFunctional(final OWLAxiom ax, final ObjectRoleExpression R) {
		return this.ontology.add(new AxiomRoleInverseFunctional(ax, R));
	}

	// Individual axioms
	/** axiom I e C */
	public Axiom instanceOf(final OWLAxiom ax, final IndividualExpression I,
			final ConceptExpression C) {
		return this.ontology.add(new AxiomInstanceOf(ax, I, C));
	}

	/** axiom <I,J>:R */
	public Axiom relatedTo(final OWLAxiom ax, final IndividualExpression I,
			final ObjectRoleExpression R, final IndividualExpression J) {
		return this.ontology.add(new AxiomRelatedTo(ax, I, R, J));
	}

	/** axiom <I,J>:\neg R */
	public Axiom relatedToNot(final OWLAxiom ax, final IndividualExpression I,
			final ObjectRoleExpression R, final IndividualExpression J) {
		return this.ontology.add(new AxiomRelatedToNot(ax, I, R, J));
	}

	/** axiom (value I A V) */
	public Axiom valueOf(final OWLAxiom ax, final IndividualExpression I,
			final DataRoleExpression A, final Literal<?> V) {
		return this.ontology.add(new AxiomValueOf(ax, I, A, V));
	}

	/** axiom <I,V>:\neg A */
	public Axiom valueOfNot(final OWLAxiom ax, final IndividualExpression I,
			final DataRoleExpression A, final Literal<?> V) {
		return this.ontology.add(new AxiomValueOfNot(ax, I, A, V));
	}

	/** same individuals */
	public Axiom processSame(final OWLAxiom ax, final List<Expression> l) {
		return this.ontology.add(new AxiomSameIndividuals(ax, l));
	}

	/** different individuals */
	public Axiom processDifferent(final OWLAxiom ax, final List<Expression> l) {
		return this.ontology.add(new AxiomDifferentIndividuals(ax, l));
	}

	/** let all concept expressions in the ArgQueue to be fairness constraints */
	public Axiom setFairnessConstraint(final OWLAxiom ax, final List<Expression> l) {
		return this.ontology.add(new AxiomFairnessConstraint(ax, l));
	}

	/** retract an axiom */
	public void retract(final Axiom axiom) {
		this.ontology.retract(axiom);
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
		if (this.getStatus().ordinal() <= kbLoading.ordinal()) {
			this.processKB(kbCChecked);
		}
		return this.getTBox().isConsistent();
	}

	/** ensure that KB is preprocessed/consistence checked */
	private void preprocessKB() {
		if (!this.isKBConsistent()) {
			throw new InconsistentOntologyException();
		}
	}

	/** ensure that KB is classified */
	public void classifyKB() {
		if (!this.isKBClassified()) {
			this.processKB(kbClassified);
		}
		if (!this.isKBConsistent()) {
			throw new InconsistentOntologyException();
		}
	}

	/** ensure that KB is realised */
	public void realiseKB() {
		if (!this.isKBRealised()) {
			this.processKB(kbRealised);
		}
		if (!this.isKBConsistent()) {
			throw new InconsistentOntologyException();
		}
	}

	// role info retrieval
	/** @return true iff object role is functional */
	public boolean isFunctional(final ObjectRoleExpression R) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role r = this.getRole(R, "Role expression expected in isFunctional()");
		if (r.isTop()) {
			return true; // universal role is symmetric
		}
		if (r.isBottom()) {
			return true; // empty role is symmetric
		}
		return this.getFunctionality(r);
	}

	/** @return true iff data role is functional */
	public boolean isFunctional(final DataRoleExpression R) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role r = this.getRole(R, "Role expression expected in isFunctional()");
		if (r.isTop()) {
			return true; // universal role is symmetric
		}
		if (r.isBottom()) {
			return true; // empty role is symmetric
		}
		return this.getFunctionality(r);
	}

	/** @return true iff role is inverse-functional */
	public boolean isInverseFunctional(final ObjectRoleExpression R) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role r = this.getRole(R, "Role expression expected in isInverseFunctional()")
				.inverse();
		if (r.isTop()) {
			return true; // universal role is symmetric
		}
		if (r.isBottom()) {
			return true; // empty role is symmetric
		}
		return this.getFunctionality(r);
	}

	/** @return true iff role is transitive */
	public boolean isTransitive(final ObjectRoleExpression R) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role r = this.getRole(R, "Role expression expected in isTransitive()");
		if (r.isTop()) {
			return true; // universal role is symmetric
		}
		if (r.isBottom()) {
			return true; // empty role is symmetric
		}
		if (!r.isTransitivityKnown()) {
			r.setTransitive(this.checkTransitivity(this.e(R)));
		}
		return r.isTransitive();
	}

	/** @return true iff role is symmetric */
	public boolean isSymmetric(final ObjectRoleExpression R) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role r = this.getRole(R, "Role expression expected in isSymmetric()");
		if (r.isTop()) {
			return true; // universal role is symmetric
		}
		if (r.isBottom()) {
			return true; // empty role is symmetric
		}
		if (!r.isSymmetryKnown()) {
			r.setSymmetric(this.checkSymmetry(this.e(R)));
		}
		return r.isSymmetric();
	}

	/** @return true iff role is asymmetric */
	public boolean isAsymmetric(final ObjectRoleExpression R) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role r = this.getRole(R, "Role expression expected in isAsymmetric()");
		if (r.isTop()) {
			return true; // universal role is symmetric
		}
		if (r.isBottom()) {
			return true; // empty role is symmetric
		}
		if (!r.isAsymmetryKnown()) {
			r.setAsymmetric(this.getTBox().isDisjointRoles(r, r.inverse()));
		}
		return r.isAsymmetric();
	}

	/** @return true iff role is reflexive */
	public boolean isReflexive(final ObjectRoleExpression R) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role r = this.getRole(R, "Role expression expected in isReflexive()");
		if (r.isTop()) {
			return true; // universal role is symmetric
		}
		if (r.isBottom()) {
			return true; // empty role is symmetric
		}
		if (!r.isReflexivityKnown()) {
			r.setReflexive(this.checkReflexivity(this.e(R)));
		}
		return r.isReflexive();
	}

	/** @return true iff role is irreflexive */
	public boolean isIrreflexive(final ObjectRoleExpression R) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role r = this.getRole(R, "Role expression expected in isIrreflexive()");
		if (r.isTop()) {
			return true; // universal role is symmetric
		}
		if (r.isBottom()) {
			return true; // empty role is symmetric
		}
		if (!r.isIrreflexivityKnown()) {
			r.setIrreflexive(this.getTBox().isIrreflexive(r));
		}
		return r.isIrreflexive();
	}

	/** @return true iff two roles are disjoint */
	public boolean isDisjointRoles(final ObjectRoleExpression R,
			final ObjectRoleExpression S) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role r = this.getRole(R, "Role expression expected in isDisjointRoles()");
		Role s = this.getRole(S, "Role expression expected in isDisjointRoles()");
		if (r.isTop() || s.isTop()) {
			return false; // universal role is not disjoint with anything
		}
		if (r.isBottom() || s.isBottom()) {
			return true; // empty role is disjoint with everything
		}
		return this.getTBox().isDisjointRoles(r, s);
	}

	/** @return true iff two roles are disjoint */
	public boolean isDisjointRoles(final DataRoleExpression R, final DataRoleExpression S) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role r = this.getRole(R, "Role expression expected in isDisjointRoles()");
		Role s = this.getRole(S, "Role expression expected in isDisjointRoles()");
		if (r.isTop() || s.isTop()) {
			return false; // universal role is not disjoint with anything
		}
		if (r.isBottom() || s.isBottom()) {
			return true; // empty role is disjoint with everything
		}
		return this.getTBox().isDisjointRoles(r, s);
	}

	/** @return true if R is a sub-role of S */
	public boolean isSubRoles(final ObjectRoleComplexExpression R,
			final ObjectRoleExpression S) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role r = this.getRole(R, "Role expression expected in isSubRoles()");
		Role s = this.getRole(S, "Role expression expected in isSubRoles()");
		if (r.isBottom() || s.isTop()) {
			return true; // \bot [= X [= \top
		}
		if (r.isTop() && s.isBottom()) {
			return false; // as \top [= \bot leads to inconsistent ontology
		}
		if (this.getExpressionManager().isEmptyRole(R)
				|| this.getExpressionManager().isUniversalRole(S)) {
			return true; // \bot [= X [= \top
		}
		if (this.getExpressionManager().isUniversalRole(R)
				&& this.getExpressionManager().isEmptyRole(S)) {
			return false; // as \top [= \bot leads to inconsistent ontology
		}
		// told case first
		if (!r.isTop() && !s.isBottom() && r.lesserequal(s)) {
			return true;
		}
		// check the general case
		// FIXME!! cache it later
		//DLTree r = e(R), s = e(S);
		return this.checkRoleSubsumption(r, s);
	}

	// single satisfiability
	/** @return true iff C is satisfiable */
	public boolean isSatisfiable(final ConceptExpression C) {
		this.preprocessKB();
		try {
			return this.checkSat(C);
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
		this.preprocessKB();
		if (this.isNameOrConst(D) && this.isNameOrConst(C)) {
			return this.checkSub(this.getTBox().getCI(this.e(C)),
					this.getTBox().getCI(this.e(D)));
		}
		DLTree nD = DLTreeFactory.createSNFNot(this.e(D));
		return !this.checkSatTree(DLTreeFactory.createSNFAnd(this.e(C), nD));
		//		return !this.checkSat(this.getExpressionManager().and(C,
		//				this.getExpressionManager().not(D)));
	}

	/** @return true iff C is disjoint with D; that is, C [= \not D holds */
	public boolean isDisjoint(final ConceptExpression C, final ConceptExpression D) {
		return this.isSubsumedBy(C, this.getExpressionManager().not(D));
	}

	/** @return true iff C is equivalent to D */
	public boolean isEquivalent(final ConceptExpression C, final ConceptExpression D) {
		if (C == D) {
			return true;
		}
		this.preprocessKB();
		if (this.isKBClassified()) { // try to detect C=D wrt named concepts
			if (this.isNameOrConst(D) && this.isNameOrConst(C)) {
				TaxonomyVertex cV = this.getTBox().getCI(this.e(C)).getTaxVertex();
				TaxonomyVertex dV = this.getTBox().getCI(this.e(D)).getTaxVertex();
				if (cV == null && dV == null) {
					return false; // 2 different fresh names
				}
				return cV == dV;
			}
		}
		// not classified or not named constants
		return this.isSubsumedBy(C, D) && this.isSubsumedBy(D, C);
	}

	// concept hierarchy
	/** apply actor__apply() to all DIRECT super-concepts of [complex] C */
	public void getSupConcepts(final ConceptExpression C, final boolean direct,
			final Actor actor) {
		this.classifyKB(); // ensure KB is ready to answer the query
		this.setUpCache(C, csClassified);
		Taxonomy tax = this.getCTaxonomy();
		if (direct) {
			tax.getRelativesInfo(this.cachedVertex, actor, false, true, true);
		} else {
			tax.getRelativesInfo(this.cachedVertex, actor, false, false, true);
		}
	}

	/** apply actor__apply() to all DIRECT sub-concepts of [complex] C */
	public void getSubConcepts(final ConceptExpression C, final boolean direct,
			final Actor actor) {
		this.classifyKB(); // ensure KB is ready to answer the query
		this.setUpCache(C, csClassified);
		Taxonomy tax = this.getCTaxonomy();
		tax.getRelativesInfo(this.cachedVertex, actor, false, direct, false);
	}

	/** apply actor__apply() to all synonyms of [complex] C */
	public void getEquivalentConcepts(final ConceptExpression C, final Actor actor) {
		this.classifyKB(); // ensure KB is ready to answer the query
		this.setUpCache(C, csClassified);
		actor.apply(this.cachedVertex);
	}

	/// apply actor::apply() to all named concepts disjoint with [complex] C
	public void getDisjointConcepts(final ConceptExpression C, final Actor actor) {
		this.classifyKB(); // ensure KB is ready to answer the query
		this.setUpCache(this.getExpressionManager().not(C), csClassified);
		Taxonomy tax = this.getCTaxonomy();
		// we are looking for all sub-concepts of (not C) (including synonyms to it)
		tax.getRelativesInfo(this.cachedVertex, actor, true, false, false);
	}

	// role hierarchy
	/** apply actor__apply() to all DIRECT super-roles of [complex] R */
	public void getSupRoles(final RoleExpression r, final boolean direct,
			final Actor actor) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role R = this.getRole(r, "Role expression expected in getSupRoles()");
		Taxonomy tax = this.getTaxonomy(R);
		tax.getRelativesInfo(this.getTaxVertex(R), actor, false, direct, true);
	}

	/** apply actor__apply() to all DIRECT sub-roles of [complex] R */
	public void getSubRoles(final RoleExpression r, final boolean direct,
			final Actor actor) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role R = this.getRole(r, "Role expression expected in getSubRoles()");
		Taxonomy tax = this.getTaxonomy(R);
		tax.getRelativesInfo(this.getTaxVertex(R), actor, false, direct, false);
	}

	/** apply actor__apply() to all synonyms of [complex] R */
	public void getEquivalentRoles(final RoleExpression r, final Actor actor) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role R = this.getRole(r, "Role expression expected in getEquivalentRoles()");
		actor.apply(this.getTaxVertex(R));
	}

	// domain and range as a set of named concepts
	/**
	 * apply actor__apply() to all DIRECT NC that are in the domain of [complex]
	 * R
	 */
	public void getORoleDomain(final ObjectRoleExpression r, final boolean direct,
			final Actor actor) {
		this.classifyKB(); // ensure KB is ready to answer the query
		this.setUpCache(
				this.getExpressionManager().exists(r, this.getExpressionManager().top()),
				csClassified);
		Taxonomy tax = this.getCTaxonomy();
		tax.getRelativesInfo(this.cachedVertex, actor, true, direct, true);
	}

	/// apply actor::apply() to all DIRECT NC that are in the domain of data role R
	//template<class Actor>
	void getDRoleDomain(final DataRoleExpression r, final boolean direct,
			final Actor actor) {
		this.classifyKB(); // ensure KB is ready to answer the query
		this.setUpCache(
				this.getExpressionManager().exists(r,
						this.getExpressionManager().dataTop()), csClassified);
		Taxonomy tax = this.getCTaxonomy();
		if (direct) {
			tax.getRelativesInfo(this.cachedVertex, actor, true, true, true);
		} else {
			// gets all named classes that are in the domain of a role
			tax.getRelativesInfo(this.cachedVertex, actor, true, false, true);
		}
	}

	/**
	 * apply actor__apply() to all DIRECT NC that are in the range of [complex]
	 * R
	 */
	public void getRoleRange(final ObjectRoleExpression r, final boolean direct,
			final Actor actor) {
		this.getORoleDomain(this.getExpressionManager().inverse(r), direct, actor);
	}

	// instances
	public void getInstances(final ConceptExpression C, final Actor actor,
			final boolean direct) {
		if (direct) {
			this.getDirectInstances(C, actor);
		} else {
			this.getInstances(C, actor);
		}
	}

	/** apply actor__apply() to all direct instances of given [complex] C */
	public void getDirectInstances(final ConceptExpression C, final Actor actor) {
		this.realiseKB(); // ensure KB is ready to answer the query
		this.setUpCache(C, csClassified);
		// implement 1-level check by hand
		// if the root vertex contains individuals -- we are done
		if (actor.apply(this.cachedVertex)) {
			return;
		}
		// if not, just go 1 level down and apply the actor regardless of what's found
		// FIXME!! check again after bucket-method will be implemented
		for (TaxonomyVertex p : this.cachedVertex.neigh(/* upDirection= */false)) {
			actor.apply(p);
		}
	}

	/** apply actor__apply() to all instances of given [complex] C */
	public void getInstances(final ConceptExpression C, final Actor actor) { // FIXME!! check for Racer's/IS approach
		this.realiseKB(); // ensure KB is ready to answer the query
		this.setUpCache(C, csClassified);
		Taxonomy tax = this.getCTaxonomy();
		tax.getRelativesInfo(this.cachedVertex, actor, true, false, false);
	}

	/**
	 * apply actor__apply() to all DIRECT concepts that are types of an
	 * individual I
	 */
	public void getTypes(final IndividualExpression I, final boolean direct,
			final Actor actor) {
		this.realiseKB(); // ensure KB is ready to answer the query
		this.setUpCache(this.getExpressionManager().oneOf(I), csClassified);
		Taxonomy tax = this.getCTaxonomy();
		tax.getRelativesInfo(this.cachedVertex, actor, true, direct, true);
	}

	/** apply actor__apply() to all synonyms of an individual I */
	public void getSameAs(final IndividualExpression I, final Actor actor) {
		this.realiseKB(); // ensure KB is ready to answer the query
		this.getEquivalentConcepts(this.getExpressionManager().oneOf(I), actor);
	}

	/** @return true iff I and J refer to the same individual */
	public boolean isSameIndividuals(final IndividualExpression I,
			final IndividualExpression J) {
		this.realiseKB();
		Individual i = this.getIndividual(I,
				"Only known individuals are allowed in the isSameAs()");
		Individual j = this.getIndividual(J,
				"Only known individuals are allowed in the isSameAs()");
		return this.getTBox().isSameIndividuals(i, j);
	}

	//----------------------------------------------------------------------------------
	// knowledge exploration queries
	//----------------------------------------------------------------------------------
	/// build a completion tree for a concept expression C (no caching as it breaks the idea of KE). @return the root node
	public DlCompletionTree buildCompletionTree(final ConceptExpression C) {
		this.preprocessKB();
		this.setUpCache(C, csSat);
		DlCompletionTree ret = this.getTBox().buildCompletionTree(this.cachedConcept);
		// init KB after the sat test to reduce the number of DAG adjustments
		if (this.KE == null) {
			this.KE = new KnowledgeExplorer(this.getTBox(), this.getExpressionManager());
		}
		return ret;
		//	return getTBox().buildCompletionTree(cachedConcept);
	}

	public KnowledgeExplorer getKnowledgeExplorer() {
		return this.KE;
	}

	/// build the set of data neighbours of a NODE, put the set of data roles into the RESULT variable
	public Set<RoleExpression> getDataRoles(final DlCompletionTree node,
			final boolean onlyDet) {
		return this.KE.getDataRoles(node, onlyDet);
	}

	/// build the set of object neighbours of a NODE, put the set of object roles and inverses into the RESULT variable
	public Set<RoleExpression> getObjectRoles(final DlCompletionTree node,
			final boolean onlyDet, final boolean needIncoming) {
		return this.KE.getObjectRoles(node, onlyDet, needIncoming);
	}

	/// build the set of neighbours of a NODE via role ROLE; put the resulting list into RESULT
	public List<DlCompletionTree> getNeighbours(final DlCompletionTree node,
			final RoleExpression role) {
		return this.KE.getNeighbours(node,
				this.getRole(role, "Role expression expected in getNeighbours() method"));
	}

	/// put into RESULT all the expressions from the NODE label; if ONLYDET is true, return only deterministic elements
	public List<ConceptExpression> getObjectLabel(final DlCompletionTree node,
			final boolean onlyDet) {
		return this.KE.getObjectLabel(node, onlyDet);
	}

	public List<DataExpression> getDataLabel(final DlCompletionTree node,
			final boolean onlyDet) {
		return this.KE.getDataLabel(node, onlyDet);
	}

	//----------------------------------------------------------------------------------
	// atomic decomposition queries
	//----------------------------------------------------------------------------------
	/** @return true iff individual I is instance of given [complex] C */
	public boolean isInstance(final IndividualExpression I, final ConceptExpression C) {
		this.realiseKB(); // ensure KB is ready to answer the query
		this.getIndividual(I, "individual name expected in the isInstance()");
		// FIXME!! this way a new concept is created; could be done more optimal
		return this.isSubsumedBy(this.getExpressionManager().oneOf(I), C);
	}

	public ReasoningKernel(final JFactReasonerConfiguration conf,
			final DatatypeFactory factory) {
		// should be commented
		this.cachedQuery = null;
		this.cachedQueryTree = null;
		this.kernelOptions = conf;
		this.datatypeFactory = factory;
		this.pTBox = null;
		this.pET = null;
		this.ModSyn = null;
		this.ModSem = null;
		this.cachedQuery = null;
		this.initCacheAndFlags();
		this.useAxiomSplitting = false;
	}

	/// try to perform the incremental reasoning on the changed ontology
	private boolean tryIncremental() {
		if (this.pTBox == null) {
			return true;
		}
		if (!this.ontology.isChanged()) {
			return false;
		}
		return true;
	}

	/// force the re-classification of the changed ontology
	private void forceReload() {
		this.clearTBox();
		this.newKB();
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
		if (this.kernelOptions.isSplits()) {
			TAxiomSplitter AxiomSplitter = new TAxiomSplitter(this.ontology);
			AxiomSplitter.buildSplit();
		}
		OntologyLoader OntologyLoader = new OntologyLoader(this.getTBox());
		OntologyLoader.visitOntology(this.ontology);
		this.ontology.setProcessed();
	}

	//----------------------------------------------------------------------------------
	// knowledge exploration queries
	//----------------------------------------------------------------------------------
	/// add the role R and all its supers to a set RESULT
	//void addRoleWithSupers (  Role R, TCGRoleSet Result );
	private void processKB(final KBStatus status) {
		assert status.ordinal() >= kbCChecked.ordinal();
		// check whether reasoning was failed
		if (this.reasoningFailed) {
			throw new ReasonerInternalException(
					"Can't classify KB because of previous errors");
		}
		// check if something have to be done
		if (this.getStatus().ordinal() >= status.ordinal()) { // nothing to do; but make sure that we are consistent
			if (!this.isKBConsistent()) {
				throw new InconsistentOntologyException();
			}
			return;
		}
		// here we have to do something: let's decide what to do
		boolean stillGo = true;
		switch (this.getStatus()) {
			case kbEmpty:
			case kbLoading:
				break; // need to do the whole cycle -- just after the switch
			case kbCChecked: {
				this.classify(status);
				stillGo = false;
				break; // do classification
			}
			case kbClassified: {
				this.realise();
				stillGo = false;
				break;
			} // do realisation
			default: // nothing should be here
				throw new UnreachableSituationException();
		}
		if (stillGo) {
			// start with loading and preprocessing -- here might be a failures
			this.reasoningFailed = true;
			// load the axioms from the ontology to the TBox
			if (this.tryIncremental()) {
				this.forceReload();
			}
			// do the consistency check
			this.pTBox.isConsistent();
			// if there were no exception thrown -- clear the failure status
			this.reasoningFailed = false;
			if (status == kbCChecked) {
				return;
			}
			this.classify(status);
		}
	}

	// do classification
	private void classify(final KBStatus status) {
		// don't do classification twice
		if (status != kbRealised) {
			//goto Realise;
			if (!this.pTBox.isConsistent()) {
				return;
			}
			this.pTBox.performClassification();
			return;
		}
		this.realise();
	}

	// do realisation
	private void realise() {
		if (!this.pTBox.isConsistent()) {
			return;
		}
		this.pTBox.performRealisation();
	}

	//-----------------------------------------------------------------------------
	//--		query caching support
	//-----------------------------------------------------------------------------
	/// classify query; cache is ready at the point. NAMED means whether concept is just a name
	void classifyQuery(final boolean named) {
		// make sure KB is classified
		this.classifyKB();
		if (!named) {
			this.getTBox().classifyQueryConcept();
		}
		this.cachedVertex = this.cachedConcept.getTaxVertex();
		if (this.cachedVertex == null) {
			this.cachedVertex = this.getCTaxonomy().getFreshVertex(this.cachedConcept);
		}
	}

	void setUpCache(final DLTree query, final CacheStatus level) {
		// if KB was changed since it was classified,
		// we should catch it before
		assert !this.ontology.isChanged();
		// check if the query is already cached
		if (this.checkQueryCache(query)) { // ... with the same level -- nothing to do
											//deleteTree(query);
											//query=null;
			if (level.ordinal() <= this.cacheLevel.ordinal()) {
				return;
			} else { // concept was defined but not classified yet
				assert level == csClassified && this.cacheLevel != csClassified;
				if (this.cacheLevel == csSat) // already check satisfiability
				{
					this.classifyQuery(this.cachedQueryTree.isCN());
					return;
				}
			}
		} else {
			// change current query
			this.setQueryCache(query);
		}
		// clean cached info
		this.cachedVertex = null;
		this.cacheLevel = level;
		// check if concept-to-cache is defined in ontology
		if (this.cachedQueryTree.isCN()) {
			this.cachedConcept = this.getTBox().getCI(this.cachedQueryTree);
		} else // case of complex query
		{
			//getTBox().clearQueryConcept();
			this.cachedConcept = this.getTBox().createQueryConcept(this.cachedQueryTree);
		}
		assert this.cachedConcept != null;
		// preprocess concept is necessary (fresh concept in query or complex one)
		if (this.cachedConcept.getpName() == 0) {
			this.getTBox().preprocessQueryConcept(this.cachedConcept);
		}
		if (level == csClassified) {
			this.classifyQuery(this.cachedQueryTree.isCN());
		}
	}

	void setUpCache(final ConceptExpression query, final CacheStatus level) {
		// if KB was changed since it was classified,
		// we should catch it before
		assert !this.ontology.isChanged();
		// check if the query is already cached
		if (this.checkQueryCache(query)) { // ... with the same level -- nothing to do
			if (level.ordinal() <= this.cacheLevel.ordinal()) {
				return;
			} else { // concept was defined but not classified yet
				assert level == csClassified && this.cacheLevel != csClassified;
				if (this.cacheLevel == csSat) // already check satisfiability
				{
					this.classifyQuery(this.isNameOrConst(this.cachedQuery));
					return;
				}
			}
		} else {
			// change current query
			this.setQueryCache(query);
		}
		// clean cached info
		this.cachedVertex = null;
		this.cacheLevel = level;
		// check if concept-to-cache is defined in ontology
		if (this.isNameOrConst(this.cachedQuery)) {
			this.cachedConcept = this.getTBox().getCI(this.e(this.cachedQuery));
		} else // case of complex query
		{
			// need to clear the query before transform it into DLTree
			//getTBox().clearQueryConcept();
			// ... as if fresh names appears there, they would be cleaned up
			this.cachedConcept = this.getTBox().createQueryConcept(
					this.e(this.cachedQuery));
		}
		assert this.cachedConcept != null;
		// preprocess concept is necessary (fresh concept in query or complex one)
		if (this.cachedConcept.getpName() == 0) {
			this.getTBox().preprocessQueryConcept(this.cachedConcept);
		}
		if (level == csClassified) {
			this.classifyQuery(this.isNameOrConst(this.cachedQuery));
		}
	}

	//----------------------------------------------------------------------------------
	// atomic decomposition queries
	//----------------------------------------------------------------------------------
	/// create new atomic decomposition of the loaded ontology using TYPE. @return size of the AD
	public int getAtomicDecompositionSize(final boolean useSemantics,
			final ModuleType type) {
		// init AD field
		if (this.AD == null) {
			//XXX
			if (useSemantics) {
				this.AD = new AtomicDecomposer(new SemanticLocalityChecker(this,
						new TSignature()));
			} else {
				this.AD = new AtomicDecomposer(new SyntacticLocalityChecker(
						new TSignature()));
			}
		}
		return this.AD.getAOS(this.ontology, type).size();
	}

	/// get a set of axioms that corresponds to the atom with the id INDEX
	public Set<Axiom> getAtomAxioms(final int index) {
		return this.AD.getAOS().get(index).getAtomAxioms();
	}

	/// get a set of axioms that corresponds to the module of the atom with the id INDEX
	public Set<Axiom> getAtomModule(final int index) {
		return this.AD.getAOS().get(index).getModule();
	}

	/// get a set of atoms on which atom with index INDEX depends
	public Set<TOntologyAtom> getAtomDependents(final int index) {
		return this.AD.getAOS().get(index).getDepAtoms();
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
		DLTree tmp = DLTreeFactory.createSNFNot(this.getTBox().getFreshConcept());
		for (int i = l.size() - 1; i > -1; i--) {
			ObjectRoleExpression p = l.get(i);
			tmp = DLTreeFactory.createSNFExists(this.e(p), tmp);
		}
		tmp = DLTreeFactory
				.createSNFAnd(tmp, DLTreeFactory.createSNFForall(DLTreeFactory
						.buildTree(new Lexeme(Token.RNAME, R)), this.getTBox()
						.getFreshConcept()));
		return !this.checkSatTree(tmp);
	}

	/** @return true if R is a super-role of a chain holding in the args */
	public boolean isSubChain(final ObjectRoleComplexExpression R,
			final List<ObjectRoleExpression> l) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		Role r = this.getRole(R, "Role expression expected in isSubChain()");
		if (r.isTop()) {
			return true; // universal role is a super of any chain
		}
		if (r.isBottom()) {
			for (ObjectRoleExpression p : l) {
				Role S = this.getRole(p,
						"Role expression expected in chain of isSubChain()");
				if (S.isBottom()) {
					return true;
				}
			}
			return false; // empty role is not a super of any chain
		}
		return this.checkSubChain(r, l);
	}

	/** @return true if R is a sub-role of S */
	public boolean isSubRoles(final DataRoleExpression R, final DataRoleExpression S) {
		this.preprocessKB(); // ensure KB is ready to answer the query
		if (this.getExpressionManager().isEmptyRole(R)
				|| this.getExpressionManager().isUniversalRole(S)) {
			return true; // \bot [= X [= \top
		}
		if (this.getExpressionManager().isUniversalRole(R)
				&& this.getExpressionManager().isEmptyRole(S)) {
			return false; // as \top [= \bot leads to inconsistent ontology
		}
		// told case first
		final uk.ac.manchester.cs.jfact.kernel.Role r = this.getRole(R,
				"Role expression expected in isSubRoles()");
		final uk.ac.manchester.cs.jfact.kernel.Role s = this.getRole(S,
				"Role expression expected in isSubRoles()");
		if (!r.isTop() && !s.isBottom() && r.lesserequal(s)) {
			return true;
		}
		// check the general case
		// FIXME!! cache it later
		return this.checkRoleSubsumption(r, s);
	}

	// all-disjoint query implementation
	public boolean isDisjointRoles(final List<Expression> l) {
		// grab all roles from the arg-list
		//List<TDLExpression> Disj = getExpressionManager().getArgList();
		List<Role> Roles = new ArrayList<Role>(l.size());
		for (Expression p : l) {
			if (p instanceof ObjectRoleExpression) {
				ObjectRoleExpression ORole = (ObjectRoleExpression) p;
				if (this.getExpressionManager().isUniversalRole(ORole)) {
					return false; // universal role is not disjoint with anything
				}
				if (this.getExpressionManager().isEmptyRole(ORole)) {
					continue; // empty role is disjoint with everything
				}
				Roles.add(this.getRole(ORole,
						"Role expression expected in isDisjointRoles()"));
			} else {
				if (!(p instanceof DataRoleExpression)) {
					throw new ReasonerInternalException(
							"Role expression expected in isDisjointRoles()");
				}
				DataRoleExpression DRole = (DataRoleExpression) p;
				if (this.getExpressionManager().isUniversalRole(DRole)) {
					return false; // universal role is not disjoint with anything
				}
				if (this.getExpressionManager().isEmptyRole(DRole)) {
					continue; // empty role is disjoint with everything
				}
				Roles.add(this.getRole(DRole,
						"Role expression expected in isDisjointRoles()"));
			}
		}
		// test pair-wise disjointness
		for (int i = 0; i < Roles.size() - 1; i++) {
			for (int j = i + 1; j < Roles.size(); j++) {
				if (!this.getTBox().isDisjointRoles(Roles.get(i), Roles.get(j))) {
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
			if (this.getExpressionManager().isUniversalRole(p)) {
				return false; // universal role is not disjoint with anything
			}
			if (this.getExpressionManager().isEmptyRole(p)) {
				continue; // empty role is disjoint with everything
			}
			Roles.add(this.getRole(p, "Role expression expected in isDisjointRoles()"));
		}
		// test pair-wise disjointness
		for (int i = 0; i < Roles.size() - 1; i++) {
			for (int j = i + 1; j < Roles.size(); j++) {
				if (!this.getTBox().isDisjointRoles(Roles.get(i), Roles.get(j))) {
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
			if (this.getExpressionManager().isUniversalRole(p)) {
				return false; // universal role is not disjoint with anything
			}
			if (this.getExpressionManager().isEmptyRole(p)) {
				continue; // empty role is disjoint with everything
			}
			Roles.add(this.getRole(p, "Role expression expected in isDisjointRoles()"));
		}
		// test pair-wise disjointness
		for (int i = 0; i < Roles.size() - 1; i++) {
			for (int j = i + 1; j < Roles.size(); j++) {
				if (!this.getTBox().isDisjointRoles(Roles.get(i), Roles.get(j))) {
					return false;
				}
			}
		}
		return true;
	}

	private List<Individual> buildRelatedCache(final Individual I, final Role R) {
		if (R.isSynonym()) {
			return this.getRelated(I, ClassifiableEntry.resolveSynonym(R));
		}
		if (R.isDataRole() || R.isBottom()) {
			return new ArrayList<Individual>();
		}
		RIActor actor = new RIActor();
		ObjectRoleExpression InvR = R.getId() > 0 ? this.getExpressionManager().inverse(
				this.getExpressionManager().objectRole(R.getName())) : this
				.getExpressionManager().objectRole(R.inverse().getName());
		ConceptExpression query;
		if (R.isTop()) {
			query = this.getExpressionManager().top();
		} else {
			query = this.getExpressionManager().value(InvR,
					this.getExpressionManager().individual(I.getName()));
		}
		this.getInstances(query, actor);
		return actor.getAcc();
	}

	public List<Individual> getRoleFillers(final IndividualExpression I,
			final ObjectRoleExpression R) {
		this.realiseKB();
		return this
				.getRelated(this.getIndividual(I,
						"Individual name expected in the getRoleFillers()"), this
						.getRole(R, "Role expression expected in the getRoleFillers()"));
	}

	public boolean isRelated(final IndividualExpression I, final ObjectRoleExpression R,
			final IndividualExpression J) {
		this.realiseKB();
		Individual i = this.getIndividual(I,
				"Individual name expected in the isRelated()");
		Role r = this.getRole(R, "Role expression expected in the isRelated()");
		if (r.isDataRole()) {
			return false;
		}
		Individual j = this.getIndividual(J,
				"Individual name expected in the isRelated()");
		List<Individual> vec = this.getRelated(i, r);
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
