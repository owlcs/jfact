package uk.ac.manchester.cs.jfact.elf;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Map;
import java.util.Set;

import conformance.PortedFrom;

// Rules for R o S [= T case; CR11
/** the rule for R in R o S [= T case */
@PortedFrom(file = "ELFReasoner.h", name = "RChainLRule")
public class RChainLRule extends TELFRule {
    private static final long serialVersionUID = 11000L;
    /** role to check the chain */
    @PortedFrom(file = "ELFReasoner.h", name = "S")
    private final TELFRole S;
    /** role to add the pair */
    @PortedFrom(file = "ELFReasoner.h", name = "T")
    private final TELFRole T;

    /** init c'tor: remember S and T */
    public RChainLRule(ELFReasoner ER, TELFRole s, TELFRole t) {
        super(ER);
        S = s;
        T = t;
    }

    /** apply a method with a given pair (C,D) */
    @Override
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    public void apply(TELFConcept addedC, TELFConcept addedD) {
        // we have R(C,D); so for all E in range(S), if S(D,E) then add T(C,E)
        for (Map.Entry<TELFConcept, Set<TELFConcept>> i : S.begin()) {
            if (i.getValue().contains(addedD)) {
                TELFConcept E = i.getKey();
                ER.addAction(new ELFAction(T, addedC, E));
            }
        }
    }
}
