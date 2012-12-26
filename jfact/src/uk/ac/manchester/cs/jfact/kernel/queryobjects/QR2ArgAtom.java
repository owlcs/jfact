package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import conformance.PortedFrom;

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