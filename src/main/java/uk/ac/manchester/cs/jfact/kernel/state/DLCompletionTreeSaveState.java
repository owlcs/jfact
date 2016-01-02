package uk.ac.manchester.cs.jfact.kernel.state;

import java.io.Serializable;

import conformance.PortedFrom;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** class for saving Completion Tree nodes state */
@PortedFrom(file = "dlCompletionTree.h", name = "SaveState")
public class DLCompletionTreeSaveState implements Serializable {


    /** saving status of the label */
    private final SaveState lab = new SaveState();
    /** curLevel of the Node structure */
    private int curLevel;
    /** amount of neighbours */
    private int nNeighbours;

    /** @return level of a saved node */
    public int level() {
        return curLevel;
    }

    /** @return save state label */
    public SaveState getLab() {
        return lab;
    }

    /** @return current level */
    public int getCurLevel() {
        return curLevel;
    }

    /** @return neighbors */
    public int getnNeighbours() {
        return nNeighbours;
    }

    /**
     * @param curLevel
     *        curLevel
     */
    public void setCurLevel(int curLevel) {
        this.curLevel = curLevel;
    }

    /**
     * @param nNeighbours
     *        nNeighbours
     */
    public void setnNeighbours(int nNeighbours) {
        this.nNeighbours = nNeighbours;
    }
}
