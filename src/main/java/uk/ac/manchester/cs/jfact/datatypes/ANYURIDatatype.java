package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whiteSpace;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace.COLLAPSE;

import java.net.URI;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

class ANYURIDatatype extends AbstractDatatype<String> {

    ANYURIDatatype() {
        super(XSDVocabulary.ANY_URI, STRINGFACETS, Utils.generateAncestors(LITERAL));
        knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
    }

    @Override
    public String parseValue(String s) {
        return COLLAPSE.normalize(s);
    }

    @Override
    public boolean isInValueSpace(String l) {
        try {
            URI.create(l);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
