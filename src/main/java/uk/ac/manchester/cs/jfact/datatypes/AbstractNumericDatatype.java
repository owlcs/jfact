package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.util.Set;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.HasIRI;

abstract class AbstractNumericDatatype<R extends Comparable<R>> extends AbstractDatatype<R> implements
    NumericDatatype<R> {

    public AbstractNumericDatatype(HasIRI uri, Set<Facet> f, Set<Datatype<?>> ancestors) {
        super(uri, f, ancestors);
    }

    @Override
    public boolean getNumeric() {
        return true;
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

    @Override
    public OrderedDatatype<R> asOrderedDatatype() {
        return this;
    }

    @Override
    public ordered getOrdered() {
        return ordered.PARTIAL;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isInValueSpace(R lit) {
        if (this.hasMinExclusive()) {
            // to be in value space, ex min must be smaller than l
            Comparable<R> l = (Comparable<R>) minExclusive.parseNumber(lit);
            if (l.compareTo(this.getMin()) <= 0) {
                return false;
            }
        }
        if (this.hasMinInclusive()) {
            Comparable<R> l = (Comparable<R>) minExclusive.parseNumber(lit);
            // to be in value space, min must be smaller or equal to l
            if (l.compareTo(this.getMin()) < 0) {
                return false;
            }
        }
        if (this.hasMaxExclusive()) {
            Comparable<R> l = (Comparable<R>) minExclusive.parseNumber(lit);
            // to be in value space, ex max must be bigger than l
            if (l.compareTo(this.getMax()) >= 0) {
                return false;
            }
        }
        if (this.hasMaxInclusive()) {
            Comparable<R> l = (Comparable<R>) minExclusive.parseNumber(lit);
            // to be in value space, ex min must be smaller than l
            if (l.compareTo(this.getMax()) > 0) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isCompatible(Datatype<?> type) {
        if (type.equals(LITERAL)) {
            return true;
        }
        if (type.getNumeric()) {
            // specific cases: float and double have overlapping value
            // spaces with all numerics but are not compatible with any
            if (type.equals(FLOAT) || type.equals(DOUBLE)) {
                return super.isCompatible(type);
            }
            NumericDatatype<R> wrapper;
            if (type instanceof NumericDatatype) {
                wrapper = (NumericDatatype<R>) type;
            } else {
                wrapper = AbstractNumericDatatype.wrap((Datatype<R>) type);
            }
            // then both types are numeric
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
                return overlapping(this, wrapper);
            }
            if (!this.hasMax()) {
                return overlapping(wrapper, this);
            }
            if (!wrapper.hasMin()) {
                return overlapping(wrapper, this);
            }
            if (!wrapper.hasMax()) {
                return overlapping(this, wrapper);
            }
            // compare their range facets:
            // disjoint if:
            // exclusives:
            // one minInclusive/exclusive is strictly larger than the other
            // maxinclusive/exclusive
            return overlapping(this, wrapper) || overlapping(wrapper, this);
        } else {
            return false;
        }
    }

    @Override
    public boolean emptyValueSpace() {
        if (!hasMin() || !hasMax()) {
            return false;
        }
        if (hasMaxExclusive() && hasMinExclusive()) {
            return getMax().compareTo((R) increase((Number) getMin())) < 0;
        }
        return getMax().compareTo(getMin()) < 0;
    }

    private static <O extends Comparable<O>> NumericDatatype<O> wrap(Datatype<O> d) {
        return new NumericDatatypeWrapper<>(d);
    }

    @Override
    public boolean hasMinExclusive() {
        return knownNumericFacetValues.containsKey(minExclusive);
    }

    @Override
    public boolean hasMinInclusive() {
        return knownNumericFacetValues.containsKey(minInclusive);
    }

    @Override
    public boolean hasMaxExclusive() {
        return knownNumericFacetValues.containsKey(maxExclusive);
    }

    @Override
    public boolean hasMaxInclusive() {
        return knownNumericFacetValues.containsKey(maxInclusive);
    }

    @Override
    public boolean hasMin() {
        return this.hasMinInclusive() || this.hasMinExclusive();
    }

    @Override
    public boolean hasMax() {
        return this.hasMaxInclusive() || this.hasMaxExclusive();
    }

    @Nullable
    @Override
    public R getMin() {
        if (this.hasMinExclusive()) {
            return (R) knownNumericFacetValues.get(minExclusive);
        }
        if (this.hasMinInclusive()) {
            return (R) knownNumericFacetValues.get(minInclusive);
        }
        return null;
    }

    @Nullable
    @Override
    public R getMax() {
        if (this.hasMaxExclusive()) {
            return (R) knownNumericFacetValues.get(maxExclusive);
        }
        if (this.hasMaxInclusive()) {
            return (R) knownNumericFacetValues.get(maxInclusive);
        }
        return null;
    }
}
