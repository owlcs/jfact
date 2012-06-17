package uk.ac.manchester.cs.jfact.kernel.datatype;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.kernel.dl.NAryExpressionImpl;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpressionArg;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataTypeExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.FacetExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NAryExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

public final class DataTypeRestriction extends NAryExpressionImpl<FacetExpression>
		implements NAryExpression<FacetExpression>, DataTypeExpression,
		DataExpressionArg<DataTypeName> {
	private final DataTypeName delegate;

	public DataTypeRestriction(final DataTypeName T) {
		delegate = T;
	}

	public void accept(DLExpressionVisitor visitor) {
		visitor.visit(this);
	}

	public <O> O accept(DLExpressionVisitorEx<O> visitor) {
		return visitor.visit(this);
	}

	public DataTypeName getExpr() {
		return delegate;
	}
}