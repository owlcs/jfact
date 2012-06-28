package uk.ac.manchester.cs.jfact.datatypes;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;

public interface Literal<T extends Comparable<T>> extends DataExpression,
        Comparable<Literal<T>> {
    Datatype<T> getDatatypeExpression();

    String value();

    T typedValue();
}
