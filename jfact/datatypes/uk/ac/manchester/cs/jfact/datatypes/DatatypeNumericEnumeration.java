package uk.ac.manchester.cs.jfact.datatypes;

import java.math.BigDecimal;
import java.util.Collection;

public class DatatypeNumericEnumeration<R extends Comparable<R>> extends
		DatatypeEnumeration<R> implements NumericDatatype<R> {
	public DatatypeNumericEnumeration(final NumericDatatype<R> d) {
		super(d);
	}

	public DatatypeNumericEnumeration(final NumericDatatype<R> d, final Literal<R> l) {
		this(d);
		literals.add(l);
	}

	public DatatypeNumericEnumeration(final NumericDatatype<R> d,
			final Collection<Literal<R>> c) {
		this(d);
		literals.addAll(c);
	}

	@Override
	public DatatypeNumericEnumeration<R> add(final Literal<R> d) {
		DatatypeNumericEnumeration<R> toReturn = new DatatypeNumericEnumeration<R>(
				(NumericDatatype<R>) host, literals);
		toReturn.literals.add(d);
		return toReturn;
	}

	@Override
	public boolean isNumericDatatype() {
		return true;
	}

	@Override
	public NumericDatatype<R> asNumericDatatype() {
		return this;
	}

	@Override
	public boolean isOrderedDatatype() {
		return true;
	}

	public boolean hasMinExclusive() {
		return false;
	}

	public boolean hasMinInclusive() {
		return !literals.isEmpty();
	}

	public boolean hasMaxExclusive() {
		return false;
	}

	public boolean hasMaxInclusive() {
		return !literals.isEmpty();
	}

	public boolean hasMin() {
		return !literals.isEmpty();
	}

	public boolean hasMax() {
		return !literals.isEmpty();
	}

	public BigDecimal getMin() {
		if (literals.isEmpty()) {
			return null;
		}
		return Facets.minInclusive.parseNumber(literals.get(0));
	}

	public BigDecimal getMax() {
		if (literals.isEmpty()) {
			return null;
		}
		return Facets.maxInclusive.parseNumber(literals.get(literals.size() - 1));
	}

	@Override
	public <O extends Comparable<O>> OrderedDatatype<O> asOrderedDatatype() {
		// TODO Auto-generated method stub
		return (OrderedDatatype<O>) this;
	}
}
