package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import conformance.PortedFrom;

/** concept atom: C(x) */
@PortedFrom(file = "QR.h", name = "QRConceptAtom")
class QRConceptAtom extends QRAtom {
    /** pointer to a concept (named one atm) */
    @PortedFrom(file = "QR.h", name = "Concept")
    ConceptExpression Concept;
    /** argument */
    @PortedFrom(file = "QR.h", name = "Arg")
    QRiObject Arg;

    /** init c'tor */
    QRConceptAtom(ConceptExpression C, QRiObject A) {
        Concept = C;
        Arg = A;
    }

    // access
    /** get concept expression */
    @PortedFrom(file = "QR.h", name = "getConcept")
    ConceptExpression getConcept() {
        return Concept;
    }

    /** get i-object */
    @PortedFrom(file = "QR.h", name = "getArg")
    QRiObject getArg() {
        return Arg;
    }
}
