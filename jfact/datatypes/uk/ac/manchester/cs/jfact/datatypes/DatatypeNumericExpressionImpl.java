package uk.ac.manchester.cs.jfact.datatypes;

import java.math.BigDecimal;

import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.ABSTRACT_NUMERIC_DATATYPE;

class DatatypeNumericExpressionImpl<O extends Comparable<O>> extends
		ABSTRACT_NUMERIC_DATATYPE<O> implements DatatypeExpression<O> {
	//TODO handle all value space restrictions in the delegations
	private final Datatype<O> host;

	public DatatypeNumericExpressionImpl(final Datatype<O> b) {
		super(b.getDatatypeURI() + "_" + DatatypeFactory.getIndex(), b.getFacets());
		if (b.isExpression()) {
			host = b.asExpression().getHostType();
		} else {
			host = b;
		}
		ancestors = Utils.generateAncestors(host);
		knownFacetValues.putAll(b.getKnownFacetValues());
	}

	public O parseValue(final String s) {
		return host.parseValue(s);
	}

	public Datatype<O> getHostType() {
		return host;
	}

	public DatatypeExpression<O> addFacet(final Facet f, final Object value) {
		if (!facets.contains(f)) {
			throw new IllegalArgumentException("Facet " + f
					+ " not allowed tor datatype " + getHostType());
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		if (value instanceof Literal && !host.isCompatible((Literal<?>) value)) {
			throw new IllegalArgumentException("Not a valid value for this expression: "
					+ f + "\t" + value + " for: " + this);
		}
		Object v;
		if (f.isNumberFacet()) {
			v = f.parseNumber(value);
		} else {
			v = f.parse(value);
		}
		DatatypeNumericExpressionImpl<O> toReturn = new DatatypeNumericExpressionImpl<O>(
				host);
		toReturn.knownFacetValues.putAll(knownFacetValues);
		// cannot have noth min/maxInclusive and min/maxExclusive values, so remove them if the feature is min/max
		if (f.equals(Facets.minExclusive) || f.equals(Facets.minInclusive)) {
			toReturn.knownFacetValues.remove(Facets.minExclusive);
			toReturn.knownFacetValues.remove(Facets.minInclusive);
		}
		if (f.equals(Facets.maxExclusive) || f.equals(Facets.maxInclusive)) {
			toReturn.knownFacetValues.remove(Facets.maxExclusive);
			toReturn.knownFacetValues.remove(Facets.maxInclusive);
		}
		toReturn.knownFacetValues.put(f, v);
		return toReturn;
	}

	@Override
	public boolean isExpression() {
		return true;
	}

	public boolean emptyValueSpace() {
		// TODO not checking string type value spaces; looks like the only sensible way would be to check for 0 length constraints
		if (getNumeric()) {
			// remember whether it's inclusive or exclusive - needed to know if the two extremes can be the same or not
			int excluded = 0;
			BigDecimal min = getFacetValue(Facets.minInclusive);
			if (min == null) {
				min = getFacetValue(Facets.minExclusive);
				excluded++;
			}
			BigDecimal max = getFacetValue(Facets.maxInclusive);
			if (max == null) {
				max = getFacetValue(Facets.maxExclusive);
				excluded++;
			}
			return DatatypeFactory.nonEmptyInterval(min, max, excluded);
		}
		return false;
	}

	@Override
	public boolean isNumericDatatype() {
		return host.isNumericDatatype();
	}

	@Override
	public NumericDatatype<O> asNumericDatatype() {
		return this;
	}

	@Override
	public boolean isOrderedDatatype() {
		return host.isOrderedDatatype();
	}

	@Override
	public <T extends Comparable<T>> OrderedDatatype<T> asOrderedDatatype() {
		return (OrderedDatatype<T>) this;
	}

	@Override
	public String toString() {
		return "numeric(" + host.toString() + "(extra facets:" + getMin() + " "
				+ getMax() + "))";
	}
}
