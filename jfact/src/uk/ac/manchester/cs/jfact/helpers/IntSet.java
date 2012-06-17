package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Arrays;

public final class IntSet extends AbstractFastSet {
	private final static int size = 17;
	FastSetSimple[] hashvalues = new FastSetSimple[size];

	private int hash(final int value) {
		//return value & hashbase;
		return Math.abs(value % size);
	}

	public void clear() {
		hashvalues = new FastSetSimple[size];
	}

	public boolean contains(final int key) {
		int hash = hash(key);
		if (hashvalues[hash] == null) {
			return false;
		}
		boolean b = hashvalues[hash].contains(key);
		return b;
	}

	public void add(final int e) {
		int hash = hash(e);
		if (hashvalues[hash] == null) {
			hashvalues[hash] = new FastSetSimple();
		}
		int previous = hashvalues[hash].size();
		hashvalues[hash].add(e);
		if (hashvalues[hash].size() != previous) {
			allValues = null;
		}
	}

	public void addAll(final FastSet c) {
		for (int i = 0; i < c.size(); i++) {
			add(c.get(i));
		}
	}

	public boolean containsAll(final FastSet c) {
		if (c instanceof IntSet) {
			IntSet set = (IntSet) c;
			for (int i = 0; i < set.hashvalues.length; i++) {
				if (set.hashvalues[i] != null) {
					if (hashvalues[i] == null) {
						return false;
					}
				}
			}
			for (int i = 0; i < set.hashvalues.length; i++) {
				if (set.hashvalues[i] != null) {
					if (!hashvalues[i].containsAll(set.hashvalues[i])) {
						return false;
					}
				}
			}
		} else {
			for (int i = 0; i < c.size(); i++) {
				if (!contains(c.get(i))) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean containsAny(final FastSet c) {
		if (c instanceof IntSet) {
			IntSet set = (IntSet) c;
			for (int i = 0; i < set.hashvalues.length; i++) {
				if (set.hashvalues[i] != null && hashvalues[i] != null) {
					if (hashvalues[i].containsAny(set.hashvalues[i])) {
						return true;
					}
				}
			}
		} else {
			for (int i = 0; i < c.size(); i++) {
				if (contains(c.get(i))) {
					return true;
				}
			}
		}
		return false;
	}

	public void remove(final int o) {
		int hash = hash(o);
		if (hashvalues[hash] == null) {
			return;
		}
		int previous = hashvalues[hash].size();
		hashvalues[hash].remove(o);
		if (hashvalues[hash].size() != previous) {
			allValues = null;
			if (hashvalues[hash].size() == 0) {
				hashvalues[hash] = null;
			}
		}
	}

	private int[] allValues = null;

	public int[] toIntArray() {
		if (allValues == null) {
			int i = 0;
			for (FastSet f : hashvalues) {
				if (f != null) {
					i += f.size();
				}
			}
			allValues = new int[i];
			int j = 0;
			for (FastSet f : hashvalues) {
				if (f != null) {
					for (int k = 0; k < f.size(); k++) {
						allValues[j] = f.get(k);
						j++;
					}
				}
			}
			Arrays.sort(allValues);
		}
		return Arrays.copyOf(allValues, size);
	}

	public boolean intersect(final FastSet f) {
		for (int i = 0; i < f.size(); i++) {
			if (contains(f.get(i))) {
				return true;
			}
		}
		return false;
	}

	public int get(final int i) {
		if (allValues == null) {
			toIntArray();
		}
		return allValues[i];
	}

	public void removeAt(final int o) {
		if (allValues == null) {
			toIntArray();
		}
		remove(allValues[o]);
		allValues = null;
	}

	public void removeAll(final int i, final int end) {
		for (int j = i; j < end; j++) {
			removeAt(j);
		}
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int size() {
		int i = 0;
		for (FastSet f : hashvalues) {
			if (f != null) {
				i += f.size();
			}
		}
		return i;
	}

	public void removeAllValues(final int... values) {
		for (int i : values) {
			remove(i);
		}
	}

	public void completeSet(final int value) {
		for (int i = 0; i <= value; i++) {
			add(i);
		}
		//XXX notice: these sets go to negative numbers. Is this the best way?
	}
}
