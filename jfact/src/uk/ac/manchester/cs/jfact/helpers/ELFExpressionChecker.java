package uk.ac.manchester.cs.jfact.helpers;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
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
import uk.ac.manchester.cs.jfact.kernel.dl.DataAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.DataBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.DataNot;
import uk.ac.manchester.cs.jfact.kernel.dl.DataOneOf;
import uk.ac.manchester.cs.jfact.kernel.dl.DataOr;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.DataTop;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionInto;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;

class ELFExpressionChecker implements DLExpressionVisitor {
	boolean value;

	/// get DLTree corresponding to an expression EXPR
	boolean v(final Expression expr) {
		expr.accept(this);
		return value;
	}

	// concept expressions
	public void visit(final ConceptTop expr) {
		value = true;
	}

	public void visit(final ConceptBottom expr) {
		value = true;
	}

	public void visit(final ConceptName expr) {
		value = true;
	}

	public void visit(final ConceptNot expr) {
		value = false;
	}

	public void visit(final ConceptAnd expr) {
		value = false;
		for (Expression p : expr.getArguments()) {
			if (!v(p)) {
				return;
			}
		}
		value = true;
	}

	public void visit(final ConceptOr expr) {
		value = false;
	}

	public void visit(final ConceptOneOf expr) {
		value = false;
	}

	public void visit(final ConceptObjectSelf expr) {
		value = false;
	}

	public void visit(final ConceptObjectValue expr) {
		value = false;
	}

	public void visit(final ConceptObjectExists expr) {
		value = false;
		// check role
		if (!v(expr.getOR())) {
			return;
		}
		// check concept
		v(expr.getConcept());
	}

	public void visit(final ConceptObjectForall expr) {
		value = false;
	}

	public void visit(final ConceptObjectMinCardinality expr) {
		value = false;
	}

	public void visit(final ConceptObjectMaxCardinality expr) {
		value = false;
	}

	public void visit(final ConceptObjectExactCardinality expr) {
		value = false;
	}

	public void visit(final ConceptDataValue expr) {
		value = false;
	}

	public void visit(final ConceptDataExists expr) {
		value = false;
	}

	public void visit(final ConceptDataForall expr) {
		value = false;
	}

	public void visit(final ConceptDataMinCardinality expr) {
		value = false;
	}

	public void visit(final ConceptDataMaxCardinality expr) {
		value = false;
	}

	public void visit(final ConceptDataExactCardinality expr) {
		value = false;
	}

	// individual expressions
	public void visit(final IndividualName expr) {
		value = false;
	}

	// object role expressions
	public void visit(final ObjectRoleTop expr) {
		value = false;
	}

	public void visit(final ObjectRoleBottom expr) {
		value = false;
	}

	public void visit(final ObjectRoleName expr) {
		value = true;
	}

	public void visit(final ObjectRoleInverse expr) {
		value = false;
	}

	public void visit(final ObjectRoleChain expr) {
		value = false;
		for (Expression p : expr.getArguments()) {
			if (!v(p)) {
				return;
			}
		}
		value = true;
	}

	public void visit(final ObjectRoleProjectionFrom expr) {
		value = false;
	}

	public void visit(final ObjectRoleProjectionInto expr) {
		value = false;
	}

	// data role expressions
	public void visit(final DataRoleTop expr) {
		value = false;
	}

	public void visit(final DataRoleBottom expr) {
		value = false;
	}

	public void visit(final DataRoleName expr) {
		value = false;
	}

	// data expressions
	public void visit(final DataTop expr) {
		value = false;
	}

	public void visit(final DataBottom expr) {
		value = false;
	}

	public void visit(final DataNot expr) {
		value = false;
	}

	public void visit(final DataAnd expr) {
		value = false;
	}

	public void visit(final DataOr expr) {
		value = false;
	}

	public void visit(final DataOneOf expr) {
		value = false;
	}

	public void visit(final Literal<?> expr) {
		value = false;
	}

	public void visit(final Datatype<?> expr) {
		value = false;
	}
}
