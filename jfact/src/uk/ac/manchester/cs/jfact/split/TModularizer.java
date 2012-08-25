package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/** class to create modules of an ontology wrt module type */
public class TModularizer {
    /** shared signature signature */
    TSignature sig;
    /** internal syntactic locality checker */
    LocalityChecker Checker;
    /** signature updater */
    // TSignatureUpdater Updater;
    /** module as a list of axioms */
    List<Axiom> Module = new ArrayList<Axiom>();
    /** pointer to a sig index; if not NULL then use optimized algo */
    SigIndex sigIndex = null;
    // / true if no atoms are processed ATM
    boolean noAtomsProcessing;
    /** queue of unprocessed entities */
    List<NamedEntity> WorkQueue = new ArrayList<NamedEntity>();
    /** number of locality check calls */
    long nChecks;
    /** number of non-local axioms */
    long nNonLocal;
    private JFactReasonerConfiguration config;

    /** update SIG wrt the axiom signature */
    void addAxiomSig(Axiom axiom) {
        TSignature axiomSig = axiom.getSignature();
        if (sigIndex != null) {
            for (NamedEntity p : axiomSig.begin()) {
                if (!sig.containsNamedEntity(p)) {
                    WorkQueue.add(p);
                    sig.add(p);
                }
            }
        }
    }

    /** add an axiom to a module */
    void addAxiomToModule(Axiom axiom) {
        axiom.setInModule(true);
        Module.add(axiom);
        // update the signature
        addAxiomSig(axiom);
    }

    /** set sig index to a given value */
    public void setSigIndex(SigIndex p) {
        sigIndex = p;
        nChecks += 2 * p.nProcessedAx();
        nNonLocal += p.getNonLocal(false).size() + p.getNonLocal(true).size();
    }

    /** @return true iff an AXiom is non-local */
    boolean isNonLocal(Axiom ax) {
        ++nChecks;
        if (Checker.local(ax)) {
            return false;
        }
        ++nNonLocal;
        return true;
    }

    /** add an axiom if it is non-local (or if noCheck is true) */
    void addNonLocal(Axiom ax, boolean noCheck) {
        if (noCheck || isNonLocal(ax)) {
            addAxiomToModule(ax);
            if (config.isRKG_USE_AD_IN_MODULE_EXTRACTION()) {
                if (noAtomsProcessing && ax.getAtom() != null) {
                    noAtomsProcessing = false;
                    addNonLocal(ax.getAtom().getModule(), true);
                    noAtomsProcessing = true;
                }
            }
        }
    }

    /** mark the ontology O such that all the marked axioms creates the module */
    // wrt SIG
    void extractModuleLoop(Collection<Axiom> args) {
        int sigSize;
        do {
            sigSize = sig.size();
            for (Axiom p : args) {
                if (!p.isInModule() && p.isUsed()) {
                    this.addNonLocal(p, /* noCheck= */false);
                }
            }
        } while (sigSize != sig.size());
    }

    /** add all the non-local axioms from given axiom-set AxSet */
    void addNonLocal(Collection<Axiom> AxSet, boolean noCheck) {
        for (Axiom q : AxSet) {
            if (!q.isInModule() && q.isInSS()) {
                this.addNonLocal(q, noCheck);
            }
        }
    }

    /** build a module traversing axioms by a signature */
    void extractModuleQueue() {
        // init queue with a sig
        for (NamedEntity p : sig.begin()) {
            WorkQueue.add(p);
        }
        // add all the axioms that are non-local wrt given value of a
        // top-locality
        this.addNonLocal(sigIndex.getNonLocal(sig.topCLocal()), true);
        // main cycle
        while (!WorkQueue.isEmpty()) {
            NamedEntity entity = WorkQueue.remove(0);
            // for all the axioms that contains entity in their signature
            this.addNonLocal(sigIndex.getAxioms(entity), false);
        }
    }

    /** extract module wrt presence of a sig index */
    void extractModule(Collection<Axiom> args) {
        Module.clear();
        // clear the module flag in the input
        for (Axiom p : args) {
            p.setInModule(false);
        }
        for (Axiom p : args) {
            if (p.isUsed()) {
                p.setInSS(true);
            }
        }
        extractModuleQueue();
        for (Axiom p : args) {
            p.setInSS(false);
        }
    }

    /** init c'tor */
    public TModularizer(JFactReasonerConfiguration config, LocalityChecker c) {
        this.config = config;
        Checker = c;
        sig = c.getSignature();
        sigIndex = new SigIndex(Checker);
        nChecks = 0;
        nNonLocal = 0;
    }

    /** allow the checker to preprocess an ontology if necessary */
    public void preprocessOntology(Collection<Axiom> vec) {
        Checker.preprocessOntology(vec);
        sigIndex.clear();
        sigIndex.preprocessOntology(vec);
    }

    // / @return true iff the axiom AX is a tautology wrt given type
    boolean isTautology(Axiom ax, ModuleType type) {
        boolean topLocality = type == ModuleType.M_TOP;
        sig = ax.getSignature();
        sig.setLocality(topLocality);
        // axiom is a tautology if it is local wrt its own signature
        boolean toReturn = Checker.local(ax);
        if (type != ModuleType.M_STAR || !toReturn) {
            return toReturn;
        }
        // here it is STAR case and AX is local wrt BOT
        sig.setLocality(!topLocality);
        return Checker.local(ax);
    }

    // / get RW access to the sigIndex (mainly to (un-)register axioms on the
    // fly)
    public SigIndex getSigIndex() {
        return sigIndex;
    }

    /** get access to the Locality checker */
    public LocalityChecker getLocalityChecker() {
        return Checker;
    }

    void extract(Axiom begin, TSignature signature, ModuleType type) {
        this.extract(Collections.singletonList(begin), signature, type);
    }

    /** extract module wrt SIGNATURE and TYPE from the set of axioms */
    // [BEGIN,END)
    public void extract(Collection<Axiom> begin, TSignature signature, ModuleType type) {
        boolean topLocality = type == ModuleType.M_TOP;
        sig = signature;
        Checker.setSignatureValue(sig);
        sig.setLocality(topLocality);
        this.extractModule(begin);
        if (type != ModuleType.M_STAR) {
            return;
        }
        // here there is a star: do the cycle until stabilization
        int size;
        List<Axiom> oldModule = new ArrayList<Axiom>();
        do {
            size = Module.size();
            oldModule.clear();
            oldModule.addAll(Module);
            topLocality = !topLocality;
            sig = signature;
            sig.setLocality(topLocality);
            this.extractModule(oldModule);
        } while (size != Module.size());
    }

    /** get number of checks made */
    long getNChecks() {
        return nChecks;
    }

    /** get number of axioms that were local */
    long getNNonLocal() {
        return nNonLocal;
    }

    /** extract module wrt SIGNATURE and TYPE from O; @return result in the Set */
    public List<Axiom> extractModule(List<Axiom> list, TSignature signature,
            ModuleType type) {
        this.extract(list, signature, type);
        return Module;
    }

    /** get the last computed module */
    public List<Axiom> getModule() {
        return Module;
    }

    /** get access to a signature */
    public TSignature getSignature() {
        return sig;
    }
}
