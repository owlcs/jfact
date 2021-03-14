package bugs.debug;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import testbase.TestBase;
import uk.ac.manchester.cs.jfact.JFactFactory;

@Ignore
@SuppressWarnings("javadoc")
public class ReplicateOREFailuresTest extends TestBase {

    private static final String POOL_SAMPLE = "/Users/ignazio/workspace/pool_sample/files/";

    @Test
    public void shouldore_ont_1270() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_1270.owl"));
    }

    @Test
    public void shouldore_ont_9151() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_9151.owl"));
    }

    @Test
    public void shouldore_ont_9654() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_9654.owl"));
    }

    @Test
    public void shouldore_ont_7729() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_7729.owl"));
    }

    @Test
    public void shouldore_ont_7192() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_7192.owl"));
    }

    @Test
    public void shouldore_ont_9899() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_9899.owl"));
    }

    @Test
    public void shouldore_ont_3282() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_3282.owl"));
    }

    @Test
    public void shouldore_ont_7646() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_7646.owl"));
    }

    @Test
    public void shouldore_ont_1199() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_1199.owl"));
    }

    @Test
    public void shouldore_ont_7557() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_7557.owl"));
    }

    @Test
    public void shouldore_ont_1938() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_1938.owl"));
    }

    @Test
    public void shouldore_ont_6132() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_6132.owl"));
    }

    @Test
    public void shouldore_ont_3087() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_3087.owl"));
    }

    @Test
    public void shouldore_ont_1673() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_1673.owl"));
    }

    @Test
    public void shouldore_ont_4911() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_4911.owl"));
    }

    @Test
    public void shouldore_ont_15226() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_15226.owl"));
    }

    @Test
    public void shouldore_ont_10006() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_10006.owl"));
    }

    @Test
    public void shouldore_ont_13928() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_13928.owl"));
    }

    @Test
    public void shouldore_ont_13647() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_13647.owl"));
    }

    @Test
    public void shouldore_ont_10838() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_10838.owl"));
    }

    @Test
    public void shouldore_ont_11378() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_11378.owl"));
    }

    @Test
    public void shouldore_ont_16315() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_16315.owl"));
    }

    protected static OWLReasonerFactory factory() {
        return new JFactFactory();
    }

    @Rule public Timeout timeout = new Timeout(180000);

    private boolean consistent(String filename) {
        try {
            File file = new File(filename);
            OWLOntology o = m.loadOntologyFromOntologyDocument(file);
            OWLReasoner r = factory().createReasoner(o);
            return r.isConsistent();
        } catch (Exception e) {
            System.out.println("ReplicateOREFailures.should() " + filename);
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }
}
