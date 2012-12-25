package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.*;

import conformance.PortedFrom;

@PortedFrom(file = "taxNamEntry.h", name = "ClassifiableEntry")
public class ClassifiableEntry extends NamedEntry {
    /** link to taxonomy entry for current entry */
    protected TaxonomyVertex taxVertex = null;
    /** links to 'told subsumers' (entries that are direct super-entries for
     * current) */
    protected Set<ClassifiableEntry> toldSubsumers = new LinkedHashSet<ClassifiableEntry>();
    /** pointer to synonym (entry which contains whole information the same as
     * current) */
    protected ClassifiableEntry pSynonym;
    /** index as a vertex in the SubsumptionMap */
    protected int index;

    protected ClassifiableEntry(String name) {
        super(name);
        pSynonym = null;
        index = 0;
    }

    /** is current entry classified */
    public boolean isClassified() {
        return taxVertex != null;
    }

    /** set up given entry */
    public void setTaxVertex(TaxonomyVertex vertex) {
        taxVertex = vertex;
    }

    /** get taxonomy vertex of the entry */
    public TaxonomyVertex getTaxVertex() {
        return taxVertex;
    }

    private boolean completelyDefined;

    // completely defined interface
    /** a Completely Defined flag */
    public boolean isCompletelyDefined() {
        return completelyDefined;
    }

    public void clearCompletelyDefined() {
        completelyDefined = false;
    }

    public void setCompletelyDefined(boolean action) {
        completelyDefined = action;
    }

    private boolean nonClassifiable;

    /** a non-classifiable flag */
    public boolean isNonClassifiable() {
        return nonClassifiable;
    }

    public void setNonClassifiable() {
        nonClassifiable = true;
    }

    public void clearNonClassifiable() {
        nonClassifiable = false;
    }

    public void setNonClassifiable(boolean action) {
        nonClassifiable = action;
    }

    /** told subsumers */
    public Collection<ClassifiableEntry> getToldSubsumers() {
        return toldSubsumers;
    }

    /** check whether entry ihas any TS */
    public boolean hasToldSubsumers() {
        return !toldSubsumers.isEmpty();
    }

    /** add told subsumer of entry (duplications possible) */
    public void addParent(ClassifiableEntry parent) {
        toldSubsumers.add(parent);
    }

    /** add all parents (with duplicates) from the range to current node */
    public void addParents(Collection<ClassifiableEntry> entries) {
        for (ClassifiableEntry c : entries) {
            addParentIfNew(c);
        }
    }

    // index interface
    /** get the index value */
    @Override
    public int getIndex() {
        return index;
    }

    /** set the index value */
    @Override
    public void setIndex(int ind) {
        index = ind;
    }

    // synonym interface
    /** check if current entry is a synonym */
    public boolean isSynonym() {
        return pSynonym != null;
    }

    /** get synonym of current entry */
    public ClassifiableEntry getSynonym() {
        return pSynonym;
    }

    /** make sure that synonym's representative is not a synonym itself */
    public void canonicaliseSynonym() {
        assert isSynonym();
        if (isSynonym()) {
            pSynonym = resolveSynonym(pSynonym);
        }
    }

    /** add entry's synonym */
    public void setSynonym(ClassifiableEntry syn) {
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
            // System.out
            // .println("ClassifiableEntry.setSynonym(): warning: assigning this synonym would create a loop; ignored\nignored synonym: "
            // + this + " -> " + syn + "\nPrevious synonyms: " + set);
            // } else {
            pSynonym = syn;
            canonicaliseSynonym();
        }
    }

    /** if two synonyms are in 'told' list, merge them */
    public void removeSynonymsFromParents() {
        List<ClassifiableEntry> toRemove = new ArrayList<ClassifiableEntry>();
        for (ClassifiableEntry c : toldSubsumers) {
            if (this == resolveSynonym(c)) {
                toRemove.add(c);
            }
        }
        toldSubsumers.removeAll(toRemove);
    }

    public static <T extends ClassifiableEntry> T resolveSynonym(T p) {
        return p == null ? null : p.isSynonym() ? resolveSynonym((T) p.pSynonym) : p;
    }

    public void addParentIfNew(ClassifiableEntry parent) {
        // resolve synonyms
        parent = resolveSynonym(parent);
        // node can not be its own parent
        if (parent == this) {
            return;
        }
        toldSubsumers.add(parent);
    }
}
