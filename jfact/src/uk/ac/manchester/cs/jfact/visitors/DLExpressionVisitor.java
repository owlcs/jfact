package uk.ac.manchester.cs.jfact.visitors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import conformance.PortedFrom;

@SuppressWarnings("javadoc")
@PortedFrom(file = "tDLExpression.h", name = "DLExpressionVisitor")
public interface DLExpressionVisitor {
    // concept expressions
    public void visit(ConceptTop expr);

    public void visit(ConceptBottom expr);

    public void visit(ConceptName expr);

    public void visit(ConceptNot expr);

    public void visit(ConceptAnd expr);

    public void visit(ConceptOr expr);

    public void visit(ConceptOneOf expr);

    public void visit(ConceptObjectSelf expr);

    public void visit(ConceptObjectValue expr);

    public void visit(ConceptObjectExists expr);

    public void visit(ConceptObjectForall expr);

    public void visit(ConceptObjectMinCardinality expr);

    public void visit(ConceptObjectMaxCardinality expr);

    public void visit(ConceptObjectExactCardinality expr);

    public void visit(ConceptDataValue expr);

    public void visit(ConceptDataExists expr);

    public void visit(ConceptDataForall expr);

    public void visit(ConceptDataMinCardinality expr);

    public void visit(ConceptDataMaxCardinality expr);

    public void visit(ConceptDataExactCardinality expr);

    // individual expressions
    public void visit(IndividualName expr);

    // object role expressions
    public void visit(ObjectRoleTop expr);

    public void visit(ObjectRoleBottom expr);

    public void visit(ObjectRoleName expr);

    public void visit(ObjectRoleInverse expr);

    public void visit(ObjectRoleChain expr);

    public void visit(ObjectRoleProjectionFrom expr);

    public void visit(ObjectRoleProjectionInto expr);

    // data role expressions
    public void visit(DataRoleTop expr);

    public void visit(DataRoleBottom expr);

    public void visit(DataRoleName expr);

    // data expressions
    public void visit(DataTop expr);

    public void visit(DataBottom expr);

    public void visit(Literal<?> expr);

    public void visit(Datatype<?> expr);

    public void visit(DataNot expr);

    public void visit(DataAnd expr);

    public void visit(DataOr expr);

    public void visit(DataOneOf expr);
}
