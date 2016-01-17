package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapitools.decomposition.Signature;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.kernel.Concept.CTTag;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheState;

/** Concept taxonomy */
@PortedFrom(file = "DLConceptTaxonomy.h", name = "DLConceptTaxonomy")
public class DLConceptTaxonomy extends TaxonomyCreator {

    /** host tBox */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "tBox") private final TBox tBox;
    /** common descendants of all parents of currently classified concept */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "Common") private final List<TaxonomyVertex> common = new ArrayList<>();
    // statistic counters
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nConcepts") private long nConcepts = 0;
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nTries") private long nTries = 0;
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nPositives") private long nPositives = 0;
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nNegatives") private long nNegatives = 0;
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nSearchCalls") private long nSearchCalls = 0;
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nSubCalls") private long nSubCalls = 0;
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nNonTrivialSubCalls") private long nNonTrivialSubCalls = 0;
    /** number of positive cached subsumptions */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nCachedPositive") private long nCachedPositive = 0;
    /** number of negative cached subsumptions */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nCachedNegative") private long nCachedNegative = 0;
    /** number of non-subsumptions detected by a sorted reasoning */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nSortedNegative") private long nSortedNegative = 0;
    /** number of non-subsumptions because of module reasons */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nModuleNegative") private long nModuleNegative = 0;
    // flags
    /** flag to use Bottom-Up search */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "flagNeedBottomUp") private boolean flagNeedBottomUp;
    /** number of processed common parents */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "nCommon") protected int nCommon = 1;
    /** set of possible parents */
    protected final Set<TaxonomyVertex> candidates = new HashSet<>();
    /** whether look into it */
    protected boolean useCandidates = false;
    protected Set<OWLEntity> mPlus;
    protected Set<OWLEntity> mMinus;

    /**
     * the only c'tor
     * 
     * @param pTax
     *        pTax
     * @param tbox
     *        tbox
     */
    public DLConceptTaxonomy(Taxonomy pTax, TBox tbox) {
        super(pTax);
        tBox = tbox;
    }

    // -- General support for DL concept classification
    /**
     * get access to curEntry as a TConcept
     * 
     * @return current entry
     */
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
            common.stream().filter(p -> p.noNeighbours(false)).forEach(this::searchBaader);
        } finally {
            clearCommon();
        }
    }

    /** actions that to be done BEFORE entry will be classified */
    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "preClassificationActions")
    public void preClassificationActions() {
        ++nConcepts;
        tBox.getOptions().getProgressMonitor().reasonerTaskProgressChanged((int) nConcepts, tBox.getNItems());
    }

    /**
     * set bottom-up flag
     * 
     * @param gcis
     *        GCIs
     */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "setBottomUp")
    public void setBottomUp(KBFlags gcis) {
        flagNeedBottomUp = gcis.isGCI() || gcis.isReflexive() && gcis.isRnD();
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

    @Nullable
    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "buildSignature")
    public Signature buildSignature(ClassifiableEntry p) {
        return tBox.getSignature(p);
    }

    @Override
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "immediatelyClassified")
    protected boolean immediatelyClassified() {
        if (classifySynonym()) {
            return true;
        }
        if (curConcept().getClassTagPlain() == CTTag.COMPLETELYDEFINED) {
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
        // or no reflexive roles w/RnD present (Refl(R), Range(R)=D)
        return flagNeedBottomUp || !useCompletelyDefined || !curConcept().isPrimitive();
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "testSub")
    @SuppressWarnings("incomplete-switch")
    private boolean testSub(Concept p, Concept q) {
        assert p != null;
        assert q != null;
        if (q.isSingleton() && q.isPrimitive() && !q.isNominal()) {
            // singleton on the RHS is useless iff it is primitive
            return false;
        }
        // nominals should be classified as usual concepts
        tBox.getOptions().getLog().printTemplate(Templates.TAX_TRYING, p.getIRI(), q.getIRI());
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
            case VALID:
                // cached result: satisfiable => non-subsumption
                tBox.getOptions().getLog().print("NOT holds (cached result)");
                ++nCachedNegative;
                return false;
            case INVALID:
                // cached result: unsatisfiable => subsumption holds
                tBox.getOptions().getLog().print("holds (cached result)");
                ++nCachedPositive;
                return true;
            default:
                // need extra tests
                tBox.getOptions().getLog().print("wasted cache test");
                break;
        }
        return testSubTBox(p, q);
    }

    /**
     * @param entity
     *        entity
     * @return true if non-subsumption is due to ENTITY is not in the
     *         \bot-module
     */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "isNotInModule")
    private boolean isNotInModule(@Nullable NamedEntity entity) {
        if (upDirection) {
            return false;
        }
        Signature sig = sigStack.peek();
        if (sig != null && entity != null && !sig.contains(entity.getEntity())) {
            return true;
        }
        return false;
    }

    /**
     * test subsumption via TBox explicitely
     * 
     * @param p
     *        p
     * @param q
     *        q
     * @return true if subsumption holds
     */
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
        o.append(String.format(Templates.DLCONCEPTTAXONOMY.getTemplate(), Long.toString(nTries), Long.toString(
            nPositives), Long.toString(nPositives * 100 / Math.max(1, nTries)), Long.toString(nCachedPositive), Long
                .toString(nCachedNegative), nSortedNegative > 0 ? String.format(
                    "Sorted reasoning deals with %s non-subsumptions%n", Long.toString(nSortedNegative)) : "",
            nModuleNegative > 0 ? "Modular reasoning deals with " + nModuleNegative + " non-subsumptions\n" : "", Long
                .toString(nSearchCalls), Long.toString(nSubCalls), Long.toString(nNonTrivialSubCalls), Long.toString(
                    nEntries * (nEntries - 1) / Math.max(1, nTries))));
        o.append(super.toString());
        return o.toString();
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "searchBaader")
    private void searchBaader(TaxonomyVertex cur) {
        // label 'visited'
        pTax.setVisited(cur);
        ++nSearchCalls;
        AtomicBoolean noPosSucc = new AtomicBoolean(true);
        // check if there are positive successors; use DFS on them.
        cur.neigh(upDirection).filter(this::enhancedSubs).forEach(p -> {
            if (!pTax.isVisited(p)) {
                searchBaader(p);
            }
            noPosSucc.set(false);
        });
        // in case current node is unchecked (no BOTTOM node) -- check it
        // explicitly
        if (!isValued(cur)) {
            setValue(cur, testSubsumption(cur));
        }
        // mark labelled leaf node as a parent (self check for incremental)
        if (noPosSucc.get() && cur.getValue()) {
            pTax.getCurrent().addNeighbour(!upDirection, cur);
        }
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "enhancedSubs1")
    private boolean enhancedSubs1(TaxonomyVertex cur) {
        ++nNonTrivialSubCalls;
        // need to be valued -- check all parents
        // propagate false
        // do this only if the concept is not it M-
        if (cur.neigh(!upDirection).anyMatch(n -> !enhancedSubs(n))) {
            return false;
        }
        // all subsumptions holds -- test current for subsumption
        return testSubsumption(cur);
    }

    /**
     * short-cut from ENHANCED_SUBS
     * 
     * @param cur
     *        cur
     * @return true if subsumption holds
     */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "enhancedSubs2")
    private boolean enhancedSubs2(TaxonomyVertex cur) {
        // if bottom-up search and CUR is not a successor of checking entity --
        // return false
        if (upDirection && !cur.isCommon()) {
            return false;
        }
        if (useCandidates && candidates.contains(cur)) {
            return false;
        }
        return enhancedSubs1(cur);
    }

    /**
     * test whether a node could be a super-node of CUR
     * 
     * @param v
     *        v
     * @return true if candidate
     */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "possibleSub")
    private boolean possibleSub(TaxonomyVertex v) {
        Concept c = (Concept) v.getPrimer();
        // non-prim concepts are candidates
        if (!c.isPrimitive()) {
            return true;
        }
        // all others should be in the possible sups list
        return ksStack.peek().isPossibleSub(c);
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
        node.neigh(false).forEach(this::propagateOneCommon);
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "propagateUp")
    private boolean propagateUp() {
        nCommon = 1;
        Iterator<TaxonomyVertex> list = pTax.getCurrent().neigh(upDirection).iterator();
        assert list.hasNext();
        // including node always have some parents (TOP at least)
        TaxonomyVertex p = list.next();
        // define possible successors of the node
        propagateOneCommon(p);
        pTax.clearVisited();
        while (list.hasNext()) {
            p = list.next();
            if (p.noNeighbours(!upDirection)) {
                return true;
            }
            if (common.isEmpty()) {
                return true;
            }
            ++nCommon;
            // aux set for the verteces in ...
            List<TaxonomyVertex> aux = new ArrayList<>(common);
            common.clear();
            // now Aux contain data from previous run
            propagateOneCommon(p);
            pTax.clearVisited();
            // clear all non-common nodes (visited on a previous run)
            aux.forEach(q -> q.correctCommon(nCommon));
        }
        return false;
    }

    @PortedFrom(file = "DLConceptTaxonomy.h", name = "clearCommon")
    private void clearCommon() {
        common.forEach(p -> p.clearCommon());
        common.clear();
    }

    /**
     * check if no BU classification is required as C=TOP
     * 
     * @return true if no classification required
     */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "isEqualToTop")
    private boolean isEqualToTop() {
        // check this up-front to avoid Sorted check's flaw wrt equals-to-top
        ModelCacheInterface cache = tBox.initCache(curConcept(), true);
        if (cache.getState() != ModelCacheState.INVALID) {
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
                assert syn.isClassified();
                TaxonomyVertex taxVertex = syn.getTaxVertex();
                assert taxVertex != null;
                if (tBox.isBlockingDet(curI)) {
                    // deterministic merge => curI = syn
                    pTax.addCurrentToSynonym(taxVertex);
                    return true;
                } else {
                    // non-det merge: check whether it is the same
                    tBox.getOptions().getLog().print("\nTAX: trying '", curI.getIRI(), "' = '", syn.getIRI(), "'... ");
                    if (testSubTBox(curI, syn)) {
                        // they are actually the same
                        pTax.addCurrentToSynonym(taxVertex);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * after merging, check whether there are extra neighbours that should be
     * taken into account
     */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "checkExtraParents")
    private void checkExtraParents() {
        pTax.current.neigh(true).forEach(this::propagateTrueUp);
        pTax.current.clearLinks(true);
        runTopDown();
        pTax.current.neigh(true).filter(p -> !isDirectParent(p)).forEach(p -> {
            p.removeLink(false, pTax.current);
            pTax.current.removeLink(true, p);
        });
        clearLabels();
    }

    /**
     * merge a single vertex V to a node represented by CUR
     * 
     * @param cur
     *        cur
     * @param v
     *        v
     * @param excludes
     *        excludes
     */
    @PortedFrom(file = "DLConceptTaxonomy.h", name = "mergeVertex")
    private void mergeVertex(TaxonomyVertex cur, TaxonomyVertex v, Set<TaxonomyVertex> excludes) {
        if (!cur.equals(v)) {
            cur.mergeIndepNode(v, excludes, curEntry);
            pTax.removeNode(v);
        }
    }

    /**
     * fill candidates
     * 
     * @param cur
     *        vertex to add to candidates (and its updirection neighbors
     */
    public void fillCandidates(TaxonomyVertex cur) {
        if (isValued(cur)) {
            if (getValue(cur)) {
                // positive value -- nothing to do
                return;
            }
        } else {
            candidates.add(cur);
        }
        cur.neigh(true).forEach(this::fillCandidates);
    }

    /**
     * @param plus
     *        plus
     * @param minus
     *        minus
     */
    public void reclassify(Set<OWLEntity> plus, Set<OWLEntity> minus) {
        mPlus = plus;
        mMinus = minus;
        pTax.deFinalise();
        // fill in an order to
        LinkedList<TaxonomyVertex> queue = new LinkedList<>();
        List<ClassifiableEntry> toProcess = new ArrayList<>();
        queue.add(pTax.getTopVertex());
        while (!queue.isEmpty()) {
            TaxonomyVertex cur = queue.remove(0);
            if (pTax.isVisited(cur)) {
                continue;
            }
            pTax.setVisited(cur);
            ClassifiableEntry entry = cur.getPrimer();
            if (mPlus.contains(entry.getEntity().getEntity()) || mMinus.contains(entry.getEntity().getEntity())) {
                toProcess.add(entry);
            }
            cur.neigh(false).forEach(queue::add);
        }
        pTax.clearVisited();
        toProcess.forEach(p -> reclassify(p.getTaxVertex(), tBox.getSignature(p)));
        pTax.finalise();
    }

    /**
     * @param node
     *        node
     * @param s
     *        s
     */
    public void reclassify(TaxonomyVertex node, @Nullable Signature s) {
        upDirection = false;
        sigStack.add(s);
        curEntry = node.getPrimer();
        TaxonomyVertex oldCur = pTax.getCurrent();
        pTax.setCurrent(node);
        // FIXME!! check the unsatisfiability later
        boolean added = mPlus.contains(curEntry.getEntity().getEntity());
        boolean removed = mMinus.contains(curEntry.getEntity().getEntity());
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
            List<TaxonomyVertex> neg = new ArrayList<>();
            node.neigh(true).forEach(p -> {
                if (!isValued(p) || !getValue(p)) {
                    if (testSubsumption(p)) {
                        propagateTrueUp(p);
                    } else {
                        setValue(p, false);
                        neg.add(p);
                    }
                }
            });
            node.removeLinks(true);
            if (useCandidates) {
                neg.forEach(this::fillCandidates);
            }
        } else {
            // all parents are there
            node.neigh(true).forEach(this::propagateTrueUp);
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
