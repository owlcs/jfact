package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.HashMap;

import conformance.PortedFrom;

@PortedFrom(file="tNameSet.h",name="TNameSet")
public class NameSet<T> extends HashMap<String, T> {
    /** creator of new name */
    private NameCreator<T> creator;

    public NameSet(NameCreator<T> p) {
        creator = p;
    }

    /** unconditionally add new element with name ID to the set; return new
     * element */
    public T add(String id) {
        T pne = creator.makeEntry(id);
        put(id, pne);
        return pne;
    }

    /** Insert id to a nameset (if necessary); @return pointer to id structure
     * created by external creator */
    public T insert(String id) {
        T pne = get(id);
        if (pne == null) {
            pne = add(id);
        }
        return pne;
    }
}
