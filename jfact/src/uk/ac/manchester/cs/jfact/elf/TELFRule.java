package uk.ac.manchester.cs.jfact.elf;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import conformance.PortedFrom;

/** pattern for the rule. Contains apply() method with updates of the monitored
 * set */
@PortedFrom(file = "ELFReasoner.h", name = "TELFRule")
class TELFRule {
    /** reasoner that is used to add actions. The number of rules = the number of
     * axioms, so the price is not too bad memory-wise. */
    @PortedFrom(file = "ELFReasoner.h", name = "ER")
    ELFReasoner ER;

    /** init c'tor */
    TELFRule(ELFReasoner er) {
        ER = er;
    }

    /** apply rule with fresh class C added to watching part */
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    void apply(TELFConcept addedC) {}

    /** apply rule with fresh pair (C,D) added to watching part */
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    void apply(TELFConcept addedC, TELFConcept addedD) {}
}
