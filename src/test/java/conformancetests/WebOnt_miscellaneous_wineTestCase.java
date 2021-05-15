package conformancetests;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLRuntimeException;

import testbase.TestBase;

@Timeout(value = 15, unit = TimeUnit.SECONDS)
@Disabled("performance changes randomly, investigate interactin with other tests")
class WebOnt_miscellaneous_wineTestCase extends TestBase {

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
    void testWebOnt_miscellaneous_001() {
        JUnitRunner r = new JUnitRunner(m, load(), conclusion, "WebOnt_miscellaneous_001",
            TestClasses.CONSISTENCY,
            "Wine example taken from the guide. Note that this is the same as the ontology http://www.w3.org/2002/03owlt/miscellaneous/consistent002 imported in other tests.");
        r.setReasonerFactory(factory());
        r.run();
    }
}
