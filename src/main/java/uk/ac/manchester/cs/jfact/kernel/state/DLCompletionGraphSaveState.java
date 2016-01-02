package uk.ac.manchester.cs.jfact.kernel.state;

import java.io.Serializable;

import conformance.PortedFrom;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** class for S/R local state */
@PortedFrom(file = "dlCompletionGraph.h", name = "SaveState")
public class DLCompletionGraphSaveState implements Serializable {


    /** number of valid nodes */
    private int nNodes = 0;
    /** end pointer of saved nodes */
    private int sNodes = 0;
    /** number of used edges */
    private int nEdges = 0;

    /** @return nNodes */
    public int getnNodes() {
        return nNodes;
    }

    /**
     * @param nNodes
     *        nNodes
     */
    public void setnNodes(int nNodes) {
        this.nNodes = nNodes;
    }

    /** @return s nodes */
    public int getsNodes() {
        return sNodes;
    }

    /**
     * @param sNodes
     *        sNodes
     */
    public void setsNodes(int sNodes) {
        this.sNodes = sNodes;
    }

    /** @return n edges */
    public int getnEdges() {
        return nEdges;
    }

    /**
     * @param nEdges
     *        nEdges
     */
    public void setnEdges(int nEdges) {
        this.nEdges = nEdges;
    }

    @Override
    public String toString() {
        return "CGSaveState (" + nNodes + ',' + nEdges + ',' + sNodes + ')';
    }
}
