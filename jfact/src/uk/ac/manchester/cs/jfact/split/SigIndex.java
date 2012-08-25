package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;

public class SigIndex {
    /** map between entities and axioms that contains them in their signature */
    MultiMap<NamedEntity, Axiom> Base = new MultiMap<NamedEntity, Axiom>();
    /** locality checker */
    LocalityChecker Checker;
    /** sets of axioms non-local wrt the empty signature */
    Set<Axiom> NonLocalTrue = new HashSet<Axiom>();
    Set<Axiom> NonLocalFalse = new HashSet<Axiom>();
    /** empty signature to test the non-locality */
    TSignature emptySig = new TSignature();
    /** number of registered axioms */
    int nRegistered = 0;
    /** number of registered axioms */
    int nUnregistered = 0;

    // access to statistics
    /** @return number of ever processed axioms */
    public int nProcessedAx() {
        return nRegistered;
    }

    /** add axiom AX to the non-local set with top-locality value TOP */
    private void checkNonLocal(Axiom ax, boolean top) {
        emptySig.setLocality(top);
        Checker.setSignatureValue(emptySig);
        if (!Checker.local(ax)) {
            if (top) {
                NonLocalFalse.add(ax);
            } else {
                NonLocalTrue.add(ax);
            }
        }
    }

    /** empty c'tor */
    public SigIndex(LocalityChecker c) {
        Checker = c;
    }

    // work with axioms
    /** register an axiom */
    private void registerAx(Axiom ax) {
        for (NamedEntity p : ax.getSignature().begin()) {
            Base.put(p, ax);
        }
        // check whether the axiom is non-local
        checkNonLocal(ax, false);
        checkNonLocal(ax, true);
        ++nRegistered;
    }

    /** unregister an axiom AX */
    private void unregisterAx(Axiom ax) {
        for (NamedEntity p : ax.getSignature().begin()) {
            Base.get(p).remove(ax);
        }
        // remove from the non-locality
        NonLocalFalse.remove(ax);
        NonLocalTrue.remove(ax);
        ++nUnregistered;
    }

    /** process an axiom wrt its Used status */
    public void processAx(Axiom ax) {
        if (ax.isUsed()) {
            registerAx(ax);
        } else {
            unregisterAx(ax);
        }
    }

    // / preprocess given set of axioms
    public void preprocessOntology(Collection<Axiom> axioms) {
        for (Axiom ax : axioms) {
            processAx(ax);
        }
    }

    // / clear internal structures
    public void clear() {
        Base.clear();
        NonLocalFalse.clear();
        NonLocalTrue.clear();
    }

    // get the set by the index
    /** given an entity, return a set of all axioms that tontain this entity in */
    // a signature
    public Collection<Axiom> getAxioms(NamedEntity entity) {
        final Collection<Axiom> collection = Base.get(entity);
        return collection;
    }

    /** get the non-local axioms with top-locality value TOP */
    public Set<Axiom> getNonLocal(boolean top) {
        return top ? NonLocalFalse : NonLocalTrue;
    }
}
