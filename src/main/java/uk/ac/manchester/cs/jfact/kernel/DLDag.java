package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLRuntimeException;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.chainsaw.FastSet;
import uk.ac.manchester.cs.chainsaw.FastSetFactory;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeEntry;
import uk.ac.manchester.cs.jfact.datatypes.LiteralEntry;
import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.StatIndex;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.modelcaches.ModelCacheInterface;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/** directed acyclic graph */
@PortedFrom(file = "dlDag.h", name = "DLDag")
public class DLDag implements Serializable {

    /** body of DAG */
    @PortedFrom(file = "dlDag.h", name = "Heap") private final List<DLVertex> heap = new ArrayList<>();
    private Set<NamedEntry> cache = new HashSet<>();
    /** all the AND nodes (needs to recompute) */
    @PortedFrom(file = "dlDag.h", name = "listAnds") private final FastSet listAnds = FastSetFactory.create();
    @Original private final EnumMap<DagTag, DLVTable> indexes = new EnumMap<>(DagTag.class);
    /** cache efficiency -- statistic purposes */
    @PortedFrom(file = "dlDag.h", name = "nCacheHits") private int nCacheHits;
    /** size of sort array */
    @PortedFrom(file = "dlDag.h", name = "sortArraySize") private int sortArraySize;
    /** sort index (if necessary). Possible values are Size, Depth, Freq */
    @PortedFrom(file = "dlDag.h", name = "iSort") private int iSort;
    /** whether or not sorting order is ascending */
    @PortedFrom(file = "dlDag.h", name = "sortAscend") private boolean sortAscend;
    /** prefer non-generating rules in OR orderings */
    @PortedFrom(file = "dlDag.h", name = "preferNonGen") private boolean preferNonGen;
    /** flag whether cache should be used */
    @PortedFrom(file = "dlDag.h", name = "useDLVCache") private boolean useDLVCache;
    @PortedFrom(file = "dlDag.h", name = "finalDagSize") private int finalDagSize;
    @Original private final JFactReasonerConfiguration options;

    /**
     * @param options
     *        Options
     */
    public DLDag(JFactReasonerConfiguration options) {
        this.options = options;
        /** hash-table for verteces (and, all, LE) fast search */
        DLVTable indexAnd = new DLVTable(this);
        DLVTable indexAll = new DLVTable(this);
        DLVTable indexLE = new DLVTable(this);
        indexes.put(DagTag.COLLECTION, indexAnd);
        indexes.put(DagTag.AND, indexAnd);
        indexes.put(DagTag.IRR, indexAll);
        indexes.put(DagTag.FORALL, indexAll);
        indexes.put(DagTag.LE, indexLE);
        nCacheHits = 0;
        useDLVCache = true;
        finalDagSize = 0;
        heap.add(new DLVertex(DagTag.BAD, this));
        heap.add(new DLVertex(DagTag.TOP, this));
        if (!isCorrectOption(options.getORSortSat()) || !isCorrectOption(options.getORSortSub())) {
            throw new OWLRuntimeException("DAG: wrong OR sorting options");
        }
    }

    /**
     * replace existing vertex at index I with a vertex V
     * 
     * @param i
     *        i
     * @param v
     *        v
     * @param c
     *        C
     */
    @PortedFrom(file = "dlDag.h", name = "replaceVertex")
    public void replaceVertex(int i, DLVertex v, NamedEntry c) {
        heap.set(i > 0 ? i : -i, v);
        v.setConcept(c);
        cache.clear();
    }

    /**
     * @param c
     *        c
     * @return index of a vertex containing a concept
     */
    @PortedFrom(file = "dlDag.h", name = "index")
    public int index(NamedEntry c) {
        for (int i = 0; i < heap.size(); i++) {
            NamedEntry concept = heap.get(i).getConcept();
            if (concept != null && concept.equals(c)) {
                return i;
            }
        }
        return BP_INVALID;
    }

