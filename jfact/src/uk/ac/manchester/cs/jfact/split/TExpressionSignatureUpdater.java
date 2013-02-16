package uk.ac.manchester.cs.jfact.split;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import conformance.PortedFrom;

/** update the signature by adding all signature elements from the expression */
@PortedFrom(file = "tSignatureUpdater.h", name = "TExpressionSignatureUpdater")
class TExpressionSignatureUpdater implements DLExpressionVisitor {
    /** Signature to be filled */
    @PortedFrom(file = "tSignatureUpdater.h", name = "sig")
    TSignature sig;

    /** helper for concept arguments */
    @PortedFrom(file = "tSignatureUpdater.h", name = "vC")
    void vC(ConceptArg expr) {
        expr.getConcept().accept(this);
    }

    /** helper for individual arguments */
    @PortedFrom(file = "tSignatureUpdater.h", name = "vI")
    void vI(IndividualExpression expr) {
        // should no longer be needed: IndividualNames are NamedEntities
        // themselves
        if (expr instanceof NamedEntity) {
            sig.add((NamedEntity) expr);
        }
    }

    /** helper for object role arguments */
    @PortedFrom(file = "tSignatureUpdater.h", name = "vOR")
    void vOR(ObjectRoleArg expr) {
        expr.getOR().accept(this);
    }

    /** helper for object role arguments */
    @PortedFrom(file = "tSignatureUpdater.h", name = "vDR")
    void vDR(DataRoleArg expr) {
        expr.getDataRoleExpression().accept(this);
    }

    /** helper for the named entity */
    @PortedFrom(file = "tSignatureUpdater.h", name = "vE")
    void vE(NamedEntity e) {
        sig.add(e);
    }

    /** array helper */
    @PortedFrom(file = "tSignatureUpdater.h", name = "processArray")
    void processArray(NAryExpression<? extends Expression> expr) {
        for (Expression p : expr.getArguments()) {
            p.accept(this);
        }
    }

    public TExpressionSignatureUpdater(TSignature s) {
        sig = s;
    }

    // concept expressions
    @Override
    public void visit(ConceptTop expr) {}

    @Override
    public void visit(ConceptBottom expr) {}

    @Override
    public void visit(ConceptName expr) {
        vE(expr);
    }

    @Override
    public void visit(ConceptNot expr) {
        vC(expr);
    }

    @Override
    public void visit(ConceptAnd expr) {
        processArray(expr);
    }

    @Override
    public void visit(ConceptOr expr) {
        processArray(expr);
    }

    @Override
    public void visit(ConceptOneOf expr) {
        processArray(expr);
    }

    @Override
    public void visit(ConceptObjectSelf expr) {
        vOR(expr);
    }

    @Override
    public void visit(ConceptObjectValue expr) {
        vOR(expr);
        vI(expr.getIndividual());
    }

    @Override
    public void visit(ConceptObjectExists expr) {
        vOR(expr);
        vC(expr);
    }

    @Override
    public void visit(ConceptObjectForall expr) {
        vOR(expr);
        vC(expr);
    }

    @Override
    public void visit(ConceptObjectMinCardinality expr) {
        vOR(expr);
        vC(expr);
    }

    @Override
    public void visit(ConceptObjectMaxCardinality expr) {
        vOR(expr);
        vC(expr);
    }

    @Override
    public void visit(ConceptObjectExactCardinality expr) {
        vOR(expr);
        vC(expr);
    }

    @Override
    public void visit(ConceptDataValue expr) {
        vDR(expr);
    }

    @Override
    public void visit(ConceptDataExists expr) {
        vDR(expr);
    }

    @Override
    public void visit(ConceptDataForall expr) {
        vDR(expr);
    }

    @Override
    public void visit(ConceptDataMinCardinality expr) {
        vDR(expr);
    }

    @Override
    public void visit(ConceptDataMaxCardinality expr) {
        vDR(expr);
    }

    @Override
    public void visit(ConceptDataExactCardinality expr) {
        vDR(expr);
    }

    // individual expressions
    @Override
    public void visit(IndividualName expr) {
        vE(expr);
    }

    // object role expressions
    @Override
    public void visit(ObjectRoleTop expr) {}

    @Override
    public void visit(ObjectRoleBottom expr) {}

    @Override
    public void visit(ObjectRoleName expr) {
        vE(expr);
    }

    @Override
    public void visit(ObjectRoleInverse expr) {
        vOR(expr);
    }

    @Override
    public void visit(ObjectRoleChain expr) {
        processArray(expr);
    }

    @Override
    public void visit(ObjectRoleProjectionFrom expr) {
        vOR(expr);
        vC(expr);
    }

    @Override
    public void visit(ObjectRoleProjectionInto expr) {
        vOR(expr);
        vC(expr);
    }

    // data role expressions
    @Override
    public void visit(DataRoleTop expr) {}

    @Override
    public void visit(DataRoleBottom expr) {}

    @Override
    public void visit(DataRoleName expr) {
        vE(expr);
    }

    // data expressions
    @Override
    public void visit(DataTop expr) {}

    @Override
    public void visit(DataBottom expr) {}

    @Override
    public void visit(Datatype<?> expr) {}

    @Override
    public void visit(Literal<?> expr) {}

    @Override
    public void visit(DataNot expr) {}

    @Override
    public void visit(DataAnd expr) {}

    @Override
    public void visit(DataOr expr) {}

    @Override
    public void visit(DataOneOf expr) {}
}
