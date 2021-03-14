package conformancetests;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLRuntimeException;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class WebOnt_miscellaneous_wineTestCase extends TestBase {

    @Rule
    public Timeout timeout = new Timeout(15_000);

    protected OWLOntology load() {
        try (InputStream in =
            WebOnt_miscellaneous_wineTestCase.class.getResourceAsStream("/wine.fss")) {
            return m.loadOntologyFromOntologyDocument(in);
        } catch (OWLOntologyCreationException | IOException e) {
            throw new OWLRuntimeException(e);
        }
    }

    @ChangedTestCase
    @Test
    public void testWebOnt_miscellaneous_001() {
        JUnitRunner r = new JUnitRunner(m, load(), conclusion, "WebOnt_miscellaneous_001", TestClasses.CONSISTENCY, "Wine example taken from the guide. Note that this is the same as the ontology http://www.w3.org/2002/03owlt/miscellaneous/consistent002 imported in other tests.");
        r.setReasonerFactory(factory());
        r.run();
    }
}
