package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;

/** signature index */
@PortedFrom(file = "SigIndex.h", name = "SigIndex")
public class SigIndex implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** map between entities and axioms that contains them in their signature */
    @PortedFrom(file = "SigIndex.h", name = "Base")
    private final Multimap<NamedEntity, AxiomInterface> Base = LinkedHashMultimap
            .create();
    /** locality checker */
    @PortedFrom(file = "SigIndex.h", name = "Checker")
    private final LocalityChecker Checker;
    /** sets of axioms non-local wrt the empty signature */
    @Original
    private final Set<AxiomInterface> NonLocalTrue = new HashSet<>();
    @Original
    private final Set<AxiomInterface> NonLocalFalse = new HashSet<>();
    /** empty signature to test the non-locality */
    @PortedFrom(file = "SigIndex.h", name = "emptySig")
    private final TSignature emptySig = new TSignature();
    /** number of registered axioms */
    @PortedFrom(file = "SigIndex.h", name = "nRegistered")
    private int nRegistered = 0;
    /** number of registered axioms */
    @PortedFrom(file = "SigIndex.h", name = "nUnregistered")
    private int nUnregistered = 0;

    // access to statistics
    /** @return number of ever processed axioms */
    @PortedFrom(file = "SigIndex.h", name = "nProcessedAx")
    public int nProcessedAx() {
        return nRegistered;
    }

    /**
     * add axiom AX to the non-local set with top-locality value TOP
     * 
     * @param ax
     *        ax
     * @param top
     *        top
     */
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

    /**
     * empty c'tor
     * 
     * @param c
     *        c
     */
    public SigIndex(LocalityChecker c) {
        Checker = c;
    }

    // work with axioms
    /**
     * register an axiom
     * 
     * @param ax
     *        ax
     */
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

    /**
     * unregister an axiom AX
     * 
     * @param ax
     *        ax
     */
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

    /**
     * process an axiom wrt its Used status
     * 
     * @param ax
     *        ax
     */
    @PortedFrom(file = "SigIndex.h", name = "processAx")
    public void processAx(AxiomInterface ax) {
        if (ax.isUsed()) {
            registerAx(ax);
        } else {
            unregisterAx(ax);
        }
    }

    /**
     * preprocess given set of axioms
     * 
     * @param axioms
     *        axioms
     */
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
    /**
     * given an entity,
     * 
     * @param entity
     *        entity
     * @return a set of all axioms that contain this entity in a signature
     */
    @PortedFrom(file = "SigIndex.h", name = "getAxioms")
    public Collection<AxiomInterface> getAxioms(NamedEntity entity) {
        return Base.get(entity);
    }

    /**
     * @param top
     *        top
     * @return the non-local axioms with top-locality value TOP
     */
    @PortedFrom(file = "SigIndex.h", name = "getNonLocal")
    public Set<AxiomInterface> getNonLocal(boolean top) {
        return top ? NonLocalFalse : NonLocalTrue;
    }
}
