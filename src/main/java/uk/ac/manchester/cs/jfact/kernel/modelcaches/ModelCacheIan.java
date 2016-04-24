package uk.ac.manchester.cs.jfact.kernel.modelcaches;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheState.*;
import static uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheType.IAN;

import java.util.stream.Stream;

import org.roaringbitmap.RoaringBitmap;

import conformance.PortedFrom;
import uk.ac.manchester.cs.chainsaw.FastSet;
import uk.ac.manchester.cs.chainsaw.FastSetFactory;
import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.*;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/** model cache Ian (Horrocks) */
@PortedFrom(file = "modelCacheIan.h", name = "modelCacheIan")
public class ModelCacheIan extends ModelCacheInterface {

    // sets for the cache
    /**
     * named concepts that appears positively det-lly in a root node of a cache
     */
    @PortedFrom(file = "modelCacheIan.h", name = "posDConcepts") private final RoaringBitmap posDConcepts = new RoaringBitmap();
    /**
     * named concepts that appears positively non-det in a root node of a cache
     */
    @PortedFrom(file = "modelCacheIan.h", name = "posNConcepts") private final RoaringBitmap posNConcepts = new RoaringBitmap();
    /**
     * named concepts that appears negatively det-lly in a root node of a cache
     */
    @PortedFrom(file = "modelCacheIan.h", name = "negDConcepts") private final RoaringBitmap negDConcepts = new RoaringBitmap();
    /**
     * named concepts that appears negatively non-det in a root node of a cache
     */
    @PortedFrom(file = "modelCacheIan.h", name = "negNConcepts") private final RoaringBitmap negNConcepts = new RoaringBitmap();
    /** extra det-lly concepts that are (partial) Simple Rule applications */
    @PortedFrom(file = "modelCacheIan.h", name = "extraDConcepts") private final FastSet extraDConcepts = FastSetFactory
        .create();
    /** extra non-det concepts that are (partial) Simple Rule applications */
    @PortedFrom(file = "modelCacheIan.h", name = "extraNConcepts") private final FastSet extraNConcepts = FastSetFactory
        .create();
    /** role names that are labels of the outgoing edges from the root node */
    @PortedFrom(file = "modelCacheIan.h", name = "existsRoles") private final FastSet existsRoles = FastSetFactory
        .create();
    /** role names that appears in the \A restrictions in the root node */
    @PortedFrom(file = "modelCacheIan.h", name = "forallRoles") private final FastSet forallRoles = FastSetFactory
        .create();
    /** role names that appears in the atmost restrictions in the root node */
    @PortedFrom(file = "modelCacheIan.h", name = "funcRoles") private final FastSet funcRoles = FastSetFactory.create();
    /** current state of cache model; recalculates on every change */
    @PortedFrom(file = "modelCacheIan.h", name = "curState") private ModelCacheState curState;
    // XXX these two fields should be used somehow
    private final int nC;
    private final int nR;
    private final JFactReasonerConfiguration simpleRules;

    /**
     * Create cache model of given CompletionTree using given HEAP
     * 
     * @param heap
     *        heap
     * @param p
     *        p
     * @param flagNominals
     *        flagNominals
     * @param nC
     *        nC
     * @param nR
     *        nR
     * @param simpleRules
     *        simpleRules
     */
    public ModelCacheIan(DLDag heap, DlCompletionTree p, boolean flagNominals, int nC, int nR,
        JFactReasonerConfiguration simpleRules) {
        this(flagNominals, nC, nR, simpleRules);
        initCacheByLabel(heap, p);
        initRolesFromArcs(p);
    }

    /**
     * @param flagNominals
     *        flagNominals
     * @param nC
     *        nC
     * @param nR
     *        nR
     * @param simpleRules
     *        simpleRules
     */
    public ModelCacheIan(boolean flagNominals, int nC, int nR, JFactReasonerConfiguration simpleRules) {
        super(flagNominals);
        curState = VALID;
        this.simpleRules = simpleRules;
        this.nC = nC;
        this.nR = nR;
    }

