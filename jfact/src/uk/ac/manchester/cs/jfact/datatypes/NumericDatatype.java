package uk.ac.manchester.cs.jfact.datatypes;

import java.math.BigDecimal;

/** all Datatypes whose getNumeric() method returns true implement this interface */
public interface NumericDatatype<R extends Comparable<R>> extends Datatype<R>,
        OrderedDatatype<BigDecimal> {
    @Override
    boolean hasMinExclusive();

    @Override
    boolean hasMinInclusive();

    @Override
    boolean hasMaxExclusive();

    @Override
    boolean hasMaxInclusive();

    @Override
    boolean hasMin();

    @Override
    boolean hasMax();

    @Override
    BigDecimal getMin();

    @Override
    BigDecimal getMax();
}
