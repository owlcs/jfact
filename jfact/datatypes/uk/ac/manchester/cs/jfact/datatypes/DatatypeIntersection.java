package uk.ac.manchester.cs.jfact.datatypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatatypeIntersection implements
		DatatypeCombination<DatatypeIntersection, Datatype<?>> {
	private final Set<Datatype<?>> basics = new HashSet<Datatype<?>>();
	private final String uri;
	private final Datatype<?> host;

	public static Datatype<?> getHostDatatype(final Collection<Datatype<?>> c) {
		List<Datatype<?>> list = new ArrayList<Datatype<?>>(c);
		// all types need to be compatible, or the intersection cannot be anything but empty
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				if (!list.get(i).isCompatible(list.get(j))) {
					return null;
				}
			}
		}
		// the most specific type needs to be returned
		int old_size;
		do {
			old_size = list.size();
			for (int i = 0; i < list.size() - 1;) {
				if (list.get(i).isSubType(list.get(i + 1))) {
					list.remove(i + 1);
				} else if (list.get(i + 1).isSubType(list.get(i))) {
					list.remove(i);
				} else {
					i++;
				}
			}
		} while (list.size() > 1 && old_size != list.size());
		// now if list.size >1, there is no single most specific type... troubles
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	public DatatypeIntersection(final Datatype<?> host) {
		uri = "intersection#a" + DatatypeFactory.getIndex();
		this.host = host;
	}

	public DatatypeIntersection(final Datatype<?> host, final Iterable<Datatype<?>> list) {
		this(host);
		for (Datatype<?> d : list) {
			basics.add(d);
		}
	}

	public Datatype<?> getHost() {
		return host;
	}

	public Iterable<Datatype<?>> getList() {
		return basics;
	}

	public DatatypeIntersection add(final Datatype<?> d) {
		DatatypeIntersection toReturn = new DatatypeIntersection(host, basics);
		toReturn.basics.add(d);
		return toReturn;
	}

	public boolean isCompatible(final Literal<?> l) {
		// must be compatible with all basics
		// host is a shortcut to them
		if (!host.isCompatible(l)) {
			return false;
		}
		for (Datatype<?> d : basics) {
			if (!d.isCompatible(l)) {
				return false;
			}
		}
		return true;
	}

	public String getDatatypeURI() {
		return uri;
	}

	public boolean isCompatible(final Datatype<?> type) {
		// must be compatible with all basics
		// host is a shortcut to them
		if (!host.isCompatible(type)) {
			return false;
		}
		for (Datatype<?> d : basics) {
			if (!d.isCompatible(type)) {
				return false;
			}
		}
		return true;
	}

	public boolean emptyValueSpace() {
		// all base types must be numeric
		if (!host.getNumeric()) {
			return false;
		}
		BigDecimal min = null;
		BigDecimal max = null;
		// all intervals must intersect - i.e., the interval with max min (excluded if any interval excludes it), min max (excluded if any interval excludes it) must contain at least one element
		// get max minimum value
		boolean minExclusive = false;
		boolean maxExclusive = false;
		for (Datatype<?> dt : basics) {
			NumericDatatype<?> d = dt.asNumericDatatype();
			BigDecimal facetValue = d.getMin();
			if (facetValue != null) {
				if (min == null || min.compareTo(facetValue) < 0) {
					min = facetValue;
				}
			}
			facetValue = d.getMax();
			if (facetValue != null) {
				if (max == null || facetValue.compareTo(max) < 0) {
					max = facetValue;
				}
			}
			if (d.hasMinExclusive()) {
				minExclusive = true;
			}
			if (d.hasMaxExclusive()) {
				maxExclusive = true;
			}
		}
		int excluded = 0;
		if (minExclusive) {
			excluded++;
		}
		if (maxExclusive) {
			excluded++;
		}
		return !DatatypeFactory.nonEmptyInterval(min, max, excluded);
	}

	@Override
	public String toString() {
		return uri + "{" + basics + "}";
	}
}
