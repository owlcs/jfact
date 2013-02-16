package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import conformance.Original;
import conformance.PortedFrom;

/** signature index */
@PortedFrom(file = "SigIndex.h", name = "SigIndex")
public class SigIndex {
    /** map between entities and axioms that contains them in their signature */
    @PortedFrom(file = "SigIndex.h", name = "Base")
    MultiMap<NamedEntity, AxiomInterface> Base = new MultiMap<NamedEntity, AxiomInterface>();
    /** locality checker */
    @PortedFrom(file = "SigIndex.h", name = "Checker")
    LocalityChecker Checker;
    /** sets of axioms non-local wrt the empty signature */
    @Original
    Set<AxiomInterface> NonLocalTrue = new HashSet<AxiomInterface>();
    @Original
    Set<AxiomInterface> NonLocalFalse = new HashSet<AxiomInterface>();
    /** empty signature to test the non-locality */
    @PortedFrom(file = "SigIndex.h", name = "emptySig")
    TSignature emptySig = new TSignature();
    /** number of registered axioms */
    @PortedFrom(file = "SigIndex.h", name = "nRegistered")
    int nRegistered = 0;
    /** number of registered axioms */
    @PortedFrom(file = "SigIndex.h", name = "nUnregistered")
    int nUnregistered = 0;

    // access to statistics
    /** @return number of ever processed axioms */
    @PortedFrom(file = "SigIndex.h", name = "nProcessedAx")
    public int nProcessedAx() {
        return nRegistered;
    }

    /** add axiom AX to the non-local set with top-locality value TOP */
    @PortedFrom(file = "SigIndex.h", name = "checkNonLocal")
    private void checkNonLocal(AxiomInterface ax, boolean top) {
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

    /** empty c'tor
     * 
     * @param c */
    public SigIndex(LocalityChecker c) {
        Checker = c;
    }

    // work with axioms
    /** register an axiom */
    @PortedFrom(file = "SigIndex.h", name = "registerAx")
    private void registerAx(AxiomInterface ax) {
        for (NamedEntity p : ax.getSignature().begin()) {
            Base.put(p, ax);
        }
        // check whether the axiom is non-local
        checkNonLocal(ax, false);
        checkNonLocal(ax, true);
        ++nRegistered;
    }

    /** unregister an axiom AX */
    @PortedFrom(file = "SigIndex.h", name = "unregisterAx")
    private void unregisterAx(AxiomInterface ax) {
        for (NamedEntity p : ax.getSignature().begin()) {
            Base.get(p).remove(ax);
        }
        // remove from the non-locality
        NonLocalFalse.remove(ax);
        NonLocalTrue.remove(ax);
        ++nUnregistered;
    }

    /** process an axiom wrt its Used status
     * 
     * @param ax */
    @PortedFrom(file = "SigIndex.h", name = "processAx")
    public void processAx(AxiomInterface ax) {
        if (ax.isUsed()) {
            registerAx(ax);
        } else {
            unregisterAx(ax);
        }
    }

    /** preprocess given set of axioms
     * 
     * @param axioms */
    @PortedFrom(file = "SigIndex.h", name = "preprocessOntology")
    public void preprocessOntology(Collection<AxiomInterface> axioms) {
        for (AxiomInterface ax : axioms) {
            processAx(ax);
        }
    }

    /** clear internal structures */
    @PortedFrom(file = "SigIndex.h", name = "clear")
    public void clear() {
        Base.clear();
        NonLocalFalse.clear();
        NonLocalTrue.clear();
    }

    // get the set by the index
    /** given an entity,
     * 
     * @param entity
     * @return a set of all axioms that contain this entity in a signature */
    @PortedFrom(file = "SigIndex.h", name = "getAxioms")
    public Collection<AxiomInterface> getAxioms(NamedEntity entity) {
        final Collection<AxiomInterface> collection = Base.get(entity);
        return collection;
    }

    /** @param top
     * @return the non-local axioms with top-locality value TOP */
    @PortedFrom(file = "SigIndex.h", name = "getNonLocal")
    public Set<AxiomInterface> getNonLocal(boolean top) {
        return top ? NonLocalFalse : NonLocalTrue;
    }
}
