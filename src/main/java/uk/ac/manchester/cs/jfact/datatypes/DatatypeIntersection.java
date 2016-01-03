package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.anyMatchOnAllPairs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;

/**
 * @author ignazio
 * @param <T>
 *        type
 */
public class DatatypeIntersection<T extends Comparable<T>>
    implements DatatypeCombination<DatatypeIntersection<T>, Datatype<T>> {

    private final Set<Datatype<T>> basics = new HashSet<>();
    private final IRI uri;
    private final Datatype<T> host;

    /**
     * @param host
     *        host
     */
    public DatatypeIntersection(Datatype<T> host) {
        uri = DatatypeFactory.getIndex("urn:intersection#a").getIRI();
        this.host = host;
    }

    /**
     * @param host
     *        host
     * @param list
     *        list
     */
    public DatatypeIntersection(Datatype<T> host, Iterable<Datatype<T>> list) {
        this(host);
        list.forEach(basics::add);
    }

    /**
     * @param collection
     *        collection
     * @return datatype host for a set of datatypes
     */
    @Nullable
    public static Datatype<?> getHostDatatype(List<Datatype<?>> collection) {
        // all types need to be compatible, or the intersection cannot be
        // anything but empty
        if (anyMatchOnAllPairs(collection, v -> !v.i.isCompatible(v.j))) {
            return null;
        }
        List<Datatype<?>> list = new ArrayList<>(collection);
        // the most specific type needs to be returned
        int oldSize;
        do {
            oldSize = list.size();
            for (int i = 0; i < list.size() - 1;) {
                Datatype<?> next = list.get(i + 1);
                Datatype<?> current = list.get(i);
                assert current != null;
                assert next != null;
                if (current.isSubType(next)) {
                    list.remove(i + 1);
                } else if (next.isSubType(current)) {
                    list.remove(i);
                } else {
                    i++;
                }
            }
        } while (list.size() > 1 && oldSize != list.size());
        // now if list.size >1, there is no single most specific type...
        // troubles
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Datatype<?> getHost() {
        return host;
    }

    @Override
    public Iterable<Datatype<T>> getList() {
        return basics;
    }

    @Override
    public DatatypeIntersection<T> add(Datatype<T> d) {
        DatatypeIntersection<T> toReturn = new DatatypeIntersection<>(host, basics);
        toReturn.basics.add(d);
        return toReturn;
    }

    @Override
    public boolean isCompatible(Literal<?> l) {
        // must be compatible with all basics
        // host is a shortcut to them
        if (!host.isCompatible(l)) {
            return false;
        }
        return basics.stream().allMatch(d -> d.isCompatible(l));
    }

    @Override
    public IRI getDatatypeIRI() {
        return uri;
    }

    @Override
    public boolean isCompatible(Datatype<?> type) {
        // must be compatible with all basics
        // host is a shortcut to them
        if (!host.isCompatible(type)) {
            return false;
        }
        return basics.stream().allMatch(d -> d.isCompatible(type));
    }

    @Override
    public boolean isContradictory(Datatype<?> type) {
        return !isCompatible(type);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public boolean emptyValueSpace() {
        // all base types must be numeric
        if (!host.getNumeric()) {
            return false;
        }
        Comparable min = null;
        Comparable max = null;
        // all intervals must intersect - i.e., the interval with max min
        // (excluded if any interval excludes it), min max (excluded if any
        // interval excludes it) must contain at least one element
        // get max minimum value
        boolean minExclusive = false;
        boolean maxExclusive = false;
        for (Datatype<T> dt : basics) {
            Comparable facetValue = dt.asNumericDatatype().getMin();
            if (facetValue != null && (min == null || min.compareTo(facetValue) < 0)) {
                min = facetValue;
            }
            facetValue = dt.asNumericDatatype().getMax();
            if (facetValue != null && (max == null || facetValue.compareTo(max) < 0)) {
                max = facetValue;
            }
            if (dt.asNumericDatatype().hasMinExclusive()) {
                minExclusive = true;
            }
            if (dt.asNumericDatatype().hasMaxExclusive()) {
                maxExclusive = true;
            }
        }
        int excluded = 0;
        if (minExclusive) {
            excluded++;
        }
        if (maxExclusive) {
            excluded++;
        }
        return !DatatypeFactory.intervalWithValues(min, max, excluded);
    }

    @Override
    public String toString() {
        return uri + "{" + basics + '}';
    }
}
