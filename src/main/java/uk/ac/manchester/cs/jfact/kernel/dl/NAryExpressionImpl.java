package uk.ac.manchester.cs.jfact.kernel.dl;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NAryExpression;

/**
 * nary expression base
 * 
 * @param <A>
 *        argument type
 */
@PortedFrom(file = "tDLExpression.h", name = "TDLNAryExpression")
public class NAryExpressionImpl<A extends Expression> implements NAryExpression<A>, Serializable {

    /** set of equivalent concept descriptions */
    private final List<A> base = new ArrayList<>();

    @Override
    public void add(Collection<A> v) {
        v.forEach(this::add);
    }

    @Override
    public void add(Expression p) {
        base.add(transform(p));
    }

    @Override
    public List<A> getArguments() {
        return base;
    }

    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "empty")
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public int size() {
        return base.size();
    }

    /** transform general expression into the argument one */
    @SuppressWarnings("unchecked")
    @Override
    public A transform(Expression arg) {
        return (A) arg;
    }

    @Override
    @Nonnull
    public String toString() {
        return "NAryExpression(" + base + ')';
    }
}
