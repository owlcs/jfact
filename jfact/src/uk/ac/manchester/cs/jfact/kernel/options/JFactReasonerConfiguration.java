package uk.ac.manchester.cs.jfact.kernel.options;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.NullReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;

public class JFactReasonerConfiguration implements OWLReasonerConfiguration {
    /**
     * Option 'useRelevantOnly' is used when creating internal DAG
     * representation for externally given TBox. If true, DAG contains only
     * concepts, relevant to query. It is safe to leave this option false.
     */
    private static final Option useRelevantOnly = getOption("useRelevantOnly", false);
    /**
     * Option 'dumpQuery' dumps sub-TBox relevant to given
     * satisfiability/subsumption query.
     */
    private static final Option dumpQuery = getOption("dumpQuery", false);
    /**
     * Option 'absorptionFlags' sets up absorption process for general axioms.
     * It text field of arbitrary length; every symbol means the absorption
     * action: (B)ottom Absorption), (T)op absorption, (E)quivalent concepts
     * replacement, (C)oncept absorption, (N)egated concept absorption, (F)orall
     * expression replacement, (R)ole absorption, (S)plit
     */
    private static final Option absorptionFlags = getOption("absorptionFlags", "BTECFSR");
    /**
     * Option 'alwaysPreferEquals' allows user to enforce usage of C=D
     * definition instead of C[=D during absorption, even if implication
     * appeares earlier in stream of axioms.
     */
    private static final Option alwaysPreferEquals = getOption("alwaysPreferEquals", true);
    /**
     * Option 'orSortSub' define the sorting order of OR vertices in the DAG
     * used in subsumption tests. Option has form of string 'Mop', where 'M' is
     * a sort field (could be 'D' for depth, 'S' for size, 'F' for frequency,
     * and '0' for no sorting), 'o' is a order field (could be 'a' for ascending
     * and 'd' for descending mode), and 'p' is a preference field (could be 'p'
     * for preferencing non-generating rules and 'n' for not doing so).
     */
    private static final Option orSortSub = getOption("orSortSub", "0");
    /**
     * Option 'orSortSat' define the sorting order of OR vertices in the DAG
     * used in satisfiability tests (used mostly in caching). Option has form of
     * string 'Mop', see orSortSub for details.
     */
    private static final Option orSortSat = getOption("orSortSat", "0");
    /**
     * Option 'IAOEFLG' define the priorities of different operations in TODO
     * list. Possible values are 7-digit strings with ony possible digit are
     * 0-6. The digits on the places 1, 2, ..., 7 are for priority of Id, And,
     * Or, Exists, Forall, LE and GE operations respectively. The smaller number
     * means the higher priority. All other constructions (TOP, BOTTOM, etc) has
     * priority 0.
     */
    private static final Option IAOEFLG = getOption("IAOEFLG", "1263005");
    /**
     * Option 'useSemanticBranching' switch semantic branching on and off. The
     * usage of semantic branching usually leads to faster reasoning, but
     * sometime could give small overhead.
     */
    private static final Option useSemanticBranching = getOption("useSemanticBranching",
            true);
    /**
     * Option 'useBackjumping' switch backjumping on and off. The usage of
     * backjumping usually leads to much faster reasoning.
     */
    private static final Option useBackjumping = getOption("useBackjumping", true);
    /** tell reasoner to use verbose output */
    private static final Option verboseOutput = getOption("verboseOutput", false);
    /**
     * Option 'useLazyBlocking' makes checking of blocking status as small as
     * possible. This greatly increase speed of reasoning.
     */
    private static final Option useLazyBlocking = getOption("useLazyBlocking", true);
    /**
     * Option 'useAnywhereBlocking' allow user to choose between Anywhere and
     * Ancestor blocking.
     */
    private static final Option useAnywhereBlocking = getOption("useAnywhereBlocking",
            true);
    /**
     * Option 'useCompletelyDefined' leads to simpler Taxonomy creation if TBox
     * contains no non-primitive concepts. Unfortunately, it is quite rare case.
     */
    private static final Option useCompletelyDefined = getOption("useCompletelyDefined",
            true);
    /**
     * Option 'useSpecialDomains' (development) controls the special processing
     * of R&D for non-simple roles. Should always be set to true.
     */
    private static final Option useSpecialDomains = getOption("useSpecialDomains", true);
    /**
     * Internal use only. Option 'skipBeforeBlock' allow user to skip given
     * number of nodes before make a block.
     */
    private static final Option skipBeforeBlock = getOption("skipBeforeBlock", 0);
    private static final List<Option> defaults = new ArrayList<Option>(Arrays.asList(
            absorptionFlags, alwaysPreferEquals, dumpQuery, IAOEFLG, orSortSat,
            orSortSub, verboseOutput, useAnywhereBlocking, useBackjumping,
            useCompletelyDefined, useLazyBlocking, useRelevantOnly, useSemanticBranching,
            useSpecialDomains, skipBeforeBlock));
    /** set of all avaliable (given) options */
    private final Map<String, Option> base = new HashMap<String, Option>();

