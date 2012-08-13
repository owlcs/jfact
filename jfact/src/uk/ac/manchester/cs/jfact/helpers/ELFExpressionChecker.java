package uk.ac.manchester.cs.jfact.helpers;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;

public class ELFExpressionChecker implements DLExpressionVisitor {
    boolean value;

    // / get DLTree corresponding to an expression EXPR
    boolean v(Expression expr) {
        expr.accept(this);
        return value;
    }

    // concept expressions
    public void visit(ConceptTop expr) {
        value = true;
    }

    public void visit(ConceptBottom expr) {
        value = true;
    }

    public void visit(ConceptName expr) {
        value = true;
    }

    public void visit(ConceptNot expr) {
        value = false;
    }

    public void visit(ConceptAnd expr) {
        value = false;
        for (Expression p : expr.getArguments()) {
            if (!v(p)) {
                return;
            }
        }
        value = true;
    }

    public void visit(ConceptOr expr) {
        value = false;
    }

    public void visit(ConceptOneOf expr) {
        value = false;
    }

    public void visit(ConceptObjectSelf expr) {
        value = false;
    }

    public void visit(ConceptObjectValue expr) {
        value = false;
    }

    public void visit(ConceptObjectExists expr) {
        value = false;
        // check role
        if (!v(expr.getOR())) {
            return;
        }
        // check concept
        v(expr.getConcept());
    }

    public void visit(ConceptObjectForall expr) {
        value = false;
    }

    public void visit(ConceptObjectMinCardinality expr) {
        value = false;
    }

    public void visit(ConceptObjectMaxCardinality expr) {
        value = false;
    }

    public void visit(ConceptObjectExactCardinality expr) {
        value = false;
    }

    public void visit(ConceptDataValue expr) {
        value = false;
    }

    public void visit(ConceptDataExists expr) {
        value = false;
    }

    public void visit(ConceptDataForall expr) {
        value = false;
    }

    public void visit(ConceptDataMinCardinality expr) {
        value = false;
    }

    public void visit(ConceptDataMaxCardinality expr) {
        value = false;
    }

    public void visit(ConceptDataExactCardinality expr) {
        value = false;
    }

    // individual expressions
    public void visit(IndividualName expr) {
        value = false;
    }

    // object role expressions
    public void visit(ObjectRoleTop expr) {
        value = false;
    }

    public void visit(ObjectRoleBottom expr) {
        value = false;
    }

    public void visit(ObjectRoleName expr) {
        value = true;
    }

    public void visit(ObjectRoleInverse expr) {
        value = false;
    }

    public void visit(ObjectRoleChain expr) {
        value = false;
        for (Expression p : expr.getArguments()) {
            if (!v(p)) {
                return;
            }
        }
        value = true;
    }

    public void visit(ObjectRoleProjectionFrom expr) {
        value = false;
    }

    public void visit(ObjectRoleProjectionInto expr) {
        value = false;
    }

    // data role expressions
    public void visit(DataRoleTop expr) {
        value = false;
    }

    public void visit(DataRoleBottom expr) {
        value = false;
    }

    public void visit(DataRoleName expr) {
        value = false;
    }

    // data expressions
    public void visit(DataTop expr) {
        value = false;
    }

    public void visit(DataBottom expr) {
        value = false;
    }

    public void visit(DataNot expr) {
        value = false;
    }

    public void visit(DataAnd expr) {
        value = false;
    }

    public void visit(DataOr expr) {
        value = false;
    }

    public void visit(DataOneOf expr) {
        value = false;
    }

    public void visit(Literal<?> expr) {
        value = false;
    }

    public void visit(Datatype<?> expr) {
        value = false;
    }
}
