package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

class NumericDatatypeWrapper<O extends Comparable<O>> implements NumericDatatype<O>,
        OrderedDatatype<BigDecimal> {
    private final Datatype<O> d;

    public NumericDatatypeWrapper(Datatype<O> d) {
        this.d = d;
    }

    public boolean isExpression() {
        return this.d.isExpression();
    }

    public DatatypeExpression<O> asExpression() {
        return this.d.asExpression();
    }

    public Collection<Datatype<?>> getAncestors() {
        return this.d.getAncestors();
    }

    public boolean getBounded() {
        return this.d.getBounded();
    }

    public cardinality getCardinality() {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<Facet> getFacets() {
        return this.d.getFacets();
    }

    public Map<Facet, Object> getKnownFacetValues() {
        return this.d.getKnownFacetValues();
    }

    public <T extends Comparable<T>> T getFacetValue(Facet f) {
        return this.d.getFacetValue(f);
    }

    public BigDecimal getNumericFacetValue(Facet f) {
        return this.d.getNumericFacetValue(f);
    }

    public boolean getNumeric() {
        return this.d.getNumeric();
    }

    public ordered getOrdered() {
        return this.d.getOrdered();
    }

    public boolean isCompatible(Datatype<?> type) {
        return this.d.isCompatible(type);
    }

    public boolean isCompatible(Literal<?> l) {
        return this.d.isCompatible(l);
    }

    public boolean isInValueSpace(O l) {
        return this.d.isInValueSpace(l);
    }

    public O parseValue(String s) {
        return this.d.parseValue(s);
    }

    public Literal<O> buildLiteral(String s) {
        return this.d.buildLiteral(s);
    }

    public boolean isSubType(Datatype<?> type) {
        return this.d.isSubType(type);
    }

    public String getDatatypeURI() {
        return this.d.getDatatypeURI();
    }

    public Collection<Literal<O>> listValues() {
        return this.d.listValues();
    }

    public void accept(DLExpressionVisitor visitor) {
        this.d.accept(visitor);
    }

    public <T> T accept(DLExpressionVisitorEx<T> visitor) {
        return this.d.accept(visitor);
    }

    public boolean hasMinExclusive() {
        return this.d.getNumericFacetValue(minExclusive) != null;
    }

    public boolean hasMinInclusive() {
        return this.d.getNumericFacetValue(minInclusive) != null;
    }

    public boolean hasMaxExclusive() {
        return this.d.getNumericFacetValue(maxExclusive) != null;
    }

    public boolean hasMaxInclusive() {
        return this.d.getNumericFacetValue(maxInclusive) != null;
    }

    public boolean hasMin() {
        return this.hasMinInclusive() || this.hasMinExclusive();
    }

    public boolean hasMax() {
        return this.hasMaxInclusive() || this.hasMaxExclusive();
    }

    public BigDecimal getMin() {
        if (this.hasMinExclusive()) {
            return this.d.getNumericFacetValue(minExclusive);
        }
        if (this.hasMinInclusive()) {
            return this.d.getNumericFacetValue(minInclusive);
        }
        return null;
    }

    public BigDecimal getMax() {
        if (this.hasMaxExclusive()) {
            return this.d.getNumericFacetValue(maxExclusive);
        }
        if (this.hasMaxInclusive()) {
            return this.d.getNumericFacetValue(maxInclusive);
        }
        return null;
    }

    public boolean isNumericDatatype() {
        return true;
    }

    public NumericDatatype<O> asNumericDatatype() {
        return this;
    }

    public boolean isOrderedDatatype() {
        return true;
    }

    public OrderedDatatype<BigDecimal> asOrderedDatatype() {
        return this;
    }
}
