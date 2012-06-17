package uk.ac.manchester.cs.jfact.datatypes;

import java.math.BigDecimal;
import java.util.Collection;

class DatatypeExpressionImpl<O extends Comparable<O>> extends ABSTRACT_DATATYPE<O>
		implements DatatypeExpression<O> {
	//TODO handle all value space restrictions in the delegations
	private final Datatype<O> host;

	public DatatypeExpressionImpl(final Datatype<O> b) {
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

	@Override
	public boolean isInValueSpace(final O l) {
		return host.isInValueSpace(l);
	}

	@Override
	public ordered getOrdered() {
		return host.getOrdered();
	}

	@Override
	public boolean getNumeric() {
		return host.getNumeric();
	}

	@Override
	public cardinality getCardinality() {
		return host.getCardinality();
	}

	@Override
	public boolean getBounded() {
		return host.getBounded();
	}

	@Override
	public Collection<Literal<O>> listValues() {
		return host.listValues();
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
		DatatypeExpressionImpl<O> toReturn = new DatatypeExpressionImpl<O>(host);
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
		toReturn.knownFacetValues.put(f, value);
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
		return (NumericDatatype<O>) this;
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
		return getClass().getName() + "(" + host.toString() + "(extra facets:"
				+ knownFacetValues + "))";
	}

	@Override
	public boolean equals(final Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (obj instanceof DatatypeExpression) {
			final DatatypeExpression<?> datatypeExpression = (DatatypeExpression<?>) obj;
			return this.host.equals(datatypeExpression.getHostType())
					&& knownFacetValues.equals(datatypeExpression.getKnownFacetValues());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return host.hashCode() + knownFacetValues.hashCode();
	}
}
