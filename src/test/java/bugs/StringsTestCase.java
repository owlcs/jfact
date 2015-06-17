package bugs;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import testbase.TestBase;
import uk.ac.manchester.cs.jfact.JFactFactory;

@SuppressWarnings("javadoc")
public class StringsTestCase extends TestBase {

    @Test
    public void shouldFindAllLiteralsEquivalent() throws OWLOntologyCreationException {
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology();
        OWLDataFactory df = OWLManager.getOWLDataFactory();
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("urn:test:p"));
        o.add(df.getOWLFunctionalDataPropertyAxiom(p));
        OWLNamedIndividual i = df.getOWLNamedIndividual(IRI.create("urn:test:i"));
        o.add(df.getOWLDataPropertyAssertionAxiom(p, i, df.getOWLLiteral("test")));
        o.add(df.getOWLDataPropertyAssertionAxiom(p, i, df.getOWLLiteral("test", "")));
        o.add(df.getOWLDataPropertyAssertionAxiom(p, i, df.getOWLLiteral("test", OWL2Datatype.RDF_PLAIN_LITERAL)));
        o.add(df.getOWLDataPropertyAssertionAxiom(p, i, df.getOWLLiteral("test", OWL2Datatype.XSD_STRING)));
        OWLReasoner r = new JFactFactory().createReasoner(o);
        assertTrue(r.isConsistent());
    }
}
