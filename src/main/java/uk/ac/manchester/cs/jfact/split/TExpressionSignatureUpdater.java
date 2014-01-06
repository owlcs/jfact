package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import uk.ac.manchester.cs.jfact.kernel.dl.ConceptAnd;
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
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionInto;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptArg;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleArg;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NAryExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleArg;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorAdapter;
import conformance.PortedFrom;

/** update the signature by adding all signature elements from the expression */
@PortedFrom(file = "tSignatureUpdater.h", name = "TExpressionSignatureUpdater")
class TExpressionSignatureUpdater extends DLExpressionVisitorAdapter implements
        DLExpressionVisitor, Serializable {
    private static final long serialVersionUID = 11000L;
    /** Signature to be filled */
    @PortedFrom(file = "tSignatureUpdater.h", name = "sig")
    private final TSignature sig;

    /** helper for concept arguments
     * 
     * @param expr
     *            expr */
    @PortedFrom(file = "tSignatureUpdater.h", name = "vC")
    private void vC(ConceptArg expr) {
        expr.getConcept().accept(this);
    }

    /** helper for individual arguments
     * 
     * @param expr
     *            expr */
    @PortedFrom(file = "tSignatureUpdater.h", name = "vI")
    private void vI(IndividualExpression expr) {
        // should no longer be needed: IndividualNames are NamedEntities
        // themselves
        if (expr instanceof NamedEntity) {
            sig.add((NamedEntity) expr);
        }
    }

    /** helper for object role arguments
     * 
     * @param expr
     *            expr */
    @PortedFrom(file = "tSignatureUpdater.h", name = "vOR")
    private void vOR(ObjectRoleArg expr) {
        expr.getOR().accept(this);
    }

    /** helper for object role arguments
     * 
     * @param expr
     *            expr */
    @PortedFrom(file = "tSignatureUpdater.h", name = "vDR")
    private void vDR(DataRoleArg expr) {
        expr.getDataRoleExpression().accept(this);
    }

    /** helper for the named entity
     * 
     * @param e
     *            e */
    @PortedFrom(file = "tSignatureUpdater.h", name = "vE")
    private void vE(NamedEntity e) {
        sig.add(e);
    }

    /** array helper
     * 
     * @param expr
     *            expr */
    @PortedFrom(file = "tSignatureUpdater.h", name = "processArray")
    private void processArray(NAryExpression<? extends Expression> expr) {
        for (Expression p : expr.getArguments()) {
            p.accept(this);
        }
    }

    public TExpressionSignatureUpdater(TSignature s) {
        sig = s;
    }

    // concept expressions
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
    public void visit(ConceptOneOf<?> expr) {
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
    public void visit(DataRoleName expr) {
        vE(expr);
    }
}
