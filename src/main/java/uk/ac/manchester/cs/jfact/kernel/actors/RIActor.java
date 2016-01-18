package uk.ac.manchester.cs.jfact.kernel.actors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.Concept;
import uk.ac.manchester.cs.jfact.kernel.Individual;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;

/** RI actor */
@PortedFrom(file = "Kernel.cpp", name = "RIActor")
public class RIActor extends TaxGatheringWalker {

    private final List<Individual> acc = new ArrayList<>();

    /**
     * process single entry in a vertex label
     * 
     * @param p
     *        p
     * @return true if try successful
     */
    @Override
    protected boolean tryEntry(ClassifiableEntry p) {
        // check the applicability
        if (p.isSystem() || !applicable(p)) {
            return false;
        }
        // print the concept
        acc.add((Individual) p);
        return true;
    }

    @Override
    protected boolean applicable(ClassifiableEntry entry) {
        return ((Concept) entry).isSingleton();
    }

    @Override
    public boolean apply(TaxonomyVertex v) {
        AtomicBoolean ret = new AtomicBoolean(tryEntry(v.getPrimer()));
        v.synonyms().forEach(p -> ret.compareAndSet(false, tryEntry(p)));
        return ret.get();
    }

    @Override
    public boolean applicable(TaxonomyVertex v) {
        if (test(v.getPrimer())) {
            return true;
        }
        return v.synonyms().anyMatch(RIActor::test);
    }

    private static boolean test(ClassifiableEntry p) {
        return !(p.isSystem() || !((Concept) p).isSingleton());
    }

    @Override
    public void clear() {
        acc.clear();
    }

    /** @return accumulator */
    public List<Individual> getAcc() {
        return acc;
    }
}
