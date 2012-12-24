package benchmark;

import java.io.File;
import java.io.IOException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.jfact.JFactFactory;

@SuppressWarnings("javadoc")
public class SimplePrecomputeBenchmark {
    private static File mainPath = new File("./test/benchmark/test.owl");

    public static void main(String[] args) throws IOException, InterruptedException,
            OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(mainPath);
        OWLReasoner reasoner = new JFactFactory().createNonBufferingReasoner(ontology);
        long startTime;
        // Wait for start signal (to connect with VisualVM):
        System.out.println("Hit [Enter] to start the benchmark!");
        while (System.in.available() < 1) {
            Thread.sleep(100);
        }
        startTime = System.nanoTime();
        reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS,
                InferenceType.CLASS_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY,
                InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.SAME_INDIVIDUAL);
        System.out.println("Took " + (System.nanoTime() - startTime) / 1000000L
                + " ms to process.");
        // System.out.println("Number of calls to 'TBox.isSubHolds(...)': "
        // + TBox.isSubHoldsCounter);
        // System.out.println("Accumulated time for 'TBox.isSubHolds(...)': "
        // + TBox.isSubHoldsTime / 1000000L + " ms");
    }
}