    /**
     * check if given string is correct sort ordering representation
     * 
     * @param str
     *        str
     * @return true if correct
     */
    @PortedFrom(file = "dlDag.h", name = "isCorrectOption")
    private static boolean isCorrectOption(@Nullable String str) {
        if (str == null) {
            return false;
        }
        int n = str.length();
        if (n < 1 || n > 3) {
            return false;
        }
        char method = str.charAt(0), order = n >= 2 ? str.charAt(1) : 'a', ngPref = n == 3 ? str.charAt(2) : 'p';
        return (method == 'S' || method == 'D' || method == 'F' || method == 'B' || method == 'G' || method == '0')
            && (order == 'a' || order == 'd') && (ngPref == 'p' || ngPref == 'n');
    }

    /** change order of ADD elements wrt statistic */
    @PortedFrom(file = "dlDag.h", name = "Recompute")
    private void recompute() {
        for (int p = 0; p < listAnds.size(); p++) {
            heap.get(listAnds.get(p)).sortEntry(this);
        }
    }

    /** clear all DFS info from elements of DAG */
    @PortedFrom(file = "dlDag.h", name = "clearDFS")
    private void clearDFS() {
        heap.forEach(d -> d.clearDFS());
    }

    /**
     * update index corresponding to DLVertex's tag
     * 
     * @param tag
     *        tag
     * @param value
     *        value
     */
    @PortedFrom(file = "dlDag.h", name = "updateIndex")
    public void updateIndex(DagTag tag, int value) {
        if (!indexes.containsKey(tag)) {
            return;
        }
        indexes.get(tag).addElement(value);
        if (tag == DagTag.COLLECTION || tag == DagTag.AND) {
            listAnds.add(value);
        }
    }

    /**
     * add vertex to the end of DAG and calculate it's statistic if necessary
     * 
     * @param v
     *        v
     * @param knownNew
     *        true if new object is known to be required
     * @return size of heap
     */
    @PortedFrom(file = "dlDag.h", name = "directAdd")
    public int directAdd(DLVertex v, boolean knownNew) {
        if (knownNew) {
            heap.add(v);
            cache.add(v.getConcept());
            // return an index of just added entry
            return heap.size() - 1;
        }
        if (cache.isEmpty()) {
            for (DLVertex v1 : heap) {
                cache.add(v1.getConcept());
            }
        }
        if (cache.contains(v.getConcept())) {
            int index = index(v.getConcept());
            if (index != BP_INVALID) {
                return index;
            }
        }
        heap.add(v);
        cache.add(v.getConcept());
        // return an index of just added entry
        return heap.size() - 1;
    }

    /**
     * add vertex to the end of DAG and calculate it's statistic if necessary;
     * put it into cache
     * 
     * @param v
     *        v
     * @return size of heap
     */
    @PortedFrom(file = "dlDag.h", name = "directAddAndCache")
    public int directAddAndCache(DLVertex v) {
        int ret = directAdd(v, false);
        if (useDLVCache) {
            updateIndex(v.getType(), ret);
        }
        return ret;
    }

    /**
     * @param p
     *        p
     * @return if given index points to the last DAG entry
     */
    @PortedFrom(file = "dlDag.h", name = "isLast")
    public boolean isLast(int p) {
        int last = heap.size() - 1;
        return p == last || -p == last;
    }

    // access methods
    /**
     * whether to use cache for nodes
     * 
     * @param val
     *        val
     */
    @PortedFrom(file = "dlDag.h", name = "setExpressionCache")
    public void setExpressionCache(boolean val) {
        useDLVCache = val;
    }

    /**
     * @param i
     *        i
     * @return access by index
     */
    @PortedFrom(file = "dlDag.h", name = "get")
    public DLVertex get(int i) {
        assert isValid(i);
        return heap.get(i < 0 ? -i : i);
    }

    /**
     * @return get size of DAG
     */
    @PortedFrom(file = "dlDag.h", name = "size")
    public int size() {
        return heap.size();
    }

    /**
     * @return approximation of the size after query is added
     */
    @PortedFrom(file = "dlDag.h", name = "maxSize")
    public int maxSize() {
        return size() + (size() < 220 ? 10 : size() / 20);
    }

