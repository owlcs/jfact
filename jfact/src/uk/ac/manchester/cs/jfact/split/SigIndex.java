package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;

public class SigIndex {
    /// map between entities and axioms that contains them in their signature
    MultiMap<NamedEntity, Axiom> Base = new MultiMap<NamedEntity, Axiom>();
    /// locality checker
    SyntacticLocalityChecker Checker;
    /// sets of axioms non-local wrt the empty signature
    Set<Axiom> NonLocalTrue = new HashSet<Axiom>();
    Set<Axiom> NonLocalFalse = new HashSet<Axiom>();
    /// empty signature to test the non-locality
    TSignature emptySig = new TSignature();
    /// number of registered axioms
    int nRegistered = 0;
    /// number of registered axioms
    int nUnregistered = 0;

    /// add an axiom AX to an axiom set AXIOMS
    void add(final Collection<Axiom> axioms, final Axiom ax) {
        axioms.add(ax);
    }

    /// remove an axiom AX from an axiom set AXIOMS
    void remove(final Collection<Axiom> axioms, final Axiom ax) {
        axioms.remove(ax);
    }

    // access to statistics
    /// get number of ever processed axioms
    int nProcessedAx() {
        return this.nRegistered;
    }

    /// get number of currently registered axioms
    int nRegisteredAx() {
        return this.nRegistered - this.nUnregistered;
    }

    /// add axiom AX to the non-local set with top-locality value TOP
    void checkNonLocal(final Axiom ax, final boolean top) {
        this.emptySig.setLocality(top);
        if (!this.Checker.local(ax)) {
            if (top) {
                this.NonLocalFalse.add(ax);
            } else {
                this.NonLocalTrue.add(ax);
            }
        }
    }

    /// empty c'tor
    public SigIndex() {
        this.Checker = new SyntacticLocalityChecker(this.emptySig);
    }

    // work with axioms
    /// register an axiom
    public void registerAx(final Axiom ax) {
        for (NamedEntity p : ax.getSignature().begin()) {
            this.Base.get(p).add(ax);
        }
        // check whether the axiom is non-local
        this.checkNonLocal(ax, /* top= */false);
        this.checkNonLocal(ax, /* top= */true);
        ++this.nRegistered;
    }

    /// unregister an axiom AX
    public void unregisterAx(final Axiom ax) {
        for (NamedEntity p : ax.getSignature().begin()) {
            this.Base.get(p).remove(ax);
        }
        // remove from the non-locality
        this.NonLocalFalse.remove(ax);
        this.NonLocalTrue.remove(ax);
        ++this.nUnregistered;
    }

    /// process an axiom wrt its Used status
    public void processAx(final Axiom ax) {
        if (ax.isUsed()) {
            this.registerAx(ax);
        } else {
            this.unregisterAx(ax);
        }
    }

    /// process the range [begin,end) of axioms
    public void processRange(final Collection<Axiom> c) {
        for (Axiom ax : c) {
            this.processAx(ax);
        }
    }

    // get the set by the index
    /// given an entity, return a set of all axioms that tontain this entity in a signature
    public Collection<Axiom> getAxioms(final NamedEntity entity) {
        return this.Base.get(entity);
    }

    /// get the non-local axioms with top-locality value TOP
    public Set<Axiom> getNonLocal(final boolean top) {
        return top ? this.NonLocalFalse : this.NonLocalTrue;
    }
}
