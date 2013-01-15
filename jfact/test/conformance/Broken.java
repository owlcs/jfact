package conformance;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

@SuppressWarnings("javadoc")
public class Broken {
    @Test
    public void testReasoner5() throws OWLOntologyCreationException {
        OWLOntologyManager mngr = OWLManager.createOWLOntologyManager();
        OWLOntology ont = mngr.createOntology();
        OWLDataFactory df = OWLManager.getOWLDataFactory();
        OWLDataProperty dp = df.getOWLDataProperty(IRI.create("urn:test:datap1"));
        mngr.addAxiom(ont, df.getOWLDataPropertyDomainAxiom(dp, df.getOWLNothing()));
        OWLReasonerFactory fac = Factory.factory();
        OWLReasoner r = fac.createNonBufferingReasoner(ont);
        assertEquals(2, r.getBottomDataPropertyNode().getEntities().size());
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
    public void testDatatype_Float_Discrete_001() {
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "  xml:base  = \"http://example.org/ontology/\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xsd = \"http://www.w3.org/2001/XMLSchema#\" >\n"
                + "\n"
                + "<owl:Ontology/>\n"
                + "\n"
                + "<owl:DatatypeProperty rdf:about=\"dp\" />\n"
                + "\n"
                + "<rdf:Description rdf:about=\"a\">\n"
                + "  <rdf:type>\n"
                + "    <owl:Restriction>\n"
                + "      <owl:onProperty rdf:resource=\"dp\" />\n"
                + "      <owl:someValuesFrom>\n"
                + "        <rdfs:Datatype>\n"
                + "          <owl:onDatatype rdf:resource=\"http://www.w3.org/2001/XMLSchema#float\" />\n"
                + "          <owl:withRestrictions rdf:parseType=\"Collection\">\n"
                + "            <rdf:Description>\n"
                + "              <xsd:minExclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">0.0</xsd:minExclusive></rdf:Description>\n"
                + "            <rdf:Description>\n"
                + "              <xsd:maxExclusive rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">1.401298464324817e-45</xsd:maxExclusive></rdf:Description></owl:withRestrictions></rdfs:Datatype></owl:someValuesFrom></owl:Restriction></rdf:type>\n"
                + "</rdf:Description>\n" + "</rdf:RDF>";
        String conclusion = "";
        String id = "Datatype_Float_Discrete_001";
        TestClasses tc = TestClasses.valueOf("INCONSISTENCY");
        String d = "The value space of xsd:float is discrete, shown with range defined on 0x00000000 and 0x00000001";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
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
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions008\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:DatatypeProperty rdf:about=\"premises008#p\">\n"
                + "    <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#unsignedShort\" /></owl:DatatypeProperty>\n"
                + "\n" + "</rdf:RDF>";
        String id = "WebOnt_I5_8_008";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "-1 is an <code>xsd:short</code> that is not an\n"
                + "<code>xsd:unsignedShort</code>;\n"
                + "100000 is an <code>xsd:unsignedInt</code> that is not\n"
                + "an <code>xsd:unsignedShort</code>; but there are no\n"
                + "<code>xsd:unsignedShort</code> which are neither\n"
                + "<code>xsd:short</code> nor\n" + "<code>xsd:unsignedInt</code>";
        // TODO to make this work, the datatype reasoner must be able to infer
        // short and unsigned int equivalent unsigned short
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
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

    @Test
    public void testConsistent_owl_real_range_with_DataOneOf() {
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
