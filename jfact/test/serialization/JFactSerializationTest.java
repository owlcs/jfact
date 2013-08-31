package serialization;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import uk.ac.manchester.cs.jfact.JFactFactory;

@SuppressWarnings("javadoc")
@Ignore
public class JFactSerializationTest extends TestCase {
    private static final OWLDataFactory f = OWLManager.getOWLDataFactory();
    OWL2Datatype owl2datatype = OWL2Datatype.XSD_INT;
    OWLDataPropertyExpression dp = f.getOWLDataProperty(IRI.create("urn:dp"));
    OWLObjectPropertyExpression op = f.getOWLObjectProperty(IRI.create("urn:op"));
    IRI iri = IRI.create("urn:iri");
    OWLLiteral owlliteral = f.getOWLLiteral(true);
    OWLAnnotationSubject as = IRI.create("urn:i");
    OWLDatatype owldatatype = f.getOWLDatatype(owl2datatype.getIRI());
    OWLDataRange dr = f.getOWLDatatypeRestriction(owldatatype);
    OWLAnnotationProperty ap = f.getOWLAnnotationProperty(IRI.create("urn:ap"));
    OWLFacet owlfacet = OWLFacet.MIN_EXCLUSIVE;
    OWLAnnotation owlannotation = f.getOWLAnnotation(ap, owlliteral);
    String string = "testString";
    OWLClassExpression c = f.getOWLClass(IRI.create("urn:classexpression"));
    PrefixManager prefixmanager = new DefaultPrefixManager();
    OWLIndividual ai = f.getOWLAnonymousIndividual();
    OWLAnnotationValue owlannotationvalue = owlliteral;
    Set<OWLObjectPropertyExpression> setop = new HashSet<OWLObjectPropertyExpression>();
    Set<OWLAnnotation> setowlannotation = new HashSet<OWLAnnotation>();
    Set<OWLDataPropertyExpression> setdp = new HashSet<OWLDataPropertyExpression>();
    List<OWLObjectPropertyExpression> listowlobjectpropertyexpression = new ArrayList<OWLObjectPropertyExpression>();
    Set<OWLIndividual> setowlindividual = new HashSet<OWLIndividual>();
    Set<OWLPropertyExpression> setowlpropertyexpression = new HashSet<OWLPropertyExpression>();
    OWLFacetRestriction[] lowlfacetrestriction = new OWLFacetRestriction[] { f
            .getOWLFacetRestriction(owlfacet, 1) };
    OWLFacetRestriction[] nulllowlfacetrestriction = new OWLFacetRestriction[] { f
            .getOWLFacetRestriction(owlfacet, 1) };
    Set<OWLClassExpression> setowlclassexpression = new HashSet<OWLClassExpression>();
    Set<OWLFacetRestriction> setowlfacetrestriction = new HashSet<OWLFacetRestriction>();
    OWLPropertyExpression[] owlpropertyexpression = new OWLPropertyExpression[] {};

    @Test
    public void testrun() throws Exception {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        m.addIRIMapper(new AutoIRIMapper(new File("."), false));
        OWLOntology o = m.loadOntologyFromOntologyDocument(new File(
                "onto/celllineontology1541.owl"));
        OWLReasoner r = new JFactFactory().createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        r = roundtrip(r);
        assertNotNull(r);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
    }

    private OWLReasoner roundtrip(OWLReasoner r) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(out);
            stream.writeObject(r);
            stream.flush();
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(in);
            return (OWLReasoner) inStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
