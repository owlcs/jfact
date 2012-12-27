package uk.ac.manchester.cs.jfact.elf;

import conformance.PortedFrom;

// -------------------------------------------------------------
// Rule for C [= \Er.D case; CR3
// -------------------------------------------------------------
/** the rule for C [= \ER.D case */
@PortedFrom(file = "ELFReasoner.h", name = "RAddRule")
public class RAddRule extends TELFRule {
    /** role to add the pair */
    TELFRole R;
    /** filler (D) of the existential */
    TELFConcept Filler;

    /** init c'tor: remember D */
    RAddRule(ELFReasoner ER, TELFRole r, TELFConcept C) {
        super(ER);
        R = r;
        Filler = C;
    }

    /** apply a method with a given source S(C) */
    @Override
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    void apply(TELFConcept Source) {
        // if ( !R.hasLabel ( Source, Filler ) )
        ER.addAction(new ELFAction(R, Source, Filler));
    }
}
