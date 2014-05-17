package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

/**
 * @author ignazio
 * @param <R>
 *        type
 */
public class DatatypeEnumeration<R extends Comparable<R>> implements
        DatatypeCombination<DatatypeEnumeration<R>, Literal<R>>, DatatypeExpression<R> {
    private final IRI uri;
    protected final Datatype<R> host;
    protected final List<Literal<R>> literals = new ArrayList<Literal<R>>();

    /**
     * @param d
     *        d
     */
    public DatatypeEnumeration(Datatype<R> d) {
        this.uri = IRI.create("urn:enum" + DatatypeFactory.getIndex());
        this.host = d;
    }

    /**
     * @param d
     *        d
     * @param l
     *        l
     */
    public DatatypeEnumeration(Datatype<R> d, Literal<R> l) {
        this(d);
        this.literals.add(l);
    }

    /**
     * @param d
     *        d
     * @param c
     *        c
     */
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

    @SuppressWarnings("rawtypes")
    @Override
    public Map<Facet, Comparable> getKnownNonNumericFacetValues() {
        return this.host.getKnownNonNumericFacetValues();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<Facet, Comparable> getKnownNumericFacetValues() {
        return this.host.getKnownNumericFacetValues();
    }

    @Override
    public <O extends Comparable<O>> O getFacetValue(Facet<O> f) {
        O o = this.host.getFacetValue(f);
        return o;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Comparable getNumericFacetValue(Facet f) {
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
    public IRI getDatatypeIRI() {
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
    public boolean isContradictory(Datatype<?> type) {
        if (type instanceof DatatypeEnumeration) {
            return Helper.intersectsWith(((DatatypeEnumeration) type).literals, literals);
        }
        return !isCompatible(type);
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
    public OrderedDatatype<R> asOrderedDatatype() {
        return null;
    }

    @Override
    public String toString() {
        return this.uri.toString() + this.literals;
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
    public DatatypeExpression<R> addNumericFacet(Facet f, Comparable<?> value) {
        System.out
                .println("DatatypeNumericEnumeration.addFacet() WARNING: cannot add facets to an enumeration; returning the same object");
        return this;
    }

    @Override
    public DatatypeExpression<R> addNonNumericFacet(Facet f, Comparable<?> value) {
        System.out
                .println("DatatypeNumericEnumeration.addFacet() WARNING: cannot add facets to an enumeration; returning the same object");
        return this;
    }

    @Override
    public IRI getName() {
        return IRI.create(toString());
    }
}
