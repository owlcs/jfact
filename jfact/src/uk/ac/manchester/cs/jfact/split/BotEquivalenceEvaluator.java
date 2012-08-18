package uk.ac.manchester.cs.jfact.split;

import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;

/**  check whether class expressions are equivalent to bottom wrt given locality class */
@SuppressWarnings("unused")
// XXX verify unused parameters
public class BotEquivalenceEvaluator extends SigAccessor implements DLExpressionVisitor {
    /**  corresponding top evaluator */
    TopEquivalenceEvaluator TopEval = null;
    /**  keep the value here */
    boolean isBotEq = false;

    /**  check whether the expression is top-equivalent */
    boolean isTopEquivalent(Expression expr) {
        return TopEval.isTopEquivalent(expr);
    }

    /**  @return true iff role expression in equivalent to const wrt locality */
    boolean isREquivalent(Expression expr) {
        return sig.topRLocal() ? isTopEquivalent(expr) : isBotEquivalent(expr);
    }

    /**  init c'tor */
    BotEquivalenceEvaluator(TSignature s) {
        super(s);
    }

    // set fields
    /**  set the corresponding top evaluator */
    void setTopEval(TopEquivalenceEvaluator eval) {
        TopEval = eval;
    }

    /**  @return true iff an EXPRession is equivalent to bottom wrt defined */
    // policy
    boolean isBotEquivalent(Expression expr) {
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

    // equivalent to R(x,y) and C(x), so copy behaviour from ER.X
    @Override
    public void visit(ObjectRoleProjectionFrom expr) {
        isBotEq = isBotEquivalent(expr.getConcept());
        if (!topRLocal()) {
            isBotEq |= isBotEquivalent(expr.getOR());
        }
    }

    // equivalent to R(x,y) and C(y), so copy behaviour from ER.X
    @Override
    public void visit(ObjectRoleProjectionInto expr) {
        isBotEq = isBotEquivalent(expr.getConcept());
        if (!topRLocal()) {
            isBotEq |= isBotEquivalent(expr.getOR());
        }
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
    public void visit(ConceptOneOf expr) {
        isBotEq = expr.isEmpty();
    }

    @Override
    public void visit(ConceptObjectSelf expr) {
        isBotEq = !sig.topRLocal() && isBotEquivalent(expr.getOR());
    }

    @Override
    public void visit(ConceptObjectValue expr) {
        isBotEq = !sig.topRLocal() && isBotEquivalent(expr.getOR());
    }

    @Override
    public void visit(ConceptObjectExists expr) {
        isBotEq = isBotEquivalent(expr.getConcept());
        if (!sig.topRLocal()) {
            isBotEq |= isBotEquivalent(expr.getOR());
        }
    }

    @Override
    public void visit(ConceptObjectForall expr) {
        isBotEq = sig.topRLocal() && isTopEquivalent(expr.getOR())
                && isBotEquivalent(expr.getConcept());
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
        isBotEq = expr.getCardinality() > 0
                && (isBotEquivalent(expr.getConcept()) || isREquivalent(expr.getOR())
                        && (sig.topRLocal() ? isTopEquivalent(expr.getConcept()) : true));
    }

    @Override
    public void visit(ConceptDataValue expr) {
        isBotEq = !sig.topRLocal() && isBotEquivalent(expr.getDataRoleExpression());
    }

    @Override
    public void visit(ConceptDataExists expr) {
        isBotEq = !sig.topRLocal() && isBotEquivalent(expr.getDataRoleExpression());
    }

    @Override
    public void visit(ConceptDataForall expr) {
        isBotEq = sig.topRLocal() && isTopEquivalent(expr.getDataRoleExpression())
                && !isTopDT(expr.getExpr());
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
}
