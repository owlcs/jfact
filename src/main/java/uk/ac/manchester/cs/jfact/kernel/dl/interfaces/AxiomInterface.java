package uk.ac.manchester.cs.jfact.kernel.dl.interfaces;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.jfact.split.TOntologyAtom;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;
import conformance.Original;
import conformance.PortedFrom;

/** Axiom interface */
@PortedFrom(file = "tDLAxiom.h", name = "TDLAxiom")
public interface AxiomInterface {

    /** @return original owl axiom */
    @Original
    OWLAxiom getOWLAxiom();

    /**
     * @param Id
     *        Id
     */
    @PortedFrom(file = "tDLAxiom.h", name = "setId")
    void setId(int Id);

    /** @return the id */
    @PortedFrom(file = "tDLAxiom.h", name = "getId")
    int getId();

    /**
     * @param Used
     *        Used
     */
    @PortedFrom(file = "tDLAxiom.h", name = "setUsed")
    void setUsed(boolean Used);

    /** @return the value of the used flag */
    @PortedFrom(file = "tDLAxiom.h", name = "isUsed")
    boolean isUsed();

    /** @return true if in module */
    @PortedFrom(file = "tDLAxiom.h", name = "isInModule")
    boolean isInModule();

    /**
     * @param inModule
     *        inModule
     */
    @PortedFrom(file = "tDLAxiom.h", name = "setInModule")
    void setInModule(boolean inModule);

    /**
     * accept method for the visitor pattern
     * 
     * @param visitor
     *        visitor
     */
    @PortedFrom(file = "tDLAxiom.h", name = "accept")
    void accept(DLAxiomVisitor visitor);

    /**
     * @param visitor
     *        visitor
     * @param <O>
     *        visitor return type
     * @return visitor result
     */
    @PortedFrom(file = "tDLAxiom.h", name = "accept")
    <O> O accept(DLAxiomVisitorEx<O> visitor);

    /**
     * @param flag
     *        flag
     */
    @PortedFrom(file = "tDLAxiom.h", name = "setInSS")
    void setInSS(boolean flag);

    /** @return the value of the isSearchSpace flag */
    @PortedFrom(file = "tDLAxiom.h", name = "isInSS")
    boolean isInSS();

    /** @return signature */
    @PortedFrom(file = "tDLAxiom.h", name = "getSignature")
    TSignature getSignature();

    /** @return atom for axiom */
    @PortedFrom(file = "tDLAxiom.h", name = "getAtom")
    TOntologyAtom getAtom();

    /**
     * @param atom
     *        atom
     */
    @PortedFrom(file = "tDLAxiom.h", name = "setAtom")
    void setAtom(TOntologyAtom atom);
}
