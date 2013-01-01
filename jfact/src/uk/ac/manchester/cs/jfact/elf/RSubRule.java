package uk.ac.manchester.cs.jfact.elf;

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
