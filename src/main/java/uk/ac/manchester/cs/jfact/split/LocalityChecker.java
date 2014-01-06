package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import conformance.PortedFrom;

/** locality checker */
@PortedFrom(file = "LocalityChecker.h", name = "LocalityChecker")
public interface LocalityChecker {
    /** @param axiom
     *            axiom
     * @return true if local */
    @PortedFrom(file = "LocalityChecker.h", name = "local")
    boolean local(AxiomInterface axiom);

    /** allow the checker to preprocess an ontology if necessary
     * 
     * @param vec
     *            vec */
    @PortedFrom(file = "LocalityChecker.h", name = "preprocessOntology")
    void preprocessOntology(Collection<AxiomInterface> vec);

    /** @param sig
     *            sig */
    @PortedFrom(file = "LocalityChecker.h", name = "setSignatureValue")
    void setSignatureValue(TSignature sig);

    /** @return signature */
    @PortedFrom(file = "LocalityChecker.h", name = "getSignature")
    TSignature getSignature();
}
