package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;
import static uk.ac.manchester.cs.jfact.kernel.DagTag.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.kernel.DLDag;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.MergableLabel;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.Role;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;

public final class DLVertex extends DLVertexTagDFS {
    static class ChildSet {
        private final Comparator<Integer> c = new Comparator<Integer>() {
            public int compare(final Integer o1, final Integer o2) {
                if (o1.equals(o2)) {
                    return 0;
                }
                if (ChildSet.this.sorter.less(o1, o2)) {
                    return -1;
                }
                return 1;
            }
        };
        final FastSet set = FastSetFactory.create();
        private final SortedIntList original = new SortedIntList();
        int[] sorted = null;
        protected DLDag sorter = null;

        @Override
        public boolean equals(final Object arg0) {
            if (arg0 == null) {
                return false;
            }
            if (this == arg0) {
                return true;
            }
            if (arg0 instanceof ChildSet) {
                ChildSet arg = (ChildSet) arg0;
                return this.set.equals(arg.set);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.set.hashCode();
        }

        public void setSorter(final DLDag d) {
            this.sorter = d;
            this.sorted = null;
        }

        public int[] sorted() {
            if (this.sorted == null) {
                this.sorted = new int[this.set.size()];
                if (this.sorter == null) {
                    for (int i = 0; i < this.set.size(); i++) {
                        // if the re is no sorting, use the original insertion order
                        this.sorted[i] = this.original.get(i);
                    }
                } else {
                    List<Integer> l = new ArrayList<Integer>();
                    for (int i = 0; i < this.set.size(); ++i) {
                        l.add(this.set.get(i));
                    }
                    Collections.sort(l, this.c);
                    for (int i = 0; i < this.sorted.length; ++i) {
                        this.sorted[i] = l.get(i);
                    }
                }
            }
            return this.sorted;
        }

        public boolean contains(final int inverse) {
            return this.set.contains(inverse);
        }

        public void clear() {
            this.set.clear();
            this.sorted = null;
        }

        public boolean add(final int p) {
            int size = this.set.size();
            this.set.add(p);
            if (this.set.size() > size) {
                this.original.add(p);
                this.sorted = null;
                return true;
            }
            return false;
        }
    }

    /** set of arguments (CEs, numbers for NR) */
    private final ChildSet child = new ChildSet();
    /** pointer to concept-like entry (for PConcept, etc) */
    private NamedEntry concept = null;
    /** pointer to role (for E\A, NR) */
    private final Role role;
    /** projection role (used for projection op only) */
    private final Role projRole;
    /** C if available */
    private int conceptIndex;
    /** n if available */
    private final int n;
    /** maximal depth, size and frequency of reference of the expression */
    private final MergableLabel sort = new MergableLabel();
    public static final boolean printExtendedStats = false;

    /** get RW access to the label */
    public MergableLabel getSort() {
        return this.sort;
    }

    /** merge local label to label LABEL */
    public void merge(final MergableLabel label) {
        this.sort.merge(label);
    }

    /** c'tor for Top/CN/And (before adding any operands) */
    public DLVertex(final DagTag op) {
        this(op, 0, null, bpINVALID, null);
    }

    /** c'tor for <= n R_C; and for \A R{n}_C; Note order C, n, R.pointer */
    public DLVertex(final DagTag op, final int m, final Role R, final int c,
            final Role ProjR) {
        super(op);
        this.role = R;
        this.projRole = ProjR;
        this.conceptIndex = c;
        this.n = m;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof DLVertex) {
            DLVertex v = (DLVertex) obj;
            return this.op == v.op && this.compare(this.role, v.role)
                    && this.compare(this.projRole, v.projRole)
                    && this.conceptIndex == v.conceptIndex && this.n == v.n
                    && this.child.equals(v.child);
        }
        return false;
    }

