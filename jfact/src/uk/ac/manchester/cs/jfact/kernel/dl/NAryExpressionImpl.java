package uk.ac.manchester.cs.jfact.kernel.dl;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NAryExpression;

public class NAryExpressionImpl<Argument extends Expression> implements
        NAryExpression<Argument> {
    /** set of equivalent concept descriptions */
    private List<Argument> Base = new ArrayList<Argument>();

    public NAryExpressionImpl() {}

    public void add(Collection<Expression> v) {
        for (Expression e : v) {
            add(e);
        }
    }

    public void add(Expression p) {
        Base.add(transform(p));
    }

    public List<Argument> getArguments() {
        return Base;
    }

    public boolean isEmpty() {
        return Base.isEmpty();
    }

    public int size() {
        return Base.size();
    }

    /** transform general expression into the argument one */
    public Argument transform(Expression arg) {
        return (Argument) arg;
    }

    @Override
    public String toString() {
        return "NAryExpression(" + Base + ")";
    }
}
