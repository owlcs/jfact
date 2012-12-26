package uk.ac.manchester.cs.jfact.elf;

import conformance.PortedFrom;

// -------------------------------------------------------------
// Rule for R(C,D) with \bot\in S(D) case; CR5
// -------------------------------------------------------------
// rule that checks whether for R(C,D) S(D) contains \bot
@PortedFrom(file="ELFReasoner.h",name="RBotRule")
public class RBotRule extends TELFRule {
    /** remember the Bottom concept */
    TELFConcept ConceptBot;

    /** init c'tor: remember E */
    RBotRule(ELFReasoner ER, TELFConcept bot) {
        super(ER);
        ConceptBot = bot;
    }

    /** apply a method with a given new pair (C,D) */
    @Override
@PortedFrom(file="ELFReasoner.h",name="apply")
    void apply(TELFConcept addedC, TELFConcept addedD) {
        // it seems like every other pair is already processed, either via
        // that rule or via add(\bot)
        if (addedD.hasSuper(ConceptBot) && !addedC.hasSuper(ConceptBot)) {
            ER.addAction(new ELFAction(addedC, ConceptBot));
        }
    }
}
