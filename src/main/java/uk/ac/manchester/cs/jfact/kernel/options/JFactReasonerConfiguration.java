package uk.ac.manchester.cs.jfact.kernel.options;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.NullReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import conformance.PortedFrom;

/** configuration. */
public class JFactReasonerConfiguration implements OWLReasonerConfiguration,
        Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 11000L;
    /**
     * Option 'dumpQuery' dumps sub-TBox relevant to given
     * satisfiability/subsumption query.
     */
    private boolean dumpQuery = false;
    /**
     * Option 'absorptionFlags' sets up absorption process for general axioms.
     * It text field of arbitrary length; every symbol means the absorption
     * action: (B)ottom Absorption), (T)op absorption, (E)quivalent concepts
     * replacement, (C)oncept absorption, (N)egated concept absorption, (F)orall
     * expression replacement, (R)ole absorption, (S)plit
     */
    private static StringOption absorptionFlags = getOption("absorptionFlags",
            "BTECFSR");
    /**
     * Option 'alwaysPreferEquals' allows user to enforce usage of C=D
     * definition instead of C[=D during absorption, even if implication
     * appeares earlier in stream of axioms.
     */
    private boolean alwaysPreferEquals = true;
    /**
     * Option 'orSortSub' define the sorting order of OR vertices in the DAG
     * used in subsumption tests. Option has form of string 'Mop', where 'M' is
     * a sort field (could be 'D' for depth, 'S' for size, 'F' for frequency,
     * and '0' for no sorting), 'o' is a order field (could be 'a' for ascending
     * and 'd' for descending mode), and 'p' is a preference field (could be 'p'
     * for preferencing non-generating rules and 'n' for not doing so).
     */
    private static StringOption orSortSub = getOption("orSortSub", "0");
    /**
     * Option 'orSortSat' define the sorting order of OR vertices in the DAG
     * used in satisfiability tests (used mostly in caching). Option has form of
     * string 'Mop', see orSortSub for details.
     */
    private static StringOption orSortSat = getOption("orSortSat", "0");
    /**
     * Option 'IAOEFLG' define the priorities of different operations in TODO
     * list. Possible values are 7-digit strings with ony possible digit are
     * 0-6. The digits on the places 1, 2, ..., 7 are for priority of Id, And,
     * Or, Exists, Forall, LE and GE operations respectively. The smaller number
     * means the higher priority. All other constructions (TOP, BOTTOM, etc) has
     * priority 0.
     */
    private static StringOption IAOEFLG = getOption("IAOEFLG", "1263005");
    /**
     * Option 'useSemanticBranching' switch semantic branching on and off. The
     * usage of semantic branching usually leads to faster reasoning, but
     * sometime could give small overhead.
     */
    @PortedFrom(file = "dlTBox.h", name = "useSemanticBranching")
    private boolean useSemanticBranching = true;
    /**
     * Option 'useBackjumping' switch backjumping on and off. The usage of
     * backjumping usually leads to much faster reasoning.
     */
    private boolean useBackjumping = true;
    /** tell reasoner to use verbose output. */
    private boolean verboseOutput = false;
    /**
     * Option 'useLazyBlocking' makes checking of blocking status as small as
     * possible. This greatly increase speed of reasoning.
     */
    @PortedFrom(file = "dlTBox.h", name = "useLazyBlocking")
    private boolean useLazyBlocking = true;
    /**
     * Option 'useAnywhereBlocking' allow user to choose between Anywhere and
     * Ancestor blocking.
     */
    @PortedFrom(file = "dlTBox.h", name = "useAnywhereBlocking")
    private boolean useAnywhereBlocking = true;
    /**
     * Option 'useCompletelyDefined' leads to simpler Taxonomy creation if TBox
     * contains no non-primitive concepts. Unfortunately, it is quite rare case.
     */
    private boolean useCompletelyDefined = true;
    /**
     * Option 'useSpecialDomains' (development) controls the special processing
     * of R and D for non-simple roles. Should always be set to true.
     */
    @PortedFrom(file = "dlTBox.h", name = "useSpecialDomains")
    private boolean useSpecialDomains = true;
    /**
     * Option 'useIncrementalReasoning' (development) allows one to reason
     * efficiently about small changes in the ontology.
     */
    private boolean useIncrementalReasoning = false;
    /** The use axiom splitting. */
    @PortedFrom(file = "Kernel.h", name = "useAxiomSplitting")
    private boolean useAxiomSplitting = false;
    /**
     * Internal use only. Option 'skipBeforeBlock' allow user to skip given
     * number of nodes before make a block.
     */
    // private static final Option skipBeforeBlock =
    // getOption("skipBeforeBlock", 0);
    /** flag to use caching during completion tree construction */
    @PortedFrom(file = "dlTBox.h", name = "useNodeCache")
    private boolean useNodeCache = true;
    /** whether we use sorted reasoning; depends on some simplifications. */
    @PortedFrom(file = "dlTBox.h", name = "useSortedReasoning")
    private boolean useSortedReasoning = true;
    /** Option 'allowUndefinedNames' describes the policy of undefined names. */
    @PortedFrom(file = "Kernel.h", name = "allowUndefinedNames")
    private boolean allowUndefinedNames = true;
    /**
     * Option 'queryAnswering', if true, switches the reasoner to a query
     * answering mode.
     */
    @PortedFrom(file = "Kernel.h", name = "queryAnswering")
    private boolean queryAnswering = false;

    /**
     * Checks if is use sorted reasoning.
     * 
     * @return true, if is use sorted reasoning
     */
    public boolean isUseSortedReasoning() {
        return useSortedReasoning;
    }

    /**
     * Sets the use sorted reasoning.
     * 
     * @param useSortedReasoning
     *        the new use sorted reasoning
     */
    public void setUseSortedReasoning(boolean useSortedReasoning) {
        this.useSortedReasoning = useSortedReasoning;
    }

    /**
     * Checks if is use special domains.
     * 
     * @return true, if is use special domains
     */
    public boolean isUseSpecialDomains() {
        return useSpecialDomains;
    }

    /**
     * @param b
     *        value for allow undefined entities
     */
    public void setAllowUndefinedNames(boolean b) {
        allowUndefinedNames = b;
    }

    /** @return is allow undefined entities */
    public boolean isAllowUndefinedNames() {
        return allowUndefinedNames;
    }

    /**
     * @param b
     *        value for query answering
     */
    public void setQueryAnswering(boolean b) {
        queryAnswering = b;
    }

    /** @return is query answering */
    public boolean isQueryAnswering() {
        return queryAnswering;
    }

    /**
     * Sets the use special domains.
     * 
     * @param b
     *        the new use special domains
     */
    public void setUseSpecialDomains(boolean b) {
        useSpecialDomains = b;
    }

    /**
     * Checks if is use lazy blocking.
     * 
     * @return true, if is use lazy blocking
     */
    public boolean isUseLazyBlocking() {
        return useLazyBlocking;
    }

    /**
     * Sets the use lazy blocking.
     * 
     * @param b
     *        the new use lazy blocking
     */
    public void setUseLazyBlocking(boolean b) {
        useLazyBlocking = b;
    }

    /**
     * Gets the use anywhere blocking.
     * 
     * @return the use anywhere blocking
     */
    public boolean getUseAnywhereBlocking() {
        return useAnywhereBlocking;
    }

    /**
     * Sets the use anywhere blocking.
     * 
     * @param b
     *        the new use anywhere blocking
     */
    public void setUseAnywhereBlocking(boolean b) {
        useAnywhereBlocking = b;
    }

    /**
     * Checks if is use semantic branching.
     * 
     * @return true, if is use semantic branching
     */
    public boolean isUseSemanticBranching() {
        return useSemanticBranching;
    }

    /**
     * Sets the use semantic branching.
     * 
     * @param b
     *        the new use semantic branching
     */
    public void setUseSemanticBranching(boolean b) {
        useSemanticBranching = b;
    }

    /**
     * set flag to use node cache to value VAL.
     * 
     * @param val
     *        the new use node cache
     */
    @PortedFrom(file = "dlTBox.h", name = "setUseNodeCache")
    public void setUseNodeCache(boolean val) {
        useNodeCache = val;
    }

    /**
     * Checks if is use node cache.
     * 
     * @return true, if is use node cache
     */
    public boolean isUseNodeCache() {
        return useNodeCache;
    }

    /** set of all avaliable (given) options. */
    private final Map<String, StringOption> base = new HashMap<String, StringOption>();

    /**
     * Gets the option.
     * 
     * @param name
     *        the name
     * @param s
     *        the s
     * @return the option
     */
    public static StringOption getOption(String name, String s) {
        return new StringOption(name, s);
    }

    /**
     * Register option.
     * 
     * @param defVal
     *        the def val
     */
    private void registerOption(StringOption defVal) {
        base.put(defVal.getOptionName(), defVal);
    }

    /**
     * Gets the.
     * 
     * @param <O>
     *        the generic type
     * @param name
     *        the name
     * @return the o
     */
    @SuppressWarnings("unchecked")
    public <O> O get(String name) {
        return (O) base.get(name).getValue();
    }

    /**
     * Gets the oR sort sat.
     * 
     * @return the oR sort sat
     */
    public String getORSortSat() {
        return this.get("orSortSat");
    }

    /**
     * Sets the or sort sat.
     * 
     * @param defSat
     *        the new or sort sat
     */
    public void setorSortSat(String defSat) {
        registerOption(getOption("orSortSat", defSat));
    }

    /**
     * Gets the oR sort sub.
     * 
     * @return the oR sort sub
     */
    public String getORSortSub() {
        return this.get("orSortSub");
    }

    /**
     * Sets the or sort sub.
     * 
     * @param defSat
     *        the new or sort sub
     */
    public void setorSortSub(String defSat) {
        registerOption(getOption("orSortSub", defSat));
    }

    /**
     * Gets the use anywhere blocking.
     * 
     * @return the use anywhere blocking
     */
    public boolean getuseAnywhereBlocking() {
        return useAnywhereBlocking;
    }

    /**
     * Gets the use backjumping.
     * 
     * @return the use backjumping
     */
    public boolean getuseBackjumping() {
        return useBackjumping;
    }

    /**
     * Gets the use lazy blocking.
     * 
     * @return the use lazy blocking
     */
    public boolean getuseLazyBlocking() {
        return useLazyBlocking;
    }

    /**
     * Gets the use semantic branching.
     * 
     * @return the use semantic branching
     */
    public boolean getuseSemanticBranching() {
        return useSemanticBranching;
    }

    /**
     * Gets the verbose output.
     * 
     * @return the verbose output
     */
    public boolean getverboseOutput() {
        return verboseOutput;
    }

    /**
     * Gets the dump query.
     * 
     * @return the dump query
     */
    public boolean getdumpQuery() {
        return dumpQuery;
    }

    /**
     * Sets the dump query.
     * 
     * @param value
     *        the new dump query
     */
    public void setdumpQuery(boolean value) {
        dumpQuery = value;
    }

    /**
     * Gets the use completely defined.
     * 
     * @return the use completely defined
     */
    public boolean getuseCompletelyDefined() {
        return useCompletelyDefined;
    }

    /**
     * Gets the always prefer equals.
     * 
     * @return the always prefer equals
     */
    public boolean getalwaysPreferEquals() {
        return alwaysPreferEquals;
    }

    /**
     * Gets the absorption flags.
     * 
     * @return the absorption flags
     */
    public String getabsorptionFlags() {
        return this.get("absorptionFlags");
    }

    /**
     * Gets the iaoeflg.
     * 
     * @return the iaoeflg
     */
    public String getIAOEFLG() {
        return this.get("IAOEFLG");
    }

    /**
     * Sets the use anywhere blocking.
     * 
     * @param b
     *        the new use anywhere blocking
     */
    public void setuseAnywhereBlocking(boolean b) {
        useAnywhereBlocking = b;
    }

    /** The progress monitor. */
    private ReasonerProgressMonitor progressMonitor = new NullReasonerProgressMonitor();
    /** The fresh entity policy. */
    @Nonnull
    private FreshEntityPolicy freshEntityPolicy = FreshEntityPolicy.ALLOW;
    /** The individual node set policy. */
    @Nonnull
    private IndividualNodeSetPolicy individualNodeSetPolicy = IndividualNodeSetPolicy.BY_NAME;
    /** The time out. */
    private long timeOut = Long.MAX_VALUE;

    /** Instantiates a new j fact reasoner configuration. */
    public JFactReasonerConfiguration() {
        base.put(absorptionFlags.getOptionName(), absorptionFlags);
        base.put(IAOEFLG.getOptionName(), IAOEFLG);
        base.put(orSortSat.getOptionName(), orSortSat);
        base.put(orSortSub.getOptionName(), orSortSub);
    }

    /**
     * Instantiates a new j fact reasoner configuration.
     * 
     * @param source
     *        the source
     */
    public JFactReasonerConfiguration(OWLReasonerConfiguration source) {
        this();
        progressMonitor = source.getProgressMonitor();
        freshEntityPolicy = source.getFreshEntityPolicy();
        individualNodeSetPolicy = source.getIndividualNodeSetPolicy();
        timeOut = source.getTimeOut();
    }

    /**
     * Instantiates a new j fact reasoner configuration.
     * 
     * @param source
     *        the source
     */
    public JFactReasonerConfiguration(JFactReasonerConfiguration source) {
        this((OWLReasonerConfiguration) source);
        alwaysPreferEquals = source.alwaysPreferEquals;
        DEBUG_SAVE_RESTORE = source.DEBUG_SAVE_RESTORE;
        dumpQuery = source.dumpQuery;
        FPP_DEBUG_SPLIT_MODULES = source.FPP_DEBUG_SPLIT_MODULES;
        freshEntityPolicy = source.freshEntityPolicy;
        individualNodeSetPolicy = source.individualNodeSetPolicy;
        RKG_DEBUG_ABSORPTION = source.RKG_DEBUG_ABSORPTION;
        RKG_IMPROVE_SAVE_RESTORE_DEPSET = source.RKG_IMPROVE_SAVE_RESTORE_DEPSET;
        RKG_PRINT_DAG_USAGE = source.RKG_PRINT_DAG_USAGE;
        RKG_UPDATE_RND_FROM_SUPERROLES = source.RKG_UPDATE_RND_FROM_SUPERROLES;
        RKG_USE_DYNAMIC_BACKJUMPING = source.RKG_USE_DYNAMIC_BACKJUMPING;
        RKG_USE_FAIRNESS = source.RKG_USE_FAIRNESS;
        RKG_USE_SIMPLE_RULES = source.RKG_USE_SIMPLE_RULES;
        RKG_USE_SORTED_REASONING = source.RKG_USE_SORTED_REASONING;
        splits = source.splits;
        timeOut = source.timeOut;
        TMP_PRINT_TAXONOMY_INFO = source.TMP_PRINT_TAXONOMY_INFO;
        USE_BLOCKING_STATISTICS = source.USE_BLOCKING_STATISTICS;
        USE_LOGGING = source.USE_LOGGING;
        USE_REASONING_STATISTICS = source.USE_REASONING_STATISTICS;
        useADInModuleExtraction = source.useADInModuleExtraction;
        useAnywhereBlocking = source.useAnywhereBlocking;
        useAxiomSplitting = source.useAxiomSplitting;
        useBackjumping = source.useBackjumping;
        useCompletelyDefined = source.useCompletelyDefined;
        useELReasoner = source.useELReasoner;
        useIncrementalReasoning = source.useIncrementalReasoning;
        useLazyBlocking = source.useLazyBlocking;
        useSemanticBranching = source.useSemanticBranching;
        useSpecialDomains = source.useSpecialDomains;
        useUndefinedNames = source.useUndefinedNames;
        verboseOutput = source.verboseOutput;
    }

    @Override
    public FreshEntityPolicy getFreshEntityPolicy() {
        return freshEntityPolicy;
    }

    @Override
    public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return individualNodeSetPolicy;
    }

    @Override
    public ReasonerProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    @Override
    public long getTimeOut() {
        return timeOut;
    }

    /**
     * Sets the verbose output.
     * 
     * @param b
     *        the new verbose output
     */
    public void setverboseOutput(boolean b) {
        verboseOutput = b;
    }

    /** The Class StringOption. */
    static class StringOption implements Serializable {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 11000L;
        /** option name. */
        private final String optionName;
        /** The value. */
        private final String value;

        /**
         * Instantiates a new string option.
         * 
         * @param name
         *        the name
         * @param defVal
         *        the def val
         */
        public StringOption(String name, String defVal) {
            optionName = name;
            value = defVal;
        }

        /**
         * Gets the option name.
         * 
         * @return the option name
         */
        public String getOptionName() {
            return optionName;
        }

        /**
         * Gets the value.
         * 
         * @param <O>
         *        the generic type
         * @return the value
         */
        @SuppressWarnings("unchecked")
        public <O> O getValue() {
            return (O) value;
        }
    }

    /** The use logging. */
    private boolean USE_LOGGING = false;
    /** The rkg debug absorption. */
    private boolean RKG_DEBUG_ABSORPTION = false;
    /** The rkg improve save restore depset. */
    private boolean RKG_IMPROVE_SAVE_RESTORE_DEPSET = false;
    /** The rkg print dag usage. */
    private boolean RKG_PRINT_DAG_USAGE = false;
    /** The rkg use simple rules. */
    private boolean RKG_USE_SIMPLE_RULES = false;
    /** The rkg use sorted reasoning. */
    private boolean RKG_USE_SORTED_REASONING = false;
    /** The use reasoning statistics. */
    private boolean USE_REASONING_STATISTICS = false;
    /** The rkg update rnd from superroles. */
    private boolean RKG_UPDATE_RND_FROM_SUPERROLES = false;
    /** The use blocking statistics. */
    private boolean USE_BLOCKING_STATISTICS = false;
    /** The rkg use dynamic backjumping. */
    private boolean RKG_USE_DYNAMIC_BACKJUMPING = false;
    /** The tmp print taxonomy info. */
    private boolean TMP_PRINT_TAXONOMY_INFO = false;
    /** The debug save restore. */
    private boolean DEBUG_SAVE_RESTORE = false;
    /** The rkg use fairness. */
    private boolean RKG_USE_FAIRNESS = false;
    /** The fpp debug split modules. */
    private boolean FPP_DEBUG_SPLIT_MODULES = false;
    /** The splits. */
    private boolean splits = false;
    /** whether EL polynomial reasoner should be used. */
    private boolean useELReasoner = false;
    /** allow reasoner to use undefined names in queries. */
    private boolean useUndefinedNames = true;

    /**
     * Checks if is logging active.
     * 
     * @return true, if is logging active
     */
    public boolean isLoggingActive() {
        return USE_LOGGING;
    }

    /**
     * Sets the logging active.
     * 
     * @param b
     *        the new logging active
     */
    public void setLoggingActive(boolean b) {
        USE_LOGGING = b;
    }

    /**
     * Checks if is absorption logging active.
     * 
     * @return true, if is absorption logging active
     */
    public boolean isAbsorptionLoggingActive() {
        return RKG_DEBUG_ABSORPTION;
    }

    /**
     * Sets the absorption logging active.
     * 
     * @param b
     *        the new absorption logging active
     */
    public void setAbsorptionLoggingActive(boolean b) {
        RKG_DEBUG_ABSORPTION = b;
    }

    /**
     * Checks if is rkg improve save restore depset.
     * 
     * @return true, if is rkg improve save restore depset
     */
    public boolean isRKG_IMPROVE_SAVE_RESTORE_DEPSET() {
        return RKG_IMPROVE_SAVE_RESTORE_DEPSET;
    }

    /**
     * Sets the rkg improve save restore depset.
     * 
     * @param b
     *        the new rkg improve save restore depset
     */
    public void setRKG_IMPROVE_SAVE_RESTORE_DEPSET(boolean b) {
        RKG_IMPROVE_SAVE_RESTORE_DEPSET = b;
    }

    /**
     * Checks if is rkg print dag usage.
     * 
     * @return true, if is rkg print dag usage
     */
    public boolean isRKG_PRINT_DAG_USAGE() {
        return RKG_PRINT_DAG_USAGE;
    }

    /**
     * Sets the rkg print dag usage.
     * 
     * @param b
     *        the new rkg print dag usage
     */
    public void setRKG_PRINT_DAG_USAGE(boolean b) {
        RKG_PRINT_DAG_USAGE = b;
    }

    /**
     * Checks if is rkg use simple rules.
     * 
     * @return true, if is rkg use simple rules
     */
    public boolean isRKG_USE_SIMPLE_RULES() {
        return RKG_USE_SIMPLE_RULES;
    }

    /**
     * Sets the rkg use simple rules.
     * 
     * @param b
     *        the new rkg use simple rules
     */
    public void setRKG_USE_SIMPLE_RULES(boolean b) {
        RKG_USE_SIMPLE_RULES = b;
    }

    /**
     * Checks if is rkg use sorted reasoning.
     * 
     * @return true, if is rkg use sorted reasoning
     */
    public boolean isRKG_USE_SORTED_REASONING() {
        return RKG_USE_SORTED_REASONING;
    }

    /**
     * Sets the rkg use sorted reasoning.
     * 
     * @param b
     *        the new rkg use sorted reasoning
     */
    public void setRKG_USE_SORTED_REASONING(boolean b) {
        RKG_USE_SORTED_REASONING = b;
    }

    /**
     * Checks if is use reasoning statistics.
     * 
     * @return true, if is use reasoning statistics
     */
    public boolean isUSE_REASONING_STATISTICS() {
        return USE_REASONING_STATISTICS;
    }

    /**
     * Sets the use reasoning statistics.
     * 
     * @param b
     *        the new use reasoning statistics
     */
    public void setUSE_REASONING_STATISTICS(boolean b) {
        USE_REASONING_STATISTICS = b;
    }

    /**
     * Checks if is rkg update rnd from superroles.
     * 
     * @return true, if is rkg update rnd from superroles
     */
    public boolean isRKG_UPDATE_RND_FROM_SUPERROLES() {
        return RKG_UPDATE_RND_FROM_SUPERROLES;
    }

    /**
     * Sets the rkg update rnd from superroles.
     * 
     * @param b
     *        the new rkg update rnd from superroles
     */
    public void setRKG_UPDATE_RND_FROM_SUPERROLES(boolean b) {
        RKG_UPDATE_RND_FROM_SUPERROLES = b;
    }

    /**
     * Checks if is use blocking statistics.
     * 
     * @return true, if is use blocking statistics
     */
    public boolean isUSE_BLOCKING_STATISTICS() {
        return USE_BLOCKING_STATISTICS;
    }

    /**
     * Sets the use blocking statistics.
     * 
     * @param b
     *        the new use blocking statistics
     */
    public void setUSE_BLOCKING_STATISTICS(boolean b) {
        USE_BLOCKING_STATISTICS = b;
    }

    /**
     * Checks if is rkg use dynamic backjumping.
     * 
     * @return true, if is rkg use dynamic backjumping
     */
    public boolean isRKG_USE_DYNAMIC_BACKJUMPING() {
        return RKG_USE_DYNAMIC_BACKJUMPING;
    }

    /**
     * Sets the rkg use dynamic backjumping.
     * 
     * @param b
     *        the new rkg use dynamic backjumping
     */
    public void setRKG_USE_DYNAMIC_BACKJUMPING(boolean b) {
        RKG_USE_DYNAMIC_BACKJUMPING = b;
    }

    /**
     * Checks if is tmp print taxonomy info.
     * 
     * @return true, if is tmp print taxonomy info
     */
    public boolean isTMP_PRINT_TAXONOMY_INFO() {
        return TMP_PRINT_TAXONOMY_INFO;
    }

    /**
     * Sets the tmp print taxonomy info.
     * 
     * @param b
     *        the new tmp print taxonomy info
     */
    public void setTMP_PRINT_TAXONOMY_INFO(boolean b) {
        TMP_PRINT_TAXONOMY_INFO = b;
    }

    /**
     * Checks if is debug save restore.
     * 
     * @return true, if is debug save restore
     */
    public boolean isDEBUG_SAVE_RESTORE() {
        return DEBUG_SAVE_RESTORE;
    }

    /**
     * Sets the debug save restore.
     * 
     * @param b
     *        the new debug save restore
     */
    public void setDEBUG_SAVE_RESTORE(boolean b) {
        DEBUG_SAVE_RESTORE = b;
    }

    /**
     * Checks if is rkg use fairness.
     * 
     * @return true, if is rkg use fairness
     */
    public boolean isRKG_USE_FAIRNESS() {
        return RKG_USE_FAIRNESS;
    }

    /**
     * Sets the rkg use fairness.
     * 
     * @param b
     *        the new rkg use fairness
     */
    public void setRKG_USE_FAIRNESS(boolean b) {
        RKG_USE_FAIRNESS = b;
    }

    /**
     * Checks if is fpp debug split modules.
     * 
     * @return true, if is fpp debug split modules
     */
    public boolean isFPP_DEBUG_SPLIT_MODULES() {
        return FPP_DEBUG_SPLIT_MODULES;
    }

    /**
     * Sets the fpp debug split modules.
     * 
     * @param b
     *        the new fpp debug split modules
     */
    public void setFPP_DEBUG_SPLIT_MODULES(boolean b) {
        FPP_DEBUG_SPLIT_MODULES = b;
    }

    /**
     * Checks if is splits.
     * 
     * @return true, if is splits
     */
    public boolean isSplits() {
        return splits;
    }

    /**
     * Sets the splits.
     * 
     * @param splits
     *        the new splits
     */
    public void setSplits(boolean splits) {
        this.splits = splits;
    }

    /**
     * Gets the log.
     * 
     * @return the log
     */
    public LogAdapter getLog() {
        if (USE_LOGGING) {
            if (logAdapterStream == null) {
                logAdapterStream = new LogAdapterStream(System.out);
            }
            return logAdapterStream;
        } else {
            return empty;
        }
    }

    /**
     * Gets the absorption log.
     * 
     * @return the absorption log
     */
    public LogAdapter getAbsorptionLog() {
        if (RKG_DEBUG_ABSORPTION) {
            if (logAbsorptionAdapterStream == null) {
                logAbsorptionAdapterStream = new LogAdapterStream(System.out);
            }
            return logAbsorptionAdapterStream;
        } else {
            return empty;
        }
    }

    /** The empty. */
    protected final LogAdapter empty = new LogAdapterImpl();
    /** The log adapter stream. */
    private LogAdapterStream logAdapterStream;

    /**
     * Sets the regular log output stream.
     * 
     * @param o
     *        the new regular log output stream
     */
    public void setRegularLogOutputStream(OutputStream o) {
        logAdapterStream = new LogAdapterStream(o);
    }

    /** The log absorption adapter stream. */
    private LogAdapterStream logAbsorptionAdapterStream;

    /**
     * Sets the absorption log output stream.
     * 
     * @param o
     *        the new absorption log output stream
     */
    public void setAbsorptionLogOutputStream(OutputStream o) {
        logAbsorptionAdapterStream = new LogAdapterStream(o);
    }

    /** The Class LogAdapterStream. */
    static class LogAdapterStream implements LogAdapter, Serializable {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 11000L;
        /** The out. */
        private transient OutputStream out;

        /**
         * Read object.
         * 
         * @param in
         *        the in
         * @throws IOException
         *         Signals that an I/O exception has occurred.
         * @throws ClassNotFoundException
         *         the class not found exception
         */
        private void readObject(ObjectInputStream in) throws IOException,
                ClassNotFoundException {
            in.defaultReadObject();
            out = System.out;
        }

        /**
         * Instantiates a new log adapter stream.
         * 
         * @param o
         *        the o
         */
        public LogAdapterStream(OutputStream o) {
            out = o;
        }

        @Override
        public LogAdapter printTemplate(Templates t, Object... strings) {
            this.print(String.format(t.getTemplate(), strings));
            return this;
        }

        @Override
        public LogAdapter print(int i) {
            this.print(Integer.toString(i));
            return this;
        }

        @Override
        public LogAdapter print(double i) {
            try {
                out.write(Double.toString(i).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }

        @Override
        public LogAdapter print(float i) {
            try {
                out.write(Float.toString(i).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }

        @Override
        public LogAdapter print(boolean i) {
            try {
                out.write(Boolean.toString(i).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }

        @Override
        public LogAdapter print(byte i) {
            this.print(Byte.toString(i));
            return this;
        }

        @Override
        public LogAdapter print(char i) {
            this.print(Character.toString(i));
            return this;
        }

        @Override
        public LogAdapter print(short i) {
            this.print(Short.toString(i));
            return this;
        }

        @Override
        public LogAdapter print(String i) {
            try {
                out.write(i.getBytes());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }

        @Override
        public LogAdapter println() {
            this.print('\n');
            return this;
        }

        @Override
        public LogAdapter print(Object s) {
            this.print(s == null ? "null" : s.toString());
            return this;
        }

        @Override
        public LogAdapter print(Object... s) {
            for (Object o : s) {
                this.print(o == null ? "null" : o.toString());
            }
            return this;
        }

        @Override
        public LogAdapter print(Object s1, Object s2) {
            this.print(s1.toString());
            this.print(s2.toString());
            return this;
        }

        @Override
        public LogAdapter print(Object s1, Object s2, Object s3) {
            this.print(s1.toString());
            this.print(s2.toString());
            this.print(s3.toString());
            return this;
        }

        @Override
        public LogAdapter print(Object s1, Object s2, Object s3, Object s4) {
            this.print(s1.toString());
            this.print(s2.toString());
            this.print(s3.toString());
            this.print(s4.toString());
            return this;
        }

        @Override
        public LogAdapter print(Object s1, Object s2, Object s3, Object s4,
                Object s5) {
            this.print(s1.toString());
            this.print(s2.toString());
            this.print(s3.toString());
            this.print(s4.toString());
            this.print(s5.toString());
            return this;
        }
    }

    /** The Class LogAdapterImpl. */
    static class LogAdapterImpl implements LogAdapter, Serializable {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 11000L;

        @Override
        public LogAdapter printTemplate(Templates t, Object... strings) {
            return this;
        }

        @Override
        public LogAdapter print(int i) {
            return this;
        }

        @Override
        public LogAdapter print(double d) {
            return this;
        }

        @Override
        public LogAdapter print(float f) {
            return this;
        }

        @Override
        public LogAdapter print(boolean b) {
            return this;
        }

        @Override
        public LogAdapter print(byte b) {
            return this;
        }

        @Override
        public LogAdapter print(char c) {
            return this;
        }

        @Override
        public LogAdapter print(short s) {
            return this;
        }

        @Override
        public LogAdapter print(String s) {
            return this;
        }

        @Override
        public LogAdapter println() {
            return this;
        }

        @Override
        public LogAdapter print(Object s) {
            return this;
        }

        @Override
        public LogAdapter print(Object... s) {
            return this;
        }

        @Override
        public LogAdapter print(Object s1, Object s2) {
            return this;
        }

        @Override
        public LogAdapter print(Object s1, Object s2, Object s3) {
            return this;
        }

        @Override
        public LogAdapter print(Object s1, Object s2, Object s3, Object s4) {
            return this;
        }

        @Override
        public LogAdapter print(Object s1, Object s2, Object s3, Object s4,
                Object s5) {
            return this;
        }
    }

    /**
     * Checks if is use el reasoner.
     * 
     * @return true, if is use el reasoner
     */
    public boolean isUseELReasoner() {
        return useELReasoner;
    }

    /**
     * Sets the use el reasoner.
     * 
     * @param useELReasoner
     *        the new use el reasoner
     */
    public void setUseELReasoner(boolean useELReasoner) {
        this.useELReasoner = useELReasoner;
    }

    /**
     * Checks if is use undefined names.
     * 
     * @return true, if is use undefined names
     */
    public boolean isUseUndefinedNames() {
        return useUndefinedNames;
    }

    /**
     * Sets the use undefined names.
     * 
     * @param useUndefinedNames
     *        the new use undefined names
     */
    public void setUseUndefinedNames(boolean useUndefinedNames) {
        this.useUndefinedNames = useUndefinedNames;
    }

    /**
     * Checks if is use axiom splitting.
     * 
     * @return true, if is use axiom splitting
     */
    public boolean isUseAxiomSplitting() {
        return useAxiomSplitting;
    }

    /**
     * Sets the use axiom splitting.
     * 
     * @param useAxiomSplitting
     *        the new use axiom splitting
     */
    public void setUseAxiomSplitting(boolean useAxiomSplitting) {
        this.useAxiomSplitting = useAxiomSplitting;
    }

    /** The use ad in module extraction. */
    private boolean useADInModuleExtraction = true;

    /**
     * Checks if is rkg use ad in module extraction.
     * 
     * @return true, if is rkg use ad in module extraction
     */
    public boolean isRKG_USE_AD_IN_MODULE_EXTRACTION() {
        return useADInModuleExtraction;
    }

    /**
     * Sets the rkg use ad in module extraction.
     * 
     * @param value
     *        the new rkg use ad in module extraction
     */
    public void setRKG_USE_AD_IN_MODULE_EXTRACTION(boolean value) {
        useADInModuleExtraction = value;
    }

    /**
     * Checks if is use incremental reasoning.
     * 
     * @return true, if is use incremental reasoning
     */
    public boolean isUseIncrementalReasoning() {
        return useIncrementalReasoning;
    }

    /**
     * Sets the use incremental reasoning.
     * 
     * @param useIncrementalReasoning
     *        the new use incremental reasoning
     */
    public void setUseIncrementalReasoning(boolean useIncrementalReasoning) {
        this.useIncrementalReasoning = useIncrementalReasoning;
    }
}
