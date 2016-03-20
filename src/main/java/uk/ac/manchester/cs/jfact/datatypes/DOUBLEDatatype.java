package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whiteSpace;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

class DOUBLEDatatype extends AbstractNumericDatatype<Double> {

    DOUBLEDatatype() {
        super(XSDVocabulary.DOUBLE, Utils.getFacets(PEW, MINMAX), Utils.generateAncestors(LITERAL));
        knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
    }

    @Override
    public boolean getBounded() {
        return true;
    }

    @Override
    public cardinality getCardinality() {
        return cardinality.FINITE;
    }

    @Override
    public boolean getNumeric() {
        return true;
    }

    @Override
    public Double parseValue(String s) {
        return Double.valueOf(s);
    }

    @Override
    public boolean isCompatible(Datatype<?> type) {
        return isDatatypeCompatible(type);
    }
}
