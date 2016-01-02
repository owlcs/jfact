package uk.ac.manchester.cs.jfact.visitors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import javax.annotation.Nonnull;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
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

/**
 * adapter for expression visitor
 * 
 * @param <A>
 *        return type
 */
public abstract class DLExpressionVisitorExAdapter<A> implements
        DLExpressionVisitorEx<A>, Serializable {


    @Nonnull
    private A defaultValue;

    /**
     * @param a
     *        return value
     */
    public DLExpressionVisitorExAdapter(A a) {
        defaultValue = a;
    }

    protected A doDefault(@SuppressWarnings("unused") Expression e) {
        return defaultValue;
    }

    @Override
    public A visit(ConceptTop expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptBottom expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptName expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptNot expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptAnd expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptOr expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptOneOf<?> expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptObjectSelf expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptObjectValue expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptObjectExists expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptObjectForall expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptObjectMinCardinality expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptObjectMaxCardinality expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptObjectExactCardinality expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptDataValue expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptDataExists expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptDataForall expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptDataMinCardinality expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptDataMaxCardinality expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ConceptDataExactCardinality expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(IndividualName expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ObjectRoleTop expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ObjectRoleBottom expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ObjectRoleName expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ObjectRoleInverse expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ObjectRoleChain expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ObjectRoleProjectionFrom expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(ObjectRoleProjectionInto expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(DataRoleTop expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(DataRoleBottom expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(DataRoleName expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(DataTop expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(DataBottom expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(Datatype<?> expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(Literal<?> expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(DataNot expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(DataAnd expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(DataOr expr) {
        return doDefault(expr);
    }

    @Override
    public A visit(DataOneOf expr) {
        return doDefault(expr);
    }
}
