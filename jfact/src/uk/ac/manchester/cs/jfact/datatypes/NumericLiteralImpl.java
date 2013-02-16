package uk.ac.manchester.cs.jfact.datatypes;

/** all literals whose datatype is numeric
 * 
 * @param <R> */
public class NumericLiteralImpl<R extends Comparable<R>> extends LiteralImpl<R> implements
        NumericLiteral<R> {
    /** @param type
     * @param value */
    public NumericLiteralImpl(NumericDatatype<R> type, String value) {
        super(type, value);
    }
}
