package bugs.debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import com.google.common.collect.Sets;

import testbase.TestBase;
import uk.ac.manchester.cs.jfact.JFactFactory;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

class EquivalentTestCase extends TestBase {

    @Test
    void shouldFildEquivalentToThingNotEmpty() throws OWLOntologyCreationException {
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
    void shouldGetRightSubsumption() throws OWLOntologyCreationException {
        OWLOntology ont = m.createOntology();
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("http://example.com/p"));
        OWLDatatype integer = df.getIntegerOWLDatatype();
        ont.add(df.getOWLDeclarationAxiom(p));
        OWLReasoner r = new JFactFactory().createNonBufferingReasoner(ont);
        OWLDataSomeValuesFrom restr2 =
            df.getOWLDataSomeValuesFrom(p, df.getOWLDatatypeRestriction(integer,
                df.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, 2)));
        OWLDataSomeValuesFrom restr4 =
            df.getOWLDataSomeValuesFrom(p, df.getOWLDatatypeRestriction(integer,
                df.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, 4)));
        assertTrue(r.isEntailed(df.getOWLSubClassOfAxiom(restr4, restr2)));
        assertTrue(r.isSatisfiable(restr2));
    }

    @Test
    @Disabled("bug: this needs fixing")
    void shouldCorrectlyPlaceEnum() throws OWLOntologyCreationException {
        OWLOntology ont = m.createOntology();
        OWLClass ca = df.getOWLClass("urn:test:ca");
        OWLClass ca1 = df.getOWLClass("urn:test:ca1");
        OWLClass cb = df.getOWLClass("urn:test:cb");
        OWLClass cb1 = df.getOWLClass("urn:test:cb1");
        OWLClass cc = df.getOWLClass("urn:test:cc");
        OWLClass cc1 = df.getOWLClass("urn:test:cc1");
        OWLClass cd = df.getOWLClass("urn:test:cd");
        OWLClass ce = df.getOWLClass("urn:test:ce");
        OWLObjectProperty oa = df.getOWLObjectProperty("urn:test:oa");
        OWLObjectProperty ob = df.getOWLObjectProperty("urn:test:ob");
        OWLObjectProperty ob1 = df.getOWLObjectProperty("urn:test:ob1");
        OWLDataProperty da = df.getOWLDataProperty("urn:test:da");
        ont.add(df.getOWLSubClassOfAxiom(cb1, cb));
        ont.add(df.getOWLSubClassOfAxiom(ca1, ca));
        ont.add(df.getOWLSubObjectPropertyOfAxiom(ob1, ob));
        ont.add(df.getOWLSubClassOfAxiom(ca, df.getOWLObjectSomeValuesFrom(oa, cb)));
        ont.add(df.getOWLSubClassOfAxiom(ca,
            df.getOWLDataSomeValuesFrom(da, df.getIntegerOWLDatatype())));
        ont.add(df.getOWLEquivalentClassesAxiom(cd, df.getOWLObjectSomeValuesFrom(oa, cb)));
        ont.add(df.getOWLSubClassOfAxiom(cd, df.getOWLObjectSomeValuesFrom(oa, ce)));
        ont.add(df.getOWLSubClassOfAxiom(ca, df.getOWLObjectSomeValuesFrom(oa, cb1)));
        ont.add(df.getOWLSubClassOfAxiom(ca, df.getOWLObjectSomeValuesFrom(ob1, cb)));
        ont.add(df.getOWLSubClassOfAxiom(cc1, cc));
        OWLReasoner r = new JFactFactory().createNonBufferingReasoner(ont,
            new JFactReasonerConfiguration().setLoggingActive(true));
        OWLDataSomeValuesFrom someInt =
            df.getOWLDataSomeValuesFrom(da, OWL2Datatype.XSD_INTEGER.getDatatype(df));
        OWLDataSomeValuesFrom intTo2 = df.getOWLDataSomeValuesFrom(da,
            df.getOWLDatatypeRestriction(OWL2Datatype.XSD_INTEGER.getDatatype(df),
                df.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE, 2)));
        // assertTrue(r.isEntailed(df.getOWLSubClassOfAxiom(intTo2, someInt)));
        assertFalse(r.isEntailed(df.getOWLSubClassOfAxiom(someInt, intTo2)));
    }
}
