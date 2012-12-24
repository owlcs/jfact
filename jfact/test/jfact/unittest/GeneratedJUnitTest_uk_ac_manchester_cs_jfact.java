package jfact.unittest;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.knowledgeexploration.OWLKnowledgeExplorerReasoner.RootNode;
import org.semanticweb.owlapi.util.Version;

import uk.ac.manchester.cs.jfact.JFactFactory;
import uk.ac.manchester.cs.jfact.JFactReasoner;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.split.ModuleType;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact {
    @Test
    public void shouldTestJFactFactory() throws Exception {
        JFactFactory testSubject0 = new JFactFactory();
        OWLReasoner result0 = testSubject0.createReasoner(mock(OWLOntology.class));
        OWLReasoner result1 = testSubject0.createReasoner(mock(OWLOntology.class),
                mock(OWLReasonerConfiguration.class));
        String result2 = testSubject0.getReasonerName();
        OWLReasoner result3 = testSubject0
                .createNonBufferingReasoner(mock(OWLOntology.class));
        OWLReasoner result4 = testSubject0.createNonBufferingReasoner(
                mock(OWLOntology.class), mock(OWLReasonerConfiguration.class));
        String result5 = testSubject0.toString();
    }

    @Test
    public void shouldTestJFactReasoner() throws Exception {
        JFactReasoner testSubject0 = new JFactReasoner(mock(OWLOntology.class),
                mock(OWLReasonerConfiguration.class), mock(BufferingMode.class));
        JFactReasoner testSubject1 = new JFactReasoner(mock(OWLOntology.class),
                mock(JFactReasonerConfiguration.class), mock(BufferingMode.class));
        testSubject0.interrupt();
        testSubject0.flush();
        testSubject0.precomputeInferences(mock(InferenceType[].class));
        boolean result0 = testSubject0.isConsistent();
        Node result1 = testSubject0.getUnsatisfiableClasses();
        NodeSet result2 = testSubject0
                .getSubClasses(mock(OWLClassExpression.class), true);
        NodeSet result3 = testSubject0.getInstances(mock(OWLClassExpression.class), true);
        NodeSet result4 = testSubject0.getObjectPropertyValues(
                mock(OWLNamedIndividual.class), mock(OWLObjectPropertyExpression.class));
        String result5 = testSubject0.getReasonerName();
        long result6 = testSubject0.getTimeOut();
        FreshEntityPolicy result7 = testSubject0.getFreshEntityPolicy();
        IndividualNodeSetPolicy result8 = testSubject0.getIndividualNodeSetPolicy();
        RootNode result9 = testSubject0.getRoot(mock(OWLClassExpression.class));
        OWLOntology result10 = testSubject0.getRootOntology();
        testSubject0.ontologiesChanged(mock(List.class));
        Version result11 = testSubject0.getReasonerVersion();
        BufferingMode result12 = testSubject0.getBufferingMode();
        List result13 = testSubject0.getPendingChanges();
        Set result14 = testSubject0.getPendingAxiomAdditions();
        Set result15 = testSubject0.getPendingAxiomRemovals();
        boolean result16 = testSubject0.isPrecomputed(mock(InferenceType.class));
        Set result17 = testSubject0.getPrecomputableInferenceTypes();
        boolean result18 = testSubject0.isSatisfiable(mock(OWLClassExpression.class));
        boolean result19 = testSubject0.isEntailed(mock(OWLAxiom.class));
        boolean result20 = testSubject0.isEntailed(mock(Set.class));
        boolean result21 = testSubject0
                .isEntailmentCheckingSupported(mock(AxiomType.class));
        Node result22 = testSubject0.getTopClassNode();
        Node result23 = testSubject0.getBottomClassNode();
        NodeSet result24 = testSubject0.getSuperClasses(mock(OWLClassExpression.class),
                true);
        Node result25 = testSubject0.getEquivalentClasses(mock(OWLClassExpression.class));
        NodeSet result26 = testSubject0
                .getDisjointClasses(mock(OWLClassExpression.class));
        Node result27 = testSubject0.getTopObjectPropertyNode();
        Node result28 = testSubject0.getBottomObjectPropertyNode();
        NodeSet result29 = testSubject0.getSubObjectProperties(
                mock(OWLObjectPropertyExpression.class), true);
        NodeSet result30 = testSubject0.getSuperObjectProperties(
                mock(OWLObjectPropertyExpression.class), true);
        Node result31 = testSubject0
                .getEquivalentObjectProperties(mock(OWLObjectPropertyExpression.class));
        NodeSet result32 = testSubject0
                .getDisjointObjectProperties(mock(OWLObjectPropertyExpression.class));
        Node result33 = testSubject0
                .getInverseObjectProperties(mock(OWLObjectPropertyExpression.class));
        NodeSet result34 = testSubject0.getObjectPropertyDomains(
                mock(OWLObjectPropertyExpression.class), true);
        NodeSet result35 = testSubject0.getObjectPropertyRanges(
                mock(OWLObjectPropertyExpression.class), true);
        Node result36 = testSubject0.getTopDataPropertyNode();
        Node result37 = testSubject0.getBottomDataPropertyNode();
        NodeSet result38 = testSubject0.getSubDataProperties(mock(OWLDataProperty.class),
                true);
        NodeSet result39 = testSubject0.getSuperDataProperties(
                mock(OWLDataProperty.class), true);
        Node result40 = testSubject0
                .getEquivalentDataProperties(mock(OWLDataProperty.class));
        NodeSet result41 = testSubject0
                .getDisjointDataProperties(mock(OWLDataPropertyExpression.class));
        NodeSet result42 = testSubject0.getDataPropertyDomains(
                mock(OWLDataProperty.class), true);
        NodeSet result43 = testSubject0.getTypes(mock(OWLNamedIndividual.class), true);
        Set result44 = testSubject0.getDataPropertyValues(mock(OWLNamedIndividual.class),
                mock(OWLDataProperty.class));
        Node result45 = testSubject0.getSameIndividuals(mock(OWLNamedIndividual.class));
        NodeSet result46 = testSubject0
                .getDifferentIndividuals(mock(OWLNamedIndividual.class));
        testSubject0.dispose();
        DatatypeFactory result47 = testSubject0.getDatatypeFactory();
        Set result48 = testSubject0.getTrace(mock(OWLAxiom.class));
        testSubject0.dumpClassHierarchy(mock(LogAdapter.class), true);
        testSubject0.writeReasoningResult(mock(LogAdapter.class), 1L);
        Node result49 = testSubject0.getObjectNeighbours(mock(RootNode.class), true);
        Collection result50 = testSubject0.getObjectNeighbours(mock(RootNode.class),
                mock(OWLObjectProperty.class));
        Collection result51 = testSubject0.getDataNeighbours(mock(RootNode.class),
                mock(OWLDataProperty.class));
        Node result52 = testSubject0.getDataNeighbours(mock(RootNode.class), true);
        Node result53 = testSubject0.getObjectLabel(mock(RootNode.class), true);
        Node result54 = testSubject0.getDataLabel(mock(RootNode.class), true);
        int result55 = testSubject0.getAtomicDecompositionSize(true,
                mock(ModuleType.class));
        Set result56 = testSubject0.getAtomAxioms(1);
        Set result57 = testSubject0.getAtomModule(1);
        Set result58 = testSubject0.getAtomDependents(1);
        Set result59 = testSubject0.getModule(mock(Set.class), true,
                mock(ModuleType.class));
        Set result60 = testSubject0.getNonLocal(mock(Set.class), true,
                mock(ModuleType.class));
        String result61 = testSubject0.toString();
    }
}
