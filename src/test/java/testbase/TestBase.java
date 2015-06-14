package testbase;

import static org.junit.Assert.*;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFJsonLDDocumentFormat;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import uk.ac.manchester.cs.jfact.JFactFactory;

@SuppressWarnings("javadoc")
public class TestBase {

    protected static OWLReasonerFactory factory() {
        return new JFactFactory();
    }

    protected static final Logger logger = LoggerFactory.getLogger(TestBase.class);
    @Nonnull
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Nonnull
    @Rule
    public Timeout timeout = new Timeout(1000000);
    @Nonnull
    protected OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
    @Nonnull
    protected final OWLDataFactory df = OWLManager.getOWLDataFactory();
    @Nonnull
    protected OWLOntologyManager m = OWLManager.createOWLOntologyManager();
    @Nonnull
    protected final OWLOntologyManager m1 = OWLManager.createOWLOntologyManager();

    @Nonnull
    protected <T> Optional<T> of(T t) {
        return Optional.fromNullable(t);
    }

    @Nonnull
    protected Optional<IRI> absent() {
        return Optional.absent();
    }

    @Nonnull
    protected <S> Set<S> singleton(S s) {
        return Collections.singleton(s);
    }

    protected Set<OWLAxiom> stripSimpleDeclarations(Collection<OWLAxiom> axioms) {
        Set<OWLAxiom> toReturn = new HashSet<>();
        for (OWLAxiom ax : axioms) {
            if (!isSimpleDeclaration(ax)) {
                toReturn.add(ax);
            }
        }
        return toReturn;
    }

    protected boolean isSimpleDeclaration(OWLAxiom ax) {
        return ax.isOfType(AxiomType.DECLARATION) && ax.getAnnotations().isEmpty();
    }

    /**
     * equivalent entity axioms with more than two entities are broken up by RDF
     * syntaxes. Ensure they are still recognized as correct roundtripping
     */
    public void applyEquivalentsRoundtrip(Set<OWLAxiom> axioms1, Set<OWLAxiom> axioms2, OWLDocumentFormat destination) {
        if (!axioms1.equals(axioms2)) {
            // remove axioms that differ only because of n-ary equivalence
            // axioms
            // http://www.w3.org/TR/owl2-mapping-to-rdf/#Axioms_that_are_Translated_to_Multiple_Triples
            for (OWLAxiom ax : new ArrayList<>(axioms1)) {
                if (ax instanceof OWLEquivalentClassesAxiom) {
                    OWLEquivalentClassesAxiom ax2 = (OWLEquivalentClassesAxiom) ax;
                    if (ax2.getClassExpressions().size() > 2) {
                        Set<OWLEquivalentClassesAxiom> pairs = ax2.splitToAnnotatedPairs();
                        if (removeIfContainsAll(axioms2, pairs, destination)) {
                            axioms1.remove(ax);
                            axioms2.removeAll(pairs);
                        }
                    }
                } else if (ax instanceof OWLEquivalentDataPropertiesAxiom) {
                    OWLEquivalentDataPropertiesAxiom ax2 = (OWLEquivalentDataPropertiesAxiom) ax;
                    if (ax2.getProperties().size() > 2) {
                        Set<OWLEquivalentDataPropertiesAxiom> pairs = ax2.splitToAnnotatedPairs();
                        if (removeIfContainsAll(axioms2, pairs, destination)) {
                            axioms1.remove(ax);
                            axioms2.removeAll(pairs);
                        }
                    }
                } else if (ax instanceof OWLEquivalentObjectPropertiesAxiom) {
                    OWLEquivalentObjectPropertiesAxiom ax2 = (OWLEquivalentObjectPropertiesAxiom) ax;
                    if (ax2.getProperties().size() > 2) {
                        Set<OWLEquivalentObjectPropertiesAxiom> pairs = ax2.splitToAnnotatedPairs();
                        if (removeIfContainsAll(axioms2, pairs, destination)) {
                            axioms1.remove(ax);
                            axioms2.removeAll(pairs);
                        }
                    }
                }
            }
        }
    }

