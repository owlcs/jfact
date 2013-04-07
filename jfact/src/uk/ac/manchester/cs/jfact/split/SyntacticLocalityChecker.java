package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import conformance.Original;
import conformance.PortedFrom;

/** syntactic locality checker for DL axioms */
@PortedFrom(file = "SyntacticLocalityChecker.h", name = "SyntacticLocalityChecker")
public class SyntacticLocalityChecker extends GeneralSyntacticLocalityChecker {
    /** set a new value of a signature (without changing a locality parameters) */
    @Override
    @Original
    public void setSignatureValue(TSignature Sig) {
        sig = Sig;
        TopEval.sig = sig;
        BotEval.sig = sig;
    }

    // set fields
    /** @return true iff an AXIOM is local wrt defined policy */
    @Override
    @PortedFrom(file = "SyntacticLocalityChecker.h", name = "local")
    public boolean local(AxiomInterface axiom) {
        axiom.accept(this);
        return isLocal;
    }

    @Override
    @Original
    public void preprocessOntology(Collection<AxiomInterface> s) {
        sig = new TSignature();
        for (AxiomInterface ax : s) {
            sig.add(ax.getSignature());
        }
    }
}
