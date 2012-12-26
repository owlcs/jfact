package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import conformance.PortedFrom;

/** equality atom x=y */
@PortedFrom(file = "QR.h", name = "QREqAtom")
class QREqAtom extends QR2ArgAtom {
    QREqAtom(QRiObject A1, QRiObject A2) {
        super(A1, A2);
    }
}