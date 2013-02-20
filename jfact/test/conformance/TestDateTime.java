package conformance;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.Assert.*;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.TimedConsoleProgressMonitor;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

@SuppressWarnings("javadoc")
public class TestDateTime {
    @Test
    public void testEqual() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLNamedIndividual x = f.getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLNamedIndividual y = f.getOWLNamedIndividual(IRI.create("urn:test:y"));
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date = f.getOWLLiteral("2008-07-08T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, x, date));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, y, date));
        m.addAxiom(o, f.getOWLFunctionalDataPropertyAxiom(p));
        m.addAxiom(o, f.getOWLSameIndividualAxiom(x, y));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertTrue("Ontology was supposed to be consistent!\n" + o.getLogicalAxioms(),
                r.isConsistent());
    }

    @Test
    public void testDifferent() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLNamedIndividual x = f.getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLNamedIndividual y = f.getOWLNamedIndividual(IRI.create("urn:test:y"));
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date1 = f.getOWLLiteral("2008-07-08T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        OWLLiteral date2 = f.getOWLLiteral("2008-07-10T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, x, date1));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, y, date2));
        m.addAxiom(o, f.getOWLFunctionalDataPropertyAxiom(p));
        m.addAxiom(o, f.getOWLSameIndividualAxiom(x, y));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertFalse("Ontology was supposed to be inconsistent!\n" + o.getLogicalAxioms(),
                r.isConsistent());
    }

    @Test
    public void testBetween() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLNamedIndividual x = f.getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLClass c = f.getOWLClass(IRI.create("urn:test:c"));
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date1 = f.getOWLLiteral("2008-07-08T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        OWLLiteral date3 = f.getOWLLiteral("2008-07-09T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        OWLLiteral date2 = f.getOWLLiteral("2008-07-10T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        OWLDataRange range = f.getOWLDatatypeRestriction(
                f.getOWLDatatype(OWL2Datatype.XSD_DATE_TIME.getIRI()),
                f.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, date1),
                f.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE, date2));
        OWLClassExpression psome = f.getOWLDataSomeValuesFrom(p, range);
        m.addAxiom(o, f.getOWLEquivalentClassesAxiom(c, psome));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, x, date3));
        m.addAxiom(o, f.getOWLFunctionalDataPropertyAxiom(p));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertTrue("x was supposed to be an instance of c!\n" + o.getLogicalAxioms(),
                r.isEntailed(f.getOWLClassAssertionAxiom(c, x)));
    }

    @Ignore
    @Test
    public void shouldPrecompute() throws OWLOntologyCreationException {
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        File base = new File("../JFact/robertstest");
        File url = new File(base, "periodic.owl");
        AutoIRIMapper mapper = new AutoIRIMapper(base, true);
        ontologyManager.addIRIMapper(mapper);
        OWLOntology ontology = ontologyManager.loadOntology(IRI.create(url));
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        OWLReasoner reasoner = Factory.factory().createReasoner(ontology,
                new SimpleConfiguration(new TimedConsoleProgressMonitor()));
        reasoner.precomputeInferences(InferenceType.values());
    }

    @Test
    public void testEqualDate() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLNamedIndividual x = f.getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLNamedIndividual y = f.getOWLNamedIndividual(IRI.create("urn:test:y"));
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date = f.getOWLLiteral("2008-07-08",
                f.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, x, date));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, y, date));
        m.addAxiom(o, f.getOWLFunctionalDataPropertyAxiom(p));
        m.addAxiom(o, f.getOWLSameIndividualAxiom(x, y));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertTrue("Ontology was supposed to be consistent!\n" + o.getLogicalAxioms(),
                r.isConsistent());
    }

    @Test
    public void testDifferentDate() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLNamedIndividual x = f.getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLNamedIndividual y = f.getOWLNamedIndividual(IRI.create("urn:test:y"));
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date1 = f.getOWLLiteral("2008-07-08",
                f.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        OWLLiteral date2 = f.getOWLLiteral("2008-07-10",
                f.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, x, date1));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, y, date2));
        m.addAxiom(o, f.getOWLFunctionalDataPropertyAxiom(p));
        m.addAxiom(o, f.getOWLSameIndividualAxiom(x, y));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertFalse("Ontology was supposed to be inconsistent!\n" + o.getLogicalAxioms(),
                r.isConsistent());
    }

    @Test
    public void testBetweenDate() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLNamedIndividual x = f.getOWLNamedIndividual(IRI.create("urn:test:x"));
        OWLClass c = f.getOWLClass(IRI.create("urn:test:c"));
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:test:p"));
        OWLLiteral date1 = f.getOWLLiteral("2008-07-08",
                f.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        OWLLiteral date3 = f.getOWLLiteral("2008-07-09",
                f.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        OWLLiteral date2 = f.getOWLLiteral("2008-07-10",
                f.getOWLDatatype(XSDVocabulary.DATE.getIRI()));
        OWLDataRange range = f.getOWLDatatypeRestriction(
                f.getOWLDatatype(OWL2Datatype.XSD_DATE_TIME.getIRI()),
                f.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, date1),
                f.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE, date2));
        OWLClassExpression psome = f.getOWLDataSomeValuesFrom(p, range);
        m.addAxiom(o, f.getOWLEquivalentClassesAxiom(c, psome));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, x, date3));
        m.addAxiom(o, f.getOWLFunctionalDataPropertyAxiom(p));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertTrue("x was supposed to be an instance of c!\n" + o.getLogicalAxioms(),
                r.isEntailed(f.getOWLClassAssertionAxiom(c, x)));
    }
}
