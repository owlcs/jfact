package uk.ac.manchester.cs.jfact.kernel.actors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import conformance.PortedFrom;

/**
 * class for acting with concept taxonomy
 * 
 * @param <T>
 *        type
 */
@PortedFrom(file = "JNIActor.h", name = "TaxonomyActor")
public class TaxonomyActor<T extends Expression> implements Actor, Serializable {

    private static final long serialVersionUID = 11000L;
    private final Policy policy;
    private final ExpressionCache cache;
    /** 2D array to return */
    @PortedFrom(file = "JNIActor.h", name = "acc")
    private final List<List<T>> acc = new ArrayList<>();
    /** 1D array to return */
    @PortedFrom(file = "JNIActor.h", name = "plain")
    private final List<T> plain = new ArrayList<>();
    /** temporary vector to keep synonyms */
    @PortedFrom(file = "JNIActor.h", name = "syn")
    private final List<T> syn = new ArrayList<>();

    @Override
    public boolean applicable(TaxonomyVertex v) {
        if (policy.applicable(v.getPrimer())) {
            return true;
        }
        return v.synonyms().stream().anyMatch(p -> policy.applicable(p));
    }

    /**
     * try current entry
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "JNIActor.h", name = "tryEntry")
    protected void tryEntry(ClassifiableEntry p) {
        if (p.isSystem()) {
            return;
        }
        if (policy.applicable(p)) {
            syn.add(asT(p));
        }
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
        v.synonyms().forEach(p -> tryEntry(p));
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
