package jfact.unittest;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.vocab.OWLFacet;

import uk.ac.manchester.cs.jfact.datatypes.*;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "javadoc" })
public class GeneratedJUnitTest_uk_ac_manchester_cs_jfact_datatypes {
    @Test
    public void shouldTestInterfaceDatatype() throws Exception {
        Datatype testSubject0 = mock(Datatype.class);
        Comparable result0 = testSubject0.parseValue(mock(String.class));
        cardinality result1 = testSubject0.getCardinality();
        boolean result2 = testSubject0.isExpression();
        DatatypeExpression result3 = testSubject0.asExpression();
        Collection result4 = testSubject0.getAncestors();
        boolean result5 = testSubject0.getBounded();
        Set result6 = testSubject0.getFacets();

        Comparable result8 = testSubject0.getFacetValue(mock(Facet.class));
        boolean result10 = testSubject0.getNumeric();
        ordered result11 = testSubject0.getOrdered();
        boolean result12 = testSubject0.isCompatible(mock(Datatype.class));
        boolean result13 = testSubject0.isCompatible(mock(Literal.class));
        boolean result14 = testSubject0.isInValueSpace(mock(Comparable.class));
        Literal result15 = testSubject0.buildLiteral(mock(String.class));
        boolean result16 = testSubject0.isSubType(mock(Datatype.class));
        String result17 = testSubject0.getDatatypeURI();
        Collection result18 = testSubject0.listValues();
        boolean result19 = testSubject0.isNumericDatatype();
        NumericDatatype result20 = testSubject0.asNumericDatatype();
        boolean result21 = testSubject0.isOrderedDatatype();
        OrderedDatatype result22 = testSubject0.asOrderedDatatype();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result23 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestInterfaceDatatypeCombination() throws Exception {
        DatatypeCombination testSubject0 = mock(DatatypeCombination.class);
        Object result0 = testSubject0.add(mock(Object.class));
        Datatype result1 = testSubject0.getHost();
        boolean result2 = testSubject0.isCompatible(mock(Literal.class));
        boolean result3 = testSubject0.isCompatible(mock(Datatype.class));
        String result4 = testSubject0.getDatatypeURI();
        Iterable result5 = testSubject0.getList();
        boolean result6 = testSubject0.emptyValueSpace();
    }

    @Test
    public void shouldTestDatatypeEntry() throws Exception {
        DatatypeEntry testSubject0 = new DatatypeEntry(mock(Datatype.class));
        String result0 = testSubject0.toString();
        int result1 = testSubject0.getIndex();
        testSubject0.setIndex(1);
        Collection result2 = testSubject0.getFacet();
        boolean result3 = testSubject0.isBasicDataType();
        Datatype result4 = testSubject0.getDatatype();
        String result5 = testSubject0.getName();
        int result6 = testSubject0.getId();
        boolean result7 = testSubject0.isBottom();
        boolean result8 = testSubject0.isTop();
        testSubject0.setId(1);
        boolean result9 = testSubject0.isSystem();
        testSubject0.setSystem();
        testSubject0.setTop();
        testSubject0.setBottom();
        NamedEntity result10 = testSubject0.getEntity();
        testSubject0.setEntity(mock(NamedEntity.class));
    }

    @Test
    public void shouldTestDatatypeEnumeration() throws Exception {
        DatatypeEnumeration testSubject0 = new DatatypeEnumeration(mock(Datatype.class));
        DatatypeEnumeration testSubject1 = new DatatypeEnumeration(mock(Datatype.class),
                mock(Literal.class));
        DatatypeEnumeration testSubject2 = new DatatypeEnumeration(mock(Datatype.class),
                mock(Collection.class));
        Object result0 = testSubject0.add(mock(Object.class));
        DatatypeEnumeration result1 = testSubject0.add(mock(Literal.class));
        String result2 = testSubject0.toString();
        Object result3 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Datatype result4 = testSubject0.getHost();
        Comparable result5 = testSubject0.parseValue(mock(String.class));
        cardinality result6 = testSubject0.getCardinality();
        boolean result7 = testSubject0.isExpression();
        DatatypeExpression result8 = testSubject0.asExpression();
        Collection result9 = testSubject0.getAncestors();
        boolean result10 = testSubject0.getBounded();
        Set result11 = testSubject0.getFacets();
        Comparable result13 = testSubject0.getFacetValue(mock(Facet.class));
        boolean result15 = testSubject0.getNumeric();
        ordered result16 = testSubject0.getOrdered();
        boolean result17 = testSubject0.isCompatible(mock(Datatype.class));
        boolean result18 = testSubject0.isCompatible(mock(Literal.class));
        boolean result19 = testSubject0.isInValueSpace(mock(Comparable.class));
        Literal result20 = testSubject0.buildLiteral(mock(String.class));
        boolean result21 = testSubject0.isSubType(mock(Datatype.class));
        String result22 = testSubject0.getDatatypeURI();
        Collection result23 = testSubject0.listValues();
        boolean result24 = testSubject0.isNumericDatatype();
        NumericDatatype result25 = testSubject0.asNumericDatatype();
        boolean result26 = testSubject0.isOrderedDatatype();
        OrderedDatatype result27 = testSubject0.asOrderedDatatype();
        Iterable result28 = testSubject0.getList();
        Datatype result29 = testSubject0.getHostType();
        boolean result31 = testSubject0.emptyValueSpace();
    }

    @Test
    public void shouldTestInterfaceDatatypeExpression() throws Exception {
        DatatypeExpression testSubject0 = mock(DatatypeExpression.class);
        Datatype result0 = testSubject0.getHostType();
        boolean result2 = testSubject0.emptyValueSpace();
        Comparable result3 = testSubject0.parseValue(mock(String.class));
        cardinality result4 = testSubject0.getCardinality();
        boolean result5 = testSubject0.isExpression();
        DatatypeExpression result6 = testSubject0.asExpression();
        Collection result7 = testSubject0.getAncestors();
        boolean result8 = testSubject0.getBounded();
        Set result9 = testSubject0.getFacets();

        Comparable result11 = testSubject0.getFacetValue(mock(Facet.class));
        boolean result13 = testSubject0.getNumeric();
        ordered result14 = testSubject0.getOrdered();
        boolean result15 = testSubject0.isCompatible(mock(Datatype.class));
        boolean result16 = testSubject0.isCompatible(mock(Literal.class));
        boolean result17 = testSubject0.isInValueSpace(mock(Comparable.class));
        Literal result18 = testSubject0.buildLiteral(mock(String.class));
        boolean result19 = testSubject0.isSubType(mock(Datatype.class));
        String result20 = testSubject0.getDatatypeURI();
        Collection result21 = testSubject0.listValues();
        boolean result22 = testSubject0.isNumericDatatype();
        NumericDatatype result23 = testSubject0.asNumericDatatype();
        boolean result24 = testSubject0.isOrderedDatatype();
        OrderedDatatype result25 = testSubject0.asOrderedDatatype();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result26 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestDatatypeFactory() throws Exception {
        DatatypeFactory testSubject0 = DatatypeFactory.getInstance();
        DatatypeFactory result0 = DatatypeFactory.getInstance();
        DatatypeExpression result1 = DatatypeFactory
                .getDatatypeExpression(mock(Datatype.class));
        List result2 = DatatypeFactory.getValues();
        Collection result3 = testSubject0.getKnownDatatypes();
        Datatype result4 = testSubject0.getKnownDatatype(mock(String.class));
        boolean result5 = testSubject0.isKnownDatatype(mock(String.class));

        DatatypeExpression result7 = DatatypeFactory
                .getNumericDatatypeExpression(mock(NumericDatatype.class));
        DatatypeExpression result8 = DatatypeFactory
                .getOrderedDatatypeExpression(mock(Datatype.class));
        String result9 = testSubject0.toString();
    }

    @Test
    public void shouldTestDatatypeIntersection() throws Exception {
        DatatypeIntersection testSubject0 = new DatatypeIntersection(mock(Datatype.class));
        DatatypeIntersection testSubject1 = new DatatypeIntersection(
                mock(Datatype.class), mock(Iterable.class));
        Object result0 = testSubject0.add(mock(Datatype.class));
        DatatypeIntersection result1 = testSubject0.add(mock(Datatype.class));
        String result2 = testSubject0.toString();
        Datatype result3 = testSubject0.getHost();
        boolean result4 = testSubject0.isCompatible(mock(Literal.class));
        boolean result5 = testSubject0.isCompatible(mock(Datatype.class));
        String result6 = testSubject0.getDatatypeURI();
        Iterable result7 = testSubject0.getList();
        boolean result8 = testSubject0.emptyValueSpace();
        Datatype result9 = DatatypeIntersection.getHostDatatype(mock(Collection.class));
    }

    @Test
    public void shouldTestDatatypeNegation() throws Exception {
        DatatypeNegation testSubject0 = new DatatypeNegation(mock(Datatype.class));
        String result0 = testSubject0.toString();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result1 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        Comparable result2 = testSubject0.parseValue(mock(String.class));
        cardinality result3 = testSubject0.getCardinality();
        boolean result4 = testSubject0.isExpression();
        DatatypeExpression result5 = testSubject0.asExpression();
        Collection result6 = testSubject0.getAncestors();
        boolean result7 = testSubject0.getBounded();
        Set result8 = testSubject0.getFacets();
        Comparable result10 = testSubject0.getFacetValue(mock(Facet.class));
        boolean result12 = testSubject0.getNumeric();
        ordered result13 = testSubject0.getOrdered();
        boolean result14 = testSubject0.isCompatible(mock(Literal.class));
        boolean result15 = testSubject0.isCompatible(mock(Datatype.class));
        boolean result16 = testSubject0.isInValueSpace(mock(Comparable.class));
        Literal result17 = testSubject0.buildLiteral(mock(String.class));
        boolean result18 = testSubject0.isSubType(mock(Datatype.class));
        String result19 = testSubject0.getDatatypeURI();
        Collection result20 = testSubject0.listValues();
        boolean result21 = testSubject0.isNumericDatatype();
        NumericDatatype result22 = testSubject0.asNumericDatatype();
        boolean result23 = testSubject0.isOrderedDatatype();
        OrderedDatatype result24 = testSubject0.asOrderedDatatype();
        Datatype result25 = testSubject0.getHostType();
        boolean result27 = testSubject0.emptyValueSpace();
    }

    @Test
    public void shouldTestDatatypeNumericEnumeration() throws Exception {
        DatatypeNumericEnumeration testSubject0 = new DatatypeNumericEnumeration(
                mock(NumericDatatype.class));
        DatatypeNumericEnumeration testSubject1 = new DatatypeNumericEnumeration(
                mock(NumericDatatype.class), mock(Literal.class));
        DatatypeNumericEnumeration testSubject2 = new DatatypeNumericEnumeration(
                mock(NumericDatatype.class), mock(Collection.class));
        DatatypeNumericEnumeration result0 = testSubject0.add(mock(Literal.class));
        DatatypeEnumeration result1 = testSubject0.add(mock(Literal.class));
        boolean result2 = testSubject0.isNumericDatatype();
        NumericDatatype result3 = testSubject0.asNumericDatatype();
        boolean result4 = testSubject0.isOrderedDatatype();
        OrderedDatatype result5 = testSubject0.asOrderedDatatype();
        boolean result6 = testSubject0.hasMaxInclusive();
        boolean result7 = testSubject0.hasMinInclusive();
        Comparable result9 = testSubject0.getMax();
        Comparable result10 = testSubject0.getMin();
        boolean result12 = testSubject0.hasMaxExclusive();
        boolean result13 = testSubject0.hasMinExclusive();
        boolean result14 = testSubject0.hasMin();
        boolean result15 = testSubject0.hasMax();
        Object result16 = testSubject0.add(mock(Object.class));
        String result17 = testSubject0.toString();
        Object result18 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Datatype result19 = testSubject0.getHost();
        Comparable result20 = testSubject0.parseValue(mock(String.class));
        cardinality result21 = testSubject0.getCardinality();
        boolean result22 = testSubject0.isExpression();
        DatatypeExpression result23 = testSubject0.asExpression();
        Collection result24 = testSubject0.getAncestors();
        boolean result25 = testSubject0.getBounded();
        Set result26 = testSubject0.getFacets();

        Comparable result28 = testSubject0.getFacetValue(mock(Facet.class));
        boolean result30 = testSubject0.getNumeric();
        ordered result31 = testSubject0.getOrdered();
        boolean result32 = testSubject0.isCompatible(mock(Datatype.class));
        boolean result33 = testSubject0.isCompatible(mock(Literal.class));
        boolean result34 = testSubject0.isInValueSpace(mock(Comparable.class));
        Literal result35 = testSubject0.buildLiteral(mock(String.class));
        boolean result36 = testSubject0.isSubType(mock(Datatype.class));
        String result37 = testSubject0.getDatatypeURI();
        Collection result38 = testSubject0.listValues();
        Iterable result39 = testSubject0.getList();
        Datatype result40 = testSubject0.getHostType();

        boolean result42 = testSubject0.emptyValueSpace();
    }

    @Test
    public void shouldTestDataTypeReasoner() throws Exception {
        DataTypeReasoner testSubject0 = new DataTypeReasoner(
                mock(JFactReasonerConfiguration.class));
        boolean result0 = testSubject0.addDataEntry(true, mock(DagTag.class),
                mock(NamedEntry.class), mock(DepSet.class));
        DepSet result1 = testSubject0.getClashSet();
        boolean result2 = testSubject0.checkClash();
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestDataTypeSituation() throws Exception {
        DataTypeSituation testSubject0 = mock(DataTypeSituation.class);
        Datatype result1 = testSubject0.getType();
        testSubject0.setPType(mock(DepSet.class));
        testSubject0.setNType(mock(DepSet.class));
        boolean result2 = testSubject0.addInterval(true, mock(Datatype.class),
                mock(DepSet.class));
        boolean result3 = testSubject0.checkPNTypeClash();
        DepSet result4 = testSubject0.getPType();
        boolean result5 = testSubject0.hasPType();
        boolean result6 = testSubject0.hasNType();
        DepSet result7 = testSubject0.getNType();
        boolean result8 = testSubject0
                .checkCompatibleValue(mock(DataTypeSituation.class));
    }

    @Test
    public void shouldTestDatatypeUnion() throws Exception {
        DatatypeUnion testSubject0 = new DatatypeUnion(mock(Datatype.class),
                mock(Collection.class));
        DatatypeUnion testSubject1 = new DatatypeUnion(mock(Datatype.class));
        DatatypeUnion result1 = testSubject0.add(mock(Datatype.class));
        String result2 = testSubject0.toString();
        Datatype result3 = testSubject0.getHost();
        boolean result4 = testSubject0.isCompatible(mock(Datatype.class));
        boolean result5 = testSubject0.isCompatible(mock(Literal.class));
        String result6 = testSubject0.getDatatypeURI();
        Iterable result7 = testSubject0.getList();
        boolean result8 = testSubject0.emptyValueSpace();
    }

    @Test
    public void shouldTestInterfaceFacet() throws Exception {
        Facet testSubject0 = mock(Facet.class);
        Comparable result0 = testSubject0.parse(mock(Object.class));
        boolean result1 = testSubject0.isNumberFacet();
        String result3 = testSubject0.getURI();
    }

    @Test
    public void shouldTestFacets() throws Exception {
        Facets testSubject0 = new Facets();
        List result0 = Facets.values();
        Facet result1 = Facets.parse(mock(OWLFacet.class));
        Facet result2 = Facets.parse(mock(String.class));
        String result3 = testSubject0.toString();
    }

    @Test
    public void shouldTestInterfaceLiteral() throws Exception {
        Literal testSubject0 = mock(Literal.class);
        String result0 = testSubject0.value();
        Datatype result1 = testSubject0.getDatatypeExpression();
        Comparable result2 = testSubject0.typedValue();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result3 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        int result4 = testSubject0.compareTo(mock(Object.class));
    }

    @Test
    public void shouldTestLiteralEntry() throws Exception {
        LiteralEntry testSubject0 = new LiteralEntry(mock(String.class));
        String result0 = testSubject0.toString();
        Datatype result1 = testSubject0.getType();
        int result2 = testSubject0.getIndex();
        testSubject0.setIndex(1);
        Collection result3 = testSubject0.getFacet();
        testSubject0.setLiteral(mock(Literal.class));
        Literal result4 = testSubject0.getLiteral();
        String result5 = testSubject0.getName();
        int result6 = testSubject0.getId();
        boolean result7 = testSubject0.isBottom();
        boolean result8 = testSubject0.isTop();
        testSubject0.setId(1);
        boolean result9 = testSubject0.isSystem();
        testSubject0.setSystem();
        testSubject0.setTop();
        testSubject0.setBottom();
        NamedEntity result10 = testSubject0.getEntity();
        testSubject0.setEntity(mock(NamedEntity.class));
    }

    @Test
    public void shouldTestInterfaceNumericDatatype() throws Exception {
        NumericDatatype testSubject0 = mock(NumericDatatype.class);
        boolean result0 = testSubject0.hasMaxInclusive();
        boolean result1 = testSubject0.hasMinInclusive();
        boolean result4 = testSubject0.hasMaxExclusive();
        boolean result5 = testSubject0.hasMinExclusive();
        boolean result6 = testSubject0.hasMin();
        boolean result7 = testSubject0.hasMax();
        Comparable result8 = testSubject0.parseValue(mock(String.class));
        cardinality result9 = testSubject0.getCardinality();
        boolean result10 = testSubject0.isExpression();
        DatatypeExpression result11 = testSubject0.asExpression();
        Collection result12 = testSubject0.getAncestors();
        boolean result13 = testSubject0.getBounded();
        Set result14 = testSubject0.getFacets();

        Comparable result16 = testSubject0.getFacetValue(mock(Facet.class));
        boolean result18 = testSubject0.getNumeric();
        ordered result19 = testSubject0.getOrdered();
        boolean result20 = testSubject0.isCompatible(mock(Datatype.class));
        boolean result21 = testSubject0.isCompatible(mock(Literal.class));
        boolean result22 = testSubject0.isInValueSpace(mock(Comparable.class));
        Literal result23 = testSubject0.buildLiteral(mock(String.class));
        boolean result24 = testSubject0.isSubType(mock(Datatype.class));
        String result25 = testSubject0.getDatatypeURI();
        Collection result26 = testSubject0.listValues();
        boolean result27 = testSubject0.isNumericDatatype();
        NumericDatatype result28 = testSubject0.asNumericDatatype();
        boolean result29 = testSubject0.isOrderedDatatype();
        OrderedDatatype result30 = testSubject0.asOrderedDatatype();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result31 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
    }

    @Test
    public void shouldTestInterfaceNumericLiteral() throws Exception {
        NumericLiteral testSubject0 = mock(NumericLiteral.class);
        String result0 = testSubject0.value();
        Datatype result1 = testSubject0.getDatatypeExpression();
        Comparable result2 = testSubject0.typedValue();
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Object result3 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        int result4 = testSubject0.compareTo(mock(Object.class));
    }

    @Test
    public void shouldTestNumericLiteralImpl() throws Exception {
        NumericLiteralImpl testSubject0 = new NumericLiteralImpl(
                mock(NumericDatatype.class), mock(String.class));
        String result0 = testSubject0.value();
        String result1 = testSubject0.toString();
        int result2 = testSubject0.compareTo(mock(Literal.class));
        Object result3 = testSubject0.accept(mock(DLExpressionVisitorEx.class));
        testSubject0.accept(mock(DLExpressionVisitor.class));
        Datatype result4 = testSubject0.getDatatypeExpression();
        Comparable result5 = testSubject0.typedValue();
        int result6 = testSubject0.compareTo(mock(Object.class));
    }

    @Test
    public void shouldTestInterfaceOrderedDatatype() throws Exception {
        OrderedDatatype testSubject0 = mock(OrderedDatatype.class);
        boolean result0 = testSubject0.hasMaxInclusive();
        boolean result1 = testSubject0.hasMinInclusive();
        Object result2 = testSubject0.getMax();
        Object result3 = testSubject0.getMin();
        boolean result4 = testSubject0.hasMaxExclusive();
        boolean result5 = testSubject0.hasMinExclusive();
        boolean result6 = testSubject0.hasMin();
        boolean result7 = testSubject0.hasMax();
    }

    @Test
    public void shouldTestUtils() throws Exception {
        Utils testSubject0 = new Utils();
        Set result0 = Utils.getFacets(mock(Facet[].class));
        Set result1 = Utils.getFacets(mock(Facet[][].class));
        Set result2 = Utils.generateAncestors(mock(Datatype.class));
        String result3 = testSubject0.toString();
    }
}
