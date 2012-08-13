package uk.ac.manchester.cs.jfact.datatypes;

import java.math.BigDecimal;
import java.util.*;

import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

public class DatatypeNegation<R extends Comparable<R>> implements Datatype<R>,
        DatatypeExpression<R> {
    private final Datatype<R> host;
    private final String uri;

    public DatatypeNegation(Datatype<R> d) {
        this.uri = "neg#" + DatatypeFactory.getIndex();
        host = d;
    }

    public boolean isExpression() {
        return true;
    }

    public DatatypeExpression<R> asExpression() {
        return this;
    }

    public Collection<Datatype<?>> getAncestors() {
        return host.getAncestors();
    }

    public boolean getBounded() {
        return host.getBounded();
    }

    public cardinality getCardinality() {
        return host.getCardinality();
    }

    public Set<Facet> getFacets() {
        return host.getFacets();
    }

    public Map<Facet, Object> getKnownFacetValues() {
        return host.getKnownFacetValues();
    }

    public <O extends Comparable<O>> O getFacetValue(Facet f) {
        return host.getFacetValue(f);
    }

    public BigDecimal getNumericFacetValue(Facet f) {
        return host.getNumericFacetValue(f);
    }

    public boolean getNumeric() {
        return host.getNumeric();
    }

    public ordered getOrdered() {
        return host.getOrdered();
    }

    public boolean isCompatible(Literal<?> l) {
        return !host.isCompatible(l);
    }

    public boolean isInValueSpace(R l) {
        return !host.isInValueSpace(l);
    }

    public R parseValue(String s) {
        // delegated to the host type
        return host.parseValue(s);
    }

    public Literal<R> buildLiteral(String s) {
        return host.buildLiteral(s);
    }

    public boolean isSubType(Datatype<?> type) {
        return host.isSubType(type);
    }

    public String getDatatypeURI() {
        return uri;
    }

    public boolean isCompatible(Datatype<?> type) {
        if (type instanceof DatatypeNegation) {
            return !host.isCompatible(((DatatypeNegation<?>) type).host);
        }
        return !host.isCompatible(type);
    }

    public void accept(DLExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public <O> O accept(DLExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public Collection<Literal<R>> listValues() {
        List<Literal<R>> toReturn = new ArrayList<Literal<R>>(host.listValues());
        for (int i = 0; i < toReturn.size();) {
            if (host.isCompatible(toReturn.get(i))) {
                toReturn.remove(i);
            } else {
                i++;
            }
        }
        return toReturn;
    }

    public boolean isNumericDatatype() {
        return host.isNumericDatatype();
    }

    public NumericDatatype<R> asNumericDatatype() {
        return new NumericDatatypeWrapper<R>(this);
    }

    public boolean isOrderedDatatype() {
        return host.isOrderedDatatype();
    }

    public <O extends Comparable<O>> OrderedDatatype<O> asOrderedDatatype() {
        return (OrderedDatatype<O>) this;
    }

    @Override
    public String toString() {
        return uri + "{" + host + "}";
    }

    public Datatype<R> getHostType() {
        return host.isExpression() ? host.asExpression().getHostType() : host;
    }

    public DatatypeExpression<R> addFacet(Facet f, Object value) {
        System.out
                .println("DatatypeNegation.addFacet() Cannot add a facet to a negation; modify the base type and rebuild a new negation. Returning the same object");
        return this;
    }

    public boolean emptyValueSpace() {
        if (!host.isExpression()) {
            return true;
        }
        // TODO verify the cases where restrictions on the host actually bracket
        // the value space, so its opposite is empty
        return false;
    }
}
