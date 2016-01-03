package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

class NumericDatatypeWrapper<O extends Comparable<O>> implements NumericDatatype<O>, OrderedDatatype<O> {

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
        return d.getCardinality();
    }

    @Override
    public Set<Facet> getFacets() {
        return this.d.getFacets();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<Facet, Comparable> getKnownNonNumericFacetValues() {
        return this.d.getKnownNonNumericFacetValues();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<Facet, Comparable> getKnownNumericFacetValues() {
        return this.d.getKnownNumericFacetValues();
    }

    @Nullable
    @Override
    public Comparable getFacetValue(Facet f) {
        return this.d.getFacetValue(f);
    }

    @Nullable
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
    public boolean isContradictory(Datatype<?> type) {
        return !isCompatible(type);
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
    public IRI getDatatypeIRI() {
        return this.d.getDatatypeIRI();
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

    @Nullable
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

    @Nullable
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

    @Override
    public IRI getIRI() {
        return IRI.create("numeric(" + d + ")");
    }

    @Override
    public String toString() {
        return getIRI().toString();
    }
}
