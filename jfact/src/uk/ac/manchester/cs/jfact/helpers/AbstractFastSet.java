package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
abstract class AbstractFastSet implements FastSet {
	public static final int limit = 5;

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('[');
		if (size() > 0) {
			b.append(get(0));
		}
		for (int i = 1; i < size(); i++) {
			b.append(',');
			b.append(' ');
			b.append(get(i));
		}
		b.append(']');
		return b.toString();
	}

	@Override
	public boolean equals(final Object arg0) {
		if (arg0 == null) {
			return false;
		}
		if (this == arg0) {
			return true;
		}
		if (arg0 instanceof FastSet) {
			FastSet f = (FastSet) arg0;
			if (f.size() != size()) {
				return false;
			}
			for (int i = 0; i < size(); i++) {
				if (get(i) != f.get(i)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (size() == 0) {
			return 0;
		}
		int i = 0;
		for (int j = 0; j < size(); j++) {
			i += get(j);
			i *= 37;
		}
		return i;
	}
}
