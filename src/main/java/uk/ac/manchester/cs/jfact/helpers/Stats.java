package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.DlCompletionGraph;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/** stats. */
public class Stats implements Serializable {

    private static final String TAB = "\n                ";
    private static final String THERE_WERE_MADE = "\nThere were made ";

    /** accumulated stats. */
    public static class AccumulatedStatistic implements Serializable {

        /** accumulated statistic. */
        private int total;
        /** current session statistic. */
        private int local;

        /** c'tor: link itself to the list. */
        public AccumulatedStatistic() {
            total = 0;
            local = 0;
        }

        /** increment local value. */
        public void inc() {
            ++local;
        }

        /** add local value to a global one. */
        public void accumulate() {
            total += local;
            local = 0;
        }

        /**
         * @param l
         *        l
         * @param b
         *        b
         * @param s1
         *        s1
         * @param s2
         *        s2
         */
        public void print(LogAdapter l, boolean b, String s1, String s2) {
            l.print(s1);
            if (b) {
                l.print(local);
            } else {
                l.print(total);
            }
            l.print(s2);
        }
    }

    // statistic elements
    /** all AccumulatedStatistic members are linked together. */
    private final List<AccumulatedStatistic> root = new ArrayList<>();
    /** The n tactic calls. */
    private final AccumulatedStatistic nTacticCalls = build(root);
    /** The n useless. */
    private final AccumulatedStatistic nUseless = build(root);
    /** The n id calls. */
    private final AccumulatedStatistic nIdCalls = build(root);
    /** The n singleton calls. */
    private final AccumulatedStatistic nSingletonCalls = build(root);
    /** The n or calls. */
    private final AccumulatedStatistic nOrCalls = build(root);
    /** The n or br calls. */
    private final AccumulatedStatistic nOrBrCalls = build(root);
    /** The n and calls. */
    private final AccumulatedStatistic nAndCalls = build(root);
    /** The n some calls. */
    private final AccumulatedStatistic nSomeCalls = build(root);
    /** The n all calls. */
    private final AccumulatedStatistic nAllCalls = build(root);
    /** The n func calls. */
    private final AccumulatedStatistic nFuncCalls = build(root);
    /** The n le calls. */
    private final AccumulatedStatistic nLeCalls = build(root);
    /** The n ge calls. */
    private final AccumulatedStatistic nGeCalls = build(root);
    /** The n nn calls. */
    private final AccumulatedStatistic nNNCalls = build(root);
    /** The n merge calls. */
    private final AccumulatedStatistic nMergeCalls = build(root);
    /** The n auto empty lookups. */
    private final AccumulatedStatistic nAutoEmptyLookups = build(root);
    /** The n auto trans lookups. */
    private final AccumulatedStatistic nAutoTransLookups = build(root);
    /** The n s rule add. */
    private final AccumulatedStatistic nSRuleAdd = build(root);
    /** The n s rule fire. */
    private final AccumulatedStatistic nSRuleFire = build(root);
    /** The n state saves. */
    private final AccumulatedStatistic nStateSaves = build(root);
    /** The n state restores. */
    private final AccumulatedStatistic nStateRestores = build(root);
    /** The n node saves. */
    private final AccumulatedStatistic nNodeSaves = build(root);
    /** The n node restores. */
    private final AccumulatedStatistic nNodeRestores = build(root);
    /** The n lookups. */
    private final AccumulatedStatistic nLookups = build(root);
    /** The n fairness violations. */
    private final AccumulatedStatistic nFairnessViolations = build(root);
    // reasoning cache
    /** The n cache try. */
    private final AccumulatedStatistic nCacheTry = build(root);
    /** The n cache failed no cache. */
    private final AccumulatedStatistic nCacheFailedNoCache = build(root);
    /** The n cache failed shallow. */
    private final AccumulatedStatistic nCacheFailedShallow = build(root);
    /** The n cache failed. */
    private final AccumulatedStatistic nCacheFailed = build(root);
    /** The n cached sat. */
    private final AccumulatedStatistic nCachedSat = build(root);
    /** The n cached unsat. */
    private final AccumulatedStatistic nCachedUnsat = build(root);

