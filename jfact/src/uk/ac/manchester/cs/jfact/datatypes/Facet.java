package uk.ac.manchester.cs.jfact.datatypes;

import java.math.BigDecimal;

/** @author ignazio */
public interface Facet {
    /** This is a convenience method that enables each facet to parse its values
     * without client code having to worry whether the values are literals or
     * strings - the typing is known to the facet implementation
     * 
     * @param value
     * @return value that is assigned to this facet */
    Comparable<BigDecimal> parseNumber(Object value);

    /** @param value
     * @return the typed value for value */
    Comparable<?> parse(Object value);

    /** @return true if number facet */
    boolean isNumberFacet();

    /** @return the facet uri */
    String getURI();
}
