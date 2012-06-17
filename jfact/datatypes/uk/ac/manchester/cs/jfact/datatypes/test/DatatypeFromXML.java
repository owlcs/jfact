package uk.ac.manchester.cs.jfact.datatypes.test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeExpression;
import uk.ac.manchester.cs.jfact.datatypes.Facet;
import uk.ac.manchester.cs.jfact.datatypes.Facets;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.datatypes.NumericDatatype;
import uk.ac.manchester.cs.jfact.datatypes.OrderedDatatype;
import uk.ac.manchester.cs.jfact.datatypes.cardinality;
import uk.ac.manchester.cs.jfact.datatypes.ordered;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

public class DatatypeFromXML<R extends Comparable<R>> implements Datatype<R> {
	private final String name;
	private final String uri;
	private final Map<String, DatatypeFromXML<?>> types;
	private final Map<String, String> restrictions;
	private final Map<String, String> hfpProperties = new HashMap<String, String>();
	private final Map<Facet, Object> knownFacetValues = new HashMap<Facet, Object>();
	private final Set<Facet> facets;

	public DatatypeFromXML(final String n, final Map<String, DatatypeFromXML<?>> types,
			final Map<String, String> restrictions) {
		this.name = n;
		if (this.name.startsWith("http://")) {
			this.uri = this.name;
		} else {
			this.uri = DatatypeParser.ns + this.name;
		}
		this.types = types;
		this.restrictions = restrictions;
		this.facets = new HashSet<Facet>();
		this.hfpProperties.put("ordered", "false");
		this.hfpProperties.put("bounded", "false");
		this.hfpProperties.put("cardinality", "countably infinite");
		this.hfpProperties.put("numeric", "false");
	}

	public DatatypeFromXML(final String n, final Map<String, DatatypeFromXML<?>> types,
			final Map<String, String> restrictions, final String ordered,
			final String bounded, final String cardinality, final String numeric,
			final Collection<Facet> fac) {
		this(n, types, restrictions);
		this.hfpProperties.put("ordered", ordered);
		this.hfpProperties.put("bounded", bounded);
		this.hfpProperties.put("cardinality", cardinality);
		this.hfpProperties.put("numeric", numeric);
		this.facets.addAll(fac);
	}

	public DatatypeFromXML(final Element e, final Map<String, DatatypeFromXML<?>> typ,
			final Map<String, String> restrict) {
		this(e.getAttribute("name"), typ, restrict);
		// this constructor does not use the defaults
		this.hfpProperties.clear();
		NodeList restr = e.getElementsByTagName("xs:restriction");
		for (int i = 0; i < restr.getLength(); i++) {
			final String attribute = ((Element) restr.item(i)).getAttribute("base");
			if (attribute != null && attribute.length() > 0) {
				this.restrictions.put(this.uri,
						attribute.replace("xs:anySimpleType", DatatypeParser.Literal)
								.replace("xs:", DatatypeParser.ns));
			} else {
				// then it's a sequence of some sort
				//					  <xs:restriction>
				//				      <xs:simpleType>
				//				        <xs:list itemType="xs:NMTOKEN"/>
				String type = ((Element) ((Element) restr.item(i)).getElementsByTagName(
						"xs:list").item(0)).getAttribute("itemType");
				if (type != null) {
					this.restrictions.put(this.uri,
							type.replace("xs:", DatatypeParser.ns));
				}
			}
			// now facets
			NodeList children = restr.item(i).getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {
				Node n = children.item(j);
				if (n instanceof Element) {
					if (!((Element) n).getTagName().equals("xs:simpleType")) {
						Facet f = Facets.parse(((Element) n).getTagName());
						this.knownFacetValues.put(f, ((Element) n).getAttribute("value"));
					}
				}
			}
		}
		NodeList props = e.getElementsByTagName("hfp:hasProperty");
		for (int i = 0; i < props.getLength(); i++) {
			Element n = (Element) props.item(i);
			this.hfpProperties.put(n.getAttribute("name"), n.getAttribute("value"));
		}
		NodeList facetList = e.getElementsByTagName("hfp:hasFacet");
		for (int i = 0; i < facetList.getLength(); i++) {
			Element n = (Element) facetList.item(i);
			this.facets.add(Facets.parse(n.getAttribute("name")));
		}
		//		System.out.println("DatatypeFromXML.DatatypeFromXML() " + uri + "\t"
		//				+ restrictions);
	}

	public void accept(final DLExpressionVisitor visitor) {
		visitor.visit(this);
	}

	public <O> O accept(final DLExpressionVisitorEx<O> visitor) {
		return visitor.visit(this);
	}

	public boolean isExpression() {
		return false;
	}

	public DatatypeExpression<R> asExpression() {
		return (DatatypeExpression<R>) this;
	}

	public Collection<Datatype<?>> getAncestors() {
		Set<Datatype<?>> toReturn = new HashSet<Datatype<?>>();
		//toReturn.add(this.types.get(DatatypeParser.Literal));
		String current = this.restrictions.get(this.uri);
		while (current != null) {
			final DatatypeFromXML<?> datatypeFromXML = this.types.get(current);
			if (datatypeFromXML != null) {
				toReturn.add(datatypeFromXML);
			}
			current = this.restrictions.get(current);
		}
		return toReturn;
	}

	public boolean getBounded() {
		if (this.hfpProperties.containsKey("bounded")) {
			return Boolean.parseBoolean(this.hfpProperties.get("bounded"));
		} else {
			Datatype<?> ancestor = !this.restrictions.containsKey(this.uri) ? null
					: this.types.get(this.restrictions.get(this.uri));
			if (ancestor != null) {
				return ancestor.getBounded();
			}
		}
		return false;
	}

