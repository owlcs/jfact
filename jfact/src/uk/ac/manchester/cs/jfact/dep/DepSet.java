package uk.ac.manchester.cs.jfact.dep;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.NoSuchElementException;

import uk.ac.manchester.cs.jfact.helpers.FastSetSimple;

public final class DepSet {
    public final static DepSet create() {
        return new DepSet();
    }

    public final static DepSet create(final int i) {
        return new DepSet(i);
    }

    public final static DepSet create(final DepSet dep) {
        DepSet toReturn = new DepSet();
        toReturn.add(dep);
        return toReturn;
    }

    public final static DepSet plus(final DepSet ds1, final DepSet ds2) {
        DepSet toReturn = new DepSet();
        toReturn.add(ds1);
        toReturn.add(ds2);
        return toReturn;
    }

    public final static DepSet create(final FastSetSimple delegate) {
        return new DepSet(delegate);
    }

    private FastSetSimple delegate = null;

    public DepSet() {}

    public DepSet(final FastSetSimple delegate) {
        this.delegate = delegate;
    }

    // to be used to get the FastSet and store it in CWDArray save/restore
    public FastSetSimple getDelegate() {
        return this.delegate;
    }

    DepSet(final int i) {
        //		only case in which the delegate is modified instead of a copy being made
        this.delegate = new FastSetSimple();
        this.delegate.add(i);
    }

    public int level() {
        if (this.delegate == null) {
            return 0;
        }
        if (this.delegate.size() == 0) {
            return 0;
        } else {
            return this.delegate.get(this.delegate.size() - 1);
        }
    }

    public boolean isEmpty() {
        return this.delegate == null || this.delegate.isEmpty();
    }

    public boolean contains(final int level) {
        return this.delegate != null && this.delegate.contains(level);
    }

    @Override
    public String toString() {
        if (this.delegate == null) {
            return "";
        }
        if (!this.delegate.isEmpty()) {
            StringBuilder b = new StringBuilder("{");
            b.append(this.delegate.get(0));
            for (int i = 1; i < this.delegate.size(); i++) {
                b.append(',');
                b.append(this.delegate.get(i));
            }
            b.append('}');
            return b.toString();
        }
        return "";
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof DepSet) {
            DepSet obj2 = (DepSet) obj;
            return this.delegate == null
                    && obj2.delegate == null
                    || (this.delegate != null ? this.delegate.equals(obj2.delegate)
                            : false); //obj2.delegate.equals(delegate);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.delegate == null ? 0 : this.delegate.hashCode();
    }

    public int size() {
        return this.delegate == null ? 0 : this.delegate.size();
    }

    public int get(final int i) {
        if (this.size() == 0) {
            throw new NoSuchElementException("the index " + i + " is not valid");
        }
        return this.delegate.get(i);
    }

    public void restrict(final int level) {
        if (this.delegate != null) {
            FastSetSimple f = new FastSetSimple();
            for (int i = 0; i < this.delegate.size() && this.delegate.get(i) < level; i++) {
                f.add(this.delegate.get(i));
            }
            if (f.size() == 0) {
                this.delegate = null;
            } else {
                this.delegate = f;
            }
        }
        // if the depset is empty, no operation
    }

    public void clear() {
        this.delegate = null;
    }

    public void add(final DepSet toAdd) {
        if (toAdd == null || toAdd.size() == 0) {
            return;
        }
        if (this.delegate == null) {
            this.delegate = toAdd.delegate;
        } else {
            this.delegate = new FastSetSimple(this.delegate, toAdd.delegate);
        }
    }

    public void add(final FastSetSimple d) {
        if (d == null || d.size() == 0) {
            return;
        }
        if (this.delegate == null) {
            this.delegate = d;
        } else {
            this.delegate = new FastSetSimple(this.delegate, d);
        }
    }
}
