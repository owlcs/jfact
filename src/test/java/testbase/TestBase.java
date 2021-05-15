package testbase;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import conformancetests.JUnitRunner;
import conformancetests.TestClasses;
import uk.ac.manchester.cs.jfact.JFactFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;
import uk.ac.manchester.cs.owl.owlapi.concurrent.NoOpReadWriteLock;

@SuppressWarnings("javadoc")
public class TestBase {

    protected static OWLDataFactory df;
    protected static OWLOntologyManager masterManager;
    protected OWLOntologyManager m;
    protected String premise;
    protected String conclusion = "";

    protected void test(String id, TestClasses tc, String d) {
        JUnitRunner r = new JUnitRunner(m, premise, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }

    protected String asString(String resource) {
        try (InputStream in = getClass().getResourceAsStream(resource)) {
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }

    protected OWLOntology asString(OWLOntologyManager man, String resource)
        throws OWLOntologyCreationException {
        try (InputStream in = getClass().getResourceAsStream(resource)) {
            return man.loadOntologyFromOntologyDocument(in);
        } catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }

    @BeforeAll
    public static void setupManagers() {
        masterManager = OWLManager.createOWLOntologyManager();
        df = masterManager.getOWLDataFactory();
    }

    @BeforeEach
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
