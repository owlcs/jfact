package uk.ac.manchester.cs.jfact.kernel.modelcaches;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;

/**
 * Model caching implementation for singleton models. Such models contains only
 * one [negated] concept in completion tree. Reduced set of operations, but very
 * efficient.
 */
public final class ModelCacheSingleton extends ModelCacheInterface {
    /** the singleton itself */
    private final int singleton;

    public ModelCacheSingleton(final int bp) {
        super(false);
        singleton = bp;
    }

    /** Check if the model contains clash */
    @Override
    public ModelCacheState getState() {
        return ModelCacheState.csValid;
    }

    /** access to internal value */
    public int getValue() {
        return singleton;
    }

    // mergable part
    /** check whether two caches can be merged; @return state of "merged" model */
    @Override
    public ModelCacheState canMerge(final ModelCacheInterface p) {
        switch (p.getCacheType()) {
            case mctConst: // TOP/BOTTOM: the current node can't add anything to the result
                return p.getState();
            case mctSingleton: // it can be a clash
                return ((ModelCacheSingleton) p).singleton == -singleton ? ModelCacheState.csInvalid
                        : ModelCacheState.csValid;
            case mctIan: // ask more intellegent object
                return p.canMerge(this);
            case mctBadType: // error
            default:
                return ModelCacheState.csUnknown;
        }
    }

    /** Get the tag identifying the cache type */
    @Override
    public ModelCacheType getCacheType() {
        return ModelCacheType.mctSingleton;
    }

    /** log this cache entry (with given level) */
    @Override
    public void logCacheEntry(final int level, final LogAdapter l) {
        l.print("\nSingleton cache: element ", singleton);
    }
}
