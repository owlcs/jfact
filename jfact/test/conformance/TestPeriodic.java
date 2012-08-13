package conformance;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.TimedConsoleProgressMonitor;
import org.semanticweb.owlapi.util.AutoIRIMapper;

public class TestPeriodic {
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        File base = new File("../JFact/robertstest");
        File url = new File(base, "periodic.owl");
        AutoIRIMapper mapper = new AutoIRIMapper(base, true);
        ontologyManager.addIRIMapper(mapper);
        OWLOntology ontology = ontologyManager.loadOntology(IRI.create(url));
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        OWLReasoner reasoner = Factory.factory().createReasoner(ontology,
                new SimpleConfiguration(new TimedConsoleProgressMonitor()));
        reasoner.precomputeInferences(InferenceType.values());
    }
}
