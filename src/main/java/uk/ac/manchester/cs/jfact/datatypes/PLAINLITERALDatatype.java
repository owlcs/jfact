package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.LITERAL;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

class PLAINLITERALDatatype extends AbstractDatatype<String> {

    PLAINLITERALDatatype() {
        super(OWLRDFVocabulary.RDF_PLAIN_LITERAL, Utils.getFacets(length, minLength, maxLength, pattern, enumeration),
            Utils.generateAncestors(LITERAL));
        knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
    }

    @Override
    public String parseValue(String s) {
        return s;
    }
}
