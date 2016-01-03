package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class LONGDatatype<R extends Comparable<R>> extends INTEGERDatatype<R> {

    protected LONGDatatype() {
        this(XSDVocabulary.LONG, Utils.generateAncestors(INTEGER));
    }

    protected LONGDatatype(HasIRI uri, Set<Datatype<?>> ancestors) {
        super(uri, ancestors);
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
        knownNumericFacetValues.put(minInclusive, new BigDecimal(Long.MIN_VALUE));
        knownNumericFacetValues.put(maxInclusive, new BigDecimal(Long.MAX_VALUE));
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public R parseValue(String s) {
        return (R) Long.valueOf(s);
    }

    @Override
    public boolean getBounded() {
        return true;
    }

    @Override
    public cardinality getCardinality() {
        return cardinality.FINITE;
    }
}
