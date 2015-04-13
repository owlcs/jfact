package uk.ac.manchester.cs.jfact.kernel.modelcaches;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheState.*;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;

/** model cache const */
@PortedFrom(file = "modelCacheConst.h", name = "modelCacheConst")
public class ModelCacheConst extends ModelCacheInterface {

    private static final long serialVersionUID = 11000L;
    /** the itself */
    @PortedFrom(file = "modelCacheConst.h", name = "isTop")
    private final boolean isTop;

    /**
     * c'tor: no nominals can be here
     * 
     * @param top
     *        top
     */
    public ModelCacheConst(boolean top) {
        super(false);
        isTop = top;
    }

    /** Check if the model contains clash */
    @Override
    @PortedFrom(file = "modelCacheConst.h", name = "getState")
    public ModelCacheState getState() {
        return isTop ? csValid : csInvalid;
    }

    /** @return the value of the constant */
    @PortedFrom(file = "modelCacheConst.h", name = "getConst")
    public boolean getConst() {
        return isTop;
    }

    // mergable part
    /**
     * check whether two caches can be merged;
     * 
     * @return state of "merged" model
     */
    @Override
    @PortedFrom(file = "modelCacheConst.h", name = "canMerge")
    public ModelCacheState canMerge(ModelCacheInterface p) {
        if (p.getCacheType() == ModelCacheType.mctConst) {
            return isTop && ((ModelCacheConst) p).isTop ? csValid : csInvalid;
        } else {
            return p.canMerge(this);
        }
    }

    /** Get the tag identifying the cache type */
    @Override
    @PortedFrom(file = "modelCacheConst.h", name = "getCacheType")
    public ModelCacheType getCacheType() {
        return ModelCacheType.mctConst;
    }

    /** log this cache entry (with given level) */
    @Override
    @PortedFrom(file = "modelCacheConst.h", name = "logCacheEntry")
    public void logCacheEntry(int level, LogAdapter l) {
        l.printTemplate(Templates.LOGCACHEENTRY, isTop ? "TOP" : "BOTTOM");
    }

    /**
     * @param bp
     *        bp
     * @return const cache by BP; BP should be either bpTOP or bpBOTTOM
     */
    @PortedFrom(file = "modelCacheConst.h", name = "createConstCache")
    public static ModelCacheConst createConstCache(int bp) {
        assert bp == Helper.bpTOP || bp == Helper.bpBOTTOM;
        return new ModelCacheConst(bp == Helper.bpTOP);
    }
}