    /** use SUB options to OR ordering */
    @PortedFrom(file = "dlDag.h", name = "setSubOrder")
    public void setSubOrder() {
        setOrderOptions(options.getORSortSub());
    }

    /** use SAT options to OR ordering; */
    @PortedFrom(file = "dlDag.h", name = "setSatOrder")
    public void setSatOrder() {
        setOrderOptions(options.getORSortSat());
    }

    /**
     * @param p
     *        p
     * @return cache for given BiPointer (may return null if no cache defined)
     */
    @Nullable
    @PortedFrom(file = "dlDag.h", name = "getCache")
    public ModelCacheInterface getCache(int p) {
        return get(p).getCache(p > 0);
    }

    /**
     * set cache for given BiPointer;
     * 
     * @param p
     *        p
     * @param cache
     *        cache
     */
    @PortedFrom(file = "dlDag.h", name = "setCache")
    public void setCache(int p, ModelCacheInterface cache) {
        get(p).setCache(p > 0, cache);
    }

    // sort interface
    /**
     * merge two given DAG entries
     * 
     * @param ml
     *        ml
     * @param p
     *        p
     */
    @PortedFrom(file = "dlDag.h", name = "merge")
    public void merge(MergableLabel ml, int p) {
        if (p != BP_INVALID && p != BP_TOP && p != BP_BOTTOM) {
            get(p).merge(ml);
        }
    }

    /**
     * @param p
     *        p
     * @param q
     *        q
     * @return check if two BPs are of the same sort
     */
    @PortedFrom(file = "dlDag.h", name = "haveSameSort")
    public boolean haveSameSort(int p, int q) {
        if (options.isUseSortedReasoning()) {
            assert p > 0 && q > 0;
            // everything has the same label as TOP
            if (p == 1 || q == 1) {
                return true;
            }
            // if some concepts were added to DAG => nothing to say
            if (p >= sortArraySize || q >= sortArraySize) {
                return true;
            }
            // check whether two sorts are identical
            return get(p).getSort().equals(get(q).getSort());
        } else {
            return true;
        }
    }

    // output interface
    /**
     * print DAG size and number of cache hits, together with DAG usage
     * 
     * @param o
     *        o
     */
    @PortedFrom(file = "dlDag.h", name = "PrintStat")
    public void printStat(LogAdapter o) {
        o.printTemplateInt(Templates.PRINT_STAT, heap.size(), nCacheHits);
        if (options.isPrintDagUsage()) {
            printDAGUsage(o);
        }
    }

    @Override
    public String toString() {
        StringBuilder o = new StringBuilder("\nDag structure");
        for (int i = 1; i < size(); ++i) {
            o.append('\n').append(i).append(' ').append(get(i));
        }
        o.append('\n');
        return o.toString();
    }

    /**
     * @param v
     *        v
     * @return bipolar pointer
     */
    @PortedFrom(file = "dlDag.h", name = "add")
    public int add(DLVertex v) {
        int ret = useDLVCache ? indexes.get(v.getType()).locate(v) : BP_INVALID;
        if (!isValid(ret)) {
            // we fail to find such vertex -- it's new
            ret = directAddAndCache(v);
            return ret;
        }
        // node was found in cache
        ++nCacheHits;
        return ret;
    }

    /** set the DAG size */
    @PortedFrom(file = "dlDag.h", name = "setFinalSize")
    public void setFinalSize() {
        finalDagSize = size();
        setExpressionCache(false);
    }

    /** clean query */
    @PortedFrom(file = "dlDag.h", name = "removeQuery")
    @SuppressWarnings("incomplete-switch")
    public void removeQuery() {
        for (int i = size() - 1; i >= finalDagSize; --i) {
            DLVertex v = heap.get(i);
            switch (v.getType()) {
                case DATATYPE:
                case DATAEXPR:
                    ((DatatypeEntry) v.getConcept()).setIndex(BP_INVALID);
                    break;
                case DATAVALUE:
                    ((LiteralEntry) v.getConcept()).setIndex(BP_INVALID);
                    break;
                case PCONCEPT:
                case NCONCEPT:
                    ((Concept) v.getConcept()).clear();
                    break;
                default:
                    break;
            }
        }
        resize(heap, finalDagSize, null);
        cache.clear();
    }

