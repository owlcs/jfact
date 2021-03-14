package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.FACETS4;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.LITERAL;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.WHITESPACE;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.increase;
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
        Float min = getMin();
        Float max = getMax();
        assert max != null;
        if (hasMaxExclusive() && hasMinExclusive()) {
            assert min != null;
            return
            // interval empty, no values admitted
            max.compareTo(min) == 0 ||
            // if diff is larger than 0, check
                max.compareTo((Float) increase(min)) < 0;
        }
        return max.compareTo(min) < 0;
    }
}
