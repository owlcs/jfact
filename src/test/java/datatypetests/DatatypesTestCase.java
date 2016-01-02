package datatypetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.Assert.*;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;
import static uk.ac.manchester.cs.jfact.kernel.DagTag.*;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.roaringbitmap.RoaringBitmap;
import org.w3c.dom.Document;

import uk.ac.manchester.cs.jfact.datatypes.DataTypeReasoner;
import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeEntry;
import uk.ac.manchester.cs.jfact.datatypes.LiteralEntry;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

@SuppressWarnings("javadoc")
public class DatatypesTestCase {

    @Nonnull private static final String XMLTEXT5 = "&lt;span xml:lang=&quot;en&quot;&gt;&lt;b&gt;Good!&lt;/b&gt;&lt;/span&gt;";
    @Nonnull private static final String XMLTEXT4 = "&lt;span xml:lang=&quot;en&quot;&gt;&lt;b&gt;Bad!&lt;/b&gt;&lt;/span&gt;";
    @Nonnull private static final String XMLTEXT3 = "&lt;br&gt;&lt;/br&gt;&lt;img src=&quot;vn.png&quot; title=&quot;Venn&quot; alt=&quot;Venn diagram&quot; longdesc=&quot;vn.html&quot;&gt;&lt;/img&gt;";
    @Nonnull private static final String XMLTEXT2 = "&lt;br&gt;&lt;/br&gt;&lt;img&gt;&lt;/img&gt;";
    @Nonnull private static final String XMLTEXT1 = "&lt;br&gt;&lt;/br&gt;&lt;img src=&quot;vn.png&quot; alt=&quot;Venn diagram&quot; longdesc=&quot;vn.html&quot; title=&quot;Venn&quot;&gt;&lt;/img&gt;";
    @Nonnull private static final String jul102008 = "2008-07-10";
    @Nonnull private static final String jul92008 = "2008-07-09";
    @Nonnull private static final String jul82008 = "2008-07-08";
    @Nonnull private static final String oct82007 = "2007-10-08";
    @Nonnull private static final String T20 = "T20:44:11.656+01:00";

    private static DatatypeEntry datatype(Datatype<?> d, int index) {
        return new DatatypeEntry(d).withIndex(index);
    }

    private static LiteralEntry literal(Datatype<?> d, String name, int index) {
        return new LiteralEntry(name).withLiteral(d.buildLiteral(name)).withIndex(index);
    }