    /**
     * @param list
     *        the list
     * @return the accumulated statistic
     */
    public static AccumulatedStatistic build(List<AccumulatedStatistic> list) {
        AccumulatedStatistic toReturn = new AccumulatedStatistic();
        list.add(toReturn);
        return toReturn;
    }

    /** Accumulate. */
    public void accumulate() {
        root.forEach(cur -> cur.accumulate());
    }

    /**
     * Log statistic data.
     * 
     * @param o
     *        the o
     * @param needLocal
     *        the need local
     * @param cGraph
     *        the c graph
     * @param options
     *        the options
     */
    public void logStatisticData(LogAdapter o, boolean needLocal, DlCompletionGraph cGraph,
        JFactReasonerConfiguration options) {
        if (options.isUseReasoningStatistics()) {
            nTacticCalls.print(o, needLocal, THERE_WERE_MADE, " tactic operations, of which:");
            nIdCalls.print(o, needLocal, "\n    CN   operations: ", "");
            nSingletonCalls.print(o, needLocal, "\n           including ", " singleton ones");
            nOrCalls.print(o, needLocal, "\n    OR   operations: ", "");
            nOrBrCalls.print(o, needLocal, "\n           ", " of which are branching");
            nAndCalls.print(o, needLocal, "\n    AND  operations: ", "");
            nSomeCalls.print(o, needLocal, "\n    SOME operations: ", "");
            nAllCalls.print(o, needLocal, "\n    ALL  operations: ", "");
            nFuncCalls.print(o, needLocal, "\n    Func operations: ", "");
            nLeCalls.print(o, needLocal, "\n    LE   operations: ", "");
            nGeCalls.print(o, needLocal, "\n    GE   operations: ", "");
            nUseless.print(o, needLocal, "\n    N/A  operations: ", "");
            nNNCalls.print(o, needLocal, THERE_WERE_MADE, " NN rule application");
            nMergeCalls.print(o, needLocal, THERE_WERE_MADE, " merging operations");
            nAutoEmptyLookups.print(o, needLocal, THERE_WERE_MADE, " RA empty transition lookups");
            nAutoTransLookups.print(o, needLocal, THERE_WERE_MADE, " RA applicable transition lookups");
            nSRuleAdd.print(o, needLocal, THERE_WERE_MADE, " simple rule additions");
            nSRuleFire.print(o, needLocal, "\n       of which ", " simple rules fired");
            nStateSaves.print(o, needLocal, THERE_WERE_MADE, " save(s) of global state");
            nStateRestores.print(o, needLocal, THERE_WERE_MADE, " restore(s) of global state");
            nNodeSaves.print(o, needLocal, THERE_WERE_MADE, " save(s) of tree state");
            nNodeRestores.print(o, needLocal, THERE_WERE_MADE, " restore(s) of tree state");
            nLookups.print(o, needLocal, THERE_WERE_MADE, " concept lookups");
            if (options.isUseFairness()) {
                nFairnessViolations.print(o, needLocal, "\nThere were ", " fairness constraints violation");
            }
            nCacheTry.print(o, needLocal, THERE_WERE_MADE, " tries to cache completion tree node, of which:");
            nCacheFailedNoCache.print(o, needLocal, TAB, " fails due to cache absence");
            nCacheFailedShallow.print(o, needLocal, TAB, " fails due to shallow node");
            nCacheFailed.print(o, needLocal, TAB, " fails due to cache merge failure");
            nCachedSat.print(o, needLocal, TAB, " cached satisfiable nodes");
            nCachedUnsat.print(o, needLocal, TAB, " cached unsatisfiable nodes");
        }
        if (!needLocal) {
            o.print("\nThe maximal graph size is ").print(cGraph.maxSize()).print(" nodes");
        }
    }

