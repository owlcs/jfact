package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import conformance.Original;
import conformance.PortedFrom;

/** class to create modules of an ontology wrt module type */
@PortedFrom(file = "Modularity.h", name = "TModularizer")
public class TModularizer {
    /** shared signature signature */
    @PortedFrom(file = "Modularity.h", name = "sig")
    TSignature sig;
    /** internal syntactic locality checker */
    @PortedFrom(file = "Modularity.h", name = "Checker")
    LocalityChecker Checker;
    /** module as a list of axioms */
    @PortedFrom(file = "Modularity.h", name = "Module")
    List<AxiomInterface> Module = new ArrayList<AxiomInterface>();
    /** pointer to a sig index; if not NULL then use optimized algo */
    @PortedFrom(file = "Modularity.h", name = "sigIndex")
    SigIndex sigIndex = null;
    /** true if no atoms are processed ATM */
    @PortedFrom(file = "Modularity.h", name = "noAtomsProcessing")
    boolean noAtomsProcessing;
    /** queue of unprocessed entities */
    @PortedFrom(file = "Modularity.h", name = "WorkQueue")
    List<NamedEntity> WorkQueue = new ArrayList<NamedEntity>();
    /** number of locality check calls */
    @PortedFrom(file = "Modularity.h", name = "nChecks")
    long nChecks;
    /** number of non-local axioms */
    @PortedFrom(file = "Modularity.h", name = "nNonLocal")
    long nNonLocal;
    @Original
    private JFactReasonerConfiguration config;

    /** update SIG wrt the axiom signature */
    @PortedFrom(file = "Modularity.h", name = "addAxiomSig")
    void addAxiomSig(AxiomInterface axiom) {
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
    @PortedFrom(file = "Modularity.h", name = "addAxiomToModule")
    void addAxiomToModule(AxiomInterface axiom) {
        axiom.setInModule(true);
        Module.add(axiom);
        // update the signature
        addAxiomSig(axiom);
    }

    /** @return true iff an AXiom is non-local */
    @PortedFrom(file = "Modularity.h", name = "isNonLocal")
    boolean isNonLocal(AxiomInterface ax) {
        ++nChecks;
        if (Checker.local(ax)) {
            return false;
        }
        ++nNonLocal;
        return true;
    }

    /** add an axiom if it is non-local (or if noCheck is true) */
    @PortedFrom(file = "Modularity.h", name = "addNonLocal")
    void addNonLocal(AxiomInterface ax, boolean noCheck) {
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

    /** add all the non-local axioms from given axiom-set AxSet */
    @PortedFrom(file = "Modularity.h", name = "addNonLocal")
    void addNonLocal(Collection<AxiomInterface> AxSet, boolean noCheck) {
        for (AxiomInterface q : AxSet) {
            if (!q.isInModule() && q.isInSS()) {
                this.addNonLocal(q, noCheck);
            }
        }
    }

    /** build a module traversing axioms by a signature */
    @PortedFrom(file = "Modularity.h", name = "extractModuleQueue")
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
    @PortedFrom(file = "Modularity.h", name = "extractModule")
    void extractModule(Collection<AxiomInterface> args) {
        Module.clear();
        // clear the module flag in the input
        for (AxiomInterface p : args) {
            p.setInModule(false);
        }
        for (AxiomInterface p : args) {
            if (p.isUsed()) {
                p.setInSS(true);
            }
        }
        extractModuleQueue();
        for (AxiomInterface p : args) {
            p.setInSS(false);
        }
    }

    /** init c'tor
     * 
     * @param config
     * @param c */
    public TModularizer(JFactReasonerConfiguration config, LocalityChecker c) {
        this.config = config;
        Checker = c;
        sig = c.getSignature();
        sigIndex = new SigIndex(Checker);
        nChecks = 0;
        nNonLocal = 0;
    }

    /** allow the checker to preprocess an ontology if necessary
     * 
     * @param vec */
    @PortedFrom(file = "Modularity.h", name = "preprocessOntology")
    public void preprocessOntology(Collection<AxiomInterface> vec) {
        Checker.preprocessOntology(vec);
        sigIndex.clear();
        sigIndex.preprocessOntology(vec);
    }

    /** @return true iff the axiom AX is a tautology wrt given type */
    @PortedFrom(file = "Modularity.h", name = "isTautology")
    boolean isTautology(AxiomInterface ax, ModuleType type) {
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

    /** @return sigIndex (mainly to (un-)register axioms on the fly) */
    @PortedFrom(file = "Modularity.h", name = "getSigIndex")
    public SigIndex getSigIndex() {
        return sigIndex;
    }

    /** @return Locality checker */
    @PortedFrom(file = "Modularity.h", name = "getLocalityChecker")
    public LocalityChecker getLocalityChecker() {
        return Checker;
    }

    @PortedFrom(file = "Modularity.h", name = "extract")
    void extract(AxiomInterface begin, TSignature signature, ModuleType type) {
        this.extract(Collections.singletonList(begin), signature, type);
    }

    /** extract module wrt SIGNATURE and TYPE from the set of axioms [BEGIN,END)
     * 
     * @param begin
     * @param signature
     * @param type */
    @PortedFrom(file = "Modularity.h", name = "extract")
    public void extract(Collection<AxiomInterface> begin, TSignature signature, ModuleType type) {
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
        List<AxiomInterface> oldModule = new ArrayList<AxiomInterface>();
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
    @PortedFrom(file = "Modularity.h", name = "getNChecks")
    long getNChecks() {
        return nChecks;
    }

    /** get number of axioms that were local */
    @PortedFrom(file = "Modularity.h", name = "getNNonLocal")
    long getNNonLocal() {
        return nNonLocal;
    }

    /** extract module wrt SIGNATURE and TYPE from O;
     * 
     * @param list
     * @param signature
     * @param type
     * @return result in the Set */
    @PortedFrom(file = "Modularity.h", name = "extractModule")
    public List<AxiomInterface> extractModule(List<AxiomInterface> list, TSignature signature,
            ModuleType type) {
        this.extract(list, signature, type);
        return Module;
    }

    /** @return the last computed module */
    @PortedFrom(file = "Modularity.h", name = "getModule")
    public List<AxiomInterface> getModule() {
        return Module;
    }

    /** @return signature */
    @PortedFrom(file = "Modularity.h", name = "getSignature")
    public TSignature getSignature() {
        return sig;
    }
}
