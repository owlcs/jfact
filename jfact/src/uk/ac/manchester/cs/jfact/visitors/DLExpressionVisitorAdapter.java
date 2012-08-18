package uk.ac.manchester.cs.jfact.visitors;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.*;

@SuppressWarnings({ "unused", "javadoc" })
public abstract class DLExpressionVisitorAdapter implements DLExpressionVisitor {
    public void visit(ConceptTop expr) {}

    public void visit(ConceptBottom expr) {}

    public void visit(ConceptName expr) {}

    public void visit(ConceptNot expr) {}

    public void visit(ConceptAnd expr) {}

    public void visit(ConceptOr expr) {}

    public void visit(ConceptOneOf expr) {}

    public void visit(ConceptObjectSelf expr) {}

    public void visit(ConceptObjectValue expr) {}

    public void visit(ConceptObjectExists expr) {}

    public void visit(ConceptObjectForall expr) {}

    public void visit(ConceptObjectMinCardinality expr) {}

    public void visit(ConceptObjectMaxCardinality expr) {}

    public void visit(ConceptObjectExactCardinality expr) {}

    public void visit(ConceptDataValue expr) {}

    public void visit(ConceptDataExists expr) {}

    public void visit(ConceptDataForall expr) {}

    public void visit(ConceptDataMinCardinality expr) {}

    public void visit(ConceptDataMaxCardinality expr) {}

    public void visit(ConceptDataExactCardinality expr) {}

    public void visit(IndividualName expr) {}

    public void visit(ObjectRoleTop expr) {}

    public void visit(ObjectRoleBottom expr) {}

    public void visit(ObjectRoleName expr) {}

    public void visit(ObjectRoleInverse expr) {}

    public void visit(ObjectRoleChain expr) {}

    public void visit(ObjectRoleProjectionFrom expr) {}

    public void visit(ObjectRoleProjectionInto expr) {}

    public void visit(DataRoleTop expr) {}

    public void visit(DataRoleBottom expr) {}

    public void visit(DataRoleName expr) {}

    public void visit(DataTop expr) {}

    public void visit(DataBottom expr) {}

    public void visit(Datatype<?> expr) {}

    public void visit(Literal<?> expr) {}

    public void visit(DataNot expr) {}

    public void visit(DataAnd expr) {}

    public void visit(DataOr expr) {}

    public void visit(DataOneOf expr) {}
}
