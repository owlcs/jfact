package uk.ac.manchester.cs.jfact.kernel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import conformance.PortedFrom;

//---------------------------------------------------------
// i-objects (vars and individuals)
//---------------------------------------------------------
/** i-object (from SWRL proposal), which is variable or an individual */
@PortedFrom(file = "QR.h", name = "QRiObject")
class QRiObject {}

/** QR variable replacing the individual */
@PortedFrom(file = "QR.h", name = "QRVariable")
class QRVariable extends QRiObject {
    /** name of a var */
    String Name;

    /** empty c'tor */
    QRVariable() {}

    /** init c'tor */
    QRVariable(String name) {
        Name = name;
    }

    String getName() {
        return Name;
    }
}

/** individual in a query */
@PortedFrom(file = "QR.h", name = "QRIndividual")
class QRIndividual extends QRiObject {
    /** original individual from Expression Manager */
    IndividualName Ind;

    /** init c'tor */
    QRIndividual(IndividualName ind) {
        Ind = ind;
    }

    /** get the name */
    IndividualName getIndividual() {
        return Ind;
    }
}

// ---------------------------------------------------------
// var factory
// ---------------------------------------------------------
@PortedFrom(file = "QR.h", name = "VariableFactory")
class VariableFactory {
    List<QRVariable> Base = new ArrayList<QRVariable>();

    /** get fresh variable */
    QRVariable getNewVar() {
        QRVariable ret = new QRVariable();
        Base.add(ret);
        return ret;
    }
}

// ---------------------------------------------------------
// atoms in the query
// ---------------------------------------------------------
/** general atom interface */
@PortedFrom(file = "QR.h", name = "QRAtom")
class QRAtom {}

/** concept atom: C(x) */
@PortedFrom(file = "QR.h", name = "QRConceptAtom")
class QRConceptAtom extends QRAtom {
    /** pointer to a concept (named one atm) */
    ConceptExpression Concept;
    /** argument */
    QRiObject Arg;

    /** init c'tor */
    QRConceptAtom(ConceptExpression C, QRiObject A) {
        Concept = C;
        Arg = A;
    }

    // access
    /** get concept expression */
    ConceptExpression getConcept() {
        return Concept;
    }

    /** get i-object */
    QRiObject getArg() {
        return Arg;
    }
}

/** interface for general 2-arg atom */
@PortedFrom(file = "QR.h", name = "QR2ArgAtom")
class QR2ArgAtom extends QRAtom {
    /** argument 1 */
    QRiObject Arg1;
    /** argument 2 */
    QRiObject Arg2;

    QR2ArgAtom(QRiObject A1, QRiObject A2) {
        Arg1 = A1;
        Arg2 = A2;
    }

    // access
    /** get first i-object */
    QRiObject getArg1() {
        return Arg1;
    }

    /** get second i-object */
    QRiObject getArg2() {
        return Arg2;
    }
}

/** role atom R(x,y) */
@PortedFrom(file = "QR.h", name = "QRRoleAtom")
class QRRoleAtom extends QR2ArgAtom {
    /** role between two i-objects */
    ObjectRoleExpression Role;

    QRRoleAtom(ObjectRoleExpression R, QRiObject A1, QRiObject A2) {
        super(A1, A2);
        Role = R;
    }

    // access
    /** get role expression */
    ObjectRoleExpression getRole() {
        return Role;
    }
}

/** equality atom x=y */
@PortedFrom(file = "QR.h", name = "QREqAtom")
class QREqAtom extends QR2ArgAtom {
    QREqAtom(QRiObject A1, QRiObject A2) {
        super(A1, A2);
    }
}

/** inequality atom x!=y */
@PortedFrom(file = "QR.h", name = "QRNeqAtom")
class QRNeqAtom extends QR2ArgAtom {
    QRNeqAtom(QRiObject A1, QRiObject A2) {
        super(A1, A2);
    }
}

/** general QR conjunctions of atoms */
@PortedFrom(file = "QR.h", name = "QRSetAtoms")
class QRSetAtoms {
    List<QRAtom> Base = new ArrayList<QRAtom>();

    /** add atom to a set */
    void addAtom(QRAtom atom) {
        Base.add(atom);
    }
}

/** class for the queries */
@PortedFrom(file = "QR.h", name = "QRQuery")
class QRQuery {
    /** query as a set of atoms */
    QRSetAtoms Body = new QRSetAtoms();
    /** set of free variables */
    Set<QRVariable> FreeVars = new HashSet<QRVariable>();

    /** add atom to a query body */
    void addAtom(QRAtom atom) {
        Body.addAtom(atom);
    }

    /** mark a variable as a free one */
    void setVarFree(QRVariable var) {
        FreeVars.add(var);
    }
}

/** rule in a general form body -> head */
@PortedFrom(file = "QR.h", name = "QRRule")
class QRRule {}
