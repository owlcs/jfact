package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;

public final class RoleAutomaton {
	/** all transitions of the automaton, groupped by a starting state */
	private final List<RAStateTransitions> base = new ArrayList<RAStateTransitions>();
	/** maps original automata state into the new ones (used in copyRA) */
	private int[] map = new int[0];
	/** initial state of the next automaton in chain */
	private int initialRA;
	/** flag whether automaton is input safe */
	private boolean inputSafe;
	/** flag whether automaton is output safe */
	private boolean outputSafe;

	/** make sure that STATE exists in the automaton (update ton's size) */
	private void ensureState(final int state) {
		if (state >= base.size()) {
			Helper.resize(base, state + 1);
		}
		for (int i = 0; i < base.size(); i++) {
			if (base.get(i) == null) {
				base.set(i, new RAStateTransitions());
			}
		}
	}

	public RoleAutomaton() {
		initialRA = 0;
		inputSafe = true;
		outputSafe = true;
		ensureState(1);
	}

	/** make the beginning of the chain */
	public void initChain(final int from) {
		initialRA = from;
	}

	/** add an Automaton to the chain with a default final state */
	public boolean addToChain(final RoleAutomaton RA, final boolean oSafe) {
		return addToChain(RA, oSafe, size() + 1);
	}

	// i/o safety
	/** get the i-safe value */
	public boolean isISafe() {
		return inputSafe;
	}

	/** get the o-safe value */
	public boolean isOSafe() {
		return outputSafe;
	}

	// add single RA
	/** add RA from simple subrole to given one */
	public void addSimpleRA(final RoleAutomaton RA) {
		boolean ok = base.get(initial).addToExisting(RA.base.get(initial).begin().get(0));
		assert ok;
	}

	/** add RA from a subrole to given one */
	public void addRA(final RoleAutomaton RA) {
		initChain(initial);
		addToChain(RA, /* oSafe= */false, final_state);
	}

	/**
	 * add TRANSition leading from a given STATE; check whether all states are
	 * correct
	 */
	public void addTransitionSafe(final int state, final RATransition trans) {
		ensureState(state);
		ensureState(trans.final_state());
		addTransition(state, trans);
	}

	/** state that the automaton is i-unsafe */
	public void setIUnsafe() {
		inputSafe = false;
	}

	/** state that the automaton is o-unsafe */
	public void setOUnsafe() {
		outputSafe = false;
	}

	/** check whether transition between FROM and TO breaks safety */
	public void checkTransition(final int from, final int to) {
		if (from == final_state) {
			setOUnsafe();
		}
		if (to == initial) {
			setIUnsafe();
		}
	}

	/**
	 * add TRANSition leading from a state FROM; all states are known to fit the
	 * ton
	 */
	public void addTransition(final int from, final RATransition trans) {
		checkTransition(from, trans.final_state());
		base.get(from).add(trans);
	}

	/** make the internal chain transition (between chainState and TO) */
	public void nextChainTransition(final int to) {
		addTransition(initialRA, new RATransition(to));
		initialRA = to;
	}

	/** get the initial state */
	public static final int initial = 0;
	/** get the final state */
	public static final int final_state = 1;

	/** create new state */
	public int newState() {
		int ret = base.size();
		ensureState(ret);
		return ret;
	}

	/** get the 1st (multi-)transition starting in STATE */
	public RAStateTransitions begin(final int state) {
		return base.get(state);
	}

	/** return number of distinct states */
	public int size() {
		return base.size();
	}

	/** set up all transitions passing number of roles */
	public void setup(final int nRoles, final boolean data) {
		for (int i = 0; i < base.size(); ++i) {
			base.get(i).setup(i, nRoles, data);
		}
	}

	public void print(final LogAdapter o) {
		for (int state = 0; state < base.size(); ++state) {
			base.get(state).print(o);
		}
	}

	public void addCopy(final RoleAutomaton RA) {
		for (int i = 0; i < RA.size(); ++i) {
			int from = map[i];
			RAStateTransitions RST = base.get(from);
			RAStateTransitions RSTOrig = RA.base.get(i);
			if (RSTOrig.isEmpty()) {
				continue;
			}
			List<RATransition> begin = RSTOrig.begin();
			for (int j = 0; j < begin.size(); j++) {
				RATransition p = begin.get(j);
				int to = p.final_state();
				RATransition trans = new RATransition(map[to]);
				checkTransition(from, trans.final_state());
				trans.add(p);
				// try to merge transitions going to the original final state
				if (to == 1 && RST.addToExisting(trans)) {
					//delete trans;
				} else {
					RST.add(trans);
				}
			}
		}
	}

	/**
	 * init internal map according to RA size, with new initial state from
	 * chainState and final (FRA) states
	 */
	public void initMap(final int RASize, int fRA) {
		map = Arrays.copyOf(map, RASize);
		// new state in the automaton
		int newState = size() - 1;
		// fill initial state; it is always known in the automata
		map[0] = initialRA;
		// fills the final state; if it is not known -- adjust newState
		if (fRA >= size()) {
			fRA = size(); // make sure we don't create an extra unused state
			++newState;
		}
		map[1] = fRA;
		// check transitions as it may turns out to be a single transition
		checkTransition(initialRA, fRA);
		// set new initial state
		initialRA = fRA;
		// fills the rest of map
		for (int i = 2; i < RASize; ++i) {
			map[i] = ++newState;
		}
		// reserve enough space for the new automaton
		ensureState(newState);
	}

	/**
	 * add an Automaton to the chain that would start from the iRA; OSAFE shows
	 * the safety of a previous automaton in a chain
	 */
	public boolean addToChain(final RoleAutomaton RA, final boolean oSafe, final int fRA) {
		boolean needFinalTrans = fRA < size() && !RA.isOSafe();
		// we can skip transition if chaining automata are i- and o-safe
		if (!oSafe && !RA.isISafe()) {
			nextChainTransition(newState());
		}
		// check whether we need an output transition
		initMap(RA.size(), needFinalTrans ? size() : fRA);
		addCopy(RA);
		if (needFinalTrans) {
			nextChainTransition(fRA);
		}
		return RA.isOSafe();
	}

	public List<RAStateTransitions> getBase() {
		return base;
	}
}
