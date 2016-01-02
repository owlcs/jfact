package uk.ac.manchester.cs.jfact.visitors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeExpression;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataExactCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataForall;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataMaxCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataMinCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataValue;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptNot;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExactCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectForall;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectMaxCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectMinCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectSelf;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectValue;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptOneOf;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptOr;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptTop;
import uk.ac.manchester.cs.jfact.kernel.dl.DataAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.DataBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.DataNot;
import uk.ac.manchester.cs.jfact.kernel.dl.DataOneOf;
import uk.ac.manchester.cs.jfact.kernel.dl.DataOr;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.DataTop;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionInto;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;

/** adapter for expression visitor */
public abstract class DLExpressionVisitorAdapter implements
        DLExpressionVisitor, Serializable {



    protected void doDefault(@SuppressWarnings("unused") Expression a) {}

    @Override
    public void visit(ConceptTop expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptBottom expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptName expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptNot expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptAnd expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptOr expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptOneOf<?> expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptObjectSelf expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptObjectValue expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptObjectExists expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptObjectForall expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptObjectMinCardinality expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptObjectMaxCardinality expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptObjectExactCardinality expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptDataValue expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptDataExists expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptDataForall expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptDataMinCardinality expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptDataMaxCardinality expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ConceptDataExactCardinality expr) {
        doDefault(expr);
    }

    @Override
    public void visit(IndividualName expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ObjectRoleTop expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ObjectRoleBottom expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ObjectRoleName expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ObjectRoleInverse expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ObjectRoleChain expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ObjectRoleProjectionFrom expr) {
        doDefault(expr);
    }

    @Override
    public void visit(ObjectRoleProjectionInto expr) {
        doDefault(expr);
    }

    @Override
    public void visit(DataRoleTop expr) {
        doDefault(expr);
    }

    @Override
    public void visit(DataRoleBottom expr) {
        doDefault(expr);
    }

    @Override
    public void visit(DataRoleName expr) {
        doDefault(expr);
    }

    @Override
    public void visit(DataTop expr) {
        doDefault(expr);
    }

    @Override
    public void visit(DataBottom expr) {
        doDefault(expr);
    }

    @Override
    public void visit(Datatype<?> expr) {
        doDefault(expr);
    }

    @Override
    public void visit(DatatypeExpression<?> expr) {
        doDefault(expr);
    }

    @Override
    public void visit(Literal<?> expr) {
        doDefault(expr);
    }

    @Override
    public void visit(DataNot expr) {
        doDefault(expr);
    }

    @Override
    public void visit(DataAnd expr) {
        doDefault(expr);
    }

    @Override
    public void visit(DataOr expr) {
        doDefault(expr);
    }

    @Override
    public void visit(DataOneOf expr) {
        doDefault(expr);
    }
}
