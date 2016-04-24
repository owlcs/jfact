package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import javax.annotation.Nullable;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;

@PortedFrom(file = "dlVertex.h", name = "DLVertexTagDFS")
class DLVertexTagDFS implements Serializable {

    @PortedFrom(file = "dlVertex.h", name = "Op") protected DagTag dagtag;
    /** aux field for DFS in presence of cycles */
    @PortedFrom(file = "dlVertex.h", name = "VisitedPos") protected boolean visitedPos = false;
    /** aux field for DFS in presence of cycles */
    @PortedFrom(file = "dlVertex.h", name = "ProcessedPos") protected boolean processedPos = false;
    /** true iff node is involved in cycle */
    @PortedFrom(file = "dlVertex.h", name = "inCyclePos") protected boolean inCyclePos = false;
    /** aux field for DFS in presence of cycles */
    @PortedFrom(file = "dlVertex.h", name = "VisitedNeg") protected boolean visitedNeg = false;
    /** aux field for DFS in presence of cycles */
    @PortedFrom(file = "dlVertex.h", name = "ProcessedNeg") protected boolean processedNeg = false;
    /** true iff node is involved in cycle */
    @PortedFrom(file = "dlVertex.h", name = "inCycleNeg") protected boolean inCycleNeg = false;
    /** cache for the positive entry */
    @PortedFrom(file = "dlVertex.h", name = "pCache") protected ModelCacheInterface pCache = null;
    /** cache for the negative entry */
    @PortedFrom(file = "dlVertex.h", name = "nCache") protected ModelCacheInterface nCache = null;

    @PortedFrom(file = "dlVertex.h", name = "Op")
    protected DLVertexTagDFS(DagTag op) {
        this.dagtag = op;
    }

    // tag access
    /** @return tag of the CE */
    @PortedFrom(file = "dlVertex.h", name = "Type")
    public DagTag getType() {
        return dagtag;
    }

    // DFS-related method
    /**
     * check whether current Vertex is being visited
     * 
     * @param pos
     *        pos
     * @return true if being visited
     */
    @PortedFrom(file = "dlVertex.h", name = "isVisited")
    public boolean isVisited(boolean pos) {
        return pos ? visitedPos : visitedNeg;
    }

    /**
     * check whether current Vertex is processed
     * 
     * @param pos
     *        pos
     * @return true if processed
     */
    @PortedFrom(file = "dlVertex.h", name = "isProcessed")
    public boolean isProcessed(boolean pos) {
        return pos ? processedPos : processedNeg;
    }

    /**
     * set that the node is being visited
     * 
     * @param pos
     *        pos
     */
    @PortedFrom(file = "dlVertex.h", name = "setVisited")
    public void setVisited(boolean pos) {
        if (pos) {
            visitedPos = true;
        } else {
            visitedNeg = true;
        }
    }

    /**
     * set that the node' DFS processing is completed
     * 
     * @param pos
     *        pos
     */
    @PortedFrom(file = "dlVertex.h", name = "setProcessed")
    public void setProcessed(boolean pos) {
        if (pos) {
            processedPos = true;
            visitedPos = false;
        } else {
            processedNeg = true;
            visitedNeg = false;
        }
    }

    /** clear DFS flags */
    @PortedFrom(file = "dlVertex.h", name = "clearDFS")
    public void clearDFS() {
        processedPos = false;
        visitedPos = false;
        processedNeg = false;
        visitedNeg = false;
    }

    /**
     * check whether concept is in cycle
     * 
     * @param pos
     *        pos
     * @return true if concept is in cycle
     */
    @PortedFrom(file = "dlVertex.h", name = "isInCycle")
    public boolean isInCycle(boolean pos) {
        return pos ? inCyclePos : inCycleNeg;
    }

    /**
     * set concept is in cycle
     * 
     * @param pos
     *        pos
     */
    @PortedFrom(file = "dlVertex.h", name = "setInCycle")
    public void setInCycle(boolean pos) {
        if (pos) {
            inCyclePos = true;
        } else {
            inCycleNeg = true;
        }
    }

    /**
     * @return cache wrt positive flag
     * @param pos
     *        pos
     */
    @Nullable
    @PortedFrom(file = "dlVertex.h", name = "getCache")
    public ModelCacheInterface getCache(boolean pos) {
        return pos ? pCache : nCache;
    }

    /**
     * set cache wrt positive flag; note that cache is set up only once
     * 
     * @param pos
     *        pos
     * @param p
     *        p
     */
    @PortedFrom(file = "dlVertex.h", name = "setCache")
    public void setCache(boolean pos, ModelCacheInterface p) {
        if (pos) {
            pCache = p;
        } else {
            nCache = p;
        }
    }
}
