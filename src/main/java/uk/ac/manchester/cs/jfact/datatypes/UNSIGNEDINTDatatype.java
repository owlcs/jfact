package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class UNSIGNEDINTDatatype<R extends Comparable<R>> extends UNSIGNEDLONGDatatype<R> {

    protected UNSIGNEDINTDatatype() {
        this(XSDVocabulary.UNSIGNED_INT, Utils.generateAncestors(UNSIGNEDLONG));
    }

    protected UNSIGNEDINTDatatype(HasIRI uri, Set<Datatype<?>> ancestors) {
        super(uri, ancestors);
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
        knownNumericFacetValues.put(minInclusive, new BigDecimal(0L));
        knownNumericFacetValues.put(maxInclusive, new BigDecimal(4294967295L));
    }

    @Override
    @SuppressWarnings("unchecked")
    public R parseValue(String s) {
        Long parseInt = Long.valueOf(s);
        if (parseInt.longValue() < 0) {
            throw new ArithmeticException("Unsigned int required, but found: " + s);
        }
        return (R) parseInt;
    }
}
