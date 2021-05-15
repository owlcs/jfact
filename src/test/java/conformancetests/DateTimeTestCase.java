package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import testbase.TestBase;

class DateTimeTestCase extends TestBase {

    @Test
    void testEqual() throws OWLOntologyCreationException {
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = df.getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLNamedIndividual y = df.getOWLNamedIndividual(IRI.create("urn:test:y"));
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date =
            df.getOWLLiteral("2008-07-08T20:44:11.656+01:00", OWL2Datatype.XSD_DATE_TIME);
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, x, date));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, y, date));
        m.addAxiom(o, df.getOWLFunctionalDataPropertyAxiom(p));
        m.addAxiom(o, df.getOWLSameIndividualAxiom(x, y));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(r.isConsistent());
    }

    @Test
    void testDifferent() throws OWLOntologyCreationException {
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = df.getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLNamedIndividual y = df.getOWLNamedIndividual(IRI.create("urn:test:y"));
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date1 =
            df.getOWLLiteral("2008-07-08T20:44:11.656+01:00", OWL2Datatype.XSD_DATE_TIME);
        OWLLiteral date2 =
            df.getOWLLiteral("2008-07-10T20:44:11.656+01:00", OWL2Datatype.XSD_DATE_TIME);
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, x, date1));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, y, date2));
        m.addAxiom(o, df.getOWLFunctionalDataPropertyAxiom(p));
        m.addAxiom(o, df.getOWLSameIndividualAxiom(x, y));
        OWLReasoner r = factory().createReasoner(o);
        assertFalse(r.isConsistent());
    }

    @Test
    void testBetween() throws OWLOntologyCreationException {
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = df.getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLClass c = df.getOWLClass(IRI.create("urn:test:c"));
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date1 =
            df.getOWLLiteral("2008-07-08T20:44:11.656+01:00", OWL2Datatype.XSD_DATE_TIME);
        OWLLiteral date3 =
            df.getOWLLiteral("2008-07-09T20:44:11.656+01:00", OWL2Datatype.XSD_DATE_TIME);
        OWLLiteral date2 =
            df.getOWLLiteral("2008-07-10T20:44:11.656+01:00", OWL2Datatype.XSD_DATE_TIME);
        OWLDataRange range =
            df.getOWLDatatypeRestriction(df.getOWLDatatype(OWL2Datatype.XSD_DATE_TIME.getIRI()),
                df.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, date1),
                df.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE, date2));
        OWLClassExpression psome = df.getOWLDataSomeValuesFrom(p, range);
        m.addAxiom(o, df.getOWLEquivalentClassesAxiom(c, psome));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, x, date3));
        m.addAxiom(o, df.getOWLFunctionalDataPropertyAxiom(p));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(r.isEntailed(df.getOWLClassAssertionAxiom(c, x)));
    }

    @Test
    void testEqualDate() throws OWLOntologyCreationException {
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = df.getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLNamedIndividual y = df.getOWLNamedIndividual(IRI.create("urn:test:y"));
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date =
            df.getOWLLiteral("2008-07-08", df.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, x, date));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, y, date));
        m.addAxiom(o, df.getOWLFunctionalDataPropertyAxiom(p));
        m.addAxiom(o, df.getOWLSameIndividualAxiom(x, y));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(r.isConsistent());
    }

    @Test
    void testDifferentDate() throws OWLOntologyCreationException {
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = df.getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLNamedIndividual y = df.getOWLNamedIndividual(IRI.create("urn:test:y"));
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date1 =
            df.getOWLLiteral("2008-07-08", df.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        OWLLiteral date2 =
            df.getOWLLiteral("2008-07-10", df.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, x, date1));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, y, date2));
        m.addAxiom(o, df.getOWLFunctionalDataPropertyAxiom(p));
        m.addAxiom(o, df.getOWLSameIndividualAxiom(x, y));
        OWLReasoner r = factory().createReasoner(o);
        assertFalse(r.isConsistent());
    }

    @Test
    void testBetweenDate() throws OWLOntologyCreationException {
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = df.getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLClass c = df.getOWLClass(IRI.create("urn:test:c"));
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date1 =
            df.getOWLLiteral("2008-07-08", df.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        OWLLiteral date3 =
            df.getOWLLiteral("2008-07-09", df.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        OWLLiteral date2 =
            df.getOWLLiteral("2008-07-10", df.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        OWLDataRange range =
            df.getOWLDatatypeRestriction(df.getOWLDatatype(OWL2Datatype.XSD_DATE_TIME.getIRI()),
                df.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, date1),
                df.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE, date2));
        OWLClassExpression psome = df.getOWLDataSomeValuesFrom(p, range);
        m.addAxiom(o, df.getOWLEquivalentClassesAxiom(c, psome));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, x, date3));
        m.addAxiom(o, df.getOWLFunctionalDataPropertyAxiom(p));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(r.isEntailed(df.getOWLClassAssertionAxiom(c, x)));
    }
}