    public static Option getOption(final String name, final boolean b) {
        return new BooleanOption(name, b);
    }

    public static Option getOption(final String name, final long l) {
        return new LongOption(name, l);
    }

    public static Option getOption(final String name, final String s) {
        return new StringOption(name, s);
    }

    private void registerOption(final Option defVal) {
        base.put(defVal.getOptionName(), defVal);
    }

    public <O> O get(final String name) {
        return (O) base.get(name).getValue();
    }

    public String getORSortSat() {
        return this.get("orSortSat");
    }

    public void setorSortSat(final String defSat) {
        registerOption(getOption("orSortSat", defSat));
    }

    public String getORSortSub() {
        return this.get("orSortSub");
    }

    public void setorSortSub(final String defSat) {
        registerOption(getOption("orSortSub", defSat));
    }

    public boolean getuseAnywhereBlocking() {
        final Boolean boolean1 = this.get("useAnywhereBlocking");
        return boolean1;
    }

    public boolean getuseBackjumping() {
        final Boolean boolean1 = this.get("useBackjumping");
        return boolean1;
    }

    public boolean getuseLazyBlocking() {
        final Boolean boolean1 = this.get("useLazyBlocking");
        return boolean1;
    }

    public boolean getuseSemanticBranching() {
        final Boolean boolean1 = this.get("useSemanticBranching");
        return boolean1;
    }

    public boolean getverboseOutput() {
        final Boolean boolean1 = this.get("verboseOutput");
        return boolean1;
    }

    public boolean getdumpQuery() {
        final Boolean boolean1 = this.get("dumpQuery");
        return boolean1;
    }

    public boolean getuseCompletelyDefined() {
        final Boolean boolean1 = this.get("useCompletelyDefined");
        return boolean1;
    }

    public boolean getuseRelevantOnly() {
        final Boolean boolean1 = this.get("useRelevantOnly");
        return boolean1;
    }

    public boolean getalwaysPreferEquals() {
        final Boolean boolean1 = this.get("alwaysPreferEquals");
        return boolean1;
    }

    public String getabsorptionFlags() {
        return this.get("absorptionFlags");
    }

    public String getIAOEFLG() {
        return this.get("IAOEFLG");
    }

    public void setuseAnywhereBlocking(final boolean b) {
        registerOption(getOption("useAnywhereBlocking", b));
    }

    private ReasonerProgressMonitor progressMonitor = new NullReasonerProgressMonitor();
    private FreshEntityPolicy freshEntityPolicy = FreshEntityPolicy.ALLOW;
    private IndividualNodeSetPolicy individualNodeSetPolicy = IndividualNodeSetPolicy.BY_NAME;
    private long timeOut = Long.MAX_VALUE;

    public JFactReasonerConfiguration() {
        for (Option o : defaults) {
            base.put(o.getOptionName(), o);
        }
    }

    public JFactReasonerConfiguration(final OWLReasonerConfiguration source) {
        this();
        progressMonitor = source.getProgressMonitor();
        freshEntityPolicy = source.getFreshEntityPolicy();
        individualNodeSetPolicy = source.getIndividualNodeSetPolicy();
        timeOut = source.getTimeOut();
    }

    public FreshEntityPolicy getFreshEntityPolicy() {
        return freshEntityPolicy;
    }

