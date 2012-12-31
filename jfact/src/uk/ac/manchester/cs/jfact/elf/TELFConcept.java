package uk.ac.manchester.cs.jfact.elf;

import java.util.HashSet;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import conformance.PortedFrom;

/** concept, set S(C) and aux things */
@PortedFrom(file = "ELFReasoner.h", name = "TELFConcept")
class TELFConcept extends TRuleSet {
    /** original concept (if any) */
    @PortedFrom(file = "ELFReasoner.h", name = "Origin")
    ConceptExpression Origin;
    /** set of supers (built during classification) */
    @PortedFrom(file = "ELFReasoner.h", name = "Supers")
    Set<TELFConcept> Supers = new HashSet<TELFConcept>();

    /** add C to supers */
    @PortedFrom(file = "ELFReasoner.h", name = "addSuper")
    void addSuper(TELFConcept C) {
        Supers.add(C);
    }

    /** empty c'tor */
    TELFConcept() {
        Origin = null;
    }

    /** init c'tor */
    TELFConcept(ConceptExpression origin) {
        Origin = origin;
    }

    /** check whether concept C is contained in supers */
    @PortedFrom(file = "ELFReasoner.h", name = "hasSuper")
    boolean hasSuper(TELFConcept C) {
        return Supers.contains(C);
    }

    /** add an super concept */
    @PortedFrom(file = "ELFReasoner.h", name = "addC")
    void addC(TELFConcept C) {
        if (hasSuper(C)) {
            return;
        }
        addSuper(C);
        this.applyRules(C);
    }
}
