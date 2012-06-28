package uk.ac.manchester.cs.jfact.datatypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

public class DatatypeEnumeration<R extends Comparable<R>> implements
        DatatypeCombination<DatatypeEnumeration<R>, Literal<R>>, DatatypeExpression<R> {
    private final String uri;
    protected Datatype<R> host;
    protected final List<Literal<R>> literals = new ArrayList<Literal<R>>();

    public DatatypeEnumeration(final Datatype<R> d) {
        this.uri = "enum" + DatatypeFactory.getIndex();
        this.host = d;
    }

    public DatatypeEnumeration(final Datatype<R> d, final Literal<R> l) {
        this(d);
        this.literals.add(l);
    }

    public DatatypeEnumeration(final Datatype<R> d, final Collection<Literal<R>> c) {
        this(d);
        this.literals.addAll(c);
        Collections.sort(this.literals);
    }

    public Datatype<?> getHost() {
        return this.host;
    }

    public DatatypeEnumeration<R> add(final Literal<R> d) {
        DatatypeEnumeration<R> toReturn = new DatatypeEnumeration<R>(this.host,
                this.literals);
        toReturn.literals.add(d);
        Collections.sort(toReturn.literals);
        return toReturn;
    }

    public Collection<Literal<R>> listValues() {
        return new ArrayList<Literal<R>>(this.literals);
    }

    public boolean isExpression() {
        return true;
    }

    public DatatypeExpression<R> asExpression() {
        return this;
    }

    public Collection<Datatype<?>> getAncestors() {
        return this.host.getAncestors();
    }

    public boolean getBounded() {
        return this.host.getBounded();
    }

    public cardinality getCardinality() {
        return cardinality.FINITE;
    }

    public Set<Facet> getFacets() {
        return this.host.getFacets();
    }

    public Map<Facet, Object> getKnownFacetValues() {
        return this.host.getKnownFacetValues();
    }

    public <O extends Comparable<O>> O getFacetValue(final Facet f) {
        return this.host.getFacetValue(f);
    }

    public BigDecimal getNumericFacetValue(final Facet f) {
        return this.host.getNumericFacetValue(f);
    }

    public boolean getNumeric() {
        return this.host.getNumeric();
    }

    public ordered getOrdered() {
        return this.host.getOrdered();
    }

    public boolean isCompatible(final Literal<?> l) {
        return this.literals.contains(l)
                && this.host.isCompatible(l.getDatatypeExpression());
    }

    public boolean isInValueSpace(final R l) {
        for (Literal<R> lit : this.literals) {
            if (lit.typedValue().equals(l)) {
                return true;
            }
        }
        return false;
    }

    public R parseValue(final String s) {
        //delegated to the host type
        return this.host.parseValue(s);
    }

    public Literal<R> buildLiteral(final String s) {
        return this.host.buildLiteral(s);
    }

    public boolean isSubType(final Datatype<?> type) {
        return this.host.isSubType(type);
    }

    public String getDatatypeURI() {
        return this.uri;
    }

    public boolean isCompatible(final Datatype<?> type) {
        //	return host.isCompatible(type);
        if (!this.host.isCompatible(type)) {
            return false;
        }
        // at least one value must be admissible in both
        for (Literal<?> l : this.literals) {
            if (type.isCompatible(l)) {
                return true;
            }
        }
        return false;
    }

    public void accept(final DLExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public <O> O accept(final DLExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public Iterable<Literal<R>> getList() {
        return this.literals;
    }

    public boolean emptyValueSpace() {
        return this.literals.isEmpty();
    }

    public boolean isNumericDatatype() {
        return false;
    }

    public NumericDatatype<R> asNumericDatatype() {
        return null;
    }

    public boolean isOrderedDatatype() {
        return false;
    }

    public <O extends Comparable<O>> OrderedDatatype<O> asOrderedDatatype() {
        return null;
    }

    @Override
    public String toString() {
        return this.uri + this.literals;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof DatatypeEnumeration) {
            return this.literals.equals(((DatatypeEnumeration<?>) obj).literals);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.literals.hashCode();
    }

    public Datatype<R> getHostType() {
        return this.host;
    }

    public DatatypeExpression<R> addFacet(final Facet f, final Object value) {
        System.out
                .println("DatatypeNumericEnumeration.addFacet() WARNING: cannot add facets to an enumeration; returning the same object");
        return this;
    }
}
