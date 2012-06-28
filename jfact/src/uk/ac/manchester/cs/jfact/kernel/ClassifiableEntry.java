package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClassifiableEntry extends NamedEntry {
    /** link to taxonomy entry for current entry */
    protected TaxonomyVertex taxVertex = null;
    /**
     * links to 'told subsumers' (entries that are direct super-entries for
     * current)
     */
    protected final Set<ClassifiableEntry> toldSubsumers = new LinkedHashSet<ClassifiableEntry>();
    /**
     * pointer to synonym (entry which contains whole information the same as
     * current)
     */
    protected ClassifiableEntry pSynonym;
    /** index as a vertex in the SubsumptionMap */
    protected int index;

    protected ClassifiableEntry(final String name) {
        super(name);
        pSynonym = null;
        index = 0;
    }

    /** is current entry classified */
    public final boolean isClassified() {
        return taxVertex != null;
    }

    /** set up given entry */
    public final void setTaxVertex(final TaxonomyVertex vertex) {
        taxVertex = vertex;
    }

    /** get taxonomy vertex of the entry */
    public final TaxonomyVertex getTaxVertex() {
        return taxVertex;
    }

    private boolean completelyDefined;

    // completely defined interface
    /** a Completely Defined flag */
    public final boolean isCompletelyDefined() {
        return completelyDefined;
    }

    public final void clearCompletelyDefined() {
        completelyDefined = false;
    }

    public final void setCompletelyDefined(final boolean action) {
        completelyDefined = action;
    }

    private boolean nonClassifiable;

    /** a non-classifiable flag */
    public final boolean isNonClassifiable() {
        return nonClassifiable;
    }

    public final void setNonClassifiable() {
        nonClassifiable = true;
    }

    public final void clearNonClassifiable() {
        nonClassifiable = false;
    }

    public final void setNonClassifiable(final boolean action) {
        nonClassifiable = action;
    }

    /** told subsumers */
    public final Collection<ClassifiableEntry> getToldSubsumers() {
        return toldSubsumers;
    }

    /** check whether entry ihas any TS */
    public final boolean hasToldSubsumers() {
        return !toldSubsumers.isEmpty();
    }

    /** add told subsumer of entry (duplications possible) */
    public final void addParent(final ClassifiableEntry parent) {
        toldSubsumers.add(parent);
    }

    /** add all parents (with duplicates) from the range to current node */
    public final void addParents(final Collection<ClassifiableEntry> entries) {
        for (ClassifiableEntry c : entries) {
            addParentIfNew(c);
        }
    }

    // index interface
    /** get the index value */
    @Override
    public final int getIndex() {
        return index;
    }

    /** set the index value */
    @Override
    public void setIndex(final int ind) {
        index = ind;
    }

    // synonym interface
    /** check if current entry is a synonym */
    public final boolean isSynonym() {
        return pSynonym != null;
    }

    /** get synonym of current entry */
    public final ClassifiableEntry getSynonym() {
        return pSynonym;
    }

    /** make sure that synonym's representative is not a synonym itself */
    public final void canonicaliseSynonym() {
        if (isSynonym()) {
            while (pSynonym.isSynonym()) {
                pSynonym = pSynonym.pSynonym;
            }
        }
    }

    /** add entry's synonym */
    public final void setSynonym(final ClassifiableEntry syn) {
        assert pSynonym == null; // do it only once
        // check there are no cycles
        Set<ClassifiableEntry> set = new HashSet<ClassifiableEntry>();
        set.add(this);
        ClassifiableEntry runner = syn;
        while (runner.isSynonym() && !set.contains(runner.pSynonym)) {
            set.add(runner.pSynonym);
            runner = runner.pSynonym;
        }
        if (!set.contains(runner.pSynonym)) {
            // then adding this synonym would cause a loop
            //			System.out
            //					.println("ClassifiableEntry.setSynonym(): warning: assigning this synonym would create a loop; ignored\nignored synonym: "
            //							+ this + " -> " + syn + "\nPrevious synonyms: " + set);
            //		} else {
            pSynonym = syn;
            canonicaliseSynonym();
        }
    }

    /** if two synonyms are in 'told' list, merge them */
    public final void removeSynonymsFromParents() {
        List<ClassifiableEntry> toRemove = new ArrayList<ClassifiableEntry>();
        for (ClassifiableEntry c : toldSubsumers) {
            if (this == resolveSynonym(c)) {
                toRemove.add(c);
            }
        }
        toldSubsumers.removeAll(toRemove);
    }

    public final static <T extends ClassifiableEntry> T resolveSynonym(final T p) {
        return p == null ? null : p.isSynonym() ? resolveSynonym((T) p.pSynonym) : p;
    }

    public final void addParentIfNew(ClassifiableEntry parent) {
        // resolve synonyms
        parent = resolveSynonym(parent);
        // node can not be its own parent
        if (parent == this) {
            return;
        }
        toldSubsumers.add(parent);
    }
}
