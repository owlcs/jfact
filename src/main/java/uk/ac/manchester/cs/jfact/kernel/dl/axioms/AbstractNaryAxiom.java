package uk.ac.manchester.cs.jfact.kernel.dl.axioms;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.OWLAxiom;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.NAryExpressionImpl;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NAryExpression;

/**
 * n-ary axiom base class
 * 
 * @param <I>
 *        expression type
 */
@Original
public abstract class AbstractNaryAxiom<I extends Expression> extends AxiomImpl implements NAryExpression<I> {

    private final NAryExpressionImpl<I> delegate;

    /**
     * @param ax
     *        ax
     * @param v
     *        v
     */
    protected AbstractNaryAxiom(OWLAxiom ax, Collection<I> v) {
        super(ax);
        delegate = new NAryExpressionImpl<>();
        delegate.add(v);
    }

    @Override
    public final void add(Collection<I> v) {
        delegate.add(v);
    }

    @Override
    public final void add(Expression p) {
        delegate.add(p);
    }

    @Override
    @PortedFrom(file = "tDLExpression.h", name = "begin")
    public final List<I> getArguments() {
        return delegate.getArguments();
    }

    @Override
    public Stream<Expression> namedEntitySignature() {
        return getArguments().stream().map(m -> m);
    }

    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "empty")
    public final boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    @PortedFrom(file = "tDLExpression.h", name = "size")
    public final int size() {
        return delegate.size();
    }

    @Override
    public final I transform(Expression arg) {
        return delegate.transform(arg);
    }
}
