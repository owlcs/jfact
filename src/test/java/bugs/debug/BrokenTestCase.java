package bugs.debug;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Declaration;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DifferentIndividuals;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DisjointClasses;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.EquivalentClasses;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.FunctionalObjectProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.InverseFunctionalObjectProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.InverseObjectProperties;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.NamedIndividual;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectOneOf;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectSomeValuesFrom;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectUnionOf;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.SubClassOf;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import conformancetests.ChangedTestCase;
import conformancetests.TestClasses;
import testbase.TestBase;

@Disabled("disabling for release")
class BrokenTestCase extends TestBase {

    @Test
    void testQualified_cardinality_boolean() {
        premise = "Prefix( : = <http://example.org/test#> )\n"
            + "Prefix( xsd: = <http://www.w3.org/2001/XMLSchema#> )\n" + '\n'
            + "Ontology(<http://owl.semanticweb.org/page/Special:GetOntology/Qualified-cardinality-boolean?m=p>\n"
            + "  Declaration(NamedIndividual(:a))\n" + "  Declaration(Class(:A))\n"
            + "  Declaration(DataProperty(:dp))\n" + '\n'
            + "  SubClassOf(:A DataExactCardinality(2 :dp xsd:boolean))\n" + '\n'
            + "  ClassAssertion(:A :a)\n" + ')';
        conclusion = "Prefix( : = <http://example.org/test#> )\n"
            + "Prefix( xsd: = <http://www.w3.org/2001/XMLSchema#> )\n" + '\n'
            + "Ontology(<http://owl.semanticweb.org/page/Special:GetOntology/Qualified-cardinality-boolean?m=c>\n"
            + "  Declaration(DataProperty(:dp))\n" + '\n'
            + "  DataPropertyAssertion(:dp :a \"true\"^^xsd:boolean)\n"
            + "  DataPropertyAssertion(:dp :a \"false\"^^xsd:boolean)\n" + ')';
        test("Qualified_cardinality_boolean", TestClasses.POSITIVE_IMPL,
            "According to qualified cardinality restriction individual a should have two boolean values. Since there are only two boolean values, the data property assertions can be entailed.");
    }

