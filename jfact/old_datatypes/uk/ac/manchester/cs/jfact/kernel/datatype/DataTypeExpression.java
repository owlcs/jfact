package uk.ac.manchester.cs.jfact.kernel.dl.interfaces;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;
import java.util.Map;

import datatypes.Datatype;
import datatypes.Facet;
import datatypes.Literal;
import datatypes.LiteralFactory;

public interface DataTypeExpression extends DataExpression {

	public enum cardinality {
		FINITE, COUNTABLYINFINITE
	}



	//equal facet: implemented by the equals() method on values
	public enum ordered {
		FALSE, PARTIAL, TOTAL
	}

	public Collection<Datatype> getAncestors();

	public boolean getBounded();

	public cardinality getCardinality();

	public Collection<Facet> getFacets();

	public Map<? extends Facet, ? extends Object> getKnownFacetValues();

	public boolean getNumeric();

	public ordered getOrdered();

	/**
	 * @return true if l is a literal with compatible datatype and value included
	 *         in this datatype value space
	 */
	public boolean isCompatible(Literal l);

	public boolean isInValueSpace(Literal l);

	/**
	 * parses a literal form to a value in the datatype value space; for use
	 * when building Literals
	 */
	public Object parseValue(String s);
	/** @return true if this datatype has type as an ancestor */
	public boolean isSubDatatypeExpression(DataTypeExpression type);


	public boolean isCompatible(DataTypeExpression type);

	/**
	 * @param datatypes
	 *            list of datatypes to intersect with this datatype to obtain a
	 *            list of values. All datatypes, including this, need to be
	 *            pairwise compatible or the enumeration of values will be
	 *            empty; for datatypes which are not finite, the intersection
	 *            must be finite or an empty collection will be returned.
	 * @return the list of possible values for this datatype which are
	 *         compatible with the listed datatypes.
	 */
	public Collection<Literal> listValues(LiteralFactory factory, DataTypeExpression... datatypes);


}
