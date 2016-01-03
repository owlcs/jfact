package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.LITERAL;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace.COLLAPSE;

import java.io.ByteArrayInputStream;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilderFactory;

import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

class XMLLITERALDatatype extends AbstractDatatype<String> {

    protected XMLLITERALDatatype() {
        super(OWLRDFVocabulary.RDF_XML_LITERAL, Collections.<Facet> emptySet(), Utils.generateAncestors(LITERAL));
        knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
    }

    @Override
    public String parseValue(String s) {
        // XXX sort of arbitrary decision; the specs say it depends on the
        // XML datatype whitespace normalization policy, but that's not
        // clear. Some W3C tests assume that text elements are irrelevant
        return COLLAPSE.normalize(s);
    }

    @Override
    public boolean isInValueSpace(String l) {
        try {
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(l.getBytes()));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