    /**
     * process CT label in given interval; set Deterministic accordingly
     * 
     * @param dlHeap
     *        DLHeap
     * @param start
     *        start
     */
    @PortedFrom(file = "modelCacheIan.h", name = "processLabelInterval")
    private void processLabelInterval(DLDag dlHeap, Stream<ConceptWDep> start) {
        start.forEach(p -> processConcept(dlHeap.get(p.getConcept()), p.getConcept() > 0, p.getDep().isEmpty()));
    }

    /**
     * fills cache sets by tree.Label; set Deterministic accordingly
     * 
     * @param dlHeap
     *        DLHeap
     * @param pCT
     *        pCT
     */
    @PortedFrom(file = "modelCacheIan.h", name = "initCacheByLabel")
    private void initCacheByLabel(DLDag dlHeap, DlCompletionTree pCT) {
        processLabelInterval(dlHeap, pCT.simpleConcepts().stream());
        processLabelInterval(dlHeap, pCT.complexConcepts().stream());
    }

    @Override
    @PortedFrom(file = "modelCacheIan.h", name = "getState")
    public ModelCacheState getState() {
        return curState;
    }

    @PortedFrom(file = "modelCacheIan.h", name = "getDConcepts")
    private RoaringBitmap getDConcepts(boolean pos) {
        return pos ? posDConcepts : negDConcepts;
    }

    /**
     * @param pos
     *        pos
     * @return N-concepts wrt polarity
     */
    @PortedFrom(file = "modelCacheIan.h", name = "getNConcepts")
    private RoaringBitmap getNConcepts(boolean pos) {
        return pos ? posNConcepts : negNConcepts;
    }

    /**
     * @param det
     *        det
     * @return extra concepts wrt deterministic flag
     */
    @PortedFrom(file = "modelCacheIan.h", name = "getExtra")
    private FastSet getExtra(boolean det) {
        return det ? extraDConcepts : extraNConcepts;
    }

    /**
     * init existRoles from arcs; can be used to create pseudo-cache with deps
     * of CT edges
     * 
     * @param pCT
     *        pCT
     */
    @PortedFrom(file = "modelCacheIan.h", name = "initRolesFromArcs")
    public void initRolesFromArcs(DlCompletionTree pCT) {
        pCT.getNeighbour().stream().filter(p -> !p.isIBlocked()).forEach(p -> addExistsRole(p.getRole()));
        curState = VALID;
    }

    /** Get the tag identifying the cache type */
    @Override
    @PortedFrom(file = "modelCacheIan.h", name = "getCacheType")
    public ModelCacheType getCacheType() {
        return IAN;
    }

    /** get type of cache (deep or shallow) */
    @Override
    @PortedFrom(file = "modelCacheIan.h", name = "shallowCache")
    public boolean shallowCache() {
        return existsRoles.isEmpty();
    }

    /** clear the cache */
    @PortedFrom(file = "modelCacheIan.h", name = "clear")
    public void clear() {
        posDConcepts.clear();
        posNConcepts.clear();
        negDConcepts.clear();
        negNConcepts.clear();
        if (simpleRules.isUseSimpleRules()) {
            extraDConcepts.clear();
            extraNConcepts.clear();
        }
        existsRoles.clear();
        forallRoles.clear();
        funcRoles.clear();
        curState = VALID;
    }

    /**
     * @param cur
     *        cur
     * @param pos
     *        pos
     * @param det
     *        det
     */
    @PortedFrom(file = "modelCacheIan.h", name = "processConcept")
    @SuppressWarnings("incomplete-switch")
    public void processConcept(DLVertex cur, boolean pos, boolean det) {
        switch (cur.getType()) {
            case TOP:
            case DATATYPE:
            case DATAVALUE:
            case DATAEXPR:
                // data entries can not be cached
                throw new UnreachableSituationException(cur.toString()
                    + " Top datatype property, datatype, data value or data expression used in an unexpected position");
            case NCONCEPT:
            case PCONCEPT:
            case NSINGLETON:
            case PSINGLETON:
                // add concepts to Concepts
                int toAdd = ((ClassifiableEntry) cur.getConcept()).getIndex();
                (det ? getDConcepts(pos) : getNConcepts(pos)).add(toAdd);
                break;
            case IRR: // for \neg \ER.Self: add R to AR-set
            case FORALL: // add AR.C roles to forallRoles
            case LE: // for <= n R: add R to forallRoles
                if (cur.getRole().isTop()) {
                    // force clash to every other edge
                    (pos ? forallRoles : existsRoles).completeSet(nR);
                } else if (pos) {
                    // no need to deal with existentials here: they would be
                    // created through edges
                    if (cur.getRole().isSimple()) {
                        forallRoles.add(cur.getRole().getIndex());
                    } else {
                        processAutomaton(cur);
                    }
                }
                break;
            default: // all other -- nothing to do
                break;
        }
    }

