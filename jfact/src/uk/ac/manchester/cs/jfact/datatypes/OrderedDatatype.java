package uk.ac.manchester.cs.jfact.datatypes;

/** all Datatypes whose getNumeric() method returns true implement this interface
 * 
 * @param <R> */
public interface OrderedDatatype<R extends Comparable<R>> {
    /** @return true if min exclusive facet applies */
    boolean hasMinExclusive();

    /** @return true if min inclusive facet applies */
    boolean hasMinInclusive();

    /** @return true if max exclusive facet applies */
    boolean hasMaxExclusive();

    /** @return true if max inclusive facet applies */
    boolean hasMaxInclusive();

    /** @return true if min exists */
    boolean hasMin();

    /** @return true if max exists */
    boolean hasMax();

    /** @return value for min */
    R getMin();

    /** @return value for max */
    R getMax();
}
