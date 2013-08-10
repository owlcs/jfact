package uk.ac.manchester.cs.jfact.elf;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import conformance.PortedFrom;

/** elf expression checker */
@PortedFrom(file = "ELFExpressionChecker.h", name = "ELFExpressionChecker")
public class ELFExpressionChecker implements DLExpressionVisitor, Serializable {
    private static final long serialVersionUID = 11000L;
    @PortedFrom(file = "ELFExpressionChecker.h", name = "value")
    boolean value;

    /** get DLTree corresponding to an expression EXPR */
    @PortedFrom(file = "ELFExpressionChecker.h", name = "v")
    boolean v(Expression expr) {
        expr.accept(this);
        return value;
    }

    // concept expressions
    @Override
    public void visit(ConceptTop expr) {
        value = true;
    }

    @Override
    public void visit(ConceptBottom expr) {
        value = true;
    }

    @Override
    public void visit(ConceptName expr) {
        value = true;
    }

    @Override
    public void visit(ConceptNot expr) {
        value = false;
    }

    @Override
    public void visit(ConceptAnd expr) {
        value = false;
        for (Expression p : expr.getArguments()) {
            if (!v(p)) {
                return;
            }
        }
        value = true;
    }

    @Override
    public void visit(ConceptOr expr) {
        value = false;
    }

    @Override
    public void visit(ConceptOneOf<?> expr) {
        value = false;
    }

    @Override
    public void visit(ConceptObjectSelf expr) {
        value = false;
    }

    @Override
    public void visit(ConceptObjectValue expr) {
        value = false;
    }

    @Override
    public void visit(ConceptObjectExists expr) {
        value = false;
        // check role
        if (!v(expr.getOR())) {
            return;
        }
        // check concept
        v(expr.getConcept());
    }

    @Override
    public void visit(ConceptObjectForall expr) {
        value = false;
    }

    @Override
    public void visit(ConceptObjectMinCardinality expr) {
        value = false;
    }

    @Override
    public void visit(ConceptObjectMaxCardinality expr) {
        value = false;
    }

    @Override
    public void visit(ConceptObjectExactCardinality expr) {
        value = false;
    }

    @Override
    public void visit(ConceptDataValue expr) {
        value = false;
    }

    @Override
    public void visit(ConceptDataExists expr) {
        value = false;
    }

    @Override
    public void visit(ConceptDataForall expr) {
        value = false;
    }

    @Override
    public void visit(ConceptDataMinCardinality expr) {
        value = false;
    }

    @Override
    public void visit(ConceptDataMaxCardinality expr) {
        value = false;
    }

    @Override
    public void visit(ConceptDataExactCardinality expr) {
        value = false;
    }

    // individual expressions
    @Override
    public void visit(IndividualName expr) {
        value = false;
    }

    // object role expressions
    @Override
    public void visit(ObjectRoleTop expr) {
        value = false;
    }

    @Override
    public void visit(ObjectRoleBottom expr) {
        value = false;
    }

    @Override
    public void visit(ObjectRoleName expr) {
        value = true;
    }

    @Override
    public void visit(ObjectRoleInverse expr) {
        value = false;
    }

    @Override
    public void visit(ObjectRoleChain expr) {
        value = false;
        for (Expression p : expr.getArguments()) {
            if (!v(p)) {
                return;
            }
        }
        value = true;
    }

    @Override
    public void visit(ObjectRoleProjectionFrom expr) {
        value = false;
    }

    @Override
    public void visit(ObjectRoleProjectionInto expr) {
        value = false;
    }

    // data role expressions
    @Override
    public void visit(DataRoleTop expr) {
        value = false;
    }

    @Override
    public void visit(DataRoleBottom expr) {
        value = false;
    }

    @Override
    public void visit(DataRoleName expr) {
        value = false;
    }

    // data expressions
    @Override
    public void visit(DataTop expr) {
        value = false;
    }

    @Override
    public void visit(DataBottom expr) {
        value = false;
    }

    @Override
    public void visit(DataNot expr) {
        value = false;
    }

    @Override
    public void visit(DataAnd expr) {
        value = false;
    }

    @Override
    public void visit(DataOr expr) {
        value = false;
    }

    @Override
    public void visit(DataOneOf expr) {
        value = false;
    }

    @Override
    public void visit(Literal<?> expr) {
        value = false;
    }

    @Override
    public void visit(Datatype<?> expr) {
        value = false;
    }
}
