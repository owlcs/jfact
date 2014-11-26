package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Stream;

import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import conformance.Original;
import conformance.PortedFrom;

/** class to represent transitions from a single state in an automaton */
@PortedFrom(file = "RAutomaton.h", name = "RAStateTransitions")
public class RAStateTransitions implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** all transitions */
    @PortedFrom(file = "RAutomaton.h", name = "Base")
    protected final List<RATransition> base = new ArrayList<>();
    /** check whether there is an empty transition going from this state */
    @PortedFrom(file = "RAutomaton.h", name = "EmptyTransition")
    protected boolean emptyTransition;
    @PortedFrom(file = "RAutomaton.h", name = "ApplicableRoles")
    private final BitSet applicableRoles = new BitSet();
    /** state from which all the transition starts */
    @PortedFrom(file = "RAutomaton.h", name = "from")
    private int from;
    /** flag whether the role is data or not (valid only for simple automata) */
    @PortedFrom(file = "RAutomaton.h", name = "DataRole")
    private boolean dataRole;
    @Original
    private int size = 0;
    /** true iff there is a top transition going from this state */
    @PortedFrom(file = "RAutomaton.h", name = "TopTransition")
    private boolean TopTransition;

    /** @return begin */
    @PortedFrom(file = "RAutomaton.h", name = "begin")
    public List<RATransition> begin() {
        return base;
    }

    /** @return begin */
    @PortedFrom(file = "RAutomaton.h", name = "begin")
    public Stream<RATransition> stream() {
        return base.stream();
    }

    /** Default constructor. */
    public RAStateTransitions() {
        emptyTransition = false;
    }

    /**
     * add a transition from a given state
     * 
     * @param trans
     *        trans
     */
    @PortedFrom(file = "RAutomaton.h", name = "add")
    public void add(RATransition trans) {
        base.add(trans);
        size++;
        if (trans.isEmpty()) {
            emptyTransition = true;
        }
        if (trans.isTop()) {
            TopTransition = true;
        }
    }

    /** @return true iff there is a top-role transition from the state */
    @PortedFrom(file = "RAutomaton.h", name = "hasTopTransition")
    public boolean hasTopTransition() {
        return TopTransition;
    }

    /** @return true iff there are no transitions from this state */
    @PortedFrom(file = "RAutomaton.h", name = "empty")
    public boolean empty() {
        return size == 0;
    }

    /** @return true iff there is an empty transition from the state */
    @PortedFrom(file = "RAutomaton.h", name = "hasEmptyTransition")
    public boolean hasEmptyTransition() {
        return emptyTransition;
    }

    /**
     * print all the transitions starting from the state FROM
     * 
     * @param o
     *        o
     */
    @PortedFrom(file = "RAutomaton.h", name = "print")
    public void print(LogAdapter o) {
        for (int i = 0; i < size; i++) {
            base.get(i).print(o, from);
        }
    }

    /**
     * set up state transitions: no more additions to the structure
     * 
     * @param state
     *        state
     * @param nRoles
     *        nRoles
     * @param data
     *        data
     */
    @PortedFrom(file = "RAutomaton.h", name = "setup")
    public void setup(int state, int nRoles, boolean data) {
        from = state;
        dataRole = data;
        // fills the set of recognisable roles
        for (int i = 0; i < size; i++) {
            for (Role t : base.get(i).begin()) {
                applicableRoles.set(t.getAbsoluteIndex());
            }
        }
    }

    /**
     * add information from TRANS to existing transition between the same
     * states.
     * 
     * @param trans
     *        trans
     * @return false if no such transition found
     */
    @PortedFrom(file = "RAutomaton.h", name = "addToExisting")
    public boolean addToExisting(RATransition trans) {
        int to = trans.final_state();
        boolean tEmpty = trans.isEmpty();
        for (int i = 0; i < size; i++) {
            RATransition p = base.get(i);
            // TODO index in Base
            if (p.final_state() == to && p.isEmpty() == tEmpty) {
                // found existing transition
                p.addIfNew(trans);
                return true;
            }
        }
        // no transition from->to found
        return false;
    }

    /**
     * @param R
     *        R
     * @return true if R is an applicable data role
     */
    @PortedFrom(file = "RAutomaton.h", name = "recognise")
    public boolean recognise(Role R) {
        if (R == null) {
            return false;
        }
        return R.isDataRole() == dataRole
                && applicableRoles.get(R.getAbsoluteIndex());
    }

    /** @return true iff there is only one transition */
    @PortedFrom(file = "RAutomaton.h", name = "isSingleton")
    public boolean isSingleton() {
        return size == 1;
    }

    /** @return state of the 1st transition; used for singletons */
    @PortedFrom(file = "RAutomaton.h", name = "getTransitionEnd")
    public int getTransitionEnd() {
        return base.get(0).final_state();
    }
}
