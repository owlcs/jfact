package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** stat index enum */
public enum StatIndex {
    //@formatter:off
    /** Depth */    DEPTH, 
    /** Size */     SIZE, 
    /** Branch */   BRANCH, 
    /** Gener */    GENER, 
    /** Freq */     FREQ;
    //@formatter:on

    /**
     * @param pos
     *        positive or negative
     * @return index for stat
     */
    public int getIndex(boolean pos) {
        return ordinal() * 2 + (pos ? 0 : 1);
    }

    /**
     * @param c
     *        stat
     * @return index for chosen stat
     */
    public static int choose(char c) {
        switch (c) {
            case 'S':
                return SIZE.getIndex(false);
            case 'D':
                return DEPTH.getIndex(false);
            case 'B':
                return BRANCH.getIndex(false);
            case 'G':
                return GENER.getIndex(false);
            default:
                return FREQ.getIndex(false);
        }
    }

    /**
     * add-up all stat values at once by explicit values
     * 
     * @param d
     *        d
     * @param s
     *        s
     * @param b
     *        b
     * @param g
     *        g
     * @param pos
     *        pos
     * @param stat
     *        stat
     * @param debug
     *        true if debug values
     */
    public static void updateStatValues(int d, int s, int b, int g, boolean pos, int[] stat, boolean debug) {
        stat[SIZE.getIndex(pos)] += s;
        stat[BRANCH.getIndex(pos)] += b;
        stat[GENER.getIndex(pos)] += g;
        if (d > stat[DEPTH.getIndex(pos)]) {
            stat[DEPTH.getIndex(pos)] = d;
        }
    }

    /**
     * add-up all values at once by a given vertex
     * 
     * @param v
     *        v
     * @param posV
     *        posV
     * @param pos
     *        pos
     * @param stat
     *        stat
     * @param debug
     *        true if debug values
     */
    public static void updateStatValues(DLVertex v, boolean posV, boolean pos, int[] stat, boolean debug) {
        updateStatValues(getDepth(posV, v.stat), getSize(posV, v.stat), getBranch(posV, v.stat), getGener(posV, v.stat),
            pos, stat, debug);
    }

    /**
     * increment frequency value
     * 
     * @param pos
     *        pos
     * @param stat
     *        stat
     */
    public static void incFreqValue(boolean pos, int[] stat) {
        stat[FREQ.getIndex(pos)] += 1;
    }

    /**
     * general access to a stat value by index
     * 
     * @param pos
     *        pos
     * @param stat
     *        stat
     * @return depth
     */
    public static int getDepth(boolean pos, int[] stat) {
        return stat[DEPTH.getIndex(pos)];
    }

    /**
     * general access to a stat value by index
     * 
     * @param pos
     *        pos
     * @param stat
     *        stat
     * @return size
     */
    protected static int getSize(boolean pos, int[] stat) {
        return stat[SIZE.getIndex(pos)];
    }

    /**
     * general access to a stat value by index
     * 
     * @param pos
     *        pos
     * @param stat
     *        stat
     * @return branch
     */
    protected static int getBranch(boolean pos, int[] stat) {
        return stat[BRANCH.getIndex(pos)];
    }

    /**
     * general access to a stat value by index
     * 
     * @param pos
     *        pos
     * @param stat
     *        stat
     * @return gener
     */
    protected static int getGener(boolean pos, int[] stat) {
        return stat[GENER.getIndex(pos)];
    }

    /**
     * general access to a stat value by index
     * 
     * @param pos
     *        pos
     * @param stat
     *        stat
     * @return freq
     */
    protected static int getFreq(boolean pos, int[] stat) {
        return stat[FREQ.getIndex(pos)];
    }
}
