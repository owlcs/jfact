package uk.ac.manchester.cs.jfact.helpers;

import java.io.Serializable;
import java.util.Collection;
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.List;

/** static methods */
public class Helper implements Serializable {

    private static final long serialVersionUID = 11000L;

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
        for (Object o : S1) {
            if (S2.contains(o)) {
                return true;
            }
        }
        return false;
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
