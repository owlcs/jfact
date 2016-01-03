package bugs;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.*;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.Profiles;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

import testbase.TestBase;
import uk.ac.manchester.cs.jfact.JFactReasoner;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

@SuppressWarnings("javadoc")
public abstract class VerifyComplianceBase extends TestBase {

    protected abstract String input();

    protected JFactReasoner reasoner;

    protected OWLOntology load(String in) throws OWLOntologyCreationException {
        OWLOntology onto = m.loadOntologyFromOntologyDocument(VerifyComplianceBase.class.getResourceAsStream(in));
        OWLProfileReport checkOntology = Profiles.OWL2_DL.checkOntology(onto);
        if (!checkOntology.isInProfile()) {
            checkOntology.getViolations().forEach(v -> System.out.println("VerifyComplianceBase.load() " + v));
        }
        return onto;
    }

    protected OWLOntology loadFromString(String in) throws OWLOntologyCreationException {
        return m.loadOntologyFromOntologyDocument(new StringDocumentSource(in));
    }

    protected static String set(Stream<? extends OWLObject> i) {
        return i.sorted().map(e -> ((HasIRI) e).getIRI().getShortForm()).collect(joining("\n"));
    }

    protected static void equal(NodeSet<? extends OWLObject> node, OWLEntity... objects) {
        assertEquals(set(Stream.of(objects)), set(node.entities()));
    }

    protected static void equal(Node<? extends OWLObject> node, OWLEntity... objects) {
        assertEquals(set(Stream.of(objects)), set(node.entities()));
    }

    protected static void equal(boolean o, boolean object) {
        assertTrue(object == o);
    }

    protected OWLClass C(String i) {
        return df.getOWLClass(IRI.create(i));
    }

    protected OWLNamedIndividual I(String i) {
        return df.getOWLNamedIndividual(IRI.create(i));
    }

    protected OWLObjectProperty OP(String i) {
        return df.getOWLObjectProperty(IRI.create(i));
    }

    protected OWLDataProperty DP(String i) {
        return df.getOWLDataProperty(IRI.create(i));
    }

    @Nonnull protected OWLDataProperty bottomDataProperty = df.getOWLBottomDataProperty();
    @Nonnull protected OWLDataProperty topDataProperty = df.getOWLTopDataProperty();
    @Nonnull protected OWLObjectProperty topObjectProperty = df.getOWLTopObjectProperty();
    @Nonnull protected OWLObjectProperty bottomObjectProperty = df.getOWLBottomObjectProperty();
    @Nonnull protected OWLClass owlThing = df.getOWLThing();
    @Nonnull protected OWLClass owlNothing = df.getOWLNothing();
    protected JFactReasonerConfiguration config = new JFactReasonerConfiguration();

    protected void enableLogging() {
        config = config.setAbsorptionLoggingActive(true).setLoggingActive(true);
    }

    @Before
    public void setUp() throws OWLOntologyCreationException {
        reasoner = (JFactReasoner) factory().createReasoner(load(input()), config);
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
    }

    protected void switchLoggingOn() {
        // reasoner.getConfiguration().setLoggingActive(true);
    }

    protected void print() {
        OWLOntology o = reasoner.getRootOntology();
        try {
            o.getOWLOntologyManager().saveOntology(o, new FunctionalSyntaxDocumentFormat(),
                new SystemOutDocumentTarget());
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }
}
