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

/** Object Role equivalence axiom */
public final class AxiomEquivalentORoles extends AxiomImpl implements Axiom,
		NAryExpression<ObjectRoleExpression> {
	private final NAryExpressionImpl<ObjectRoleExpression> delegate;

	public AxiomEquivalentORoles(final OWLAxiom ax, final List<Expression> v) {
		super(ax);
		delegate = new NAryExpressionImpl<ObjectRoleExpression>();
		delegate.add(v);
	}

	public void accept(final DLAxiomVisitor visitor) {
		visitor.visit(this);
	}

	public <O> O accept(final DLAxiomVisitorEx<O> visitor) {
		return visitor.visit(this);
	}

	public void add(final Collection<Expression> v) {
		delegate.add(v);
	}

	public void add(final Expression p) {
		delegate.add(p);
	}

	public List<ObjectRoleExpression> getArguments() {
		return delegate.getArguments();
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	public int size() {
		return delegate.size();
	}

	public ObjectRoleExpression transform(final Expression arg) {
		return delegate.transform(arg);
	}
}
