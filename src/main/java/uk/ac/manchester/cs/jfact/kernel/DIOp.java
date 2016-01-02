package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
enum DIOp {
    // concept expressions
    NOT("not"),
    AND("and"),
    OR("or"),
    EXISTS("some"),
    FORALL("all"),
    GE("atleast"),
    LE("atmost"),
    // role expressions
    INV,
    // individual expressions
    ONEOF,
    // end of the enum
    ENDOP,
    // concept axioms
    DEFINEC("defprimconcept"),
    IMPLIESC("implies_c"),
    EQUALSC("equal_c"),
    DISJOINTC,
    // role axioms
    DEFINER("defprimrole"),
    TRANSITIVER("transitive"),
    FUNCTIONALR("functional"),
    IMPLIESR("implies_r"),
    EQUALSR("equal_r"),
    DOMAINR("domain"),
    RANGER("range"),
    // individual axioms
    INSTANCEOF;

    private final String s;

    private DIOp() {
        s = "";
    }

    private DIOp(String s) {
        this.s = s;
    }

    public String getString() {
        return s;
    }
}
