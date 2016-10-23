package uk.ac.manchester.cs.jfact.kernel.options;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.NullReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;

/** configuration. */
public class JFactReasonerConfiguration implements OWLReasonerConfiguration, Serializable {

    private static final String OR_SORT_SAT = "orSortSat";
    private static final String OR_SORT_SUB = "orSortSub";
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
     * expression replacement, (R)ole absorption, (S)plit, Simple (f)orall
     * expression replacement
     */
    private static StringOption absorptionFlags = getOption("absorptionFlags", "BTEfCFSR");
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
    private static StringOption orSortSub = getOption(OR_SORT_SUB, "0");
    /**
     * Option 'orSortSat' define the sorting order of OR vertices in the DAG
     * used in satisfiability tests (used mostly in caching). Option has form of
     * string 'Mop', see orSortSub for details.
     */
    private static StringOption orSortSat = getOption(OR_SORT_SAT, "0");
    /**
     * Option 'IAOEFLG' define the priorities of different operations in to do
     * list. Possible values are 7-digit strings with ony possible digit are
     * 0-6. The digits on the places 1, 2, ..., 7 are for priority of Id, And,
     * Or, Exists, Forall, LE and GE operations respectively. The smaller number
     * means the higher priority. All other constructions (TOP, BOTTOM, etc) has
     * priority 0.
     */
    private static final StringOption IAOEFLG = getOption("IAOEFLG", "1263005");
    /**
     * Option 'useSemanticBranching' switch semantic branching on and off. The
     * usage of semantic branching usually leads to faster reasoning, but
     * sometime could give small overhead.
     */
    @PortedFrom(file = "dlTBox.h", name = "useSemanticBranching") private boolean useSemanticBranching = true;
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
    @PortedFrom(file = "dlTBox.h", name = "useLazyBlocking") private boolean useLazyBlocking = true;
    /**
     * Option 'useAnywhereBlocking' allow user to choose between Anywhere and
     * Ancestor blocking.
     */
    @PortedFrom(file = "dlTBox.h", name = "useAnywhereBlocking") private boolean useAnywhereBlocking = true;
    /**
     * Option 'useCompletelyDefined' leads to simpler Taxonomy creation if TBox
     * contains no non-primitive concepts. Unfortunately, it is quite rare case.
     */
    private boolean useCompletelyDefined = true;
    /**
     * Option 'useSpecialDomains' (development) controls the special processing
     * of R and D for non-simple roles. Should always be set to true.
     */
    @PortedFrom(file = "dlTBox.h", name = "useSpecialDomains") private boolean useSpecialDomains = true;
    /**
     * Option 'useIncrementalReasoning' (development) allows one to reason
     * efficiently about small changes in the ontology.
     */
    private boolean useIncrementalReasoning = false;
    /** The use axiom splitting. */
    @PortedFrom(file = "Kernel.h", name = "useAxiomSplitting") private boolean useAxiomSplitting = false;
    /** flag to use caching during completion tree construction */
    @PortedFrom(file = "dlTBox.h", name = "useNodeCache") private boolean useNodeCache = true;
    /** whether we use sorted reasoning; depends on some simplifications. */
    @PortedFrom(file = "dlTBox.h", name = "useSortedReasoning") private boolean useSortedReasoning = true;
    /** Option 'allowUndefinedNames' describes the policy of undefined names. */
    @PortedFrom(file = "Kernel.h", name = "allowUndefinedNames") private boolean allowUndefinedNames = true;
    private boolean needLogging = false;
    /**
     * Option 'queryAnswering', if true, switches the reasoner to a query
     * answering mode.
     */
    @PortedFrom(file = "Kernel.h", name = "queryAnswering") private boolean queryAnswering = false;
    protected static final LogAdapter empty = new LogAdapterImpl();
    /** The log adapter stream. */
    private LogAdapterStream logAdapterStream;
    /** set of all avaliable (given) options. */
    private final Map<String, StringOption> base = new HashMap<>();
    /** The progress monitor. */
    private ReasonerProgressMonitor progressMonitor = new NullReasonerProgressMonitor();
    /** The fresh entity policy. */
    @Nonnull private FreshEntityPolicy freshEntityPolicy = FreshEntityPolicy.ALLOW;
    /** The individual node set policy. */
    @Nonnull private IndividualNodeSetPolicy individualNodeSetPolicy = IndividualNodeSetPolicy.BY_NAME;
    /** The time out. */
    private long timeOut = Long.MAX_VALUE;
    /** The log absorption adapter stream. */
    private LogAdapterStream logAbsorptionAdapterStream;
    /** The use ad in module extraction. */
    private boolean useADInModuleExtraction = true;
    /** The use logging. */
    private boolean useLogging = false;
    /** The rkg debug absorption. */
    private boolean debugAbsorption = false;
    /** The rkg improve save restore depset. */
    private boolean improveSaveRestoreDepset = false;
    /** The rkg print dag usage. */
    private boolean printDagUsage = false;
    /** The rkg use simple rules. */
    private boolean useSimpleRules = false;
    /** The use reasoning statistics. */
    private boolean useReasoningStatistics = false;
    /** The rkg update rnd from superroles. */
    private boolean updaterndFromSuperRoles = false;
    /** The use blocking statistics. */
    private boolean useBlockingStatistics = false;
    /** The rkg use dynamic backjumping. */
    private boolean useDynamicBackjumping = false;
    /** The tmp print taxonomy info. */
    private boolean printTaxonomyInfo = false;
    /** The debug save restore. */
    private boolean debugSaveRestore = false;
    /** The rkg use fairness. */
    private boolean useFairness = false;
    /** The fpp debug split modules. */
    private boolean debugSplitModules = false;
    /** The splits. */
    private boolean splits = false;
    /** whether EL polynomial reasoner should be used. */
    private boolean useELReasoner = false;
    /** allow reasoner to use undefined names in queries. */
    private boolean useUndefinedNames = true;
    /** how many nodes skip before block; work only with FAIRNESS */
    private int nSkipBeforeBlock=0;

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
        debugSaveRestore = source.debugSaveRestore;
        dumpQuery = source.dumpQuery;
        debugSplitModules = source.debugSplitModules;
        freshEntityPolicy = source.freshEntityPolicy;
        individualNodeSetPolicy = source.individualNodeSetPolicy;
        debugAbsorption = source.debugAbsorption;
        improveSaveRestoreDepset = source.improveSaveRestoreDepset;
        printDagUsage = source.printDagUsage;
        updaterndFromSuperRoles = source.updaterndFromSuperRoles;
        useDynamicBackjumping = source.useDynamicBackjumping;
        useFairness = source.useFairness;
        nSkipBeforeBlock=source.nSkipBeforeBlock;
        useSimpleRules = source.useSimpleRules;
        splits = source.splits;
        timeOut = source.timeOut;
        printTaxonomyInfo = source.printTaxonomyInfo;
        useBlockingStatistics = source.useBlockingStatistics;
        useLogging = source.useLogging;
        useReasoningStatistics = source.useReasoningStatistics;
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

