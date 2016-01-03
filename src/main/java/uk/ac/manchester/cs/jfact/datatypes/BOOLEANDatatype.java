package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

import uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace;

class BOOLEANDatatype extends AbstractDatatype<Boolean> {

    BOOLEANDatatype() {
        super(XSDVocabulary.BOOLEAN, Utils.getFacets(pattern, whiteSpace), Utils.generateAncestors(LITERAL));
        knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
    }

    @Override
    public cardinality getCardinality() {
        return cardinality.FINITE;
    }

    @Override
    public Collection<Literal<Boolean>> listValues() {
        // if all datatypes are compatible, the intersection is the two
        // booleans minu any restriction
        List<Literal<Boolean>> toReturn = new ArrayList<>(2);
        toReturn.add(buildLiteral(Boolean.toString(true)));
        toReturn.add(buildLiteral(Boolean.toString(false)));
        return toReturn;
    }

    @Override
    public Boolean parseValue(String s) {
        whitespace facet = (whitespace) whiteSpace.parse(knownNonNumericFacetValues.get(whiteSpace));
        return Boolean.valueOf(facet.normalize(s));
    }
}
