package uk.ac.manchester.cs.jfact.helpers;

public enum Templates {
    TAX_TRYING("\nTAX: trying '%s' [= '%s'... "), INTERVAL(" %s%s%s,%s%s"), CLASH(
            " DT-%s"), CREATE_EDGE(" ce(%s%s%s,%s)"), IS_BLOCKED_FAILURE_BY(" fb(%s,%s)"), LOG_NODE_BLOCKED(
            " %sb(%s%s%s)"), LOG_SR_NODE(" %s(%s[%s],%s)"), DETERMINE_SORTS(
            "\nThere are %s different sorts in TBox\n"), WRITE_STATE(
            "\nLoaded KB used DL with following features:\nKB contains %sinverse role(s)\nKB contains %srole hierarchy\nKB contains %stransitive role(s)\nKB contains %stop role expressions\nKB contains quantifier(s)\nKB contains %sfunctional restriction(s)\nKB contains %snumber restriction(s)\nKB contains %snominal(s)\n"), BUILD_CACHE_UNSAT(
            "\nDAG entry %s is unsatisfiable\n"), CAN_BE_CACHED(" cf(%s)"), CHECK_MERGE_CLASH(
            " x(%s,%s%s)"), COMMON_TACTIC_BODY_OR(" E(%s)"), COMMON_TACTIC_BODY_SOME(
            " nf(%s)"), COMMON_TACTIC_BODY_SOME2(" f(%s):"), CONSISTENT_NOMINAL(
            "\nThe ontology is %s"), DN(" DN(%s%s)"), CN(" cn(%s%s)"), NN(" NN(%s)"), E(
            " E(%s,%s,%s)"), LOG_FINISH_ENTRY(" Clash%s"), DLVERTEXPrint2("(%s) %s %s"), DLVERTEXPrint3(
            " %s{%s} %s"), DLVERTEXPrint4(" %s, %s => %s"), LOGCACHEENTRY(
            "\nConst cache: element %s"), DLCOMPLETIONTREEARC("<%s%s>"), DLCONCEPTTAXONOMY(
            "Totally %s subsumption tests was made\nAmong them %s (%s) successfull\n"
                    + "Besides that %s successfull and %s unsuccessfull subsumption tests were cached\n"
                    + "%sThere were made %s search calls\n"
                    + "There were made %s Sub calls, of which %s non-trivial\nCurrent efficiency (wrt Brute-force) is %s\n"), PRINTDAGUSAGE(
            "There are %s unused DAG entries (%s percent of %s total)\n"), READCONFIG(
            "Init useSemanticBranching = %s\nInit useBackjumping = %s\nInit useLazyBlocking  = %s\nInit useAnywhereBlocking = %s\n"), PRINT_STAT(
            "Heap size = %s nodes\nThere were %s cache hits\n"), REPORT1(" cached(%s)"), SAVE(
            " ss(%s)"), ISSUBHOLDS1(
            "\n----------------------\nChecking subsumption '%s [= %s':\n"), ISSUBHOLDS2(
            "\nThe '%s [= %s' subsumption%s holds w.r.t. TBox"), INCORPORATE(
            "\nTAX:inserting '%s' with up = {"), MERGE(" m(%s->%s)"), RESTORE(" sr(%s)"), CLASSIFY_CONCEPTS(
            "\n\n---Start classifying %s concepts"), CLASSIFY_CONCEPTS2(
            "\n---Done: %s %s concepts classified"), READ_CONFIG(
            "Init useCompletelyDefined = %s\nInit useRelevantOnly = %s\nInit dumpQuery = %s\nInit alwaysPreferEquals = %s\n"
    /*
     * \ nInit usePrecompletion = % s "
     */), TOLD_SUBSUMERS(" '%s'"), TRANSFORM_TOLD_CYCLES(
            "\nTold cycle elimination done with %s synonyms created"), IS_SATISFIABLE(
            "\n-----------\nChecking satisfiability of '%s':\n"), IS_SATISFIABLE1(
            "\nThe '%s' concept is %ssatisfiable w.r.t. TBox");
    private String template;

    private Templates(String s) {
        template = s;
    }

    public String getTemplate() {
        return template;
    }
}
