package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;

import conformance.Original;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;

/**
 * @author ignazio
 * @param <R>
 *        type
 */
@Original
public interface Datatype<R extends Comparable<R>> extends DataExpression {

    /** @return true if this datatype is an expression */
    boolean isExpression();

    /**
     * @return this datatype as a datatype expression, if it is an expression.
     */
    DatatypeExpression<R> asExpression();

    /** @return the known ancestors of this datatype */
    Collection<Datatype<?>> getAncestors();

    /** @return true if this datatype value space is bounded */
    boolean getBounded();

    /**
     * @return the cardinality of the value space: finite or countably infinite
     */
    cardinality getCardinality();

    /**
     * @return the available facets for this datatype. The collection is
     *         immutable - only specs sanctioned facets allowed
     */
    Set<Facet> getFacets();

    /** @return the known values for a subset of the available facets */
    @SuppressWarnings("rawtypes")
        Map<Facet, Comparable> getKnownNumericFacetValues();

    /** @return the known values for a subset of the available facets */
    @SuppressWarnings("rawtypes")
        Map<Facet, Comparable> getKnownNonNumericFacetValues();

    /**
     * @param f
     *        facet
     * @return known value for f, or null if there is no known value for the
     *         facet
     */
    @Nullable
    @SuppressWarnings("rawtypes")
        Comparable getFacetValue(Facet f);

    /**
     * @param f
     *        facet
     * @return numeric value
     */
    @Nullable
    @SuppressWarnings("rawtypes")
        Comparable getNumericFacetValue(Facet f);

    /** @return true if this datatype is numeric */
    boolean getNumeric();

    /** @return the kind of ordering: false, partial or total */
    ordered getOrdered();

    /**
     * @param type
     *        type
     * @return true if type\s value space and this datatype's value space have
     *         an intersection, e.g., non negative integers and non positive
     *         integers intersect at 0
     */
    boolean isCompatible(Datatype<?> type);

    /**
     * @param l
     *        literal
     * @return true if l is a literal with compatible datatype and value
     *         included in this datatype value space
     */
    boolean isCompatible(Literal<?> l);

    /**
     * @param type
     *        type
     * @return true if the datatype is contradictory, e.g., the two appearing
     *         together in a datatype situation cause a clash. e.g., +{"6"} and
     *         +{"4"} are not compatible and not contradictory, +{"6"} and
     *         -{"6"} are compatible and contradictory
     */
    boolean isContradictory(Datatype<?> type);

    /**
     * @param l
     *        literal
     * @return false if this literal representation does not represent a value
     *         included in the value space of this datatype; its datatype must
     *         be this datatype
     */
    boolean isInValueSpace(R l);

    /**
     * @return true if this datatype expression is limited in such a way that
     *         there are no valid values
     */
    boolean emptyValueSpace();

    /**
     * @param s
     *        parses a literal form to a value in the datatype value space; for
     *        use when building Literals
     * @return value
     */
    R parseValue(String s);

    /**
     * @param s
     *        literal
     * @return a literal with parseValue(s) as typed value, generic type O equal
     *         to the internal class representing the type, and datatype this
     *         datatype.
     */
    Literal<R> buildLiteral(String s);

    /**
     * @param type
     *        type
     * @return true if this datatype has type as an ancestor
     */
    boolean isSubType(Datatype<?> type);

    /**
     * @return the datatype IRI
     */
    IRI getDatatypeIRI();

    /**
     * @return the list of possible values for this datatype which are
     *         compatible with the listed datatypes.
     */
    Collection<Literal<R>> listValues();

    /** @return true if the datatype is numeric */
    boolean isNumericDatatype();

    /** @return cast as numeric datatype */
    NumericDatatype<R> asNumericDatatype();

    /** @return true if ordered */
    boolean isOrderedDatatype();

    /** @return cast as ordered datatype */
    OrderedDatatype<R> asOrderedDatatype();

    /**
     * @return a new numeric expression if this is a numeric datatype, otherwise
     *         null
     */
    default DatatypeExpression<R> wrapAsNumericExpression() {
        return isNumericDatatype() ? new DatatypeNumericExpressionImpl<>(this) : null;
    }

    /**
     * @return a new ordered expression if this is ano ordered datatype,
     *         otherwise null
     */
    default DatatypeExpression<R> wrapAsOrderedExpression() {
        return isOrderedDatatype() ? new DatatypeOrderedExpressionImpl<>(this) : null;
    }

    /**
     * @return a new datatype expression
     */
    default DatatypeExpression<R> wrapAsDatatypeExpression() {
        return new DatatypeExpressionImpl<>(this);
    }

    /**
     * @return host dataype
     */
    default Datatype<R> host() {
        if (isExpression()) {
            return asExpression().getHostType();
        }
        return this;
    }
}
