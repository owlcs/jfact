package bugs;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.jfact.JFactFactory;

public class AddIndividualsAfterLoadingTestCase {
    @Test
    public void shouldLoadAndNotFailQuery() throws Exception {
        // given
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology();
        OWLOntologyManager m = o.getOWLOntologyManager();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLClass c1 = f.getOWLClass(IRI.create("urn:test#c1"));
        OWLClass c2 = f.getOWLClass(IRI.create("urn:test#c2"));
        OWLObjectProperty p = f.getOWLObjectProperty(IRI.create("urn:test#p"));
        m.addAxiom(o, f.getOWLDisjointClassesAxiom(c1, c2));
        m.addAxiom(o, f.getOWLObjectPropertyDomainAxiom(p, c1));
        m.addAxiom(o, f.getOWLObjectPropertyRangeAxiom(p, c2));
        // OWLReasoner r = new JFactFactory().createReasoner(o);
        // r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        OWLIndividual i = f.getOWLNamedIndividual(IRI.create("urn:test#i"));
        OWLIndividual j = f.getOWLNamedIndividual(IRI.create("urn:test#j"));
        m.addAxiom(o, f.getOWLObjectPropertyAssertionAxiom(p, i, j));
        // r.flush();
        // r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        OWLReasoner r = new JFactFactory().createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        OWLIndividual k = f.getOWLNamedIndividual(IRI.create("urn:test#k"));
        OWLIndividual l = f.getOWLNamedIndividual(IRI.create("urn:test#l"));
        m.addAxiom(o, f.getOWLObjectPropertyAssertionAxiom(p, k, l));
        r.flush();
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
    }
}
