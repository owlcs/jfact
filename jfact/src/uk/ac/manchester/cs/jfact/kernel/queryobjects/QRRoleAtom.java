package uk.ac.manchester.cs.jfact.kernel.queryobjects;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import conformance.PortedFrom;

/** role atom R(x,y) */
@PortedFrom(file = "QR.h", name = "QRRoleAtom")
class QRRoleAtom extends QR2ArgAtom {
    /** role between two i-objects */
    ObjectRoleExpression Role;

    QRRoleAtom(ObjectRoleExpression R, QRiObject A1, QRiObject A2) {
        super(A1, A2);
        Role = R;
    }

    // access
    /** get role expression */
    ObjectRoleExpression getRole() {
        return Role;
    }
}