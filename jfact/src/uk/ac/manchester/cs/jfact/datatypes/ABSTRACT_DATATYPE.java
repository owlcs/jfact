package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.*;

import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

abstract class ABSTRACT_DATATYPE<R extends Comparable<R>> implements Datatype<R>,
        Serializable {
    private static final long serialVersionUID = 11000L;
    protected final Set<Facet> facets;
    protected Set<Datatype<?>> ancestors;
    protected final Map<Facet, Comparable> knownNumericFacetValues = new HashMap<Facet, Comparable>();
    protected final Map<Facet, Comparable> knownNonNumericFacetValues = new HashMap<Facet, Comparable>();
    protected final String uri;

    public ABSTRACT_DATATYPE(String u, Set<Facet> f) {
        this.facets = Collections.unmodifiableSet(f);
        this.uri = u;
    }

    @Override
    public String getDatatypeURI() {
        return this.uri;
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public int hashCode() {
        return this.uri.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof Datatype<?>) {
            return this.uri.equals(((Datatype<?>) obj).getDatatypeURI());
        }
        return false;
    }

    @Override
    public Collection<Datatype<?>> getAncestors() {
        return this.ancestors;
    }

    @Override
    public Set<Facet> getFacets() {
        return this.facets;
    }

    @Override
    public Map<Facet, Comparable> getKnownNumericFacetValues() {
        return this.knownNumericFacetValues;
    }

    @Override
    public Map<Facet, Comparable> getKnownNonNumericFacetValues() {
        return this.knownNonNumericFacetValues;
    }

    @Override
    public Comparable getFacetValue(Facet f) {
        if (f.isNumberFacet()) {
            return this.getNumericFacetValue(f);
        }
        return this.knownNonNumericFacetValues.get(f);
    }

    @Override
    public Comparable getNumericFacetValue(Facet f) {
        return this.knownNumericFacetValues.get(f);
    }

    @Override
    public boolean isSubType(Datatype<?> type) {
        return this.ancestors.contains(type) || this.equals(type);
    }

    @Override
    public String toString() {
        String datatypeURI = this.getDatatypeURI();
        datatypeURI = datatypeURI.substring(datatypeURI.lastIndexOf('#'));
        return datatypeURI;
    }

    @Override
    public boolean isCompatible(Datatype<?> type) {
        if (type.isExpression()) {
            type = type.asExpression().getHostType();
        }
        return type.equals(this) || type.equals(DatatypeFactory.LITERAL)
                || type.isSubType(this) || this.isSubType(type);
    }

    @Override
    public boolean isCompatible(Literal<?> l) {
        if (!this.isCompatible(l.getDatatypeExpression())) {
            return false;
        }
        try {
            R value = parseValue(l.value());
            return this.isInValueSpace(value);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            // parsing exceptions will be caught here
            return false;
        }
    }

    // most common answer; restrictions on value spaces to be tested in
    // subclasses
    @Override
    public boolean isInValueSpace(R l) {
        return true;
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
    public boolean isExpression() {
        return false;
    }

    @Override
    public DatatypeExpression<R> asExpression() {
        if (!this.isExpression()) {
            throw new UnsupportedOperationException("Type: " + this.getDatatypeURI()
                    + " is not an expression");
        }
        return (DatatypeExpression<R>) this;
    }

    @Override
    public Literal<R> buildLiteral(String s) {
        if (this.getNumeric()) {
            return new NumericLiteralImpl<R>(this.asNumericDatatype(), s);
        }
        return new LiteralImpl<R>(this, s);
    }

    @Override
    public Collection<Literal<R>> listValues() {
        return Collections.emptyList();
    }

    @Override
    public boolean getBounded() {
        return false;
    }

    @Override
    public boolean getNumeric() {
        return false;
    }

    @Override
    public ordered getOrdered() {
        return ordered.FALSE;
    }

    @Override
    public cardinality getCardinality() {
        return cardinality.COUNTABLYINFINITE;
    }

    <T extends Comparable<T>> boolean overlapping(OrderedDatatype<T> first,
            OrderedDatatype<T> second) {
        if (first.hasMaxInclusive() && second.hasMinInclusive()) {
            return first.getMax().compareTo(second.getMin()) >= 0;
        }
        T minSecond = second.getMin();
        if (first.hasMaxExclusive() && minSecond != null) {
            return first.getMax().compareTo(minSecond) > 0;
        }
        // if we get here, first has no max, hence it's unbounded upwards
        return false;
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
    public OrderedDatatype<R> asOrderedDatatype() {
        return null;
    }

    @Override
    public boolean emptyValueSpace() {
        return false;
    }
}
