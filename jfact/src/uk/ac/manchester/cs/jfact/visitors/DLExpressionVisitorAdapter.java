package uk.ac.manchester.cs.jfact.visitors;

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

@SuppressWarnings("unused")
public abstract class DLExpressionVisitorAdapter implements DLExpressionVisitor {
    public void visit(final ConceptTop expr) {}

    public void visit(final ConceptBottom expr) {}

    public void visit(final ConceptName expr) {}

    public void visit(final ConceptNot expr) {}

    public void visit(final ConceptAnd expr) {}

    public void visit(final ConceptOr expr) {}

    public void visit(final ConceptOneOf expr) {}

    public void visit(final ConceptObjectSelf expr) {}

    public void visit(final ConceptObjectValue expr) {}

    public void visit(final ConceptObjectExists expr) {}

    public void visit(final ConceptObjectForall expr) {}

    public void visit(final ConceptObjectMinCardinality expr) {}

    public void visit(final ConceptObjectMaxCardinality expr) {}

    public void visit(final ConceptObjectExactCardinality expr) {}

    public void visit(final ConceptDataValue expr) {}

    public void visit(final ConceptDataExists expr) {}

    public void visit(final ConceptDataForall expr) {}

    public void visit(final ConceptDataMinCardinality expr) {}

    public void visit(final ConceptDataMaxCardinality expr) {}

    public void visit(final ConceptDataExactCardinality expr) {}

    public void visit(final IndividualName expr) {}

    public void visit(final ObjectRoleTop expr) {}

    public void visit(final ObjectRoleBottom expr) {}

    public void visit(final ObjectRoleName expr) {}

    public void visit(final ObjectRoleInverse expr) {}

    public void visit(final ObjectRoleChain expr) {}

    public void visit(final ObjectRoleProjectionFrom expr) {}

    public void visit(final ObjectRoleProjectionInto expr) {}

    public void visit(final DataRoleTop expr) {}

    public void visit(final DataRoleBottom expr) {}

    public void visit(final DataRoleName expr) {}

    public void visit(final DataTop expr) {}

    public void visit(final DataBottom expr) {}

    public void visit(final Datatype<?> expr) {}

    public void visit(final Literal<?> expr) {}

    public void visit(final DataNot expr) {}

    public void visit(final DataAnd expr) {}

    public void visit(final DataOr expr) {}

    public void visit(final DataOneOf expr) {}
}
