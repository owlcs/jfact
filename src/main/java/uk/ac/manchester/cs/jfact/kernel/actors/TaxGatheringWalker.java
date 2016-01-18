package uk.ac.manchester.cs.jfact.kernel.actors;

import java.util.ArrayList;
import java.util.List;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;

/** taxonomy walkers that gathers all the relevant nodes */
@PortedFrom(file = "TaxGatheringWalker.h", name = "TaxGatheringWalker")
abstract class TaxGatheringWalker implements Actor {

    /** vertices that satisfy the condition */
    protected List<TaxonomyVertex> found = new ArrayList<>();

    /** check whether actor is applicable to the ENTRY */
    protected abstract boolean applicable(ClassifiableEntry entry);

    /** @return true iff current entry is visible */
    protected boolean tryEntry(ClassifiableEntry p) {
        return !p.isSystem() && applicable(p);
    }

    /** @return true if at least one entry of a vertex V is visible */
    protected boolean tryVertex(TaxonomyVertex v) {
        if (tryEntry(v.getPrimer())) {
            return true;
        }
        return v.synonyms().anyMatch(this::tryEntry);
    }

    @Override
    public void clear() {
        found.clear();
    }

    @Override
    public boolean apply(TaxonomyVertex v) {
        if (tryVertex(v)) {
            found.add(v);
            return true;
        }
        return false;
    }
}
