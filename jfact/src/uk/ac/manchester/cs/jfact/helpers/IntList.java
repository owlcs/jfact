package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Arrays;

public final class IntList extends AbstractFastSet {
	protected int[] values;
	protected int size = 0;
	protected static final int defaultSize = 16;

	public IntList() {}

	public final int get(final int i) {
		if (values != null) {
			return values[i];
		}
		throw new IllegalArgumentException("Illegal argument " + i + ": no such element");
	}

	protected final void init() {
		values = new int[defaultSize];
		size = 0;
	}

	public final void add(final int e) {
		if (values == null) {
			init();
		}
		// i is now the insertion point
		if (size >= values.length) {
			// no space left, increase
			values = Arrays.copyOf(values, values.length + defaultSize);
		}
		values[size] = e;
		size++;
	}

	public final void addAll(final FastSet c) {
		if (c.isEmpty()) {
			return;
		}
		// merge two sorted arrays: how bad can it be?
		if (values == null) {
			//extreme case: just copy the other set
			values = Arrays.copyOf(((IntList) c).values, c.size());
			size = c.size();
			return;
		}
		for (int i = 0; i < c.size(); i++) {
			add(c.get(i));
		}
	}

	public final void clear() {
		values = null;
		size = 0;
	}

	public final boolean contains(final int o) {
		if (values != null) {
			for (int i = 0; i < size; i++) {
				if (values[i] == o) {
					return true;
				}
			}
		}
		return false;
	}

	public final boolean containsAll(final FastSet c) {
		if (c.isEmpty()) {
			return true;
		}
		if (isEmpty()) {
			return false;
		}
		if (c.size() > size) {
			return false;
		}
		for (int i = 0; i < c.size(); i++) {
			if (!contains(c.get(i))) {
				return false;
			}
		}
		return true;
	}

	public final boolean isEmpty() {
		return values == null;
	}

	public final boolean containsAny(final FastSet c) {
		if (c.isEmpty() || isEmpty()) {
			return false;
		}
		for (int i = 0; i < c.size(); i++) {
			if (contains(c.get(i))) {
				return true;
			}
		}
		return false;
	}

	public final void remove(final int o) {
		if (values == null) {
			return;
		}
		for (int i = 0; i < size; i++) {
			if (values[i] == o) {
				removeAt(i);
			}
		}
	}

	public final int size() {
		return size;
	}

	public final int[] toIntArray() {
		if (values == null) {
			return new int[0];
		}
		return Arrays.copyOf(values, size);
	}

	public final boolean intersect(final FastSet f) {
		return containsAny(f);
	}

	@Override
	public final boolean equals(final Object arg0) {
		if (arg0 == null) {
			return false;
		}
		if (this == arg0) {
			return true;
		}
		if (arg0 instanceof FastSet) {
			FastSet arg = (FastSet) arg0;
			if (size != arg.size()) {
				return false;
			}
			for (int i = 0; i < size(); i++) {
				if (arg.get(i) != get(i)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	public final void removeAt(final int i) {
		if (values == null) {
			return;
		}
		if (i > -1 && i < size) {
			if (size == 1) {
				values = null;
				size = 0;
				return;
			}
			for (int j = i; j < size - 1; j++) {
				values[j] = values[j + 1];
			}
			size--;
		}
		if (size == 0) {
			values = null;
		}
	}

	public final void removeAll(final int i, final int end) {
		if (values == null) {
			return;
		}
		if (end < -1 || end < i || end > size || i < -1 || i > size) {
			throw new IllegalArgumentException("illegal arguments: " + i + " " + end
					+ " size: " + size);
		}
		if (size == 1 || i == 0 && end == size) {
			values = null;
			size = 0;
			return;
		}
		if (end == size) {
			size = i;
		} else {
			int delta = end - i;
			for (int j = i; j < size - delta; j++) {
				values[j] = values[j + delta];
			}
			size -= delta;
		}
		if (size == 0) {
			values = null;
		}
	}

	public final void removeAllValues(final int... vals) {
		if (values == null) {
			return;
		}
		for (int i : vals) {
			remove(i);
		}
		if (size == 0) {
			values = null;
		}
	}

	public void completeSet(final int value) {
		for (int i = 0; i <= value; i++) {
			add(i);
		}
		//XXX notice: these sets go to negative numbers. Is this the best way?
	}
}
