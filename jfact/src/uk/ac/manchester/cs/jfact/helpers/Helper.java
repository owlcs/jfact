package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.List;

public final class Helper {
	public static void resize(final List<?> l, final int n) {
		if (l.size() > n) {
			while (l.size() > n) {
				l.remove(l.size() - 1);
			}
		} else {
			while (l.size() < n) {
				l.add(null);
			}
		}
	}

	public static <T> void resize(final List<T> l, final int n, final T filler) {
		if (l.size() > n) {
			while (l.size() > n) {
				l.remove(l.size() - 1);
			}
		} else {
			while (l.size() < n) {
				l.add(filler);
			}
		}
	}

	public static final int InitBranchingLevelValue = 1;
	public static final int bpINVALID = 0;
	public static final int bpTOP = 1;
	public static final int bpBOTTOM = -1;

	public static final int createBiPointer(final int index, final boolean pos) {
		return pos ? index : -index;
	}

	public static boolean isCorrect(final int p) {
		return p != bpINVALID;
	}

	public static final boolean isValid(final int p) {
		return p != bpINVALID;
	}
}
