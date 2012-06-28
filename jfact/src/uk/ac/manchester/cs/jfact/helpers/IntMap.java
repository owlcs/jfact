package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.List;

public final class IntMap<V> {
    class Entry implements Comparable<Entry> {
        int index;
        V value;

        @Override
        public int hashCode() {
            return index;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            if (obj instanceof IntMap.Entry) {
                return index == ((IntMap.Entry) obj).index;
            }
            return false;
        }

        public int compareTo(final Entry arg0) {
            return this.index - arg0.index;
        }

        @Override
        public String toString() {
            return "{" + index + " " + value + "}";
        }
    }

    private final List<Entry> values = new ArrayList<Entry>();

    public void clear() {
        values.clear();
        size = 0;
    }

    public boolean containsKey(final int key) {
        boolean toReturn = insertionIndex(key) > -1;
        return toReturn;
    }

    private int insertionIndex(final int key) {
        if (size == 0) {
            return -1;
        }
        if (key < values.get(0).index) {
            return -1;
        }
        if (key > values.get(size - 1).index) {
            return -size() - 1;
        }
        int lowerbound = 0;
        if (size < AbstractFastSet.limit) {
            for (; lowerbound < size; lowerbound++) {
                int v = values.get(lowerbound).index;
                if (v == key) {
                    return lowerbound;
                }
                if (v > key) {
                    return -lowerbound - 1;
                }
            }
            return -lowerbound - 1;
        }
        int upperbound = size - 1;
        while (lowerbound <= upperbound) {
            int intermediate = lowerbound + (upperbound - lowerbound) / 2;
            int v = values.get(intermediate).index;
            if (v == key) {
                return intermediate;
            }
            if (v < key) {
                lowerbound = intermediate + 1;
            } else {
                upperbound = intermediate - 1;
            }
        }
        return -lowerbound - 1;
    }

    public boolean containsValue(final V value) {
        for (int i = 0; i < size; i++) {
            if (values.get(i).value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(final IntMap<V> c) {
        if (c.size == 0) {
            return true;
        }
        if (size == 0) {
            return false;
        }
        if (c.size > size) {
            return false;
        }
        if (values.get(0).index > c.values.get(0).index
                || values.get(size - 1).index < c.values.get(c.size - 1).index) {
            // c boundaries are outside this set
            return false;
        }
        int i = 0;
        int j = 0;
        int currentValue;
        while (j < c.size) {
            currentValue = c.values.get(j).index;
            boolean found = false;
            while (i < size) {
                if (values.get(i).index == currentValue) {
                    // found the current value, next element in c - increase j
                    found = true;
                    break;
                }
                if (values.get(i).index > currentValue) {
                    // found a value larger than the value it's looking for - c is not contained
                    return false;
                }
                // get(i) is < than current value: check next i
                i++;
            }
            if (!found) {
                // finished exploring this and currentValue was not found - it happens if currentValue < any element in this set
                return false;
            }
            j++;
        }
        return true;
    }

    public List<Entry> entrySet() {
        return values;
    }

    public int index(final int key) {
        return insertionIndex(key);
    }

    public V get(final int key) {
        int index = insertionIndex(key);
        if (index < 0) {
            return null;
        }
        return values.get(index).value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int[] keySet() {
        int[] keys = new int[size];
        for (int i = 0; i < size; i++) {
            keys[i] = values.get(i).index;
        }
        return keys;
    }

    int size = 0;

    public void put(final int key, final V value) {
        int index = insertionIndex(key);
        if (index > -1) {
            values.get(index).value = value;
            return;
        }
        index = -index - 1;
        Entry e = new Entry();
        e.index = key;
        e.value = value;
        values.add(index, e);
        size++;
    }

    public V remove(final int key) {
        int index = insertionIndex(key);
        if (index > -1) {
            size--;
            return values.remove(index).value;
        }
        return null;
    }

    public int size() {
        return size;
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof IntMap) {
            return values.equals(((IntMap<?>) obj).values);
        }
        return false;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
