package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import javax.annotation.Nullable;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;

/** completion tree arc */
@PortedFrom(file = "dlCompletionTreeArc.h", name = "DlCompletionTreeArc")
public class DlCompletionTreeArc implements Serializable {

    /** pointer to "to" node */
    private final DlCompletionTree node;
    /** role, labelling given arc */
    protected Role role;
    /** dep-set of the arc */
    protected DepSet depSet;
    /** pointer to reverse arc */
    protected DlCompletionTreeArc reverse;
    /** true if the edge going from a predecessor to a successor */
    private boolean succEdge = true;

    /**
     * init an arc with R as a label and NODE on given LEVEL; use it inside
     * MAKEARCS only
     */
    /** class for restoring edge */
    static class EdgeRestorer extends Restorer {

        private final DlCompletionTreeArc arc;
        private final Role role;

        public EdgeRestorer(DlCompletionTreeArc q) {
            arc = q;
            role = q.role;
        }

        @Override
        public void restore() {
            arc.role = role;
            arc.reverse.role = role.inverse();
        }
    }

    /** class for restoring dep-set */
    static class EdgeDepRestorer extends Restorer {

        private final DlCompletionTreeArc arc;
        private final DepSet dep;

        public EdgeDepRestorer(DlCompletionTreeArc q) {
            arc = q;
            dep = DepSet.create(q.getDep());
        }

        @Override
        public void restore() {
            arc.depSet = DepSet.create(dep);
        }
    }

    /**
     * @param r
     *        r
     * @param dep
     *        dep
     * @param n
     *        n
     */
    public DlCompletionTreeArc(Role r, DepSet dep, DlCompletionTree n) {
        role = r;
        depSet = DepSet.create(dep);
        node = n;
        reverse = null;
    }

    /**
     * set given arc as a reverse of current
     * 
     * @param v
     *        v
     */
    public void setReverse(DlCompletionTreeArc v) {
        reverse = v;
        v.reverse = this;
    }

    /** @return label of the edge */
    @Nullable
    public Role getRole() {
        return role;
    }

    /** @return dep-set of the edge */
    public DepSet getDep() {
        return depSet;
    }

    /**
     * set the successor field
     * 
     * @param val
     *        val
     */
    public void setSuccEdge(boolean val) {
        succEdge = val;
    }

    /** @return true if the edge is the successor one */
    public boolean isSuccEdge() {
        return succEdge;
    }

    /** @return true if isSuccEdge and not isBlocked and not isReflexiveEdge */
    public boolean unblockable() {
        return isSuccEdge() && !isIBlocked() && !isReflexiveEdge();
    }

    /** @return true if the edge is the predecessor one */
    public boolean isPredEdge() {
        return !succEdge;
    }

    /** @return end of arc */
    public DlCompletionTree getArcEnd() {
        return node;
    }

    /** @return reverse arc */
    public DlCompletionTreeArc getReverse() {
        return reverse;
    }

    /**
     * @param pRole
     *        pRole
     * @return check if arc is labelled by a super-role of PROLE
     */
    public boolean isNeighbour(Role pRole) {
        return !isIBlocked() && role.lesserequal(pRole);
    }

    /**
     * @param pRole
     *        pRole
     * @return same as above; fills DEP with current DEPSET if so
     */
    @Nullable
    public DepSet neighbourDepSet(Role pRole) {
        if (isNeighbour(pRole)) {
            return DepSet.create(depSet);
        }
        return null;
    }

    /** @return is arc merged to another */
    public boolean isIBlocked() {
        return role == null;
    }

    /** @return check whether the edge is reflexive */
    public boolean isReflexiveEdge() {
        return node.equals(reverse.node);
    }

    /**
     * save and invalidate arc (together with reverse arc)
     * 
     * @return restorer
     */
    @Nullable
    public Restorer save() {
        if (role == null) {
            return null;
        }
        Restorer ret = new EdgeRestorer(this);
        role = null;
        reverse.role = null;
        return ret;
    }

    /**
     * add dep-set to an edge; return restorer
     * 
     * @param dep
     *        dep
     * @return restorer
     */
    @Nullable
    public Restorer addDep(DepSet dep) {
        if (dep.isEmpty()) {
            return null;
        }
        Restorer ret = new EdgeDepRestorer(this);
        depSet.add(dep);
        return ret;
    }

    /**
     * print current arc
     * 
     * @param o
     *        o
     */
    public void print(LogAdapter o) {
        o.printTemplate(Templates.DLCOMPLETIONTREEARC, isIBlocked() ? "-" : role.getIRI(), depSet);
    }
}
