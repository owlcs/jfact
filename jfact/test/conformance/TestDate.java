package conformance;

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

public class TestDate {
	@Test
	public void testEqual() throws OWLOntologyCreationException {
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
	public void testDifferent() throws OWLOntologyCreationException {
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
	public void testBetween() throws OWLOntologyCreationException {
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
