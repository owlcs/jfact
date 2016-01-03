package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

class POSITIVEINTEGERDatatype<R extends Comparable<R>> extends NONNEGATIVEINTEGERDatatype<R> {

    protected POSITIVEINTEGERDatatype() {
        super(XSDVocabulary.POSITIVE_INTEGER, Utils.generateAncestors(NONNEGATIVEINTEGER));
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
        knownNumericFacetValues.put(minInclusive, new BigDecimal(1L));
    }

    @Override
    @SuppressWarnings("unchecked")
    public R parseValue(String s) {
        BigInteger parseValue = new BigInteger(s);
        if (parseValue.compareTo(BigInteger.ZERO) <= 0) {
            throw new ArithmeticException("Positive integer required, but found: " + s);
        }
        return (R) parseValue;
    }
}
