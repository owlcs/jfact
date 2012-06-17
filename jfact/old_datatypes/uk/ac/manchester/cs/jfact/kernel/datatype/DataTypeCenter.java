package uk.ac.manchester.cs.jfact.kernel.datatype;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.HashMap;
import java.util.Map;

import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;

public final class DataTypeCenter {
	/**
	 * vector of registered data types; initially contains unrestricted NUMBER
	 * and STRING
	 */
	private final Map<Datatypes, DataTypeEntryCollection<?>> Types = new HashMap<Datatypes, DataTypeEntryCollection<?>>();

	public DataTypeCenter() {
		// primitive DataTypes
		//XXX all this is not really needed
		for (Datatypes d : Datatypes.values()) {
			Types.put(d, new DataTypeEntryCollection(d));
		}
	}

	/** get NUMBER DT that can be used in TBox */
	public DLTree getNumberType() {
		return DLTreeFactory.wrap(new DataTypeName(Datatypes.INT));
	}

	/** get STRING DT that can be used in TBox */
	public DLTree getStringType() {
		return DLTreeFactory.wrap(new DataTypeName(Datatypes.STRING));
	}

	/** get datetime DT that can be used in TBox */
	public DLTree getDateTimeDataType() {
		return DLTreeFactory.wrap(new DataTypeName(Datatypes.DATETIME));
	}

	/// get fresh DT that can be used in TBox
	public DLTree getFreshDataType() {
		return DLTreeFactory.wrap(new DataTypeName(Datatypes.FRESH));
	}

	/** get REAL DT that can be used in TBox */
	public DLTree getRealType() {
		//XXX needs extension
		return DLTreeFactory.wrap(new DataTypeName(Datatypes.FLOAT));
	}

	/** get BOOL DT that can be used in TBox */
	public DLTree getBoolType() {
		return DLTreeFactory.wrap(new DataTypeName(Datatypes.BOOLEAN));
	}

	/** return registered data value by given NAME of a Type, given by SAMPLE */
	public DLTree getDataValue(final String name, Datatypes type) {
		//TODO this is possibly a problem
		return DLTreeFactory.wrap(create(name, type)); // may throw
	}

	/** facet for >=/>/</<= */
	public DLTree getIntervalFacetExpr(DLTree val, boolean min, boolean excl) {
		// get value as an DTE
		DataEntry<?> value = (DataEntry<?>) DLTreeFactory.unwrap(val);
		// create new (unnamed) expression
		DataEntry ret = createFacet(value.getDatatype());
		// apply appropriate facet to it
		ret.getFacet().update(min, excl, value.getComp());
		return DLTreeFactory.wrap(ret);
	}

	private DataEntry create(String name, Datatypes type) {
		return Types.get(type).get(name);
	}

	private DataEntry createFacet(Datatypes type) {
		return Types.get(type).getExpr();
	}

	public void initDataTypeReasoner(DataTypeReasoner DTReasoner) {
		for (Datatypes type : Datatypes.values()) {
			DTReasoner.registerDataType(type);
		}
	}
}