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
import javax.annotation.Nonnull;

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

/**
 * expression visitor
 * 
 * @param <O>
 *        return type
 */
public interface DLExpressionVisitorEx<O> {

    // concept expressions
    /**
     * @param expr
     *        ConceptTop to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptTop expr);

    /**
     * @param expr
     *        ConceptBottom to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptBottom expr);

    /**
     * @param expr
     *        ConceptName to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptName expr);

    /**
     * @param expr
     *        ConceptNot to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptNot expr);

    /**
     * @param expr
     *        ConceptAnd to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptAnd expr);

    /**
     * @param expr
     *        ConceptOr to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptOr expr);

    /**
     * @param expr
     *        ConceptOneOf to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptOneOf<?> expr);

    /**
     * @param expr
     *        ConceptObjectSelf to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptObjectSelf expr);

    /**
     * @param expr
     *        ConceptObjectValue to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptObjectValue expr);

    /**
     * @param expr
     *        ConceptObjectExists to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptObjectExists expr);

    /**
     * @param expr
     *        ConceptObjectForall to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptObjectForall expr);

    /**
     * @param expr
     *        ConceptObjectMinCardinality to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptObjectMinCardinality expr);

    /**
     * @param expr
     *        ConceptObjectMaxCardinality to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptObjectMaxCardinality expr);

    /**
     * @param expr
     *        ConceptObjectExactCardinality to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptObjectExactCardinality expr);

    /**
     * @param expr
     *        ConceptDataValue to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptDataValue expr);

    /**
     * @param expr
     *        ConceptDataExists to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptDataExists expr);

    /**
     * @param expr
     *        ConceptDataForall to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptDataForall expr);

    /**
     * @param expr
     *        ConceptDataMinCardinality to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptDataMinCardinality expr);

    /**
     * @param expr
     *        ConceptDataMaxCardinality to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptDataMaxCardinality expr);

    /**
     * @param expr
     *        ConceptDataExactCardinality to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ConceptDataExactCardinality expr);

    // individual expressions
    /**
     * @param expr
     *        IndividualName to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull IndividualName expr);

    // object role expressions
    /**
     * @param expr
     *        ObjectRoleTop to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ObjectRoleTop expr);

    /**
     * @param expr
     *        ObjectRoleBottom to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ObjectRoleBottom expr);

    /**
     * @param expr
     *        ObjectRoleName to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ObjectRoleName expr);

    /**
     * @param expr
     *        ObjectRoleInverse to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ObjectRoleInverse expr);

    /**
     * @param expr
     *        ObjectRoleChain to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ObjectRoleChain expr);

    /**
     * @param expr
     *        ObjectRoleProjectionFrom to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ObjectRoleProjectionFrom expr);

    /**
     * @param expr
     *        ObjectRoleProjectionInto to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull ObjectRoleProjectionInto expr);

    // data role expressions
    /**
     * @param expr
     *        DataRoleTop to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull DataRoleTop expr);

    /**
     * @param expr
     *        DataRoleBottom to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull DataRoleBottom expr);

    /**
     * @param expr
     *        DataRoleName to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull DataRoleName expr);

    // data expressions
    /**
     * @param expr
     *        DataTop to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull DataTop expr);

    /**
     * @param expr
     *        DataBottom to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull DataBottom expr);

    /**
     * @param expr
     *        Literal to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull Literal<?> expr);

    /**
     * @param expr
     *        Datatype to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull Datatype<?> expr);

    /**
     * @param expr
     *        DatatypeExpression to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull DatatypeExpression<?> expr);

    /**
     * @param expr
     *        DataNot to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull DataNot expr);

    /**
     * @param expr
     *        DataAnd to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull DataAnd expr);

    /**
     * @param expr
     *        DataOr to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull DataOr expr);

    /**
     * @param expr
     *        DataOneOf to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull DataOneOf expr);
}
