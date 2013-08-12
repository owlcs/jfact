package uk.ac.manchester.cs.jfact.elf;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import conformance.PortedFrom;

// Rule for R(C,D) with \bot\in S(D) case; CR5
// rule that checks whether for R(C,D) S(D) contains \bot
/** r bot rule */
@PortedFrom(file = "ELFReasoner.h", name = "RBotRule")
public class RBotRule extends TELFRule {
    private static final long serialVersionUID = 11000L;
    /** remember the Bottom concept */
    @PortedFrom(file = "ELFReasoner.h", name = "CBot")
    private final TELFConcept ConceptBot;

    /** init c'tor: remember E */
    public RBotRule(ELFReasoner ER, TELFConcept bot) {
        super(ER);
        ConceptBot = bot;
    }

    /** apply a method with a given new pair (C,D) */
    @Override
    @PortedFrom(file = "ELFReasoner.h", name = "apply")
    public void apply(TELFConcept addedC, TELFConcept addedD) {
        // it seems like every other pair is already processed, either via that
        // rule or via add(\bot)
        if (addedD.hasSuper(ConceptBot) && !addedC.hasSuper(ConceptBot)) {
            ER.addAction(new ELFAction(addedC, ConceptBot));
        }
    }
}
