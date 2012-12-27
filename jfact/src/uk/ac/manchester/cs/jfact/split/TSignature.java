package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import conformance.Original;
import conformance.PortedFrom;

/** class to hold the signature of a module */
@PortedFrom(file = "tSignature.h", name = "TSignature")
public class TSignature {
    /** set to keep all the elements in signature */
    private Set<NamedEntity> set = new HashSet<NamedEntity>();
    /** true if concept TOP-locality; false if concept BOTTOM-locality */
    private boolean topCLocality = false;
    /** true if role TOP-locality; false if role BOTTOM-locality */
    private boolean topRLocality = false;

    public TSignature() {}

    public TSignature(TSignature copy) {
        set.addAll(copy.set);
        topCLocality = copy.topCLocality;
        topRLocality = copy.topRLocality;
    }

    /** add names to signature */
    @PortedFrom(file = "tSignature.h", name = "add")
    public void add(NamedEntity p) {
        set.add(p);
    }

    /** remove given element from a signature */
    @PortedFrom(file = "tSignature.h", name = "remove")
    public void remove(NamedEntity p) {
        set.remove(p);
    }

    /** add another signature to a given one */
    @PortedFrom(file = "tSignature.h", name = "add")
    void add(TSignature Sig) {
        set.addAll(Sig.set);
    }

    /** set new locality polarity */
    @PortedFrom(file = "tSignature.h", name = "setLocality")
    public void setLocality(boolean top) {
        this.setLocality(top, top);
    }

    /** set new locality polarity */
    @PortedFrom(file = "tSignature.h", name = "setLocality")
    public void setLocality(boolean topC, boolean topR) {
        topCLocality = topC;
        topRLocality = topR;
    }

    // comparison
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof TSignature) {
            return set.equals(((TSignature) obj).set);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return set.hashCode();
    }

    /** check whether 2 signatures are the same */
    // boolean operator == ( TSignature& sig ) { return Set == sig.Set; }
    /** check whether 2 signatures are different */
    // boolean operator != ( TSignature& sig ) { return Set != sig.Set; }
    /** operator < */
    // boolean operator < ( TSignature& sig ) { return Set < sig.Set; }
    /** @return true iff signature contains given element */
    @Original
    public boolean containsNamedEntity(NamedEntity p) {
        return set.contains(p);
    }

    /** @return true iff signature contains given element */
    @PortedFrom(file = "tSignature.h", name = "contains")
    public boolean contains(Expression p) {
        if (p instanceof NamedEntity) {
            return containsNamedEntity((NamedEntity) p);
        }
        if (p instanceof ObjectRoleInverse) {
            return contains(((ObjectRoleInverse) p).getOR());
        }
        return false;
    }

    /** @return size of the signature */
    @PortedFrom(file = "tSignature.h", name = "size")
    public int size() {
        return set.size();
    }

    /** clear the signature */
    @PortedFrom(file = "tSignature.h", name = "clear")
    public void clear() {
        set.clear();
    }

    @PortedFrom(file = "tSignature.h", name = "begin")
    public Set<NamedEntity> begin() {
        return set;
    }

    /** @return true iff concepts are treated as TOPs */
    @PortedFrom(file = "tSignature.h", name = "topCLocal")
    public boolean topCLocal() {
        return topCLocality;
    }

    /** @return true iff roles are treated as TOPs */
    @PortedFrom(file = "tSignature.h", name = "topRLocal")
    public boolean topRLocal() {
        return topRLocality;
    }

    @Original
    public List<NamedEntity> intersect(TSignature s2) {
        List<NamedEntity> ret = new ArrayList<NamedEntity>();
        Set<NamedEntity> s = new HashSet<NamedEntity>(set);
        s.retainAll(s2.set);
        ret.addAll(s);
        return ret;
    }
}
