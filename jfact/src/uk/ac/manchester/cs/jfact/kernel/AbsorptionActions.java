package uk.ac.manchester.cs.jfact.kernel;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

public enum AbsorptionActions {
        /** absorb bottom action */
        B('B') {
            @Override
            public boolean execute(Axiom ax, AxiomSet tb) {
                return ax.absorbIntoBottom();
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
                return tb.processNewAxiom(ax.simplifyCN());
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
                return tb.split(ax);
            }
        };
        private char c;

        private AbsorptionActions(char ch) {
            c = ch;
        }

        /** @param ch
         * @return ABS for ch, or null if no match is found */
        public static AbsorptionActions get(char ch) {
            for (AbsorptionActions v : values()) {
                if (v.c == ch) {
                    return v;
                }
            }
            throw new ReasonerInternalException("Incorrect absorption flags given: " + ch);
        }

    public abstract boolean execute(Axiom ax, AxiomSet tb);
    }
