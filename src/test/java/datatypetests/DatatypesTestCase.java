package datatypetests;

import static org.junit.jupiter.api.Assertions.assertEquals;
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.BOOLEAN;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.BYTE;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.DATETIME;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.FLOAT;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.INT;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.INTEGER;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.LITERAL;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.NEGATIVEINTEGER;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.NONNEGATIVEINTEGER;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.NONPOSITIVEINTEGER;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.PLAINLITERAL;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.POSITIVEINTEGER;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.SHORT;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.STRING;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.UNSIGNEDBYTE;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.XMLLITERAL;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.fractionDigits;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.maxInclusive;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.minInclusive;
import static uk.ac.manchester.cs.jfact.kernel.DagTag.DATAEXPR;
import static uk.ac.manchester.cs.jfact.kernel.DagTag.DATATYPE;
import static uk.ac.manchester.cs.jfact.kernel.DagTag.DATAVALUE;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.roaringbitmap.RoaringBitmap;
import org.w3c.dom.Document;

import uk.ac.manchester.cs.jfact.datatypes.DataTypeReasoner;
import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeEntry;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeExpression;
import uk.ac.manchester.cs.jfact.datatypes.LiteralEntry;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

class DatatypesTestCase {
    private static final String NUMERIC_FLOAT = "numeric(#float(extra facets:0.0 1.17549435E-38))";
    private static final String _2_INTEGER = "\"2\"^^integer";
    private static final String _1_INTEGER = "\"1\"^^integer";
    private static final String _3_INTEGER = "\"3\"^^integer";
    private static final String LITERAL2 = "#Literal";
    private static final String _4_INTEGER = "\"4\"^^integer";
    private static final String _5_INTEGER = "\"5\"^^integer";
    private static final String _6_INTEGER = "\"6\"^^integer";
    private static final String AGAMIDAE = "\"Agamidae@\"^^PlainLiteral";
    private static final String XANTUSIIDAE = "\"Xantusiidae@\"^^PlainLiteral";
    private static final String AMPHISBAENIDAE = "\"Amphisbaenidae@\"^^PlainLiteral";
    private static final String SPHENODONTIDAE = "\"Sphenodontidae@\"^^PlainLiteral";
    private static final String LEPTOTYPHLOPIDAE = "\"Leptotyphlopidae@\"^^PlainLiteral";
    private static final String GEKKONIDAE = "\"Gekkonidae@\"^^PlainLiteral";
    private static final String EMYDIDAE = "\"Emydidae@\"^^PlainLiteral";
    private static final String ANOMALEPIDAE = "\"Anomalepidae@\"^^PlainLiteral";
    private static final String CROCODYLIDAE = "\"Crocodylidae@\"^^PlainLiteral";
    private static final String LOXOCEMIDAE = "\"Loxocemidae@\"^^PlainLiteral";
    private static final String BIPEDIDAE = "\"Bipedidae@\"^^PlainLiteral";
    private static final String CORDYLIDAE = "\"Cordylidae@\"^^PlainLiteral";

    private static DatatypeEntry datatype(Datatype<?> d, int index) {
        return new DatatypeEntry(d).withIndex(index);
    }

    private static LiteralEntry literal(Datatype<?> d, String name, int index) {
        return new LiteralEntry(name).withLiteral(d.buildLiteral(name)).withIndex(index);
    }

    private JFactReasonerConfiguration config;
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

