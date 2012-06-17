package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
public interface FastSet {
	void add(int e);

	void addAll(FastSet c);

	void clear();

	boolean contains(int o);

	boolean containsAll(FastSet c);

	boolean isEmpty();

	void remove(int o);

	int size();

	int[] toIntArray();

	boolean intersect(FastSet f);

	int get(int i);

	void removeAt(int o);

	void removeAll(int i, int end);

	void removeAllValues(int... values);

	boolean containsAny(FastSet c);

	void completeSet(int value);
}
