package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry.resolveSynonym;

import java.util.ArrayList;
import java.util.List;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.helpers.Pair;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/** nominal reasoner */
@PortedFrom(file = "ReasonerNom.h", name = "NominalReasoner")
public class NominalReasoner extends DlSatTester {

    /** all nominals defined in TBox */
    @PortedFrom(file = "ReasonerNom.h", name = "Nominals") protected final List<Individual> nominals = new ArrayList<>();

    /**
     * @param tbox
     *        tbox
     * @param options
     *        Options
     */
    public NominalReasoner(TBox tbox, JFactReasonerConfiguration options) {
        super(tbox, options);
        tBox.individuals().filter(pi -> !pi.isSynonym()).forEach(nominals::add);
    }

    /** there are nominals */
    @Override
    @PortedFrom(file = "ReasonerNom.h", name = "hasNominals")
    public boolean hasNominals() {
        return true;
    }

    /** internal nominal reasoning interface */
    /**
     * create cache entry for given singleton
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "ReasonerNom.h", name = "registerNominalCache")
    protected void registerNominalCache(Individual p) {
        dlHeap.setCache(p.getpName(), createModelCache(p.getNode().resolvePBlocker()));
    }

    /**
     * init single nominal node
     * 
     * @param nom
     *        nom
     * @return true if inconsistent
     */
    @PortedFrom(file = "ReasonerNom.h", name = "initNominalNode")
    protected boolean initNominalNode(Individual nom) {
        DlCompletionTree node = cGraph.getNewNode();
        node.setNominalLevel();
        // init nominal with associated node
        nom.setNode(node);
        // ABox is inconsistent
        return initNewNode(node, DepSet.create(), nom.getpName());
    }

    /**
     * use classification information for the nominal P
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "ReasonerNom.h", name = "updateClassifiedSingleton")
    protected void updateClassifiedSingleton(Individual p) {
        registerNominalCache(p);
        if (p.getNode().isPBlocked()) {
            // BP of the individual P is merged to
            int bp = p.getNode().getBlocker().label().getSimpleConcepts().iterator().next().getConcept();
            Individual blocker = (Individual) dlHeap.get(bp).getConcept();
            assert blocker.getNode().equals(p.getNode().getBlocker());
            tBox.addSameIndividuals(p, new Pair(blocker, p.getNode().getPurgeDep().isEmpty()));
        }
    }

    /** prerpare Nominal Reasoner to a new job */
    @Override
    @PortedFrom(file = "ReasonerNom.h", name = "prepareReasoner")
    protected void prepareReasoner() {
        options.getLog().print("\nInitNominalReasoner:");
        restore(1);
        // check whether branching op is not a barrier...
        if (!(bContext instanceof BCBarrier)) {
            // replace it with a barrier
            stack.pop();
            createBCBarrier();
        }
        // save the barrier (also remember the entry to be produced)
        save();
        // clear last session information
        resetSessionFlags();
    }

    /** @return check whether ontology with nominals is consistent */
    @PortedFrom(file = "ReasonerNom.h", name = "consistentNominalCloud")
    public boolean consistentNominalCloud() {
        options.getLog().print("\n\nChecking consistency of an ontology with individuals:\n");
        boolean result;
        // reserve the root for the forthcoming reasoning
        if (initNewNode(cGraph.getRoot(), DepSet.create(), Helper.BP_TOP) || initNominalCloud()) {
            // clash during initialisation
            options.getLog().print("\ninit done\n");
            result = false;
        } else {
            // perform a normal reasoning
            options.getLog().print("\nrunning sat...");
            result = runSat();
            options.getLog().print(" done: ");
            options.getLog().print(result);
            options.getLog().print("\n");
        }
        if (result && noBranchingOps()) {
            // all nominal cloud is classified w/o branching -- make a barrier
            options.getLog().print("InitNominalReasoner[");
            curNode = null;
            createBCBarrier();
            save();
            // the barrier doesn't introduce branching itself
            nonDetShift = 1;
            options.getLog().print("]");
        }
        options.getLog().printTemplate(Templates.CONSISTENT_NOMINAL, result ? "consistent" : "INCONSISTENT");
        if (!result) {
            return false;
        }
        // ABox is consistent . create cache for every nominal in KB
        nominals.forEach(this::updateClassifiedSingleton);
        return true;
    }

    @PortedFrom(file = "ReasonerNom.h", name = "initNominalCloud")
    private boolean initNominalCloud() {
        if (nominals.stream().anyMatch(this::initNominalNode)) {
            return true;
        }
        if (tBox.getRelatedI().stream().anyMatch(this::initRelatedNominals)) {
            return true;
        }
        if (tBox.getDifferent().isEmpty()) {
            return false;
        }
        DepSet dummy = DepSet.create();
        for (List<Individual> r : tBox.getDifferent()) {
            cGraph.initIR();
            if (r.stream().anyMatch(p -> cGraph.setCurIR(resolveSynonym(p).getNode(), dummy))) {
                return true;
            }
            cGraph.finiIR();
        }
        return false;
    }

    @PortedFrom(file = "ReasonerNom.h", name = "initRelatedNominals")
    private boolean initRelatedNominals(Related rel) {
        DlCompletionTree from = resolveSynonym(rel.getA()).getNode();
        DlCompletionTree to = resolveSynonym(rel.getB()).getNode();
        Role r = resolveSynonym(rel.getRole());
        DepSet dep = DepSet.create();
        if (r.isDisjoint() && checkDisjointRoleClash(from, to, r, dep)) {
            return true;
        }
        DlCompletionTreeArc pA = cGraph.addRoleLabel(from, to, false, r, dep);
        return setupEdge(pA, dep, 0);
    }

    /** create BC for the barrier */
    @PortedFrom(file = "Reasoner.h", name = "createBCBarrier")
    private void createBCBarrier() {
        bContext = stack.pushBarrier();
    }

    /** @return true if unsatisfiable */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "checkExtraCond")
    public boolean checkExtraCond() {
        prepareReasoner();
        DepSet dummy = DepSet.create();
        for (int i = 0; i < tBox.getIV().size(); i++) {
            if (addToDoEntry(tBox.getIV().get(i).getNode(), tBox.getConceptsForQueryAnswering().get(i), dummy, "QA")) {
                return true;
            }
        }
        return !checkSatisfiability();
    }
}