    @BeforeEach
    void setUp() {
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
        Stream.of(literal(XMLLITERAL,
            "&lt;br&gt;&lt;/br&gt;&lt;img src=&quot;vn.png&quot; alt=&quot;Venn diagram&quot; longdesc=&quot;vn.html&quot; title=&quot;Venn&quot;&gt;&lt;/img&gt;",
            7), literal(XMLLITERAL, "&lt;br&gt;&lt;/br&gt;&lt;img&gt;&lt;/img&gt;", 7),
            literal(XMLLITERAL,
                "&lt;br&gt;&lt;/br&gt;&lt;img src=&quot;vn.png&quot; title=&quot;Venn&quot; alt=&quot;Venn diagram&quot; longdesc=&quot;vn.html&quot;&gt;&lt;/img&gt;",
                7),
            literal(XMLLITERAL,
                "&lt;span xml:lang=&quot;en&quot;&gt;&lt;b&gt;Bad!&lt;/b&gt;&lt;/span&gt;", 5),
            literal(XMLLITERAL,
                "&lt;span xml:lang=&quot;en&quot;&gt;&lt;b&gt;Good!&lt;/b&gt;&lt;/span&gt;", 5),
            literal(INTEGER, "6", 6), literal(INTEGER, "5", 5), literal(INTEGER, "1", 1),
            literal(INTEGER, "4", 4), literal(INT, "4", 4), plain("Agamidae@", 26),
            plain("Amphisbaenidae@", 10), plain("Anomalepidae@", 22), plain("Bipedidae@", 34),
            plain("Cordylidae@", 50), plain("Crocodylidae@", 30), plain("Emydidae@", 14),
            plain("Gekkonidae@", 42), plain("Leptotyphlopidae@", 38), plain("Loxocemidae@", 46),
            plain("Sphenodontidae@", 54), plain("Xantusiidae@", 18), literal(STRING, "aString", 5),
            plain("value@", 5), literal(STRING, "A string", 8), literal(NEGATIVEINTEGER, "-1", 5),
            literal(FLOAT, "0.0", 7), literal(FLOAT, "-0.0", 5), literal(INTEGER, "0", 5),
            literal(INTEGER, "18", 5), literal(POSITIVEINTEGER, "1998", 679),
            literal(POSITIVEINTEGER, "1998", 729), literal(INTEGER, "19", 7),
            literal(DATETIME, "2007-10-08" + "T20:44:11.656+01:00", 7),
            literal(DATETIME, "2008-07-08" + "T20:44:11.656+01:00", 5), literal(INT, "2007", 5),
            literal(DATETIME, "2008-07-08", 5), literal(DATETIME, "2008-07-09", 8),
            literal(DATETIME, "2008-07-09" + "T20:44:11.656+01:00", 8),
            literal(DATETIME, "2008-07-10", 8),
            literal(DATETIME, "2008-07-10" + "T20:44:11.656+01:00", 7), literal(INT, "2008", 8),
            literal(INTEGER, "3", 6), literal(INT, "3", 7), literal(INTEGER, "6542145", 8),
            literal(BOOLEAN, "false", 10), literal(BOOLEAN, "true", 10), literal(INTEGER, "2", 5),
            literal(SHORT, "2", 5), literal(INT, "0", 0))
            .forEach(x -> toReturn.put(x.getLiteral().toString(), x));
        toReturn.put(LITERAL2, datatype(LITERAL, 4));
        toReturn.put("#integer", datatype(INTEGER, 6));
        toReturn.put("#boolean", datatype(BOOLEAN, 1));
        toReturn.put("#nonPositiveInteger", datatype(NONPOSITIVEINTEGER, 4));
        toReturn.put("#short", datatype(SHORT, 5));
        toReturn.put("#unsignedByte", datatype(UNSIGNEDBYTE, 5));

        toReturn.put("#byte", datatype(BYTE, 4));
        toReturn.put("#integer", datatype(INTEGER, 7));
        toReturn.put("#nonNegativeInteger", datatype(NONNEGATIVEINTEGER, 6));
        toReturn.put("#positiveInteger", datatype(POSITIVEINTEGER, 11));
        toReturn.put("#short", datatype(SHORT, 3));
        toReturn.put("#string", datatype(STRING, 4));
        toReturn.put(NUMERIC_FLOAT,
            datatype(
                FLOAT.wrapAsNumericExpression().addNumericFacet(minInclusive, Float.valueOf(0F))
                    .addNumericFacet(maxInclusive, Float.valueOf(1.17549435E-38F)),
                5));
        toReturn.put("numeric(#int(extra facets:2008 2009))",
            datatype(
                INT.wrapAsNumericExpression().addNumericFacet(minInclusive, Integer.valueOf(2008))
                    .addNumericFacet(maxInclusive, Integer.valueOf(2009)),
                7));
        toReturn.put("numeric(#int(extra facets:2007 2009))",
            datatype(
                INT.wrapAsNumericExpression().addNumericFacet(minInclusive, Integer.valueOf(2007))
                    .addNumericFacet(maxInclusive, Integer.valueOf(2009))
                    .addNonNumericFacet(fractionDigits, Integer.valueOf(0)),
                5));
        toReturn.put("numeric(#integer(extra facets:4 null))",
            datatype(
                INTEGER.wrapAsNumericExpression().addNumericFacet(minInclusive, new BigDecimal("4"))
                    .addNonNumericFacet(fractionDigits, Integer.valueOf(0)),
                9));
        toReturn.put("numeric(#integer(extra facets:18 null))",
            datatype(INTEGER.wrapAsNumericExpression()
                .addNumericFacet(minInclusive, new BigDecimal("18"))
                .addNonNumericFacet(fractionDigits, Integer.valueOf(0)), 5));

        toReturn.put("#dateTime( minInclusive 2008-09-08 19:44:11)",
            datatype(DATETIME.wrapAsOrderedExpression().addNumericFacet(minInclusive,
                date(2008, 9, 8, 19, 44)), toReturn.size()));
        toReturn.put("#dateTime( minInclusive 2008-06-08 maxInclusive 2008-06-10)",
            datatype(minmax(date(2008, 6, 8), date(2008, 6, 10)), toReturn.size()));
        toReturn.put(
            "#dateTime( minInclusive 2008-06-08 19:44:11 maxInclusive 2008-06-10 19:44:11)",
            datatype(minmax(date1(2008, 6, 8), date1(2008, 6, 10)), toReturn.size()));
        toReturn.put(
            "#dateTime( minInclusive 2008-06-08 19:44:11 maxInclusive 2008-09-08 19:44:11)",
            datatype(minmax(date1(2008, 6, 8), date1(2008, 9, 8)), toReturn.size()));
        toReturn.put(
            "#dateTime( minInclusive 2007-09-08 19:44:11 maxInclusive 2009-09-08 19:44:11)",
            datatype(minmax(date1(2007, 9, 8), date1(2009, 9, 8)), toReturn.size()));
        toReturn.put("#dateTime( maxInclusive 2008-09-08 19:44:11)",
            datatype(
                DATETIME.wrapAsOrderedExpression().addNumericFacet(maxInclusive, date1(2008, 9, 8)),
                toReturn.size()));
        toReturn.put("#dateTime( minInclusive 2008-06-08 maxInclusive 2008-10-10)",
            datatype(minmax(date(2008, 6, 8), date(2008, 10, 10)), toReturn.size()));
        return toReturn;
    }

