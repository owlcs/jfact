package bugs;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
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
    protected OWLDataFactory df = OWLManager.getOWLDataFactory();

    @Nonnull
    protected OWLOntology load(String in) throws OWLOntologyCreationException {
        OWLOntology onto = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        VerifyComplianceBase.class.getResourceAsStream(in));
        OWLProfileReport checkOntology = Profiles.OWL2_DL.checkOntology(onto);
        if (!checkOntology.isInProfile()) {
            checkOntology
                    .getViolations()
                    .forEach(
                            v -> System.out
                                    .println("VerifyComplianceBase.load() " + v));
        }
        return onto;
    }

    @Nonnull
    protected OWLOntology loadFromString(@Nonnull String in)
            throws OWLOntologyCreationException {
        return OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(new StringDocumentSource(in));
    }

    protected static String set(Stream<OWLEntity> i) {
        return i.sorted().map(e -> e.getIRI().getShortForm())
                .collect(joining("\n"));
    }

    protected static void equal(NodeSet<? extends OWLObject> node,
            OWLEntity... objects) {
        assertEquals(set(Stream.of(objects)),
                set((Stream<OWLEntity>) node.entities()));
    }

    protected static void equal(Node<? extends OWLObject> node,
            OWLEntity... objects) {
        assertEquals(set(Stream.of(objects)),
                set((Stream<OWLEntity>) node.entities()));
    }

    protected static void equal(Object o, boolean object) {
        assertEquals(object, o);
    }

    @Nonnull
    protected OWLClass C(@Nonnull String i) {
        return df.getOWLClass(IRI.create(i));
    }

    @Nonnull
    protected OWLNamedIndividual I(@Nonnull String i) {
        return df.getOWLNamedIndividual(IRI.create(i));
    }

    @Nonnull
    protected OWLObjectProperty OP(@Nonnull String i) {
        return df.getOWLObjectProperty(IRI.create(i));
    }

    @Nonnull
    protected OWLDataProperty DP(@Nonnull String i) {
        return df.getOWLDataProperty(IRI.create(i));
    }

    @Nonnull
    protected OWLDataProperty bottomDataProperty = df
            .getOWLBottomDataProperty();
    @Nonnull
    protected OWLDataProperty topDataProperty = df.getOWLTopDataProperty();
    @Nonnull
    protected OWLObjectProperty topObjectProperty = df
            .getOWLTopObjectProperty();
    @Nonnull
    protected OWLObjectProperty bottomObjectProperty = df
            .getOWLBottomObjectProperty();
    @Nonnull
    protected OWLClass owlThing = df.getOWLThing();
    @Nonnull
    protected OWLClass owlNothing = df.getOWLNothing();

    @Before
    public void setUp() throws OWLOntologyCreationException {
        reasoner = (JFactReasoner) factory().createReasoner(load(input()),
                new JFactReasonerConfiguration());
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
    }

    protected void switchLoggingOn() {
        // reasoner.getConfiguration().setLoggingActive(true);
    }

    protected void print() {
        OWLOntology o = reasoner.getRootOntology();
        try {
            o.getOWLOntologyManager().saveOntology(o,
                    new FunctionalSyntaxDocumentFormat(),
                    new SystemOutDocumentTarget());
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }
}
