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

/** datatype negation
 * 
 * @param <R> */
public class DatatypeNegation<R extends Comparable<R>> implements DatatypeExpression<R>,
        Serializable {
    private static final long serialVersionUID = 11000L;
    private final Datatype<R> host;
    private final String uri;

    /** @param d */
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
    public boolean emptyValueSpace() {
        return !host.emptyValueSpace();
    }

    @Override
    public Map<Facet, Comparable> getKnownNumericFacetValues() {
        return host.getKnownNumericFacetValues();
    }

    @Override
    public Map<Facet, Comparable> getKnownNonNumericFacetValues() {
        return host.getKnownNonNumericFacetValues();
    }

    @Override
    public <O extends Comparable<O>> O getFacetValue(Facet f) {
        return host.getFacetValue(f);
    }

    @Override
    public Comparable getNumericFacetValue(Facet f) {
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
    public OrderedDatatype<R> asOrderedDatatype() {
        return (OrderedDatatype<R>) this;
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
    public DatatypeExpression<R> addNumericFacet(Facet f, Comparable value) {
        System.out
                .println("DatatypeNegation.addFacet() Cannot add a facet to a negation; modify the base type and rebuild a new negation. Returning the same object");
        return this;
    }

    @Override
    public DatatypeExpression<R> addNonNumericFacet(Facet f, Comparable value) {
        System.out
                .println("DatatypeNegation.addFacet() Cannot add a facet to a negation; modify the base type and rebuild a new negation. Returning the same object");
        return this;
    }

    @Override
    public String getName() {
        return toString();
    }
}
