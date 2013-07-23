package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
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
    @PortedFrom(file = "tSignature.h", name = "Set")
    private Set<NamedEntity> set = new HashSet<NamedEntity>();
    /** true if concept TOP-locality; false if concept BOTTOM-locality */
    @PortedFrom(file = "tSignature.h", name = "topCLocality")
    private boolean topCLocality = false;
    /** true if role TOP-locality; false if role BOTTOM-locality */
    @PortedFrom(file = "tSignature.h", name = "topRLocality")
    private boolean topRLocality = false;

    @SuppressWarnings("javadoc")
    public TSignature() {}

    /** @param copy */
    public TSignature(TSignature copy) {
        set.addAll(copy.set);
        topCLocality = copy.topCLocality;
        topRLocality = copy.topRLocality;
    }

    /** add names to signature
     * 
     * @param p */
    @PortedFrom(file = "tSignature.h", name = "add")
    public void add(NamedEntity p) {
        set.add(p);
    }

    /** remove given element from a signature
     * 
     * @param p */
    @PortedFrom(file = "tSignature.h", name = "remove")
    public void remove(NamedEntity p) {
        set.remove(p);
    }

    /** add another signature to a given one
     * 
     * @param Sig */
    @PortedFrom(file = "tSignature.h", name = "add")
    public void add(TSignature Sig) {
        set.addAll(Sig.set);
    }

    /** set new locality polarity
     * 
     * @param top */
    @PortedFrom(file = "tSignature.h", name = "setLocality")
    public void setLocality(boolean top) {
        this.setLocality(top, top);
    }

    /** set new locality polarity
     * 
     * @param topC
     * @param topR */
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

    /** @param entity
     * @return true iff SIGnature does NOT contain given entity */
    @PortedFrom(file = "LocalityChecker.h", name = "nc")
    public boolean nc(NamedEntity entity) {
        return !containsNamedEntity(entity);
    }

    /** @param p
     * @return true iff signature contains given element */
    @Original
    public boolean containsNamedEntity(NamedEntity p) {
        return set.contains(p);
    }

    /** @param p
     * @return true iff signature contains given element */
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

    /** @return named entites */
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

    /** @return true iff concepts are treated as TOPs */
    @PortedFrom(file = "tSignature.h", name = "botCLocal")
    public boolean botCLocal() {
        return !topCLocal();
    }

    /** @return true iff roles are treated as TOPs */
    @PortedFrom(file = "tSignature.h", name = "botRLocal")
    public boolean botRLocal() {
        return !topRLocal();
    }

    /** @param s2
     * @return true if this and s2 intersect */
    @Original
    public List<NamedEntity> intersect(TSignature s2) {
        List<NamedEntity> ret = new ArrayList<NamedEntity>();
        Set<NamedEntity> s = new HashSet<NamedEntity>(set);
        s.retainAll(s2.set);
        ret.addAll(s);
        return ret;
    }

    /** @return true if *THIS \subseteq SIG (\subset if IMPROPER = false ) */
    @PortedFrom(file = "tSignature.h", name = "subset")
    public boolean subset(TSignature sig, boolean improper) {
        boolean subset = sig.set.containsAll(set);
        if (improper) {
            return subset && sig.set.size() != set.size();
        }
        return subset;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("[");
        for (NamedEntity p : set) {
            b.append(p.getName()).append(" ");
        }
        b.append("]");
        return b.toString();
    }
}
