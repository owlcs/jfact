package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import conformance.PortedFrom;

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