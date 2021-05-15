package bugs.debug;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import testbase.TestBase;
import uk.ac.manchester.cs.jfact.JFactFactory;

@Disabled
@Timeout(value = 180, unit = TimeUnit.SECONDS)
class ReplicateOREFailuresTest extends TestBase {

    private static final String POOL_SAMPLE = "/Users/ignazio/workspace/pool_sample/files/";

    @Test
    void shouldore_ont_1270() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_1270.owl"));
    }

    @Test
    void shouldore_ont_9151() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_9151.owl"));
    }

    @Test
    void shouldore_ont_9654() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_9654.owl"));
    }

    @Test
    void shouldore_ont_7729() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_7729.owl"));
    }

    @Test
    void shouldore_ont_7192() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_7192.owl"));
    }

    @Test
    void shouldore_ont_9899() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_9899.owl"));
    }

    @Test
    void shouldore_ont_3282() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_3282.owl"));
    }

    @Test
    void shouldore_ont_7646() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_7646.owl"));
    }

    @Test
    void shouldore_ont_1199() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_1199.owl"));
    }

    @Test
    void shouldore_ont_7557() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_7557.owl"));
    }

    @Test
    void shouldore_ont_1938() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_1938.owl"));
    }

    @Test
    void shouldore_ont_6132() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_6132.owl"));
    }

    @Test
    void shouldore_ont_3087() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_3087.owl"));
    }

    @Test
    void shouldore_ont_1673() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_1673.owl"));
    }

    @Test
    void shouldore_ont_4911() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_4911.owl"));
    }

    @Test
    void shouldore_ont_15226() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_15226.owl"));
    }

    @Test
    void shouldore_ont_10006() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_10006.owl"));
    }

    @Test
    void shouldore_ont_13928() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_13928.owl"));
    }

    @Test
    void shouldore_ont_13647() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_13647.owl"));
    }

    @Test
    void shouldore_ont_10838() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_10838.owl"));
    }

    @Test
    void shouldore_ont_11378() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_11378.owl"));
    }

    @Test
    void shouldore_ont_16315() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_16315.owl"));
    }

    protected static OWLReasonerFactory factory() {
        return new JFactFactory();
    }


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
