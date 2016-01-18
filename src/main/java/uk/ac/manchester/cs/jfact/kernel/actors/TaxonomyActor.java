package uk.ac.manchester.cs.jfact.kernel.actors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;

/**
 * class for acting with concept taxonomy
 * 
 * @param <T>
 *        type
 */
@PortedFrom(file = "JNIActor.h", name = "TaxonomyActor")
public class TaxonomyActor<T extends Expression> extends TaxGatheringWalker {

    private final Policy policy;
    private final ExpressionCache cache;
    /** 2D array to return */
    @PortedFrom(file = "JNIActor.h", name = "acc") private final List<List<T>> acc = new ArrayList<>();
    /** 1D array to return */
    @PortedFrom(file = "JNIActor.h", name = "plain") private final List<T> plain = new ArrayList<>();
    /** temporary vector to keep synonyms */
    @PortedFrom(file = "JNIActor.h", name = "syn") private final List<T> syn = new ArrayList<>();

    /**
     * @param em
     *        em
     * @param p
     *        p
     */
    @PortedFrom(file = "JNIActor.h", name = "TaxonomyActor")
    public TaxonomyActor(ExpressionCache em, Policy p) {
        cache = em;
        policy = p;
    }

    @Override
    public boolean applicable(TaxonomyVertex v) {
        if (applicable(v.getPrimer())) {
            return true;
        }
        return v.synonyms().anyMatch(this::applicable);
    }

    @Override
    protected boolean applicable(ClassifiableEntry entry) {
        return policy.applicable(entry);
    }

    /**
     * try current entry
     * 
     * @param p
     *        p
     */
    @Override
    @PortedFrom(file = "JNIActor.h", name = "tryEntry")
    protected boolean tryEntry(ClassifiableEntry p) {
        if (!p.isSystem() && applicable(p)) {
            syn.add(asT(p));
            return true;
        }
        return false;
    }

    /**
     * @param p
     *        classifiable entry
     * @return p rebuilt as T
     */
    @SuppressWarnings("unchecked")
    protected T asT(ClassifiableEntry p) {
        return (T) policy.buildTree(cache, p);
    }

    @Override
    @PortedFrom(file = "JNIActor.h", name = "clear")
    public void clear() {
        acc.clear();
        plain.clear();
    }

    // return values
    /**
     * @return single vector of synonyms (necessary for Equivalents, for
     *         example)
     */
    @PortedFrom(file = "JNIActor.h", name = "getSynonyms")
    public Collection<T> getSynonyms() {
        return acc.isEmpty() ? syn : acc.get(0);
    }

    /** @return 2D array of all required elements of the taxonomy */
    @PortedFrom(file = "JNIActor.h", name = "getElements")
    public List<Collection<T>> getElements() {
        if (policy.needPlain()) {
            return Collections.singletonList(plain);
        }
        return new ArrayList<>(acc);
    }

    @Override
    @PortedFrom(file = "JNIActor.h", name = "apply")
    public boolean apply(TaxonomyVertex v) {
        syn.clear();
        tryEntry(v.getPrimer());
        v.synonyms().forEach(this::tryEntry);
        /** no applicable elements were found */
        if (syn.isEmpty()) {
            return false;
        }
        if (policy.needPlain()) {
            plain.addAll(syn);
        } else {
            acc.add(new ArrayList<>(syn));
        }
        return true;
    }

    @Override
    public void removePastBoundaries(Collection<TaxonomyVertex> pastBoundary) {
        List<T> entries = new ArrayList<>();
        pastBoundary.forEach(t -> removePastBoundaries(entries, t));
        plain.removeAll(entries);
        acc.forEach(l -> l.removeAll(entries));
    }

    protected void removePastBoundaries(List<T> entries, TaxonomyVertex t) {
        entries.add(asT(t.getPrimer()));
        TaxonomyVertex t1 = t.getSynonymNode();
        while (t1 != null) {
            entries.add(asT(t1.getPrimer()));
            t1 = t1.getSynonymNode();
        }
    }
}
