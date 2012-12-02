package uk.ac.manchester.cs.jfact.kernel.options;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;

public class JFactReasonerConfiguration implements OWLReasonerConfiguration {
    /** Option 'useRelevantOnly' is used when creating internal DAG
     * representation for externally given TBox. If true, DAG contains only
     * concepts, relevant to query. It is safe to leave this option false. */
    private boolean useRelevantOnly = false;
    /** Option 'dumpQuery' dumps sub-TBox relevant to given
     * satisfiability/subsumption query. */
    private boolean dumpQuery = false;
    /** Option 'absorptionFlags' sets up absorption process for general axioms.
     * It text field of arbitrary length; every symbol means the absorption
     * action: (B)ottom Absorption), (T)op absorption, (E)quivalent concepts
     * replacement, (C)oncept absorption, (N)egated concept absorption, (F)orall
     * expression replacement, (R)ole absorption, (S)plit */
    private static final Option absorptionFlags = getOption("absorptionFlags", "BTECFSR");
    /** Option 'alwaysPreferEquals' allows user to enforce usage of C=D
     * definition instead of C[=D during absorption, even if implication
     * appeares earlier in stream of axioms. */
    private boolean alwaysPreferEquals = true;
    /** Option 'orSortSub' define the sorting order of OR vertices in the DAG
     * used in subsumption tests. Option has form of string 'Mop', where 'M' is
     * a sort field (could be 'D' for depth, 'S' for size, 'F' for frequency,
     * and '0' for no sorting), 'o' is a order field (could be 'a' for ascending
     * and 'd' for descending mode), and 'p' is a preference field (could be 'p'
     * for preferencing non-generating rules and 'n' for not doing so). */
    private static final Option orSortSub = getOption("orSortSub", "0");
    /** Option 'orSortSat' define the sorting order of OR vertices in the DAG
     * used in satisfiability tests (used mostly in caching). Option has form of
     * string 'Mop', see orSortSub for details. */
    private static final Option orSortSat = getOption("orSortSat", "0");
    /** Option 'IAOEFLG' define the priorities of different operations in TODO
     * list. Possible values are 7-digit strings with ony possible digit are
     * 0-6. The digits on the places 1, 2, ..., 7 are for priority of Id, And,
     * Or, Exists, Forall, LE and GE operations respectively. The smaller number
     * means the higher priority. All other constructions (TOP, BOTTOM, etc) has
     * priority 0. */
    private static final Option IAOEFLG = getOption("IAOEFLG", "1263005");
    /** Option 'useSemanticBranching' switch semantic branching on and off. The
     * usage of semantic branching usually leads to faster reasoning, but
     * sometime could give small overhead. */
    private boolean useSemanticBranching = true;
    /** Option 'useBackjumping' switch backjumping on and off. The usage of
     * backjumping usually leads to much faster reasoning. */
    private boolean useBackjumping = true;
    /** tell reasoner to use verbose output */
    private boolean verboseOutput = false;
    /** Option 'useLazyBlocking' makes checking of blocking status as small as
     * possible. This greatly increase speed of reasoning. */
    private boolean useLazyBlocking = true;
    /** Option 'useAnywhereBlocking' allow user to choose between Anywhere and
     * Ancestor blocking. */
    private boolean useAnywhereBlocking = true;
    /** Option 'useCompletelyDefined' leads to simpler Taxonomy creation if TBox
     * contains no non-primitive concepts. Unfortunately, it is quite rare case. */
    private boolean useCompletelyDefined = true;
    /** Option 'useSpecialDomains' (development) controls the special processing
     * of R&D for non-simple roles. Should always be set to true. */
    private boolean useSpecialDomains = true;
    /** Internal use only. Option 'skipBeforeBlock' allow user to skip given
     * number of nodes before make a block. */
    private static final Option skipBeforeBlock = getOption("skipBeforeBlock", 0);
    private static final List<Option> defaults = new ArrayList<Option>(Arrays.asList(
            absorptionFlags, IAOEFLG, orSortSat, orSortSub, skipBeforeBlock));
    /** set of all avaliable (given) options */
    private final Map<String, Option> base = new HashMap<String, Option>();

