package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.REAL;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

class RATIONALDatatype<R extends Comparable<R>> extends REALDatatype<R> {

    protected RATIONALDatatype(HasIRI uri, Set<Facet> f, Set<Datatype<?>> ancestors) {
        super(uri, f, ancestors);
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
    }

    protected RATIONALDatatype() {
        this(OWL2Datatype.OWL_RATIONAL);
    }

    protected RATIONALDatatype(HasIRI uri) {
        this(uri, Collections.<Facet> emptySet(), Utils.generateAncestors(REAL));
    }

    @Override
    @SuppressWarnings("unchecked")
    public R parseValue(String s) {
        return (R) new BigDecimal(s);
    }
}