    /**
     * @param cur
     *        cur
     */
    @PortedFrom(file = "modelCacheIan.h", name = "processAutomaton")
    public void processAutomaton(DLVertex cur) {
        RAStateTransitions rst = cur.getRole().getAutomaton().get(cur.getState());
        // for every transition starting from a given state,
        // add the role that is accepted by a transition
        rst.begin().stream().flatMap(p -> p.begin().stream()).forEach(r -> forallRoles.add(r.getIndex()));
    }

    /**
     * adds role to exists- and func-role if necessary
     * 
     * @param r
     *        R
     */
    @PortedFrom(file = "modelCacheIan.h", name = "addRoleToCache")
    private void addRoleToCache(Role r) {
        existsRoles.add(r.getIndex());
        if (r.isTopFunc()) {
            // all other top-funcs would be added separately
            funcRoles.add(r.getIndex());
        }
    }

    /**
     * adds role (and all its super-roles) to exists- and funcRoles
     * 
     * @param r
     *        R
     */
    @PortedFrom(file = "modelCacheIan.h", name = "addExistsRole")
    private void addExistsRole(Role r) {
        addRoleToCache(r);
        r.getAncestor().forEach(this::addRoleToCache);
    }

    @Override
    @PortedFrom(file = "modelCacheIan.h", name = "canMerge")
    public ModelCacheState canMerge(ModelCacheInterface p) {
        if (hasNominalClash(p)) {
            // fail to merge due to nominal precense
            return FAILED;
        }
        // check if something goes wrong
        if (p.getState() != VALID || curState != VALID) {
            return mergeStatus(p.getState(), curState);
        }
        // here both models are valid
        switch (p.getCacheType()) {
            case CONST:
                // check for TOP (as the model is valid)
                return VALID;
            case SINGLETON:
                // check for the Singleton
                int singleton = ((ModelCacheSingleton) p).getValue();
                return isMergableSingleton(Math.abs(singleton), singleton > 0);
            case IAN:
                return isMergableIan((ModelCacheIan) p);
            case BADTYPE:
            default:
                // something unexpected
                return UNKNOWN;
        }
    }

    /**
     * @param singleton
     *        Singleton
     * @param pos
     *        pos
     * @return invalid, failed or valid depending on whether singleton is
     *         included
     */
    @PortedFrom(file = "modelCacheIan.h", name = "isMergableSingleton")
    public ModelCacheState isMergableSingleton(int singleton, boolean pos) {
        assert singleton != 0;
        // deterministic clash
        if (getDConcepts(!pos).contains(singleton)) {
            return INVALID;
        } else if (getNConcepts(!pos).contains(singleton)) {
            return FAILED;
        }
        return VALID;
    }

    /**
     * @param q
     *        q
     * @return invalid, failed or valid
     */
    @PortedFrom(file = "modelCacheIan.h", name = "isMergableIan")
    public ModelCacheState isMergableIan(ModelCacheIan q) {
        if (intersection(posDConcepts, q.negDConcepts) || intersection(q.posDConcepts, negDConcepts)) {
            return INVALID;
        } else if (existsRoles.intersect(q.forallRoles) || q.existsRoles.intersect(forallRoles) || funcRoles.intersect(
            q.funcRoles) || intersection(posDConcepts, q.negNConcepts) || intersection(posNConcepts, q.negDConcepts)
            || intersection(posNConcepts, q.negNConcepts) || intersection(q.posDConcepts, negNConcepts) || intersection(
                q.posNConcepts, negDConcepts) || intersection(q.posNConcepts, negNConcepts)) {
            return FAILED;
        } else {
            if (simpleRules.isUseSimpleRules() && getExtra(true).intersect(q.getExtra(true))) {
                return INVALID;
            }
            if (simpleRules.isUseSimpleRules() && (getExtra(true).intersect(q.getExtra(false)) || getExtra(false)
                .intersect(q.getExtra(true)) || getExtra(false).intersect(q.getExtra(false)))) {
                return FAILED;
            }
            return VALID;
        }
    }

