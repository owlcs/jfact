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
            this.host = b.asExpression().getHostType();
        } else {
            this.host = b;
        }
        this.ancestors = Utils.generateAncestors(this.host);
        this.knownFacetValues.putAll(b.getKnownFacetValues());
    }

    public O parseValue(final String s) {
        return this.host.parseValue(s);
    }

    @Override
    public boolean isInValueSpace(final O l) {
        if (!this.host.isInValueSpace(l)) {
            return false;
        }
        return this.host.isInValueSpace(l);
    }

    @Override
    public ordered getOrdered() {
        return this.host.getOrdered();
    }

    @Override
    public boolean getNumeric() {
        return this.host.getNumeric();
    }

    @Override
    public cardinality getCardinality() {
        return this.host.getCardinality();
    }

    @Override
    public boolean getBounded() {
        return this.host.getBounded();
    }

    @Override
    public Collection<Literal<O>> listValues() {
        return this.host.listValues();
    }

    public Datatype<O> getHostType() {
        return this.host;
    }

    public DatatypeExpression<O> addFacet(final Facet f, final Object value) {
        if (!this.facets.contains(f)) {
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
        DatatypeOrderedExpressionImpl<O> toReturn = new DatatypeOrderedExpressionImpl<O>(
                this.host);
        toReturn.knownFacetValues.putAll(this.knownFacetValues);
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
        if (this.getNumeric()) {
            // remember whether it's inclusive or exclusive - needed to know if the two extremes can be the same or not
            int excluded = 0;
            BigDecimal min = (BigDecimal) this.getFacetValue(minInclusive);
            if (min == null) {
                min = (BigDecimal) this.getFacetValue(minExclusive);
                excluded++;
            }
            BigDecimal max = (BigDecimal) this.getFacetValue(maxInclusive);
            if (max == null) {
                max = (BigDecimal) this.getFacetValue(maxExclusive);
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
        return null;
    }

    @Override
    public boolean isOrderedDatatype() {
        return this.host.isOrderedDatatype();
    }

    @Override
    public OrderedDatatype<O> asOrderedDatatype() {
        return this;
    }

    public boolean hasMinExclusive() {
        return this.knownFacetValues.containsKey(minExclusive);
    }

    public boolean hasMinInclusive() {
        return this.knownFacetValues.containsKey(minInclusive);
    }

    public boolean hasMaxExclusive() {
        return this.knownFacetValues.containsKey(maxExclusive);
    }

    public boolean hasMaxInclusive() {
        return this.knownFacetValues.containsKey(maxInclusive);
    }

    public boolean hasMin() {
        return this.hasMinInclusive() || this.hasMinExclusive();
    }

    public boolean hasMax() {
        return this.hasMaxInclusive() || this.hasMaxExclusive();
    }

    public O getMin() {
        if (this.hasMinInclusive()) {
            return (O) this.knownFacetValues.get(minInclusive);
        }
        if (this.hasMinExclusive()) {
            return (O) this.knownFacetValues.get(minExclusive);
        }
        return null;
    }

    public O getMax() {
        if (this.hasMaxInclusive()) {
            return (O) this.knownFacetValues.get(maxInclusive);
        }
        if (this.hasMaxExclusive()) {
            return (O) this.knownFacetValues.get(maxExclusive);
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(" + this.host.toString() + "(extra facets:"
                + this.knownFacetValues + "))";
    }
}
