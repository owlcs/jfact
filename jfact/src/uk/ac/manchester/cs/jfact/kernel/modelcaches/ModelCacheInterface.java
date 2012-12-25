package uk.ac.manchester.cs.jfact.kernel.modelcaches;

import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import conformance.PortedFrom;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
@PortedFrom(file = "modelCacheInterface.h", name = "modelCacheInterface")
public abstract class ModelCacheInterface {
    /** flag to show that model contains nominals */
    private boolean hasNominalNode;

    /** Create cache model with given precense of nominals */
    public ModelCacheInterface(boolean flagNominals) {
        setHasNominalNode(flagNominals);
    }

    /** check whether both models have nominals; in this case, merge is
     * impossible */
    public boolean hasNominalClash(ModelCacheInterface p) {
        return isHasNominalNode() && p.isHasNominalNode();
    }

    /** update knoweledge about nominals in the model after merging */
    public void updateNominalStatus(ModelCacheInterface p) {
        setHasNominalNode(isHasNominalNode() | p.isHasNominalNode());
    }

    // mergable part
    /** Check the model cache internal state. */
    public abstract ModelCacheState getState();

    /** check whether two caches can be merged; @return state of "merged" model */
    public abstract ModelCacheState canMerge(ModelCacheInterface p);

    /** Get the tag identifying the cache type */
    public ModelCacheType getCacheType() {
        return ModelCacheType.mctBadType;
    }

    /** get type of cache (deep or shallow) */
    public boolean shallowCache() {
        return true;
    }

    /** log this cache entry (with given level) */
    public void logCacheEntry(int level, LogAdapter l) {}

    public void setHasNominalNode(boolean hasNominalNode) {
        this.hasNominalNode = hasNominalNode;
    }

    public boolean isHasNominalNode() {
        return hasNominalNode;
    }
}
