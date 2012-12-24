package uk.ac.manchester.cs.jfact.datatypes;

import java.math.BigDecimal;
import java.util.*;

import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

/** @author ignazio
 * @param <R> */
public class DatatypeEnumeration<R extends Comparable<R>> implements
        DatatypeCombination<DatatypeEnumeration<R>, Literal<R>>, DatatypeExpression<R> {
    private final String uri;
    protected Datatype<R> host;
    protected final List<Literal<R>> literals = new ArrayList<Literal<R>>();

    /** @param d */
    public DatatypeEnumeration(Datatype<R> d) {
        this.uri = "enum" + DatatypeFactory.getIndex();
        this.host = d;
    }

    /** @param d
     * @param l */
    public DatatypeEnumeration(Datatype<R> d, Literal<R> l) {
        this(d);
        this.literals.add(l);
    }

    /** @param d
     * @param c */
    public DatatypeEnumeration(Datatype<R> d, Collection<Literal<R>> c) {
        this(d);
        this.literals.addAll(c);
        Collections.sort(this.literals);
    }

    @Override
    public Datatype<?> getHost() {
        return this.host;
    }

    @Override
    public DatatypeEnumeration<R> add(Literal<R> d) {
        DatatypeEnumeration<R> toReturn = new DatatypeEnumeration<R>(this.host,
                this.literals);
        toReturn.literals.add(d);
        Collections.sort(toReturn.literals);
        return toReturn;
    }

    @Override
    public Collection<Literal<R>> listValues() {
        return new ArrayList<Literal<R>>(this.literals);
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
        return this.host.getAncestors();
    }

    @Override
    public boolean getBounded() {
        return this.host.getBounded();
    }

    @Override
    public cardinality getCardinality() {
        return cardinality.FINITE;
    }

    @Override
    public Set<Facet> getFacets() {
        return this.host.getFacets();
    }

    @Override
    public Map<Facet, Object> getKnownFacetValues() {
        return this.host.getKnownFacetValues();
    }

    @Override
    public <O extends Comparable<O>> O getFacetValue(Facet f) {
        return this.host.getFacetValue(f);
    }

    @Override
    public BigDecimal getNumericFacetValue(Facet f) {
        return this.host.getNumericFacetValue(f);
    }

    @Override
    public boolean getNumeric() {
        return this.host.getNumeric();
    }

    @Override
    public ordered getOrdered() {
        return this.host.getOrdered();
    }

    @Override
    public boolean isCompatible(Literal<?> l) {
        return this.literals.contains(l)
                && this.host.isCompatible(l.getDatatypeExpression());
    }

    @Override
    public boolean isInValueSpace(R l) {
        for (Literal<R> lit : this.literals) {
            if (lit.typedValue().equals(l)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public R parseValue(String s) {
        // delegated to the host type
        return this.host.parseValue(s);
    }

    @Override
    public Literal<R> buildLiteral(String s) {
        return this.host.buildLiteral(s);
    }

    @Override
    public boolean isSubType(Datatype<?> type) {
        return this.host.isSubType(type);
    }

    @Override
    public String getDatatypeURI() {
        return this.uri;
    }

    @Override
    public boolean isCompatible(Datatype<?> type) {
        // return host.isCompatible(type);
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

    @Override
    public void accept(DLExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(DLExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Iterable<Literal<R>> getList() {
        return this.literals;
    }

    @Override
    public boolean emptyValueSpace() {
        return this.literals.isEmpty();
    }

    @Override
    public boolean isNumericDatatype() {
        return false;
    }

    @Override
    public NumericDatatype<R> asNumericDatatype() {
        return null;
    }

    @Override
    public boolean isOrderedDatatype() {
        return false;
    }

    @Override
    public <O extends Comparable<O>> OrderedDatatype<O> asOrderedDatatype() {
        return null;
    }

    @Override
    public String toString() {
        return this.uri + this.literals;
    }

    @Override
    public boolean equals(Object obj) {
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

    @Override
    public Datatype<R> getHostType() {
        return this.host;
    }

    @Override
    public DatatypeExpression<R> addFacet(Facet f, Object value) {
        System.out
                .println("DatatypeNumericEnumeration.addFacet() WARNING: cannot add facets to an enumeration; returning the same object");
        return this;
    }
}
