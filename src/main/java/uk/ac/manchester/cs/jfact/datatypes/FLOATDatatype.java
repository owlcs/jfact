package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whiteSpace;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

class FLOATDatatype extends AbstractNumericDatatype<Float> {

    protected FLOATDatatype() {
        super(XSDVocabulary.FLOAT, FACETS4, Utils.generateAncestors(LITERAL));
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
    public Float parseValue(String s) {
        String trim = s.trim();
        if ("-INF".equals(trim)) {
            return Float.valueOf(Float.NEGATIVE_INFINITY);
        }
        if ("INF".equals(trim)) {
            return Float.valueOf(Float.POSITIVE_INFINITY);
        }
        return Float.valueOf(s);
    }

    @Override
    public boolean isCompatible(Datatype<?> type) {
        return isDatatypeCompatible(type);
    }

    @Override
    public boolean emptyValueSpace() {
        if (!hasMin() || !hasMax()) {
            return false;
        }
        if (hasMaxExclusive() && hasMinExclusive()) {
            if (getMin().compareTo(getMax()) == 0) {
                // interval empty, no values admitted
                return true;
            }
            // if diff is larger than 0, check
            return getMax().compareTo((Float) increase(getMin())) < 0;
        }
        return getMax().compareTo(getMin()) < 0;
    }
}
