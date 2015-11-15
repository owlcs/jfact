package bugs.debug;

import static org.junit.Assert.*;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWLFacet;

import com.google.common.collect.Sets;

import uk.ac.manchester.cs.jfact.JFactFactory;

public class EquivalentTestCase {

    @Test
    public void shouldFildEquivalentToThingNotEmpty() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = m.getOWLDataFactory();
        OWLOntology o = m.createOntology();
        OWLClass t = df.getOWLClass("urn:test:t");
        o.add(df.getOWLSubClassOfAxiom(t, df.getOWLThing()));
        OWLReasoner r = new JFactFactory().createReasoner(o);
        Node<OWLClass> equivalentClasses = r.getEquivalentClasses(df.getOWLThing());
        assertEquals(Sets.newHashSet(df.getOWLThing()), asSet(equivalentClasses.entities()));
        NodeSet<OWLClass> subClasses = r.getSubClasses(t, true);
        assertEquals(1L, subClasses.nodes().count());
        assertEquals(Sets.newHashSet(df.getOWLNothing()), asSet(subClasses.entities()));
    }

    @Test
    public void shouldGetRightSubsumption() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = m.getOWLDataFactory();
        OWLOntology ont = m.createOntology();
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("http://example.com/p"));
        OWLDatatype integer = df.getIntegerOWLDatatype();
        ont.add(df.getOWLDeclarationAxiom(p));
        OWLReasoner r = new JFactFactory().createNonBufferingReasoner(ont);
        OWLDataSomeValuesFrom restr2 = df.getOWLDataSomeValuesFrom(p, df.getOWLDatatypeRestriction(integer, df
            .getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, 2)));
        OWLDataSomeValuesFrom restr4 = df.getOWLDataSomeValuesFrom(p, df.getOWLDatatypeRestriction(integer, df
            .getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, 4)));
        assertTrue(r.isEntailed(df.getOWLSubClassOfAxiom(restr4, restr2)));
        assertTrue(r.isSatisfiable(restr2));
    }
}
