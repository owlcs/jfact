package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** stat index enum */
public enum StatIndex {
    /** Depth */
    Depth,
    /** Size */
    Size,
    /** Branch */
    Branch,
    /** Gener */
    Gener,
    /** Freq */
    Freq;

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
                return Size.getIndex(false);
            case 'D':
                return Depth.getIndex(false);
            case 'B':
                return Branch.getIndex(false);
            case 'G':
                return Gener.getIndex(false);
            default:
                return Freq.getIndex(false);
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
     */
    public static void updateStatValues(int d, int s, int b, int g,
            boolean pos, int[] stat) {
        stat[Size.getIndex(pos)] += s;
        stat[Branch.getIndex(pos)] += b;
        stat[Gener.getIndex(pos)] += g;
        if (d > stat[Depth.getIndex(pos)]) {
            stat[Depth.getIndex(pos)] = d;
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
     */
    public static void updateStatValues(DLVertex v, boolean posV, boolean pos,
            int[] stat) {
        updateStatValues(getDepth(posV, v.stat), getSize(posV, v.stat),
                getBranch(posV, v.stat), getGener(posV, v.stat), pos, stat);
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
        stat[Freq.getIndex(pos)] += 1;
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
        return stat[Depth.getIndex(pos)];
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
        return stat[Size.getIndex(pos)];
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
        return stat[Branch.getIndex(pos)];
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
        return stat[Gener.getIndex(pos)];
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
        return stat[Freq.getIndex(pos)];
    }
}
