package conformance;

import static org.junit.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

@SuppressWarnings("javadoc")
public class Broken {
    @Test
    public void testBugFix() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLDataProperty p = f.getOWLDataProperty(IRI.create("urn:t:t#p"));
        OWLNamedIndividual i = f.getOWLNamedIndividual(IRI.create("urn:t:t#i"));
        m.addAxiom(o, f.getOWLDeclarationAxiom(p));
        m.addAxiom(o, f.getOWLDeclarationAxiom(i));
        OWLDataOneOf owlDataOneOf = f.getOWLDataOneOf(f.getOWLLiteral(1),
                f.getOWLLiteral(2), f.getOWLLiteral(3), f.getOWLLiteral(4));
        OWLDataOneOf owlDataOneOf2 = f.getOWLDataOneOf(f.getOWLLiteral(4),
                f.getOWLLiteral(5), f.getOWLLiteral(6));
        m.addAxiom(o, f.getOWLDataPropertyRangeAxiom(p, owlDataOneOf));
        m.addAxiom(o, f.getOWLDataPropertyRangeAxiom(p, owlDataOneOf2));
        m.addAxiom(o, f.getOWLClassAssertionAxiom(f.getOWLDataMinCardinality(1, p), i));
        OWLReasoner r = Factory.factory().createReasoner(o);
        OWLDataPropertyAssertionAxiom ass = f.getOWLDataPropertyAssertionAxiom(p, i, 4);
        boolean entailed = r.isEntailed(ass);
        assertTrue(entailed);
    }

    @Test
    public void testContradicting_dateTime_restrictions() {
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
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#short\" />\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#unsignedInt\" /></owl:DatatypeProperty></rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions008\" >\n"
                + "  <owl:Ontology/>\n"
                + "  <owl:DatatypeProperty rdf:about=\"premises008#p\">\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#unsignedShort\" /></owl:DatatypeProperty>\n"
                + "\n" + "</rdf:RDF>";
        String id = "WebOnt_I5_8_008";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "-1 is an <code>xsd:short</code> that is not an\n"
                + "<code>xsd:unsignedShort</code>;\n"
                + "100000 is an <code>xsd:unsignedInt</code> that is not\n"
                + "an <code>xsd:unsignedShort</code>; but there are no\n"
                + "<code>xsd:unsignedShort</code> which are neither\n"
                + "<code>xsd:short</code> nor\n" + "<code>xsd:unsignedInt</code>";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    @Test
    public void testWebOnt_I5_8_010() {
        // String premise = "<rdf:RDF\n"
        // + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
        // + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
        // + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
        // +
        // "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/premises010\" >\n"
        // + "  <owl:Ontology/>\n"
        // + "  <owl:DatatypeProperty rdf:ID=\"p\">\n"
        // +
        // "    <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\" />\n"
        // + "  </owl:DatatypeProperty>\n"
        // + "  <rdf:Description rdf:ID=\"john\">\n"
        // + "    <rdf:type>\n"
        // + "      <owl:Restriction>\n"
        // + "        <owl:onProperty rdf:resource=\"#p\"/>\n"
        // +
        // "        <owl:someValuesFrom rdf:resource=\"http://www.w3.org/2001/XMLSchema#nonPositiveInteger\" />\n"
        // + "      </owl:Restriction>\n"
        // + "   </rdf:type>\n"
        // + "  </rdf:Description>\n</rdf:RDF>";
        // String conclusion = "<rdf:RDF\n"
        // + "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
        // + "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
        // + "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
        // +
        // "    xmlns:first=\"http://www.w3.org/2002/03owlt/I5.8/premises010#\"\n"
        // +
        // "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions010\" >\n"
        // + "  <owl:Ontology/>\n"
        // + "  <owl:DatatypeProperty rdf:about=\"premises010#p\"/>\n"
        // + "  <owl:Thing rdf:about=\"premises010#john\">\n"
        // +
        // "    <first:p rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">0</first:p>\n"
        // + "  </owl:Thing></rdf:RDF>";
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
        String id = "WebOnt_someValuesFrom_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A simple infinite loop for implementors to avoid.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        // r.printPremise();System.out.println("WebOnt_someValuesFrom_003.testWebOnt_someValuesFrom_003()");r.printConsequence();
        r.run();
    }

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
}