    protected LiteralEntry plain(String s, int i) {
        return literal(PLAINLITERAL, s, i);
    }

    protected DatatypeExpression<Date> minmax(Date a1, Date a2) {
        return DATETIME.wrapAsOrderedExpression().addNumericFacet(minInclusive, a1)
            .addNumericFacet(maxInclusive, a2);
    }

    private static Date date(int year, int month, int day) {
        return new DateTime(year, month, day, 0, 0).toDate();
    }

    private static Date date(int year, int month, int day, int hour, int minute) {
        return new DateTime(year, month, day, hour, minute).toDate();
    }

    private static Date date1(int year, int month, int day) {
        return new DateTime(year, month, day, 19, 44, 11, 0).toDate();
    }

    @Nullable
    private NamedEntry getNamedEntry(String e) {
        NamedEntry namedEntry = entries.get(e);
        if (namedEntry == null) {
            System.out.println("\t" + e);
            entries.keySet().forEach(System.out::println);
        }
        return namedEntry;
    }

    @Disabled("float representation doesn't allow this")
    @Test
    void test36() {
        makeCall(true, DATAEXPR, NUMERIC_FLOAT);
        assertTrue(makeCall(true, DATAEXPR, NUMERIC_FLOAT));
    }

    @Test
    void test72() {
        makeCall(false, DATAVALUE, _3_INTEGER);
        clash(false);
    }

    @Test
    void test31() {
        makeCall(true, DATATYPE, LITERAL2);
        makeCall(false, DATAVALUE, _6_INTEGER);
        clash(false);
    }

    @Test
    void test32() {
        makeCall(true, DATATYPE, LITERAL2);
        makeCall(false, DATAVALUE, _6_INTEGER);
        makeCall(false, DATAVALUE, _5_INTEGER);
        clash(false);
    }

    @Test
    void test33() {
        makeCall(true, DATATYPE, LITERAL2);
        makeCall(false, DATAVALUE, _6_INTEGER);
        makeCall(false, DATAVALUE, _5_INTEGER);
        makeCall(true, DATAVALUE, _4_INTEGER);
        clash(false);
    }



    @Test
    void test30() {
        makeCall(true, DATATYPE, LITERAL2);
        makeCall(false, DATAVALUE, _6_INTEGER, 1);
        makeCall(true, DATAVALUE, _5_INTEGER, 1);
        makeCall(false, DATAVALUE, _3_INTEGER, 1);
        makeCall(false, DATAVALUE, _2_INTEGER, 1);
        makeCall(false, DATAVALUE, _1_INTEGER, 1);
        makeCall(true, DATAVALUE, _4_INTEGER, 1);
        clash(true);
    }

