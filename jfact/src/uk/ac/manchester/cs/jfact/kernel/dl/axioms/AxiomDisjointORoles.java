package uk.ac.manchester.cs.jfact.kernel.dl.axioms;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.jfact.kernel.dl.NAryExpressionImpl;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NAryExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;
import conformance.PortedFrom;

/** Object Role disjointness axiom */
@PortedFrom(file="tDLAxiom.h",name="TDLAxiomDisjointORoles")
public class AxiomDisjointORoles extends AxiomImpl implements Axiom,
        NAryExpression<ObjectRoleExpression> {
    private NAryExpressionImpl<ObjectRoleExpression> delegate;

    public AxiomDisjointORoles(OWLAxiom ax, List<Expression> v) {
        super(ax);
        delegate = new NAryExpressionImpl<ObjectRoleExpression>();
        delegate.add(v);
    }

    @Override

@PortedFrom(file="tDLAxiom.h",name="accept")
    public void accept(DLAxiomVisitor visitor) {
        visitor.visit(this);
    }

    @Override

@PortedFrom(file="tDLAxiom.h",name="accept")
    public <O> O accept(DLAxiomVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void add(Collection<Expression> v) {
        delegate.add(v);
    }

    @Override
    public void add(Expression p) {
        delegate.add(p);
    }

    @Override
    public List<ObjectRoleExpression> getArguments() {
        return delegate.getArguments();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public ObjectRoleExpression transform(Expression arg) {
        return delegate.transform(arg);
    }
}
