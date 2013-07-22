package uk.ac.manchester.cs.jfact.kernel.actors;

import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.Concept;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;
import conformance.Original;
import conformance.PortedFrom;

/** @author ignazio */
@Original
public class ActorImpl implements Actor {
    /** vertices that satisfy the condition */
    @PortedFrom(file = "Actor.h", name = "found")
    protected List<TaxonomyVertex> found = new ArrayList<TaxonomyVertex>();
    /** flag to look at concept-like or role-like entities */
    @PortedFrom(file = "Actor.h", name = "isRole")
    protected boolean isRole;
    /** flag to look at concepts or object roles */
    @PortedFrom(file = "Actor.h", name = "isStandard")
    protected boolean isStandard;
    /** flag to throw exception at the 1st found */
    @PortedFrom(file = "Actor.h", name = "interrupt")
    protected boolean interrupt;

    @PortedFrom(file = "Actor.h", name = "clear")
    public void clear() {
        found.clear();
    }

    /** taxonomy walking method.
     * 
     * @return true if node was processed, false if node can not be processed in
     *         current settings */
    @PortedFrom(file = "Actor.h", name = "apply")
    public boolean apply(TaxonomyVertex v) {
        if (tryVertex(v)) {
            found.add(v);
            return true;
        }
        return false;
    }

    /** check whether actor is applicable to the ENTRY */
    @PortedFrom(file = "Actor.h", name = "applicable")
    protected boolean applicable(ClassifiableEntry entry) {
        if (isRole)   // object- or data-role
        {
            if (isStandard) {
                return true;
            } else {
                // data role -- need only direct ones
                return entry.getId() > 0;
            }
        } else {
            // concept or individual: standard are concepts
            return ((Concept) entry).isSingleton() != isStandard;
        }
    }

    /** fills an array with all suitable data from the vertex */
    @PortedFrom(file = "Actor.h", name = "fillArray")
    protected List<ClassifiableEntry> fillArray(TaxonomyVertex v) {
        List<ClassifiableEntry> array = new ArrayList<ClassifiableEntry>();
        if (tryEntry(v.getPrimer())) {
            array.add(v.getPrimer());
        }
        for (ClassifiableEntry p : v.begin_syn()) {
            if (tryEntry(p)) {
                array.add(p);
            }
        }
        return array;
    }

    /** @return true iff current entry is visible */
    protected boolean tryEntry(ClassifiableEntry p) {
        return !p.isSystem() && applicable(p);
    }

    /** @return true if at least one entry of a vertex V is visible */
    protected boolean tryVertex(TaxonomyVertex v) {
        if (tryEntry(v.getPrimer())) {
            return true;
        }
        for (ClassifiableEntry p : v.begin_syn()) {
            if (tryEntry(p)) {
                return true;
            }
        }
        return false;
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

    /** @param value
     *            set the interrupt parameter to VALUE */
    @PortedFrom(file = "Actor.h", name = "setInterruptAfterFirstFound")
    public void setInterruptAfterFirstFound(boolean value) {
        interrupt = value;
    }

    /** @return get NULL-terminated 2D array of all required elements of the
     *         taxonomy */
    @PortedFrom(file = "Actor.h", name = "getElements2D")
    public List<List<ClassifiableEntry>> getElements2D() {
        List<List<ClassifiableEntry>> ret = new ArrayList<List<ClassifiableEntry>>();
        for (int i = 0; i < found.size(); ++i) {
            ret.add(fillArray(found.get(i)));
        }
        return ret;
    }

    /** @return get NULL-terminated 1D array of all required elements of the
     *         taxonomy */
    @PortedFrom(file = "Actor.h", name = "getElements1D")
    public List<ClassifiableEntry> getElements1D() {
        List<ClassifiableEntry> vec = new ArrayList<ClassifiableEntry>();
        for (TaxonomyVertex p : found) {
            vec.addAll(fillArray(p));
        }
        return vec;
    }
}
