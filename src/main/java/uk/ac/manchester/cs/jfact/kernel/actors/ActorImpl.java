package uk.ac.manchester.cs.jfact.kernel.actors;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.*;

import java.util.ArrayList;
import java.util.List;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.Concept;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;

/** @author ignazio */
@Original
public class ActorImpl extends TaxGatheringWalker {

    /** flag to look at concept-like or role-like entities */
    @PortedFrom(file = "Actor.h", name = "isRole") protected boolean isRole;
    /** flag to look at concepts or object roles */
    @PortedFrom(file = "Actor.h", name = "isStandard") protected boolean isStandard;
    /** flag to throw exception at the 1st found */
    @PortedFrom(file = "Actor.h", name = "interrupt") protected boolean interrupt;

    /**
     * check whether actor is applicable to the ENTRY
     * 
     * @param entry
     *        entry
     * @return true if applicable
     */
    @Override
    @PortedFrom(file = "Actor.h", name = "applicable")
    protected boolean applicable(ClassifiableEntry entry) {
        if (isRole) {
            // object- or data-role
            if (isStandard) {
                return true;
            } else {
                // data role -- need only direct ones and TOP/BOT
                return entry.getId() > -1;
            }
        } else {
            // concept or individual: standard are concepts
            return entry instanceof Concept && ((Concept) entry).isSingleton() != isStandard;
        }
    }

    /**
     * fills an array with all suitable data from the vertex
     * 
     * @param v
     *        v
     * @return all suitable data
     */
    @PortedFrom(file = "Actor.h", name = "fillArray")
    protected List<ClassifiableEntry> fillArray(TaxonomyVertex v) {
        List<ClassifiableEntry> array = new ArrayList<>();
        if (tryEntry(v.getPrimer())) {
            array.add(v.getPrimer());
        }
        add(array, v.synonyms().filter(this::tryEntry));
        return array;
    }

    @Override
    public boolean applicable(TaxonomyVertex v) {
        if (tryEntry(v.getPrimer())) {
            return true;
        }
        return v.synonyms().anyMatch(this::tryEntry);
    }

    /** set the actor to look for classes */
    @PortedFrom(file = "Actor.h", name = "needConcepts")
    public void needConcepts() {
        isRole = false;
        isStandard = true;
    }

    /** set the actor to look for individuals */
    @PortedFrom(file = "Actor.h", name = "needIndividuals")
    public void needIndividuals() {
        isRole = false;
        isStandard = false;
    }

    /** set the actor to look for object properties */
    @PortedFrom(file = "Actor.h", name = "needObjectRoles")
    public void needObjectRoles() {
        isRole = true;
        isStandard = true;
    }

    /** set the actor to look for individuals */
    @PortedFrom(file = "Actor.h", name = "needDataRoles")
    public void needDataRoles() {
        isRole = true;
        isStandard = false;
    }

    /**
     * @param value
     *        set the interrupt parameter to VALUE
     */
    @PortedFrom(file = "Actor.h", name = "setInterruptAfterFirstFound")
    public void setInterruptAfterFirstFound(boolean value) {
        interrupt = value;
    }

    /**
     * @return get NULL-terminated 2D array of all required elements of the
     *         taxonomy
     */
    @PortedFrom(file = "Actor.h", name = "getElements2D")
    public List<List<ClassifiableEntry>> getElements2D() {
        return asList(found.stream().map(this::fillArray));
    }

    /**
     * @return get NULL-terminated 1D array of all required elements of the
     *         taxonomy
     */
    @PortedFrom(file = "Actor.h", name = "getElements1D")
    public List<ClassifiableEntry> getElements1D() {
        return asList(found.stream().flatMap(p -> fillArray(p).stream()));
    }
}
