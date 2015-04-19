package uk.ac.manchester.cs.jfact.dep;

import java.io.Serializable;

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
        return create(RoaringBitmap.bitmapOf(values));
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
    public static DepSet create(RoaringBitmap delegate) {
        return new DepSet(delegate);
    }

    @Original
    private RoaringBitmap delegate = null;

    protected DepSet() {}

    /**
     * @param d
     *        d
     */
    private DepSet(RoaringBitmap d) {
        delegate = d;
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

    protected DepSet(int i) {
        delegate = RoaringBitmap.bitmapOf(i);
    }

    /** @return last delegate */
    @PortedFrom(file = "tDepSet.h", name = "level")
    public int level() {
        return max(delegate);
    }

    private static int max(RoaringBitmap set) {
        if (set == null || set.isEmpty()) {
            return 0;
        }
        return set.getReverseIntIterator().next();
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
        return "{" + delegate.toString() + "}";
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
        return delegate == null ? 0 : delegate.getCardinality();
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
    public void add(DepSet toAdd) {
        if (toAdd == null || toAdd.size() == 0) {
            return;
        }
        if (delegate == null) {
            delegate = toAdd.delegate;
            return;
        }
        delegate = RoaringBitmap.or(delegate, toAdd.delegate);
    }

    // /**
    // * @param d
    // * add all elements in the depset to this depset
    // */
    // @PortedFrom(file = "tDepSet.h", name = "add")
    // private void add(TIntSet d) {
    // if (d == null || d.size() == 0) {
    // return;
    // }
    // if (delegate == null) {
    // delegate = d;
    // return;
    // }
    // if (delegate.containsAll(d)) {
    // return;
    // }
    // TIntSet newSet = new TIntHashSet(delegate);
    // newSet.addAll(d);
    // delegate = newSet;
    // }
}
