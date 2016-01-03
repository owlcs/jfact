package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class NONNEGATIVEINTEGERDatatype<R extends Comparable<R>> extends INTEGERDatatype<R> {

    protected NONNEGATIVEINTEGERDatatype() {
        this(XSDVocabulary.NON_NEGATIVE_INTEGER, Utils.generateAncestors(INTEGER));
    }

    protected NONNEGATIVEINTEGERDatatype(HasIRI uri, Set<Datatype<?>> ancestors) {
        super(uri, ancestors);
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
        knownNumericFacetValues.put(minInclusive, new BigDecimal(0L));
    }

    @Override
    @SuppressWarnings("unchecked")
    public R parseValue(String s) {
        BigInteger parseValue = new BigInteger(s);
        if (parseValue.compareTo(BigInteger.ZERO) < 0) {
            throw new ArithmeticException("Non negative integer required, but found: " + s);
        }
        return (R) parseValue;
    }
}
