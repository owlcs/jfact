package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry.resolveSynonym;

import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.helpers.Pair;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

public final class NominalReasoner extends DlSatTester {
    /** all nominals defined in TBox */
    protected List<Individual> nominals = new ArrayList<Individual>();

    /** there are nominals */
    @Override
    public boolean hasNominals() {
        return true;
    }

    ///		internal nominal reasoning interface
    /** create cache entry for given singleton */
    protected void registerNominalCache(final Individual p) {
        dlHeap.setCache(p.getpName(), createModelCache(p.getNode().resolvePBlocker()));
    }

    /** init single nominal node */
    protected boolean initNominalNode(final Individual nom) {
        DlCompletionTree node = cGraph.getNewNode();
        node.setNominalLevel();
        nom.setNode(node); // init nominal with associated node
        return initNewNode(node, DepSet.create(), nom.getpName()); // ABox is inconsistent
    }

    /** use classification information for the nominal P */
    protected void updateClassifiedSingleton(final Individual p) {
        registerNominalCache(p);
        if (p.getNode().isPBlocked()) {
            // BP of the individual P is merged to
            int bp = p.getNode().getBlocker().label().get_sc().get(0).getConcept();
            Individual blocker = (Individual) dlHeap.get(bp).getConcept();
            assert blocker.getNode().equals(p.getNode().getBlocker());
            tBox.sameIndividuals.put(p, new Pair<Individual, Boolean>(blocker, p
                    .getNode().getPurgeDep().isEmpty()));
        }
    }

    public NominalReasoner(final TBox tbox, final JFactReasonerConfiguration Options,
            final DatatypeFactory datatypeFactory) {
        super(tbox, Options, datatypeFactory);
        for (Individual pi : tBox.i_begin()) {
            if (!pi.isSynonym()) {
                nominals.add(pi);
            }
        }
    }

    /** prerpare Nominal Reasoner to a new job */
    @Override
    protected void prepareReasoner() {
        options.getLog().print("\nInitNominalReasoner:");
        restore(1);
        // check whether branching op is not a barrier...
        if (!(bContext instanceof BCBarrier)) { // replace it with a barrier
            stack.pop();
            createBCBarrier();
        }
        // save the barrier (also remember the entry to be produced)
        save();
        // clear last session information
        resetSessionFlags();
    }

    /** check whether ontology with nominals is consistent */
    public boolean consistentNominalCloud() {
        options.getLog().print(
                "\n\nChecking consistency of an ontology with individuals:\n");
        boolean result = false;
        if (initNewNode(cGraph.getRoot(), DepSet.create(), Helper.bpTOP)
                || initNominalCloud()) {
            options.getLog().print("\ninit done\n");
            result = false;
        } else {
            options.getLog().print("\nrunning sat...");
            result = runSat();
            options.getLog().print(" done: ");
            options.getLog().print(result);
            options.getLog().print("\n");
        }
        if (result && noBranchingOps()) {
            options.getLog().print("InitNominalReasoner[");
            curNode = null;
            createBCBarrier();
            save();
            nonDetShift = 1;
            options.getLog().print("]");
        }
        options.getLog().printTemplate(Templates.CONSISTENT_NOMINAL,
                result ? "consistent" : "INCONSISTENT");
        if (!result) {
            return false;
        }
        for (Individual p : nominals) {
            updateClassifiedSingleton(p);
        }
        return true;
    }

    private boolean initNominalCloud() {
        for (Individual p : nominals) {
            if (initNominalNode(p)) {
                return true;
            }
        }
        for (int i = 0; i < tBox.getRelatedI().size(); i += 2) {
            if (initRelatedNominals(tBox.getRelatedI().get(i))) {
                return true;
            }
        }
        if (tBox.getDifferent().isEmpty()) {
            return false;
        }
        DepSet dummy = DepSet.create();
        for (List<Individual> r : tBox.getDifferent()) {
            cGraph.initIR();
            for (Individual p : r) {
                if (cGraph.setCurIR(resolveSynonym(p).getNode(), dummy)) {
                    return true;
                }
            }
            cGraph.finiIR();
        }
        return false;
    }

    @Override
    protected boolean isNNApplicable(final Role r, final int C, final int stopper) {
        if (!curNode.isNominalNode()) {
            return false;
        }
        if (curNode.isLabelledBy(stopper)) {
            return false;
        }
        List<DlCompletionTreeArc> neighbour = curNode.getNeighbour();
        for (int i = 0; i < neighbour.size(); i++) {
            DlCompletionTreeArc p = neighbour.get(i);
            DlCompletionTree suspect = p.getArcEnd();
            if (p.isPredEdge() && suspect.isBlockableNode() && p.isNeighbour(r)
                    && suspect.isLabelledBy(C)) {
                options.getLog().printTemplate(Templates.NN, suspect.getId());
                return true;
            }
        }
        return false;
    }

    private boolean initRelatedNominals(final Related rel) {
        DlCompletionTree from = resolveSynonym(rel.getA()).getNode();
        DlCompletionTree to = resolveSynonym(rel.getB()).getNode();
        Role R = resolveSynonym(rel.getRole());
        DepSet dep = DepSet.create();
        if (R.isDisjoint() && checkDisjointRoleClash(from, to, R, dep)) {
            return true;
        }
        DlCompletionTreeArc pA = cGraph.addRoleLabel(from, to, false, R, dep);
        return setupEdge(pA, dep, 0);
    }

    /** create BC for the barrier */
    private void createBCBarrier() {
        bContext = stack.pushBarrier();
    }
}
