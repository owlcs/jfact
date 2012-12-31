package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import java.util.ArrayList;
import java.util.List;

import conformance.PortedFrom;

/** general QR conjunctions of atoms */
@PortedFrom(file = "QR.h", name = "QRSetAtoms")
class QRSetAtoms {
    @PortedFrom(file = "QR.h", name = "Base")
    List<QRAtom> Base = new ArrayList<QRAtom>();

    /** add atom to a set */
    @PortedFrom(file = "QR.h", name = "addAtom")
    void addAtom(QRAtom atom) {
        Base.add(atom);
    }
}
