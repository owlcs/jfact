package bugs;

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import uk.ac.manchester.cs.jfact.JFactFactory;

@SuppressWarnings("javadoc")
public class AddIndividualsAfterLoadingTestCase {

    @Test
    public void shouldLoadAndNotFailQuery() throws Exception {
        // given
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology();
        OWLOntologyManager m = o.getOWLOntologyManager();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLClass c1 = f.getOWLClass(IRI.create("urn:test#c1"));
        OWLClass c2 = f.getOWLClass(IRI.create("urn:test#c2"));
        OWLObjectProperty p = f.getOWLObjectProperty(IRI.create("urn:test#p"));
        m.addAxiom(o, f.getOWLDisjointClassesAxiom(c1, c2));
        m.addAxiom(o, f.getOWLObjectPropertyDomainAxiom(p, c1));
        m.addAxiom(o, f.getOWLObjectPropertyRangeAxiom(p, c2));
        // OWLReasoner r = new JFactFactory().createReasoner(o);
        // r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        OWLIndividual i = f.getOWLNamedIndividual(IRI.create("urn:test#i"));
        OWLIndividual j = f.getOWLNamedIndividual(IRI.create("urn:test#j"));
        m.addAxiom(o, f.getOWLObjectPropertyAssertionAxiom(p, i, j));
        // r.flush();
        // r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        OWLReasoner r = new JFactFactory().createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        OWLIndividual k = f.getOWLNamedIndividual(IRI.create("urn:test#k"));
        OWLIndividual l = f.getOWLNamedIndividual(IRI.create("urn:test#l"));
        m.addAxiom(o, f.getOWLObjectPropertyAssertionAxiom(p, k, l));
        OWLDataProperty dt = f.getOWLDataProperty(IRI.create("urn:test#dt"));
        m.addAxiom(o, f.getOWLDeclarationAxiom(dt));
        m.addAxiom(
                o,
                f.getOWLDataPropertyRangeAxiom(dt,
                        f.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI())));
        m.addAxiom(
                o,
                f.getOWLDataPropertyAssertionAxiom(dt, l,
                        f.getOWLLiteral("test")));
        r.flush();
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        assertTrue(r.isConsistent());
    }

    @Test
    public void inconsistent() throws OWLOntologyCreationException,
            OWLOntologyStorageException {
        String in = "Prefix(:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Prefix(ontology:=<http://owl.cs.man.ac.uk/ontology#>)\n"
                + "Ontology(\n"
                + "Declaration(Class(ontology:Course))\n"
                + "Declaration(Class(ontology:Person))\n"
                + "Declaration(Class(ontology:Student))\n"
                + "Declaration(Class(ontology:University))\n"
                + "Declaration(ObjectProperty(ontology:attend))\n"
                + "Declaration(ObjectProperty(ontology:isEnrolledAt))\n"
                + "Declaration(NamedIndividual(<http://owl.cs.man.ac.uk/ontology>))\n"
                + "Declaration(NamedIndividual(ontology:Amal))\n"
                + "Declaration(NamedIndividual(ontology:Leila))\n"
                + "Declaration(NamedIndividual(ontology:ManchesterUniversity))\n"
                + "Declaration(NamedIndividual(ontology:Mary))\n"
                + "Declaration(NamedIndividual(ontology:Peter))\n"
                + "EquivalentClasses(ontology:Student ObjectIntersectionOf(ontology:Person ObjectSomeValuesFrom(ontology:attend ontology:Course) ObjectExactCardinality(1 ontology:isEnrolledAt ontology:University)))\n"
                + "SubClassOf(ontology:Student ontology:Person)\n"
                + "ClassAssertion(ontology:Person ontology:Amal)\n"
                + "ClassAssertion(ObjectComplementOf(ontology:Student) ontology:Amal)\n"
                + "ClassAssertion(ObjectSomeValuesFrom(ontology:attend ontology:Course) ontology:Amal)\n"
                + "ClassAssertion(ObjectMaxCardinality(1 ontology:isEnrolledAt ontology:University) ontology:Amal)\n"
                + "ObjectPropertyAssertion(ontology:isEnrolledAt ontology:Amal ontology:ManchesterUniversity)\n"
                + "ClassAssertion(ontology:Student ontology:Leila)\n"
                + "ClassAssertion(ontology:University ontology:ManchesterUniversity)\n"
                + "ClassAssertion(ontology:Student ontology:Mary)\n"
                + "ClassAssertion(ontology:Student ontology:Peter))";
        OWLOntology o = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new StringDocumentSource(in,
                                OWLOntologyDocumentSourceBase
                                        .getNextDocumentIRI("test"),
                                new OWLFunctionalSyntaxOntologyFormat(), null));
        o.getOWLOntologyManager().saveOntology(o,
                new OWLFunctionalSyntaxOntologyFormat(),
                new SystemOutDocumentTarget());
        OWLReasoner r = new JFactFactory().createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        assertFalse(r.isConsistent());
    }
}
