package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whiteSpace;

import java.math.BigDecimal;
import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class DECIMALDatatype<R extends Comparable<R>> extends RATIONALDatatype<R> {

    DECIMALDatatype() {
        this(XSDVocabulary.DECIMAL, Utils.generateAncestors(RATIONAL));
    }

    DECIMALDatatype(HasIRI uri, Set<Datatype<?>> ancestors) {
        super(uri, Utils.getFacets(DIGS, PEW, MINMAX), ancestors);
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public R parseValue(String s) {
        return (R) new BigDecimal(s);
    }

    @Override
    public ordered getOrdered() {
        return ordered.TOTAL;
    }
}
