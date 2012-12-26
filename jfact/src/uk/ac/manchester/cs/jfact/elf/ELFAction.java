package uk.ac.manchester.cs.jfact.elf;

import conformance.PortedFrom;

// -------------------------------------------------------------
// Action class
// -------------------------------------------------------------
/** single algorithm action (application of a rule) */
@PortedFrom(file = "ELFReasoner.h", name = "ELFAction")
class ELFAction {
    /** role R corresponded to R(C,D) */
    TELFRole R = null;
    /** concept C; to add */
    TELFConcept C = null;
    /** concept D; to add */
    TELFConcept D = null;

    /** init c'tor for C action */
    ELFAction(TELFConcept c, TELFConcept d) {
        R = null;
        C = c;
        D = d;
    }

    /** init c'tor for R action */
    ELFAction(TELFRole r, TELFConcept c, TELFConcept d) {
        R = r;
        C = c;
        D = d;
    }

    /** action itself, depending on the R state */
@PortedFrom(file="ELFReasoner.h",name="apply")
    void apply() {
        if (R != null) {
            R.addR(C, D);
        } else {
            C.addC(D);
        }
    }
}