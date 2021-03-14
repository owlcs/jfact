package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.LITERAL;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace.COLLAPSE;

import java.io.ByteArrayInputStream;
import java.util.Collections;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.xml.sax.InputSource;

class XMLLITERALDatatype extends AbstractDatatype<String> {

    protected XMLLITERALDatatype() {
        super(OWLRDFVocabulary.RDF_XML_LITERAL, Collections.<Facet>emptySet(),
            Utils.generateAncestors(LITERAL));
        knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
    }

    @Override
    public String parseValue(String s) {
        // Sort of arbitrary decision; the specs say it depends on the
        // XML datatype whitespace normalization policy, but that's not
        // clear. Some W3C tests assume that text elements are irrelevant
        return COLLAPSE.normalize(s);
    }

    @Override
    public boolean isInValueSpace(String l) {
        try {
            FactoryHolder.builder().parse(new InputSource(new ByteArrayInputStream(l.getBytes())));
        } catch (@SuppressWarnings("unused") Exception e) {
            return false;
        }
        return true;
    }

    private static final class FactoryHolder {
        static final DocumentBuilderFactory FACTORY = factory();

        static DocumentBuilder builder() throws ParserConfigurationException {
            return FactoryHolder.FACTORY.newDocumentBuilder();
        }

        private static DocumentBuilderFactory factory() {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            f.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            return f;
        }
    }
}
