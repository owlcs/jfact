package uk.ac.manchester.cs.jfact.kernel.actors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;
import conformance.PortedFrom;

/** Actor interface */
@PortedFrom(file = "Actor.h", name = "Actor")
public interface Actor {
    /** @param v
     *            the vertex to act upon
     * @return true if applied */
    @PortedFrom(file = "Actor.h", name = "apply")
    boolean apply(TaxonomyVertex v);
    
    /** @return get 1-d NULL-terminated array of synonyms of the 1st
     *         entry(necessary for Equivalents, for example) */
    @PortedFrom(file = "Actor.h", name = "getSynonyms")
    List<String> getSynonyms();

    /** @return get NULL-terminated 2D array of all required elements of the
     *         taxonomy */
    @PortedFrom(file = "Actor.h", name = "getElements2D")
    List<List<String>> getElements2D();

    /** @return get NULL-terminated 1D array of all required elements of the
     *         taxonomy */
    @PortedFrom(file = "Actor.h", name = "getElements1D")
    List<String> getElements1D();

    /** set the actor to look for classes */
    @PortedFrom(file = "Actor.h", name = "needConcepts")
    void needConcepts();

    /** set the actor to look for individuals */
    @PortedFrom(file = "Actor.h", name = "needIndividuals")
    void needIndividuals();

    /** set the actor to look for object properties */
    @PortedFrom(file = "Actor.h", name = "needObjectRoles")
    void needObjectRoles();

    /** set the actor to look for individuals */
    @PortedFrom(file = "Actor.h", name = "needDataRoles")
    void needDataRoles();

    /** @param value
     *            set the interrupt parameter to VALUE */
    @PortedFrom(file = "Actor.h", name = "setInterruptAfterFirstFound")
    void setInterruptAfterFirstFound(boolean value);
}
