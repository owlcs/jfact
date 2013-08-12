package uk.ac.manchester.cs.jfact.elf;

import java.io.Serializable;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import conformance.PortedFrom;

// Action class
/** single algorithm action (application of a rule) */
@PortedFrom(file = "ELFReasoner.h", name = "ELFAction")
class ELFAction implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** role R corresponded to R(C,D) */
    @PortedFrom(file = "ELFReasoner.h", name = "R")
    private final TELFRole R;
    /** concept C; to add */
    @PortedFrom(file = "ELFReasoner.h", name = "C")
    private final TELFConcept C;
    /** concept D; to add */
    @PortedFrom(file = "ELFReasoner.h", name = "D")
    private final TELFConcept D;

    /** init c'tor for C action */
    public ELFAction(TELFConcept c, TELFConcept d) {
        this(null, c, d);
    }

    /** init c'tor for R action */
    public ELFAction(TELFRole r, TELFConcept c, TELFConcept d) {
        R = r;
        C = c;
        D = d;
    }

    /** action itself, depending on the R state */
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    public void apply() {
        if (R != null) {
            R.addR(C, D);
        } else {
            C.addC(D);
        }
    }
}
