package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class NCNAMEDatatype extends NAMEDatatype {

    protected NCNAMEDatatype() {
        super(XSDVocabulary.NCNAME, Utils.generateAncestors(NAME));
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        knownNonNumericFacetValues.put(pattern, "[\\i-[:]][\\c-[:]]*");
    }

    @Override
    public IRI getDatatypeIRI() {
        return uri;
    }
}
