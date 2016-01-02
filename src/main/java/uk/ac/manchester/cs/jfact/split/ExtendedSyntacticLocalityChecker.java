package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeExpression;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NAryExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.RoleExpression;

abstract class CardinalityEvaluatorBase extends SigAccessor {

    protected UpperBoundDirectEvaluator ubd;
    protected LowerBoundDirectEvaluator lbd;
    protected UpperBoundComplementEvaluator ubc;
    protected LowerBoundComplementEvaluator lbc;
    // / keep the value here
    protected int value;

    // / init c'tor
    public CardinalityEvaluatorBase(TSignature s) {
        super(s);
        value = 0;
    }

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
    protected abstract int getForallValue(RoleExpression r, Expression c);

    // / helper for things like >= m R.C
    protected abstract int getMinValue(int m, RoleExpression r, Expression c);

    // / helper for things like <= m R.C
    protected abstract int getMaxValue(int m, RoleExpression r, Expression c);

    // / helper for things like = m R.C
    protected abstract int getExactValue(int m, RoleExpression r, Expression c);

    // / set all other evaluators
    public void setEvaluators(UpperBoundDirectEvaluator pUD, LowerBoundDirectEvaluator pLD,
        UpperBoundComplementEvaluator pUC, LowerBoundComplementEvaluator pLC) {
        ubd = pUD;
        lbd = pLD;
        ubc = pUC;
        lbc = pLC;
        assert ubd == this || lbd == this || ubc == this || lbc == this;
    }

    // / implementation of evaluation
    public int getUpperBoundDirect(Expression expr) {
        return ubd.getValue(expr);
    }

    // / implementation of evaluation
    public int getUpperBoundComplement(Expression expr) {
        return ubc.getValue(expr);
    }

    // / implementation of evaluation
    public int getLowerBoundDirect(Expression expr) {
        return lbd.getValue(expr);
    }

    // / implementation of evaluation
    public int getLowerBoundComplement(Expression expr) {
        return lbc.getValue(expr);
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
        value = getMinValue(expr.getCardinality(), expr.getDataRoleExpression(), expr.getExpr());
    }

    @Override
    public void visit(ConceptDataMaxCardinality expr) {
        value = getMaxValue(expr.getCardinality(), expr.getDataRoleExpression(), expr.getExpr());
    }

