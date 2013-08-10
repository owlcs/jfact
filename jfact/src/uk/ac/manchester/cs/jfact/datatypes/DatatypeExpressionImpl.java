package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;

class DatatypeExpressionImpl<O extends Comparable<O>> extends ABSTRACT_DATATYPE<O>
        implements DatatypeExpression<O> {
    private static final long serialVersionUID = 11000L;
    // TODO handle all value space restrictions in the delegations
    private final Datatype<O> host;

    public DatatypeExpressionImpl(Datatype<O> b) {
        super(b.getDatatypeURI() + "_" + DatatypeFactory.getIndex(), b.getFacets());
        if (b.isExpression()) {
            this.host = b.asExpression().getHostType();
        } else {
            this.host = b;
        }
        ancestors = Utils.generateAncestors(this.host);
        knownNumericFacetValues.putAll(b.getKnownNumericFacetValues());
        knownNonNumericFacetValues.putAll(b.getKnownNonNumericFacetValues());
    }

    @Override
    public O parseValue(String s) {
        return this.host.parseValue(s);
    }

    @Override
    public boolean isInValueSpace(O l) {
        return this.host.isInValueSpace(l);
    }

    @Override
    public ordered getOrdered() {
        return this.host.getOrdered();
    }

    @Override
    public boolean getNumeric() {
        return this.host.getNumeric();
    }

    @Override
    public cardinality getCardinality() {
        return this.host.getCardinality();
    }

    @Override
    public boolean getBounded() {
        return this.host.getBounded();
    }

    @Override
    public Collection<Literal<O>> listValues() {
        return this.host.listValues();
    }

    @Override
    public Datatype<O> getHostType() {
        return this.host;
    }

    @Override
    public DatatypeExpression<O> addNumericFacet(Facet f, Comparable value) {
        if (!facets.contains(f)) {
            throw new IllegalArgumentException("Facet " + f
                    + " not allowed tor datatype " + this.getHostType());
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        DatatypeExpressionImpl<O> toReturn = new DatatypeExpressionImpl<O>(this.host);
        toReturn.knownNumericFacetValues.putAll(knownNumericFacetValues);
        toReturn.knownNonNumericFacetValues.putAll(knownNonNumericFacetValues);
        // cannot have noth min/maxInclusive and min/maxExclusive values, so
        // remove them if the feature is min/max
        if (f.equals(Facets.minExclusive) || f.equals(Facets.minInclusive)) {
            toReturn.knownNumericFacetValues.remove(Facets.minExclusive);
            toReturn.knownNumericFacetValues.remove(Facets.minInclusive);
        }
        if (f.equals(Facets.maxExclusive) || f.equals(Facets.maxInclusive)) {
            toReturn.knownNumericFacetValues.remove(Facets.maxExclusive);
            toReturn.knownNumericFacetValues.remove(Facets.maxInclusive);
        }
        toReturn.knownNumericFacetValues.put(f, value);
        return toReturn;
    }

    @Override
    public DatatypeExpression<O> addNonNumericFacet(Facet f, Comparable value) {
        if (!facets.contains(f)) {
            throw new IllegalArgumentException("Facet " + f
                    + " not allowed tor datatype " + this.getHostType());
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        DatatypeExpressionImpl<O> toReturn = new DatatypeExpressionImpl<O>(this.host);
        toReturn.knownNumericFacetValues.putAll(knownNumericFacetValues);
        toReturn.knownNonNumericFacetValues.putAll(knownNonNumericFacetValues);
        toReturn.knownNonNumericFacetValues.put(f, value);
        return toReturn;
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    @Override
    public boolean emptyValueSpace() {
        // TODO not checking string type value spaces; looks like the only
        // sensible way would be to check for 0 length constraints
        if (this.getNumeric()) {
            // remember whether it's inclusive or exclusive - needed to know if
            // the two extremes can be the same or not
            int excluded = 0;
            Comparable min = getNumericFacetValue(Facets.minInclusive);
            if (min == null) {
                min = getNumericFacetValue(Facets.minExclusive);
                excluded++;
            }
            Comparable max = getNumericFacetValue(Facets.maxInclusive);
            if (max == null) {
                max = getNumericFacetValue(Facets.maxExclusive);
                excluded++;
            }
            return DatatypeFactory.nonEmptyInterval(min, max, excluded);
        }
        return false;
    }

    @Override
    public boolean isNumericDatatype() {
        return this.host.isNumericDatatype();
    }

    @Override
    public NumericDatatype<O> asNumericDatatype() {
        return (NumericDatatype<O>) this;
    }

    @Override
    public boolean isOrderedDatatype() {
        return this.host.isOrderedDatatype();
    }

    @Override
    public OrderedDatatype<O> asOrderedDatatype() {
        return (OrderedDatatype<O>) this;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(" + this.host.toString() + "(extra facets:"
                + knownNumericFacetValues + "))";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof DatatypeExpression) {
            DatatypeExpression<?> datatypeExpression = (DatatypeExpression<?>) obj;
            return this.host.equals(datatypeExpression.getHostType())
                    && knownNumericFacetValues.equals(datatypeExpression
                            .getKnownNumericFacetValues())
                    && knownNonNumericFacetValues.equals(datatypeExpression
                            .getKnownNonNumericFacetValues());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.host.hashCode() + knownNumericFacetValues.hashCode();
    }
}
