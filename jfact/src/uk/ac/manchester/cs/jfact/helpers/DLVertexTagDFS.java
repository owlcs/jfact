package uk.ac.manchester.cs.jfact.helpers;

import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;
import conformance.PortedFrom;

@PortedFrom(file = "dlVertex.h", name = "DLVertexTagDFS")
class DLVertexTagDFS {
    protected DagTag op; // 17 types
    /** aux field for DFS in presence of cycles */
    protected boolean visitedPos = false;
    /** aux field for DFS in presence of cycles */
    protected boolean processedPos = false;
    /** true iff node is involved in cycle */
    protected boolean inCyclePos = false;
    /** aux field for DFS in presence of cycles */
    protected boolean visitedNeg = false;
    /** aux field for DFS in presence of cycles */
    protected boolean processedNeg = false;
    /** true iff node is involved in cycle */
    protected boolean inCycleNeg = false;
    /** cache for the positive entry */
    protected ModelCacheInterface pCache = null;
    /** cache for the negative entry */
    protected ModelCacheInterface nCache = null;

    protected DLVertexTagDFS(DagTag op) {
        this.op = op;
    }

    // tag access
    /** @return tag of the CE */
    @PortedFrom(file = "dlVertex.h", name = "Type")
    public DagTag getType() {
        return op;
    }

    // DFS-related method
    /** check whether current Vertex is being visited
     * 
     * @param pos
     * @return true if being visited */
    @PortedFrom(file = "dlVertex.h", name = "isVisited")
    public boolean isVisited(boolean pos) {
        return pos ? visitedPos : visitedNeg;
    }

    /** check whether current Vertex is processed
     * 
     * @param pos
     * @return true if processed */
    @PortedFrom(file = "dlVertex.h", name = "isProcessed")
    public boolean isProcessed(boolean pos) {
        return pos ? processedPos : processedNeg;
    }

    /** set that the node is being visited
     * 
     * @param pos */
    @PortedFrom(file = "dlVertex.h", name = "setVisited")
    public void setVisited(boolean pos) {
        if (pos) {
            visitedPos = true;
        } else {
            visitedNeg = true;
        }
    }

    /** set that the node' DFS processing is completed
     * 
     * @param pos */
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

    /** check whether concept is in cycle
     * 
     * @param pos
     * @return true if concept is in cycle */
    @PortedFrom(file = "dlVertex.h", name = "isInCycle")
    public boolean isInCycle(boolean pos) {
        return pos ? inCyclePos : inCycleNeg;
    }

    /** set concept is in cycle
     * 
     * @param pos */
    @PortedFrom(file = "dlVertex.h", name = "setInCycle")
    public void setInCycle(boolean pos) {
        if (pos) {
            inCyclePos = true;
        } else {
            inCycleNeg = true;
        }
    }

    /** @return cache wrt positive flag
     * @param pos */
    @PortedFrom(file = "dlVertex.h", name = "getCache")
    public ModelCacheInterface getCache(boolean pos) {
        return pos ? pCache : nCache;
    }

    /** set cache wrt positive flag; note that cache is set up only once
     * 
     * @param pos
     * @param p */
    @PortedFrom(file = "dlVertex.h", name = "setCache")
    public void setCache(boolean pos, ModelCacheInterface p) {
        if (pos) {
            pCache = p;
        } else {
            nCache = p;
        }
    }
}
