package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.util.Collection;

class DatatypeOrderedExpressionImpl<O extends Comparable<O>> extends ABSTRACT_DATATYPE<O>
        implements DatatypeExpression<O>, OrderedDatatype<O> {
    // TODO handle all value space restrictions in the delegations
    private final Datatype<O> host;

    public DatatypeOrderedExpressionImpl(Datatype<O> b) {
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
    public boolean isInValueSpace(O l) {
        if (this.hasMinExclusive()) {
            // to be in value space, ex min must be smaller than l
            if (l.compareTo(this.getMin()) <= 0) {
                return false;
            }
        }
        if (this.hasMinInclusive()) {
            // to be in value space, min must be smaller or equal to l
            if (l.compareTo(this.getMin()) < 0) {
                return false;
            }
        }
        if (this.hasMaxExclusive()) {
            // to be in value space, ex max must be bigger than l
            if (l.compareTo(this.getMax()) >= 0) {
                return false;
            }
        }
        if (this.hasMaxInclusive()) {
            // to be in value space, ex min must be smaller than l
            if (l.compareTo(this.getMax()) > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCompatible(Datatype<?> type) {
        // if (!super.isCompatible(type)) {
        // return false;
        // }
        if (type.equals(LITERAL)) {
            return true;
        }
        // if(isSubType(type)||type.isSubType(this)) {
        // return true;
        // }
        if (type.isOrderedDatatype()) {
            OrderedDatatype<O> wrapper = type.asOrderedDatatype();
            if (wrapper == null) {
                System.out.println("DatatypeOrderedExpressionImpl.isCompatible()");
            }
            // if both have no max or both have no min -> there is an
            // overlap
            // if one has no max, then min must be smaller than max of the
            // other
            // if one has no min, the max must be larger than min of the
            // other
            // if one has neither max nor min, they are compatible
            if (!this.hasMax() && !this.hasMin()) {
                return true;
            }
            if (!wrapper.hasMax() && !wrapper.hasMin()) {
                return true;
            }
            if (!this.hasMax() && !wrapper.hasMax()) {
                return true;
            }
            if (!this.hasMin() && !wrapper.hasMin()) {
                return true;
            }
            if (!this.hasMin()) {
                return this.overlapping(this, wrapper);
            }
            if (!this.hasMax()) {
                return this.overlapping(wrapper, this);
            }
            if (!wrapper.hasMin()) {
                return this.overlapping(wrapper, this);
            }
            if (!wrapper.hasMax()) {
                return this.overlapping(this, wrapper);
            }
            // compare their range facets:
            // disjoint if:
            // exclusives:
            // one minInclusive/exclusive is strictly larger than the other
            // maxinclusive/exclusive
            return this.overlapping(this, wrapper) || this.overlapping(wrapper, this);
        } else {
            return false;
        }
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
        DatatypeOrderedExpressionImpl<O> toReturn = new DatatypeOrderedExpressionImpl<O>(
                this.host);
        toReturn.knownFacetValues.putAll(knownFacetValues);
        // cannot have noth min/maxInclusive and min/maxExclusive values, so
        // remove them if the feature is min/max
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

    @Override
    public boolean emptyValueSpace() {
        // TODO not checking string type value spaces; looks like the only
        // sensible way would be to check for 0 length constraints
        if (this.getNumeric()) {
            // remember whether it's inclusive or exclusive - needed to know if
            // the two extremes can be the same or not
            int excluded = 0;
            BigDecimal min = (BigDecimal) getFacetValue(minInclusive);
            if (min == null) {
                min = (BigDecimal) getFacetValue(minExclusive);
                excluded++;
            }
            BigDecimal max = (BigDecimal) getFacetValue(maxInclusive);
            if (max == null) {
                max = (BigDecimal) getFacetValue(maxExclusive);
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

    @Override
    public boolean hasMinExclusive() {
        return knownFacetValues.containsKey(minExclusive);
    }

    @Override
    public boolean hasMinInclusive() {
        return knownFacetValues.containsKey(minInclusive);
    }

    @Override
    public boolean hasMaxExclusive() {
        return knownFacetValues.containsKey(maxExclusive);
    }

    @Override
    public boolean hasMaxInclusive() {
        return knownFacetValues.containsKey(maxInclusive);
    }

    @Override
    public boolean hasMin() {
        return this.hasMinInclusive() || this.hasMinExclusive();
    }

    @Override
    public boolean hasMax() {
        return this.hasMaxInclusive() || this.hasMaxExclusive();
    }

    @Override
    public O getMin() {
        if (this.hasMinInclusive()) {
            Object object = knownFacetValues.get(minInclusive);
            if (object instanceof Literal) {
                return ((Literal<O>) object).typedValue();
            }
            return (O) object;
        }
        if (this.hasMinExclusive()) {
            Object object = knownFacetValues.get(minExclusive);
            if (object instanceof Literal) {
                return ((Literal<O>) object).typedValue();
            }
            return (O) object;
        }
        return null;
    }

    @Override
    public O getMax() {
        if (this.hasMaxInclusive()) {
            Object object = knownFacetValues.get(maxInclusive);
            if (object instanceof Literal) {
                return ((Literal<O>) object).typedValue();
            }
            return (O) object;
        }
        if (this.hasMaxExclusive()) {
            Object object = knownFacetValues.get(maxExclusive);
            if (object instanceof Literal) {
                return ((Literal<O>) object).typedValue();
            }
            return (O) object;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(" + this.host.toString() + "(extra facets:"
                + knownFacetValues + "))";
    }
}
