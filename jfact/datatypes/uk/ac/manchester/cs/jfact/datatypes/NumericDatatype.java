package uk.ac.manchester.cs.jfact.datatypes;

import java.math.BigDecimal;

/**
 * all Datatypes whose getNumeric() method returns true implement this interface
 */
public interface NumericDatatype<R extends Comparable<R>> extends Datatype<R>,
        OrderedDatatype<BigDecimal> {
    boolean hasMinExclusive();

    boolean hasMinInclusive();

    boolean hasMaxExclusive();

    boolean hasMaxInclusive();

    boolean hasMin();

    boolean hasMax();

    BigDecimal getMin();

    BigDecimal getMax();
}
