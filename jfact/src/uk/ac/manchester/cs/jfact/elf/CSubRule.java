package uk.ac.manchester.cs.jfact.elf;

import conformance.PortedFrom;

// -------------------------------------------------------------
// Rule for C [= D case; CR1
// -------------------------------------------------------------
/** the rule for C [= D case */
@PortedFrom(file = "ELFReasoner.h", name = "CSubRule")
public class CSubRule extends TELFRule {
    /** super of a concept; it would be added to S(C) */
    TELFConcept Sup = null;

    /** init c'tor: remember D */
    CSubRule(ELFReasoner ER, TELFConcept D) {
        super(ER);
        Sup = D;
    }

    /** apply a method with a given S(C) */
    @Override
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    void apply(TELFConcept addedC) {
        if (!addedC.hasSuper(Sup)) {
            ER.addAction(new ELFAction(addedC, Sup));
        }
    }
}
