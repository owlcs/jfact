package uk.ac.manchester.cs.jfact.visitors;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
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

public interface DLExpressionVisitorEx<O> {
    // concept expressions
    O visit(ConceptTop expr);

    O visit(ConceptBottom expr);

    O visit(ConceptName expr);

    O visit(ConceptNot expr);

    O visit(ConceptAnd expr);

    O visit(ConceptOr expr);

    O visit(ConceptOneOf expr);

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
