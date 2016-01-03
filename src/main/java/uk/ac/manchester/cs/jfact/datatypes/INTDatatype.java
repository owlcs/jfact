package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class INTDatatype<R extends Comparable<R>> extends LONGDatatype<R> {

    protected INTDatatype() {
        this(XSDVocabulary.INT, Utils.generateAncestors(LONG));
    }

    protected INTDatatype(HasIRI uri, Set<Datatype<?>> ancestors) {
        super(uri, ancestors);
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
        knownNumericFacetValues.put(minInclusive, new BigDecimal(Integer.MIN_VALUE));
        knownNumericFacetValues.put(maxInclusive, new BigDecimal(Integer.MAX_VALUE));
    }

    @Override
    @SuppressWarnings("unchecked")
    public R parseValue(String s) {
        return (R) Integer.valueOf(s);
    }
}
