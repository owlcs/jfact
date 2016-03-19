package uk.ac.manchester.cs.jfact.datatypes;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

/**
 * datatype negation
 * 
 * @param <R>
 *        type
 */
public class DatatypeNegation<R extends Comparable<R>> implements DatatypeExpression<R>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatatypeNegation.class);
    @Nonnull private final Datatype<R> host;
    @Nonnull private final IRI uri;

    /**
     * @param d
     *        d
     */
    public DatatypeNegation(Datatype<R> d) {
        this.uri = DatatypeFactory.getIndex("urn:neg").getIRI();
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
        // XXX hack: we need to check cardinalities
        if (host.emptyValueSpace()) {
            return false;
        }
        if (host.getCardinality() == cardinality.COUNTABLYINFINITE) {
            return false;
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<Facet, Comparable> getKnownNumericFacetValues() {
        return host.getKnownNumericFacetValues();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<Facet, Comparable> getKnownNonNumericFacetValues() {
        return host.getKnownNonNumericFacetValues();
    }

    @Nullable
    @Override
    public Comparable getFacetValue(Facet f) {
        return host.getFacetValue(f);
    }

    @Nullable
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
    public IRI getDatatypeIRI() {
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
    public boolean isContradictory(Datatype<?> type) {
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
    public Collection<Literal<R>> listValues() {
        return asList(host.listValues().stream().filter(p -> !host.isCompatible(p)));
    }

    @Override
    public boolean isNumericDatatype() {
        return host.isNumericDatatype();
    }

    @Override
    public NumericDatatype<R> asNumericDatatype() {
        return new NumericDatatypeWrapper<>(this);
    }

    @Override
    public boolean isOrderedDatatype() {
        return host.isOrderedDatatype();
    }

    @SuppressWarnings("unchecked")
    @Override
    public OrderedDatatype<R> asOrderedDatatype() {
        return (OrderedDatatype<R>) this;
    }

    @Override
    @Nonnull
    public String toString() {
        return uri + "{" + host + '}';
    }

    @Override
    public Datatype<R> getHostType() {
        return host.isExpression() ? host.asExpression().getHostType() : host;
    }

    @Override
    public DatatypeExpression<R> addNumericFacet(Facet f, @Nullable Comparable<?> value) {
        LOGGER.warn(
            "DatatypeNumericEnumeration.addFacet() WARNING: cannot add facets to a negation; returning the same object: {}",
            this);
        return this;
    }

    @Override
    public DatatypeExpression<R> addNonNumericFacet(Facet f, @Nullable Comparable<?> value) {
        LOGGER.warn(
            "DatatypeNumericEnumeration.addFacet() WARNING: cannot add facets to a negation; returning the same object: {}",
            this);
        return this;
    }

    @Override
    public IRI getIRI() {
        return IRI.create(toString());
    }
}
