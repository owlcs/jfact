package uk.ac.manchester.cs.jfact.split;

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
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptArg;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleArg;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NAryExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleArg;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;

/// update the signature by adding all signature elements from the expression
class TExpressionSignatureUpdater implements DLExpressionVisitor {
    /// Signature to be filled
    TSignature sig;

    /// helper for concept arguments
    void vC(final ConceptArg expr) {
        expr.getConcept().accept(this);
    }

    /// helper for individual arguments
    void vI(final IndividualExpression expr) {
        // should no longer be needed: IndividualNames are NamedEntities themselves
        if (expr instanceof NamedEntity) {
            sig.add((NamedEntity) expr);
        }
    }

    /// helper for object role arguments
    void vOR(final ObjectRoleArg expr) {
        expr.getOR().accept(this);
    }

    /// helper for object role arguments
    void vDR(final DataRoleArg expr) {
        expr.getDataRoleExpression().accept(this);
    }

    /// helper for the named entity
    void vE(final NamedEntity e) {
        sig.add(e);
    }

    /// array helper
    void processArray(final NAryExpression<? extends Expression> expr) {
        for (Expression p : expr.getArguments()) {
            p.accept(this);
        }
    }

    //TODO check whether it must copy or change
    public TExpressionSignatureUpdater(final TSignature s) {
        sig = new TSignature(s);
    }

    // concept expressions
    public void visit(final ConceptTop expr) {}

    public void visit(final ConceptBottom expr) {}

    public void visit(final ConceptName expr) {
        vE(expr);
    }

    public void visit(final ConceptNot expr) {
        vC(expr);
    }

    public void visit(final ConceptAnd expr) {
        processArray(expr);
    }

    public void visit(final ConceptOr expr) {
        processArray(expr);
    }

    public void visit(final ConceptOneOf expr) {
        processArray(expr);
    }

    public void visit(final ConceptObjectSelf expr) {
        vOR(expr);
    }

    public void visit(final ConceptObjectValue expr) {
        vOR(expr);
        vI(expr.getI());
    }

    public void visit(final ConceptObjectExists expr) {
        vOR(expr);
        vC(expr);
    }

    public void visit(final ConceptObjectForall expr) {
        vOR(expr);
        vC(expr);
    }

    public void visit(final ConceptObjectMinCardinality expr) {
        vOR(expr);
        vC(expr);
    }

    public void visit(final ConceptObjectMaxCardinality expr) {
        vOR(expr);
        vC(expr);
    }

    public void visit(final ConceptObjectExactCardinality expr) {
        vOR(expr);
        vC(expr);
    }

    public void visit(final ConceptDataValue expr) {
        vDR(expr);
    }

    public void visit(final ConceptDataExists expr) {
        vDR(expr);
    }

    public void visit(final ConceptDataForall expr) {
        vDR(expr);
    }

    public void visit(final ConceptDataMinCardinality expr) {
        vDR(expr);
    }

    public void visit(final ConceptDataMaxCardinality expr) {
        vDR(expr);
    }

    public void visit(final ConceptDataExactCardinality expr) {
        vDR(expr);
    }

    // individual expressions
    public void visit(final IndividualName expr) {
        vE(expr);
    }

    // object role expressions
    public void visit(final ObjectRoleTop expr) {}

    public void visit(final ObjectRoleBottom expr) {}

    public void visit(final ObjectRoleName expr) {
        vE(expr);
    }

    public void visit(final ObjectRoleInverse expr) {
        vOR(expr);
    }

    public void visit(final ObjectRoleChain expr) {
        processArray(expr);
    }

    public void visit(final ObjectRoleProjectionFrom expr) {
        vOR(expr);
        vC(expr);
    }

    public void visit(final ObjectRoleProjectionInto expr) {
        vOR(expr);
        vC(expr);
    }

    // data role expressions
    public void visit(final DataRoleTop expr) {}

    public void visit(final DataRoleBottom expr) {}

    public void visit(final DataRoleName expr) {
        vE(expr);
    }

    // data expressions
    public void visit(final DataTop expr) {}

    public void visit(final DataBottom expr) {}

    public void visit(final Datatype<?> expr) {}

    public void visit(final Literal<?> expr) {}

    public void visit(final DataNot expr) {}

    public void visit(final DataAnd expr) {}

    public void visit(final DataOr expr) {}

    public void visit(final DataOneOf expr) {}
}
