package uk.ac.manchester.cs.jfact.kernel;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;

import conformance.Original;
import conformance.PortedFrom;

/** classifiable entry */
@PortedFrom(file = "taxNamEntry.h", name = "ClassifiableEntry")
public class ClassifiableEntry extends NamedEntry {

    /** link to taxonomy entry for current entry */
    @PortedFrom(file = "taxNamEntry.h", name = "taxVertex") protected TaxonomyVertex taxVertex = null;
    /**
     * links to 'told subsumers' (entries that are direct super-entries for
     * current)
     */
    @PortedFrom(file = "taxNamEntry.h", name = "toldSubsumers") protected List<ClassifiableEntry> toldSubsumers = null;
    /**
     * pointer to synonym (entry which contains whole information the same as
     * current)
     */
    @PortedFrom(file = "taxNamEntry.h", name = "pSynonym") protected ClassifiableEntry pSynonym = null;
    /** index as a vertex in the SubsumptionMap */
    @PortedFrom(file = "taxNamEntry.h", name = "Index") protected int index = 0;
    @Original private boolean completelyDefined;
    @Original private boolean nonClassifiable;

    protected ClassifiableEntry(IRI name) {
        super(name);
    }

    /**
     * @return is current entry classified
     */
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

    /**
     * @return taxonomy vertex of the entry
     */
    @Nullable
    @PortedFrom(file = "taxNamEntry.h", name = "getTaxVertex")
    public TaxonomyVertex getTaxVertex() {
        return taxVertex;
    }

    // completely defined interface
    /**
     * @return a Completely Defined flag
     */
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

    /**
     * @return non classifiable?
     */
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

    /**
     * @return told subsumers
     */
    @Nullable
    @PortedFrom(file = "taxNamEntry.h", name = "told_begin")
    public Collection<ClassifiableEntry> getToldSubsumers() {
        return toldSubsumers;
    }

    /**
     * @return whether entry ihas any TS
     */
    @PortedFrom(file = "taxNamEntry.h", name = "hasToldSubsumers")
    public boolean hasToldSubsumers() {
        return toldSubsumers != null && !toldSubsumers.isEmpty();
    }

    /**
     * add told subsumer of entry (duplications possible)
     * 
     * @param parent
     *        parent
     */
    @PortedFrom(file = "taxNamEntry.h", name = "addParent")
    public void addParent(ClassifiableEntry parent) {
        // a node cannot be its own parent
        if (parent != this) {
            addP(parent);
        }
    }

    protected void addP(ClassifiableEntry e) {
        if (toldSubsumers == null) {
            toldSubsumers = new ArrayList<>();
        } else if (toldSubsumers.contains(e)) {
            // do not create duplicates
            return;
        }
        toldSubsumers.add(e);
    }

    /**
     * add all parents (with duplicates) from the range to current node
     * 
     * @param entries
     *        entries
     */
    @PortedFrom(file = "taxNamEntry.h", name = "addParents")
    public void addParents(@Nullable Collection<ClassifiableEntry> entries) {
        if (entries != null) {
            entries.forEach(this::addParentIfNew);
        }
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
    /**
     * @return if current entry is a synonym
     */
    @PortedFrom(file = "taxNamEntry.h", name = "isSynonym")
    public boolean isSynonym() {
        return pSynonym != null;
    }

    /**
     * @return synonym of current entry
     */
    @PortedFrom(file = "taxNamEntry.h", name = "getSynonym")
    public ClassifiableEntry getSynonym() {
        return pSynonym;
    }

    /** make sure that synonym's representative is not a synonym itself */
    @PortedFrom(file = "taxNamEntry.h", name = "canonicaliseSynonym")
    public void canonicaliseSynonym() {
        assert isSynonym();
        pSynonym = resolveSynonym(pSynonym);
    }

    /**
     * add entry's synonym
     * 
     * @param syn
     *        syn
     */
    @PortedFrom(file = "taxNamEntry.h", name = "setSynonym")
    public void setSynonym(@Nullable ClassifiableEntry syn) {
        // do it only once
        assert pSynonym == null;
        // XXX check this code
        // FaCT++ has
        // pSynonym = syn;
        // canonicaliseSynonym();
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
        if (hasToldSubsumers()) {
            List<ClassifiableEntry> toKeep = asList(toldSubsumers.stream().map(ClassifiableEntry::resolveSynonym)
                .filter(p -> this != p).distinct());
            toldSubsumers.clear();
            toldSubsumers.addAll(toKeep);
            if (toldSubsumers.isEmpty()) {
                toldSubsumers = null;
            }
        }
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
        addParent(resolveSynonym(parent));
    }
}
