package jfact.unittest;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.*;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.split.*;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact_split {
    @Test
    public void shouldTestAOStructure() throws Exception {
        AOStructure testSubject0 = new AOStructure();
        TOntologyAtom result0 = testSubject0.get(mock(int.class));
        int result1 = testSubject0.size();
        List result2 = testSubject0.begin();
        TOntologyAtom result3 = testSubject0.newAtom();
        testSubject0.reduceGraph();
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestAtomicDecomposer() throws Exception {
        AtomicDecomposer testSubject0 = new AtomicDecomposer(mock(TModularizer.class));
        AOStructure result0 = testSubject0.getAOS(mock(Ontology.class),
                mock(ModuleType.class));
        AOStructure result1 = testSubject0.getAOS();
        String result2 = testSubject0.toString();
    }

    @Test
    public void shouldTestBotEquivalenceEvaluator() throws Exception {
        BotEquivalenceEvaluator testSubject0 = null;
        testSubject0.visit(mock(DataRoleName.class));
        testSubject0.visit(mock(ObjectRoleInverse.class));
        testSubject0.visit(mock(ObjectRoleChain.class));
        testSubject0.visit(mock(DataRoleTop.class));
        testSubject0.visit(mock(DataRoleBottom.class));
        testSubject0.visit(mock(ConceptTop.class));
        testSubject0.visit(mock(ConceptBottom.class));
        testSubject0.visit(mock(ObjectRoleProjectionFrom.class));
        testSubject0.visit(mock(ObjectRoleProjectionInto.class));
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
        testSubject0.visit(mock(ObjectRoleTop.class));
        testSubject0.visit(mock(ObjectRoleBottom.class));
        testSubject0.visit(mock(ObjectRoleName.class));
        boolean result0 = testSubject0.topCLocal();
        boolean result1 = testSubject0.topRLocal();
        boolean result2 = testSubject0.isTopOrBuiltInDataType(mock(Expression.class));
        boolean result3 = testSubject0.isTopOrBuiltInInfDataType(mock(Expression.class));
        testSubject0.visit(mock(IndividualName.class));
        testSubject0.visit(mock(DataTop.class));
        testSubject0.visit(mock(DataBottom.class));
        testSubject0.visit(mock(Datatype.class));
        testSubject0.visit(mock(Literal.class));
        testSubject0.visit(mock(DataNot.class));
        testSubject0.visit(mock(DataAnd.class));
        testSubject0.visit(mock(DataOr.class));
        testSubject0.visit(mock(DataOneOf.class));
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestKnowledgeExplorer() throws Exception {
        KnowledgeExplorer testSubject0 = new KnowledgeExplorer(mock(TBox.class),
                mock(ExpressionManager.class));
        Set result0 = testSubject0.getObjectRoles(mock(DlCompletionTree.class),
                mock(boolean.class), mock(boolean.class));
        Set result1 = testSubject0.getDataRoles(mock(DlCompletionTree.class),
                mock(boolean.class));
        List result2 = testSubject0.getNeighbours(mock(DlCompletionTree.class),
                mock(Role.class));
        List result3 = testSubject0.getObjectLabel(mock(DlCompletionTree.class),
                mock(boolean.class));
        List result4 = testSubject0.getDataLabel(mock(DlCompletionTree.class),
                mock(boolean.class));
        String result5 = testSubject0.toString();
    }

    @Test
    public void shouldTestInterfaceLocalityChecker() throws Exception {
        LocalityChecker testSubject0 = mock(LocalityChecker.class);
        TSignature result0 = testSubject0.getSignature();
        boolean result1 = testSubject0.local(mock(Axiom.class));
        testSubject0.preprocessOntology(mock(Collection.class));
        testSubject0.setSignatureValue(mock(TSignature.class));
    }

    @Test
    public void shouldTestModuleType() throws Exception {
        ModuleType testSubject0 = null;
        ModuleType[] result0 = ModuleType.values();
        ModuleType result1 = ModuleType.valueOf(mock(String.class));
        String result2 = testSubject0.name();
        String result3 = testSubject0.toString();
        Enum result6 = Enum.valueOf(mock(Class.class), mock(String.class));
        Class result7 = testSubject0.getDeclaringClass();
        int result8 = testSubject0.ordinal();
    }

    @Test
    public void shouldTestSemanticLocalityChecker() throws Exception {
        SemanticLocalityChecker testSubject0 = new SemanticLocalityChecker(
                mock(ReasoningKernel.class), mock(TSignature.class));
        TSignature result0 = testSubject0.getSignature();
        testSubject0.visit(mock(AxiomValueOfNot.class));
        testSubject0.visit(mock(AxiomConceptInclusion.class));
        testSubject0.visit(mock(AxiomInstanceOf.class));
        testSubject0.visit(mock(AxiomRelatedTo.class));
        testSubject0.visit(mock(AxiomRelatedToNot.class));
        testSubject0.visit(mock(AxiomValueOf.class));
        testSubject0.visit(mock(AxiomDeclaration.class));
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
        boolean result1 = testSubject0.local(mock(Axiom.class));
        testSubject0.preprocessOntology(mock(Collection.class));
        testSubject0.setSignatureValue(mock(TSignature.class));
        String result2 = testSubject0.toString();
    }

    @Test
    public void shouldTestSigAccessor() throws Exception {
        SigAccessor testSubject0 = null;
        boolean result0 = testSubject0.topCLocal();
        boolean result1 = testSubject0.topRLocal();
        boolean result2 = testSubject0.isTopOrBuiltInDataType(mock(Expression.class));
        boolean result3 = testSubject0.isTopOrBuiltInInfDataType(mock(Expression.class));
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
        testSubject0.visit(mock(Datatype.class));
        testSubject0.visit(mock(Literal.class));
        testSubject0.visit(mock(DataNot.class));
        testSubject0.visit(mock(DataAnd.class));
        testSubject0.visit(mock(DataOr.class));
        testSubject0.visit(mock(DataOneOf.class));
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestSigIndex() throws Exception {
        SigIndex testSubject0 = new SigIndex(mock(LocalityChecker.class));
        Collection result0 = testSubject0.getAxioms(mock(NamedEntity.class));
        Set result1 = testSubject0.getNonLocal(mock(boolean.class));
        testSubject0.processRange(mock(Collection.class));
        testSubject0.registerAx(mock(Axiom.class));
        testSubject0.unregisterAx(mock(Axiom.class));
        testSubject0.processAx(mock(Axiom.class));
        String result2 = testSubject0.toString();
    }

    @Test
    public void shouldTestSyntacticLocalityChecker() throws Exception {
        SyntacticLocalityChecker testSubject0 = new SyntacticLocalityChecker(
                mock(TSignature.class));
        TSignature result0 = testSubject0.getSignature();
        testSubject0.visit(mock(AxiomRoleAsymmetric.class));
        testSubject0.visit(mock(AxiomRoleInverseFunctional.class));
        testSubject0.visit(mock(AxiomConceptInclusion.class));
        testSubject0.visit(mock(AxiomInstanceOf.class));
        testSubject0.visit(mock(AxiomRelatedTo.class));
        testSubject0.visit(mock(AxiomRelatedToNot.class));
        testSubject0.visit(mock(AxiomValueOf.class));
        testSubject0.visit(mock(AxiomValueOfNot.class));
        testSubject0.visit(mock(AxiomDeclaration.class));
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
        testSubject0.visit(mock(AxiomDRoleRange.class));
        testSubject0.visit(mock(AxiomRoleTransitive.class));
        testSubject0.visit(mock(AxiomRoleReflexive.class));
        testSubject0.visit(mock(AxiomRoleIrreflexive.class));
        testSubject0.visit(mock(AxiomRoleSymmetric.class));
        testSubject0.visit(mock(AxiomORoleFunctional.class));
        testSubject0.visit(mock(AxiomDRoleFunctional.class));
        testSubject0.visitOntology(mock(Ontology.class));
        boolean result1 = testSubject0.local(mock(Axiom.class));
        testSubject0.preprocessOntology(mock(Collection.class));
        testSubject0.setSignatureValue(mock(TSignature.class));
        boolean result2 = testSubject0.topCLocal();
        boolean result3 = testSubject0.topRLocal();
        boolean result4 = testSubject0.isTopOrBuiltInDataType(mock(Expression.class));
        boolean result5 = testSubject0.isTopOrBuiltInInfDataType(mock(Expression.class));
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
        testSubject0.visit(mock(Datatype.class));
        testSubject0.visit(mock(Literal.class));
        testSubject0.visit(mock(DataNot.class));
        testSubject0.visit(mock(DataAnd.class));
        testSubject0.visit(mock(DataOr.class));
        testSubject0.visit(mock(DataOneOf.class));
        String result6 = testSubject0.toString();
    }

    @Test
    public void shouldTestTAxiomSplitter() throws Exception {
        TAxiomSplitter testSubject0 = new TAxiomSplitter(
                new JFactReasonerConfiguration(), mock(Ontology.class));
        testSubject0.buildSplit();
        String result0 = testSubject0.toString();
    }

    @Test
    public void shouldTestTModularizer() throws Exception {
        TModularizer testSubject0 = new TModularizer(new JFactReasonerConfiguration(),
                mock(LocalityChecker.class));
        TSignature result0 = testSubject0.getSignature();
        List result1 = testSubject0.getModule();
        List result2 = testSubject0.extractModule(mock(List.class),
                mock(TSignature.class), mock(ModuleType.class));
        testSubject0.preprocessOntology(mock(Collection.class));
        testSubject0.setSigIndex(mock(SigIndex.class));
        testSubject0.extract(mock(Collection.class), mock(TSignature.class),
                mock(ModuleType.class));
        LocalityChecker result3 = testSubject0.getLocalityChecker();
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestTOntologyAtom() throws Exception {
        TOntologyAtom testSubject0 = new TOntologyAtom();
        int result0 = testSubject0.getId();
        testSubject0.setId(mock(int.class));
        testSubject0.addAxiom(mock(Axiom.class));
        Set result1 = testSubject0.getAtomAxioms();
        Set result2 = testSubject0.getModule();
        testSubject0.filterDep();
        testSubject0.buildAllDepAtoms(mock(Set.class));
        Set result3 = testSubject0.getAllDepAtoms(mock(Set.class));
        testSubject0.setModule(mock(Collection.class));
        testSubject0.addDepAtom(mock(TOntologyAtom.class));
        Set result4 = testSubject0.getDepAtoms();
        String result5 = testSubject0.toString();
    }

    @Test
    public void shouldTestTopEquivalenceEvaluator() throws Exception {
        TopEquivalenceEvaluator testSubject0 = null;
        testSubject0.visit(mock(ObjectRoleProjectionFrom.class));
        testSubject0.visit(mock(ObjectRoleProjectionInto.class));
        testSubject0.visit(mock(DataRoleName.class));
        testSubject0.visit(mock(ObjectRoleInverse.class));
        testSubject0.visit(mock(ObjectRoleChain.class));
        testSubject0.visit(mock(DataRoleTop.class));
        testSubject0.visit(mock(DataRoleBottom.class));
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
        testSubject0.visit(mock(ObjectRoleTop.class));
        testSubject0.visit(mock(ObjectRoleBottom.class));
        testSubject0.visit(mock(ObjectRoleName.class));
        boolean result0 = testSubject0.topCLocal();
        boolean result1 = testSubject0.topRLocal();
        boolean result2 = testSubject0.isTopOrBuiltInDataType(mock(Expression.class));
        boolean result3 = testSubject0.isTopOrBuiltInInfDataType(mock(Expression.class));
        testSubject0.visit(mock(IndividualName.class));
        testSubject0.visit(mock(DataTop.class));
        testSubject0.visit(mock(DataBottom.class));
        testSubject0.visit(mock(Datatype.class));
        testSubject0.visit(mock(Literal.class));
        testSubject0.visit(mock(DataNot.class));
        testSubject0.visit(mock(DataAnd.class));
        testSubject0.visit(mock(DataOr.class));
        testSubject0.visit(mock(DataOneOf.class));
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestTSignature() throws Exception {
        TSignature testSubject0 = new TSignature(mock(TSignature.class));
        TSignature testSubject1 = new TSignature();
        testSubject0.add(mock(NamedEntity.class));
        testSubject0.clear();
        boolean result0 = testSubject0.contains(mock(Expression.class));
        int result1 = testSubject0.size();
        testSubject0.remove(mock(NamedEntity.class));
        List result2 = testSubject0.intersect(mock(TSignature.class));
        Set result3 = testSubject0.begin();
        testSubject0.setLocality(mock(boolean.class));
        testSubject0.setLocality(mock(boolean.class), mock(boolean.class));
        boolean result4 = testSubject0.containsNamedEntity(mock(NamedEntity.class));
        boolean result5 = testSubject0.topCLocal();
        boolean result6 = testSubject0.topRLocal();
        String result7 = testSubject0.toString();
    }

    @Test
    public void shouldTestTSignatureUpdater() throws Exception {
        TSignatureUpdater testSubject0 = new TSignatureUpdater(mock(TSignature.class));
        testSubject0.visit(mock(AxiomDeclaration.class));
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
        testSubject0.visit(mock(AxiomDRoleRange.class));
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
        testSubject0.visit(mock(AxiomValueOfNot.class));
        testSubject0.visit(mock(AxiomValueOf.class));
        testSubject0.visitOntology(mock(Ontology.class));
        String result0 = testSubject0.toString();
    }

    @Test
    public void shouldTestTSplitRules() throws Exception {
        TSplitRules testSubject0 = new TSplitRules(new JFactReasonerConfiguration());
        NamedEntity result0 = testSubject0.getEntity(mock(int.class));
        testSubject0.createSplitRules(mock(TSplitVars.class));
        testSubject0.initEntityMap(mock(DLDag.class));
        testSubject0.ensureDagSize(mock(int.class));
        List result1 = testSubject0.getRules();
        String result2 = testSubject0.toString();
    }

    @Test
    public void shouldTestTSplitVar() throws Exception {
        TSplitVar testSubject0 = new TSplitVar();
        List result0 = testSubject0.getEntries();
        String result1 = testSubject0.toString();
    }

    @Test
    public void shouldTestTSplitVars() throws Exception {
        TSplitVars testSubject0 = new TSplitVars();
        boolean result0 = testSubject0.empty();
        List result1 = testSubject0.getEntries();
        String result2 = testSubject0.toString();
    }
}
