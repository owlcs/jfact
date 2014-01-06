package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import conformance.PortedFrom;

/** set of all known var splits with access by name */
@PortedFrom(file = "tSplitVars.h", name = "TSplitVars")
public class TSplitVars implements Serializable {
    private static final long serialVersionUID = 11000L;
    @PortedFrom(file = "tSplitVars.h", name = "Base")
    protected final List<TSplitVar> Base = new ArrayList<TSplitVar>();
    @PortedFrom(file = "tSplitVars.h", name = "Index")
    protected final Map<ConceptName, TSplitVar> Index = new HashMap<ConceptName, TSplitVar>();

    /** default constructor */
    public TSplitVars() {}

    /** @param name
     *            name
     * @return true iff the NAME has split in the set */
    @PortedFrom(file = "tSplitVars.h", name = "hasCN")
    public boolean hasCN(ConceptName name) {
        return Index.containsKey(name);
    }

    /** @param name
     *            name
     * @return split corresponding to given name; only correct for known names */
    @PortedFrom(file = "tSplitVars.h", name = "get")
    public TSplitVar get(ConceptName name) {
        return Index.get(name);
    }

    /** put SPLIT into the set corresponding to NAME
     * 
     * @param name
     *            name
     * @param split
     *            split */
    @PortedFrom(file = "tSplitVars.h", name = "set")
    public void set(ConceptName name, TSplitVar split) {
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
