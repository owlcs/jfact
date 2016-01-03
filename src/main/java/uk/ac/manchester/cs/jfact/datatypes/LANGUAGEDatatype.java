package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

class LANGUAGEDatatype extends TOKENDatatype {

    protected LANGUAGEDatatype() {
        super(XSDVocabulary.LANGUAGE, Utils.generateAncestors(TOKEN));
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, "[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*");
    }
}
