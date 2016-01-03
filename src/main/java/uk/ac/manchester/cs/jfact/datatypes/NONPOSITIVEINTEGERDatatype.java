package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class NONPOSITIVEINTEGERDatatype<R extends Comparable<R>> extends INTEGERDatatype<R> {

    protected NONPOSITIVEINTEGERDatatype() {
        this(XSDVocabulary.NON_POSITIVE_INTEGER, Utils.generateAncestors(INTEGER));
    }

    protected NONPOSITIVEINTEGERDatatype(HasIRI uri, Set<Datatype<?>> ancestors) {
        super(uri, ancestors);
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
        knownNumericFacetValues.put(maxInclusive, new BigDecimal(0L));
    }

    @Override
    @SuppressWarnings("unchecked")
    public R parseValue(String s) {
        BigInteger parse = new BigInteger(s);
        if (parse.compareTo(BigInteger.ZERO) > 0) {
            throw new ArithmeticException("Non positive integer required, but found: " + s);
        }
        return (R) parse;
    }
}
