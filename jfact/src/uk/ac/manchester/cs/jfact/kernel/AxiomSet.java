package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.InAx.*;

import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;

public final class AxiomSet {
    /** host TBox that holds all concepts/etc */
    protected final TBox tboxHost;
    /** set of axioms that accumilates incoming (and newly created) axioms; Tg */
    private List<Axiom> accumulator = new ArrayList<Axiom>();

    private interface Abs {
        boolean absMethod(Axiom ax);
    }

    /// set of absorption action, in order
    private final List<Abs> actions = new ArrayList<AxiomSet.Abs>();

    /** add already built GCI p */
    private void insertGCI(final Axiom p) {
        tboxHost.getOptions().getAbsorptionLog()
                .print("\n new axiom (", accumulator.size(), "):", p);
        accumulator.add(p);
    }

    /** insert GCI if new; @return true iff already exists */
    private boolean insertIfNew(final Axiom q) {
        if (!accumulator.contains(q)) {
            insertGCI(q);
            return false;
        }
        return true;
    }

    /// helper that inserts an axiom into Accum; @return bool if success
    protected boolean processNewAxiom(final Axiom q) {
        if (q == null) {
            return false;
        }
        if (insertIfNew(q)) {
            return false;
        }
        return true;
    }

    public AxiomSet(final TBox host) {
        tboxHost = host;
    }

    /** add axiom for the GCI C [= D */
    public void addAxiom(final DLTree C, final DLTree D) {
        SAbsInput();
        Axiom p = new Axiom();
        p.add(C);
        p.add(DLTreeFactory.createSNFNot(D));
        insertGCI(p);
    }

    /** get number of (not absorbed) GCIs */
    private int size() {
        return accumulator.size();
    }

    /** @return true if non-concept aborption were executed */
    public boolean wasRoleAbsorptionApplied() {
        String string = "SAbsRApply";
        return InAx.created.containsKey(string);
    }

    /** get GCI of all non-absorbed axioms */
    public DLTree getGCI() {
        List<DLTree> l = new ArrayList<DLTree>();
        for (Axiom p : accumulator) {
            l.add(p.createAnAxiom(null));
        }
        return DLTreeFactory.createSNFAnd(l);
    }

    /** split given axiom */
    protected boolean split(final Axiom p) {
        List<Axiom> splitted = p.split(tboxHost);
        if (splitted.isEmpty()) {
            // nothing to split
            return false;
        }
        for (Axiom q : splitted) {
            if (accumulator.contains(q)) {
                // there is already such an axiom in process; delete it
                return false;
            }
        }
        // do the actual insertion if necessary
        for (Axiom q : splitted) {
            insertGCI(q);
        }
        return true;
    }

    public int absorb() {
        // GCIs to process
        List<Axiom> GCIs = new ArrayList<Axiom>();
        // we will change Accum (via split rule), so indexing and compare with size
        for (int i = 0; i < accumulator.size(); i++) {
            Axiom ax = accumulator.get(i);
            tboxHost.getOptions().getAbsorptionLog().print("\nProcessing (", i, "):");
            if (!absorbGCI(ax)) {
                GCIs.add(ax);
            }
        }
        // clear absorbed and remove them from Accum
        accumulator = GCIs;
        tboxHost.getOptions().getAbsorptionLog()
                .print("\nAbsorption done with ", accumulator.size(), " GCIs left\n");
        printStatistics();
        return size();
    }

    private boolean absorbGCI(final Axiom p) {
        SAbsAction();
        for (Abs abs : actions) {
            if (abs.absMethod(p)) {
                return true;
            }
        }
        tboxHost.getOptions().getAbsorptionLog().print(" keep as GCI");
        return false;
    }

    public boolean initAbsorptionFlags(final String flags) {
        actions.clear();
        for (char c : flags.toCharArray()) {
            switch (c) {
                case 'B':
                    actions.add(new Abs() {
                        public boolean absMethod(final Axiom ax) {
                            return ax.absorbIntoBottom(tboxHost);
                        }
                    });
                    break;
                case 'T':
                    actions.add(new Abs() {
                        public boolean absMethod(final Axiom ax) {
                            return ax.absorbIntoTop(tboxHost);
                        }
                    });
                    break;
                case 'E':
                    actions.add(new Abs() {
                        public boolean absMethod(final Axiom ax) {
                            return processNewAxiom(ax.simplifyCN(tboxHost));
                        }
                    });
                    break;
                case 'C':
                    actions.add(new Abs() {
                        public boolean absMethod(final Axiom ax) {
                            return ax.absorbIntoConcept(tboxHost);
                        }
                    });
                    break;
                case 'N':
                    actions.add(new Abs() {
                        public boolean absMethod(final Axiom ax) {
                            return ax.absorbIntoNegConcept(tboxHost);
                        }
                    });
                    break;
                case 'F':
                    actions.add(new Abs() {
                        public boolean absMethod(final Axiom ax) {
                            return processNewAxiom(ax.simplifyForall(tboxHost));
                        }
                    });
                    break;
                case 'R':
                    actions.add(new Abs() {
                        public boolean absMethod(final Axiom ax) {
                            return ax.absorbIntoDomain(tboxHost);
                        }
                    });
                    break;
                case 'S':
                    actions.add(new Abs() {
                        public boolean absMethod(final Axiom ax) {
                            return split(ax);
                        }
                    });
                    break;
                default:
                    return true;
            }
        }
        tboxHost.getOptions().getAbsorptionLog()
                .print("Init absorption order as ", flags, "\n");
        return false;
    }

    private void printStatistics() {
        if (!created.containsKey("SAbsAction")) {
            return;
        }
        final LogAdapter log = tboxHost.getOptions().getAbsorptionLog();
        log.print("\nAbsorption dealt with ", get("SAbsInput"),
                " input axioms\nThere were made ", get("SAbsAction"),
                " absorption actions, of which:");
        if (get("SAbsRepCN") > 0) {
            log.print("\n\t", get("SAbsRepCN"), " concept name replacements");
        }
        if (get("SAbsRepForall") > 0) {
            log.print("\n\t", get("SAbsRepForall"), " universals replacements");
        }
        if (get("SAbsSplit") > 0) {
            log.print("\n\t", get("SAbsSplit"), " conjunction splits");
        }
        if (get("SAbsBApply") > 0) {
            log.print("\n\t", get("SAbsBApply"), " BOTTOM absorptions");
        }
        if (get("SAbsTApply") > 0) {
            log.print("\n\t", get("SAbsTApply"), " TOP absorptions");
        }
        if (get("SAbsCApply") > 0) {
            log.print("\n\t", get("SAbsCApply"), " concept absorption with ",
                    get("SAbsCAttempt"), " possibilities");
        }
        if (get("SAbsNApply") > 0) {
            log.print("\n\t", get("SAbsNApply"), " negated concept absorption with ",
                    get("SAbsNAttempt"), " possibilities");
        }
        if (get("SAbsRApply") > 0) {
            log.print("\n\t", get("SAbsRApply"), " role domain absorption with ",
                    get("SAbsRAttempt"), " possibilities");
        }
        if (!accumulator.isEmpty()) {
            log.print("\nThere are ", accumulator.size(), " GCIs left");
        }
    }
}
