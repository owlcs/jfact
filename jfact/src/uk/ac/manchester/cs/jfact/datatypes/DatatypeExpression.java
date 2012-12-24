package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** Represents an extended datatype - intersection, union or further
 * restrictions. All implementations must be immutable */
public interface DatatypeExpression<Representation extends Comparable<Representation>>
        extends Datatype<Representation> {
    /** @return the predefined datatype which is host for this expression */
    Datatype<Representation> getHostType();

    /** add a new facet value for this datatype expression
     * 
     * @param f
     *            a valid facet for the host datatype
     * @param value
     *            the value for the facet */
    DatatypeExpression<Representation> addFacet(Facet f, Object value);

    /** @return true if this datatype expression is limited in such a way that
     *         there are no valid values */
    boolean emptyValueSpace();
}
