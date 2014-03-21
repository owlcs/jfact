package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.datatypes.cardinality;
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
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionInto;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.RoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import conformance.PortedFrom;

/**
 * check whether class expressions are equivalent to top wrt given locality
 * class
 */
@PortedFrom(file = "SyntacticLocalityChecker.h", name = "TopEquivalenceEvaluator")
public class TopEquivalenceEvaluator extends SigAccessor implements
        DLExpressionVisitor {

    private static final long serialVersionUID = 11000L;
    /** corresponding bottom evaluator */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "BotEval")
    private BotEquivalenceEvaluator BotEval = null;
    /** keep the value here */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isTopEq")
    private boolean isTopEq = false;

    /**
     * check whether the expression is top-equivalent
     * 
     * @param expr
     *        expr
     * @return true if bot equivalent
     */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isBotEquivalent")
    private boolean isBotEquivalent(Expression expr) {
        return BotEval.isBotEquivalent(expr);
    }

    // FaCT++ extension: equivalent to R(x,y) and C(x), so copy behaviour from
    // ER.X
    @Override
    public void visit(ObjectRoleProjectionFrom expr) {
        isTopEq = isMinTopEquivalent(1, expr.getOR(), expr.getConcept());
    }

    // FaCT++ extension: equivalent to R(x,y) and C(y), so copy behaviour from
    // ER.X
    @Override
    public void visit(ObjectRoleProjectionInto expr) {
        isTopEq = isMinTopEquivalent(1, expr.getOR(), expr.getConcept());
    }

    /**
     * @param expr
     *        expr
     * @return true iff role expression in equivalent to const wrt locality
     */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isREquivalent")
    private boolean isREquivalent(Expression expr) {
        return topRLocal() ? isTopEquivalent(expr) : isBotEquivalent(expr);
    }

    // non-empty Concept/Data expression
    /**
     * @param C
     *        C
     * @return true iff C^I is non-empty
     */
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
    /**
     * @param C
     *        C
     * @param n
     *        n
     * @return true if #C^I > n
     */
    private boolean isCardLargerThan(Expression C, int n) {
        if (n == 0) {
            return isBotDistinct(C);
        }
        // data top is infinite
        if (C instanceof DataExpression && isTopEquivalent(C)) {
            return true;
        }
        if (C instanceof Datatype) {
            return ((Datatype<?>) C).getCardinality() == cardinality.COUNTABLYINFINITE;
        }
        // FIXME!! try to be more precise
        return false;
    }

    // QCRs
    /**
     * @param n
     *        n
     * @param R
     *        R
     * @param C
     *        C
     * @return true iff (>= n R.C) is topEq
     */
    private boolean isMinTopEquivalent(int n, RoleExpression R, Expression C) {
        return n == 0 || isTopEquivalent(R) && isCardLargerThan(C, n - 1);
    }

    /**
     * @param n
     *        n
     * @param R
     *        R
     * @param C
     *        C
     * @return true iff (<= n R.C) is topEq
     */
    @SuppressWarnings("unused")
    private boolean isMaxTopEquivalent(int n, RoleExpression R, Expression C) {
        return isBotEquivalent(R) || isBotEquivalent(C);
    }

    // set fields
    /**
     * set the corresponding bottom evaluator
     * 
     * @param eval
     *        eval
     */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "setBotEval")
    public void setBotEval(BotEquivalenceEvaluator eval) {
        BotEval = eval;
    }

    /**
     * @param expr
     *        expr
     * @return true iff an EXPRession is equivalent to top wrt defined policy
     */
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "isTopEquivalent")
    public boolean isTopEquivalent(Expression expr) {
        expr.accept(this);
        return isTopEq;
    }

    // concept expressions
    @Override
    public void visit(ConceptTop expr) {
        isTopEq = true;
    }

    @Override
    public void visit(ConceptBottom expr) {
        isTopEq = false;
    }

    @Override
    public void visit(ConceptName expr) {
        isTopEq = sig.topCLocal() && !sig.contains(expr);
    }

    @Override
    public void visit(ConceptNot expr) {
        isTopEq = isBotEquivalent(expr.getConcept());
    }

    @Override
    public void visit(ConceptAnd expr) {
        for (ConceptExpression p : expr.getArguments()) {
            if (!isTopEquivalent(p)) {
                return;
            }
        }
        isTopEq = true;
    }

    @Override
    public void visit(ConceptOr expr) {
        for (ConceptExpression p : expr.getArguments()) {
            if (isTopEquivalent(p)) {
                return;
            }
        }
        isTopEq = false;
    }

    @Override
    public void visit(ConceptOneOf<?> expr) {
        isTopEq = false;
    }

    @Override
    public void visit(ConceptObjectSelf expr) {
        isTopEq = isTopEquivalent(expr.getOR());
    }

    @Override
    public void visit(ConceptObjectValue expr) {
        isTopEq = isTopEquivalent(expr.getOR());
    }

    @Override
    public void visit(ConceptObjectExists expr) {
        isTopEq = isMinTopEquivalent(1, expr.getOR(), expr.getConcept());
    }

    @Override
    public void visit(ConceptObjectForall expr) {
        isTopEq = isTopEquivalent(expr.getConcept())
                || isBotEquivalent(expr.getOR());
    }

    @Override
    public void visit(ConceptObjectMinCardinality expr) {
        isTopEq = expr.getCardinality() == 0 || expr.getCardinality() == 1
                && sig.topRLocal() && isTopEquivalent(expr.getOR())
                && isTopEquivalent(expr.getConcept());
    }

    @Override
    public void visit(ConceptObjectMaxCardinality expr) {
        isTopEq = isBotEquivalent(expr.getConcept()) || !sig.topRLocal()
                && isBotEquivalent(expr.getOR());
    }

    @Override
    public void visit(ConceptObjectExactCardinality expr) {
        isTopEq = expr.getCardinality() == 0
                && (isBotEquivalent(expr.getConcept()) || !sig.topRLocal()
                        && isBotEquivalent(expr.getOR()));
    }

    @Override
    public void visit(ConceptDataValue expr) {
        isTopEq = isTopEquivalent(expr.getDataRoleExpression());
    }

    @Override
    public void visit(ConceptDataExists expr) {
        isTopEq = isMinTopEquivalent(1, expr.getDataRoleExpression(),
                expr.getExpr());
    }

    @Override
    public void visit(ConceptDataForall expr) {
        isTopEq = isTopEquivalent(expr.getExpr())
                || isBotEquivalent(expr.getDataRoleExpression());
    }

    @Override
    public void visit(ConceptDataMinCardinality expr) {
        isTopEq = expr.getCardinality() == 0;
        if (sig.topRLocal()) {
            isTopEq |= isTopEquivalent(expr.getDataRoleExpression())
                    && (expr.getCardinality() == 1 ? isTopOrBuiltInDataType(expr
                            .getExpr())
                            : isTopOrBuiltInDataType(expr.getExpr()));
        }
    }

    @Override
    public void visit(ConceptDataMaxCardinality expr) {
        isTopEq = !sig.topRLocal()
                && isBotEquivalent(expr.getDataRoleExpression());
    }

    @Override
    public void visit(ConceptDataExactCardinality expr) {
        isTopEq = !sig.topRLocal() && expr.getCardinality() == 0
                && isBotEquivalent(expr.getDataRoleExpression());
    }

    // object role expressions
    @Override
    public void visit(ObjectRoleTop expr) {
        isTopEq = true;
    }

    @Override
    public void visit(ObjectRoleBottom expr) {
        isTopEq = false;
    }

    @Override
    public void visit(ObjectRoleName expr) {
        isTopEq = sig.topRLocal() && !sig.contains(expr);
    }

    @Override
    public void visit(ObjectRoleInverse expr) {
        isTopEq = isTopEquivalent(expr.getOR());
    }

    @Override
    public void visit(ObjectRoleChain expr) {
        isTopEq = false;
        for (ObjectRoleExpression p : expr.getArguments()) {
            if (!isTopEquivalent(p)) {
                return;
            }
        }
        isTopEq = true;
    }

    // data role expressions
    @Override
    public void visit(DataRoleTop expr) {
        isTopEq = true;
    }

    @Override
    public void visit(DataRoleBottom expr) {
        isTopEq = false;
    }

    @Override
    public void visit(DataRoleName expr) {
        isTopEq = sig.topRLocal() && !sig.contains(expr);
    }

    // data expressions
    @Override
    public void visit(DataTop arg) {
        isTopEq = true;
    }

    @Override
    public void visit(DataBottom arg) {
        isTopEq = false;
    }

    @Override
    public void visit(Datatype<?> arg) {
        isTopEq = false;
    }

    @Override
    public void visit(Literal<?> arg) {
        isTopEq = false;
    }

    @Override
    public void visit(DataNot expr) {
        isTopEq = isBotEquivalent(expr.getExpr());
    }

    @Override
    public void visit(DataAnd expr) {
        for (DataExpression p : expr.getArguments()) {
            if (!isTopEquivalent(p)) {
                return;
            }
        }
        isTopEq = true;
    }

    @Override
    public void visit(DataOr expr) {
        for (DataExpression p : expr.getArguments()) {
            if (isTopEquivalent(p)) {
                return;
            }
        }
        isTopEq = false;
    }

    @Override
    public void visit(DataOneOf arg) {
        isTopEq = false;
    }
}
