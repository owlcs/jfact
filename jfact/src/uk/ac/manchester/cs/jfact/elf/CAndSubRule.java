package uk.ac.manchester.cs.jfact.elf;

import conformance.PortedFrom;

// Rule for C1 and C2 [= D case; CR2
/** the rule for C1 and C2 [= D case */
@PortedFrom(file = "ELFReasoner.h", name = "CAndSubRule")
public class CAndSubRule extends TELFRule {
    /** concept to find in order to fire a rule */
    @PortedFrom(file = "ELFReasoner.h", name = "Conj")
    TELFConcept Conj;
    /** super of a concept; it would be added to S(C) */
    @PortedFrom(file = "ELFReasoner.h", name = "Sup")
    TELFConcept Sup;

    /** init c'tor: remember D */
    CAndSubRule(ELFReasoner ER, TELFConcept C, TELFConcept D) {
        super(ER);
        Conj = C;
        Sup = D;
    }

    /** apply a method with a given S(C) */
    @Override
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    void apply(TELFConcept C) {
        if (C.hasSuper(Conj) && !C.hasSuper(Sup)) {
            ER.addAction(new ELFAction(C, Sup));
        }
    }
}
