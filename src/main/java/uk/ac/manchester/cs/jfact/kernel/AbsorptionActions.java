package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

/**
 * @author ignazio
 */
public enum AbsorptionActions {
    /** absorb bottom action */
    B('B') {

        @Override
        public boolean execute(Axiom ax, AxiomSet tb) {
            return ax.absorbIntoBottom(tb.tboxHost);
        }
    },
    /** absorb top action */
    T('T') {

        @Override
        public boolean execute(Axiom ax, AxiomSet tb) {
            return ax.absorbIntoTop(tb.tboxHost);
        }
    },
    /** process simplified CN axiom action */
    E('E') {

        @Override
        public boolean execute(Axiom ax, AxiomSet tb) {
            return tb.processNewAxiom(ax.simplifyCN(tb.tboxHost));
        }
    },
    /** absorb in concept action */
    C('C') {

        @Override
        public boolean execute(Axiom ax, AxiomSet tb) {
            return ax.absorbIntoConcept(tb.tboxHost);
        }
    },
    /** absorb in concept negation action */
    N('N') {

        @Override
        public boolean execute(Axiom ax, AxiomSet tb) {
            return ax.absorbIntoNegConcept(tb.tboxHost);
        }
    },
    /** absorb in concept forall action */
    FA('f') {

        @Override
        public boolean execute(Axiom ax, AxiomSet tb) {
            return tb.processNewAxiom(ax.simplifySForall(tb.tboxHost));
        }
    },
    /** process simplified forall axiom action */
    F('F') {

        @Override
        public boolean execute(Axiom ax, AxiomSet tb) {
            return tb.processNewAxiom(ax.simplifyForall(tb.tboxHost));
        }
    },
    /** absorb into domain axiom action */
    R('R') {

        @Override
        public boolean execute(Axiom ax, AxiomSet tb) {
            return ax.absorbIntoDomain(tb.tboxHost);
        }
    },
    /** split axiom action */
    S('S') {

        @Override
        public boolean execute(Axiom ax, AxiomSet tb) {
            return tb.split(ax, tb.tboxHost);
        }
    };

    private char c;

    private AbsorptionActions(char ch) {
        c = ch;
    }

    /**
     * @param ch
     *        ch
     * @return ABS for ch, or null if no match is found
     */
    public static AbsorptionActions get(char ch) {
        for (AbsorptionActions v : values()) {
            if (v.c == ch) {
                return v;
            }
        }
        throw new ReasonerInternalException("Incorrect absorption flags given: " + ch);
    }

    /**
     * @param ax
     *        ax
     * @param tb
     *        tb
     * @return true if the rule is fired
     */
    public abstract boolean execute(Axiom ax, AxiomSet tb);
}
