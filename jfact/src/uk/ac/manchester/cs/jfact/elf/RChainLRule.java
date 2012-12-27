package uk.ac.manchester.cs.jfact.elf;

import java.util.Map;
import java.util.Set;

import conformance.PortedFrom;

// -------------------------------------------------------------
// Rules for R o S [= T case; CR11
// -------------------------------------------------------------
/** the rule for R in R o S [= T case */
@PortedFrom(file = "ELFReasoner.h", name = "RChainLRule")
public class RChainLRule extends TELFRule {
    /** role to check the chain */
    TELFRole S;
    /** role to add the pair */
    TELFRole T;

    /** init c'tor: remember S and T */
    RChainLRule(ELFReasoner ER, TELFRole s, TELFRole t) {
        super(ER);
        S = s;
        T = t;
    }

    /** apply a method with a given pair (C,D) */
    @Override
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    void apply(TELFConcept addedC, TELFConcept addedD) {
        // we have R(C,D); so for all E in range(S), if S(D,E) then add
        // T(C,E)
        for (Map.Entry<TELFConcept, Set<TELFConcept>> i : S.begin()) {
            if (i.getValue().contains(addedD)) {
                TELFConcept E = i.getKey();
                // if ( !T.hasLabel ( addedC, E ) )
                ER.addAction(new ELFAction(T, addedC, E));
            }
        }
    }
}
