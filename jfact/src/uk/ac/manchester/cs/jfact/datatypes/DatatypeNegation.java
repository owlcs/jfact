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

    @Override
    public boolean isExpression() {
        return true;
    }

    @Override
    public DatatypeExpression<R> asExpression() {
        return this;
    }

    @Override
    public Collection<Datatype<?>> getAncestors() {
        return host.getAncestors();
    }

    @Override
    public boolean getBounded() {
        return host.getBounded();
    }

    @Override
    public cardinality getCardinality() {
        return host.getCardinality();
    }

    @Override
    public Set<Facet> getFacets() {
        return host.getFacets();
    }

    @Override
    public Map<Facet, Object> getKnownFacetValues() {
        return host.getKnownFacetValues();
    }

    @Override
    public <O extends Comparable<O>> O getFacetValue(Facet f) {
        return host.getFacetValue(f);
    }

    @Override
    public BigDecimal getNumericFacetValue(Facet f) {
        return host.getNumericFacetValue(f);
    }

    @Override
    public boolean getNumeric() {
        return host.getNumeric();
    }

    @Override
    public ordered getOrdered() {
        return host.getOrdered();
    }

    @Override
    public boolean isCompatible(Literal<?> l) {
        return !host.isCompatible(l);
    }

    @Override
    public boolean isInValueSpace(R l) {
        return !host.isInValueSpace(l);
    }

    @Override
    public R parseValue(String s) {
        // delegated to the host type
        return host.parseValue(s);
    }

    @Override
    public Literal<R> buildLiteral(String s) {
        return host.buildLiteral(s);
    }

    @Override
    public boolean isSubType(Datatype<?> type) {
        return host.isSubType(type);
    }

    @Override
    public String getDatatypeURI() {
        return uri;
    }

    @Override
    public boolean isCompatible(Datatype<?> type) {
        if (type instanceof DatatypeNegation) {
            return !host.isCompatible(((DatatypeNegation<?>) type).host);
        }
        return !host.isCompatible(type);
    }

    @Override
    public void accept(DLExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(DLExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
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

    @Override
    public boolean isNumericDatatype() {
        return host.isNumericDatatype();
    }

    @Override
    public NumericDatatype<R> asNumericDatatype() {
        return new NumericDatatypeWrapper<R>(this);
    }

    @Override
    public boolean isOrderedDatatype() {
        return host.isOrderedDatatype();
    }

    @Override
    public <O extends Comparable<O>> OrderedDatatype<O> asOrderedDatatype() {
        return (OrderedDatatype<O>) this;
    }

    @Override
    public String toString() {
        return uri + "{" + host + "}";
    }

    @Override
    public Datatype<R> getHostType() {
        return host.isExpression() ? host.asExpression().getHostType() : host;
    }

    @Override
    public DatatypeExpression<R> addFacet(Facet f, Object value) {
        System.out
                .println("DatatypeNegation.addFacet() Cannot add a facet to a negation; modify the base type and rebuild a new negation. Returning the same object");
        return this;
    }

    @Override
    public boolean emptyValueSpace() {
        if (!host.isExpression()) {
            return true;
        }
        // TODO verify the cases where restrictions on the host actually bracket
        // the value space, so its opposite is empty
        return false;
    }
}
