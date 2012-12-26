package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import java.util.ArrayList;
import java.util.List;

import conformance.PortedFrom;

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