package jfact.unittest;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact_kernel_dl_axioms {
    @Test
    public void shouldTestAxiomConceptInclusion() throws Exception {
        AxiomConceptInclusion testSubject0 = new AxiomConceptInclusion(
                mock(OWLAxiom.class), mock(ConceptExpression.class),
                mock(ConceptExpression.class));
        String result0 = testSubject0.toString();
        int result1 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result2 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        ConceptExpression result3 = testSubject0.getSubConcept();
        ConceptExpression result4 = testSubject0.getSupConcept();
        boolean result5 = testSubject0.isUsed();
        TOntologyAtom result6 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result7 = testSubject0.isInModule();
        OWLAxiom result8 = testSubject0.getOWLAxiom();
        boolean result9 = testSubject0.isInSS();
        TSignature result10 = testSubject0.getSignature();
    }

    @Test
    public void shouldTestAxiomDeclaration() throws Exception {
        AxiomDeclaration testSubject0 = new AxiomDeclaration(mock(OWLAxiom.class),
                mock(Expression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        Expression result2 = testSubject0.getDeclaration();
        boolean result3 = testSubject0.isUsed();
        TOntologyAtom result4 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result5 = testSubject0.isInModule();
        OWLAxiom result6 = testSubject0.getOWLAxiom();
        boolean result7 = testSubject0.isInSS();
        TSignature result8 = testSubject0.getSignature();
        String result9 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomDifferentIndividuals() throws Exception {
        AxiomDifferentIndividuals testSubject0 = new AxiomDifferentIndividuals(
                mock(OWLAxiom.class), mock(List.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        boolean result0 = testSubject0.isEmpty();
        int result1 = testSubject0.size();
        Expression result2 = testSubject0.transform(mock(Expression.class));
        IndividualExpression result3 = testSubject0.transform(mock(Expression.class));
        int result4 = testSubject0.getId();
        Object result5 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        List result6 = testSubject0.getArguments();
        boolean result7 = testSubject0.isUsed();
        TOntologyAtom result8 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result9 = testSubject0.isInModule();
        OWLAxiom result10 = testSubject0.getOWLAxiom();
        boolean result11 = testSubject0.isInSS();
        TSignature result12 = testSubject0.getSignature();
        String result13 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomDisjointConcepts() throws Exception {
        AxiomDisjointConcepts testSubject0 = new AxiomDisjointConcepts(
                mock(OWLAxiom.class), mock(List.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        boolean result0 = testSubject0.isEmpty();
        int result1 = testSubject0.size();
        Expression result2 = testSubject0.transform(mock(Expression.class));
        ConceptExpression result3 = testSubject0.transform(mock(Expression.class));
        int result4 = testSubject0.getId();
        Object result5 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        List result6 = testSubject0.getArguments();
        boolean result7 = testSubject0.isUsed();
        TOntologyAtom result8 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result9 = testSubject0.isInModule();
        OWLAxiom result10 = testSubject0.getOWLAxiom();
        boolean result11 = testSubject0.isInSS();
        TSignature result12 = testSubject0.getSignature();
        String result13 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomDisjointDRoles() throws Exception {
        AxiomDisjointDRoles testSubject0 = new AxiomDisjointDRoles(mock(OWLAxiom.class),
                mock(List.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        boolean result0 = testSubject0.isEmpty();
        int result1 = testSubject0.size();
        Expression result2 = testSubject0.transform(mock(Expression.class));
        DataRoleExpression result3 = testSubject0.transform(mock(Expression.class));
        int result4 = testSubject0.getId();
        Object result5 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        List result6 = testSubject0.getArguments();
        boolean result7 = testSubject0.isUsed();
        TOntologyAtom result8 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result9 = testSubject0.isInModule();
        OWLAxiom result10 = testSubject0.getOWLAxiom();
        boolean result11 = testSubject0.isInSS();
        TSignature result12 = testSubject0.getSignature();
        String result13 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomDisjointORoles() throws Exception {
        AxiomDisjointORoles testSubject0 = new AxiomDisjointORoles(mock(OWLAxiom.class),
                mock(List.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        boolean result0 = testSubject0.isEmpty();
        int result1 = testSubject0.size();
        Expression result2 = testSubject0.transform(mock(Expression.class));
        ObjectRoleExpression result3 = testSubject0.transform(mock(Expression.class));
        int result4 = testSubject0.getId();
        Object result5 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        List result6 = testSubject0.getArguments();
        boolean result7 = testSubject0.isUsed();
        TOntologyAtom result8 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result9 = testSubject0.isInModule();
        OWLAxiom result10 = testSubject0.getOWLAxiom();
        boolean result11 = testSubject0.isInSS();
        TSignature result12 = testSubject0.getSignature();
        String result13 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomDisjointUnion() throws Exception {
        AxiomDisjointUnion testSubject0 = new AxiomDisjointUnion(mock(OWLAxiom.class),
                mock(ConceptExpression.class), mock(Collection.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        boolean result0 = testSubject0.isEmpty();
        int result1 = testSubject0.size();
        ConceptExpression result2 = testSubject0.transform(mock(Expression.class));
        Expression result3 = testSubject0.transform(mock(Expression.class));
        int result4 = testSubject0.getId();
        Object result5 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        List result6 = testSubject0.getArguments();
        boolean result7 = testSubject0.isUsed();
        TOntologyAtom result8 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result9 = testSubject0.isInModule();
        OWLAxiom result10 = testSubject0.getOWLAxiom();
        boolean result11 = testSubject0.isInSS();
        ConceptExpression result12 = testSubject0.getC();
        TSignature result13 = testSubject0.getSignature();
        String result14 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomDRoleDomain() throws Exception {
        AxiomDRoleDomain testSubject0 = new AxiomDRoleDomain(mock(OWLAxiom.class),
                mock(DataRoleExpression.class), mock(ConceptExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        ConceptExpression result2 = testSubject0.getDomain();
        testSubject0.setId(1);
        boolean result3 = testSubject0.isUsed();
        TOntologyAtom result4 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result5 = testSubject0.isInModule();
        OWLAxiom result6 = testSubject0.getOWLAxiom();
        boolean result7 = testSubject0.isInSS();
        DataRoleExpression result8 = testSubject0.getRole();
        TSignature result9 = testSubject0.getSignature();
        String result10 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomDRoleFunctional() throws Exception {
        AxiomDRoleFunctional testSubject0 = new AxiomDRoleFunctional(
                mock(OWLAxiom.class), mock(DataRoleExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        DataRoleExpression result7 = testSubject0.getRole();
        TSignature result8 = testSubject0.getSignature();
        String result9 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomDRoleRange() throws Exception {
        AxiomDRoleRange testSubject0 = new AxiomDRoleRange(mock(OWLAxiom.class),
                mock(DataRoleExpression.class), mock(DataExpression.class));
        int result0 = testSubject0.getId();
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        DataExpression result7 = testSubject0.getRange();
        DataRoleExpression result8 = testSubject0.getRole();
        TSignature result9 = testSubject0.getSignature();
        String result10 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomDRoleSubsumption() throws Exception {
        AxiomDRoleSubsumption testSubject0 = new AxiomDRoleSubsumption(
                mock(OWLAxiom.class), mock(DataRoleExpression.class),
                mock(DataRoleExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        DataRoleExpression result2 = testSubject0.getSubRole();
        boolean result3 = testSubject0.isUsed();
        TOntologyAtom result4 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result5 = testSubject0.isInModule();
        OWLAxiom result6 = testSubject0.getOWLAxiom();
        boolean result7 = testSubject0.isInSS();
        DataRoleExpression result8 = testSubject0.getRole();
        TSignature result9 = testSubject0.getSignature();
        String result10 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomEquivalentConcepts() throws Exception {
        AxiomEquivalentConcepts testSubject0 = new AxiomEquivalentConcepts(
                mock(OWLAxiom.class), mock(List.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        boolean result0 = testSubject0.isEmpty();
        int result1 = testSubject0.size();
        Expression result2 = testSubject0.transform(mock(Expression.class));
        ConceptExpression result3 = testSubject0.transform(mock(Expression.class));
        int result4 = testSubject0.getId();
        Object result5 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        List result6 = testSubject0.getArguments();
        boolean result7 = testSubject0.isUsed();
        TOntologyAtom result8 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result9 = testSubject0.isInModule();
        OWLAxiom result10 = testSubject0.getOWLAxiom();
        boolean result11 = testSubject0.isInSS();
        TSignature result12 = testSubject0.getSignature();
        String result13 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomEquivalentDRoles() throws Exception {
        AxiomEquivalentDRoles testSubject0 = new AxiomEquivalentDRoles(
                mock(OWLAxiom.class), mock(List.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        boolean result0 = testSubject0.isEmpty();
        int result1 = testSubject0.size();
        Expression result2 = testSubject0.transform(mock(Expression.class));
        DataRoleExpression result3 = testSubject0.transform(mock(Expression.class));
        int result4 = testSubject0.getId();
        Object result5 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        List result6 = testSubject0.getArguments();
        boolean result7 = testSubject0.isUsed();
        TOntologyAtom result8 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result9 = testSubject0.isInModule();
        OWLAxiom result10 = testSubject0.getOWLAxiom();
        boolean result11 = testSubject0.isInSS();
        TSignature result12 = testSubject0.getSignature();
        String result13 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomEquivalentORoles() throws Exception {
        AxiomEquivalentORoles testSubject0 = new AxiomEquivalentORoles(
                mock(OWLAxiom.class), mock(List.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        boolean result0 = testSubject0.isEmpty();
        int result1 = testSubject0.size();
        Expression result2 = testSubject0.transform(mock(Expression.class));
        ObjectRoleExpression result3 = testSubject0.transform(mock(Expression.class));
        int result4 = testSubject0.getId();
        Object result5 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        List result6 = testSubject0.getArguments();
        boolean result7 = testSubject0.isUsed();
        TOntologyAtom result8 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result9 = testSubject0.isInModule();
        OWLAxiom result10 = testSubject0.getOWLAxiom();
        boolean result11 = testSubject0.isInSS();
        TSignature result12 = testSubject0.getSignature();
        String result13 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomFairnessConstraint() throws Exception {
        AxiomFairnessConstraint testSubject0 = new AxiomFairnessConstraint(
                mock(OWLAxiom.class), mock(List.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        boolean result0 = testSubject0.isEmpty();
        int result1 = testSubject0.size();
        Expression result2 = testSubject0.transform(mock(Expression.class));
        ConceptExpression result3 = testSubject0.transform(mock(Expression.class));
        int result4 = testSubject0.getId();
        Object result5 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        List result6 = testSubject0.getArguments();
        boolean result7 = testSubject0.isUsed();
        TOntologyAtom result8 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result9 = testSubject0.isInModule();
        OWLAxiom result10 = testSubject0.getOWLAxiom();
        boolean result11 = testSubject0.isInSS();
        TSignature result12 = testSubject0.getSignature();
        String result13 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomInstanceOf() throws Exception {
        AxiomInstanceOf testSubject0 = new AxiomInstanceOf(mock(OWLAxiom.class),
                mock(IndividualExpression.class), mock(ConceptExpression.class));
        int result0 = testSubject0.getId();
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        ConceptExpression result7 = testSubject0.getC();
        IndividualExpression result8 = testSubject0.getIndividual();
        TSignature result9 = testSubject0.getSignature();
        String result10 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomORoleDomain() throws Exception {
        AxiomORoleDomain testSubject0 = new AxiomORoleDomain(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        ConceptExpression result2 = testSubject0.getDomain();
        testSubject0.setId(1);
        boolean result3 = testSubject0.isUsed();
        TOntologyAtom result4 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result5 = testSubject0.isInModule();
        OWLAxiom result6 = testSubject0.getOWLAxiom();
        boolean result7 = testSubject0.isInSS();
        ObjectRoleExpression result8 = testSubject0.getRole();
        TSignature result9 = testSubject0.getSignature();
        String result10 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomORoleFunctional() throws Exception {
        AxiomORoleFunctional testSubject0 = new AxiomORoleFunctional(
                mock(OWLAxiom.class), mock(ObjectRoleExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        ObjectRoleExpression result7 = testSubject0.getRole();
        TSignature result8 = testSubject0.getSignature();
        String result9 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomORoleRange() throws Exception {
        AxiomORoleRange testSubject0 = new AxiomORoleRange(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        int result0 = testSubject0.getId();
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        ConceptExpression result7 = testSubject0.getRange();
        ObjectRoleExpression result8 = testSubject0.getRole();
        TSignature result9 = testSubject0.getSignature();
        String result10 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomORoleSubsumption() throws Exception {
        AxiomORoleSubsumption testSubject0 = new AxiomORoleSubsumption(
                mock(OWLAxiom.class), mock(ObjectRoleComplexExpression.class),
                mock(ObjectRoleExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        ObjectRoleComplexExpression result2 = testSubject0.getSubRole();
        boolean result3 = testSubject0.isUsed();
        TOntologyAtom result4 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result5 = testSubject0.isInModule();
        OWLAxiom result6 = testSubject0.getOWLAxiom();
        boolean result7 = testSubject0.isInSS();
        ObjectRoleExpression result8 = testSubject0.getRole();
        TSignature result9 = testSubject0.getSignature();
        String result10 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomRelatedTo() throws Exception {
        AxiomRelatedTo testSubject0 = new AxiomRelatedTo(mock(OWLAxiom.class),
                mock(IndividualExpression.class), mock(ObjectRoleExpression.class),
                mock(IndividualExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        IndividualExpression result7 = testSubject0.getRelatedIndividual();
        ObjectRoleExpression result8 = testSubject0.getRelation();
        IndividualExpression result9 = testSubject0.getIndividual();
        TSignature result10 = testSubject0.getSignature();
        String result11 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomRelatedToNot() throws Exception {
        AxiomRelatedToNot testSubject0 = new AxiomRelatedToNot(mock(OWLAxiom.class),
                mock(IndividualExpression.class), mock(ObjectRoleExpression.class),
                mock(IndividualExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        IndividualExpression result7 = testSubject0.getRelatedIndividual();
        ObjectRoleExpression result8 = testSubject0.getRelation();
        IndividualExpression result9 = testSubject0.getIndividual();
        TSignature result10 = testSubject0.getSignature();
        String result11 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomRoleAsymmetric() throws Exception {
        AxiomRoleAsymmetric testSubject0 = new AxiomRoleAsymmetric(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        ObjectRoleExpression result7 = testSubject0.getRole();
        TSignature result8 = testSubject0.getSignature();
        String result9 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomRoleInverse() throws Exception {
        AxiomRoleInverse testSubject0 = new AxiomRoleInverse(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class), mock(ObjectRoleExpression.class));
        int result0 = testSubject0.getId();
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        ObjectRoleExpression result7 = testSubject0.getInvRole();
        ObjectRoleExpression result8 = testSubject0.getRole();
        TSignature result9 = testSubject0.getSignature();
        String result10 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomRoleInverseFunctional() throws Exception {
        AxiomRoleInverseFunctional testSubject0 = new AxiomRoleInverseFunctional(
                mock(OWLAxiom.class), mock(ObjectRoleExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        ObjectRoleExpression result7 = testSubject0.getRole();
        TSignature result8 = testSubject0.getSignature();
        String result9 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomRoleIrreflexive() throws Exception {
        AxiomRoleIrreflexive testSubject0 = new AxiomRoleIrreflexive(
                mock(OWLAxiom.class), mock(ObjectRoleExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        ObjectRoleExpression result7 = testSubject0.getRole();
        TSignature result8 = testSubject0.getSignature();
        String result9 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomRoleReflexive() throws Exception {
        AxiomRoleReflexive testSubject0 = new AxiomRoleReflexive(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        ObjectRoleExpression result7 = testSubject0.getRole();
        TSignature result8 = testSubject0.getSignature();
        String result9 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomRoleSymmetric() throws Exception {
        AxiomRoleSymmetric testSubject0 = new AxiomRoleSymmetric(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        ObjectRoleExpression result7 = testSubject0.getRole();
        TSignature result8 = testSubject0.getSignature();
        String result9 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomRoleTransitive() throws Exception {
        AxiomRoleTransitive testSubject0 = new AxiomRoleTransitive(mock(OWLAxiom.class),
                mock(ObjectRoleExpression.class));
        int result0 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result1 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.setId(1);
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        ObjectRoleExpression result7 = testSubject0.getRole();
        TSignature result8 = testSubject0.getSignature();
        String result9 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomSameIndividuals() throws Exception {
        AxiomSameIndividuals testSubject0 = new AxiomSameIndividuals(
                mock(OWLAxiom.class), mock(List.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        boolean result0 = testSubject0.isEmpty();
        int result1 = testSubject0.size();
        Expression result2 = testSubject0.transform(mock(Expression.class));
        IndividualExpression result3 = testSubject0.transform(mock(Expression.class));
        int result4 = testSubject0.getId();
        Object result5 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
        testSubject0.setId(1);
        List result6 = testSubject0.getArguments();
        boolean result7 = testSubject0.isUsed();
        TOntologyAtom result8 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result9 = testSubject0.isInModule();
        OWLAxiom result10 = testSubject0.getOWLAxiom();
        boolean result11 = testSubject0.isInSS();
        TSignature result12 = testSubject0.getSignature();
        String result13 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomSingleDRole() throws Exception {
        AxiomSingleDRole testSubject0 = mock(AxiomSingleDRole.class);
        int result0 = testSubject0.getId();
        testSubject0.setId(1);
        DataRoleExpression result1 = testSubject0.getRole();
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        TSignature result7 = testSubject0.getSignature();
        String result8 = testSubject0.toString();
        Object result9 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
    }

    @Test
    public void shouldTestAxiomSingleORole() throws Exception {
        AxiomSingleORole testSubject0 = mock(AxiomSingleORole.class);
        int result0 = testSubject0.getId();
        testSubject0.setId(1);
        ObjectRoleExpression result1 = testSubject0.getRole();
        boolean result2 = testSubject0.isUsed();
        TOntologyAtom result3 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result4 = testSubject0.isInModule();
        OWLAxiom result5 = testSubject0.getOWLAxiom();
        boolean result6 = testSubject0.isInSS();
        TSignature result7 = testSubject0.getSignature();
        String result8 = testSubject0.toString();
        Object result9 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
    }

    @Test
    public void shouldTestAxiomValueOf() throws Exception {
        AxiomValueOf testSubject0 = new AxiomValueOf(mock(OWLAxiom.class),
                mock(IndividualExpression.class), mock(DataRoleExpression.class),
                mock(Literal.class));
        Literal result0 = testSubject0.getValue();
        int result1 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result2 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        DataRoleExpression result3 = testSubject0.getAttribute();
        testSubject0.setId(1);
        boolean result4 = testSubject0.isUsed();
        TOntologyAtom result5 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result6 = testSubject0.isInModule();
        OWLAxiom result7 = testSubject0.getOWLAxiom();
        boolean result8 = testSubject0.isInSS();
        IndividualExpression result9 = testSubject0.getIndividual();
        TSignature result10 = testSubject0.getSignature();
        String result11 = testSubject0.toString();
    }

    @Test
    public void shouldTestAxiomValueOfNot() throws Exception {
        AxiomValueOfNot testSubject0 = new AxiomValueOfNot(mock(OWLAxiom.class),
                mock(IndividualExpression.class), mock(DataRoleExpression.class),
                mock(Literal.class));
        Literal result0 = testSubject0.getValue();
        int result1 = testSubject0.getId();
        testSubject0.accept(mock(DLAxiomVisitor.class));
        Object result2 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        DataRoleExpression result3 = testSubject0.getAttribute();
        testSubject0.setId(1);
        boolean result4 = testSubject0.isUsed();
        TOntologyAtom result5 = testSubject0.getAtom();
        testSubject0.setInModule(true);
        testSubject0.setAtom(mock(TOntologyAtom.class));
        testSubject0.setUsed(true);
        testSubject0.setInSS(true);
        boolean result6 = testSubject0.isInModule();
        OWLAxiom result7 = testSubject0.getOWLAxiom();
        boolean result8 = testSubject0.isInSS();
        IndividualExpression result9 = testSubject0.getIndividual();
        TSignature result10 = testSubject0.getSignature();
        String result11 = testSubject0.toString();
    }
}
