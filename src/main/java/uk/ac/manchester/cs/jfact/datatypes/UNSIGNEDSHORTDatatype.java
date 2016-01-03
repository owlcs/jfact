package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class UNSIGNEDSHORTDatatype<R extends Comparable<R>> extends UNSIGNEDINTDatatype<R> {

    protected UNSIGNEDSHORTDatatype() {
        this(XSDVocabulary.UNSIGNED_SHORT, Utils.generateAncestors(UNSIGNEDINT));
    }

    protected UNSIGNEDSHORTDatatype(HasIRI uri, Set<Datatype<?>> ancestors) {
        super(uri, ancestors);
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
        knownNumericFacetValues.put(minInclusive, BigDecimal.ZERO);
        knownNumericFacetValues.put(maxInclusive, new BigDecimal(65535));
    }

    @Override
    @SuppressWarnings("unchecked")
    public R parseValue(String s) {
        Integer parseShort = Integer.valueOf(s);
        if (parseShort.intValue() < 0) {
            throw new ArithmeticException("Unsigned short required, but found: " + s);
        }
        return (R) parseShort;
    }
}