    @Test
    void test130() {
        makeCall(true, DATAEXPR, "#dateTime( minInclusive 2008-09-08 19:44:11)");
        makeCall(true, DATAEXPR, "#dateTime( maxInclusive 2008-09-08 19:44:11)");
        clash(false);
    }


    @ValueSource(strings = {"numeric(#integer(extra facets:18 null))",
        "numeric(#integer(extra facets:4 null))"})
    @ParameterizedTest
    void test84(String s1) {
        makeCall(true, DATAEXPR, s1);
        assertFalse(makeCall(true, DATAEXPR, s1));
    }


    @ValueSource(
        strings = {"#dateTime( minInclusive 2008-06-08 19:44:11 maxInclusive 2008-06-10 19:44:11)",
            "#dateTime( minInclusive 2008-06-08 maxInclusive 2008-06-10)",
            "#dateTime( minInclusive 2008-06-08 19:44:11 maxInclusive 2008-09-08 19:44:11)",
            "numeric(#int(extra facets:2007 2009))"})
    @ParameterizedTest
    void test122(String s1) {
        makeCall(true, DATAEXPR, s1);
        clash(false);
    }



    @CsvSource({"\"aString\"^^string,true,#integer,true", "\"6542145\"^^integer,true,#byte,true",
        "\"A string\"^^string,false,#positiveInteger,false",
        "\"-1\"^^negativeInteger,false,#positiveInteger,false",
        "\"18\"^^integer,true,#integer,false",
        "\"1998\"^^positiveInteger,true,#positiveInteger,false"})
    @ParameterizedTest
    void test135(String s1, boolean b1, String s2, boolean b2) {
        makeCall(true, DATAVALUE, s1);
        makeCall(b1, DATATYPE, s2);
        clash(b2);
    }

    @CsvSource({"true,\"0.0\"^^float,\"-0.0\"^^float,true",
        "true,\"19\"^^integer,\"18\"^^integer,true",
        "true,\"&lt;br&gt;&lt;/br&gt;&lt;img src=&quot;vn.png&quot; title=&quot;Venn&quot; alt=&quot;Venn diagram&quot; longdesc=&quot;vn.html&quot;&gt;&lt;/img&gt;\"^^XMLLiteral,\"&lt;br&gt;&lt;/br&gt;&lt;img src=&quot;vn.png&quot; alt=&quot;Venn diagram&quot; longdesc=&quot;vn.html&quot; title=&quot;Venn&quot;&gt;&lt;/img&gt;\"^^XMLLiteral,true",
        "true,\"&lt;span xml:lang=&quot;en&quot;&gt;&lt;b&gt;Bad!&lt;/b&gt;&lt;/span&gt;\"^^XMLLiteral,\"&lt;span xml:lang=&quot;en&quot;&gt;&lt;b&gt;Good!&lt;/b&gt;&lt;/span&gt;\"^^XMLLiteral,true",
        "true,\"2008-07-10\"^^dateTime,\"2008-07-08\"^^dateTime,true",
        "true,\"2008-07-10T20:44:11.656+01:00\"^^dateTime,\"2008-07-08T20:44:11.656+01:00\"^^dateTime,true",
        "false,\"3\"^^integer,\"2\"^^integer,false", "false,\"3\"^^integer,\"2\"^^short,false"})
    @ParameterizedTest
    void test133(boolean b1, String s1, String s2, boolean b2) {
        makeCall(b1, DATAVALUE, s1);
        makeCall(true, DATAVALUE, s2);
        clash(b2);
    }

    @CsvSource({"\"3\"^^integer,\"2\"^^integer,\"4\"^^integer",
        "\"3\"^^integer,\"2\"^^short,\"4\"^^int"})
    @ParameterizedTest
    void test134(String s1, String s2, String s3) {
        makeCall(false, DATAVALUE, s1);
        makeCall(true, DATAVALUE, s2);
        makeCall(true, DATAVALUE, s3);
        clash(true);
    }

