package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.*;

import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.kernel.Concept.CTTag;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheState;
import uk.ac.manchester.cs.jfact.split.SplitVarEntry;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.split.TSplitVar;
import conformance.PortedFrom;

/** Concept taxonomy */
@PortedFrom(file = "DLConceptTaxonomy.h", name = "DLConceptTaxonomy")
public class DLConceptTaxonomy extends TaxonomyCreator {
    private static final long serialVersionUID = 11000L;
    /** flag shows that subsumption check could be simplified */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "inSplitCheck")
    private boolean inSplitCheck = false;
    /** host tBox */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "tBox")
    private final TBox tBox;
    /** common descendants of all parents of currently classified concept */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "Common")
    private final List<TaxonomyVertex> common = new ArrayList<TaxonomyVertex>();
    // statistic counters
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nConcepts")
    private long nConcepts = 0;
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nTries")
    private long nTries = 0;
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nPositives")
    private long nPositives = 0;
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nNegatives")
    private long nNegatives = 0;
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nSearchCalls")
    private long nSearchCalls = 0;
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nSubCalls")
    private long nSubCalls = 0;
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nNonTrivialSubCalls")
    private long nNonTrivialSubCalls = 0;
    /** number of positive cached subsumptions */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nCachedPositive")
    private long nCachedPositive = 0;
    /** number of negative cached subsumptions */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nCachedNegative")
    private long nCachedNegative = 0;
    /** number of non-subsumptions detected by a sorted reasoning */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nSortedNegative")
    private long nSortedNegative = 0;
    /** number of non-subsumptions because of module reasons */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nModuleNegative")
    private long nModuleNegative = 0;
    // flags
    /** flag to use Bottom-Up search */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "flagNeedBottomUp")
    private boolean flagNeedBottomUp;
    /** number of processed common parents */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nCommon")
    protected int nCommon = 1;
    /** set of possible parents */
    protected final Set<TaxonomyVertex> candidates = new HashSet<TaxonomyVertex>();
    /** whether look into it */
    protected boolean useCandidates = false;
    protected Set<NamedEntity> MPlus;
    protected Set<NamedEntity> MMinus;

    // -- General support for DL concept classification
    /** get access to curEntry as a TConcept */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "curConcept")
    private Concept curConcept() {
        return (Concept) curEntry;
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "enhancedSubs")
    private boolean enhancedSubs(TaxonomyVertex cur) {
        ++nSubCalls;
        if (isValued(cur)) {
            return getValue(cur);
        } else {
            return setValue(cur, enhancedSubs2(cur));
        }
    }

    /** explicitely run TD phase */
    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "runTopDown")
    public void runTopDown() {
        searchBaader(pTax.getTopVertex());
    }

    /** explicitely run BU phase */
    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "runBottomUp")
    public void runBottomUp() {
        try {
            if (propagateUp()) {
                return;
            }
            if (isEqualToTop()) {
                return;
            }
            if (pTax.queryMode()) {
                // after classification -- bottom set up already
                searchBaader(pTax.getBottomVertex());
                return;
            }
            // during classification -- have to find leaf nodes
            for (int i = 0; i < common.size(); i++) {
                TaxonomyVertex p = common.get(i);
                if (p.noNeighbours(false)) {
                    searchBaader(p);
                }
            }
        } finally {
            clearCommon();
        }
    }

    /** actions that to be done BEFORE entry will be classified */
    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "preClassificationActions")
    public void preClassificationActions() {
        ++nConcepts;
        tBox.getOptions().getProgressMonitor()
                .reasonerTaskProgressChanged((int) nConcepts, tBox.getNItems());
    }

    /** check if it is necessary to log taxonomy action */
    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "needLogging")
    protected boolean needLogging() {
        return true;
    }

    /** the only c'tor
     * 
     * @param pTax
     * @param tbox */
    public DLConceptTaxonomy(Taxonomy pTax, TBox tbox) {
        super(pTax);
        tBox = tbox;
    }

    /** process all splits */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "processSplits")
    public void processSplits() {
        for (TSplitVar v : tBox.getSplits().getEntries()) {
            mergeSplitVars(v);
        }
    }

    /** set bottom-up flag
     * 
     * @param GCIs */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "setBottomUp")
    public void setBottomUp(KBFlags GCIs) {
        flagNeedBottomUp = GCIs.isGCI() || GCIs.isReflexive() && GCIs.isRnD();
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "isUnsatisfiable")
    private boolean isUnsatisfiable() {
        Concept p = curConcept();
        if (tBox.isSatisfiable(p)) {
            return false;
        }
        pTax.addCurrentToSynonym(pTax.getBottomVertex());
        return true;
    }

    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "buildSignature")
    public TSignature buildSignature(ClassifiableEntry p) {
        return tBox.getSignature(p);
    }

    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "immediatelyClassified")
    protected boolean immediatelyClassified() {
        if (classifySynonym()) {
            return true;
        }
        if (curConcept().getClassTagPlain() == CTTag.cttTrueCompletelyDefined) {
            return false;
            // true CD concepts can not be unsat
        }
        // after SAT testing plan would be implemented
        tBox.initCache(curConcept(), false);
        return isUnsatisfiable();
    }

    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "needTopDown")
    protected boolean needTopDown() {
        return !(useCompletelyDefined && curEntry.isCompletelyDefined());
    }

    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "needBottomUp")
    protected boolean needBottomUp() {
        // we DON'T need bottom-up phase for primitive concepts during CD-like
        // reasoning
        // if no GCIs are in the TBox (C [= T, T [= X or Y, X [= D, Y [= D)
        // or no reflexive roles w/RnD precent (Refl(R), Range(R)=D)
        return flagNeedBottomUp || !useCompletelyDefined || !curConcept().isPrimitive();
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "testSub")
    private boolean testSub(Concept p, Concept q) {
        assert p != null;
        assert q != null;
        if (q.isSingleton() && q.isPrimitive() && !q.isNominal()) {
            // singleton on the RHS is useless iff it is primitive
            return false;
        }
        if (inSplitCheck) {
            if (q.isPrimitive()) {
                return false;
            }
            return testSubTBox(p, q);
        }
        tBox.getOptions().getLog()
                .printTemplate(Templates.TAX_TRYING, p.getName(), q.getName());
        if (tBox.testSortedNonSubsumption(p, q)) {
            tBox.getOptions().getLog().print("NOT holds (sorted result)");
            ++nSortedNegative;
            return false;
        }
        if (isNotInModule(q.getEntity())) {
            tBox.getOptions().getLog().print("NOT holds (module result)");
            ++nModuleNegative;
            return false;
        }
        switch (tBox.testCachedNonSubsumption(p, q)) {
            case csValid:
                tBox.getOptions().getLog().print("NOT holds (cached result)");
                ++nCachedNegative;
                return false;
            case csInvalid:
                tBox.getOptions().getLog().print("holds (cached result)");
                ++nCachedPositive;
                return true;
            default:
                tBox.getOptions().getLog().print("wasted cache test");
                break;
        }
        return testSubTBox(p, q);
    }

    /** @return true if non-subsumption is due to ENTITY is not in the \bot-module */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "isNotInModule")
    private boolean isNotInModule(NamedEntity entity) {
        if (upDirection) {
            return false;
        }
        TSignature sig = sigStack.peek();
        if (sig != null && entity != null && !sig.containsNamedEntity(entity)) {
            return true;
        }
        return false;
    }

    /** test subsumption via TBox explicitely */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "testSubTBox")
    private boolean testSubTBox(Concept p, Concept q) {
        boolean res = tBox.isSubHolds(p, q);
        // update statistic
        ++nTries;
        if (res) {
            ++nPositives;
        } else {
            ++nNegatives;
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder o = new StringBuilder();
        o.append(String.format(
                Templates.DLCONCEPTTAXONOMY.getTemplate(),
                nTries,
                nPositives,
                nPositives * 100 / Math.max(1, nTries),
                nCachedPositive,
                nCachedNegative,
                nSortedNegative > 0 ? String.format(
                        "Sorted reasoning deals with %s non-subsumptions\n",
                        nSortedNegative) : "",
                nModuleNegative > 0 ? "Modular reasoning deals with " + nModuleNegative
                        + " non-subsumptions\n" : "", nSearchCalls, nSubCalls,
                nNonTrivialSubCalls, nEntries * (nEntries - 1) / Math.max(1, nTries)));
        o.append(super.toString());
        return o.toString();
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "searchBaader")
    private void searchBaader(TaxonomyVertex cur) {
        pTax.setVisited(cur);
        ++nSearchCalls;
        boolean noPosSucc = true;
        List<TaxonomyVertex> neigh = cur.neigh(upDirection);
        int size = neigh.size();
        for (int i = 0; i < size; i++) {
            TaxonomyVertex p = neigh.get(i);
            if (enhancedSubs(p)) {
                if (!pTax.isVisited(p)) {
                    searchBaader(p);
                }
                noPosSucc = false;
            }
        }
        // in case current node is unchecked (no BOTTOM node) -- check it
        // explicitly
        if (!isValued(cur)) {
            setValue(cur, testSubsumption(cur));
        }
        if (noPosSucc && cur.getValue()) {
            pTax.getCurrent().addNeighbour(!upDirection, cur);
        }
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "enhancedSubs1")
    private boolean enhancedSubs1(TaxonomyVertex cur) {
        ++nNonTrivialSubCalls;
        // need to be valued -- check all parents
        // propagate false
        // do this only if the concept is not it M-
        List<TaxonomyVertex> neigh = cur.neigh(!upDirection);
        int size = neigh.size();
        for (int i = 0; i < size; i++) {
            if (!enhancedSubs(neigh.get(i))) {
                return false;
            }
        }
        return testSubsumption(cur);
    }

    /** short-cuf from ENHANCED_SUBS */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "enhancedSubs2")
    private boolean enhancedSubs2(TaxonomyVertex cur) {
        // if bottom-up search and CUR is not a successor of checking entity --
        // return false
        if (upDirection && !cur.isCommon()) {
            return false;
        }
        // for top-down search it's enough to look at defined concepts and
        // non-det ones
        // if (tBox.getOptions().isSplits()) {
        // if (!inSplitCheck && !upDirection && !possibleSub(cur)) {
        // return false;
        // }
        // }
        if (useCandidates && candidates.contains(cur)) {
            return false;
        }
        return enhancedSubs1(cur);
    }

    /** test whether a node could be a super-node of CUR */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "possibleSub")
    private boolean possibleSub(TaxonomyVertex v) {
        Concept C = (Concept) v.getPrimer();
        // non-prim concepts are candidates
        if (!C.isPrimitive()) {
            return true;
        }
        // all others should be in the possible sups list
        return ksStack.peek().isPossibleSub(C);
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "testSubsumption")
    private boolean testSubsumption(TaxonomyVertex cur) {
        Concept testC = (Concept) cur.getPrimer();
        if (upDirection) {
            return testSub(testC, curConcept());
        } else {
            return testSub(curConcept(), testC);
        }
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "propagateOneCommon")
    private void propagateOneCommon(TaxonomyVertex node) {
        // checked if node already was visited this session
        if (pTax.isVisited(node)) {
            return;
        }
        // mark node visited
        pTax.setVisited(node);
        node.setCommon();
        if (node.correctCommon(nCommon)) {
            common.add(node);
        }
        // mark all children
        List<TaxonomyVertex> neigh = node.neigh(false);
        for (int i = 0; i < neigh.size(); i++) {
            propagateOneCommon(neigh.get(i));
        }
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "propagateUp")
    private boolean propagateUp() {
        nCommon = 1;
        List<TaxonomyVertex> list = pTax.getCurrent().neigh(upDirection);
        int size = list.size();
        assert size > 0;
        // there is at least one parent (TOP)
        TaxonomyVertex p = list.get(0);
        // define possible successors of the node
        propagateOneCommon(p);
        pTax.clearVisited();
        for (int i = 1; i < size; i++) {
            p = list.get(i);
            if (p.noNeighbours(!upDirection)) {
                return true;
            }
            if (common.isEmpty()) {
                return true;
            }
            ++nCommon;
            List<TaxonomyVertex> aux = new ArrayList<TaxonomyVertex>(common);
            common.clear();
            propagateOneCommon(p);
            pTax.clearVisited();
            int auxSize = aux.size();
            for (int j = 0; j < auxSize; j++) {
                aux.get(j).correctCommon(nCommon);
            }
        }
        return false;
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "clearCommon")
    private void clearCommon() {
        int size = common.size();
        for (int i = 0; i < size; i++) {
            common.get(i).clearCommon();
        }
        common.clear();
    }

    /** check if no BU classification is required as C=TOP */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "isEqualToTop")
    private boolean isEqualToTop() {
        // check this up-front to avoid Sorted check's flaw wrt equals-to-top
        ModelCacheInterface cache = tBox.initCache(curConcept(), true);
        if (cache.getState() != ModelCacheState.csInvalid) {
            return false;
        }
        // here concept = TOP
        pTax.current.addNeighbour(false, pTax.getTopVertex());
        return true;
    }

    /** @return true iff curEntry is classified as a synonym */
    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "classifySynonym")
    protected boolean classifySynonym() {
        if (super.classifySynonym()) {
            return true;
        }
        if (curConcept().isSingleton()) {
            Individual curI = (Individual) curConcept();
            if (tBox.isBlockedInd(curI)) {
                // check whether current entry is the same as another individual
                Individual syn = tBox.getBlockingInd(curI);
                assert syn.getTaxVertex() != null;
                if (tBox.isBlockingDet(curI)) {
                    // deterministic merge => curI = syn
                    pTax.addCurrentToSynonym(syn.getTaxVertex());
                    return true;
                } else {
                    // non-det merge: check whether it is the same
                    tBox.getOptions()
                            .getLog()
                            .print("\nTAX: trying '", curI.getName(), "' = '",
                                    syn.getName(), "'... ");
                    if (testSubTBox(curI, syn)) {
                        // they are actually the same
                        pTax.addCurrentToSynonym(syn.getTaxVertex());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** after merging, check whether there are extra neighbours that should be
     * taken into account */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "checkExtraParents")
    private void checkExtraParents() {
        inSplitCheck = true;
        for (TaxonomyVertex p : pTax.current.neigh(true)) {
            propagateTrueUp(p);
        }
        pTax.current.clearLinks(true);
        runTopDown();
        List<TaxonomyVertex> vec = new ArrayList<TaxonomyVertex>();
        for (TaxonomyVertex p : pTax.current.neigh(true)) {
            if (!isDirectParent(p)) {
                vec.add(p);
            }
        }
        for (TaxonomyVertex p : vec) {
            p.removeLink(false, pTax.current);
            pTax.current.removeLink(true, p);
        }
        clearLabels();
        inSplitCheck = false;
    }

    /** merge vars came from a given SPLIT together */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "mergeSplitVars")
    private void mergeSplitVars(TSplitVar split) {
        Set<TaxonomyVertex> splitVertices = new HashSet<TaxonomyVertex>();
        TaxonomyVertex v = split.getC().getTaxVertex();
        boolean cIn = v != null;
        if (v != null) {
            splitVertices.add(v);
        }
        for (SplitVarEntry q : split.getEntries()) {
            splitVertices.add(q.concept.getTaxVertex());
        }
        // set V to be a node-to-add
        // FIXME!! check later the case whether both TOP and BOT are there
        if (splitVertices.contains(pTax.getBottomVertex())) {
            v = pTax.getBottomVertex();
        } else if (splitVertices.contains(pTax.getTopVertex())) {
            v = pTax.getTopVertex();
        } else {
            setCurrentEntry(split.getC());
            v = pTax.current;
        }
        if (!v.equals(pTax.current) && !cIn) {
            v.addSynonym(split.getC());
        }
        for (TaxonomyVertex p : splitVertices) {
            mergeVertex(v, p, splitVertices);
        }
        if (v == pTax.current) {
            checkExtraParents();
            pTax.finishCurrentNode();
        }
    }

    /** merge a single vertex V to a node represented by CUR */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "mergeVertex")
    private void mergeVertex(TaxonomyVertex cur, TaxonomyVertex v,
            Set<TaxonomyVertex> excludes) {
        if (!cur.equals(v)) {
            cur.mergeIndepNode(v, excludes, curEntry);
            pTax.removeNode(v);
        }
    }

    /** fill candidates
     * 
     * @param cur
     *            vertex to add to candidates (and its updirection neighbors */
    public void fillCandidates(TaxonomyVertex cur) {
        if (isValued(cur)) {
            if (getValue(cur)) {
                // positive value -- nothing to do
                return;
            }
        } else {
            candidates.add(cur);
        }
        for (TaxonomyVertex p : cur.neigh(true)) {
            fillCandidates(p);
        }
    }

    /** @param plus
     * @param minus */
    public void reclassify(Set<NamedEntity> plus, Set<NamedEntity> minus) {
        MPlus = plus;
        MMinus = minus;
        pTax.deFinalise();
        // fill in an order to
        LinkedList<TaxonomyVertex> queue = new LinkedList<TaxonomyVertex>();
        List<ClassifiableEntry> toProcess = new ArrayList<ClassifiableEntry>();
        queue.add(pTax.getTopVertex());
        while (!queue.isEmpty()) {
            TaxonomyVertex cur = queue.remove(0);
            if (pTax.isVisited(cur)) {
                continue;
            }
            pTax.setVisited(cur);
            ClassifiableEntry entry = cur.getPrimer();
            if (MPlus.contains(entry.getEntity()) || MMinus.contains(entry.getEntity())) {
                toProcess.add(entry);
            }
            queue.addAll(cur.neigh(false));
        }
        pTax.clearVisited();
        // System.out.println("Determine concepts that need reclassification ("
        // + toProcess.size() + "): done in " + t);
        // System.out.println("Add/Del names Taxonomy:" + tax);
        for (int i = 0; i < toProcess.size(); i++) {
            ClassifiableEntry p = toProcess.get(i);
            reclassify(p.getTaxVertex(), tBox.getSignature(p));
        }
        pTax.finalise();
    }

    /** @param node
     * @param s */
    public void reclassify(TaxonomyVertex node, TSignature s) {
        upDirection = false;
        sigStack.add(s);
        curEntry = node.getPrimer();
        TaxonomyVertex oldCur = pTax.getCurrent();
        pTax.setCurrent(node);
        // FIXME!! check the unsatisfiability later
        boolean added = MPlus.contains(curEntry.getEntity());
        boolean removed = MMinus.contains(curEntry.getEntity());
        assert added || removed;
        clearLabels();
        setValue(pTax.getTopVertex(), true);
        if (node.noNeighbours(true)) {
            node.addNeighbour(true, pTax.getTopVertex());
        }
        // we use candidates set if nothing was added (so no need to look
        // further from current subs)
        useCandidates = !added;
        candidates.clear();
        if (removed) {
            // re-check all parents
            List<TaxonomyVertex> pos = new ArrayList<TaxonomyVertex>();
            List<TaxonomyVertex> neg = new ArrayList<TaxonomyVertex>();
            for (TaxonomyVertex p : node.neigh(true)) {
                if (isValued(p) && getValue(p)) {
                    continue;
                }
                boolean sub = testSubsumption(p);
                if (sub) {
                    pos.add(p);
                    propagateTrueUp(p);
                } else {
                    setValue(p, sub);
                    neg.add(p);
                }
            }
            node.removeLinks(true);
            // for (TaxonomyVertex q : pos) {
            // node.addNeighbour(true, q);
            // }
            if (useCandidates) {
                for (TaxonomyVertex q : neg) {
                    fillCandidates(q);
                }
            }
        } else {
            // all parents are there
            for (TaxonomyVertex p : node.neigh(true)) {
                propagateTrueUp(p);
            }
            node.removeLinks(true);
        }
        // FIXME!! for now. later check the equivalence etc
        setValue(node, true);
        // the landscape is prepared
        searchBaader(pTax.getTopVertex());
        node.incorporate(pTax.getOptions());
        clearLabels();
        sigStack.pop();
        pTax.setCurrent(oldCur);
    }
}
