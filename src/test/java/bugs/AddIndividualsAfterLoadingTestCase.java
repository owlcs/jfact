package bugs;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class AddIndividualsAfterLoadingTestCase extends TestBase {

    @Test
    public void shouldLoadAndNotFailQuery() throws Exception {
        // given
        OWLOntology o = m.createOntology();
        OWLClass c1 = df.getOWLClass(IRI.create("urn:test#c1"));
        OWLClass c2 = df.getOWLClass(IRI.create("urn:test#c2"));
        OWLObjectProperty p = df.getOWLObjectProperty(IRI.create("urn:test#p"));
        m.addAxiom(o, df.getOWLDisjointClassesAxiom(c1, c2));
        m.addAxiom(o, df.getOWLObjectPropertyDomainAxiom(p, c1));
        m.addAxiom(o, df.getOWLObjectPropertyRangeAxiom(p, c2));
        // OWLReasoner r = new JFactFactory().createReasoner(o);
        // r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        OWLIndividual i = df.getOWLNamedIndividual(IRI.create("urn:test#i"));
        OWLIndividual j = df.getOWLNamedIndividual(IRI.create("urn:test#j"));
        m.addAxiom(o, df.getOWLObjectPropertyAssertionAxiom(p, i, j));
        // r.flush();
        // r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        OWLReasoner r = factory().createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        OWLIndividual k = df.getOWLNamedIndividual(IRI.create("urn:test#k"));
        OWLIndividual l = df.getOWLNamedIndividual(IRI.create("urn:test#l"));
        m.addAxiom(o, df.getOWLObjectPropertyAssertionAxiom(p, k, l));
        OWLDataProperty dt = df.getOWLDataProperty(IRI.create("urn:test#dt"));
        m.addAxiom(o, df.getOWLDeclarationAxiom(dt));
        m.addAxiom(o, df.getOWLDataPropertyRangeAxiom(dt, df.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI())));
        m.addAxiom(o, df.getOWLDataPropertyAssertionAxiom(dt, l, df.getOWLLiteral("test")));
        r.flush();
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        assertTrue(r.isConsistent());
    }

    @Test
    public void shouldLoadAndNotFailQueryDataHasValue() throws Exception {
        OWLOntology o = m.createOntology();
        OWLDataProperty p = df.getOWLDataProperty("urn:test#p");
        OWLDatatype floatType = df.getFloatOWLDatatype();
        OWLNamedIndividual i = df.getOWLNamedIndividual("urn:test#i");
        o.add(df.getOWLDataPropertyRangeAxiom(p, floatType));
        o.add(df.getOWLDataPropertyAssertionAxiom(p, i, 19.0F));
        OWLFacetRestriction fLess20 = df.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE, 20f);
        OWLReasoner r = factory().createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        OWLDatatypeRestriction lessThan20 = df.getOWLDatatypeRestriction(floatType, fLess20);
        NodeSet<OWLNamedIndividual> instances = r.getInstances(df.getOWLDataAllValuesFrom(p, lessThan20), false);
        assertTrue(instances.containsEntity(i));
    }

    @Test
    public void debug() throws OWLOntologyCreationException {
        String input = "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" + "\n" + "\n" + "Ontology(<urn:test>\n" + "\n"
            + "Declaration(Class(<urn:test#Producto>))\n" + "Declaration(Class(<urn:test#lessThan20>))\n"
            + "Declaration(DataProperty(<urn:test#hasEnergia>))\n" + "Declaration(NamedIndividual(<urn:test#prod1>))\n"
            + "Declaration(NamedIndividual(<urn:test#prod2>))\n" + "Declaration(NamedIndividual(<urn:test#prod3>))\n"
            + "Declaration(NamedIndividual(<urn:test#prod4>))\n" + "Declaration(NamedIndividual(<urn:test#prod5>))\n"
            + "EquivalentClasses(<urn:test#lessThan20> ObjectIntersectionOf(DataSomeValuesFrom(<urn:test#hasEnergia> DatatypeRestriction(xsd:float xsd:maxExclusive \"20.0\"^^xsd:float)) <urn:test#Producto>))\n"
            + "SubClassOf(<urn:test#lessThan20> <urn:test#Producto>)\n"
            + "FunctionalDataProperty(<urn:test#hasEnergia>)\n"
            + "DataPropertyDomain(<urn:test#hasEnergia> <urn:test#Producto>)\n"
            + "DataPropertyRange(<urn:test#hasEnergia> xsd:float)\n"
            + "DataPropertyAssertion(<urn:test#hasEnergia> <urn:test#prod1> \"30.0\"^^xsd:float)\n"
            + "DataPropertyAssertion(<urn:test#hasEnergia> <urn:test#prod2> \"35.0\"^^xsd:float)\n"
            + "DataPropertyAssertion(<urn:test#hasEnergia> <urn:test#prod3> \"58.0\"^^xsd:float)\n"
            + "DataPropertyAssertion(<urn:test#hasEnergia> <urn:test#prod4> \"14.0\"^^xsd:float)\n"
            + "DataPropertyAssertion(<urn:test#hasEnergia> <urn:test#prod5> \"5.0\"^^xsd:float))";
        OWLOntology o = m.loadOntologyFromOntologyDocument(new StringDocumentSource(input));
        OWLReasoner r = factory().createReasoner(o);
        r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("urn:test#hasEnergia"));
        OWLNamedIndividual i = df.getOWLNamedIndividual(IRI.create("urn:test#prod4"));
        OWLDatatype fdt = df.getFloatOWLDatatype();
        OWLFacetRestriction fLess20 = df.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE, 20f);
        OWLDataSomeValuesFrom dsv = df.getOWLDataSomeValuesFrom(p, df.getOWLDatatypeRestriction(fdt, fLess20));
        NodeSet<OWLNamedIndividual> instances = r.getInstances(dsv, false);
        assertTrue(instances.containsEntity(i));
    }
}
