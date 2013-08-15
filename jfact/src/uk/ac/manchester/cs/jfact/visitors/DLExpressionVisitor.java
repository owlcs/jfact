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
    void visit(ConceptTop expr);

    void visit(ConceptBottom expr);

    void visit(ConceptName expr);

    void visit(ConceptNot expr);

    void visit(ConceptAnd expr);

    void visit(ConceptOr expr);

    void visit(ConceptOneOf<?> expr);

    void visit(ConceptObjectSelf expr);

    void visit(ConceptObjectValue expr);

    void visit(ConceptObjectExists expr);

    void visit(ConceptObjectForall expr);

    void visit(ConceptObjectMinCardinality expr);

    void visit(ConceptObjectMaxCardinality expr);

    void visit(ConceptObjectExactCardinality expr);

    void visit(ConceptDataValue expr);

    void visit(ConceptDataExists expr);

    void visit(ConceptDataForall expr);

    void visit(ConceptDataMinCardinality expr);

    void visit(ConceptDataMaxCardinality expr);

    void visit(ConceptDataExactCardinality expr);

    // individual expressions
    void visit(IndividualName expr);

    // object role expressions
    void visit(ObjectRoleTop expr);

    void visit(ObjectRoleBottom expr);

    void visit(ObjectRoleName expr);

    void visit(ObjectRoleInverse expr);

    void visit(ObjectRoleChain expr);

    void visit(ObjectRoleProjectionFrom expr);

    void visit(ObjectRoleProjectionInto expr);

    // data role expressions
    void visit(DataRoleTop expr);

    void visit(DataRoleBottom expr);

    void visit(DataRoleName expr);

    // data expressions
    void visit(DataTop expr);

    void visit(DataBottom expr);

    void visit(Literal<?> expr);

    void visit(Datatype<?> expr);

    void visit(DataNot expr);

    void visit(DataAnd expr);

    void visit(DataOr expr);

    void visit(DataOneOf expr);
}
