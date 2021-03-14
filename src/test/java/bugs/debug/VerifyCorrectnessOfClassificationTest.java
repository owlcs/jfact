package bugs.debug;

import static org.junit.Assert.*;

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

@SuppressWarnings("javadoc")
@Ignore
public class VerifyCorrectnessOfClassificationTest extends TestBase {

    private static final String POOL_SAMPLE = "/Users/ignazio/workspace/pool_sample/files/";

    @Test
    public void shouldore_ont_699() {
        assertFalse(consistent(POOL_SAMPLE + "ore_ont_699.owl"));
    }

    @Test
    public void shouldore_ont_960() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_960.owl"));
    }

    @Test
    public void shouldore_ont_5448() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_5448.owl"));
    }

    @Test
    public void shouldore_ont_5092() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_5092.owl"));
    }

    @Test
    public void shouldore_ont_5502() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_5502.owl"));
    }

    @Test
    public void shouldore_ont_2817() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_2817.owl"));
    }

    @Test
    public void shouldore_ont_9422() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_9422.owl"));
    }

    @Test
    public void shouldore_ont_5964() {
        assertFalse(consistent(POOL_SAMPLE + "ore_ont_5964.owl"));
    }

    @Test
    public void shouldore_ont_1445() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_1445.owl"));
    }

    @Test
    public void shouldore_ont_6266() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_6266.owl"));
    }

    @Test
    public void shouldore_ont_3329() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_3329.owl"));
    }

    @Test
    public void shouldore_ont_7584() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_7584.owl"));
    }

    @Test
    public void shouldore_ont_5292() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_5292.owl"));
    }

    @Test
    public void shouldore_ont_3114() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_3114.owl"));
    }

    @Test
    public void shouldore_ont_6469() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_6469.owl"));
    }

    @Test
    public void shouldore_ont_11296() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_11296.owl"));
    }

    @Test
    public void shouldore_ont_13674() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_13674.owl"));
    }

    @Test
    public void shouldore_ont_14134() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_14134.owl"));
    }

    @Test
    public void shouldore_ont_14551() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_14551.owl"));
    }

    @Test
    public void shouldore_ont_14684() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_14684.owl"));
    }

    @Test
    public void shouldore_ont_15544() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_15544.owl"));
    }

    @Test
    public void shouldore_ont_16120() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_16120.owl"));
    }

    @Test
    public void shouldore_ont_9534() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_9534.owl"));
    }

    @Test
    public void shouldore_ont_9429() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_9429.owl"));
    }

    @Test
    public void shouldore_ont_3077() {
        assertFalse(consistent(POOL_SAMPLE + "ore_ont_3077.owl"));
    }

    @Test
    public void shouldore_ont_13581() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_13581.owl"));
    }

    @Test
    public void shouldore_ont_12039() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_12039.owl"));
    }

    @Test
    public void shouldore_ont_13337() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_13337.owl"));
    }

    @Test
    public void shouldore_ont_10173() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_10173.owl"));
    }

    @Test
    public void shouldore_ont_10409() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_10409.owl"));
    }

    @Test
    public void shouldore_ont_13299() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_13299.owl"));
    }

    @Test
    public void shouldore_ont_10985() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_10985.owl"));
    }

    @Test
    public void shouldore_ont_12955() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_12955.owl"));
    }

    protected static OWLReasonerFactory factory() {
        return new JFactFactory();
    }

    @Test
    public void shouldore_ont_3917() {
        assertFalse(consistent(POOL_SAMPLE + "ore_ont_3917.owl"));
    }

    @Test
    public void shouldore_ont_16666() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_16666.owl"));
    }

    @Test
    public void shouldore_ont_2792() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_2792.owl"));
    }

    @Test
    public void shouldore_ont_15682() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_15682.owl"));
    }

    @Test
    public void shouldore_ont_13991() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_13991.owl"));
    }

    @Test
    public void shouldore_ont_6635() {
        assertTrue(consistent(POOL_SAMPLE + "ore_ont_6635.owl"));
    }

    @Rule public Timeout timeout = new Timeout(20000);

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
