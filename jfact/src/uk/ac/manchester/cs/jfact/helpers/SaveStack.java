package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.LinkedList;

public class SaveStack<T> {
	protected final LinkedList<T> list = new LinkedList<T>();

	/** get an object from a fixed depth */
	public T pop(final int depth) {
		top(depth);
		return pop();
	}

	/** get an object from a fixed depth */
	public T top(final int depth) {
		assert list.size() >= depth;
		while (list.size() > depth) {
			pop();
		}
		return list.peek();
	}

	public T top() {
		return list.peek();
	}

	public T pop() {
		assert !list.isEmpty();
		T pop = list.pop();
		return pop;
	}

	public void push(final T e) {
		list.push(e);
	}

	public void clear() {
		list.clear();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public int size() {
		return list.size();
	}
}
