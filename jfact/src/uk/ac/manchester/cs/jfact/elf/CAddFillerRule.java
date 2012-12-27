package uk.ac.manchester.cs.jfact.elf;

import java.util.Set;

import conformance.PortedFrom;

// -------------------------------------------------------------
// Rules for \Er.C [= D case; CR4
// -------------------------------------------------------------
/** rule that checks an addition of C to S(Y) and checks whether there is X */
// s.t. R(X,Y)
@PortedFrom(file = "ELFReasoner.h", name = "CAddFillerRule")
public class CAddFillerRule extends TELFRule {
    /** role to add the pair */
    TELFRole R;
    /** super (E) of the existential */
    TELFConcept Sup;

    /** init c'tor: remember E */
    CAddFillerRule(ELFReasoner ER, TELFRole r, TELFConcept C) {
        super(ER);
        R = r;
        Sup = C;
    }

    /** apply a method with a given source S(C) */
    @Override
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    void apply(TELFConcept Source) {
        Set<TELFConcept> SupSet = R.getPredSet(Source);
        if (!SupSet.isEmpty()) {
            for (TELFConcept p : SupSet) {
                if (!p.hasSuper(Sup)) {
                    ER.addAction(new ELFAction(p, Sup));
                }
            }
        }
    }
}
