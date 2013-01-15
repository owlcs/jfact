package uk.ac.manchester.cs.jfact.datatypes;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;
import conformance.Original;

/** @author ignazio
 * @param <Representation> */
@Original
public interface Datatype<Representation extends Comparable<Representation>> extends
        DataExpression {
    /** @return true if this datatype is an expression */
    boolean isExpression();

    /** @return this datatype as a datatype expression, if it is an expression.
     * @throws UnsupportedOperationException
     *             if this datatype is not an expression (isExpression() returns
     *             false) */
    DatatypeExpression<Representation> asExpression();

    /** @return the known ancestors of this datatype */
    Collection<Datatype<?>> getAncestors();

    /** @return true if this datatype value space is bounded */
    boolean getBounded();

    /** @return the cardinality of the value space: finite or countably infinite */
    cardinality getCardinality();

    /** @return the available facets for this datatype. The collection is
     *         immutable - only specs sanctioned facets allowed */
    Set<Facet> getFacets();

    /** @return the known values for a subset of the available facets */
    Map<Facet, Comparable> getKnownNumericFacetValues();

    /** @return the known values for a subset of the available facets */
    Map<Facet, Comparable> getKnownNonNumericFacetValues();

    /** @param f
     * @return known value for f, or null if there is no known value for the
     *         facet */
    <O extends Comparable<O>> O getFacetValue(Facet f);

    /** @param f
     * @return numeric value */
    Comparable getNumericFacetValue(Facet f);

    /** @return true if this datatype is numeric */
    boolean getNumeric();

    /** @return the kind of ordering: false, partial or total */
    ordered getOrdered();

    /** @param type
     * @return true if type\s value space and this datatype's value space have
     *         an intersection, e.g., non negative integers and non positive
     *         integers intersect at 0 */
    boolean isCompatible(Datatype<?> type);

    /** @param l
     * @return true if l is a literal with compatible datatype and value
     *         included in this datatype value space */
    boolean isCompatible(Literal<?> l);

    /** @param l
     * @return false if this literal representation does not represent a value
     *         included in the value space of this datatype; its datatype must
     *         be this datatype */
    boolean isInValueSpace(Representation l);

    /** @return true if this datatype expression is limited in such a way that
     *         there are no valid values */
    boolean emptyValueSpace();

    /** @param s
     *            parses a literal form to a value in the datatype value space;
     *            for use when building Literals
     * @return value */
    Representation parseValue(String s);

    /** @param s
     * @return a literal with parseValue(s) as typed value, generic type O equal
     *         to the internal class representing the type, and datatype this
     *         datatype. */
    Literal<Representation> buildLiteral(String s);

    /** @param type
     * @return true if this datatype has type as an ancestor */
    boolean isSubType(Datatype<?> type);

    /** @return the datatype uri as a string (there does seem to be no need for a
     *         more complex representation) */
    String getDatatypeURI();

    /** @return the list of possible values for this datatype which are compatible
     *         with the listed datatypes. */
    Collection<Literal<Representation>> listValues();

    /** @return true if the datatype is numeric */
    boolean isNumericDatatype();

    /** @return cast as numeric datatype */
    NumericDatatype<Representation> asNumericDatatype();

    /** @return true if ordered */
    boolean isOrderedDatatype();

    /** @return cast as ordered datatype */
    OrderedDatatype<Representation> asOrderedDatatype();
}