    /**
     * @param defSat
     *        defSat
     * @param defSub
     *        defSub
     */
    @PortedFrom(file = "dlDag.h", name = "setOrderDefaults")
    public void setOrderDefaults(String defSat, String defSub) {
        assert isCorrectOption(defSat) && isCorrectOption(defSub);
        options.getLog().print("orSortSat: initial=", options.getORSortSat(), ", default=", defSat);
        if (options.getORSortSat().charAt(0) == '0') {
            options.setorSortSat(defSat);
        }
        options.getLog().print(", used=", options.getORSortSat(), "\n");
        options.getLog().print("orSortSub: initial=", options.getORSortSub(), ", default=", defSub);
        if (options.getORSortSub().charAt(0) == '0') {
            options.setorSortSub(defSub);
        }
        options.getLog().print(", used=", options.getORSortSub(), "\n");
    }

    /**
     * @param opt
     *        opt
     */
    @PortedFrom(file = "dlDag.h", name = "setOrderOptions")
    public void setOrderOptions(String opt) {
        if (opt.charAt(0) == '0') {
            return;
        }
        sortAscend = opt.charAt(1) == 'a';
        preferNonGen = opt.charAt(2) == 'p';
        iSort = StatIndex.choose(opt.charAt(0));
        recompute();
    }

    @PortedFrom(file = "dlDag.h", name = "computeVertexStat")
    @SuppressWarnings("incomplete-switch")
    private void computeVertexStat(DLVertex v, boolean pos, int depth) {
        // in case of cycle: mark concept as such
        if (v.isVisited(pos)) {
            v.setInCycle(pos);
            return;
        }
        v.setVisited(pos);
        // ensure that the statistic is gather for all sub-concepts of the
        // expression
        switch (v.getType()) {
            case COLLECTION:// if pos then behaves like and
                if (!pos) {
                    break;
                }
                // fallthrough
                //$FALL-THROUGH$
            case AND:// check all the conjuncts
                for (int q : v.begin()) {
                    int index = createBiPointer(q, pos);
                    DLVertex vertex = get(index);
                    boolean pos2 = index > 0;
                    if (!vertex.isProcessed(pos2)) {
                        computeVertexStat(vertex, pos2, depth + 1);
                    }
                }
                break;
            case PROJ:
                if (!pos) {
                    break;
                }
                // fallthrough
                //$FALL-THROUGH$
            case PCONCEPT:
            case NCONCEPT:
            case PSINGLETON:
            case NSINGLETON:
            case FORALL:
            case CHOOSE:
            case LE:// check a single referenced concept
                int index = createBiPointer(v.getConceptIndex(), pos);
                DLVertex vertex = get(index);
                boolean pos2 = index > 0;
                if (!vertex.isProcessed(pos2)) {
                    computeVertexStat(vertex, pos2, depth + 1);
                }
                break;
            default:// nothing to do
                break;
        }
        v.setProcessed(pos);
        // here all the necessary statistics is gathered -- use it in the init
        updateVertexStat(v, pos);
    }

    @PortedFrom(file = "dlDag.h", name = "updateVertexStat")
    @SuppressWarnings("incomplete-switch")
    private void updateVertexStat(DLVertex v, boolean pos) {
        int s = 1, b = 0, g = 0;
        if (!v.getType().omitStat(pos)) {
            if (isValid(v.getConceptIndex())) {
                updateVertexStat(v, v.getConceptIndex(), pos);
            } else {
                for (int q : v.begin()) {
                    updateVertexStat(v, q, pos);
                }
            }
        }
        // correct values wrt POS
        int d = v.getDepth(pos);
        switch (v.getType()) {
            case AND:
                if (!pos) {
                    ++b;
                    // OR is branching
                }
                break;
            case FORALL:
                ++d;
                // increase depth
                if (!pos) {
                    ++g;
                    // SOME is generating
                }
                break;
            case LE:
                ++d;
                // increase depth
                if (!pos) {
                    ++g;
                    // >= is generating
                } else if (v.getNumberLE() != 1) {
                    ++b;
                    // <= is branching
                }
                break;
            case PROJ:
                if (pos) {
                    ++b;
                    // projection sometimes involves branching
                }
                break;
            default:
                break;
        }
        v.updateStatValues(d, s, b, g, pos);
    }

