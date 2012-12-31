package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import java.util.HashSet;
import java.util.Set;

import conformance.PortedFrom;

/** class for the queries */
@PortedFrom(file = "QR.h", name = "QRQuery")
class QRQuery {
    /** query as a set of atoms */
    @PortedFrom(file = "QR.h", name = "Body")
    QRSetAtoms Body = new QRSetAtoms();
    /** set of free variables */
    @PortedFrom(file = "QR.h", name = "FreeVars")
    Set<QRVariable> FreeVars = new HashSet<QRVariable>();

    /** add atom to a query body */
    @PortedFrom(file = "QR.h", name = "addAtom")
    void addAtom(QRAtom atom) {
        Body.addAtom(atom);
    }

    /** mark a variable as a free one */
    @PortedFrom(file = "QR.h", name = "setVarFree")
    void setVarFree(QRVariable var) {
        FreeVars.add(var);
    }
}
