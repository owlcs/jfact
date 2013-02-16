package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import conformance.PortedFrom;

/** set of all known var splits with access by name */
@PortedFrom(file = "tSplitVars.h", name = "TSplitVars")
public class TSplitVars {
    @PortedFrom(file = "tSplitVars.h", name = "Base")
    protected List<TSplitVar> Base = new ArrayList<TSplitVar>();
    @PortedFrom(file = "tSplitVars.h", name = "Index")
    protected Map<ConceptName, TSplitVar> Index = new HashMap<ConceptName, TSplitVar>();

    @SuppressWarnings("javadoc")
    public TSplitVars() {}

    /** @return true iff the NAME has split in the set */
    @PortedFrom(file = "tSplitVars.h", name = "hasCN")
    boolean hasCN(ConceptName name) {
        return Index.containsKey(name);
    }

    /** @return split corresponding to given name; only correct for known names */
    @PortedFrom(file = "tSplitVars.h", name = "get")
    TSplitVar get(ConceptName name) {
        return Index.get(name);
    }

    /** put SPLIT into the set corresponding to NAME */
    @PortedFrom(file = "tSplitVars.h", name = "set")
    void set(ConceptName name, TSplitVar split) {
        Index.put(name, split);
        Base.add(split);
    }

    /** @return list of split var entries */
    @PortedFrom(file = "tSplitVars.h", name = "begin")
    public List<TSplitVar> getEntries() {
        return Base;
    }

    /** @return true iff split-set is empty */
    @PortedFrom(file = "tSplitVars.h", name = "empty")
    public boolean empty() {
        return Base.isEmpty();
    }
}
