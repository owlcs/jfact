package uk.ac.manchester.cs.jfact.elf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import conformance.PortedFrom;

/** role, set R(C,D) */
@PortedFrom(file = "ELFReasoner.h", name = "TELFRole")
class TELFRole extends TRuleSet {
    /** original role (if any) */
    ObjectRoleExpression Origin;
    /** map itself */
    Map<TELFConcept, Set<TELFConcept>> PredMap = new HashMap<TELFConcept, Set<TELFConcept>>();

    /** add (C,D) to label */
@PortedFrom(file="ELFReasoner.h",name="addLabel")
    void addLabel(TELFConcept C, TELFConcept D) {
        PredMap.get(D).add(C);
    }

    /** empty c'tor */
    TELFRole() {
        Origin = null;
    }

    /** init c'tor */
    TELFRole(ObjectRoleExpression origin) {
        Origin = origin;
    }

    /** get the (possibly empty) set of predecessors of given D */
@PortedFrom(file="ELFReasoner.h",name="getPredSet")
    Set<TELFConcept> getPredSet(TELFConcept D) {
        return PredMap.get(D);
    }

@PortedFrom(file="ELFReasoner.h",name="begin")
    Iterable<Map.Entry<TELFConcept, Set<TELFConcept>>> begin() {
        return PredMap.entrySet();
    }

    /** check whether (C,D) is in the R-set */
@PortedFrom(file="ELFReasoner.h",name="hasLabel")
    boolean hasLabel(TELFConcept C, TELFConcept D) {
        return PredMap.get(D).contains(C);
    }

    /** add pair (C,D) to a set */
@PortedFrom(file="ELFReasoner.h",name="addR")
    void addR(TELFConcept C, TELFConcept D) {
        if (hasLabel(C, D)) {
            return;
        }
        addLabel(C, D);
        this.applyRules(C, D);
    }
}
