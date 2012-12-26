package uk.ac.manchester.cs.jfact.split;

import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import conformance.PortedFrom;

/** check whether class expressions are equivalent to top wrt given locality
 * class */
@PortedFrom(file = "SyntacticLocalityChecker.h", name = "TopEquivalenceEvaluator")
public class TopEquivalenceEvaluator extends SigAccessor implements DLExpressionVisitor {
    /** corresponding bottom evaluator */
    BotEquivalenceEvaluator BotEval = null;
    /** keep the value here */
    boolean isTopEq = false;

    /** check whether the expression is top-equivalent */
@PortedFrom(file="SyntacticLocalityChecker.h",name="isBotEquivalent")
    boolean isBotEquivalent(Expression expr) {
        return BotEval.isBotEquivalent(expr);
    }

    // equivalent to R(x,y) and C(x), so copy behaviour from ER.X
    @Override
    public void visit(ObjectRoleProjectionFrom expr) {
        isTopEq = topRLocal() && isTopEquivalent(expr.getOR())
                && isTopEquivalent(expr.getConcept());
    }

    // equivalent to R(x,y) and C(y), so copy behaviour from ER.X
    @Override
    public void visit(ObjectRoleProjectionInto expr) {
        isTopEq = topRLocal() && isTopEquivalent(expr.getOR())
                && isTopEquivalent(expr.getConcept());
    }

    /** @return true iff role expression in equivalent to const wrt locality */
@PortedFrom(file="SyntacticLocalityChecker.h",name="isREquivalent")
    boolean isREquivalent(Expression expr) {
        return topRLocal() ? isTopEquivalent(expr) : isBotEquivalent(expr);
    }

    // set fields
    /** set the corresponding bottom evaluator */
@PortedFrom(file="SyntacticLocalityChecker.h",name="setBotEval")
    void setBotEval(BotEquivalenceEvaluator eval) {
        BotEval = eval;
    }

    /** @return true iff an EXPRession is equivalent to top wrt defined policy */
@PortedFrom(file="SyntacticLocalityChecker.h",name="isTopEquivalent")
    boolean isTopEquivalent(Expression expr) {
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
    public void visit(ConceptOneOf expr) {
        isTopEq = false;
    }

    @Override
    public void visit(ConceptObjectSelf expr) {
        isTopEq = sig.topRLocal() && isTopEquivalent(expr.getOR());
    }

    @Override
    public void visit(ConceptObjectValue expr) {
        isTopEq = sig.topRLocal() && isTopEquivalent(expr.getOR());
    }

    @Override
    public void visit(ConceptObjectExists expr) {
        isTopEq = sig.topRLocal() && isTopEquivalent(expr.getOR())
                && isTopEquivalent(expr.getConcept());
    }

    @Override
    public void visit(ConceptObjectForall expr) {
        isTopEq = isTopEquivalent(expr.getConcept()) || !sig.topRLocal()
                && isBotEquivalent(expr.getOR());
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
        isTopEq = sig.topRLocal() && isTopEquivalent(expr.getDataRoleExpression());
    }

    @Override
    public void visit(ConceptDataExists expr) {
        isTopEq = sig.topRLocal() && isTopEquivalent(expr.getDataRoleExpression())
                && isTopOrBuiltInDataType(expr.getExpr());
    }

    @Override
    public void visit(ConceptDataForall expr) {
        isTopEq = isTopDT(expr.getExpr()) || !sig.topRLocal()
                && isBotEquivalent(expr.getDataRoleExpression());
    }

    @Override
    public void visit(ConceptDataMinCardinality expr) {
        isTopEq = expr.getCardinality() == 0;
        if (sig.topRLocal()) {
            isTopEq |= isTopEquivalent(expr.getDataRoleExpression())
                    && (expr.getCardinality() == 1 ? isTopOrBuiltInDataType(expr
                            .getExpr()) : isTopOrBuiltInDataType(expr.getExpr()));
        }
    }

    @Override
    public void visit(ConceptDataMaxCardinality expr) {
        isTopEq = !sig.topRLocal() && isBotEquivalent(expr.getDataRoleExpression());
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
}
