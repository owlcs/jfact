package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Arrays;

public class SortedIntList {
    protected int[] values;
    protected int size = 0;
    protected static int defaultSize = 16;

    protected int insertionIndex(int key) {
        if (size == 0) {
            return 0;
        }
        for (int i = 0; i < size; i++) {
            if (key > values[i]) {
                return i;
            }
            if (key == values[i]) {
                return -1;
            }
        }
        return size;
    }

    public SortedIntList() {}

    public int get(int i) {
        if (values != null) {
            return values[i];
        }
        throw new IllegalArgumentException("Illegal argument " + i + ": no such element");
    }

    protected void init() {
        values = new int[defaultSize];
        Arrays.fill(values, Integer.MIN_VALUE);
        size = 0;
    }

    public void add(int e) {
        int pos = -1;
        if (values == null) {
            init();
            // pos stays at -1, in an empty set that's the place to start - it
            // will become 0
        }
        // else find the right place
        pos = insertionIndex(e);
        if (pos < 0) {
            return;
        }
        int i = pos;
        // i is now the insertion point
        if (i >= values.length || size >= values.length) {
            // no space left, increase
            values = Arrays.copyOf(values, values.length + defaultSize);
        }
        // size ensured, shift and insert now
        for (int j = size - 1; j >= i; j--) {
            values[j + 1] = values[j];
        }
        values[i] = e;
        // increase used size
        size++;
    }

    public void clear() {
        values = null;
        size = 0;
    }

    public boolean contains(int o) {
        if (values != null) {
            int i = insertionIndex(o);
            boolean toReturn = i > -1;
            return toReturn;
        }
        return false;
    }

    public boolean isEmpty() {
        return values == null;
    }

    public void remove(int o) {
        if (values == null) {
            return;
        }
        int i = insertionIndex(o);
        removeAt(i);
    }

    public int size() {
        return size;
    }

    public int[] toIntArray() {
        if (values == null) {
            return new int[0];
        }
        return Arrays.copyOf(values, size);
    }

    public void removeAt(int i) {
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
}
