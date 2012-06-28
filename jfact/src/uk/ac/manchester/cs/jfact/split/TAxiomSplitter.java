package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptTop;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomConceptInclusion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;

public class TAxiomSplitter {
    /// keep the single rename: named concept C in an axiom (C=D or C[=D) into a new name C' and new axiom C'=D or C'[=D
    protected class TRecord {
        ConceptName oldName, newName;
        final List<Axiom> oldAxioms = new ArrayList<Axiom>();
        Axiom newAxiom = null;
        TSignature newAxSig = null;
        final Set<Axiom> Module = new HashSet<Axiom>(); // module for a new axiom

        /// set old axiom as an equivalent AX; create a new one
        void setEqAx(final AxiomEquivalentConcepts ax) {
            oldAxioms.add(ax);
            List<Expression> copy = new ArrayList<Expression>();
            for (ConceptExpression p : ax.getArguments()) {
                if (p.equals(oldName)) {
                    copy.add(newName);
                } else {
                    copy.add(p);
                }
            }
            newAxiom = new AxiomEquivalentConcepts(ax.getOWLAxiom(), copy);
        }

        /// set a new implication axiom based on a (known) set of old ones
        void setImpAx(final ConceptExpression Desc) {
            newAxiom = new AxiomConceptInclusion(null, newName, Desc);
        }
    }

    protected final Set<ConceptName> SubNames = new HashSet<ConceptName>();
    protected final Set<ConceptName> Rejects = new HashSet<ConceptName>();
    protected final List<TRecord> Renames = new ArrayList<TRecord>(),
            R2 = new ArrayList<TRecord>();
    protected final Map<ConceptName, TRecord> ImpRens = new HashMap<ConceptName, TRecord>();
    protected final Map<ConceptName, Set<AxiomConceptInclusion>> ImplNames = new HashMap<ConceptName, Set<AxiomConceptInclusion>>();
    private int newNameId;
    protected TSignature sig = new TSignature(); // seed signature
    protected TModularizer mod = null;
    protected final Set<TSplitVar> RejSplits = new HashSet<TSplitVar>();
    protected final Ontology O;
    SigIndex sigIndex = new SigIndex();

    /// rename old concept into a new one with a fresh name
    protected ConceptName rename(final ConceptName oldName) {
        ConceptExpression c = O.getExpressionManager().concept(
                oldName.getName() + "+" + ++newNameId);
        if (c instanceof ConceptName) {
            return (ConceptName) c;
        }
        return null;
    }

    /// register a record in the ontology
    void registerRec(final TRecord rec) {
        for (Axiom p : rec.oldAxioms) {
            O.retract(p);
            sigIndex.unregisterAx(p);
        }
        O.add(rec.newAxiom);
        sigIndex.registerAx(rec.newAxiom);
    }

    /// unregister a record
    void unregisterRec(final TRecord rec) {
        for (Axiom p : rec.oldAxioms) {
            p.setUsed(true);
            sigIndex.registerAx(p);
        }
        rec.newAxiom.setUsed(false);
        sigIndex.unregisterAx(rec.newAxiom);
    }

    /// create a signature of a module corresponding to a new axiom in record
    protected void buildSig(final TRecord rec) {
        sig = rec.newAxiom.getSignature();
        mod.extract(O.getAxioms(), sig, ModuleType.M_STAR); // build a module/signature for the axiom
        rec.newAxSig = mod.getSignature(); // FIXME!! check that SIG wouldn't change after some axiom retractions
        rec.Module.clear();
        rec.Module.addAll(mod.getModule());
    }

    /// add axiom CI in a form C [= D for D != TOP
    protected void addSingleCI(final AxiomConceptInclusion ci) {
        if (ci != null && !(ci.getSupConcept() instanceof ConceptTop)) { // skip axioms with RHS=TOP
            if (ci.getSubConcept() instanceof ConceptName) {
                ConceptName name = (ConceptName) ci.getSubConcept();
                SubNames.add(name);
                if (!ImplNames.containsKey(name)) {
                    ImplNames.put(name, new HashSet<AxiomConceptInclusion>());
                }
                ImplNames.get(name).add(ci);
            }
        }
    }

    /// register all axioms in a form C [= D
    protected void registerCIs() {
        // FIXME!! check for the case (not D) [= (not C) later
        // FIXME!! disjoints here as well
        for (Axiom p : O.getAxioms()) {
            if (p.isUsed() && p instanceof AxiomConceptInclusion) {
                addSingleCI((AxiomConceptInclusion) p);
            }
        }
    }

    /// check whether an equivalent axiom is splittable; @return split name or NULL if not splittable
    protected ConceptName getEqSplit(final AxiomEquivalentConcepts ce) {
        // check whether it is not a synonym definition
        ConceptName splitName = null, name = null;
        int size = ce.size();
        for (ConceptExpression q : ce.getArguments()) {
            if (q instanceof ConceptName) {
                name = (ConceptName) q;
                if (SubNames.contains(name)) { // found a split candidate; save the name
                    if (splitName == null) {
                        splitName = name;
                    } else {
                        // FIXME!! now we jump out right now, later on we're going to do the same with changed axiom
                        return splitName;
                    }
                } else {
                    --size;
                }
            }
        }
        return size > 1 ? splitName : null;
    }

