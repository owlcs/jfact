package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;

import javax.annotation.Nullable;

/**
 * numeric enumeration
 * 
 * @param <R>
 *        type
 */
public class DatatypeNumericEnumeration<R extends Comparable<R>> extends DatatypeEnumeration<R>
    implements NumericDatatype<R> {

    /**
     * @param d
     *        d
     */
    public DatatypeNumericEnumeration(NumericDatatype<R> d) {
        super(d);
    }

    /**
     * @param d
     *        d
     * @param l
     *        l
     */
    public DatatypeNumericEnumeration(NumericDatatype<R> d, Literal<R> l) {
        this(d);
        literals.add(l);
    }

    /**
     * @param d
     *        d
     * @param c
     *        c
     */
    public DatatypeNumericEnumeration(NumericDatatype<R> d, Collection<Literal<R>> c) {
        this(d);
        literals.addAll(c);
    }

    @Override
    public DatatypeNumericEnumeration<R> add(Literal<R> d) {
        DatatypeNumericEnumeration<R> toReturn = new DatatypeNumericEnumeration<>((NumericDatatype<R>) host, literals);
        toReturn.literals.add(d);
        return toReturn;
    }

    @Override
    public boolean isNumericDatatype() {
        return true;
    }

    @Override
    public NumericDatatype<R> asNumericDatatype() {
        return this;
    }

    @Override
    public boolean isOrderedDatatype() {
        return true;
    }

    @Override
    public boolean hasMinExclusive() {
        return false;
    }

    @Override
    public boolean hasMinInclusive() {
        return !literals.isEmpty();
    }

    @Override
    public boolean hasMaxExclusive() {
        return false;
    }

    @Override
    public boolean hasMaxInclusive() {
        return !literals.isEmpty();
    }

    @Override
    public boolean hasMin() {
        return !literals.isEmpty();
    }

    @Override
    public boolean hasMax() {
        return !literals.isEmpty();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    @Override
    public R getMin() {
        if (literals.isEmpty()) {
            return null;
        }
        return (R) Facets.minInclusive.parseNumber(literals.get(0));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    @Override
    public R getMax() {
        if (literals.isEmpty()) {
            return null;
        }
        return (R) Facets.maxInclusive.parseNumber(literals.get(literals.size() - 1));
    }

    @Override
    public OrderedDatatype<R> asOrderedDatatype() {
        return this;
    }
}
