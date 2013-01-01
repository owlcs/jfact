package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import java.util.ArrayList;
import java.util.List;

import conformance.PortedFrom;

// var factory
@PortedFrom(file = "QR.h", name = "VariableFactory")
class VariableFactory {
    @PortedFrom(file = "QR.h", name = "Base")
    List<QRVariable> Base = new ArrayList<QRVariable>();

    /** get fresh variable */
    @PortedFrom(file = "QR.h", name = "getNewVar")
    QRVariable getNewVar() {
        QRVariable ret = new QRVariable();
        Base.add(ret);
        return ret;
    }
}
