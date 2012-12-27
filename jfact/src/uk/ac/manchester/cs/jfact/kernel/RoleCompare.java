package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;
import java.util.Comparator;

import conformance.PortedFrom;

@PortedFrom(file = "tRole.cpp", name = "TRoleCompare")
class RoleCompare implements Comparator<Role>, Serializable {
    @Override
    @PortedFrom(file = "tRole.cpp", name = "compare")
    public int compare(Role p, Role q) {
        int n = p.getId();
        int m = q.getId();
        if (n > 0 && m < 0) {
            return -1;
        }
        if (n < 0 && m > 0) {
            return 1;
        }
        return 0;
    }
}
