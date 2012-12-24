package uk.ac.manchester.cs.jfact.kernel.dl.axioms;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.split.TSignatureUpdater;

abstract class AxiomImpl implements Axiom {
    /** id of the axiom */
    private int id;
    /** signature (built lazily on demand) */
    TSignature sig = null;
    /** flag to show whether or not the axiom is in the search space for the */
    // optimised modularization algorithm
    boolean inSearchSpace = false;
    /** flag to show whether or not the axiom is in the module */
    boolean inModule;
    /** flag to show whether it is used (to support retraction) */
    private boolean used;
    TOntologyAtom atom = null;
    private OWLAxiom axiom;

    @Override
    public TOntologyAtom getAtom() {
        return atom;
    }

    @Override
    public void setAtom(TOntologyAtom atom) {
        this.atom = atom;
    }

    /** set the isSearchSpace flag */
    @Override
    public void setInSS(boolean flag) {
        inSearchSpace = flag;
    }

    /** get the value of the isSearchSpace flag */
    @Override
    public boolean isInSS() {
        return inSearchSpace;
    }

    // signature access
    @Override
    public TSignature getSignature() {
        if (sig == null) {
            buildSignature();
        }
        return sig;
    }

    void buildSignature() {
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
    public void setId(int Id) {
        id = Id;
    }

    @Override
    public void setInModule(boolean inModule) {
        this.inModule = inModule;
    }

    @Override
    public void setUsed(boolean Used) {
        used = Used;
    }

    @Override
    public String toString() {
        return axiom == null ? super.toString() : axiom.toString();
    }
}
