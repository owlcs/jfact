package uk.ac.manchester.cs.jfact.visitors;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.*;

@SuppressWarnings("unused")
public abstract class DLExpressionVisitorExAdapter<A> implements DLExpressionVisitorEx<A> {
    public A visit(ConceptTop expr) {
        return null;
    }

    public A visit(ConceptBottom expr) {
        return null;
    }

    public A visit(ConceptName expr) {
        return null;
    }

    public A visit(ConceptNot expr) {
        return null;
    }

    public A visit(ConceptAnd expr) {
        return null;
    }

    public A visit(ConceptOr expr) {
        return null;
    }

    public A visit(ConceptOneOf expr) {
        return null;
    }

    public A visit(ConceptObjectSelf expr) {
        return null;
    }

    public A visit(ConceptObjectValue expr) {
        return null;
    }

    public A visit(ConceptObjectExists expr) {
        return null;
    }

    public A visit(ConceptObjectForall expr) {
        return null;
    }

    public A visit(ConceptObjectMinCardinality expr) {
        return null;
    }

    public A visit(ConceptObjectMaxCardinality expr) {
        return null;
    }

    public A visit(ConceptObjectExactCardinality expr) {
        return null;
    }

    public A visit(ConceptDataValue expr) {
        return null;
    }

    public A visit(ConceptDataExists expr) {
        return null;
    }

    public A visit(ConceptDataForall expr) {
        return null;
    }

    public A visit(ConceptDataMinCardinality expr) {
        return null;
    }

    public A visit(ConceptDataMaxCardinality expr) {
        return null;
    }

    public A visit(ConceptDataExactCardinality expr) {
        return null;
    }

    public A visit(IndividualName expr) {
        return null;
    }

    public A visit(ObjectRoleTop expr) {
        return null;
    }

    public A visit(ObjectRoleBottom expr) {
        return null;
    }

    public A visit(ObjectRoleName expr) {
        return null;
    }

    public A visit(ObjectRoleInverse expr) {
        return null;
    }

    public A visit(ObjectRoleChain expr) {
        return null;
    }

    public A visit(ObjectRoleProjectionFrom expr) {
        return null;
    }

    public A visit(ObjectRoleProjectionInto expr) {
        return null;
    }

    public A visit(DataRoleTop expr) {
        return null;
    }

    public A visit(DataRoleBottom expr) {
        return null;
    }

    public A visit(DataRoleName expr) {
        return null;
    }

    public A visit(DataTop expr) {
        return null;
    }

    public A visit(DataBottom expr) {
        return null;
    }

    public A visit(Datatype<?> expr) {
        return null;
    }

    public A visit(Literal<?> expr) {
        return null;
    }

    public A visit(DataNot expr) {
        return null;
    }

    public A visit(DataAnd expr) {
        return null;
    }

    public A visit(DataOr expr) {
        return null;
    }

    public A visit(DataOneOf expr) {
        return null;
    }
}
