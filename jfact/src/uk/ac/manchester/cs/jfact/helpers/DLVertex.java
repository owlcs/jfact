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

import uk.ac.manchester.cs.jfact.kernel.*;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;

public class DLVertex extends DLVertexTagDFS {
    static class ChildSet {
        private Comparator<Integer> c = new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                if (o1.equals(o2)) {
                    return 0;
                }
                if (sorter.less(o1, o2)) {
                    return -1;
                }
                return 1;
            }
        };
        FastSet set = FastSetFactory.create();
        private SortedIntList original = new SortedIntList();
        int[] sorted = null;
        protected DLDag sorter = null;

        @Override
        public boolean equals(Object arg0) {
            if (arg0 == null) {
                return false;
            }
            if (this == arg0) {
                return true;
            }
            if (arg0 instanceof ChildSet) {
                ChildSet arg = (ChildSet) arg0;
                return set.equals(arg.set);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return set.hashCode();
        }

        public void setSorter(DLDag d) {
            sorter = d;
            sorted = null;
        }

        public int[] sorted() {
            if (sorted == null) {
                sorted = new int[set.size()];
                if (sorter == null) {
                    for (int i = 0; i < set.size(); i++) {
                        // if the re is no sorting, use the original insertion
                        // order
                        sorted[i] = original.get(i);
                    }
                } else {
                    List<Integer> l = new ArrayList<Integer>();
                    for (int i = 0; i < set.size(); ++i) {
                        l.add(set.get(i));
                    }
                    Collections.sort(l, c);
                    for (int i = 0; i < sorted.length; ++i) {
                        sorted[i] = l.get(i);
                    }
                }
            }
            return sorted;
        }

        public boolean contains(int inverse) {
            return set.contains(inverse);
        }

        public void clear() {
            set.clear();
            sorted = null;
        }

        public boolean add(int p) {
            int size = set.size();
            set.add(p);
            if (set.size() > size) {
                original.add(p);
                sorted = null;
                return true;
            }
            return false;
        }
    }

    /** set of arguments (CEs, numbers for NR) */
    private ChildSet child = new ChildSet();
    /** pointer to concept-like entry (for PConcept, etc) */
    private NamedEntry concept = null;
    /** pointer to role (for E\A, NR) */
    private Role role;
    /** projection role (used for projection op only) */
    private Role projRole;
    /** C if available */
    private int conceptIndex;
    /** n if available */
    private int n;
    /** maximal depth, size and frequency of reference of the expression */
    private MergableLabel sort = new MergableLabel();
    public static boolean printExtendedStats = false;

    /** get RW access to the label */
    public MergableLabel getSort() {
        return sort;
    }

    /** merge local label to label LABEL */
    public void merge(MergableLabel label) {
        sort.merge(label);
    }

    /** c'tor for Top/CN/And (before adding any operands) */
    public DLVertex(DagTag op) {
        this(op, 0, null, bpINVALID, null);
    }

    /** c'tor for <= n R_C; and for \A R{n}_C; Note order C, n, R.pointer */
    public DLVertex(DagTag op, int m, Role R, int c, Role ProjR) {
        super(op);
        role = R;
        projRole = ProjR;
        conceptIndex = c;
        n = m;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof DLVertex) {
            DLVertex v = (DLVertex) obj;
            return op == v.op && compare(role, v.role) && compare(projRole, v.projRole)
                    && conceptIndex == v.conceptIndex && n == v.n
                    && child.equals(v.child);
        }
        return false;
    }

    private boolean compare(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    @Override
    public int hashCode() {
        return (op == null ? 0 : op.hashCode()) + (role == null ? 0 : role.hashCode())
                + (projRole == null ? 0 : projRole.hashCode()) + conceptIndex + n
                + (child == null ? 0 : child.hashCode());
    }

    /** return C for concepts/quantifiers/NR verteces */
    public int getConceptIndex() {
        return conceptIndex;
    }

    /** return N for the (<= n R) vertex */
    public int getNumberLE() {
        return n;
    }

    /** return N for the (>= n R) vertex */
    public int getNumberGE() {
        return n + 1;
    }

    /** return STATE for the (\all R{state}.C) vertex */
    public int getState() {
        return n;
    }

    /** return pointer to the first concept name of the entry */
    public int[] begin() {
        return child.sorted();
    }

    /** return pointer to Role for the Role-like verteces */
    public Role getRole() {
        return role;
    }

    /** return pointer to Projection Role for the Projection verteces */
    public Role getProjRole() {
        return projRole;
    }

    /** get (RW) TConcept for concept-like fields */
    public NamedEntry getConcept() {
        return concept;
    }

    /** set TConcept value to entry */
    public void setConcept(NamedEntry p) {
        concept = p;
    }

    /** set a concept (child) to Name-like vertex */
    public void setChild(int p) {
        conceptIndex = p;
    }

    public boolean addChild(int p) {
        if (p == bpTOP) {
            return false;
        }
        if (op == dtBad) {
            return true;
        }
        if (p == bpBOTTOM) {
            // clash:
            child.clear();
            op = dtBad;
            return true;
        }
        if (child.contains(-p)) {
            child.clear();
            op = dtBad;
            return true;
        }
        child.add(p);
        return false;
    }

    public int getAndToDagValue() {
        if (child.set.size() == 0) {
            return bpTOP;
        }
        if (child.set.size() == 1) {
            return child.set.get(0);
        }
        return bpINVALID;
    }

    public void sortEntry(DLDag dag) {
        if (op != dtAnd) {
            return;
        }
        child.setSorter(dag);
    }

    @Override
    public String toString() {
        StringBuilder o = new StringBuilder();
        if (printExtendedStats) {
            o.append("[d(");
            o.append(stat[0]);
            o.append("/");
            o.append(stat[1]);
            o.append("),s(");
            o.append(stat[2]);
            o.append("/");
            o.append(stat[3]);
            o.append("),b(");
            o.append(stat[4]);
            o.append("/");
            o.append(stat[5]);
            o.append("),g(");
            o.append(stat[6]);
            o.append("/");
            o.append(stat[7]);
            o.append("),f(");
            o.append(stat[8]);
            o.append("/");
            o.append(stat[9]);
            o.append(")] ");
        }
        o.append(op.getName());
        switch (op) {
            case dtAnd:
            case dtCollection:
            case dtSplitConcept:
                break;
            case dtTop:
            case dtNN:
                return o.toString();
            case dtDataExpr:
                o.append(" ");
                o.append(concept);
                return o.toString();
            case dtDataValue:
            case dtDataType:
            case dtPConcept:
            case dtNConcept:
            case dtPSingleton:
            case dtNSingleton:
                o.append(String.format(Templates.DLVERTEXPrint2.getTemplate(),
                        concept.getName(), op.isNNameTag() ? "=" : "[=", conceptIndex));
                return o.toString();
            case dtLE:
                o.append(" ");
                o.append(n);
                o.append(" ");
                o.append(role.getName());
                o.append(" ");
                o.append(conceptIndex);
                return o.toString();
            case dtForall:
                o.append(String.format(Templates.DLVERTEXPrint3.getTemplate(),
                        role.getName(), n, conceptIndex));
                return o.toString();
            case dtIrr:
                o.append(" ");
                o.append(role.getName());
                return o.toString();
            case dtProj:
                o.append(String.format(Templates.DLVERTEXPrint4.getTemplate(),
                        role.getName(), conceptIndex, projRole.getName()));
                return o.toString();
            case dtChoose:
                o.append(" ");
                o.append(getConceptIndex());
                return o.toString();
            default:
                throw new ReasonerInternalException(String.format(
                        "Error printing vertex of type %s(%s)", op.getName(), op));
        }
        for (int q : child.sorted()) {
            o.append(" ");
            o.append(q);
        }
        return o.toString();
    }

    /** maximal depth, size and frequency of reference of the expression */
    protected int[] stat = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    /** add-up all stat values at once by explicit values */
    public void updateStatValues(int d, int s, int b, int g, boolean pos) {
        StatIndex.updateStatValues(d, s, b, g, pos, stat);
    }

    /** add-up all values at once by a given vertex */
    public void updateStatValues(DLVertex v, boolean posV, boolean pos) {
        StatIndex.updateStatValues(v, posV, pos, stat);
    }

    /** increment frequency value */
    public void incFreqValue(boolean pos) {
        StatIndex.incFreqValue(pos, stat);
    }

    // get methods
    /** general access to a stat value by index */
    public int getStat(int i) {
        return stat[i];
    }

    /** general access to a stat value by index */
    public int getDepth(boolean pos) {
        return StatIndex.getDepth(pos, stat);
    }

    /** usage statistic for pos- and neg occurences of a vertex */
    protected long posUsage = 0;
    protected long negUsage = 0;

    /** get access to a usage wrt POS */
    public long getUsage(boolean pos) {
        return pos ? posUsage : negUsage;
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

    protected DLVertexTagDFS(DagTag op) {
        this.op = op;
    }

    // tag access
    /** return tag of the CE */
    public DagTag getType() {
        return op;
    }

    // DFS-related method
    /** check whether current Vertex is being visited */
    public boolean isVisited(boolean pos) {
        return pos ? visitedPos : visitedNeg;
    }

    /** check whether current Vertex is processed */
    public boolean isProcessed(boolean pos) {
        return pos ? processedPos : processedNeg;
    }

    /** set that the node is being visited */
    public void setVisited(boolean pos) {
        if (pos) {
            visitedPos = true;
        } else {
            visitedNeg = true;
        }
    }

    /** set that the node' DFS processing is completed */
    public void setProcessed(boolean pos) {
        if (pos) {
            processedPos = true;
            visitedPos = false;
        } else {
            processedNeg = true;
            visitedNeg = false;
        }
    }

    /** clear DFS flags */
    public void clearDFS() {
        processedPos = false;
        visitedPos = false;
        processedNeg = false;
        visitedNeg = false;
    }

    /** check whether concept is in cycle */
    public boolean isInCycle(boolean pos) {
        return pos ? inCyclePos : inCycleNeg;
    }

    /** set concept is in cycle */
    public void setInCycle(boolean pos) {
        if (pos) {
            inCyclePos = true;
        } else {
            inCycleNeg = true;
        }
    }

    /** cache for the positive entry */
    protected ModelCacheInterface pCache = null;
    /** cache for the negative entry */
    protected ModelCacheInterface nCache = null;

    /** return cache wrt positive flag */
    public ModelCacheInterface getCache(boolean pos) {
        return pos ? pCache : nCache;
    }

    /** set cache wrt positive flag; note that cache is set up only once */
    public void setCache(boolean pos, ModelCacheInterface p) {
        if (pos) {
            pCache = p;
        } else {
            nCache = p;
        }
    }
}