    private JFactReasonerConfiguration config;
    private final DatatypeEntry literalDatatypeEntry4 = datatype(LITERAL, 4);
    private final DatatypeEntry integerDatatypeEntry6 = datatype(INTEGER, 6);
    private final DatatypeEntry nonposinteger4 = datatype(NONPOSITIVEINTEGER, 4);
    private final DatatypeEntry shortDatatypeEntry5 = datatype(SHORT, 5);
    private final DatatypeEntry unsignedbyteDatatypeEntry5 = datatype(UNSIGNEDBYTE, 5);
    private final LiteralEntry Agamidae = literal(PLAINLITERAL, "Agamidae@", 26);
    private final LiteralEntry Amphisbaenidae = literal(PLAINLITERAL, "Amphisbaenidae@", 10);
    private final LiteralEntry Anomalepidae = literal(PLAINLITERAL, "Anomalepidae@", 22);
    private final LiteralEntry Bipedidae = literal(PLAINLITERAL, "Bipedidae@", 34);
    private final LiteralEntry Cordylidae = literal(PLAINLITERAL, "Cordylidae@", 50);
    private final LiteralEntry Crocodylidae = literal(PLAINLITERAL, "Crocodylidae@", 30);
    private final LiteralEntry Emydidae = literal(PLAINLITERAL, "Emydidae@", 14);
    private final LiteralEntry Gekkonidae = literal(PLAINLITERAL, "Gekkonidae@", 42);
    private final LiteralEntry Leptotyphlopidae = literal(PLAINLITERAL, "Leptotyphlopidae@", 38);
    private final LiteralEntry Loxocemidae = literal(PLAINLITERAL, "Loxocemidae@", 46);
    private final LiteralEntry Sphenodontidae = literal(PLAINLITERAL, "Sphenodontidae@", 54);
    private final LiteralEntry Xantusiidae = literal(PLAINLITERAL, "Xantusiidae@", 18);
    private final LiteralEntry aString = literal(STRING, "aString", 5);
    private final LiteralEntry valueLiteral = literal(PLAINLITERAL, "value@", 5);
    private final LiteralEntry a_String = literal(STRING, "A string", 8);
    private final LiteralEntry name_1 = literal(NEGATIVEINTEGER, "-1", 5);
    private final LiteralEntry name0float = literal(FLOAT, "0.0", 7);
    private final LiteralEntry name0_float = literal(FLOAT, "-0.0", 5);
    private final LiteralEntry name0integer = literal(INTEGER, "0", 5);
    private final LiteralEntry name18integer = literal(INTEGER, "18", 5);
    private final LiteralEntry name1998_679 = literal(POSITIVEINTEGER, "1998", 679);
    private final LiteralEntry name1998_729 = literal(POSITIVEINTEGER, "1998", 729);
    private final LiteralEntry name19integer = literal(INTEGER, "19", 7);
    private final LiteralEntry name200710 = literal(DATETIME, oct82007 + T20, 7);
    private final LiteralEntry name2008075 = literal(DATETIME, jul82008 + T20, 5);
    private final LiteralEntry name2007 = literal(INT, "2007", 5);
    private final LiteralEntry name20080708 = literal(DATETIME, jul82008, 5);
    private final LiteralEntry name20080709 = literal(DATETIME, jul92008, 8);
    private final LiteralEntry name2008070920 = literal(DATETIME, jul92008 + T20, 8);
    private final LiteralEntry name20080710 = literal(DATETIME, jul102008, 8);
    private final LiteralEntry name2008071020 = literal(DATETIME, jul102008 + T20, 7);
    private final LiteralEntry name2008 = literal(INT, "2008", 8);
    private final LiteralEntry name3integer6 = literal(INTEGER, "3", 6);
    private final LiteralEntry name3int7 = literal(INT, "3", 7);
    private final LiteralEntry name6542145 = literal(INTEGER, "6542145", 8);
    private final LiteralEntry booleanFalse = literal(BOOLEAN, "false", 10);
    private final LiteralEntry booleanTrue = literal(BOOLEAN, "true", 10);
    private final LiteralEntry name2integer5 = literal(INTEGER, "2", 5);
    private final LiteralEntry name2short5 = literal(SHORT, "2", 5);
    private final LiteralEntry xml1 = literal(XMLLITERAL, XMLTEXT1, 7);
    private final LiteralEntry xml2 = literal(XMLLITERAL, XMLTEXT2, 7);
    private final LiteralEntry xml3 = literal(XMLLITERAL, XMLTEXT3, 7);
    private final LiteralEntry xml4 = literal(XMLLITERAL, XMLTEXT4, 5);
    private final LiteralEntry xml5 = literal(XMLLITERAL, XMLTEXT5, 5);
    private final DatatypeEntry byte4 = datatype(BYTE, 4);
    private final DatatypeEntry integer7 = datatype(INTEGER, 7);
    private final DatatypeEntry nonNegativeInteger6 = datatype(NONNEGATIVEINTEGER, 6);
    private final DatatypeEntry positiveInteger11 = datatype(POSITIVEINTEGER, 11);
    private final DatatypeEntry short3 = datatype(SHORT, 3);
    private final DatatypeEntry string4 = datatype(STRING, 4);
    private final LiteralEntry int0 = literal(INT, "0", 0);
    private final DatatypeEntry float_11 = datatype(FLOAT.wrapAsNumericExpression()
        .addNumericFacet(minInclusive, Float.valueOf(0F)).addNumericFacet(maxInclusive, Float.valueOf(1.17549435E-38F)),
        5);
    private final DatatypeEntry int_46 = datatype(INT.wrapAsNumericExpression()
        .addNumericFacet(minInclusive, Integer.valueOf(2008)).addNumericFacet(maxInclusive, Integer.valueOf(2009)), 7);
    private final DatatypeEntry int_63 = datatype(INT.wrapAsNumericExpression()
        .addNumericFacet(minInclusive, Integer.valueOf(2007)).addNumericFacet(maxInclusive, Integer.valueOf(2009))
        .addNonNumericFacet(fractionDigits, Integer.valueOf(0)), 5);
    private final DatatypeEntry integer_49 = datatype(INTEGER.wrapAsNumericExpression()
        .addNumericFacet(minInclusive, new BigDecimal("4")).addNonNumericFacet(fractionDigits, Integer.valueOf(0)), 9);
    private final DatatypeEntry integer_7 = datatype(INTEGER.wrapAsNumericExpression()
        .addNumericFacet(minInclusive, new BigDecimal("18")).addNonNumericFacet(fractionDigits, Integer.valueOf(0)), 5);
    private DataTypeReasoner datatypeReasoner;

    String s(NamedEntry dataEntry) {
        Object o = null;
        if (dataEntry instanceof DatatypeEntry) {
            o = ((DatatypeEntry) dataEntry).getDatatype();
        } else if (dataEntry instanceof LiteralEntry) {
            o = ((LiteralEntry) dataEntry).getLiteral();
        } else {
            o = dataEntry;
        }
        return o.toString();
    }

    static class DataCall {

        DagTag d;
        NamedEntry dataEntry;
        boolean positive;
        DepSet dep;
    }

    @Before
    public void setUp() {
        config = new JFactReasonerConfiguration();
        datatypeReasoner = new DataTypeReasoner(config);
    }

