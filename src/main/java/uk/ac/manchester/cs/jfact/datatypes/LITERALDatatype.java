package uk.ac.manchester.cs.jfact.datatypes;

import java.util.Collections;

import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

class LITERALDatatype extends AbstractDatatype<String> {

    LITERALDatatype() {
        super(OWLRDFVocabulary.RDFS_LITERAL, Collections.<Facet> emptySet(), Collections.<Datatype<?>> emptySet());
    }

    @Override
    public String parseValue(String s) {
        return s;
    }
}
