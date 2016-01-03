package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

class NMTOKENSDatatype extends NMTOKENDatatype {

    protected NMTOKENSDatatype() {
        super(XSDVocabulary.NMTOKENS, Utils.generateAncestors(NMTOKEN));
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(minLength, Integer.valueOf(1));
    }
}
