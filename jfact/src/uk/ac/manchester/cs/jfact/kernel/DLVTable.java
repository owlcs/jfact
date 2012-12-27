package uk.ac.manchester.cs.jfact.kernel;

import static uk.ac.manchester.cs.jfact.helpers.Helper.*;

import java.util.HashMap;
import java.util.Map;

import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.FastSet;
import uk.ac.manchester.cs.jfact.helpers.FastSetFactory;
import conformance.PortedFrom;

@PortedFrom(file = "dlVHash.h", name = "dlVHashTable")
class DLVTable {
    /** host DAG that contains actual nodes; */
    private DLDag host;
    /** HT for nodes */
    private Map<DLVertex, FastSet> table = new HashMap<DLVertex, FastSet>();

    protected DLVTable(DLDag dag) {
        host = dag;
    }

    private int locate(FastSet leaf, DLVertex v) {
        for (int i = 0; i < leaf.size(); i++) {
            int p = leaf.get(i);
            if (v.equals(host.get(p))) {
                return p;
            }
        }
        return bpINVALID;
    }

    protected int locate(DLVertex v) {
        FastSet p = table.get(v);
        return p == null ? bpINVALID : locate(p, v);
    }

    protected void addElement(int pos) {
        FastSet leaf = table.get(host.get(pos));
        if (leaf == null) {
            leaf = FastSetFactory.create();
            table.put(host.get(pos), leaf);
        }
        leaf.add(pos);
    }

    @Override
    public String toString() {
        return table.toString() + "\n" + host.toString();
    }
}
