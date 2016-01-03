package uk.ac.manchester.cs.jfact.kernel.dl.axioms;

import javax.annotation.Nonnull;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;

/**
 * Axiom utilities.
 */
public class Axioms {

    @Nonnull private static final Expression DUMMY_EXPRESSION = new Expression() {};
    @Nonnull private static final AxiomInterface DUMMY = new AxiomInterface() {};

    private Axioms() {}

    /** @return a dummy implementation of Expression */
    public static Expression dummyExpression() {
        return DUMMY_EXPRESSION;
    }

    /** @return a dummy implementation of AxiomInterface */
    public static AxiomInterface dummy() {
        return DUMMY;
    }
}
