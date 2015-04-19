package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.junit.Test;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class WebOnt_miscellaneous_wine extends TestBase {

    @Test
    @Changed
    public void testWebOnt_miscellaneous_001() {
        String conclusion = "";
        String id = "WebOnt_miscellaneous_001";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Wine example taken from the guide. Note that this is the same as the ontology http://www.w3.org/2002/03owlt/miscellaneous/consistent002 imported in other tests.";
        JUnitRunner r = new JUnitRunner(
            WebOnt_miscellaneous_wine.class.getResourceAsStream("/wine.fss"),
            conclusion, id, tc, d);
        // r.setConfig(new JFactReasonerConfiguration().setLoggingActive(true));
        r.setReasonerFactory(factory());
        r.run();
    }
}
