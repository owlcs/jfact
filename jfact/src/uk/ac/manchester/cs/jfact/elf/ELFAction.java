package uk.ac.manchester.cs.jfact.elf;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import conformance.PortedFrom;

// Action class
/** single algorithm action (application of a rule) */
@PortedFrom(file = "ELFReasoner.h", name = "ELFAction")
class ELFAction {
    /** role R corresponded to R(C,D) */
    @PortedFrom(file = "ELFReasoner.h", name = "R")
    TELFRole R = null;
    /** concept C; to add */
    @PortedFrom(file = "ELFReasoner.h", name = "C")
    TELFConcept C = null;
    /** concept D; to add */
    @PortedFrom(file = "ELFReasoner.h", name = "D")
    TELFConcept D = null;

    /** init c'tor for C action */
    ELFAction(TELFConcept c, TELFConcept d) {
        R = null;
        C = c;
        D = d;
    }

    /** init c'tor for R action */
    ELFAction(TELFRole r, TELFConcept c, TELFConcept d) {
        R = r;
        C = c;
        D = d;
    }

    /** action itself, depending on the R state */
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    void apply() {
        if (R != null) {
            R.addR(C, D);
        } else {
            C.addC(D);
        }
    }
}
