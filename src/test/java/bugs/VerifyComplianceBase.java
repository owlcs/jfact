package bugs;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

import uk.ac.manchester.cs.jfact.JFactFactory;
import uk.ac.manchester.cs.jfact.JFactReasoner;

@SuppressWarnings("javadoc")
public abstract class VerifyComplianceBase {

    protected abstract String input();

    protected JFactReasoner reasoner;
    protected OWLDataFactory df = OWLManager.getOWLDataFactory();

    protected OWLOntology load(String in) throws OWLOntologyCreationException {
        return OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        VerifyComplianceBase.class.getResourceAsStream(in));
    }

    protected OWLOntology loadFromString(String in)
            throws OWLOntologyCreationException {
        return OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(new StringDocumentSource(in));
    }

    private Set<String> set(Iterable<OWLEntity> i) {
        Set<String> s = new HashSet<String>();
        for (OWLEntity e : i) {
            s.add(e.getIRI().getFragment());
        }
        return s;
    }

    @SuppressWarnings({ "unchecked" })
    protected void equal(NodeSet<?> node, OWLEntity... objects) {
        assertEquals(set(Arrays.asList(objects)),
                set((Set<OWLEntity>) node.getFlattened()));
    }

    @SuppressWarnings("unchecked")
    protected void equal(Node<?> node, OWLEntity... objects) {
        assertEquals(set(Arrays.asList(objects)),
                set((Set<OWLEntity>) node.getEntities()));
    }

    protected void equal(Object o, boolean object) {
        assertEquals(object, o);
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

    protected OWLDataProperty bottomDataProperty = df
            .getOWLBottomDataProperty();
    protected OWLDataProperty topDataProperty = df.getOWLTopDataProperty();
    protected OWLObjectProperty topObjectProperty = df
            .getOWLTopObjectProperty();
    protected OWLObjectProperty bottomObjectProperty = df
            .getOWLBottomObjectProperty();
    protected OWLClass owlThing = df.getOWLThing();
    protected OWLClass owlNothing = df.getOWLNothing();

    @Before
    public void setUp() throws OWLOntologyCreationException {
        reasoner = (JFactReasoner) new JFactFactory()
                .createReasoner(load(input()));
    }

    protected void switchLoggingOn() {
        // reasoner.getConfiguration().setLoggingActive(true);
    }

    protected void print() {
        OWLOntology o = reasoner.getRootOntology();
        try {
            o.getOWLOntologyManager().saveOntology(o,
                    new OWLFunctionalSyntaxOntologyFormat(),
                    new SystemOutDocumentTarget());
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }
}
