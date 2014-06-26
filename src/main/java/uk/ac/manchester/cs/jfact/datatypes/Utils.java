package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/** @author ignazio */
public class Utils implements Serializable {

    private static final long serialVersionUID = 11000L;

    /**
     * @param facets
     *        facets
     * @return set of facets
     */
    public static Set<Facet> getFacets(Facet... facets) {
        Set<Facet> toReturn = new HashSet<>();
        for (Facet f : facets) {
            toReturn.add(f);
        }
        return toReturn;
    }

    /**
     * @param facets
     *        facets
     * @return set of facets
     */
    public static Set<Facet> getFacets(Facet[]... facets) {
        Set<Facet> toReturn = new HashSet<>();
        for (Facet[] fac : facets) {
            for (Facet f : fac) {
                toReturn.add(f);
            }
        }
        return toReturn;
    }

    /**
     * @param d
     *        d
     * @return ancestors
     */
    public static Set<Datatype<?>> generateAncestors(Datatype<?> d) {
        Set<Datatype<?>> toReturn = new HashSet<>(d.getAncestors());
        toReturn.add(d);
        return toReturn;
    }
}
