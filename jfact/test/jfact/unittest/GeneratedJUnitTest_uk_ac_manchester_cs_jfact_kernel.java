package jfact.unittest;

import static org.mockito.Mockito.*;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.jfact.datatypes.*;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.*;
import uk.ac.manchester.cs.jfact.kernel.*;
import uk.ac.manchester.cs.jfact.kernel.Concept.CTTag;
import uk.ac.manchester.cs.jfact.kernel.ToDoList.ToDoEntry;
import uk.ac.manchester.cs.jfact.kernel.actors.Actor;
import uk.ac.manchester.cs.jfact.kernel.actors.SupConceptActor;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheState;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.kernel.state.SaveState;
import uk.ac.manchester.cs.jfact.split.*;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact_kernel {
    @Test
    public void shouldTestAxiom() throws Exception {
        uk.ac.manchester.cs.jfact.kernel.Axiom testSubject0 = mock(uk.ac.manchester.cs.jfact.kernel.Axiom.class);
        testSubject0.add(mock(DLTree.class));
        String result0 = testSubject0.toString();
        List result1 = testSubject0.split(mock(TBox.class));
        TOntologyAtom result2 = testSubject0.getAtom();
        testSubject0.setAtom(mock(TOntologyAtom.class));
        boolean result3 = testSubject0.absorbIntoNegConcept(mock(TBox.class));
        DLTree result4 = testSubject0.createAnAxiom(mock(DLTree.class));
        uk.ac.manchester.cs.jfact.kernel.Axiom result5 = testSubject0
                .simplifyCN(mock(TBox.class));
        uk.ac.manchester.cs.jfact.kernel.Axiom result6 = testSubject0
                .simplifyForall(mock(TBox.class));
        boolean result7 = testSubject0.absorbIntoBottom(mock(TBox.class));
        boolean result8 = testSubject0.absorbIntoConcept(mock(TBox.class));
        boolean result9 = testSubject0.absorbIntoDomain(mock(TBox.class));
        boolean result10 = testSubject0.absorbIntoTop(mock(TBox.class));
    }

    @Test
    public void shouldTestAxiomSet() throws Exception {
        AxiomSet testSubject0 = new AxiomSet(mock(TBox.class));
        testSubject0.addAxiom(mock(DLTree.class), mock(DLTree.class));
        int result0 = testSubject0.absorb();
        boolean result1 = testSubject0.wasRoleAbsorptionApplied();
        DLTree result2 = testSubject0.getGCI();
        boolean result3 = testSubject0.initAbsorptionFlags(mock(String.class));
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestCGLabel() throws Exception {
        CGLabel testSubject0 = new CGLabel();
        testSubject0.add(mock(DagTag.class), mock(ConceptWDep.class));
        String result0 = testSubject0.toString();
        boolean result1 = testSubject0.contains(1);
        testSubject0.init();
        int result2 = testSubject0.getId();
        testSubject0.save(mock(SaveState.class));
        boolean result3 = testSubject0.lesserequal(mock(CGLabel.class));
        testSubject0.restore(mock(SaveState.class), 1);
        List result4 = testSubject0.get_sc();
        List result5 = testSubject0.get_cc();
        ArrayIntMap result6 = testSubject0.get_sc_concepts();
        ArrayIntMap result7 = testSubject0.get_cc_concepts();
        CWDArray result8 = testSubject0.getLabel(mock(DagTag.class));
        boolean result9 = testSubject0.containsCC(1);
        ConceptWDep result10 = testSubject0.getConceptWithBP(1);
        int result11 = testSubject0.baseSize();
    }

    @Test
    public void shouldTestClassifiableEntry() throws Exception {
        ClassifiableEntry testSubject0 = mock(ClassifiableEntry.class);
        int result0 = testSubject0.getIndex();
        testSubject0.setIndex(1);
        boolean result1 = testSubject0.isSynonym();
        ClassifiableEntry result2 = ClassifiableEntry
                .resolveSynonym(mock(ClassifiableEntry.class));
        testSubject0.setCompletelyDefined(true);
        testSubject0.addParent(mock(ClassifiableEntry.class));
        testSubject0.removeSynonymsFromParents();
        testSubject0.setSynonym(mock(ClassifiableEntry.class));
        Collection result3 = testSubject0.getToldSubsumers();
        testSubject0.addParents(mock(Collection.class));
        ClassifiableEntry result4 = testSubject0.getSynonym();
        boolean result5 = testSubject0.isClassified();
        TaxonomyVertex result6 = testSubject0.getTaxVertex();
        testSubject0.setTaxVertex(mock(TaxonomyVertex.class));
        boolean result7 = testSubject0.isCompletelyDefined();
        testSubject0.clearCompletelyDefined();
        boolean result8 = testSubject0.isNonClassifiable();
        testSubject0.setNonClassifiable(true);
        testSubject0.setNonClassifiable();
        testSubject0.clearNonClassifiable();
        boolean result9 = testSubject0.hasToldSubsumers();
        testSubject0.addParentIfNew(mock(ClassifiableEntry.class));
        testSubject0.canonicaliseSynonym();
        String result10 = testSubject0.toString();
        String result11 = testSubject0.getName();
        int result12 = testSubject0.getId();
        boolean result13 = testSubject0.isBottom();
        boolean result14 = testSubject0.isTop();
        testSubject0.setId(1);
        boolean result15 = testSubject0.isSystem();
        testSubject0.setSystem();
        testSubject0.setTop();
        testSubject0.setBottom();
        NamedEntity result16 = testSubject0.getEntity();
        testSubject0.setEntity(mock(NamedEntity.class));
    }

    @Test
    public void shouldTestConcept() throws Exception {
        Concept testSubject0 = new Concept(mock(String.class));
        String result0 = testSubject0.toString();
        boolean result1 = testSubject0.isPrimitive();
        testSubject0.push(mock(LinkedList.class), mock(DLTree.class));
        boolean result2 = testSubject0.isSingleton();
        boolean result3 = testSubject0.isRelevant(1L);
        testSubject0.setRelevant(1L);
        testSubject0.setpName(1);
        DLTree result4 = testSubject0.getDescription();
        DLTree result5 = testSubject0.makeNonPrimitive(mock(DLTree.class));
        testSubject0.addDesc(mock(DLTree.class));
        testSubject0.removeSelfFromDescription();
        boolean result6 = testSubject0.canInitNonPrim(mock(DLTree.class));
        boolean result7 = testSubject0.initToldSubsumers(mock(DLTree.class),
                mock(Set.class));
        testSubject0.initToldSubsumers();
        int result8 = testSubject0.getpName();
        int result9 = testSubject0.resolveId();
        testSubject0.setToldTop(mock(Concept.class));
        int result10 = testSubject0.calculateTSDepth();
        testSubject0.addExtraRule(1);
        CTTag result11 = testSubject0.getClassTag();
        int result12 = testSubject0.getpBody();
        LogicFeatures result13 = testSubject0.getPosFeatures();
        LogicFeatures result14 = testSubject0.getNegFeatures();
        testSubject0.setNominal(true);
        testSubject0.setpBody(1);
        testSubject0.setPrimitive(true);
        Concept result15 = Concept.getTOP();
        Concept result16 = Concept.getBOTTOM();
        Concept result17 = Concept.getTEMP();
        CTTag result18 = testSubject0.getClassTagPlain();
        boolean result19 = testSubject0.isNominal();
        int result20 = testSubject0.getTsDepth();
        testSubject0.addLeaves(mock(Collection.class));
        testSubject0.dropRelevant(1L);
        boolean result21 = testSubject0.isHasSP();
        testSubject0.removeDescription();
        testSubject0.setHasSP(true);
        boolean result22 = testSubject0.hasExtraRules();
        FastSet result23 = testSubject0.getExtraRules();
        testSubject0.searchTSbyRoleAndSupers(mock(Role.class), mock(Set.class));
        testSubject0.setSingleton(true);
        int result24 = testSubject0.getIndex();
        testSubject0.setIndex(1);
        boolean result25 = testSubject0.isSynonym();
        ClassifiableEntry result26 = ClassifiableEntry
                .resolveSynonym(mock(ClassifiableEntry.class));
        testSubject0.setCompletelyDefined(true);
        testSubject0.addParent(mock(ClassifiableEntry.class));
        testSubject0.removeSynonymsFromParents();
        testSubject0.setSynonym(mock(ClassifiableEntry.class));
        Collection result27 = testSubject0.getToldSubsumers();
        testSubject0.addParents(mock(Collection.class));
        ClassifiableEntry result28 = testSubject0.getSynonym();
        boolean result29 = testSubject0.isClassified();
        TaxonomyVertex result30 = testSubject0.getTaxVertex();
        testSubject0.setTaxVertex(mock(TaxonomyVertex.class));
        boolean result31 = testSubject0.isCompletelyDefined();
        testSubject0.clearCompletelyDefined();
        boolean result32 = testSubject0.isNonClassifiable();
        testSubject0.setNonClassifiable(true);
        testSubject0.setNonClassifiable();
        testSubject0.clearNonClassifiable();
        boolean result33 = testSubject0.hasToldSubsumers();
        testSubject0.addParentIfNew(mock(ClassifiableEntry.class));
        testSubject0.canonicaliseSynonym();
        String result34 = testSubject0.getName();
        int result35 = testSubject0.getId();
        boolean result36 = testSubject0.isBottom();
        boolean result37 = testSubject0.isTop();
        testSubject0.setId(1);
        boolean result38 = testSubject0.isSystem();
        testSubject0.setSystem();
        testSubject0.setTop();
        testSubject0.setBottom();
        NamedEntity result39 = testSubject0.getEntity();
        testSubject0.setEntity(mock(NamedEntity.class));
    }

    @Test
    public void shouldTestConceptWDep() throws Exception {
        ConceptWDep testSubject0 = new ConceptWDep(1);
        ConceptWDep testSubject1 = new ConceptWDep(1, mock(DepSet.class));
        String result0 = testSubject0.toString();
        int result1 = testSubject0.getConcept();
        testSubject0.addDep(mock(DepSet.class));
        DepSet result2 = testSubject0.getDep();
    }

    @Test
    public void shouldTestCWDArray() throws Exception {
        CWDArray testSubject0 = new CWDArray();
        DepSet result0 = testSubject0.get(1);
        String result1 = testSubject0.toString();
        boolean result2 = testSubject0.contains(1);
        int result3 = testSubject0.size();
        testSubject0.init();
        int result4 = testSubject0.save();
        int result5 = testSubject0.index(1);
        boolean result6 = testSubject0.lesserequal(mock(CWDArray.class));
        testSubject0.restore(1, 1);
        List result7 = testSubject0.getBase();
        ArrayIntMap result8 = testSubject0.getContainedConcepts();
        ConceptWDep result9 = testSubject0.getConceptWithBP(1);
        List result10 = testSubject0.updateDepSet(mock(DepSet.class));
        Restorer result11 = testSubject0.updateDepSet(1, mock(DepSet.class));
    }

    @Test
    public void shouldTestDagTag() throws Exception {
        DagTag testSubject0 = DagTag.dtAnd;
        DagTag[] result0 = DagTag.values();
        DagTag result1 = DagTag.valueOf(mock(String.class));
        String result2 = testSubject0.getName();
        boolean result3 = testSubject0.isNNameTag();
        boolean result4 = testSubject0.omitStat(true);
        boolean result5 = testSubject0.isPNameTag();
        boolean result6 = testSubject0.isCNameTag();
        boolean result7 = testSubject0.isComplexConcept();
        String result8 = testSubject0.name();
        String result9 = testSubject0.toString();
        Enum result12 = Enum.valueOf(mock(Class.class), mock(String.class));
        Class result13 = testSubject0.getDeclaringClass();
        int result14 = testSubject0.ordinal();
    }

    @Test
    public void shouldTestDlCompletionGraph() throws Exception {
        DlCompletionGraph testSubject0 = new DlCompletionGraph(1, mock(DlSatTester.class));
        testSubject0.clear();
        testSubject0.print(mock(LogAdapter.class));
        testSubject0.save();
        testSubject0.merge(mock(DlCompletionTree.class), mock(DlCompletionTree.class),
                mock(DepSet.class), mock(List.class));
        int result0 = testSubject0.maxSize();
        DlCompletionTree result1 = testSubject0.getNewNode();
        testSubject0.saveRareCond(mock(List.class));
        testSubject0.saveRareCond(mock(Restorer.class));
        testSubject0.saveNode(mock(DlCompletionTree.class), 1);
        testSubject0.clearStatistics();
        testSubject0.initContext(1, true, true);
        testSubject0.setBlockingMethod(true, true);
        testSubject0.addConceptToNode(mock(DlCompletionTree.class),
                mock(ConceptWDep.class), mock(DagTag.class));
        testSubject0.detectBlockedStatus(mock(DlCompletionTree.class));
        DlCompletionTree result2 = testSubject0.getRoot();
        DlCompletionTree result3 = testSubject0.getNode(1);
        testSubject0.updateDBlockedStatus(mock(DlCompletionTree.class));
        testSubject0.retestCGBlockedStatus();
        DlCompletionTreeArc result4 = testSubject0.addRoleLabel(
                mock(DlCompletionTree.class), mock(DlCompletionTree.class), true,
                mock(Role.class), mock(DepSet.class));
        DlCompletionTreeArc result5 = testSubject0.createNeighbour(
                mock(DlCompletionTree.class), true, mock(Role.class), mock(DepSet.class));
        DlCompletionTreeArc result6 = testSubject0.createLoop(
                mock(DlCompletionTree.class), mock(Role.class), mock(DepSet.class));
        testSubject0.restore(1);
        boolean result7 = testSubject0.nonMergable(mock(DlCompletionTree.class),
                mock(DlCompletionTree.class), mock(Reference.class));
        testSubject0.initIR();
        boolean result8 = testSubject0.setCurIR(mock(DlCompletionTree.class),
                mock(DepSet.class));
        testSubject0.finiIR();
        String result9 = testSubject0.toString();
    }

    @Test
    public void shouldTestDlCompletionTree() throws Exception {
        DlCompletionTree testSubject0 = new DlCompletionTree(1,
                mock(JFactReasonerConfiguration.class));
        String result0 = testSubject0.toString();
        int result1 = testSubject0.compareTo(mock(DlCompletionTree.class));
        testSubject0.init(1);
        int result3 = testSubject0.getId();
        testSubject0.save(1);
        CGLabel result4 = testSubject0.label();
        boolean result5 = testSubject0.isDBlocked();
        testSubject0.clearAffected();
        boolean result6 = testSubject0.isBlocked();
        Restorer result7 = testSubject0.setUBlocked();
        List result8 = testSubject0.getNeighbour();
        boolean result9 = testSubject0.isIBlocked();
        Restorer result10 = testSubject0.setDBlocked(mock(DlCompletionTree.class));
        boolean result11 = testSubject0.isPBlocked();
        boolean result12 = testSubject0.isNominalNode();
        Restorer result13 = testSubject0.setIBlocked(mock(DlCompletionTree.class));
        boolean result14 = testSubject0.isAffected();
        boolean result15 = testSubject0.isIllegallyDBlocked();
        testSubject0.addConcept(mock(ConceptWDep.class), mock(DagTag.class));
        testSubject0.setAffected();
        DlCompletionTree result16 = testSubject0.resolvePBlocker(mock(DepSet.class));
        DlCompletionTree result17 = testSubject0.resolvePBlocker();
        boolean result18 = testSubject0.isLoopLabelled(1);
        DlCompletionTreeArc result19 = testSubject0.getEdgeLabelled(mock(Role.class),
                mock(DlCompletionTree.class));
        boolean result20 = testSubject0.needSave(1);
        boolean result21 = testSubject0.needRestore(1);
        int result22 = testSubject0.getInit();
        boolean result23 = testSubject0.canBlockInit(1);
        boolean result24 = testSubject0.isBlockedBy_SHIQ(mock(DLDag.class),
                mock(DlCompletionTree.class));
        boolean result25 = testSubject0.isBlockedBy_SHI(mock(DLDag.class),
                mock(DlCompletionTree.class));
        boolean result26 = testSubject0.isBlockedBy_SH(mock(DlCompletionTree.class));
        DlCompletionTree result27 = testSubject0.getParentNode();
        boolean result28 = testSubject0.hasParent();
        boolean result29 = testSubject0.isBlockableNode();
        boolean result30 = testSubject0.isBlockedPBlockedNominalNodeCached();
        boolean result31 = testSubject0.nonMergable(mock(DlCompletionTree.class),
                mock(Reference.class));
        Restorer result32 = testSubject0.updateIR(mock(DlCompletionTree.class),
                mock(DepSet.class));
        boolean result33 = testSubject0.initIR(1, mock(DepSet.class));
        testSubject0.addNeighbour(mock(DlCompletionTreeArc.class));
        Restorer result34 = testSubject0.setPBlocked(mock(DlCompletionTree.class),
                mock(DepSet.class));
        testSubject0.printBody(mock(LogAdapter.class));
        boolean result35 = testSubject0.isDataNode();
        boolean result36 = testSubject0.isCached();
        Restorer result37 = testSubject0.setCached(true);
        DlCompletionTree result38 = testSubject0.isSomeApplicable(mock(Role.class), 1);
        List result39 = testSubject0.beginl_cc();
        String result40 = testSubject0.logNode();
        testSubject0.setInit(1);
        List result41 = testSubject0.beginl_sc();
        testSubject0.setNominalLevel();
        testSubject0.setNominalLevel(1);
        testSubject0.setDataNode();
        int result42 = testSubject0.getNominalLevel();
        boolean result43 = testSubject0.isLabelledBy(1);
        boolean result44 = testSubject0.B2Complex(mock(RAStateTransitions.class), 1);
        ArrayIntMap result45 = testSubject0.beginl_sc_concepts();
        ArrayIntMap result46 = testSubject0.beginl_cc_concepts();
        DlCompletionTree result47 = testSubject0.getBlocker();
        DepSet result48 = testSubject0.getPurgeDep();
        testSubject0.setBlocker(mock(DlCompletionTree.class));
    }

    @Test
    public void shouldTestDlCompletionTreeArc() throws Exception {
        DlCompletionTreeArc testSubject0 = new DlCompletionTreeArc(mock(Role.class),
                mock(DepSet.class), mock(DlCompletionTree.class));
        testSubject0.print(mock(LogAdapter.class));
        Restorer result0 = testSubject0.save();
        Role result1 = testSubject0.getRole();
        boolean result2 = testSubject0.isSuccEdge();
        boolean result3 = testSubject0.isIBlocked();
        boolean result4 = testSubject0.isReflexiveEdge();
        DlCompletionTree result5 = testSubject0.getArcEnd();
        Restorer result6 = testSubject0.addDep(mock(DepSet.class));
        boolean result7 = testSubject0.isPredEdge();
        testSubject0.setSuccEdge(true);
        testSubject0.setReverse(mock(DlCompletionTreeArc.class));
        DepSet result8 = testSubject0.getDep();
        DlCompletionTreeArc result9 = testSubject0.getReverse();
        boolean result10 = testSubject0.isNeighbour(mock(Role.class), mock(DepSet.class));
        boolean result11 = testSubject0.isNeighbour(mock(Role.class));
        String result12 = testSubject0.toString();
    }

    @Test
    public void shouldTestDLConceptTaxonomy() throws Exception {
        DLConceptTaxonomy testSubject0 = new DLConceptTaxonomy(mock(Concept.class),
                mock(Concept.class), mock(TBox.class));
        String result0 = testSubject0.toString();
        testSubject0.setBottomUp(mock(KBFlags.class));
        testSubject0.runTopDown();
        testSubject0.runBottomUp();
        testSubject0.preClassificationActions();
        testSubject0.setCompletelyDefined(true);
        testSubject0.getRelativesInfo(mock(TaxonomyVertex.class), mock(Actor.class),
                true, true, true);
        boolean result1 = testSubject0.getRelativesInfo(mock(TaxonomyVertex.class),
                mock(SupConceptActor.class), true, true, true);
        testSubject0.finalise();
        testSubject0.classifyEntry(mock(ClassifiableEntry.class));
        TaxonomyVertex result2 = testSubject0.getTopVertex();
        TaxonomyVertex result3 = testSubject0.getBottomVertex();
        testSubject0.addCurrentToSynonym(mock(TaxonomyVertex.class));
        boolean result4 = testSubject0.queryMode();
    }

    @Test
    public void shouldTestDLDag() throws Exception {
        DLDag testSubject0 = new DLDag(mock(JFactReasonerConfiguration.class));
        int result0 = testSubject0.add(mock(DLVertex.class));
        DLVertex result1 = testSubject0.get(1);
        String result2 = testSubject0.toString();
        int result3 = testSubject0.size();
        int result4 = testSubject0.index(mock(NamedEntry.class));
        testSubject0.merge(mock(MergableLabel.class), 1);
        int result5 = testSubject0.maxSize();
        testSubject0.setCache(1, mock(ModelCacheInterface.class));
        ModelCacheInterface result6 = testSubject0.getCache(1);
        testSubject0.replaceVertex(1, mock(DLVertex.class), mock(NamedEntry.class));
        testSubject0.updateIndex(mock(DagTag.class), 1);
        int result7 = testSubject0.directAdd(mock(DLVertex.class));
        int result8 = testSubject0.directAddAndCache(mock(DLVertex.class));
        boolean result9 = testSubject0.isLast(1);
        testSubject0.setExpressionCache(true);
        testSubject0.setSubOrder();
        testSubject0.setOrderOptions(mock(String.class));
        testSubject0.setSatOrder();
        boolean result10 = testSubject0.haveSameSort(1, 1);
        testSubject0.printStat(mock(LogAdapter.class));
        testSubject0.printDAGUsage(mock(LogAdapter.class));
        testSubject0.setFinalSize();
        testSubject0.removeQuery();
        testSubject0.setOrderDefaults(mock(String.class), mock(String.class));
        testSubject0.gatherStatistic();
        boolean result11 = testSubject0.less(1, 1);
        testSubject0.determineSorts(mock(RoleMaster.class), mock(RoleMaster.class));
        testSubject0.updateSorts(1, mock(Role.class), 1);
    }

    @Test
    public void shouldTestDlSatTester() throws Exception {
        DlSatTester testSubject0 = mock(DlSatTester.class);
        JFactReasonerConfiguration result0 = testSubject0.getOptions();
        testSubject0.setBlockingMethod(true, true);
        DLDag result1 = testSubject0.getDAG();
        testSubject0.repeatUnblockedNode(mock(DlCompletionTree.class), true);
        boolean result2 = testSubject0.checkIrreflexivity(mock(Role.class));
        boolean result3 = testSubject0.runSat(1, 1);
        ModelCacheInterface result4 = testSubject0.buildCacheByCGraph(true);
        boolean result5 = testSubject0.checkDisjointRoles(mock(Role.class),
                mock(Role.class));
        DlCompletionTree result6 = testSubject0.getRootNode();
        testSubject0.writeTotalStatistic(mock(LogAdapter.class));
        float result7 = testSubject0.printReasoningTime(mock(LogAdapter.class));
        testSubject0.initToDoPriorities();
        ModelCacheInterface result8 = testSubject0.createCache(1, mock(FastSet.class));
        boolean result9 = testSubject0.hasNominals();
        boolean result10 = testSubject0.setupEdge(mock(DlCompletionTreeArc.class),
                mock(DepSet.class), 1);
        boolean result11 = testSubject0.findConcept(mock(CWDArray.class), 1);
        String result12 = testSubject0.toString();
    }

    @Test
    public void shouldTestExpressionManager() throws Exception {
        ExpressionManager testSubject0 = new ExpressionManager();
        ConceptExpression result0 = testSubject0.value(mock(ObjectRoleExpression.class),
                mock(IndividualExpression.class));
        ConceptExpression result1 = testSubject0.value(mock(DataRoleExpression.class),
                mock(Literal.class));
        testSubject0.clear();
        ConceptExpression result2 = testSubject0.exists(mock(DataRoleExpression.class),
                mock(DataExpression.class));
        ConceptExpression result3 = testSubject0.exists(mock(ObjectRoleExpression.class),
                mock(ConceptExpression.class));
        ConceptExpression result4 = testSubject0.cardinality(1,
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        ConceptExpression result5 = testSubject0.cardinality(1,
                mock(DataRoleExpression.class), mock(DataExpression.class));
        ConceptExpression result6 = testSubject0.and(mock(List.class));
        ConceptExpression result7 = testSubject0.and(mock(ConceptExpression.class),
                mock(ConceptExpression.class));
        ConceptExpression result8 = testSubject0.or(mock(List.class));
        ConceptExpression result9 = testSubject0.or(mock(ConceptExpression.class),
                mock(ConceptExpression.class));
        ConceptExpression result10 = testSubject0.not(mock(ConceptExpression.class));
        ObjectRoleComplexExpression result11 = testSubject0.compose(
                mock(Expression.class), mock(Expression.class));
        ObjectRoleComplexExpression result12 = testSubject0.compose(mock(List.class));
        ObjectRoleExpression result13 = testSubject0
                .inverse(mock(ObjectRoleExpression.class));
        boolean result14 = testSubject0.isUniversalRole(mock(DataRoleExpression.class));
        boolean result15 = testSubject0
                .isUniversalRole(mock(ObjectRoleComplexExpression.class));
        ConceptExpression result16 = testSubject0.top();
        ConceptExpression result17 = testSubject0.bottom();
        DataRoleExpression result18 = testSubject0.dataRole(mock(String.class));
        ConceptExpression result19 = testSubject0.concept(mock(String.class));
        ObjectRoleExpression result20 = testSubject0.objectRoleTop();
        ObjectRoleExpression result21 = testSubject0.objectRoleBottom();
        DataRoleExpression result22 = testSubject0.dataRoleTop();
        DataRoleExpression result23 = testSubject0.dataRoleBottom();
        DataExpression result24 = testSubject0.dataTop();
        DataExpression result25 = testSubject0.dataBottom();
        testSubject0.setTopBottomRoles(mock(String.class), mock(String.class),
                mock(String.class), mock(String.class));
        boolean result26 = testSubject0.isEmptyRole(mock(DataRoleExpression.class));
        boolean result27 = testSubject0
                .isEmptyRole(mock(ObjectRoleComplexExpression.class));
        int result28 = testSubject0.nConcepts();
        int result29 = testSubject0.nIndividuals();
        int result30 = testSubject0.nORoles();
        int result31 = testSubject0.nDRoles();
        ConceptExpression result32 = testSubject0.oneOf(mock(List.class));
        ConceptExpression result33 = testSubject0.oneOf(mock(IndividualExpression.class));
        ConceptExpression result34 = testSubject0
                .selfReference(mock(ObjectRoleExpression.class));
        ConceptExpression result35 = testSubject0.forall(mock(DataRoleExpression.class),
                mock(DataExpression.class));
        ConceptExpression result36 = testSubject0.forall(
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        ConceptExpression result37 = testSubject0.minCardinality(1,
                mock(DataRoleExpression.class), mock(DataExpression.class));
        ConceptExpression result38 = testSubject0.minCardinality(1,
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        ConceptExpression result39 = testSubject0.maxCardinality(1,
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        ConceptExpression result40 = testSubject0.maxCardinality(1,
                mock(DataRoleExpression.class), mock(DataExpression.class));
        IndividualExpression result41 = testSubject0.individual(mock(String.class));
        ObjectRoleExpression result42 = testSubject0.objectRole(mock(String.class));
        ObjectRoleComplexExpression result43 = testSubject0.projectFrom(
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        ObjectRoleComplexExpression result44 = testSubject0.projectInto(
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        String result45 = testSubject0.getDataTop();
        DataExpression result46 = testSubject0.dataNot(mock(DataExpression.class));
        DataExpression result47 = testSubject0.dataAnd(mock(List.class));
        DataExpression result48 = testSubject0.dataOr(mock(List.class));
        DataExpression result49 = testSubject0.dataOneOf(mock(List.class));
        String result50 = testSubject0.toString();
    }

    @Test
    public void shouldTestExpressionTranslator() throws Exception {
        ExpressionTranslator testSubject0 = mock(ExpressionTranslator.class);
        Object result0 = testSubject0.visit(mock(ConceptTop.class));
        Object result1 = testSubject0.visit(mock(ConceptAnd.class));
        Object result2 = testSubject0.visit(mock(ConceptNot.class));
        Object result3 = testSubject0.visit(mock(ConceptName.class));
        Object result4 = testSubject0.visit(mock(ConceptBottom.class));
        DLTree result5 = testSubject0.visit(mock(ConceptTop.class));
        DLTree result6 = testSubject0.visit(mock(ConceptBottom.class));
        DLTree result7 = testSubject0.visit(mock(ConceptName.class));
        DLTree result8 = testSubject0.visit(mock(ConceptNot.class));
        DLTree result9 = testSubject0.visit(mock(ConceptAnd.class));
        DLTree result10 = testSubject0.visit(mock(ConceptOr.class));
        DLTree result11 = testSubject0.visit(mock(ConceptOneOf.class));
        DLTree result12 = testSubject0.visit(mock(ConceptObjectSelf.class));
        DLTree result13 = testSubject0.visit(mock(ConceptObjectValue.class));
        DLTree result14 = testSubject0.visit(mock(ConceptObjectExists.class));
        DLTree result15 = testSubject0.visit(mock(ConceptObjectForall.class));
        DLTree result16 = testSubject0.visit(mock(ConceptObjectMinCardinality.class));
        DLTree result17 = testSubject0.visit(mock(ConceptObjectMaxCardinality.class));
        DLTree result18 = testSubject0.visit(mock(ConceptObjectExactCardinality.class));
        DLTree result19 = testSubject0.visit(mock(ConceptDataValue.class));
        DLTree result20 = testSubject0.visit(mock(ConceptDataExists.class));
        DLTree result21 = testSubject0.visit(mock(ConceptDataForall.class));
        DLTree result22 = testSubject0.visit(mock(ConceptDataMinCardinality.class));
        DLTree result23 = testSubject0.visit(mock(ConceptDataMaxCardinality.class));
        DLTree result24 = testSubject0.visit(mock(ConceptDataExactCardinality.class));
        DLTree result25 = testSubject0.visit(mock(IndividualName.class));
        DLTree result26 = testSubject0.visit(mock(ObjectRoleTop.class));
        DLTree result27 = testSubject0.visit(mock(ObjectRoleBottom.class));
        DLTree result28 = testSubject0.visit(mock(ObjectRoleName.class));
        DLTree result29 = testSubject0.visit(mock(ObjectRoleInverse.class));
        DLTree result30 = testSubject0.visit(mock(ObjectRoleChain.class));
        DLTree result31 = testSubject0.visit(mock(ObjectRoleProjectionFrom.class));
        DLTree result32 = testSubject0.visit(mock(ObjectRoleProjectionInto.class));
        DLTree result33 = testSubject0.visit(mock(DataRoleTop.class));
        DLTree result34 = testSubject0.visit(mock(DataRoleBottom.class));
        DLTree result35 = testSubject0.visit(mock(DataRoleName.class));
        DLTree result36 = testSubject0.visit(mock(DataTop.class));
        DLTree result37 = testSubject0.visit(mock(DataBottom.class));
        DLTree result38 = testSubject0.visit(mock(Datatype.class));
        DLTree result39 = testSubject0.visit(mock(Literal.class));
        DLTree result40 = testSubject0.visit(mock(DataNot.class));
        DLTree result41 = testSubject0.visit(mock(DataAnd.class));
        DLTree result42 = testSubject0.visit(mock(DataOr.class));
        DLTree result43 = testSubject0.visit(mock(DataOneOf.class));
        Object result44 = testSubject0.visit(mock(DataOneOf.class));
        Object result45 = testSubject0.visit(mock(DataOr.class));
        Object result46 = testSubject0.visit(mock(DataAnd.class));
        Object result47 = testSubject0.visit(mock(DataNot.class));
        Object result48 = testSubject0.visit(mock(Literal.class));
        Object result49 = testSubject0.visit(mock(Datatype.class));
        Object result50 = testSubject0.visit(mock(DataBottom.class));
        Object result51 = testSubject0.visit(mock(DataTop.class));
        Object result52 = testSubject0.visit(mock(DataRoleName.class));
        Object result53 = testSubject0.visit(mock(DataRoleBottom.class));
        Object result54 = testSubject0.visit(mock(DataRoleTop.class));
        Object result55 = testSubject0.visit(mock(ObjectRoleProjectionInto.class));
        Object result56 = testSubject0.visit(mock(ObjectRoleProjectionFrom.class));
        Object result57 = testSubject0.visit(mock(ObjectRoleChain.class));
        Object result58 = testSubject0.visit(mock(ObjectRoleInverse.class));
        Object result59 = testSubject0.visit(mock(ObjectRoleName.class));
        Object result60 = testSubject0.visit(mock(ObjectRoleBottom.class));
        Object result61 = testSubject0.visit(mock(ObjectRoleTop.class));
        Object result62 = testSubject0.visit(mock(IndividualName.class));
        Object result63 = testSubject0.visit(mock(ConceptDataExactCardinality.class));
        Object result64 = testSubject0.visit(mock(ConceptDataMaxCardinality.class));
        Object result65 = testSubject0.visit(mock(ConceptDataMinCardinality.class));
        Object result66 = testSubject0.visit(mock(ConceptDataForall.class));
        Object result67 = testSubject0.visit(mock(ConceptDataExists.class));
        Object result68 = testSubject0.visit(mock(ConceptDataValue.class));
        Object result69 = testSubject0.visit(mock(ConceptObjectExactCardinality.class));
        Object result70 = testSubject0.visit(mock(ConceptObjectMaxCardinality.class));
        Object result71 = testSubject0.visit(mock(ConceptObjectMinCardinality.class));
        Object result72 = testSubject0.visit(mock(ConceptObjectForall.class));
        Object result73 = testSubject0.visit(mock(ConceptObjectExists.class));
        Object result74 = testSubject0.visit(mock(ConceptObjectValue.class));
        Object result75 = testSubject0.visit(mock(ConceptObjectSelf.class));
        Object result76 = testSubject0.visit(mock(ConceptOneOf.class));
        Object result77 = testSubject0.visit(mock(ConceptOr.class));
        String result78 = testSubject0.toString();
    }

    @Test
    public void shouldTestInAx() throws Exception {
        InAx testSubject0 = new InAx();
        String result0 = testSubject0.toString();
    }

    @Test
    public void shouldTestIndividual() throws Exception {
        Individual testSubject0 = new Individual(mock(String.class));
        DlCompletionTree result0 = testSubject0.getNode();
        testSubject0.initToldSubsumers();
        testSubject0.addRelated(mock(Individual.class));
        testSubject0.addRelated(mock(Related.class));
        boolean result1 = testSubject0.hasRelatedCache(mock(Role.class));
        List result2 = testSubject0.getRelatedCache(mock(Role.class));
        testSubject0.setRelatedCache(mock(Role.class), mock(List.class));
        testSubject0.setNode(mock(DlCompletionTree.class));
        List result3 = testSubject0.getRelatedIndex();
        String result4 = testSubject0.toString();
        boolean result5 = testSubject0.isPrimitive();
        testSubject0.push(mock(LinkedList.class), mock(DLTree.class));
        boolean result6 = testSubject0.isSingleton();
        boolean result7 = testSubject0.isRelevant(1L);
        testSubject0.setRelevant(1L);
        testSubject0.setpName(1);
        DLTree result8 = testSubject0.getDescription();
        DLTree result9 = testSubject0.makeNonPrimitive(mock(DLTree.class));
        testSubject0.addDesc(mock(DLTree.class));
        testSubject0.removeSelfFromDescription();
        boolean result10 = testSubject0.canInitNonPrim(mock(DLTree.class));
        boolean result11 = testSubject0.initToldSubsumers(mock(DLTree.class),
                mock(Set.class));
        int result12 = testSubject0.getpName();
        int result13 = testSubject0.resolveId();
        testSubject0.setToldTop(mock(Concept.class));
        int result14 = testSubject0.calculateTSDepth();
        testSubject0.addExtraRule(1);
        CTTag result15 = testSubject0.getClassTag();
        int result16 = testSubject0.getpBody();
        LogicFeatures result17 = testSubject0.getPosFeatures();
        LogicFeatures result18 = testSubject0.getNegFeatures();
        testSubject0.setNominal(true);
        testSubject0.setpBody(1);
        testSubject0.setPrimitive(true);
        Concept result19 = Concept.getTOP();
        Concept result20 = Concept.getBOTTOM();
        Concept result21 = Concept.getTEMP();
        CTTag result22 = testSubject0.getClassTagPlain();
        boolean result23 = testSubject0.isNominal();
        int result24 = testSubject0.getTsDepth();
        testSubject0.addLeaves(mock(Collection.class));
        testSubject0.dropRelevant(1L);
        boolean result25 = testSubject0.isHasSP();
        testSubject0.removeDescription();
        testSubject0.setHasSP(true);
        boolean result26 = testSubject0.hasExtraRules();
        FastSet result27 = testSubject0.getExtraRules();
        testSubject0.searchTSbyRoleAndSupers(mock(Role.class), mock(Set.class));
        testSubject0.setSingleton(true);
        int result28 = testSubject0.getIndex();
        testSubject0.setIndex(1);
        boolean result29 = testSubject0.isSynonym();
        ClassifiableEntry result30 = ClassifiableEntry
                .resolveSynonym(mock(ClassifiableEntry.class));
        testSubject0.setCompletelyDefined(true);
        testSubject0.addParent(mock(ClassifiableEntry.class));
        testSubject0.removeSynonymsFromParents();
        testSubject0.setSynonym(mock(ClassifiableEntry.class));
        Collection result31 = testSubject0.getToldSubsumers();
        testSubject0.addParents(mock(Collection.class));
        ClassifiableEntry result32 = testSubject0.getSynonym();
        boolean result33 = testSubject0.isClassified();
        TaxonomyVertex result34 = testSubject0.getTaxVertex();
        testSubject0.setTaxVertex(mock(TaxonomyVertex.class));
        boolean result35 = testSubject0.isCompletelyDefined();
        testSubject0.clearCompletelyDefined();
        boolean result36 = testSubject0.isNonClassifiable();
        testSubject0.setNonClassifiable(true);
        testSubject0.setNonClassifiable();
        testSubject0.clearNonClassifiable();
        boolean result37 = testSubject0.hasToldSubsumers();
        testSubject0.addParentIfNew(mock(ClassifiableEntry.class));
        testSubject0.canonicaliseSynonym();
        String result38 = testSubject0.getName();
        int result39 = testSubject0.getId();
        boolean result40 = testSubject0.isBottom();
        boolean result41 = testSubject0.isTop();
        testSubject0.setId(1);
        boolean result42 = testSubject0.isSystem();
        testSubject0.setSystem();
        testSubject0.setTop();
        testSubject0.setBottom();
        NamedEntity result43 = testSubject0.getEntity();
        testSubject0.setEntity(mock(NamedEntity.class));
    }

    @Test
    public void shouldTestKBFlags() throws Exception {
        KBFlags testSubject0 = new KBFlags();
        boolean result0 = testSubject0.isReflexive();
        testSubject0.setReflexive(true);
        boolean result1 = testSubject0.isGCI();
        testSubject0.setGCI(true);
        testSubject0.setRnD();
        boolean result2 = testSubject0.isRnD();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestLexeme() throws Exception {
        Lexeme testSubject0 = new Lexeme(mock(Token.class), mock(NamedEntry.class));
        Lexeme testSubject1 = new Lexeme(mock(Token.class));
        Lexeme testSubject2 = new Lexeme(mock(Token.class), 1);
        Lexeme testSubject3 = new Lexeme(mock(Lexeme.class));
        String result0 = testSubject0.toString();
        int result1 = testSubject0.getData();
        Token result2 = testSubject0.getToken();
        NamedEntry result3 = testSubject0.getNE();
    }

    @Test
    public void shouldTestLogicFeatures() throws Exception {
        LogicFeatures testSubject0 = new LogicFeatures(mock(LogicFeatures.class));
        LogicFeatures testSubject1 = new LogicFeatures();
        boolean result0 = testSubject0.isEmpty();
        testSubject0.or(mock(LogicFeatures.class));
        LogicFeatures result1 = LogicFeatures.plus(mock(LogicFeatures.class),
                mock(LogicFeatures.class));
        boolean result2 = testSubject0.hasSingletons();
        testSubject0.fillConceptData(mock(Concept.class));
        testSubject0.fillRoleData(mock(Role.class), true);
        testSubject0.fillDAGData(mock(DLVertex.class), true);
        testSubject0.mergeRoles();
        boolean result3 = testSubject0.hasInverseRole();
        boolean result4 = testSubject0.hasFunctionalRestriction();
        boolean result5 = testSubject0.hasNumberRestriction();
        boolean result6 = testSubject0.hasQNumberRestriction();
        boolean result7 = testSubject0.hasSomeAll();
        testSubject0.setInverseRoles();
        boolean result8 = testSubject0.hasTopRole();
        testSubject0.writeState(mock(LogAdapter.class));
        boolean result9 = testSubject0.hasSelfRef();
        String result10 = testSubject0.toString();
    }

    @Test
    public void shouldTestMergableLabel() throws Exception {
        MergableLabel testSubject0 = new MergableLabel();
        MergableLabel result0 = testSubject0.resolve();
        testSubject0.merge(mock(MergableLabel.class));
        boolean result1 = testSubject0.isSample();
        String result2 = testSubject0.toString();
    }

    @Test
    public void shouldTestInterfaceNameCreator() throws Exception {
        NameCreator testSubject0 = mock(NameCreator.class);
        Object result0 = testSubject0.makeEntry(mock(String.class));
    }

    @Test
    public void shouldTestNamedEntry() throws Exception {
        NamedEntry testSubject0 = mock(NamedEntry.class);
        String result0 = testSubject0.toString();
        String result1 = testSubject0.getName();
        int result2 = testSubject0.getId();
        int result3 = testSubject0.getIndex();
        testSubject0.setIndex(1);
        boolean result4 = testSubject0.isBottom();
        boolean result5 = testSubject0.isTop();
        testSubject0.setId(1);
        boolean result6 = testSubject0.isSystem();
        testSubject0.setSystem();
        testSubject0.setTop();
        testSubject0.setBottom();
        NamedEntity result7 = testSubject0.getEntity();
        testSubject0.setEntity(mock(NamedEntity.class));
    }

    @Test
    public void shouldTestNamedEntryCollection() throws Exception {
        NamedEntryCollection testSubject0 = new NamedEntryCollection(mock(String.class),
                mock(NameCreator.class), mock(JFactReasonerConfiguration.class));
        NamedEntry result0 = testSubject0.get(mock(String.class));
        int result1 = testSubject0.size();
        boolean result2 = testSubject0.remove(mock(NamedEntry.class));
        boolean result3 = testSubject0.isRegistered(mock(String.class));
        boolean result4 = testSubject0.isLocked();
        List result5 = testSubject0.getList();
        boolean result6 = testSubject0.setLocked(true);
        testSubject0.registerNew(mock(NamedEntry.class));
        NamedEntry result7 = testSubject0.registerElem(mock(NamedEntry.class));
        String result8 = testSubject0.toString();
    }

    @Test
    public void shouldTestNameSet() throws Exception {
        NameSet testSubject0 = new NameSet(mock(NameCreator.class));
        Object result0 = testSubject0.add(mock(String.class));
        Object result1 = testSubject0.insert(mock(String.class));
        Object result2 = testSubject0.get(mock(Object.class));
        Object result3 = testSubject0.put(mock(Object.class), mock(Object.class));
        Collection result4 = testSubject0.values();
        Object result5 = testSubject0.clone();
        testSubject0.clear();
        boolean result6 = testSubject0.isEmpty();
        int result7 = testSubject0.size();
        Set result8 = testSubject0.entrySet();
        testSubject0.putAll(mock(Map.class));
        Object result9 = testSubject0.remove(mock(Object.class));
        Set result10 = testSubject0.keySet();
        boolean result11 = testSubject0.containsValue(mock(Object.class));
        boolean result12 = testSubject0.containsKey(mock(Object.class));
        String result13 = testSubject0.toString();
    }

    @Test
    public void shouldTestNominalReasoner() throws Exception {
        NominalReasoner testSubject0 = new NominalReasoner(mock(TBox.class),
                mock(JFactReasonerConfiguration.class), mock(DatatypeFactory.class));
        boolean result0 = testSubject0.consistentNominalCloud();
        boolean result1 = testSubject0.hasNominals();
        JFactReasonerConfiguration result2 = testSubject0.getOptions();
        testSubject0.setBlockingMethod(true, true);
        DLDag result3 = testSubject0.getDAG();
        testSubject0.repeatUnblockedNode(mock(DlCompletionTree.class), true);
        boolean result4 = testSubject0.checkIrreflexivity(mock(Role.class));
        boolean result5 = testSubject0.runSat(1, 1);
        ModelCacheInterface result6 = testSubject0.buildCacheByCGraph(true);
        boolean result7 = testSubject0.checkDisjointRoles(mock(Role.class),
                mock(Role.class));
        DlCompletionTree result8 = testSubject0.getRootNode();
        testSubject0.writeTotalStatistic(mock(LogAdapter.class));
        float result9 = testSubject0.printReasoningTime(mock(LogAdapter.class));
        testSubject0.initToDoPriorities();
        ModelCacheInterface result10 = testSubject0.createCache(1, mock(FastSet.class));
        boolean result11 = testSubject0.setupEdge(mock(DlCompletionTreeArc.class),
                mock(DepSet.class), 1);
        boolean result12 = testSubject0.findConcept(mock(CWDArray.class), 1);
        String result13 = testSubject0.toString();
    }

    @Test
    public void shouldTestOntology() throws Exception {
        Ontology testSubject0 = new Ontology();
        Axiom result0 = testSubject0.add(mock(Axiom.class));
        Axiom result1 = testSubject0.get(1);
        testSubject0.clear();
        int result2 = testSubject0.size();
        Object result3 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setProcessed();
        List result4 = testSubject0.getAxioms();
        boolean result5 = testSubject0.isChanged();
        testSubject0.retract(mock(Axiom.class));
        ExpressionManager result6 = testSubject0.getExpressionManager();
        String result7 = testSubject0.toString();
    }

    @Test
    public void shouldTestOntologyLoader() throws Exception {
        OntologyLoader testSubject0 = new OntologyLoader(mock(TBox.class));
        testSubject0.visit(mock(AxiomORoleDomain.class));
        testSubject0.visit(mock(AxiomConceptInclusion.class));
        testSubject0.visit(mock(AxiomInstanceOf.class));
        testSubject0.visit(mock(AxiomRelatedTo.class));
        testSubject0.visit(mock(AxiomRelatedToNot.class));
        testSubject0.visit(mock(AxiomValueOf.class));
        testSubject0.visit(mock(AxiomValueOfNot.class));
        testSubject0.visit(mock(AxiomDeclaration.class));
        testSubject0.visit(mock(AxiomEquivalentConcepts.class));
        testSubject0.visit(mock(AxiomDisjointConcepts.class));
        testSubject0.visit(mock(AxiomEquivalentORoles.class));
        testSubject0.visit(mock(AxiomEquivalentDRoles.class));
        testSubject0.visit(mock(AxiomDisjointORoles.class));
        testSubject0.visit(mock(AxiomDisjointDRoles.class));
        testSubject0.visit(mock(AxiomDisjointUnion.class));
        testSubject0.visit(mock(AxiomSameIndividuals.class));
        testSubject0.visit(mock(AxiomDifferentIndividuals.class));
        testSubject0.visit(mock(AxiomFairnessConstraint.class));
        testSubject0.visit(mock(AxiomRoleInverse.class));
        testSubject0.visit(mock(AxiomORoleSubsumption.class));
        testSubject0.visit(mock(AxiomDRoleSubsumption.class));
        testSubject0.visit(mock(AxiomDRoleDomain.class));
        testSubject0.visit(mock(AxiomORoleRange.class));
        testSubject0.visit(mock(AxiomDRoleRange.class));
        testSubject0.visit(mock(AxiomRoleTransitive.class));
        testSubject0.visit(mock(AxiomRoleReflexive.class));
        testSubject0.visit(mock(AxiomRoleIrreflexive.class));
        testSubject0.visit(mock(AxiomRoleSymmetric.class));
        testSubject0.visit(mock(AxiomRoleAsymmetric.class));
        testSubject0.visit(mock(AxiomORoleFunctional.class));
        testSubject0.visit(mock(AxiomDRoleFunctional.class));
        testSubject0.visit(mock(AxiomRoleInverseFunctional.class));
        testSubject0.visitOntology(mock(Ontology.class));
        Individual result0 = testSubject0.getIndividual(mock(IndividualExpression.class),
                mock(String.class));
        testSubject0.ensureNames(mock(Expression.class));
        String result1 = testSubject0.toString();
    }

    @Test
    public void shouldTestRAStateTransitions() throws Exception {
        RAStateTransitions testSubject0 = new RAStateTransitions();
        testSubject0.add(mock(RATransition.class));
        boolean result0 = testSubject0.isEmpty();
        testSubject0.setup(1, 1, true);
        testSubject0.print(mock(LogAdapter.class));
        boolean result1 = testSubject0.isSingleton();
        List result2 = testSubject0.begin();
        boolean result3 = testSubject0.hasEmptyTransition();
        boolean result4 = testSubject0.recognise(mock(Role.class));
        int result5 = testSubject0.getTransitionEnd();
        boolean result6 = testSubject0.addToExisting(mock(RATransition.class));
        String result7 = testSubject0.toString();
    }

    @Test
    public void shouldTestRATransition() throws Exception {
        RATransition testSubject0 = new RATransition(1, mock(Role.class));
        RATransition testSubject1 = new RATransition(1);
        testSubject0.add(mock(RATransition.class));
        boolean result0 = testSubject0.isEmpty();
        testSubject0.print(mock(LogAdapter.class), 1);
        Collection result1 = testSubject0.begin();
        boolean result2 = testSubject0.applicable(mock(Role.class));
        int result3 = testSubject0.final_state();
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestReasonerFreshEntityException() throws Exception {
        ReasonerFreshEntityException testSubject0 = new ReasonerFreshEntityException(
                mock(String.class));
        ReasonerFreshEntityException testSubject1 = new ReasonerFreshEntityException(
                mock(String.class), mock(String.class));
        ReasonerFreshEntityException testSubject2 = new ReasonerFreshEntityException(
                mock(String.class), mock(Throwable.class), mock(String.class));
        ReasonerFreshEntityException testSubject3 = new ReasonerFreshEntityException(
                mock(Throwable.class), mock(String.class));
        String result0 = testSubject0.getIri();
        testSubject0.printStackTrace(mock(PrintStream.class));
        testSubject0.printStackTrace();
        testSubject0.printStackTrace(mock(PrintWriter.class));
        Throwable result1 = testSubject0.fillInStackTrace();
        Throwable result2 = testSubject0.getCause();
        Throwable result3 = testSubject0.initCause(mock(Throwable.class));
        String result4 = testSubject0.toString();
        String result5 = testSubject0.getMessage();
        String result6 = testSubject0.getLocalizedMessage();
        StackTraceElement[] result7 = testSubject0.getStackTrace();
        testSubject0.setStackTrace(mock(StackTraceElement[].class));
    }

    @Test
    public void shouldTestReasoningKernel() throws Exception {
        ReasoningKernel testSubject0 = new ReasoningKernel(
                mock(JFactReasonerConfiguration.class), mock(DatatypeFactory.class));
        boolean result0 = testSubject0.isInstance(mock(IndividualExpression.class),
                mock(ConceptExpression.class));
        Axiom result1 = testSubject0.valueOf(mock(OWLAxiom.class),
                mock(IndividualExpression.class), mock(DataRoleExpression.class),
                mock(Literal.class));
        testSubject0.getInstances(mock(ConceptExpression.class), mock(Actor.class));
        testSubject0.getInstances(mock(ConceptExpression.class), mock(Actor.class), true);
        boolean result2 = testSubject0
                .isInverseFunctional(mock(ObjectRoleExpression.class));
        boolean result3 = testSubject0.isSymmetric(mock(ObjectRoleExpression.class));
        boolean result4 = testSubject0.isAsymmetric(mock(ObjectRoleExpression.class));
        boolean result5 = testSubject0.isReflexive(mock(ObjectRoleExpression.class));
        boolean result6 = testSubject0.isIrreflexive(mock(ObjectRoleExpression.class));
        boolean result7 = testSubject0.isTransitive(mock(ObjectRoleExpression.class));
        boolean result8 = testSubject0.isFunctional(mock(DataRoleExpression.class));
        boolean result9 = testSubject0.isFunctional(mock(ObjectRoleExpression.class));
        Axiom result10 = testSubject0.setTransitive(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class));
        Axiom result11 = testSubject0.setSymmetric(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class));
        Axiom result12 = testSubject0.setAsymmetric(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class));
        Axiom result13 = testSubject0.setReflexive(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class));
        Axiom result14 = testSubject0.setIrreflexive(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class));
        boolean result15 = testSubject0.isDisjoint(mock(ConceptExpression.class),
                mock(ConceptExpression.class));
        testSubject0.retract(mock(Axiom.class));
        ExpressionManager result16 = testSubject0.getExpressionManager();
        boolean result17 = testSubject0.isSatisfiable(mock(ConceptExpression.class));
        testSubject0.getTypes(mock(IndividualExpression.class), true, mock(Actor.class));
        testSubject0.setTopBottomRoleNames(mock(String.class), mock(String.class),
                mock(String.class), mock(String.class));
        testSubject0.setInterruptedSwitch(mock(AtomicBoolean.class));
        boolean result18 = testSubject0.clearKB();
        testSubject0.getEquivalentConcepts(mock(ConceptExpression.class),
                mock(Actor.class));
        boolean result19 = testSubject0.isKBRealised();
        testSubject0.realiseKB();
        boolean result20 = testSubject0.isKBConsistent();
        List result21 = testSubject0.getTrace();
        testSubject0.needTracing();
        testSubject0.getSubConcepts(mock(ConceptExpression.class), true,
                mock(Actor.class));
        testSubject0
                .getDisjointConcepts(mock(ConceptExpression.class), mock(Actor.class));
        testSubject0.getSubRoles(mock(RoleExpression.class), true, mock(Actor.class));
        testSubject0.getSupRoles(mock(RoleExpression.class), true, mock(Actor.class));
        testSubject0.getEquivalentRoles(mock(RoleExpression.class), mock(Actor.class));
        List result22 = testSubject0.getRoleFillers(mock(IndividualExpression.class),
                mock(ObjectRoleExpression.class));
        testSubject0.getSameAs(mock(IndividualExpression.class), mock(Actor.class));
        testSubject0.getSupConcepts(mock(ConceptExpression.class), true,
                mock(Actor.class));
        testSubject0.writeReasoningResult(mock(LogAdapter.class), 1L);
        DlCompletionTree result23 = testSubject0
                .buildCompletionTree(mock(ConceptExpression.class));
        Set result24 = testSubject0.getObjectRoles(mock(DlCompletionTree.class), true,
                true);
        Set result25 = testSubject0.getDataRoles(mock(DlCompletionTree.class), true);
        List result26 = testSubject0.getNeighbours(mock(DlCompletionTree.class),
                mock(RoleExpression.class));
        List result27 = testSubject0.getObjectLabel(mock(DlCompletionTree.class), true);
        List result28 = testSubject0.getDataLabel(mock(DlCompletionTree.class), true);
        int result29 = testSubject0.getAtomicDecompositionSize(true,
                mock(ModuleType.class));
        Set result30 = testSubject0.getAtomAxioms(1);
        Set result31 = testSubject0.getAtomModule(1);
        Set result32 = testSubject0.getAtomDependents(1);
        List result33 = testSubject0.getModule(mock(List.class), true,
                mock(ModuleType.class));
        Set result34 = testSubject0.getNonLocal(mock(List.class), true,
                mock(ModuleType.class));
        Axiom result35 = testSubject0.setFairnessConstraint(mock(OWLAxiom.class),
                mock(List.class));
        boolean result36 = testSubject0.isSameIndividuals(
                mock(IndividualExpression.class), mock(IndividualExpression.class));
        boolean result37 = testSubject0.isDisjointRoles(mock(List.class));
        boolean result38 = testSubject0.isDisjointRoles(mock(ObjectRoleExpression.class),
                mock(ObjectRoleExpression.class));
        boolean result39 = testSubject0.isDisjointRoles(mock(DataRoleExpression.class),
                mock(DataRoleExpression.class));
        Axiom result40 = testSubject0.processDifferent(mock(OWLAxiom.class),
                mock(List.class));
        Axiom result41 = testSubject0.processSame(mock(OWLAxiom.class), mock(List.class));
        Axiom result42 = testSubject0.setInverseRoles(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class), mock(ObjectRoleExpression.class));
        boolean result43 = testSubject0.isRelated(mock(IndividualExpression.class),
                mock(ObjectRoleExpression.class), mock(IndividualExpression.class));
        testSubject0.setSignature(mock(TSignature.class));
        testSubject0.setIgnoreExprCache(true);
        Axiom result44 = testSubject0.disjointUnion(mock(OWLAxiom.class),
                mock(ConceptExpression.class), mock(List.class));
        TModularizer result45 = testSubject0.getModExtractor(true);
        boolean result46 = testSubject0.isKBClassified();
        boolean result47 = testSubject0.isKBPreprocessed();
        Axiom result48 = testSubject0.declare(mock(OWLAxiom.class),
                mock(Expression.class));
        Axiom result49 = testSubject0.impliesConcepts(mock(OWLAxiom.class),
                mock(ConceptExpression.class), mock(ConceptExpression.class));
        Axiom result50 = testSubject0.equalConcepts(mock(OWLAxiom.class),
                mock(List.class));
        Axiom result51 = testSubject0.disjointConcepts(mock(OWLAxiom.class),
                mock(List.class));
        Axiom result52 = testSubject0
                .impliesORoles(mock(OWLAxiom.class),
                        mock(ObjectRoleComplexExpression.class),
                        mock(ObjectRoleExpression.class));
        Axiom result53 = testSubject0.impliesDRoles(mock(OWLAxiom.class),
                mock(DataRoleExpression.class), mock(DataRoleExpression.class));
        Axiom result54 = testSubject0.equalORoles(mock(OWLAxiom.class), mock(List.class));
        Axiom result55 = testSubject0.equalDRoles(mock(OWLAxiom.class), mock(List.class));
        Axiom result56 = testSubject0.disjointORoles(mock(OWLAxiom.class),
                mock(List.class));
        Axiom result57 = testSubject0.disjointDRoles(mock(OWLAxiom.class),
                mock(List.class));
        Axiom result58 = testSubject0.setODomain(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        Axiom result59 = testSubject0.setDDomain(mock(OWLAxiom.class),
                mock(DataRoleExpression.class), mock(ConceptExpression.class));
        Axiom result60 = testSubject0.setORange(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        Axiom result61 = testSubject0.setDRange(mock(OWLAxiom.class),
                mock(DataRoleExpression.class), mock(DataExpression.class));
        Axiom result62 = testSubject0.setOFunctional(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class));
        Axiom result63 = testSubject0.setDFunctional(mock(OWLAxiom.class),
                mock(DataRoleExpression.class));
        Axiom result64 = testSubject0.setInverseFunctional(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class));
        Axiom result65 = testSubject0.instanceOf(mock(OWLAxiom.class),
                mock(IndividualExpression.class), mock(ConceptExpression.class));
        Axiom result66 = testSubject0.relatedTo(mock(OWLAxiom.class),
                mock(IndividualExpression.class), mock(ObjectRoleExpression.class),
                mock(IndividualExpression.class));
        Axiom result67 = testSubject0.relatedToNot(mock(OWLAxiom.class),
                mock(IndividualExpression.class), mock(ObjectRoleExpression.class),
                mock(IndividualExpression.class));
        Axiom result68 = testSubject0.valueOfNot(mock(OWLAxiom.class),
                mock(IndividualExpression.class), mock(DataRoleExpression.class),
                mock(Literal.class));
        testSubject0.classifyKB();
        boolean result69 = testSubject0
                .isSubRoles(mock(ObjectRoleComplexExpression.class),
                        mock(ObjectRoleExpression.class));
        boolean result70 = testSubject0.isSubRoles(mock(DataRoleExpression.class),
                mock(DataRoleExpression.class));
        boolean result71 = testSubject0.isSubsumedBy(mock(ConceptExpression.class),
                mock(ConceptExpression.class));
        boolean result72 = testSubject0.isEquivalent(mock(ConceptExpression.class),
                mock(ConceptExpression.class));
        testSubject0.getORoleDomain(mock(ObjectRoleExpression.class), true,
                mock(Actor.class));
        testSubject0.getRoleRange(mock(ObjectRoleExpression.class), true,
                mock(Actor.class));
        testSubject0.getDirectInstances(mock(ConceptExpression.class), mock(Actor.class));
        KnowledgeExplorer result73 = testSubject0.getKnowledgeExplorer();
        boolean result74 = testSubject0.isSubChain(
                mock(ObjectRoleComplexExpression.class), mock(List.class));
        String result77 = testSubject0.toString();
    }

    @Test
    public void shouldTestRelated() throws Exception {
        Related testSubject0 = new Related(mock(Individual.class),
                mock(Individual.class), mock(Role.class));
        Role result0 = testSubject0.getRole();
        testSubject0.simplify();
        Individual result1 = testSubject0.getA();
        Individual result2 = testSubject0.getB();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestRestorer() throws Exception {
        Restorer testSubject0 = mock(Restorer.class);
        testSubject0.restore();
        int result0 = testSubject0.getRaresavestackLevel();
        testSubject0.setRaresavestackLevel(1);
        String result1 = testSubject0.toString();
    }

    @Test
    public void shouldTestRole() throws Exception {
        Role testSubject0 = mock(Role.class);
        String result0 = testSubject0.toString();
        testSubject0.print(mock(LogAdapter.class));
        boolean result1 = testSubject0.isSymmetric();
        boolean result2 = testSubject0.isAsymmetric();
        boolean result3 = testSubject0.isReflexive();
        boolean result4 = testSubject0.isIrreflexive();
        boolean result5 = testSubject0.isTransitive();
        boolean result6 = testSubject0.isFunctional();
        List result7 = testSubject0.getAncestor();
        Role result8 = Role.resolveRole(mock(DLTree.class));
        boolean result9 = testSubject0.isDataRole();
        Role result10 = testSubject0.inverse();
        testSubject0.setId(1);
        boolean result11 = testSubject0.isSimple();
        testSubject0.completeAutomaton(1);
        MergableLabel result12 = testSubject0.getRangeLabel();
        Role result13 = testSubject0.realInverse();
        testSubject0.setInverse(mock(Role.class));
        DLTree result15 = testSubject0.getTSpecialDomain();
        boolean result16 = testSubject0.hasSpecialDomain();
        testSubject0.initSpecialDomain();
        testSubject0.setSpecialDomain(1);
        testSubject0.setDataRole();
        testSubject0.setDataRole(true);
        testSubject0.clearDataRole();
        boolean result17 = testSubject0.isFunctionalityKnown();
        testSubject0.setFunctional();
        testSubject0.setFunctional(1);
        testSubject0.setFunctional(true);
        boolean result18 = testSubject0.isTransitivityKnown();
        testSubject0.setTransitive(true);
        testSubject0.setTransitive();
        boolean result19 = testSubject0.isSymmetryKnown();
        testSubject0.setSymmetric();
        testSubject0.setSymmetric(true);
        boolean result20 = testSubject0.isAsymmetryKnown();
        testSubject0.setAsymmetric(true);
        boolean result21 = testSubject0.isReflexivityKnown();
        testSubject0.setReflexive(true);
        testSubject0.setReflexive();
        boolean result22 = testSubject0.isIrreflexivityKnown();
        testSubject0.setIrreflexive(true);
        testSubject0.setIrreflexive();
        boolean result23 = testSubject0.isTopFunc();
        int result24 = testSubject0.getFunctional();
        boolean result25 = testSubject0.isRelevant(1L);
        testSubject0.setRelevant(1L);
        MergableLabel result26 = testSubject0.getDomainLabel();
        testSubject0.setDomain(mock(DLTree.class));
        testSubject0.setRange(mock(DLTree.class));
        DLTree result27 = testSubject0.getTDomain();
        testSubject0.collectDomainFromSupers();
        testSubject0.setBPDomain(1);
        int result28 = testSubject0.getBPDomain();
        int result29 = testSubject0.getBPRange();
        testSubject0.addDisjointRole(mock(Role.class));
        testSubject0.checkHierarchicalDisjoint();
        boolean result30 = testSubject0.isDisjoint(mock(Role.class));
        boolean result31 = testSubject0.isDisjoint();
        int result32 = testSubject0.getAbsoluteIndex();
        boolean result33 = testSubject0.lesserequal(mock(Role.class));
        List result34 = testSubject0.begin_topfunc();
        testSubject0.addComposition(mock(DLTree.class));
        RoleAutomaton result35 = testSubject0.getAutomaton();
        Role result36 = testSubject0.eliminateToldCycles();
        testSubject0.consistent();
        testSubject0.addFeaturesToSynonym();
        testSubject0.initADbyTaxonomy(mock(Taxonomy.class), 1);
        testSubject0.postProcess();
        Role result37 = testSubject0.getInverse();
        int result38 = testSubject0.getIndex();
        testSubject0.setIndex(1);
        boolean result39 = testSubject0.isSynonym();
        ClassifiableEntry result40 = ClassifiableEntry
                .resolveSynonym(mock(ClassifiableEntry.class));
        testSubject0.setCompletelyDefined(true);
        testSubject0.addParent(mock(ClassifiableEntry.class));
        testSubject0.removeSynonymsFromParents();
        testSubject0.setSynonym(mock(ClassifiableEntry.class));
        Collection result41 = testSubject0.getToldSubsumers();
        testSubject0.addParents(mock(Collection.class));
        ClassifiableEntry result42 = testSubject0.getSynonym();
        boolean result43 = testSubject0.isClassified();
        TaxonomyVertex result44 = testSubject0.getTaxVertex();
        testSubject0.setTaxVertex(mock(TaxonomyVertex.class));
        boolean result45 = testSubject0.isCompletelyDefined();
        testSubject0.clearCompletelyDefined();
        boolean result46 = testSubject0.isNonClassifiable();
        testSubject0.setNonClassifiable(true);
        testSubject0.setNonClassifiable();
        testSubject0.clearNonClassifiable();
        boolean result47 = testSubject0.hasToldSubsumers();
        testSubject0.addParentIfNew(mock(ClassifiableEntry.class));
        testSubject0.canonicaliseSynonym();
        String result48 = testSubject0.getName();
        int result49 = testSubject0.getId();
        boolean result50 = testSubject0.isBottom();
        boolean result51 = testSubject0.isTop();
        boolean result52 = testSubject0.isSystem();
        testSubject0.setSystem();
        testSubject0.setTop();
        testSubject0.setBottom();
        NamedEntity result53 = testSubject0.getEntity();
        testSubject0.setEntity(mock(NamedEntity.class));
    }

    @Test
    public void shouldTestRoleAutomaton() throws Exception {
        RoleAutomaton testSubject0 = new RoleAutomaton();
        int result0 = testSubject0.size();
        testSubject0.setup(1, true);
        testSubject0.print(mock(LogAdapter.class));
        testSubject0.addRA(mock(RoleAutomaton.class));
        testSubject0.addRA(mock(RoleAutomaton.class));
        testSubject0.addTransitionSafe(1, mock(RATransition.class));
        boolean result1 = testSubject0.isISafe();
        boolean result2 = testSubject0.isOSafe();
        testSubject0.initChain(1);
        boolean result3 = testSubject0.addToChain(mock(RoleAutomaton.class), true, 1);
        boolean result4 = testSubject0.addToChain(mock(RoleAutomaton.class), true);
        RAStateTransitions result5 = testSubject0.begin(1);
        List result6 = testSubject0.getBase();
        int result7 = testSubject0.newState();
        testSubject0.addTransition(1, mock(RATransition.class));
        testSubject0.setIUnsafe();
        testSubject0.setOUnsafe();
        testSubject0.checkTransition(1, 1);
        testSubject0.nextChainTransition(1);
        testSubject0.addCopy(mock(RoleAutomaton.class));
        testSubject0.initMap(1, 1);
        String result8 = testSubject0.toString();
    }

    @Test
    public void shouldTestRoleMaster() throws Exception {
        RoleMaster testSubject0 = new RoleMaster(true, mock(String.class),
                mock(String.class), mock(JFactReasonerConfiguration.class));
        int result0 = testSubject0.size();
        testSubject0.print(mock(LogAdapter.class), mock(String.class));
        List result1 = testSubject0.getRoles();
        testSubject0.setUndefinedNames(true);
        Taxonomy result2 = testSubject0.getTaxonomy();
        boolean result3 = testSubject0.hasReflexiveRoles();
        testSubject0.addDisjointRoles(mock(Role.class), mock(Role.class));
        testSubject0.addRoleSynonym(mock(Role.class), mock(Role.class));
        testSubject0.initAncDesc();
        testSubject0.fillReflexiveRoles(mock(List.class));
        NamedEntry result4 = testSubject0.ensureRoleName(mock(String.class));
        testSubject0.addRoleParent(mock(DLTree.class), mock(Role.class));
        String result5 = testSubject0.toString();
    }

    @Test
    public void shouldTestTaxonomy() throws Exception {
        Taxonomy testSubject0 = new Taxonomy(mock(ClassifiableEntry.class),
                mock(ClassifiableEntry.class), mock(JFactReasonerConfiguration.class));
        String result0 = testSubject0.toString();
        testSubject0.setCompletelyDefined(true);
        testSubject0.getRelativesInfo(mock(TaxonomyVertex.class), mock(Actor.class),
                true, true, true);
        boolean result1 = testSubject0.getRelativesInfo(mock(TaxonomyVertex.class),
                mock(SupConceptActor.class), true, true, true);
        testSubject0.finalise();
        testSubject0.classifyEntry(mock(ClassifiableEntry.class));
        TaxonomyVertex result2 = testSubject0.getTopVertex();
        TaxonomyVertex result3 = testSubject0.getBottomVertex();
        testSubject0.addCurrentToSynonym(mock(TaxonomyVertex.class));
        boolean result4 = testSubject0.queryMode();
    }

    @Test
    public void shouldTestTaxonomyVertex() throws Exception {
        TaxonomyVertex testSubject0 = new TaxonomyVertex();
        TaxonomyVertex testSubject1 = new TaxonomyVertex(mock(ClassifiableEntry.class));
        String result0 = testSubject0.toString();
        testSubject0.clear();
        boolean result1 = testSubject0.getValue();
        testSubject0.addNeighbour(true, mock(TaxonomyVertex.class));
        boolean result2 = testSubject0.isValued(1L);
        testSubject0.setHostVertex(mock(ClassifiableEntry.class));
        testSubject0.setSample(mock(ClassifiableEntry.class), true);
        List result3 = testSubject0.neigh(true);
        boolean result4 = testSubject0.isChecked(1L);
        testSubject0.setChecked(1L);
        boolean result5 = testSubject0.setValued(true, 1L);
        boolean result6 = testSubject0.isCommon();
        testSubject0.setCommon();
        testSubject0.clearCommon();
        boolean result7 = testSubject0.correctCommon(1);
        Set result8 = testSubject0.begin_syn();
        testSubject0.addSynonym(mock(ClassifiableEntry.class));
        ClassifiableEntry result9 = testSubject0.getPrimer();
        boolean result10 = testSubject0.noNeighbours(true);
        TaxonomyVertex result11 = testSubject0.getSynonymNode();
        testSubject0.removeLastLink(true);
        testSubject0.clearLinks(true);
        boolean result12 = testSubject0.removeLink(true, mock(TaxonomyVertex.class));
        testSubject0.incorporate(mock(JFactReasonerConfiguration.class));
        String result13 = testSubject0.printSynonyms();
        String result14 = testSubject0.printNeighbours(true);
    }

    @Test
    public void shouldTestTBox() throws Exception {
        TBox testSubject0 = new TBox(mock(DatatypeFactory.class),
                mock(JFactReasonerConfiguration.class), mock(String.class),
                mock(String.class), mock(String.class), mock(String.class),
                mock(AtomicBoolean.class));
        testSubject0.print();
        boolean result0 = testSubject0.isConsistent();
        boolean result1 = testSubject0.isIrreflexive(mock(Role.class));
        Concept result2 = testSubject0.getConcept(mock(String.class));
        testSubject0.determineSorts();
        Individual result3 = testSubject0.getIndividual(mock(String.class));
        JFactReasonerConfiguration result4 = testSubject0.getOptions();
        boolean result5 = testSubject0.isSatisfiable(mock(Concept.class));
        testSubject0.writeReasoningResult(1L);
        DLTree result6 = testSubject0.getTree(mock(Concept.class));
        DLTree result7 = testSubject0.makeNonPrimitive(mock(Concept.class),
                mock(DLTree.class));
        Concept result8 = testSubject0.replaceForall(mock(DLTree.class));
        testSubject0.clearRelevanceInfo();
        Concept result9 = testSubject0.checkToldCycle(mock(Concept.class));
        testSubject0.addSubsumeAxiom(mock(Concept.class), mock(DLTree.class));
        testSubject0.addSubsumeAxiom(mock(DLTree.class), mock(DLTree.class));
        List result10 = testSubject0.i_begin();
        List result11 = testSubject0.c_begin();
        String result12 = testSubject0.getDataEntryByBP(1);
        boolean result13 = testSubject0.initNonPrimitive(mock(Concept.class),
                mock(DLTree.class));
        testSubject0.checkEarlySynonym(mock(Concept.class));
        Concept result14 = testSubject0.getCI(mock(DLTree.class));
        testSubject0.initToldSubsumers();
        testSubject0.processDisjoint(mock(List.class));
        int result15 = testSubject0.reflexive2dag(mock(Role.class));
        int result16 = testSubject0.dataForall2dag(mock(Role.class), 1);
        int result17 = testSubject0.dataAtMost2dag(1, mock(Role.class), 1);
        int result18 = testSubject0.concept2dag(mock(Concept.class));
        testSubject0.addConceptToHeap(mock(Concept.class));
        testSubject0.processGCI(mock(DLTree.class), mock(DLTree.class));
        testSubject0.absorbAxioms();
        int result19 = testSubject0.countSynonyms();
        testSubject0.setToldTop();
        testSubject0.calculateTSDepth();
        testSubject0.initRuleFields(mock(List.class), 1);
        testSubject0.fillsClassificationTag();
        testSubject0.setConceptIndex(mock(Concept.class));
        DlSatTester result20 = testSubject0.getReasoner();
        testSubject0.printConcepts(mock(LogAdapter.class));
        testSubject0.printConcept(mock(LogAdapter.class), mock(Concept.class));
        testSubject0.printIndividuals(mock(LogAdapter.class));
        testSubject0.printSimpleRules(mock(LogAdapter.class));
        testSubject0.printAxioms(mock(LogAdapter.class));
        testSubject0.printDagEntry(mock(LogAdapter.class), 1);
        boolean result21 = testSubject0.isIRinQuery();
        boolean result22 = testSubject0.isNRinQuery();
        testSubject0.clearFeatures();
        DLTree result23 = testSubject0.getFreshConcept();
        RoleMaster result24 = testSubject0.getORM();
        RoleMaster result25 = testSubject0.getDRM();
        RoleMaster result26 = testSubject0.getRM(mock(Role.class));
        DLDag result27 = testSubject0.getDag();
        boolean result28 = testSubject0.isIndividual(mock(DLTree.class));
        boolean result29 = testSubject0.setForbidUndefinedNames(true);
        testSubject0.registerIndividualRelation(mock(NamedEntry.class),
                mock(NamedEntry.class), mock(NamedEntry.class));
        testSubject0.finishLoading();
        boolean result30 = testSubject0.hasFC();
        testSubject0.setFairnessConstraintDLTrees(mock(List.class));
        int result31 = testSubject0.getTG();
        boolean result33 = testSubject0.testHasNominals();
        boolean result34 = testSubject0.canUseSortedReasoning();
        testSubject0.performClassification();
        testSubject0.createTaxonomy(true);
        testSubject0.performRealisation();
        DLConceptTaxonomy result35 = testSubject0.getTaxonomy();
        testSubject0.setConsistency(true);
        testSubject0.prepareReasoning();
        boolean result37 = testSubject0.performConsistencyCheck();
        boolean result38 = testSubject0.testSortedNonSubsumption(mock(Concept.class),
                mock(Concept.class));
        testSubject0.buildDAG();
        int result39 = testSubject0.tree2dag(mock(DLTree.class));
        testSubject0.initRangeDomain(mock(RoleMaster.class));
        int result40 = testSubject0.atmost2dag(1, mock(Role.class), 1);
        int result41 = testSubject0.addDataExprToHeap(mock(LiteralEntry.class));
        int result42 = testSubject0.addDataExprToHeap(mock(DatatypeEntry.class));
        int result43 = testSubject0.addDatatypeExpressionToHeap(mock(Datatype.class));
        int result44 = testSubject0.and2dag(mock(DLVertex.class), mock(DLTree.class));
        int result45 = testSubject0.forall2dag(mock(Role.class), 1);
        int result46 = testSubject0.fillArrays(mock(List.class));
        int result47 = testSubject0.getNItems();
        testSubject0.classifyConcepts(mock(List.class), true, mock(String.class));
        boolean result48 = testSubject0.isBlockedInd(mock(Concept.class));
        Individual result49 = testSubject0.getBlockingInd(mock(Concept.class));
        testSubject0.preprocess();
        testSubject0.initReasoner();
        testSubject0.prepareFeatures(mock(Concept.class), mock(Concept.class));
        testSubject0.buildSimpleCache();
        boolean result50 = testSubject0.isSubHolds(mock(Concept.class),
                mock(Concept.class));
        boolean result51 = testSubject0.isSameIndividuals(mock(Individual.class),
                mock(Individual.class));
        boolean result52 = testSubject0.isDisjointRoles(mock(Role.class),
                mock(Role.class));
        Concept result53 = testSubject0.createQueryConcept(mock(DLTree.class));
        ModelCacheInterface result54 = testSubject0.initCache(mock(Concept.class), true);
        testSubject0.classifyQueryConcept();
        DLTree result55 = testSubject0.applyAxiomCToCN(mock(DLTree.class),
                mock(DLTree.class));
        DLTree result56 = testSubject0.applyAxiomCNToC(mock(DLTree.class),
                mock(DLTree.class));
        boolean result57 = testSubject0.axiomToRangeDomain(mock(DLTree.class),
                mock(DLTree.class));
        testSubject0.addSubsumeForDefined(mock(Concept.class), mock(DLTree.class));
        boolean result58 = testSubject0.addNonprimitiveDefinition(mock(DLTree.class),
                mock(DLTree.class));
        boolean result59 = testSubject0.switchToNonprimitive(mock(DLTree.class),
                mock(DLTree.class));
        testSubject0.processDisjointC(mock(Collection.class));
        testSubject0.processEquivalentC(mock(List.class));
        testSubject0.processDifferent(mock(List.class));
        testSubject0.processSame(mock(List.class));
        testSubject0.processDisjointR(mock(List.class));
        testSubject0.processEquivalentR(mock(List.class));
        testSubject0.preprocessRelated();
        testSubject0.transformToldCycles();
        testSubject0.transformSingletonHierarchy();
        testSubject0.printFeatures();
        testSubject0.calculateStatistic();
        testSubject0.removeExtraDescriptions();
        Individual result60 = testSubject0.getSPForConcept(mock(Concept.class));
        testSubject0.setToDoPriorities();
        boolean result61 = testSubject0.isBlockingDet(mock(Concept.class));
        ModelCacheState result62 = testSubject0.testCachedNonSubsumption(
                mock(Concept.class), mock(Concept.class));
        List result63 = testSubject0.getDifferent();
        List result64 = testSubject0.getRelatedI();
        DLDag result65 = testSubject0.getDLHeap();
        KBFlags result66 = testSubject0.getGCIs();
        AtomicBoolean result67 = testSubject0.isCancelled();
        List result68 = testSubject0.getFairness();
        String result69 = testSubject0.toString();
    }

    @Test
    public void shouldTestTDag2Interface() throws Exception {
        TDag2Interface testSubject0 = new TDag2Interface(mock(DLDag.class),
                mock(ExpressionManager.class));
        RoleExpression result0 = testSubject0.getDataRoleExpression(mock(Role.class));
        Expression result1 = testSubject0.getExpr(1, true);
        testSubject0.ensureDagSize();
        ConceptExpression result2 = testSubject0.getCExpr(1);
        RoleExpression result3 = testSubject0.getObjectRoleExpression(mock(Role.class));
        ConceptExpression result4 = testSubject0.buildCExpr(mock(DLVertex.class));
        DataExpression result5 = testSubject0.getDExpr(1);
        DataExpression result6 = testSubject0.buildDExpr(mock(DLVertex.class));
        String result8 = testSubject0.toString();
    }

    @Test
    public void shouldTestToDoList() throws Exception {
        ToDoList testSubject0 = new ToDoList();
        String result0 = testSubject0.toString();
        testSubject0.clear();
        boolean result1 = testSubject0.isEmpty();
        testSubject0.save();
        testSubject0.addEntry(mock(DlCompletionTree.class), mock(DagTag.class),
                mock(ConceptWDep.class));
        ToDoEntry result3 = testSubject0.getNextEntry();
        testSubject0.restore(1);
        testSubject0.initPriorities(mock(String.class));
        boolean result4 = testSubject0.isSaveStateGenerationStarted();
        testSubject0.startSaveStateGeneration();
    }

    @Test
    public void shouldTestToken() throws Exception {
        Token testSubject0 = Token.AND;
        Token[] result0 = Token.values();
        Token result1 = Token.valueOf(mock(String.class));
        String result2 = testSubject0.getName();
        String result3 = testSubject0.name();
        String result4 = testSubject0.toString();
        Enum result7 = Enum.valueOf(mock(Class.class), mock(String.class));
        Class result8 = testSubject0.getDeclaringClass();
        int result9 = testSubject0.ordinal();
    }
}
