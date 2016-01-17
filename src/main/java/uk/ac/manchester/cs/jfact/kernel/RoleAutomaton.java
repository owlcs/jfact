package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;

/** role automaton */
@PortedFrom(file = "RAutomaton.h", name = "RoleAutomaton")
public class RoleAutomaton implements Serializable {

    /** get the initial state */
    @Original public static final int INITIAL = 0;
    /** get the final state */
    @PortedFrom(file = "RAutomaton.h", name = "final") public static final int FINAL_STATE = 1;
    /** all transitions of the automaton, groupped by a starting state */
    @PortedFrom(file = "RAutomaton.h", name = "Base") private final List<RAStateTransitions> base = new ArrayList<>();
    /** maps original automata state into the new ones (used in copyRA) */
    @PortedFrom(file = "RAutomaton.h", name = "map") private int[] map = new int[0];
    /** initial state of the next automaton in chain */
    @PortedFrom(file = "RAutomaton.h", name = "iRA") private int initialRA;
    /** flag whether automaton is input safe */
    @PortedFrom(file = "RAutomaton.h", name = "ISafe") private boolean inputSafe;
    /** flag whether automaton is output safe */
    @PortedFrom(file = "RAutomaton.h", name = "OSafe") private boolean outputSafe;
    // automaton completeness
    @PortedFrom(file = "RAutomaton.h", name = "Complete") private boolean complete;

    /** Default constructor. */
    public RoleAutomaton() {
        initialRA = 0;
        inputSafe = true;
        outputSafe = true;
        ensureState(1);
    }

    /**
     * make sure that STATE exists in the automaton (update ton's size)
     * 
     * @param state
     *        state
     */
    @PortedFrom(file = "RAutomaton.h", name = "ensureState")
    private void ensureState(int state) {
        IntStream.range(base.size(), state + 1).forEach(i -> base.add(new RAStateTransitions()));
    }

    /**
     * make the beginning of the chain
     * 
     * @param from
     *        from
     */
    @PortedFrom(file = "RAutomaton.h", name = "initChain")
    public void initChain(int from) {
        initialRA = from;
    }

    /**
     * add an Automaton to the chain with a default state
     * 
     * @param ra
     *        RA
     * @param oSafe
     *        oSafe
     * @return is o safe
     */
    @PortedFrom(file = "RAutomaton.h", name = "addToChain")
    public boolean addToChain(RoleAutomaton ra, boolean oSafe) {
        return addToChain(ra, oSafe, size() + 1);
    }

    // i/o safety
    /** @return the i-safe value */
    @PortedFrom(file = "RAutomaton.h", name = "isISafe")
    public boolean isISafe() {
        return inputSafe;
    }

    /** @return the o-safe value */
    @PortedFrom(file = "RAutomaton.h", name = "isOSafe")
    public boolean isOSafe() {
        return outputSafe;
    }

    // add single RA
    /**
     * add RA from a subrole to given one
     * 
     * @param ra
     *        RA
     */
    @PortedFrom(file = "RAutomaton.h", name = "addRA")
    public void addRA(RoleAutomaton ra) {
        if (ra.isSimple()) {
            boolean ok = base.get(INITIAL).addToExisting(ra.get(INITIAL).begin().get(0));
            assert ok;
        } else {
            initChain(INITIAL);
            addToChain(ra, false, FINAL_STATE);
        }
    }

