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
public class WebOnt_miscellaneous_002TestCase extends TestBase {

    @Test
    @ChangedTestCase
    public void testWebOnt_miscellaneous_002() throws OWLOntologyCreationException {
        String conclusion = "";
        String id = "WebOnt_miscellaneous_002";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Food example taken from the guide. Note that this is the same as the ontology http://www.w3.org/2002/03owlt/miscellaneous/consistent002 imported in other tests.";
        JUnitRunner r = new JUnitRunner(m, asString(m, "/webont002.owl"), conclusion, id, tc, d);
        r.setReasonerFactory(factory());
        r.run();
    }
}
