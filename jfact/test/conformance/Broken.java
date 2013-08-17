package conformance;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.Assert.*;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

@SuppressWarnings("javadoc")

public class Broken {
    // XXX this needs to be fixed
    @Test
    public void testConsistent_but_all_unsat() throws Exception {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<!DOCTYPE rdf:RDF [<!ENTITY example \"http://example.com/\" ><!ENTITY owl \"http://www.w3.org/2002/07/owl#\" ><!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" ><!ENTITY owl2xml \"http://www.w3.org/2006/12/owl2-xml#\" ><!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" ><!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n"
                + "]>\n"
                + "<rdf:RDF xmlns=\"http://example.com/\"\n"
                + "     xml:base=\"http://example.com/\" xmlns:owl2xml=\"http://www.w3.org/2006/12/owl2-xml#\" xmlns:example=\"http://example.com/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"><owl:Ontology rdf:about=\"http://owl.semanticweb.org/page/Special:GetOntology/Consistent-but-all-unsat?m=p\"/>\n"
                + "    <owl:ObjectProperty rdf:about=\"2aTOa\"><rdf:type rdf:resource=\"&owl;FunctionalProperty\"/><rdf:type rdf:resource=\"&owl;InverseFunctionalProperty\"/><rdfs:label rdf:datatype=\"&xsd;string\">2a&lt;=&gt;a</rdfs:label></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"2aTObUNIONc\"><rdf:type rdf:resource=\"&owl;FunctionalProperty\"/><rdf:type rdf:resource=\"&owl;InverseFunctionalProperty\"/><rdfs:label rdf:datatype=\"&xsd;string\">2a&lt;=&gt;bUNIONc</rdfs:label></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"aTO2a\"><rdf:type rdf:resource=\"&owl;FunctionalProperty\"/><rdf:type rdf:resource=\"&owl;InverseFunctionalProperty\"/><rdfs:label rdf:datatype=\"&xsd;string\">a&lt;=&gt;2a</rdfs:label><owl:inverseOf rdf:resource=\"2aTOa\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"aTOb\"><rdf:type rdf:resource=\"&owl;FunctionalProperty\"/><rdf:type rdf:resource=\"&owl;InverseFunctionalProperty\"/><rdfs:label rdf:datatype=\"&xsd;string\">a&lt;=&gt;b</rdfs:label></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"bUNIONcTO2a\"><rdf:type rdf:resource=\"&owl;FunctionalProperty\"/><rdf:type rdf:resource=\"&owl;InverseFunctionalProperty\"/><rdfs:label rdf:datatype=\"&xsd;string\">bUNIONc&lt;=&gt;2a</rdfs:label><owl:inverseOf rdf:resource=\"2aTObUNIONc\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"bTOa\"><rdf:type rdf:resource=\"&owl;FunctionalProperty\"/><rdf:type rdf:resource=\"&owl;InverseFunctionalProperty\"/><rdfs:label rdf:datatype=\"&xsd;string\">b&lt;=&gt;a</rdfs:label><owl:inverseOf rdf:resource=\"aTOb\"/></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"bTOc\"><rdf:type rdf:resource=\"&owl;FunctionalProperty\"/><rdf:type rdf:resource=\"&owl;InverseFunctionalProperty\"/><rdfs:label rdf:datatype=\"&xsd;string\">b&lt;=&gt;c</rdfs:label></owl:ObjectProperty>\n"
                + "    <owl:ObjectProperty rdf:about=\"cTOb\"><rdf:type rdf:resource=\"&owl;FunctionalProperty\"/><rdf:type rdf:resource=\"&owl;InverseFunctionalProperty\"/><rdfs:label rdf:datatype=\"&xsd;string\">c&lt;=&gt;b</rdfs:label><owl:inverseOf rdf:resource=\"bTOc\"/></owl:ObjectProperty>\n"
                + "    <owl:Class rdf:about=\"2a\"><rdfs:label rdf:datatype=\"&xsd;string\">2a</rdfs:label><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"2aTObUNIONc\"/><owl:someValuesFrom rdf:resource=\"bUNIONc\"/></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"2aTOa\"/><owl:someValuesFrom rdf:resource=\"a\"/></owl:Restriction></rdfs:subClassOf><owl:disjointWith rdf:resource=\"a\"/><owl:disjointWith rdf:resource=\"b\"/><owl:disjointWith rdf:resource=\"bUNIONc\"/><owl:disjointWith rdf:resource=\"c\"/></owl:Class>\n"
                + "    <owl:Class rdf:about=\"a\"><rdfs:label rdf:datatype=\"&xsd;string\">a</rdfs:label><rdfs:subClassOf><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"i1\"/></owl:oneOf></owl:Class><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"i2\"/></owl:oneOf></owl:Class><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"i3\"/></owl:oneOf></owl:Class></owl:unionOf></owl:Class></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"aTO2a\"/><owl:someValuesFrom rdf:resource=\"2a\"/></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"aTOb\"/><owl:someValuesFrom rdf:resource=\"b\"/></owl:Restriction></rdfs:subClassOf><owl:disjointWith rdf:resource=\"b\"/><owl:disjointWith rdf:resource=\"c\"/></owl:Class>\n"
                + "    <owl:Class rdf:about=\"b\"><rdfs:label rdf:datatype=\"&xsd;string\">b</rdfs:label><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"bTOc\"/><owl:someValuesFrom rdf:resource=\"c\"/></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"bTOa\"/><owl:someValuesFrom rdf:resource=\"a\"/></owl:Restriction></rdfs:subClassOf><owl:disjointWith rdf:resource=\"c\"/></owl:Class>\n"
                + "    <owl:Class rdf:about=\"bUNIONc\"><rdfs:label rdf:datatype=\"&xsd;string\">bUNIONc</rdfs:label><owl:equivalentClass><owl:Class><owl:unionOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"b\"/><rdf:Description rdf:about=\"c\"/></owl:unionOf></owl:Class></owl:equivalentClass><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"bUNIONcTO2a\"/><owl:someValuesFrom rdf:resource=\"2a\"/></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:Class rdf:about=\"c\"><rdfs:label rdf:datatype=\"&xsd;string\">c</rdfs:label><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"cTOb\"/><owl:someValuesFrom rdf:resource=\"b\"/></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <rdf:Description rdf:about=\"i1\"/>\n"
                + "    <rdf:Description rdf:about=\"i2\"/>\n"
                + "    <rdf:Description rdf:about=\"i3\"/>\n" + "</rdf:RDF>";
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<!DOCTYPE rdf:RDF [<!ENTITY owl \"http://www.w3.org/2002/07/owl#\" ><!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" ><!ENTITY owl2xml \"http://www.w3.org/2006/12/owl2-xml#\" ><!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" ><!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" ><!ENTITY Ontology1242664364013 \"http://www.semanticweb.org/ontologies/2009/4/Ontology1242664364013.owl#\" ><!ENTITY Ontology12426643640132 \"http://www.semanticweb.org/ontologies/2009/4/Ontology1242664364013.owl#2\" >\n"
                + "]>\n"
                + "<rdf:RDF xmlns=\"http://example.com/\"\n"
                + "     xml:base=\"http://example.com/\" xmlns:owl2xml=\"http://www.w3.org/2006/12/owl2-xml#\" xmlns:example=\"http://example.com/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"><owl:Ontology rdf:about=\"http://owl.semanticweb.org/page/Special:GetOntology/Consistent-but-all-unsat?m=c\"/>\n"
                + "    <owl:Class rdf:about=\"2a\"><rdfs:subClassOf rdf:resource=\"&owl;Nothing\"/></owl:Class>\n"
                + "    <owl:Class rdf:about=\"a\"><rdfs:subClassOf rdf:resource=\"&owl;Nothing\"/><rdfs:subClassOf rdf:resource=\"&owl;Thing\"/></owl:Class>\n"
                + "    <owl:Class rdf:about=\"b\"><rdfs:subClassOf rdf:resource=\"&owl;Nothing\"/></owl:Class>\n"
                + "    <owl:Class rdf:about=\"c\"><rdfs:subClassOf rdf:resource=\"&owl;Nothing\"/></owl:Class>\n"
                + "    <owl:Class rdf:about=\"&owl;Nothing\"/>\n"
                + "    <owl:Class rdf:about=\"&owl;Thing\"/>\n" + "</rdf:RDF>\n";
        String id = "Consistent_but_all_unsat";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An ontology that is consistent, but all named classes are unsatisfiable.  Ideas by Alan Ruttenberg";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        // r.getConfiguration().setLoggingActive(true);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    // XXX this needs to be fixed
    @Test
    public void testWebOnt_oneOf_004() {
        String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\nPrefix(xml:=<http://www.w3.org/XML/1998/namespace>)\nPrefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\nPrefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(\nDeclaration(DataProperty(<urn:t:p#p>))\n"
                + "DataPropertyRange(<urn:t:p#p> DataOneOf(\"1\"^^xsd:integer \"2\"^^xsd:integer \"3\"^^xsd:integer \"4\"^^xsd:integer))\n"
                + "DataPropertyRange(<urn:t:p#p> DataOneOf(\"4\"^^xsd:integer \"5\"^^xsd:integer \"6\"^^xsd:integer))\n"
                + "ClassAssertion(owl:Thing <urn:t:p#i>)\n"
                + "ClassAssertion(DataMinCardinality(1 <urn:t:p#p>) <urn:t:p#i>)\n"
                // +"DataPropertyAssertion(<urn:t:p#p> <urn:t:p#i> \"4\"^^xsd:integer)"
                + ")";
        String conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\nPrefix(xml:=<http://www.w3.org/XML/1998/namespace>)\nPrefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\nPrefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(\nDeclaration(DataProperty(<urn:t:p#p>))\n"
                + "ClassAssertion(owl:Thing <urn:t:p#i>)\n"
                + "DataPropertyAssertion(<urn:t:p#p> <urn:t:p#i> \"4\"^^xsd:integer))";
        String id = "WebOnt_oneOf_004";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "This test illustrates the use of dataRange in OWL DL. This test combines some of the ugliest features of XML, RDF and OWL.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    // XXX this needs to be fixed
    @Test
    @Changed(reason = "changed to fix it")
    public void testone_two() {
        String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(<http://example.com/>\n"
                + "Declaration(Class(<http://example.com/2a>))\n"
                + "Declaration(Class(<http://example.com/a>))\n"
                + "Declaration(Class(<http://example.com/b>))\n"
                + "Declaration(Class(<http://example.com/b_and_c>))\n"
                + "Declaration(Class(<http://example.com/c>))\n"
                + "SubClassOf(<http://example.com/2a> ObjectSomeValuesFrom(<http://example.com/2a_to_a> <http://example.com/a>))\n"
                + "SubClassOf(<http://example.com/2a> ObjectSomeValuesFrom(<http://example.com/2a_to_b_and_c> <http://example.com/b_and_c>))\n"
                + "DisjointClasses(<http://example.com/2a> <http://example.com/a>)\n"
                + "DisjointClasses(<http://example.com/2a> <http://example.com/b>)\n"
                + "DisjointClasses(<http://example.com/2a> <http://example.com/b_and_c>)\n"
                + "DisjointClasses(<http://example.com/2a> <http://example.com/c>)\n"
                + "Declaration(ObjectProperty(<http://example.com/2a_to_a>))\n"
                + "Declaration(ObjectProperty(<http://example.com/2a_to_b_and_c>))\n"
                + "Declaration(ObjectProperty(<http://example.com/a_to_2a_prime>))\n"
                + "Declaration(ObjectProperty(<http://example.com/a_to_b>))\n"
                + "Declaration(ObjectProperty(<http://example.com/b_and_c_to_2a_prime>))\n"
                + "Declaration(ObjectProperty(<http://example.com/b_to_a_prime>))\n"
                + "Declaration(ObjectProperty(<http://example.com/b_to_c>))\n"
                + "Declaration(ObjectProperty(<http://example.com/c_to_b_prime>))\n"
                + "EquivalentClasses(<http://example.com/a> ObjectOneOf(<http://example.com/j> <http://example.com/i> <http://example.com/k>))\n"
                + "SubClassOf(<http://example.com/a> ObjectSomeValuesFrom(<http://example.com/a_to_2a_prime> <http://example.com/2a>))\n"
                + "SubClassOf(<http://example.com/a> ObjectSomeValuesFrom(<http://example.com/a_to_b> <http://example.com/b>))\n"
                + "DisjointClasses(<http://example.com/a> <http://example.com/2a>)\n"
                + "DisjointClasses(<http://example.com/a> <http://example.com/b>)\n"
                + "DisjointClasses(<http://example.com/a> <http://example.com/c>)\n"
                + "SubClassOf(<http://example.com/b> ObjectSomeValuesFrom(<http://example.com/b_to_a_prime> <http://example.com/a>))\n"
                + "SubClassOf(<http://example.com/b> ObjectSomeValuesFrom(<http://example.com/b_to_c> <http://example.com/c>))\n"
                + "DisjointClasses(<http://example.com/b> <http://example.com/2a>)\n"
                + "DisjointClasses(<http://example.com/b> <http://example.com/a>)\n"
                + "DisjointClasses(<http://example.com/b> <http://example.com/c>)\n"
                + "EquivalentClasses(<http://example.com/b_and_c> ObjectUnionOf(<http://example.com/c> <http://example.com/b>))\n"
                + "SubClassOf(<http://example.com/b_and_c> ObjectSomeValuesFrom(<http://example.com/b_and_c_to_2a_prime> <http://example.com/2a>))\n"
                + "DisjointClasses(<http://example.com/b_and_c> <http://example.com/2a>)\n"
                + "SubClassOf(<http://example.com/c> ObjectSomeValuesFrom(<http://example.com/c_to_b_prime> <http://example.com/b>))\n"
                + "DisjointClasses(<http://example.com/c> <http://example.com/2a>)\n"
                + "DisjointClasses(<http://example.com/c> <http://example.com/a>)\n"
                + "DisjointClasses(<http://example.com/c> <http://example.com/b>)\n"
                + "InverseObjectProperties(<http://example.com/a_to_2a_prime> <http://example.com/2a_to_a>)\n"
                + "FunctionalObjectProperty(<http://example.com/2a_to_a>)\n"
                + "InverseFunctionalObjectProperty(<http://example.com/2a_to_a>)\n"
                + "InverseObjectProperties(<http://example.com/b_and_c_to_2a_prime> <http://example.com/2a_to_b_and_c>)\n"
                + "FunctionalObjectProperty(<http://example.com/2a_to_b_and_c>)\n"
                + "InverseFunctionalObjectProperty(<http://example.com/2a_to_b_and_c>)\n"
                + "InverseObjectProperties(<http://example.com/a_to_2a_prime> <http://example.com/2a_to_a>)\n"
                + "FunctionalObjectProperty(<http://example.com/a_to_2a_prime>)\n"
                + "InverseFunctionalObjectProperty(<http://example.com/a_to_2a_prime>)\n"
                + "InverseObjectProperties(<http://example.com/b_to_a_prime> <http://example.com/a_to_b>)\n"
                + "FunctionalObjectProperty(<http://example.com/a_to_b>)\n"
                + "InverseFunctionalObjectProperty(<http://example.com/a_to_b>)\n"
                + "InverseObjectProperties(<http://example.com/b_and_c_to_2a_prime> <http://example.com/2a_to_b_and_c>)\n"
                + "FunctionalObjectProperty(<http://example.com/b_and_c_to_2a_prime>)\n"
                + "InverseFunctionalObjectProperty(<http://example.com/b_and_c_to_2a_prime>)\n"
                + "InverseObjectProperties(<http://example.com/b_to_a_prime> <http://example.com/a_to_b>)\n"
                + "FunctionalObjectProperty(<http://example.com/b_to_a_prime>)\n"
                + "InverseFunctionalObjectProperty(<http://example.com/b_to_a_prime>)\n"
                + "InverseObjectProperties(<http://example.com/c_to_b_prime> <http://example.com/b_to_c>)\n"
                + "FunctionalObjectProperty(<http://example.com/b_to_c>)\n"
                + "InverseFunctionalObjectProperty(<http://example.com/b_to_c>)\n"
                + "InverseObjectProperties(<http://example.com/c_to_b_prime> <http://example.com/b_to_c>)\n"
                + "FunctionalObjectProperty(<http://example.com/c_to_b_prime>)\n"
                + "InverseFunctionalObjectProperty(<http://example.com/c_to_b_prime>)\n"
                + "DifferentIndividuals(<http://example.com/i> <http://example.com/j> <http://example.com/k>))";
        String conclusion = "";
        String id = "one_two";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Start with 3 classes, a,b,c and relate them so instances have to be in a 1:1 relationship with each other.\n"
                + "The class b-and-c is the union of b and c. Therefore there have to be 2 instances of b-and-c for every instance of a.\n"
                + "Relate the class 2a to b-and-c so that *their* instances are in 1:1 relationship.\n"
                + "Now relate 2a to a so that *their* instances are in a 1:1 relationship. This should lead to a situation in which every instance\n"
                + "of 2a is 1:1 with an instance of a, and at the same time 2:1 with an instance of a.\n"
                + "Unless all the classes have an infinite number of members or are empty this doesn't work. This example has a is the enumerated class {i,j,k} (i,j,k all different individuals). So it should be inconsistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    // XXX this needs to be fixed
    @Test
    public void testBugFix() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataProperty p = DataProperty(IRI.create("urn:t:t#p"));
        OWLNamedIndividual i = NamedIndividual(IRI.create("urn:t:t#i"));
        m.addAxiom(o, Declaration(p));
        m.addAxiom(o, Declaration(i));
        OWLDataOneOf owlDataOneOf = DataOneOf(Literal(1), Literal(2), Literal(3),
                Literal(4));
        OWLDataOneOf owlDataOneOf2 = DataOneOf(Literal(4), Literal(5), Literal(6));
        m.addAxiom(o, DataPropertyRange(p, owlDataOneOf));
        m.addAxiom(o, DataPropertyRange(p, owlDataOneOf2));
        m.addAxiom(o, ClassAssertion(DataMinCardinality(1, p, TopDatatype()), i));
        OWLReasoner r = Factory.factory().createReasoner(o);
        OWLDataPropertyAssertionAxiom ass = DataPropertyAssertion(p, i, Literal(4));
        assertTrue(r.isConsistent());
        boolean entailed = r.isEntailed(ass);
        assertTrue(entailed);
    }

    @Ignore
    @Test
    public void testUnsatisfiableClasses() throws OWLOntologyCreationException {
        OWLOntologyManager mngr = OWLManager.createOWLOntologyManager();
        OWLOntology ont = mngr.createOntology();
        OWLDataFactory df = OWLManager.getOWLDataFactory();
        OWLDataProperty dp = df.getOWLDataProperty(IRI.create("urn:test:datap1"));
        mngr.addAxiom(ont, df.getOWLDataPropertyDomainAxiom(dp, df.getOWLNothing()));
        OWLReasonerFactory fac = Factory.factory();
        OWLReasoner r = fac.createNonBufferingReasoner(ont);
        assertEquals(2, r.getBottomDataPropertyNode().getEntities().size());
    }

    @Ignore
    @Test
    public void testWebOnt_I5_8_009() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/premises009\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:DatatypeProperty rdf:ID=\"p\">\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\" />\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#nonPositiveInteger\" /></owl:DatatypeProperty></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions009\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:DatatypeProperty rdf:about=\"premises009#p\">\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#short\" /></owl:DatatypeProperty>\n"
                + "\n" + "</rdf:RDF>";
        String id = "WebOnt_I5_8_009";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "0 is the only <code>xsd:nonNegativeInteger</code> which is\n"
                + "also an <code>xsd:nonPositiveInteger</code>. 0 is an\n"
                + "<code>xsd:short</code>.";
        // XXX while it is true, I don't see why the zero should be a short
        // instead of a oneof from int or integer or any of the types in the
        // middle.
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Ignore
    @Test
    public void testWebOnt_I5_8_008() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/premises008\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:DatatypeProperty rdf:ID=\"p\">\n"
                + "    <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#short\" />\n"
                + "    <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#unsignedInt\" />"
                + "</owl:DatatypeProperty></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions008\" >\n<owl:Ontology/>\n"
                + "  <owl:DatatypeProperty rdf:about=\"premises008#p\">\n"
                + "    <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#unsignedShort\" /></owl:DatatypeProperty></rdf:RDF>";
        String id = "WebOnt_I5_8_008";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "-1 is an xsd:short that is not an xsd:unsignedShort; 100000 is an "
                + "xsd:unsignedInt that is not an xsd:unsignedShort; but there are no\n"
                + "xsd:unsignedShort which are neither xsd:short nor xsd:unsignedInt";
        // TODO to make this work, the datatype reasoner must be able to infer
        // short and unsigned int equivalent unsigned short
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Ignore
    @Test
    public void testWebOnt_someValuesFrom_003() {
        String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(\n"
                + "Declaration(Class(<urn:person>))\n"
                + "EquivalentClasses(<urn:person> ObjectSomeValuesFrom(<urn:parent> <urn:person>))\n"
                + "Declaration(ObjectProperty(<urn:parent>))\n"
                + "ClassAssertion(<urn:person> <urn:fred>))";
        // "<rdf:RDF\n"
        // + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
        // + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
        // +
        // "    xmlns:first=\"http://www.w3.org/2002/03owlt/someValuesFrom/premises003#\"\n"
        // +
        // "    xml:base=\"http://www.w3.org/2002/03owlt/someValuesFrom/premises003\" >\n"
        // + "   <owl:Ontology/>\n"
        // + "   <owl:Class rdf:ID=\"person\">\n"
        // + "     <owl:equivalentClass><owl:Restriction>\n"
        // +
        // "         <owl:onProperty><owl:ObjectProperty rdf:ID=\"parent\"/></owl:onProperty>\n"
        // + "         <owl:someValuesFrom rdf:resource=\"#person\" />\n"
        // + "       </owl:Restriction></owl:equivalentClass>\n"
        // + "    </owl:Class>\n"
        // + "    <first:person rdf:ID=\"fred\" />\n" + "\n"
        // + "</rdf:RDF>";
        String conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
                + "Ontology(\n"
                + "Declaration(ObjectProperty(<urn:parent>))\n"
                +
                // "Declaration(ObjectProperty(<urn:parent>))\n"+
                // "ClassAssertion(owl:Thing <urn:fred>)\n"+
                "ObjectPropertyAssertion(<urn:parent> <urn:fred> _:genid2)\n"
                + "ClassAssertion(owl:Thing _:genid3)\n"
                + "ClassAssertion(owl:Thing _:genid2)\n"
                + "ObjectPropertyAssertion(<urn:parent> _:genid2 _:genid3))";
        // "<rdf:RDF\n"
        // + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
        // + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
        // +
        // "    xmlns:first=\"http://www.w3.org/2002/03owlt/someValuesFrom/premises003#\"\n"
        // +
        // "    xml:base=\"http://www.w3.org/2002/03owlt/someValuesFrom/conclusions003\" >\n"
        // + "   <owl:Ontology/>\n"
        // + "   <owl:ObjectProperty rdf:about=\"premises003#parent\"/>\n"
        // + "   <owl:Thing rdf:about=\"premises003#fred\">"
        // +
        // "<first:parent><owl:Thing><first:parent><owl:Thing/></first:parent></owl:Thing>\n"
        // + "     </first:parent>\n" + "   </owl:Thing>\n" + "</rdf:RDF>";
        // XXX I do not understand these blank nodes used as existential
        // variables
        String id = "WebOnt_someValuesFrom_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A simple infinite loop for implementors to avoid.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Ignore
    @Test
    public void testsomevaluesfrom2bnode() throws OWLOntologyCreationException {
        // String premise =
        // "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"  \n"
        // + "          xmlns:ex=\"http://example.org/\"\n"
        // + "          xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
        // + "          xml:base=\"http://example.org/\">\n"
        // + "  <owl:Ontology />\n"
        // + "  <owl:ObjectProperty rdf:about=\"p\"/>\n"
        // + "  <rdf:Description rdf:about=\"a\">\n"
        // + "        <rdf:type>\n"
        // + "            <owl:Restriction>\n"
        // + "                <owl:onProperty rdf:resource=\"p\"/>\n"
        // +
        // "                <owl:someValuesFrom rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/>\n"
        // + "            </owl:Restriction>\n"
        // + "        </rdf:type>\n"
        // + "    </rdf:Description>\n"
        // + "</rdf:RDF>";
        // String conclusion =
        // "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"  \n"
        // + "          xmlns:ex=\"http://example.org/\"\n"
        // + "          xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
        // + "          xml:base=\"http://example.org/\">\n"
        // + "  <owl:Ontology />\n"
        // + "  <owl:ObjectProperty rdf:about=\"p\"/>\n"
        // + "  <rdf:Description rdf:about=\"a\">\n"
        // + "    <ex:p><rdf:Description/></ex:p> \n"
        // + "  </rdf:Description>\n" + "</rdf:RDF>";
        //
        // String id = "somevaluesfrom2bnode";
        // TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        // String d = "Shows that a BNode is an existential variable.";
        // JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        // r.setReasonerFactory(Factory.factory());
        // r.printPremise();
        // r.printConsequence();
        // r.run();
        // XXX I do not understand these blank nodes used as existential
        // variables
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLOntology o = m.createOntology();
        OWLObjectProperty p = f.getOWLObjectProperty(IRI.create("urn:p"));
        OWLNamedIndividual a = f.getOWLNamedIndividual(IRI.create("urn:a"));
        OWLObjectSomeValuesFrom c = f.getOWLObjectSomeValuesFrom(p, f.getOWLThing());
        m.addAxiom(o, f.getOWLClassAssertionAxiom(c, a));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertTrue(r.isEntailed(f.getOWLObjectPropertyAssertionAxiom(p, a,
                f.getOWLAnonymousIndividual())));
    }

    @Ignore
    @Test
    public void testConsistent_owl_real_range_with_DataOneOf() {
        // XXX integers, float and reals do not share a value space
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp owl:real)) \n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp DataOneOf(\"-INF\"^^xsd:float \"-0\"^^xsd:integer))\n)\n  ClassAssertion(:A :a)\n)";
        String conclusion = "";
        String id = "Consistent_owl_real_range_with_DataOneOf";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "The individual a must have either negative Infinity or 0 (-0 as integer is 0) as dp fillers and all dp successors must be from owl:real, which excludes negative infinity, but allows 0.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
