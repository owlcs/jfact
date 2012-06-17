package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;

public class SigIndex {
	/// map between entities and axioms that contains them in their signature
	Map<NamedEntity, Set<Axiom>> Base = new HashMap<NamedEntity, Set<Axiom>>();
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
		return nRegistered;
	}

	/// get number of currently registered axioms
	int nRegisteredAx() {
		return nRegistered - nUnregistered;
	}

	/// add axiom AX to the non-local set with top-locality value TOP
	void checkNonLocal(final Axiom ax, final boolean top) {
		emptySig.setLocality(top);
		if (!Checker.local(ax)) {
			if (top) {
				NonLocalFalse.add(ax);
			} else {
				NonLocalTrue.add(ax);
			}
		}
	}

	/// empty c'tor
	public SigIndex() {
		Checker = new SyntacticLocalityChecker(emptySig);
	}

	// work with axioms
	/// register an axiom
	public void registerAx(final Axiom ax) {
		for (NamedEntity p : ax.getSignature().begin()) {
			Base.get(p).add(ax);
		}
		// check whether the axiom is non-local
		checkNonLocal(ax, /* top= */false);
		checkNonLocal(ax, /* top= */true);
		++nRegistered;
	}

	/// unregister an axiom AX
	public void unregisterAx(final Axiom ax) {
		for (NamedEntity p : ax.getSignature().begin()) {
			Base.get(p).remove(ax);
		}
		// remove from the non-locality
		NonLocalFalse.remove(ax);
		NonLocalTrue.remove(ax);
		++nUnregistered;
	}

	/// process an axiom wrt its Used status
	public void processAx(final Axiom ax) {
		if (ax.isUsed()) {
			registerAx(ax);
		} else {
			unregisterAx(ax);
		}
	}

	/// process the range [begin,end) of axioms
	public void processRange(final Collection<Axiom> c) {
		for (Axiom ax : c) {
			processAx(ax);
		}
	}

	// get the set by the index
	/// given an entity, return a set of all axioms that tontain this entity in a signature
	public Set<Axiom> getAxioms(final NamedEntity entity) {
		return Base.get(entity);
	}

	/// get the non-local axioms with top-locality value TOP
	public Set<Axiom> getNonLocal(final boolean top) {
		return top ? NonLocalFalse : NonLocalTrue;
	}
}
