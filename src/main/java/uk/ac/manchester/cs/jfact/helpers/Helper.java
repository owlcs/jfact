package uk.ac.manchester.cs.jfact.helpers;

import java.io.Serializable;
import java.util.Collection;
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/** static methods */
public class Helper implements Serializable {

    private static final long serialVersionUID = 11000L;

    /**
     * @param l
     *        list to walk
     * @param f
     *        consumer
     * @param <T>
     *        type
     */
    public static <T> void pairs(List<T> l, BiConsumer<T, T> f) {
        for (int i = 0; i < l.size() - 1; i++) {
            f.accept(l.get(i), l.get(i + 1));
        }
    }

    /**
     * @param l
     *        list to walk
     * @param f
     *        consumer
     * @param <T>
     *        type
     */
    public static <T> void allPairs(List<T> l, BiConsumer<T, T> f) {
        for (int i = 0; i < l.size() - 1; i++) {
            for (int j = i + 1; j < l.size(); j++) {
                f.accept(l.get(i), l.get(j));
            }
        }
    }

    /**
     * @param l
     *        list to walk
     * @param f
     *        consumer
     * @param <T>
     *        type
     * @return true if any match
     */
    public static <T> boolean anyMatchOnAllPairs(List<T> l,
        BiFunction<T, T, Boolean> f) {
        for (int i = 0; i < l.size() - 1; i++) {
            for (int j = i + 1; j < l.size(); j++) {
                if (f.apply(l.get(i), l.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param l
     *        list to walk
     * @param f
     *        consumer
     * @param <T>
     *        type
     * @return true if all match
     */
    public static <T> boolean allMatchOnAllPairs(List<T> l,
        BiFunction<T, T, Boolean> f) {
        for (int i = 0; i < l.size() - 1; i++) {
            for (int j = i + 1; j < l.size(); j++) {
                if (!f.apply(l.get(i), l.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param l
     *        list to walk
     * @param f
     *        consumer
     * @param <T>
     *        type
     * @return true if all match
     */
    public static <T> boolean anyMatchOnPairs(List<T> l,
        BiFunction<T, T, Boolean> f) {
        for (int i = 0; i < l.size() - 1; i++) {
            if (f.apply(l.get(i), l.get(i + 1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param l
     *        list to walk
     * @param f
     *        consumer
     * @param <T>
     *        type
     * @return true if aall match
     */
    public static <T> boolean allMatchOnPairs(List<T> l,
        BiFunction<T, T, Boolean> f) {
        for (int i = 0; i < l.size() - 1; i++) {
            if (!f.apply(l.get(i), l.get(i + 1))) {
                return false;
            }
        }
        return true;
    }

    /**
     * check whether set S1 intersects with the set S2
     * 
     * @param S1
     *        S1
     * @param S2
     *        S2
     * @return true if S1 and S2 intersect
     */
    public static boolean intersectsWith(Collection<?> S1, Collection<?> S2) {
        return S1.stream().anyMatch(o -> S2.contains(o));
    }

    /**
     * Find an element in the intersection of two collections
     * 
     * @param S1
     *        S1
     * @param S2
     *        S2
     * @param <T>
     *        type
     * @return one element from the intersection if S1 and S2 intersect
     */
    public static <T> Optional<T> elementFromIntersection(Collection<T> S1,
        Collection<T> S2) {
        return S1.stream().filter(o -> S2.contains(o)).findAny();
    }

    /**
     * @param l
     *        l
     * @param n
     *        n
     */
    public static void resize(List<?> l, int n) {
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

    /**
     * @param l
     *        l
     * @param n
     *        n
     * @param <T>
     *        argument type
     * @param filler
     *        filler
     */
    public static <T> void resize(List<T> l, int n, T filler) {
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

    /** brancing level value */
    public static final int InitBranchingLevelValue = 1;
    /** invalid bipolar pointer */
    public static final int bpINVALID = 0;
    /** top bipolar pointer */
    public static final int bpTOP = 1;
    /** bottom bipolar pointer */
    public static final int bpBOTTOM = -1;

    /**
     * @param index
     *        index
     * @param pos
     *        pos
     * @return bipolar pointer
     */
    public static int createBiPointer(int index, boolean pos) {
        return pos ? index : -index;
    }

    /**
     * @param p
     *        p
     * @return true if correct
     */
    public static boolean isCorrect(int p) {
        return p != bpINVALID;
    }

    /**
     * @param p
     *        p
     * @return true if valid
     */
    public static boolean isValid(int p) {
        return p != bpINVALID;
    }
}
