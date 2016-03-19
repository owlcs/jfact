package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(DatatypeEnumeration.class);
    @Nonnull private final IRI uri;
    @Nonnull protected final Datatype<R> host;
    @Nonnull protected final List<Literal<R>> literals = new ArrayList<>();

    /**
     * @param d
     *        d
     */
    public DatatypeEnumeration(Datatype<R> d) {
        this.uri = DatatypeFactory.getIndex("urn:enum").getIRI();
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
        DatatypeEnumeration<R> toReturn = new DatatypeEnumeration<>(this.host, this.literals);
        toReturn.literals.add(d);
        Collections.sort(toReturn.literals);
        return toReturn;
    }

    @Override
    public Collection<Literal<R>> listValues() {
        return new ArrayList<>(this.literals);
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

    @Nullable
    @Override
    public Comparable getFacetValue(Facet f) {
        return this.host.getFacetValue(f);
    }

    @Nullable
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
        return this.literals.contains(l) && this.host.isCompatible(l.getDatatypeExpression());
    }

    @Override
    public boolean isInValueSpace(R l) {
        return this.literals.stream().map(p -> p.typedValue()).anyMatch(p -> p.equals(l));
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
        return this.host.isSubType(type) && isCompatible(type);
    }

    @Override
    public IRI getDatatypeIRI() {
        return this.uri;
    }

    @Override
    public boolean isCompatible(Datatype<?> type) {
        if (!this.host.isCompatible(type)) {
            return false;
        }
        // at least one value must be admissible in both
        return this.literals.stream().anyMatch(type::isCompatible);
    }

    @Override
    public boolean isContradictory(Datatype<?> type) {
        if (type instanceof DatatypeEnumeration) {
            return Helper.intersectsWith(((DatatypeEnumeration<?>) type).literals, literals);
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
        throw new ReasonerInternalException(this + " is not a numeric datatype");
    }

    @Override
    public boolean isOrderedDatatype() {
        return false;
    }

    @Override
    public OrderedDatatype<R> asOrderedDatatype() {
        throw new ReasonerInternalException(this + " is not an ordered datatype");
    }

    @Override
    @Nonnull
    public String toString() {
        return this.uri.toString() + this.literals;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
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
    public DatatypeExpression<R> addNumericFacet(Facet f, @Nullable Comparable<?> value) {
        LOGGER.warn(
            "DatatypeNumericEnumeration.addFacet() WARNING: cannot add facets to an enumeration; returning the same object: {}",
            this);
        return this;
    }

    @Override
    public DatatypeExpression<R> addNonNumericFacet(Facet f, @Nullable Comparable<?> value) {
        LOGGER.warn(
            "DatatypeNumericEnumeration.addFacet() WARNING: cannot add facets to an enumeration; returning the same object: {}",
            this);
        return this;
    }

    @Override
    public IRI getIRI() {
        return IRI.create(toString());
    }
}
