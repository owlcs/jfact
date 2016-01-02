package uk.ac.manchester.cs.jfact.kernel.modelcaches;

import java.io.Serializable;

import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import conformance.Original;
import conformance.PortedFrom;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** model cache */
@PortedFrom(file = "modelCacheInterface.h", name = "modelCacheInterface")
public abstract class ModelCacheInterface implements Serializable {


    /** flag to show that model contains nominals */
    @PortedFrom(file = "modelCacheInterface.h", name = "hasNominalNode")
    private boolean hasNominalNode;

    /**
     * Create cache model with given precense of nominals
     * 
     * @param flagNominals
     *        flagNominals
     */
    public ModelCacheInterface(boolean flagNominals) {
        setHasNominalNode(flagNominals);
    }

    /**
     * @param p
     *        p
     * @return check whether both models have nominals; in this case, merge is
     *         impossible
     */
    @PortedFrom(file = "modelCacheInterface.h", name = "hasNominalClash")
    public boolean hasNominalClash(ModelCacheInterface p) {
        return isHasNominalNode() && p.isHasNominalNode();
    }

    /**
     * update knoweledge about nominals in the model after merging
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "modelCacheInterface.h", name = "updateNominalStatus")
    public void updateNominalStatus(ModelCacheInterface p) {
        setHasNominalNode(isHasNominalNode() || p.isHasNominalNode());
    }

    // mergable part
    /** @return Check the model cache internal state. */
    @PortedFrom(file = "modelCacheInterface.h", name = "getState")
    public abstract ModelCacheState getState();

    /**
     * check whether two caches can be merged;
     * 
     * @param p
     *        p
     * @return state of "merged" model
     */
    @PortedFrom(file = "modelCacheInterface.h", name = "canMerge")
    public abstract ModelCacheState canMerge(ModelCacheInterface p);

    /** @return the tag identifying the cache type */
    @PortedFrom(file = "modelCacheInterface.h", name = "getCacheType")
    public ModelCacheType getCacheType() {
        return ModelCacheType.BADTYPE;
    }

    /** @return type of cache (deep or shallow) */
    @PortedFrom(file = "modelCacheInterface.h", name = "shallowCache")
    public boolean shallowCache() {
        return true;
    }

    /**
     * log this cache entry (with given level)
     * 
     * @param level
     *        level
     * @param l
     *        l
     */
    @PortedFrom(file = "modelCacheInterface.h", name = "logCacheEntry")
    public abstract void logCacheEntry(int level, LogAdapter l);

    /**
     * @param hasNominalNode
     *        hasNominalNode
     */
    @Original
    public void setHasNominalNode(boolean hasNominalNode) {
        this.hasNominalNode = hasNominalNode;
    }

    /** @return has nominal node */
    @Original
    public boolean isHasNominalNode() {
        return hasNominalNode;
    }
}