    @CsvSource({
        "\"2008-07-09\"^^dateTime,false,#dateTime( minInclusive 2008-06-08 maxInclusive 2008-06-10),false",
        "\"2008-07-09\"^^dateTime,false,#dateTime( minInclusive 2008-06-08 maxInclusive 2008-10-10),true",
        "\"2007-10-08T20:44:11.656+01:00\"^^dateTime,true,#dateTime( minInclusive 2008-06-08 19:44:11 maxInclusive 2008-09-08 19:44:11),true",
        "\"2007\"^^int,true,numeric(#int(extra facets:2008 2009)),true",
        "\"2008-07-08T20:44:11.656+01:00\"^^dateTime,false,#dateTime( minInclusive 2008-06-08 19:44:11 maxInclusive 2008-09-08 19:44:11),true",
        "\"2008\"^^int,false,numeric(#int(extra facets:2007 2009)),true"})
    @ParameterizedTest
    void test123(String s1, boolean b1, String s2, boolean b2) {
        makeCall(true, DATAVALUE, s1);
        makeCall(b1, DATAEXPR, s2);
        clash(b2);
    }

    @CsvSource({"true,#integer,true,#string,true", "true,#integer,false,#Literal,true",
        "false,#short,true,#byte,true", "false,#unsignedByte,true,#short,false"})
    @ParameterizedTest
    void test890(boolean b1, String s1, boolean b2, String s2, boolean b3) {
        makeCall(b1, DATATYPE, s1);
        makeCall(b2, DATATYPE, s2);
        clash(b3);
    }

    private static Stream<Arguments> twoStrings() {
        String[] values = {SPHENODONTIDAE, CORDYLIDAE, LOXOCEMIDAE, AGAMIDAE, ANOMALEPIDAE,
            XANTUSIIDAE, EMYDIDAE, AMPHISBAENIDAE};
        List<Arguments> l = new ArrayList<>();
        for (int i = 0; i < values.length - 1; i++) {
            for (int j = i + 1; j < values.length; j++) {
                l.add(Arguments.of(values[i], values[j]));
            }
        }
        return l.stream();
    }

    @ParameterizedTest
    @MethodSource("twoStrings")
    void testTwoStrings(String a, String b) {
        makeCall(true, DATAVALUE, a, 1);
        makeCall(true, DATATYPE, LITERAL2, 1);
        makeCall(true, DATAVALUE, b, 1);
        clash(true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"0\"^^integer", "\"2008-07-08T20:44:11.656+01:00\"^^dateTime",
        "\"2008-07-09T20:44:11.656+01:00\"^^dateTime", "\"0.0\"^^float", "\"19\"^^integer",
        "\"2008\"^^int", "\"2008-07-10T20:44:11.656+01:00\"^^dateTime",
        "\"&lt;br&gt;&lt;/br&gt;&lt;img&gt;&lt;/img&gt;\"^^XMLLiteral",
        "\"&lt;br&gt;&lt;/br&gt;&lt;img src=&quot;vn.png&quot; title=&quot;Venn&quot; alt=&quot;Venn diagram&quot; longdesc=&quot;vn.html&quot;&gt;&lt;/img&gt;\"^^XMLLiteral",
        "\"&lt;span xml:lang=&quot;en&quot;&gt;&lt;b&gt;Bad!&lt;/b&gt;&lt;/span&gt;\"^^XMLLiteral",
        GEKKONIDAE, BIPEDIDAE, CORDYLIDAE, LOXOCEMIDAE, CROCODYLIDAE, ANOMALEPIDAE, EMYDIDAE,
        LEPTOTYPHLOPIDAE, SPHENODONTIDAE, AMPHISBAENIDAE, XANTUSIIDAE, AGAMIDAE,
        "\"value@\"^^PlainLiteral", "\"2008-07-10\"^^dateTime", "\"2008-07-09\"^^dateTime",
        "\"2008-07-08\"^^dateTime"})
    void test(String value) {
        makeCall(true, DATAVALUE, value);
        clash(false);
    }

    @ParameterizedTest
    @ValueSource(strings = {CORDYLIDAE, LOXOCEMIDAE, ANOMALEPIDAE, EMYDIDAE, SPHENODONTIDAE,
        AGAMIDAE, AMPHISBAENIDAE})
    void test756(String value) {
        makeCall(true, DATAVALUE, value);
        makeCall(true, DATATYPE, LITERAL2);
        clash(false);
    }

