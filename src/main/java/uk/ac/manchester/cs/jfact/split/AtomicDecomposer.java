package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import conformance.Original;
import conformance.PortedFrom;

/** atomical decomposer of the ontology */
@PortedFrom(file = "AtomicDecomposer.h", name = "AtomicDecomposer")
public class AtomicDecomposer implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** atomic structure to build */
    @PortedFrom(file = "AtomicDecomposer.h", name = "AOS")
    private AOStructure AOS = null;
    /** modularizer to build modules */
    @PortedFrom(file = "AtomicDecomposer.h", name = "pModularizer")
    private final TModularizer Modularizer;
    /** tautologies of the ontology */
    @PortedFrom(file = "AtomicDecomposer.h", name = "Tautologies")
    private final List<AxiomInterface> Tautologies = new ArrayList<AxiomInterface>();
    /** progress indicator */
    @PortedFrom(file = "AtomicDecomposer.h", name = "PI")
    private ProgressIndicatorInterface PI = null;
    /** fake atom that represents the whole ontology */
    @PortedFrom(file = "AtomicDecomposer.h", name = "rootAtom")
    private TOntologyAtom rootAtom = null;
    /** module type for current AOS creation */
    @PortedFrom(file = "AtomicDecomposer.h", name = "type")
    private ModuleType type;

    /**
     * @param c
     *        modularizer
     */
    public AtomicDecomposer(TModularizer c) {
        Modularizer = c;
    }

    /** restore all tautologies back */
    @PortedFrom(file = "AtomicDecomposer.h", name = "restoreTautologies")
    private void restoreTautologies() {
        for (AxiomInterface p : Tautologies) {
            p.setUsed(true);
        }
    }

    /**
     * @param pi
     *        progress indicator to use
     */
    @PortedFrom(file = "AtomicDecomposer.h", name = "setProgressIndicator")
    public void setProgressIndicator(ProgressIndicatorInterface pi) {
        PI = pi;
    }

    /**
     * remove tautologies (axioms that are always local) from the ontology
     * temporarily
     * 
     * @param O
     *        O
     */
    @PortedFrom(file = "AtomicDecomposer.h", name = "removeTautologies")
    private void removeTautologies(Ontology O) {
        // we might use it for another decomposition
        Tautologies.clear();
        long nAx = 0;
        for (AxiomInterface p : O.getAxioms()) {
            if (p.isUsed()) {
                // check whether an axiom is local wrt its own signature
                Modularizer.extract(p, p.getSignature(), type);
                if (Modularizer.isTautology(p, type)) {
                    Tautologies.add(p);
                    p.setUsed(false);
                } else {
                    ++nAx;
                }
            }
        }
        if (PI != null) {
            PI.setLimit(nAx);
        }
    }

    /**
     * build a module for given axiom AX; use parent atom's module as a base for
     * the module search
     * 
     * @param sig
     *        sig
     * @param parent
     *        parent
     * @return new atom
     */
    @PortedFrom(file = "AtomicDecomposer.h", name = "buildModule")
    private TOntologyAtom buildModule(TSignature sig, TOntologyAtom parent) {
        // build a module for a given signature
        Modularizer.extract(parent.getModule(), sig, type);
        List<AxiomInterface> Module = Modularizer.getModule();
        // if module is empty (empty bottom atom) -- do nothing
        if (Module.isEmpty()) {
            return null;
        }
        // here the module is created; report it
        if (PI != null) {
            PI.incIndicator();
        }
        // check if the module corresponds to a PARENT one; modules are the same
        // iff their sizes are the same
        if (parent != rootAtom && Module.size() == parent.getModule().size()) {
            return parent;
        }
        // create new atom with that module
        TOntologyAtom atom = AOS.newAtom();
        atom.setModule(Module);
        return atom;
    }

    /**
     * create atom for given axiom AX; use parent atom's module as a base for
     * the module search
     * 
     * @param ax
     *        ax
     * @param parent
     *        parent
     * @return new atom
     */
    @PortedFrom(file = "AtomicDecomposer.h", name = "createAtom")
    private TOntologyAtom createAtom(AxiomInterface ax, TOntologyAtom parent) {
        // check whether axiom already has an atom
        if (ax.getAtom() != null) {
            return ax.getAtom();
        }
        // build an atom: use a module to find atomic dependencies
        TOntologyAtom atom = buildModule(ax.getSignature(), parent);
        // no empty modules should be here
        assert atom != null;
        // register axiom as a part of an atom
        atom.addAxiom(ax);
        // if atom is the same as parent -- nothing more to do
        if (atom == parent) {
            return parent;
        }
        // not the same as parent: for all atom's axioms check their atoms and
        // make ATOM depend on them
        for (AxiomInterface q : atom.getModule()) {
            if (!q.equals(ax)) {
                atom.addDepAtom(createAtom(q, atom));
            }
        }
        return atom;
    }

    /** @return all tautologies */
    @Original
    public List<AxiomInterface> getTautologies() {
        return new ArrayList<AxiomInterface>(Tautologies);
    }

    /** @return the atom structure */
    @PortedFrom(file = "AtomicDecomposer.h", name = "getAOS")
    public AOStructure getAOS() {
        return AOS;
    }

    /**
     * @param O
     *        O
     * @param t
     *        t
     * @return the atomic structure for given module type T
     */
    @PortedFrom(file = "AtomicDecomposer.h", name = "getAOS")
    public AOStructure getAOS(Ontology O, ModuleType t) {
        // remember the type of the module
        type = t;
        // prepare a new AO structure
        AOS = new AOStructure();
        // init semantic locality checker
        Modularizer.preprocessOntology(O.getAxioms());
        // we don't need tautologies here
        removeTautologies(O);
        // init the root atom
        rootAtom = new TOntologyAtom();
        rootAtom.setModule(new HashSet<AxiomInterface>(O.getAxioms()));
        // build the "bottom" atom for an empty signature
        TOntologyAtom BottomAtom = buildModule(new TSignature(), rootAtom);
        if (BottomAtom != null) {
            for (AxiomInterface q : BottomAtom.getModule()) {
                BottomAtom.addAxiom(q);
            }
        }
        // create atoms for all the axioms in the ontology
        for (AxiomInterface p : O.getAxioms()) {
            if (p.isUsed() && p.getAtom() == null) {
                createAtom(p, rootAtom);
            }
        }
        // restore tautologies in the ontology
        restoreTautologies();
        rootAtom = null;
        AOS.reduceGraph();
        return AOS;
    }

    /** @return number of performed locality checks */
    @PortedFrom(file = "AtomicDecomposer.h", name = "getLocChekNumber")
    public long getLocChekNumber() {
        return Modularizer.getNChecks();
    }
}
