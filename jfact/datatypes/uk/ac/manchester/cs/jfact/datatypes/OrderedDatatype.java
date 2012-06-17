package uk.ac.manchester.cs.jfact.datatypes;

/**
 * all Datatypes whose getNumeric() method returns true implement this interface
 */
public interface OrderedDatatype<R extends Comparable<R>> {
	boolean hasMinExclusive();

	boolean hasMinInclusive();

	boolean hasMaxExclusive();

	boolean hasMaxInclusive();

	boolean hasMin();

	boolean hasMax();

	R getMin();

	R getMax();
}
