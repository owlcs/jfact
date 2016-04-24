package uk.ac.manchester.cs.jfact.datatypes;

import org.semanticweb.owlapi.vocab.OWLFacet;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/**
 * @author ignazio
 */
public interface Facet {

    /**
     * This is a convenience method that enables each facet to parse its values
     * without client code having to worry whether the values are literals or
     * strings - the typing is known to the facet implementation
     * 
     * @param value
     *        value
     * @param <T>
     *        type
     * @return value that is assigned to this facet
     */
    <T extends Comparable<T>> T parseNumber(Object value);

    /**
     * @param value
     *        value
     * @return the typed value for value
     */
    @SuppressWarnings("rawtypes")
    Comparable parse(Object value);

    /** @return true if number facet */
    boolean isNumberFacet();

    /** @return the facet uri */
    String getURI();

    /** @return OWLFacet vale */
    OWLFacet facet();
}
