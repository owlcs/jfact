package datatypes;

import static org.junit.Assert.*;
import static uk.ac.manchester.cs.jfact.kernel.DagTag.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.ac.manchester.cs.jfact.datatypes.*;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

@SuppressWarnings("javadoc")
public class DatatypesTestCase {
    private DataTypeReasoner testSubject;
    private DatatypeEntry literalDatatypeEntry4 = new DatatypeEntry(
            DatatypeFactory.LITERAL).withIndex(4);
    private DatatypeEntry literalDatatypeEntry5 = new DatatypeEntry(
            DatatypeFactory.LITERAL).withIndex(5);
    private DatatypeEntry literalDatatypeEntry6 = new DatatypeEntry(
            DatatypeFactory.LITERAL).withIndex(6);
    private DatatypeEntry literalDatatypeEntry8 = new DatatypeEntry(
            DatatypeFactory.LITERAL).withIndex(8);
    private DatatypeEntry booleanDatatypeEntry4 = new DatatypeEntry(
            DatatypeFactory.BOOLEAN).withIndex(4);
    private DatatypeEntry integerDatatypeEntry5 = new DatatypeEntry(
            DatatypeFactory.INTEGER).withIndex(5);
    private DatatypeEntry integerDatatypeEntry6 = new DatatypeEntry(
            DatatypeFactory.INTEGER).withIndex(6);
    private DatatypeEntry nonpositiveintegerDatatypeEntry4 = new DatatypeEntry(
            DatatypeFactory.NONPOSITIVEINTEGER).withIndex(4);
    private DatatypeEntry shortDatatypeEntry5 = new DatatypeEntry(DatatypeFactory.SHORT)
            .withIndex(5);
    private DatatypeEntry unsignedbyteDatatypeEntry5 = new DatatypeEntry(
            DatatypeFactory.UNSIGNEDBYTE).withIndex(5);
    private LiteralEntry Agamidae = new LiteralEntry("Agamidae@").withLiteral(
            DatatypeFactory.PLAINLITERAL.buildLiteral("Agamidae@")).withIndex(26);
    private LiteralEntry Amphisbaenidae = new LiteralEntry("Amphisbaenidae@")
            .withLiteral(DatatypeFactory.PLAINLITERAL.buildLiteral("Amphisbaenidae@"))
            .withIndex(10);
    private LiteralEntry Anomalepidae = new LiteralEntry("Anomalepidae@").withLiteral(
            DatatypeFactory.PLAINLITERAL.buildLiteral("Anomalepidae@")).withIndex(22);
    private LiteralEntry Bipedidae = new LiteralEntry("Bipedidae@").withLiteral(
            DatatypeFactory.PLAINLITERAL.buildLiteral("Bipedidae@")).withIndex(34);
    private LiteralEntry Cordylidae = new LiteralEntry("Cordylidae@").withLiteral(
            DatatypeFactory.PLAINLITERAL.buildLiteral("Cordylidae@")).withIndex(50);
    private LiteralEntry Crocodylidae = new LiteralEntry("Crocodylidae@").withLiteral(
            DatatypeFactory.PLAINLITERAL.buildLiteral("Crocodylidae@")).withIndex(30);
    private LiteralEntry Emydidae = new LiteralEntry("Emydidae@").withLiteral(
            DatatypeFactory.PLAINLITERAL.buildLiteral("Emydidae@")).withIndex(14);
    private LiteralEntry Gekkonidae = new LiteralEntry("Gekkonidae@").withLiteral(
            DatatypeFactory.PLAINLITERAL.buildLiteral("Gekkonidae@")).withIndex(42);
    private LiteralEntry Leptotyphlopidae = new LiteralEntry("Leptotyphlopidae@")
            .withLiteral(DatatypeFactory.PLAINLITERAL.buildLiteral("Leptotyphlopidae@"))
            .withIndex(38);
    private LiteralEntry Loxocemidae = new LiteralEntry("Loxocemidae@").withLiteral(
            DatatypeFactory.PLAINLITERAL.buildLiteral("Loxocemidae@")).withIndex(46);
    private LiteralEntry Sphenodontidae = new LiteralEntry("Sphenodontidae@")
            .withLiteral(DatatypeFactory.PLAINLITERAL.buildLiteral("Sphenodontidae@"))
            .withIndex(54);
    private LiteralEntry Xantusiidae = new LiteralEntry("Xantusiidae@").withLiteral(
            DatatypeFactory.PLAINLITERAL.buildLiteral("Xantusiidae@")).withIndex(18);
    private LiteralEntry aString = new LiteralEntry("aString").withLiteral(
            DatatypeFactory.STRING.buildLiteral("aString")).withIndex(5);
    private LiteralEntry valueLiteral = new LiteralEntry("value@").withLiteral(
            DatatypeFactory.PLAINLITERAL.buildLiteral("value@")).withIndex(5);
    private LiteralEntry a_String = new LiteralEntry("A string").withLiteral(
            DatatypeFactory.STRING.buildLiteral("A string")).withIndex(8);
    private LiteralEntry name_1 = new LiteralEntry("-1").withLiteral(
            DatatypeFactory.NEGATIVEINTEGER.buildLiteral("-1")).withIndex(5);
    private LiteralEntry name0float = new LiteralEntry("0.0").withLiteral(
            DatatypeFactory.FLOAT.buildLiteral("0.0")).withIndex(7);
    private LiteralEntry name0_float = new LiteralEntry("-0.0").withLiteral(
            DatatypeFactory.FLOAT.buildLiteral("-0.0")).withIndex(5);
    private LiteralEntry name0integer = new LiteralEntry("0").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("0")).withIndex(5);
    private LiteralEntry name18integer = new LiteralEntry("18").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("18")).withIndex(5);
    private LiteralEntry name1998_679 = new LiteralEntry("1998").withLiteral(
            DatatypeFactory.POSITIVEINTEGER.buildLiteral("1998")).withIndex(679);
    private LiteralEntry name1998_729 = new LiteralEntry("1998").withLiteral(
            DatatypeFactory.POSITIVEINTEGER.buildLiteral("1998")).withIndex(729);
    private LiteralEntry name19integer = new LiteralEntry("19").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("19")).withIndex(7);
    private LiteralEntry name200710 = new LiteralEntry("2007-10-08T20:44:11.656+01:00")
            .withLiteral(
                    DatatypeFactory.DATETIME
                            .buildLiteral("2007-10-08T20:44:11.656+01:00")).withIndex(7);
    private LiteralEntry name2008075 = new LiteralEntry("2008-07-08T20:44:11.656+01:00")
            .withLiteral(
                    DatatypeFactory.DATETIME
                            .buildLiteral("2008-07-08T20:44:11.656+01:00")).withIndex(5);
    private LiteralEntry name2008078 = new LiteralEntry("2008-07-08T20:44:11.656+01:00")
            .withLiteral(
                    DatatypeFactory.DATETIME
                            .buildLiteral("2008-07-08T20:44:11.656+01:00")).withIndex(8);
    private LiteralEntry name20080785 = new LiteralEntry("2008-07-08T20:44:11.656+01:00")
            .withLiteral(
                    DatatypeFactory.DATETIME
                            .buildLiteral("2008-07-08T20:44:11.656+01:00")).withIndex(5);
    private LiteralEntry name2007 = new LiteralEntry("2007").withLiteral(
            DatatypeFactory.INT.buildLiteral("2007")).withIndex(5);
    private LiteralEntry name20080708 = new LiteralEntry("2008-07-08").withLiteral(
            DatatypeFactory.DATETIME.buildLiteral("2008-07-08")).withIndex(5);
    private LiteralEntry name200807086 = new LiteralEntry("2008-07-08").withLiteral(
            DatatypeFactory.DATETIME.buildLiteral("2008-07-08")).withIndex(5);
    private LiteralEntry name20080709 = new LiteralEntry("2008-07-09").withLiteral(
            DatatypeFactory.DATETIME.buildLiteral("2008-07-09")).withIndex(8);
    private LiteralEntry name2008070920 = new LiteralEntry(
            "2008-07-09T20:44:11.656+01:00").withLiteral(
            DatatypeFactory.DATETIME.buildLiteral("2008-07-09T20:44:11.656+01:00"))
            .withIndex(8);
    private LiteralEntry name20080710 = new LiteralEntry("2008-07-10").withLiteral(
            DatatypeFactory.DATETIME.buildLiteral("2008-07-10")).withIndex(8);
    private LiteralEntry name2008071020 = new LiteralEntry(
            "2008-07-10T20:44:11.656+01:00").withLiteral(
            DatatypeFactory.DATETIME.buildLiteral("2008-07-10T20:44:11.656+01:00"))
            .withIndex(7);
    private LiteralEntry name2008 = new LiteralEntry("2008").withLiteral(
            DatatypeFactory.INT.buildLiteral("2008")).withIndex(8);
    private LiteralEntry name3integer6 = new LiteralEntry("3").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("3")).withIndex(6);
    private LiteralEntry name3integer7 = new LiteralEntry("3").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("3")).withIndex(7);
    private LiteralEntry name3int7 = new LiteralEntry("3").withLiteral(
            DatatypeFactory.INT.buildLiteral("3")).withIndex(7);
    private LiteralEntry name6542145 = new LiteralEntry("6542145").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("6542145")).withIndex(8);
    private LiteralEntry booleanFalse = new LiteralEntry("false").withLiteral(
            DatatypeFactory.BOOLEAN.buildLiteral("false")).withIndex(10);
    private LiteralEntry booleanTrue = new LiteralEntry("true").withLiteral(
            DatatypeFactory.BOOLEAN.buildLiteral("true")).withIndex(10);
    private LiteralEntry name2integer5 = new LiteralEntry("2").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("2")).withIndex(5);
    private LiteralEntry name2short5 = new LiteralEntry("2").withLiteral(
            DatatypeFactory.SHORT.buildLiteral("2")).withIndex(5);
    private LiteralEntry xml1 = new LiteralEntry(
            "&lt;br&gt;&lt;/br&gt;&lt;img src=&quot;vn.png&quot; alt=&quot;Venn diagram&quot; longdesc=&quot;vn.html&quot; title=&quot;Venn&quot;&gt;&lt;/img&gt;")
            .withLiteral(
                    DatatypeFactory.XMLLITERAL
                            .buildLiteral("&lt;br&gt;&lt;/br&gt;&lt;img src=&quot;vn.png&quot; alt=&quot;Venn diagram&quot; longdesc=&quot;vn.html&quot; title=&quot;Venn&quot;&gt;&lt;/img&gt;"))
            .withIndex(7);
    private LiteralEntry xml2 = new LiteralEntry(
            "&lt;br&gt;&lt;/br&gt;&lt;img&gt;&lt;/img&gt;").withLiteral(
            DatatypeFactory.XMLLITERAL
                    .buildLiteral("&lt;br&gt;&lt;/br&gt;&lt;img&gt;&lt;/img&gt;"))
            .withIndex(7);
    private LiteralEntry xml3 = new LiteralEntry(
            "&lt;span xml:lang=&quot;en&quot;&gt;&lt;b&gt;Bad!&lt;/b&gt;&lt;/span&gt;")
            .withLiteral(
                    DatatypeFactory.XMLLITERAL
                            .buildLiteral("&lt;span xml:lang=&quot;en&quot;&gt;&lt;b&gt;Bad!&lt;/b&gt;&lt;/span&gt;"))
            .withIndex(7);
    private LiteralEntry xml4 = new LiteralEntry(
            "&lt;br&gt;&lt;/br&gt;&lt;img src=&quot;vn.png&quot; title=&quot;Venn&quot; alt=&quot;Venn diagram&quot; longdesc=&quot;vn.html&quot;&gt;&lt;/img&gt;")
            .withLiteral(
                    DatatypeFactory.XMLLITERAL
                            .buildLiteral("&lt;br&gt;&lt;/br&gt;&lt;img src=&quot;vn.png&quot; title=&quot;Venn&quot; alt=&quot;Venn diagram&quot; longdesc=&quot;vn.html&quot;&gt;&lt;/img&gt;"))
            .withIndex(5);
    private LiteralEntry xml5 = new LiteralEntry(
            "&lt;br&gt;&lt;/br&gt;&lt;img&gt;&lt;/img&gt;").withLiteral(
            DatatypeFactory.XMLLITERAL
                    .buildLiteral("&lt;br&gt;&lt;/br&gt;&lt;img&gt;&lt;/img&gt;"))
            .withIndex(5);
    private LiteralEntry xml6 = new LiteralEntry(
            "&lt;span xml:lang=&quot;en&quot;&gt;&lt;b&gt;Good!&lt;/b&gt;&lt;/span&gt;")
            .withLiteral(
                    DatatypeFactory.XMLLITERAL
                            .buildLiteral("&lt;span xml:lang=&quot;en&quot;&gt;&lt;b&gt;Good!&lt;/b&gt;&lt;/span&gt;"))
            .withIndex(5);
    private DatatypeEntry literalEntry = new DatatypeEntry(DatatypeFactory.LITERAL)
            .withIndex(7);
    private DatatypeEntry byte3 = new DatatypeEntry(DatatypeFactory.BYTE).withIndex(3);
    private DatatypeEntry byte4 = new DatatypeEntry(DatatypeFactory.BYTE).withIndex(4);
    private DatatypeEntry integer0 = new DatatypeEntry(DatatypeFactory.INTEGER)
            .withIndex(0);
    private DatatypeEntry integer7 = new DatatypeEntry(DatatypeFactory.INTEGER)
            .withIndex(7);
    private DatatypeEntry nonNegativeInteger6 = new DatatypeEntry(
            DatatypeFactory.NONNEGATIVEINTEGER).withIndex(6);
    private DatatypeEntry positiveInteger0 = new DatatypeEntry(
            DatatypeFactory.POSITIVEINTEGER).withIndex(0);
    private DatatypeEntry positiveInteger11 = new DatatypeEntry(
            DatatypeFactory.POSITIVEINTEGER).withIndex(11);
    private DatatypeEntry short3 = new DatatypeEntry(DatatypeFactory.SHORT).withIndex(3);
    private DatatypeEntry string4 = new DatatypeEntry(DatatypeFactory.STRING)
            .withIndex(4);
    private LiteralEntry int0 = new LiteralEntry("0").withLiteral(
            DatatypeFactory.INT.buildLiteral("0")).withIndex(0);
    private DatatypeEntry dateTime_1165 = new DatatypeEntry(DatatypeFactory
            .getOrderedDatatypeExpression(DatatypeFactory.DATETIME)
            .addNumericFacet(Facets.minInclusive,
                    new DateTime(1215546251656L).toGregorianCalendar())
            .addNumericFacet(Facets.maxInclusive,
                    new DateTime(1215719051656L).toGregorianCalendar())).withIndex(5);
    private DatatypeEntry dateTime_1173 = new DatatypeEntry(DatatypeFactory
            .getOrderedDatatypeExpression(DatatypeFactory.DATETIME)
            .addNumericFacet(Facets.minInclusive,
                    new DateTime(1215471600000L).toGregorianCalendar())
            .addNumericFacet(Facets.maxInclusive,
                    new DateTime(1215644400000L).toGregorianCalendar())).withIndex(5);
    private DatatypeEntry dateTime_14 = new DatatypeEntry(DatatypeFactory
            .getOrderedDatatypeExpression(DatatypeFactory.DATETIME)
            .addNumericFacet(Facets.minInclusive,
                    new DateTime(1215546251656L).toGregorianCalendar())
            .addNumericFacet(Facets.maxInclusive,
                    new DateTime(1215719051656L).toGregorianCalendar())).withIndex(5);
    private DatatypeEntry dateTime_22 = new DatatypeEntry(DatatypeFactory
            .getOrderedDatatypeExpression(DatatypeFactory.DATETIME)
            .addNumericFacet(Facets.minInclusive,
                    new DateTime(1215546251656L).toGregorianCalendar())
            .addNumericFacet(Facets.maxInclusive,
                    new DateTime(1223495051656L).toGregorianCalendar())).withIndex(5);
    private DatatypeEntry dateTime_26 = new DatatypeEntry(DatatypeFactory
            .getOrderedDatatypeExpression(DatatypeFactory.DATETIME)
            .addNumericFacet(Facets.minInclusive,
                    new DateTime(1215546251656L).toGregorianCalendar())
            .addNumericFacet(Facets.maxInclusive,
                    new DateTime(1223495051656L).toGregorianCalendar())).withIndex(5);
    private DatatypeEntry dateTime_55 = new DatatypeEntry(DatatypeFactory
            .getOrderedDatatypeExpression(DatatypeFactory.DATETIME)
            .addNumericFacet(Facets.minInclusive,
                    new DateTime(1191872651656L).toGregorianCalendar())
            .addNumericFacet(Facets.maxInclusive,
                    new DateTime(1255031051656L).toGregorianCalendar())).withIndex(5);
    private DatatypeEntry dateTime_79 = new DatatypeEntry(DatatypeFactory
            .getOrderedDatatypeExpression(DatatypeFactory.DATETIME).addNumericFacet(
                    Facets.minInclusive,
                    new DateTime(1223495051656L).toGregorianCalendar())).withIndex(7);
    private DatatypeEntry float_11 = new DatatypeEntry(DatatypeFactory
            .getNumericDatatypeExpression(DatatypeFactory.FLOAT)
            .addNumericFacet(Facets.minInclusive, 0F)
            .addNumericFacet(Facets.maxInclusive, 1.17549435E-38F)).withIndex(5);
    private DatatypeEntry int_46 = new DatatypeEntry(DatatypeFactory
            .getNumericDatatypeExpression(DatatypeFactory.INT)
            .addNumericFacet(Facets.minInclusive, 2008)
            .addNumericFacet(Facets.maxInclusive, 2009)).withIndex(7);
    private DatatypeEntry int_63 = new DatatypeEntry(DatatypeFactory
            .getNumericDatatypeExpression(DatatypeFactory.INT)
            .addNumericFacet(Facets.minInclusive, 2007)
            .addNumericFacet(Facets.maxInclusive, 2009)
            .addNonNumericFacet(Facets.fractionDigits, 0)).withIndex(5);
    private DatatypeEntry integer_49 = new DatatypeEntry(DatatypeFactory
            .getNumericDatatypeExpression(DatatypeFactory.INTEGER)
            .addNumericFacet(Facets.minInclusive, new BigInteger("4"))
            .addNonNumericFacet(Facets.fractionDigits, 0)).withIndex(9);
    private DatatypeEntry integer_7 = new DatatypeEntry(DatatypeFactory
            .getNumericDatatypeExpression(DatatypeFactory.INTEGER)
            .addNumericFacet(Facets.minInclusive, new BigInteger("18"))
            .addNonNumericFacet(Facets.fractionDigits, 0)).withIndex(5);
    private DatatypeEntry dateTime_81 = new DatatypeEntry(DatatypeFactory
            .getOrderedDatatypeExpression(DatatypeFactory.DATETIME).addNumericFacet(
                    Facets.maxInclusive,
                    new DateTime(1223495051656L).toGregorianCalendar())).withIndex(5);

    @Before
    public void setUp() {
        testSubject = new DataTypeReasoner(new JFactReasonerConfiguration());
    }

    private boolean makeCall(boolean positive, DagTag t, String s) {
        return testSubject.addDataEntry(positive, t, getNamedEntry(s), DepSet.create());
    }

    private boolean makeCall(boolean positive, DagTag t, NamedEntry s) {
        return testSubject.addDataEntry(positive, t, s, DepSet.create());
    }

    private boolean makeCall(boolean positive, DagTag t, int depset, String s) {
        return testSubject.addDataEntry(positive, t, getNamedEntry(s), getDepSet(depset));
    }

    private boolean makeCall(boolean positive, DagTag t, int depset, NamedEntry s) {
        return testSubject.addDataEntry(positive, t, s, getDepSet(depset));
    }

    private DepSet getDepSet(int s) {
        return DepSet.create(s);
    }

    public static String printDocument(Document doc) throws IOException,
            TransformerException {
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

    private NamedEntry getNamedEntry(String e) {
        // <extId>0</extId> <system>false</system> <top>false</top>
        // <bottom>false</bottom>
        System.out.println(e);
        try {
            Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(e)));
            String s = printDocument(d);
            s = s.replace(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n", "");
            s = s.replace("    <extId>0</extId>\n", "");
            s = s.replace("    <system>false</system>\n", "");
            s = s.replace("    <top>false</top>\n", "");
            s = s.replace("    <bottom>false</bottom>\n", "");
            if (// s.indexOf("DatatypeOrderedExpressionImpl") < 0 &&
                // s.indexOf("DatatypeNumericExpressionImpl") < 0 &&
            s.length() > 450) {
                // System.out.println(s);
            }
        } catch (SAXException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (TransformerException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

    @Test
    public void shouldPass1() {
        assertFalse(makeCall(false, dtDataType, unsignedbyteDatatypeEntry5));
        assertFalse(makeCall(true, dtDataType, short3));
    }

    @Test
    public void shouldPass2() {
        assertFalse(makeCall(false, dtDataValue, name3integer6));
        assertFalse(makeCall(true, dtDataValue, name2integer5));
    }

    @Test
    public void shouldPass3() {
        assertFalse(makeCall(false, dtDataValue, name3integer7));
    }

    @Test
    public void shouldPass4() {
        assertFalse(makeCall(false, dtDataValue, name3integer7));
        assertFalse(makeCall(true, dtDataValue, name2short5));
    }

    @Test
    public void shouldPass5() {
        assertFalse(makeCall(true, dtDataExpr, dateTime_1165));
    }

    @Test
    public void shouldPass6() {
        assertFalse(makeCall(true, dtDataExpr, dateTime_1173));
    }

    @Test
    public void shouldPass7() {
        assertFalse(makeCall(true, dtDataExpr, dateTime_14));
    }

    @Test
    public void shouldPass8() {
        assertFalse(makeCall(true, dtDataExpr, dateTime_55));
    }

    @Test
    public void shouldPass9() {
        assertFalse(makeCall(true, dtDataExpr, dateTime_79));
        assertFalse(makeCall(true, dtDataExpr, dateTime_81));
    }

    @Test
    public void shouldPass10() {
        assertFalse(makeCall(true, dtDataExpr, float_11));
    }

    @Test
    public void shouldPass11() {
        assertFalse(makeCall(true, dtDataValue, name2008070920));
    }

    @Test
    public void shouldPass12() {
        assertFalse(makeCall(true, dtDataExpr, int_63));
    }

    @Test
    public void shouldPass13() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
    }

    @Test
    public void shouldPass14() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass15() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry6));
    }

    @Test
    public void shouldPass16() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry8));
    }

    @Test
    public void shouldPass17() {
        assertFalse(makeCall(true, dtDataType, booleanDatatypeEntry4));
    }

    @Test
    public void shouldPass18() {
        assertFalse(makeCall(true, dtDataType, booleanDatatypeEntry4));
        assertFalse(makeCall(false, dtDataValue, booleanFalse));
    }

    @Test
    public void shouldPass19() {
        assertFalse(makeCall(true, dtDataType, booleanDatatypeEntry4));
        assertFalse(makeCall(false, dtDataValue, booleanTrue));
    }

    @Test
    public void shouldPass20() {
        assertFalse(makeCall(true, dtDataType, integerDatatypeEntry6));
        assertFalse(makeCall(true, dtDataType, string4));
    }

    @Test
    public void shouldPass21() {
        assertFalse(makeCall(true, dtDataType, nonpositiveintegerDatatypeEntry4));
        assertFalse(makeCall(true, dtDataType, nonNegativeInteger6));
        assertFalse(makeCall(false, dtDataValue, int0));
    }

    @Test
    public void shouldPass22() {
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry4));
    }

    @Test
    public void shouldPass23() {
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass24() {
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry8));
    }

    @Test
    public void shouldPass25() {
        assertFalse(makeCall(true, dtDataType, 2, literalDatatypeEntry4));
    }

    @Test
    public void shouldPass26() {
        assertFalse(makeCall(true, dtDataType, 2, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass27() {
        assertFalse(makeCall(true, dtDataType, 2, literalDatatypeEntry6));
    }

    @Test
    public void shouldPass28() {
        assertFalse(makeCall(true, dtDataType, 2, literalDatatypeEntry8));
    }

    @Test
    public void shouldPass29() {
        assertFalse(makeCall(true, dtDataType, 3, literalDatatypeEntry4));
    }

    @Test
    public void shouldPass30() {
        assertFalse(makeCall(true, dtDataType, 3, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass31() {
        assertFalse(makeCall(true, dtDataType, 3, literalDatatypeEntry6));
    }

    @Test
    public void shouldPass32() {
        assertFalse(makeCall(true, dtDataType, 4, literalDatatypeEntry4));
    }

    @Test
    public void shouldPass33() {
        assertFalse(makeCall(true, dtDataType, 4, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass34() {
        assertFalse(makeCall(true, dtDataType, 5, literalDatatypeEntry4));
    }

    @Test
    public void shouldPass35() {
        assertFalse(makeCall(true, dtDataType, 5, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass36() {
        assertFalse(makeCall(true, dtDataType, 5, literalDatatypeEntry6));
    }

    @Test
    public void shouldPass37() {
        assertFalse(makeCall(true, dtDataType, 6, literalDatatypeEntry4));
    }

    @Test
    public void shouldPass38() {
        assertFalse(makeCall(true, dtDataType, 6, literalDatatypeEntry6));
    }

    @Test
    public void shouldPass39() {
        assertFalse(makeCall(true, dtDataType, 7, literalDatatypeEntry6));
    }

    @Test
    public void shouldPass40() {
        assertFalse(makeCall(true, dtDataType, 8, literalDatatypeEntry6));
    }

    @Test
    public void shouldPass41() {
        assertFalse(makeCall(true, dtDataType, 9, literalDatatypeEntry6));
    }

    @Test
    public void shouldPass42() {
        assertFalse(makeCall(true, dtDataValue, xml1));
    }

    @Test
    public void shouldPass43() {
        assertFalse(makeCall(true, dtDataValue, xml1));
        assertTrue(makeCall(true, dtDataValue, xml4));
    }

    @Test
    public void shouldPass44() {
        assertFalse(makeCall(true, dtDataValue, xml3));
    }

    @Test
    public void shouldPass45() {
        assertFalse(makeCall(true, dtDataValue, xml2));
    }

    @Test
    public void shouldPass46() {
        assertFalse(makeCall(true, dtDataValue, xml2));
        assertFalse(makeCall(true, dtDataValue, xml5));
    }

    @Test
    public void shouldPass47() {
        assertFalse(makeCall(true, dtDataValue, xml3));
        assertTrue(makeCall(true, dtDataValue, xml6));
    }

    @Test
    public void shouldPass48() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass49() {
        assertFalse(makeCall(true, dtDataValue, name_1));
        assertFalse(makeCall(false, dtDataType, positiveInteger11));
    }

    @Test
    public void shouldPass50() {
        assertFalse(makeCall(true, dtDataValue, name0float));
    }

    @Test
    public void shouldPass51() {
        assertFalse(makeCall(true, dtDataValue, name0float));
        assertTrue(makeCall(true, dtDataValue, name0_float));
    }

    @Test
    public void shouldPass52() {
        assertTrue(makeCall(true, dtDataExpr, integer_7));
    }

    @Test
    public void shouldPass53() {
        assertFalse(makeCall(true, dtDataValue, name2008075));
    }

    @Test
    public void shouldPass54() {
        assertFalse(makeCall(true, dtDataValue, name0integer));
    }

    @Test
    public void shouldPass55() {
        assertFalse(makeCall(true, dtDataValue, name18integer));
        assertFalse(makeCall(true, dtDataType, integer0));
    }

    @Test
    public void shouldPass56() {
        assertFalse(makeCall(true, dtDataValue, name1998_679));
        assertFalse(makeCall(true, dtDataType, positiveInteger0));
    }

    @Test
    public void shouldPass57() {
        assertFalse(makeCall(true, dtDataValue, name1998_729));
        assertFalse(makeCall(true, dtDataType, positiveInteger0));
    }

    @Test
    public void shouldPass58() {
        assertFalse(makeCall(true, dtDataValue, name19integer));
    }

    @Test
    public void shouldPass59() {
        assertFalse(makeCall(true, dtDataValue, name19integer));
        assertTrue(makeCall(true, dtDataValue, name18integer));
    }

    @Test
    public void shouldPass60() {
        assertFalse(makeCall(true, dtDataValue, name2007));
        assertTrue(makeCall(true, dtDataExpr, int_46));
    }

    @Test
    public void shouldPass61() {
        assertTrue(makeCall(true, dtDataExpr, integer_49));
    }

    @Test
    public void shouldPass62() {
        assertFalse(makeCall(true, dtDataValue, aString));
        assertFalse(makeCall(true, dtDataType, integer7));
    }

    @Test
    public void shouldPass63() {
        assertFalse(makeCall(true, dtDataValue, name2008075));
    }

    @Test
    public void shouldPass64() {
        assertFalse(makeCall(true, dtDataValue, name2008078));
    }

    @Test
    public void shouldPass65() {
        assertFalse(makeCall(true, dtDataValue, name2008078));
        assertTrue(makeCall(false, dtDataExpr, dateTime_55));
    }

    @Test
    public void shouldPass66() {
        assertFalse(makeCall(true, dtDataValue, name2008078));
        assertTrue(makeCall(false, dtDataExpr, dateTime_55));
    }

    @Test
    public void shouldPass67() {
        assertFalse(makeCall(true, dtDataValue, name2008));
    }

    @Test
    public void shouldPass68() {
        assertFalse(makeCall(true, dtDataValue, name20080709));
        assertTrue(makeCall(false, dtDataExpr, dateTime_1173));
    }

    @Test
    public void shouldPass69() {
        assertFalse(makeCall(true, dtDataValue, name20080709));
        assertTrue(makeCall(false, dtDataExpr, dateTime_1173));
    }

    @Test
    public void shouldPass70() {
        assertFalse(makeCall(true, dtDataValue, name20080708));
    }

    @Test
    public void shouldPass71() {
        assertFalse(makeCall(true, dtDataValue, name2008070920));
        assertTrue(makeCall(false, dtDataExpr, dateTime_1165));
    }

    @Test
    public void shouldPass72() {
        assertFalse(makeCall(true, dtDataValue, name2008070920));
        assertTrue(makeCall(false, dtDataExpr, dateTime_1165));
    }

    @Test
    public void shouldPass73() {
        assertFalse(makeCall(true, dtDataValue, name20080709));
    }

    @Test
    public void shouldPass74() {
        assertFalse(makeCall(true, dtDataValue, name2008070920));
        assertTrue(makeCall(false, dtDataExpr, dateTime_14));
    }

    @Test
    public void shouldPass75() {
        assertFalse(makeCall(true, dtDataValue, name2008070920));
        assertTrue(makeCall(false, dtDataExpr, dateTime_14));
    }

    @Test
    public void shouldPass76() {
        assertFalse(makeCall(true, dtDataValue, name200710));
        assertTrue(makeCall(true, dtDataExpr, dateTime_22));
    }

    @Test
    public void shouldPass77() {
        assertFalse(makeCall(true, dtDataValue, name200710));
        assertTrue(makeCall(true, dtDataExpr, dateTime_26));
    }

    @Test
    public void shouldPass78() {
        assertFalse(makeCall(false, dtDataValue, name3integer6));
    }

    @Test
    public void shouldPass79() {
        assertFalse(makeCall(true, dtDataValue, name20080710));
    }

    @Test
    public void shouldPass791() {
        assertFalse(makeCall(true, dtDataValue, name20080710));
        assertTrue(makeCall(true, dtDataValue, name200807086));
    }

    @Test
    public void shouldPass792() {
        assertFalse(makeCall(true, dtDataValue, name2008070920));
    }

    @Test
    public void shouldPass793() {
        assertFalse(makeCall(true, dtDataValue, name2008071020));
    }

    @Test
    public void shouldPass794() {
        assertFalse(makeCall(true, dtDataValue, name2008071020));
        assertTrue(makeCall(true, dtDataValue, name20080785));
    }

    @Test
    public void shouldPass795() {
        assertFalse(makeCall(true, dtDataValue, name2008071020));
        assertTrue(makeCall(true, dtDataValue, name20080785));
    }

    @Test
    public void shouldPass80() {
        assertFalse(makeCall(true, dtDataType, integerDatatypeEntry5));
        assertFalse(makeCall(false, dtDataType, literalEntry));
    }

    @Test
    public void shouldPass81() {
        assertFalse(makeCall(true, dtDataValue, name2008));
        assertTrue(makeCall(false, dtDataExpr, int_63));
    }

    @Test
    public void shouldPass82() {
        assertFalse(makeCall(true, dtDataValue, name2008));
        assertTrue(makeCall(false, dtDataExpr, int_63));
    }

    @Test
    public void shouldPass83() {
        assertFalse(makeCall(true, dtDataType, nonpositiveintegerDatatypeEntry4));
        assertFalse(makeCall(true, dtDataType, nonNegativeInteger6));
    }

    @Test
    public void shouldPass84() {
        assertFalse(makeCall(true, dtDataValue, name3int7));
    }

    @Test
    public void shouldPass85() {
        assertFalse(makeCall(true, dtDataValue, name6542145));
        assertFalse(makeCall(true, dtDataType, byte4));
    }

    @Test
    public void shouldPass86() {
        assertFalse(makeCall(true, dtDataValue, a_String));
        assertFalse(makeCall(false, dtDataType, positiveInteger11));
    }

    @Test
    public void shouldPass87() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
    }

    @Test
    public void shouldPass88() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass89() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass891() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
    }

    @Test
    public void shouldPass90() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass91() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass92() {
        assertFalse(makeCall(true, dtDataValue, Amphisbaenidae));
    }

    @Test
    public void shouldPass93() {
        assertFalse(makeCall(true, dtDataValue, Amphisbaenidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass94() {
        assertFalse(makeCall(true, dtDataValue, Amphisbaenidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass95() {
        assertFalse(makeCall(true, dtDataValue, Amphisbaenidae));
    }

    @Test
    public void shouldPass96() {
        assertFalse(makeCall(true, dtDataValue, Amphisbaenidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass97() {
        assertFalse(makeCall(true, dtDataValue, Amphisbaenidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass98() {
        assertFalse(makeCall(true, dtDataValue, Anomalepidae));
    }

    @Test
    public void shouldPass99() {
        assertFalse(makeCall(true, dtDataValue, Anomalepidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass100() {
        assertFalse(makeCall(true, dtDataValue, Anomalepidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass101() {
        assertFalse(makeCall(true, dtDataValue, Anomalepidae));
    }

    @Test
    public void shouldPass102() {
        assertFalse(makeCall(true, dtDataValue, Anomalepidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass103() {
        assertFalse(makeCall(true, dtDataValue, Anomalepidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass104() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass105() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass106() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass107() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass108() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass109() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass110() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass111() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass112() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass113() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass114() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass115() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass116() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
    }

    @Test
    public void shouldPass117() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass118() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass119() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
    }

    @Test
    public void shouldPass120() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass121() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass122() {
        assertFalse(makeCall(true, dtDataValue, Emydidae));
    }

    @Test
    public void shouldPass123() {
        assertFalse(makeCall(true, dtDataValue, Emydidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass124() {
        assertFalse(makeCall(true, dtDataValue, Emydidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass125() {
        assertFalse(makeCall(true, dtDataValue, Emydidae));
    }

    @Test
    public void shouldPass126() {
        assertFalse(makeCall(true, dtDataValue, Emydidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass127() {
        assertFalse(makeCall(true, dtDataValue, Emydidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass128() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass129() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass139() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass130() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass131() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass132() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass133() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass134() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass135() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass1391() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass136() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass137() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass138() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass140() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass141() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass142() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass143() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass144() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass145() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass146() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass147() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass148() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass149() {
        assertFalse(makeCall(true, dtDataValue, Xantusiidae));
    }

    @Test
    public void shouldPass150() {
        assertFalse(makeCall(true, dtDataValue, Xantusiidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass151() {
        assertFalse(makeCall(true, dtDataValue, Xantusiidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass152() {
        assertFalse(makeCall(true, dtDataValue, Xantusiidae));
    }

    @Test
    public void shouldPass153() {
        assertFalse(makeCall(true, dtDataValue, Xantusiidae));
        assertFalse(makeCall(false, dtDataType, 1, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass154() {
        assertFalse(makeCall(true, dtDataValue, Xantusiidae));
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry5));
    }

    @Test
    public void shouldPass155() {
        assertFalse(makeCall(true, dtDataValue, valueLiteral));
    }

    @Test
    public void shouldPass156() {
        assertFalse(makeCall(true, dtDataValue, 1, Agamidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass157() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass158() {
        assertFalse(makeCall(true, dtDataValue, 1, Agamidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass159() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass160() {
        assertFalse(makeCall(true, dtDataValue, 1, Agamidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass161() {
        assertFalse(makeCall(true, dtDataValue, Anomalepidae));
    }

    @Test
    public void shouldPass162() {
        assertFalse(makeCall(true, dtDataValue, 1, Agamidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass163() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
    }

    @Test
    public void shouldPass164() {
        assertFalse(makeCall(true, dtDataValue, 1, Agamidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass165() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass166() {
        assertFalse(makeCall(true, dtDataValue, 1, Agamidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass167() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass168() {
        assertFalse(makeCall(true, dtDataValue, 1, Agamidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass169() {
        assertFalse(makeCall(true, dtDataValue, Anomalepidae));
    }

    @Test
    public void shouldPass170() {
        assertFalse(makeCall(true, dtDataValue, 1, Agamidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass171() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
    }

    @Test
    public void shouldPass172() {
        assertFalse(makeCall(true, dtDataValue, 1, Anomalepidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass173() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass174() {
        assertFalse(makeCall(true, dtDataValue, 1, Anomalepidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass175() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass176() {
        assertFalse(makeCall(true, dtDataValue, 1, Anomalepidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass177() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
    }

    @Test
    public void shouldPass178() {
        assertFalse(makeCall(true, dtDataValue, 1, Anomalepidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass179() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass1791() {
        assertFalse(makeCall(true, dtDataValue, 1, Anomalepidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass180() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass181() {
        assertFalse(makeCall(true, dtDataValue, 1, Anomalepidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass182() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
    }

    @Test
    public void shouldPass183() {
        assertFalse(makeCall(true, dtDataValue, 1, Bipedidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass184() {
        assertFalse(makeCall(true, dtDataValue, Xantusiidae));
    }

    @Test
    public void shouldPass185() {
        assertFalse(makeCall(true, dtDataValue, 1, Bipedidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass186() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass187() {
        assertFalse(makeCall(true, dtDataValue, 1, Bipedidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass188() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass189() {
        assertFalse(makeCall(true, dtDataValue, 1, Bipedidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Crocodylidae));
    }

    @Test
    public void shouldPass190() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass191() {
        assertFalse(makeCall(true, dtDataValue, 1, Bipedidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass192() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass193() {
        assertFalse(makeCall(true, dtDataValue, 1, Bipedidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass194() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass195() {
        assertFalse(makeCall(true, dtDataValue, 1, Bipedidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass196() {
        assertFalse(makeCall(true, dtDataValue, Xantusiidae));
    }

    @Test
    public void shouldPass197() {
        assertFalse(makeCall(true, dtDataValue, 1, Bipedidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass198() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass199() {
        assertFalse(makeCall(true, dtDataValue, 1, Bipedidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass1991() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass200() {
        assertFalse(makeCall(true, dtDataValue, 1, Bipedidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Crocodylidae));
    }

    @Test
    public void shouldPass201() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass202() {
        assertFalse(makeCall(true, dtDataValue, 1, Bipedidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass203() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass204() {
        assertFalse(makeCall(true, dtDataValue, 1, Bipedidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass205() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass206() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass207() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass208() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass209() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass210() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass211() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass212() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Bipedidae));
    }

    @Test
    public void shouldPass213() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass214() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Crocodylidae));
    }

    @Test
    public void shouldPass215() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass216() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass217() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass218() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Gekkonidae));
    }

    @Test
    public void shouldPass219() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass220() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
    }

    @Test
    public void shouldPass221() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
    }

    @Test
    public void shouldPass222() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Loxocemidae));
    }

    @Test
    public void shouldPass223() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass224() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass225() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
    }

    @Test
    public void shouldPass226() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass227() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass228() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass229() {
        assertFalse(makeCall(false, dtDataType, shortDatatypeEntry5));
        assertFalse(makeCall(true, dtDataType, byte3));
    }

    @Test
    public void shouldPass230() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass231() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass232() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Bipedidae));
    }

    @Test
    public void shouldPass233() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass234() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Crocodylidae));
    }

    @Test
    public void shouldPass235() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass236() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass237() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass238() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Gekkonidae));
    }

    @Test
    public void shouldPass239() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass240() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
    }

    @Test
    public void shouldPass241() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
    }

    @Test
    public void shouldPass242() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Loxocemidae));
    }

    @Test
    public void shouldPass243() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass244() {
        assertFalse(makeCall(true, dtDataValue, 1, Cordylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass245() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
    }

    @Test
    public void shouldPass246() {
        assertFalse(makeCall(true, dtDataValue, 1, Crocodylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass247() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass248() {
        assertFalse(makeCall(true, dtDataValue, 1, Crocodylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass249() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
    }

    @Test
    public void shouldPass250() {
        assertFalse(makeCall(true, dtDataValue, 1, Crocodylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass251() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass252() {
        assertFalse(makeCall(true, dtDataValue, 1, Crocodylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass253() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass254() {
        assertFalse(makeCall(true, dtDataValue, 1, Crocodylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass255() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass256() {
        assertFalse(makeCall(true, dtDataValue, 1, Crocodylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass257() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass258() {
        assertFalse(makeCall(true, dtDataValue, 1, Crocodylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass259() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
    }

    @Test
    public void shouldPass260() {
        assertFalse(makeCall(true, dtDataValue, 1, Crocodylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass261() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass262() {
        assertFalse(makeCall(true, dtDataValue, 1, Crocodylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass263() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass264() {
        assertFalse(makeCall(true, dtDataValue, 1, Crocodylidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass265() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass266() {
        assertFalse(makeCall(true, dtDataValue, 1, Emydidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass267() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass268() {
        assertFalse(makeCall(true, dtDataValue, 1, Emydidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass269() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass270() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass271() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
    }

    @Test
    public void shouldPass272() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass273() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass274() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass275() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass276() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Bipedidae));
    }

    @Test
    public void shouldPass277() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass278() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Crocodylidae));
    }

    @Test
    public void shouldPass279() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass280() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass281() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass282() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
    }

    @Test
    public void shouldPass283() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass284() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass285() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass286() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass287() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
    }

    @Test
    public void shouldPass288() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass289() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass290() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass291() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass292() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Bipedidae));
    }

    @Test
    public void shouldPass293() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass294() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Crocodylidae));
    }

    @Test
    public void shouldPass295() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass296() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass297() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass298() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
    }

    @Test
    public void shouldPass299() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass301() {
        assertFalse(makeCall(true, dtDataValue, 1, Gekkonidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass302() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass303() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass304() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass305() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass306() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass307() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass308() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass309() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Bipedidae));
    }

    @Test
    public void shouldPass310() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
    }

    @Test
    public void shouldPass311() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Crocodylidae));
    }

    @Test
    public void shouldPass312() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass313() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass314() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass315() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass316() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass317() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass318() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass319() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass320() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass321() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass322() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass323() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Bipedidae));
    }

    @Test
    public void shouldPass324() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
    }

    @Test
    public void shouldPass325() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Crocodylidae));
    }

    @Test
    public void shouldPass326() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass327() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass328() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass329() {
        assertFalse(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass330() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass331() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass332() {
        assertFalse(makeCall(true, dtDataValue, Emydidae));
    }

    @Test
    public void shouldPass333() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass334() {
        assertFalse(makeCall(true, dtDataValue, Xantusiidae));
    }

    @Test
    public void shouldPass335() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass336() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass337() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Bipedidae));
    }

    @Test
    public void shouldPass338() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
    }

    @Test
    public void shouldPass339() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Crocodylidae));
    }

    @Test
    public void shouldPass340() {
        assertFalse(makeCall(true, dtDataValue, Anomalepidae));
    }

    @Test
    public void shouldPass341() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass342() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass343() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Gekkonidae));
    }

    @Test
    public void shouldPass344() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass345() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
    }

    @Test
    public void shouldPass346() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass347() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass348() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass349() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass350() {
        assertFalse(makeCall(true, dtDataValue, Emydidae));
    }

    @Test
    public void shouldPass351() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass352() {
        assertFalse(makeCall(true, dtDataValue, Xantusiidae));
    }

    @Test
    public void shouldPass353() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass354() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass355() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Bipedidae));
    }

    @Test
    public void shouldPass356() {
        assertFalse(makeCall(true, dtDataValue, Crocodylidae));
    }

    @Test
    public void shouldPass357() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Crocodylidae));
    }

    @Test
    public void shouldPass358() {
        assertFalse(makeCall(true, dtDataValue, Anomalepidae));
    }

    @Test
    public void shouldPass359() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass360() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass361() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Gekkonidae));
    }

    @Test
    public void shouldPass362() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass363() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
    }

    @Test
    public void shouldPass364() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass365() {
        assertFalse(makeCall(true, dtDataValue, 1, Loxocemidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass366() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass367() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass368() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass369() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass370() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass371() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass372() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass373() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Bipedidae));
    }

    @Test
    public void shouldPass374() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass375() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Cordylidae));
    }

    @Test
    public void shouldPass376() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass377() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Crocodylidae));
    }

    @Test
    public void shouldPass378() {
        assertFalse(makeCall(true, dtDataValue, Anomalepidae));
    }

    @Test
    public void shouldPass379() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass380() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass381() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Gekkonidae));
    }

    @Test
    public void shouldPass382() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass383() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
    }

    @Test
    public void shouldPass384() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass385() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Loxocemidae));
    }

    @Test
    public void shouldPass386() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass387() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass388() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass389() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Agamidae));
    }

    @Test
    public void shouldPass390() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass391() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass392() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass393() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Anomalepidae));
    }

    @Test
    public void shouldPass394() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass395() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Bipedidae));
    }

    @Test
    public void shouldPass396() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass397() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Cordylidae));
    }

    @Test
    public void shouldPass398() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass399() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Crocodylidae));
    }

    @Test
    public void shouldPass400() {
        assertFalse(makeCall(true, dtDataValue, Anomalepidae));
    }

    @Test
    public void shouldPass401() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass402() {
        assertFalse(makeCall(true, dtDataValue, Bipedidae));
    }

    @Test
    public void shouldPass403() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Gekkonidae));
    }

    @Test
    public void shouldPass404() {
        assertFalse(makeCall(true, dtDataValue, Loxocemidae));
    }

    @Test
    public void shouldPass405() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Leptotyphlopidae));
    }

    @Test
    public void shouldPass406() {
        assertFalse(makeCall(true, dtDataValue, Leptotyphlopidae));
    }

    @Test
    public void shouldPass407() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Loxocemidae));
    }

    @Test
    public void shouldPass408() {
        assertFalse(makeCall(true, dtDataValue, Cordylidae));
    }

    @Test
    public void shouldPass409() {
        assertFalse(makeCall(true, dtDataValue, 1, Sphenodontidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Xantusiidae));
    }

    @Test
    public void shouldPass410() {
        assertFalse(makeCall(true, dtDataValue, Sphenodontidae));
    }

    @Test
    public void shouldPass411() {
        assertFalse(makeCall(true, dtDataValue, 1, Xantusiidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass412() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
    }

    @Test
    public void shouldPass413() {
        assertFalse(makeCall(true, dtDataValue, 1, Xantusiidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass414() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    @Test
    public void shouldPass415() {
        assertFalse(makeCall(true, dtDataValue, 1, Xantusiidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Amphisbaenidae));
    }

    @Test
    public void shouldPass416() {
        assertFalse(makeCall(true, dtDataValue, Agamidae));
    }

    @Test
    public void shouldPass417() {
        assertFalse(makeCall(true, dtDataValue, 1, Xantusiidae));
        assertFalse(makeCall(true, dtDataType, 1, literalDatatypeEntry5));
        assertTrue(makeCall(true, dtDataValue, 1, Emydidae));
    }

    @Test
    public void shouldPass418() {
        assertFalse(makeCall(true, dtDataValue, Gekkonidae));
    }

    private LiteralEntry integer5 = new LiteralEntry("5").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("5")).withIndex(8);
    private LiteralEntry integer4 = new LiteralEntry("4").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("4")).withIndex(7);
    private LiteralEntry integer6 = new LiteralEntry("6").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("6")).withIndex(9);
    private LiteralEntry integer3 = new LiteralEntry("3").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("3")).withIndex(13);
    private LiteralEntry integer2 = new LiteralEntry("2").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("2")).withIndex(12);
    private LiteralEntry integer1 = new LiteralEntry("1").withLiteral(
            DatatypeFactory.INTEGER.buildLiteral("1")).withIndex(11);

    @Test
    public void shouldPass419() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
    }

    @Test
    public void shouldPass420() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
    }

    @Test
    public void shouldPass421() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
    }

    @Test
    public void shouldPass422() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(true, dtDataValue, 1, integer6));
    }

    @Test
    public void shouldPass423() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(true, dtDataValue, 1, integer6));
        assertFalse(makeCall(true, dtDataValue, 2, integer3));
    }

    @Test
    public void shouldPass432() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(true, dtDataValue, 1, integer6));
        assertFalse(makeCall(false, dtDataValue, 2, integer3));
    }

    @Test
    public void shouldPass424() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(true, dtDataValue, 1, integer6));
        assertFalse(makeCall(false, dtDataValue, 2, integer3));
        assertFalse(makeCall(true, dtDataValue, 2, integer2));
    }

    @Test
    public void shouldPass433() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(true, dtDataValue, 1, integer6));
        assertFalse(makeCall(false, dtDataValue, 2, integer3));
    }

    @Test
    public void shouldPass425() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(true, dtDataValue, 1, integer6));
        assertFalse(makeCall(false, dtDataValue, 2, integer3));
        assertFalse(makeCall(false, dtDataValue, 2, integer2));
    }

    @Test
    public void shouldPass426() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(true, dtDataValue, 1, integer6));
        assertFalse(makeCall(false, dtDataValue, 2, integer3));
        assertFalse(makeCall(false, dtDataValue, 2, integer2));
        assertFalse(makeCall(true, dtDataValue, 2, integer1));
    }

    @Test
    public void shouldPass434() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(true, dtDataValue, 1, integer6));
        assertFalse(makeCall(false, dtDataValue, 1, integer3));
    }

    @Test
    public void shouldPass427() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(false, dtDataValue, 1, integer6));
    }

    @Test
    public void shouldPass428() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(false, dtDataValue, 1, integer6));
        assertFalse(makeCall(true, dtDataValue, 1, integer5));
    }

    @Test
    public void shouldPass429() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(false, dtDataValue, integer6));
    }

    @Test
    public void shouldPass430() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(false, dtDataValue, integer6));
        assertFalse(makeCall(false, dtDataValue, integer5));
    }

    @Test
    public void shouldPass431() {
        assertFalse(makeCall(true, dtDataType, literalDatatypeEntry4));
        assertFalse(makeCall(false, dtDataValue, integer6));
        assertFalse(makeCall(false, dtDataValue, integer5));
        assertFalse(makeCall(true, dtDataValue, integer4));
    }
}
