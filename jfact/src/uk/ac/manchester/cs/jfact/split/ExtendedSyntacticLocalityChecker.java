package uk.ac.manchester.cs.jfact.split;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeExpression;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;

abstract class CardinalityEvaluatorBase extends SigAccessor {
    protected UpperBoundDirectEvaluator UBD;
    protected LowerBoundDirectEvaluator LBD;
    protected UpperBoundComplementEvaluator UBC;
    protected LowerBoundComplementEvaluator LBC;
    // / keep the value here
    protected int value;

    // methods to
    // / main method to use
    protected int getValue(Expression expr) {
        expr.accept(this);
        return value;
    }

    // visitor helpers
    protected boolean isBotEquivalent(Expression expr) {
        return getUpperBoundDirect(expr) == 0;
    }

    protected boolean isTopEquivalent(Expression expr) {
        return getUpperBoundComplement(expr) == 0;
    }

    // / helper for entities
    protected abstract int getEntityValue(NamedEntity entity);

    // / helper for All
    protected abstract int getForallValue(RoleExpression R, Expression C);

    // / helper for things like >= m R.C
    protected abstract int getMinValue(int m, RoleExpression R, Expression C);

    // / helper for things like <= m R.C
    protected abstract int getMaxValue(int m, RoleExpression R, Expression C);

    // / helper for things like = m R.C
    protected abstract int getExactValue(int m, RoleExpression R, Expression C);

    // / init c'tor
    public CardinalityEvaluatorBase(TSignature s) {
        super(s);
        value = 0;
    }

    // / set all other evaluators
    public void setEvaluators(UpperBoundDirectEvaluator pUD,
            LowerBoundDirectEvaluator pLD, UpperBoundComplementEvaluator pUC,
            LowerBoundComplementEvaluator pLC) {
        UBD = pUD;
        LBD = pLD;
        UBC = pUC;
        LBC = pLC;
        assert UBD == this || LBD == this || UBC == this || LBC == this;
    }

    // / implementation of evaluation
    public int getUpperBoundDirect(Expression expr) {
        return _getUpperBoundDirect(expr);
    }

    // / implementation of evaluation
    public int getUpperBoundComplement(Expression expr) {
        return _getUpperBoundComplement(expr);
    }

    // / implementation of evaluation
    public int getLowerBoundDirect(Expression expr) {
        return _getLowerBoundDirect(expr);
    }

    // / implementation of evaluation
    public int getLowerBoundComplement(Expression expr) {
        return _getLowerBoundComplement(expr);
    }

    // / implementation of evaluation
    private int _getUpperBoundDirect(Expression expr) {
        return UBD.getValue(expr);
    }

    // / implementation of evaluation
    private int _getUpperBoundComplement(Expression expr) {
        return UBC.getValue(expr);
    }

    // / implementation of evaluation
    private int _getLowerBoundDirect(Expression expr) {
        return LBD.getValue(expr);
    }

    // / implementation of evaluation
    private int _getLowerBoundComplement(Expression expr) {
        return LBC.getValue(expr);
    }

    // visitor implementation: common cases
    // concept expressions
    @Override
    public void visit(ConceptName expr) {
        value = getEntityValue(expr);
    }

    @Override
    public void visit(ConceptObjectExists expr) {
        value = getMinValue(1, expr.getOR(), expr.getConcept());
    }

    @Override
    public void visit(ConceptObjectForall expr) {
        value = getForallValue(expr.getOR(), expr.getConcept());
    }

    @Override
    public void visit(ConceptObjectMinCardinality expr) {
        value = getMinValue(expr.getCardinality(), expr.getOR(), expr.getConcept());
    }

    @Override
    public void visit(ConceptObjectMaxCardinality expr) {
        value = getMaxValue(expr.getCardinality(), expr.getOR(), expr.getConcept());
    }

    @Override
    public void visit(ConceptObjectExactCardinality expr) {
        value = getExactValue(expr.getCardinality(), expr.getOR(), expr.getConcept());
    }

