package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

class NumericDatatypeWrapper<O extends Comparable<O>> implements NumericDatatype<O>,
        OrderedDatatype<O> {
    private final Datatype<O> d;

    public NumericDatatypeWrapper(Datatype<O> d) {
        this.d = d;
    }

    @Override
    public boolean isExpression() {
        return this.d.isExpression();
    }

    @Override
    public DatatypeExpression<O> asExpression() {
        return this.d.asExpression();
    }

    @Override
    public Collection<Datatype<?>> getAncestors() {
        return this.d.getAncestors();
    }

    @Override
    public boolean getBounded() {
        return this.d.getBounded();
    }

    @Override
    public cardinality getCardinality() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Facet> getFacets() {
        return this.d.getFacets();
    }

    @Override
    public Map<Facet, Comparable> getKnownNonNumericFacetValues() {
        return this.d.getKnownNonNumericFacetValues();
    }

    @Override
    public Map<Facet, Comparable> getKnownNumericFacetValues() {
        return this.d.getKnownNumericFacetValues();
    }

    @Override
    public <T extends Comparable<T>> T getFacetValue(Facet f) {
        return this.d.getFacetValue(f);
    }

    @Override
    public Comparable getNumericFacetValue(Facet f) {
        return this.d.getNumericFacetValue(f);
    }

    @Override
    public boolean getNumeric() {
        return this.d.getNumeric();
    }

    @Override
    public ordered getOrdered() {
        return this.d.getOrdered();
    }

    @Override
    public boolean isCompatible(Datatype<?> type) {
        return this.d.isCompatible(type);
    }

    @Override
    public boolean isCompatible(Literal<?> l) {
        return this.d.isCompatible(l);
    }

    @Override
    public boolean isInValueSpace(O l) {
        return this.d.isInValueSpace(l);
    }

    @Override
    public O parseValue(String s) {
        return this.d.parseValue(s);
    }

    @Override
    public Literal<O> buildLiteral(String s) {
        return this.d.buildLiteral(s);
    }

    @Override
    public boolean isSubType(Datatype<?> type) {
        return this.d.isSubType(type);
    }

    @Override
    public String getDatatypeURI() {
        return this.d.getDatatypeURI();
    }

    @Override
    public Collection<Literal<O>> listValues() {
        return this.d.listValues();
    }

    @Override
    public void accept(DLExpressionVisitor visitor) {
        this.d.accept(visitor);
    }

    @Override
    public <T> T accept(DLExpressionVisitorEx<T> visitor) {
        return this.d.accept(visitor);
    }

    @Override
    public boolean hasMinExclusive() {
        return this.d.getNumericFacetValue(minExclusive) != null;
    }

    @Override
    public boolean hasMinInclusive() {
        return this.d.getNumericFacetValue(minInclusive) != null;
    }

    @Override
    public boolean hasMaxExclusive() {
        return this.d.getNumericFacetValue(maxExclusive) != null;
    }

    @Override
    public boolean hasMaxInclusive() {
        return this.d.getNumericFacetValue(maxInclusive) != null;
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
        if (this.hasMinExclusive()) {
            return (O) this.d.getNumericFacetValue(minExclusive);
        }
        if (this.hasMinInclusive()) {
            return (O) this.d.getNumericFacetValue(minInclusive);
        }
        return null;
    }

    @Override
    public O getMax() {
        if (this.hasMaxExclusive()) {
            return (O) this.d.getNumericFacetValue(maxExclusive);
        }
        if (this.hasMaxInclusive()) {
            return (O) this.d.getNumericFacetValue(maxInclusive);
        }
        return null;
    }

    @Override
    public boolean isNumericDatatype() {
        return true;
    }

    @Override
    public NumericDatatype<O> asNumericDatatype() {
        return this;
    }

    @Override
    public boolean isOrderedDatatype() {
        return true;
    }

    @Override
    public OrderedDatatype<O> asOrderedDatatype() {
        return this;
    }

    @Override
    public boolean emptyValueSpace() {
        return d.emptyValueSpace();
    }
}
