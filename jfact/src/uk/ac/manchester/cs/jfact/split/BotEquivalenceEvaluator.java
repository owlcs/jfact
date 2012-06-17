package uk.ac.manchester.cs.jfact.split;

import uk.ac.manchester.cs.jfact.kernel.dl.ConceptAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataExactCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataForall;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataMaxCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataMinCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataValue;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptNot;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExactCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectForall;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectMaxCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectMinCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectSelf;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectValue;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptOneOf;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptOr;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptTop;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionInto;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;

/// check whether class expressions are equivalent to bottom wrt given locality class
@SuppressWarnings("unused")
//XXX verify unused parameters
public class BotEquivalenceEvaluator extends SigAccessor implements DLExpressionVisitor {
	/// corresponding top evaluator
	TopEquivalenceEvaluator TopEval = null;
	/// keep the value here
	boolean isBotEq = false;

	/// check whether the expression is top-equivalent
	boolean isTopEquivalent(final Expression expr) {
		return TopEval.isTopEquivalent(expr);
	}

	/// @return true iff role expression in equivalent to const wrt locality
	boolean isREquivalent(final Expression expr) {
		return sig.topRLocal() ? isTopEquivalent(expr) : isBotEquivalent(expr);
	}

	/// init c'tor
	BotEquivalenceEvaluator(final TSignature s) {
		super(s);
	}

	// set fields
	/// set the corresponding top evaluator
	void setTopEval(final TopEquivalenceEvaluator eval) {
		TopEval = eval;
	}

	/// @return true iff an EXPRession is equivalent to bottom wrt defined policy
	boolean isBotEquivalent(final Expression expr) {
		expr.accept(this);
		return isBotEq;
	}

	// concept expressions
	@Override
	public void visit(final ConceptTop expr) {
		isBotEq = false;
	}

	@Override
	public void visit(final ConceptBottom expr) {
		isBotEq = true;
	}

	// equivalent to R(x,y) and C(x), so copy behaviour from ER.X
	@Override
	public void visit(final ObjectRoleProjectionFrom expr) {
		isBotEq = isBotEquivalent(expr.getConcept());
		if (!topRLocal()) {
			isBotEq |= isBotEquivalent(expr.getOR());
		}
	}

	// equivalent to R(x,y) and C(y), so copy behaviour from ER.X
	@Override
	public void visit(final ObjectRoleProjectionInto expr) {
		isBotEq = isBotEquivalent(expr.getConcept());
		if (!topRLocal()) {
			isBotEq |= isBotEquivalent(expr.getOR());
		}
	}

	@Override
	public void visit(final ConceptName expr) {
		isBotEq = !sig.topCLocal() && !sig.contains(expr);
	}

	@Override
	public void visit(final ConceptNot expr) {
		isBotEq = isTopEquivalent(expr.getConcept());
	}

	@Override
	public void visit(final ConceptAnd expr) {
		for (ConceptExpression p : expr.getArguments()) {
			if (isBotEquivalent(p)) {
				return;
			}
		}
		isBotEq = false;
	}

	@Override
	public void visit(final ConceptOr expr) {
		for (ConceptExpression p : expr.getArguments()) {
			if (!isBotEquivalent(p)) {
				return;
			}
		}
		isBotEq = true;
	}

	@Override
	public void visit(final ConceptOneOf expr) {
		isBotEq = expr.isEmpty();
	}

	@Override
	public void visit(final ConceptObjectSelf expr) {
		isBotEq = !sig.topRLocal() && isBotEquivalent(expr.getOR());
	}

	@Override
	public void visit(final ConceptObjectValue expr) {
		isBotEq = !sig.topRLocal() && isBotEquivalent(expr.getOR());
	}

	@Override
	public void visit(final ConceptObjectExists expr) {
		isBotEq = isBotEquivalent(expr.getConcept());
		if (!sig.topRLocal()) {
			isBotEq |= isBotEquivalent(expr.getOR());
		}
	}

	@Override
	public void visit(final ConceptObjectForall expr) {
		isBotEq = sig.topRLocal() && isTopEquivalent(expr.getOR())
				&& isBotEquivalent(expr.getConcept());
	}

	@Override
	public void visit(final ConceptObjectMinCardinality expr) {
		isBotEq = expr.getCardinality() > 0
				&& (isBotEquivalent(expr.getConcept()) || !sig.topRLocal()
						&& isBotEquivalent(expr.getOR()));
	}

	@Override
	public void visit(final ConceptObjectMaxCardinality expr) {
		isBotEq = sig.topRLocal() && expr.getCardinality() > 0
				&& isTopEquivalent(expr.getOR()) && isTopEquivalent(expr.getConcept());
	}

	@Override
	public void visit(final ConceptObjectExactCardinality expr) {
		isBotEq = expr.getCardinality() > 0
				&& (isBotEquivalent(expr.getConcept()) || isREquivalent(expr.getOR())
						&& (sig.topRLocal() ? isTopEquivalent(expr.getConcept()) : true));
	}

	@Override
	public void visit(final ConceptDataValue expr) {
		isBotEq = !sig.topRLocal() && isBotEquivalent(expr.getDataRoleExpression());
	}

	@Override
	public void visit(final ConceptDataExists expr) {
		isBotEq = !sig.topRLocal() && isBotEquivalent(expr.getDataRoleExpression());
	}

	@Override
	public void visit(final ConceptDataForall expr) {
		isBotEq = sig.topRLocal() && isTopEquivalent(expr.getDataRoleExpression())
				&& !isTopDT(expr.getExpr());
	}

	@Override
	public void visit(final ConceptDataMinCardinality expr) {
		isBotEq = !sig.topRLocal() && expr.getCardinality() > 0
				&& isBotEquivalent(expr.getDataRoleExpression());
	}

	@Override
	public void visit(final ConceptDataMaxCardinality expr) {
		isBotEq = sig.topRLocal()
				&& isTopEquivalent(expr.getDataRoleExpression())
				&& (expr.getCardinality() <= 1 ? isTopOrBuiltInDataType(expr.getExpr())
						: isTopOrBuiltInDataType(expr.getExpr()));
	}

	@Override
	public void visit(final ConceptDataExactCardinality expr) {
		isBotEq = isREquivalent(expr.getDataRoleExpression())
				&& (sig.topRLocal() ? expr.getCardinality() == 0 ? isTopOrBuiltInDataType(expr
						.getExpr()) : isTopOrBuiltInDataType(expr.getExpr())
						: expr.getCardinality() > 0);
	}

	// object role expressions
	@Override
	public void visit(final ObjectRoleTop expr) {
		isBotEq = false;
	}

	@Override
	public void visit(final ObjectRoleBottom expr) {
		isBotEq = true;
	}

	@Override
	public void visit(final ObjectRoleName expr) {
		isBotEq = !sig.topRLocal() && !sig.contains(expr);
	}

	@Override
	public void visit(final ObjectRoleInverse expr) {
		isBotEq = isBotEquivalent(expr.getOR());
	}

	@Override
	public void visit(final ObjectRoleChain expr) {
		for (ObjectRoleExpression p : expr.getArguments()) {
			if (isBotEquivalent(p)) {
				return;
			}
		}
		isBotEq = false;
	}

	// data role expressions
	@Override
	public void visit(final DataRoleTop expr) {
		isBotEq = false;
	}

	@Override
	public void visit(final DataRoleBottom expr) {
		isBotEq = true;
	}

	@Override
	public void visit(final DataRoleName expr) {
		isBotEq = !sig.topRLocal() && !sig.contains(expr);
	}
}
