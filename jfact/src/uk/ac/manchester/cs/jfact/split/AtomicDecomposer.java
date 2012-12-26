package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import conformance.PortedFrom;

/** atomical decomposer of the ontology */
@PortedFrom(file = "AtomicDecomposer.h", name = "AtomicDecomposer")
public class AtomicDecomposer {
    /** atomic structure to build */
    AOStructure AOS = null;
    /** modularizer to build modules */
    TModularizer Modularizer;
    /** tautologies of the ontology */
    List<Axiom> Tautologies = new ArrayList<Axiom>();
    /** progress indicator */
    ProgressIndicatorInterface PI = null;
    /** fake atom that represents the whole ontology */
    TOntologyAtom rootAtom = null;
    /** module type for current AOS creation */
    ModuleType type;

    public AtomicDecomposer(TModularizer c) {
        Modularizer = c;
    }

    // /** initialize signature index (for the improved modularization
    // algorithm) */
    // void initSigIndex(Ontology O) {
    // SigIndex SI = new SigIndex();
    // SI.processRange(O.getAxioms());
    // Modularizer.setSigIndex(SI);
    // }
    /** restore all tautologies back */
@PortedFrom(file="AtomicDecomposer.h",name="restoreTautologies")
    void restoreTautologies() {
        for (Axiom p : Tautologies) {
            p.setUsed(true);
        }
    }

    /** set progress indicator to be PI */
@PortedFrom(file="AtomicDecomposer.h",name="setProgressIndicator")
    void setProgressIndicator(ProgressIndicatorInterface pi) {
        PI = pi;
    }

    // #define RKG_DEBUG_AD
    /** remove tautologies (axioms that are always local) from the ontology */
    // temporarily
@PortedFrom(file="AtomicDecomposer.h",name="removeTautologies")
    void removeTautologies(Ontology O) {
        // we might use it for another decomposition
        Tautologies.clear();
        long nAx = 0;
        for (Axiom p : O.getAxioms()) {
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

    /** build a module for given axiom AX; use parent atom's module as a base */
    // for the module search
@PortedFrom(file="AtomicDecomposer.h",name="buildModule")
    TOntologyAtom buildModule(TSignature sig, TOntologyAtom parent) {
        // build a module for a given signature
        Modularizer.extract(parent.getModule(), sig, type);
        List<Axiom> Module = Modularizer.getModule();
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

    /** create atom for given axiom AX; use parent atom's module as a base for */
    // the module search
@PortedFrom(file="AtomicDecomposer.h",name="createAtom")
    TOntologyAtom createAtom(Axiom ax, TOntologyAtom parent) {
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
        // #ifdef RKG_DEBUG_AD
        /** / do cycle via set to keep the order */
        // typedef std::set<TDLAxiom*> AxSet;
        // Set<Axiom> M=new HashSet<Axiom> ( atom.getModule() );
        // for ( Axiom q : M )
        // #else
        for (Axiom q : atom.getModule()) {
            // #endif
            if (!q.equals(ax)) {
                atom.addDepAtom(createAtom(q, atom));
            }
        }
        return atom;
    }

    public List<Axiom> getTautologies() {
        return new ArrayList<Axiom>(Tautologies);
    }

@PortedFrom(file="AtomicDecomposer.h",name="getAOS")
    public AOStructure getAOS() {
        return AOS;
    }

    /** get the atomic structure for given module type T */
@PortedFrom(file="AtomicDecomposer.h",name="getAOS")
    public AOStructure getAOS(Ontology O, ModuleType t) {
        // remember the type of the module
        type = t;
        // prepare a new AO structure
        AOS = new AOStructure();
        // init semantic locality checker
        // init semantic locality checker
        Modularizer.preprocessOntology(O.getAxioms());
        // we don't need tautologies here
        removeTautologies(O);
        // init the root atom
        rootAtom = new TOntologyAtom();
        rootAtom.setModule(new HashSet<Axiom>(O.getAxioms()));
        // build the "bottom" atom for an empty signature
        TOntologyAtom BottomAtom = buildModule(new TSignature(), rootAtom);
        if (BottomAtom != null) {
            for (Axiom q : BottomAtom.getModule()) {
                BottomAtom.addAxiom(q);
            }
        }
        // create atoms for all the axioms in the ontology
        for (Axiom p : O.getAxioms()) {
            if (p.isUsed() && p.getAtom() == null) {
                createAtom(p, rootAtom);
            }
        }
        // restore tautologies in the ontology
        restoreTautologies();
        // System.out.println("AtomicDecomposer.getAOS()\nThere were "
        // + Modularizer.getNNonLocal() + " non-local axioms out of "
        // + Modularizer.getNChecks() + " totally checked");
        // clear the root atom
        rootAtom = null;
        // reduce graph
        AOS.reduceGraph();
        return AOS;
    }
}
