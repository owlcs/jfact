package uk.ac.manchester.cs.jfact.elf;

import java.util.ArrayList;
import java.util.List;

import conformance.PortedFrom;

// Concepts and roles, i.e. S(C) and R(C,D)
/** aux class to support set of rules and rule applications */
@PortedFrom(file = "ELFReasoner.h", name = "TRuleSet")
class TRuleSet {
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
