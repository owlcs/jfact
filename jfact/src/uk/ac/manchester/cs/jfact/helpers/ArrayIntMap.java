package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Arrays;

@SuppressWarnings("javadoc")
public class ArrayIntMap {
    private int[][] values;
    private int size = 0;
    private static int defaultSize = 16;

    private int insertionIndex(int key) {
        if (size == 0) {
            return -1;
        }
        if (key < values[0][0]) {
            return -1;
        }
        if (key > values[0][size - 1]) {
            return -size - 1;
        }
        int lowerbound = 0;
        if (size < AbstractFastSet.limit) {
            for (; lowerbound < size; lowerbound++) {
                if (values[0][lowerbound] == key) {
                    return lowerbound;
                }
                if (values[0][lowerbound] > key) {
                    return -lowerbound - 1;
                }
            }
            return -lowerbound - 1;
        }
        int upperbound = size - 1;
        while (lowerbound <= upperbound) {
            int intermediate = lowerbound + (upperbound - lowerbound) / 2;
            if (values[0][intermediate] == key) {
                return intermediate;
            }
            if (values[0][intermediate] < key) {
                lowerbound = intermediate + 1;
            } else {
                upperbound = intermediate - 1;
            }
        }
        return -lowerbound - 1;
    }

    private void init() {
        values = new int[2][defaultSize];
        size = 0;
    }

    public void put(int e, int v) {
        int pos = -1;
        if (values == null) {
            init();
            // pos stays at -1, in an empty set that's the place to start - it
            // will become 0
        } else {
            // else find the right place
            pos = insertionIndex(e);
        }
        if (pos > -1) {
            return;
        }
        int i = -pos - 1;
        // i is now the insertion point
        if (i >= values[0].length || size >= values[0].length) {
            // no space left, increase
            int[][] replacementvalues = new int[2][values[0].length + defaultSize];
            for (int j = 0; j < values[0].length; j++) {
                replacementvalues[0][j] = values[0][j];
                replacementvalues[1][j] = values[1][j];
            }
            values = replacementvalues;
        }
        // size ensured, shift and insert now
        for (int j = size - 1; j >= i; j--) {
            values[0][j + 1] = values[0][j];
            values[1][j + 1] = values[1][j];
        }
        values[0][i] = e;
        values[1][i] = v;
        // increase used size
        size++;
    }

    public void clear() {
        values = null;
        size = 0;
    }

    public boolean containsKey(int o) {
        if (values != null) {
            boolean b = insertionIndex(o) > -1;
            return b;
        }
        return false;
    }

    public boolean containsAll(ArrayIntMap c) {
        if (c.size == 0) {
            return true;
        }
        if (size == 0) {
            return false;
        }
        if (c.size > size) {
            return false;
        }
        if (size == c.size) {
            for (int i = 0; i < size; i++) {
                if (values[0][i] != c.values[0][i]) {
                    return false;
                }
            }
            return true;
        }
        if (values[0][0] > c.values[0][0]
                || values[0][size - 1] < c.values[0][c.size() - 1]) {
            // c boundaries are outside this set
            return false;
        }
        int i = 0;
        int j = 0;
        int currentValue;
        while (j < c.size()) {
            currentValue = c.values[0][j];
            boolean found = false;
            while (i < size) {
                if (values[0][i] == currentValue) {
                    // found the current value, next element in c - increase j
                    found = true;
                    break;
                }
                if (values[0][i] > currentValue) {
                    // found a value larger than the value it's looking for - c
                    // is not contained
                    return false;
                }
                // get(i) is < than current value: check next i
                i++;
            }
            if (!found) {
                // finished exploring this and currentValue was not found - it
                // happens if currentValue < any element in this set
                return false;
            }
            j++;
        }
        return true;
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
                values[0][j] = values[0][j + 1];
                values[1][j] = values[1][j + 1];
            }
            size--;
        }
        if (size == 0) {
            values = null;
        }
    }

    public boolean containsValue(int value) {
        for (int i = 0; i < size; i++) {
            if (values[1][i] == value) {
                return true;
            }
        }
        return false;
    }

    public int[] values() {
        return Arrays.copyOf(values[1], size);
    }

    public int get(int key) {
        int index = insertionIndex(key);
        if (index < 0) {
            return Integer.MIN_VALUE;
        }
        return values[1][index];
    }

    public int[] keySet() {
        return Arrays.copyOf(values[0], size);
    }

    public int keySet(int i) {
        return values[0][i];
    }

    @Override
    public int hashCode() {
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum += values[0][i];
        }
        return sum;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof ArrayIntMap) {
            ArrayIntMap o = (ArrayIntMap) obj;
            if (size != o.size) {
                return false;
            }
            if (size == 0) {
                // same size and both empty: equal
                return true;
            }
            for (int i = 0; i < size; i++) {
                if (values[0][i] != o.values[0][i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if (values != null) {
            return Arrays.toString(Arrays.copyOf(values[0], size)) + "\n"
                    + Arrays.toString(Arrays.copyOf(values[1], size));
        }
        return "[]";
    }
}
