package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
enum DIOp {
    // concept expressions
    diNot("not"),
    diAnd("and"),
    diOr("or"),
    diExists("some"),
    diForall("all"),
    diGE("atleast"),
    diLE("atmost"),
    // role expressions
    diInv,
    // individual expressions
    diOneOf,
    // end of the enum
    diEndOp,
    // wrong axiom
    // diErrorAx(9),
    // concept axioms
    diDefineC("defprimconcept"),
    diImpliesC("implies_c"),
    diEqualsC("equal_c"),
    diDisjointC,
    // role axioms
    diDefineR("defprimrole"),
    diTransitiveR("transitive"),
    diFunctionalR("functional"),
    diImpliesR("implies_r"),
    diEqualsR("equal_r"),
    diDomainR("domain"),
    diRangeR("range"),
    // individual axioms
    diInstanceOf;

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
