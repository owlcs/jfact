package uk.ac.manchester.cs.jfact.elf;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import conformance.PortedFrom;

// Rule for R [= S case; CR10
/** the rule for R [= S case */
@PortedFrom(file = "ELFReasoner.h", name = "RSubRule")
public class RSubRule extends TELFRule {
    /** role to add the pair */
    @PortedFrom(file = "ELFReasoner.h", name = "S")
    TELFRole S;

    /** init c'tor: remember S */
    RSubRule(ELFReasoner ER, TELFRole s) {
        super(ER);
        S = s;
    }

    /** apply a method with a given pair (C,D) */
    @Override
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    void apply(TELFConcept addedC, TELFConcept addedD) {
        ER.addAction(new ELFAction(S, addedC, addedD));
    }
}
