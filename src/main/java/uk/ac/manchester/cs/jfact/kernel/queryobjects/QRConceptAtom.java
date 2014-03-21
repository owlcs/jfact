package uk.ac.manchester.cs.jfact.kernel.queryobjects;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import conformance.PortedFrom;

/** concept atom: C(x) */
@PortedFrom(file = "QR.h", name = "QRConceptAtom")
public class QRConceptAtom extends QRAtom {

    private static final long serialVersionUID = 11000L;
    /** pointer to a concept (named one atm) */
    @PortedFrom(file = "QR.h", name = "Concept")
    private final ConceptExpression Concept;
    /** argument */
    @PortedFrom(file = "QR.h", name = "Arg")
    private final QRiObject Arg;

    /**
     * init c'tor
     * 
     * @param C
     *        C
     * @param A
     *        A
     */
    public QRConceptAtom(ConceptExpression C, QRiObject A) {
        Concept = C;
        Arg = A;
    }

    // access
    /** @return concept expression */
    @PortedFrom(file = "QR.h", name = "getConcept")
    public ConceptExpression getConcept() {
        return Concept;
    }

    /** @return i-object */
    @PortedFrom(file = "QR.h", name = "getArg")
    public QRiObject getArg() {
        return Arg;
    }

    @Override
    public String toString() {
        return Concept.getName() + "(" + getArg() + ")";
    }
}
