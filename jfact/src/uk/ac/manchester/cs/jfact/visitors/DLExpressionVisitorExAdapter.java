package uk.ac.manchester.cs.jfact.visitors;

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

@SuppressWarnings("unused")
public abstract class DLExpressionVisitorExAdapter<A> implements DLExpressionVisitorEx<A> {
	public A visit(final ConceptTop expr) {
		return null;
	}

	public A visit(final ConceptBottom expr) {
		return null;
	}

	public A visit(final ConceptName expr) {
		return null;
	}

	public A visit(final ConceptNot expr) {
		return null;
	}

	public A visit(final ConceptAnd expr) {
		return null;
	}

	public A visit(final ConceptOr expr) {
		return null;
	}

	public A visit(final ConceptOneOf expr) {
		return null;
	}

	public A visit(final ConceptObjectSelf expr) {
		return null;
	}

	public A visit(final ConceptObjectValue expr) {
		return null;
	}

	public A visit(final ConceptObjectExists expr) {
		return null;
	}

	public A visit(final ConceptObjectForall expr) {
		return null;
	}

	public A visit(final ConceptObjectMinCardinality expr) {
		return null;
	}

	public A visit(final ConceptObjectMaxCardinality expr) {
		return null;
	}

	public A visit(final ConceptObjectExactCardinality expr) {
		return null;
	}

	public A visit(final ConceptDataValue expr) {
		return null;
	}

	public A visit(final ConceptDataExists expr) {
		return null;
	}

	public A visit(final ConceptDataForall expr) {
		return null;
	}

	public A visit(final ConceptDataMinCardinality expr) {
		return null;
	}

	public A visit(final ConceptDataMaxCardinality expr) {
		return null;
	}

	public A visit(final ConceptDataExactCardinality expr) {
		return null;
	}

	public A visit(final IndividualName expr) {
		return null;
	}

	public A visit(final ObjectRoleTop expr) {
		return null;
	}

	public A visit(final ObjectRoleBottom expr) {
		return null;
	}

	public A visit(final ObjectRoleName expr) {
		return null;
	}

	public A visit(final ObjectRoleInverse expr) {
		return null;
	}

	public A visit(final ObjectRoleChain expr) {
		return null;
	}

	public A visit(final ObjectRoleProjectionFrom expr) {
		return null;
	}

	public A visit(final ObjectRoleProjectionInto expr) {
		return null;
	}

	public A visit(final DataRoleTop expr) {
		return null;
	}

	public A visit(final DataRoleBottom expr) {
		return null;
	}

	public A visit(final DataRoleName expr) {
		return null;
	}

	public A visit(final DataTop expr) {
		return null;
	}

	public A visit(final DataBottom expr) {
		return null;
	}

	public A visit(final Datatype<?> expr) {
		return null;
	}

	public A visit(final Literal<?> expr) {
		return null;
	}

	public A visit(final DataNot expr) {
		return null;
	}

	public A visit(final DataAnd expr) {
		return null;
	}

	public A visit(final DataOr expr) {
		return null;
	}

	public A visit(final DataOneOf expr) {
		return null;
	}
}
