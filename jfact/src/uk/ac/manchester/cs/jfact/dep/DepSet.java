package uk.ac.manchester.cs.jfact.dep;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.NoSuchElementException;

import uk.ac.manchester.cs.jfact.helpers.FastSetSimple;
import conformance.Original;
import conformance.PortedFrom;

/** Dependency set
 * 
 * @author ignazio */
@PortedFrom(file = "tDepSet.h", name = "TDepSet")
public class DepSet implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** @return empty depset */
    @PortedFrom(file = "tDepSet.h", name = "create")
    public static DepSet create() {
        return new DepSet();
    }

    /** @param i
     * @return depset with value i */
    @PortedFrom(file = "tDepSet.h", name = "create")
    public static DepSet create(int i) {
        return new DepSet(i);
    }

    /** @param dep
     * @return copy of dep */
    @PortedFrom(file = "tDepSet.h", name = "create")
    public static DepSet create(DepSet dep) {
        DepSet toReturn = new DepSet();
        toReturn.add(dep);
        return toReturn;
    }

    /** @param ds1
     * @param ds2
     * @return union of ds1 and ds2 */
    @PortedFrom(file = "tDepSet.h", name = "+")
    public static DepSet plus(DepSet ds1, DepSet ds2) {
        DepSet toReturn = new DepSet();
        toReturn.add(ds1);
        toReturn.add(ds2);
        return toReturn;
    }

    /** @param delegate
     * @return depset wrapper over delegate */
    @PortedFrom(file = "tDepSet.h", name = "create")
    public static DepSet create(FastSetSimple delegate) {
        return new DepSet(delegate);
    }

    @Original
    private FastSetSimple delegate = null;

    protected DepSet() {}

    /** @param d */
    public DepSet(FastSetSimple d) {
        delegate = d;
    }

    /** to be used to get the FastSet and store it in CWDArray save/restore
     * 
     * @return delegate */
    @Original
    public FastSetSimple getDelegate() {
        return delegate;
    }

    protected DepSet(int i) {
        // only case in which the delegate is modified
        delegate = new FastSetSimple();
        delegate.add(i);
    }

    /** @return last delegate */
    @PortedFrom(file = "tDepSet.h", name = "level")
    public int level() {
        if (isEmpty()) {
            return 0;
        } else {
            return delegate.get(delegate.size() - 1);
        }
    }

    /** @return true if empty or null delegate */
    @PortedFrom(file = "tDepSet.h", name = "empty")
    public boolean isEmpty() {
        return delegate == null || delegate.isEmpty();
    }

    /** @param level
     * @return true if delegate contains level */
    @PortedFrom(file = "tDepSet.h", name = "contains")
    public boolean contains(int level) {
        return delegate != null && delegate.contains(level);
    }

    @Override
    public String toString() {
        if (delegate == null) {
            return "";
        }
        if (!delegate.isEmpty()) {
            StringBuilder b = new StringBuilder("{");
            b.append(delegate.get(0));
            for (int i = 1; i < delegate.size(); i++) {
                b.append(',');
                b.append(delegate.get(i));
            }
            b.append('}');
            return b.toString();
        }
        return "";
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

    /** @param i
     * @return element at position i; if no such element exists, throws
     *         NoSuchElementExcepton */
    @PortedFrom(file = "tDepSet.h", name = "get")
    public int get(int i) {
        if (isEmpty()) {
            throw new NoSuchElementException("the index " + i + " is not valid");
        }
        return delegate.get(i);
    }

    /** @param level
     *            level to cut the delegate to */
    @PortedFrom(file = "tDepSet.h", name = "restrict")
    public void restrict(int level) {
        if (delegate != null) {
            FastSetSimple f = new FastSetSimple();
            for (int i = 0; i < delegate.size() && delegate.get(i) < level; i++) {
                f.add(delegate.get(i));
            }
            if (f.size() == 0) {
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

    /** @param toAdd
     *            add all elements in the depset to this depset */
    @PortedFrom(file = "tDepSet.h", name = "add")
    public void add(DepSet toAdd) {
        if (toAdd == null || toAdd.size() == 0) {
            return;
        }
        if (delegate == null) {
            delegate = toAdd.delegate;
        } else {
            delegate = new FastSetSimple(delegate, toAdd.delegate);
        }
    }

    /** @param d
     *            add all elements in the depset to this depset */
    @PortedFrom(file = "tDepSet.h", name = "add")
    public void add(FastSetSimple d) {
        if (d == null || d.size() == 0) {
            return;
        }
        if (delegate == null) {
            delegate = d;
        } else {
            delegate = new FastSetSimple(delegate, d);
        }
    }
}
