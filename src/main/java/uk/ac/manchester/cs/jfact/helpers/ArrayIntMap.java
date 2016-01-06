package uk.ac.manchester.cs.jfact.helpers;

import java.io.Serializable;
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Arrays;

import javax.annotation.Nullable;

/**
 * map int to int
 * 
 * @author ignazio
 */
public class ArrayIntMap implements Serializable {

    private int[][] values;
    private int size = 0;
    private static final int DEFAULTSIZE = 16;

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
        if (size < 5) {
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
        values = new int[2][DEFAULTSIZE];
        size = 0;
    }

    /**
     * @param e
     *        e
     * @param v
     *        v
     */
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
            int[][] replacementvalues = new int[2][values[0].length + DEFAULTSIZE];
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

    /** clear the map */
    public void clear() {
        values = null;
        size = 0;
    }

    /**
     * @param o
     *        o
     * @return true if o is a key
     */
    public boolean containsKey(int o) {
        if (values != null) {
            return insertionIndex(o) > -1;
        }
        return false;
    }

    /**
     * @param c
     *        c
     * @return true if all elements in c are contained
     */
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
        if (values[0][0] > c.values[0][0] || values[0][size - 1] < c.values[0][c.size() - 1]) {
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

    /**
     * @param o
     *        o
     * @return value removed
     */
    public int remove(int o) {
        if (values == null) {
            return -1;
        }
        int i = insertionIndex(o);
        return removeAt(i);
    }

    /** @return size */
    public int size() {
        return size;
    }

    /**
     * @param i
     *        i
     * @return value at it
     */
    public int removeAt(int i) {
        int toReturn = -1;
        if (values == null) {
            return toReturn;
        }
        if (i > -1 && i < size) {
            toReturn = values[1][i];
            if (size == 1) {
                values = null;
                size = 0;
                return toReturn;
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
        return toReturn;
    }

    /**
     * @param key
     *        key
     * @return value for key
     */
    public int get(int key) {
        int index = insertionIndex(key);
        if (index < 0) {
            return Integer.MIN_VALUE;
        }
        return values[1][index];
    }

    /**
     * @param i
     *        i
     * @return key at position i
     */
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
    public boolean equals(@Nullable Object obj) {
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
            return Arrays.toString(Arrays.copyOf(values[0], size)) + '\n'
                + Arrays.toString(Arrays.copyOf(values[1], size));
        }
        return "[]";
    }
}
