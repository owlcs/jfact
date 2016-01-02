package uk.ac.manchester.cs.jfact.kernel.dl;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.IRI;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptDataCardinalityExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleArg;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;

/** base class for data cardinality */
@Original
public abstract class AbstractConceptDataCardinality
    implements ConceptDataCardinalityExpression, DataRoleArg, Serializable {

    @PortedFrom(file = "tDLExpression.h", name = "N") private final int cardinality;
    /** data role argument */
    @PortedFrom(file = "tDLExpression.h", name = "DR") private final DataRoleExpression dataRoleExpression;
    @PortedFrom(file = "tDLExpression.h", name = "Expr") private final DataExpression expression;

    protected AbstractConceptDataCardinality(int n, DataRoleExpression r, DataExpression e) {
        dataRoleExpression = r;
        expression = e;
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

    @Override
    @Nonnull
    public String toString() {
        return "Cardinality(" + cardinality + " " + dataRoleExpression + " " + expression;
    }

    @Override
    public IRI getIRI() {
        return IRI.create(toString());
    }
}
