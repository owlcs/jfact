package uk.ac.manchester.cs.jfact.kernel.modelcaches;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheState.*;
import static uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheType.*;

import java.util.BitSet;
import java.util.List;

import uk.ac.manchester.cs.jfact.helpers.*;
import uk.ac.manchester.cs.jfact.kernel.*;
import conformance.PortedFrom;

@PortedFrom(file = "modelCacheIan.h", name = "modelCacheIan")
public class ModelCacheIan extends ModelCacheInterface {
    // sets for the cache
    /** named concepts that appears positively det-lly in a root node of a cache */
    public BitSet posDConcepts = new BitSet();
    /** named concepts that appears positively non-det in a root node of a cache */
    public BitSet posNConcepts = new BitSet();
    /** named concepts that appears negatively det-lly in a root node of a cache */
    public BitSet negDConcepts = new BitSet();
    /** named concepts that appears negatively non-det in a root node of a cache */
    public BitSet negNConcepts = new BitSet();
    /** extra det-lly concepts that are (partial) Simple Rule applications */
    public FastSet extraDConcepts = FastSetFactory.create();
    /** extra non-det concepts that are (partial) Simple Rule applications */
    public FastSet extraNConcepts = FastSetFactory.create();
    /** role names that are labels of the outgoing edges from the root node */
    public FastSet existsRoles = FastSetFactory.create();
    /** role names that appears in the \A restrictions in the root node */
    public FastSet forallRoles = FastSetFactory.create();
    /** role names that appears in the atmost restrictions in the root node */
    public FastSet funcRoles = FastSetFactory.create();
    /** current state of cache model; recalculates on every change */
    public ModelCacheState curState;
    public int nC;
    public int nR;
    private boolean simpleRules;

    /** process CT label in given interval; set Deterministic accordingly */
    @PortedFrom(file = "modelCacheIan.h", name = "processLabelInterval")
    private void processLabelInterval(DLDag DLHeap, List<ConceptWDep> start) {
        for (int i = 0; i < start.size(); i++) {
            ConceptWDep p = start.get(i);
            int bp = p.getConcept();
            processConcept(DLHeap.get(bp), bp > 0, p.getDep().isEmpty());
        }
    }

    /** fills cache sets by tree.Label; set Deterministic accordingly */
    @PortedFrom(file = "modelCacheIan.h", name = "initCacheByLabel")
    private void initCacheByLabel(DLDag DLHeap, DlCompletionTree pCT) {
        processLabelInterval(DLHeap, pCT.beginl_sc());
        processLabelInterval(DLHeap, pCT.beginl_cc());
    }

    /** Create cache model of given CompletionTree using given HEAP */
    public ModelCacheIan(DLDag heap, DlCompletionTree p, boolean flagNominals, int nC,
            int nR, boolean simpleRules) {
        this(flagNominals, nC, nR, simpleRules);
        initCacheByLabel(heap, p);
        initRolesFromArcs(p);
    }

    /** empty c'tor */
    public ModelCacheIan(boolean flagNominals, int nC, int nR, boolean simpleRules) {
        super(flagNominals);
        curState = csValid;
        this.simpleRules = simpleRules;
        this.nC = nC;
        this.nR = nR;
    }

    @Override
    @PortedFrom(file = "modelCacheIan.h", name = "getState")
    public ModelCacheState getState() {
        return curState;
    }

    @PortedFrom(file = "modelCacheIan.h", name = "getDConcepts")
    private BitSet getDConcepts(boolean pos) {
        return pos ? posDConcepts : negDConcepts;
    }

    /** get RW access to N-concepts wrt polarity */
    @PortedFrom(file = "modelCacheIan.h", name = "getNConcepts")
    private BitSet getNConcepts(boolean pos) {
        return pos ? posNConcepts : negNConcepts;
    }

    /** get RW access to extra concepts wrt deterministic flag */
    @PortedFrom(file = "modelCacheIan.h", name = "getExtra")
    private FastSet getExtra(boolean det) {
        return det ? extraDConcepts : extraNConcepts;
    }

