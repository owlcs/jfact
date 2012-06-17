package uk.ac.manchester.cs.jfact.datatypes;

import java.math.BigDecimal;

public interface Facet {
	/**
	 * This is a convenience method that enables each facet to parse its values
	 * without client code having to worry whether the values are literals or
	 * strings - the typing is known to the facet implementation
	 * 
	 * @return value that is assigned to this facet
	 */
	BigDecimal parseNumber(Object value);

	Object parse(Object value);

	boolean isNumberFacet();

	String getURI();
}
