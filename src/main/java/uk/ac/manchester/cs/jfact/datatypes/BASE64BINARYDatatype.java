package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whiteSpace;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace.COLLAPSE;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

class BASE64BINARYDatatype extends AbstractDatatype<String> {

    BASE64BINARYDatatype() {
        super(XSDVocabulary.BASE_64_BINARY, STRINGFACETS, Utils.generateAncestors(LITERAL));
        knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, COLLAPSE);
    }

    @Override
    public String parseValue(String s) {
        return COLLAPSE.normalize(s);
    }

    @Override
    public boolean isInValueSpace(String s) {
        // all characters are letters, numbers, or +/=
        return s.chars().allMatch(c -> Character.isLetterOrDigit((char) c) || "+/=".indexOf(c) > -1);
    }
}
