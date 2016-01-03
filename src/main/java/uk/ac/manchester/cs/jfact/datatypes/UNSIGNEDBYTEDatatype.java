package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

abstract class UNSIGNEDBYTEDatatype<R extends Comparable<R>> extends UNSIGNEDSHORTDatatype<R> {

    protected UNSIGNEDBYTEDatatype() {
        super(XSDVocabulary.UNSIGNED_BYTE, Utils.generateAncestors(UNSIGNEDSHORT));
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
        knownNumericFacetValues.put(minInclusive, new BigDecimal((short) 0));
        knownNumericFacetValues.put(maxInclusive, new BigDecimal((short) 255));
    }
}