    @Override
    public void visit(ConceptDataExists expr) {
        value = getMinValue(1, expr.getDataRoleExpression(), expr.getExpr());
    }

    @Override
    public void visit(ConceptDataForall expr) {
        value = getForallValue(expr.getDataRoleExpression(), expr.getExpr());
    }

    @Override
    public void visit(ConceptDataMinCardinality expr) {
        value = getMinValue(expr.getCardinality(), expr.getDataRoleExpression(),
                expr.getExpr());
    }

    @Override
    public void visit(ConceptDataMaxCardinality expr) {
        value = getMaxValue(expr.getCardinality(), expr.getDataRoleExpression(),
                expr.getExpr());
    }

    @Override
    public void visit(ConceptDataExactCardinality expr) {
        value = getExactValue(expr.getCardinality(), expr.getDataRoleExpression(),
                expr.getExpr());
    }

    // object role expressions
    @Override
    public void visit(ObjectRoleName expr) {
        value = getEntityValue(expr);
    }

    // FaCT++ extension: equivalent to R(x,y) and C(x), so copy behaviour from
    // ER.X
    @Override
    public void visit(ObjectRoleProjectionFrom expr) {
        value = getMinValue(1, expr.getOR(), expr.getConcept());
    }

    // FaCT++ extension: equivalent to R(x,y) and C(y), so copy behaviour from
    // ER.X
    @Override
    public void visit(ObjectRoleProjectionInto expr) {
        value = getMinValue(1, expr.getOR(), expr.getConcept());
    }

    // data role expressions
    @Override
    public void visit(DataRoleName expr) {
        value = getEntityValue(expr);
    }
}

// / determine how many instances can an expression have
class UpperBoundDirectEvaluator extends CardinalityEvaluatorBase {
    // / define a special value for concepts that are not in C^{<= n}
    protected int getNoneValue() {
        return -1;
    }

    // / define a special value for concepts that are in C^{<= n} for all n
    protected int getAllValue() {
        return 0;
    }

    // / helper for entities TODO: checks only C top-locality, not R
    @Override
    protected int getEntityValue(NamedEntity entity) {
        return sig.botCLocal() && sig.nc(entity) ? getAllValue() : getNoneValue();
    }

