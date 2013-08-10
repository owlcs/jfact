package uk.ac.manchester.cs.jfact.elf;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import conformance.PortedFrom;

/** role, set R(C,D) */
@PortedFrom(file = "ELFReasoner.h", name = "TELFRole")
class TELFRole extends TRuleSet { private static final long serialVersionUID=11000L;
    /** original role (if any) */
    @PortedFrom(file = "ELFReasoner.h", name = "Origin")
    ObjectRoleExpression Origin;
    /** map itself */
    @PortedFrom(file = "ELFReasoner.h", name = "PredMap")
    Map<TELFConcept, Set<TELFConcept>> PredMap = new HashMap<TELFConcept, Set<TELFConcept>>();

    /** add (C,D) to label */
    @PortedFrom(file = "ELFReasoner.h", name = "addLabel")
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
    @PortedFrom(file = "ELFReasoner.h", name = "getPredSet")
    Set<TELFConcept> getPredSet(TELFConcept D) {
        return PredMap.get(D);
    }

    @PortedFrom(file = "ELFReasoner.h", name = "begin")
    Iterable<Map.Entry<TELFConcept, Set<TELFConcept>>> begin() {
        return PredMap.entrySet();
    }

    /** check whether (C,D) is in the R-set */
    @PortedFrom(file = "ELFReasoner.h", name = "hasLabel")
    boolean hasLabel(TELFConcept C, TELFConcept D) {
        return PredMap.get(D).contains(C);
    }

    /** add pair (C,D) to a set */
    @PortedFrom(file = "ELFReasoner.h", name = "addR")
    void addR(TELFConcept C, TELFConcept D) {
        if (hasLabel(C, D)) {
            return;
        }
        addLabel(C, D);
        this.applyRules(C, D);
    }
}
