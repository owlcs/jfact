package jfact.unittest;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact_kernel_dl {
    @Test
    public void shouldTestConceptAnd() throws Exception {
        ConceptAnd testSubject0 = new ConceptAnd(mock(List.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        String result1 = testSubject0.toString();
        boolean result2 = testSubject0.isEmpty();
        int result3 = testSubject0.size();
        Expression result4 = testSubject0.transform(mock(Expression.class));
        List result5 = testSubject0.getArguments();
    }

    @Test
    public void shouldTestConceptBottom() throws Exception {
        ConceptBottom testSubject0 = new ConceptBottom();
        String result0 = testSubject0.toString();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result1 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestConceptDataExactCardinality() throws Exception {
        ConceptDataExactCardinality testSubject0 = new ConceptDataExactCardinality(1,
                mock(DataRoleExpression.class), mock(DataExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        int result1 = testSubject0.getCardinality();
        DataRoleExpression result2 = testSubject0.getDataRoleExpression();
        DataExpression result3 = testSubject0.getExpr();
        Object result4 = testSubject0.getExpr();
        String result5 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptDataExists() throws Exception {
        ConceptDataExists testSubject0 = new ConceptDataExists(
                mock(DataRoleExpression.class), mock(DataExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        DataRoleExpression result1 = testSubject0.getDataRoleExpression();
        DataExpression result2 = testSubject0.getExpr();
        Object result3 = testSubject0.getExpr();
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptDataForall() throws Exception {
        ConceptDataForall testSubject0 = new ConceptDataForall(
                mock(DataRoleExpression.class), mock(DataExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        DataRoleExpression result1 = testSubject0.getDataRoleExpression();
        DataExpression result2 = testSubject0.getExpr();
        Object result3 = testSubject0.getExpr();
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptDataMaxCardinality() throws Exception {
        ConceptDataMaxCardinality testSubject0 = new ConceptDataMaxCardinality(1,
                mock(DataRoleExpression.class), mock(DataExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        int result1 = testSubject0.getCardinality();
        DataRoleExpression result2 = testSubject0.getDataRoleExpression();
        DataExpression result3 = testSubject0.getExpr();
        Object result4 = testSubject0.getExpr();
        String result5 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptDataMinCardinality() throws Exception {
        ConceptDataMinCardinality testSubject0 = new ConceptDataMinCardinality(1,
                mock(DataRoleExpression.class), mock(DataExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        int result1 = testSubject0.getCardinality();
        DataRoleExpression result2 = testSubject0.getDataRoleExpression();
        DataExpression result3 = testSubject0.getExpr();
        Object result4 = testSubject0.getExpr();
        String result5 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptDataValue() throws Exception {
        ConceptDataValue testSubject0 = new ConceptDataValue(
                mock(DataRoleExpression.class), mock(Literal.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        DataRoleExpression result1 = testSubject0.getDataRoleExpression();
        Literal result2 = testSubject0.getExpr();
        Object result3 = testSubject0.getExpr();
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptName() throws Exception {
        ConceptName testSubject0 = new ConceptName(mock(String.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        String result1 = testSubject0.toString();
        String result2 = testSubject0.getName();
        NamedEntry result3 = testSubject0.getEntry();
        testSubject0.setEntry(mock(NamedEntry.class));
    }

    @Test
    public void shouldTestConceptNot() throws Exception {
        ConceptNot testSubject0 = new ConceptNot(mock(ConceptExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ConceptExpression result1 = testSubject0.getConcept();
        String result2 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptObjectExactCardinality() throws Exception {
        ConceptObjectExactCardinality testSubject0 = new ConceptObjectExactCardinality(1,
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ConceptExpression result1 = testSubject0.getConcept();
        ObjectRoleExpression result2 = testSubject0.getOR();
        int result3 = testSubject0.getCardinality();
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptObjectExists() throws Exception {
        ConceptObjectExists testSubject0 = new ConceptObjectExists(
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ConceptExpression result1 = testSubject0.getConcept();
        ObjectRoleExpression result2 = testSubject0.getOR();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptObjectForall() throws Exception {
        ConceptObjectForall testSubject0 = new ConceptObjectForall(
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ConceptExpression result1 = testSubject0.getConcept();
        ObjectRoleExpression result2 = testSubject0.getOR();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptObjectMaxCardinality() throws Exception {
        ConceptObjectMaxCardinality testSubject0 = new ConceptObjectMaxCardinality(1,
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ConceptExpression result1 = testSubject0.getConcept();
        ObjectRoleExpression result2 = testSubject0.getOR();
        int result3 = testSubject0.getCardinality();
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptObjectMinCardinality() throws Exception {
        ConceptObjectMinCardinality testSubject0 = new ConceptObjectMinCardinality(1,
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ConceptExpression result1 = testSubject0.getConcept();
        ObjectRoleExpression result2 = testSubject0.getOR();
        int result3 = testSubject0.getCardinality();
        String result4 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptObjectSelf() throws Exception {
        ConceptObjectSelf testSubject0 = new ConceptObjectSelf(
                mock(ObjectRoleExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ObjectRoleExpression result1 = testSubject0.getOR();
        String result2 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptObjectValue() throws Exception {
        ConceptObjectValue testSubject0 = new ConceptObjectValue(
                mock(ObjectRoleExpression.class), mock(IndividualExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ObjectRoleExpression result1 = testSubject0.getOR();
        IndividualExpression result2 = testSubject0.getI();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestConceptOneOf() throws Exception {
        ConceptOneOf testSubject0 = new ConceptOneOf(mock(List.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        String result1 = testSubject0.toString();
        boolean result2 = testSubject0.isEmpty();
        int result3 = testSubject0.size();
        Expression result4 = testSubject0.transform(mock(Expression.class));
        List result5 = testSubject0.getArguments();
    }

    @Test
    public void shouldTestConceptOr() throws Exception {
        ConceptOr testSubject0 = new ConceptOr(mock(List.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        String result1 = testSubject0.toString();
        boolean result2 = testSubject0.isEmpty();
        int result3 = testSubject0.size();
        Expression result4 = testSubject0.transform(mock(Expression.class));
        List result5 = testSubject0.getArguments();
    }

    @Test
    public void shouldTestConceptTop() throws Exception {
        ConceptTop testSubject0 = new ConceptTop();
        String result0 = testSubject0.toString();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result1 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestDataAnd() throws Exception {
        DataAnd testSubject0 = new DataAnd(mock(List.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        String result1 = testSubject0.toString();
        boolean result2 = testSubject0.isEmpty();
        int result3 = testSubject0.size();
        Expression result4 = testSubject0.transform(mock(Expression.class));
        List result5 = testSubject0.getArguments();
    }

    @Test
    public void shouldTestDataBottom() throws Exception {
        DataBottom testSubject0 = new DataBottom();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        String result1 = testSubject0.toString();
    }

    @Test
    public void shouldTestDataNot() throws Exception {
        DataNot testSubject0 = new DataNot(mock(DataExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        DataExpression result1 = testSubject0.getExpr();
        Object result2 = testSubject0.getExpr();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestDataOneOf() throws Exception {
        DataOneOf testSubject0 = new DataOneOf(mock(List.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        String result1 = testSubject0.toString();
        boolean result2 = testSubject0.isEmpty();
        int result3 = testSubject0.size();
        Expression result4 = testSubject0.transform(mock(Expression.class));
        List result5 = testSubject0.getArguments();
    }

    @Test
    public void shouldTestDataOr() throws Exception {
        DataOr testSubject0 = new DataOr(mock(List.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        String result1 = testSubject0.toString();
        boolean result2 = testSubject0.isEmpty();
        int result3 = testSubject0.size();
        Expression result4 = testSubject0.transform(mock(Expression.class));
        List result5 = testSubject0.getArguments();
    }

    @Test
    public void shouldTestDataRoleBottom() throws Exception {
        DataRoleBottom testSubject0 = new DataRoleBottom();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        String result1 = testSubject0.toString();
    }

    @Test
    public void shouldTestDataRoleName() throws Exception {
        DataRoleName testSubject0 = new DataRoleName(mock(String.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        String result1 = testSubject0.toString();
        String result2 = testSubject0.getName();
        NamedEntry result3 = testSubject0.getEntry();
        testSubject0.setEntry(mock(NamedEntry.class));
    }

    @Test
    public void shouldTestDataRoleTop() throws Exception {
        DataRoleTop testSubject0 = new DataRoleTop();
        String result0 = testSubject0.toString();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result1 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestDataTop() throws Exception {
        DataTop testSubject0 = new DataTop();
        String result0 = testSubject0.toString();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result1 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestIndividualName() throws Exception {
        IndividualName testSubject0 = new IndividualName(mock(String.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        String result1 = testSubject0.toString();
        String result2 = testSubject0.getName();
        NamedEntry result3 = testSubject0.getEntry();
        testSubject0.setEntry(mock(NamedEntry.class));
    }

    @Test
    public void shouldTestNamedEntityImpl() throws Exception {
        NamedEntityImpl testSubject0 = mock(NamedEntityImpl.class);
        String result0 = testSubject0.toString();
        String result1 = testSubject0.getName();
        NamedEntry result2 = testSubject0.getEntry();
        testSubject0.setEntry(mock(NamedEntry.class));
    }

    @Test
    public void shouldTestNAryExpressionImpl() throws Exception {
        NAryExpressionImpl testSubject0 = new NAryExpressionImpl();
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        String result0 = testSubject0.toString();
        boolean result1 = testSubject0.isEmpty();
        int result2 = testSubject0.size();
        Expression result3 = testSubject0.transform(mock(Expression.class));
        List result4 = testSubject0.getArguments();
    }

    @Test
    public void shouldTestObjectRoleBottom() throws Exception {
        ObjectRoleBottom testSubject0 = new ObjectRoleBottom();
        String result0 = testSubject0.toString();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result1 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestObjectRoleChain() throws Exception {
        ObjectRoleChain testSubject0 = new ObjectRoleChain(mock(List.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        testSubject0.add(mock(Collection.class));
        testSubject0.add(mock(Expression.class));
        String result1 = testSubject0.toString();
        boolean result2 = testSubject0.isEmpty();
        int result3 = testSubject0.size();
        Expression result4 = testSubject0.transform(mock(Expression.class));
        List result5 = testSubject0.getArguments();
    }

    @Test
    public void shouldTestObjectRoleInverse() throws Exception {
        ObjectRoleInverse testSubject0 = new ObjectRoleInverse(
                mock(ObjectRoleExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ObjectRoleExpression result1 = testSubject0.getOR();
        String result2 = testSubject0.toString();
    }

    @Test
    public void shouldTestObjectRoleName() throws Exception {
        ObjectRoleName testSubject0 = new ObjectRoleName(mock(String.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        String result1 = testSubject0.toString();
        String result2 = testSubject0.getName();
        NamedEntry result3 = testSubject0.getEntry();
        testSubject0.setEntry(mock(NamedEntry.class));
    }

    @Test
    public void shouldTestObjectRoleProjectionFrom() throws Exception {
        ObjectRoleProjectionFrom testSubject0 = new ObjectRoleProjectionFrom(
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ConceptExpression result1 = testSubject0.getConcept();
        ObjectRoleExpression result2 = testSubject0.getOR();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestObjectRoleProjectionInto() throws Exception {
        ObjectRoleProjectionInto testSubject0 = new ObjectRoleProjectionInto(
                mock(ObjectRoleExpression.class), mock(ConceptExpression.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result0 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        ConceptExpression result1 = testSubject0.getConcept();
        ObjectRoleExpression result2 = testSubject0.getOR();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestObjectRoleTop() throws Exception {
        ObjectRoleTop testSubject0 = new ObjectRoleTop();
        String result0 = testSubject0.toString();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result1 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }
}
