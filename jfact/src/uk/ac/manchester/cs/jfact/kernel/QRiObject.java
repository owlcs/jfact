package uk.ac.manchester.cs.jfact.kernel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;

//---------------------------------------------------------
// i-objects (vars and individuals)
//---------------------------------------------------------
/// i-object (from SWRL proposal), which is variable or an individual
class QRiObject {}

/// QR variable replacing the individual
class QRVariable extends QRiObject {
	/// name of a var
	String Name;

	/// empty c'tor
	QRVariable() {}

	/// init c'tor
	QRVariable(final String name) {
		this.Name = name;
	}

	String getName() {
		return this.Name;
	}
}

/// individual in a query
class QRIndividual extends QRiObject {
	/// original individual from Expression Manager
	IndividualName Ind;

	/// init c'tor
	QRIndividual(final IndividualName ind) {
		this.Ind = ind;
	}

	/// get the name
	IndividualName getIndividual() {
		return this.Ind;
	}
}

//---------------------------------------------------------
// var factory
//---------------------------------------------------------
class VariableFactory {
	List<QRVariable> Base = new ArrayList<QRVariable>();

	/// get fresh variable
	QRVariable getNewVar() {
		QRVariable ret = new QRVariable();
		this.Base.add(ret);
		return ret;
	}
}

//---------------------------------------------------------
// atoms in the query
//---------------------------------------------------------
/// general atom interface
class QRAtom {}

/// concept atom: C(x)
class QRConceptAtom extends QRAtom {
	/// pointer to a concept (named one atm)
	ConceptExpression Concept;
	/// argument
	QRiObject Arg;

	/// init c'tor
	QRConceptAtom(final ConceptExpression C, final QRiObject A) {
		this.Concept = C;
		this.Arg = A;
	}

	// access
	/// get concept expression
	ConceptExpression getConcept() {
		return this.Concept;
	}

	/// get i-object
	QRiObject getArg() {
		return this.Arg;
	}
}

/// interface for general 2-arg atom
class QR2ArgAtom extends QRAtom {
	/// argument 1
	QRiObject Arg1;
	/// argument 2
	QRiObject Arg2;

	QR2ArgAtom(final QRiObject A1, final QRiObject A2) {
		this.Arg1 = A1;
		this.Arg2 = A2;
	}

	// access
	/// get first i-object
	QRiObject getArg1() {
		return this.Arg1;
	}

	/// get second i-object
	QRiObject getArg2() {
		return this.Arg2;
	}
}

/// role atom R(x,y)
class QRRoleAtom extends QR2ArgAtom {
	/// role between two i-objects
	ObjectRoleExpression Role;

	QRRoleAtom(final ObjectRoleExpression R, final QRiObject A1, final QRiObject A2) {
		super(A1, A2);
		this.Role = R;
	}

	// access
	/// get role expression
	ObjectRoleExpression getRole() {
		return this.Role;
	}
}

/// equality atom x=y
class QREqAtom extends QR2ArgAtom {
	QREqAtom(final QRiObject A1, final QRiObject A2) {
		super(A1, A2);
	}
}

/// inequality atom x!=y
class QRNeqAtom extends QR2ArgAtom {
	QRNeqAtom(final QRiObject A1, final QRiObject A2) {
		super(A1, A2);
	}
}

/// general QR conjunctions of atoms
class QRSetAtoms {
	List<QRAtom> Base = new ArrayList<QRAtom>();

	/// add atom to a set
	void addAtom(final QRAtom atom) {
		this.Base.add(atom);
	}
}

/// class for the queries
class QRQuery {
	/// query as a set of atoms
	QRSetAtoms Body = new QRSetAtoms();
	/// set of free variables
	Set<QRVariable> FreeVars = new HashSet<QRVariable>();

	/// add atom to a query body
	void addAtom(final QRAtom atom) {
		this.Body.addAtom(atom);
	}

	/// mark a variable as a free one
	void setVarFree(final QRVariable var) {
		this.FreeVars.add(var);
	}
}

/// rule in a general form body -> head
class QRRule {}
