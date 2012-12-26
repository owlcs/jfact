package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import conformance.PortedFrom;

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