    private boolean compare(final Object o1, final Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    @Override
    public int hashCode() {
        return (this.op == null ? 0 : this.op.hashCode())
                + (this.role == null ? 0 : this.role.hashCode())
                + (this.projRole == null ? 0 : this.projRole.hashCode())
                + this.conceptIndex + this.n
                + (this.child == null ? 0 : this.child.hashCode());
    }

    /** return C for concepts/quantifiers/NR verteces */
    public int getConceptIndex() {
        return this.conceptIndex;
    }

    /** return N for the (<= n R) vertex */
    public int getNumberLE() {
        return this.n;
    }

    /** return N for the (>= n R) vertex */
    public int getNumberGE() {
        return this.n + 1;
    }

    /** return STATE for the (\all R{state}.C) vertex */
    public int getState() {
        return this.n;
    }

    /** return pointer to the first concept name of the entry */
    public int[] begin() {
        return this.child.sorted();
    }

    /** return pointer to Role for the Role-like verteces */
    public Role getRole() {
        return this.role;
    }

    /** return pointer to Projection Role for the Projection verteces */
    public Role getProjRole() {
        return this.projRole;
    }

    /** get (RW) TConcept for concept-like fields */
    public NamedEntry getConcept() {
        return this.concept;
    }

    /** set TConcept value to entry */
    public void setConcept(final NamedEntry p) {
        this.concept = p;
    }

    /** set a concept (child) to Name-like vertex */
    public void setChild(final int p) {
        this.conceptIndex = p;
    }

    public boolean addChild(final int p) {
        if (p == bpTOP) {
            return false;
        }
        if (this.op == dtBad) {
            return true;
        }
        if (p == bpBOTTOM) {
            //clash:
            this.child.clear();
            this.op = dtBad;
            return true;
        }
        if (this.child.contains(-p)) {
            this.child.clear();
            this.op = dtBad;
            return true;
        }
        this.child.add(p);
        return false;
    }

    public int getAndToDagValue() {
        if (this.child.set.size() == 0) {
            return bpTOP;
        }
        if (this.child.set.size() == 1) {
            return this.child.set.get(0);
        }
        return bpINVALID;
    }

    public void sortEntry(final DLDag dag) {
        if (this.op != dtAnd) {
            return;
        }
        this.child.setSorter(dag);
    }

    @Override
    public String toString() {
        StringBuilder o = new StringBuilder();
        if (printExtendedStats) {
            o.append("[d(");
            o.append(this.stat[0]);
            o.append("/");
            o.append(this.stat[1]);
            o.append("),s(");
            o.append(this.stat[2]);
            o.append("/");
            o.append(this.stat[3]);
            o.append("),b(");
            o.append(this.stat[4]);
            o.append("/");
            o.append(this.stat[5]);
            o.append("),g(");
            o.append(this.stat[6]);
            o.append("/");
            o.append(this.stat[7]);
            o.append("),f(");
            o.append(this.stat[8]);
            o.append("/");
            o.append(this.stat[9]);
            o.append(")] ");
        }
        o.append(this.op.getName());
        switch (this.op) {
            case dtAnd:
            case dtCollection:
            case dtSplitConcept:
                break;
            case dtTop:
            case dtNN:
                return o.toString();
            case dtDataExpr:
                o.append(" ");
                o.append(this.concept);
                return o.toString();
            case dtDataValue:
            case dtDataType:
            case dtPConcept:
            case dtNConcept:
            case dtPSingleton:
            case dtNSingleton:
                o.append(String.format(Templates.DLVERTEXPrint2.getTemplate(),
                        this.concept.getName(), this.op.isNNameTag() ? "=" : "[=",
                        this.conceptIndex));
                return o.toString();
            case dtLE:
                o.append(" ");
                o.append(this.n);
                o.append(" ");
                o.append(this.role.getName());
                o.append(" ");
                o.append(this.conceptIndex);
                return o.toString();
            case dtForall:
                o.append(String.format(Templates.DLVERTEXPrint3.getTemplate(),
                        this.role.getName(), this.n, this.conceptIndex));
                return o.toString();
            case dtIrr:
                o.append(" ");
                o.append(this.role.getName());
                return o.toString();
            case dtProj:
                o.append(String.format(Templates.DLVERTEXPrint4.getTemplate(),
                        this.role.getName(), this.conceptIndex, this.projRole.getName()));
                return o.toString();
            case dtChoose:
                o.append(" ");
                o.append(this.getConceptIndex());
                return o.toString();
            default:
                throw new ReasonerInternalException(String.format(
                        "Error printing vertex of type %s(%s)", this.op.getName(),
                        this.op));
        }
        for (int q : this.child.sorted()) {
            o.append(" ");
            o.append(q);
        }
        return o.toString();
    }

    /** maximal depth, size and frequency of reference of the expression */
    protected final int[] stat = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    /** add-up all stat values at once by explicit values */
    public void updateStatValues(final int d, final int s, final int b, final int g,
            final boolean pos) {
        StatIndex.updateStatValues(d, s, b, g, pos, this.stat);
    }

    /** add-up all values at once by a given vertex */
    public void updateStatValues(final DLVertex v, final boolean posV, final boolean pos) {
        StatIndex.updateStatValues(v, posV, pos, this.stat);
    }

    /** increment frequency value */
    public void incFreqValue(final boolean pos) {
        StatIndex.incFreqValue(pos, this.stat);
    }

    // get methods
    /** general access to a stat value by index */
    public int getStat(final int i) {
        return this.stat[i];
    }

    /** general access to a stat value by index */
    public int getDepth(final boolean pos) {
        return StatIndex.getDepth(pos, this.stat);
    }

    /** usage statistic for pos- and neg occurences of a vertex */
    protected long posUsage = 0;
    protected long negUsage = 0;

    /** get access to a usage wrt POS */
    public long getUsage(final boolean pos) {
        return pos ? this.posUsage : this.negUsage;
    }
}

class DLVertexTagDFS {
    protected DagTag op; // 17 types
    /** aux field for DFS in presence of cycles */
    protected boolean visitedPos = false;
    /** aux field for DFS in presence of cycles */
    protected boolean processedPos = false;
    /** true iff node is involved in cycle */
    protected boolean inCyclePos = false;
    /** aux field for DFS in presence of cycles */
    protected boolean visitedNeg = false;
    /** aux field for DFS in presence of cycles */
    protected boolean processedNeg = false;
    /** true iff node is involved in cycle */
    protected boolean inCycleNeg = false;

