package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whiteSpace;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace.PRESERVE;

import java.util.Set;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class STRINGDatatype extends AbstractDatatype<String> {

    public STRINGDatatype() {
        this(XSDVocabulary.STRING, Utils.generateAncestors(PLAINLITERAL));
    }

    STRINGDatatype(HasIRI uri, Set<Datatype<?>> ancestors) {
        super(uri, STRINGFACETS, ancestors);
        knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, PRESERVE);
    }

    @Override
    public String parseValue(String s) {
        return s;
    }
}
