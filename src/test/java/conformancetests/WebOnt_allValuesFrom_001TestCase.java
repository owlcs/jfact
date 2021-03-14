package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.junit.Test;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class WebOnt_allValuesFrom_001TestCase extends TestBase {

    @Test
    public void testWebOnt_miscellaneous_203() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/inconsistent203#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/inconsistent203\" >\n"
                + '\n' + "  <owl:Ontology/>\n" + '\n' + "  <owl:DatatypeProperty\n"
                + "           rdf:ID=\"fp\" />\n" + "  <owl:FunctionalProperty\n"
                + "           rdf:about=\"#fp\" />\n" + "  <owl:Thing>\n"
                + "     <first:fp rdf:parseType=\"Literal\">\n" + "<br />\n"
                + "<img src=\"vn.png\" alt=\"Venn diagram\" longdesc=\"vn.html\" title=\"Venn\"></img>\n"
                + '\n' + "</first:fp>\n" + "     <first:fp rdf:parseType=\"Literal\"><br \n"
                + "></br>\n" + "<img \n" + "src=\"vn.png\" title=\n" + "\"Venn\" alt\n"
                + "=\"Venn diagram\" longdesc=\n" + "\"vn.html\" /></first:fp></owl:Thing>\n"
                + "</rdf:RDF>";
        test("WebOnt_miscellaneous_203", TestClasses.INCONSISTENCY,
            "This shows that initial whitespace in an rdf:XMLLiteral (http://www.w3.org/TR/rdf-concepts/#section-XMLLiteral) is significant within OWL.");
    }

    @Test
    public void testWebOnt_miscellaneous_202() {
        premise =
            // "<rdf:RDF\n"
            // + " xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
            // + " xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
            // + " xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
            // + " xmlns=\"http://www.w3.org/1999/xhtml\"\n"
            // +
            // "
            // xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent202#\"\n"
            // +
            // "
            // xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent202\"
            // >\n"
            // + "\n"
            // + " <owl:Ontology/>\n"
            // + " <owl:DatatypeProperty rdf:ID=\"fp\" />\n"
            // + " <owl:FunctionalProperty\n"
            // + " rdf:about=\"#fp\" />\n"
            // + " <owl:Thing>\n"
            // + " <first:fp rdf:parseType=\"Literal\"><br />\n"
            // +
            // "<img src=\"vn.png\" alt=\"Venn diagram\" longdesc=\"vn.html\"
            // title=\"Venn\"></img></first:fp>\n"
            // + " <first:fp rdf:parseType=\"Literal\"><br \n"
            // + "></br>\n" + "<img \n" + "src=\"vn.png\" title=\n"
            // + "\"Venn\" alt\n" + "=\"Venn diagram\" longdesc=\n"
            // + "\"vn.html\" /></first:fp>\n" + " </owl:Thing>\n"
            // + "</rdf:RDF>";
            "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
                + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
                + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
                + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" + "Ontology(\n"
                + "Declaration(DataProperty(<urn:fp>))\n" + "FunctionalDataProperty(<urn:fp>)\n"
                + "ClassAssertion(owl:Thing <urn:id2>)\n"
                + "DataPropertyAssertion(<urn:fp> <urn:id2> \"<br></br>\n\n"
                + "<img></img>\"^^rdf:XMLLiteral)\n"
                + "DataPropertyAssertion(<urn:fp> <urn:id2> \"<br></br>\n"
                + "<img></img>\"^^rdf:XMLLiteral)\n" + ')';
        // TODO this is silly, to pass this test the reasoner needs a lot of
        // extra processing for literals... maybe the OWL API should do this, it
        // can be useful for users in general and it's actually down to the
        // equals method for XML Literals.
        test("WebOnt_miscellaneous_202", TestClasses.CONSISTENCY,
            "This shows that insignificant whitespace in an rdf:XMLLiteral is not significant within OWL.");
    }

    @Test
    public void testWebOnt_miscellaneous_204() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:first=\"http://www.w3.org/2002/03owlt/miscellaneous/inconsistent204#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/miscellaneous/inconsistent204\" ><owl:Ontology/>"
                + "<owl:FunctionalProperty rdf:ID=\"fp\"/>\n"
                + "   <owl:DatatypeProperty rdf:about=\"#fp\"/><owl:Thing>\n"
                + "     <first:fp rdf:parseType=\"Literal\"><span xml:lang='en'><b>Good!</b></span></first:fp>\n"
                + "     <first:fp rdf:parseType=\"Literal\"><span xml:lang='en'><b>Bad!</b></span></first:fp></owl:Thing></rdf:RDF>";
        test("WebOnt_miscellaneous_204", TestClasses.INCONSISTENCY,
            "This shows a simple inconsistency depending on the datatype rdf:XMLLiteral. This file is inconsistent with a datatype map which supports rdf:XMLLiteral, and consistent otherwise.");
    }

    @Test
    public void testWebOnt_allValuesFrom_001() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/allValuesFrom/premises001#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/allValuesFrom/premises001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:ID=\"r\"><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#p\"/><owl:allValuesFrom rdf:resource=\"#c\"/></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
                + "    <owl:ObjectProperty rdf:ID=\"p\"/>\n" + "    <owl:Class rdf:ID=\"c\"/>\n"
                + "    <first:r rdf:ID=\"i\"><rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/><first:p><owl:Thing rdf:ID=\"o\" /></first:p></first:r></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:first=\"http://www.w3.org/2002/03owlt/allValuesFrom/premises001#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/allValuesFrom/conclusions001\" >\n"
                + "    <owl:Ontology/>\n"
                + "    <first:c rdf:about=\"premises001#o\"><rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/></first:c><owl:Class rdf:about=\"premises001#c\"/></rdf:RDF>";
        test("WebOnt_allValuesFrom_001", TestClasses.POSITIVE_IMPL, "A simple example.");
    }

    @Test
    public void testWebOnt_allValuesFrom_002() {
        premise = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" + "Ontology(\n"
            + "Declaration(Class(<urn:t:t#c>))\n" + "Declaration(Class(<urn:t:t#r>))\n"
            + "Declaration(ObjectProperty(<urn:t:t#p>))\n"
            + "SubClassOf(<urn:t:t#r> ObjectAllValuesFrom(<urn:t:t#p> <urn:t:t#c>))\n"
            + "ClassAssertion(<urn:t:t#r> <urn:t:t#i>)\n"
            + "ClassAssertion(owl:Thing <urn:t:t#i>)\n"
            + "ClassAssertion(owl:Thing <urn:t:t#newind>))";
        conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" + "Ontology(\n"
            + "Declaration(Class(<urn:t:t#c>))\n" + "Declaration(Class(<urn:t:t#r>))\n"
            + "Declaration(ObjectProperty(<urn:t:t#p>))\n" +
            // "ClassAssertion(owl:Thing <urn:t:t#i>)\n"+
            // "ClassAssertion(owl:Thing <urn:t:t#newind>)\n"+
            "ObjectPropertyAssertion(<urn:t:t#p> <urn:t:t#i> <urn:t:t#newind>)\n"
            + "ClassAssertion(<urn:t:t#c> <urn:t:t#newind>)\n" +
            // "ClassAssertion(owl:Thing <urn:t:t#newind>)" +
            ')';
        test("WebOnt_allValuesFrom_002", TestClasses.NEGATIVE_IMPL,
            "Another simple example; contrast with owl:someValuesFrom.");
    }

    @Test
    public void testWebOnt_I4_6_004() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I4.6/premises004\" >\n"
                + "    <owl:Ontology/>\n" + "    <owl:Class rdf:about=\"nonconclusions004#C1\">\n"
                + "       <owl:equivalentClass>\n"
                + "           <owl:Class rdf:about=\"nonconclusions004#C2\"/></owl:equivalentClass></owl:Class>\n"
                + "</rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I4.6/nonconclusions004\" >\n"
                + "    <owl:Ontology/>\n" + "    <owl:Class rdf:ID=\"C1\">\n"
                + "       <owl:sameAs>\n"
                + "           <owl:Class rdf:ID=\"C2\"/></owl:sameAs></owl:Class>\n" + "</rdf:RDF>";
        test("WebOnt_I4_6_004", TestClasses.NEGATIVE_IMPL,
            "owl:sameAs is stronger than owl:equivalentClass.");
    }

    @Test
    public void testWebOnt_I5_21_002() {
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.21/conclusions002\" >\n" + '\n'
                + "    <owl:Ontology/>\n"
                + "    <owl:Class rdf:about=\"premises002#Amphisbaenidae\">\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Agamidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Anomalepidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Emydidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Crocodylidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Gekkonidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Sphenodontidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Cordylidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Bipedidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Leptotyphlopidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Xantusiidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Loxocemidae\"/></owl:Class>\n"
                + '\n' + "    <owl:Class rdf:about=\"premises002#Agamidae\">\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Anomalepidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Emydidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Crocodylidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Gekkonidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Sphenodontidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Cordylidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Bipedidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Leptotyphlopidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Xantusiidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Loxocemidae\"/></owl:Class>\n"
                + '\n' + "    <owl:Class rdf:about=\"premises002#Anomalepidae\">\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Emydidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Crocodylidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Gekkonidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Sphenodontidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Cordylidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Bipedidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Leptotyphlopidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Xantusiidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Loxocemidae\"/></owl:Class>\n"
                + '\n' + "    <owl:Class rdf:about=\"premises002#Emydidae\">\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Crocodylidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Gekkonidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Sphenodontidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Cordylidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Bipedidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Leptotyphlopidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Xantusiidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Loxocemidae\"/></owl:Class>\n"
                + '\n' + "    <owl:Class rdf:about=\"premises002#Crocodylidae\">\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Gekkonidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Sphenodontidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Cordylidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Bipedidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Leptotyphlopidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Xantusiidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Loxocemidae\"/></owl:Class>\n"
                + '\n' + "    <owl:Class rdf:about=\"premises002#Gekkonidae\">\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Sphenodontidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Cordylidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Bipedidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Leptotyphlopidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Xantusiidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Loxocemidae\"/></owl:Class>\n"
                + '\n' + "    <owl:Class rdf:about=\"premises002#Sphenodontidae\">\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Cordylidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Bipedidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Leptotyphlopidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Xantusiidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Loxocemidae\"/></owl:Class>\n"
                + '\n' + "    <owl:Class rdf:about=\"premises002#Cordylidae\">\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Bipedidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Leptotyphlopidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Xantusiidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Loxocemidae\"/></owl:Class>\n"
                + '\n' + "    <owl:Class rdf:about=\"premises002#Bipedidae\">\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Leptotyphlopidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Xantusiidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Loxocemidae\"/></owl:Class>\n"
                + '\n' + "    <owl:Class rdf:about=\"premises002#Leptotyphlopidae\">\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Xantusiidae\"/>\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Loxocemidae\"/></owl:Class>\n"
                + '\n' + "    <owl:Class rdf:about=\"premises002#Xantusiidae\">\n"
                + "      <owl:disjointWith rdf:resource=\"premises002#Loxocemidae\"/></owl:Class>\n"
                + '\n' + "    <owl:Class rdf:about=\"premises002#Loxocemidae\"></owl:Class>\n"
                + '\n' + "</rdf:RDF>";
        premise = asString("/WebOnt_I5_21_002.owl");
        test("WebOnt_I5_21_002", TestClasses.POSITIVE_IMPL,
            "The construct used here shows how to express mutual disjointness between classes with  O(N) triples.");
    }

    @Test
    public void testWebOnt_I5_8_006() {
        premise =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.8/premises006#\" xmlns:second=\"http://www.w3.org/2002/03owlt/I5.8/conclusions006#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/premises006\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:DatatypeProperty rdf:ID=\"p\">\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#byte\" /></owl:DatatypeProperty></rdf:RDF>";
        conclusion =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:first=\"http://www.w3.org/2002/03owlt/I5.8/premises006#\" xmlns:second=\"http://www.w3.org/2002/03owlt/I5.8/conclusions006#\"\n"
                + "    xml:base=\"http://www.w3.org/2002/03owlt/I5.8/conclusions006\" >\n"
                + "  <owl:Ontology/>\n" + "  <owl:DatatypeProperty rdf:about=\"premises006#p\">\n"
                + "    <rdfs:range rdf:resource=\n"
                + "  \"http://www.w3.org/2001/XMLSchema#short\" /></owl:DatatypeProperty>\n" + '\n'
                + "</rdf:RDF>";
        test("WebOnt_I5_8_006", TestClasses.POSITIVE_IMPL, "All xsd:byte\n" + "are xsd:short.");
    }
}