    public static Option getOption(String name, long l) {
        return new LongOption(name, l);
    }

    public static Option getOption(String name, String s) {
        return new StringOption(name, s);
    }

    private void registerOption(Option defVal) {
        base.put(defVal.getOptionName(), defVal);
    }

    public <O> O get(String name) {
        return (O) base.get(name).getValue();
    }

    public String getORSortSat() {
        return this.get("orSortSat");
    }

    public void setorSortSat(String defSat) {
        registerOption(getOption("orSortSat", defSat));
    }

    public String getORSortSub() {
        return this.get("orSortSub");
    }

    public void setorSortSub(String defSat) {
        registerOption(getOption("orSortSub", defSat));
    }

    public boolean getuseAnywhereBlocking() {
        return useAnywhereBlocking;
    }

    public boolean getuseBackjumping() {
        return useBackjumping;
    }

    public boolean getuseLazyBlocking() {
        return useLazyBlocking;
    }

    public boolean getuseSemanticBranching() {
        return useSemanticBranching;
    }

    public boolean getverboseOutput() {
        return verboseOutput;
    }

    public boolean getdumpQuery() {
        return dumpQuery;
    }

    public void setdumpQuery(boolean value) {
        dumpQuery = value;
    }

    public boolean getuseCompletelyDefined() {
        return useCompletelyDefined;
    }

    public boolean getuseRelevantOnly() {
        return useRelevantOnly;
    }

    public boolean getalwaysPreferEquals() {
        return alwaysPreferEquals;
    }

    public String getabsorptionFlags() {
        return this.get("absorptionFlags");
    }

    public String getIAOEFLG() {
        return this.get("IAOEFLG");
    }

