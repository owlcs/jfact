package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;
import static uk.ac.manchester.cs.jfact.kernel.DagTag.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.chainsaw.FastSet;
import uk.ac.manchester.cs.chainsaw.FastSetFactory;
import uk.ac.manchester.cs.jfact.kernel.DLDag;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.MergableLabel;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.Role;

/**
 * DL Vertex
 * 
 * @author ignazio
 */
@PortedFrom(file = "dlVertex.h", name = "DLVertex")
public class DLVertex extends DLVertexTagDFS {

    class ChildSet implements Comparator<Integer>, Serializable {

        protected final FastSet children = FastSetFactory.create();
        private final SortedIntList original = new SortedIntList();
        private int[] sorted = null;
        protected DLDag sorter = null;

        @Override
        @PortedFrom(file = "dlVertex.h", name = "compare")
        public int compare(@Nullable Integer o1, @Nullable Integer o2) {
            return sorter.compare(o1.intValue(), o2.intValue());
        }

        @Override
        public boolean equals(@Nullable Object arg0) {
            if (arg0 == null) {
                return false;
            }
            if (this == arg0) {
                return true;
            }
            if (arg0 instanceof ChildSet) {
                ChildSet arg = (ChildSet) arg0;
                return children.equals(arg.children);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return children.hashCode();
        }

        public void setSorter(DLDag d) {
            sorter = d;
            sorted = null;
        }

        public int[] sorted() {
            if (sorted == null) {
                sorted = new int[children.size()];
                if (sorter == null) {
                    for (int i = 0; i < children.size(); i++) {
                        // if there is no sorting, use the original insertion
                        // order
                        sorted[i] = original.get(i);
                    }
                } else {
                    List<Integer> l = new ArrayList<>();
                    for (int i = 0; i < children.size(); ++i) {
                        l.add(Integer.valueOf(children.get(i)));
                    }
                    Collections.sort(l, this);
                    for (int i = 0; i < sorted.length; ++i) {
                        sorted[i] = l.get(i).intValue();
                    }
                }
            }
            return sorted;
        }

        public boolean contains(int inverse) {
            return children.contains(inverse);
        }

        public void clear() {
            children.clear();
            sorted = null;
        }

        public boolean add(int p) {
            if (children.add(p)) {
                original.add(p);
                sorted = null;
                return true;
            }
            return false;
        }
    }

    @PortedFrom(file = "dlVertex.h", name = "Child")
    /** set of arguments (CEs, numbers for NR) */
    private final ChildSet child = new ChildSet();
    /** pointer to concept-like entry (for PConcept, etc) */
    @PortedFrom(file = "dlVertex.h", name = "Concept") private NamedEntry concept = null;
    /** pointer to role (for E\A, NR) */
    @PortedFrom(file = "dlVertex.h", name = "Role") private final Role role;
    /** projection role (used for projection op only) */
    @PortedFrom(file = "dlVertex.h", name = "ProjRole") private final Role projRole;
    /** C if available */
    @PortedFrom(file = "dlVertex.h", name = "C") private int conceptIndex;
    /** n if available */
    @PortedFrom(file = "dlVertex.h", name = "n") private final int n;
    /** maximal depth, size and frequency of reference of the expression */
    @PortedFrom(file = "dlVertex.h", name = "Sort") private final MergableLabel sort = new MergableLabel();
    private final DLDag heap;
    /** maximal depth, size and frequency of reference of the expression */
    @PortedFrom(file = "dlVertex.h", name = "stat") protected final int[] stat = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0 };
    /** usage statistic for pos- and neg occurences of a vertex */
    @PortedFrom(file = "dlVertex.h", name = "posUsage") protected final long posUsage = 0;
    @PortedFrom(file = "dlVertex.h", name = "negUsage") protected final long negUsage = 0;

    /**
     * c'tor for Top/CN/And (before adding any operands)
     * 
     * @param op
     *        op
     * @param heap
     *        heap
     */
    public DLVertex(DagTag op, DLDag heap) {
        this(op, 0, null, BP_INVALID, null, heap);
    }

    /**
     * c'tor for max n R_C; and for \A R{n}_C; Note order C, n, R.pointer
     * 
     * @param op
     *        op
     * @param m
     *        m
     * @param r
     *        R
     * @param c
     *        c
     * @param projR
     *        ProjR
     * @param heap
     *        heap
     */
    public DLVertex(DagTag op, int m, @Nullable Role r, int c, @Nullable Role projR, DLDag heap) {
        super(op);
        role = r;
        projRole = projR;
        conceptIndex = c;
        n = m;
        this.heap = heap;
    }