    // XXX this needs to be fixed
    @Test
    @ChangedTestCase(reason = "changed to fix unreliable iris")
    void testone_two() throws OWLException {
        IRI ontoIRI = IRI("urn:onetwo");
        OWLOntology o = m.createOntology(ontoIRI);
        String ns = "http://example.com/";
        OWLClass twoa = Class(IRI(ns + "twoa"));
        OWLObjectProperty atotwoaprime = ObjectProperty(IRI(ns + "atotwoaprime"));
        OWLObjectProperty atob = ObjectProperty(IRI(ns + "atob"));
        OWLClass bandc = Class(IRI(ns + "bandc"));
        OWLClass b = Class(IRI(ns + 'b'));
        OWLClass c = Class(IRI(ns + 'c'));
        OWLClass a = Class(IRI(ns + 'a'));
        OWLObjectProperty twoatobandc = ObjectProperty(IRI(ns + "twoatobandc"));
        OWLNamedIndividual j = NamedIndividual(IRI(ns + 'j'));
        OWLNamedIndividual i = NamedIndividual(IRI(ns + 'i'));
        OWLNamedIndividual k = NamedIndividual(IRI(ns + 'k'));
        OWLObjectProperty bandctotwoaprime = ObjectProperty(IRI(ns + "bandctotwoaprime"));
        OWLObjectProperty btoaprime = ObjectProperty(IRI(ns + "btoaprime"));
        OWLObjectProperty btoc = ObjectProperty(IRI(ns + "btoc"));
        OWLObjectProperty ctobprime = ObjectProperty(IRI(ns + "ctobprime"));
        OWLObjectProperty twoatoa = ObjectProperty(IRI(ns + "twoatoa"));
        m.addAxiom(o, Declaration(a));
        m.addAxiom(o, EquivalentClasses(a, ObjectOneOf(j, i, k)));
        m.addAxiom(o, SubClassOf(a, ObjectSomeValuesFrom(atob, b)));
        m.addAxiom(o, SubClassOf(a, ObjectSomeValuesFrom(atotwoaprime, twoa)));
        m.addAxiom(o, DisjointClasses(a, b));
        m.addAxiom(o, DisjointClasses(a, c));
        m.addAxiom(o, DisjointClasses(a, twoa));
        m.addAxiom(o, Declaration(b));
        m.addAxiom(o, SubClassOf(b, ObjectSomeValuesFrom(btoaprime, a)));
        m.addAxiom(o, SubClassOf(b, ObjectSomeValuesFrom(btoc, c)));
        m.addAxiom(o, DisjointClasses(b, a));
        m.addAxiom(o, DisjointClasses(b, c));
        m.addAxiom(o, DisjointClasses(b, twoa));
        m.addAxiom(o, Declaration(bandc));
        m.addAxiom(o, EquivalentClasses(bandc, ObjectUnionOf(c, b)));
        m.addAxiom(o, SubClassOf(bandc, ObjectSomeValuesFrom(bandctotwoaprime, twoa)));
        m.addAxiom(o, DisjointClasses(bandc, twoa));
        m.addAxiom(o, Declaration(c));
        m.addAxiom(o, SubClassOf(c, ObjectSomeValuesFrom(ctobprime, b)));
        m.addAxiom(o, DisjointClasses(c, a));
        m.addAxiom(o, DisjointClasses(c, b));
        m.addAxiom(o, DisjointClasses(c, twoa));
        m.addAxiom(o, Declaration(twoa));
        m.addAxiom(o, SubClassOf(twoa, ObjectSomeValuesFrom(twoatoa, a)));
        m.addAxiom(o, SubClassOf(twoa, ObjectSomeValuesFrom(twoatobandc, bandc)));
        m.addAxiom(o, DisjointClasses(twoa, a));
        m.addAxiom(o, DisjointClasses(twoa, b));
        m.addAxiom(o, DisjointClasses(twoa, bandc));
        m.addAxiom(o, DisjointClasses(twoa, c));
        m.addAxiom(o, Declaration(atob));
        m.addAxiom(o, InverseObjectProperties(btoaprime, atob));
        m.addAxiom(o, FunctionalObjectProperty(atob));
        m.addAxiom(o, InverseFunctionalObjectProperty(atob));
        m.addAxiom(o, Declaration(atotwoaprime));
        m.addAxiom(o, InverseObjectProperties(atotwoaprime, twoatoa));
        m.addAxiom(o, FunctionalObjectProperty(atotwoaprime));
        m.addAxiom(o, InverseFunctionalObjectProperty(atotwoaprime));
        m.addAxiom(o, Declaration(bandctotwoaprime));
        m.addAxiom(o, InverseObjectProperties(bandctotwoaprime, twoatobandc));
        m.addAxiom(o, FunctionalObjectProperty(bandctotwoaprime));
        m.addAxiom(o, InverseFunctionalObjectProperty(bandctotwoaprime));
        m.addAxiom(o, Declaration(btoaprime));
        m.addAxiom(o, InverseObjectProperties(btoaprime, atob));
        m.addAxiom(o, FunctionalObjectProperty(btoaprime));
        m.addAxiom(o, InverseFunctionalObjectProperty(btoaprime));
        m.addAxiom(o, Declaration(btoc));
        m.addAxiom(o, InverseObjectProperties(ctobprime, btoc));
        m.addAxiom(o, FunctionalObjectProperty(btoc));
        m.addAxiom(o, InverseFunctionalObjectProperty(btoc));
        m.addAxiom(o, Declaration(ctobprime));
        m.addAxiom(o, InverseObjectProperties(ctobprime, btoc));
        m.addAxiom(o, FunctionalObjectProperty(ctobprime));
        m.addAxiom(o, InverseFunctionalObjectProperty(ctobprime));
        m.addAxiom(o, Declaration(twoatoa));
        m.addAxiom(o, InverseObjectProperties(atotwoaprime, twoatoa));
        m.addAxiom(o, FunctionalObjectProperty(twoatoa));
        m.addAxiom(o, InverseFunctionalObjectProperty(twoatoa));
        m.addAxiom(o, Declaration(twoatobandc));
        m.addAxiom(o, InverseObjectProperties(bandctotwoaprime, twoatobandc));
        m.addAxiom(o, FunctionalObjectProperty(twoatobandc));
        m.addAxiom(o, InverseFunctionalObjectProperty(twoatobandc));
        m.addAxiom(o, DifferentIndividuals(i, j, k));
        OWLReasoner reasoner = factory().createReasoner(o);
        assertFalse(reasoner.isConsistent(),
            "Start with 3 classes, a,b,c and relate them so instances have to be in a 1:1 relationship with each other.\n"
                + "The class b-and-c is the union of b and c. Therefore there have to be 2 instances of b-and-c for every instance of a.\n"
                + "Relate the class 2a to b-and-c so that *their* instances are in 1:1 relationship.\n"
                + "Now relate 2a to a so that *their* instances are in a 1:1 relationship. This should lead to a situation in which every instance\n"
                + "of 2a is 1:1 with an instance of a, and at the same time 2:1 with an instance of a.\n"
                + "Unless all the classes have an infinite number of members or are empty this doesn't work. This example has a is the enumerated class {i,j,k} (i,j,k all different individuals). So it should be inconsistent.");
    }

