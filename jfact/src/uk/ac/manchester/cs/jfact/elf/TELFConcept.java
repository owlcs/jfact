package uk.ac.manchester.cs.jfact.elf;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.HashSet;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import conformance.PortedFrom;

/** concept, set S(C) and aux things */
@PortedFrom(file = "ELFReasoner.h", name = "TELFConcept")
class TELFConcept extends TRuleSet { private static final long serialVersionUID=11000L;
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
