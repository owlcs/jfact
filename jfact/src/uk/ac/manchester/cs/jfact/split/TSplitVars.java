package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;

/// set of all known var splits with access by name
public class TSplitVars {
    protected final List<TSplitVar> Base = new ArrayList<TSplitVar>();
    protected final Map<ConceptName, TSplitVar> Index = new HashMap<ConceptName, TSplitVar>();

    public TSplitVars() {}

    /// @return true iff the NAME has split in the set
    boolean hasCN(final ConceptName name) {
        return Index.containsKey(name);
    }

    /// @return split corresponding to  given name; only correct for known names
    TSplitVar get(final ConceptName name) {
        return Index.get(name);
    }

    /// put SPLIT into the set corresponding to NAME
    void set(final ConceptName name, final TSplitVar split) {
        Index.put(name, split);
        Base.add(split);
    }

    public List<TSplitVar> getEntries() {
        return Base;
    }

    /// @return true iff split-set is empty
    public boolean empty() {
        return Base.isEmpty();
    }
}
