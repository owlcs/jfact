package uk.ac.manchester.cs.jfact.kernel.dl.axioms;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

/**
 * Axiom utilities.
 */
public class Axioms {

    private Axioms() {}

    @Nonnull private static final Expression DUMMY_EXPRESSION = new Expression() {

        @Override
        public IRI getIRI() {
            return null;
        }

        @SuppressWarnings("null")
        @Override
        public <O> O accept(DLExpressionVisitorEx<O> visitor) {
            return null;
        }

        @Override
        public void accept(DLExpressionVisitor visitor) {}
    };
    @Nonnull private static final AxiomInterface DUMMY = new AxiomInterface() {};

    /** @return a dummy implementation of Expression */
    public static Expression dummyExpression() {
        return DUMMY_EXPRESSION;
    }

    /** @return a dummy implementation of AxiomInterface */
    public static AxiomInterface dummy() {
        return DUMMY;
    }
}
