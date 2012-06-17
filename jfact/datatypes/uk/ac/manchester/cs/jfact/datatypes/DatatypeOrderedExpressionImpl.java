package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.util.Collection;

class DatatypeOrderedExpressionImpl<O extends Comparable<O>> extends ABSTRACT_DATATYPE<O>
		implements DatatypeExpression<O>, OrderedDatatype<O> {
	//TODO handle all value space restrictions in the delegations
	private final Datatype<O> host;

	public DatatypeOrderedExpressionImpl(final Datatype<O> b) {
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
		if (!host.isInValueSpace(l)) {
			return false;
		}
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
		if (value instanceof Literal && !host.isCompatible((Literal<?>) value)) {
			throw new IllegalArgumentException("Not a valid value for this expression: "
					+ f + "\t" + value + " for: " + this);
		}
		DatatypeOrderedExpressionImpl<O> toReturn = new DatatypeOrderedExpressionImpl<O>(
				host);
		toReturn.knownFacetValues.putAll(knownFacetValues);
		// cannot have noth min/maxInclusive and min/maxExclusive values, so remove them if the feature is min/max
		if (f.equals(minExclusive) || f.equals(minInclusive)) {
			toReturn.knownFacetValues.remove(minExclusive);
			toReturn.knownFacetValues.remove(minInclusive);
		}
		if (f.equals(maxExclusive) || f.equals(maxInclusive)) {
			toReturn.knownFacetValues.remove(maxExclusive);
			toReturn.knownFacetValues.remove(maxInclusive);
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
			BigDecimal min = getFacetValue(minInclusive);
			if (min == null) {
				min = getFacetValue(minExclusive);
				excluded++;
			}
			BigDecimal max = getFacetValue(maxInclusive);
			if (max == null) {
				max = getFacetValue(maxExclusive);
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
	public OrderedDatatype<O> asOrderedDatatype() {
		return this;
	}

	public boolean hasMinExclusive() {
		return knownFacetValues.containsKey(minExclusive);
	}

	public boolean hasMinInclusive() {
		return knownFacetValues.containsKey(minInclusive);
	}

	public boolean hasMaxExclusive() {
		return knownFacetValues.containsKey(maxExclusive);
	}

	public boolean hasMaxInclusive() {
		return knownFacetValues.containsKey(maxInclusive);
	}

	public boolean hasMin() {
		return hasMinInclusive() || hasMinExclusive();
	}

	public boolean hasMax() {
		return hasMaxInclusive() || hasMaxExclusive();
	}

	public O getMin() {
		if (hasMinInclusive()) {
			return (O) knownFacetValues.get(minInclusive);
		}
		if (hasMinExclusive()) {
			return (O) knownFacetValues.get(minExclusive);
		}
		return null;
	}

	public O getMax() {
		if (hasMaxInclusive()) {
			return (O) knownFacetValues.get(maxInclusive);
		}
		if (hasMaxExclusive()) {
			return (O) knownFacetValues.get(maxExclusive);
		}
		return null;
	}

	@Override
	public String toString() {
		return getClass().getName() + "(" + host.toString() + "(extra facets:"
				+ knownFacetValues + "))";
	}
}
