package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;

/// class to create modules of an ontology wrt module type
public class TModularizer {
	/// shared signature signature
	TSignature sig;
	/// internal syntactic locality checker
	LocalityChecker Checker;
	/// signature updater
	//TSignatureUpdater Updater;
	/// module as a list of axioms
	List<Axiom> Module = new ArrayList<Axiom>();
	/// pointer to a sig index; if not NULL then use optimized algo
	SigIndex sigIndex;
	/// queue of unprocessed entities
	List<NamedEntity> WorkQueue = new ArrayList<NamedEntity>();
	/// number of locality check calls
	long nChecks;
	/// number of non-local axioms
	long nNonLocal;

	/// update SIG wrt the axiom signature
	void addAxiomSig(final Axiom axiom) {
		TSignature axiomSig = axiom.getSignature();
		if (sigIndex != null) {
			for (NamedEntity p : axiomSig.begin()) {
				if (!sig.containsNamedEntity(p)) {
					WorkQueue.add(p);
				}
			}
		}
		sig.add(axiomSig);
	}

	/// add an axiom to a module
	void addAxiomToModule(final Axiom axiom) {
		axiom.setInModule(true);
		Module.add(axiom);
		// update the signature
		addAxiomSig(axiom);
	}

	/// set sig index to a given value
	public void setSigIndex(final SigIndex p) {
		sigIndex = p;
		nChecks += 2 * p.nProcessedAx();
		nNonLocal += p.getNonLocal(false).size() + p.getNonLocal(true).size();
	}

	/// allow the checker to preprocess an ontology if necessary
	public void preprocessOntology(final Collection<Axiom> vec) {
		Checker.preprocessOntology(vec);
	}

	/// @return true iff an AXiom is non-local
	boolean isNonLocal(final Axiom ax) {
		++nChecks;
		if (Checker.local(ax)) {
			return false;
		}
		++nNonLocal;
		return true;
	}

	/// add an axiom if it is non-local (or in noCheck is true)
	void addNonLocal(final Axiom ax, final boolean noCheck) {
		if (noCheck || isNonLocal(ax)) {
			addAxiomToModule(ax);
		}
	}

	/// mark the ontology O such that all the marked axioms creates the module wrt SIG
	void extractModuleLoop(final Collection<Axiom> args) {
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

	/// add all the non-local axioms from given axiom-set AxSet
	void addNonLocal(final Set<Axiom> AxSet, final boolean noCheck) {
		for (Axiom q : AxSet) {
			if (!q.isInModule() && q.isInSS()) {
				this.addNonLocal(q, noCheck);
			}
		}
	}

	/// build a module traversing axioms by a signature
	void extractModuleQueue() {
		// init queue with a sig
		for (NamedEntity p : sig.begin()) {
			WorkQueue.add(p);
		}
		// add all the axioms that are non-local wrt given value of a top-locality
		this.addNonLocal(sigIndex.getNonLocal(sig.topCLocal()), /*
																 * noCheck=
																 */true);
		// main cycle
		while (!WorkQueue.isEmpty()) {
			NamedEntity entity = WorkQueue.remove(0);
			// for all the axioms that contains entity in their signature
			this.addNonLocal(sigIndex.getAxioms(entity), /* noCheck= */false);
		}
	}

	/// extract module wrt presence of a sig index
	void extractModule(final Collection<Axiom> args) {
		Module.clear();
		//Module.reserve(args.size());
		// clear the module flag in the input
		for (Axiom p : args) {
			p.setInModule(false);
		}
		//		do {
		//			sigSize = sig.size();
		//			for (Axiom p : args) {
		//				if (!p.isInModule() && p.isUsed() && !Checker.local(p)) {
		//					addAxiomToModule(p);
		//				}
		//			}
		//		} while (sigSize != sig.size());
		if (sigIndex != null) {
			for (Axiom p : args) {
				if (p.isUsed()) {
					p.setInSS(true);
				}
			}
			extractModuleQueue();
			for (Axiom p : args) {
				p.setInSS(false);
			}
		} else {
			extractModuleLoop(args);
		}
	}

	/// init c'tor
	public TModularizer(final LocalityChecker c) {
		Checker = c;
		sig = c.getSignature();
		sigIndex = null;
		nChecks = 0;
		nNonLocal = 0;
	}

	/// get access to the Locality checker
	public LocalityChecker getLocalityChecker() {
		return Checker;
	}

	void extract(final Axiom begin, final TSignature signature, final ModuleType type) {
		this.extract(Collections.singletonList(begin), signature, type);
	}

	/// extract module wrt SIGNATURE and TYPE from the set of axioms [BEGIN,END)
	public void extract(final Collection<Axiom> begin, final TSignature signature,
			final ModuleType type) {
		boolean topLocality = type == ModuleType.M_TOP;
		sig = signature;
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

	/// get number of checks made
	long getNChecks() {
		return nChecks;
	}

	/// get number of axioms that were local
	long getNNonLocal() {
		return nNonLocal;
	}

	/// extract module wrt SIGNATURE and TYPE from O; @return result in the Set
	public List<Axiom> extractModule(final List<Axiom> list, final TSignature signature,
			final ModuleType type) {
		this.extract(list, signature, type);
		return Module;
	}

	/// get the last computed module
	public List<Axiom> getModule() {
		return Module;
	}

	/// get access to a signature
	public TSignature getSignature() {
		return sig;
	}
}
