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
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nullable;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;

/** atomical decomposer of the ontology */
@PortedFrom(file = "AtomicDecomposer.h", name = "AtomicDecomposer")
public class AtomicDecomposer implements Serializable {

    /** atomic structure to build */
    @PortedFrom(file = "AtomicDecomposer.h", name = "AOS") private AOStructure aos = null;
    /** modularizer to build modules */
    @PortedFrom(file = "AtomicDecomposer.h", name = "pModularizer") private final TModularizer modularizer;
    /** tautologies of the ontology */
    @PortedFrom(file = "AtomicDecomposer.h", name = "Tautologies") private final List<AxiomInterface> tautologies = new ArrayList<>();
    /** progress indicator */
    @PortedFrom(file = "AtomicDecomposer.h", name = "PI") private ProgressIndicatorInterface pi = null;
    /** fake atom that represents the whole ontology */
    @PortedFrom(file = "AtomicDecomposer.h", name = "rootAtom") private TOntologyAtom rootAtom = null;
    /** module type for current AOS creation */
    @PortedFrom(file = "AtomicDecomposer.h", name = "type") private ModuleType type;

    /**
     * @param c
     *        modularizer
     */
    public AtomicDecomposer(TModularizer c) {
        modularizer = c;
    }

    /** restore all tautologies back */
    @PortedFrom(file = "AtomicDecomposer.h", name = "restoreTautologies")
    private void restoreTautologies() {
        tautologies.forEach(p -> p.setUsed(true));
    }

    /**
     * @param pi
     *        progress indicator to use
     */
    @PortedFrom(file = "AtomicDecomposer.h", name = "setProgressIndicator")
    public void setProgressIndicator(ProgressIndicatorInterface pi) {
        this.pi = pi;
    }

    /**
     * remove tautologies (axioms that are always local) from the ontology
     * temporarily
     * 
     * @param o
     *        O
     */
    @PortedFrom(file = "AtomicDecomposer.h", name = "removeTautologies")
    private void removeTautologies(Ontology o) {
        // we might use it for another decomposition
        tautologies.clear();
        AtomicLong nAx = new AtomicLong(0);
        o.getAxioms().stream().filter(AxiomInterface::isUsed).forEach(p -> checkAndAdd(nAx, p));
        if (pi != null) {
            pi.setLimit(nAx.get());
        }
    }

    protected void checkAndAdd(AtomicLong nAx, AxiomInterface p) {
        // check whether an axiom is local wrt its own signature
        modularizer.extract(p, p.getSignature(), type);
        if (modularizer.isTautology(p, type)) {
            tautologies.add(p);
            p.setUsed(false);
        } else {
            nAx.incrementAndGet();
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
    @Nullable
    @PortedFrom(file = "AtomicDecomposer.h", name = "buildModule")
    private TOntologyAtom buildModule(@Nullable TSignature sig, TOntologyAtom parent) {
        // build a module for a given signature
        modularizer.extract(parent.getModule(), sig, type);
        List<AxiomInterface> module = modularizer.getModule();
        // if module is empty (empty bottom atom) -- do nothing
        if (module.isEmpty()) {
            return null;
        }
        // here the module is created; report it
        if (pi != null) {
            pi.incIndicator();
        }
        // check if the module corresponds to a PARENT one; modules are the same
        // iff their sizes are the same
        if (parent != rootAtom && module.size() == parent.getModule().size()) {
            return parent;
        }
        // create new atom with that module
        TOntologyAtom atom = aos.newAtom();
        atom.setModule(module);
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
    @Nullable
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
        atom.getModule().stream().filter(q -> !q.equals(ax)).forEach(q -> atom.addDepAtom(createAtom(q, atom)));
        return atom;
    }

    /** @return all tautologies */
    @Original
    public List<AxiomInterface> getTautologies() {
        return new ArrayList<>(tautologies);
    }

    /** @return the atom structure */
    @PortedFrom(file = "AtomicDecomposer.h", name = "getAOS")
    public AOStructure getAOS() {
        return aos;
    }

    /**
     * @param o
     *        O
     * @param t
     *        t
     * @return the atomic structure for given module type T
     */
    @PortedFrom(file = "AtomicDecomposer.h", name = "getAOS")
    public AOStructure getAOS(Ontology o, ModuleType t) {
        // remember the type of the module
        type = t;
        // prepare a new AO structure
        aos = new AOStructure();
        // init semantic locality checker
        modularizer.preprocessOntology(o.getAxioms());
        // we don't need tautologies here
        removeTautologies(o);
        // init the root atom
        rootAtom = new TOntologyAtom();
        rootAtom.setModule(new HashSet<>(o.getAxioms()));
        // build the "bottom" atom for an empty signature
        TOntologyAtom bottomAtom = buildModule(new TSignature(), rootAtom);
        if (bottomAtom != null) {
            bottomAtom.getModule().forEach(bottomAtom::addAxiom);
        }
        // create atoms for all the axioms in the ontology
        o.getAxioms().stream().filter(p -> p.isUsed() && p.getAtom() == null).forEach(p -> createAtom(p, rootAtom));
        // restore tautologies in the ontology
        restoreTautologies();
        rootAtom = null;
        aos.reduceGraph();
        return aos;
    }

    /** @return number of performed locality checks */
    @PortedFrom(file = "AtomicDecomposer.h", name = "getLocChekNumber")
    public long getLocChekNumber() {
        return modularizer.getNChecks();
    }
}
