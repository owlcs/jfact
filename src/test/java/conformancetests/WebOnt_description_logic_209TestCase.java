/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
package conformancetests;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class WebOnt_description_logic_209TestCase extends TestBase {

    @Test
    public void testWebOnt_description_logic_209() throws OWLOntologyCreationException {
        String conclusion = "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" + "Ontology(\n"
            + "Declaration(Class(<http://oiled.man.example.net/test#C122>))\n"
            + "ClassAssertion(<http://oiled.man.example.net/test#C122> <http://oiled.man.example.net/test#V16448>)\n" +
            // ClassAssertion(owl:Thing
            // <http://oiled.man.example.net/test#V16448>)
        ')';
        String id = "WebOnt_description_logic_209";
        TestClasses tc = TestClasses.valueOf("NEGATIVE_IMPL");
        String d = "DL Test: k_poly\n" + "ABox test from DL98 systems comparison.\n"
            + "(Modified in light of implementation feedback, see test description-logic-208).";
        JUnitRunner r = new JUnitRunner(m, asString(m, "/webont209.owl"), conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }
}
