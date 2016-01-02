package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/** class to create modules of an ontology wrt module type */
@PortedFrom(file = "Modularity.h", name = "TModularizer")
public class TModularizer implements Serializable {

    /** shared signature signature */
    @PortedFrom(file = "Modularity.h", name = "sig") private TSignature sig;
    /** internal syntactic locality checker */
    @PortedFrom(file = "Modularity.h", name = "Checker") private final LocalityChecker checker;
    /** module as a list of axioms */
    @PortedFrom(file = "Modularity.h", name = "Module") private final List<AxiomInterface> module = new ArrayList<>();
    /** pointer to a sig index; if not NULL then use optimized algo */
    @PortedFrom(file = "Modularity.h", name = "sigIndex") private final SigIndex sigIndex;
    /** true if no atoms are processed ATM */
    @PortedFrom(file = "Modularity.h", name = "noAtomsProcessing") private boolean noAtomsProcessing;
    /** queue of unprocessed entities */
    @PortedFrom(file = "Modularity.h", name = "WorkQueue") private final List<NamedEntity> workQueue = new ArrayList<>();
    /** number of locality check calls */
    @PortedFrom(file = "Modularity.h", name = "nChecks") private long nChecks;
    /** number of non-local axioms */
    @PortedFrom(file = "Modularity.h", name = "nNonLocal") private long nNonLocal;
    @Original private final JFactReasonerConfiguration config;

    /**
     * init c'tor
     * 
     * @param config
     *        config
     * @param c
     *        c
     */
    public TModularizer(JFactReasonerConfiguration config, LocalityChecker c) {
        this.config = config;
        checker = c;
        sig = c.getSignature();
        sigIndex = new SigIndex(checker);
        nChecks = 0;
        nNonLocal = 0;
    }

    /**
     * update SIG wrt the axiom signature
     * 
     * @param axiom
     *        axiom
     */
    @PortedFrom(file = "Modularity.h", name = "addAxiomSig")
    private void addAxiomSig(AxiomInterface axiom) {
        TSignature axiomSig = axiom.getSignature();
        if (sigIndex != null) {
            axiomSig.begin().stream().filter(p -> !sig.containsNamedEntity(p)).forEach(this::addEntity);
        }
    }

    protected void addEntity(NamedEntity p) {
        workQueue.add(p);
        sig.add(p);
    }

    /**
     * add an axiom to a module
     * 
     * @param axiom
     *        axiom
     */
    @PortedFrom(file = "Modularity.h", name = "addAxiomToModule")
    private void addAxiomToModule(AxiomInterface axiom) {
        axiom.setInModule(true);
        module.add(axiom);
        // update the signature
        addAxiomSig(axiom);
    }

    /**
     * @param ax
     *        ax
     * @return true iff an AXiom is non-local
     */
    @PortedFrom(file = "Modularity.h", name = "isNonLocal")
    private boolean isNonLocal(AxiomInterface ax) {
        ++nChecks;
        if (checker.local(ax)) {
            return false;
        }
        ++nNonLocal;
        return true;
    }

    /**
     * add an axiom if it is non-local (or if noCheck is true)
     * 
     * @param ax
     *        ax
     * @param noCheck
     *        noCheck
     */
    @PortedFrom(file = "Modularity.h", name = "addNonLocal")
    private void addNonLocal(AxiomInterface ax, boolean noCheck) {
        if (noCheck || isNonLocal(ax)) {
            addAxiomToModule(ax);
            if (config.isUseADInModuleExtraction() && noAtomsProcessing && ax.getAtom() != null) {
                noAtomsProcessing = false;
                addNonLocal(ax.getAtom().getModule(), true);
                noAtomsProcessing = true;
            }
        }
    }

    /**
     * add all the non-local axioms from given axiom-set AxSet
     * 
     * @param axSet
     *        AxSet
     * @param noCheck
     *        noCheck
     */
    @PortedFrom(file = "Modularity.h", name = "addNonLocal")
    private void addNonLocal(Collection<AxiomInterface> axSet, boolean noCheck) {
        // in the given range but not in module yet
        axSet.stream().filter(q -> !q.isInModule() && q.isInSS()).forEach(q -> addNonLocal(q, noCheck));
    }

    /** build a module traversing axioms by a signature */
    @PortedFrom(file = "Modularity.h", name = "extractModuleQueue")
    private void extractModuleQueue() {
        // init queue with a sig
        sig.begin().forEach(workQueue::add);
        // add all the axioms that are non-local wrt given value of a
        // top-locality
        this.addNonLocal(sigIndex.getNonLocal(sig.topCLocal()), true);
        // main cycle
        while (!workQueue.isEmpty()) {
            NamedEntity entity = workQueue.remove(0);
            // for all the axioms that contains entity in their signature
            this.addNonLocal(sigIndex.getAxioms(entity), false);
        }
    }

