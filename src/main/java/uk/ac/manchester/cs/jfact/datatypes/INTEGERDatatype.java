package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.math.BigInteger;
import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class INTEGERDatatype<R extends Comparable<R>> extends DECIMALDatatype<R> {

    protected INTEGERDatatype() {
        this(XSDVocabulary.INTEGER, Utils.generateAncestors(DECIMAL));
    }

    protected INTEGERDatatype(HasIRI uri, Set<Datatype<?>> ancestors) {
        super(uri, ancestors);
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
        knownNonNumericFacetValues.put(fractionDigits, Integer.valueOf(0));
    }

    @Override
    @SuppressWarnings("unchecked")
    public R parseValue(String s) {
        return (R) new BigInteger(s);
    }
}
