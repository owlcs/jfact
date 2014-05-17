package uk.ac.manchester.cs.jfact.datatypes;

import org.semanticweb.owlapi.model.IRI;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/**
 * @author ignazio
 * @param <T>
 *        type
 * @param <E>
 *        element
 */
public interface DatatypeCombination<T, E> {

    /**
     * @param d
     *        d
     * @return the type
     */
    T add(E d);

    /**
     * @param type
     *        type
     * @return true if type\s value space and this datatype's value space have
     *         an intersection, e.g., non negative integers and non positive
     *         integers intersect at 0
     */
    boolean isCompatible(Datatype<?> type);

    /**
     * @param l
     *        literal
     * @return true if l is a literal with compatible datatype and value
     *         included in this datatype value space
     */
    boolean isCompatible(Literal<?> l);

    /**
     * @param type
     *        type
     * @return true if the datatype is contradictory, e.g., the two appearing
     *         together in a datatype situation cause a clash. e.g., +{"6"} and
     *         +{"4"} are not compatible and not contradictory, +{"6"} and
     *         -{"6"} are compatible and contradictory
     */
    boolean isContradictory(Datatype<?> type);

    /** @return the datatype uri */
    IRI getDatatypeIRI();

    /** @return list of elements */
    Iterable<E> getList();

    /** @return true if the value space is empty */
    boolean emptyValueSpace();

    /** @return the host datatype */
    Datatype<?> getHost();
}
