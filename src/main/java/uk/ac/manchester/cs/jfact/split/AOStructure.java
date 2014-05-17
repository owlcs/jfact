package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import conformance.PortedFrom;

/** atomical ontology structure */
@PortedFrom(file = "AtomicDecomposer.h", name = "AOStructure")
public class AOStructure implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** all the atoms */
    @PortedFrom(file = "AtomicDecomposer.h", name = "Atoms")
    private final List<TOntologyAtom> Atoms = new ArrayList<TOntologyAtom>();

    /** @return a new atom */
    @PortedFrom(file = "AtomicDecomposer.h", name = "newAtom")
    public TOntologyAtom newAtom() {
        TOntologyAtom ret = new TOntologyAtom();
        ret.setId(Atoms.size());
        Atoms.add(ret);
        return ret;
    }

    /** reduce graph of the atoms in the structure */
    @PortedFrom(file = "AtomicDecomposer.h", name = "reduceGraph")
    public void reduceGraph() {
        Set<TOntologyAtom> checked = new HashSet<TOntologyAtom>();
        for (TOntologyAtom p : Atoms) {
            p.getAllDepAtoms(checked);
        }
    }

    /** @return atoms */
    @PortedFrom(file = "AtomicDecomposer.h", name = "begin")
    public List<TOntologyAtom> getAtoms() {
        return Atoms;
    }

    /**
     * @param index
     *        index
     * @return atom by its index
     */
    @PortedFrom(file = "AtomicDecomposer.h", name = "get")
    public TOntologyAtom get(int index) {
        return Atoms.get(index);
    }

    /** @return size of the structure */
    @PortedFrom(file = "AtomicDecomposer.h", name = "size")
    public int size() {
        return Atoms.size();
    }
}
