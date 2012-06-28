package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.kernel.DLDag;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomConceptInclusion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.split.TSplitVar.Entry;

/// all split rules: vector of rules with init and access methods
public class TSplitRules {
    /// class to check whether there is a need to unsplit splitted var
    public class TSplitRule {
        /// signature of equivalent part of the split
        Set<NamedEntity> eqSig;
        /// signature of subsumption part of the split
        Set<NamedEntity> impSig;
        /// pointer to split vertex to activate
        int bpSplit;

        //			/// check whether set SUB contains in the set SUP
        //		static boolean containsIn (  Set< NamedEntity> Sub,  Set< NamedEntity> Sup )
        //			{ return includes ( Sup.begin(), Sup.end(), Sub.begin(), Sub.end() ); }
        //			/// check whether set S1 intersects with the set S2
        //		static boolean intersectsWith (  Set< NamedEntity> S1,  Set< NamedEntity> S2 )
        //		{
        //			SigSet::const_iterator q = S1.begin(), q_end = S1.end(), p = S2.begin(), p_end = S2.end();
        //			while ( p != p_end && q != q_end )
        //			{
        //				if ( *p == *q )
        //					return true;
        //				if ( *p < *q )
        //					++p;
        //				else
        //					++q;
        //			}
        //			return false;
        //		}
        TSplitRule() {}

        /// init c'tor
        TSplitRule(final Set<NamedEntity> es, final Set<NamedEntity> is, final int p) {
            this.eqSig = new HashSet<NamedEntity>(es);
            this.impSig = new HashSet<NamedEntity>(is);
            this.bpSplit = p;
        }

        /// copy c'tor
        TSplitRule(final TSplitRule copy) {
            this(copy.eqSig, copy.impSig, copy.bpSplit);
        }

        // access methods
        /// get bipolar pointer of the rule
        public int bp() {
            return this.bpSplit;
        }

        /// check whether signatures of a rule are related to current signature in such a way that allows rule to fire
        public boolean canFire(final Set<NamedEntity> CurrentSig) {
            return CurrentSig.containsAll(this.eqSig)
                    && this.intersectsWith(this.impSig, CurrentSig);
        }

        /// calculates dep-set for a rule that can fire, write it to DEP.
        public DepSet fireDep(final Set<NamedEntity> CurrentSig,
                final Map<NamedEntity, DepSet> SigDep) {
            DepSet dep = DepSet.create();
            // eqSig is contained in current, so need all
            for (NamedEntity p : this.eqSig) {
                dep = DepSet.plus(dep, SigDep.get(p));
            }
            // impSig has partial intersect with current; 1st common entity is fine
            for (NamedEntity p : this.impSig) {
                if (CurrentSig.contains(p)) {
                    dep = DepSet.plus(dep, SigDep.get(p));
                    break;
                }
            }
            return dep;
        }

        /// check whether set S1 intersects with the set S2
        boolean intersectsWith(final Set<?> S1, final Set<?> S2) {
            for (Object o : S1) {
                if (S2.contains(o)) {
                    return true;
                }
            }
            return false;
        }
    }

    /// all known rules
    List<TSplitRule> Base = new ArrayList<TSplitRule>();
    /// all entities that appears in all the splits in a set
    Set<NamedEntity> PossibleSignature = new HashSet<NamedEntity>();
    /// map between BP and TNamedEntities
    List<NamedEntity> EntityMap = new ArrayList<NamedEntity>();

    public List<TSplitRule> getRules() {
        return this.Base;
    }

    /// add new split rule
    void addSplitRule(final Set<NamedEntity> eqSig, final Set<NamedEntity> impSig,
            final int bp) {
        this.Base.add(new TSplitRule(eqSig, impSig, bp));
    }

    /// calculate single entity based on a named entry ENTRY and possible signature
    NamedEntity getSingleEntity(final NamedEntry entry) {
        if (entry == null) {
            return null;
        }
        NamedEntity ret = entry.getEntity();
        // now keep only known signature concepts
        return this.PossibleSignature.contains(ret) ? ret : null;
    }

    /// create all the split rules by given split set SPLITS
    public void createSplitRules(final TSplitVars Splits) {
        for (TSplitVar p : Splits.getEntries()) {
            this.initSplit(p);
        }
    }

    /// ensure that Map has the same size as DAG, so there would be no access violation
    public void ensureDagSize(final int dagSize) {
        Helper.resize(this.EntityMap, dagSize);
    }

    /// @return named entity corresponding to a given bp
    public NamedEntity getEntity(final int bp) {
        return this.EntityMap.get(bp > 0 ? bp : -bp);
    }

    /// init entity map using given DAG. note that this should be done AFTER rule splits are created!
    public void initEntityMap(final DLDag Dag) {
        int size = Dag.size();
        Helper.resize(this.EntityMap, size);
        this.EntityMap.set(0, null);
        this.EntityMap.set(1, null);
        for (int i = 2; i < size - 1; ++i) {
            this.EntityMap.set(i, this.getSingleEntity(Dag.get(i).getConcept()));
        }
    }

