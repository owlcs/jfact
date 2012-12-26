package uk.ac.manchester.cs.jfact.elf;

import conformance.PortedFrom;

/** pattern for the rule. Contains apply() method with updates of the monitored
 * set */
@PortedFrom(file = "ELFReasoner.h", name = "TELFRule")
class TELFRule {
    /** reasoner that is used to add actions. The number of rules = the number of
     * axioms, so the price is not too bad memory-wise. */
    ELFReasoner ER;

    /** init c'tor */
    TELFRule(ELFReasoner er) {
        ER = er;
    }

    /** apply rule with fresh class C added to watching part */
@PortedFrom(file="ELFReasoner.h",name="apply")
    void apply(TELFConcept addedC) {}

    /** apply rule with fresh pair (C,D) added to watching part */
@PortedFrom(file="ELFReasoner.h",name="apply")
    void apply(TELFConcept addedC, TELFConcept addedD) {}
}
