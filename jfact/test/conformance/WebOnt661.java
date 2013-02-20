package conformance;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.junit.Test;

@SuppressWarnings("javadoc")
public class WebOnt661 {
    @Test
    public void testWebOnt_description_logic_661() {
        String premise = "<rdf:RDF xmlns:oiled=\"http://oiled.man.example.net/test#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
                + " xml:base=\"http://www.w3.org/2002/03owlt/description-logic/premises661\">\n"
                + " <owl:Ontology rdf:about=\"\"/>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C82.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.65\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C94.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.30\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C30.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.8\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C78.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.25\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C132.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.48\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#byte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C140\">\n"
                // TODO added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C74\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C138.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C78\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.25\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C76\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.24\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C74\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C54\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C72.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C72\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C68.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C70\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.21\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#integer\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C70\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C28\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C26\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.7\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#integer\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C26\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"http://oiled.man.example.net/test#R1\"/></owl:onProperty><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C24\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C24\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C22\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C20\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.6\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#byte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C20\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C92.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.29\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C40.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.11\"/></owl:onProperty><owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C76.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.24\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C88.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.64\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C12.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.3\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C48.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.13\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#byte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C128\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C126\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C130.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.44\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C126\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C124\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C124\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C102.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.35\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#integer\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C122\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C120\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C138.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.47\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#decimal\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C120\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C118\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C58\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C56\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C56\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C54\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C14\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C52\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C52\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C32.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C50.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C50\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C48.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.15\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C4.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.1\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C88\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.64\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C90.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.52\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C86\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C84\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.28\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#integer\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C84\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C82\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.53\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C50.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.15\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C82\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.65\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C80\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C76.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C78.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C86.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.28\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#integer\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C98.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.63\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#integer\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C10.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.16\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C22.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.6\"/><owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#byte\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C34.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.9\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C46.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.12\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#decimal\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C108\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C106\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.36\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C106\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C104\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C18.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.56\"/></owl:onProperty><owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#integer\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C104\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C100.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.49\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#integer\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C112.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.39\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C102\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C100\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.35\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#integer\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C136.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.46\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C100\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C98\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.49\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#integer\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C38\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C36\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C108.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.36\"/><owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C36\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.9\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C32\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C30.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.14\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#integer\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C30\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C22.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C28.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.8\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C138\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#C136.comp\"/></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.47\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#decimal\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C136\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C116\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C134.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.46\"/><owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.22\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C134\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C130.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C132\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.45\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C132\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.48\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#byte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C130\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C122\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C128\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.44\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C68\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C60\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C66\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.20\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#byte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C66\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C64\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C64\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C62\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C62\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C72.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.21\"/><owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#integer\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C60\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#C58\"/></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C84.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.53\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C96.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.38\"/></owl:onProperty><owl:cardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C32.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.14\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#integer\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C68.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.20\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#byte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C8\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.2\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C16.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:ID=\"P.4\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C28.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.7\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#integer\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#TEST\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C6\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C140\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C110.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:DatatypeProperty rdf:ID=\"P.37\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.1\"/><owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C134.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.45\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.22\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#int\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C18\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.56\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#integer\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C16\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.4\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C14\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C8.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C12.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C12\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.3\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C98\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.63\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#integer\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C10\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.16\"/><owl:maxCardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:maxCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C96\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C94.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C2\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.38\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C94\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C86.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C92.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.30\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#short\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C92\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C90\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C16.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.29\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C90\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom rdf:resource=\"http://oiled.man.example.net/test#C88\"/></owl:Restriction></owl:equivalentClass><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.52\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#unsignedByte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C118\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C10.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C116\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C80\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C114\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C114\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C96.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C112.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C112\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C110.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.39\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C110\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C102.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C108.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.37\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C8.comp\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.2\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#short\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C48\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C40.comp\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C46.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.13\"/><owl:cardinality rdf:datatype=\"/2001/XMLSchema#byte\">0</owl:cardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C46\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C44\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34.comp\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.12\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#decimal\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C44\"><owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:someValuesFrom><owl:Class rdf:about=\"http://oiled.man.example.net/test#C42\"/></owl:someValuesFrom></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C42\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C4\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + " </owl:Class>\n"
                + " <owl:Class rdf:about=\"http://oiled.man.example.net/test#C40\">\n"
                // added
                + "  <owl:equivalentClass><owl:Class>\n"
                // end added
                + "  <owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"http://oiled.man.example.net/test#C38\"/><owl:Class rdf:about=\"http://oiled.man.example.net/test#C34\"/></owl:intersectionOf>\n"
                // added
                + "  </owl:Class></owl:equivalentClass>\n"
                // end added
                + "  <owl:equivalentClass><owl:Restriction><owl:onProperty rdf:resource=\"#P.11\"/><owl:minCardinality rdf:datatype=\"/2001/XMLSchema#nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></owl:equivalentClass></owl:Class>\n"
                + " <owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\"><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C16\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C2\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C34\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C4\"/><rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:allValuesFrom rdf:resource=\"http://oiled.man.example.net/test#C98.comp\"/></owl:Restriction></rdf:type><rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:allValuesFrom rdf:resource=\"http://oiled.man.example.net/test#C88.comp\"/></owl:Restriction></rdf:type><rdf:type><owl:Restriction><owl:onProperty rdf:resource=\"http://oiled.man.example.net/test#R1\"/><owl:allValuesFrom rdf:resource=\"http://oiled.man.example.net/test#C82.comp\"/></owl:Restriction></rdf:type><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C132.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C100.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C108.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C102.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C90.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C84.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C92.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C86.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C18.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C78.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C96.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C76.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C134.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C10.comp\"/><rdf:type rdf:resource=\"http://oiled.man.example.net/test#C112.comp\"/></owl:Thing>\n"
                + "</rdf:RDF>";
        String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "      xml:base=\"http://www.w3.org/2002/03owlt/description-logic/conclusions661\"\n"
                + ">\n"
                + "<owl:Ontology/>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\"><rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C110\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\"><rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C94\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\"><rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C136\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\"><rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C58\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\"><rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C80\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\"><rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C56\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\"><rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C116\"/></rdf:type></owl:Thing>\n"
                + "<owl:Thing rdf:about=\"http://oiled.man.example.net/test#V822576\"><rdf:type>\n"
                + "<owl:Class rdf:about=\"http://oiled.man.example.net/test#C114\"/></rdf:type>\n"
                + "</owl:Thing>\n" + "</rdf:RDF>";
        String id = "WebOnt_description_logic_661";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "DL Test: k_branch\n" + "ABox test from DL98 systems comparison.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
