package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.FastSet;
import uk.ac.manchester.cs.jfact.helpers.FastSetFactory;
import conformance.PortedFrom;

@PortedFrom(file = "dlVHash.h", name = "dlVHashTable")
class DLVTable implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** host DAG that contains actual nodes; */
    private final DLDag host;
    /** HT for nodes */
    private final Map<DLVertex, FastSet> table = new HashMap<DLVertex, FastSet>();

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
