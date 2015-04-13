package uk.ac.manchester.cs.jfact.dep;

import java.io.Serializable;

import conformance.Original;
import conformance.PortedFrom;
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * Dependency set
 * 
 * @author ignazio
 */
@PortedFrom(file = "tDepSet.h", name = "TDepSet")
public class DepSet implements Serializable {

    private static final long serialVersionUID = 11000L;

    /** @return empty depset */
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
     * @param values
     *        values
     * @return new depset with stated values
     */
    public static DepSet create(int... values) {
        TIntSet set = new TIntHashSet();
        for (int i : values) {
            set.add(i);
        }
        return create(set);
    }

    /**
     * @param dep
     *        dep
     * @return copy of dep
     */
    @PortedFrom(file = "tDepSet.h", name = "create")
    public static DepSet create(DepSet dep) {
        DepSet toReturn = new DepSet();
        toReturn.add(dep);
        return toReturn;
    }

    /**
     * @param ds1
     *        ds1
     * @param ds2
     *        ds2
     * @return union of ds1 and ds2
     */
    @PortedFrom(file = "tDepSet.h", name = "+")
    public static DepSet plus(DepSet ds1, DepSet ds2) {
        if (ds1 == null && ds2 == null) {
            return new DepSet();
        }
        if (ds1 == null || ds1.isEmpty()) {
            return new DepSet(ds2 == null ? null : ds2.delegate);
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
    public static DepSet create(TIntSet delegate) {
        return new DepSet(delegate);
    }

    @Original
    private TIntSet delegate = null;

    protected DepSet() {}

    /**
     * @param d
     *        d
     */
    public DepSet(TIntSet d) {
        delegate = d;
    }

    /**
     * to be used to get the FastSet and store it in CWDArray save/restore
     * 
     * @return delegate
     */
    @Original
    public TIntSet getDelegate() {
        return delegate;
    }

    protected DepSet(int i) {
        // only case in which the delegate is modified
        delegate = new TIntHashSet();
        delegate.add(i);
    }

    /** @return last delegate */
    @PortedFrom(file = "tDepSet.h", name = "level")
    public int level() {
        return max(delegate);
    }

    private static int max(TIntSet set) {
        if (set == null || set.isEmpty()) {
            return 0;
        }
        int max = 0;
        TIntIterator iterator = set.iterator();
        while (iterator.hasNext()) {
            int next = iterator.next();
            if (next > max) {
                max = next;
            }
        }
        return max;
    }

    /** @return true if empty or null delegate */
    @PortedFrom(file = "tDepSet.h", name = "empty")
    public boolean isEmpty() {
        return delegate == null || delegate.isEmpty();
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "";
        }
        StringBuilder b = new StringBuilder("{");
        TIntIterator iterator = delegate.iterator();
        while (iterator.hasNext()) {
            b.append(iterator.next());
            if (iterator.hasNext()) {
                b.append(',');
            }
        }
        b.append('}');
        return b.toString();
    }

    @Override
    public boolean equals(Object obj) {
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
            return delegate.equals(obj2.delegate);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return delegate == null ? 0 : delegate.hashCode();
    }

    /** @return delegate size */
    @PortedFrom(file = "tDepSet.h", name = "size")
    public int size() {
        return delegate == null ? 0 : delegate.size();
    }

    /**
     * @param level
     *        level to cut the delegate to
     */
    @PortedFrom(file = "tDepSet.h", name = "restrict")
    public void restrict(int level) {
        if (delegate != null) {
            TIntSet f = new TIntHashSet();
            TIntIterator it = delegate.iterator();
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
    public void add(DepSet toAdd) {
        if (toAdd == null || toAdd.size() == 0) {
            return;
        }
        if (delegate == null) {
            delegate = toAdd.delegate;
            return;
        }
        if (delegate.containsAll(toAdd.delegate)) {
            return;
        }
        TIntSet newSet = new TIntHashSet(delegate);
        newSet.addAll(toAdd.delegate);
        delegate = newSet;
    }

    /**
     * @param d
     *        add all elements in the depset to this depset
     */
    @PortedFrom(file = "tDepSet.h", name = "add")
    public void add(TIntSet d) {
        if (d == null || d.size() == 0) {
            return;
        }
        if (delegate == null) {
            delegate = d;
            return;
        }
        if (delegate.containsAll(d)) {
            return;
        }
        TIntSet newSet = new TIntHashSet(delegate);
        newSet.addAll(d);
        delegate = newSet;
    }
}
