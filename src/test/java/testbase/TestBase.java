package testbase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.jfact.JFactFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;
import uk.ac.manchester.cs.owl.owlapi.concurrent.NoOpReadWriteLock;

@SuppressWarnings("javadoc")
public class TestBase {

    protected static OWLDataFactory df;
    protected static OWLOntologyManager masterManager;
    protected OWLOntologyManager m;

    @BeforeClass
    public static void setupManagers() {
        masterManager = OWLManager.createOWLOntologyManager();
        df = masterManager.getOWLDataFactory();
    }

    @Before
    public void setupManagersClean() {
        m = setupManager();
    }

    protected static OWLOntologyManager setupManager() {
        OWLOntologyManager manager = new OWLOntologyManagerImpl(df, new NoOpReadWriteLock());
        manager.getOntologyFactories().set(masterManager.getOntologyFactories());
        manager.getOntologyParsers().set(masterManager.getOntologyParsers());
        manager.getOntologyStorers().set(masterManager.getOntologyStorers());
        manager.getIRIMappers().set(masterManager.getIRIMappers());
        return manager;
    }

    protected static OWLReasonerFactory factory() {
        return new JFactFactory();
    }
}
