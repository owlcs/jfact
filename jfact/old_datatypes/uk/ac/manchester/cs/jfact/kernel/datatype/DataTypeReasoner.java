package uk.ac.manchester.cs.jfact.kernel.datatype;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.LeveLogger.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.dep.DepSetFactory;
import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.LeveLogger.Templates;
import uk.ac.manchester.cs.jfact.helpers.Reference;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.DLDag;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.datatype.DataTypeAppearance.DepDTE;

public final class DataTypeReasoner {
	/** map Type.pName.Type appearance */
	private final Map<Datatypes, DataTypeAppearance<?>> map = new HashMap<Datatypes, DataTypeAppearance<?>>();
	/** external DAG */
	private final DLDag dlHeap;
	/** dep-set for the clash for *all* the types */
	private final Reference<DepSet> clashDep = new Reference<DepSet>();

	/** process data value */
	private <O> boolean processDataValue(boolean pos, DataEntry<O> c, final DepSet dep) {
		DataTypeAppearance<O> type = (DataTypeAppearance<O>) map.get(c.getDatatype());
		if (pos) {
			type.setPType(new DepDTE(c, dep));
		}
		// create interval [c,c]
		DataInterval<O> constraints = new DataInterval<O>();
		constraints.updateMin(false, c.getComp());
		constraints.updateMax(false, c.getComp());
		return type.addInterval(pos, constraints, dep);
	}

	/** process data expr */
	private <O> boolean processDataExpr(boolean pos, final DataEntry<O> c,
			final DepSet dep) {
		DataInterval<O> constraints = c.getFacet();
		if (constraints.isEmpty()) {
			return false;
		}
		DataTypeAppearance<O> type = (DataTypeAppearance<O>) map.get(c.getDatatype());
		if (pos) {
			type.setPType(new DepDTE(c, dep));
		}
		return type.addInterval(pos, constraints, dep);
	}

	/** get data entry structure by a BP */
	private NamedEntry getDataEntry(int p) {
		return dlHeap.get(p).getConcept();
	}

	/** get TDE with a dep-set by a CWD */
	private DepDTE getDTE(int p, final DepSet dep) {
		return new DepDTE(getDataEntry(p), dep);
	}

	/** c'tor: save DAG */
	public DataTypeReasoner(final DLDag dag) {
		dlHeap = dag;
	}

	// managing DTR
	/** add data type to the reasoner */
	protected void registerDataType(Datatypes p) {
		map.put(p, new DataTypeAppearance(clashDep));
	}

	/** prepare types for the reasoning */
	public void clear() {
		for (DataTypeAppearance<?> p : map.values()) {
			p.clear();
		}
	}

	/** get clash-set */
	public DepSet getClashSet() {
		return clashDep.getReference();
	}

	public boolean addDataEntry(int p, final DepSet dep) {
		final DLVertex v = dlHeap.get(p);
		NamedEntry dataEntry = getDataEntry(p);
		switch (v.getType()) {
			case dtDataType: {
				Datatypes t = ((Datatyped) dataEntry).getDatatype();
				DataTypeAppearance<?> type = map.get(t);
				logger.print(Templates.INTERVAL, (p > 0 ? "+" : "-"), dataEntry.getName());
				if (p > 0) {
					type.setPType(getDTE(p, dep));
				} else {
					type.setNType(getDTE(p, dep));
				}
				return false;
			}
			case dtDataValue:
				return processDataValue(p > 0, (DataEntry) dataEntry, dep);
			case dtDataExpr:
				return processDataExpr(p > 0, (DataEntry) dataEntry, dep);
			case dtAnd:
				return false;
			default:
				//TODO this case needs investigation; is it a mistake? whenever something is supposed to be a data node and is actually a primitive concept?
				// or is it just a regular clash?
				//				System.out
				//						.println("DataTypeReasoner.addDataEntry() warning: this case might indicate errors in the datatype reasoning");
				//				return true;
				throw new UnreachableSituationException(v.toString());
		}
	}

