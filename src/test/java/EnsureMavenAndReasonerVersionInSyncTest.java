import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.Version;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import testbase.TestBase;

public class EnsureMavenAndReasonerVersionInSyncTest extends TestBase{
    @Test
    public void shouldBeInSync() throws Exception {
        
        OWLReasoner r = factory().createReasoner(m.createOntology());
        Document d=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("pom.xml"));
        String mavenVersion=((Element)((Element)d.getElementsByTagName("project").item(0)).getElementsByTagName("version").item(1)).getTextContent();
        Version rVersion = r.getReasonerVersion();
        String reasonerVersion= rVersion.getMajor()+"."+rVersion.getMinor()+"."+rVersion.getPatch();
        assertEquals(mavenVersion, reasonerVersion);        
    }
}
