package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import conformance.PortedFrom;

/** QR variable replacing the individual */
@PortedFrom(file = "QR.h", name = "QRVariable")
class QRVariable extends QRiObject {
    /** name of a var */
    @PortedFrom(file = "QR.h", name = "Name")
    String Name;

    /** empty c'tor */
    QRVariable() {}

    /** init c'tor */
    QRVariable(String name) {
        Name = name;
    }

    @PortedFrom(file = "QR.h", name = "getName")
    String getName() {
        return Name;
    }
}
