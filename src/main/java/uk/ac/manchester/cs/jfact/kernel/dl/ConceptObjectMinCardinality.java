package uk.ac.manchester.cs.jfact.kernel.dl;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import org.semanticweb.owlapi.model.IRI;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptObjectCardinalityExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NumberArg;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

/** object min cardinality */
@PortedFrom(file = "tDLExpression.h", name = "TDLConceptObjectMinCardinality")
public class ConceptObjectMinCardinality implements ConceptObjectCardinalityExpression, NumberArg, Serializable {

    @Original private final ConceptExpression delegate;
    @PortedFrom(file = "tDLExpression.h", name = "N") private final int n;
    @Original private final ObjectRoleExpression roleDelegate;

    /**
     * @param n
     *        n
     * @param r
     *        R
     * @param c
     *        C
     */
    public ConceptObjectMinCardinality(int n, ObjectRoleExpression r, ConceptExpression c) {
        this.n = n;
        delegate = c;
        roleDelegate = r;
    }

    @Override
    @PortedFrom(file = "tDLExpression.h", name = "accept")
    public void accept(DLExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    @PortedFrom(file = "tDLExpression.h", name = "accept")
    public <O> O accept(DLExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    @PortedFrom(file = "tDLExpression.h", name = "getNumber")
    public int getCardinality() {
        return n;
    }

    @Override
    @PortedFrom(file = "tDLExpression.h", name = "getC")
    public ConceptExpression getConcept() {
        return delegate;
    }

    @Override
    @PortedFrom(file = "tDLExpression.h", name = "getOR")
    public ObjectRoleExpression getOR() {
        return roleDelegate;
    }

    @Override
    public IRI getIRI() {
        return IRI.create("objectMinCardinality(" + roleDelegate + " " + n + " " + delegate + ")");
    }

    @Override
    public String toString() {
        return getIRI().toString();
    }
}
