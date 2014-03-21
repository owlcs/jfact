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
import conformance.PortedFrom;

/** expression visitor */
@PortedFrom(file = "tDLExpression.h", name = "DLExpressionVisitor")
public interface DLExpressionVisitor {

    // concept expressions
    /**
     * @param expr
     *        ConceptTop to visit
     */
    void visit(ConceptTop expr);

    /**
     * @param expr
     *        ConceptBottom to visit
     */
    void visit(ConceptBottom expr);

    /**
     * @param expr
     *        ConceptName to visit
     */
    void visit(ConceptName expr);

    /**
     * @param expr
     *        ConceptNot to visit
     */
    void visit(ConceptNot expr);

    /**
     * @param expr
     *        ConceptAnd to visit
     */
    void visit(ConceptAnd expr);

    /**
     * @param expr
     *        ConceptOr to visit
     */
    void visit(ConceptOr expr);

    /**
     * @param expr
     *        ConceptOneOf to visit
     */
    void visit(ConceptOneOf<?> expr);

    /**
     * @param expr
     *        ConceptObjectSelf to visit
     */
    void visit(ConceptObjectSelf expr);

    /**
     * @param expr
     *        ConceptObjectValue to visit
     */
    void visit(ConceptObjectValue expr);

    /**
     * @param expr
     *        ConceptObjectExists to visit
     */
    void visit(ConceptObjectExists expr);

    /**
     * @param expr
     *        ConceptObjectForall to visit
     */
    void visit(ConceptObjectForall expr);

    /**
     * @param expr
     *        ConceptObjectMinCardinality to visit
     */
    void visit(ConceptObjectMinCardinality expr);

    /**
     * @param expr
     *        ConceptObjectMaxCardinality to visit
     */
    void visit(ConceptObjectMaxCardinality expr);

    /**
     * @param expr
     *        ConceptObjectExactCardinality to visit
     */
    void visit(ConceptObjectExactCardinality expr);

    /**
     * @param expr
     *        ConceptDataValue to visit
     */
    void visit(ConceptDataValue expr);

    /**
     * @param expr
     *        ConceptDataExists to visit
     */
    void visit(ConceptDataExists expr);

    /**
     * @param expr
     *        ConceptDataForall to visit
     */
    void visit(ConceptDataForall expr);

    /**
     * @param expr
     *        ConceptDataMinCardinality to visit
     */
    void visit(ConceptDataMinCardinality expr);

    /**
     * @param expr
     *        ConceptDataMaxCardinality to visit
     */
    void visit(ConceptDataMaxCardinality expr);

    /**
     * @param expr
     *        ConceptDataExactCardinality to visit
     */
    void visit(ConceptDataExactCardinality expr);

    // individual expressions
    /**
     * @param expr
     *        IndividualName to visit
     */
    void visit(IndividualName expr);

    // object role expressions
    /**
     * @param expr
     *        ObjectRoleTop to visit
     */
    void visit(ObjectRoleTop expr);

    /**
     * @param expr
     *        ObjectRoleBottom to visit
     */
    void visit(ObjectRoleBottom expr);

    /**
     * @param expr
     *        ObjectRoleName to visit
     */
    void visit(ObjectRoleName expr);

    /**
     * @param expr
     *        ObjectRoleInverse to visit
     */
    void visit(ObjectRoleInverse expr);

    /**
     * @param expr
     *        ObjectRoleChain to visit
     */
    void visit(ObjectRoleChain expr);

    /**
     * @param expr
     *        ObjectRoleProjectionFrom to visit
     */
    void visit(ObjectRoleProjectionFrom expr);

    /**
     * @param expr
     *        ObjectRoleProjectionInto to visit
     */
    void visit(ObjectRoleProjectionInto expr);

    // data role expressions
    /**
     * @param expr
     *        DataRoleTop to visit
     */
    void visit(DataRoleTop expr);

    /**
     * @param expr
     *        DataRoleBottom to visit
     */
    void visit(DataRoleBottom expr);

    /**
     * @param expr
     *        DataRoleName to visit
     */
    void visit(DataRoleName expr);

    // data expressions
    /**
     * @param expr
     *        DataTop to visit
     */
    void visit(DataTop expr);

    /**
     * @param expr
     *        DataBottom to visit
     */
    void visit(DataBottom expr);

    /**
     * @param expr
     *        Literal to visit
     */
    void visit(Literal<?> expr);

    /**
     * @param expr
     *        Datatype to visit
     */
    void visit(Datatype<?> expr);

    /**
     * @param expr
     *        DatatypeExpression to visit
     */
    void visit(DatatypeExpression<?> expr);

    /**
     * @param expr
     *        DataNot to visit
     */
    void visit(DataNot expr);

    /**
     * @param expr
     *        DataAnd to visit
     */
    void visit(DataAnd expr);

    /**
     * @param expr
     *        DataOr to visit
     */
    void visit(DataOr expr);

    /**
     * @param expr
     *        DataOneOf to visit
     */
    void visit(DataOneOf expr);
}
