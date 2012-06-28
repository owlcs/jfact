package uk.ac.manchester.cs.jfact.split;

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
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionInto;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;

/// check whether class expressions are equivalent to top wrt given locality class
public class TopEquivalenceEvaluator extends SigAccessor implements DLExpressionVisitor {
    /// corresponding bottom evaluator
    BotEquivalenceEvaluator BotEval = null;
    /// keep the value here
    boolean isTopEq = false;

    /// check whether the expression is top-equivalent
    boolean isBotEquivalent(final Expression expr) {
        return BotEval.isBotEquivalent(expr);
    }

    // equivalent to R(x,y) and C(x), so copy behaviour from ER.X
    @Override
    public void visit(final ObjectRoleProjectionFrom expr) {
        isTopEq = topRLocal() && isTopEquivalent(expr.getOR())
                && isTopEquivalent(expr.getConcept());
    }

    // equivalent to R(x,y) and C(y), so copy behaviour from ER.X
    @Override
    public void visit(final ObjectRoleProjectionInto expr) {
        isTopEq = topRLocal() && isTopEquivalent(expr.getOR())
                && isTopEquivalent(expr.getConcept());
    }

    /// @return true iff role expression in equivalent to const wrt locality
    boolean isREquivalent(final Expression expr) {
        return topRLocal() ? isTopEquivalent(expr) : isBotEquivalent(expr);
    }

    /// init c'tor
    TopEquivalenceEvaluator(final TSignature s) {
        super(s);
    }

    // set fields
    /// set the corresponding bottom evaluator
    void setBotEval(final BotEquivalenceEvaluator eval) {
        BotEval = eval;
    }

    /// @return true iff an EXPRession is equivalent to top wrt defined policy
    boolean isTopEquivalent(final Expression expr) {
        expr.accept(this);
        return isTopEq;
    }

    // concept expressions
    @Override
    public void visit(final ConceptTop expr) {
        isTopEq = true;
    }

    @Override
    public void visit(final ConceptBottom expr) {
        isTopEq = false;
    }

    @Override
    public void visit(final ConceptName expr) {
        isTopEq = sig.topCLocal() && !sig.contains(expr);
    }

    @Override
    public void visit(final ConceptNot expr) {
        isTopEq = isBotEquivalent(expr.getConcept());
    }

    @Override
    public void visit(final ConceptAnd expr) {
        for (ConceptExpression p : expr.getArguments()) {
            if (!isTopEquivalent(p)) {
                return;
            }
        }
        isTopEq = true;
    }

    @Override
    public void visit(final ConceptOr expr) {
        for (ConceptExpression p : expr.getArguments()) {
            if (isTopEquivalent(p)) {
                return;
            }
        }
        isTopEq = false;
    }

    @Override
    public void visit(final ConceptOneOf expr) {
        isTopEq = false;
    }

    @Override
    public void visit(final ConceptObjectSelf expr) {
        isTopEq = sig.topRLocal() && isTopEquivalent(expr.getOR());
    }

    @Override
    public void visit(final ConceptObjectValue expr) {
        isTopEq = sig.topRLocal() && isTopEquivalent(expr.getOR());
    }

    @Override
    public void visit(final ConceptObjectExists expr) {
        isTopEq = sig.topRLocal() && isTopEquivalent(expr.getOR())
                && isTopEquivalent(expr.getConcept());
    }

    @Override
    public void visit(final ConceptObjectForall expr) {
        isTopEq = isTopEquivalent(expr.getConcept()) || !sig.topRLocal()
                && isBotEquivalent(expr.getOR());
    }

    @Override
    public void visit(final ConceptObjectMinCardinality expr) {
        isTopEq = expr.getCardinality() == 0 || sig.topRLocal()
                && isTopEquivalent(expr.getOR()) && isTopEquivalent(expr.getConcept());
    }

    @Override
    public void visit(final ConceptObjectMaxCardinality expr) {
        isTopEq = isBotEquivalent(expr.getConcept()) || !sig.topRLocal()
                && isBotEquivalent(expr.getOR());
    }

    @Override
    public void visit(final ConceptObjectExactCardinality expr) {
        isTopEq = expr.getCardinality() == 0
                && (isBotEquivalent(expr.getConcept()) || !sig.topRLocal()
                        && isBotEquivalent(expr.getOR()));
    }

    @Override
    public void visit(final ConceptDataValue expr) {
        isTopEq = sig.topRLocal() && isTopEquivalent(expr.getDataRoleExpression());
    }

    @Override
    public void visit(final ConceptDataExists expr) {
        isTopEq = sig.topRLocal() && isTopEquivalent(expr.getDataRoleExpression())
                && isTopOrBuiltInDataType(expr.getExpr());
    }

    @Override
    public void visit(final ConceptDataForall expr) {
        isTopEq = isTopDT(expr.getExpr()) || !sig.topRLocal()
                && isBotEquivalent(expr.getDataRoleExpression());
    }

    @Override
    public void visit(final ConceptDataMinCardinality expr) {
        isTopEq = expr.getCardinality() == 0;
        if (sig.topRLocal()) {
            isTopEq |= isTopEquivalent(expr.getDataRoleExpression())
                    && (expr.getCardinality() == 1 ? isTopOrBuiltInDataType(expr
                            .getExpr()) : isTopOrBuiltInDataType(expr.getExpr()));
        }
    }

    @Override
    public void visit(final ConceptDataMaxCardinality expr) {
        isTopEq = !sig.topRLocal() && isBotEquivalent(expr.getDataRoleExpression());
    }

    @Override
    public void visit(final ConceptDataExactCardinality expr) {
        isTopEq = !sig.topRLocal() && expr.getCardinality() == 0
                && isBotEquivalent(expr.getDataRoleExpression());
    }

    // object role expressions
    @Override
    public void visit(final ObjectRoleTop expr) {
        isTopEq = true;
    }

    @Override
    public void visit(final ObjectRoleBottom expr) {
        isTopEq = false;
    }

    @Override
    public void visit(final ObjectRoleName expr) {
        isTopEq = sig.topRLocal() && !sig.contains(expr);
    }

    @Override
    public void visit(final ObjectRoleInverse expr) {
        isTopEq = isTopEquivalent(expr.getOR());
    }

    @Override
    public void visit(final ObjectRoleChain expr) {
        for (ObjectRoleExpression p : expr.getArguments()) {
            if (!isTopEquivalent(p)) {
                return;
            }
        }
        isTopEq = true;
    }

    // data role expressions
    @Override
    public void visit(final DataRoleTop expr) {
        isTopEq = true;
    }

    @Override
    public void visit(final DataRoleBottom expr) {
        isTopEq = false;
    }

    @Override
    public void visit(final DataRoleName expr) {
        isTopEq = sig.topRLocal() && !sig.contains(expr);
    }
}
