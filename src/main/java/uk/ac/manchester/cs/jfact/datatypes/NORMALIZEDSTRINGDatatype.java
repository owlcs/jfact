package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.STRING;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whiteSpace;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace.REPLACE;

import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class NORMALIZEDSTRINGDatatype extends STRINGDatatype {

    protected NORMALIZEDSTRINGDatatype() {
        this(XSDVocabulary.NORMALIZED_STRING, Utils.generateAncestors(STRING));
    }

    protected NORMALIZEDSTRINGDatatype(HasIRI uri, Set<Datatype<?>> ancestors) {
        super(uri, ancestors);
        knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, REPLACE);
    }
}
