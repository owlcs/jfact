package uk.ac.manchester.cs.jfact.visitors;

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
    O visit(ConceptTop expr);

    /**
     * @param expr
     *        ConceptBottom to visit
     * @return visitor value
     */
    O visit(ConceptBottom expr);

    /**
     * @param expr
     *        ConceptName to visit
     * @return visitor value
     */
    O visit(ConceptName expr);

    /**
     * @param expr
     *        ConceptNot to visit
     * @return visitor value
     */
    O visit(ConceptNot expr);

    /**
     * @param expr
     *        ConceptAnd to visit
     * @return visitor value
     */
    O visit(ConceptAnd expr);

    /**
     * @param expr
     *        ConceptOr to visit
     * @return visitor value
     */
    O visit(ConceptOr expr);

    /**
     * @param expr
     *        ConceptOneOf to visit
     * @return visitor value
     */
    O visit(ConceptOneOf<?> expr);

    /**
     * @param expr
     *        ConceptObjectSelf to visit
     * @return visitor value
     */
    O visit(ConceptObjectSelf expr);

    /**
     * @param expr
     *        ConceptObjectValue to visit
     * @return visitor value
     */
    O visit(ConceptObjectValue expr);

    /**
     * @param expr
     *        ConceptObjectExists to visit
     * @return visitor value
     */
    O visit(ConceptObjectExists expr);

    /**
     * @param expr
     *        ConceptObjectForall to visit
     * @return visitor value
     */
    O visit(ConceptObjectForall expr);

    /**
     * @param expr
     *        ConceptObjectMinCardinality to visit
     * @return visitor value
     */
    O visit(ConceptObjectMinCardinality expr);

    /**
     * @param expr
     *        ConceptObjectMaxCardinality to visit
     * @return visitor value
     */
    O visit(ConceptObjectMaxCardinality expr);

    /**
     * @param expr
     *        ConceptObjectExactCardinality to visit
     * @return visitor value
     */
    O visit(ConceptObjectExactCardinality expr);

    /**
     * @param expr
     *        ConceptDataValue to visit
     * @return visitor value
     */
    O visit(ConceptDataValue expr);

    /**
     * @param expr
     *        ConceptDataExists to visit
     * @return visitor value
     */
    O visit(ConceptDataExists expr);

    /**
     * @param expr
     *        ConceptDataForall to visit
     * @return visitor value
     */
    O visit(ConceptDataForall expr);

    /**
     * @param expr
     *        ConceptDataMinCardinality to visit
     * @return visitor value
     */
    O visit(ConceptDataMinCardinality expr);

    /**
     * @param expr
     *        ConceptDataMaxCardinality to visit
     * @return visitor value
     */
    O visit(ConceptDataMaxCardinality expr);

    /**
     * @param expr
     *        ConceptDataExactCardinality to visit
     * @return visitor value
     */
    O visit(ConceptDataExactCardinality expr);

    // individual expressions
    /**
     * @param expr
     *        IndividualName to visit
     * @return visitor value
     */
    O visit(IndividualName expr);

    // object role expressions
    /**
     * @param expr
     *        ObjectRoleTop to visit
     * @return visitor value
     */
    O visit(ObjectRoleTop expr);

    /**
     * @param expr
     *        ObjectRoleBottom to visit
     * @return visitor value
     */
    O visit(ObjectRoleBottom expr);

    /**
     * @param expr
     *        ObjectRoleName to visit
     * @return visitor value
     */
    O visit(ObjectRoleName expr);

    /**
     * @param expr
     *        ObjectRoleInverse to visit
     * @return visitor value
     */
    O visit(ObjectRoleInverse expr);

    /**
     * @param expr
     *        ObjectRoleChain to visit
     * @return visitor value
     */
    O visit(ObjectRoleChain expr);

    /**
     * @param expr
     *        ObjectRoleProjectionFrom to visit
     * @return visitor value
     */
    O visit(ObjectRoleProjectionFrom expr);

    /**
     * @param expr
     *        ObjectRoleProjectionInto to visit
     * @return visitor value
     */
    O visit(ObjectRoleProjectionInto expr);

    // data role expressions
    /**
     * @param expr
     *        DataRoleTop to visit
     * @return visitor value
     */
    O visit(DataRoleTop expr);

    /**
     * @param expr
     *        DataRoleBottom to visit
     * @return visitor value
     */
    O visit(DataRoleBottom expr);

    /**
     * @param expr
     *        DataRoleName to visit
     * @return visitor value
     */
    O visit(DataRoleName expr);

    // data expressions
    /**
     * @param expr
     *        DataTop to visit
     * @return visitor value
     */
    O visit(DataTop expr);

    /**
     * @param expr
     *        DataBottom to visit
     * @return visitor value
     */
    O visit(DataBottom expr);

    /**
     * @param expr
     *        Literal to visit
     * @return visitor value
     */
    O visit(Literal<?> expr);

    /**
     * @param expr
     *        Datatype to visit
     * @return visitor value
     */
    O visit(Datatype<?> expr);

    /**
     * @param expr
     *        DatatypeExpression to visit
     * @return visitor value
     */
    O visit(DatatypeExpression<?> expr);

    /**
     * @param expr
     *        DataNot to visit
     * @return visitor value
     */
    O visit(DataNot expr);

    /**
     * @param expr
     *        DataAnd to visit
     * @return visitor value
     */
    O visit(DataAnd expr);

    /**
     * @param expr
     *        DataOr to visit
     * @return visitor value
     */
    O visit(DataOr expr);

    /**
     * @param expr
     *        DataOneOf to visit
     * @return visitor value
     */
    O visit(DataOneOf expr);
}
