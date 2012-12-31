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
import conformance.Original;
import conformance.PortedFrom;

@Original
public abstract class AbstractConceptDataCardinality implements
        ConceptDataCardinalityExpression, DataRoleArg {
    @PortedFrom(file = "tDLExpression.h", name = "N")
    private int cardinality;
    /** data role argument */
    @PortedFrom(file = "tDLExpression.h", name = "DR")
    private DataRoleExpression dataRoleExpression;
    @PortedFrom(file = "tDLExpression.h", name = "Expr")
    private DataExpression expression;

    protected AbstractConceptDataCardinality(int n, DataRoleExpression R, DataExpression E) {
        dataRoleExpression = R;
        expression = E;
        cardinality = n;
    }

    @Override
    @PortedFrom(file = "tDLExpression.h", name = "getNumber")
    public int getCardinality() {
        return cardinality;
    }

    /** get access to the argument */
    @Override
    public DataRoleExpression getDataRoleExpression() {
        return dataRoleExpression;
    }

    @Override
    @PortedFrom(file = "tDLExpression.h", name = "getExpr")
    public DataExpression getExpr() {
        return expression;
    }
}
