package uk.ac.manchester.cs.jfact.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
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
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorAdapter;

@SuppressWarnings("unused")
//XXX verify unused parameters
public class ELFAxiomChecker extends DLAxiomVisitorAdapter {
	ELFExpressionChecker eCh = new ELFExpressionChecker();
	boolean value;

	boolean v(final Expression expr) {
		value = eCh.v(expr);
		return value;
	}

	@Override
	public void visit(final AxiomDeclaration axiom) {
		v(axiom.getDeclaration());
	}

	@Override
	public void visit(final AxiomEquivalentConcepts axiom) {
		value = false;
		for (Expression p : axiom.getArguments()) {
			if (!v(p)) {
				return;
			}
		}
		value = true;
	}

	@Override
	public void visit(final AxiomDisjointConcepts axiom) {
		value = false;
		for (Expression p : axiom.getArguments()) {
			if (!v(p)) {
				return;
			}
		}
		value = true;
	}

	@Override
	public void visit(final AxiomDisjointUnion axiom) {
		value = axiom.size() > 1;
	}

	@Override
	public void visit(final AxiomEquivalentORoles axiom) {
		value = false;
		for (Expression p : axiom.getArguments()) {
			if (!v(p)) {
				return;
			}
		}
		value = true;
	}

	@Override
	public void visit(final AxiomEquivalentDRoles axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomDisjointORoles axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomDisjointDRoles axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomSameIndividuals axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomDifferentIndividuals axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomFairnessConstraint axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomRoleInverse axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomORoleSubsumption axiom) {
		if (v(axiom.getSubRole())) {
			v(axiom.getRole());
		}
	}

	@Override
	public void visit(final AxiomDRoleSubsumption axiom) {
		value = false;
	}

	// FIXME!! check later
	@Override
	public void visit(final AxiomORoleDomain axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomDRoleDomain axiom) {
		value = false;
	}

	// FIXME!! check later
	@Override
	public void visit(final AxiomORoleRange axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomDRoleRange axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomRoleTransitive axiom) {
		value = true;
	}

	@Override
	public void visit(final AxiomRoleReflexive axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomRoleIrreflexive axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomRoleSymmetric axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomRoleAsymmetric axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomORoleFunctional axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomDRoleFunctional axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomRoleInverseFunctional axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomConceptInclusion axiom) {
		if (v(axiom.getSubConcept())) {
			v(axiom.getSupConcept());
		}
	}

	@Override
	public void visit(final AxiomInstanceOf axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomRelatedTo axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomRelatedToNot axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomValueOf axiom) {
		value = false;
	}

	@Override
	public void visit(final AxiomValueOfNot axiom) {
		value = false;
	}

	@Override
	public void visitOntology(final Ontology ontology) {
		value = true;
		for (Axiom p : ontology.getAxioms()) {
			if (p.isUsed()) {
				p.accept(this);
			}
		}
	}

	public ELFAxiomChecker() {
		value = true;
	}

	public boolean value() {
		return value;
	}
}

/// pattern for the rule. Contains apply() method with updates of the monitored set
class TELFRule {
	/// reasoner that is used to add actions. The number of rules = the number of axioms, so the price is not too bad memory-wise.
	ELFReasoner ER;

	/// init c'tor
	TELFRule(final ELFReasoner er) {
		ER = er;
	}

	/// apply rule with fresh class C added to watching part
	void apply(final TELFConcept addedC) {}

	/// apply rule with fresh pair (C,D) added to watching part
	void apply(final TELFConcept addedC, final TELFConcept addedD) {}
}

//-------------------------------------------------------------
// Concepts and roles, i.e. S(C) and R(C,D)
//-------------------------------------------------------------
/// aux class to support set of rules and rule applications
class TRuleSet {
	/// set of rules to apply on change
	List<TELFRule> Rules = new ArrayList<TELFRule>();

	/// apply all rules with a single argument
	void applyRules(final TELFConcept addedC) {
		for (TELFRule p : Rules) {
			p.apply(addedC);
		}
	}

	/// apply all rules with two arguments
	void applyRules(final TELFConcept addedC, final TELFConcept addedD) {
		for (TELFRule p : Rules) {
			p.apply(addedC, addedD);
		}
	}

	/// add rule to a set
	void addRule(final TELFRule rule) {
		Rules.add(rule);
	}
}

/// concept, set S(C) and aux things
class TELFConcept extends TRuleSet {
	/// original concept (if any)
	ConceptExpression Origin;
	/// set of supers (built during classification)
	Set<TELFConcept> Supers = new HashSet<TELFConcept>();

	/// add C to supers
	void addSuper(final TELFConcept C) {
		Supers.add(C);
	}

	/// empty c'tor
	TELFConcept() {
		Origin = null;
	}

	/// init c'tor
	TELFConcept(final ConceptExpression origin) {
		Origin = origin;
	}

	/// check whether concept C is contained in supers
	boolean hasSuper(final TELFConcept C) {
		return Supers.contains(C);
	}

	/// add an super concept
	void addC(final TELFConcept C) {
		if (hasSuper(C)) {
			return;
		}
		addSuper(C);
		this.applyRules(C);
	}
}

/// role, set R(C,D)
class TELFRole extends TRuleSet {
	/// original role (if any)
	ObjectRoleExpression Origin;
	/// map itself
	Map<TELFConcept, Set<TELFConcept>> PredMap = new HashMap<TELFConcept, Set<TELFConcept>>();

	/// add (C,D) to label
	void addLabel(final TELFConcept C, final TELFConcept D) {
		PredMap.get(D).add(C);
	}

	/// empty c'tor
	TELFRole() {
		Origin = null;
	}

	/// init c'tor
	TELFRole(final ObjectRoleExpression origin) {
		Origin = origin;
	}

	/// get the (possibly empty) set of predecessors of given D
	Set<TELFConcept> getPredSet(final TELFConcept D) {
		return PredMap.get(D);
	}

	Iterable<Map.Entry<TELFConcept, Set<TELFConcept>>> begin() {
		return PredMap.entrySet();
	}

	/// check whether (C,D) is in the R-set
	boolean hasLabel(final TELFConcept C, final TELFConcept D) {
		return PredMap.get(D).contains(C);
	}

	/// add pair (C,D) to a set
	void addR(final TELFConcept C, final TELFConcept D) {
		if (hasLabel(C, D)) {
			return;
		}
		addLabel(C, D);
		this.applyRules(C, D);
	}
}

//-------------------------------------------------------------
// Action class
//-------------------------------------------------------------
/// single algorithm action (application of a rule)
class ELFAction {
	/// role R corresponded to R(C,D)
	TELFRole R = null;
	/// concept C; to add
	TELFConcept C = null;
	/// concept D; to add
	TELFConcept D = null;

	/// init c'tor for C action
	ELFAction(final TELFConcept c, final TELFConcept d) {
		R = null;
		C = c;
		D = d;
	}

	/// init c'tor for R action
	ELFAction(final TELFRole r, final TELFConcept c, final TELFConcept d) {
		R = r;
		C = c;
		D = d;
	}

	/// action itself, depending on the R state
	void apply() {
		if (R != null) {
			R.addR(C, D);
		} else {
			C.addC(D);
		}
	}
}
