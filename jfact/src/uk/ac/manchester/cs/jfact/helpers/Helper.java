package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.List;

/** static methods */
public class Helper {
    /** @param l
     * @param n */
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

    /** @param l
     * @param n
     * @param filler */
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
    public static int InitBranchingLevelValue = 1;
    /** invalid bipolar pointer */
    public static int bpINVALID = 0;
    /** top bipolar pointer */
    public static int bpTOP = 1;
    /** bottom bipolar pointer */
    public static int bpBOTTOM = -1;

    /** @param index
     * @param pos
     * @return bipolar pointer */
    public static int createBiPointer(int index, boolean pos) {
        return pos ? index : -index;
    }

    /** @param p
     * @return true if correct */
    public static boolean isCorrect(int p) {
        return p != bpINVALID;
    }

    /** @param p
     * @return true if valid */
    public static boolean isValid(int p) {
        return p != bpINVALID;
    }
}
