package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
public final class IntCache {
	private static int hits = 0;
	private static int miss = 0;
	int lastcontained = Integer.MAX_VALUE;
	int lastnotcontained = Integer.MAX_VALUE;

	public final boolean isContained(final int key) {
		//		if (lastcontained == key) {
		//			hits++;
		//		} else {
		//			miss++;
		//		}
		//		if (hits % 100000 == 0 || miss % 100000 == 0) {
		//			System.out.println("LightIntCache.isContained() " + miss / (hits + 1));
		//		}
		return lastcontained == key;
	}

	public final boolean isNotContained(final int key) {
		//		if (lastnotcontained == key) {
		//			hits++;
		//		} else {
		//			miss++;
		//		}
		//		if (hits % 100000 == 0 || miss % 100000 == 0) {
		//			System.out.println("LightIntCache.isContained() " + miss / (hits + 1));
		//		}
		return lastnotcontained == key;
	}

	public final void add(final int key) {
		if (lastnotcontained == key) {
			lastnotcontained = Integer.MAX_VALUE;
		}
	}

	public final void delete(final int key) {
		if (lastcontained == key) {
			lastcontained = Integer.MAX_VALUE;
		}
	}

	public final void hit(final int key) {
		if (lastnotcontained == key) {
			lastnotcontained = Integer.MAX_VALUE;
		}
		lastcontained = key;
	}

	public final void miss(final int key) {
		if (lastcontained == key) {
			lastcontained = Integer.MAX_VALUE;
		}
		lastnotcontained = key;
	}

	public final void resetContained() {
		lastcontained = Integer.MAX_VALUE;
	}

	public final void resetNotContained() {
		lastnotcontained = Integer.MAX_VALUE;
	}
}
