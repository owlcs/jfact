package conformance;

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

@SuppressWarnings("javadoc")
public class TopObjectPropertyTest {
    OWLDataFactory df = OWLManager.getOWLDataFactory();

    @Test
    public void testReasoner3() throws OWLOntologyCreationException {
        OWLOntologyManager mngr = OWLManager.createOWLOntologyManager();
        OWLOntology ont = mngr.createOntology();
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("http://example.com/p"));
        // just so p is known in the ontology
        mngr.applyChange(new AddAxiom(ont, df.getOWLFunctionalDataPropertyAxiom(p)));
        OWLReasonerFactory fac = Factory.factory();
        OWLReasoner r = fac.createNonBufferingReasoner(ont);
        assertTrue(r.isEntailed(df.getOWLSubClassOfAxiom(
                df.getOWLDataSomeValuesFrom(p, df.getIntegerOWLDatatype()),
                df.getOWLDataSomeValuesFrom(p, df.getTopDatatype()))));
    }

    @Test
    public void testReasoner4() throws OWLOntologyCreationException {
        OWLOntologyManager mngr = OWLManager.createOWLOntologyManager();
        OWLOntology ont = mngr.createOntology();
        OWLReasonerFactory fac = Factory.factory();
        OWLReasoner r = fac.createNonBufferingReasoner(ont);
        assertFalse(r.getTopDataPropertyNode().getEntities().isEmpty());
    }

}
