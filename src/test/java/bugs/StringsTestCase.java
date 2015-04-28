package bugs;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.owlapi.api.test.baseclasses.TestBase;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import uk.ac.manchester.cs.jfact.JFactFactory;

@SuppressWarnings("javadoc")
public class StringsTestCase extends TestBase {

    @Test
    public void shouldFindAllLiteralsEquivalent()
        throws OWLOntologyCreationException {
        OWLOntology o = m.createOntology();
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("urn:test:p"));
        m.addAxiom(o, df.getOWLFunctionalDataPropertyAxiom(p));
        OWLNamedIndividual i = df.getOWLNamedIndividual(IRI.create(
        "urn:test:i"));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, i, df
        .getOWLLiteral("test")));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, i, df
        .getOWLLiteral("test", "")));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, i, df
        .getOWLLiteral("test", OWL2Datatype.RDF_PLAIN_LITERAL)));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(p, i, df
        .getOWLLiteral("test", OWL2Datatype.XSD_STRING)));
        OWLReasoner r = new JFactFactory().createReasoner(o);
        assertTrue(r.isConsistent());
    }
}
