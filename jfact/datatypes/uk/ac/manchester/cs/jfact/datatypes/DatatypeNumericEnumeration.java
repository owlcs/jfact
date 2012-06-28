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
        this.literals.add(l);
    }

    public DatatypeNumericEnumeration(final NumericDatatype<R> d,
            final Collection<Literal<R>> c) {
        this(d);
        this.literals.addAll(c);
    }

    @Override
    public DatatypeNumericEnumeration<R> add(final Literal<R> d) {
        DatatypeNumericEnumeration<R> toReturn = new DatatypeNumericEnumeration<R>(
                (NumericDatatype<R>) this.host, this.literals);
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
        return !this.literals.isEmpty();
    }

    public boolean hasMaxExclusive() {
        return false;
    }

    public boolean hasMaxInclusive() {
        return !this.literals.isEmpty();
    }

    public boolean hasMin() {
        return !this.literals.isEmpty();
    }

    public boolean hasMax() {
        return !this.literals.isEmpty();
    }

    public BigDecimal getMin() {
        if (this.literals.isEmpty()) {
            return null;
        }
        return (BigDecimal) Facets.minInclusive.parseNumber(this.literals.get(0));
    }

    public BigDecimal getMax() {
        if (this.literals.isEmpty()) {
            return null;
        }
        return (BigDecimal) Facets.maxInclusive.parseNumber(this.literals
                .get(this.literals.size() - 1));
    }

    @Override
    public OrderedDatatype<BigDecimal> asOrderedDatatype() {
        return this;
    }
}
