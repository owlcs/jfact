package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class WebOntTestCase extends TestBase {

    @Test
    public void testWebOnt_AnnotationProperty_003() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/AnnotationProperty/consistent003#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/AnnotationProperty/consistent003\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:AnnotationProperty rdf:ID=\"ap\"/>\n"
                + "  <owl:Class rdf:ID=\"A\"><first:ap><rdf:Description rdf:ID=\"B\"/></first:ap></owl:Class>\n"
                + "</rdf:RDF>";
        test("WebOnt_AnnotationProperty_003", TestClasses.CONSISTENCY,
            "URI references used in annotations don't need to be typed.");
    }

    @Test
    public void testWebOnt_AnnotationProperty_004() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\n"
                + "\"http://www.w3.org/2002/03owlt/AnnotationProperty/consistent004\" >\n"
                + "   <owl:Ontology />\n" + "   <owl:AnnotationProperty rdf:ID=\"ap\">\n"
                + "     <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#string\"/></owl:AnnotationProperty></rdf:RDF>";
        test("WebOnt_AnnotationProperty_004", TestClasses.CONSISTENCY,
            "AnnotationProperty's in OWL Lite and OWL DL, may not have range constraints.  They are permitted in OWL 2 DL.");
    }

    @Test
    public void testWebOnt_I4_5_001() {
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I4.5/premises001#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I4.5/conclusions001\" >\n"
                + "  <owl:Ontology/>\n" + "  <first:EuroMP rdf:about=\"premises001#Kinnock\" />\n"
                + "  <owl:Class rdf:about=\"premises001#EuroMP\"/>\n" + "</rdf:RDF>";
        premise = asString("/WebOnt_I4_5_001.owl");
        test("WebOnt_I4_5_001", TestClasses.POSITIVE_IMPL,
            "An example combinging owl:oneOf and owl:inverseOf.");
    }

    @Test
    public void testWebOnt_I4_5_002() {
        premise = asString("/WebOnt_I4_5_002.owl");
        test("WebOnt_I4_5_002", TestClasses.INCONSISTENCY,
            "An example combining owl:oneOf and owl:inverseOf.");
    }

    @Test
    public void testWebOnt_I4_6_005_Direct() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I4.6/premises005\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:about=\"nonconclusions005#C1\"><rdfs:comment>An example class.</rdfs:comment><owl:equivalentClass><owl:Class rdf:about=\"nonconclusions005#C2\"/></owl:equivalentClass></owl:Class></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I4.6/nonconclusions005\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"C2\"><rdfs:comment>An example class.</rdfs:comment></owl:Class></rdf:RDF>";
        test("WebOnt_I4_6_005_Direct", TestClasses.POSITIVE_IMPL,
            "Under the direct semantics, test WebOnt-I4.6-005 must be treated as a positive entailment test because the direct semantics ignore annotations in the conclusion ontology.");
    }

    @Test
    public void testWebOnt_I5_24_003() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.24/premises003\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:ObjectProperty rdf:ID=\"prop\">\n"
                + "     <rdfs:range>\n"
                + "        <owl:Class rdf:about=\"#A\"/></rdfs:range></owl:ObjectProperty>\n"
                + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.24/conclusions003\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises003#prop\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:about=\"premises003#A\"/></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class></rdf:RDF>";
        test("WebOnt_I5_24_003", TestClasses.POSITIVE_IMPL,
            "This is a typical definition of range from description logic.");
    }

    @Test
    public void testWebOnt_I5_24_004() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.24/premises004\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"conclusions004#prop\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:about=\"conclusions004#A\"/></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.24/conclusions004\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:ObjectProperty rdf:ID=\"prop\">\n"
                + "     <rdfs:range>\n"
                + "        <owl:Class rdf:about=\"#A\"/></rdfs:range></owl:ObjectProperty>\n"
                + "</rdf:RDF>";
        test("WebOnt_I5_24_004", TestClasses.POSITIVE_IMPL,
            "This is a typical definition of range from description logic. It works both ways.");
    }

    @Test
    public void testWebOnt_I5_26_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.26/consistent001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"A\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf></owl:Class>\n"
                + "    <rdf:Description><rdf:type rdf:nodeID=\"B\"/></rdf:Description>\n"
                + "    <owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"C\"/><rdf:Description rdf:nodeID=\"B\"/></owl:intersectionOf></owl:Class></rdf:RDF>";
        test("WebOnt_I5_26_001", TestClasses.CONSISTENCY,
            "Structure sharing was not permitted in OWL DL, between a class description and a type triple, but is permitted in OWL 2 DL.");
    }

    @Test
    public void testWebOnt_I5_26_002() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.26/consistent002\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"J\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf><owl:equivalentClass><owl:Class rdf:ID=\"A\"/><owl:Class rdf:ID=\"K\"/></owl:equivalentClass></owl:Class>\n"
                + "    <rdf:Description><rdf:type rdf:nodeID=\"B\"/></rdf:Description></rdf:RDF>";
        test("WebOnt_I5_26_002", TestClasses.CONSISTENCY,
            "Structure sharing was not permitted in OWL DL, between an owl:equivalentClass triple\n"
                + "and a type triple, but is permitted in OWL 2 DL.");
    }

    @Test
    public void testWebOnt_I5_26_003() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.26/consistent003\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"B\"/><owl:Class rdf:ID=\"K\"/></owl:intersectionOf></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"notB\"><owl:complementOf rdf:nodeID=\"B\"/></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"u\"><owl:unionOf rdf:parseType=\"Collection\"><rdf:Description rdf:nodeID=\"B\"/><owl:Class rdf:ID=\"A\"/></owl:unionOf></owl:Class></rdf:RDF>";
        test("WebOnt_I5_26_003", TestClasses.CONSISTENCY,
            "Structure sharing was not permitted in OWL DL, between two class descriptions, but is permitted in OWL 2 DL.");
    }

    @Test
    public void testWebOnt_I5_26_004() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.26/consistent004\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"A\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf><owl:disjointWith><owl:Class rdf:ID=\"C\"/></owl:disjointWith></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"notB\"><owl:complementOf rdf:nodeID=\"B\"/></owl:Class></rdf:RDF>";
        test("WebOnt_I5_26_004", TestClasses.CONSISTENCY,
            "Structure sharing was not permitted in OWL DL, between a class description and an owl:disjointWith triple, but is permitted in OWL 2 DL.");
    }

    @Test
    @ChangedTestCase
    public void testWebOnt_I5_26_005() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xml:base=\"http://www.w3.org/2002/03owlt/I5.26/consistent005\" ><owl:Ontology/>\n"
                + "   <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"A\"/><owl:Class rdf:ID=\"B\"/>"
                + "</owl:intersectionOf><owl:disjointWith><owl:Class rdf:ID=\"C\"/></owl:disjointWith><owl:equivalentClass><owl:Class rdf:ID=\"D\"/></owl:equivalentClass></owl:Class></rdf:RDF>";
        test("WebOnt_I5_26_005", TestClasses.CONSISTENCY,
            "Structure sharing was not permitted in OWL DL, between an owl:equivalentClass triple and an owl:disjointWith triple, but is permitted in OWL 2 DL.");
    }

    @Test
    @ChangedTestCase(
        reason = "test doesn't make sense; This code does the test in a meaningful way")
    public void testWebOnt_I5_26_009() throws OWLOntologyCreationException {
        OWLOntology o = m.createOntology();
        OWLObjectProperty p = df.getOWLObjectProperty(IRI.create("urn:test:test#p"));
        m.addAxiom(o, df.getOWLDeclarationAxiom(p));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(r.isConsistent());
        OWLObjectMinCardinality c = df.getOWLObjectMinCardinality(1, p);
        assertTrue(r.isEntailed(df.getOWLSubClassOfAxiom(c, c)));
    }

    @Test
    @Ignore("Conclusion does not contain axioms")
    public void testWebOnt_I5_26_010() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.26/premises010#\" xml:base=\"http://www.w3.org/2002/03owlt/I5.26/premises010\" ><owl:Ontology/><owl:ObjectProperty rdf:ID=\"p\" /></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xml:base=\"http://www.w3.org/2002/03owlt/I5.26/conclusions010\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:Restriction rdf:nodeID=\"n\">\n"
                + "     <owl:onProperty><owl:ObjectProperty rdf:about=\"premises010#p\" /></owl:onProperty>\n"
                + "     <owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></rdf:RDF>";
        test("WebOnt_I5_26_010", TestClasses.POSITIVE_IMPL,
            "The abstract syntax form of the conclusions is: EquivalentClasses( restriction( first:p, minCardinality(1) ) ) ObjectProperty( first:p ) This is trivially true given that first:p is an individualvaluedPropertyID.");
    }

    @Test
    public void testWebOnt_I5_2_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.2/consistent001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/consistent001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"Nothing\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:maxCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></owl:Class></rdf:RDF>";
        test("WebOnt_I5_2_001", TestClasses.CONSISTENCY,
            "A class like owl:Nothing can be defined using OWL Lite restrictions.");
    }

    @Test
    public void testWebOnt_I5_2_002() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/premises002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"Nothing\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:maxCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</owl:maxCardinality></owl:Restriction></rdfs:subClassOf></owl:Class></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/conclusions002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:about=\"premises002#Nothing\"><owl:equivalentClass><owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Nothing\" /></owl:equivalentClass></owl:Class></rdf:RDF>";
        test("WebOnt_I5_2_002", TestClasses.POSITIVE_IMPL,
            "A class like owl:Nothing can be defined using OWL Lite restrictions.");
    }

    @Test
    public void testWebOnt_I5_2_003() {
        premise = asString("/WebOnt_I5_2_003.owl");
        test("WebOnt_I5_2_003", TestClasses.CONSISTENCY,
            "The complement of a class can be defined using OWL Lite restrictions.");
    }

    @Test
    public void testWebOnt_I5_2_004() {
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/conclusions004\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:Class rdf:about=\"premises004#notA\">\n"
                + "     <owl:complementOf><owl:Class rdf:about=\"premises004#A\"/></owl:complementOf></owl:Class>\n"
                + "</rdf:RDF>";
        premise = asString("/WebOnt_I5_2_004.owl");
        test("WebOnt_I5_2_004", TestClasses.POSITIVE_IMPL,
            "The complement of a class can be defined using OWL Lite restrictions.");
    }

    @Test
    public void testWebOnt_I5_2_005() {
        premise = asString("/WebOnt_I5_2_005.owl");
        test("WebOnt_I5_2_005", TestClasses.CONSISTENCY,
            "The union of two classes can be defined using OWL Lite restrictions, and owl:intersectionOf.");
    }

    @Test
    public void testWebOnt_I5_2_006() {
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.2/conclusions006\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:Class rdf:about=\"premises006#AorB\">\n"
                + "    <owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"premises006#A\"/><owl:Class rdf:about=\"premises006#B\"/></owl:unionOf></owl:Class>\n"
                + "</rdf:RDF>";
        premise = asString("/WebOnt_I5_2_006.owl");
        test("WebOnt_I5_2_006", TestClasses.POSITIVE_IMPL,
            "The union of two classes can be defined using OWL Lite restrictions, and owl:intersectionOf.");
    }

    @Test
    public void testWebOnt_I5_3_006() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.3/consistent006#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.3/consistent006\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Thing><first:p><owl:Thing/></first:p>\n"
                + "   </owl:Thing>\n" + "   <owl:ObjectProperty rdf:ID=\"p\" />\n" + "</rdf:RDF>";
        test("WebOnt_I5_3_006", TestClasses.CONSISTENCY,
            "A minimal OWL Lite version of <a xmlns=\"http://www.w3.org/1999/xhtml\" href=\"#I5.3-005\">test 005</a>.");
    }

    @Test
    public void testWebOnt_I5_3_008() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.3/consistent008#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.3/consistent008\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Thing><first:dp>value</first:dp></owl:Thing>\n"
                + "    <owl:DatatypeProperty rdf:ID=\"dp\" />\n" + "</rdf:RDF>";
        test("WebOnt_I5_3_008", TestClasses.CONSISTENCY,
            "An OWL Lite version of <a xmlns=\"http://www.w3.org/1999/xhtml\" href=\"#I5.3-007\">test 007</a>.");
    }

    @Test
    public void testWebOnt_I5_3_010() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.3/consistent010#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.3/consistent010\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:ObjectProperty rdf:ID=\"p\"/>   \n"
                + "   <owl:Thing><first:p><owl:Class rdf:ID=\"c\"/></first:p></owl:Thing></rdf:RDF>";
        test("WebOnt_I5_3_010", TestClasses.CONSISTENCY,
            "Classes could not be the object of regular properties in OWL DL.  This ontology is permissible in OWL 2 DL due to class / individual punning.");
    }

    @Test
    public void testWebOnt_I5_3_011() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.3/consistent011#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.3/consistent011\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:AnnotationProperty rdf:ID=\"p\"/>   \n"
                + "   <owl:Thing><first:p><owl:Class rdf:ID=\"c\"/></first:p></owl:Thing></rdf:RDF>";
        test("WebOnt_I5_3_011", TestClasses.CONSISTENCY,
            "Classes can be the object of annotation properties in OWL Lite and DL.");
    }

    @Test
    public void testWebOnt_I5_5_005() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.5/premises005\" >\n"
                + "    <owl:Class rdf:ID=\"a\" />\n" + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.5/conclusions005\" >\n"
                + "   <owl:Class>\n"
                + "     <owl:unionOf><rdf:List><rdf:first><owl:Class rdf:about=\"premises005#a\"/></rdf:first><rdf:rest rdf:resource = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#nil\"/></rdf:List></owl:unionOf></owl:Class>\n"
                + "</rdf:RDF>";
        test("WebOnt_I5_5_005", TestClasses.POSITIVE_IMPL,
            "This test exhibits the effect of the comprehension principles in OWL Full.  The conclusion ontology only contains a class declaration, ObjectUnionOf class expression does not appear in an axiom.");
    }

    @Test
    public void testWebOnt_I5_8_007() throws OWLOntologyCreationException {
        // premise = "<rdf:RDF\n"
        // + " xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
        // + " xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
        // + " xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
        // +
        // " xml:base=\"http://www.w3.org/2002/03owlt/I5.8/premises007\" >\n"
        // + " <owl:Ontology/>\n"
        // + " <owl:DatatypeProperty rdf:ID=\"p\">\n"
        // + " <rdfs:range rdf:resource=\n"
        // + " \"http://www.w3.org/2001/XMLSchema#short\" />\n"
        // + " </owl:DatatypeProperty>\n" + "</rdf:RDF>";
        // conclusion = "<rdf:RDF\n"
        // + " xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
        // + " xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
        // + " xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
        // +
        // " xml:base=\"http://www.w3.org/2002/03owlt/I5.8/nonconclusions007\"
        // >\n"
        // + " <owl:Ontology/>\n"
        // + " <owl:DatatypeProperty rdf:about=\"premises007#p\">\n"
        // + " <rdfs:range rdf:resource=\n"
        // + " \"http://www.w3.org/2001/XMLSchema#unsignedByte\" />\n"
        // + " </owl:DatatypeProperty>\n" + "</rdf:RDF>";
        // String id = "WebOnt_I5_8_007";
        // TestClasses tc = TestClasses.NEGATIVE_IMPL;
        // String d = "-1 is an xsd:short\n" + "that is not an\n"
        // + " xsd:unsignedByte.";
        // JUnitRunner r = new JUnitRunner(m,premise, conclusion, id, tc, d);
        // r.setReasonerFactory(factory());
        // r.run();
        //
        OWLOntology o = m.createOntology();
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("urn:p"));
        m.addAxiom(o,
            df.getOWLDataPropertyRangeAxiom(p, df.getOWLDatatype(OWL2Datatype.XSD_SHORT.getIRI())));
        OWLReasoner r = factory().createReasoner(o);
        assertFalse("unsigned byte should not be inferred",
            r.isEntailed(df.getOWLDataPropertyRangeAxiom(p,
                df.getOWLDatatype(OWL2Datatype.XSD_UNSIGNED_BYTE.getIRI()))));
    }

    @Test
    public void testWebOnt_I5_8_011() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n"
                + "  <owl:Ontology/>\n" + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions011\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <rdfs:Datatype rdf:about=\"http://www.w3.org/2001/XMLSchema#integer\"/>\n"
                + "  <rdfs:Datatype rdf:about=\"http://www.w3.org/2001/XMLSchema#string\"/></rdf:RDF>";
        test("WebOnt_I5_8_011", TestClasses.POSITIVE_IMPL,
            "The empty graph entails that xsd:integer and xsd:string are a rdfs:Datatype");
    }

    @Test
    public void testWebOnt_InverseFunctionalProperty_001() {
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl =\"http://www.w3.org/2002/07/owl#\"\n"
                + "  xml:base=\"http://www.w3.org/2002/03owlt/InverseFunctionalProperty/conclusions001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <rdf:Description rdf:about=\"premises001#subject1\"><owl:sameAs rdf:resource=\"premises001#subject2\" /></rdf:Description></rdf:RDF>";
        premise = asString("/WebOnt_InverseFunctionalProperty_001.owl");
        test("WebOnt_InverseFunctionalProperty_001", TestClasses.POSITIVE_IMPL,
            "If prop belongs to owl:InverseFunctionalProperty, and object denotes a resource which is the object of two prop triples, then the subjects of these triples have the same denotation.");
    }

    @Test
    public void testWebOnt_Nothing_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Nothing/inconsistent001\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:Nothing/>\n" + "</rdf:RDF>";
        test("WebOnt_Nothing_001", TestClasses.INCONSISTENCY,
            "The triple asserts something of type owl:Nothing, however that is the empty class.");
    }

    @Test
    public void testWebOnt_Ontology_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/Ontology/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Ontology/premises001\" >\n"
                + "   <owl:Ontology rdf:about=\"\" />\n" + "   <owl:Class rdf:ID=\"Car\">\n"
                + "     <owl:equivalentClass><owl:Class rdf:ID=\"Automobile\"/></owl:equivalentClass></owl:Class>\n"
                + "  <first:Car rdf:ID=\"car\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Car>\n"
                + "  <first:Automobile rdf:ID=\"auto\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Automobile></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/Ontology/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Ontology/conclusions001\" >\n"
                + "  <owl:Ontology />\n" + "  <first:Car rdf:about=\"premises001#auto\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Car>\n"
                + "  <first:Automobile rdf:about=\"premises001#car\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Automobile>\n"
                + "   <owl:Class rdf:about=\"premises001#Car\"/>\n"
                + "   <owl:Class rdf:about=\"premises001#Automobile\"/>\n" + "</rdf:RDF>";
        test("WebOnt_Ontology_001", TestClasses.POSITIVE_IMPL,
            "This is a variation of <a xmlns=\"http://www.w3.org/1999/xhtml\" href=\"#equivalentClass-001\">equivalentClass-001</a>, showing the use of owl:Ontology triples in the premises and conclusions.");
    }

    @Test
    public void testWebOnt_Restriction_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/Restriction/inconsistent001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Restriction/inconsistent001\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:ObjectProperty rdf:ID=\"op\"/>\n"
                + "   <rdf:Description rdf:ID=\"a\">\n"
                + "     <rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"#op\"/><owl:someValuesFrom rdf:resource=\n"
                + "     \"http://www.w3.org/2002/07/owl#Nothing\" /></owl:Restriction></rdf:type></rdf:Description>\n"
                + "   <rdf:Description rdf:ID=\"b\">\n"
                + "     <rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"#op\"/><owl:someValuesFrom rdf:resource=\n"
                + "     \"http://www.w3.org/2002/07/owl#Nothing\" /></owl:Restriction></rdf:type></rdf:Description>\n"
                + "</rdf:RDF>";
        test("WebOnt_Restriction_001", TestClasses.INCONSISTENCY,
            "This test shows the syntax for using the same restriction twice in OWL Lite.");
    }

    @Test
    public void testWebOnt_Restriction_002() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Restriction/inconsistent002\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:ObjectProperty rdf:ID=\"op\"/>\n"
                + "   <rdf:Description rdf:ID=\"a\">\n"
                + "     <rdf:type><owl:Restriction rdf:nodeID=\"r\"><owl:onProperty rdf:resource=\"#op\"/><owl:someValuesFrom rdf:resource=\n"
                + "     \"http://www.w3.org/2002/07/owl#Nothing\" /></owl:Restriction></rdf:type></rdf:Description>\n"
                + "   <rdf:Description rdf:ID=\"b\">\n" + "     <rdf:type rdf:nodeID=\"r\"/>\n"
                + "   </rdf:Description></rdf:RDF>";
        test("WebOnt_Restriction_002", TestClasses.INCONSISTENCY,
            "This test shows syntax that was not permitted in OWL Lite or OWL DL for using the same restriction twice, but is permitted in OWL 2 DL.");
    }

    @Test
    public void testWebOnt_Restriction_003() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/Restriction/consistent003#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Restriction/consistent003\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:DatatypeProperty rdf:ID=\"dp\"/>\n"
                + "   <owl:Class rdf:ID=\"C\">\n"
                + "     <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"superC\"/><owl:Restriction rdf:nodeID=\"r\"><owl:onProperty rdf:resource=\"#dp\"/><owl:someValuesFrom rdf:resource=\n"
                + "     \"http://www.w3.org/2001/XMLSchema#byte\" /></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + "   <owl:Class rdf:ID=\"D\">\n"
                + "     <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"superD\"/><rdf:Description rdf:nodeID=\"r\"/></owl:intersectionOf></owl:Class>\n"
                + "</rdf:RDF>";
        test("WebOnt_Restriction_003", TestClasses.CONSISTENCY,
            "This test shows syntax that was not permitted in OWL Lite or OWL DL for using the same restriction twice, but is permitted in OWL 2 DL.");
    }

    @Test
    public void testWebOnt_Restriction_004() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Restriction/consistent004\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:DatatypeProperty rdf:ID=\"dp\"/>\n"
                + "   <owl:Class rdf:ID=\"C\">\n"
                + "     <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"superC\"/><owl:Restriction><owl:onProperty rdf:resource=\"#dp\"/><owl:someValuesFrom rdf:resource=\n"
                + "     \"http://www.w3.org/2001/XMLSchema#byte\" /></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + "   <owl:Class rdf:ID=\"D\">\n"
                + "     <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"superD\"/><owl:Restriction><owl:onProperty rdf:resource=\"#dp\"/><owl:someValuesFrom rdf:resource=\n"
                + "     \"http://www.w3.org/2001/XMLSchema#byte\" /></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + "</rdf:RDF>";
        test("WebOnt_Restriction_004", TestClasses.CONSISTENCY,
            "This test shows OWL Lite syntax for using two equivalent restrictions.");
    }

    @Test
    public void testWebOnt_SymmetricProperty_002() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/SymmetricProperty/premises002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/SymmetricProperty/premises002\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:InverseFunctionalProperty rdf:about=\"#equalityOnA\"><rdfs:range><owl:Class rdf:ID=\"A\"><owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"a\"/><owl:Thing rdf:ID=\"b\"/></owl:oneOf></owl:Class></rdfs:range></owl:InverseFunctionalProperty>\n"
                + "    <owl:Thing rdf:about=\"#a\"><first:equalityOnA rdf:resource=\"#a\"/></owl:Thing>\n"
                + "    <owl:Thing rdf:about=\"#b\"><first:equalityOnA rdf:resource=\"#b\"/></owl:Thing>\n"
                + "    <owl:Thing rdf:ID=\"c\"/>\n" + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/SymmetricProperty/premises002#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/SymmetricProperty/conclusions002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:SymmetricProperty rdf:about=\"premises002#equalityOnA\">\n"
                + "     <rdfs:domain><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:about=\"premises002#a\"/><owl:Thing rdf:about=\"premises002#b\"/><owl:Thing rdf:about=\"premises002#c\"/></owl:oneOf></owl:Class></rdfs:domain></owl:SymmetricProperty>\n"
                + "   <owl:Thing rdf:about=\"premises002#a\">\n"
                + "     <first:equalityOnA rdf:resource=\"premises002#a\"/></owl:Thing></rdf:RDF>";
        test("WebOnt_SymmetricProperty_002", TestClasses.POSITIVE_IMPL,
            "Test illustrating extensional semantics of owl:SymmetricProperty.");
    }

    @Test
    public void testWebOnt_SymmetricProperty_003() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/SymmetricProperty/premises003#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/SymmetricProperty/premises003\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Thing rdf:about=\"premises003#Ghent\"><first:path><owl:Thing rdf:about=\"premises003#Antwerp\"/></first:path></owl:Thing>\n"
                + "    <owl:SymmetricProperty rdf:about=\"premises003#path\"/></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/SymmetricProperty/premises003#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/SymmetricProperty/conclusions003\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Thing rdf:about=\"premises003#Antwerp\"><first:path><owl:Thing rdf:about=\"premises003#Ghent\"/></first:path></owl:Thing>\n"
                + "    <owl:ObjectProperty rdf:about=\"premises003#path\"/></rdf:RDF>";
        test("WebOnt_SymmetricProperty_003", TestClasses.POSITIVE_IMPL,
            "A Lite version of test <a xmlns=\"http://www.w3.org/1999/xhtml\" href=\"#SymmetricProperty-001\">001</a>.");
    }

    @Test
    public void testWebOnt_Thing_003() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Thing/inconsistent003\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\">\n"
                + "    <owl:equivalentClass rdf:resource\n"
                + "       =\"http://www.w3.org/2002/07/owl#Nothing\"/></owl:Class></rdf:RDF>";
        test("WebOnt_Thing_003", TestClasses.INCONSISTENCY,
            "The extension of OWL Thing may not be empty.");
    }

    @Test
    public void testWebOnt_Thing_004() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/Thing/consistent004\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:Class rdf:about=\"http://www.w3.org/2002/07/owl#Thing\"><owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:about=\"#s\"/></owl:oneOf></owl:Class></rdf:RDF>";
        test("WebOnt_Thing_004", TestClasses.CONSISTENCY,
            "The extension of OWL Thing may be a singleton in OWL DL.");
    }

    @Test
    public void testWebOnt_TransitiveProperty_002() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/TransitiveProperty/premises002#\" xmlns:second=\"http://www.w3.org/2002/03owlt/TransitiveProperty/conclusions002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/TransitiveProperty/premises002\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:SymmetricProperty rdf:ID=\"symProp\">\n"
                + "     <rdfs:range><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"a\"/><owl:Thing rdf:ID=\"b\"/></owl:oneOf></owl:Class></rdfs:range></owl:SymmetricProperty>\n"
                + "   <owl:Thing rdf:about=\"#a\">\n"
                + "     <first:symProp rdf:resource=\"#a\"/>\n" + "   </owl:Thing>\n"
                + "   <owl:Thing rdf:about=\"#b\">\n"
                + "     <first:symProp rdf:resource=\"#b\"/>\n" + "   </owl:Thing></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/TransitiveProperty/premises002#\" xmlns:second=\"http://www.w3.org/2002/03owlt/TransitiveProperty/conclusions002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/TransitiveProperty/conclusions002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:TransitiveProperty rdf:about=\"premises002#symProp\"/>\n"
                + "   <rdf:Description rdf:about=\"premises002#a\">\n"
                + "     <rdf:type><owl:Restriction><rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Class\"/><owl:onProperty rdf:resource=\"premises002#symProp\"/><owl:someValuesFrom  rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/></owl:Restriction></rdf:type></rdf:Description>\n"
                + "</rdf:RDF>";
        test("WebOnt_TransitiveProperty_002", TestClasses.POSITIVE_IMPL,
            "Test illustrating extensional semantics of owl:TransitiveProperty.");
    }

    @Test
    public void testWebOnt_backwardCompatibleWith_002() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/backwardCompatibleWith/consistent002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/backwardCompatibleWith/consistent002\" >\n"
                + "   <rdf:Description><owl:backwardCompatibleWith><owl:Ontology rdf:about=\"http://www.example.org/\"/></owl:backwardCompatibleWith></rdf:Description></rdf:RDF>";
        test("WebOnt_backwardCompatibleWith_002", TestClasses.CONSISTENCY,
            "In OWL Lite and DL the subject and object of a triple with predicate owl:backwardCompatibleWith must both be explicitly typed as owl:Ontology.  In OWL 2, this RDF graph parses to a single ontology with URI http://www.example.org/ and an annotation assertion between a blank node and that URI.");
    }

    @Test
    public void testWebOnt_cardinality_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/premises001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/conclusions001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:about=\"premises001#c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"premises001#p\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"premises001#p\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"premises001#p\"/></rdf:RDF>";
        test("WebOnt_cardinality_001", TestClasses.POSITIVE_IMPL,
            "An owl:cardinality constraint is simply shorthand for a pair of owl:minCardinality and owl:maxCardinality constraints.");
    }

    @Test
    public void testWebOnt_cardinality_002() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/premises002\" >    \n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:about=\"conclusions002#c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"conclusions002#p\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"conclusions002#p\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "     <owl:ObjectProperty rdf:about=\"conclusions002#p\"/></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/conclusions002\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "</rdf:RDF>";
        test("WebOnt_cardinality_002", TestClasses.POSITIVE_IMPL,
            "An owl:cardinality constraint is simply shorthand for a pair of owl:minCardinality and owl:maxCardinality constraints.");
    }

    @Test
    public void testWebOnt_cardinality_003() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/premises003\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:cardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/conclusions003\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:about=\"premises003#c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"premises003#p\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:maxCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"premises003#p\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:about=\"premises003#p\"/></rdf:RDF>";
        test("WebOnt_cardinality_003", TestClasses.POSITIVE_IMPL,
            "An owl:cardinality constraint is simply shorthand for a pair of owl:minCardinality and owl:maxCardinality constraints.");
    }

    @Test
    public void testWebOnt_cardinality_004() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/premises004\" >    \n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:about=\"conclusions004#c\"><rdfs:subClassOf><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty rdf:resource=\"conclusions004#p\"/><owl:maxCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:maxCardinality></owl:Restriction><owl:Restriction><owl:onProperty rdf:resource=\"conclusions004#p\"/><owl:minCardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction></owl:intersectionOf></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "     <owl:ObjectProperty rdf:about=\"conclusions004#p\"/></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/cardinality/conclusions004\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"c\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:cardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "     <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "</rdf:RDF>";
        test("WebOnt_cardinality_004", TestClasses.POSITIVE_IMPL,
            "An owl:cardinality constraint is simply shorthand for a pair of owl:minCardinality and owl:maxCardinality constraints.");
    }

    @Test
    public void testWebOnt_description_logic_001() {
        premise = asString("/WebOnt_description_logic_001.owl");
        test("WebOnt_description_logic_001", TestClasses.INCONSISTENCY,
            "DL Test: fact1.1 If a, b and c are disjoint, then: (a and b) or (b and c) or (c and a) is unsatisfiable.");
    }

    @Test
    public void testWebOnt_description_logic_002() {
        premise = asString("/WebOnt_description_logic_002.owl");
        test("WebOnt_description_logic_002", TestClasses.INCONSISTENCY, "DL Test: fact2.1");
    }

    @Test
    public void testWebOnt_description_logic_003() {
        premise = asString("/WebOnt_description_logic_003.owl");
        test("WebOnt_description_logic_003", TestClasses.INCONSISTENCY, "DL Test: fact3.1");
    }

    @Test
    public void testWebOnt_description_logic_004() {
        premise = asString("/WebOnt_description_logic_004.owl");
        test("WebOnt_description_logic_004", TestClasses.INCONSISTENCY, "DL Test: fact4.1");
    }

    @Test
    public void testWebOnt_description_logic_005() {
        premise = asString("/WebOnt_description_logic_005.owl");
        test("WebOnt_description_logic_005", TestClasses.CONSISTENCY, "DL Test: fact4.2");
    }

    @Test
    public void testWebOnt_description_logic_006() {
        premise = asString("/WebOnt_description_logic_006.owl");
        test("WebOnt_description_logic_006", TestClasses.CONSISTENCY, "DL Test: t1.1");
    }

    @Test
    public void testWebOnt_description_logic_007() {
        premise = asString("/WebOnt_description_logic_007.owl");
        test("WebOnt_description_logic_007", TestClasses.INCONSISTENCY, "DL Test: t1.2");
    }

    @Test
    public void testWebOnt_description_logic_008() {
        premise = asString("/WebOnt_description_logic_008.owl");
        test("WebOnt_description_logic_008", TestClasses.INCONSISTENCY, "DL Test: t1.3");
    }

    @Test
    public void testWebOnt_description_logic_009() {
        premise = asString("/WebOnt_description_logic_009.owl");
        test("WebOnt_description_logic_009", TestClasses.CONSISTENCY, "DL Test: t10.1");
    }

    @Test
    public void testWebOnt_description_logic_010() {
        premise = asString("/WebOnt_description_logic_010.owl");
        test("WebOnt_description_logic_010", TestClasses.INCONSISTENCY, "DL Test: t10.2");
    }

    @Test
    public void testWebOnt_description_logic_011() {
        premise = asString("/WebOnt_description_logic_011.owl");
        test("WebOnt_description_logic_011", TestClasses.INCONSISTENCY, "DL Test: t10.3");
    }

    @Test
    public void testWebOnt_description_logic_012() {
        premise = asString("/WebOnt_description_logic_012.owl");
        test("WebOnt_description_logic_012", TestClasses.INCONSISTENCY, "DL Test: t10.4");
    }

    @Test
    public void testWebOnt_description_logic_013() {
        premise = asString("/WebOnt_description_logic_013.owl");
        test("WebOnt_description_logic_013", TestClasses.INCONSISTENCY, "DL Test: t10.5");
    }

    @Test
    public void testWebOnt_description_logic_014() {
        premise = asString("/WebOnt_description_logic_014.owl");
        test("WebOnt_description_logic_014", TestClasses.INCONSISTENCY, "DL Test: t11.1");
    }

    @Test
    public void testWebOnt_description_logic_015() {
        premise = asString("/WebOnt_description_logic_015.owl");
        test("WebOnt_description_logic_015", TestClasses.INCONSISTENCY, "DL Test: t12.1");
    }

    @Test
    public void testWebOnt_description_logic_016() {
        premise = asString("/WebOnt_description_logic_016.owl");
        test("WebOnt_description_logic_016", TestClasses.CONSISTENCY, "DL Test: t2.1");
    }

    @Test
    public void testWebOnt_description_logic_017() {
        premise = asString("/WebOnt_description_logic_017.owl");
        test("WebOnt_description_logic_017", TestClasses.INCONSISTENCY, "DL Test: t2.2");
    }

    @Test
    public void testWebOnt_description_logic_018() {
        premise = asString("/WebOnt_description_logic_018.owl");
        test("WebOnt_description_logic_018", TestClasses.CONSISTENCY,
            "DL Test: t3.1 There are 90 possible partitions in the satisfiable case");
    }

    @Test
    public void testWebOnt_description_logic_019() {
        premise = asString("/WebOnt_description_logic_019.owl");
        test("WebOnt_description_logic_019", TestClasses.INCONSISTENCY,
            "DL Test: t3.2 There are 301 possible partitions in the unsatisfiable case");
    }

    @Test
    public void testWebOnt_description_logic_020() {
        premise = asString("/WebOnt_description_logic_020.owl");
        test("WebOnt_description_logic_020", TestClasses.CONSISTENCY,
            "DL Test: t3a.1 there are 1,701 possible partitions in the satisfiable case");
    }

    @Test
    public void testWebOnt_description_logic_021() {
        premise = asString("/WebOnt_description_logic_021.owl");
        test("WebOnt_description_logic_021", TestClasses.CONSISTENCY,
            "DL Test: t3a.2 There are 7,770 possible partitions in the unsatisfiable case");
    }

    @Test
    public void testWebOnt_description_logic_022() {
        premise = asString("/WebOnt_description_logic_022.owl");
        test("WebOnt_description_logic_022", TestClasses.INCONSISTENCY,
            "DL Test: t3a.3 There are 42,525 possible partitions in the satisfiable case");
    }

    @Test
    public void testWebOnt_description_logic_023() {
        premise = asString("/WebOnt_description_logic_023.owl");
        test("WebOnt_description_logic_023", TestClasses.INCONSISTENCY,
            "DL Test: t4.1 Dynamic blocking example");
    }

    @Test
    public void testWebOnt_description_logic_024() {
        premise = asString("/WebOnt_description_logic_024.owl");
        test("WebOnt_description_logic_024", TestClasses.CONSISTENCY,
            "DL Test: t5.1 Non-finite model example from paper The concept should be coherent but has no finite model");
    }

    @Test
    public void testWebOnt_description_logic_025() {
        premise = asString("/WebOnt_description_logic_025.owl");
        test("WebOnt_description_logic_025", TestClasses.CONSISTENCY,
            "DL Test: t5f.1 Non-finite model example from paper The concept should be coherent but has no finite model");
    }

    @Test
    public void testWebOnt_description_logic_026() {
        premise = asString("/WebOnt_description_logic_026.owl");
        test("WebOnt_description_logic_026", TestClasses.INCONSISTENCY,
            "DL Test: t6.1 Double blocking example. The concept should be incoherent but needs double blocking");
    }

    @Test
    public void testWebOnt_description_logic_027() {
        premise = asString("/WebOnt_description_logic_027.owl");
        test("WebOnt_description_logic_027", TestClasses.INCONSISTENCY,
            "DL Test: t6f.1 Double blocking example. The concept should be incoherent but needs double blocking");
    }

    @Test
    public void testWebOnt_description_logic_028() {
        premise = asString("/WebOnt_description_logic_028.owl");
        test("WebOnt_description_logic_028", TestClasses.CONSISTENCY, "DL Test: t7.1");
    }

    @Test
    public void testWebOnt_description_logic_029() {
        premise = asString("/WebOnt_description_logic_029.owl");
        test("WebOnt_description_logic_029", TestClasses.INCONSISTENCY, "DL Test: t7.2");
    }

    @Test
    public void testWebOnt_description_logic_030() {
        premise = asString("/WebOnt_description_logic_030.owl");
        test("WebOnt_description_logic_030", TestClasses.INCONSISTENCY, "DL Test: t7.3");
    }

    @Test
    public void testWebOnt_description_logic_031() {
        premise = asString("/WebOnt_description_logic_031.owl");
        test("WebOnt_description_logic_031", TestClasses.CONSISTENCY, "DL Test: t7f.1");
    }

    @Test
    public void testWebOnt_description_logic_032() {
        premise = asString("/WebOnt_description_logic_032.owl");
        test("WebOnt_description_logic_032", TestClasses.INCONSISTENCY, "DL Test: t7f.2");
    }

    @Test
    public void testWebOnt_description_logic_033() {
        premise = asString("/WebOnt_description_logic_033.owl");
        test("WebOnt_description_logic_033", TestClasses.INCONSISTENCY, "DL Test: t7f.3");
    }

    @Test
    public void testWebOnt_description_logic_034() {
        premise = asString("/WebOnt_description_logic_034.owl");
        test("WebOnt_description_logic_034", TestClasses.CONSISTENCY, "DL Test: t8.1");
    }

    @Test
    public void testWebOnt_description_logic_035() {
        premise = asString("/WebOnt_description_logic_035.owl");
        test("WebOnt_description_logic_035", TestClasses.INCONSISTENCY,
            "A test for the interaction of one-of and inverse using the idea of a spy point. Everything is related to the spy via the property p and we know that the spy has at most two invP successors, thus limiting the cardinality of the domain to being at most 2.");
    }

    @Test
    public void testWebOnt_description_logic_040() {
        premise = asString("/WebOnt_description_logic_040.owl");
        test("WebOnt_description_logic_040", TestClasses.INCONSISTENCY,
            "This kind of pattern comes up a lot in more complex ontologies. Failure to cope with this kind of pattern is one of the reasons that many reasoners have been unable to cope with such ontologies.");
    }

    @Test
    public void testWebOnt_description_logic_101() {
        premise = asString("/WebOnt_description_logic_101.owl");
        test("WebOnt_description_logic_101", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn1.1 Tbox tests from Heinsohn et al. Tests incoherency caused by disjoint concept");
    }

    @Test
    public void testWebOnt_description_logic_102() {
        premise = asString("/WebOnt_description_logic_102.owl");
        test("WebOnt_description_logic_102", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn1.2 Tbox tests from Heinsohn et al. Tests incoherency caused by disjoint concept");
    }

    @Test
    public void testWebOnt_description_logic_103() {
        premise = asString("/WebOnt_description_logic_103.owl");
        test("WebOnt_description_logic_103", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn1.3 Tbox tests from Heinsohn et al. Tests incoherency caused by disjoint concept");
    }

    @Test
    public void testWebOnt_description_logic_104() {
        premise = asString("/WebOnt_description_logic_104.owl");
        test("WebOnt_description_logic_104", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn1.4 Tbox tests from Heinsohn et al. Tests incoherency caused by disjoint concept");
    }

    @Test
    public void testWebOnt_description_logic_105() {
        premise = asString("/WebOnt_description_logic_105.owl");
        test("WebOnt_description_logic_105", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn2.1 Tbox tests from Heinsohn et al. Tests incoherency caused by number restrictions");
    }

    @Test
    public void testWebOnt_description_logic_106() {
        premise = asString("/WebOnt_description_logic_106.owl");
        test("WebOnt_description_logic_106", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn2.2 Tbox tests from Heinsohn et al. Tests incoherency caused by number restrictions");
    }

    @Test
    public void testWebOnt_description_logic_107() {
        premise = asString("/WebOnt_description_logic_107.owl");
        test("WebOnt_description_logic_107", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn3.1 Tbox tests from Heinsohn et al. Tests incoherency caused by number restrictions and role hierarchy");
    }

    @Test
    public void testWebOnt_description_logic_108() {
        premise = asString("/WebOnt_description_logic_108.owl");
        test("WebOnt_description_logic_108", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn3.2 Tbox tests from Heinsohn et al. Tests incoherency caused by number restrictions and role hierarchy");
    }

    @Test
    public void testWebOnt_description_logic_109() {
        premise = asString("/WebOnt_description_logic_109.owl");
        test("WebOnt_description_logic_109", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn3c.1 Tbox tests from Heinsohn et al. Tests incoherency caused by number restrictions and role hierarchy");
    }

    @Test
    public void testWebOnt_description_logic_110() {
        premise = asString("/WebOnt_description_logic_110.owl");
        test("WebOnt_description_logic_110", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn4.1 Tbox tests from Heinsohn et al. Tests role restrictions");
    }

    @Test
    public void testWebOnt_description_logic_111() {
        premise = asString("/WebOnt_description_logic_111.owl");
        test("WebOnt_description_logic_111", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn4.2 Tbox tests from Heinsohn et al. Tests role restrictions");
    }

    @Test
    public void testWebOnt_description_logic_201() {
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "      xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions201\"> <owl:Ontology/>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C110\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C94\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C136\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C58\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C80\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C56\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C116\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\">\n"
                + "  <rdf:type><owl:Class rdf:about=\"http://oiled.man.example.net/test#C114\"/></rdf:type>\n"
                + "</owl:Thing>\n" + "</rdf:RDF>";
        premise = asString("/WebOnt_description_logic_201.owl");
        test("WebOnt_description_logic_201", TestClasses.POSITIVE_IMPL,
            "DL Test:  ABox test from DL98 systems comparison.");
    }

    @Test
    public void testWebOnt_description_logic_205() {
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "      xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions205\"\n"
                + ">\n" + "<owl:Ontology/>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C14\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16561\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16562\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></rdf:type>\n"
                + "</owl:Thing>\n" + "</rdf:RDF>";
        premise = asString("/WebOnt_description_logic_205.owl");
        test("WebOnt_description_logic_205", TestClasses.POSITIVE_IMPL,
            "DL Test: k_lin ABox test from DL98 systems comparison.");
    }

    @Test
    public void testWebOnt_description_logic_207() {
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "      xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions207\"\n"
                + ">\n" + "<owl:Ontology/>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V21080\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V21081\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></rdf:type>\n"
                + "</owl:Thing>\n" + "</rdf:RDF>";
        premise = asString("/WebOnt_description_logic_207.owl");
        test("WebOnt_description_logic_207", TestClasses.POSITIVE_IMPL,
            "DL Test: k_ph ABox test from DL98 systems comparison.");
    }

    @Test
    public void testWebOnt_description_logic_503() {
        premise = asString("/WebOnt_description_logic_503.owl");
        test("WebOnt_description_logic_503", TestClasses.CONSISTENCY,
            "This is a different encoding of test 501.");
    }

    @Test
    public void testWebOnt_description_logic_504() {
        premise = asString("/WebOnt_description_logic_504.owl");
        test("WebOnt_description_logic_504", TestClasses.INCONSISTENCY,
            "This is a different encoding of test 502.");
    }

    @Test
    public void testWebOnt_description_logic_601() {
        premise = asString("/WebOnt_description_logic_601.owl");
        test("WebOnt_description_logic_601", TestClasses.INCONSISTENCY,
            "DL Test: fact1.1 If a, b and c are disjoint, then: (a and b) or (b and c) or (c and a) is unsatisfiable.");
    }

    @Test
    public void testWebOnt_description_logic_602_old() {
        premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
            + "Prefix(urn:=<urn:test#>)\n" + "Ontology(<urn:testonto:>\n"
            + "Declaration(Class(<urn:A.2>))\n"
            + "EquivalentClasses(<urn:A.2> ObjectAllValuesFrom(<urn:r> <urn:c>))\n"
            + "SubClassOf(<urn:A.2> <urn:d>)\n" + "Declaration(Class(<urn:Unsatisfiable>))\n"
            + "SubClassOf(<urn:Unsatisfiable> <urn:c>)\n"
            + "SubClassOf(<urn:Unsatisfiable> <urn:d.comp>)\n" + "Declaration(Class(<urn:c>))\n"
            + "SubClassOf(<urn:c> ObjectAllValuesFrom(<urn:r> <urn:c>))\n"
            + "Declaration(Class(<urn:d>))\n"
            + "EquivalentClasses(<urn:d> ObjectMaxCardinality(0 <urn:p>))\n"
            + "Declaration(Class(<urn:d.comp>))\n"
            + "EquivalentClasses(<urn:d.comp> ObjectMinCardinality(1 <urn:p>))\n"
            + "Declaration(ObjectProperty(<urn:r>))\n" + "Declaration(ObjectProperty(<urn:p>))\n"
            + "ClassAssertion(<urn:Unsatisfiable> urn:ind))";
        test("WebOnt_description_logic_602", TestClasses.INCONSISTENCY, "DL Test: fact2.1");
    }

    @Test
    public void testWebOnt_description_logic_602() throws OWLOntologyCreationException {
        OWLOntology o = m.createOntology();
        OWLClass A = df.getOWLClass(IRI.create("urn:A"));
        OWLClass C = df.getOWLClass(IRI.create("urn:C"));
        OWLClass D = df.getOWLClass(IRI.create("urn:D"));
        OWLClass B = df.getOWLClass(IRI.create("urn:B"));
        OWLClass U = df.getOWLClass(IRI.create("urn:U"));
        OWLObjectProperty p = df.getOWLObjectProperty(IRI.create("urn:p"));
        OWLObjectProperty r = df.getOWLObjectProperty(IRI.create("urn:r"));
        OWLObjectAllValuesFrom rAllC = df.getOWLObjectAllValuesFrom(r, C);
        m.addAxiom(o, df.getOWLEquivalentClassesAxiom(A, rAllC));
        m.addAxiom(o, df.getOWLSubClassOfAxiom(A, D));
        m.addAxiom(o, df.getOWLSubClassOfAxiom(U, C));
        m.addAxiom(o, df.getOWLSubClassOfAxiom(U, B));
        m.addAxiom(o, df.getOWLSubClassOfAxiom(C, rAllC));
        OWLObjectMaxCardinality zeroP = df.getOWLObjectMaxCardinality(0, p);
        m.addAxiom(o, df.getOWLEquivalentClassesAxiom(D, zeroP));
        OWLObjectMinCardinality oneP = df.getOWLObjectMinCardinality(1, p);
        m.addAxiom(o, df.getOWLEquivalentClassesAxiom(B, oneP));
        OWLReasoner reasoner = factory().createReasoner(o);
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        assertFalse("cannot find unsatisfiable class", reasoner.isSatisfiable(U));
        assertTrue("cannot infer disjoint",
            reasoner.isEntailed(df.getOWLDisjointClassesAxiom(D, B)));
        assertTrue("cannot infer U [= B", reasoner.isEntailed(df.getOWLSubClassOfAxiom(U, B)));
        assertTrue("cannot infer U [= C", reasoner.isEntailed(df.getOWLSubClassOfAxiom(U, C)));
        assertTrue("cannot infer C [= r some C",
            reasoner.isEntailed(df.getOWLSubClassOfAxiom(C, rAllC)));
        assertTrue("cannot infer r some C = A",
            reasoner.isEntailed(df.getOWLEquivalentClassesAxiom(rAllC, A)));
        assertTrue("cannot infer A [= D", reasoner.isEntailed(df.getOWLSubClassOfAxiom(A, D)));
        assertTrue("cannot infer U [= D", reasoner.isEntailed(df.getOWLSubClassOfAxiom(U, D)));
        assertFalse("cannot find unsatisfiable class", reasoner.isSatisfiable(U));
    }

    @Test
    public void testWebOnt_description_logic_603() {
        premise = asString("/WebOnt_description_logic_603.owl");
        test("WebOnt_description_logic_603", TestClasses.INCONSISTENCY, "DL Test: fact3.1");
    }

    @Test
    public void testWebOnt_description_logic_604() {
        premise = asString("/WebOnt_description_logic_604.owl");
        test("WebOnt_description_logic_604", TestClasses.INCONSISTENCY, "DL Test: fact4.1");
    }

    @Test
    public void testWebOnt_description_logic_605() {
        premise = asString("/WebOnt_description_logic_605.owl");
        test("WebOnt_description_logic_605", TestClasses.CONSISTENCY, "DL Test: fact4.2");
    }

    @Test
    public void testWebOnt_description_logic_606() {
        premise = asString("/WebOnt_description_logic_606.owl");
        test("WebOnt_description_logic_606", TestClasses.CONSISTENCY, "DL Test: t1.1");
    }

    @Test
    public void testWebOnt_description_logic_608() {
        premise = asString("/WebOnt_description_logic_608.owl");
        test("WebOnt_description_logic_608", TestClasses.INCONSISTENCY, "DL Test: t1.3");
    }

    @Test
    public void testWebOnt_description_logic_609() {
        premise = asString("/WebOnt_description_logic_609.owl");
        test("WebOnt_description_logic_609", TestClasses.CONSISTENCY, "DL Test: t10.1");
    }

    @Test
    public void testWebOnt_description_logic_610() {
        premise = asString("/WebOnt_description_logic_610.owl");
        test("WebOnt_description_logic_610", TestClasses.INCONSISTENCY, "DL Test: t10.2");
    }

    @Test
    public void testWebOnt_description_logic_611() {
        premise = asString("/WebOnt_description_logic_611.owl");
        test("WebOnt_description_logic_611", TestClasses.INCONSISTENCY, "DL Test: t10.3");
    }

    @Test
    public void testWebOnt_description_logic_612() {
        premise = asString("/WebOnt_description_logic_612.owl");
        test("WebOnt_description_logic_612", TestClasses.INCONSISTENCY, "DL Test: t10.4");
    }

    @Test
    public void testWebOnt_description_logic_613() {
        premise = asString("/WebOnt_description_logic_613.owl");
        test("WebOnt_description_logic_613", TestClasses.INCONSISTENCY, "DL Test: t10.5");
    }

    @Test
    public void testWebOnt_description_logic_614() {
        premise = asString("/WebOnt_description_logic_614.owl");
        test("WebOnt_description_logic_614", TestClasses.INCONSISTENCY, "DL Test: t11.1");
    }

    @Test
    public void testWebOnt_description_logic_615() {
        premise = asString("/WebOnt_description_logic_615.owl");
        test("WebOnt_description_logic_615", TestClasses.INCONSISTENCY, "DL Test: t12.1");
    }

    @Test
    public void testWebOnt_description_logic_616() {
        premise = asString("/WebOnt_description_logic_616.owl");
        test("WebOnt_description_logic_616", TestClasses.CONSISTENCY, "DL Test: t2.1");
    }

    @Test
    public void testWebOnt_description_logic_617() {
        premise = asString("/WebOnt_description_logic_617.owl");
        test("WebOnt_description_logic_617", TestClasses.INCONSISTENCY, "DL Test: t2.2");
    }

    @Test
    public void testWebOnt_description_logic_623() {
        premise = asString("/WebOnt_description_logic_623.owl");
        test("WebOnt_description_logic_623", TestClasses.INCONSISTENCY,
            "DL Test: t4.1 Dynamic blocking example");
    }

    @Test
    public void testWebOnt_description_logic_624() {
        premise = asString("/WebOnt_description_logic_624.owl");
        test("WebOnt_description_logic_624", TestClasses.CONSISTENCY,
            "DL Test: t5.1 Non-finite model example from paper The concept should be coherent but has no finite model");
    }

    @Test
    public void testWebOnt_description_logic_625() {
        premise = asString("/WebOnt_description_logic_625.owl");
        test("WebOnt_description_logic_625", TestClasses.CONSISTENCY,
            "DL Test: t5f.1 Non-finite model example from paper The concept should be coherent but has no finite model");
    }

    @Test
    public void testWebOnt_description_logic_626() {
        premise = asString("/WebOnt_description_logic_626.owl");
        test("WebOnt_description_logic_626", TestClasses.INCONSISTENCY,
            "DL Test: t6.1 Double blocking example. The concept should be incoherent but needs double blocking");
    }

    @Test
    public void testWebOnt_description_logic_627() {
        premise = asString("/WebOnt_description_logic_627.owl");
        test("WebOnt_description_logic_627", TestClasses.INCONSISTENCY,
            "DL Test: t6f.1 Double blocking example. The concept should be incoherent but needs double blocking");
    }

    @Test
    public void testWebOnt_description_logic_628() {
        premise = asString("/WebOnt_description_logic_628.owl");
        test("WebOnt_description_logic_628", TestClasses.CONSISTENCY, "DL Test: t7.1");
    }

    @Test
    public void testWebOnt_description_logic_629() {
        premise = asString("/WebOnt_description_logic_629.owl");
        test("WebOnt_description_logic_629", TestClasses.INCONSISTENCY, "DL Test: t7.2");
    }

    @Test
    public void testWebOnt_description_logic_630() {
        premise = asString("/WebOnt_description_logic_630.owl");
        test("WebOnt_description_logic_630", TestClasses.INCONSISTENCY, "DL Test: t7.3");
    }

    @Test
    public void testWebOnt_description_logic_631() {
        premise = asString("/WebOnt_description_logic_631.owl");
        test("WebOnt_description_logic_631", TestClasses.CONSISTENCY, "DL Test: t7f.1");
    }

    @Test
    public void testWebOnt_description_logic_632() {
        premise = asString("/WebOnt_description_logic_632.owl");
        test("WebOnt_description_logic_632", TestClasses.INCONSISTENCY, "DL Test: t7f.2");
    }

    @Test
    public void testWebOnt_description_logic_633() {
        premise = asString("/WebOnt_description_logic_633.owl");
        test("WebOnt_description_logic_633", TestClasses.INCONSISTENCY, "DL Test: t7f.3");
    }

    @Test
    public void testWebOnt_description_logic_634() {
        premise = asString("/WebOnt_description_logic_634.owl");
        test("WebOnt_description_logic_634", TestClasses.CONSISTENCY, "DL Test: t8.1");
    }

    @Test
    public void testWebOnt_description_logic_641() {
        premise = asString("/WebOnt_description_logic_641.owl");
        test("WebOnt_description_logic_641", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn1.1 Tbox tests from Heinsohn et al. Tests incoherency caused by disjoint concept");
    }

    @Test
    public void testWebOnt_description_logic_642() {
        premise = asString("/WebOnt_description_logic_642.owl");
        test("WebOnt_description_logic_642", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn1.2 Tbox tests from Heinsohn et al. Tests incoherency caused by disjoint concept");
    }

    @Test
    public void testWebOnt_description_logic_643() {
        premise = asString("/WebOnt_description_logic_643.owl");
        test("WebOnt_description_logic_643", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn1.3 Tbox tests from Heinsohn et al. Tests incoherency caused by disjoint concept");
    }

    @Test
    public void testWebOnt_description_logic_644() {
        premise = asString("/WebOnt_description_logic_644.owl");
        test("WebOnt_description_logic_644", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn1.4 Tbox tests from Heinsohn et al. Tests incoherency caused by disjoint concept");
    }

    @Test
    public void testWebOnt_description_logic_646() {
        premise = asString("/WebOnt_description_logic_646.owl");
        test("WebOnt_description_logic_646", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn2.2 Tbox tests from Heinsohn et al. Tests incoherency caused by number restrictions");
    }

    @Test
    public void testWebOnt_description_logic_650() {
        premise = asString("/WebOnt_description_logic_650.owl");
        test("WebOnt_description_logic_650", TestClasses.INCONSISTENCY,
            "DL Test: heinsohn4.1 Tbox tests from Heinsohn et al. Tests role restrictions");
    }

    @Test
    public void testWebOnt_description_logic_665() {
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "      xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions665\"\n"
                + ">\n" + "<owl:Ontology/>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16560\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C14\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16561\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V16562\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></rdf:type>\n"
                + "</owl:Thing>\n" + "</rdf:RDF>";
        premise = asString("/WebOnt_description_logic_665.owl");
        test("WebOnt_description_logic_665", TestClasses.POSITIVE_IMPL,
            "DL Test: k_lin ABox test from DL98 systems comparison.");
    }

    @Test
    public void testWebOnt_description_logic_667() {
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "      xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions667\"\n"
                + ">\n" + "<owl:Ontology/>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V21080\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V21081\">\n"
                + "  <rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></rdf:type>\n"
                + "</owl:Thing>\n" + "</rdf:RDF>";
        premise = asString("/WebOnt_description_logic_667.owl");
        test("WebOnt_description_logic_667", TestClasses.POSITIVE_IMPL,
            "DL Test: k_ph ABox test from DL98 systems comparison.");
    }

    @Test
    public void testWebOnt_description_logic_901() {
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/description-logic/premises901#\" xmlns:second=\"http://www.w3.org/2002/03owlt/description-logic/conclusions901#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions901\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:Class>\n"
                + "    <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises901#p\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises901#q\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">3</owl:minCardinality></owl:Restriction></owl:intersectionOf>\n"
                + "    <rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises901#r\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">5</owl:minCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "</rdf:RDF>";
        premise = asString("/WebOnt_description_logic_901.owl");
        test("WebOnt_description_logic_901", TestClasses.POSITIVE_IMPL,
            "This entailment can be replicated for any three natural numbers i, j, k such that i+j >= k. In this example, they are chosen as 2, 3 and 5.");
    }

    @Test
    public void testWebOnt_description_logic_902() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/premises902\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:ObjectProperty rdf:ID=\"r\"/>\n"
                + "  <owl:ObjectProperty rdf:ID=\"p\">\n"
                + "    <rdfs:subPropertyOf rdf:resource=\"#r\"/>\n" + "    <rdfs:range>\n"
                + "      <owl:Class rdf:ID=\"A\"/></rdfs:range></owl:ObjectProperty>\n"
                + "  <owl:ObjectProperty rdf:ID=\"q\">\n"
                + "    <rdfs:subPropertyOf rdf:resource=\"#r\"/>\n" + "    <rdfs:range>\n"
                + "      <owl:Class rdf:ID=\"B\"/></rdfs:range></owl:ObjectProperty>\n"
                + "  <owl:Class rdf:about=\"#A\">\n"
                + "    <owl:disjointWith rdf:resource=\"#B\"/>\n" + "  </owl:Class></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/nonconclusions902\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:Class>\n"
                + "    <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises902#p\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">2</owl:minCardinality></owl:Restriction><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises902#q\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">3</owl:minCardinality></owl:Restriction></owl:intersectionOf>\n"
                + "    <rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"premises902#r\"/></owl:onProperty><owl:minCardinality rdf:datatype=\n"
                + "\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">6</owl:minCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "</rdf:RDF>";
        test("WebOnt_description_logic_902", TestClasses.NEGATIVE_IMPL,
            "This non-entailment can be replicated for any three natural numbers i, j, k such that i+j < k. In this example, they are chosen as 2, 3 and 6.");
    }

    @Test
    public void testWebOnt_description_logic_905() {
        premise = asString("/WebOnt_description_logic_905.owl");
        test("WebOnt_description_logic_905", TestClasses.CONSISTENCY,
            "This test shows integer multiplication in OWL DL. N is 2. M is 3. N times M is 6.");
    }

    @Test
    public void testWebOnt_description_logic_908() {
        premise = asString("/WebOnt_description_logic_908.owl");
        test("WebOnt_description_logic_908", TestClasses.CONSISTENCY,
            "This test shows integer multiplication in OWL DL, interacting with infinity.N times infinity is 2  times infinity. M times infinity is 3 times infinity. N times M times infinity is 5 times infinity.");
    }

    @Test
    public void testWebOnt_differentFrom_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/differentFrom/premises001#\" xmlns:second=\"http://www.w3.org/2002/03owlt/differentFrom/conclusions001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/differentFrom/premises001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <rdf:Description rdf:about=\"premises001#a\"><owl:differentFrom rdf:resource=\"premises001#b\"/></rdf:Description></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/differentFrom/premises001#\" xmlns:second=\"http://www.w3.org/2002/03owlt/differentFrom/conclusions001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/differentFrom/conclusions001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <rdf:Description rdf:about=\"premises001#b\"><owl:differentFrom rdf:resource=\"premises001#a\"/></rdf:Description></rdf:RDF>";
        test("WebOnt_differentFrom_001", TestClasses.POSITIVE_IMPL,
            "differentFrom is a SymmetricProperty.");
    }

    @Test
    public void testWebOnt_disjointWith_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:first=\"http://www.w3.org/2002/03owlt/disjointWith/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/premises001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"A\"><owl:disjointWith><owl:Class rdf:ID=\"B\"/></owl:disjointWith></owl:Class>\n"
                + "   <first:A rdf:ID=\"a\"/>\n" + "   <owl:Thing rdf:about=\"#a\"/>\n"
                + "   <first:B rdf:ID=\"b\"/>\n" + "   <owl:Thing rdf:about=\"#b\"/></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/disjointWith/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/conclusions001\" >\n"
                + "   <owl:Ontology/>\n"
                + "    <owl:Thing rdf:about=\"premises001#a\"><owl:differentFrom><owl:Thing rdf:about=\"premises001#b\"/></owl:differentFrom></owl:Thing></rdf:RDF>";
        test("WebOnt_disjointWith_001", TestClasses.POSITIVE_IMPL,
            "Disjoint classes have different members.");
    }

    @Test
    public void testWebOnt_disjointWith_003() {
        premise = asString("/WebOnt_disjointWith_003.owl");
        test("WebOnt_disjointWith_003", TestClasses.CONSISTENCY,
            "If the owl:disjointWith edges in the graph form an undirected complete subgraph  then this may be within OWL DL.");
    }

    @Test
    public void testWebOnt_disjointWith_004() {
        premise = asString("/WebOnt_disjointWith_004.owl");
        test("WebOnt_disjointWith_004", TestClasses.CONSISTENCY,
            "This example has owl:disjointWith edges in the graph which cannot be generated by the mapping rules for DisjointClasses. Consider the lack of owl:disjointWith edge between nodes C and D.");
    }

    @Test
    public void testWebOnt_disjointWith_005() {
        premise = asString("/WebOnt_disjointWith_005.owl");
        test("WebOnt_disjointWith_005", TestClasses.CONSISTENCY,
            "If the owl:disjointWith edges in the graph form unconnected undirected complete subgraphs then this may be within OWL DL.");
    }

    @Test
    public void testWebOnt_disjointWith_006() {
        premise = asString("/WebOnt_disjointWith_006.owl");
        test("WebOnt_disjointWith_006", TestClasses.CONSISTENCY,
            "If the owl:disjointWith edges in the graph form undirected complete subgraphs which share blank nodes then this was not within OWL DL, but is permissible in OWL 2 DL.");
    }

    @Test
    public void testWebOnt_disjointWith_007() {
        premise = asString("/WebOnt_disjointWith_007.owl");
        test("WebOnt_disjointWith_007", TestClasses.CONSISTENCY,
            "If the owl:disjointWith edges in the graph form undirected complete subgraphs which share URIref nodes but do not share blank node\nthen this may be within OWL DL.");
    }

    @Test
    public void testWebOnt_disjointWith_008() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/consistent008\" >\n"
                + "    <owl:Ontology/>\n" + "    <owl:Class rdf:ID=\"A\"/>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf><owl:disjointWith rdf:resource=\"#A\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"C\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"C\"/></owl:intersectionOf><owl:disjointWith rdf:resource=\"#A\"/></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"D\"><owl:disjointWith rdf:nodeID=\"B\"/><owl:disjointWith rdf:nodeID=\"C\"/></owl:Class></rdf:RDF>";
        test("WebOnt_disjointWith_008", TestClasses.CONSISTENCY,
            "A further example that cannot be generated from the mapping rule for DisjointClasses.");
    }

    @Test
    public void testWebOnt_disjointWith_009() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/disjointWith/consistent009\" >\n"
                + "    <owl:Ontology/>\n" + "    <owl:Class rdf:ID=\"A\"/>\n"
                + "    <owl:Class rdf:nodeID=\"B\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"B\"/></owl:intersectionOf><owl:disjointWith rdf:resource=\"#A\"/></owl:Class>\n"
                + "    <owl:Class rdf:nodeID=\"C\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"C\"/></owl:intersectionOf><owl:disjointWith rdf:resource=\"#A\"/></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"D\"><owl:disjointWith rdf:nodeID=\"B\"/><owl:disjointWith rdf:nodeID=\"C\"/><owl:disjointWith rdf:resource=\"#A\"/></owl:Class></rdf:RDF>";
        test("WebOnt_disjointWith_009", TestClasses.CONSISTENCY,
            "If the owl:disjointWith edges in the graph form undirected complete subgraphs which share URIref nodes but do not share blank node then this may be within OWL DL.");
    }

    @Test
    public void testWebOnt_equivalentClass_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentClass/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises001\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:ID=\"Car\">\n"
                + "     <owl:equivalentClass><owl:Class rdf:ID=\"Automobile\"/></owl:equivalentClass></owl:Class>\n"
                + "  <first:Car rdf:ID=\"car\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Car>\n"
                + "  <first:Automobile rdf:ID=\"auto\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Automobile></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentClass/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/conclusions001\" >\n"
                + "  <owl:Ontology/>\n" + "  <first:Car rdf:about=\"premises001#auto\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Car>\n"
                + "  <first:Automobile rdf:about=\"premises001#car\">\n"
                + "     <rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\" /></first:Automobile>\n"
                + "   <owl:Class rdf:about=\"premises001#Car\"/>\n"
                + "   <owl:Class rdf:about=\"premises001#Automobile\"/>\n" + "</rdf:RDF>";
        test("WebOnt_equivalentClass_001", TestClasses.POSITIVE_IMPL,
            "Two classes may have the same class extension.");
    }

    @Test
    public void testWebOnt_equivalentClass_002() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises002\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:ID=\"Car\">\n"
                + "     <owl:equivalentClass><owl:Class rdf:ID=\"Automobile\"/></owl:equivalentClass></owl:Class>\n"
                + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/conclusions002\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:about=\"premises002#Car\">\n"
                + "     <rdfs:subClassOf><owl:Class rdf:about=\"premises002#Automobile\"><rdfs:subClassOf rdf:resource=\"premises002#Car\" /></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "</rdf:RDF>";
        test("WebOnt_equivalentClass_002", TestClasses.POSITIVE_IMPL,
            "Two classes may be different names for the same set of individuals");
    }

    @Test
    public void testWebOnt_equivalentClass_003() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises003\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:about=\"conclusions003#Car\">\n"
                + "     <rdfs:subClassOf><owl:Class rdf:about=\"conclusions003#Automobile\"><rdfs:subClassOf rdf:resource=\"conclusions003#Car\" /></owl:Class></rdfs:subClassOf></owl:Class>\n"
                + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/conclusions003\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:ID=\"Car\">\n"
                + "     <owl:equivalentClass><owl:Class rdf:ID=\"Automobile\"/></owl:equivalentClass></owl:Class>\n"
                + "</rdf:RDF>";
        test("WebOnt_equivalentClass_003", TestClasses.POSITIVE_IMPL,
            "Two classes may be different names for the same set of individuals");
    }

    @Test
    public void testWebOnt_equivalentClass_004() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises004\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"c1\"><owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "    <owl:Class rdf:ID=\"c3\"/><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"c2\"><owl:intersectionOf rdf:parseType=\"Collection\">\n"
                + "    <owl:Class rdf:ID=\"c3\"/><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality></owl:Restriction></owl:intersectionOf></owl:Class>\n"
                + "     <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/conclusions004\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:Class rdf:about=\"premises004#c1\">\n"
                + "     <owl:equivalentClass><owl:Class rdf:about=\"premises004#c2\"/></owl:equivalentClass></owl:Class>\n"
                + "</rdf:RDF>";
        test("WebOnt_equivalentClass_004", TestClasses.POSITIVE_IMPL,
            "Two classes with the same complete description are equivalent.");
    }

    @Test
    public void testWebOnt_equivalentClass_005() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises005\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"c1\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:ID=\"c2\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:cardinality\n"
                + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:cardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "     <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/nonconclusions005\" >\n"
                + "    <owl:Ontology/>\n" + "<owl:Class rdf:about=\"premises005#c1\">\n"
                + "     <owl:equivalentClass><owl:Class rdf:about=\"premises005#c2\"/></owl:equivalentClass></owl:Class>\n"
                + "</rdf:RDF>";
        test("WebOnt_equivalentClass_005", TestClasses.NEGATIVE_IMPL,
            "Two classes with the same partial description are not equivalent.");
    }

    @Test
    public void testWebOnt_equivalentClass_006() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises006\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:ID=\"A\"/>\n"
                + "   <owl:Class rdf:ID=\"B\"/>\n" + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/conclusions006\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class><owl:complementOf rdf:resource=\"premises006#A\"/></owl:Class><owl:Class><owl:complementOf rdf:resource=\"premises006#B\"/></owl:Class></owl:intersectionOf><owl:equivalentClass><owl:Class><owl:complementOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"premises006#A\"/><rdf:Description rdf:about=\"premises006#B\"/></owl:unionOf></owl:Class></owl:complementOf></owl:Class></owl:equivalentClass></owl:Class>   \n"
                + "   <owl:Class rdf:about=\"premises006#A\"/>\n"
                + "   <owl:Class rdf:about=\"premises006#B\"/>\n" + "</rdf:RDF>";
        test("WebOnt_equivalentClass_006", TestClasses.POSITIVE_IMPL, "De Morgan's law.");
    }

    @Test
    public void testWebOnt_equivalentClass_008_Direct() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentClass/premises008#\" \n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/premises008\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class rdf:ID=\"c1\"><owl:equivalentClass><owl:Class rdf:ID=\"c2\"/></owl:equivalentClass>\n"
                + "     <first:annotate>description of c1</first:annotate></owl:Class>\n"
                + "   <owl:AnnotationProperty rdf:ID=\"annotate\" />\n" + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentClass/premises008#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/nonconclusions008\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:about=\"premises008#c2\">\n"
                + "     <first:annotate>description of c1</first:annotate></owl:Class>\n"
                + "   <owl:AnnotationProperty rdf:about=\"premises008#annotate\" /></rdf:RDF>";
        test("WebOnt_equivalentClass_008_Direct", TestClasses.POSITIVE_IMPL,
            "A version of WebOnt-equivalentClass-008 modified for the Direct Semantics, under which annotations in the entailed ontology are ignored.");
    }

    @Test
    public void testWebOnt_equivalentClass_009() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/equivalentClass/consistent009\" >\n"
                + "   <owl:Ontology/>\n" + " <owl:Class rdf:nodeID=\"a\">\n"
                + "   <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"A\"/></owl:oneOf>\n"
                + "   <owl:equivalentClass>   \n"
                + "     <owl:Class rdf:nodeID=\"b\"><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"J\"/><owl:Class rdf:ID=\"B\"/></owl:unionOf></owl:Class></owl:equivalentClass>  \n"
                + "   <owl:equivalentClass>    \n"
                + "     <owl:Class rdf:nodeID=\"c\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:ID=\"K\"/><owl:Class rdf:ID=\"C\"/></owl:intersectionOf></owl:Class></owl:equivalentClass>  \n"
                + "   <owl:equivalentClass>    \n"
                + "     <owl:Class rdf:nodeID=\"d\"><owl:complementOf><owl:Class rdf:ID=\"D\"/></owl:complementOf></owl:Class></owl:equivalentClass>  \n"
                + " </owl:Class>\n" + "</rdf:RDF>";
        test("WebOnt_equivalentClass_009", TestClasses.CONSISTENCY,
            "A possible mapping of the EquivalentClasses axiom, which is connected but without a Hamiltonian path.");
    }

    @Test
    public void testWebOnt_equivalentProperty_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:ID=\"hasHead\"><owl:equivalentProperty><owl:ObjectProperty rdf:ID=\"hasLeader\"/></owl:equivalentProperty></owl:ObjectProperty>\n"
                + "   <owl:Thing rdf:ID=\"X\">\n"
                + "     <first:hasLeader><owl:Thing rdf:ID=\"Y\"/></first:hasLeader></owl:Thing>\n"
                + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/conclusions001\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Thing rdf:about=\"premises001#X\">\n"
                + "     <first:hasHead><owl:Thing rdf:about=\"premises001#Y\"/></first:hasHead></owl:Thing>   \n"
                + "   <owl:ObjectProperty rdf:about=\"premises001#hasHead\"/></rdf:RDF>";
        test("WebOnt_equivalentProperty_001", TestClasses.POSITIVE_IMPL,
            "hasLeader may be stated to be the owl:equivalentProperty of hasHead.");
    }

    @Test
    public void testWebOnt_equivalentProperty_002() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:ID=\"hasHead\"><owl:equivalentProperty><owl:ObjectProperty rdf:ID=\"hasLeader\"/></owl:equivalentProperty></owl:ObjectProperty></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises002#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/conclusions002\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:about=\"premises002#hasHead\"><rdfs:subPropertyOf rdf:resource=\"premises002#hasLeader\"/></owl:ObjectProperty>\n"
                + "   <owl:ObjectProperty rdf:about=\"premises002#hasLeader\"><rdfs:subPropertyOf rdf:resource=\"premises002#hasHead\"/></owl:ObjectProperty></rdf:RDF>";
        test("WebOnt_equivalentProperty_002", TestClasses.POSITIVE_IMPL,
            "A reasoner can also deduce that hasLeader is a subProperty of hasHead and hasHead is a subProperty of hasLeader.");
    }

    @Test
    public void testWebOnt_equivalentProperty_003() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises003\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:about=\"conclusions003#hasHead\"><rdfs:subPropertyOf rdf:resource=\"conclusions003#hasLeader\"/></owl:ObjectProperty>\n"
                + "   <owl:ObjectProperty rdf:about=\"conclusions003#hasLeader\"><rdfs:subPropertyOf rdf:resource=\"conclusions003#hasHead\"/></owl:ObjectProperty></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/conclusions003\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:ID=\"hasHead\"><owl:equivalentProperty><owl:ObjectProperty rdf:ID=\"hasLeader\"/></owl:equivalentProperty></owl:ObjectProperty></rdf:RDF>";
        test("WebOnt_equivalentProperty_003", TestClasses.POSITIVE_IMPL,
            "The inverse entailment of test 002 also holds.");
    }

    @Test
    public void testWebOnt_equivalentProperty_004() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/premises004\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:ID=\"p\"><rdfs:domain rdf:resource=\"#d\"/></owl:ObjectProperty>\n"
                + "   <owl:ObjectProperty rdf:ID=\"q\"><rdfs:domain rdf:resource=\"#d\"/></owl:ObjectProperty>\n"
                + "   <owl:FunctionalProperty rdf:about=\"#q\"/>\n"
                + "   <owl:FunctionalProperty rdf:about=\"#p\"/>\n"
                + "   <owl:Thing rdf:ID=\"v\"/>\n" + "   <owl:Class rdf:ID=\"d\">\n"
                + "     <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:hasValue rdf:resource=\"#v\"/></owl:Restriction></owl:equivalentClass>\n"
                + "     <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#q\"/><owl:hasValue rdf:resource=\"#v\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/equivalentProperty/conclusions004\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:ObjectProperty rdf:about=\"premises004#p\"><owl:equivalentProperty><owl:ObjectProperty rdf:about=\"premises004#q\"/></owl:equivalentProperty></owl:ObjectProperty></rdf:RDF>";
        test("WebOnt_equivalentProperty_004", TestClasses.POSITIVE_IMPL,
            "If p and q have the same property extension then p equivalentProperty q.");
    }

    @Test
    public void testWebOnt_imports_011() {
        premise = "<rdf:RDF " + "xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' "
            + "xmlns:rdfs='http://www.w3.org/2000/01/rdf-schema#' "
            + "xmlns:owl='http://www.w3.org/2002/07/owl#' "
            + "    xml:base='http://www.w3.org/2002/03owlt/imports/premises011' >\n"
            + "    <owl:Ontology rdf:about=''></owl:Ontology>\n"
            + "<owl:Class rdf:about='urn:test#Man'><rdfs:subClassOf rdf:resource='urn:test#Mortal'/></owl:Class>\n"
            + "    <owl:Class rdf:about='urn:test#Mortal'/>\n"
            + "    <owl:Thing rdf:about='urn:test#Socrates'><rdf:type><owl:Class rdf:about='urn:test#Man'/></rdf:type></owl:Thing></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' xmlns:rdfs='http://www.w3.org/2000/01/rdf-schema#' xmlns:owl='http://www.w3.org/2002/07/owl#'\n"
                + "    xml:base='http://www.w3.org/2002/03owlt/imports/conclusions011' >\n"
                + "    <owl:Ontology/>\n"
                + "    <rdf:Description rdf:about='urn:test#Socrates'><rdf:type><owl:Class rdf:about='urn:test#Mortal'/></rdf:type></rdf:Description></rdf:RDF>";
        test("WebOnt_imports_011", TestClasses.POSITIVE_IMPL,
            "A Lite version of test <a xmlns=\"http://www.w3.org/1999/xhtml\" href=\"#imports-001\">imports-001</a>.");
    }

    @Test
    public void testWebOnt_maxCardinality_001() {
        premise = asString("/WebOnt_maxCardinality_001.owl");
        test("WebOnt_maxCardinality_001", TestClasses.INCONSISTENCY,
            "A property with maximum cardinality of two cannot take three distinct values on some subject node.");
    }

    @Test
    public void testWebOnt_miscellaneous_102() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent102#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent102\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Thing rdf:ID=\"i\">\n"
                + "     <rdf:type><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"p\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:ID=\"a\"/></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#p\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:ID=\"s\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></rdf:type></owl:Thing>\n"
                + "</rdf:RDF>";
        test("WebOnt_miscellaneous_102", TestClasses.CONSISTENCY,
            "Abstract syntax restrictions with multiple components are in OWL DL.");
    }

    @Test
    public void testWebOnt_miscellaneous_103() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent103#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent103\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Thing rdf:ID=\"i\">\n"
                + "     <rdf:type><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"p\"/></owl:onProperty><owl:allValuesFrom><owl:Class rdf:ID=\"a\"/></owl:allValuesFrom></owl:Restriction><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#q\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:ID=\"s\"/></owl:someValuesFrom></owl:Restriction></owl:intersectionOf></owl:Class></rdf:type></owl:Thing>\n"
                + "</rdf:RDF>";
        test("WebOnt_miscellaneous_103", TestClasses.CONSISTENCY,
            "This description cannot be expressed as a multicomponent restriction in the OWL 1 abstract syntax.");
    }

    @Test
    public void testWebOnt_miscellaneous_302_Direct() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/premises302#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/premises302\" >\n"
                + "<owl:Ontology/>\n" + "<owl:AnnotationProperty rdf:ID=\"prop\" />\n"
                + "<owl:Thing rdf:about=\"#a\">\n" + "   <first:prop>foo</first:prop></owl:Thing>\n"
                + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/premises302#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/nonconclusions302\" >\n"
                + "<owl:Ontology/>\n"
                + "<owl:AnnotationProperty rdf:about=\"premises302#prop\" />\n"
                + "<owl:Thing rdf:about=\"premises302#a\">\n" + "   <first:prop>bar</first:prop>\n"
                + "</owl:Thing>\n" + "</rdf:RDF>";
        test("WebOnt_miscellaneous_302_Direct", TestClasses.POSITIVE_IMPL,
            "A version of WebOnt-miscellaneous-302 applicable under the Direct Semantics, in which the annotation in the entailed ontology is not considered.");
    }

    @Test
    public void testWebOnt_miscellaneous_303() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent303\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:AnnotationProperty rdf:about='http://purl.org/dc/elements/1.0/creator'/></rdf:RDF>";
        test("WebOnt_miscellaneous_303", TestClasses.CONSISTENCY,
            "dc:creator may be declared as an annotation property.");
    }

    @Test
    public void testWebOnt_oneOf_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/oneOf/consistent001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/oneOf/consistent001\" >\n"
                + "   <owl:Ontology/>\n"
                + "   <owl:Class><owl:oneOf rdf:parseType=\"Collection\"><rdf:Description rdf:ID=\"amy\"/><rdf:Description rdf:ID=\"bob\"/><rdf:Description rdf:ID=\"caroline\"/></owl:oneOf><owl:equivalentClass><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><rdf:Description rdf:ID=\"yolanda\"/><rdf:Description rdf:ID=\"zebedee\"/></owl:oneOf></owl:Class></owl:equivalentClass></owl:Class></rdf:RDF>";
        test("WebOnt_oneOf_001", TestClasses.CONSISTENCY,
            "oneOf <em>does not</em> indicate that the named individuals are distinct. Thus a consistent interpretation of this file is when all the individual names denote the\n"
                + "same individual.");
    }

    @Test
    public void testWebOnt_sameAs_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/sameAs/premises001#\"\n"
                + "  xml:base=\"http://www.w3.org/2002/03owlt/sameAs/premises001\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:ID=\"c1\">\n" + "     <owl:sameAs>\n"
                + "       <owl:Class rdf:ID=\"c2\"/></owl:sameAs>\n"
                + "     <first:annotate>description of c1</first:annotate></owl:Class>\n"
                + "   <owl:AnnotationProperty rdf:ID=\"annotate\" />\n" + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/sameAs/premises001#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/sameAs/conclusions001\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:about=\"premises001#c2\">\n"
                + "     <first:annotate>description of c1</first:annotate></owl:Class>\n"
                + "   <owl:AnnotationProperty rdf:about=\"premises001#annotate\" /></rdf:RDF>";
        test("WebOnt_sameAs_001", TestClasses.POSITIVE_IMPL,
            "Annotation properties refer to a class instance. sameAs, in OWL Full, also refers to the class instance.");
    }

    @Test
    public void testWebOnt_unionOf_003() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/unionOf/premises003\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:ID=\"A\">\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"a\"/></owl:oneOf></owl:Class>\n"
                + "   <owl:Class rdf:ID=\"B\">\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"b\"/></owl:oneOf></owl:Class>\n"
                + "   <owl:Class rdf:ID=\"A-and-B\">\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:about=\"#a\"/><owl:Thing rdf:about=\"#b\"/></owl:oneOf></owl:Class>\n"
                + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/unionOf/conclusions003\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:about=\"premises003#A-and-B\">\n"
                + "     <owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"premises003#A\"/><owl:Class rdf:about=\"premises003#B\"/></owl:unionOf></owl:Class>\n"
                + "</rdf:RDF>";
        test("WebOnt_unionOf_003", TestClasses.POSITIVE_IMPL,
            "Sets with appropriate extensions are related by unionOf.");
    }

    @Test
    public void testWebOnt_unionOf_004() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/unionOf/premises004#\" xmlns:second=\"http://www.w3.org/2002/03owlt/unionOf/conclusions004#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/unionOf/premises004\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:about=\"#A-and-B\">\n"
                + "     <owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#A\"/><owl:Class rdf:about=\"#B\"/></owl:unionOf></owl:Class>\n"
                + "   <owl:Class rdf:ID=\"A\">\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"a\"/></owl:oneOf></owl:Class>\n"
                + "   <owl:Class rdf:ID=\"B\">\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:ID=\"b\"/></owl:oneOf></owl:Class>\n"
                + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/unionOf/premises004#\" xmlns:second=\"http://www.w3.org/2002/03owlt/unionOf/conclusions004#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/unionOf/conclusions004\" >\n"
                + "   <owl:Ontology/>\n" + "   <owl:Class rdf:about=\"premises004#A-and-B\">\n"
                + "     <owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:about=\"premises004#a\"/><owl:Thing rdf:about=\"premises004#b\"/></owl:oneOf></owl:Class>\n"
                + "</rdf:RDF>";
        test("WebOnt_unionOf_004", TestClasses.POSITIVE_IMPL,
            "An inverse to test <a xmlns=\"http://www.w3.org/1999/xhtml\" href=\"#unionOf-003\">003</a>.");
    }
}
