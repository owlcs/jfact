package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** templates */
public enum Templates {
    //@formatter:off
    /** TAX_TRYING               */ TAX_TRYING              ("\nTAX: trying '%s' [= '%s'... "), 
    /** INTERVAL                 */ INTERVAL                (" %s%s%s,%s%s"), 
    /** CREATE_EDGE              */ CREATE_EDGE             (" ce(%s%s%s,%s)"),
    /** IS_BLOCKED_FAILURE_BY    */ IS_BLOCKED_FAILURE_BY   (" fb(%s,%s)"),
    /** LOG_NODE_BLOCKED         */ LOG_NODE_BLOCKED        (" %sb(%s%s%s)"),
    /** LOG_SR_NODE              */ LOG_SR_NODE             (" %s(%s[%s],%s)"),
    /** DETERMINE_SORTS          */ DETERMINE_SORTS         ("\nThere are %s different sorts in TBox\n"),
    /** WRITE_STATE              */ WRITE_STATE             ("\nLoaded KB used DL with following features:\nKB contains %sinverse role(s)\nKB contains %srole hierarchy\nKB contains %stransitive role(s)\nKB contains %stop role expressions\nKB contains quantifier(s)\nKB contains %sfunctional restriction(s)\nKB contains %snumber restriction(s)\nKB contains %snominal(s)\n"),
    /** BUILD_CACHE_UNSAT        */ BUILD_CACHE_UNSAT       ("\nDAG entry %s is unsatisfiable\n"),
    /** CAN_BE_CACHED            */ CAN_BE_CACHED           (" cf(%s)"),
    /** CHECK_MERGE_CLASH        */ CHECK_MERGE_CLASH       (" x(%2$s,%3$s%1$s)"),
    /** COMMON_TACTIC_BODY_OR    */ COMMON_TACTIC_BODY_OR   (" E(%s)"),
    /** COMMON_TACTIC_BODY_SOME  */ COMMON_TACTIC_BODY_SOME (" nf(%s)"),
    /** COMMON_TACTIC_BODY_SOME2 */ COMMON_TACTIC_BODY_SOME2(" f(%s):"),
    /** CONSISTENT_NOMINAL       */ CONSISTENT_NOMINAL      ("\nThe ontology is %s"),
    /** DN                       */ DN                      (" DN(%2$s%1$s)"),
    /** CN                       */ CN                      (" cn(%2$s%1$s)"),
    /** NN                       */ NN                      (" NN(%s)"),
    /** E                        */ E                       (" E(%s,%s,%s)"),
    /** LOG_FINISH_ENTRY         */ LOG_FINISH_ENTRY        (" Clash%s"),
    /** DLVERTEXPrint2           */ DLVERTEXPRINT2          ("(%s) %s %s"),
    /** DLVERTEXPrint3           */ DLVERTEXPRINT3          (" %s{%s} %s"),
    /** DLVERTEXPrint4           */ DLVERTEXPRINT4          (" %s, %s => %s"),
    /** LOGCACHEENTRY            */ LOGCACHEENTRY           ("\nConst cache: element %s"),
    /** DLCOMPLETIONTREEARC      */ DLCOMPLETIONTREEARC     ("<%s%s>"),
    /** DLCONCEPTTAXONOMY        */ DLCONCEPTTAXONOMY       ("Totally %s subsumption tests was made\nAmong them %s (%s) successfull\n" + "Besides that %s successfull and %s unsuccessfull subsumption tests were cached\n" + "%sThere were made %s search calls\n" + "There were made %s Sub calls, of which %s non-trivial\nCurrent efficiency (wrt Brute-force) is %s\n"),
    /** PRINTDAGUSAGE            */ PRINTDAGUSAGE           ("There are %s unused DAG entries (%s percent of %s total)\n"),
    /** READCONFIG               */ READCONFIG              ("Init useSemanticBranching = %s\nInit useBackjumping = %s\nInit useLazyBlocking  = %s\nInit useAnywhereBlocking = %s\n"),
    /** PRINT_STAT               */ PRINT_STAT              ("Heap size = %s nodes\nThere were %s cache hits\n"),
    /** REPORT1                  */ REPORT1                 (" cached(%s)"),
    /** SAVE                     */ SAVE                    (" ss(%s)"),
    /** ISSUBHOLDS1              */ ISSUBHOLDS1             ("\n----------------------\nChecking subsumption '%s [= %s':\n"),
    /** ISSUBHOLDS2              */ ISSUBHOLDS2             ("\nThe '%s [= %s' subsumption%s holds w.r.t. TBox"),
    /** INCORPORATE              */ INCORPORATE             ("\nTAX:inserting '%s' with up = {"),
    /** MERGE                    */ MERGE                   (" m(%s->%s)"),
    /** RESTORE                  */ RESTORE                 (" sr(%s)"),
    /** CLASSIFY_CONCEPTS        */ CLASSIFY_CONCEPTS       ("\n\n---Start classifying %s concepts"),
    /** CLASSIFY_CONCEPTS2       */ CLASSIFY_CONCEPTS2      ("\n---Done: %2$s %1$s concepts classified"),
    /** READ_CONFIG              */ READ_CONFIG             ("Init useCompletelyDefined = %s\nInit useRelevantOnly = %s\nInit dumpQuery = %s\nInit alwaysPreferEquals = %s\n"),
    /** TOLD_SUBSUMERS           */ TOLD_SUBSUMERS          (" '%s'"),
    /** TRANSFORM_TOLD_CYCLES    */ TRANSFORM_TOLD_CYCLES   ("\nTold cycle elimination done with %s synonyms created"),
    /** IS_SATISFIABLE           */ IS_SATISFIABLE          ("\n-----------\nChecking satisfiability of '%s':\n"),
    /** IS_SATISFIABLE1          */ IS_SATISFIABLE1         ("\nThe '%s' concept is %ssatisfiable w.r.t. TBox");
    //@formatter:on
    private final String template;

    private Templates(String s) {
        template = s;
    }

    /** @return template */
    public String getTemplate() {
        return template;
    }
}
