package uk.ac.manchester.cs.jfact.kernel.actors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;

/** Actor interface */
@PortedFrom(file = "Actor.h", name = "Actor")
public interface Actor extends WalkerInterface {

    /** clear collections */
    default void clear() {
        // nothing to do as default
    }

    /**
     * @param v
     *        vertex to check
     * @return true if applicable
     */
    boolean applicable(TaxonomyVertex v);

    /**
     * @param pastBoundary
     *        nodes to remove
     */
    default void removePastBoundaries(@SuppressWarnings("unused") Collection<TaxonomyVertex> pastBoundary) {}
}