    @ParameterizedTest
    @ValueSource(
        strings = {GEKKONIDAE, BIPEDIDAE, CORDYLIDAE, LOXOCEMIDAE, CROCODYLIDAE, ANOMALEPIDAE,
            EMYDIDAE, LEPTOTYPHLOPIDAE, SPHENODONTIDAE, XANTUSIIDAE, AGAMIDAE, AMPHISBAENIDAE})
    void test755(String value) {
        makeCall(true, DATAVALUE, value);
        makeCall(false, DATATYPE, LITERAL2, 1);
        clash(true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"true\"^^boolean", "\"false\"^^boolean"})
    void test78(String s) {
        makeCall(true, DATATYPE, "#boolean");
        makeCall(false, DATAVALUE, s);
        clash(false);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test19(boolean b1) {
        makeCall(true, DATATYPE, LITERAL2);
        makeCall(b1, DATAVALUE, _6_INTEGER, 1);
        clash(false);
    }


    @CsvSource({"true,true,\"3\"^^integer,2,true", "true,false,\"3\"^^integer,2,false",
        "true,false,\"3\"^^integer,1,false", "false,true,\"5\"^^integer,1,false"})
    @ParameterizedTest
    void test56(boolean b1, boolean b2, String s, int depset, boolean b3) {
        makeCall(true, DATATYPE, LITERAL2);
        makeCall(b1, DATAVALUE, _6_INTEGER, 1);
        makeCall(b2, DATAVALUE, s, depset);
        clash(b3);
    }


    @CsvSource({"true,false,\"3\"^^integer,2,true,\"2\"^^integer,2,true",
        "true,false,\"3\"^^integer,2,false,\"2\"^^integer,2,false",
        "true,false,\"3\"^^integer,1,false,\"2\"^^integer,1,false",
        "false,true,\"2\"^^integer,1,true,\"3\"^^integer,2,true",
        "false,true,\"2\"^^integer,1,false,\"3\"^^integer,1,false",
        "false,true,\"2\"^^integer,1,false,\"3\"^^integer,2,false"})
    @ParameterizedTest
    void test22(boolean b1, boolean b2, String s1, int depset1, boolean b3, String s2, int depset2,
        boolean b4) {
        makeCall(true, DATATYPE, LITERAL2);
        makeCall(b1, DATAVALUE, _6_INTEGER, 1);
        makeCall(b2, DATAVALUE, s1, depset1);
        makeCall(b3, DATAVALUE, s2, depset2);
        clash(b4);
    }



    @CsvSource({"true,false,\"3\"^^integer,2,\"2\"^^integer,2,true,\"1\"^^integer,2,true",
        "false,true,\"5\"^^integer,1,\"3\"^^integer,2,true,\"2\"^^integer,2,true",
        "true,false,\"3\"^^integer,1,\"2\"^^integer,1,false,\"1\"^^integer,1,false",
        "false,true,\"5\"^^integer,1,\"3\"^^integer,2,false,\"2\"^^integer,2,false",
        "false,true,\"5\"^^integer,1,\"3\"^^integer,1,false,\"2\"^^integer,1,false"})
    @ParameterizedTest
    void test28(boolean b1, boolean b2, String s1, int depset1, String s2, int depset2, boolean b3,
        String s3, int depset3, boolean b4) {
        makeCall(true, DATATYPE, LITERAL2);
        makeCall(b1, DATAVALUE, _6_INTEGER, 1);
        makeCall(b2, DATAVALUE, s1, depset1);
        makeCall(false, DATAVALUE, s2, depset2);
        makeCall(b3, DATAVALUE, s3, depset3);
        clash(b4);
    }

    @CsvSource({
        "true,false,\"3\"^^integer,\"2\"^^integer,1,\"1\"^^integer,1,true,\"4\"^^integer,1,true",
        "false,true,\"5\"^^integer,\"3\"^^integer,2,\"2\"^^integer,2,true,\"1\"^^integer,2,true",
        "false,true,\"5\"^^integer,\"3\"^^integer,1,\"2\"^^integer,1,false,\"1\"^^integer,1,false"})
    @ParameterizedTest
    void test29(boolean b1, boolean b2, String s1, String s2, int depset1, String s3, int depset2,
        boolean b3, String s4, int depset3, boolean b4) {
        makeCall(true, DATATYPE, LITERAL2);
        makeCall(b1, DATAVALUE, _6_INTEGER, 1);
        makeCall(b2, DATAVALUE, s1, 1);
        makeCall(false, DATAVALUE, s2, depset1);
        makeCall(false, DATAVALUE, s3, depset2);
        makeCall(b3, DATAVALUE, s4, depset3);
        clash(b4);
    }

    protected void clash(boolean b) {
        assertEquals(b, datatypeReasoner.checkClash());
    }
}
