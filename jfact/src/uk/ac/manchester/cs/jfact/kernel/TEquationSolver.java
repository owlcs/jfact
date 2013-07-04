package uk.ac.manchester.cs.jfact.kernel;

import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import conformance.PortedFrom;

@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TEquationSolver")
class TEquationSolver {
    /**
     * 
     */
    private final ConjunctiveQueryFolding conjunctiveQueryFolding;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "LeftPart")
    private ConceptExpression LeftPart;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "RightPart")
    private ConceptExpression RightPart;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "ExpressionMarker")
    private TExpressionMarker ExpressionMarker;

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TEquationSolver")
    public TEquationSolver(ConjunctiveQueryFolding conjunctiveQueryFolding,
            ConceptExpression leftPart, String propositionalVariable,
            TExpressionMarker expressionMarker) {
        this.conjunctiveQueryFolding = conjunctiveQueryFolding;
        LeftPart = leftPart;
        RightPart = this.conjunctiveQueryFolding.getpEM().concept(propositionalVariable);
        ExpressionMarker = expressionMarker;
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Solve")
    public void Solve() {
        while (!conjunctiveQueryFolding.IsNominal(LeftPart)) {
            if (LeftPart instanceof ConceptObjectExists) {
                ConceptObjectExists leftDiamond = (ConceptObjectExists) LeftPart;
                ObjectRoleInverse invRole = (ObjectRoleInverse) leftDiamond.getOR();
                ObjectRoleExpression role = invRole.getOR();
                ConceptExpression newLeftPart = leftDiamond.getConcept();
                ConceptExpression newRightPart = conjunctiveQueryFolding.getpEM().forall(
                        role, RightPart);
                LeftPart = newLeftPart;
                RightPart = newRightPart;
            } else if (LeftPart instanceof ConceptAnd) {
                ConceptAnd leftAnd = (ConceptAnd) LeftPart;
                ConceptExpression arg1 = leftAnd.getArguments().get(0);
                ConceptExpression arg2 = leftAnd.getArguments().get(1);
                if (!ExpressionMarker.KnownToBeSimple(arg1)) {
                    ConceptExpression t;
                    t = arg1;
                    arg1 = arg2;
                    arg2 = t;
                }
                ConceptExpression newLeftPart = arg1;
                ConceptExpression newRightPart;
                if (arg2 instanceof ConceptTop) {
                    newRightPart = RightPart;
                } else {
                    newRightPart = conjunctiveQueryFolding.getpEM().or(
                            conjunctiveQueryFolding.getpEM().not(arg2), RightPart);
                }
                LeftPart = newLeftPart;
                RightPart = newRightPart;
            }
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getNominal")
    public String getNominal() {
        String longNominal = ((ConceptName) LeftPart).getName();
        int colon = longNominal.indexOf(":");
        return longNominal.substring(0, colon);
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getPhi")
    public ConceptExpression getPhi() {
        return RightPart;
    }
}