    /**
     * add TRANSition leading from a given STATE; check whether all states are
     * correct
     * 
     * @param state
     *        state
     * @param trans
     *        trans
     */
    @PortedFrom(file = "RAutomaton.h", name = "addTransitionSafe")
    public void addTransitionSafe(int state, RATransition trans) {
        ensureState(state);
        ensureState(trans.finalState());
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

    /**
     * check whether transition between FROM and TO breaks safety
     * 
     * @param from
     *        from
     * @param to
     *        to
     */
    @PortedFrom(file = "RAutomaton.h", name = "checkTransition")
    public void checkTransition(int from, int to) {
        if (from == FINAL_STATE) {
            setOUnsafe();
        }
        if (to == INITIAL) {
            setIUnsafe();
        }
    }

    /**
     * add TRANSition leading from a state FROM; all states are known to fit the
     * ton
     * 
     * @param from
     *        from
     * @param trans
     *        trans
     */
    @PortedFrom(file = "RAutomaton.h", name = "addTransition")
    public void addTransition(int from, RATransition trans) {
        checkTransition(from, trans.finalState());
        base.get(from).add(trans);
    }

    /**
     * make the internal chain transition (between chainState and TO)
     * 
     * @param to
     *        to
     */
    @PortedFrom(file = "RAutomaton.h", name = "nextChainTransition")
    public void nextChainTransition(int to) {
        addTransition(initialRA, new RATransition(to));
        initialRA = to;
    }

    /** @return new state */
    @PortedFrom(file = "RAutomaton.h", name = "newState")
    public int newState() {
        int ret = base.size();
        ensureState(ret);
        return ret;
    }

    /**
     * @param state
     *        state
     * @return the 1st (multi-)transition starting in STATE
     */
    @PortedFrom(file = "RAutomaton.h", name = "begin")
    public RAStateTransitions getRAStateTransitions(int state) {
        return base.get(state);
    }

    /** @return number of distinct states */
    @PortedFrom(file = "RAutomaton.h", name = "size")
    public int size() {
        return base.size();
    }

    /**
     * set up all transitions passing number of roles
     * 
     * @param nRoles
     *        nRoles
     * @param data
     *        data
     */
    @PortedFrom(file = "RAutomaton.h", name = "setup")
    public void setup(int nRoles, boolean data) {
        for (int i = 0; i < base.size(); ++i) {
            base.get(i).setup(i, nRoles, data);
        }
    }

    /**
     * @param o
     *        o
     */
    @PortedFrom(file = "RAutomaton.h", name = "print")
    public void print(LogAdapter o) {
        base.forEach(p -> p.print(o));
    }

    /**
     * @param ra
     *        RA
     */
    @PortedFrom(file = "RAutomaton.h", name = "addCopy")
    public void addCopy(RoleAutomaton ra) {
        for (int i = 0; i < ra.size(); ++i) {
            int from = map[i];
            RAStateTransitions rst = base.get(from);
            RAStateTransitions rstOrig = ra.base.get(i);
            if (!rstOrig.empty()) {
                rstOrig.begin().forEach(p -> {
                    int to = p.finalState();
                    RATransition trans = new RATransition(map[to]);
                    checkTransition(from, trans.finalState());
                    trans.add(p);
                    // try to merge transitions going to the original state
                    if (to != 1 || !rst.addToExisting(trans)) {
                        rst.add(trans);
                    }
                });
            }
        }
    }

    /**
     * init internal map according to RA size, with new initial state from
     * chainState and (FRA) states
     * 
     * @param raSize
     *        RASize
     * @param fra
     *        fRA
     */
    @PortedFrom(file = "RAutomaton.h", name = "initMap")
    public void initMap(int raSize, int fra) {
        map = Arrays.copyOf(map, raSize);
        // new state in the automaton
        int newState = size() - 1;
        // fill initial state; it is always known in the automata
        map[0] = initialRA;
        // fills the state; if it is not known -- adjust newState
        if (fra >= size()) {
            // make sure we don't create an extra unused state
            fra = size();
            ++newState;
        }
        map[1] = fra;
        // check transitions as it may turns out to be a single transition
        checkTransition(initialRA, fra);
        // set new initial state
        initialRA = fra;
        // fills the rest of map
        for (int i = 2; i < raSize; ++i) {
            map[i] = ++newState;
        }
        // reserve enough space for the new automaton
        ensureState(newState);
    }

    /**
     * add an Automaton to the chain that would start from the iRA; OSAFE shows
     * the safety of a previous automaton in a chain
     * 
     * @param ra
     *        RA
     * @param oSafe
     *        oSafe
     * @param fRA
     *        fRA
     * @return is o safe
     */
    @PortedFrom(file = "RAutomaton.h", name = "addToChain")
    public boolean addToChain(RoleAutomaton ra, boolean oSafe, int fRA) {
        boolean needFinalTrans = fRA < size() && !ra.isOSafe();
        // we can skip transition if chaining automata are i- and o-safe
        if (!oSafe && !ra.isISafe()) {
            nextChainTransition(newState());
        }
        // check whether we need an output transition
        initMap(ra.size(), needFinalTrans ? size() : fRA);
        addCopy(ra);
        if (needFinalTrans) {
            nextChainTransition(fRA);
        }
        return ra.isOSafe();
    }

    /** @return state transitions */
    @PortedFrom(file = "RAutomaton.h", name = "begin")
    public List<RAStateTransitions> getBase() {
        return base;
    }

    /**
     * @param i
     *        index
     * @return transitions
     */
    @PortedFrom(file = "RAutomaton.h", name = "begin")
    public RAStateTransitions get(int i) {
        return base.get(i);
    }

    /**
     * mark an automaton as completed
     * 
     * @param b
     *        new value
     */
    @PortedFrom(file = "RAutomaton.h", name = "setCompleted")
    public void setCompleted(boolean b) {
        complete = b;
    }

    /** @return check whether automaton is completed */
    @PortedFrom(file = "RAutomaton.h", name = "isCompleted")
    public boolean isCompleted() {
        return complete;
    }

    /** @return true iff the automaton is simple */
    @PortedFrom(file = "RAutomaton.h", name = "isSimple")
    public boolean isSimple() {
        return size() == 2 && inputSafe && outputSafe;
    }
}
