package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;
import java.util.List;

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
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;

/// syntactic locality checker for DL axioms
public class SyntacticLocalityChecker extends SigAccessor implements DLAxiomVisitor,
		LocalityChecker {
	/// top evaluator
	TopEquivalenceEvaluator TopEval;
	/// bottom evaluator
	BotEquivalenceEvaluator BotEval;
	/// remember the axiom locality value here
	boolean isLocal;

	/// @return true iff EXPR is top equivalent
	boolean isTopEquivalent(final Expression expr) {
		return TopEval.isTopEquivalent(expr);
	}

	/// @return true iff EXPR is bottom equivalent
	boolean isBotEquivalent(final Expression expr) {
		return BotEval.isBotEquivalent(expr);
	}

	/// @return true iff role expression in equivalent to const wrt locality
	boolean isREquivalent(final Expression expr) {
		return sig.topRLocal() ? isTopEquivalent(expr) : isBotEquivalent(expr);
	}

	/// init c'tor
	public SyntacticLocalityChecker(final TSignature s) {
		super(s);
		TopEval = new TopEquivalenceEvaluator(s);
		BotEval = new BotEquivalenceEvaluator(s);
		TopEval.setBotEval(BotEval);
		BotEval.setTopEval(TopEval);
	}

	public TSignature getSignature() {
		return sig;
	}

	/// set a new value of a signature (without changing a locality parameters)
	public void setSignatureValue(final TSignature Sig) {
		sig = Sig;
	}

	// set fields
	/// @return true iff an AXIOM is local wrt defined policy
	public boolean local(final Axiom axiom) {
		axiom.accept(this);
		return isLocal;
	}

	/// load ontology to a given KB
	public void visitOntology(final Ontology ontology) {
		for (Axiom p : ontology.getAxioms()) {
			if (p.isUsed()) {
				p.accept(this);
			}
		}
	}

	public void visit(final AxiomDeclaration axiom) {
		isLocal = true;
	}

	public void visit(final AxiomEquivalentConcepts axiom) {
		// 1 element => local
		if (axiom.size() == 1) {
			isLocal = true;
			return;
		}
		// axiom is local iff all the classes are either top- or bot-local
		isLocal = false;
		List<ConceptExpression> args = axiom.getArguments();
		if (args.size() > 0) {
			if (isBotEquivalent(args.get(0))) {
				for (int i = 1; i < args.size(); i++) {
					if (!isBotEquivalent(args.get(i))) {
						return;
					}
				}
			} else {
				if (!isTopEquivalent(args.get(0))) {
					return;
				}
				for (int i = 1; i < args.size(); i++) {
					if (!isTopEquivalent(args.get(i))) {
						return;
					}
				}
			}
		}
		isLocal = true;
	}

	public void visit(final AxiomDisjointConcepts axiom) {
		// local iff at most 1 concept is not bot-equiv
		boolean hasNBE = false;
		isLocal = true;
		for (ConceptExpression p : axiom.getArguments()) {
			if (!isBotEquivalent(p)) {
				if (hasNBE) {
					isLocal = false;
					break;
				} else {
					hasNBE = true;
				}
			}
		}
	}

	public void visit(final AxiomDisjointUnion axiom) {
		isLocal = false;
		boolean topLoc = sig.topCLocal();
		if (!(topLoc ? isTopEquivalent(axiom.getC()) : isBotEquivalent(axiom.getC()))) {
			return;
		}
		boolean topEqDesc = false;
		for (ConceptExpression p : axiom.getArguments()) {
			if (!isBotEquivalent(p)) {
				if (!topLoc) {
					return; // non-local straight away
				}
				if (isTopEquivalent(p)) {
					if (topEqDesc) {
						return; // 2nd top in there -- non-local
					} else {
						topEqDesc = true;
					}
				} else {
					return; // non-local
				}
			}
		}
		isLocal = true;
	}

	public void visit(final AxiomEquivalentORoles axiom) {
		isLocal = true;
		if (axiom.size() <= 1) {
			return;
		}
		for (ObjectRoleExpression p : axiom.getArguments()) {
			if (!isREquivalent(p)) {
				isLocal = false;
				break;
			}
		}
	}

	public void visit(final AxiomEquivalentDRoles axiom) {
		isLocal = true;
		if (axiom.size() <= 1) {
			return;
		}
		for (DataRoleExpression p : axiom.getArguments()) {
			if (!isREquivalent(p)) {
				isLocal = false;
				break;
			}
		}
	}

	public void visit(final AxiomDisjointORoles axiom) {
		isLocal = false;
		if (sig.topRLocal()) {
			return;
		}
		boolean hasNBE = false;
		for (ObjectRoleExpression p : axiom.getArguments()) {
			if (!isREquivalent(p)) {
				if (hasNBE) {
					return; // false here
				} else {
					hasNBE = true;
				}
			}
		}
		isLocal = true;
	}

	public void visit(final AxiomDisjointDRoles axiom) {
		isLocal = false;
		if (sig.topRLocal()) {
			return;
		}
		boolean hasNBE = false;
		for (DataRoleExpression p : axiom.getArguments()) {
			if (!isREquivalent(p)) {
				if (hasNBE) {
					return; // false here
				} else {
					hasNBE = true;
				}
			}
		}
		isLocal = true;
	}

	public void visit(final AxiomSameIndividuals axiom) {
		isLocal = true;
	}

	public void visit(final AxiomDifferentIndividuals axiom) {
		isLocal = true;
	}

	/// there is no such axiom in OWL API, but I hope nobody would use Fairness here
	public void visit(final AxiomFairnessConstraint axiom) {
		isLocal = true;
	}

	public void visit(final AxiomRoleInverse axiom) {
		isLocal = isREquivalent(axiom.getRole()) && isREquivalent(axiom.getInvRole());
	}

	public void visit(final AxiomORoleSubsumption axiom) {
		isLocal = isREquivalent(sig.topRLocal() ? axiom.getRole() : axiom.getSubRole());
	}

	public void visit(final AxiomDRoleSubsumption axiom) {
		isLocal = isREquivalent(sig.topRLocal() ? axiom.getRole() : axiom.getSubRole());
	}

	public void visit(final AxiomORoleDomain axiom) {
		isLocal = isTopEquivalent(axiom.getDomain());
		if (!sig.topRLocal()) {
			isLocal |= isBotEquivalent(axiom.getRole());
		}
	}

	public void visit(final AxiomDRoleDomain axiom) {
		isLocal = isTopEquivalent(axiom.getDomain());
		if (!sig.topRLocal()) {
			isLocal |= isBotEquivalent(axiom.getRole());
		}
	}

	public void visit(final AxiomORoleRange axiom) {
		isLocal = isTopEquivalent(axiom.getRange());
		if (!sig.topRLocal()) {
			isLocal |= isBotEquivalent(axiom.getRole());
		}
	}

	public void visit(final AxiomDRoleRange axiom) {
		isLocal = isTopDT(axiom.getRange());
		if (!sig.topRLocal()) {
			isLocal |= isBotEquivalent(axiom.getRole());
		}
	}

	public void visit(final AxiomRoleTransitive axiom) {
		isLocal = isREquivalent(axiom.getRole());
	}

	/// as BotRole is irreflexive, the only local axiom is topEquivalent(R)
	public void visit(final AxiomRoleReflexive axiom) {
		isLocal = isTopEquivalent(axiom.getRole());
	}

	public void visit(final AxiomRoleIrreflexive axiom) {
		isLocal = !sig.topRLocal();
	}

	public void visit(final AxiomRoleSymmetric axiom) {
		isLocal = isREquivalent(axiom.getRole());
	}

	public void visit(final AxiomRoleAsymmetric axiom) {
		isLocal = !sig.topRLocal();
	}

	public void visit(final AxiomORoleFunctional axiom) {
		isLocal = !sig.topRLocal() && isBotEquivalent(axiom.getRole());
	}

	public void visit(final AxiomDRoleFunctional axiom) {
		isLocal = !sig.topRLocal() && isBotEquivalent(axiom.getRole());
	}

	public void visit(final AxiomRoleInverseFunctional axiom) {
		isLocal = !sig.topRLocal() && isBotEquivalent(axiom.getRole());
	}

	public void visit(final AxiomConceptInclusion axiom) {
		isLocal = isBotEquivalent(axiom.getSubConcept())
				|| isTopEquivalent(axiom.getSupConcept());
	}

	public void visit(final AxiomInstanceOf axiom) {
		isLocal = isTopEquivalent(axiom.getC());
	}

	public void visit(final AxiomRelatedTo axiom) {
		isLocal = sig.topRLocal() && isTopEquivalent(axiom.getRelation());
	}

	public void visit(final AxiomRelatedToNot axiom) {
		isLocal = !sig.topRLocal() && isBotEquivalent(axiom.getRelation());
	}

	public void visit(final AxiomValueOf axiom) {
		isLocal = sig.topRLocal() && isTopEquivalent(axiom.getAttribute());
	}

	public void visit(final AxiomValueOfNot axiom) {
		isLocal = !sig.topRLocal() && isBotEquivalent(axiom.getAttribute());
	}

	public void preprocessOntology(final Collection<Axiom> s) {
		sig = new TSignature();
		for (Axiom ax : s) {
			sig.add(ax.getSignature());
		}
	}
}