    /**
     * gather vertex freq statistics
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "dlDag.h", name = "computeVertexFreq")
    private void computeVertexFreq(int p) {
        DLVertex v = get(p);
        boolean pos = p > 0;
        if (v.isVisited(pos)) {
            // avoid cycles
            return;
        }
        // increment frequence of current vertex
        v.incFreqValue(pos);
        v.setVisited(pos);
        if (v.getType().omitStat(pos)) {
            // negation of primitive concept-like
            return;
        }
        // increment frequence of all subvertex
        if (isValid(v.getConceptIndex())) {
            computeVertexFreq(v.getConceptIndex(), pos);
        } else {
            for (int q : v.begin()) {
                computeVertexFreq(q, pos);
            }
        }
    }

    /**
     * helper for the recursion
     * 
     * @param v
     *        v
     * @param p
     *        p
     * @param pos
     *        pos
     */
    @PortedFrom(file = "dlDag.h", name = "updateVertexStat")
    private void updateVertexStat(DLVertex v, int p, boolean pos) {
        DLVertex w = get(p);
        boolean same = pos == p > 0;
        // update in-cycle information
        if (w.isInCycle(same)) {
            v.setInCycle(pos);
        }
        v.updateStatValues(w, same, pos);
    }

    /**
     * helper for the recursion
     * 
     * @param p
     *        p
     * @param pos
     *        pos
     */
    @PortedFrom(file = "dlDag.h", name = "computeVertexFreq")
    private void computeVertexFreq(int p, boolean pos) {
        computeVertexFreq(createBiPointer(p, pos));
    }

    /** stats collection */
    @PortedFrom(file = "dlDag.h", name = "gatherStatistic")
    public void gatherStatistic() {
        // gather main statistics for disjunctions
        for (int i = 0; i < listAnds.size(); i++) {
            int index = -listAnds.get(i);
            DLVertex v = get(index);
            boolean pos = index > 0;
            if (!v.isProcessed(pos)) {
                computeVertexStat(v, pos, 0);
            }
        }
        // if necessary -- gather frequency
        if (options.getORSortSat().charAt(0) != 'F' && options.getORSortSub().charAt(0) != 'F') {
            return;
        }
        clearDFS();
        for (int i = size() - 1; i > 1; --i) {
            if (get(i).getType().isCNameTag()) {
                computeVertexFreq(i);
            }
        }
    }

    /**
     * @param p1
     *        p1
     * @param p2
     *        p2
     * @return true if p1 dlvertex is smaller than p2 dlvertex
     */
    @PortedFrom(file = "dlDag.h", name = "less")
    public int compare(int p1, int p2) {
        if (p1 == p2) {
            return 0;
        }
        if (preferNonGen) {
            if (p1 < 0 && p2 > 0) {
                return -1;
            }
            if (p1 > 0 && p2 < 0) {
                return 1;
            }
        }
        DLVertex v1 = get(p1);
        DLVertex v2 = get(p2);
        int key1 = v1.getStat(iSort);
        int key2 = v2.getStat(iSort);
        if (key1 == key2) {
            return 0;
        }
        if (sortAscend) {
            return key1 - key2;
        } else {
            return key2 - key1;
        }
    }

    /**
     * @param o
     *        debug dag usage
     */
    @PortedFrom(file = "dlDag.h", name = "PrintDAGUsage")
    public void printDAGUsage(LogAdapter o) {
        // number of no-used DAG entries
        int n = 0;
        // number of total DAG entries
        int total = heap.size() * 2 - 2;
        for (DLVertex i : heap) {
            if (i.getUsage(true) == 0) {
                ++n;
            }
            if (i.getUsage(false) == 0) {
                ++n;
            }
        }
        o.printTemplateInt(Templates.PRINTDAGUSAGE, n, n * 100 / total, total);
    }

