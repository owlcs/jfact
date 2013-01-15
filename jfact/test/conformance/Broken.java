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