    @Override
    public void visit(ConceptDataExactCardinality expr) {
        value = getExactValue(expr.getCardinality(), expr.getDataRoleExpression(), expr.getExpr());
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

    public UpperBoundDirectEvaluator(TSignature s) {
        super(s);
    }

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
    protected int getForallValue(RoleExpression r, Expression c) {
        if (isTopEquivalent(r) && getLowerBoundComplement(c) >= 1) {
            return getAllValue();
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like >= m R.C
    @Override
    protected int getMinValue(int m, RoleExpression r, Expression c) {
        // m > 0 and...
        if (m <= 0) {
            return getNoneValue();
        }
        // r = \bot or...
        if (isBotEquivalent(r)) {
            return getAllValue();
        }
        // c \in c^{<= m-1}
        int ubC = getUpperBoundDirect(c);
        if (ubC != getNoneValue() && ubC < m) {
            return getAllValue();
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like <= m R.C
    @Override
    protected int getMaxValue(int m, RoleExpression r, Expression c) {
        // r = \top and...
        if (!isTopEquivalent(r)) {
            return getNoneValue();
        }
        // c\in c^{>= m+1}
        int lbC = getLowerBoundDirect(c);
        if (lbC != getNoneValue() && lbC > m) {
            return getAllValue();
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like = m R.C
    @Override
    protected int getExactValue(int m, RoleExpression r, Expression c) {
        // here the maximal value between Mix and Max is an answer. The -1 case
        // will be dealt with automagically
        return Math.max(getMinValue(m, r, c), getMaxValue(m, r, c));
    }

    // / helper for And
    protected <C extends Expression> int getAndValue(NAryExpression<C> expr) {
        // we are looking for the maximal value here; -1 will be dealt with
        // automagically
        return expr.getArguments().stream().mapToInt(this::getUpperBoundDirect).reduce(getNoneValue(), Math::max);
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
    public void visit(ConceptOneOf<?> expr) {
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
        value = isBotEquivalent(expr.getDataRoleExpression()) ? getAllValue() : getNoneValue();
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
        if (expr.getArguments().stream().anyMatch(this::isBotEquivalent)) {
            value = getAllValue();
        } else {
            value = getNoneValue();
        }
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
    public void visit(Literal<?> c) {
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
}

class UpperBoundComplementEvaluator extends CardinalityEvaluatorBase {

    // / init c'tor
    public UpperBoundComplementEvaluator(TSignature s) {
        super(s);
    }

    /**
     * define a special value for concepts that are not in C^{<= n}
     * 
     * @return none value
     */
    protected int getNoneValue() {
        return -1;
    }

    /**
     * define a special value for concepts that are in C^{<= n} for all n
     * 
     * @return all value
     */
    protected int getAllValue() {
        return 0;
    }

    /** helper for entities TODO: checks only C top-locality, not R */
    @Override
    protected int getEntityValue(NamedEntity entity) {
        return topCLocal() && sig.nc(entity) ? getAllValue() : getNoneValue();
    }

    /** helper for All */
    @Override
    protected int getForallValue(RoleExpression r, Expression c) {
        if (isBotEquivalent(r) || getUpperBoundComplement(c) == 0) {
            return getAllValue();
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like >= m R.C
    @Override
    protected int getMinValue(int m, RoleExpression r, Expression c) {
        // m == 0 or...
        if (m == 0) {
            return getAllValue();
        }
        // r = \top and...
        if (!isTopEquivalent(r)) {
            return getNoneValue();
        }
        // c \in c^{>= m}
        return getLowerBoundDirect(c) >= m ? getAllValue() : getNoneValue();
    }

    // / helper for things like <= m R.C
    @Override
    protected int getMaxValue(int m, RoleExpression r, Expression c) {
        // r = \bot or...
        if (isBotEquivalent(r)) {
            return getAllValue();
        }
        // c\in c^{<= m}
        int lbC = getUpperBoundDirect(c);
        if (lbC != getNoneValue() && lbC <= m) {
            return getAllValue();
        } else {
            return getNoneValue();
        }
    }

    // / helper for things like = m R.C
    @Override
    protected int getExactValue(int m, RoleExpression r, Expression c) {
        // here the minimal value between Mix and Max is an answer. The -1 case
        // will be dealt with automagically
        return Math.min(getMinValue(m, r, c), getMaxValue(m, r, c));
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
        // we are looking for the maximal value here; -1 will be dealt with
        // automagically
        return expr.getArguments().stream().mapToInt(this::getUpperBoundComplement).reduce(getNoneValue(), Math::max);
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
    public void visit(ConceptOneOf<?> c) {
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
        value = isTopEquivalent(expr.getDataRoleExpression()) ? getAllValue() : getNoneValue();
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
        if (expr.getArguments().stream().anyMatch(p -> !isTopEquivalent(p))) {
            value = getNoneValue();
        } else {
            value = getAllValue();
        }
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
    public void visit(Datatype<?> c) {
        value = getNoneValue();
    }

    // negated restriction include negated DT
    @Override
    public void visit(DatatypeExpression<?> c) {
        value = getNoneValue();
    }

    @Override
    public void visit(Literal<?> c) {
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
}

class LowerBoundDirectEvaluator extends CardinalityEvaluatorBase {

    /**
     * @param s
     *        signature
     */
    public LowerBoundDirectEvaluator(TSignature s) {
        super(s);
    }

    /**
     * @return a special value for concepts that are not in C^{greater or equal
     *         n}
     */
    protected int getNoneValue() {
        return 0;
    }

    /**
     * @return a special value for concepts that are in C^{greater or equal n}
     *         for all n
     */
    protected int getAllValue() {
        return -1;
    }

    /** helper for entities TODO: checks only C top-locality, not R */
    @Override
    protected int getEntityValue(NamedEntity entity) {
        return topCLocal() && sig.nc(entity) ? 1 : getNoneValue();
    }

    /** helper for All */
    @Override
    protected int getForallValue(RoleExpression r, Expression c) {
        if (isBotEquivalent(r) || getUpperBoundComplement(c) == 0) {
            return 1;
        } else {
            return getNoneValue();
        }
    }

    /** helper for things like greater or equal m R.C */
    @Override
    protected int getMinValue(int m, RoleExpression r, Expression c) {
        // m == 0 or...
        if (m == 0) {
            return getAllValue();
        }
        // r = \top and...
        if (!isTopEquivalent(r)) {
            return getNoneValue();
        }
        // c \in c^{>= m}
        return getLowerBoundDirect(c) >= m ? m : getNoneValue();
    }

    /** helper for things like lesser or equal m R.C */
    @Override
    protected int getMaxValue(int m, RoleExpression r, Expression c) {
        // r = \bot or...
        if (isBotEquivalent(r)) {
            return 1;
        }
        // c\in c^{<= m}
        int lbC = getUpperBoundDirect(c);
        if (lbC != getNoneValue() && lbC <= m) {
            return 1;
        } else {
            return getNoneValue();
        }
    }

    /** helper for things like = m R.C */
    @Override
    protected int getExactValue(int m, RoleExpression r, Expression c) {
        int min = getMinValue(m, r, c), max = getMaxValue(m, r, c);
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

    /**
     * @return helper for And
     * @param expr
     *        expression
     */
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

    /**
     * @return helper for Or
     * @param expr
     *        expression
     */
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
    public void visit(ConceptOneOf<?> expr) {
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
        if (expr.getArguments().stream().anyMatch(p -> !isTopEquivalent(p))) {
            value = getNoneValue();
        } else {
            value = getAllValue();
        }
    }

    @Override
    public void visit(DataRoleTop c) {
        value = getAllValue();
    }

    @Override
    public void visit(DataRoleBottom c) {
        value = getNoneValue();
    }

    @Override
    public void visit(DataTop c) {
        value = getAllValue();
    }

    @Override
    public void visit(DataBottom c) {
        value = getNoneValue();
    }

    @Override
    public void visit(Datatype<?> c) {
        // negated datatype is a union of all other DTs that are infinite
        value = getNoneValue();
    }

    @Override
    public void visit(DatatypeExpression<?> c) {
        // negated restriction include negated DT
        value = getNoneValue();
    }

    @Override
    public void visit(Literal<?> c) {
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

    // / init c'tor
    public LowerBoundComplementEvaluator(TSignature s) {
        super(s);
    }

    /**
     * @return a special value for concepts that are not in C^{greater or equal
     *         n}
     */
    protected int getNoneValue() {
        return 0;
    }

    /**
     * @return a special value for concepts that are in C^{greater or equal n}
     *         for all n
     */
    protected int getAllValue() {
        return -1;
    }

    /** helper for entities TODO: checks only C top-locality, not R */
    @Override
    protected int getEntityValue(NamedEntity entity) {
        return sig.botCLocal() && sig.nc(entity) ? 1 : getNoneValue();
    }

    /** helper for All */
    @Override
    protected int getForallValue(RoleExpression r, Expression c) {
        if (isTopEquivalent(r) && getLowerBoundComplement(c) >= 1) {
            return 1;
        } else {
            return getNoneValue();
        }
    }

    /** helper for things like greater or equal m R.C */
    @Override
    protected int getMinValue(int m, RoleExpression r, Expression c) {
        // m > 0 and...
        if (m <= 0) {
            return getNoneValue();
        }
        // r = \bot or...
        if (isBotEquivalent(r)) {
            return 1;
        }
        // c \in c^{<= m-1}
        int ubC = getUpperBoundDirect(c);
        if (ubC != getNoneValue() && ubC < m) {
            return 1;
        } else {
            return getNoneValue();
        }
    }

    /** helper for things like lesser or equal m R.C */
    @Override
    protected int getMaxValue(int m, RoleExpression r, Expression c) {
        // r = \top and...
        if (!isTopEquivalent(r)) {
            return getNoneValue();
        }
        // c\in c^{>= m+1}
        int lbC = getLowerBoundDirect(c);
        if (lbC != getNoneValue() && lbC > m) {
            return m + 1;
        } else {
            return getNoneValue();
        }
    }

    /** helper for things like = m R.C */
    @Override
    protected int getExactValue(int m, RoleExpression r, Expression c) {
        // here the maximal value between Mix and Max is an answer. The -1 case
        // will be dealt with automagically
        // because both min and max are between 0 and m+1
        return Math.max(getMinValue(m, r, c), getMaxValue(m, r, c));
    }

    /**
     * @param expr
     *        expression
     * @return helper for And
     */
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

    /**
     * @param expr
     *        expression
     * @return helper for Or
     */
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
    public void visit(ConceptOneOf<?> c) {
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
        if (expr.getArguments().stream().anyMatch(this::isBotEquivalent)) {
            value = getAllValue();
        } else {
            value = getNoneValue();
        }
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
    public void visit(Literal<?> c) {
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

    protected UpperBoundDirectEvaluator ubd;
    protected LowerBoundDirectEvaluator lbd;
    protected UpperBoundComplementEvaluator ubc;
    protected LowerBoundComplementEvaluator lbc;

    /**
     * init c'tor
     * 
     * @param s
     *        s
     */
    public ExtendedSyntacticLocalityChecker(TSignature s) {
        super(s);
        ubd = new UpperBoundDirectEvaluator(s);
        lbd = new LowerBoundDirectEvaluator(s);
        ubc = new UpperBoundComplementEvaluator(s);
        lbc = new LowerBoundComplementEvaluator(s);
        ubd.setEvaluators(ubd, lbd, ubc, lbc);
        lbd.setEvaluators(ubd, lbd, ubc, lbc);
        ubc.setEvaluators(ubd, lbd, ubc, lbc);
        lbc.setEvaluators(ubd, lbd, ubc, lbc);
    }

    /** @return true iff EXPR is top equivalent */
    @Override
    protected boolean isTopEquivalent(Expression expr) {
        return ubc.getUpperBoundComplement(expr) == 0;
    }

    /** @return true iff EXPR is bottom equivalent */
    @Override
    protected boolean isBotEquivalent(Expression expr) {
        return ubd.getUpperBoundDirect(expr) == 0;
    }
}
