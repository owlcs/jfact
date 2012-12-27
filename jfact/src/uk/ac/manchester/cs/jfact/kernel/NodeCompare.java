package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;
import java.util.Comparator;

import conformance.PortedFrom;

@PortedFrom(file = "Tactic.cpp", name = "NodeCompare")
class NodeCompare implements Comparator<DlCompletionTree>, Serializable {
    @Override
@PortedFrom(file="Tactic.cpp",name="compare")
    public int compare(DlCompletionTree o1, DlCompletionTree o2) {
        // TODO Auto-generated method stub
        return 0;
        // XXX implement this properly
        // bool operator() ( DlCompletionTree* p, DlCompletionTree* q )
        // const { return *p < *q; }
    }
}
