package uk.ac.manchester.cs.jfact.kernel;

import java.util.Map;

import uk.ac.manchester.cs.jfact.kernel.dl.ConceptAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptTop;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorAdapter;
import conformance.PortedFrom;

@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TReplacer")
class TReplacer extends DLExpressionVisitorAdapter {
    /**
         * 
         */
    private final ConjunctiveQueryFolding conjunctiveQueryFolding;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "ReplaceResult")
    private Map<ConceptExpression, ConceptExpression> ReplaceResult;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "ExpressionToReplace")
    private ConceptExpression ExpressionToReplace;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PropositionalVariable")
    private ConceptExpression PropositionalVariable;

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TReplacer")
    public TReplacer(ConjunctiveQueryFolding conjunctiveQueryFolding,
            ConceptExpression expression, String propositionalVariable) {
        this.conjunctiveQueryFolding = conjunctiveQueryFolding;
        ExpressionToReplace = expression;
        PropositionalVariable = this.conjunctiveQueryFolding.getpEM().concept(
                propositionalVariable);
    }

    // concept expressions
    @Override
    public void visit(ConceptTop expr) {
        ReplaceResult.put(expr, expr);
    }

    @Override
    public void visit(ConceptName expr) {
        if (expr.equals(ExpressionToReplace)) {
            ReplaceResult.put(expr, PropositionalVariable);
        } else {
            ReplaceResult.put(expr, expr);
        }
    }

    @Override
    public void visit(ConceptAnd expr) {
        if (expr.equals(ExpressionToReplace)) {
            ReplaceResult.put(expr, PropositionalVariable);
        } else {
            ConceptExpression s = null;
            for (ConceptExpression p : expr.getArguments()) {
                p.accept(this);
                if (p == expr.getArguments().get(0)) {
                    s = ReplaceResult.get(p);
                } else {
                    s = conjunctiveQueryFolding.getpEM().and(s, ReplaceResult.get(p));
                }
            }
            ReplaceResult.put(expr, s);
        }
    }

    @Override
    public void visit(ConceptObjectExists expr) {
        if (expr.equals(ExpressionToReplace)) {
            ReplaceResult.put(expr, PropositionalVariable);
        } else {
            ObjectRoleExpression role = expr.getOR();
            expr.getConcept().accept(this);
            ReplaceResult.put(
                    expr,
                    conjunctiveQueryFolding.getpEM().exists(role,
                            ReplaceResult.get(expr.getConcept())));
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getReplaceResult")
    public ConceptExpression getReplaceResult(ConceptExpression c) {
        return ReplaceResult.get(c);
    }
}
