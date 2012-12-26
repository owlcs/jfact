package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import conformance.PortedFrom;

/** inequality atom x!=y */
@PortedFrom(file = "QR.h", name = "QRNeqAtom")
class QRNeqAtom extends QR2ArgAtom {
    QRNeqAtom(QRiObject A1, QRiObject A2) {
        super(A1, A2);
    }
}