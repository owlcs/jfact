package uk.ac.manchester.cs.jfact.datatypes;

import java.util.Collection;

public class DatatypeNumericEnumeration<R extends Comparable<R>> extends
        DatatypeEnumeration<R> implements NumericDatatype<R> {
    public DatatypeNumericEnumeration(NumericDatatype<R> d) {
        super(d);
    }

    public DatatypeNumericEnumeration(NumericDatatype<R> d, Literal<R> l) {
        this(d);
        literals.add(l);
    }

    public DatatypeNumericEnumeration(NumericDatatype<R> d, Collection<Literal<R>> c) {
        this(d);
        literals.addAll(c);
    }

    @Override
    public DatatypeNumericEnumeration<R> add(Literal<R> d) {
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

    @Override
    public boolean hasMinExclusive() {
        return false;
    }

    @Override
    public boolean hasMinInclusive() {
        return !literals.isEmpty();
    }

    @Override
    public boolean hasMaxExclusive() {
        return false;
    }

    @Override
    public boolean hasMaxInclusive() {
        return !literals.isEmpty();
    }

    @Override
    public boolean hasMin() {
        return !literals.isEmpty();
    }

    @Override
    public boolean hasMax() {
        return !literals.isEmpty();
    }

    @Override
    public R getMin() {
        if (literals.isEmpty()) {
            return null;
        }
        return Facets.minInclusive.parseNumber(literals.get(0));
    }

    @Override
    public R getMax() {
        if (literals.isEmpty()) {
            return null;
        }
        return Facets.maxInclusive
                .parseNumber(literals.get(literals.size() - 1));
    }

    @Override
    public OrderedDatatype<R> asOrderedDatatype() {
        return this;
    }
}
