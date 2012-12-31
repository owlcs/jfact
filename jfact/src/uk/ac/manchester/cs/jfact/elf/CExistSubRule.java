package uk.ac.manchester.cs.jfact.elf;

import conformance.PortedFrom;

/** rule that checks the addition of (X,Y) to R and finds a C in S(Y) */
@PortedFrom(file = "ELFReasoner.h", name = "CExistSubRule")
public class CExistSubRule extends TELFRule {
    /** filler of an existential */
    @PortedFrom(file = "ELFReasoner.h", name = "Filler")
    TELFConcept Filler;
    /** super of an axiom concept; it would be added to S(C) */
    @PortedFrom(file = "ELFReasoner.h", name = "Sup")
    TELFConcept Sup;

    /** init c'tor: remember D */
    CExistSubRule(ELFReasoner ER, TELFConcept filler, TELFConcept sup) {
        super(ER);
        Filler = filler;
        Sup = sup;
    }

    /** apply a method with an added pair (C,D) */
    @Override
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    void apply(TELFConcept addedC, TELFConcept addedD) {
        if (addedD.hasSuper(Filler) && !addedC.hasSuper(Sup)) {
            ER.addAction(new ELFAction(addedC, Sup));
        }
    }
}