    /// make the axiom split for the equivalence axiom
    protected void makeEqSplit(final AxiomEquivalentConcepts ce) {
        if (ce == null) {
            return;
        }
        ConceptName splitName = getEqSplit(ce);
        if (splitName == null) {
            return;
        }
        // create new record
        TRecord rec = new TRecord();
        rec.oldName = splitName;
        rec.newName = rename(splitName);
        rec.setEqAx(ce);
        registerRec(rec);
        // register rec
        Renames.add(rec);
        //		std::cout << "split " << splitName.getName() << " into " << rec.newName.getName() << "\n";
        //		ce.accept(pr); rec.newAxiom.accept(pr);
    }

    /// split all possible EQ axioms
    protected void registerEQ() {
        // use index instead of iterators will be invalidated during additions
        for (int i = 0; i < O.size(); ++i) {
            if (O.get(i).isUsed() && O.get(i) instanceof AxiomEquivalentConcepts) {
                makeEqSplit((AxiomEquivalentConcepts) O.get(i));
            }
        }
    }

    /// make implication split for a given old NAME
    protected TRecord makeImpSplit(final ConceptName oldName) {
        ConceptName newName = rename(oldName);
        //		std::cout << "split " << oldName.getName() << " into " << newName.getName() << "\n";
        TRecord rec = new TRecord();
        rec.oldName = oldName;
        rec.newName = newName;
        List<Expression> args = new ArrayList<Expression>();
        //O.getExpressionManager().newArgList();
        for (AxiomConceptInclusion s : ImplNames.get(oldName)) {
            rec.oldAxioms.add(s);
            //O.getExpressionManager().addArg((s).getSupConcept());
            args.add(s.getSupConcept());
            //			(*s).accept(pr);
        }
        rec.setImpAx(O.getExpressionManager().and(args));
        registerRec(rec);
        //		rec.newAxiom.accept(pr);
        return rec;
    }

    /// get imp record of a given name; create if necessary
    protected TRecord getImpRec(final ConceptName oldName) {
        if (!ImpRens.containsKey(oldName)) {
            ImpRens.put(oldName, makeImpSplit(oldName));
        }
        return ImpRens.get(oldName);
    }

    /// create all the necessary records for the implications
    protected void createAllImplications() {
        for (TRecord r : Renames) {
            getImpRec(r.oldName);
        }
    }

    /// clear modules of Imp and Eq split records
    protected void clearModules() {
        for (Map.Entry<ConceptName, TRecord> p : ImpRens.entrySet()) {
            p.getValue().newAxSig.clear();
        }
        for (TRecord r : Renames) {
            r.newAxSig.clear();
        }
    }

    /// check whether the record is independent wrt modularity; @return true iff split was incorrect
    protected boolean checkSplitCorrectness(final TRecord rec) {
        if (Rejects.contains(rec.oldName)) {
            //		unsplit:	// restore the old axiom, get rid of the new one
            unregisterRec(rec);
            return true;
        }
        TRecord imp = getImpRec(rec.oldName);
        if (imp.newAxSig.size() == 0) {
            buildSig(imp);
        }
        buildSig(rec);
        if (rec.newAxSig.containsNamedEntity(rec.oldName)
                || !rec.newAxSig.intersect(imp.newAxSig).isEmpty()) {
            // mark name as rejected, un-register imp
            Rejects.add(rec.oldName);
            unregisterRec(imp);
            unregisterRec(rec);
            return true;
        } else // keep the split
        {
            R2.add(rec);
            return false;
        }
    }

    /// move all independent splits in R2; delete all the rest
    protected void keepIndependentSplits() {
        boolean change;
        do {
            change = false;
            clearModules();
            for (TRecord r : Renames) {
                change |= checkSplitCorrectness(r);
            }
            Renames.clear();
            Renames.addAll(R2);
            R2.clear();
        } while (change);
    }

    /// split all implications corresponding to oldName; @return split pointer
    protected TSplitVar splitImplicationsFor(final ConceptName oldName) {
        // check whether we already did translation for such a name
        if (O.Splits.hasCN(oldName)) {
            return O.Splits.get(oldName);
        }
        TRecord rec = getImpRec(oldName);
        // create new split
        TSplitVar split = new TSplitVar();
        split.oldName = oldName;
        split.addEntry(rec.newName, rec.newAxSig, rec.Module);
        O.Splits.set(oldName, split);
        return split;
    }

    /// split all implications for which equivalences were split as well
    protected void splitImplications() {
        for (TRecord r : Renames) {
            if (!Rejects.contains(r.oldName)) {
                TSplitVar split = splitImplicationsFor(r.oldName);
                split.addEntry(r.newName, r.newAxSig, r.Module);
            } else {
                unregisterRec(r);
            }
        }
    }

    public TAxiomSplitter(final Ontology o) {
        //pr(std::cout);
        newNameId = 0;
        O = o;
        mod = new TModularizer(new SyntacticLocalityChecker(sig));
        sigIndex.processRange(o.getAxioms());
        mod.setSigIndex(sigIndex);
    }

    public void buildSplit() {
        // first make a set of named concepts C s.t. C [= D is in the ontology
        registerCIs();
        // now check if some of the C's contains in an equivalence axioms
        registerEQ();
        if (Renames.size() == 0) {
            return;
        }
        // make records for the implications
        createAllImplications();
        // here we have a maximal split; check whether modules are fine
        keepIndependentSplits();
        // now R2 contains all separated axioms; make one replacement for every C [= D axiom
        splitImplications();
    }
}
