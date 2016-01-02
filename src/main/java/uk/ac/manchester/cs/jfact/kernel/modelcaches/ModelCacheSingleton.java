package uk.ac.manchester.cs.jfact.kernel.modelcaches;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import conformance.PortedFrom;

/**
 * Model caching implementation for singleton models. Such models contains only
 * one [negated] concept in completion tree. Reduced set of operations, but very
 * efficient.
 */
@PortedFrom(file = "modelCacheSingleton.h", name = "modelCacheSingleton")
public class ModelCacheSingleton extends ModelCacheInterface {


    /** the singleton itself */
    @PortedFrom(file = "modelCacheSingleton.h", name = "Singleton")
    private final int singleton;

    /**
     * @param bp
     *        bp
     */
    public ModelCacheSingleton(int bp) {
        super(false);
        singleton = bp;
    }

    /** Check if the model contains clash */
    @Override
    @PortedFrom(file = "modelCacheSingleton.h", name = "getState")
    public ModelCacheState getState() {
        return ModelCacheState.VALID;
    }

    /** @return internal value */
    @PortedFrom(file = "modelCacheSingleton.h", name = "getValue")
    public int getValue() {
        return singleton;
    }

    // mergable part
    /**
     * check whether two caches can be merged;
     * 
     * @return state of "merged" model
     */
    @Override
    @PortedFrom(file = "modelCacheSingleton.h", name = "canMerge")
    public ModelCacheState canMerge(ModelCacheInterface p) {
        switch (p.getCacheType()) {
            case CONST: // TOP/BOTTOM: the current node can't add anything to
                           // the result
                return p.getState();
            case SINGLETON: // it can be a clash
                return ((ModelCacheSingleton) p).singleton == -singleton ? ModelCacheState.INVALID
                        : ModelCacheState.VALID;
            case IAN: // ask more intellegent object
                return p.canMerge(this);
            case BADTYPE: // error
            default:
                return ModelCacheState.UNKNOWN;
        }
    }

    /** Get the tag identifying the cache type */
    @Override
    @PortedFrom(file = "modelCacheSingleton.h", name = "getCacheType")
    public ModelCacheType getCacheType() {
        return ModelCacheType.SINGLETON;
    }

    /** log this cache entry (with given level) */
    @Override
    @PortedFrom(file = "modelCacheSingleton.h", name = "logCacheEntry")
    public void logCacheEntry(int level, LogAdapter l) {
        l.print("\nSingleton cache: element ").print(singleton);
    }
}
