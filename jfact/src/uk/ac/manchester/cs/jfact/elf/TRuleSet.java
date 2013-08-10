package uk.ac.manchester.cs.jfact.elf;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import conformance.PortedFrom;

// Concepts and roles, i.e. S(C) and R(C,D)
/** aux class to support set of rules and rule applications */
@PortedFrom(file = "ELFReasoner.h", name = "TRuleSet")
class TRuleSet implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** set of rules to apply on change */
    @PortedFrom(file = "ELFReasoner.h", name = "Rules")
    List<TELFRule> Rules = new ArrayList<TELFRule>();

    /** apply all rules with a single argument */
    @PortedFrom(file = "ELFReasoner.h", name = "applyRules")
    void applyRules(TELFConcept addedC) {
        for (TELFRule p : Rules) {
            p.apply(addedC);
        }
    }

    /** apply all rules with two arguments */
    @PortedFrom(file = "ELFReasoner.h", name = "applyRules")
    void applyRules(TELFConcept addedC, TELFConcept addedD) {
        for (TELFRule p : Rules) {
            p.apply(addedC, addedD);
        }
    }

    /** add rule to a set */
    @PortedFrom(file = "ELFReasoner.h", name = "addRule")
    void addRule(TELFRule rule) {
        Rules.add(rule);
    }
}
