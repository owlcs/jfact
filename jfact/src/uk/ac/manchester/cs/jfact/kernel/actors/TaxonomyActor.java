package uk.ac.manchester.cs.jfact.kernel.actors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import conformance.PortedFrom;

/** class for acting with concept taxonomy
 * 
 * @param <T> */
@PortedFrom(file = "JNIActor.h", name = "TaxonomyActor")
public class TaxonomyActor<T extends Expression> implements Actor, Serializable {
    private static final long serialVersionUID = 11000L;
    private Policy policy;
    private ExpressionManager expressionManager;
    /** 2D array to return */
    @PortedFrom(file = "JNIActor.h", name = "acc")
    private List<List<T>> acc = new ArrayList<List<T>>();
    /** 1D array to return */
    @PortedFrom(file = "JNIActor.h", name = "plain")
    private List<T> plain = new ArrayList<T>();
    /** temporary vector to keep synonyms */
    @PortedFrom(file = "JNIActor.h", name = "syn")
    private List<T> syn = new ArrayList<T>();

    /** try current entry
     * 
     * @param p */
    @PortedFrom(file = "JNIActor.h", name = "tryEntry")
    protected void tryEntry(ClassifiableEntry p) {
        if (p.isSystem()) {
            return;
        }
        if (policy.applicable(p)) {
            syn.add((T) policy.buildTree(expressionManager, p));
        }
    }

    /** @param em
     * @param p */
    @PortedFrom(file = "JNIActor.h", name = "TaxonomyActor")
    public TaxonomyActor(ExpressionManager em, Policy p) {
        expressionManager = em;
        policy = p;
    }

    @PortedFrom(file = "JNIActor.h", name = "clear")
    public void clear() {
        acc.clear();
        plain.clear();
    }

    // return values
    /** @return single vector of synonyms (necessary for Equivalents, for example) */
    @PortedFrom(file = "JNIActor.h", name = "getSynonyms")
    public Collection<T> getSynonyms() {
        return acc.isEmpty() ? syn : acc.get(0);
    }

    /** @return 2D array of all required elements of the taxonomy */
    @PortedFrom(file = "JNIActor.h", name = "getElements")
    public Collection<Collection<T>> getElements() {
        Collection<Collection<T>> toReturn = new ArrayList<Collection<T>>();
        if (policy.needPlain()) {
            toReturn.add(plain);
        } else {
            for (int i = 0; i < acc.size(); ++i) {
                toReturn.add(acc.get(i));
            }
        }
        return toReturn;
    }

    /** taxonomy walking method.
     * 
     * @return true if node was processed, and there is no need to go further,
     *         false if node can not be processed in current settings */
    @PortedFrom(file = "JNIActor.h", name = "apply")
    public boolean apply(TaxonomyVertex v) {
        syn.clear();
        tryEntry(v.getPrimer());
        for (ClassifiableEntry p : v.begin_syn()) {
            tryEntry(p);
        }
        /** no applicable elements were found */
        if (syn.isEmpty()) {
            return false;
        }
        if (policy.needPlain()) {
            plain.addAll(syn);
        } else {
            acc.add(syn);
        }
        return true;
    }
}
