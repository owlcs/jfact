package uk.ac.manchester.cs.jfact.kernel.dl.interfaces;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.pairs;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;

import conformance.PortedFrom;

/**
 * nary expression
 * 
 * @param <A>
 *        argument type
 */
@PortedFrom(file = "tDLExpression.h", name = "TDLNAryExpression")
public interface NAryExpression<A extends Expression> {

    /**
     * @param arg
     *        arg
     * @return transform general expression into the argument one
     */
    @PortedFrom(file = "tDLExpression.h", name = "transform")
        A transform(Expression arg);

    /**
     * add a single element to the array
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "tDLExpression.h", name = "add")
        void add(A p);

    /**
     * add a vector
     * 
     * @param v
     *        v
     */
    @PortedFrom(file = "tDLExpression.h", name = "add")
        void add(Collection<A> v);

    /** @return members */
    @PortedFrom(file = "tDLExpression.h", name = "begin")
        List<A> getArguments();

    /**
     * @param f
     *        function to apply to first element and all others pairs
     * @return true if any matches
     */
    default boolean anyMatchWithFirst(BiPredicate<A, A> f) {
        List<A> arguments = getArguments();
        for (int i = 1; i < arguments.size(); i++) {
            if (f.test(arguments.get(0), arguments.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param f
     *        function to apply to first element and all others pairs
     * @return true if all match
     */
    default boolean allMatchWithFirst(BiPredicate<A, A> f) {
        List<A> arguments = getArguments();
        for (int i = 1; i < arguments.size(); i++) {
            if (!f.test(arguments.get(0), arguments.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param f
     *        function to apply to all pairs
     * @return true if any matches
     */
    default boolean anyMatch(BiPredicate<A, A> f) {
        return pairs(getArguments()).anyMatch(v -> f.test(v.i, v.j));
    }

    /**
     * @param f
     *        function to apply to all pairs
     * @return true if all match
     */
    default boolean allMatch(BiPredicate<A, A> f) {
        return pairs(getArguments()).allMatch(v -> f.test(v.i, v.j));
    }

    /** @return true if empty */
    @PortedFrom(file = "tDLExpression.h", name = "empty")
        boolean isEmpty();

    /** @return size of members */
    @PortedFrom(file = "tDLExpression.h", name = "size")
        int size();
}