    @Test
    void testConsistent_Datatype_restrictions_with_Different_Types() {
        premise = "Prefix(:=<http://example.org/>)\n"
            + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
            + "  Declaration(NamedIndividual(:a))\n" + "  Declaration(DataProperty(:dp))\n"
            + "  Declaration(Class(:A))\n" + "  SubClassOf(:A "
            + "DataAllValuesFrom(:dp DataOneOf(\"3\"^^xsd:integer \"4\"^^xsd:int))) \n"
            + "  SubClassOf(:A "
            + "DataAllValuesFrom(:dp DataOneOf(\"2\"^^xsd:short \"3\"^^xsd:int)))\n"
            + "  ClassAssertion(:A :a)\n"
            + "  ClassAssertion(DataSomeValuesFrom(:dp DataOneOf(\"3\"^^xsd:integer)) :a\n"
            + "  )\n" + ')';
        test("Consistent_Datatype_restrictions_with_Different_Types", TestClasses.CONSISTENCY,
            "The individual a must have dp fillers that are in the sets {3, 4} and {2, 3} (different types are used, but shorts and ints are integers). Furthermore, the dp filler must be 3, but since 3 is in both sets, the ontology is consistent.");
    }

    @Test
    void testUnsatisfiableClasses() throws OWLOntologyCreationException {
        OWLOntology ont = m.createOntology();
        OWLDataProperty dp = df.getOWLDataProperty(IRI.create("urn:test:datap1"));
        m.addAxiom(ont, df.getOWLDataPropertyDomainAxiom(dp, df.getOWLNothing()));
        OWLReasonerFactory fac = factory();
        OWLReasoner r = fac.createNonBufferingReasoner(ont);
        assertEquals(2L, r.getBottomDataPropertyNode().entities().count(),
            r.getBottomDataPropertyNode().toString());
    }

