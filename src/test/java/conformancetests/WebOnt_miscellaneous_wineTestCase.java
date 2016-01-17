package conformancetests;

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

    @Rule public Timeout timeout = new Timeout(15_000);

    protected OWLOntology load() {
        try {
            return m.loadOntologyFromOntologyDocument(readWine());
        } catch (OWLOntologyCreationException e) {
            throw new OWLRuntimeException(e);
        }
    }

    protected InputStream readWine() {
        return WebOnt_miscellaneous_wineTestCase.class.getResourceAsStream("/wine.fss");
    }

    @ChangedTestCase
    @Test
    public void testWebOnt_miscellaneous_001() {
        String conclusion = "";
        String id = "WebOnt_miscellaneous_001";
        OWLOntology o = load();
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Wine example taken from the guide. Note that this is the same as the ontology http://www.w3.org/2002/03owlt/miscellaneous/consistent002 imported in other tests.";
        JUnitRunner r = new JUnitRunner(m, o, conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }
}
