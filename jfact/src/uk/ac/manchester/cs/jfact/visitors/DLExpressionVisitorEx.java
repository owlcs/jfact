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

@SuppressWarnings("javadoc")
public interface DLExpressionVisitorEx<O> {
    // concept expressions
    O visit(ConceptTop expr);

    O visit(ConceptBottom expr);

    O visit(ConceptName expr);

    O visit(ConceptNot expr);

    O visit(ConceptAnd expr);

    O visit(ConceptOr expr);

    O visit(ConceptOneOf<?> expr);

    O visit(ConceptObjectSelf expr);

    O visit(ConceptObjectValue expr);

    O visit(ConceptObjectExists expr);

    O visit(ConceptObjectForall expr);

    O visit(ConceptObjectMinCardinality expr);

    O visit(ConceptObjectMaxCardinality expr);

    O visit(ConceptObjectExactCardinality expr);

    O visit(ConceptDataValue expr);

    O visit(ConceptDataExists expr);

    O visit(ConceptDataForall expr);

    O visit(ConceptDataMinCardinality expr);

    O visit(ConceptDataMaxCardinality expr);

    O visit(ConceptDataExactCardinality expr);

    // individual expressions
    O visit(IndividualName expr);

    // object role expressions
    O visit(ObjectRoleTop expr);

    O visit(ObjectRoleBottom expr);

    O visit(ObjectRoleName expr);

    O visit(ObjectRoleInverse expr);

    O visit(ObjectRoleChain expr);

    O visit(ObjectRoleProjectionFrom expr);

    O visit(ObjectRoleProjectionInto expr);

    // data role expressions
    O visit(DataRoleTop expr);

    O visit(DataRoleBottom expr);

    O visit(DataRoleName expr);

    // data expressions
    O visit(DataTop expr);

    O visit(DataBottom expr);

    O visit(Literal<?> expr);

    O visit(Datatype<?> expr);

    O visit(DataNot expr);

    O visit(DataAnd expr);

    O visit(DataOr expr);

    O visit(DataOneOf expr);
}
