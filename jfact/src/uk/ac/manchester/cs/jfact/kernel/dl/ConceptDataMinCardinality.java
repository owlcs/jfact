package uk.ac.manchester.cs.jfact.kernel.dl;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptDataCardinalityExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleArg;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

public final class ConceptDataMinCardinality implements ConceptDataCardinalityExpression,
        DataRoleArg {
    /** data role argument */
    private final DataRoleExpression dataRoleExpression;
    private final int delegate;
    private final DataExpression delegateExpression;

    public ConceptDataMinCardinality(final int n, final DataRoleExpression R,
            final DataExpression E) {
        dataRoleExpression = R;
        delegateExpression = E;
        delegate = n;
    }

    public void accept(final DLExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public <O> O accept(final DLExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public int getCardinality() {
        return delegate;
    }

    /** get access to the argument */
    public DataRoleExpression getDataRoleExpression() {
        return dataRoleExpression;
    }

    public DataExpression getExpr() {
        return delegateExpression;
    }
}
