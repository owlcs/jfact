package uk.ac.manchester.cs.jfact.kernel;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.roaringbitmap.RoaringBitmap;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.ArrayIntMap;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/** List of concepts with dependencies */
@PortedFrom(file = "CWDArray.h", name = "CWDArray")
public class CWDArray implements Serializable {

    /** array of concepts together with dep-sets */
    @PortedFrom(file = "CWDArray.h", name = "Base") private final List<ConceptWDep> base;
    @Original @Nonnull private RoaringBitmap cache = new RoaringBitmap();
    @Original private final ArrayIntMap indexes = new ArrayIntMap();
    @Original private int size = 0;
    @Original private JFactReasonerConfiguration options;

    /**
     * @param config
     *        configuration
     * @param size
     *        initial size
     */
    public CWDArray(JFactReasonerConfiguration config, int size) {
        options = config;
        base = new ArrayList<>(size);
    }

    /** init/clear label */
    @PortedFrom(file = "CWDArray.h", name = "init")
    public void init() {
        base.clear();
        cache.clear();
        indexes.clear();
        size = 0;
    }

    /**
     * @return list of concepts
     */
    @PortedFrom(file = "CWDArray.h", name = "begin")
    public List<ConceptWDep> getBase() {
        return base;
    }

    /**
     * @return contained concept map
     */
    @Original
    public ArrayIntMap getContainedConcepts() {
        return indexes;
    }

    /**
     * adds concept P to a label - to be called only from CGLabel
     * 
     * @param p
     *        p
     */
    @Original
    protected void privateAdd(ConceptWDep p) {
        base.add(p);
        size++;
        cache.add(asPositive(p.getConcept()));
        indexes.put(p.getConcept(), size - 1);
    }

    /**
     * @param bp
     *        bp
     * @return true if label contains BP (ignoring dep-set)
     */
    @PortedFrom(file = "CWDArray.h", name = "contains")
    public boolean contains(int bp) {
        return cache.contains(asPositive(bp));
    }

    @Original
    private static int asPositive(int p) {
        return p >= 0 ? 2 * p : 1 - 2 * p;
    }

    /**
     * @param bp
     *        bp
     * @return index of given bp
     */
    @PortedFrom(file = "CWDArray.h", name = "index")
    public int index(int bp) {
        // check that the index actually exist: quicker
        if (!cache.contains(asPositive(bp))) {
            return -1;
        }
        return indexes.get(bp);
    }

    /**
     * @param bp
     *        bp
     * @return depset for given bp
     */
    @Nullable
    @PortedFrom(file = "CWDArray.h", name = "get")
    public DepSet get(int bp) {
        ConceptWDep cwd = getConceptWithBP(bp);
        if (cwd == null) {
            return null;
        }
        return cwd.getDep();
    }

    /**
     * @param bp
     *        bp
     * @return concept with given bp
     */
    @Nullable
    @Original
    public ConceptWDep getConceptWithBP(int bp) {
        // check that the index actually exist: quicker
        if (!cache.contains(asPositive(bp))) {
            return null;
        }
        int i = indexes.get(bp);
        if (i < 0) {
            return null;
        }
        return base.get(i);
    }

    /**
     * @return size of list
     */
    @PortedFrom(file = "CWDArray.h", name = "size")
    public int size() {
        return size;
    }

    /**
     * @param label
     *        label
     * @return true if this list is lesser or equal label
     */
    @PortedFrom(file = "CWDArray.h", name = "<=")
    public boolean lesserequal(CWDArray label) {
        // use the cache on the label if there is one
        for (int i : cache) {
            if (!label.cache.contains(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return indexes.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof CWDArray) {
            CWDArray obj2 = (CWDArray) obj;
            return indexes.equals(obj2.indexes);
        }
        return false;
    }

    /**
     * save label using given SS
     * 
     * @return save level
     */
    @PortedFrom(file = "CWDArray.h", name = "save")
    public int save() {
        return size;
    }

    /**
     * @param index
     *        index
     * @param dep
     *        dep
     * @return restorer for saved dep set
     */
    @PortedFrom(file = "CWDArray.h", name = "updateDepSet")
    public Restorer updateDepSet(int index, DepSet dep) {
        if (dep.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Restorer ret = new UnMerge(this, base.get(index), index);
        base.get(index).addDep(dep);
        return ret;
    }

    /**
     * @param dep
     *        dep
     * @return restorer for saved dep set
     */
    @PortedFrom(file = "CWDArray.h", name = "updateDepSet")
    public List<Restorer> updateDepSet(DepSet dep) {
        if (dep.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return asList(IntStream.range(0, size).mapToObj(i -> updateDepSet(i, dep)));
    }

    /**
     * @param ss
     *        ss
     * @param level
     *        level
     */
    @PortedFrom(file = "CWDArray.h", name = "restore")
    public void restore(int ss, int level) {
        // count the number of entries /not/ deleted
        int count = 0;
        for (int i = ss; i < size; i++) {
            // if backjumping is enabled, an entity is deleted only if the
            // depset level is the same or above level, otherwise the entry is
            // kept
            if (!options.isUseDynamicBackjumping() || base.get(i).getDep().level() >= level) {
                int concept = base.get(i).getConcept();
                indexes.remove(concept);
                cache.remove(asPositive(concept));
            } else {
                count++;
            }
        }
        Helper.resize(base, ss + count, null);
        size = ss + count;
    }

    @Override
    public String toString() {
        return base.subList(0, size).toString();
    }
}
