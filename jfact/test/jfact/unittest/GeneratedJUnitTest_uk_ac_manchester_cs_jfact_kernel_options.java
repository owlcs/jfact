package jfact.unittest;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.kernel.options.LongOption;
import uk.ac.manchester.cs.jfact.kernel.options.Option;
import uk.ac.manchester.cs.jfact.kernel.options.StringOption;

@SuppressWarnings({ "unused", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact_kernel_options {
    @Test
    public void shouldTestJFactReasonerConfiguration() throws Exception {
        JFactReasonerConfiguration testSubject0 = new JFactReasonerConfiguration();
        JFactReasonerConfiguration testSubject1 = new JFactReasonerConfiguration(
                mock(OWLReasonerConfiguration.class));
        Object result0 = testSubject0.get(mock(String.class));
        ReasonerProgressMonitor result1 = testSubject0.getProgressMonitor();
        long result2 = testSubject0.getTimeOut();
        FreshEntityPolicy result3 = testSubject0.getFreshEntityPolicy();
        IndividualNodeSetPolicy result4 = testSubject0.getIndividualNodeSetPolicy();
        String result5 = testSubject0.getORSortSub();
        String result6 = testSubject0.getORSortSat();
        boolean result7 = testSubject0.isRKG_USE_SORTED_REASONING();
        boolean result8 = testSubject0.isRKG_PRINT_DAG_USAGE();
        LogAdapter result9 = testSubject0.getLog();
        testSubject0.setorSortSat(mock(String.class));
        testSubject0.setorSortSub(mock(String.class));
        Option result11 = JFactReasonerConfiguration.getOption(mock(String.class),
                mock(String.class));
        Option result12 = JFactReasonerConfiguration.getOption(mock(String.class),
                mock(long.class));
        boolean result13 = testSubject0.getuseAnywhereBlocking();
        boolean result14 = testSubject0.getuseBackjumping();
        boolean result15 = testSubject0.getuseLazyBlocking();
        boolean result16 = testSubject0.getuseSemanticBranching();
        boolean result17 = testSubject0.getverboseOutput();
        boolean result18 = testSubject0.getdumpQuery();
        boolean result19 = testSubject0.getuseCompletelyDefined();
        boolean result20 = testSubject0.getuseRelevantOnly();
        boolean result21 = testSubject0.getalwaysPreferEquals();
        String result22 = testSubject0.getabsorptionFlags();
        String result23 = testSubject0.getIAOEFLG();
        testSubject0.setuseAnywhereBlocking(mock(boolean.class));
        testSubject0.setverboseOutput(mock(boolean.class));
        boolean result24 = testSubject0.isLoggingActive();
        testSubject0.setLoggingActive(mock(boolean.class));
        boolean result25 = testSubject0.isAbsorptionLoggingActive();
        testSubject0.setAbsorptionLoggingActive(mock(boolean.class));
        boolean result26 = testSubject0.isRKG_IMPROVE_SAVE_RESTORE_DEPSET();
        testSubject0.setRKG_IMPROVE_SAVE_RESTORE_DEPSET(mock(boolean.class));
        testSubject0.setRKG_PRINT_DAG_USAGE(mock(boolean.class));
        boolean result27 = testSubject0.isRKG_USE_SIMPLE_RULES();
        testSubject0.setRKG_USE_SIMPLE_RULES(mock(boolean.class));
        testSubject0.setRKG_USE_SORTED_REASONING(mock(boolean.class));
        boolean result28 = testSubject0.isUSE_REASONING_STATISTICS();
        testSubject0.setUSE_REASONING_STATISTICS(mock(boolean.class));
        boolean result29 = testSubject0.isRKG_UPDATE_RND_FROM_SUPERROLES();
        testSubject0.setRKG_UPDATE_RND_FROM_SUPERROLES(mock(boolean.class));
        boolean result30 = testSubject0.isUSE_BLOCKING_STATISTICS();
        testSubject0.setUSE_BLOCKING_STATISTICS(mock(boolean.class));
        boolean result31 = testSubject0.isRKG_USE_DYNAMIC_BACKJUMPING();
        testSubject0.setRKG_USE_DYNAMIC_BACKJUMPING(mock(boolean.class));
        boolean result32 = testSubject0.isTMP_PRINT_TAXONOMY_INFO();
        testSubject0.setTMP_PRINT_TAXONOMY_INFO(mock(boolean.class));
        boolean result33 = testSubject0.isDEBUG_SAVE_RESTORE();
        testSubject0.setDEBUG_SAVE_RESTORE(mock(boolean.class));
        boolean result34 = testSubject0.isRKG_USE_FAIRNESS();
        testSubject0.setRKG_USE_FAIRNESS(mock(boolean.class));
        boolean result35 = testSubject0.isFPP_DEBUG_SPLIT_MODULES();
        testSubject0.setFPP_DEBUG_SPLIT_MODULES(mock(boolean.class));
        boolean result36 = testSubject0.isSplits();
        testSubject0.setSplits(mock(boolean.class));
        LogAdapter result37 = testSubject0.getAbsorptionLog();
        boolean result38 = testSubject0.isUseELReasoner();
        testSubject0.setUseELReasoner(mock(boolean.class));
        boolean result39 = testSubject0.isUseUndefinedNames();
        testSubject0.setUseUndefinedNames(mock(boolean.class));
        boolean result40 = testSubject0.isUseAxiomSplitting();
        testSubject0.setUseAxiomSplitting(mock(boolean.class));
        String result41 = testSubject0.toString();
    }

    @Test
    public void shouldTestLongOption() throws Exception {
        LongOption testSubject0 = new LongOption(mock(String.class), mock(Long.class));
        Object result0 = testSubject0.getValue();
        String result1 = testSubject0.getOptionName();
        String result2 = testSubject0.toString();
    }

    @Test
    public void shouldTestInterfaceOption() throws Exception {
        Option testSubject0 = mock(Option.class);
        Object result0 = testSubject0.getValue();
        String result1 = testSubject0.getOptionName();
    }

    @Test
    public void shouldTestStringOption() throws Exception {
        StringOption testSubject0 = new StringOption(mock(String.class),
                mock(String.class));
        Object result0 = testSubject0.getValue();
        String result1 = testSubject0.getOptionName();
        String result2 = testSubject0.toString();
    }
}
