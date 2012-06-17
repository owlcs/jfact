package uk.ac.manchester.cs.jfact.datatypes.test;

import static org.junit.Assert.assertEquals;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.datatypes.Facet;

public class DatatypeParser {
	public static String ns = "http://www.w3.org/2001/XMLSchema#";
	static final String Literal = "http://www.w3.org/2000/01/rdf-schema#Literal";

	public static void testDatatypes() throws SAXException, IOException,
			ParserConfigurationException {
		Map<String, DatatypeFromXML<?>> types = getTypes();
		DatatypeFactory f = DatatypeFactory.getInstance();
		for (Datatype<?> d : f.getKnownDatatypes()) {
			DatatypeFromXML<?> rebuilt = types.get(d.getDatatypeURI());
			assertEquals(rebuilt.getBounded(), d.getBounded());
			assertEquals(rebuilt.getOrdered(), d.getOrdered());
			assertEquals(rebuilt.getCardinality(), d.getCardinality());
			assertEquals(rebuilt.getNumeric(), d.getNumeric());
			assertEquals(rebuilt.getDatatypeURI(), d.getDatatypeURI());
			for (Datatype<?> t : f.getKnownDatatypes()) {
				final boolean compatible = rebuilt.isCompatible(t);
				final boolean compatible2 = d.isCompatible(t);
				assertEquals("not equal: " + t + "\t" + rebuilt + "\t" + d, compatible,
						compatible2);
				assertEquals("for " + t + "\t" + rebuilt + "\t" + d,
						rebuilt.isSubType(t), d.isSubType(t));
			}
			assertEquals(rebuilt.getAncestors(), d.getAncestors());
			assertEquals(rebuilt.getFacets(), d.getFacets());
			Map<Facet, Object> m1 = rebuilt.getKnownFacetValues();
			Map<Facet, Object> m2 = d.getKnownFacetValues();
			assertEquals(m1.keySet(), m2.keySet());
			for (Facet facet : m1.keySet()) {
				Object parse1 = null;
				Object parse2 = null;
				if (facet.isNumberFacet()) {
					parse1 = facet.parseNumber(m1.get(facet));
					parse2 = facet.parseNumber(m2.get(facet));
				} else {
					parse1 = facet.parse(m1.get(facet));
					parse2 = facet.parse(m2.get(facet));
				}
				assertEquals(parse1, parse2);
			}
		}
	}

	public static Map<String, DatatypeFromXML<?>> getTypes() throws SAXException,
			IOException, ParserConfigurationException {
		String simple = "xs:simpleType";
		String complex = "xs:complexType";
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new File("datatypes/schema.xsd"));
		final NodeList childNodes = doc.getElementsByTagName(simple);
		DatatypeFactory f = DatatypeFactory.getInstance();
		Map<String, Element> known = new HashMap<String, Element>();
		for (int i = 0; i < childNodes.getLength(); i++) {
			final Element e = (Element) childNodes.item(i);
			String uri = ns + e.getAttribute("name");
			if (f.isKnownDatatype(uri)) {
				//System.out.println("DatatypeParser.main() " + uri);
				known.put(uri, e);
			}
		}
		Map<String, DatatypeFromXML<?>> types = new HashMap<String, DatatypeFromXML<?>>();
		Map<String, String> restrictions = new HashMap<String, String>();
		types.put(Literal, new DatatypeFromXML<String>(Literal, types, restrictions));
		for (Datatype<?> d : f.getKnownDatatypes()) {
			if (known.containsKey(d.getDatatypeURI())) {
				DatatypeFromXML<?> rebuilt = getDatatype(d,
						known.get(d.getDatatypeURI()), types, restrictions);
			}
		}
		// hack in rational and real
		final List<Facet> list = Arrays.asList(minExclusive, minInclusive, maxExclusive,
				maxInclusive);
		DatatypeFromXML<BigDecimal> real = new DatatypeFromXML<BigDecimal>(
				"http://www.w3.org/2002/07/owl#real", types, restrictions, "partial",
				"false", "countably infinite", "true", list);
		DatatypeFromXML<BigDecimal> rational = new DatatypeFromXML<BigDecimal>(
				"http://www.w3.org/2002/07/owl#rational", types, restrictions, "partial",
				"false", "countably infinite", "true", list);
		String decimal = ns + "decimal";
		restrictions.put(decimal, rational.getDatatypeURI());
		restrictions.put(rational.getDatatypeURI(), real.getDatatypeURI());
		restrictions.put(real.getDatatypeURI(), Literal);
		types.put(rational.getDatatypeURI(), rational);
		types.put(real.getDatatypeURI(), real);
		// hack in plain literals
		DatatypeFromXML<String> plain = new DatatypeFromXML<String>(
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral", types,
				restrictions, "false", "false", "countably infinite", "false",
				Arrays.asList(length, minLength, maxLength, pattern, enumeration));
		restrictions.put(plain.getDatatypeURI(), Literal);
		types.put(plain.getDatatypeURI(), plain);
		// hack in XML literals
		plain = new DatatypeFromXML<String>(
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral", types,
				restrictions, "false", "false", "countably infinite", "false",
				Arrays.<Facet> asList());
		restrictions.put(plain.getDatatypeURI(), Literal);
		types.put(plain.getDatatypeURI(), plain);
		// hack in dateTimeStamp
		DatatypeFromXML<Calendar> dts = new DatatypeFromXML<Calendar>("dateTimeStamp",
				types, restrictions, "partial", "false", "countably infinite", "false",
				Arrays.<Facet> asList());
		restrictions.put(dts.getDatatypeURI(), ns + "dateTime");
		types.put(dts.getDatatypeURI(), dts);
		return types;
	}

	private static <R extends Comparable<R>> DatatypeFromXML<R> getDatatype(
			final Datatype<R> d, final Element element,
			final Map<String, DatatypeFromXML<?>> map,
			final Map<String, String> restrictions) {
		final DatatypeFromXML<R> datatypeFromXML = new DatatypeFromXML<R>(element, map,
				restrictions);
		map.put(datatypeFromXML.getDatatypeURI(), datatypeFromXML);
		return datatypeFromXML;
	}
}