    /**
     * @param b
     *        new value
     * @return modified object
     */
    public JFactReasonerConfiguration setNeedLogging(boolean b) {
        needLogging = b;
        return this;
    }

    /**
     * check if it is necessary to log taxonomy action
     * 
     * @return true if necessary to log
     */
    public boolean isNeedLogging() {
        return needLogging;
    }

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
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseSortedReasoning(boolean useSortedReasoning) {
        this.useSortedReasoning = useSortedReasoning;
        return this;
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
     * @return modified instance
     */
    public JFactReasonerConfiguration setAllowUndefinedNames(boolean b) {
        allowUndefinedNames = b;
        return this;
    }

    /**
     * @return is allow undefined entities
     */
    public boolean isAllowUndefinedNames() {
        return allowUndefinedNames;
    }

    /**
     * @param b
     *        value for query answering
     * @return modified instance
     */
    public JFactReasonerConfiguration setQueryAnswering(boolean b) {
        queryAnswering = b;
        return this;
    }

    /**
     * @return is query answering
     */
    public boolean isQueryAnswering() {
        return queryAnswering;
    }

    /**
     * Sets the use special domains.
     * 
     * @param b
     *        the new use special domains
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseSpecialDomains(boolean b) {
        useSpecialDomains = b;
        return this;
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
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseLazyBlocking(boolean b) {
        useLazyBlocking = b;
        return this;
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
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseAnywhereBlocking(boolean b) {
        useAnywhereBlocking = b;
        return this;
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
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseSemanticBranching(boolean b) {
        useSemanticBranching = b;
        return this;
    }

    /**
     * set flag to use node cache to value VAL.
     * 
     * @param val
     *        the new use node cache
     * @return modified instance
     */
    @PortedFrom(file = "dlTBox.h", name = "setUseNodeCache")
    public JFactReasonerConfiguration setUseNodeCache(boolean val) {
        useNodeCache = val;
        return this;
    }

    /**
     * Checks if is use node cache.
     * 
     * @return true, if is use node cache
     */
    public boolean isUseNodeCache() {
        return useNodeCache;
    }

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
     * @return modified instance
     */
    private JFactReasonerConfiguration registerOption(StringOption defVal) {
        base.put(defVal.getOptionName(), defVal);
        return this;
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
        return this.get(OR_SORT_SAT);
    }

    /**
     * Sets the or sort sat.
     * 
     * @param defSat
     *        the new or sort sat
     * @return modified instance
     */
    public JFactReasonerConfiguration setorSortSat(String defSat) {
        registerOption(getOption(OR_SORT_SAT, defSat));
        return this;
    }

    /**
     * Gets the oR sort sub.
     * 
     * @return the oR sort sub
     */
    public String getORSortSub() {
        return this.get(OR_SORT_SUB);
    }

    /**
     * Sets the or sort sub.
     * 
     * @param defSat
     *        the new or sort sub
     * @return modified instance
     */
    public JFactReasonerConfiguration setorSortSub(String defSat) {
        registerOption(getOption(OR_SORT_SUB, defSat));
        return this;
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
     * @return modified instance
     */
    public JFactReasonerConfiguration setdumpQuery(boolean value) {
        dumpQuery = value;
        return this;
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
        if (progressMonitor == null) {
            progressMonitor = new ReasonerProgressMonitor() {};
        }
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
     * @return modified instance
     */
    public JFactReasonerConfiguration setverboseOutput(boolean b) {
        verboseOutput = b;
        return this;
    }

    /** The Class StringOption. */
    static class StringOption implements Serializable {

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

    /**
     * Checks if is logging active.
     * 
     * @return true, if is logging active
     */
    public boolean isLoggingActive() {
        return useLogging;
    }

    /**
     * Sets the logging active.
     * 
     * @param b
     *        the new logging active
     * @return modified instance
     */
    public JFactReasonerConfiguration setLoggingActive(boolean b) {
        useLogging = b;
        return this;
    }

    /**
     * Checks if is absorption logging active.
     * 
     * @return true, if is absorption logging active
     */
    public boolean isAbsorptionLoggingActive() {
        return debugAbsorption;
    }

    /**
     * Sets the absorption logging active.
     * 
     * @param b
     *        the new absorption logging active
     * @return modified instance
     */
    public JFactReasonerConfiguration setAbsorptionLoggingActive(boolean b) {
        debugAbsorption = b;
        return this;
    }

    /**
     * Checks if is rkg improve save restore depset.
     * 
     * @return true, if is rkg improve save restore depset
     */
    public boolean isImproveSaveRestoreDepset() {
        return improveSaveRestoreDepset;
    }

    /**
     * Sets the rkg improve save restore depset.
     * 
     * @param b
     *        the new rkg improve save restore depset
     * @return modified instance
     */
    public JFactReasonerConfiguration setImproveSaveRestoreDepset(boolean b) {
        improveSaveRestoreDepset = b;
        return this;
    }

    /**
     * Checks if is rkg print dag usage.
     * 
     * @return true, if is rkg print dag usage
     */
    public boolean isPrintDagUsage() {
        return printDagUsage;
    }

    /**
     * Sets the rkg print dag usage.
     * 
     * @param b
     *        the new rkg print dag usage
     * @return modified instance
     */
    public JFactReasonerConfiguration setPrintDagUsage(boolean b) {
        printDagUsage = b;
        return this;
    }

    /**
     * Checks if is rkg use simple rules.
     * 
     * @return true, if is rkg use simple rules
     */
    public boolean isUseSimpleRules() {
        return useSimpleRules;
    }

    /**
     * Sets the rkg use simple rules.
     * 
     * @param b
     *        the new rkg use simple rules
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseSimpleRules(boolean b) {
        useSimpleRules = b;
        return this;
    }

    /**
     * Checks if is use reasoning statistics.
     * 
     * @return true, if is use reasoning statistics
     */
    public boolean isUseReasoningStatistics() {
        return useReasoningStatistics;
    }

    /**
     * Sets the use reasoning statistics.
     * 
     * @param b
     *        the new use reasoning statistics
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseReasoningStatistics(boolean b) {
        useReasoningStatistics = b;
        return this;
    }

    /**
     * Checks if is rkg update rnd from superroles.
     * 
     * @return true, if is rkg update rnd from superroles
     */
    public boolean isUpdaterndFromSuperRoles() {
        return updaterndFromSuperRoles;
    }

    /**
     * Sets the rkg update rnd from superroles.
     * 
     * @param b
     *        the new rkg update rnd from superroles
     * @return modified instance
     */
    public JFactReasonerConfiguration setUpdaterndFromSuperRoles(boolean b) {
        updaterndFromSuperRoles = b;
        return this;
    }

    /**
     * Checks if is use blocking statistics.
     * 
     * @return true, if is use blocking statistics
     */
    public boolean isUseBlockingStatistics() {
        return useBlockingStatistics;
    }

    /**
     * Sets the use blocking statistics.
     * 
     * @param b
     *        the new use blocking statistics
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseBlockingStatistics(boolean b) {
        useBlockingStatistics = b;
        return this;
    }

    /**
     * Checks if is rkg use dynamic backjumping.
     * 
     * @return true, if is rkg use dynamic backjumping
     */
    public boolean isUseDynamicBackjumping() {
        return useDynamicBackjumping;
    }

    /**
     * Sets the rkg use dynamic backjumping.
     * 
     * @param b
     *        the new rkg use dynamic backjumping
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseDynamicBackjumping(boolean b) {
        useDynamicBackjumping = b;
        return this;
    }

    /**
     * Checks if is tmp print taxonomy info.
     * 
     * @return true, if is tmp print taxonomy info
     */
    public boolean isPrintTaxonomyInfo() {
        return printTaxonomyInfo;
    }

    /**
     * Sets the tmp print taxonomy info.
     * 
     * @param b
     *        the new tmp print taxonomy info
     * @return modified instance
     */
    public JFactReasonerConfiguration setPrintTaxonomyInfo(boolean b) {
        printTaxonomyInfo = b;
        return this;
    }

    /**
     * Checks if is debug save restore.
     * 
     * @return true, if is debug save restore
     */
    public boolean isDebugSaveRestore() {
        return debugSaveRestore;
    }

    /**
     * Sets the debug save restore.
     * 
     * @param b
     *        the new debug save restore
     * @return modified instance
     */
    public JFactReasonerConfiguration setDebugSaveRestore(boolean b) {
        debugSaveRestore = b;
        return this;
    }

    /**
     * Checks if is rkg use fairness.
     * 
     * @return true, if is rkg use fairness
     */
    public boolean isUseFairness() {
        return useFairness;
    }

    /**
     * Sets the rkg use fairness.
     * 
     * @param b
     *        the new rkg use fairness
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseFairness(boolean b) {
        useFairness = b;
        return this;
    }

    /** @return number of nodes to skip */
    public int getnSkipBeforeBlock() {
        return nSkipBeforeBlock;
    }

    /**
     * @param nSkipBeforeBlock
     *        number of nodes to skip
     */
    public void setnSkipBeforeBlock(int nSkipBeforeBlock) {
        this.nSkipBeforeBlock = nSkipBeforeBlock;
    }

    /**
     * Checks if is fpp debug split modules.
     * 
     * @return true, if is fpp debug split modules
     */
    public boolean isDebugSplitModules() {
        return debugSplitModules;
    }

    /**
     * Sets the fpp debug split modules.
     * 
     * @param b
     *        the new fpp debug split modules
     * @return modified instance
     */
    public JFactReasonerConfiguration setDebugSplitModules(boolean b) {
        debugSplitModules = b;
        return this;
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
     * @return modified instance
     */
    public JFactReasonerConfiguration setSplits(boolean splits) {
        this.splits = splits;
        return this;
    }

    /**
     * Gets the log.
     * 
     * @return the log
     */
    public LogAdapter getLog() {
        if (useLogging) {
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
        if (debugAbsorption) {
            if (logAbsorptionAdapterStream == null) {
                logAbsorptionAdapterStream = new LogAdapterStream(System.out);
            }
            return logAbsorptionAdapterStream;
        } else {
            return empty;
        }
    }

    /**
     * @param out
     *        output stream to use
     * @return modified object
     */
    public JFactReasonerConfiguration setAbsorptionLog(OutputStream out) {
        logAbsorptionAdapterStream = new LogAdapterStream(out);
        return this;
    }

    /**
     * Sets the regular log output stream.
     * 
     * @param o
     *        the new regular log output stream
     * @return modified instance
     */
    public JFactReasonerConfiguration setRegularLogOutputStream(OutputStream o) {
        logAdapterStream = new LogAdapterStream(o);
        return this;
    }

    /**
     * Sets the absorption log output stream.
     * 
     * @param o
     *        the new absorption log output stream
     * @return modified instance
     */
    public JFactReasonerConfiguration setAbsorptionLogOutputStream(OutputStream o) {
        logAbsorptionAdapterStream = new LogAdapterStream(o);
        return this;
    }

    /** The Class LogAdapterStream. */
    @SuppressWarnings("null")
    static class LogAdapterStream implements LogAdapter, Serializable {

        /** The out. */
        private transient PrintStream out;

        /**
         * Instantiates a new log adapter stream.
         * 
         * @param o
         *        the o
         */
        public LogAdapterStream(OutputStream o) {
            out = new PrintStream(o);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

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
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            out = System.out;
        }

        @Override
        public LogAdapter printTemplate(Templates t, Object... strings) {
            this.print(String.format(t.getTemplate(), strings));
            return this;
        }

        @Override
        public LogAdapter printTemplateInt(Templates t, int... strings) {
            List<Object> l = new ArrayList<>(strings.length + 1);
            for (int i : strings) {
                l.add(Integer.valueOf(i));
            }
            this.print(String.format(t.getTemplate(), l.toArray()));
            return this;
        }

        @Override
        public LogAdapter printTemplateMixInt(Templates t, Object s, int... strings) {
            List<Object> l = new ArrayList<>(strings.length + 1);
            l.add(s);
            for (int i : strings) {
                l.add(Integer.valueOf(i));
            }
            this.print(String.format(t.getTemplate(), l.toArray()));
            return this;
        }

        @Override
        public LogAdapter print(int i) {
            out.print(i);
            return this;
        }

        @Override
        public LogAdapter print(double i) {
            out.print(i);
            return this;
        }

        @Override
        public LogAdapter print(float i) {
            out.print(i);
            return this;
        }

        @Override
        public LogAdapter print(boolean i) {
            out.print(i);
            return this;
        }

        @Override
        public LogAdapter print(byte i) {
            this.print(i);
            return this;
        }

        @Override
        public LogAdapter print(char i) {
            this.print(i);
            return this;
        }

        @Override
        public LogAdapter print(short i) {
            this.print(i);
            return this;
        }

        @Override
        public LogAdapter print(String i) {
            out.print(i);
            out.flush();
            return this;
        }

        @Override
        public LogAdapter println() {
            this.print('\n');
            return this;
        }

        @Override
        public LogAdapter print(@Nullable Object s) {
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
        public LogAdapter print(Object s1, Object s2, Object s3, Object s4, Object s5) {
            this.print(s1.toString());
            this.print(s2.toString());
            this.print(s3.toString());
            this.print(s4.toString());
            this.print(s5.toString());
            return this;
        }
    }

    /** The Class LogAdapterImpl. */
    static class LogAdapterImpl implements LogAdapter, Serializable {}

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
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseELReasoner(boolean useELReasoner) {
        this.useELReasoner = useELReasoner;
        return this;
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
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseUndefinedNames(boolean useUndefinedNames) {
        this.useUndefinedNames = useUndefinedNames;
        return this;
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
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseAxiomSplitting(boolean useAxiomSplitting) {
        this.useAxiomSplitting = useAxiomSplitting;
        return this;
    }

    /**
     * Checks if is rkg use ad in module extraction.
     * 
     * @return true, if is rkg use ad in module extraction
     */
    public boolean isUseADInModuleExtraction() {
        return useADInModuleExtraction;
    }

    /**
     * Sets the rkg use ad in module extraction.
     * 
     * @param value
     *        the new rkg use ad in module extraction
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseADInModuleExtraction(boolean value) {
        useADInModuleExtraction = value;
        return this;
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
     * @return modified instance
     */
    public JFactReasonerConfiguration setUseIncrementalReasoning(boolean useIncrementalReasoning) {
        this.useIncrementalReasoning = useIncrementalReasoning;
        return this;
    }
}
