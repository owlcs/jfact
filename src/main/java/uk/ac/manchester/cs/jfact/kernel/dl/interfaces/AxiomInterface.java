package uk.ac.manchester.cs.jfact.kernel.dl.interfaces;

import java.io.Serializable;

import javax.annotation.Nullable;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.semanticweb.owlapi.model.OWLAxiom;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;

/** Axiom interface */
@SuppressWarnings("unused")
@PortedFrom(file = "tDLAxiom.h", name = "TDLAxiom")
public interface AxiomInterface extends Serializable {

    /** @return original owl axiom */
    @Original
    @Nullable
    default OWLAxiom getOWLAxiom() {
        return null;
    }

    /**
     * @param id
     *        Id
     */
    @PortedFrom(file = "tDLAxiom.h", name = "setId")
    default void setId(int id) {}

    /** @return the id */
    @PortedFrom(file = "tDLAxiom.h", name = "getId")
    default int getId() {
        return 0;
    }

    /**
     * @param used
     *        Used
     */
    @PortedFrom(file = "tDLAxiom.h", name = "setUsed")
    default void setUsed(boolean used) {}

    /** @return the value of the used flag */
    @PortedFrom(file = "tDLAxiom.h", name = "isUsed")
    default boolean isUsed() {
        return false;
    }

    /** @return true if in module */
    @PortedFrom(file = "tDLAxiom.h", name = "isInModule")
    default boolean isInModule() {
        return false;
    }

    /**
     * @param inModule
     *        inModule
     */
    @PortedFrom(file = "tDLAxiom.h", name = "setInModule")
    default void setInModule(boolean inModule) {}

    /**
     * accept method for the visitor pattern
     * 
     * @param visitor
     *        visitor
     */
    @PortedFrom(file = "tDLAxiom.h", name = "accept")
    default void accept(DLAxiomVisitor visitor) {}

    /**
     * @param visitor
     *        visitor
     * @param <O>
     *        visitor return type
     * @return visitor result
     */
    @Nullable
    @PortedFrom(file = "tDLAxiom.h", name = "accept")
    default <O> O accept(DLAxiomVisitorEx<O> visitor) {
        return null;
    }

    /**
     * @param flag
     *        flag
     */
    @PortedFrom(file = "tDLAxiom.h", name = "setInSS")
    default void setInSS(boolean flag) {}

    /** @return the value of the isSearchSpace flag */
    @PortedFrom(file = "tDLAxiom.h", name = "isInSS")
    default boolean isInSS() {
        return false;
    }

    /** @return signature */
    @Nullable
    @PortedFrom(file = "tDLAxiom.h", name = "getSignature")
    default TSignature getSignature() {
        return null;
    }

    /** @return atom for axiom */
    @Nullable
    @PortedFrom(file = "tDLAxiom.h", name = "getAtom")
    default TOntologyAtom getAtom() {
        return null;
    }

    /**
     * @param atom
     *        atom
     */
    @PortedFrom(file = "tDLAxiom.h", name = "setAtom")
    default void setAtom(TOntologyAtom atom) {}
}
