package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
@SuppressWarnings("javadoc")
public class IntCache {
    private static int hits = 0;
    private static int miss = 0;
    int lastcontained = Integer.MAX_VALUE;
    int lastnotcontained = Integer.MAX_VALUE;

    public boolean isContained(int key) {
        return lastcontained == key;
    }

    public boolean isNotContained(int key) {
        return lastnotcontained == key;
    }

    public void add(int key) {
        if (lastnotcontained == key) {
            lastnotcontained = Integer.MAX_VALUE;
        }
    }

    public void delete(int key) {
        if (lastcontained == key) {
            lastcontained = Integer.MAX_VALUE;
        }
    }

    public void hit(int key) {
        if (lastnotcontained == key) {
            lastnotcontained = Integer.MAX_VALUE;
        }
        lastcontained = key;
    }

    public void miss(int key) {
        if (lastcontained == key) {
            lastcontained = Integer.MAX_VALUE;
        }
        lastnotcontained = key;
    }

    public void resetContained() {
        lastcontained = Integer.MAX_VALUE;
    }

    public void resetNotContained() {
        lastnotcontained = Integer.MAX_VALUE;
    }
}
