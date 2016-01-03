package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whiteSpace;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace.COLLAPSE;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

class HEXBINARYDatatype extends AbstractDatatype<String> {

    HEXBINARYDatatype() {
        super(XSDVocabulary.HEX_BINARY, STRINGFACETS, Utils.generateAncestors(LITERAL));
        knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
    }

    @Override
    public String parseValue(String s) {
        return COLLAPSE.normalize(s);
    }

    @Override
    public boolean isInValueSpace(String s) {
        // all characters are numbers, or ABCDEF
        return s.chars().allMatch(c -> Character.isDigit((char) c) || "ABCDEF".indexOf(c) > -1);
    }
}
