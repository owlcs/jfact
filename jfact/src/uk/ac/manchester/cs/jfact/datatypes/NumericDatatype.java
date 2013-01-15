package uk.ac.manchester.cs.jfact.datatypes;


/** all Datatypes whose getNumeric() method returns true implement this interface */
public interface NumericDatatype<R extends Comparable<R>> extends Datatype<R>,
        OrderedDatatype<R> {
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
    R getMin();

    @Override
    R getMax();
}