	// try to find contradiction:
	// -- if we have 2 same elements or direct contradiction (like "p" and "(not p)")
	//    then addConcept() will eliminate this;
	// => negations are not interesting also (p & ~p are eliminated; ~p means "all except p").
	// -- all cases with 2 different values of the same class are found in previous search;
	// -- The remaining problems are
	//   - check if there are 2 different positive classes
	//   - check if some value is present together with negation of its class
	//   - check if some value is present together with the other class
	//   - check if two values of different classes are present at the same time
	public boolean checkClash() {
		List<Map.Entry<Datatypes, DataTypeAppearance<?>>> types = new ArrayList<Map.Entry<Datatypes, DataTypeAppearance<?>>>();
		for (Map.Entry<Datatypes, DataTypeAppearance<?>> k : map.entrySet()) {
			if (k.getValue().hasPType() || k.getValue().hasNType()) {
				types.add(k);
			}
		}
		int size = types.size();
		if (size == 0) {
			// empty, nothing to do
			return false;
		}
		if (size == 1) {
			// only one, positive or negative - just check it
			return types.get(0).getValue().checkPNTypeClash();
		}
		if (size > 1) {
			// check if any value is already clashing with itself
			for (int i = 0; i < size; i++) {
				Map.Entry<Datatypes, DataTypeAppearance<?>> p = types.get(i);
				if (p.getValue().checkPNTypeClash()) {
					logger.print(Templates.CHECKCLASH);
					clashDep.setReference(p.getValue().getPType().second);
					return true;
				}
			}
			// for every two datatypes, they must either be disjoint and opposite, or one subdatatype of the other
			// if a subtype b, then b and not a, otherwise clash
			// a subtype b => b compatible a (all a are b) but not a compatible b (some b might not be a)
			for (int i = 0; i < size; i++) {
				Map.Entry<Datatypes, DataTypeAppearance<?>> p1 = types.get(i);
				for (int j = i + 1; j < size; j++) {
					Map.Entry<Datatypes, DataTypeAppearance<?>> p2 = types.get(j);
					if (p1.getKey().compatible(p2.getKey()) && p1.getValue().hasNType()
							&& p2.getValue().hasPType()) {
						// clash: not(Literal) && INT is impossible
						logger.print(Templates.CHECKCLASH);
						clashDep.setReference(DepSetFactory
								.plus(p1.getValue().hasPType() ? p1.getValue().getPType().second
										: p1.getValue().getNType().second, p2.getValue()
										.hasPType() ? p2.getValue().getPType().second
										: p2.getValue().getNType().second));
						return true;
					}
					if (p2.getKey().compatible(p1.getKey()) && p2.getValue().hasNType()
							&& p1.getValue().hasPType()) {
						// clash: not(Literal) && INT is impossible
						logger.print(Templates.CHECKCLASH);
						clashDep.setReference(DepSetFactory
								.plus(p1.getValue().hasPType() ? p1.getValue().getPType().second
										: p1.getValue().getNType().second, p2.getValue()
										.hasPType() ? p2.getValue().getPType().second
										: p2.getValue().getNType().second));
						return true;
					}
					if (!p1.getKey().compatible(p2.getKey())
							&& !p2.getKey().compatible(p1.getKey())) {
						// they're disjoint: they can't be both positive (but can be both negative)
						if (p1.getValue().hasPType() && p2.getValue().hasPType()) {
							// special case: disjoint datatypes with overlapping value spaces, e.g., nongeginteger, and nonposinteger and value = 0
							if (!p1.getValue().checkCompatibleValue(p2.getValue(),
									p1.getKey(), p2.getKey())) {
								logger.print(Templates.CHECKCLASH);
								clashDep.setReference(DepSetFactory.plus(p1.getValue()
										.getPType().second,
										p2.getValue().getPType().second));
								return true;
							}
						}
					}
				}
			}
			return false;
		}
		// this will never be reached because the previous ifs are a partition of the possible
		//sizes for types, but the compiler is not smart enough to see this
		return false;
		//		DataTypeAppearance type = null;
		//		for (DataTypeAppearance p : map.values()) {
		//			if (p.hasPType()) {
		//				if (type == null) {
		//					type = p;
		//				} else {
		//					Datatypes type_datatype = type
		//							.getPDatatype();
		//					Datatypes p_datatype = p
		//							.getPDatatype();
		//					if (!p_datatype
		//							.compatible(type_datatype)
		//							&& !type_datatype
		//									.compatible(p_datatype)) {
		//						logger.print(Templates.CHECKCLASH);
		//						clashDep.setReference(DepSetFactory.plus(
		//								type.getPType().second,
		//								p.getPType().second));
		//						return true;
		//					}
		//					// if one of them is compatible with the other but not the other way around, then replace type with the most restrictive one
		//					// XXX this is still dubious
		//					if (type_datatype
		//							.compatible(p_datatype)
		//							&& !p_datatype
		//									.compatible(type_datatype)) {
		//						type = p;
		//					}
		//					// else irrelevant: type is already the most restrictive
		//				}
		//			}
		//		}
		//		return type != null ? type.checkPNTypeClash()
		//				: false;
	}
}