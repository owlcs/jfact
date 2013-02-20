package uk.ac.manchester.cs.jfact.elf;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
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