    // / helper for All
    @Override
    protected int getForallValue(RoleExpression R, Expression C) {
        if (isTopEquivalent(R) && getLowerBoundComplement(C) >= 1) {
            return getAllValue();
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like >= m R.C
    @Override
    protected int getMinValue(int m, RoleExpression R, Expression C) {
        // m > 0 and...
        if (m <= 0) {
            return getNoneValue();
        }
        // R = \bot or...
        if (isBotEquivalent(R)) {
            return getAllValue();
        }
        // C \in C^{<= m-1}
        int ubC = getUpperBoundDirect(C);
        if (ubC != getNoneValue() && ubC < m) {
            return getAllValue();
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like <= m R.C
    @Override
    protected int getMaxValue(int m, RoleExpression R, Expression C) {
        // R = \top and...
        if (!isTopEquivalent(R)) {
            return getNoneValue();
        }
        // C\in C^{>= m+1}
        int lbC = getLowerBoundDirect(C);
        if (lbC != getNoneValue() && lbC > m) {
            return getAllValue();
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like = m R.C
    @Override
    protected int getExactValue(int m, RoleExpression R, Expression C) {
        // here the maximal value between Mix and Max is an answer. The -1 case
        // will be dealt with automagically
        return Math.max(getMinValue(m, R, C), getMaxValue(m, R, C));
    }

    // / helper for And
    protected <C extends Expression> int getAndValue(NAryExpression<C> expr) {
        int max = getNoneValue();
        // we are looking for the maximal value here; -1 will be dealt with
        // automagically
        for (C p : expr.getArguments()) {
            max = Math.max(max, getUpperBoundDirect(p));
        }
        return max;
    }

    // / helper for Or
    protected <C extends Expression> int getOrValue(NAryExpression<C> expr) {
        int sum = 0, n;
        for (C p : expr.getArguments()) {
            n = getUpperBoundDirect(p);
            if (n == getNoneValue()) {
                return getNoneValue();
            }
            sum += n;
        }
        return sum;
    }

    public UpperBoundDirectEvaluator(TSignature s) {
        super(s);
    }

    // concept expressions
    @Override
    public void visit(ConceptTop c) {
        value = getNoneValue();
    }

    @Override
    public void visit(ConceptBottom c) {
        value = getAllValue();
    }

    @Override
    public void visit(ConceptNot expr) {
        value = getUpperBoundComplement(expr.getConcept());
    }

    @Override
    public void visit(ConceptAnd expr) {
        value = getAndValue(expr);
    }

    @Override
    public void visit(ConceptOr expr) {
        value = getOrValue(expr);
    }

    @Override
    public void visit(ConceptOneOf expr) {
        value = expr.size();
    }

    @Override
    public void visit(ConceptObjectSelf expr) {
        value = isBotEquivalent(expr.getOR()) ? getAllValue() : getNoneValue();
    }

    @Override
    public void visit(ConceptObjectValue expr) {
        value = isBotEquivalent(expr.getOR()) ? getAllValue() : getNoneValue();
    }

    @Override
    public void visit(ConceptDataValue expr) {
        value = isBotEquivalent(expr.getDataRoleExpression()) ? getAllValue()
                : getNoneValue();
    }

    // object role expressions
    @Override
    public void visit(ObjectRoleTop c) {
        value = getNoneValue();
    }

    @Override
    public void visit(ObjectRoleBottom c) {
        value = getAllValue();
    }

    @Override
    public void visit(ObjectRoleInverse expr) {
        value = getUpperBoundDirect(expr.getOR());
    }

    @Override
    public void visit(ObjectRoleChain expr) {
        for (ObjectRoleExpression p : expr.getArguments()) {
            if (isBotEquivalent(p)) {
                value = getAllValue();
                return;
            }
        }
        value = getNoneValue();
    }

    // data role expressions
    @Override
    public void visit(DataRoleTop c) {
        value = getNoneValue();
    }

    @Override
    public void visit(DataRoleBottom c) {
        value = getAllValue();
    }

    // data expressions
    @Override
    public void visit(DataTop c) {
        value = getNoneValue();
    }

    @Override
    public void visit(DataBottom c) {
        value = getAllValue();
    }

    // FIXME!! not ready
    // public void visit ( DataTypeName ) { isBotEq = false; }
    // FIXME!! not ready
    // public void visit ( DataTypeRestriction ) { isBotEq = false; }
    @Override
    public void visit(Literal c) {
        value = 1;
    }

    @Override
    public void visit(DataNot expr) {
        value = getUpperBoundComplement(expr.getExpr());
    }

    @Override
    public void visit(DataAnd expr) {
        value = getAndValue(expr);
    }

    @Override
    public void visit(DataOr expr) {
        value = getOrValue(expr);
    }

    @Override
    public void visit(DataOneOf expr) {
        value = expr.size();
    }
}; // UpperBoundDirectEvaluator

class UpperBoundComplementEvaluator extends CardinalityEvaluatorBase {
    // / define a special value for concepts that are not in C^{<= n}
    protected int getNoneValue() {
        return -1;
    }

    // / define a special value for concepts that are in C^{<= n} for all n
    protected int getAllValue() {
        return 0;
    }

    // / helper for entities TODO: checks only C top-locality, not R
    @Override
    protected int getEntityValue(NamedEntity entity) {
        return topCLocal() && sig.nc(entity) ? getAllValue() : getNoneValue();
    }

    // / helper for All
    @Override
    protected int getForallValue(RoleExpression R, Expression C) {
        if (isBotEquivalent(R) || getUpperBoundComplement(C) == 0) {
            return getAllValue();
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like >= m R.C
    @Override
    protected int getMinValue(int m, RoleExpression R, Expression C) {
        // m == 0 or...
        if (m == 0) {
            return getAllValue();
        }
        // R = \top and...
        if (!isTopEquivalent(R)) {
            return getNoneValue();
        }
        // C \in C^{>= m}
        return getLowerBoundDirect(C) >= m ? getAllValue() : getNoneValue();
    }

    // / helper for things like <= m R.C
    @Override
    protected int getMaxValue(int m, RoleExpression R, Expression C) {
        // R = \bot or...
        if (isBotEquivalent(R)) {
            return getAllValue();
        }
        // C\in C^{<= m}
        int lbC = getUpperBoundDirect(C);
        if (lbC != getNoneValue() && lbC <= m) {
            return getAllValue();
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like = m R.C
    @Override
    protected int getExactValue(int m, RoleExpression R, Expression C) {
        // here the minimal value between Mix and Max is an answer. The -1 case
        // will be dealt with automagically
        return Math.min(getMinValue(m, R, C), getMaxValue(m, R, C));
    }

    // / helper for And
    protected <C extends Expression> int getAndValue(NAryExpression<C> expr) {
        int sum = 0, n;
        for (C p : expr.getArguments()) {
            n = getUpperBoundComplement(p);
            if (n == getNoneValue()) {
                return getNoneValue();
            }
            sum += n;
        }
        return sum;
    }

    // / helper for Or
    protected <C extends Expression> int getOrValue(NAryExpression<C> expr) {
        int max = getNoneValue();
        // we are looking for the maximal value here; -1 will be dealt with
        // automagically
        for (C p : expr.getArguments()) {
            max = Math.max(max, getUpperBoundComplement(p));
        }
        return max;
    }

    // / init c'tor
    public UpperBoundComplementEvaluator(TSignature s) {
        super(s);
    }

    // concept expressions
    @Override
    public void visit(ConceptTop c) {
        value = getAllValue();
    }

    @Override
    public void visit(ConceptBottom c) {
        value = getNoneValue();
    }

    @Override
    public void visit(ConceptNot expr) {
        value = getUpperBoundDirect(expr.getConcept());
    }

    @Override
    public void visit(ConceptAnd expr) {
        value = getAndValue(expr);
    }

    @Override
    public void visit(ConceptOr expr) {
        value = getOrValue(expr);
    }

    @Override
    public void visit(ConceptOneOf c) {
        value = getNoneValue();
    }

    @Override
    public void visit(ConceptObjectSelf expr) {
        value = isTopEquivalent(expr.getOR()) ? getAllValue() : getNoneValue();
    }

    @Override
    public void visit(ConceptObjectValue expr) {
        value = isTopEquivalent(expr.getOR()) ? getAllValue() : getNoneValue();
    }

    @Override
    public void visit(ConceptDataValue expr) {
        value = isTopEquivalent(expr.getDataRoleExpression()) ? getAllValue()
                : getNoneValue();
    }

    // object role expressions
    @Override
    public void visit(ObjectRoleTop c) {
        value = getAllValue();
    }

    @Override
    public void visit(ObjectRoleBottom c) {
        value = getNoneValue();
    }

    @Override
    public void visit(ObjectRoleInverse expr) {
        value = getUpperBoundComplement(expr.getOR());
    }

    @Override
    public void visit(ObjectRoleChain expr) {
        for (ObjectRoleExpression p : expr.getArguments()) {
            if (!isTopEquivalent(p)) {
                value = getNoneValue();
                return;
            }
        }
        value = getAllValue();
    }

    // data role expressions
    @Override
    public void visit(DataRoleTop c) {
        value = getAllValue();
    }

    @Override
    public void visit(DataRoleBottom c) {
        value = getNoneValue();
    }

    // data expressions
    @Override
    public void visit(DataTop c) {
        value = getAllValue();
    }

    @Override
    public void visit(DataBottom c) {
        value = getNoneValue();
    }

    // negated datatype is a union of all other DTs that are infinite
    @Override
    public void visit(Datatype c) {
        value = getNoneValue();
    }

    // negated restriction include negated DT
    public void visit(DatatypeExpression c) {
        value = getNoneValue();
    }

    @Override
    public void visit(Literal c) {
        value = getNoneValue();
    }

    @Override
    public void visit(DataNot expr) {
        value = getUpperBoundDirect(expr.getExpr());
    }

    @Override
    public void visit(DataAnd expr) {
        value = getAndValue(expr);
    }

    @Override
    public void visit(DataOr expr) {
        value = getOrValue(expr);
    }

    @Override
    public void visit(DataOneOf c) {
        value = getNoneValue();
    }
}; // UpperBoundComplementEvaluator

class LowerBoundDirectEvaluator extends CardinalityEvaluatorBase {
    // / define a special value for concepts that are not in C^{>= n}
    protected int getNoneValue() {
        return 0;
    }

    // / define a special value for concepts that are in C^{>= n} for all n
    protected int getAllValue() {
        return -1;
    }

    // / helper for entities TODO: checks only C top-locality, not R
    @Override
    protected int getEntityValue(NamedEntity entity) {
        return topCLocal() && sig.nc(entity) ? 1 : getNoneValue();
    }

    // / helper for All
    @Override
    protected int getForallValue(RoleExpression R, Expression C) {
        if (isBotEquivalent(R) || getUpperBoundComplement(C) == 0) {
            return 1;
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like >= m R.C
    @Override
    protected int getMinValue(int m, RoleExpression R, Expression C) {
        // m == 0 or...
        if (m == 0) {
            return getAllValue();
        }
        // R = \top and...
        if (!isTopEquivalent(R)) {
            return getNoneValue();
        }
        // C \in C^{>= m}
        return getLowerBoundDirect(C) >= m ? m : getNoneValue();
    }

    // / helper for things like <= m R.C
    @Override
    protected int getMaxValue(int m, RoleExpression R, Expression C) {
        // R = \bot or...
        if (isBotEquivalent(R)) {
            return 1;
        }
        // C\in C^{<= m}
        int lbC = getUpperBoundDirect(C);
        if (lbC != getNoneValue() && lbC <= m) {
            return 1;
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like = m R.C
    @Override
    protected int getExactValue(int m, RoleExpression R, Expression C) {
        int min = getMinValue(m, R, C), max = getMaxValue(m, R, C);
        // we need to take the lowest value
        if (min == getNoneValue() || max == getNoneValue()) {
            return getNoneValue();
        }
        if (min == getAllValue()) {
            return max;
        }
        if (max == getAllValue()) {
            return min;
        }
        return Math.min(min, max);
    }

    // / helper for And
    // FIXME!! not done yet
    protected <C extends Expression> int getAndValue(NAryExpression<C> expr) {
        // return m - sumK, where
        boolean foundC = false;    // true if found a conjunct that is in C^{>=}
        int foundM = 0;
        int mMax = 0, kMax = 0; // the m- and k- values for the C_j with max m+k
        int sumK = 0;           // sum of all known k
        // 1st pass: check for none-case, deals with deterministic cases
        for (C p : expr.getArguments()) {
            int m = getLowerBoundDirect(p);        // C_j \in C^{>= m}
            int k = getUpperBoundComplement(p);    // C_j \in CC^{<= k}
            // case 0: if both aren't known then we don't know
            if (m == getNoneValue() && k == getNoneValue()) {
                return getNoneValue();
            }
            // if only k exists then add it to k
            if (m == getNoneValue()) {
                // if ( k == getAllValue() ) // we don't have any bound then
                // return getNoneValue();
                sumK += k;
                continue;
            }
            // if only m exists then set it to m
            if (k == getNoneValue()) {
                if (foundC) {
                    return getNoneValue();
                }
                foundC = true;
                foundM = m;
                continue;
            }
            // here both k and m are values
            sumK += k;  // count k for the
            if (k + m > kMax + mMax) {
                kMax = k;
                mMax = m;
            }
        }
        // here we know the best candidate for M, and only need to set it up
        if (foundC)   // found during the deterministic case
        {
            foundM -= sumK;
            return foundM > 0 ? foundM : getNoneValue();
        } else    // no deterministic option; choose the best one
        {
            sumK -= kMax;
            mMax -= sumK;
            return mMax > 0 ? mMax : getNoneValue();
        }
    }

    // / helper for Or
    protected <C extends Expression> int getOrValue(NAryExpression<C> expr) {
        int max = getNoneValue();
        // we are looking for the maximal value here; -1 need to be
        // special-cased
        for (C p : expr.getArguments()) {
            int n = getUpperBoundDirect(p);
            if (n == getAllValue()) {
                return getAllValue();
            }
            max = Math.max(max, n);
        }
        return max;
    }

    // / init c'tor
    public LowerBoundDirectEvaluator(TSignature s) {
        super(s);
    }

    // concept expressions
    @Override
    public void visit(ConceptTop c) {
        value = 1;
    }

    @Override
    public void visit(ConceptBottom c) {
        value = getNoneValue();
    }

    @Override
    public void visit(ConceptNot expr) {
        value = getLowerBoundComplement(expr.getConcept());
    }

    @Override
    public void visit(ConceptAnd expr) {
        value = getAndValue(expr);
    }

    @Override
    public void visit(ConceptOr expr) {
        value = getOrValue(expr);
    }

    @Override
    public void visit(ConceptOneOf expr) {
        value = expr.size() > 0 ? 1 : getNoneValue();
    }

    @Override
    public void visit(ConceptObjectSelf expr) {
        value = isTopEquivalent(expr.getOR()) ? 1 : getNoneValue();
    }

    // FIXME!! differ from the paper
    @Override
    public void visit(ConceptObjectValue expr) {
        value = isTopEquivalent(expr.getOR()) ? 1 : getNoneValue();
    }

    @Override
    public void visit(ConceptDataValue expr) {
        value = isTopEquivalent(expr.getDataRoleExpression()) ? 1 : getNoneValue();
    }

    // object role expressions
    @Override
    public void visit(ObjectRoleTop c) {
        value = getAllValue();
    }

    @Override
    public void visit(ObjectRoleBottom c) {
        value = getNoneValue();
    }

    @Override
    public void visit(ObjectRoleInverse expr) {
        value = getLowerBoundDirect(expr.getOR());
    }

    @Override
    public void visit(ObjectRoleChain expr) {
        for (ObjectRoleExpression p : expr.getArguments()) {
            if (!isTopEquivalent(p)) {
                value = getNoneValue();
                return;
            }
        }
        value = getAllValue();
    }

    // data role expressions
    @Override
    public void visit(DataRoleTop c) {
        value = getAllValue();
    }

    @Override
    public void visit(DataRoleBottom c) {
        value = getNoneValue();
    }

    // data expressions
    @Override
    public void visit(DataTop c) {
        value = getAllValue();
    }

    @Override
    public void visit(DataBottom c) {
        value = getNoneValue();
    }

    // negated datatype is a union of all other DTs that are infinite
    @Override
    public void visit(Datatype c) {
        value = getNoneValue();
    }

    // negated restriction include negated DT
    public void visit(DatatypeExpression c) {
        value = getNoneValue();
    }

    @Override
    public void visit(Literal c) {
        value = getNoneValue();
    }

    @Override
    public void visit(DataNot expr) {
        value = getLowerBoundComplement(expr.getExpr());
    }

    @Override
    public void visit(DataAnd expr) {
        value = getAndValue(expr);
    }

    @Override
    public void visit(DataOr expr) {
        value = getOrValue(expr);
    }

    @Override
    public void visit(DataOneOf expr) {
        value = expr.size() > 0 ? 1 : getNoneValue();
    }
}

class LowerBoundComplementEvaluator extends CardinalityEvaluatorBase {
    // / define a special value for concepts that are not in C^{>= n}
    protected int getNoneValue() {
        return 0;
    }

    // / define a special value for concepts that are in C^{>= n} for all n
    protected int getAllValue() {
        return -1;
    }

    // / helper for entities TODO: checks only C top-locality, not R
    @Override
    protected int getEntityValue(NamedEntity entity) {
        return sig.botCLocal() && sig.nc(entity) ? 1 : getNoneValue();
    }

    // / helper for All
    @Override
    protected int getForallValue(RoleExpression R, Expression C) {
        if (isTopEquivalent(R) && getLowerBoundComplement(C) >= 1) {
            return 1;
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like >= m R.C
    @Override
    protected int getMinValue(int m, RoleExpression R, Expression C) {
        // m > 0 and...
        if (m <= 0) {
            return getNoneValue();
        }
        // R = \bot or...
        if (isBotEquivalent(R)) {
            return 1;
        }
        // C \in C^{<= m-1}
        int ubC = getUpperBoundDirect(C);
        if (ubC != getNoneValue() && ubC < m) {
            return 1;
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like <= m R.C
    @Override
    protected int getMaxValue(int m, RoleExpression R, Expression C) {
        // R = \top and...
        if (!isTopEquivalent(R)) {
            return getNoneValue();
        }
        // C\in C^{>= m+1}
        int lbC = getLowerBoundDirect(C);
        if (lbC != getNoneValue() && lbC > m) {
            return m + 1;
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like = m R.C
    @Override
    protected int getExactValue(int m, RoleExpression R, Expression C) {
        // here the maximal value between Mix and Max is an answer. The -1 case
        // will be dealt with automagically
        // because both min and max are between 0 and m+1
        return Math.max(getMinValue(m, R, C), getMaxValue(m, R, C));
    }

    // / helper for And
    protected <C extends Expression> int getAndValue(NAryExpression<C> expr) {
        int max = getNoneValue();
        // we are looking for the maximal value here; -1 need to be
        // special-cased
        for (C p : expr.getArguments()) {
            int n = getUpperBoundDirect(p);
            if (n == getAllValue()) {
                return getAllValue();
            }
            max = Math.max(max, n);
        }
        return max;
    }

    // / helper for Or
    protected <C extends Expression> int getOrValue(NAryExpression<C> expr) {
        // return m - sumK, where
        boolean foundC = false;    // true if found a conjunct that is in C^{>=}
        int foundM = 0;
        int mMax = 0, kMax = 0; // the m- and k- values for the C_j with max m+k
        int sumK = 0;           // sum of all known k
        // 1st pass: check for none-case, deals with deterministic cases
        for (C p : expr.getArguments()) {
            int m = getLowerBoundComplement(p);    // C_j \in CC^{>= m}
            int k = getUpperBoundDirect(p);        // C_j \in C^{<= k}
            // case 0: if both aren't known then we don't know
            if (m == getNoneValue() && k == getNoneValue()) {
                return getNoneValue();
            }
            // if only k exists then add it to k
            if (m == getNoneValue()) {
                // if ( k == getAllValue() ) // we don't have any bound then
                // return getNoneValue();
                sumK += k;
                continue;
            }
            // if only m exists then set it to m
            if (k == getNoneValue()) {
                if (foundC) {
                    return getNoneValue();
                }
                foundC = true;
                foundM = m;
                continue;
            }
            // here both k and m are values
            sumK += k;  // count k for the
            if (k + m > kMax + mMax) {
                kMax = k;
                mMax = m;
            }
        }
        // here we know the best candidate for M, and only need to set it up
        if (foundC)   // found during the deterministic case
        {
            foundM -= sumK;
            return foundM > 0 ? foundM : getNoneValue();
        } else    // no deterministic option; choose the best one
        {
            sumK -= kMax;
            mMax -= sumK;
            return mMax > 0 ? mMax : getNoneValue();
        }
    }

    // / init c'tor
    public LowerBoundComplementEvaluator(TSignature s) {
        super(s);
    }

    // concept expressions
    @Override
    public void visit(ConceptTop c) {
        value = getNoneValue();
    }

    @Override
    public void visit(ConceptBottom c) {
        value = 1;
    }

    @Override
    public void visit(ConceptNot expr) {
        value = getLowerBoundDirect(expr.getConcept());
    }

    @Override
    public void visit(ConceptAnd expr) {
        value = getAndValue(expr);
    }

    @Override
    public void visit(ConceptOr expr) {
        value = getOrValue(expr);
    }

    @Override
    public void visit(ConceptOneOf c) {
        value = getNoneValue();
    }

    @Override
    public void visit(ConceptObjectSelf expr) {
        value = isBotEquivalent(expr.getOR()) ? 1 : getNoneValue();
    }

    @Override
    public void visit(ConceptObjectValue expr) {
        value = isBotEquivalent(expr.getOR()) ? 1 : getNoneValue();
    }

    @Override
    public void visit(ConceptDataValue expr) {
        value = isBotEquivalent(expr.getDataRoleExpression()) ? 1 : getNoneValue();
    }

    // object role expressions
    @Override
    public void visit(ObjectRoleTop c) {
        value = getNoneValue();
    }

    @Override
    public void visit(ObjectRoleBottom c) {
        value = getAllValue();
    }

    @Override
    public void visit(ObjectRoleInverse expr) {
        value = getLowerBoundComplement(expr.getOR());
    }

    @Override
    public void visit(ObjectRoleChain expr) {
        for (ObjectRoleExpression p : expr.getArguments()) {
            if (isBotEquivalent(p)) {
                value = getAllValue();
                return;
            }
        }
        value = getNoneValue();
    }

    // data role expressions
    @Override
    public void visit(DataRoleTop c) {
        value = getNoneValue();
    }

    @Override
    public void visit(DataRoleBottom c) {
        value = getAllValue();
    }

    // data expressions
    @Override
    public void visit(DataTop c) {
        value = getNoneValue();
    }

    @Override
    public void visit(DataBottom c) {
        value = getAllValue();
    }

    // FIXME!! not ready
    // public void visit ( DataTypeName ) { isBotEq = false; }
    // FIXME!! not ready
    // public void visit ( DataTypeRestriction ) { isBotEq = false; }
    @Override
    public void visit(Literal c) {
        value = 1;
    }

    @Override
    public void visit(DataNot expr) {
        value = getLowerBoundDirect(expr.getExpr());
    }

    @Override
    public void visit(DataAnd expr) {
        value = getAndValue(expr);
    }

    @Override
    public void visit(DataOr expr) {
        value = getOrValue(expr);
    }

    @Override
    public void visit(DataOneOf c) {
        value = getNoneValue();
    }
}

/** syntactic locality checker for DL axioms */
public class ExtendedSyntacticLocalityChecker extends GeneralSyntacticLocalityChecker {
    protected UpperBoundDirectEvaluator UBD;
    protected LowerBoundDirectEvaluator LBD;
    protected UpperBoundComplementEvaluator UBC;
    protected LowerBoundComplementEvaluator LBC;

    /** @return true iff EXPR is top equivalent */
    @Override
    protected boolean isTopEquivalent(Expression expr) {
        return UBC.getUpperBoundComplement(expr) == 0;
    }

    /** @return true iff EXPR is bottom equivalent */
    @Override
    protected boolean isBotEquivalent(Expression expr) {
        return UBD.getUpperBoundDirect(expr) == 0;
    }



    // / init c'tor
    public ExtendedSyntacticLocalityChecker(TSignature s) {
        super(s);
        UBD = new UpperBoundDirectEvaluator(s);
        LBD = new LowerBoundDirectEvaluator(s);
        UBC = new UpperBoundComplementEvaluator(s);
        LBC = new LowerBoundComplementEvaluator(s);
        UBD.setEvaluators(UBD, LBD, UBC, LBC);
        LBD.setEvaluators(UBD, LBD, UBC, LBC);
        UBC.setEvaluators(UBD, LBD, UBC, LBC);
        LBC.setEvaluators(UBD, LBD, UBC, LBC);
    }
}
