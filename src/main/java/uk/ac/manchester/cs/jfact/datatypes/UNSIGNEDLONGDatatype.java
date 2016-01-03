package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class UNSIGNEDLONGDatatype<R extends Comparable<R>> extends NONNEGATIVEINTEGERDatatype<R> {

    protected UNSIGNEDLONGDatatype() {
        this(XSDVocabulary.UNSIGNED_LONG, Utils.generateAncestors(NONNEGATIVEINTEGER));
    }

    protected UNSIGNEDLONGDatatype(HasIRI uri, Set<Datatype<?>> ancestors) {
        super(uri, ancestors);
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
        knownNumericFacetValues.put(minInclusive, BigDecimal.ZERO);
        knownNumericFacetValues.put(maxInclusive, new BigDecimal("18446744073709551615"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public R parseValue(String s) {
        BigInteger b = new BigInteger(s);
        if (b.compareTo(BigInteger.ZERO) < 0) {
            throw new ArithmeticException("Unsigned long required, but found: " + s);
        }
        return (R) b;
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
