package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

class REALDatatype<R extends Comparable<R>> extends AbstractNumericDatatype<R> {

    public REALDatatype() {
        this(OWL2Datatype.OWL_REAL);
    }

    REALDatatype(HasIRI uri) {
        this(uri, Utils.getFacets(MINMAX), Utils.generateAncestors(LITERAL));
    }

    REALDatatype(HasIRI uri, Set<Facet> f, Set<Datatype<?>> ancestors) {
        super(uri, f, ancestors);
        knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
    }

    @SuppressWarnings("unchecked")
    @Override
    public R parseValue(String s) {
        return (R) new BigDecimal(s);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public boolean isInValueSpace(R l) {
        if (knownNumericFacetValues.containsKey(minExclusive)) {
            Comparable v = getNumericFacetValue(minExclusive);
            Comparable input = minExclusive.parseNumber(l);
            if (input.compareTo(v) <= 0) {
                return false;
            }
        }
        if (knownNumericFacetValues.containsKey(minInclusive)) {
            Comparable v = getNumericFacetValue(minInclusive);
            Comparable input = minInclusive.parseNumber(l);
            if (input.compareTo(v) < 0) {
                return false;
            }
        }
        if (knownNumericFacetValues.containsKey(maxInclusive)) {
            Comparable v = getNumericFacetValue(maxInclusive);
            Comparable input = maxInclusive.parseNumber(l);
            if (input.compareTo(v) > 0) {
                return false;
            }
        }
        if (knownNumericFacetValues.containsKey(maxExclusive)) {
            Comparable v = getNumericFacetValue(maxExclusive);
            Comparable input = maxExclusive.parseNumber(l);
            if (input.compareTo(v) >= 0) {
                return false;
            }
        }
        return true;
    }
}
