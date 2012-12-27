package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.kernel.Concept.CTTag;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheState;
import uk.ac.manchester.cs.jfact.split.TSplitVar;
import uk.ac.manchester.cs.jfact.split.TSplitVars;
import conformance.PortedFrom;

@PortedFrom(file = "DLConceptTaxonomy.h", name = "DLConceptTaxonomy")
public class DLConceptTaxonomy extends Taxonomy {
    /** all the derived subsumers of a class (came from the model) */
    class DerivedSubsumers extends KnownSubsumers {
        // protected: // typedefs
        /** set of the subsumers */
        // typedef Taxonomy::SubsumerSet SubsumerSet;
        /** SS RW iterator */
        // typedef SubsumerSet::iterator ss_iterator;
        // protected: // members
        /** set of sure- and possible subsumers */
        protected List<ClassifiableEntry> Sure, Possible;

        // public: // interface
        /** c'tor: copy given sets */
        public DerivedSubsumers(List<ClassifiableEntry> sure,
                List<ClassifiableEntry> possible) {
            Sure = new ArrayList<ClassifiableEntry>(sure);
            Possible = new ArrayList<ClassifiableEntry>(possible);
        }

        // iterators
        /** begin of the Sure subsumers interval */
        @Override
        public List<ClassifiableEntry> s_begin() {
            return Sure;
        }

        /** begin of the Possible subsumers interval */
        @Override
        public List<ClassifiableEntry> p_begin() {
            return Possible;
        }
    }

    /** set of split vars */
    TSplitVars Splits;
    /** flag shows that subsumption check could be simplified */
    boolean inSplitCheck;
    /** host tBox */
    private TBox tBox;
    /** common descendants of all parents of currently classified concept */
    private List<TaxonomyVertex> common = new ArrayList<TaxonomyVertex>();
    // statistic counters
    private long nConcepts;
    private long nTries;
    private long nPositives;
    private long nNegatives;
    private long nSearchCalls;
    private long nSubCalls;
    private long nNonTrivialSubCalls;
    /** number of positive cached subsumptions */
    private long nCachedPositive;
    /** number of negative cached subsumptions */
    private long nCachedNegative;
    /** number of non-subsumptions detected by a sorted reasoning */
    private long nSortedNegative;
    // flags
    /** flag to use Bottom-Up search */
    private boolean flagNeedBottomUp;
    /** number of processed common parents */
    protected int nCommon = 1;

