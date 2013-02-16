package uk.ac.manchester.cs.jfact.kernel.dl.interfaces;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;
import java.util.List;

import conformance.PortedFrom;

/** nary expression
 * 
 * @param <Argument> */
@PortedFrom(file = "tDLExpression.h", name = "TDLNAryExpression")
public interface NAryExpression<Argument extends Expression> {
    /** @param arg
     * @return transform general expression into the argument one */
    @PortedFrom(file = "tDLExpression.h", name = "transform")
    Argument transform(Expression arg);

    /** add a single element to the array
     * 
     * @param p */
    @PortedFrom(file = "tDLExpression.h", name = "add")
    void add(Argument p);

    /** add a vector
     * 
     * @param v */
    @PortedFrom(file = "tDLExpression.h", name = "add")
    void add(Collection<Argument> v);

    /** @return members */
    @PortedFrom(file = "tDLExpression.h", name = "begin")
    List<Argument> getArguments();

    /** @return true if empty */
    @PortedFrom(file = "tDLExpression.h", name = "empty")
    boolean isEmpty();

    /** @return size of members */
    @PortedFrom(file = "tDLExpression.h", name = "size")
    int size();
}
