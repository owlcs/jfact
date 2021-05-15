package bugs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import testbase.TestBase;
import uk.ac.manchester.cs.jfact.JFactFactory;

class StringsTestCase extends TestBase {

    @Test
    void shouldFindAllLiteralsEquivalent() throws OWLOntologyCreationException {
        OWLOntology o = m.createOntology();
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("urn:test:p"));
        o.add(df.getOWLFunctionalDataPropertyAxiom(p));
        OWLNamedIndividual i = df.getOWLNamedIndividual(IRI.create("urn:test:i"));
        o.add(df.getOWLDataPropertyAssertionAxiom(p, i, df.getOWLLiteral("test")));
        o.add(df.getOWLDataPropertyAssertionAxiom(p, i, df.getOWLLiteral("test", "")));
        o.add(df.getOWLDataPropertyAssertionAxiom(p, i,
            df.getOWLLiteral("test", OWL2Datatype.RDF_PLAIN_LITERAL)));
        o.add(df.getOWLDataPropertyAssertionAxiom(p, i,
            df.getOWLLiteral("test", OWL2Datatype.XSD_STRING)));
        OWLReasoner r = new JFactFactory().createReasoner(o);
        assertTrue(r.isConsistent());
    }
}
