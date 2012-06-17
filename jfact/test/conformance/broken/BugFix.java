package conformance.broken;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import conformance.Factory;

public class BugFix {
	@Test
	public void testBugFix() throws OWLOntologyCreationException {
		final OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o = m.createOntology();
		OWLDataFactory f = m.getOWLDataFactory();
		final OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:t:t#p"));
		final OWLNamedIndividual i = f.getOWLNamedIndividual(IRI.create("urn:t:t#i"));
		m.addAxiom(o, f.getOWLDeclarationAxiom(p));
		m.addAxiom(o, f.getOWLDeclarationAxiom(i));
		final OWLDataOneOf owlDataOneOf = f.getOWLDataOneOf(f.getOWLLiteral(1),
				f.getOWLLiteral(2), f.getOWLLiteral(3), f.getOWLLiteral(4));
		final OWLDataOneOf owlDataOneOf2 = f.getOWLDataOneOf(f.getOWLLiteral(4),
				f.getOWLLiteral(5), f.getOWLLiteral(6));
		m.addAxiom(o, f.getOWLDataPropertyRangeAxiom(p, owlDataOneOf));
		m.addAxiom(o, f.getOWLDataPropertyRangeAxiom(p, owlDataOneOf2));
		m.addAxiom(o, f.getOWLClassAssertionAxiom(f.getOWLDataMinCardinality(1, p), i));
		OWLReasoner r = Factory.factory().createReasoner(o);
		final OWLDataPropertyAssertionAxiom ass = f.getOWLDataPropertyAssertionAxiom(p,
				i, 4);
		final boolean entailed = r.isEntailed(ass);
		assertTrue(entailed);
	}
}
