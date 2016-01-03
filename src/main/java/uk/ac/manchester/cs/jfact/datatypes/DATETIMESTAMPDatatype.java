package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.DATETIME;

import org.semanticweb.owlapi.vocab.XSDVocabulary;

class DATETIMESTAMPDatatype extends DATETIMEDatatype {

    DATETIMESTAMPDatatype() {
        super(XSDVocabulary.DATE_TIME_STAMP, Utils.generateAncestors(DATETIME));
    }
}