    // -- General support for DL concept classification
    /** get access to curEntry as a TConcept */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "curConcept")
    private Concept curConcept() {
        return (Concept) curEntry;
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "enhancedSubs")
    private boolean enhancedSubs(boolean upDirection, TaxonomyVertex cur) {
        ++nSubCalls;
        if (cur.isValued(valueLabel)) {
            return cur.getValue();
        } else {
            return cur.setValued(enhancedSubs2(upDirection, cur), valueLabel);
        }
    }

    /** explicitely run TD phase */
    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "runTopDown")
    public void runTopDown() {
        searchBaader(false, getTopVertex());
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
            if (!willInsertIntoTaxonomy) { // after classification -- bottom set
                                           // up already
                searchBaader(true, getBottomVertex());
                return;
            }
            // during classification -- have to find leaf nodes
            for (int i = 0; i < common.size(); i++) {
                TaxonomyVertex p = common.get(i);
                if (p.noNeighbours(false)) {
                    searchBaader(true, p);
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

    /** the only c'tor */
    public DLConceptTaxonomy(Concept pTop, Concept pBottom, TBox kb) {
        super(pTop, pBottom, kb.getOptions());
        tBox = kb;
        nConcepts = 0;
        nTries = 0;
        nPositives = 0;
        nNegatives = 0;
        nSearchCalls = 0;
        nSubCalls = 0;
        nNonTrivialSubCalls = 0;
        nCachedPositive = 0;
        nCachedNegative = 0;
        nSortedNegative = 0;
        // flagNeedBottomUp = GCIs.isGCI() || GCIs.isReflexive() &&
        // GCIs.isRnD();
        inSplitCheck = false;
    }

    /** process all splits */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "processSplits")
    void processSplits() {
        for (TSplitVar v : Splits.getEntries()) {
            mergeSplitVars(v);
        }
    }

    /** set split vars */
    void setSplitVars(TSplitVars s) {
        Splits = s;
    }

    /** get access to split vars */
    TSplitVars getSplits() {
        return Splits;
    }

    /** set bottom-up flag */
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
        addCurrentToSynonym(getBottomVertex());
        return true;
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
        if (q.isSingleton() // singleton on the RHS is useless iff...
                && q.isPrimitive() // it is primitive
                && !q.isNominal()) {
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
                        nSortedNegative) : "", nSearchCalls, nSubCalls,
                nNonTrivialSubCalls, nEntries * (nEntries - 1) / Math.max(1, nTries)));
        o.append(super.toString());
        return o.toString();
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "searchBaader")
    private void searchBaader(boolean upDirection, TaxonomyVertex cur) {
        cur.setChecked(checkLabel);
        ++nSearchCalls;
        boolean noPosSucc = true;
        List<TaxonomyVertex> neigh = cur.neigh(upDirection);
        int size = neigh.size();
        for (int i = 0; i < size; i++) {
            TaxonomyVertex p = neigh.get(i);
            if (enhancedSubs(upDirection, p)) {
                if (!p.isChecked(checkLabel)) {
                    searchBaader(upDirection, p);
                }
                noPosSucc = false;
            }
        }
        // in case current node is unchecked (no BOTTOM node) -- check it
        // explicitely
        if (!cur.isValued(valueLabel)) {
            cur.setValued(testSubsumption(upDirection, cur), valueLabel);
        }
        if (noPosSucc && cur.getValue()) {
            current.addNeighbour(!upDirection, cur);
        }
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "enhancedSubs1")
    private boolean enhancedSubs1(boolean upDirection, TaxonomyVertex cur) {
        ++nNonTrivialSubCalls;
        List<TaxonomyVertex> neigh = cur.neigh(!upDirection);
        int size = neigh.size();
        for (int i = 0; i < size; i++) {
            if (!enhancedSubs(upDirection, neigh.get(i))) {
                return false;
            }
        }
        return testSubsumption(upDirection, cur);
    }

    /** short-cuf from ENHANCED_SUBS */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "enhancedSubs2")
    private boolean enhancedSubs2(boolean upDirection, TaxonomyVertex cur) {
        // if bottom-up search and CUR is not a successor of checking entity --
        // return false
        if (upDirection && !cur.isCommon()) {
            return false;
        }
        if (tBox.getOptions().isSplits()) {
            if (!inSplitCheck && !upDirection && !possibleSub(cur)) {
                return false;
            }
        }
        return enhancedSubs1(upDirection, cur);
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
    private boolean testSubsumption(boolean upDirection, TaxonomyVertex cur) {
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
        if (node.isChecked(checkLabel)) {
            return;
        }
        // mark node visited
        node.setChecked(checkLabel);
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
        boolean upDirection = true;
        nCommon = 1;
        List<TaxonomyVertex> list = current.neigh(upDirection);
        int size = list.size();
        assert size > 0; // there is at least one parent (TOP)
        TaxonomyVertex p = list.get(0);
        // define possible successors of the node
        propagateOneCommon(p);
        clearCheckedLabel();
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
            clearCheckedLabel();
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
        current.addNeighbour(false, getTopVertex());
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
            if (tBox.isBlockedInd(curI)) { // check whether current entry is the
                                           // same as another individual
                Individual syn = tBox.getBlockingInd(curI);
                assert syn.getTaxVertex() != null;
                if (tBox.isBlockingDet(curI)) {
                    // deterministic merge => curI = syn
                    addCurrentToSynonym(syn.getTaxVertex());
                    return true;
                } else {
                    // non-det merge: check whether it is the same
                    tBox.getOptions()
                            .getLog()
                            .print("\nTAX: trying '", curI.getName(), "' = '",
                                    syn.getName(), "'... ");
                    if (testSubTBox(curI, syn)) {
                        // they are actually the same
                        addCurrentToSynonym(syn.getTaxVertex());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** after merging, check whether there are extra neighbours that should be */
    // taken into account
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "checkExtraParents")
    void checkExtraParents() {
        inSplitCheck = true;
        for (TaxonomyVertex p : current.neigh(true)) {
            propagateTrueUp(p);
        }
        current.clearLinks(true);
        runTopDown();
        List<TaxonomyVertex> vec = new ArrayList<TaxonomyVertex>();
        for (TaxonomyVertex p : current.neigh(true)) {
            if (!isDirectParent(p)) {
                vec.add(p);
            }
        }
        for (TaxonomyVertex p : vec) {
            p.removeLink(false, current);
            current.removeLink(true, p);
        }
        clearLabels();
        inSplitCheck = false;
    }

    /** merge vars came from a given SPLIT together */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "mergeSplitVars")
    void mergeSplitVars(TSplitVar split) {
        Set<TaxonomyVertex> splitVertices = new HashSet<TaxonomyVertex>();
        TaxonomyVertex v = split.C.getTaxVertex();
        boolean cIn = v != null;
        if (v != null) {
            splitVertices.add(v);
        }
        for (TSplitVar.Entry q : split.getEntries()) {
            splitVertices.add(q.concept.getTaxVertex());
        }
        // set V to be a node-to-add
        // FIXME!! check later the case whether both TOP and BOT are there
        if (splitVertices.contains(getBottomVertex())) {
            v = getBottomVertex();
        } else if (splitVertices.contains(getTopVertex())) {
            v = getTopVertex();
        } else {
            setCurrentEntry(split.C);
            v = current;
        }
        if (!v.equals(current) && !cIn) {
            v.addSynonym(split.C);
        }
        for (TaxonomyVertex p : splitVertices) {
            mergeVertex(v, p, splitVertices);
        }
        if (v == current) {
            checkExtraParents();
            insertCurrentNode();
        }
    }

    /** merge a single vertex V to a node represented by CUR */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "mergeVertex")
    void mergeVertex(TaxonomyVertex cur, TaxonomyVertex v, Set<TaxonomyVertex> excludes) {
        if (!cur.equals(v)) {
            cur.mergeIndepNode(v, excludes, curEntry);
            removeNode(v);
        }
    }
}