	public cardinality getCardinality() {
		if (this.hfpProperties.containsKey("cardinality")) {
			return cardinality.parse(this.hfpProperties.get("cardinality"));
		} else {
			Datatype<?> ancestor = !this.restrictions.containsKey(this.uri) ? null
					: this.types.get(this.restrictions.get(this.uri));
			if (ancestor != null) {
				return ancestor.getCardinality();
			}
		}
		return cardinality.COUNTABLYINFINITE;
	}

	public Set<Facet> getFacets() {
		final HashSet<Facet> hashSet = new HashSet<Facet>(this.facets);
		String ancestor = this.restrictions.get(this.uri);
		if (ancestor != null) {
			Datatype<?> d = this.types.get(ancestor);
			if (d != null) {
				hashSet.addAll(d.getFacets());
			}
		}
		return hashSet;
	}

	public Map<Facet, Object> getKnownFacetValues() {
		Map<Facet, Object> toReturn = new HashMap<Facet, Object>(this.knownFacetValues);
		String current = this.restrictions.get(this.uri);
		if (current != null) {
			Map<Facet, Object> map = this.types.get(current).getKnownFacetValues();
			for (Facet key : map.keySet()) {
				if (!toReturn.containsKey(key)) {
					toReturn.put(key, map.get(key));
				}
			}
		}
		return toReturn;
	}

	public <O extends Comparable<O>> O getFacetValue(final Facet f) {
		Map<Facet, Object> toReturn = this.getKnownFacetValues();
		if (toReturn.containsKey(f)) {
			if (!f.isNumberFacet()) {
				return (O) this.getNumericFacetValue(f);
			}
			return (O) this.getNumericFacetValue(f);
		}
		return null;
	}

	public BigDecimal getNumericFacetValue(final Facet f) {
		Map<Facet, Object> toReturn = this.getKnownFacetValues();
		if (toReturn.containsKey(f)) {
			return f.parseNumber(toReturn.get(f));
		}
		return null;
	}

	public boolean getNumeric() {
		if (this.hfpProperties.containsKey("numeric")) {
			return Boolean.parseBoolean(this.hfpProperties.get("numeric"));
		} else {
			Datatype<?> ancestor = !this.restrictions.containsKey(this.uri) ? null
					: this.types.get(this.restrictions.get(this.uri));
			if (ancestor != null) {
				return ancestor.getNumeric();
			}
		}
		return false;
	}

	public ordered getOrdered() {
		if (this.hfpProperties.containsKey("ordered")) {
			return ordered.parse(this.hfpProperties.get("ordered"));
		} else {
			Datatype<?> ancestor = !this.restrictions.containsKey(this.uri) ? null
					: this.types.get(this.restrictions.get(this.uri));
			if (ancestor != null) {
				return ancestor.getOrdered();
			}
		}
		return ordered.FALSE;
	}

	public boolean isCompatible(final Datatype<?> type) {
		if (type instanceof NumericDatatype) {
			return type.isCompatible(this);
		}
		return type.getDatatypeURI().equals(this.getDatatypeURI())
				|| type.getDatatypeURI().equals(DatatypeParser.Literal)
				|| this.isSubType(type) || type.isSubType(this);
	}

	public boolean isCompatible(final Literal<?> l) {
		return this.isCompatible(l.getDatatypeExpression())
				&& this.isInValueSpace(this.parseValue(l.value()));
	}

	public boolean isInValueSpace(final R l) {
		//TODO verify the constraining facets
		return false;
	}

	public R parseValue(final String s) {
		return null;
	}

	public Literal<R> buildLiteral(final String s) {
		return new Literal<R>() {
			public void accept(final DLExpressionVisitor visitor) {
				visitor.visit(this);
			}

			public <O> O accept(final DLExpressionVisitorEx<O> visitor) {
				return visitor.visit(this);
			}

			public int compareTo(final Literal<R> o) {
				return this.typedValue().compareTo(o.typedValue());
			}

			public Datatype<R> getDatatypeExpression() {
				return DatatypeFromXML.this;
			}

			public String value() {
				return s;
			}

			public R typedValue() {
				return DatatypeFromXML.this.parseValue(s);
			}
		};
	}

	public boolean isSubType(final Datatype<?> type) {
		if (this.equals(type)) {
			return true;
		}
		final Collection<Datatype<?>> ancestors = this.getAncestors();
		final boolean contains = ancestors.contains(type);
		//		System.out.println("DatatypeFromXML.isSubType() " + ancestors);
		//		System.out.println("DatatypeFromXML.isSubType() " + type);
		//		System.out.println("DatatypeFromXML.isSubType() " + contains);
		return contains;
	}

	public String getDatatypeURI() {
		return this.uri;
	}

	public Collection<Literal<R>> listValues() {
		return Collections.emptyList();
	}

	@Override
	public String toString() {
		return "Datatype[" + this.getDatatypeURI() + "]";
	}

	@Override
	public int hashCode() {
		return this.uri.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (obj instanceof Datatype<?>) {
			return this.uri.equals(((Datatype<?>) obj).getDatatypeURI());
		}
		return false;
	}

	public boolean isNumericDatatype() {
		// TODO Auto-generated method stub
		return false;
	}

	public NumericDatatype<R> asNumericDatatype() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isOrderedDatatype() {
		// TODO Auto-generated method stub
		return false;
	}

	public <O extends Comparable<O>> OrderedDatatype<O> asOrderedDatatype() {
		// TODO Auto-generated method stub
		return null;
	}
}
