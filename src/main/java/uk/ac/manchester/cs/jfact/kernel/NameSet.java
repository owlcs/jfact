package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.LinkedHashMap;
import java.util.function.Function;

import conformance.PortedFrom;

/**
 * name set
 * 
 * @param <T>
 *        type
 * @param <K>
 *        key
 */
@PortedFrom(file = "tNameSet.h", name = "TNameSet")
public class NameSet<T, K> extends LinkedHashMap<K, T> {

    /** creator of new name */
    @PortedFrom(file = "tNameSet.h", name = "Creator") private final Function<K, T> creator;

    /**
     * @param p
     *        p
     */
    public NameSet(Function<K, T> p) {
        creator = p;
    }

    /**
     * unconditionally add new element with name ID to the set;
     * 
     * @param id
     *        id
     * @return new element
     */
    @PortedFrom(file = "tNameSet.h", name = "add")
    public T add(K id) {
        T pne = creator.apply(id);
        put(id, pne);
        return pne;
    }

    /**
     * Insert id to a nameset (if necessary);
     * 
     * @param id
     *        id
     * @return pointer to id structure created by external creator
     */
    @PortedFrom(file = "tNameSet.h", name = "insert")
    public T insert(K id) {
        T pne = get(id);
        if (pne == null) {
            pne = add(id);
        }
        return pne;
    }
}
