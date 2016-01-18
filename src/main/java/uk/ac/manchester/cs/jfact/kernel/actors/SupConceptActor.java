package uk.ac.manchester.cs.jfact.kernel.actors;

import java.io.Serializable;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;

/** class for exploring concept taxonomy to find super classes */
@PortedFrom(file = "Kernel.cpp", name = "SupConceptActor")
public class SupConceptActor implements Actor, Serializable {

    @PortedFrom(file = "Kernel.cpp", name = "pe") protected final ClassifiableEntry pe;

    /**
     * @param q
     *        q
     */
    public SupConceptActor(ClassifiableEntry q) {
        pe = q;
    }

    @PortedFrom(file = "Kernel.cpp", name = "entry")
    protected boolean entry(ClassifiableEntry q) {
        return !pe.equals(q);
    }

    @Override
    @PortedFrom(file = "Kernel.cpp", name = "apply")
    public boolean apply(TaxonomyVertex v) {
        if (!entry(v.getPrimer())) {
            return false;
        }
        return v.synonyms().allMatch(this::entry);
    }

    @Override
    public boolean applicable(TaxonomyVertex v) {
        if (entry(v.getPrimer())) {
            return true;
        }
        return v.synonyms().anyMatch(this::entry);
    }
}
