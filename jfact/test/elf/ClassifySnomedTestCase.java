package elf;

import java.io.File;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.BufferingMode;

import uk.ac.manchester.cs.jfact.JFactReasoner;
import uk.ac.manchester.cs.jfact.elf.ELFReasoner;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

public class ClassifySnomedTestCase {
    @Test
    public void classifyAcute() throws Exception {
        OWLOntology o = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(new File("snomed/acute_module.owl"));
        JFactReasoner r = new JFactReasoner(o, new JFactReasonerConfiguration(),
                BufferingMode.BUFFERING);
        ELFReasoner reasoner = new ELFReasoner(new JFactReasonerConfiguration(),
                r.getOntology());
        reasoner.classify();
    }

    @Test
    public void classifyChronic() throws Exception {
        OWLOntology o = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(new File("snomed/chronic_module.owl"));
        JFactReasoner r = new JFactReasoner(o, new JFactReasonerConfiguration(),
                BufferingMode.BUFFERING);
        ELFReasoner reasoner = new ELFReasoner(new JFactReasonerConfiguration(),
                r.getOntology());
        reasoner.classify();
    }

    @Test
    public void classifyPresent() throws Exception {
        OWLOntology o = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new File("snomed/present_clinical_finding_module.owl"));
        JFactReasoner r = new JFactReasoner(o, new JFactReasonerConfiguration(),
                BufferingMode.BUFFERING);
        ELFReasoner reasoner = new ELFReasoner(new JFactReasonerConfiguration(),
                r.getOntology());
        reasoner.classify();
    }
}
