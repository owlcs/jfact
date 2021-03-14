package bugs;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLRuntimeException;
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
        try (InputStream input = VerifyComplianceBase.class.getResourceAsStream(in)) {
            OWLOntology onto = m.loadOntologyFromOntologyDocument(input);
            OWLProfileReport checkOntology = Profiles.OWL2_DL.checkOntology(onto);
            if (!checkOntology.isInProfile()) {
                checkOntology.getViolations().forEach(System.out::println);
            }
            return onto;
        } catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
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

    @Nonnull
    protected OWLDataProperty bottomDataProperty = df.getOWLBottomDataProperty();
    @Nonnull
    protected OWLDataProperty topDataProperty = df.getOWLTopDataProperty();
    @Nonnull
    protected OWLObjectProperty topObjectProperty = df.getOWLTopObjectProperty();
    @Nonnull
    protected OWLObjectProperty bottomObjectProperty = df.getOWLBottomObjectProperty();
    @Nonnull
    protected OWLClass owlThing = df.getOWLThing();
    @Nonnull
    protected OWLClass owlNothing = df.getOWLNothing();
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
