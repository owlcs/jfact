package uk.ac.manchester.cs.jfact.kernel.dl.axioms;

import javax.annotation.Nonnull;

import org.semanticweb.owlapitools.decomposition.AxiomWrapper;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;

/**
 * Axiom utilities.
 */
public class Axioms {

    @Nonnull private static final Expression DUMMY_EXPRESSION = new Expression() {};
    @Nonnull private static final AxiomWrapper DUMMY = new AxiomWrapper(null);

    private Axioms() {}

    /** @return a dummy implementation of Expression */
    public static Expression dummyExpression() {
        return DUMMY_EXPRESSION;
    }

    /** @return a dummy implementation of AxiomInterface */
    public static AxiomWrapper dummy() {
        return DUMMY;
    }
}
