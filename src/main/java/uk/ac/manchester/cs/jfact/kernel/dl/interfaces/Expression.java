package uk.ac.manchester.cs.jfact.kernel.dl.interfaces;

import javax.annotation.Nullable;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

/** expression */
@PortedFrom(file = "tNAryQueue.h", name = "Expression")
public interface Expression extends Entity {

    /**
     * accept method for the visitor pattern
     * 
     * @param visitor
     *        visitor
     */
    @PortedFrom(file = "tDLExpression.h", name = "accept")
        void accept(DLExpressionVisitor visitor);

    /**
     * @param visitor
     *        visitor
     * @param <O>
     *        visitor type
     * @return visitor value
     */
    @Nullable
    @PortedFrom(file = "tDLExpression.h", name = "accept")
    <O> O accept(DLExpressionVisitorEx<O> visitor);
}
