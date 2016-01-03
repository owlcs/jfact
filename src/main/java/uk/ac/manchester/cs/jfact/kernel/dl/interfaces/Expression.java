package uk.ac.manchester.cs.jfact.kernel.dl.interfaces;

import org.semanticweb.owlapi.model.IRI;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

/** expression */
@SuppressWarnings("unused")
@PortedFrom(file = "tNAryQueue.h", name = "Expression")
public interface Expression extends Entity {

    @Override
    default IRI getIRI() {
        throw new UnsupportedOperationException();
    }

    /**
     * accept method for the visitor pattern
     * 
     * @param visitor
     *        visitor
     */
    @PortedFrom(file = "tDLExpression.h", name = "accept")
    default void accept(DLExpressionVisitor visitor) {}

    /**
     * @param visitor
     *        visitor
     * @param <O>
     *        visitor type
     * @return visitor value
     */
    @PortedFrom(file = "tDLExpression.h", name = "accept")
    default <O> O accept(DLExpressionVisitorEx<O> visitor) {
        return null;
    }
}
