package correctness;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.jfact.JFactFactory;
import uk.ac.manchester.cs.jfact.JFactReasoner;
import uk.ac.manchester.cs.jfact.split.ModuleType;

public class TestModuleExtraction {
    @Test
    public void shouldFindSevenAxioms() throws OWLOntologyCreationException {
        OWLOntology o = OWLManager
                .createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new File(
                                "/Users/ignazio/bioportal/aba-adult-mouse-brain/aba-adult-mouse-brain_main.owl"));
        OWLDataFactory f = OWLManager.getOWLDataFactory();
        JFactReasoner reasoner = (JFactReasoner) new JFactFactory().createReasoner(o);
        Set<OWLEntity> signature = new HashSet<OWLEntity>();
        OWLClass pyr = f.getOWLClass(IRI
                .create("http://mouse.brain-map.org/atlas/index.html#PYR"));
        signature.add(pyr);
        OWLClass uvu = f.getOWLClass(IRI
                .create("http://mouse.brain-map.org/atlas/index.html#UVU"));
        signature.add(uvu);
        Set<OWLAxiom> module = reasoner.getModule(signature, false,
                ModuleType.values()[0]);
        for (OWLAxiom x : module) {
            System.out.println(x);
        }
        assertTrue(module.contains(f.getOWLDisjointClassesAxiom(pyr, uvu)));
        OWLClass verm = f.getOWLClass(IRI
                .create("http://mouse.brain-map.org/atlas/index.html#VERM"));
        assertTrue(module.contains(f.getOWLSubClassOfAxiom(pyr, verm)));
        assertTrue(module.contains(f.getOWLSubClassOfAxiom(uvu, verm)));
        assertEquals(7, module.size());
        // SubClassOf(<http://mouse.brain-map.org/atlas/index.html#CB>
        // <http://mouse.brain-map.org/atlas/index.html#Brain>),
        // SubClassOf(<http://mouse.brain-map.org/atlas/index.html#VERM>
        // <http://mouse.brain-map.org/atlas/index.html#CBX>),
        // SubClassOf(<http://mouse.brain-map.org/atlas/index.html#Brain>
        // <http://mouse.brain-map.org/atlas/index.html#structures>),
        // SubClassOf(<http://mouse.brain-map.org/atlas/index.html#CBX>
        // <http://mouse.brain-map.org/atlas/index.html#CB>)]
    }
}
