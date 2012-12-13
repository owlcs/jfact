package jfact.unittest;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact_kernel_dl_interfaces {
    @Test
    public void shouldTestInterfaceAxiom() throws Exception {
        Axiom testSubject0 = mock(Axiom.class);
        int result0 = testSubject0.getId();
        TSignature result1 = testSubject0.getSignature();
        Object result2 = testSubject0.accept(mock(DLAxiomVisitorEx.class));
        testSubject0.accept(mock(DLAxiomVisitor.class));
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
    }

    @Test
    public void shouldTestInterfaceConceptArg() throws Exception {
        ConceptArg testSubject0 = mock(ConceptArg.class);
        ConceptExpression result0 = testSubject0.getConcept();
    }

    @Test
    public void shouldTestInterfaceConceptDataCardinalityExpression() throws Exception {
        ConceptDataCardinalityExpression testSubject0 = mock(ConceptDataCardinalityExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        DataRoleExpression result1 = testSubject0.getDataRoleExpression();
        Object result2 = testSubject0.getExpr();
        int result3 = testSubject0.getCardinality();
    }

    @Test
    public void shouldTestInterfaceConceptDataRoleExpression() throws Exception {
        ConceptDataRoleExpression testSubject0 = mock(ConceptDataRoleExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        DataRoleExpression result1 = testSubject0.getDataRoleExpression();
    }

    @Test
    public void shouldTestInterfaceConceptDataRVExpression() throws Exception {
        ConceptDataRVExpression testSubject0 = mock(ConceptDataRVExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        DataRoleExpression result1 = testSubject0.getDataRoleExpression();
        Object result2 = testSubject0.getExpr();
    }

    @Test
    public void shouldTestInterfaceConceptExpression() throws Exception {
        ConceptExpression testSubject0 = mock(ConceptExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestInterfaceConceptObjectCardinalityExpression() throws Exception {
        ConceptObjectCardinalityExpression testSubject0 = mock(ConceptObjectCardinalityExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ObjectRoleExpression result1 = testSubject0.getOR();
        ConceptExpression result2 = testSubject0.getConcept();
        int result3 = testSubject0.getCardinality();
    }

    @Test
    public void shouldTestInterfaceConceptObjectRCExpression() throws Exception {
        ConceptObjectRCExpression testSubject0 = mock(ConceptObjectRCExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ObjectRoleExpression result1 = testSubject0.getOR();
        ConceptExpression result2 = testSubject0.getConcept();
    }

    @Test
    public void shouldTestInterfaceConceptObjectRoleExpression() throws Exception {
        ConceptObjectRoleExpression testSubject0 = mock(ConceptObjectRoleExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ObjectRoleExpression result1 = testSubject0.getOR();
    }

    @Test
    public void shouldTestInterfaceDataExpression() throws Exception {
        DataExpression testSubject0 = mock(DataExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestInterfaceDataExpressionArg() throws Exception {
        DataExpressionArg testSubject0 = mock(DataExpressionArg.class);
        Object result0 = testSubject0.getExpr();
    }

    @Test
    public void shouldTestInterfaceDataRoleArg() throws Exception {
        DataRoleArg testSubject0 = mock(DataRoleArg.class);
        DataRoleExpression result0 = testSubject0.getDataRoleExpression();
    }

    @Test
    public void shouldTestInterfaceDataRoleExpression() throws Exception {
        DataRoleExpression testSubject0 = mock(DataRoleExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestInterfaceEntity() throws Exception {
        Entity testSubject0 = mock(Entity.class);
    }

    @Test
    public void shouldTestInterfaceExpression() throws Exception {
        Expression testSubject0 = mock(Expression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestInterfaceIndividualExpression() throws Exception {
        IndividualExpression testSubject0 = mock(IndividualExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestInterfaceNamedEntity() throws Exception {
        NamedEntity testSubject0 = mock(NamedEntity.class);
        String result0 = testSubject0.getName();
        NamedEntry result1 = testSubject0.getEntry();
        testSubject0.setEntry(mock(NamedEntry.class));
    }

    @Test
    public void shouldTestInterfaceNAryExpression() throws Exception {
        NAryExpression testSubject0 = mock(NAryExpression.class);
        testSubject0.add(mock(Expression.class));
        testSubject0.add(mock(Collection.class));
        boolean result0 = testSubject0.isEmpty();
        int result1 = testSubject0.size();
        Expression result2 = testSubject0.transform(mock(Expression.class));
        List result3 = testSubject0.getArguments();
    }

    @Test
    public void shouldTestInterfaceNumberArg() throws Exception {
        NumberArg testSubject0 = mock(NumberArg.class);
        int result0 = testSubject0.getCardinality();
    }

    @Test
    public void shouldTestInterfaceObjectRoleArg() throws Exception {
        ObjectRoleArg testSubject0 = mock(ObjectRoleArg.class);
        ObjectRoleExpression result0 = testSubject0.getOR();
    }

    @Test
    public void shouldTestInterfaceObjectRoleComplexExpression() throws Exception {
        ObjectRoleComplexExpression testSubject0 = mock(ObjectRoleComplexExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestInterfaceObjectRoleExpression() throws Exception {
        ObjectRoleExpression testSubject0 = mock(ObjectRoleExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestInterfaceRoleExpression() throws Exception {
        RoleExpression testSubject0 = mock(RoleExpression.class);
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }
}
