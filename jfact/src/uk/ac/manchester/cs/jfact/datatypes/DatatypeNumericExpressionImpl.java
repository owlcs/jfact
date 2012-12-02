package uk.ac.manchester.cs.jfact.datatypes;

import java.math.BigDecimal;

import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.ABSTRACT_NUMERIC_DATATYPE;

class DatatypeNumericExpressionImpl<O extends Comparable<O>> extends
        ABSTRACT_NUMERIC_DATATYPE<O> implements DatatypeExpression<O> {
    // TODO handle all value space restrictions in the delegations
    private final Datatype<O> host;

    public DatatypeNumericExpressionImpl(Datatype<O> b) {
        super(b.getDatatypeURI() + "_" + DatatypeFactory.getIndex(), b.getFacets());
        if (b.isExpression()) {
            this.host = b.asExpression().getHostType();
        } else {
            this.host = b;
        }
        ancestors = Utils.generateAncestors(this.host);
        knownFacetValues.putAll(b.getKnownFacetValues());
    }

    @Override
    public O parseValue(String s) {
        return this.host.parseValue(s);
    }

    @Override
    public Datatype<O> getHostType() {
        return this.host;
    }

    @Override
    public DatatypeExpression<O> addFacet(Facet f, Object value) {
        if (!facets.contains(f)) {
            throw new IllegalArgumentException("Facet " + f
                    + " not allowed tor datatype " + this.getHostType());
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        if (value instanceof Literal && !this.host.isCompatible((Literal<?>) value)) {
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
                this.host);
        toReturn.knownFacetValues.putAll(knownFacetValues);
        // cannot have noth min/maxInclusive and min/maxExclusive values, so
        // remove them if the feature is min/max
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

    @Override
    public boolean emptyValueSpace() {
        // TODO not checking string type value spaces; looks like the only
        // sensible way would be to check for 0 length constraints
        if (getNumeric()) {
            // remember whether it's inclusive or exclusive - needed to know if
            // the two extremes can be the same or not
            int excluded = 0;
            BigDecimal min = (BigDecimal) getFacetValue(Facets.minInclusive);
            if (min == null) {
                min = (BigDecimal) getFacetValue(Facets.minExclusive);
                excluded++;
            }
            BigDecimal max = (BigDecimal) getFacetValue(Facets.maxInclusive);
            if (max == null) {
                max = (BigDecimal) getFacetValue(Facets.maxExclusive);
                excluded++;
            }
            return DatatypeFactory.nonEmptyInterval(min, max, excluded);
        }
        return false;
    }

    @Override
    public boolean isNumericDatatype() {
        return this.host.isNumericDatatype();
    }

    @Override
    public NumericDatatype<O> asNumericDatatype() {
        return this;
    }

    @Override
    public boolean isOrderedDatatype() {
        return this.host.isOrderedDatatype();
    }

    @Override
    public OrderedDatatype<BigDecimal> asOrderedDatatype() {
        return this;
    }

    @Override
    public String toString() {
        return "numeric(" + this.host.toString() + "(extra facets:" + getMin() + " "
                + getMax() + "))";
    }
}
