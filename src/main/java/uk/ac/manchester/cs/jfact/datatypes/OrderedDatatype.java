package uk.ac.manchester.cs.jfact.datatypes;

import javax.annotation.Nullable;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/**
 * all Datatypes whose getNumeric() method returns true implement this interface
 * 
 * @param <R>
 *        type
 */
public interface OrderedDatatype<R extends Comparable<R>> {

    /** @return true if min exclusive facet applies */
    boolean hasMinExclusive();

    /** @return true if min inclusive facet applies */
    boolean hasMinInclusive();

    /** @return true if max exclusive facet applies */
    boolean hasMaxExclusive();

    /** @return true if max inclusive facet applies */
    boolean hasMaxInclusive();

    /** @return true if min exists */
    boolean hasMin();

    /** @return true if max exists */
    boolean hasMax();

    /** @return value for min */
    @Nullable
        R getMin();

    /** @return value for max */
    @Nullable
        R getMax();
}
