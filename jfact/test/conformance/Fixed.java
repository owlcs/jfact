package conformance;

import static org.junit.Assert.*;
import static org.semanticweb.owlapi.api.test.OWLFunctionalSyntaxFactory.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

@SuppressWarnings("javadoc")
public class Fixed {

    @Test
    public void testConsistent_but_all_unsat() {
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
        r.setReasonerFactory(Factory.factory());
        r.run();
    }




    @Test
    public void testPlus_and_Minus_Zero_Integer() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\nDeclaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp DataOneOf(\"0\"^^xsd:integer))\n  ) \n"
                + "  ClassAssertion(:A :a)\n  ClassAssertion( DataSomeValuesFrom(:dp DataOneOf(\"-0\"^^xsd:integer)) :a\n  )\n)";
        String conclusion = "";
        String id = "Plus_and_Minus_Zero_Integer";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "For integers 0 and -0 are the same value, so the ontology is consistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        // r.getConfiguration().setLoggingActive(true);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testEqual() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = NamedIndividual(IRI("urn:test:x"));
        OWLNamedIndividual y = NamedIndividual(IRI("urn:test:y"));
        OWLDataProperty p = DataProperty(IRI("urn:test:p"));
        OWLLiteral date = Literal("2008-07-08T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        m.addAxiom(o, DataPropertyAssertion(p, x, date));
        m.addAxiom(o, DataPropertyAssertion(p, y, date));
        m.addAxiom(o, FunctionalDataProperty(p));
        m.addAxiom(o, SameIndividual(x, y));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertTrue("Ontology was supposed to be consistent!\n" + o.getLogicalAxioms(),
                r.isConsistent());
    }

    @Test
    public void testDifferent() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = NamedIndividual(IRI("urn:test:x"));
        OWLNamedIndividual y = NamedIndividual(IRI("urn:test:y"));
        OWLDataProperty p = DataProperty(IRI("urn:test:p"));
        OWLLiteral date1 = Literal("2008-07-08T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        OWLLiteral date2 = Literal("2008-07-10T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        m.addAxiom(o, DataPropertyAssertion(p, x, date1));
        m.addAxiom(o, DataPropertyAssertion(p, y, date2));
        m.addAxiom(o, FunctionalDataProperty(p));
        m.addAxiom(o, SameIndividual(x, y));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertFalse("Ontology was supposed to be inconsistent!\n" + o.getLogicalAxioms(),
                r.isConsistent());
    }

    @Test
    public void testReasoner6() throws OWLOntologyCreationException,
            OWLOntologyStorageException {
        OWLOntologyManager mngr = OWLManager.createOWLOntologyManager();
        OWLOntology ont = mngr.createOntology();
        OWLReasonerFactory fac = Factory.factory();
        OWLReasoner r = fac.createReasoner(ont);
        System.out.println("TopObjectPropertyTest.testReasoner6() "
                + r.getBottomDataPropertyNode().getEntities());
        assertEquals(1, r.getBottomDataPropertyNode().getEntities().size());
    }

    @Test
    public void testContradicting_datatype_Restrictions() {
        String premise = "Prefix(:=<http://example.org/>)\nPrefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp DataOneOf(\"3\"^^xsd:integer \"4\"^^xsd:integer))) \n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp DataOneOf(\"2\"^^xsd:integer \"3\"^^xsd:integer)))\n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp DatatypeRestriction(xsd:integer xsd:minInclusive \"4\"^^xsd:integer)))\n"
                + "  ClassAssertion(:A :a))";
        String conclusion = "";
        String id = "Contradicting_datatype_Restrictions";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a is in A and thus must have a dp filler that is an integer >= 4. Furthermore the dp fillers must be in the set {3, 4} and in the set {2, 3}. Although 3 is in both sets, 3 is not >= 4, which causes the inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testDatatype_Float_Discrete_001() throws OWLOntologyCreationException {
        // XXX old test appears to use the wrong value...
        // String premise = "<?xml version=\"1.0\"?>\n"
        // + "<rdf:RDF\n"
        // +
        // "  xml:base  = \"http://example.org/ontology/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd = \"http://www.w3.org/2001/XMLSchema#\" >\n"
        // + "\n"
        // + "<owl:Ontology/>\n"
        // + "\n"
        // + "<owl:DatatypeProperty rdf:about=\"dp\" />\n"
        // + "\n"
        // + "<rdf:Description rdf:about=\"a\">\n"
        // + "  <rdf:type>\n"
        // + "    <owl:Restriction>\n"
        // + "      <owl:onProperty rdf:resource=\"dp\" />\n"
        // + "      <owl:someValuesFrom>\n"
        // + "        <rdfs:Datatype>\n"
        // +
        // "          <owl:onDatatype rdf:resource=\"http://www.w3.org/2001/XMLSchema#float\" />\n"
        // + "          <owl:withRestrictions rdf:parseType=\"Collection\">\n"
        // + "            <rdf:Description>\n"
        // +
        // "              <xsd:minExclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">0.0</xsd:minExclusive></rdf:Description>\n"
        // + "            <rdf:Description>\n"
        // +
        // "              <xsd:maxExclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">1.401298464324817e-45</xsd:maxExclusive></rdf:Description></owl:withRestrictions></rdfs:Datatype></owl:someValuesFrom></owl:Restriction></rdf:type>\n"
        // + "</rdf:Description>\n" + "</rdf:RDF>";
        // String conclusion = "";
        // String id = "Datatype_Float_Discrete_001";
        // TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        // String d =
        // "The value space of xsd:float is discrete, shown with range defined on 0x00000000 and 0x00000001";
        // JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        // r.setReasonerFactory(Factory.factory());
        // r.printPremise();
        // r.getConfiguration().setLoggingActive(true);
        // r.run();
        OWLDataProperty dp = DataProperty(IRI("http://example.org/ontology/dp"));
        OWLDatatype f = Datatype(OWL2Datatype.XSD_FLOAT.getIRI());
        OWLAxiom ax1 = Declaration(dp);
        OWLLiteral f0 = Literal(0F);
        OWLLiteral f1 = Literal(Float.MIN_NORMAL);
        OWLFacetRestriction min = FacetRestriction(OWLFacet.MIN_EXCLUSIVE, f0);
        OWLFacetRestriction max = FacetRestriction(OWLFacet.MAX_EXCLUSIVE, f1);
        OWLDataSomeValuesFrom superClass = DataSomeValuesFrom(dp,
                DatatypeRestriction(f, min, max));
        OWLClass c = Class(IRI("http://example.org/ontology/c"));
        OWLAxiom ax3 = SubClassOf(c, superClass);
        OWLAxiom ax2 = ClassAssertion(c,
                NamedIndividual(IRI("http://example.org/ontology/a")));
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology(
                new HashSet<OWLAxiom>(Arrays.asList(ax1, ax2, ax3)));
        // JFactReasonerConfiguration config = new JFactReasonerConfiguration();
        // config.setLoggingActive(true);
        // OWLReasoner r = Factory.factory().createReasoner(o, config);
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertFalse(r.isConsistent());
    }

    @Ignore
    @Test
    public void testConsistent_owl_real_range_with_DataOneOf() {
        // XX integers, float and reals do not share a value space
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
        r.getConfiguration().setLoggingActive(true);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
    @Test
    public void testWebOnt_I5_8_010() {
        String premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "Declaration(DataProperty(<urn:t#p>))\n"
                + "DataPropertyRange(<urn:t#p> xsd:nonNegativeInteger)\n"
                + "ClassAssertion(DataSomeValuesFrom(<urn:t#p> xsd:nonPositiveInteger) <urn:t#john>)\n)";
        String conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Ontology(\nDeclaration(DataProperty(<urn:t#p>))\n"
                + "ClassAssertion(owl:Thing <urn:t#john>)\n"
                + "DataPropertyAssertion(<urn:t#p> <urn:t#john> \"0\"^^xsd:int)\n)";
        String id = "WebOnt_I5_8_010";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "0 is the only <code>xsd:nonNegativeInteger</code> which is\n"
                + "also an <code>xsd:nonPositiveInteger</code>.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        // r.getConfiguration().setLoggingActive(true);
        r.run();
    }

    @Test
    public void testBetween() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = NamedIndividual(IRI("urn:test:x"));
        OWLClass c = Class(IRI("urn:test:c"));
        OWLDataProperty p = DataProperty(IRI("urn:test:p"));
        OWLLiteral date1 = Literal("2008-07-08T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        OWLLiteral date3 = Literal("2008-07-09T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        OWLLiteral date2 = Literal("2008-07-10T20:44:11.656+01:00",
                OWL2Datatype.XSD_DATE_TIME);
        OWLDataRange range = DatatypeRestriction(
                Datatype(OWL2Datatype.XSD_DATE_TIME.getIRI()),
                FacetRestriction(OWLFacet.MIN_INCLUSIVE, date1),
                FacetRestriction(OWLFacet.MAX_INCLUSIVE, date2));
        OWLClassExpression psome = DataSomeValuesFrom(p, range);
        m.addAxiom(o, EquivalentClasses(c, psome));
        m.addAxiom(o, DataPropertyAssertion(p, x, date3));
        m.addAxiom(o, FunctionalDataProperty(p));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertTrue(r.isConsistent());
        assertTrue("x was supposed to be an instance of c!\n" + o.getLogicalAxioms(),
                r.isEntailed(ClassAssertion(c, x)));
    }

    @Test
    public void testdatatype_restriction_min_max_inconsistency() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp DatatypeRestriction(xsd:integer xsd:minInclusive \"18\"^^xsd:integer))) \n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp DatatypeRestriction(xsd:integer xsd:maxInclusive \"10\"^^xsd:integer))\n)\n"
                + "  ClassAssertion(:A :a))";
        String conclusion = "";
        String id = "datatype_restriction_min_max_inconsistency";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a is supposed to have an integer dp-successor >= 18, but all dp-successors must be <= 10, which is impossible.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        // r.getConfiguration().setLoggingActive(true);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testfunctionality_clash() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:hasAge))\n"
                + "  FunctionalDataProperty(:hasAge) \n"
                + "  ClassAssertion(DataHasValue(:hasAge \"18\"^^xsd:integer) :a) \n"
                + "  ClassAssertion(DataHasValue(:hasAge \"19\"^^xsd:integer) :a))";
        String conclusion = "";
        String id = "functionality_clash";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The property hasAge is functional, but the individual a has two distinct hasAge fillers.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testinconsistent_datatypes() {
        String premise = "Prefix(:=<http://example.org/>)\nPrefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\nOntology(\n"
                + "  Declaration(NamedIndividual(:a))\nDeclaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp xsd:string)) \n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp xsd:integer)) \n"
                + "  ClassAssertion(:A :a))";
        String conclusion = "";
        String id = "inconsistent_datatypes";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a is in the extension of the class A and is thus required to have a dp-successor that is an integer and at the same time all dp-successors are required to be strings, which causes the inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testone_two() {
        // TODO changed to fix it
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

    @Test
    public void testrdfbased_sem_bool_intersection_inst_comp() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                // end added
                + "  <ex:x rdf:about=\"http://www.example.org#z\"><rdf:type rdf:resource=\"http://www.example.org#y\"/></ex:x>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                // added
                + "<owl:equivalentClass><owl:Class>"
                // end added
                + "    <owl:intersectionOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"http://www.example.org#x\"/><rdf:Description rdf:about=\"http://www.example.org#y\"/></owl:intersectionOf>\n"
                // added
                + "</owl:Class></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                // end added
                + "  <ex:c rdf:about=\"http://www.example.org#z\"/>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_bool_intersection_inst_comp";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An individual, which is an instance of every component class of an intersection, is an instance of the intersection class expression.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_bool_intersection_term() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                // added
                + "<rdfs:subClassOf><owl:Class>"
                // end added
                + "    <owl:intersectionOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"http://www.example.org#x\"/><rdf:Description rdf:about=\"http://www.example.org#y\"/></owl:intersectionOf>\n"
                // added
                + "</owl:Class></rdfs:subClassOf>"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#x\"/><rdfs:subClassOf rdf:resource=\"http://www.example.org#y\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_bool_intersection_term";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If a class is an intersection of other classes, then the original class is a subclass of each of the other classes.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_bool_union_inst_comp() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <ex:x rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                // added
                + "<owl:equivalentClass><owl:Class>"
                // end added
                + "    <owl:unionOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"http://www.example.org#x\"/><rdf:Description rdf:about=\"http://www.example.org#y\"/></owl:unionOf>\n"
                // added
                + "</owl:Class></owl:equivalentClass>" +
                // end added
                "  </rdf:Description>\n</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                // end added
                + "  <ex:c rdf:about=\"http://www.example.org#z\"/>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_bool_union_inst_comp";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An individual, which is an instance of one of the component classes of a union, is an instance of the union class expression.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_bool_union_term() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\">\n"
                // added
                + "<owl:equivalentClass><owl:Class>"
                // end added
                + "    <owl:unionOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"http://www.example.org#x\"/><rdf:Description rdf:about=\"http://www.example.org#y\"/></owl:unionOf>\n"
                // added
                + "</owl:Class></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#y\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#c\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#y\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_bool_union_term";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If a class is a union of other classes, then each of the other classes are subclasses of the original class.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_char_functional_inst() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <owl:FunctionalProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\"><ex:p rdf:resource=\"http://www.example.org#y1\"/><ex:p rdf:resource=\"http://www.example.org#y2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#y1\"><owl:sameAs rdf:resource=\"http://www.example.org#y2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_char_functional_inst";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For two triples with the same functional property as their predicates and with the same subject, the objects are the same.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_enum_inst_included() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#e\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#e\"><owl:oneOf rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"http://www.example.org#x\"/><rdf:Description rdf:about=\"http://www.example.org#y\"/></owl:oneOf></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#e\"/>\n"
                // end added
                + "  <ex:e rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <ex:e rdf:about=\"http://www.example.org#y\"/>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_enum_inst_included";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If a class defines an enumeration class expression from two individuals, than both individuals are instances of the class.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_different_irrflxv() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "<owl:Thing rdf:about=\"http://www.example.org#x\"/>\n"
                + "<owl:Thing rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\"><owl:sameAs rdf:resource=\"http://www.example.org#x\"/></rdf:Description>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\"><owl:differentFrom rdf:resource=\"http://www.example.org#z\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_different_irrflxv";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Diversity of two individuals is irreflexive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_disclass_irrflxv() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#fake\"/>\n"
                + "  <owl:Thing rdf:about=\"http://www.example.org#x\"/>\n"
                // end added
                + "  <ex:c rdf:about=\"http://www.example.org#x\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#c\"><owl:disjointWith rdf:resource=\"http://www.example.org#fake\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#fake\"><owl:equivalentClass rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_disclass_irrflxv";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Disjointness of two non-empty classes is irreflexive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_disprop_eqprop() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#s2\"><ex:p2 rdf:resource=\"http://www.example.org#o2\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s1\"><ex:p1 rdf:resource=\"http://www.example.org#o1\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\"><owl:equivalentProperty rdf:resource=\"http://www.example.org#p2\"/><owl:propertyDisjointWith rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_disprop_eqprop";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Two non-empty properties cannot both be equivalent and disjoint.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_disprop_inst() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#s\"><ex:p1 rdf:resource=\"http://www.example.org#o\"/><ex:p2 rdf:resource=\"http://www.example.org#o\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p1\"><owl:propertyDisjointWith rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_disprop_inst";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Triples with disjoint properties as their predicates have different subjects or different objects.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_disprop_irrflxv() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#q\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#s\"><ex:p rdf:resource=\"http://www.example.org#o\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#q\"><owl:equivalentProperty rdf:resource=\"http://www.example.org#p\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\"><owl:propertyDisjointWith rdf:resource=\"http://www.example.org#q\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_eqdis_disprop_irrflxv";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "Disjointness of two non-empty properties is irreflexive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_eqclass_subclass_1() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#c1\"><owl:equivalentClass rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#c2\"><rdfs:subClassOf><rdf:Description rdf:about=\"http://www.example.org#c1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></rdfs:subClassOf></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_eqclass_subclass_1";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Two equivalent classes are sub classes of each other.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_sameas_subst() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#s2\"><owl:sameAs><rdf:Description rdf:about=\"http://www.example.org#s1\"><ex:p1 rdf:resource=\"http://www.example.org#o1\"/></rdf:Description></owl:sameAs></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#o2\"><owl:sameAs rdf:resource=\"http://www.example.org#o1\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p2\"><owl:sameAs rdf:resource=\"http://www.example.org#p1\"/></rdf:Description>\n"
                +
                // added
                "  <rdf:Description rdf:about=\"http://www.example.org#p2\"><owl:equivalentProperty rdf:resource=\"http://www.example.org#p1\"/></rdf:Description>\n"
                +
                // end added
                "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#s2\"><ex:p1 rdf:resource=\"http://www.example.org#o1\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s1\"><ex:p2 rdf:resource=\"http://www.example.org#o1\"/><ex:p1 rdf:resource=\"http://www.example.org#o2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_sameas_subst";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equality of two individuals allows for substituting the subject, predicate and object of an RDF triple by an equal individual.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_sameas_sym() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "<owl:Thing rdf:about=\"http://www.example.org#x\"/>\n"
                + "<owl:Thing rdf:about=\"http://www.example.org#y\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\"><owl:sameAs rdf:resource=\"http://www.example.org#y\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "<owl:Thing rdf:about=\"http://www.example.org#x\"/>\n"
                + "<owl:Thing rdf:about=\"http://www.example.org#y\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#y\"><owl:sameAs rdf:resource=\"http://www.example.org#x\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_sameas_sym";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equality of two individuals is symmetrical.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_eqdis_sameas_trans() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\"><owl:sameAs><rdf:Description rdf:about=\"http://www.example.org#y\"><owl:sameAs rdf:resource=\"http://www.example.org#z\"/></rdf:Description></owl:sameAs></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x\"><owl:sameAs rdf:resource=\"http://www.example.org#z\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_eqdis_sameas_trans";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "Equality of two individuals is transitive.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_ndis_alldisjointproperties_fw() {
        // XXX test modified because of ontology not compliant with OWL 2
        // XXX without declarations, some properties default to datatype
        // properties and some to annotation properties
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p3\"/>\n"
                // end added
                + "  <owl:AllDisjointProperties rdf:about=\"http://www.example.org#z\"><owl:members rdf:parseType=\"Collection\"><rdf:Description rdf:about=\"http://www.example.org#p1\"/><rdf:Description rdf:about=\"http://www.example.org#p2\"/><rdf:Description rdf:about=\"http://www.example.org#p3\"/></owl:members></owl:AllDisjointProperties>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#s\"><ex:p1 rdf:resource=\"http://www.example.org#o\"/><ex:p2 rdf:resource=\"http://www.example.org#o\"/></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_ndis_alldisjointproperties_fw";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "All the members of an owl:AllDisjointProperties construct are mutually disjoint properties.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_npa_dat_fw() {
        // XXX test modified because of ontology not compliant with OWL 2
        // NegativeDataPropertyAssertion( DPE a lt ) _:x rdf:type
        // owl:NegativePropertyAssertion .
        // _:x owl:sourceIndividual T(a) .
        // _:x owl:assertionProperty T(DPE) .
        // _:x owl:targetValue T(lt) .
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <owl:Thing rdf:about=\"http://www.example.org#s\"/>\n"
                + "  <owl:DatatypeProperty rdf:about=\"http://www.example.org#p\"/><rdf:Description rdf:about=\"http://www.example.org#s\"><ex:p>data</ex:p></rdf:Description>\n"
                // end added
                + "  <owl:NegativePropertyAssertion><owl:sourceIndividual rdf:resource=\"http://www.example.org#s\"/><owl:assertionProperty rdf:resource=\"http://www.example.org#p\"/><owl:targetValue>data</owl:targetValue></owl:NegativePropertyAssertion></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_npa_dat_fw";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "A negative data property assertion DNPA(s p \"data\") must not occur together with the corresponding positive data property assertion s p \"data\".";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_rdfs_domain_cond() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#u\"><ex:p rdf:resource=\"http://www.example.org#v\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\"><rdfs:domain rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                // end added
                + "  <ex:c rdf:about=\"http://www.example.org#u\"/>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_rdfs_domain_cond";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The left hand side individual in a given triple is entailed to be an instance of the domain of the predicate.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_rdfs_range_cond() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#u\"><ex:p rdf:resource=\"http://www.example.org#v\"/></rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#p\"><rdfs:range rdf:resource=\"http://www.example.org#c\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                // end added
                + "  <ex:c rdf:about=\"http://www.example.org#v\"/>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_rdfs_range_cond";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "The right hand side individual in a given triple is entailed to be an instance of the range of the predicate.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_restrict_allvalues_cmp_class() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:allValuesFrom><rdf:Description rdf:about=\"http://www.example.org#c1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></owl:allValuesFrom><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:allValuesFrom rdf:resource=\"http://www.example.org#c2\"/><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_allvalues_cmp_class";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A universal restriction on some property and some class is a sub class of another universal restriction on the same property but on a super class.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_restrict_allvalues_cmp_prop() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:allValuesFrom rdf:resource=\"http://www.example.org#c\"/><owl:onProperty><rdf:Description rdf:about=\"http://www.example.org#p1\"><rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></owl:onProperty>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:allValuesFrom rdf:resource=\"http://www.example.org#c\"/><owl:onProperty rdf:resource=\"http://www.example.org#p2\"/>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#x1\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_allvalues_cmp_prop";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A universal restriction on some property and some class is a sub class of another universal restriction on the same class but on a sub property.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_restrict_allvalues_inst_obj() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "<owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <ex:z rdf:about=\"http://www.example.org#w\"><ex:p rdf:resource=\"http://www.example.org#x\"/></ex:z>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                // added
                + "<rdfs:subClassOf><owl:Restriction>"
                // end added
                + "    <owl:allValuesFrom rdf:resource=\"http://www.example.org#c\"/><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                // added
                + "</owl:Restriction></rdfs:subClassOf>"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                // end added
                + "  <ex:c rdf:about=\"http://www.example.org#x\"/>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_restrict_allvalues_inst_obj";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If an individual w is an instance of the universal restriction on property p and class c, then for any triple w p x follows that x is an instance of c.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_restrict_hasvalue_cmp_prop() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:hasValue rdf:resource=\"http://www.example.org#v\"/><owl:onProperty><rdf:Description rdf:about=\"http://www.example.org#p1\"><rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></owl:onProperty>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:hasValue rdf:resource=\"http://www.example.org#v\"/><owl:onProperty rdf:resource=\"http://www.example.org#p2\"/>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_hasvalue_cmp_prop";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A has-value restriction on some property and some value is a sub class of another has-value restriction on the same value but on a super property.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_restrict_hasvalue_inst_obj() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "<owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                // TODO this is a bug, should not be needed by the reasoner
                + "<owl:Thing rdf:about=\"http://www.example.org#u\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <ex:z rdf:about=\"http://www.example.org#w\"/>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:hasValue rdf:resource=\"http://www.example.org#u\"/><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#w\"><ex:p rdf:resource=\"http://www.example.org#u\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_hasvalue_inst_obj";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If an individual w is an instance of the has-value restriction on property p to value u, then the triple w p u can be entailed.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

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
        boolean entailed = r.isEntailed(ass);
        assertTrue(entailed);
    }

    @Test
    public void testrdfbased_sem_restrict_hasvalue_inst_subj() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:hasValue rdf:resource=\"http://www.example.org#u\"/><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#w\"><ex:p rdf:resource=\"http://www.example.org#u\"/></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                // end added
                + "  <ex:z rdf:about=\"http://www.example.org#w\"/>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_restrict_hasvalue_inst_subj";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For a triple w p u, the individual w is an instance of the has-value restriction on p to u.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_restrict_maxcard_inst_obj_one() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <ex:z rdf:about=\"http://www.example.org#w\"><ex:p rdf:resource=\"http://www.example.org#x1\"/><ex:p rdf:resource=\"http://www.example.org#x2\"/></ex:z>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                // added
                + "  <rdfs:subClassOf>\n"
                + "  <owl:Restriction>\n"
                // end added
                + "    <owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxCardinality><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                // added
                + "  </owl:Restriction>\n" + "  </rdfs:subClassOf>\n"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\"><owl:sameAs rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_maxcard_inst_obj_one";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If an individual w is an instance of the max-1-cardinality restriction on property p, and if there are triples w p x1 and w p x2, then x1 equals x2.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_restrict_maxcard_inst_obj_zero() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "<owl:Class rdf:about=\"http://www.example.org#z\"/>"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>"
                // end add
                + "  <ex:z rdf:about=\"http://www.example.org#w\"><ex:p rdf:resource=\"http://www.example.org#x\"/></ex:z>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                // added
                + "<rdfs:subClassOf><rdf:Description>"
                + "<rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Restriction\"/>"
                // end add
                + "    <owl:maxCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</owl:maxCardinality><owl:onProperty rdf:resource=\"http://www.example.org#p\"/></rdf:Description>\n</rdfs:subClassOf></rdf:Description></rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_restrict_maxcard_inst_obj_zero";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "If an individual w is an instance of the max-0-cardinality restriction on property p, then there cannot be any triple w p x.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_restrict_maxqcr_inst_obj_one() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <owl:Thing rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Thing rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <ex:z rdf:about=\"http://www.example.org#w\"><ex:p><ex:c rdf:about=\"http://www.example.org#x1\"/></ex:p><ex:p><ex:c rdf:about=\"http://www.example.org#x2\"/></ex:p></ex:z>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:maxQualifiedCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">1</owl:maxQualifiedCardinality><owl:onProperty rdf:resource=\"http://www.example.org#p\"/><owl:onClass rdf:resource=\"http://www.example.org#c\"/>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\"><owl:sameAs rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_maxqcr_inst_obj_one";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "If an individual w is an instance of the max-1-QCR on property p to class c, and if there are triples w p x1 and w p x2, with x1 and x2 being in c, then x1 equals x2.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_restrict_maxqcr_inst_obj_zero() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <ex:z rdf:about=\"http://www.example.org#w\"><ex:p><ex:c rdf:about=\"http://www.example.org#x\"/></ex:p></ex:z>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:maxQualifiedCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</owl:maxQualifiedCardinality><owl:onProperty rdf:resource=\"http://www.example.org#p\"/><owl:onClass rdf:resource=\"http://www.example.org#c\"/>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "rdfbased_sem_restrict_maxqcr_inst_obj_zero";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "If an individual w is an instance of the max-0-QCR on property p to class c, then there cannot be any triple w p x with x in c.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_restrict_somevalues_cmp_class() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#c1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                // added
                + "<rdfs:subClassOf><owl:Restriction>"
                // end added
                + "    <owl:someValuesFrom><rdf:Description rdf:about=\"http://www.example.org#c1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#c2\"/></rdf:Description></owl:someValuesFrom><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                // added
                + "</owl:Restriction></rdfs:subClassOf>"
                // end added
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:someValuesFrom rdf:resource=\"http://www.example.org#c2\"/><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_somevalues_cmp_class";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An existential restriction on some property and some class is a sub class of another existential restriction on the same property but on a super class.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_restrict_somevalues_cmp_prop() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p1\"/>\n"
                + "  <owl:ObjectProperty rdf:about=\"http://www.example.org#p2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:someValuesFrom rdf:resource=\"http://www.example.org#c\"/><owl:onProperty><rdf:Description rdf:about=\"http://www.example.org#p1\"><rdfs:subPropertyOf rdf:resource=\"http://www.example.org#p2\"/></rdf:Description></owl:onProperty>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#x2\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:someValuesFrom rdf:resource=\"http://www.example.org#c\"/><owl:onProperty rdf:resource=\"http://www.example.org#p2\"/>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>"
                // end added
                + "  </rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Class rdf:about=\"http://www.example.org#x1\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#x2\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#x1\"><rdfs:subClassOf rdf:resource=\"http://www.example.org#x2\"/></rdf:Description></rdf:RDF>";
        String id = "rdfbased_sem_restrict_somevalues_cmp_prop";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "An existential restriction on some property and some class is a sub class of another existential restriction on the same class but on a super property.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testrdfbased_sem_restrict_somevalues_inst_subj() {
        // XXX test modified because of ontology not compliant with OWL 2
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Thing rdf:about=\"http://www.example.org#x\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#c\"/>\n"
                + "<owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org#p\"/>\n"
                // end added
                + "  <rdf:Description rdf:about=\"http://www.example.org#z\">\n"
                // added
                + "<owl:equivalentClass><owl:Restriction>"
                // end added
                + "    <owl:someValuesFrom rdf:resource=\"http://www.example.org#c\"/><owl:onProperty rdf:resource=\"http://www.example.org#p\"/>\n"
                // added
                + "</owl:Restriction></owl:equivalentClass>\n"
                // end added
                + "  </rdf:Description>\n"
                + "  <rdf:Description rdf:about=\"http://www.example.org#w\"><ex:p><ex:c rdf:about=\"http://www.example.org#x\"/></ex:p></rdf:Description></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:ex=\"http://www.example.org#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n"
                // added
                + "  <owl:Thing rdf:about=\"http://www.example.org#w\"/>\n"
                + "  <owl:Class rdf:about=\"http://www.example.org#z\"/>\n"
                // end added
                + "  <ex:z rdf:about=\"http://www.example.org#w\"/>\n" + "</rdf:RDF>";
        String id = "rdfbased_sem_restrict_somevalues_inst_subj";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "For a triple w p x, with x being an instance of a class c, the individual w is an instance of the existential restriction on p to c.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void teststring_integer_clash() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:hasAge))\n"
                + "  DataPropertyRange(:hasAge xsd:integer)\n"
                + "  ClassAssertion(DataHasValue(:hasAge \"aString\"^^xsd:string) :a)\n"
                + ")";
        String conclusion = "";
        String id = "string_integer_clash";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The range of hasAge is integer, but a has an asserted string hasAge filler.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        // r.getConfiguration().setLoggingActive(true);
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_501() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/consistent501\" >\n"
                + "<owl:Ontology/>\n"
                + "<owl:Class rdf:ID='TorF'> \n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:ID='T'><owl:differentFrom rdf:resource='#F'/></owl:Thing><owl:Thing rdf:ID='F'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus1'/><owl:Thing rdf:about='#minus1'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus2'/><owl:Thing rdf:about='#minus2'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus3'/><owl:Thing rdf:about='#minus3'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus4'/><owl:Thing rdf:about='#minus4'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus5'/><owl:Thing rdf:about='#minus5'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus6'/><owl:Thing rdf:about='#minus6'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus7'/><owl:Thing rdf:about='#minus7'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus8'/><owl:Thing rdf:about='#minus8'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus9'/><owl:Thing rdf:about='#minus9'/></owl:oneOf></owl:Class>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus1'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#plus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#minus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#plus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#plus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#plus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#minus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#minus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#plus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#plus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#plus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#minus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#minus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#minus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus1'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#plus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#minus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#plus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#plus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_501";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "This is the classic 3 SAT problem.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testWebOnt_description_logic_502() {
        String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent502#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/description-logic/inconsistent502\" >\n"
                + "<owl:Ontology/>\n"
                + "<owl:Class rdf:ID='TorF'> \n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:ID='T'><owl:differentFrom rdf:resource='#F'/></owl:Thing><owl:Thing rdf:ID='F'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus1'/><owl:Thing rdf:about='#minus1'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus2'/><owl:Thing rdf:about='#minus2'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus3'/><owl:Thing rdf:about='#minus3'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus4'/><owl:Thing rdf:about='#minus4'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus5'/><owl:Thing rdf:about='#minus5'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus6'/><owl:Thing rdf:about='#minus6'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus7'/><owl:Thing rdf:about='#minus7'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus8'/><owl:Thing rdf:about='#minus8'/></owl:oneOf>\n"
                + "  <owl:oneOf rdf:parseType='Collection'><owl:Thing rdf:about='#plus9'/><owl:Thing rdf:about='#minus9'/></owl:oneOf></owl:Class>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#minus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#plus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus1'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#plus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#minus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#minus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#plus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#minus1'/><rdf:Description rdf:about='#plus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#plus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#minus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#plus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#minus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#plus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus2'/><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#minus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus7'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#plus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#plus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#plus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#plus7'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#plus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus5'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus6'/><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus1'/><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus9'/><rdf:Description rdf:about='#minus8'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#plus3'/><rdf:Description rdf:about='#minus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#plus6'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#plus5'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#plus4'/><rdf:Description rdf:about='#plus3'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus8'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#plus2'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus5'/><rdf:Description rdf:about='#minus2'/><rdf:Description rdf:about='#minus9'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#minus3'/><rdf:Description rdf:about='#minus4'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#minus9'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus4'/><rdf:Description rdf:about='#minus1'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "<rdf:Description rdf:about='#T'><rdf:type><owl:Class><owl:oneOf rdf:parseType='Collection'><rdf:Description rdf:about='#plus6'/><rdf:Description rdf:about='#minus7'/><rdf:Description rdf:about='#minus8'/></owl:oneOf></owl:Class></rdf:type></rdf:Description>\n"
                + "</rdf:RDF>";
        String conclusion = "";
        String id = "WebOnt_description_logic_502";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "This is the classic 3 SAT problem.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testBetweenDate() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = NamedIndividual(IRI("urn:test:x"));
        OWLClass c = Class(IRI("urn:test:c"));
        OWLDataProperty p = DataProperty(IRI("urn:test:p"));
        OWLDatatype type = Datatype(OWL2Datatype.XSD_DATE_TIME.getIRI());
        OWLLiteral date1 = Literal("2007-10-08T20:44:11.656+01:00", type);
        OWLLiteral date2 = Literal("2009-10-08T20:44:11.656+01:00", type);
        OWLLiteral date3 = Literal("2008-07-08T20:44:11.656+01:00", type);
        OWLDataRange range = DatatypeRestriction(type,
                FacetRestriction(OWLFacet.MIN_INCLUSIVE, date1),
                FacetRestriction(OWLFacet.MAX_INCLUSIVE, date2));
        OWLClassExpression psome = DataSomeValuesFrom(p, range);
        m.addAxiom(o, EquivalentClasses(c, psome));
        m.addAxiom(o, DataPropertyAssertion(p, x, date3));
        m.addAxiom(o, FunctionalDataProperty(p));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertTrue(r.isEntailed(ClassAssertion(c, x)));
    }

    @Test
    public void testBetweenNumbers() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLNamedIndividual x = NamedIndividual(IRI("urn:test:x"));
        OWLClass c = Class(IRI("urn:test:c"));
        OWLDataProperty p = DataProperty(IRI("urn:test:p"));
        OWLDatatype type = Datatype(XSDVocabulary.INT.getIRI());
        OWLLiteral date1 = Literal("2007", type);
        // Literal("2008-07-08", type);
        OWLLiteral date2 = Literal("2009", type);
        // Literal("2008-07-10", type);
        OWLLiteral date3 = Literal("2008", type);
        // Literal("2008-07-09", type);
        OWLDataRange range = DatatypeRestriction(type,
                FacetRestriction(OWLFacet.MIN_INCLUSIVE, date1),
                FacetRestriction(OWLFacet.MAX_INCLUSIVE, date2));
        OWLClassExpression psome = DataSomeValuesFrom(p, range);
        m.addAxiom(o, EquivalentClasses(c, psome));
        m.addAxiom(o, DataPropertyAssertion(p, x, date3));
        m.addAxiom(o, FunctionalDataProperty(p));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertTrue(r.isEntailed(ClassAssertion(c, x)));
    }

    @Test
    public void testContradicting_dateTime_restrictions_programmatic()
            throws OWLOntologyStorageException, OWLOntologyCreationException {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        OWLClass A = Class("http://example.org/A");
        OWLNamedIndividual a = NamedIndividual(IRI("http://example.org/a"));
        OWLDataProperty dp = DataProperty(IRI("http://example.org/dp"));
        OWLDatatype type = Datatype(OWL2Datatype.XSD_DATE_TIME.getIRI());
        OWLLiteral lit1 = Literal("2007-10-08T20:44:11.656+01:00", type);
        OWLLiteral lit2 = Literal("2008-10-08T20:44:11.656+01:00", type);
        OWLLiteral lit3 = Literal("2008-07-08T20:44:11.656+01:00", type);
        OWLFacetRestriction min = FacetRestriction(OWLFacet.MIN_INCLUSIVE, lit3);
        OWLFacetRestriction max = FacetRestriction(OWLFacet.MAX_INCLUSIVE, lit2);
        axioms.add(Declaration(A));
        axioms.add(SubClassOf(A,
                DataAllValuesFrom(dp, DatatypeRestriction(type, max, min))));
        axioms.add(SubClassOf(A, DataHasValue(dp, lit1)));
        axioms.add(Declaration(dp));
        axioms.add(Declaration(a));
        axioms.add(ClassAssertion(A, a));
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology(axioms);
        assertFalse(Factory.factory().createReasoner(o).isConsistent());
    }

    @Test
    public void testContradicting_int_restrictions() throws OWLOntologyStorageException,
            OWLOntologyCreationException {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        OWLClass A = Class("http://example.org/A");
        OWLNamedIndividual a = NamedIndividual(IRI("http://example.org/a"));
        OWLDataProperty dp = DataProperty(IRI("http://example.org/dp"));
        OWLDatatype type = Datatype(OWL2Datatype.XSD_INT.getIRI());
        OWLLiteral lit1 = Literal("2007", type);
        OWLLiteral lit2 = Literal("2009", type);
        OWLLiteral lit3 = Literal("2008", type);
        OWLFacetRestriction min = FacetRestriction(OWLFacet.MIN_INCLUSIVE, lit3);
        OWLFacetRestriction max = FacetRestriction(OWLFacet.MAX_INCLUSIVE, lit2);
        axioms.add(Declaration(A));
        axioms.add(SubClassOf(A,
                DataAllValuesFrom(dp, DatatypeRestriction(type, max, min))));
        axioms.add(SubClassOf(A, DataHasValue(dp, lit1)));
        axioms.add(Declaration(dp));
        axioms.add(Declaration(a));
        axioms.add(ClassAssertion(A, a));
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology(axioms);
        assertFalse(Factory.factory().createReasoner(o).isConsistent());
    }

    @Test
    public void testContradicting_dateTime_restrictions()
            throws OWLOntologyStorageException, OWLOntologyCreationException {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n"
                + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A \n"
                + "    DataHasValue(:dp \"2007-10-08T20:44:11.656+01:00\"^^xsd:dateTime)) \n"
                + "  SubClassOf(:A \n"
                + "    DataAllValuesFrom(:dp DatatypeRestriction(\n"
                + "      xsd:dateTime \n"
                + "      xsd:minInclusive \"2008-07-08T20:44:11.656+01:00\"^^xsd:dateTime \n"
                + "      xsd:maxInclusive \"2008-10-08T20:44:11.656+01:00\"^^xsd:dateTime)\n"
                + "    )\n" + "  ) \n" + "  ClassAssertion(:A :a)\n" + ")";
        String conclusion = "";
        String id = "Contradicting_dateTime_restrictions";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a must have a dp filler that is a date from 2007, but the restrictions on dp allow only values from 2008, which makes the ontology inconsistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testInconsistent_Data_Complement_with_the_Restrictions() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n" + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp \n"
                + "    DataOneOf(\"3\"^^xsd:integer \"4\"^^xsd:integer))\n" + "  ) \n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp \n"
                + "    DataOneOf(\"2\"^^xsd:integer \"3\"^^xsd:integer))\n" + "  )\n"
                + "  ClassAssertion(:A :a)\n"
                + "  ClassAssertion(DataSomeValuesFrom(:dp\n"
                + "  DataComplementOf(DataOneOf(\"3\"^^xsd:integer))) :a)\n" + ")";
        String conclusion = "";
        String id = "Inconsistent_Data_Complement_with_the_Restrictions";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a must have dp fillers that are in the sets {3, 4} and {2, 3}, but at the same time 3 is not allowed as a dp filler for a, which causes the inconsistency.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testPlus_and_Minus_Zero_are_Distinct() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n"
                + "  Declaration(NamedIndividual(:Meg))\n"
                + "  Declaration(DataProperty(:numberOfChildren))\n"
                + "  DataPropertyAssertion(:numberOfChildren :Meg \"+0.0\"^^xsd:float) \n"
                + "  DataPropertyAssertion(:numberOfChildren :Meg \"-0.0\"^^xsd:float) \n"
                + "  FunctionalDataProperty(:numberOfChildren)\n" + ")";
        String conclusion = "";
        String id = "Plus_and_Minus_Zero_are_Distinct";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "For floats and double, +0.0 and -0.0 are distinct values, which contradicts the functionality for numberOfChildren.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testQualified_cardinality_boolean() {
        String premise = "Prefix( : = <http://example.org/test#> )\n"
                + "Prefix( xsd: = <http://www.w3.org/2001/XMLSchema#> )\n"
                + "\n"
                + "Ontology(<http://owl.semanticweb.org/page/Special:GetOntology/Qualified-cardinality-boolean?m=p>\n"
                + "  Declaration(NamedIndividual(:a))\n" + "  Declaration(Class(:A))\n"
                + "  Declaration(DataProperty(:dp))\n" + "\n"
                + "  SubClassOf(:A DataExactCardinality(2 :dp xsd:boolean))\n" + "\n"
                + "  ClassAssertion(:A :a)\n" + ")";
        String conclusion = "Prefix( : = <http://example.org/test#> )\n"
                + "Prefix( xsd: = <http://www.w3.org/2001/XMLSchema#> )\n"
                + "\n"
                + "Ontology(<http://owl.semanticweb.org/page/Special:GetOntology/Qualified-cardinality-boolean?m=c>\n"
                + "  Declaration(DataProperty(:dp))\n" + "\n"
                + "  DataPropertyAssertion(:dp :a \"true\"^^xsd:boolean)\n"
                + "  DataPropertyAssertion(:dp :a \"false\"^^xsd:boolean)\n" + ")";
        String id = "Qualified_cardinality_boolean";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "According to qualified cardinality restriction individual a should have two boolean values. Since there are only two boolean values, the data property assertions can be entailed.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

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

    @Test
    public void testInconsistent_Byte_Filler() {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + "Ontology(\n"
                + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp))\n" + "  Declaration(Class(:A))\n"
                + "  SubClassOf(:A DataAllValuesFrom(:dp xsd:byte))  \n"
                + "  ClassAssertion(:A :a)\n" + "  ClassAssertion(\n"
                + "    DataSomeValuesFrom(:dp DataOneOf(\"6542145\"^^xsd:integer)) :a\n"
                + "  )\n" + ")";
        String conclusion = "";
        String id = "Inconsistent_Byte_Filler";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The individual a must have the integer 6542145 as dp filler, but all fillers must also be bytes. Since 6542145 is not byte, the ontology is inconsistent.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