    protected DLVertexTagDFS(final DagTag op) {
        this.op = op;
    }

    // tag access
    /** return tag of the CE */
    public DagTag getType() {
        return this.op;
    }

    // DFS-related method
    /** check whether current Vertex is being visited */
    public boolean isVisited(final boolean pos) {
        return pos ? this.visitedPos : this.visitedNeg;
    }

    /** check whether current Vertex is processed */
    public boolean isProcessed(final boolean pos) {
        return pos ? this.processedPos : this.processedNeg;
    }

    /** set that the node is being visited */
    public void setVisited(final boolean pos) {
        if (pos) {
            this.visitedPos = true;
        } else {
            this.visitedNeg = true;
        }
    }

    /** set that the node' DFS processing is completed */
    public void setProcessed(final boolean pos) {
        if (pos) {
            this.processedPos = true;
            this.visitedPos = false;
        } else {
            this.processedNeg = true;
            this.visitedNeg = false;
        }
    }

    /** clear DFS flags */
    public void clearDFS() {
        this.processedPos = false;
        this.visitedPos = false;
        this.processedNeg = false;
        this.visitedNeg = false;
    }

    /** check whether concept is in cycle */
    public boolean isInCycle(final boolean pos) {
        return pos ? this.inCyclePos : this.inCycleNeg;
    }

    /** set concept is in cycle */
    public void setInCycle(final boolean pos) {
        if (pos) {
            this.inCyclePos = true;
        } else {
            this.inCycleNeg = true;
        }
    }

    /** cache for the positive entry */
    protected ModelCacheInterface pCache = null;
    /** cache for the negative entry */
    protected ModelCacheInterface nCache = null;

    /** return cache wrt positive flag */
    public ModelCacheInterface getCache(final boolean pos) {
        return pos ? this.pCache : this.nCache;
    }

    /** set cache wrt positive flag; note that cache is set up only once */
    public void setCache(final boolean pos, final ModelCacheInterface p) {
        if (pos) {
            this.pCache = p;
        } else {
            this.nCache = p;
        }
    }
}
