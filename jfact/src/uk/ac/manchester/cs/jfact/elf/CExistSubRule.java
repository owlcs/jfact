package uk.ac.manchester.cs.jfact.elf;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import conformance.PortedFrom;

/** rule that checks the addition of (X,Y) to R and finds a C in S(Y) */
@PortedFrom(file = "ELFReasoner.h", name = "CExistSubRule")
public class CExistSubRule extends TELFRule { private static final long serialVersionUID=11000L;
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
