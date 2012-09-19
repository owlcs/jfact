package test;

import static org.junit.Assert.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.datatypes.Facet;
import uk.ac.manchester.cs.jfact.datatypes.Literal;

public class MainTestCase {
    @Test
    public void testByteFiller() {
        Literal<BigInteger> l = DatatypeFactory.INTEGER.buildLiteral("6542145");
        assertFalse(DatatypeFactory.BYTE.isCompatible(l));
    }

    @Test
    public void testLimits() {
        for (Datatype<?> d : DatatypeFactory.getValues()) {
            String str = d.toString();
            for (Facet facet : d.getFacets()) {
                if (facet.isNumberFacet()) {
                    BigDecimal facetValue = d.getNumericFacetValue(facet);
                    if (facetValue != null) {
                        Literal<?> plain = d.buildLiteral(facetValue.toPlainString());
                        Literal<?> onemin = d.buildLiteral(facetValue.subtract(
                                BigDecimal.ONE).toPlainString());
                        Literal<?> onep = d.buildLiteral(facetValue.add(BigDecimal.ONE)
                                .toPlainString());
                        if (facet.equals(minInclusive)) {
                            assertTrue(str, d.isCompatible(plain));
                            assertFalse(str, d.isCompatible(onemin));
                        }
                        if (facet.equals(maxInclusive)) {
                            assertTrue(str, d.isCompatible(plain));
                            assertFalse(str, d.isCompatible(onep));
                        }
                        if (facet.equals(minExclusive)) {
                            assertFalse(str, d.isCompatible(plain));
                            assertFalse(str, d.isCompatible(onemin));
                        }
                        if (facet.equals(maxExclusive)) {
                            assertFalse(str, d.isCompatible(plain));
                            assertFalse(str, d.isCompatible(onep));
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testOne() {
        for (Datatype<?> d : DatatypeFactory.getValues()) {
            if (d.getNumeric()) {
                Literal<?> one = d.buildLiteral("1");
                Literal<?> minusone = d.buildLiteral("-1");
                Literal<?> zero = d.buildLiteral("0");
                if (!d.isCompatible(one)) {
                    assertTrue(d.toString(), d.isCompatible(minusone));
                } else {
                    assertTrue(d.toString(), d.isCompatible(one));
                }
                if (!d.isCompatible(zero)) {
                    assertTrue(d.equals(DatatypeFactory.NEGATIVEINTEGER)
                            || d.equals(DatatypeFactory.POSITIVEINTEGER));
                }
            }
        }
    }

    @Test
    public void testOverlap() throws SAXException, IOException,
            ParserConfigurationException {
        Map<String, DatatypeFromXML<?>> map = DatatypeParser.getTypes();
        for (Datatype<?> d1 : DatatypeFactory.getValues()) {
            for (Datatype<?> d2 : DatatypeFactory.getValues()) {
                if (d1.getNumeric() == d2.getNumeric()) {
                    assertEquals(d1.isCompatible(d2), d2.isCompatible(d1));
                    Datatype<?> d1xml = map.get(d1.getDatatypeURI());
                    Datatype<?> d2xml = map.get(d2.getDatatypeURI());
                    assertTrue(d1xml.isCompatible(d1));
                    assertTrue(d2xml.isCompatible(d2));
                    assertTrue(d1.isCompatible(d1xml));
                    assertTrue(d2.isCompatible(d2xml));
                    assertEquals(d1xml.isCompatible(d2xml), d2xml.isCompatible(d1xml));
                    assertEquals(d1.isCompatible(d2xml), d2xml.isCompatible(d1));
                    assertEquals(d1xml.isCompatible(d2), d2.isCompatible(d1xml));
                }
            }
        }
    }

    @Test
    public void testCommonValuespace() {
        for (Datatype<?> d1 : DatatypeFactory.getValues()) {
            for (Datatype<?> d2 : DatatypeFactory.getValues()) {
                if (d1.getNumeric() && d2.getNumeric()) {
                    if (!d1.equals(d2)) {
                        if (d1.equals(DatatypeFactory.DOUBLE)
                                || d1.equals(DatatypeFactory.FLOAT)
                                || d2.equals(DatatypeFactory.DOUBLE)
                                || d2.equals(DatatypeFactory.FLOAT)) {
                            assertFalse(d1.toString() + "\t" + d2.toString(),
                                    d1.isCompatible(d2));
                            assertFalse(d2.toString() + "\t" + d1.toString(),
                                    d2.isCompatible(d1));
                        } else if (d1.isCompatible(d1.buildLiteral("0"))
                                && d2.isCompatible(d2.buildLiteral("0"))) {
                            assertTrue(d1.toString() + "\t" + d2.toString(),
                                    d1.isCompatible(d2));
                            assertTrue(d2.toString() + "\t" + d1.toString(),
                                    d2.isCompatible(d1));
                        } else {
                            // either both positive or both negative
                            // or one per side
                            if (d1.isCompatible(d1.buildLiteral("-1"))
                                    && d2.isCompatible(d2.buildLiteral("-1"))) {
                                if (!d1.isCompatible(d2)) {
                                    d1.isCompatible(d2);
                                }
                                assertTrue(d1.toString() + "\t" + d2.toString(),
                                        d1.isCompatible(d2));
                                assertTrue(d2.toString() + "\t" + d1.toString(),
                                        d2.isCompatible(d1));
                            } else if (d1.isCompatible(d1.buildLiteral("1"))
                                    && d2.isCompatible(d2.buildLiteral("1"))) {
                                assertTrue(d1.toString() + "\t" + d2.toString(),
                                        d1.isCompatible(d2));
                                assertTrue(d2.toString() + "\t" + d1.toString(),
                                        d2.isCompatible(d1));
                            } else {
                                assertFalse(d1.toString() + "\t" + d2.toString(),
                                        d1.isCompatible(d2));
                                assertFalse(d2.toString() + "\t" + d1.toString(),
                                        d2.isCompatible(d1));
                            }
                        }
                    }
                }
            }
        }
    }
    // @Test public void testMostSpecificType() {
    // List<Datatype<?>> list = new ArrayList<Datatype<?>>();
    // for (Datatype<?> d1 : DatatypeFactory.getValues()) {
    // for (Datatype<?> d2 : DatatypeFactory.getValues()) {
    // if (d1 != d2) {
    // list.clear();
    // list.add(d1);
    // list.add(d2);
    // Datatype<?> result = DatatypeIntersection.getHostDatatype(list);
    // System.out.println("MainTestCase.testMostSpecificType() " + list
    // + "\t result: " + result);
    // }
    // }
    // }
    // }
}
