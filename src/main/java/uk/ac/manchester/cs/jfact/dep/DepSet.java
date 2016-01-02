package uk.ac.manchester.cs.jfact.dep;

import java.io.Serializable;

import javax.annotation.Nullable;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;

import conformance.Original;
import conformance.PortedFrom;

/**
 * Dependency set
 * 
 * @author ignazio
 */
@PortedFrom(file = "tDepSet.h", name = "TDepSet")
public class DepSet implements Serializable {

    @Original private RoaringBitmap delegate = null;

    protected DepSet() {}

    /**
     * @param d
     *        d
     */
    private DepSet(RoaringBitmap d) {
        delegate = d;
    }

    protected DepSet(int i) {
        delegate = RoaringBitmap.bitmapOf(i);
    }

    /**
     * to be used to get the FastSet and store it in CWDArray save/restore
     * 
     * @return delegate
     */
    @Original
    public RoaringBitmap getDelegate() {
        return delegate;
    }

    /**
     * @return empty depset
     */
    @PortedFrom(file = "tDepSet.h", name = "create")
    public static DepSet create() {
        return new DepSet();
    }

    /**
     * @param i
     *        i
     * @return depset with value i
     */
    @PortedFrom(file = "tDepSet.h", name = "create")
    public static DepSet create(int i) {
        return new DepSet(i);
    }

    /**
     * @param dep
     *        dep
     * @return copy of dep
     */
    @PortedFrom(file = "tDepSet.h", name = "create")
    public static DepSet create(@Nullable DepSet dep) {
        if (dep == null) {
            return create();
        }
        return new DepSet(dep.delegate);
    }

    /**
     * @param ds1
     *        ds1
     * @param ds2
     *        ds2
     * @return union of ds1 and ds2
     */
    @PortedFrom(file = "tDepSet.h", name = "+")
    public static DepSet plus(@Nullable DepSet ds1, @Nullable DepSet ds2) {
        if (ds1 == null && ds2 == null) {
            return new DepSet();
        }
        if (ds1 == null || ds1.isEmpty()) {
            if (ds2 == null || ds2.isEmpty()) {
                return new DepSet();
            }
            return new DepSet(ds2.delegate);
        }
        if (ds2 == null || ds2.isEmpty()) {
            return new DepSet(ds1.delegate);
        }
        DepSet toReturn = new DepSet();
        toReturn.add(ds1);
        toReturn.add(ds2);
        return toReturn;
    }

    /**
     * @param delegate
     *        delegate
     * @return depset wrapper over delegate
     */
    @PortedFrom(file = "tDepSet.h", name = "create")
    public static DepSet create(RoaringBitmap delegate) {
        return new DepSet(delegate);
    }

    /**
     * @return last delegate
     */
    @PortedFrom(file = "tDepSet.h", name = "level")
    public int level() {
        return max(delegate);
    }

    private static int max(@Nullable RoaringBitmap set) {
        if (set == null || set.isEmpty()) {
            return 0;
        }
        return set.getReverseIntIterator().next();
    }

    /**
     * @return true if empty or null delegate
     */
    @PortedFrom(file = "tDepSet.h", name = "empty")
    public boolean isEmpty() {
        return delegate == null || delegate.isEmpty();
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "";
        }
        return "{" + delegate.toString() + "}";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof DepSet) {
            DepSet obj2 = (DepSet) obj;
            if (delegate == null) {
                return obj2.delegate == null;
            }
            if (isEmpty() != obj2.isEmpty()) {
                return false;
            }
            if (isEmpty()) {
                return true;
            }
            return delegate.equals(obj2.delegate);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return delegate == null ? 0 : delegate.hashCode();
    }

    /**
     * @param level
     *        level to cut the delegate to
     */
    @PortedFrom(file = "tDepSet.h", name = "restrict")
    public void restrict(int level) {
        if (delegate != null) {
            RoaringBitmap f = new RoaringBitmap();
            IntIterator it = delegate.getIntIterator();
            while (it.hasNext()) {
                int i = it.next();
                if (i < level) {
                    f.add(i);
                }
            }
            if (f.isEmpty()) {
                delegate = null;
            } else {
                delegate = f;
            }
        }
        // if the depset is empty, no operation
    }

    /** empty the delegate */
    @PortedFrom(file = "tDepSet.h", name = "clear")
    public void clear() {
        delegate = null;
    }

    /**
     * @param toAdd
     *        add all elements in the depset to this depset
     */
    @PortedFrom(file = "tDepSet.h", name = "add")
    public void add(@Nullable DepSet toAdd) {
        if (toAdd == null || toAdd.isEmpty()) {
            return;
        }
        if (delegate == null) {
            delegate = toAdd.delegate;
            return;
        }
        if (delegate.equals(toAdd.delegate)) {
            return;
        }
        delegate = RoaringBitmap.or(delegate, toAdd.delegate);
    }
}