    @Test
    void testWebOnt_I5_8_009() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/premises009\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:DatatypeProperty rdf:ID=\"p\">\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\" />\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#nonPositiveInteger\" /></owl:DatatypeProperty></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions009\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:DatatypeProperty rdf:about=\"premises009#p\">\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#short\" /></owl:DatatypeProperty>\n" + '\n'
                + "</rdf:RDF>";
        // XXX while it is true, I don't see why the zero should be a short
        // instead of a oneof from int or integer or any of the types in the
        // middle.
        test("WebOnt_I5_8_009", TestClasses.POSITIVE_IMPL,
            "0 is the only xsd:nonNegativeInteger which is also an xsd:nonPositiveInteger. 0 is an\n"
                + "xsd:short.");
    }

    @Test
    void testWebOnt_I5_8_010() {
        premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
            + "Declaration(DataProperty(<urn:t#p>))\n"
            + "DataPropertyRange(<urn:t#p> xsd:nonNegativeInteger)\n"
            + "ClassAssertion(DataSomeValuesFrom(<urn:t#p> xsd:nonPositiveInteger) <urn:t#john>)\n)";
        conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Ontology(\nDeclaration(DataProperty(<urn:t#p>))\n"
            + "ClassAssertion(owl:Thing <urn:t#john>)\n"
            + "DataPropertyAssertion(<urn:t#p> <urn:t#john> \"0\"^^xsd:int)\n)";
        test("WebOnt_I5_8_010", TestClasses.POSITIVE_IMPL,
            "0 is the only xsd:nonNegativeInteger which is also an xsd:nonPositiveInteger.");
    }

    @Test
    void testWebOnt_I5_8_008() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/premises008\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:DatatypeProperty rdf:ID=\"p\">\n"
                + "    <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#short\" />\n"
                + "    <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#unsignedInt\" />"
                + "</owl:DatatypeProperty></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions008\" >\n<owl:Ontology/>\n"
                + "  <owl:DatatypeProperty rdf:about=\"premises008#p\">\n"
                + "    <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#unsignedShort\" /></owl:DatatypeProperty></rdf:RDF>";
        // TODO to make this work, the datatype reasoner must be able to infer
        // short and unsigned int equivalent unsigned short
        test("WebOnt_I5_8_008", TestClasses.POSITIVE_IMPL,
            "-1 is an xsd:short that is not an xsd:unsignedShort; 100000 is an xsd:unsignedInt that is not an xsd:unsignedShort; but there are no xsd:unsignedShort which are neither xsd:short nor xsd:unsignedInt");
    }

    @Test
    void testWebOnt_someValuesFrom_003() {
        premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" + "Ontology(\n"
            + "Declaration(Class(<urn:person>))\n"
            + "EquivalentClasses(<urn:person> ObjectSomeValuesFrom(<urn:parent> <urn:person>))\n"
            + "Declaration(ObjectProperty(<urn:parent>))\n"
            + "ClassAssertion(<urn:person> <urn:fred>))";
        conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" + "Ontology(\n"
            + "Declaration(ObjectProperty(<urn:parent>))\n" +
            // "Declaration(ObjectProperty(<urn:parent>))\n"+
            // "ClassAssertion(owl:Thing <urn:fred>)\n"+
            "ObjectPropertyAssertion(<urn:parent> <urn:fred> _:genid2)\n"
            + "ClassAssertion(owl:Thing _:genid3)\n" + "ClassAssertion(owl:Thing _:genid2)\n"
            + "ObjectPropertyAssertion(<urn:parent> _:genid2 _:genid3))";
        // XXX I do not understand these blank nodes used as existential
        // variables
        test("WebOnt_someValuesFrom_003", TestClasses.POSITIVE_IMPL,
            "A simple infinite loop for implementors to avoid.");
    }

    @Test
    void testsomevaluesfrom2bnode() throws OWLOntologyCreationException {
        // XXX I do not understand these blank nodes used as existential
        // variables
        OWLOntology o = m.createOntology();
        OWLObjectProperty p = df.getOWLObjectProperty(IRI.create("urn:p"));
        OWLNamedIndividual a = df.getOWLNamedIndividual(IRI.create("urn:a"));
        OWLObjectSomeValuesFrom c = df.getOWLObjectSomeValuesFrom(p, df.getOWLThing());
        m.addAxiom(o, df.getOWLClassAssertionAxiom(c, a));
        OWLReasoner r = factory().createReasoner(o);
        assertTrue(r.isEntailed(
            df.getOWLObjectPropertyAssertionAxiom(p, a, df.getOWLAnonymousIndividual())));
    }
}
