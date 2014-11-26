package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import conformance.Original;
import conformance.PortedFrom;

/** classifiable entry */
@PortedFrom(file = "taxNamEntry.h", name = "ClassifiableEntry")
public class ClassifiableEntry extends NamedEntry {

    private static final long serialVersionUID = 11000L;
    /** link to taxonomy entry for current entry */
    @PortedFrom(file = "taxNamEntry.h", name = "taxVertex")
    protected TaxonomyVertex taxVertex = null;
    /**
     * links to 'told subsumers' (entries that are direct super-entries for
     * current)
     */
    @PortedFrom(file = "taxNamEntry.h", name = "toldSubsumers")
    protected final Set<ClassifiableEntry> toldSubsumers = new LinkedHashSet<>();
    /**
     * pointer to synonym (entry which contains whole information the same as
     * current)
     */
    @PortedFrom(file = "taxNamEntry.h", name = "pSynonym")
    protected ClassifiableEntry pSynonym = null;
    /** index as a vertex in the SubsumptionMap */
    @PortedFrom(file = "taxNamEntry.h", name = "Index")
    protected int index = 0;

    protected ClassifiableEntry(IRI name) {
        super(name);
    }

    /** @return is current entry classified */
    @PortedFrom(file = "taxNamEntry.h", name = "isClassified")
    public boolean isClassified() {
        return taxVertex != null;
    }

    /**
     * set up given entry
     * 
     * @param vertex
     *        vertex
     */
    @PortedFrom(file = "taxNamEntry.h", name = "setTaxVertex")
    public void setTaxVertex(TaxonomyVertex vertex) {
        taxVertex = vertex;
    }

    /** @return taxonomy vertex of the entry */
    @PortedFrom(file = "taxNamEntry.h", name = "getTaxVertex")
    public TaxonomyVertex getTaxVertex() {
        return taxVertex;
    }

    @Original
    private boolean completelyDefined;

    // completely defined interface
    /** @return a Completely Defined flag */
    @Original
    public boolean isCompletelyDefined() {
        return completelyDefined;
    }

    /**
     * @param action
     *        action
     */
    @Original
    public void setCompletelyDefined(boolean action) {
        completelyDefined = action;
    }

    @Original
    private boolean nonClassifiable;

    /** @return non classifiable? */
    @Original
    public boolean isNonClassifiable() {
        return nonClassifiable;
    }

    /**
     * @param action
     *        action
     */
    @Original
    public void setNonClassifiable(boolean action) {
        nonClassifiable = action;
    }

    /** @return told subsumers */
    @PortedFrom(file = "taxNamEntry.h", name = "told_begin")
    public Collection<ClassifiableEntry> getToldSubsumers() {
        return toldSubsumers;
    }

    /** @return whether entry ihas any TS */
    @PortedFrom(file = "taxNamEntry.h", name = "hasToldSubsumers")
    public boolean hasToldSubsumers() {
        return !toldSubsumers.isEmpty();
    }

    /**
     * add told subsumer of entry (duplications possible)
     * 
     * @param parent
     *        parent
     */
    @PortedFrom(file = "taxNamEntry.h", name = "addParent")
    public void addParent(ClassifiableEntry parent) {
        toldSubsumers.add(parent);
    }

    /**
     * add all parents (with duplicates) from the range to current node
     * 
     * @param entries
     *        entries
     */
    @PortedFrom(file = "taxNamEntry.h", name = "addParents")
    public void addParents(Collection<ClassifiableEntry> entries) {
        entries.forEach(c -> addParentIfNew(c));
    }

    // index interface
    /** get the index value */
    @Override
    @PortedFrom(file = "taxNamEntry.h", name = "getIndex")
    public int getIndex() {
        return index;
    }

    /** set the index value */
    @Override
    @PortedFrom(file = "taxNamEntry.h", name = "setIndex")
    public void setIndex(int ind) {
        index = ind;
    }

    // synonym interface
    /** @return if current entry is a synonym */
    @PortedFrom(file = "taxNamEntry.h", name = "isSynonym")
    public boolean isSynonym() {
        return pSynonym != null;
    }

    /** @return synonym of current entry */
    @PortedFrom(file = "taxNamEntry.h", name = "getSynonym")
    public ClassifiableEntry getSynonym() {
        return pSynonym;
    }

    /** make sure that synonym's representative is not a synonym itself */
    @PortedFrom(file = "taxNamEntry.h", name = "canonicaliseSynonym")
    public void canonicaliseSynonym() {
        assert isSynonym();
        if (isSynonym()) {
            pSynonym = resolveSynonym(pSynonym);
        }
    }

    /**
     * add entry's synonym
     * 
     * @param syn
     *        syn
     */
    @PortedFrom(file = "taxNamEntry.h", name = "setSynonym")
    public void setSynonym(ClassifiableEntry syn) {
        // do it only once
        assert pSynonym == null;
        // check there are no cycles
        Set<ClassifiableEntry> set = new HashSet<>();
        set.add(this);
        ClassifiableEntry runner = syn;
        while (runner.isSynonym() && !set.contains(runner.pSynonym)) {
            set.add(runner.pSynonym);
            runner = runner.pSynonym;
        }
        if (!set.contains(runner.pSynonym)) {
            // then adding this synonym would cause a loop
            pSynonym = syn;
            canonicaliseSynonym();
        }
    }

    /** if two synonyms are in 'told' list, merge them */
    @PortedFrom(file = "taxNamEntry.h", name = "removeSynonymsFromParents")
    public void removeSynonymsFromParents() {
        List<ClassifiableEntry> toRemove = new ArrayList<>();
        for (ClassifiableEntry c : toldSubsumers) {
            if (this == resolveSynonym(c)) {
                toRemove.add(c);
            }
        }
        toldSubsumers.removeAll(toRemove);
    }

    /**
     * @param p
     *        p
     * @param <T>
     *        expression type
     * @return resolved synonym
     */
    @SuppressWarnings("unchecked")
    @PortedFrom(file = "taxNamEntry.h", name = "resolveSynonym")
    public static <T extends ClassifiableEntry> T resolveSynonym(T p) {
        if (p == null) {
            return null;
        }
        if (p.isSynonym()) {
            return resolveSynonym((T) p.pSynonym);
        }
        return p;
    }

    /**
     * @param parent
     *        parent
     */
    @PortedFrom(file = "taxNamEntry.h", name = "addParentIfNew")
    public void addParentIfNew(ClassifiableEntry parent) {
        // resolve synonyms
        parent = resolveSynonym(parent);
        // node can not be its own parent
        if (parent == this) {
            return;
        }
        addParent(parent);
    }
}