    /// build a set out of signature SIG w/o given ENTITY
    Set<NamedEntity> buildSet(final TSignature sig, final NamedEntity entity) {
        Set<NamedEntity> set = new HashSet<NamedEntity>();
        //	std::cout << "Building set for " << entity.getName() << "\n";
        for (NamedEntity p : sig.begin()) {
            if (p != entity && p instanceof ConceptName) {
                //			std::cout << "In the set: " << (*p).getName() << "\n";
                set.add(p);
            }
        }
        //	std::cout << "done\n";
        // register all elements in the set in PossibleSignature
        this.PossibleSignature.addAll(set);
        return set;
    }

    /// init split as a set-of-sets
    void initSplit(final TSplitVar split) {
        //	std::cout << "Processing split for " << split.oldName.getName() << ":\n";
        Entry p = split.getEntries().get(0);
        Set<NamedEntity> impSet = this.buildSet(p.sig, p.name);
        int bp = split.C.getpBody() + 1; // choose-rule stays next to a split-definition of C
        for (int i = 1; i < split.getEntries().size(); i++) {
            p = split.getEntries().get(i);
            if (p.Module.size() == 1) {
                this.addSplitRule(this.buildSet(p.sig, p.name), impSet, bp);
            } else {
                // make set of all the seed signatures of for p.Module
                Set<TSignature> Out = new HashSet<TSignature>();
                // prepare vector of available entities
                List<NamedEntity> Allowed = new ArrayList<NamedEntity>();
                //			std::cout << "\n\n\nMaking split for module with " << p.name.getName();
                List<Axiom> Module = new ArrayList<Axiom>(p.Module);
                // prepare signature for the process
                TSignature sig = p.sig;
                this.prepareStartSig(Module, sig, Allowed);
                // build all the seed sigs for p.sig
                this.BuildAllSeedSigs(Allowed, sig, Module, Out);
                for (TSignature q : Out) {
                    this.addSplitRule(this.buildSet(q, p.name), impSet, bp);
                }
            }
        }
    }

    /// prepare start signature
    void prepareStartSig(final List<Axiom> Module, final TSignature sig,
            final List<NamedEntity> Allowed) {
        // remove all defined concepts from signature
        for (Axiom p : Module) {
            if (p instanceof AxiomEquivalentConcepts) {
                // we don't need class names here
                for (ConceptExpression q : ((AxiomEquivalentConcepts) p).getArguments()) {
                    // FIXME!! check for the case A=B for named classes
                    if (q instanceof ConceptName) {
                        sig.remove((ConceptName) q);
                    }
                }
            } else {
                if (!(p instanceof AxiomConceptInclusion)) {
                    continue;
                }
                // don't need the left-hand part either if it is a name
                final ConceptExpression c = ((AxiomConceptInclusion) p).getSubConcept();
                if (c instanceof ConceptName) {
                    sig.remove((ConceptName) c);
                }
            }
        }
        // now put every concept name into Allowed
        for (NamedEntity r : sig.begin()) {
            if (r instanceof ConceptName) {
                // concept name
                Allowed.add(r);
            }
        }
    }

    /// build all the seed signatures
    void BuildAllSeedSigs(final List<NamedEntity> Allowed, final TSignature StartSig,
            final List<Axiom> Module, final Set<TSignature> Out) {
        // copy the signature
        TSignature sig = StartSig;
        //	std::cout << "\nBuilding seed signatures:";
        // create a set of allowed entities for the next round
        List<NamedEntity> RecAllowed = new ArrayList<NamedEntity>();
        List<NamedEntity> Keepers = new ArrayList<NamedEntity>();
        Set<Axiom> outModule = new HashSet<Axiom>();
        TModularizer mod = //XXX
        new TModularizer(new SyntacticLocalityChecker(sig));
        for (NamedEntity p : Allowed) {
            if (sig.containsNamedEntity(p)) {
                sig.remove(p);
                //			std::cout << "\nTrying " << (*p).getName() << ": ";
                outModule.addAll(mod.extractModule(Module, sig, ModuleType.M_STAR));
                if (mod.getModule().size() == Module.size()) { // possible to remove one
                    //				std::cout << "remove";
                    RecAllowed.add(p);
                } else {
                    //				std::cout << "keep";
                    Keepers.add(p);
                }
                sig.add(p);
            }
        }
        //	std::cout << "\nDone with " << RecAllowed.size() << " sigs left";
        if (RecAllowed.isEmpty()) // minimal seed signature
        {
            Out.add(StartSig);
            return;
        }
        if (!Keepers.isEmpty()) {
            for (NamedEntity p : RecAllowed) {
                sig.remove(p);
            }
            outModule.addAll(mod.extractModule(Module, sig, ModuleType.M_STAR));
            if (mod.getModule().size() == Module.size()) {
                Out.add(sig);
                return;
            }
        }
        // need to try smaller sigs
        sig = StartSig;
        for (NamedEntity p : RecAllowed) {
            sig.remove(p);
            this.BuildAllSeedSigs(RecAllowed, sig, Module, Out);
            sig.add(p);
        }
    }
}
