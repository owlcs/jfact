package uk.ac.manchester.cs.jfact.datatypes;

/** all literals whose datatype is numeric */
public class NumericLiteralImpl<R extends Comparable<R>> extends LiteralImpl<R> implements
		NumericLiteral<R> {
	public NumericLiteralImpl(final NumericDatatype<R> type, final String value) {
		super(type, value);
	}
}