    /** @return the n tactic calls */
    public AccumulatedStatistic getnTacticCalls() {
        return nTacticCalls;
    }

    /** @return the n useless */
    public AccumulatedStatistic getnUseless() {
        return nUseless;
    }

    /** @return the n id calls */
    public AccumulatedStatistic getnIdCalls() {
        return nIdCalls;
    }

    /**
     * Gets the n singleton calls.
     * 
     * @return the n singleton calls
     */
    public AccumulatedStatistic getnSingletonCalls() {
        return nSingletonCalls;
    }

    /** @return the n or calls */
    public AccumulatedStatistic getnOrCalls() {
        return nOrCalls;
    }

    /** @return the n or br calls */
    public AccumulatedStatistic getnOrBrCalls() {
        return nOrBrCalls;
    }

    /** @return the n and calls */
    public AccumulatedStatistic getnAndCalls() {
        return nAndCalls;
    }

    /** @return the n some calls */
    public AccumulatedStatistic getnSomeCalls() {
        return nSomeCalls;
    }

    /** @return the n all calls */
    public AccumulatedStatistic getnAllCalls() {
        return nAllCalls;
    }

    /** @return the n func calls */
    public AccumulatedStatistic getnFuncCalls() {
        return nFuncCalls;
    }

    /** @return the n le calls */
    public AccumulatedStatistic getnLeCalls() {
        return nLeCalls;
    }

    /** @return the n ge calls */
    public AccumulatedStatistic getnGeCalls() {
        return nGeCalls;
    }

    /** @return the n nn calls */
    public AccumulatedStatistic getnNNCalls() {
        return nNNCalls;
    }

    /** @return the n merge calls */
    public AccumulatedStatistic getnMergeCalls() {
        return nMergeCalls;
    }

    /** @return the n auto empty lookups */
    public AccumulatedStatistic getnAutoEmptyLookups() {
        return nAutoEmptyLookups;
    }

    /** @return the n auto trans lookups */
    public AccumulatedStatistic getnAutoTransLookups() {
        return nAutoTransLookups;
    }

    /** @return the n s rule add */
    public AccumulatedStatistic getnSRuleAdd() {
        return nSRuleAdd;
    }

    /** @return the n s rule fire */
    public AccumulatedStatistic getnSRuleFire() {
        return nSRuleFire;
    }

    /** @return the n state saves */
    public AccumulatedStatistic getnStateSaves() {
        return nStateSaves;
    }

    /** @return the n state restores */
    public AccumulatedStatistic getnStateRestores() {
        return nStateRestores;
    }

    /** @return the n node saves */
    public AccumulatedStatistic getnNodeSaves() {
        return nNodeSaves;
    }

    /** @return the n node restores */
    public AccumulatedStatistic getnNodeRestores() {
        return nNodeRestores;
    }

    /** @return the n lookups */
    public AccumulatedStatistic getnLookups() {
        return nLookups;
    }

    /** @return the n fairness violations */
    public AccumulatedStatistic getnFairnessViolations() {
        return nFairnessViolations;
    }

    /** @return the n cache try */
    public AccumulatedStatistic getnCacheTry() {
        return nCacheTry;
    }

    /** @return the n cache failed no cache */
    public AccumulatedStatistic getnCacheFailedNoCache() {
        return nCacheFailedNoCache;
    }

    /** @return the n cache failed shallow */
    public AccumulatedStatistic getnCacheFailedShallow() {
        return nCacheFailedShallow;
    }

    /** @return the n cache failed */
    public AccumulatedStatistic getnCacheFailed() {
        return nCacheFailed;
    }

    /** @return the n cached sat */
    public AccumulatedStatistic getnCachedSat() {
        return nCachedSat;
    }

    /** @return the n cached unsat */
    public AccumulatedStatistic getnCachedUnsat() {
        return nCachedUnsat;
    }
}
