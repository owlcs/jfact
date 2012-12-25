package uk.ac.manchester.cs.jfact.kernel.dl.interfaces;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.jfact.split.TOntologyAtom;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;
import conformance.PortedFrom;

@PortedFrom(file="tAxiom.h",name="TAxiom")
public interface Axiom extends Entity {
    OWLAxiom getOWLAxiom();

    /** set the id */
    void setId(int Id);

    /** get the id */
    int getId();

    /** set the used flag */
    void setUsed(boolean Used);

    /** get the value of the used flag */
    boolean isUsed();

    boolean isInModule();

    void setInModule(boolean inModule);

    /** accept method for the visitor pattern */
    void accept(DLAxiomVisitor visitor);

    <O> O accept(DLAxiomVisitorEx<O> visitor);

    void setInSS(boolean flag);

    boolean isInSS();

    TSignature getSignature();

    TOntologyAtom getAtom();

    void setAtom(TOntologyAtom atom);
}
