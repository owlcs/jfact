package conformance.broken;

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import conformance.Factory;

public class BugFix {
    @Test
    public void testBugFix() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:t:t#p"));
        OWLNamedIndividual i = f.getOWLNamedIndividual(IRI.create("urn:t:t#i"));
        m.addAxiom(o, f.getOWLDeclarationAxiom(p));
        m.addAxiom(o, f.getOWLDeclarationAxiom(i));
        OWLDataOneOf owlDataOneOf = f.getOWLDataOneOf(f.getOWLLiteral(1),
                f.getOWLLiteral(2), f.getOWLLiteral(3), f.getOWLLiteral(4));
        OWLDataOneOf owlDataOneOf2 = f.getOWLDataOneOf(f.getOWLLiteral(4),
                f.getOWLLiteral(5), f.getOWLLiteral(6));
        m.addAxiom(o, f.getOWLDataPropertyRangeAxiom(p, owlDataOneOf));
        m.addAxiom(o, f.getOWLDataPropertyRangeAxiom(p, owlDataOneOf2));
        m.addAxiom(o, f.getOWLClassAssertionAxiom(f.getOWLDataMinCardinality(1, p), i));
        OWLReasoner r = Factory.factory().createReasoner(o);
        OWLDataPropertyAssertionAxiom ass = f.getOWLDataPropertyAssertionAxiom(p, i, 4);
        boolean entailed = r.isEntailed(ass);
        assertTrue(entailed);
    }
}