    boolean intersection(RoaringBitmap a, RoaringBitmap b) {
        return !RoaringBitmap.and(a, b).isEmpty();
    }

    /**
     * @param p
     *        p
     * @return invalid, failed or valid
     */
    @PortedFrom(file = "modelCacheIan.h", name = "merge")
    @SuppressWarnings("incomplete-switch")
    public ModelCacheState merge(ModelCacheInterface p) {
        assert p != null;
        // check for nominal clash
        if (hasNominalClash(p)) {
            curState = FAILED;
            return curState;
        }
        switch (p.getCacheType()) {
            case CONST: // adds TOP/BOTTOM
                curState = mergeStatus(curState, p.getState());
                break;
            case SINGLETON: // adds Singleton
                int em = ((ModelCacheSingleton) p).getValue();
                mergeSingleton(Math.abs(em), em > 0);
                break;
            case IAN:
                mergeIan((ModelCacheIan) p);
                break;
            default:
                throw new UnreachableSituationException();
        }
        updateNominalStatus(p);
        return curState;
    }

    /**
     * actual merge with a singleton cache
     * 
     * @param singleton
     *        Singleton
     * @param pos
     *        pos
     */
    @PortedFrom(file = "modelCacheIan.h", name = "mergeSingleton")
    private void mergeSingleton(int singleton, boolean pos) {
        ModelCacheState newState = isMergableSingleton(singleton, pos);
        if (newState != VALID) {
            // some clash occured: adjust state
            curState = mergeStatus(curState, newState);
        } else {
            // add singleton; no need to change state here
            getDConcepts(pos).add(singleton);
        }
    }

    /**
     * actual merge with an Ian's cache
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "modelCacheIan.h", name = "mergeIan")
    private void mergeIan(ModelCacheIan p) {
        // setup curState
        curState = isMergableIan(p);
        // merge all sets:
        posDConcepts.or(p.posDConcepts);
        posNConcepts.or(p.posNConcepts);
        negDConcepts.or(p.negDConcepts);
        negNConcepts.or(p.negNConcepts);
        if (simpleRules.isUseSimpleRules()) {
            extraDConcepts.addAll(p.extraDConcepts);
            extraNConcepts.addAll(p.extraNConcepts);
        }
        existsRoles.addAll(p.existsRoles);
        forallRoles.addAll(p.forallRoles);
        funcRoles.addAll(p.funcRoles);
    }

    @Override
    @PortedFrom(file = "modelCacheIan.h", name = "logCacheEntry")
    public void logCacheEntry(int level, LogAdapter l) {
        l.print("\nIan cache: posDConcepts = {", posDConcepts, "}, posNConcepts = {", posNConcepts,
            "}, negDConcepts = {", negDConcepts, "}, negNConcepts = {", negNConcepts, "}, existsRoles = {", existsRoles,
            "}, forallRoles = {", forallRoles, "}, funcRoles = {", funcRoles, "}");
    }

    @PortedFrom(file = "modelCacheInterface.h", name = "mergeStatus")
    private static ModelCacheState mergeStatus(ModelCacheState s1, ModelCacheState s2) {
        // if one of caches is definitely UNSAT, then merge will be the same
        if (s1 == INVALID || s2 == INVALID) {
            return INVALID;
        }
        // if one of caches is unsure then result will be the same
        if (s1 == FAILED || s2 == FAILED) {
            return FAILED;
        }
        // if one of caches is not inited, than result would be the same
        if (s1 == UNKNOWN || s2 == UNKNOWN) {
            return UNKNOWN;
        } else {
            // valid+valid = valid
            return VALID;
        }
    }
}
