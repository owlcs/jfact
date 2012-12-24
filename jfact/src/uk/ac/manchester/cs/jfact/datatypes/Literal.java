package uk.ac.manchester.cs.jfact.datatypes;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;

/** @author ignazio
 * @param <T> */
public interface Literal<T extends Comparable<T>> extends DataExpression,
        Comparable<Literal<T>> {
    /** @return datatype */
    Datatype<T> getDatatypeExpression();

    /** @return value as string */
    String value();

    /** @return value */
    T typedValue();
}