    /** init existRoles from arcs; can be used to create pseudo-cache with deps
     * of CT edges */
    @PortedFrom(file = "modelCacheIan.h", name = "initRolesFromArcs")
    public void initRolesFromArcs(DlCompletionTree pCT) {
        List<DlCompletionTreeArc> list = pCT.getNeighbour();
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isIBlocked()) {
                addExistsRole(list.get(i).getRole());
            }
        }
        curState = csValid;
    }

    /** Get the tag identifying the cache type */
    @Override
    @PortedFrom(file = "modelCacheIan.h", name = "getCacheType")
    public ModelCacheType getCacheType() {
        return mctIan;
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
        if (simpleRules) {
            extraDConcepts.clear();
            extraNConcepts.clear();
        }
        existsRoles.clear();
        forallRoles.clear();
        funcRoles.clear();
        curState = csValid;
    }

    @PortedFrom(file = "modelCacheIan.h", name = "processConcept")
    public void processConcept(DLVertex cur, boolean pos, boolean det) {
        switch (cur.getType()) {
            case dtTop:
            case dtDataType:
            case dtDataValue:
            case dtDataExpr:
                throw new UnreachableSituationException(
                        cur.toString()
                                + " Top datatype property, datatype, data value or data expression used in an unexpected position");
            case dtNConcept:
            case dtPConcept:
            case dtNSingleton:
            case dtPSingleton:
                int toAdd = ((ClassifiableEntry) cur.getConcept()).getIndex();
                (det ? getDConcepts(pos) : getNConcepts(pos)).set(toAdd);
                break;
            case dtIrr: // for \neg \ER.Self: add R to AR-set
            case dtForall: // add AR.C roles to forallRoles
            case dtLE: // for <= n R: add R to forallRoles
                if (cur.getRole().isTop()) {
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

    @PortedFrom(file = "modelCacheIan.h", name = "processAutomaton")
    public void processAutomaton(DLVertex cur) {
        RAStateTransitions RST = cur.getRole().getAutomaton().getBase()
                .get(cur.getState());
        // for every transition starting from a given state,
        // add the role that is accepted by a transition
        List<RATransition> begin = RST.begin();
        for (int i = 0; i < begin.size(); i++) {
            for (Role r : begin.get(i).begin()) {
                forallRoles.add(r.getIndex());
            }
        }
    }

    /** adds role to exists- and func-role if necessary */
    @PortedFrom(file = "modelCacheIan.h", name = "addRoleToCache")
    private void addRoleToCache(Role R) {
        existsRoles.add(R.getIndex());
        if (R.isTopFunc()) {
            funcRoles.add(R.getIndex());
        }
    }

    /** adds role (and all its super-roles) to exists- and funcRoles */
    @PortedFrom(file = "modelCacheIan.h", name = "addExistsRole")
    private void addExistsRole(Role R) {
        addRoleToCache(R);
        List<Role> list = R.getAncestor();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            addRoleToCache(list.get(i));
        }
    }

    @Override
    @PortedFrom(file = "modelCacheIan.h", name = "canMerge")
    public ModelCacheState canMerge(ModelCacheInterface p) {
        if (hasNominalClash(p)) {
            return csFailed;
        }
        if (p.getState() != csValid || curState != csValid) {
            return mergeStatus(p.getState(), curState);
        }
        switch (p.getCacheType()) {
            case mctConst:
                return csValid;
            case mctSingleton: {
                int Singleton = ((ModelCacheSingleton) p).getValue();
                return isMergableSingleton(Math.abs(Singleton), Singleton > 0);
            }
            case mctIan:
                return isMergableIan((ModelCacheIan) p);
            default:
                return csUnknown;
        }
    }

    @PortedFrom(file = "modelCacheIan.h", name = "isMergableSingleton")
    public ModelCacheState isMergableSingleton(int Singleton, boolean pos) {
        assert Singleton != 0;
        // deterministic clash
        if (getDConcepts(!pos).get(Singleton)) {
            return csInvalid;
        } else if (getNConcepts(!pos).get(Singleton)) {
            return csFailed;
        }
        return csValid;
    }

    @PortedFrom(file = "modelCacheIan.h", name = "isMergableIan")
    public ModelCacheState isMergableIan(ModelCacheIan q) {
        if (posDConcepts.intersects(q.negDConcepts)
                || q.posDConcepts.intersects(negDConcepts)
        // || IfDefs.RKG_USE_SIMPLE_RULES
        // && getExtra(true).intersect(q.getExtra(true))
        ) {
            return csInvalid;
        } else if (existsRoles.intersect(q.forallRoles)
                || q.existsRoles.intersect(forallRoles)
                || funcRoles.intersect(q.funcRoles)
                || posDConcepts.intersects(q.negNConcepts)
                || posNConcepts.intersects(q.negDConcepts)
                || posNConcepts.intersects(q.negNConcepts)
                || q.posDConcepts.intersects(negNConcepts)
                || q.posNConcepts.intersects(negDConcepts)
                || q.posNConcepts.intersects(negNConcepts)
        // || IfDefs.RKG_USE_SIMPLE_RULES
        // && (getExtra(true).intersect(q.getExtra(false))
        // || getExtra(false).intersect(q.getExtra(true)) || getExtra(
        // false).intersect(q.getExtra(false)))
        ) {
            return csFailed;
        } else {
            if (simpleRules && getExtra(true).intersect(q.getExtra(true))) {
                return csInvalid;
            }
            if (simpleRules
                    && (getExtra(true).intersect(q.getExtra(false))
                            || getExtra(false).intersect(q.getExtra(true)) || getExtra(
                                false).intersect(q.getExtra(false)))) {
                return csFailed;
            }
            return csValid;
        }
    }

    @PortedFrom(file = "modelCacheIan.h", name = "merge")
    public ModelCacheState merge(ModelCacheInterface p) {
        assert p != null;
        // check for nominal clash
        if (hasNominalClash(p)) {
            curState = csFailed;
            return curState;
        }
        switch (p.getCacheType()) {
            case mctConst: // adds TOP/BOTTOM
                curState = mergeStatus(curState, p.getState());
                break;
            case mctSingleton: // adds Singleton
                int Singleton = ((ModelCacheSingleton) p).getValue();
                mergeSingleton(Math.abs(Singleton), Singleton > 0);
                break;
            case mctIan:
                mergeIan((ModelCacheIan) p);
                break;
            default:
                throw new UnreachableSituationException();
        }
        updateNominalStatus(p);
        return curState;
    }

    /** actual merge with a singleton cache */
    @PortedFrom(file = "modelCacheIan.h", name = "mergeSingleton")
    private void mergeSingleton(int Singleton, boolean pos) {
        ModelCacheState newState = isMergableSingleton(Singleton, pos);
        if (newState != csValid) {
            curState = mergeStatus(curState, newState);
        } else {
            getDConcepts(pos).set(Singleton);
        }
    }

    /** actual merge with an Ian's cache */
    @PortedFrom(file = "modelCacheIan.h", name = "mergeIan")
    private void mergeIan(ModelCacheIan p) {
        // setup curState
        curState = isMergableIan(p);
        // merge all sets:
        posDConcepts.or(p.posDConcepts);
        posNConcepts.or(p.posNConcepts);
        negDConcepts.or(p.negDConcepts);
        negNConcepts.or(p.negNConcepts);
        if (simpleRules) {
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
        l.print("\nIan cache: posDConcepts = {", posDConcepts, "}, posNConcepts = {",
                posNConcepts, "}, negDConcepts = {", negDConcepts, "}, negNConcepts = {",
                negNConcepts, "}, existsRoles = {", existsRoles, "}, forallRoles = {",
                forallRoles, "}, funcRoles = {", funcRoles, "}");
    }

    @PortedFrom(file = "modelCacheInterface.h", name = "mergeStatus")
    private ModelCacheState mergeStatus(ModelCacheState s1, ModelCacheState s2) {
        // if one of caches is definitely UNSAT, then merge will be the same
        if (s1 == csInvalid || s2 == csInvalid) {
            return csInvalid;
        }
        // if one of caches is unsure then result will be the same
        if (s1 == csFailed || s2 == csFailed) {
            return csFailed;
        }
        // if one of caches is not inited, than result would be the same
        if (s1 == csUnknown || s2 == csUnknown) {
            return csUnknown;
        } else {
            // valid+valid = valid
            return csValid;
        }
    }
}