    /**
     * extract module wrt presence of a sig index
     * 
     * @param args
     *        args
     */
    @PortedFrom(file = "Modularity.h", name = "extractModule")
    private void extractModule(Collection<AxiomInterface> args) {
        module.clear();
        // clear the module flag in the input
        args.forEach(p -> {
            p.setInModule(false);
            if (p.isUsed()) {
                p.setInSS(true);
            }
        });
        extractModuleQueue();
        args.forEach(p -> p.setInSS(false));
    }

    /**
     * allow the checker to preprocess an ontology if necessary
     * 
     * @param vec
     *        vec
     */
    @PortedFrom(file = "Modularity.h", name = "preprocessOntology")
    public void preprocessOntology(Collection<AxiomInterface> vec) {
        checker.preprocessOntology(vec);
        sigIndex.clear();
        sigIndex.preprocessOntology(vec);
        nChecks += 2 * vec.size();
    }

    /**
     * @param ax
     *        ax
     * @param type
     *        type
     * @return true iff the axiom AX is a tautology wrt given type
     */
    @PortedFrom(file = "Modularity.h", name = "isTautology")
    public boolean isTautology(AxiomInterface ax, ModuleType type) {
        boolean topLocality = type == ModuleType.M_TOP;
        sig = ax.getSignature();
        sig.setLocality(topLocality);
        // axiom is a tautology if it is local wrt its own signature
        boolean toReturn = checker.local(ax);
        if (type != ModuleType.M_STAR || !toReturn) {
            return toReturn;
        }
        // here it is STAR case and AX is local wrt BOT
        sig.setLocality(!topLocality);
        return checker.local(ax);
    }

    /** @return sigIndex (mainly to (un-)register axioms on the fly) */
    @PortedFrom(file = "Modularity.h", name = "getSigIndex")
    public SigIndex getSigIndex() {
        return sigIndex;
    }

    /** @return Locality checker */
    @PortedFrom(file = "Modularity.h", name = "getLocalityChecker")
    public LocalityChecker getLocalityChecker() {
        return checker;
    }

    /**
     * @param begin
     *        begin
     * @param signature
     *        signature
     * @param type
     *        type
     */
    @PortedFrom(file = "Modularity.h", name = "extract")
    public void extract(AxiomInterface begin, @Nullable TSignature signature, ModuleType type) {
        this.extract(Collections.singletonList(begin), signature, type);
    }

    /**
     * extract module wrt SIGNATURE and TYPE from the set of axioms [BEGIN,END)
     * 
     * @param begin
     *        begin
     * @param signature
     *        signature
     * @param type
     *        type
     */
    @PortedFrom(file = "Modularity.h", name = "extract")
    public void extract(Collection<AxiomInterface> begin, @Nullable TSignature signature, ModuleType type) {
        boolean topLocality = type == ModuleType.M_TOP;
        sig = signature;
        checker.setSignatureValue(sig);
        sig.setLocality(topLocality);
        this.extractModule(begin);
        if (type != ModuleType.M_STAR) {
            return;
        }
        // here there is a star: do the cycle until stabilization
        int size;
        List<AxiomInterface> oldModule = new ArrayList<>();
        do {
            size = module.size();
            oldModule.clear();
            oldModule.addAll(module);
            topLocality = !topLocality;
            sig = signature;
            sig.setLocality(topLocality);
            this.extractModule(oldModule);
        } while (size != module.size());
    }

    /** @return get number of checks made */
    @PortedFrom(file = "Modularity.h", name = "getNChecks")
    public long getNChecks() {
        return nChecks;
    }

    /**
     * extract module wrt SIGNATURE and TYPE from O;
     * 
     * @param list
     *        list
     * @param signature
     *        signature
     * @param type
     *        type
     * @return result in the Set
     */
    @PortedFrom(file = "Modularity.h", name = "extractModule")
    public List<AxiomInterface> extractModule(List<AxiomInterface> list, TSignature signature, ModuleType type) {
        this.extract(list, signature, type);
        return module;
    }

    /** @return the last computed module */
    @PortedFrom(file = "Modularity.h", name = "getModule")
    public List<AxiomInterface> getModule() {
        return module;
    }

    /** @return signature */
    @PortedFrom(file = "Modularity.h", name = "getSignature")
    public TSignature getSignature() {
        return sig;
    }
}