    /**
     * build the sort system for given TBox
     * 
     * @param orm
     *        ORM
     * @param drm
     *        DRM
     */
    @PortedFrom(file = "dlDag.h", name = "determineSorts")
    public void determineSorts(RoleMaster orm, RoleMaster drm) {
        sortArraySize = heap.size();
        // init roles R&D sorts
        List<Role> ormBegin = orm.getRoles();
        ormBegin.stream().filter(p -> !p.isSynonym()).forEach(this::mergeSorts);
        List<Role> drmBegin = drm.getRoles();
        drmBegin.stream().filter(p -> !p.isSynonym()).forEach(this::mergeSorts);
        heap.stream().skip(2).forEach(this::mergeSorts);
        AtomicInteger sum = new AtomicInteger();
        heap.stream().skip(2).forEach(p -> {
            MergableLabel lab = p.getSort();
            lab.resolve();
            if (lab.isSample()) {
                sum.incrementAndGet();
            }
        });
        ormBegin.stream().filter(p -> !p.isSynonym()).forEach(p -> {
            MergableLabel lab = p.getDomainLabel();
            lab.resolve();
            if (lab.isSample()) {
                sum.incrementAndGet();
            }
        });
        drmBegin.stream().filter(p -> !p.isSynonym()).forEach(p -> {
            MergableLabel lab = p.getDomainLabel();
            lab.resolve();
            if (lab.isSample()) {
                sum.incrementAndGet();
            }
        });
        // we added a temp concept here; don't count it
        if (sum.get() > 0) {
            sum.decrementAndGet();
        }
        options.getLog().printTemplate(Templates.DETERMINE_SORTS, sum.get() > 0 ? sum : "no");
    }

    /**
     * merge sorts for a given role
     * 
     * @param r
     *        R
     */
    @PortedFrom(file = "dlDag.h", name = "mergeSorts")
    private void mergeSorts(Role r) {
        // associate role domain labels
        r.mergeSupersDomain();
        merge(r.getDomainLabel(), r.getBPDomain());
        // also associate functional nodes (if any)
        r.beginTopfunc().forEach(q -> merge(r.getDomainLabel(), q.getFunctional()));
    }

    /**
     * merge sorts for a given vertex
     * 
     * @param v
     *        v
     */
    @PortedFrom(file = "dlDag.h", name = "mergeSorts")
    @SuppressWarnings("incomplete-switch")
    private void mergeSorts(DLVertex v) {
        switch (v.getType()) {
            case LE:// set R&D for role
            case FORALL:
                v.merge(v.getRole().getDomainLabel());// domain(role)=cur
                merge(v.getRole().getRangeLabel(), v.getConceptIndex());
                break;
            case PROJ:// projection: equate R&D of R and ProjR, and D(R) with
                      // C
                v.merge(v.getRole().getDomainLabel());
                v.merge(v.getProjRole().getDomainLabel());
                merge(v.getRole().getDomainLabel(), v.getConceptIndex());
                v.getRole().getRangeLabel().merge(v.getProjRole().getRangeLabel());
                break;
            case IRR:// equate R&D for role
                v.merge(v.getRole().getDomainLabel());
                v.merge(v.getRole().getRangeLabel());
                break;
            case AND:
            case COLLECTION:
                for (int q : v.begin()) {
                    merge(v.getSort(), q);
                }
                break;
            case NSINGLETON:
            case PSINGLETON:
            case PCONCEPT:
            case NCONCEPT:// merge with description
            case CHOOSE:
                merge(v.getSort(), v.getConceptIndex());
                break;
            case DATATYPE:// nothing to do
            case DATAVALUE:
            case DATAEXPR:
            case NN:
                break;
            case TOP:
            default:
                throw new UnreachableSituationException();
        }
    }

    /**
     * update sorts for (a,b):R construction
     * 
     * @param a
     *        a
     * @param d
     *        R
     * @param b
     *        b
     */
    @PortedFrom(file = "dlDag.h", name = "updateSorts")
    public void updateSorts(int a, Role d, int b) {
        merge(d.getDomainLabel(), a);
        merge(d.getRangeLabel(), b);
    }
}
