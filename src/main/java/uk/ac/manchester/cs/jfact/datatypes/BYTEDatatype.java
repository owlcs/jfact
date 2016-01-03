package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

class BYTEDatatype extends SHORTDatatype<Byte> {

    protected BYTEDatatype() {
        super(XSDVocabulary.BYTE, Utils.generateAncestors(SHORT));
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
        knownNumericFacetValues.put(minInclusive, new BigDecimal(Byte.MIN_VALUE));
        knownNumericFacetValues.put(maxInclusive, new BigDecimal(Byte.MAX_VALUE));
    }

    @Override
    public Byte parseValue(String s) {
        return Byte.valueOf(s);
    }
}
