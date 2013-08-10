package uk.ac.manchester.cs.jfact.visitors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.*;

@SuppressWarnings("javadoc")
public abstract class DLExpressionVisitorAdapter implements DLExpressionVisitor,
        Serializable {
    private static final long serialVersionUID = 11000L;
    @Override
    public void visit(ConceptTop expr) {}

    @Override
    public void visit(ConceptBottom expr) {}

    @Override
    public void visit(ConceptName expr) {}

    @Override
    public void visit(ConceptNot expr) {}

    @Override
    public void visit(ConceptAnd expr) {}

    @Override
    public void visit(ConceptOr expr) {}

    @Override
    public void visit(ConceptOneOf<?> expr) {}

    @Override
    public void visit(ConceptObjectSelf expr) {}

    @Override
    public void visit(ConceptObjectValue expr) {}

    @Override
    public void visit(ConceptObjectExists expr) {}

    @Override
    public void visit(ConceptObjectForall expr) {}

    @Override
    public void visit(ConceptObjectMinCardinality expr) {}

    @Override
    public void visit(ConceptObjectMaxCardinality expr) {}

    @Override
    public void visit(ConceptObjectExactCardinality expr) {}

    @Override
    public void visit(ConceptDataValue expr) {}

    @Override
    public void visit(ConceptDataExists expr) {}

    @Override
    public void visit(ConceptDataForall expr) {}

    @Override
    public void visit(ConceptDataMinCardinality expr) {}

    @Override
    public void visit(ConceptDataMaxCardinality expr) {}

    @Override
    public void visit(ConceptDataExactCardinality expr) {}

    @Override
    public void visit(IndividualName expr) {}

    @Override
    public void visit(ObjectRoleTop expr) {}

    @Override
    public void visit(ObjectRoleBottom expr) {}

    @Override
    public void visit(ObjectRoleName expr) {}

    @Override
    public void visit(ObjectRoleInverse expr) {}

    @Override
    public void visit(ObjectRoleChain expr) {}

    @Override
    public void visit(ObjectRoleProjectionFrom expr) {}

    @Override
    public void visit(ObjectRoleProjectionInto expr) {}

    @Override
    public void visit(DataRoleTop expr) {}

    @Override
    public void visit(DataRoleBottom expr) {}

    @Override
    public void visit(DataRoleName expr) {}

    @Override
    public void visit(DataTop expr) {}

    @Override
    public void visit(DataBottom expr) {}

    @Override
    public void visit(Datatype<?> expr) {}

    @Override
    public void visit(Literal<?> expr) {}

    @Override
    public void visit(DataNot expr) {}

    @Override
    public void visit(DataAnd expr) {}

    @Override
    public void visit(DataOr expr) {}

    @Override
    public void visit(DataOneOf expr) {}
}