    public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return individualNodeSetPolicy;
    }

    public ReasonerProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setverboseOutput(final boolean b) {
        registerOption(getOption("verboseOutput", b));
    }

    boolean USE_LOGGING = false;
    boolean RKG_DEBUG_ABSORPTION = false;
    boolean RKG_IMPROVE_SAVE_RESTORE_DEPSET = false;
    boolean RKG_PRINT_DAG_USAGE = false;
    boolean RKG_USE_SIMPLE_RULES = false;
    boolean RKG_USE_SORTED_REASONING = false;
    boolean USE_REASONING_STATISTICS = false;
    boolean RKG_UPDATE_RND_FROM_SUPERROLES = false;
    boolean USE_BLOCKING_STATISTICS = false;
    boolean RKG_USE_DYNAMIC_BACKJUMPING = false;
    boolean TMP_PRINT_TAXONOMY_INFO = false;
    boolean DEBUG_SAVE_RESTORE = false;
    boolean RKG_USE_FAIRNESS = false;
    boolean FPP_DEBUG_SPLIT_MODULES = false;
    boolean splits = false;
    /// whether EL polynomial reasoner should be used
    boolean useELReasoner = false;
    /// allow reasoner to use undefined names in queries
    boolean useUndefinedNames = true;
    boolean useAxiomSplitting = false;

    public boolean isLoggingActive() {
        return USE_LOGGING;
    }

    public void setLoggingActive(final boolean b) {
        USE_LOGGING = b;
    }

    public boolean isAbsorptionLoggingActive() {
        return RKG_DEBUG_ABSORPTION;
    }

    public void setAbsorptionLoggingActive(final boolean b) {
        RKG_DEBUG_ABSORPTION = b;
    }

    public boolean isRKG_IMPROVE_SAVE_RESTORE_DEPSET() {
        return RKG_IMPROVE_SAVE_RESTORE_DEPSET;
    }

    public void setRKG_IMPROVE_SAVE_RESTORE_DEPSET(final boolean b) {
        RKG_IMPROVE_SAVE_RESTORE_DEPSET = b;
    }

    public boolean isRKG_PRINT_DAG_USAGE() {
        return RKG_PRINT_DAG_USAGE;
    }

    public void setRKG_PRINT_DAG_USAGE(final boolean b) {
        RKG_PRINT_DAG_USAGE = b;
    }

    public boolean isRKG_USE_SIMPLE_RULES() {
        return RKG_USE_SIMPLE_RULES;
    }

    public void setRKG_USE_SIMPLE_RULES(final boolean b) {
        RKG_USE_SIMPLE_RULES = b;
    }

    public boolean isRKG_USE_SORTED_REASONING() {
        return RKG_USE_SORTED_REASONING;
    }

    public void setRKG_USE_SORTED_REASONING(final boolean b) {
        RKG_USE_SORTED_REASONING = b;
    }

    public boolean isUSE_REASONING_STATISTICS() {
        return USE_REASONING_STATISTICS;
    }

    public void setUSE_REASONING_STATISTICS(final boolean b) {
        USE_REASONING_STATISTICS = b;
    }

    public boolean isRKG_UPDATE_RND_FROM_SUPERROLES() {
        return RKG_UPDATE_RND_FROM_SUPERROLES;
    }

    public void setRKG_UPDATE_RND_FROM_SUPERROLES(final boolean b) {
        RKG_UPDATE_RND_FROM_SUPERROLES = b;
    }

    public boolean isUSE_BLOCKING_STATISTICS() {
        return USE_BLOCKING_STATISTICS;
    }

    public void setUSE_BLOCKING_STATISTICS(final boolean b) {
        USE_BLOCKING_STATISTICS = b;
    }

    public boolean isRKG_USE_DYNAMIC_BACKJUMPING() {
        return RKG_USE_DYNAMIC_BACKJUMPING;
    }

    public void setRKG_USE_DYNAMIC_BACKJUMPING(final boolean b) {
        RKG_USE_DYNAMIC_BACKJUMPING = b;
    }

    public boolean isTMP_PRINT_TAXONOMY_INFO() {
        return TMP_PRINT_TAXONOMY_INFO;
    }

    public void setTMP_PRINT_TAXONOMY_INFO(final boolean b) {
        TMP_PRINT_TAXONOMY_INFO = b;
    }

    public boolean isDEBUG_SAVE_RESTORE() {
        return DEBUG_SAVE_RESTORE;
    }

    public void setDEBUG_SAVE_RESTORE(final boolean b) {
        DEBUG_SAVE_RESTORE = b;
    }

    public boolean isRKG_USE_FAIRNESS() {
        return RKG_USE_FAIRNESS;
    }

    public void setRKG_USE_FAIRNESS(final boolean b) {
        RKG_USE_FAIRNESS = b;
    }

    public boolean isFPP_DEBUG_SPLIT_MODULES() {
        return FPP_DEBUG_SPLIT_MODULES;
    }

    public void setFPP_DEBUG_SPLIT_MODULES(final boolean b) {
        FPP_DEBUG_SPLIT_MODULES = b;
    }

    public boolean isSplits() {
        return splits;
    }

    public void setSplits(final boolean splits) {
        this.splits = splits;
    }

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

    LogAdapter empty = new LogAdapterImpl();
    private LogAdapterStream logAdapterStream;

    public void setRegularLogOutputStream(final OutputStream o) {
        logAdapterStream = new LogAdapterStream(o);
    }

    private LogAdapterStream logAbsorptionAdapterStream;

    public void setAbsorptionLogOutputStream(final OutputStream o) {
        logAbsorptionAdapterStream = new LogAdapterStream(o);
    }

    static class LogAdapterStream implements LogAdapter {
        private final OutputStream out;

        public LogAdapterStream(final OutputStream o) {
            out = o;
        }

        public void printTemplate(final Templates t, final Object... strings) {
            this.print(String.format(t.getTemplate(), strings));
        }

        public void print(final int i) {
            this.print(Integer.toString(i));
        }

        public void print(final double i) {
            try {
                out.write(Double.toString(i).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void print(final float i) {
            try {
                out.write(Float.toString(i).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void print(final boolean i) {
            try {
                out.write(Boolean.toString(i).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void print(final byte i) {
            this.print(Byte.toString(i));
        }

        public void print(final char i) {
            this.print(Character.toString(i));
        }

        public void print(final short i) {
            this.print(Short.toString(i));
        }

        public void print(final String i) {
            try {
                out.write(i.getBytes());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void println() {
            this.print('\n');
        }

        public void print(final Object s) {
            this.print(s == null ? "null" : s.toString());
        }

        public void print(final Object... s) {
            for (Object o : s) {
                this.print(o == null ? "null" : o.toString());
            }
        }

        public void print(final Object s1, final Object s2) {
            this.print(s1.toString());
            this.print(s2.toString());
        }

        public void print(final Object s1, final Object s2, final Object s3) {
            this.print(s1.toString());
            this.print(s2.toString());
            this.print(s3.toString());
        }

        public void print(final Object s1, final Object s2, final Object s3,
                final Object s4) {
            this.print(s1.toString());
            this.print(s2.toString());
            this.print(s3.toString());
            this.print(s4.toString());
        }

        public void print(final Object s1, final Object s2, final Object s3,
                final Object s4, final Object s5) {
            this.print(s1.toString());
            this.print(s2.toString());
            this.print(s3.toString());
            this.print(s4.toString());
            this.print(s5.toString());
        }
    }

    @SuppressWarnings("unused")
    static final class LogAdapterImpl implements LogAdapter {
        public void printTemplate(final Templates t, final Object... strings) {}

        public void print(final int i) {}

        public void print(final double d) {}

        public void print(final float f) {}

        public void print(final boolean b) {}

        public void print(final byte b) {}

        public void print(final char c) {}

        public void print(final short s) {}

        public void print(final String s) {}

        public void println() {}

        public void print(final Object s) {}

        public void print(final Object... s) {}

        public void print(final Object s1, final Object s2) {}

        public void print(final Object s1, final Object s2, final Object s3) {}

        public void print(final Object s1, final Object s2, final Object s3,
                final Object s4) {}

        public void print(final Object s1, final Object s2, final Object s3,
                final Object s4, final Object s5) {}
    }

    public boolean isUseELReasoner() {
        return useELReasoner;
    }

    public void setUseELReasoner(final boolean useELReasoner) {
        this.useELReasoner = useELReasoner;
    }

    public boolean isUseUndefinedNames() {
        return useUndefinedNames;
    }

    public void setUseUndefinedNames(final boolean useUndefinedNames) {
        this.useUndefinedNames = useUndefinedNames;
    }

    public boolean isUseAxiomSplitting() {
        return useAxiomSplitting;
    }

    public void setUseAxiomSplitting(final boolean useAxiomSplitting) {
        this.useAxiomSplitting = useAxiomSplitting;
    }
}
