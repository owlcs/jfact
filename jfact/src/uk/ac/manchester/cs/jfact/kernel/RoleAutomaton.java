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
import conformance.Original;
import conformance.PortedFrom;

@PortedFrom(file = "RAutomaton.h", name = "RoleAutomaton")
public class RoleAutomaton {
    /** all transitions of the automaton, groupped by a starting state */
    @PortedFrom(file = "RAutomaton.h", name = "Base")
    private List<RAStateTransitions> base = new ArrayList<RAStateTransitions>();
    /** maps original automata state into the new ones (used in copyRA) */
    @PortedFrom(file = "RAutomaton.h", name = "map")
    private int[] map = new int[0];
    /** initial state of the next automaton in chain */
    @PortedFrom(file = "RAutomaton.h", name = "iRA")
    private int initialRA;
    /** flag whether automaton is input safe */
    @PortedFrom(file = "RAutomaton.h", name = "ISafe")
    private boolean inputSafe;
    /** flag whether automaton is output safe */
    @PortedFrom(file = "RAutomaton.h", name = "OSafe")
    private boolean outputSafe;

    /** make sure that STATE exists in the automaton (update ton's size) */
    @PortedFrom(file = "RAutomaton.h", name = "ensureState")
    private void ensureState(int state) {
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
    @PortedFrom(file = "RAutomaton.h", name = "initChain")
    public void initChain(int from) {
        initialRA = from;
    }

    /** add an Automaton to the chain with a default state */
    @PortedFrom(file = "RAutomaton.h", name = "addToChain")
    public boolean addToChain(RoleAutomaton RA, boolean oSafe) {
        return addToChain(RA, oSafe, size() + 1);
    }

    // i/o safety
    /** get the i-safe value */
    @PortedFrom(file = "RAutomaton.h", name = "isISafe")
    public boolean isISafe() {
        return inputSafe;
    }

    /** get the o-safe value */
    @PortedFrom(file = "RAutomaton.h", name = "isOSafe")
    public boolean isOSafe() {
        return outputSafe;
    }

    // add single RA
    /** add RA from a subrole to given one */
    @PortedFrom(file = "RAutomaton.h", name = "addRA")
    public void addRA(RoleAutomaton RA) {
        assert !isCompleted();
        if (RA.isSimple()) {
            boolean ok = base.get(initial).addToExisting(
                    RA.getBase().get(initial).begin().get(0));
            assert ok;
        } else {
            initChain(initial);
            addToChain(RA, /* oSafe= */false, final_state);
        }
    }

    /** add TRANSition leading from a given STATE; check whether all states are
     * correct */
    @PortedFrom(file = "RAutomaton.h", name = "addTransitionSafe")
    public void addTransitionSafe(int state, RATransition trans) {
        ensureState(state);
        ensureState(trans.final_state());
        addTransition(state, trans);
    }

    /** state that the automaton is i-unsafe */
    @PortedFrom(file = "RAutomaton.h", name = "setIUnsafe")
    public void setIUnsafe() {
        inputSafe = false;
    }

    /** state that the automaton is o-unsafe */
    @PortedFrom(file = "RAutomaton.h", name = "setOUnsafe")
    public void setOUnsafe() {
        outputSafe = false;
    }

    /** check whether transition between FROM and TO breaks safety */
    @PortedFrom(file = "RAutomaton.h", name = "checkTransition")
    public void checkTransition(int from, int to) {
        if (from == final_state) {
            setOUnsafe();
        }
        if (to == initial) {
            setIUnsafe();
        }
    }

    /** add TRANSition leading from a state FROM; all states are known to fit the
     * ton */
    @PortedFrom(file = "RAutomaton.h", name = "addTransition")
    public void addTransition(int from, RATransition trans) {
        checkTransition(from, trans.final_state());
        base.get(from).add(trans);
    }

    /** make the internal chain transition (between chainState and TO) */
    @PortedFrom(file = "RAutomaton.h", name = "nextChainTransition")
    public void nextChainTransition(int to) {
        addTransition(initialRA, new RATransition(to));
        initialRA = to;
    }

    /** get the initial state */
    @Original
    public static int initial = 0;
    /** get the state */
    @PortedFrom(file = "RAutomaton.h", name = "final")
    public static int final_state = 1;

    /** create new state */
    @PortedFrom(file = "RAutomaton.h", name = "newState")
    public int newState() {
        int ret = base.size();
        ensureState(ret);
        return ret;
    }

    /** get the 1st (multi-)transition starting in STATE */
    @PortedFrom(file = "RAutomaton.h", name = "begin")
    public RAStateTransitions begin(int state) {
        return base.get(state);
    }

    /** return number of distinct states */
    @PortedFrom(file = "RAutomaton.h", name = "size")
    public int size() {
        return base.size();
    }

    /** set up all transitions passing number of roles */
    @PortedFrom(file = "RAutomaton.h", name = "setup")
    public void setup(int nRoles, boolean data) {
        for (int i = 0; i < base.size(); ++i) {
            base.get(i).setup(i, nRoles, data);
        }
    }

    @PortedFrom(file = "RAutomaton.h", name = "print")
    public void print(LogAdapter o) {
        for (int state = 0; state < base.size(); ++state) {
            base.get(state).print(o);
        }
    }

    @PortedFrom(file = "RAutomaton.h", name = "addCopy")
    public void addCopy(RoleAutomaton RA) {
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
                // try to merge transitions going to the original state
                if (to == 1 && RST.addToExisting(trans)) {
} else {
                    RST.add(trans);
                }
            }
        }
    }

    /** init internal map according to RA size, with new initial state from
     * chainState and (FRA) states */
    @PortedFrom(file = "RAutomaton.h", name = "initMap")
    public void initMap(int RASize, int fRA) {
        map = Arrays.copyOf(map, RASize);
        // new state in the automaton
        int newState = size() - 1;
        // fill initial state; it is always known in the automata
        map[0] = initialRA;
        // fills the state; if it is not known -- adjust newState
        if (fRA >= size()) {
            // make sure we don't create an extra unused state
            fRA = size();
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

    /** add an Automaton to the chain that would start from the iRA; OSAFE shows
     * the safety of a previous automaton in a chain */
    @PortedFrom(file = "RAutomaton.h", name = "addToChain")
    public boolean addToChain(RoleAutomaton RA, boolean oSafe, int fRA) {
        assert !isCompleted();
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

    @PortedFrom(file = "RAutomaton.h", name = "begin")
    public List<RAStateTransitions> getBase() {
        return base;
    }

    // automaton completeness
    @PortedFrom(file = "RAutomaton.h", name = "Complete")
    private boolean Complete;

    /** mark an automaton as completed */
    @PortedFrom(file = "RAutomaton.h", name = "setCompleted")
    void setCompleted() {
        Complete = true;
    }

    @PortedFrom(file = "RAutomaton.h", name = "setCompleted")
    void setCompleted(boolean b) {
        Complete = b;
    }

    /** check whether automaton is completed */
    @PortedFrom(file = "RAutomaton.h", name = "isCompleted")
    boolean isCompleted() {
        return Complete;
    }

    /** @return true iff the automaton is simple */
    @PortedFrom(file = "RAutomaton.h", name = "isSimple")
    boolean isSimple() {
        assert isCompleted();
        return size() == 2 && inputSafe && outputSafe;
    }
}
