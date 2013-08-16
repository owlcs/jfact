package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.datatypes.cardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import conformance.PortedFrom;

/** check whether class expressions are equivalent to bottom wrt given locality
 * class */
// XXX verify unused parameters
@PortedFrom(file = "SyntacticLocalityChecker.h", name = "BotEquivalenceEvaluator")
public class BotEquivalenceEvaluator extends SigAccessor implements DLExpressionVisitor,
        Serializable {
    private static final long serialVersionUID = 11000L;
    /** corresponding top evaluator */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "TopEval")
    private TopEquivalenceEvaluator TopEval = null;
    /** keep the value here */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isBotEq")
    private boolean isBotEq = false;

    /** check whether the expression is top-equivalent */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isTopEquivalent")
    private boolean isTopEquivalent(Expression expr) {
        return TopEval.isTopEquivalent(expr);
    }

    /** @return true iff role expression in equivalent to const wrt locality */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isREquivalent")
    private boolean isREquivalent(Expression expr) {
        return sig.topRLocal() ? isTopEquivalent(expr) : isBotEquivalent(expr);
    }

    // non-empty Concept/Data expression
    /** @return true iff C^I is non-empty */
    private boolean isBotDistinct(Expression C) {
        // TOP is non-empty
        if (isTopEquivalent(C)) {
            return true;
        }
        // built-in DT are non-empty
        // FIXME!! that's it for now
        return C instanceof Datatype;
    }

    // cardinality of a concept/data expression interpretation
    /** @return true if #C^I > n */
    private boolean isCardLargerThan(Expression C, int n) {
        // data top is infinite
        if (C instanceof DataExpression && isTopEquivalent(C)) {
            return true;
        }
        if (n == 0) {
            return isBotDistinct(C);
        }
        if (C instanceof Datatype) {   // string/time are infinite DT
            return ((Datatype<?>) C).getCardinality() == cardinality.COUNTABLYINFINITE;
        }
        // FIXME!! try to be more precise
        return false;
    }

    // QCRs
    /** @return true iff (>= n R.C) is botEq */
    private boolean isMinBotEquivalent(int n, RoleExpression R, Expression C) {
        return n > 0 && (isBotEquivalent(R) || isBotEquivalent(C));
    }

    /** @return true iff (<= n R.C) is botEq */
    private boolean isMaxBotEquivalent(int n, RoleExpression R, Expression C) {
        return isTopEquivalent(R) && isCardLargerThan(C, n);
    }

    // set fields
    /** set the corresponding top evaluator */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "setTopEval")
    protected void setTopEval(TopEquivalenceEvaluator eval) {
        TopEval = eval;
    }

    /** @return true iff an EXPRession is equivalent to bottom wrt defined policy */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isBotEquivalent")
    protected boolean isBotEquivalent(Expression expr) {
        expr.accept(this);
        return isBotEq;
    }

    // concept expressions
    @Override
    public void visit(ConceptTop expr) {
        isBotEq = false;
    }

    @Override
    public void visit(ConceptBottom expr) {
        isBotEq = true;
    }

    // FaCT++ extension: equivalent to R(x,y) and C(x), so copy behaviour from
    // ER.X
    @Override
    public void visit(ObjectRoleProjectionFrom expr) {
        isBotEq = isMinBotEquivalent(1, expr.getOR(), expr.getConcept());
    }

    // FaCT++ extension: equivalent to R(x,y) and C(y), so copy behaviour from
    // ER.X
    @Override
    public void visit(ObjectRoleProjectionInto expr) {
        isBotEq = isMinBotEquivalent(1, expr.getOR(), expr.getConcept());
    }

    @Override
    public void visit(ConceptName expr) {
        isBotEq = !sig.topCLocal() && !sig.contains(expr);
    }

    @Override
    public void visit(ConceptNot expr) {
        isBotEq = isTopEquivalent(expr.getConcept());
    }

    @Override
    public void visit(ConceptAnd expr) {
        for (ConceptExpression p : expr.getArguments()) {
            if (isBotEquivalent(p)) {
                return;
            }
        }
        isBotEq = false;
    }

    @Override
    public void visit(ConceptOr expr) {
        for (ConceptExpression p : expr.getArguments()) {
            if (!isBotEquivalent(p)) {
                return;
            }
        }
        isBotEq = true;
    }

    @Override
    public void visit(ConceptOneOf<?> expr) {
        isBotEq = expr.isEmpty();
    }

    @Override
    public void visit(ConceptObjectSelf expr) {
        isBotEq = isBotEquivalent(expr.getOR());
    }

    @Override
    public void visit(ConceptObjectValue expr) {
        isBotEq = isBotEquivalent(expr.getOR());
    }

    @Override
    public void visit(ConceptObjectExists expr) {
        isBotEq = isMinBotEquivalent(1, expr.getOR(), expr.getConcept());
    }

    @Override
    public void visit(ConceptObjectForall expr) {
        isBotEq = isTopEquivalent(expr.getOR()) && isBotEquivalent(expr.getConcept());
    }

    @Override
    public void visit(ConceptObjectMinCardinality expr) {
        isBotEq = expr.getCardinality() > 0
                && (isBotEquivalent(expr.getConcept()) || !sig.topRLocal()
                        && isBotEquivalent(expr.getOR()));
    }

    @Override
    public void visit(ConceptObjectMaxCardinality expr) {
        isBotEq = sig.topRLocal() && expr.getCardinality() > 0
                && isTopEquivalent(expr.getOR()) && isTopEquivalent(expr.getConcept());
    }

    @Override
    public void visit(ConceptObjectExactCardinality expr) {
        int n = expr.getCardinality();
        ObjectRoleExpression R = expr.getOR();
        ConceptExpression C = expr.getConcept();
        isBotEq = isMinBotEquivalent(n, R, C) || isMaxBotEquivalent(n, R, C);
    }

    @Override
    public void visit(ConceptDataValue expr) {
        isBotEq = isBotEquivalent(expr.getDataRoleExpression());
    }

    @Override
    public void visit(ConceptDataExists expr) {
        isBotEq = isMinBotEquivalent(1, expr.getDataRoleExpression(), expr.getExpr());
    }

    @Override
    public void visit(ConceptDataForall expr) {
        isBotEq = isTopEquivalent(expr.getDataRoleExpression())
                && !isTopEquivalent(expr.getExpr());
    }

    @Override
    public void visit(ConceptDataMinCardinality expr) {
        isBotEq = !sig.topRLocal() && expr.getCardinality() > 0
                && isBotEquivalent(expr.getDataRoleExpression());
    }

    @Override
    public void visit(ConceptDataMaxCardinality expr) {
        isBotEq = sig.topRLocal()
                && isTopEquivalent(expr.getDataRoleExpression())
                && (expr.getCardinality() <= 1 ? isTopOrBuiltInDataType(expr.getExpr())
                        : isTopOrBuiltInDataType(expr.getExpr()));
    }

    @Override
    public void visit(ConceptDataExactCardinality expr) {
        isBotEq = isREquivalent(expr.getDataRoleExpression())
                && (sig.topRLocal() ? expr.getCardinality() == 0 ? isTopOrBuiltInDataType(expr
                        .getExpr()) : isTopOrBuiltInDataType(expr.getExpr())
                        : expr.getCardinality() > 0);
    }

    // object role expressions
    @Override
    public void visit(ObjectRoleTop expr) {
        isBotEq = false;
    }

    @Override
    public void visit(ObjectRoleBottom expr) {
        isBotEq = true;
    }

    @Override
    public void visit(ObjectRoleName expr) {
        isBotEq = !sig.topRLocal() && !sig.contains(expr);
    }

    @Override
    public void visit(ObjectRoleInverse expr) {
        isBotEq = isBotEquivalent(expr.getOR());
    }

    @Override
    public void visit(ObjectRoleChain expr) {
        isBotEq = true;
        for (ObjectRoleExpression p : expr.getArguments()) {
            if (isBotEquivalent(p)) {
                return;
            }
        }
        isBotEq = false;
    }

    // data role expressions
    @Override
    public void visit(DataRoleTop expr) {
        isBotEq = false;
    }

    @Override
    public void visit(DataRoleBottom expr) {
        isBotEq = true;
    }

    @Override
    public void visit(DataRoleName expr) {
        isBotEq = !sig.topRLocal() && !sig.contains(expr);
    }

    // data expressions
    @Override
    public void visit(DataTop arg) {
        isBotEq = false;
    }

    @Override
    public void visit(DataBottom arg) {
        isBotEq = true;
    }

    @Override
    public void visit(Datatype<?> arg) {
        isBotEq = false;
    }

    @Override
    public void visit(Literal<?> arg) {
        isBotEq = false;
    }

    @Override
    public void visit(DataNot expr) {
        isBotEq = isTopEquivalent(expr.getExpr());
    }

    @Override
    public void visit(DataAnd expr) {
        for (DataExpression p : expr.getArguments()) {
            if (isBotEquivalent(p)) {
                return;
            }
        }
        isBotEq = false;
    }

    @Override
    public void visit(DataOr expr) {
        for (DataExpression p : expr.getArguments()) {
            if (!isBotEquivalent(p)) {
                return;
            }
        }
        isBotEq = true;
    }

    @Override
    public void visit(DataOneOf expr) {
        isBotEq = expr.isEmpty();
    }
}