    private boolean makeCall(boolean positive, DagTag t, String s, int... depset) {
        return datatypeReasoner.addDataEntry(positive, t, getNamedEntry(s), getDepSet(depset));
    }

    private static DepSet getDepSet(int... s) {
        return DepSet.create(RoaringBitmap.bitmapOf(s));
    }

    public static String printDocument(Document doc) throws TransformerException {
        StringWriter w = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(doc), new StreamResult(w));
        return w.toString();
    }

    private final Map<String, NamedEntry> entries = getMap();

    public Map<String, NamedEntry> getMap() {
        Map<String, NamedEntry> toReturn = new HashMap<>();
        toReturn.put(XMLTEXT1, xml1);
        toReturn.put(XMLTEXT2, xml2);
        toReturn.put(XMLTEXT3, xml3);
        toReturn.put(XMLTEXT4, xml4);
        toReturn.put(XMLTEXT5, xml5);
        toReturn.put("\"6\"^^#integer", literal(INTEGER, "6", 6));
        toReturn.put("\"5\"^^#integer", literal(INTEGER, "5", 5));
        toReturn.put("\"1\"^^#integer", literal(INTEGER, "1", 1));
        toReturn.put("\"4\"^^#integer", literal(INTEGER, "4", 4));
        toReturn.put("\"4\"^^#int", literal(INT, "4", 4));
        toReturn.put("#Literal", literalDatatypeEntry4);
        toReturn.put("#integer", integerDatatypeEntry6);
        toReturn.put("#boolean", datatype(BOOLEAN, 1));
        toReturn.put("#nonPositiveInteger", nonposinteger4);
        toReturn.put("#short", shortDatatypeEntry5);
        toReturn.put("#unsignedByte", unsignedbyteDatatypeEntry5);
        toReturn.put("\"Agamidae@\"^^#PlainLiteral", Agamidae);
        toReturn.put("\"Amphisbaenidae@\"^^#PlainLiteral", Amphisbaenidae);
        toReturn.put("\"Anomalepidae@\"^^#PlainLiteral", Anomalepidae);
        toReturn.put("\"Bipedidae@\"^^#PlainLiteral", Bipedidae);
        toReturn.put("\"Cordylidae@\"^^#PlainLiteral", Cordylidae);
        toReturn.put("\"Crocodylidae@\"^^#PlainLiteral", Crocodylidae);
        toReturn.put("\"Emydidae@\"^^#PlainLiteral", Emydidae);
        toReturn.put("\"Gekkonidae@\"^^#PlainLiteral", Gekkonidae);
        toReturn.put("\"Leptotyphlopidae@\"^^#PlainLiteral", Leptotyphlopidae);
        toReturn.put("\"Loxocemidae@\"^^#PlainLiteral", Loxocemidae);
        toReturn.put("\"Sphenodontidae@\"^^#PlainLiteral", Sphenodontidae);
        toReturn.put("\"Xantusiidae@\"^^#PlainLiteral", Xantusiidae);
        toReturn.put("\"aString\"^^#string", aString);
        toReturn.put("\"value@\"^^#PlainLiteral", valueLiteral);
        toReturn.put("\"A string\"^^#string", a_String);
        toReturn.put("\"-1\"^^#negativeInteger", name_1);
        toReturn.put("\"0.0\"^^#float", name0float);
        toReturn.put("\"-0.0\"^^#float", name0_float);
        toReturn.put("\"0\"^^#integer", name0integer);
        toReturn.put("\"18\"^^#integer", name18integer);
        toReturn.put("\"1998\"^^#positiveInteger", name1998_679);
        toReturn.put("\"1998\"^^#positiveInteger", name1998_729);
        toReturn.put("\"19\"^^#integer", name19integer);
        toReturn.put("\"2007-10-08T20:44:11.656+01:00\"^^#dateTime", name200710);
        toReturn.put("\"2008-07-08T20:44:11.656+01:00\"^^#dateTime", name2008075);
        toReturn.put("\"2007\"^^#int", name2007);
        toReturn.put("\"2008-07-08\"^^#dateTime", name20080708);
        toReturn.put("\"2008-07-09\"^^#dateTime", name20080709);
        toReturn.put("\"2008-07-09T20:44:11.656+01:00\"^^#dateTime", name2008070920);
        toReturn.put("\"2008-07-10\"^^#dateTime", name20080710);
        toReturn.put("\"2008-07-10T20:44:11.656+01:00\"^^#dateTime", name2008071020);
        toReturn.put("\"2008\"^^#int", name2008);
        toReturn.put("\"3\"^^#integer", name3integer6);
        toReturn.put("\"3\"^^#int", name3int7);
        toReturn.put("\"6542145\"^^#integer", name6542145);
        toReturn.put("\"false\"^^#boolean", booleanFalse);
        toReturn.put("\"true\"^^#boolean", booleanTrue);
        toReturn.put("\"2\"^^#integer", name2integer5);
        toReturn.put("\"2\"^^#short", name2short5);
        toReturn.put("#byte", byte4);
        toReturn.put("#integer", integer7);
        toReturn.put("#nonNegativeInteger", nonNegativeInteger6);
        toReturn.put("#positiveInteger", positiveInteger11);
        toReturn.put("#short", short3);
        toReturn.put("#string", string4);
        toReturn.put("\"0\"^^#int", int0);
        toReturn.put("numeric(#float(extra facets:0.0 1.17549435E-38))", float_11);
        toReturn.put("numeric(#int(extra facets:2008 2009))", int_46);
        toReturn.put("numeric(#int(extra facets:2007 2009))", int_63);
        toReturn.put("numeric(#integer(extra facets:4 null))", integer_49);
        toReturn.put("numeric(#integer(extra facets:18 null))", integer_7);
        toReturn.put("#dateTime( minInclusive 2008-09-08 19:44:11)",
            datatype(DATETIME.wrapAsOrderedExpression().addNumericFacet(minInclusive, date(2008, 9, 8, 19, 44)),
                toReturn.size()));
        toReturn.put("#dateTime( minInclusive 2008-06-08 maxInclusive 2008-06-10)",
            datatype(DATETIME.wrapAsOrderedExpression().addNumericFacet(minInclusive, date(2008, 6, 8))
                .addNumericFacet(maxInclusive, date(2008, 6, 10)), toReturn.size()));
        toReturn.put("#dateTime( minInclusive 2008-06-08 19:44:11 maxInclusive 2008-06-10 19:44:11)",
            datatype(DATETIME.wrapAsOrderedExpression().addNumericFacet(minInclusive, date(2008, 6, 8, 19, 44, 11))
                .addNumericFacet(maxInclusive, date(2008, 6, 10, 19, 44, 11)), toReturn.size()));
        toReturn.put("#dateTime( minInclusive 2008-06-08 19:44:11 maxInclusive 2008-09-08 19:44:11)",
            datatype(DATETIME.wrapAsOrderedExpression().addNumericFacet(minInclusive, date(2008, 6, 8, 19, 44, 11))
                .addNumericFacet(maxInclusive, date(2008, 9, 8, 19, 44, 11)), toReturn.size()));
        toReturn.put("#dateTime( minInclusive 2007-09-08 19:44:11 maxInclusive 2009-09-08 19:44:11)",
            datatype(DATETIME.wrapAsOrderedExpression().addNumericFacet(minInclusive, date(2007, 9, 8, 19, 44, 11))
                .addNumericFacet(maxInclusive, date(2009, 9, 8, 19, 44, 11)), toReturn.size()));
        toReturn.put("#dateTime( maxInclusive 2008-09-08 19:44:11)",
            datatype(DATETIME.wrapAsOrderedExpression().addNumericFacet(maxInclusive, date(2008, 9, 8, 19, 44, 11)),
                toReturn.size()));
        toReturn.put("#dateTime( minInclusive 2008-06-08 maxInclusive 2008-10-10)",
            datatype(DATETIME.wrapAsOrderedExpression().addNumericFacet(minInclusive, date(2008, 6, 8))
                .addNumericFacet(maxInclusive, date(2008, 10, 10)), toReturn.size()));
        return toReturn;
    }

    private static Date date(int year, int month, int day) {
        return new DateTime(year, month, day, 0, 0).toDate();
    }

    private static Date date(int year, int month, int day, int hour, int minute) {
        return new DateTime(year, month, day, hour, minute).toDate();
    }

    private static Date date(int year, int month, int day, int hour, int minute, int second) {
        return new DateTime(year, month, day, hour, minute, second, 0).toDate();
    }

    @Nullable
    private NamedEntry getNamedEntry(String e) {
        if (entries.containsKey(e)) {
            return entries.get(e);
        }
        System.out.println(e);
        return null;
    }

    @Test
    public void test1() {
        makeCall(true, DATATYPE, "#integer");
        makeCall(true, DATATYPE, "#string");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test2() {
        makeCall(true, DATAVALUE, "\"0\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test3() {
        makeCall(true, DATAVALUE, "\"0.0\"^^#float");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test4() {
        makeCall(true, DATAVALUE, "\"0.0\"^^#float");
        makeCall(true, DATAVALUE, "\"-0.0\"^^#float");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test8() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test9() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"3\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test10() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test11() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(true, DATAVALUE, "\"2\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test12() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test13() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test14() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 2);
        makeCall(true, DATAVALUE, "\"1\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test15() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test16() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test17() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"1\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test18() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"1\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"4\"^^#integer", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test19() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test20() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test21() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"3\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test22() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test23() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(true, DATAVALUE, "\"2\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test24() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test25() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test26() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 2);
        makeCall(true, DATAVALUE, "\"1\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test27() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test28() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test29() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"1\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test30() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"1\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"4\"^^#integer", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test31() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test32() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer");
        makeCall(false, DATAVALUE, "\"5\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test33() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer");
        makeCall(false, DATAVALUE, "\"5\"^^#integer");
        makeCall(true, DATAVALUE, "\"4\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test34() {
        makeCall(true, DATAEXPR, "numeric(#integer(extra facets:18 null))");
        assertFalse(makeCall(true, DATAEXPR, "numeric(#integer(extra facets:18 null))"));
    }

    @Test
    public void test35() {
        makeCall(true, DATAVALUE, "\"2008-07-08T20:44:11.656+01:00\"^^#dateTime");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    @Ignore
    public void test36() {
        makeCall(true, DATAEXPR, "numeric(#float(extra facets:0.0 1.17549435E-38))");
        assertTrue(makeCall(true, DATAEXPR, "numeric(#float(extra facets:0.0 1.17549435E-38))"));
    }

    @Test
    public void test37() {
        makeCall(true, DATAVALUE, "\"2008-07-09T20:44:11.656+01:00\"^^#dateTime");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test38() {
        makeCall(true, DATAEXPR, "#dateTime( minInclusive 2008-06-08 19:44:11 maxInclusive 2008-06-10 19:44:11)");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test44() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test45() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"3\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test46() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test47() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(true, DATAVALUE, "\"2\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test48() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test49() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test50() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 2);
        makeCall(true, DATAVALUE, "\"1\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test51() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test52() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test53() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"1\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test54() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"1\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"4\"^^#integer", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test55() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test56() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test57() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"3\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test58() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test59() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(true, DATAVALUE, "\"2\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test60() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test61() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test62() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 2);
        makeCall(true, DATAVALUE, "\"1\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test63() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test64() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test65() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"1\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test66() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"1\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"4\"^^#integer", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test67() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test68() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer");
        makeCall(false, DATAVALUE, "\"5\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test69() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer");
        makeCall(false, DATAVALUE, "\"5\"^^#integer");
        makeCall(true, DATAVALUE, "\"4\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test70() {
        makeCall(true, DATAVALUE, "\"2007-10-08T20:44:11.656+01:00\"^^#dateTime");
        makeCall(true, DATAEXPR, "#dateTime( minInclusive 2008-06-08 19:44:11 maxInclusive 2008-09-08 19:44:11)");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test71() {
        makeCall(true, DATAVALUE, "\"2007-10-08T20:44:11.656+01:00\"^^#dateTime");
        makeCall(true, DATAEXPR, "#dateTime( minInclusive 2008-06-08 19:44:11 maxInclusive 2008-09-08 19:44:11)");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test72() {
        makeCall(false, DATAVALUE, "\"3\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test73() {
        makeCall(false, DATAVALUE, "\"3\"^^#integer");
        makeCall(true, DATAVALUE, "\"2\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test74() {
        makeCall(false, DATAVALUE, "\"3\"^^#integer");
        makeCall(true, DATAVALUE, "\"2\"^^#integer");
        makeCall(true, DATAVALUE, "\"4\"^^#integer");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test75() {
        makeCall(true, DATATYPE, "#boolean");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test76() {
        makeCall(true, DATATYPE, "#boolean");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test77() {
        makeCall(true, DATATYPE, "#boolean");
        makeCall(false, DATAVALUE, "\"true\"^^#boolean");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test78() {
        makeCall(true, DATATYPE, "#boolean");
        makeCall(false, DATAVALUE, "\"false\"^^#boolean");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test79() {
        makeCall(true, DATATYPE, "#boolean");
        makeCall(false, DATAVALUE, "\"true\"^^#boolean");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test80() {
        makeCall(true, DATATYPE, "#boolean");
        makeCall(false, DATAVALUE, "\"false\"^^#boolean");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test81() {
        makeCall(true, DATAVALUE, "\"19\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test82() {
        makeCall(true, DATAVALUE, "\"19\"^^#integer");
        makeCall(true, DATAVALUE, "\"18\"^^#integer");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test83() {
        makeCall(true, DATAVALUE, "\"2007\"^^#int");
        makeCall(true, DATAEXPR, "numeric(#int(extra facets:2008 2009))");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test84() {
        makeCall(true, DATAEXPR, "numeric(#integer(extra facets:4 null))");
        assertFalse(makeCall(true, DATAEXPR, "numeric(#integer(extra facets:4 null))"));
    }

    @Test
    public void test85() {
        makeCall(true, DATAVALUE, "\"aString\"^^#string");
        makeCall(true, DATATYPE, "#integer");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test86() {
        makeCall(true, DATAVALUE, "\"0\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test87() {
        makeCall(true, DATAVALUE, "\"6542145\"^^#integer");
        makeCall(true, DATATYPE, "#byte");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test88() {
        makeCall(true, DATAVALUE, "\"2008-07-08T20:44:11.656+01:00\"^^#dateTime");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test89() {
        makeCall(true, DATAEXPR, "#dateTime( minInclusive 2007-09-08 19:44:11 maxInclusive 2009-09-08 19:44:11)");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test90() {
        makeCall(true, DATAVALUE, "\"2008-07-08T20:44:11.656+01:00\"^^#dateTime");
        makeCall(false, DATAEXPR, "#dateTime( minInclusive 2007-09-08 19:44:11 maxInclusive 2009-09-08 19:44:11)");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test91() {
        makeCall(true, DATAVALUE, "\"2008-07-08T20:44:11.656+01:00\"^^#dateTime");
        makeCall(false, DATAEXPR, "#dateTime( minInclusive 2007-09-08 19:44:11 maxInclusive 2009-09-08 19:44:11)");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test95() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test96() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"3\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test97() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test98() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(true, DATAVALUE, "\"2\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test99() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test100() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test101() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 2);
        makeCall(true, DATAVALUE, "\"1\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test102() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test103() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test104() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"1\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test105() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(true, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"1\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"4\"^^#integer", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test106() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test107() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test108() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"3\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test109() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test110() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(true, DATAVALUE, "\"2\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test111() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test112() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test113() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 2);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 2);
        makeCall(true, DATAVALUE, "\"1\"^^#integer", 2);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test114() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test115() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test116() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"1\"^^#integer", 1);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test117() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"5\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"3\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"2\"^^#integer", 1);
        makeCall(false, DATAVALUE, "\"1\"^^#integer", 1);
        makeCall(true, DATAVALUE, "\"4\"^^#integer", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test118() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test119() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer");
        makeCall(false, DATAVALUE, "\"5\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test120() {
        makeCall(true, DATATYPE, "#Literal");
        makeCall(false, DATAVALUE, "\"6\"^^#integer");
        makeCall(false, DATAVALUE, "\"5\"^^#integer");
        makeCall(true, DATAVALUE, "\"4\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test121() {
        makeCall(true, DATAVALUE, "\"2008\"^^#int");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test122() {
        makeCall(true, DATAEXPR, "numeric(#int(extra facets:2007 2009))");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test123() {
        makeCall(true, DATAVALUE, "\"2008\"^^#int");
        makeCall(false, DATAEXPR, "numeric(#int(extra facets:2007 2009))");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test124() {
        makeCall(true, DATAVALUE, "\"2008\"^^#int");
        makeCall(false, DATAEXPR, "numeric(#int(extra facets:2007 2009))");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test125() {
        makeCall(true, DATAVALUE, "\"2008-07-10T20:44:11.656+01:00\"^^#dateTime");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test126() {
        makeCall(true, DATAVALUE, "\"2008-07-10T20:44:11.656+01:00\"^^#dateTime");
        makeCall(true, DATAVALUE, "\"2008-07-08T20:44:11.656+01:00\"^^#dateTime");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test127() {
        makeCall(true, DATATYPE, "#integer");
        makeCall(false, DATATYPE, "#Literal");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test128() {
        makeCall(true, DATAVALUE, "\"A string\"^^#string");
        makeCall(false, DATATYPE, "#positiveInteger");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test129() {
        makeCall(true, DATAVALUE, "\"-1\"^^#negativeInteger");
        makeCall(false, DATATYPE, "#positiveInteger");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test130() {
        makeCall(true, DATAEXPR, "#dateTime( minInclusive 2008-09-08 19:44:11)");
        makeCall(true, DATAEXPR, "#dateTime( maxInclusive 2008-09-08 19:44:11)");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test131() {
        makeCall(true, DATAVALUE, "\"18\"^^#integer");
        makeCall(true, DATATYPE, "#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test132() {
        makeCall(false, DATAVALUE, "\"3\"^^#integer");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test133() {
        makeCall(false, DATAVALUE, "\"3\"^^#integer");
        makeCall(true, DATAVALUE, "\"2\"^^#short");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test134() {
        makeCall(false, DATAVALUE, "\"3\"^^#integer");
        makeCall(true, DATAVALUE, "\"2\"^^#short");
        makeCall(true, DATAVALUE, "\"4\"^^#int");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test135() {
        makeCall(true, DATAVALUE, "\"1998\"^^#positiveInteger");
        makeCall(true, DATATYPE, "#positiveInteger");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test136() {
        makeCall(true, DATAVALUE, "\"1998\"^^#positiveInteger");
        makeCall(true, DATATYPE, "#positiveInteger");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test137() {
        makeCall(true, DATAVALUE, XMLTEXT2);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test138() {
        makeCall(true, DATAVALUE, XMLTEXT3);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test139() {
        makeCall(true, DATAVALUE, XMLTEXT3);
        makeCall(true, DATAVALUE, XMLTEXT1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test140() {
        makeCall(true, DATAVALUE, XMLTEXT4);
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test141() {
        makeCall(true, DATAVALUE, XMLTEXT4);
        makeCall(true, DATAVALUE, XMLTEXT5);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test142() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test143() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test144() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test145() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test146() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test147() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test148() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test149() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test150() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test151() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test152() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test153() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test154() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test155() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test156() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test157() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test158() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test159() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test160() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test161() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test162() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test163() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test164() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test165() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test166() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test167() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test168() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test169() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test170() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test171() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test172() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test173() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test174() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test175() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test176() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test177() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test178() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test179() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test180() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test181() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test182() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test183() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test184() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test185() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test186() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test187() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test188() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test189() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test190() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test191() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test192() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test193() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test194() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test195() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test196() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test197() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test198() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test199() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test200() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test201() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test202() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test203() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test204() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test205() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test206() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test207() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test208() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test209() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test210() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test211() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test212() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test213() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test214() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test215() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test216() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test217() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test218() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test219() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test220() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test221() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test222() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test223() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test224() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test225() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test226() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test227() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test228() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test229() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test230() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test231() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test232() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test233() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test234() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test235() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test236() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test237() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test238() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test239() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test240() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test241() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test242() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test243() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test244() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test245() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test246() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test247() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test248() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test249() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test250() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test251() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test252() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test253() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test254() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test255() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test256() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test257() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test258() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test259() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test260() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test261() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test262() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test263() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test264() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test265() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test266() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test267() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test268() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test269() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test270() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test271() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test272() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test273() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test274() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test275() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test276() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test277() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test278() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test279() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test280() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test281() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test282() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test283() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test284() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test285() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test286() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test287() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test288() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test289() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test290() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test291() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test292() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test293() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test294() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test295() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test296() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test297() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test298() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test299() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test300() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test301() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test302() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test303() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test304() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test305() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test306() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test307() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test308() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test309() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test310() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test311() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test312() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test313() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test314() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test315() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test316() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test317() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test318() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test319() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test320() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test321() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test322() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test323() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test324() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test325() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test326() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test327() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test328() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test329() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test330() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test331() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test332() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test333() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test334() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test335() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test336() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test337() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test338() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test339() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test340() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test341() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test342() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test343() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test344() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test345() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test346() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test347() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test348() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test349() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test350() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test351() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test352() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test353() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test354() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test355() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test356() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test357() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test358() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test359() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test360() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test361() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test362() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test363() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test364() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test365() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test366() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test367() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test368() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test369() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test370() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test371() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test372() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test373() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test374() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test375() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test376() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test377() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test378() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test379() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test380() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test381() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test382() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test383() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test384() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test385() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test386() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test387() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test388() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test389() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test390() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test391() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test392() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test393() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test394() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test395() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test396() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test397() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test398() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test399() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test400() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test401() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test402() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test403() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test404() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test405() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test406() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test407() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test408() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test409() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test410() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test411() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test412() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test413() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test414() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test415() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test416() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test417() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test418() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test419() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test420() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test421() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test422() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test423() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test424() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test425() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test426() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test427() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test428() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test429() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test430() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test431() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test432() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test433() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test434() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test435() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test436() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test437() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test438() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test439() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test440() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test441() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test442() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test443() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test444() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test445() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test446() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test447() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test448() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test449() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test450() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test451() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test452() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test453() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test454() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test455() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test456() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test457() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test458() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test459() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test460() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test461() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test462() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test463() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test464() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test465() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test466() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test467() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test468() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test469() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test470() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test471() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test472() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test473() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test474() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test475() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test476() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test477() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test478() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test479() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test480() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test481() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test482() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test483() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test484() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test485() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test486() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test487() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test488() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test489() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test490() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test491() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test492() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test493() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test494() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test495() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test496() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test497() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test498() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test499() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test500() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test501() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test502() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test503() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test504() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test505() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test506() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test507() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test508() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test509() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test510() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test511() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test512() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test513() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test514() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test515() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test516() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test517() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test518() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test519() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test520() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test521() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test522() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test523() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test524() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test525() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test526() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test527() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test528() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test529() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test530() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test531() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test532() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test533() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test534() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test535() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test536() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test537() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test538() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test539() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test540() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test541() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test542() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test543() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test544() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test545() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test546() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test547() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test548() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test549() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test550() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test551() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test552() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test553() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test554() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test555() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test556() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test557() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test558() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test559() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test560() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test561() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test562() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test563() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test564() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test565() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test566() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test567() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test568() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test569() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test570() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test571() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test572() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test573() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test574() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test575() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test576() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test577() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test578() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test579() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test580() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test581() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test582() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test583() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test584() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test585() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test586() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test587() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test588() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test589() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test590() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test591() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test592() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test593() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test594() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test595() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test596() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test597() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test598() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test599() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test600() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test601() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test602() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test603() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test604() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test605() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test606() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test607() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test608() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test609() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test610() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test611() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test612() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test613() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test614() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test615() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test616() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test617() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test618() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test619() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test620() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test621() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test622() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test623() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test624() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test625() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test626() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test627() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test628() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test629() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test630() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test631() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test632() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test633() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test634() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test635() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test636() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test637() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test638() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test639() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test640() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test641() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test642() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test643() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test644() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test645() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test646() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test647() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test648() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test649() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test650() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test651() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test652() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test653() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test654() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test655() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test656() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test657() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test658() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test659() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test660() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test661() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test662() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test663() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test664() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test665() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test666() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test667() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test668() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test669() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test670() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test671() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test672() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test673() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test674() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test675() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test676() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test677() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test678() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test679() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test680() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test681() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test682() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test683() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test684() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test685() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test686() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test687() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test688() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test689() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test690() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test691() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test692() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test693() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test694() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test695() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test696() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test697() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test698() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test699() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test700() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test701() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test702() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test703() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test704() {
        makeCall(true, DATAVALUE, "\"Crocodylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test705() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test706() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test707() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test708() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test709() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test710() {
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test711() {
        makeCall(true, DATAVALUE, "\"Agamidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Anomalepidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test712() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test713() {
        makeCall(true, DATAVALUE, "\"Gekkonidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test714() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test715() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test716() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test717() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test718() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test719() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test720() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test721() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test722() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test723() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test724() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test725() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test726() {
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test727() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Xantusiidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test728() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test729() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test730() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test731() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test732() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test733() {
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test734() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Emydidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test735() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test736() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test737() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test738() {
        makeCall(true, DATAVALUE, "\"Leptotyphlopidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test739() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test740() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test741() {
        makeCall(true, DATAVALUE, "\"Sphenodontidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test742() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test743() {
        makeCall(true, DATAVALUE, "\"Bipedidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test744() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test745() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test746() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test747() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test748() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test749() {
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test750() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Loxocemidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test751() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test752() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test753() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test754() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test755() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(false, DATATYPE, "#Literal", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test756() {
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral");
        makeCall(true, DATATYPE, "#Literal");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test757() {
        makeCall(true, DATAVALUE, "\"Cordylidae@\"^^#PlainLiteral", 1);
        makeCall(true, DATATYPE, "#Literal", 1);
        makeCall(true, DATAVALUE, "\"Amphisbaenidae@\"^^#PlainLiteral", 1);
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test758() {
        makeCall(false, DATATYPE, "#short");
        makeCall(true, DATATYPE, "#byte");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test889() {
        makeCall(true, DATAVALUE, "\"value@\"^^#PlainLiteral");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test890() {
        makeCall(false, DATATYPE, "#unsignedByte");
        makeCall(true, DATATYPE, "#short");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test891() {
        makeCall(true, DATAVALUE, "\"2008-07-08T20:44:11.656+01:00\"^^#dateTime");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test892() {
        makeCall(true, DATAVALUE, "\"2008-07-10\"^^#dateTime");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test893() {
        makeCall(true, DATAVALUE, "\"2008-07-10\"^^#dateTime");
        makeCall(true, DATAVALUE, "\"2008-07-08\"^^#dateTime");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test894() {
        makeCall(true, DATAVALUE, "\"2008-07-09T20:44:11.656+01:00\"^^#dateTime");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test895() {
        makeCall(true, DATAEXPR, "#dateTime( minInclusive 2008-06-08 19:44:11 maxInclusive 2008-06-10 19:44:11)");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test898() {
        makeCall(true, DATAVALUE, "\"2008-07-09\"^^#dateTime");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test899() {
        makeCall(true, DATAEXPR, "#dateTime( minInclusive 2008-06-08 maxInclusive 2008-06-10)");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test901() {
        makeCall(true, DATAVALUE, "\"2008-07-09\"^^#dateTime");
        makeCall(false, DATAEXPR, "#dateTime( minInclusive 2008-06-08 maxInclusive 2008-06-10)");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test901_() {
        makeCall(true, DATAVALUE, "\"2008-07-09\"^^#dateTime");
        makeCall(false, DATAEXPR, "#dateTime( minInclusive 2008-06-08 maxInclusive 2008-10-10)");
        assertTrue(datatypeReasoner.checkClash());
    }

    @Test
    public void test902() {
        makeCall(true, DATAVALUE, "\"2008-07-08\"^^#dateTime");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test903() {
        makeCall(true, DATAVALUE, "\"2008-07-10T20:44:11.656+01:00\"^^#dateTime");
        assertFalse(datatypeReasoner.checkClash());
    }

    @Test
    public void test904() {
        makeCall(true, DATAVALUE, "\"2008-07-10T20:44:11.656+01:00\"^^#dateTime");
        makeCall(true, DATAVALUE, "\"2008-07-08T20:44:11.656+01:00\"^^#dateTime");
        assertTrue(datatypeReasoner.checkClash());
    }
}
