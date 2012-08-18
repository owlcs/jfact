package jfact.unittest;

import static org.mockito.Mockito.*;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.helpers.*;
import uk.ac.manchester.cs.jfact.helpers.Stats.AccumulatedStatistic;
import uk.ac.manchester.cs.jfact.kernel.*;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact_helpers {
    @Test
    public void shouldTestArrayIntMap() throws Exception {
        ArrayIntMap testSubject0 = new ArrayIntMap();
        int result0 = testSubject0.get(mock(int.class));
        testSubject0.put(mock(int.class), mock(int.class));
        String result1 = testSubject0.toString();
        int[] result2 = testSubject0.values();
        testSubject0.clear();
        boolean result3 = testSubject0.isEmpty();
        int result4 = testSubject0.size();
        testSubject0.remove(mock(int.class));
        int[] result5 = testSubject0.keySet();
        int result6 = testSubject0.keySet(mock(int.class));
        boolean result7 = testSubject0.containsValue(mock(int.class));
        boolean result8 = testSubject0.containsKey(mock(int.class));
        boolean result9 = testSubject0.containsAll(mock(ArrayIntMap.class));
        testSubject0.removeAt(mock(int.class));
    }

    @Test
    public void shouldTestDLTreeFactory() throws Exception {
        DLTreeFactory testSubject0 = new DLTreeFactory();
        DLTree result0 = DLTreeFactory.wrap(mock(NamedEntry.class));
        DLTree result1 = DLTreeFactory.createEntry(mock(Token.class),
                mock(NamedEntry.class));
        DLTree result2 = DLTreeFactory.createBottom();
        DLTree result3 = DLTreeFactory.createInverse(mock(DLTree.class));
        boolean result4 = DLTreeFactory.isTopRole(mock(DLTree.class));
        boolean result5 = DLTreeFactory.isBotRole(mock(DLTree.class));
        DLTree result6 = DLTreeFactory.createTop();
        DLTree result7 = DLTreeFactory.buildDisjAux(mock(List.class));
        DLTree result8 = DLTreeFactory.createSNFNot(mock(DLTree.class));
        DLTree result9 = DLTreeFactory.createSNFNot(mock(DLTree.class),
                mock(DLTree.class));
        DLTree result10 = DLTreeFactory.createSNFAnd(mock(Collection.class),
                mock(DLTree.class));
        DLTree result11 = DLTreeFactory.createSNFAnd(mock(DLTree.class),
                mock(DLTree.class));
        DLTree result12 = DLTreeFactory.createSNFAnd(mock(Collection.class));
        DLTree result13 = DLTreeFactory.createSNFExists(mock(DLTree.class),
                mock(DLTree.class));
        DLTree result14 = DLTreeFactory.createSNFForall(mock(DLTree.class),
                mock(DLTree.class));
        DLTree result15 = DLTreeFactory.createRole(mock(Role.class));
        DLTree result16 = DLTreeFactory.createSNFLE(mock(int.class), mock(DLTree.class),
                mock(DLTree.class));
        DLTree result17 = DLTreeFactory.createSNFSelf(mock(DLTree.class));
        DLTree result18 = DLTreeFactory.createSNFGE(mock(int.class), mock(DLTree.class),
                mock(DLTree.class));
        DLTree result19 = DLTreeFactory.createSNFOr(mock(Collection.class));
        DLTree result20 = DLTreeFactory.inverseComposition(mock(DLTree.class));
        NamedEntry result21 = DLTreeFactory.unwrap(mock(DLTree.class));
        DLTree result22 = DLTreeFactory.buildTree(mock(Lexeme.class), mock(DLTree.class));
        DLTree result23 = DLTreeFactory.buildTree(mock(Lexeme.class), mock(DLTree.class),
                mock(DLTree.class));
        DLTree result24 = DLTreeFactory.buildTree(mock(Lexeme.class));
        boolean result25 = DLTreeFactory.isFunctionalExpr(mock(DLTree.class),
                mock(NamedEntry.class));
        boolean result26 = DLTreeFactory.isSNF(mock(DLTree.class));
        boolean result27 = DLTreeFactory
                .isSubTree(mock(DLTree.class), mock(DLTree.class));
        boolean result28 = DLTreeFactory.isUniversalRole(mock(DLTree.class));
        boolean result29 = DLTreeFactory.replaceSynonymsFromTree(mock(DLTree.class));
        String result30 = testSubject0.toString();
    }

    @Test
    public void shouldTestDLVertex() throws Exception {
        DLVertex testSubject0 = new DLVertex(mock(DagTag.class));
        DLVertex testSubject1 = new DLVertex(mock(DagTag.class), mock(int.class),
                mock(Role.class), mock(int.class), mock(Role.class));
        String result0 = testSubject0.toString();
        int result1 = testSubject0.getState();
        DagTag result2 = testSubject0.getType();
        testSubject0.merge(mock(MergableLabel.class));
        testSubject0.setCache(mock(boolean.class), mock(ModelCacheInterface.class));
        boolean result3 = testSubject0.addChild(mock(int.class));
        MergableLabel result4 = testSubject0.getSort();
        int result5 = testSubject0.getConceptIndex();
        int result6 = testSubject0.getNumberLE();
        int result7 = testSubject0.getNumberGE();
        int[] result8 = testSubject0.begin();
        Role result9 = testSubject0.getRole();
        Role result10 = testSubject0.getProjRole();
        NamedEntry result11 = testSubject0.getConcept();
        testSubject0.setConcept(mock(NamedEntry.class));
        testSubject0.setChild(mock(int.class));
        int result12 = testSubject0.getAndToDagValue();
        testSubject0.sortEntry(mock(DLDag.class));
        testSubject0.updateStatValues(mock(int.class), mock(int.class), mock(int.class),
                mock(int.class), mock(boolean.class));
        testSubject0.updateStatValues(mock(DLVertex.class), mock(boolean.class),
                mock(boolean.class));
        testSubject0.incFreqValue(mock(boolean.class));
        int result13 = testSubject0.getStat(mock(int.class));
        int result14 = testSubject0.getDepth(mock(boolean.class));
        long result15 = testSubject0.getUsage(mock(boolean.class));
        boolean result16 = testSubject0.isVisited(mock(boolean.class));
        testSubject0.setProcessed(mock(boolean.class));
        testSubject0.setInCycle(mock(boolean.class));
        testSubject0.clearDFS();
        ModelCacheInterface result17 = testSubject0.getCache(mock(boolean.class));
        boolean result18 = testSubject0.isInCycle(mock(boolean.class));
        boolean result19 = testSubject0.isProcessed(mock(boolean.class));
        testSubject0.setVisited(mock(boolean.class));
    }

    @Test
    public void shouldTestELFAxiomChecker() throws Exception {
        ELFAxiomChecker testSubject0 = new ELFAxiomChecker();
        boolean result0 = testSubject0.value();
        testSubject0.visit(mock(AxiomEquivalentConcepts.class));
        testSubject0.visit(mock(AxiomDisjointConcepts.class));
        testSubject0.visit(mock(AxiomDisjointUnion.class));
        testSubject0.visit(mock(AxiomEquivalentORoles.class));
        testSubject0.visit(mock(AxiomEquivalentDRoles.class));
        testSubject0.visit(mock(AxiomDisjointORoles.class));
        testSubject0.visit(mock(AxiomDisjointDRoles.class));
        testSubject0.visit(mock(AxiomSameIndividuals.class));
        testSubject0.visit(mock(AxiomDifferentIndividuals.class));
        testSubject0.visit(mock(AxiomFairnessConstraint.class));
        testSubject0.visit(mock(AxiomRoleInverse.class));
        testSubject0.visit(mock(AxiomORoleSubsumption.class));
        testSubject0.visit(mock(AxiomDRoleSubsumption.class));
        testSubject0.visit(mock(AxiomORoleDomain.class));
        testSubject0.visit(mock(AxiomDRoleDomain.class));
        testSubject0.visit(mock(AxiomORoleRange.class));
        testSubject0.visit(mock(AxiomRoleTransitive.class));
        testSubject0.visit(mock(AxiomRoleReflexive.class));
        testSubject0.visit(mock(AxiomRoleIrreflexive.class));
        testSubject0.visit(mock(AxiomRoleSymmetric.class));
        testSubject0.visit(mock(AxiomRoleAsymmetric.class));
        testSubject0.visit(mock(AxiomORoleFunctional.class));
        testSubject0.visit(mock(AxiomDRoleFunctional.class));
        testSubject0.visit(mock(AxiomRoleInverseFunctional.class));
        testSubject0.visit(mock(AxiomConceptInclusion.class));
        testSubject0.visit(mock(AxiomInstanceOf.class));
        testSubject0.visit(mock(AxiomRelatedTo.class));
        testSubject0.visit(mock(AxiomRelatedToNot.class));
        testSubject0.visit(mock(AxiomValueOf.class));
        testSubject0.visit(mock(AxiomValueOfNot.class));
        testSubject0.visit(mock(AxiomDRoleRange.class));
        testSubject0.visit(mock(AxiomDeclaration.class));
        testSubject0.visitOntology(mock(Ontology.class));
        String result1 = testSubject0.toString();
    }

    @Test
    public void shouldTestELFExpressionChecker() throws Exception {
        ELFExpressionChecker testSubject0 = new ELFExpressionChecker() {};
        testSubject0.visit(mock(ConceptTop.class));
        testSubject0.visit(mock(ConceptBottom.class));
        testSubject0.visit(mock(ConceptName.class));
        testSubject0.visit(mock(ConceptNot.class));
        testSubject0.visit(mock(ConceptAnd.class));
        testSubject0.visit(mock(ConceptOr.class));
        testSubject0.visit(mock(ConceptOneOf.class));
        testSubject0.visit(mock(ConceptObjectSelf.class));
        testSubject0.visit(mock(ConceptObjectValue.class));
        testSubject0.visit(mock(ConceptObjectExists.class));
        testSubject0.visit(mock(ConceptObjectForall.class));
        testSubject0.visit(mock(ConceptObjectMinCardinality.class));
        testSubject0.visit(mock(ConceptObjectMaxCardinality.class));
        testSubject0.visit(mock(ConceptObjectExactCardinality.class));
        testSubject0.visit(mock(ConceptDataValue.class));
        testSubject0.visit(mock(ConceptDataExists.class));
        testSubject0.visit(mock(ConceptDataForall.class));
        testSubject0.visit(mock(ConceptDataMinCardinality.class));
        testSubject0.visit(mock(ConceptDataMaxCardinality.class));
        testSubject0.visit(mock(ConceptDataExactCardinality.class));
        testSubject0.visit(mock(IndividualName.class));
        testSubject0.visit(mock(ObjectRoleTop.class));
        testSubject0.visit(mock(ObjectRoleBottom.class));
        testSubject0.visit(mock(ObjectRoleName.class));
        testSubject0.visit(mock(ObjectRoleInverse.class));
        testSubject0.visit(mock(ObjectRoleChain.class));
        testSubject0.visit(mock(ObjectRoleProjectionFrom.class));
        testSubject0.visit(mock(ObjectRoleProjectionInto.class));
        testSubject0.visit(mock(DataRoleTop.class));
        testSubject0.visit(mock(DataRoleBottom.class));
        testSubject0.visit(mock(DataRoleName.class));
        testSubject0.visit(mock(DataTop.class));
        testSubject0.visit(mock(DataBottom.class));
        testSubject0.visit(mock(DataNot.class));
        testSubject0.visit(mock(DataAnd.class));
        testSubject0.visit(mock(DataOr.class));
        testSubject0.visit(mock(DataOneOf.class));
        testSubject0.visit(mock(Literal.class));
        testSubject0.visit(mock(Datatype.class));
        String result0 = testSubject0.toString();
    }

    @Test
    public void shouldTestELFNormalizer() throws Exception {
        ELFNormalizer testSubject0 = new ELFNormalizer(mock(ExpressionManager.class));
        testSubject0.visit(mock(AxiomORoleSubsumption.class));
        testSubject0.visit(mock(AxiomConceptInclusion.class));
        testSubject0.visit(mock(AxiomRoleTransitive.class));
        testSubject0.visit(mock(AxiomDisjointConcepts.class));
        testSubject0.visit(mock(AxiomEquivalentORoles.class));
        testSubject0.visit(mock(AxiomDisjointUnion.class));
        testSubject0.visit(mock(AxiomEquivalentConcepts.class));
        testSubject0.visitOntology(mock(Ontology.class));
        testSubject0.visit(mock(AxiomDeclaration.class));
        testSubject0.visit(mock(AxiomEquivalentDRoles.class));
        testSubject0.visit(mock(AxiomDisjointORoles.class));
        testSubject0.visit(mock(AxiomDisjointDRoles.class));
        testSubject0.visit(mock(AxiomSameIndividuals.class));
        testSubject0.visit(mock(AxiomDifferentIndividuals.class));
        testSubject0.visit(mock(AxiomFairnessConstraint.class));
        testSubject0.visit(mock(AxiomRoleInverse.class));
        testSubject0.visit(mock(AxiomDRoleSubsumption.class));
        testSubject0.visit(mock(AxiomORoleDomain.class));
        testSubject0.visit(mock(AxiomDRoleDomain.class));
        testSubject0.visit(mock(AxiomORoleRange.class));
        testSubject0.visit(mock(AxiomDRoleRange.class));
        testSubject0.visit(mock(AxiomRoleReflexive.class));
        testSubject0.visit(mock(AxiomRoleIrreflexive.class));
        testSubject0.visit(mock(AxiomRoleSymmetric.class));
        testSubject0.visit(mock(AxiomRoleAsymmetric.class));
        testSubject0.visit(mock(AxiomORoleFunctional.class));
        testSubject0.visit(mock(AxiomDRoleFunctional.class));
        testSubject0.visit(mock(AxiomRoleInverseFunctional.class));
        testSubject0.visit(mock(AxiomInstanceOf.class));
        testSubject0.visit(mock(AxiomRelatedTo.class));
        testSubject0.visit(mock(AxiomRelatedToNot.class));
        testSubject0.visit(mock(AxiomValueOf.class));
        testSubject0.visit(mock(AxiomValueOfNot.class));
        String result0 = testSubject0.toString();
    }

    @Test
    public void shouldTestELFReasoner() throws Exception {
        ELFReasoner testSubject0 = new ELFReasoner(
                mock(JFactReasonerConfiguration.class), mock(Ontology.class));
        testSubject0.classify();
        String result0 = testSubject0.toString();
    }

    @Test
    public void shouldTestInterfaceFastSet() throws Exception {
        FastSet testSubject0 = mock(FastSet.class);
        testSubject0.add(mock(int.class));
        int result0 = testSubject0.get(mock(int.class));
        testSubject0.clear();
        boolean result1 = testSubject0.isEmpty();
        boolean result2 = testSubject0.contains(mock(int.class));
        testSubject0.addAll(mock(FastSet.class));
        int result3 = testSubject0.size();
        testSubject0.remove(mock(int.class));
        testSubject0.removeAll(mock(int.class), mock(int.class));
        boolean result4 = testSubject0.containsAll(mock(FastSet.class));
        int[] result5 = testSubject0.toIntArray();
        boolean result6 = testSubject0.containsAny(mock(FastSet.class));
        testSubject0.removeAt(mock(int.class));
        boolean result7 = testSubject0.intersect(mock(FastSet.class));
        testSubject0.removeAllValues(mock(int[].class));
        testSubject0.completeSet(mock(int.class));
    }

    @Test
    public void shouldTestFastSetFactory() throws Exception {
        FastSetFactory testSubject0 = new FastSetFactory();
        FastSet result0 = FastSetFactory.create();
        String result1 = testSubject0.toString();
    }

    @Test
    public void shouldTestFastSetSimple() throws Exception {
        FastSetSimple testSubject0 = new FastSetSimple();
        FastSetSimple testSubject1 = new FastSetSimple(mock(FastSetSimple.class),
                mock(FastSetSimple.class));
        testSubject0.add(mock(int.class));
        int result0 = testSubject0.get(mock(int.class));
        String result1 = testSubject0.toString();
        testSubject0.clear();
        boolean result2 = testSubject0.isEmpty();
        boolean result3 = testSubject0.contains(mock(int.class));
        testSubject0.addAll(mock(FastSet.class));
        int result4 = testSubject0.size();
        testSubject0.remove(mock(int.class));
        testSubject0.removeAll(mock(int.class), mock(int.class));
        boolean result5 = testSubject0.containsAll(mock(FastSet.class));
        int[] result6 = testSubject0.toIntArray();
        boolean result7 = testSubject0.containsAny(mock(FastSet.class));
        testSubject0.removeAt(mock(int.class));
        boolean result8 = testSubject0.intersect(mock(FastSet.class));
        testSubject0.removeAllValues(mock(int[].class));
        testSubject0.completeSet(mock(int.class));
    }

    @Test
    public void shouldTestHelper() throws Exception {
        Helper testSubject0 = new Helper();
        Helper.resize(mock(List.class), mock(int.class));
        Helper.resize(mock(List.class), mock(int.class), mock(Object.class));
        boolean result0 = Helper.isValid(mock(int.class));
        int result1 = Helper.createBiPointer(mock(int.class), mock(boolean.class));
        boolean result2 = Helper.isCorrect(mock(int.class));
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestIntCache() throws Exception {
        IntCache testSubject0 = new IntCache();
        testSubject0.add(mock(int.class));
        testSubject0.delete(mock(int.class));
        testSubject0.hit(mock(int.class));
        testSubject0.miss(mock(int.class));
        boolean result0 = testSubject0.isContained(mock(int.class));
        boolean result1 = testSubject0.isNotContained(mock(int.class));
        testSubject0.resetContained();
        testSubject0.resetNotContained();
        String result2 = testSubject0.toString();
    }

    @Test
    public void shouldTestIntList() throws Exception {
        IntList testSubject0 = new IntList();
        testSubject0.add(mock(int.class));
        int result0 = testSubject0.get(mock(int.class));
        String result1 = testSubject0.toString();
        testSubject0.clear();
        boolean result2 = testSubject0.isEmpty();
        boolean result3 = testSubject0.contains(mock(int.class));
        testSubject0.addAll(mock(FastSet.class));
        int result4 = testSubject0.size();
        testSubject0.remove(mock(int.class));
        testSubject0.removeAll(mock(int.class), mock(int.class));
        boolean result5 = testSubject0.containsAll(mock(FastSet.class));
        int[] result6 = testSubject0.toIntArray();
        boolean result7 = testSubject0.containsAny(mock(FastSet.class));
        testSubject0.removeAt(mock(int.class));
        boolean result8 = testSubject0.intersect(mock(FastSet.class));
        testSubject0.removeAllValues(mock(int[].class));
        testSubject0.completeSet(mock(int.class));
    }

    @Test
    public void shouldTestIntMap() throws Exception {
        IntMap testSubject0 = new IntMap();
        Object result0 = testSubject0.get(mock(int.class));
        testSubject0.put(mock(int.class), mock(Object.class));
        String result1 = testSubject0.toString();
        testSubject0.clear();
        boolean result2 = testSubject0.isEmpty();
        int result3 = testSubject0.size();
        List result4 = testSubject0.entrySet();
        Object result5 = testSubject0.remove(mock(int.class));
        int[] result6 = testSubject0.keySet();
        boolean result7 = testSubject0.containsValue(mock(Object.class));
        boolean result8 = testSubject0.containsKey(mock(int.class));
        boolean result9 = testSubject0.containsAll(mock(IntMap.class));
        int result10 = testSubject0.index(mock(int.class));
    }

    @Test
    public void shouldTestIntSet() throws Exception {
        IntSet testSubject0 = new IntSet();
        testSubject0.add(mock(int.class));
        int result0 = testSubject0.get(mock(int.class));
        String result1 = testSubject0.toString();
        testSubject0.clear();
        boolean result2 = testSubject0.isEmpty();
        boolean result3 = testSubject0.contains(mock(int.class));
        testSubject0.addAll(mock(FastSet.class));
        int result4 = testSubject0.size();
        testSubject0.remove(mock(int.class));
        testSubject0.removeAll(mock(int.class), mock(int.class));
        boolean result5 = testSubject0.containsAll(mock(FastSet.class));
        int[] result6 = testSubject0.toIntArray();
        boolean result7 = testSubject0.containsAny(mock(FastSet.class));
        testSubject0.removeAt(mock(int.class));
        boolean result8 = testSubject0.intersect(mock(FastSet.class));
        testSubject0.removeAllValues(mock(int[].class));
        testSubject0.completeSet(mock(int.class));
    }

    @Test
    public void shouldTestInterfaceLogAdapter() throws Exception {
        LogAdapter testSubject0 = mock(LogAdapter.class);
        testSubject0.println();
        testSubject0.print(mock(int.class));
        testSubject0.print(mock(double.class));
        testSubject0.print(mock(float.class));
        testSubject0.print(mock(boolean.class));
        testSubject0.print(mock(byte.class));
        testSubject0.print(mock(char.class));
        testSubject0.print(mock(short.class));
        testSubject0.print(mock(String.class));
        testSubject0.print(mock(Object.class));
        testSubject0.print(mock(Object[].class));
        testSubject0.print(mock(Object.class), mock(Object.class));
        testSubject0.print(mock(Object.class), mock(Object.class), mock(Object.class));
        testSubject0.print(mock(Object.class), mock(Object.class), mock(Object.class),
                mock(Object.class));
        testSubject0.print(mock(Object.class), mock(Object.class), mock(Object.class),
                mock(Object.class), mock(Object.class));
        testSubject0.printTemplate(mock(Templates.class), mock(Object[].class));
    }

    @Test
    public void shouldTestPair() throws Exception {
        Pair testSubject0 = new Pair(mock(Object.class), mock(Object.class));
        String result0 = testSubject0.toString();
    }

    @Test
    public void shouldTestReference() throws Exception {
        Reference testSubject0 = new Reference();
        Reference testSubject1 = new Reference(mock(Object.class));
        String result0 = testSubject0.toString();
        testSubject0.delete();
        testSubject0.setReference(mock(Object.class));
        Object result1 = testSubject0.getReference();
    }

    @Test
    public void shouldTestSaveStack() throws Exception {
        SaveStack testSubject0 = new SaveStack();
        testSubject0.clear();
        boolean result0 = testSubject0.isEmpty();
        int result1 = testSubject0.size();
        testSubject0.push(mock(Object.class));
        Object result2 = testSubject0.pop();
        Object result3 = testSubject0.pop(mock(int.class));
        Object result4 = testSubject0.top();
        Object result5 = testSubject0.top(mock(int.class));
        String result6 = testSubject0.toString();
    }

    @Test
    public void shouldTestSortedIntList() throws Exception {
        SortedIntList testSubject0 = new SortedIntList();
        testSubject0.add(mock(int.class));
        int result0 = testSubject0.get(mock(int.class));
        testSubject0.clear();
        boolean result1 = testSubject0.isEmpty();
        boolean result2 = testSubject0.contains(mock(int.class));
        int result3 = testSubject0.size();
        testSubject0.remove(mock(int.class));
        int[] result4 = testSubject0.toIntArray();
        testSubject0.removeAt(mock(int.class));
        String result5 = testSubject0.toString();
    }

    @Test
    public void shouldTestStatIndex() throws Exception {
        StatIndex testSubject0 = StatIndex.valueOf(mock(String.class));
        StatIndex[] result0 = StatIndex.values();
        int result2 = testSubject0.getIndex(mock(boolean.class));
        StatIndex.updateStatValues(mock(int.class), mock(int.class), mock(int.class),
                mock(int.class), mock(boolean.class), mock(int[].class));
        StatIndex.updateStatValues(mock(DLVertex.class), mock(boolean.class),
                mock(boolean.class), mock(int[].class));
        StatIndex.incFreqValue(mock(boolean.class), mock(int[].class));
        int result3 = StatIndex.getDepth(mock(boolean.class), mock(int[].class));
        int result4 = StatIndex.choose(mock(char.class));
        String result5 = testSubject0.name();
        String result6 = testSubject0.toString();
        Enum result9 = Enum.valueOf(mock(Class.class), mock(String.class));
        Class result10 = testSubject0.getDeclaringClass();
        int result11 = testSubject0.ordinal();
    }

    @Test
    public void shouldTestStats() throws Exception {
        Stats testSubject0 = new Stats();
        AccumulatedStatistic result0 = testSubject0.build(mock(List.class));
        testSubject0.accumulate();
        testSubject0.logStatisticData(mock(LogAdapter.class), mock(boolean.class),
                mock(DlCompletionGraph.class), mock(JFactReasonerConfiguration.class));
        AccumulatedStatistic result1 = testSubject0.getnTacticCalls();
        AccumulatedStatistic result2 = testSubject0.getnUseless();
        AccumulatedStatistic result3 = testSubject0.getnIdCalls();
        AccumulatedStatistic result4 = testSubject0.getnSingletonCalls();
        AccumulatedStatistic result5 = testSubject0.getnOrCalls();
        AccumulatedStatistic result6 = testSubject0.getnOrBrCalls();
        AccumulatedStatistic result7 = testSubject0.getnAndCalls();
        AccumulatedStatistic result8 = testSubject0.getnSomeCalls();
        AccumulatedStatistic result9 = testSubject0.getnAllCalls();
        AccumulatedStatistic result10 = testSubject0.getnFuncCalls();
        AccumulatedStatistic result11 = testSubject0.getnLeCalls();
        AccumulatedStatistic result12 = testSubject0.getnGeCalls();
        AccumulatedStatistic result13 = testSubject0.getnNNCalls();
        AccumulatedStatistic result14 = testSubject0.getnMergeCalls();
        AccumulatedStatistic result15 = testSubject0.getnAutoEmptyLookups();
        AccumulatedStatistic result16 = testSubject0.getnAutoTransLookups();
        AccumulatedStatistic result17 = testSubject0.getnSRuleAdd();
        AccumulatedStatistic result18 = testSubject0.getnSRuleFire();
        AccumulatedStatistic result19 = testSubject0.getnStateSaves();
        AccumulatedStatistic result20 = testSubject0.getnStateRestores();
        AccumulatedStatistic result21 = testSubject0.getnNodeSaves();
        AccumulatedStatistic result22 = testSubject0.getnNodeRestores();
        AccumulatedStatistic result23 = testSubject0.getnLookups();
        AccumulatedStatistic result24 = testSubject0.getnFairnessViolations();
        AccumulatedStatistic result25 = testSubject0.getnCacheTry();
        AccumulatedStatistic result26 = testSubject0.getnCacheFailedNoCache();
        AccumulatedStatistic result27 = testSubject0.getnCacheFailedShallow();
        AccumulatedStatistic result28 = testSubject0.getnCacheFailed();
        AccumulatedStatistic result29 = testSubject0.getnCachedSat();
        AccumulatedStatistic result30 = testSubject0.getnCachedUnsat();
        String result31 = testSubject0.toString();
    }

    @Test
    public void shouldTestTemplates() throws Exception {
        Templates testSubject0 = Templates.valueOf(mock(String.class));
        String result2 = testSubject0.getTemplate();
        String result3 = testSubject0.name();
        String result4 = testSubject0.toString();
        Enum result7 = Enum.valueOf(mock(Class.class), mock(String.class));
        Class result8 = testSubject0.getDeclaringClass();
        int result9 = testSubject0.ordinal();
    }

    @Test
    public void shouldTestTimer() throws Exception {
        Timer testSubject0 = new Timer();
        String result0 = testSubject0.toString();
        testSubject0.start();
        testSubject0.stop();
        testSubject0.reset();
        long result1 = testSubject0.calcDelta();
        long result2 = testSubject0.getResultTime();
    }

    @Test
    public void shouldTestUnreachableSituationException() throws Exception {
        UnreachableSituationException testSubject0 = new UnreachableSituationException();
        UnreachableSituationException testSubject1 = new UnreachableSituationException(
                mock(String.class));
        UnreachableSituationException testSubject2 = new UnreachableSituationException(
                mock(String.class), mock(Throwable.class));
        testSubject0.printStackTrace(mock(PrintStream.class));
        testSubject0.printStackTrace();
        testSubject0.printStackTrace(mock(PrintWriter.class));
        Throwable result0 = testSubject0.fillInStackTrace();
        Throwable result1 = testSubject0.getCause();
        Throwable result2 = testSubject0.initCause(mock(Throwable.class));
        String result3 = testSubject0.toString();
        String result4 = testSubject0.getMessage();
        String result5 = testSubject0.getLocalizedMessage();
        StackTraceElement[] result6 = testSubject0.getStackTrace();
        testSubject0.setStackTrace(mock(StackTraceElement[].class));
    }
}
