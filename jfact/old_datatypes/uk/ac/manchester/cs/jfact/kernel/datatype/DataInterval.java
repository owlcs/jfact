package uk.ac.manchester.cs.jfact.kernel.datatype;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
public class DataInterval<O> {
	/** left border of the interval */
	protected DatatypeRepresentation<O> min;
	/** right border of the interval */
	protected DatatypeRepresentation<O> max;
	/** type of the left border */
	protected boolean minExcl;
	/** type of the right border */
	protected boolean maxExcl;

	public DataInterval() {}

	public DataInterval(DataInterval<O> copy) {
		min = copy.min == null ? null : copy.min.getDatatype().build(copy.min.getValue());
		max = copy.max == null ? null : copy.max.getDatatype().build(copy.max.getValue());
		minExcl = copy.minExcl;
		maxExcl = copy.maxExcl;
	}

	/** check if min value range have been set */
	public boolean hasMin() {
		return min != null;
	}

	/** check if max value range have been set */
	public boolean hasMax() {
		return max != null;
	}

	/** no constraints */
	public boolean isEmpty() {
		return !hasMin() && !hasMax();
	}

	/** closed interval */
	public boolean closed() {
		return hasMin() && hasMax();
	}

	/** update MIN border of an interval with VALUE wrt EXCL */
	public boolean updateMin(boolean excl, final DatatypeRepresentation<O> value) {
		if (hasMin()) {
			// another min value: check if we need update
			// constraint is >= or >
			if (value.lesser(min)) {
				return false;
			}
			if (min.equals(value) && minExcl && !excl) {
				// was: (5,}; now: [5,}: no update needed
				return false;
				// fallthrough: update is necessary for everything else
			}
		}
			min = value.getDatatype().build(value.getValue());
			minExcl = min.correctMin(excl);

		return true;
	}

	/** update MAX border of an interval with VALUE wrt EXCL */
	public boolean updateMax(boolean excl, final DatatypeRepresentation<O> value) {
		if (hasMax()) {
			// another max value: check if we need update
			// constraint is <= or <
			if (max.lesser(value)) {
				return false;
			}
			if (max.equals(value) && maxExcl && !excl) {
				// was: {,5); now: {,5]: no update needed
				return false;
				// fallthrough: update is necessary for everything else
			}
		}

			max = value.getDatatype().build(value.getValue());
			maxExcl = max.correctMax(excl);
		return true;
	}

	/** update given border of an interval with VALUE wrt EXCL */
	public boolean update(boolean minimum, boolean excl,
			final DatatypeRepresentation<O> value) {
		return minimum ? updateMin(excl, value) : updateMax(excl, value);
	}

	/** @return true iff all the data is consistent wrt given TYPE */
	public boolean consistent(DatatypeRepresentation<?> dtype) {
		if (hasMin() && !min.getDatatype().compatible(dtype.getDatatype())) {
			return false;
		}
		if (hasMax() && !max.getDatatype().compatible(dtype.getDatatype())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (hasMin()) {
			b.append((minExcl ? '(' : '['));
			b.append(min);
		} else {
			b.append('{');
		}
		b.append(",");
		if (hasMax()) {
			b.append(max);
			b.append((maxExcl ? ')' : ']'));
		} else {
			b.append('}');
		}
		return b.toString();
	}
}