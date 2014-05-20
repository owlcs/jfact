package uk.ac.manchester.cs.jfact.kernel.dl.axioms;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.split.TSignatureUpdater;
import conformance.PortedFrom;

@PortedFrom(file = "tDLAxiom.h", name = "TDLAxiom")
abstract class AxiomImpl implements AxiomInterface, Serializable {

    private static final long serialVersionUID = 11000L;
    /** id of the axiom */
    private int id;
    /** signature (built lazily on demand) */
    private TSignature sig = null;
    /** flag to show whether or not the axiom is in the search space for the */
    // optimised modularization algorithm
    private boolean inSearchSpace = false;
    /** flag to show whether or not the axiom is in the module */
    private boolean inModule;
    /** flag to show whether it is used (to support retraction) */
    private boolean used;
    private TOntologyAtom atom = null;
    private final OWLAxiom axiom;

    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "getAtom")
    public TOntologyAtom getAtom() {
        return atom;
    }

    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "setAtom")
    public void setAtom(TOntologyAtom atom) {
        this.atom = atom;
    }

    /** set the isSearchSpace flag */
    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "setInSS")
    public void setInSS(boolean flag) {
        inSearchSpace = flag;
    }

    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "isInSS")
    public boolean isInSS() {
        return inSearchSpace;
    }

    // signature access
    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "getSignature")
    public TSignature getSignature() {
        if (sig == null) {
            buildSignature();
        }
        return sig;
    }

    private void buildSignature() {
        sig = new TSignature();
        TSignatureUpdater Updater = new TSignatureUpdater(sig);
        this.accept(Updater);
    }

    public AxiomImpl(OWLAxiom ax) {
        axiom = ax;
        used = true;
        inModule = false;
    }

    @Override
    public OWLAxiom getOWLAxiom() {
        return axiom;
    }

    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "getId")
    public int getId() {
        return id;
    }

    @Override
    public boolean isInModule() {
        return inModule;
    }

    @Override
    public boolean isUsed() {
        return used;
    }

    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "setId")
    public void setId(int Id) {
        id = Id;
    }

    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "setInModule")
    public void setInModule(boolean inModule) {
        this.inModule = inModule;
    }

    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "setUsed")
    public void setUsed(boolean Used) {
        used = Used;
    }

    @Override
    public String toString() {
        return axiom == null ? super.toString() : axiom.toString();
    }
}