    /**
     * get RW access to the label
     * 
     * @return sort label
     */
    @PortedFrom(file = "dlVertex.h", name = "getSort")
    public MergableLabel getSort() {
        return sort;
    }

    /**
     * merge local label to label LABEL
     * 
     * @param label
     *        label to merge
     */
    @PortedFrom(file = "dlVertex.h", name = "merge")
    public void merge(MergableLabel label) {
        sort.merge(label);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof DLVertex) {
            DLVertex v = (DLVertex) obj;
            return compareIndexes(v) && compareRoles(v) && child.equals(v.child);
        }
        return false;
    }

    protected boolean compareIndexes(DLVertex v) {
        return dagtag == v.dagtag && conceptIndex == v.conceptIndex && n == v.n;
    }

    protected boolean compareRoles(DLVertex v) {
        return compare(role, v.role) && compare(projRole, v.projRole);
    }

    @PortedFrom(file = "dlVertex.h", name = "compare")
    private static boolean compare(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    @Override
    public int hashCode() {
        int hash = dagtag == null ? 0 : dagtag.hashCode();
        hash = 31 * hash + (role == null ? 0 : role.hashCode());
        hash = 31 * hash + (projRole == null ? 0 : projRole.hashCode());
        hash = 31 * hash + conceptIndex;
        hash = 31 * hash + n;
        hash = 31 * hash + (child == null ? 0 : child.hashCode());
        return hash;
    }

    /**
     * @return C for concepts/quantifiers/NR verteces
     */
    @PortedFrom(file = "dlVertex.h", name = "getC")
    public int getConceptIndex() {
        return conceptIndex;
    }

    /**
     * @return N for the (max n R) vertex
     */
    @PortedFrom(file = "dlVertex.h", name = "getNumberLE")
    public int getNumberLE() {
        return n;
    }

    /**
     * @return N for the (min n R) vertex
     */
    @PortedFrom(file = "dlVertex.h", name = "getNumberGE")
    public int getNumberGE() {
        return n + 1;
    }

    /**
     * @return STATE for the (\all R{state}.C) vertex
     */
    @PortedFrom(file = "dlVertex.h", name = "getState")
    public int getState() {
        return n;
    }

    /**
     * @return pointer to the first concept name of the entry
     */
    @PortedFrom(file = "dlVertex.h", name = "begin")
    public int[] begin() {
        return child.sorted();
    }

    /**
     * @return pointer to Role for the Role-like verteces
     */
    @PortedFrom(file = "dlVertex.h", name = "getRole")
    public Role getRole() {
        return role;
    }

    /**
     * @return pointer to Projection Role for the Projection verteces
     */
    @PortedFrom(file = "dlVertex.h", name = "getProjRole")
    public Role getProjRole() {
        return projRole;
    }

    /**
     * @return TConcept for concept-like fields
     */
    @Nullable
    @PortedFrom(file = "dlVertex.h", name = "getConcept")
    public NamedEntry getConcept() {
        return concept;
    }

    /**
     * set TConcept value to entry
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "dlVertex.h", name = "setConcept")
    public void setConcept(NamedEntry p) {
        concept = p;
    }

    /**
     * set a concept (child) to Name-like vertex
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "dlVertex.h", name = "setChild")
    public void setChild(int p) {
        conceptIndex = p;
    }

    /**
     * @param p
     *        p
     * @return true if dtBad
     */
    @PortedFrom(file = "dlVertex.h", name = "addChild")
    public boolean addChild(int p) {
        // if adds to broken vertex -- do nothing
        // if adding BOTTOM -- return clash (empty vertex) immediately
        // this can happen in case of nested simplifications; see bNested1
        if (dagtag == BAD || p == BP_BOTTOM) {
            return true;
        }
        // if adds TOP -- nothing to do
        if (p == BP_TOP) {
            return false;
        }
        if (child.contains(p)) {
            return false;
        }
        if (child.contains(-p)) {
            return true;
        }
        // XXX check this sorting:
        // we need to insert p into set
        // long offset = q - Child.begin();
        // Child.push_back(Child.back());
        // for (q_end = Child.begin()+offset, q = Child.end()-1; q != q_end;
        // --q)
        // *q=*(q-1); // copy the tail
        // *q = p;
        // // FIXME: add some simplification (about AR.C1, AR.c2 etc)
        // return false;
        child.add(p);
        return false;
    }

    /**
     * @return andToDag
     */
    @Original
    public int getAndToDagValue() {
        if (child.children.size() == 0) {
            return BP_TOP;
        }
        if (child.children.size() == 1) {
            return child.children.get(0);
        }
        return BP_INVALID;
    }

    /**
     * @param dag
     *        dag
     */
    @PortedFrom(file = "dlVertex.h", name = "sortEntry")
    public void sortEntry(DLDag dag) {
        if (dagtag != AND) {
            return;
        }
        child.setSorter(dag);
    }

    /**
     * @param extendedStats
     *        true if extended stats should be printed
     * @return toString value
     */
    public String toString(boolean extendedStats) {
        StringBuilder o = new StringBuilder();
        if (extendedStats) {
            o.append(String.format("[d(%s/%s),s(%s/%s),b(%s/%s),g(%s/%s),f(%s/%s)] ", Integer.toString(stat[0]),
                Integer.toString(stat[1]), Integer.toString(stat[2]), Integer.toString(stat[3]),
                Integer.toString(stat[4]), Integer.toString(stat[5]), Integer.toString(stat[6]),
                Integer.toString(stat[7]), Integer.toString(stat[8]), Integer.toString(stat[9])));
        }
        o.append(toString());
        return o.toString();
    }

    @Override
    @Nonnull
    public String toString() {
        switch (dagtag) {
            case AND:
            case COLLECTION:
                break;
            case TOP:
            case NN:
                return dagtag.getName();
            case DATAEXPR:
                return dagtag.getName() + ' ' + concept;
            case DATAVALUE:
            case DATATYPE:
            case PCONCEPT:
            case NCONCEPT:
            case PSINGLETON:
            case NSINGLETON:
                return dagtag.getName() + String.format(Templates.DLVERTEXPRINT2.getTemplate(), concept.getIRI(),
                    dagtag.isNNameTag() ? "=" : "[=", concept);
            case LE:
                return dagtag.getName() + ' ' + n + ' ' + role.getIRI() + ' ' + concept;
            case FORALL:
                return dagtag.getName() + String.format(Templates.DLVERTEXPRINT3.getTemplate(), role.getIRI(),
                    Integer.toString(n), concept);
            case IRR:
                return dagtag.getName() + ' ' + role.getIRI();
            case PROJ:
                return dagtag.getName()
                    + String.format(Templates.DLVERTEXPRINT4.getTemplate(), role.getIRI(), concept, projRole.getIRI());
            case CHOOSE:
                return dagtag.getName() + ' ' + concept;
            default:
                throw new ReasonerInternalException(
                    String.format("Error printing vertex of type %s(%s)", dagtag.getName(), dagtag));
        }
        return childrenToString();
    }

    protected String childrenToString() {
        StringBuilder o = new StringBuilder(dagtag.getName());
        for (int q : child.sorted()) {
            o.append(' ').append(heap.get(q));
        }
        return o.toString();
    }

    /**
     * add-up all stat values at once by explicit values
     * 
     * @param d
     *        d
     * @param s
     *        s
     * @param b
     *        b
     * @param g
     *        g
     * @param pos
     *        pos
     */
    @PortedFrom(file = "dlVertex.h", name = "updateStatValues")
    public void updateStatValues(int d, int s, int b, int g, boolean pos) {
        StatIndex.updateStatValues(d, s, b, g, pos, stat, false);
    }

    /**
     * add-up all values at once by a given vertex
     * 
     * @param v
     *        v
     * @param posV
     *        posV
     * @param pos
     *        pos
     */
    @PortedFrom(file = "dlVertex.h", name = "updateStatValues")
    public void updateStatValues(DLVertex v, boolean posV, boolean pos) {
        StatIndex.updateStatValues(v, posV, pos, stat, false);
    }

    /**
     * increment frequency value
     * 
     * @param pos
     *        pos
     */
    @PortedFrom(file = "dlVertex.h", name = "incFreqValue")
    public void incFreqValue(boolean pos) {
        StatIndex.incFreqValue(pos, stat);
    }

    // get methods
    /**
     * general access to a stat value by index
     * 
     * @param i
     *        i
     * @return stat at position i
     */
    @PortedFrom(file = "dlVertex.h", name = "getStat")
    public int getStat(int i) {
        return stat[i];
    }

    /**
     * general access to a stat value by index
     * 
     * @param pos
     *        pos
     * @return depth of queue pos
     */
    @PortedFrom(file = "dlVertex.h", name = "getDepth")
    public int getDepth(boolean pos) {
        return StatIndex.getDepth(pos, stat);
    }

    /**
     * get access to a usage wrt POS
     * 
     * @param pos
     *        pos
     * @return usage
     */
    @PortedFrom(file = "dlVertex.h", name = "getUsage")
    public long getUsage(boolean pos) {
        return pos ? posUsage : negUsage;
    }
}
