package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;
import java.util.Comparator;

import conformance.PortedFrom;

@PortedFrom(file = "Tactic.cpp", name = "EdgeCompare")
class EdgeCompare implements Comparator<DlCompletionTreeArc>, Serializable {
    @Override
@PortedFrom(file="Tactic.cpp",name="compare")
    public int compare(DlCompletionTreeArc o1, DlCompletionTreeArc o2) {
        return o1.getArcEnd().compareTo(o2.getArcEnd());
    }
}
