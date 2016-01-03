package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

class NEGATIVEINTEGERDatatype<R extends Comparable<R>> extends NONPOSITIVEINTEGERDatatype<R> {

    protected NEGATIVEINTEGERDatatype() {
        super(XSDVocabulary.NEGATIVE_INTEGER, Utils.generateAncestors(NONPOSITIVEINTEGER));
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
        knownNumericFacetValues.put(maxInclusive, new BigDecimal(-1L));
    }

    @Override
    @SuppressWarnings("unchecked")
    public R parseValue(String s) {
        BigInteger parse = new BigInteger(s);
        if (parse.compareTo(new BigInteger("-1")) > 0) {
            throw new ArithmeticException("Negative integer required, but found: " + s);
        }
        return (R) parse;
    }
}