    public void setuseAnywhereBlocking(boolean b) {
        useAnywhereBlocking = b;
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

    public JFactReasonerConfiguration(OWLReasonerConfiguration source) {
        this();
        progressMonitor = source.getProgressMonitor();
        freshEntityPolicy = source.getFreshEntityPolicy();
        individualNodeSetPolicy = source.getIndividualNodeSetPolicy();
        timeOut = source.getTimeOut();
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

    public void setverboseOutput(boolean b) {
        verboseOutput = b;
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
    /** whether EL polynomial reasoner should be used */
    boolean useELReasoner = false;
    /** allow reasoner to use undefined names in queries */
    boolean useUndefinedNames = true;
    boolean useAxiomSplitting = false;

    public boolean isLoggingActive() {
        return USE_LOGGING;
    }

    public void setLoggingActive(boolean b) {
        USE_LOGGING = b;
    }

    public boolean isAbsorptionLoggingActive() {
        return RKG_DEBUG_ABSORPTION;
    }

    public void setAbsorptionLoggingActive(boolean b) {
        RKG_DEBUG_ABSORPTION = b;
    }

    public boolean isRKG_IMPROVE_SAVE_RESTORE_DEPSET() {
        return RKG_IMPROVE_SAVE_RESTORE_DEPSET;
    }

    public void setRKG_IMPROVE_SAVE_RESTORE_DEPSET(boolean b) {
        RKG_IMPROVE_SAVE_RESTORE_DEPSET = b;
    }

    public boolean isRKG_PRINT_DAG_USAGE() {
        return RKG_PRINT_DAG_USAGE;
    }

    public void setRKG_PRINT_DAG_USAGE(boolean b) {
        RKG_PRINT_DAG_USAGE = b;
    }

    public boolean isRKG_USE_SIMPLE_RULES() {
        return RKG_USE_SIMPLE_RULES;
    }

    public void setRKG_USE_SIMPLE_RULES(boolean b) {
        RKG_USE_SIMPLE_RULES = b;
    }

    public boolean isRKG_USE_SORTED_REASONING() {
        return RKG_USE_SORTED_REASONING;
    }

    public void setRKG_USE_SORTED_REASONING(boolean b) {
        RKG_USE_SORTED_REASONING = b;
    }

    public boolean isUSE_REASONING_STATISTICS() {
        return USE_REASONING_STATISTICS;
    }

    public void setUSE_REASONING_STATISTICS(boolean b) {
        USE_REASONING_STATISTICS = b;
    }

    public boolean isRKG_UPDATE_RND_FROM_SUPERROLES() {
        return RKG_UPDATE_RND_FROM_SUPERROLES;
    }

    public void setRKG_UPDATE_RND_FROM_SUPERROLES(boolean b) {
        RKG_UPDATE_RND_FROM_SUPERROLES = b;
    }

    public boolean isUSE_BLOCKING_STATISTICS() {
        return USE_BLOCKING_STATISTICS;
    }

    public void setUSE_BLOCKING_STATISTICS(boolean b) {
        USE_BLOCKING_STATISTICS = b;
    }

    public boolean isRKG_USE_DYNAMIC_BACKJUMPING() {
        return RKG_USE_DYNAMIC_BACKJUMPING;
    }

    public void setRKG_USE_DYNAMIC_BACKJUMPING(boolean b) {
        RKG_USE_DYNAMIC_BACKJUMPING = b;
    }

    public boolean isTMP_PRINT_TAXONOMY_INFO() {
        return TMP_PRINT_TAXONOMY_INFO;
    }

    public void setTMP_PRINT_TAXONOMY_INFO(boolean b) {
        TMP_PRINT_TAXONOMY_INFO = b;
    }

    public boolean isDEBUG_SAVE_RESTORE() {
        return DEBUG_SAVE_RESTORE;
    }

    public void setDEBUG_SAVE_RESTORE(boolean b) {
        DEBUG_SAVE_RESTORE = b;
    }

    public boolean isRKG_USE_FAIRNESS() {
        return RKG_USE_FAIRNESS;
    }

    public void setRKG_USE_FAIRNESS(boolean b) {
        RKG_USE_FAIRNESS = b;
    }

    public boolean isFPP_DEBUG_SPLIT_MODULES() {
        return FPP_DEBUG_SPLIT_MODULES;
    }

    public void setFPP_DEBUG_SPLIT_MODULES(boolean b) {
        FPP_DEBUG_SPLIT_MODULES = b;
    }

    public boolean isSplits() {
        return splits;
    }

    public void setSplits(boolean splits) {
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

    public void setRegularLogOutputStream(OutputStream o) {
        logAdapterStream = new LogAdapterStream(o);
    }

    private LogAdapterStream logAbsorptionAdapterStream;

    public void setAbsorptionLogOutputStream(OutputStream o) {
        logAbsorptionAdapterStream = new LogAdapterStream(o);
    }

    static class LogAdapterStream implements LogAdapter {
        private OutputStream out;

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
        public LogAdapter print(Object s1, Object s2, Object s3, Object s4, Object s5) {
            this.print(s1.toString());
            this.print(s2.toString());
            this.print(s3.toString());
            this.print(s4.toString());
            this.print(s5.toString());
            return this;
        }
    }

    static class LogAdapterImpl implements LogAdapter {
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
        public LogAdapter print(Object s1, Object s2, Object s3, Object s4, Object s5) {
            return this;
        }
    }

    public boolean isUseELReasoner() {
        return useELReasoner;
    }

    public void setUseELReasoner(boolean useELReasoner) {
        this.useELReasoner = useELReasoner;
    }

    public boolean isUseUndefinedNames() {
        return useUndefinedNames;
    }

    public void setUseUndefinedNames(boolean useUndefinedNames) {
        this.useUndefinedNames = useUndefinedNames;
    }

    public boolean isUseAxiomSplitting() {
        return useAxiomSplitting;
    }

    public void setUseAxiomSplitting(boolean useAxiomSplitting) {
        this.useAxiomSplitting = useAxiomSplitting;
    }

    private boolean useADInModuleExtraction;

    public boolean isRKG_USE_AD_IN_MODULE_EXTRACTION() {
        return useADInModuleExtraction;
    }

    public void setRKG_USE_AD_IN_MODULE_EXTRACTION(boolean value) {
        useADInModuleExtraction = false;
    }
}