    private boolean removeIfContainsAll(Collection<OWLAxiom> axioms, Collection<? extends OWLAxiom> others,
        OWLDocumentFormat destination) {
        if (axioms.containsAll(others)) {
            axioms.removeAll(others);
            return true;
        }
        // some syntaxes attach xsd:string to annotation values that did not
        // have it previously
        if (!(destination instanceof RDFJsonLDDocumentFormat)) {
            return false;
        }
        Set<OWLAxiom> toRemove = new HashSet<>();
        for (OWLAxiom ax : others) {
            OWLAxiom reannotated = ax.getAxiomWithoutAnnotations().getAnnotatedAxiom(reannotate(ax.getAnnotations()));
            toRemove.add(reannotated);
        }
        axioms.removeAll(toRemove);
        return true;
    }

    private Set<OWLAnnotation> reannotate(Set<OWLAnnotation> anns) {
        OWLDatatype stringType = df.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI());
        Set<OWLAnnotation> toReturn = new HashSet<>();
        for (OWLAnnotation a : anns) {
            Optional<OWLLiteral> asLiteral = a.getValue().asLiteral();
            if (asLiteral.isPresent() && asLiteral.get().isRDFPlainLiteral()) {
                OWLAnnotation replacement = df.getOWLAnnotation(a.getProperty(), df.getOWLLiteral(asLiteral.get()
                    .getLiteral(), stringType));
                toReturn.add(replacement);
            } else {
                toReturn.add(a);
            }
        }
        return toReturn;
    }

    /**
     * @param leftOnly
     * @param rightOnly
     * @return
     */
    public static boolean verifyErrorIsDueToBlankNodesId(@Nonnull Set<OWLAxiom> leftOnly,
        @Nonnull Set<OWLAxiom> rightOnly) {
        Set<String> leftOnlyStrings = new HashSet<>();
        Set<String> rightOnlyStrings = new HashSet<>();
        for (OWLAxiom ax : leftOnly) {
            leftOnlyStrings.add(ax.toString().replaceAll("_:anon-ind-[0-9]+", "blank").replaceAll("_:genid[0-9]+",
                "blank"));
        }
        for (OWLAxiom ax : rightOnly) {
            rightOnlyStrings.add(ax.toString().replaceAll("_:anon-ind-[0-9]+", "blank").replaceAll("_:genid[0-9]+",
                "blank"));
        }
        return rightOnlyStrings.equals(leftOnlyStrings);
    }

    /**
     * ignore declarations of builtins and of named individuals - named
     * individuals do not /need/ a declaration, but addiong one is not an error.
     * 
     * @param parse
     *        true if the axiom belongs to the parsed ones, false for the input
     * @return true if the axiom can be ignored
     */
    public boolean isIgnorableAxiom(OWLAxiom ax, boolean parse) {
        if (ax instanceof OWLDeclarationAxiom) {
            OWLDeclarationAxiom d = (OWLDeclarationAxiom) ax;
            if (parse) {
                // all extra declarations in the parsed ontology are fine
                return true;
            }
            // declarations of builtin and named individuals can be ignored
            return d.getEntity().isBuiltIn() || d.getEntity().isOWLNamedIndividual();
        }
        return false;
    }

    @Nonnull
    private final String uriBase = "http://www.semanticweb.org/owlapi/test";

    @Nonnull
    public OWLOntology getOWLOntology(String name) {
        try {
            IRI iri = IRI(uriBase + '/' + name);
            if (m.contains(iri)) {
                return m.getOntology(iri);
            } else {
                return m.createOntology(iri);
            }
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    public OWLOntology loadOntology(String fileName) {
        try {
            URL url = getClass().getResource('/' + fileName);
            return m.loadOntologyFromOntologyDocument(new IRIDocumentSource(IRI.create(url), null, null),
                new OWLOntologyLoaderConfiguration().setReportStackTraces(true));
        } catch (OWLOntologyCreationException e) {
            fail(e.getMessage());
            throw new OWLRuntimeException(e);
        }
    }

    @Nonnull
    public IRI iri(String name) {
        return IRI(uriBase + '#' + name);
    }

    public void addAxiom(@Nonnull OWLOntology ont, @Nonnull OWLAxiom ax) {
        m.addAxiom(ont, ax);
    }

    // @Test
    public void checkVerify() {
        OWLDataProperty t = df.getOWLDataProperty(IRI.create("urn:test#t"));
        Set<OWLAxiom> ax1 = new HashSet<>();
        ax1.add(df.getOWLDataPropertyAssertionAxiom(t, df.getOWLAnonymousIndividual(), df.getOWLLiteral("test1")));
        ax1.add(df.getOWLDataPropertyAssertionAxiom(t, df.getOWLAnonymousIndividual(), df.getOWLLiteral("test2")));
        Set<OWLAxiom> ax2 = new HashSet<>();
        ax2.add(df.getOWLDataPropertyAssertionAxiom(t, df.getOWLAnonymousIndividual(), df.getOWLLiteral("test1")));
        ax2.add(df.getOWLDataPropertyAssertionAxiom(t, df.getOWLAnonymousIndividual(), df.getOWLLiteral("test2")));
        assertFalse(ax1.equals(ax2));
        assertTrue(verifyErrorIsDueToBlankNodesId(ax1, ax2));
    }

    @SuppressWarnings("unused")
    protected boolean isIgnoreDeclarationAxioms(OWLDocumentFormat format) {
        return true;
    }

    @SuppressWarnings("unused")
    protected void handleSaved(StringDocumentTarget target, OWLDocumentFormat format) {
        // System.out.println(target.toString());
    }

    @Nonnull
    protected OWLOntology loadOntologyFromString(@Nonnull String input) throws OWLOntologyCreationException {
        return OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new StringDocumentSource(input));
    }

    @Nonnull
    protected OWLOntology loadOntologyFromString(@Nonnull String input, @Nonnull IRI i, @Nonnull OWLDocumentFormat f) {
        StringDocumentSource documentSource = new StringDocumentSource(input, i, f, null);
        try {
            return OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(documentSource);
        } catch (OWLOntologyCreationException e) {
            throw new OWLRuntimeException(e);
        }
    }

    @Nonnull
    protected OWLOntology loadOntologyFromString(@Nonnull StringDocumentSource input)
        throws OWLOntologyCreationException {
        return OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(input);
    }

    @Nonnull
    protected OWLOntology loadOntologyFromString(@Nonnull StringDocumentTarget input)
        throws OWLOntologyCreationException {
        return OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new StringDocumentSource(input));
    }

    @Nonnull
    protected OWLOntology loadOntologyFromString(@Nonnull StringDocumentTarget input, OWLDocumentFormat f)
        throws OWLOntologyCreationException {
        return OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new StringDocumentSource(input
            .toString(), OWLOntologyDocumentSourceBase.getNextDocumentIRI("string:ontology"), f, null));
    }

    @Nonnull
    protected OWLOntology loadOntologyStrict(@Nonnull StringDocumentTarget o) throws OWLOntologyCreationException {
        return loadOntologyWithConfig(o, new OWLOntologyLoaderConfiguration().setStrict(true));
    }

    @Nonnull
    protected OWLOntology loadOntologyWithConfig(@Nonnull StringDocumentTarget o,
        @Nonnull OWLOntologyLoaderConfiguration c) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        return manager.loadOntologyFromOntologyDocument(new StringDocumentSource(o), c);
    }

    @Nonnull
    protected StringDocumentTarget saveOntology(@Nonnull OWLOntology o) throws OWLOntologyStorageException {
        return saveOntology(o, o.getOWLOntologyManager().getOntologyFormat(o));
    }

    @Nonnull
    protected StringDocumentTarget saveOntology(@Nonnull OWLOntology o, @Nonnull OWLDocumentFormat format)
        throws OWLOntologyStorageException {
        StringDocumentTarget t = new StringDocumentTarget();
        o.getOWLOntologyManager().saveOntology(o, format, t);
        return t;
    }

    @Nonnull
    protected OWLOntology roundTrip(@Nonnull OWLOntology o, @Nonnull OWLDocumentFormat format)
        throws OWLOntologyCreationException, OWLOntologyStorageException {
        return loadOntologyFromString(saveOntology(o, format), format);
    }

    @Nonnull
    protected OWLOntology roundTrip(@Nonnull OWLOntology o, @Nonnull OWLDocumentFormat format,
        @Nonnull OWLOntologyLoaderConfiguration c) throws OWLOntologyCreationException, OWLOntologyStorageException {
        return loadOntologyWithConfig(saveOntology(o, format), c);
    }

    @Nonnull
    protected OWLOntology roundTrip(@Nonnull OWLOntology o) throws OWLOntologyCreationException,
        OWLOntologyStorageException {
        return loadOntologyFromString(saveOntology(o));
    }
}
