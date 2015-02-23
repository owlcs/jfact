package uk.ac.manchester.cs.jfact.kernel.actors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.Concept;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;
import conformance.Original;
import conformance.PortedFrom;

/** @author ignazio */
@Original
public class ActorImpl implements Actor, Serializable {

    private static final long serialVersionUID = 11000L;
    /** vertices that satisfy the condition */
    @PortedFrom(file = "Actor.h", name = "found")
    protected final List<TaxonomyVertex> found = new ArrayList<>();
    /** flag to look at concept-like or role-like entities */
    @PortedFrom(file = "Actor.h", name = "isRole")
    protected boolean isRole;
    /** flag to look at concepts or object roles */
    @PortedFrom(file = "Actor.h", name = "isStandard")
    protected boolean isStandard;
    /** flag to throw exception at the 1st found */
    @PortedFrom(file = "Actor.h", name = "interrupt")
    protected boolean interrupt;

    @Override
    @PortedFrom(file = "Actor.h", name = "clear")
    public void clear() {
        found.clear();
    }

    @Override
    @PortedFrom(file = "Actor.h", name = "apply")
    public boolean apply(TaxonomyVertex v) {
        if (tryVertex(v)) {
            found.add(v);
            return true;
        }
        return false;
    }

    /**
     * check whether actor is applicable to the ENTRY
     * 
     * @param entry
     *        entry
     * @return true if applicable
     */
    @PortedFrom(file = "Actor.h", name = "applicable")
    protected boolean applicable(ClassifiableEntry entry) {
        if (isRole) {
            // object- or data-role
            if (isStandard) {
                return true;
            } else {
                // data role -- need only direct ones
                return entry.getId() > 0;
            }
        } else {
            // concept or individual: standard are concepts
            return entry instanceof Concept
                    && ((Concept) entry).isSingleton() != isStandard;
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
        v.synonyms().stream().filter(p -> tryEntry(p))
                .forEach(p -> array.add(p));
        return array;
    }

    @Override
    public boolean applicable(TaxonomyVertex v) {
        if (tryEntry(v.getPrimer())) {
            return true;
        }
        return v.synonyms().stream().anyMatch(p -> tryEntry(p));
    }

    /**
     * @param p
     *        p
     * @return true iff current entry is visible
     */
    protected boolean tryEntry(ClassifiableEntry p) {
        return !p.isSystem() && applicable(p);
    }

    /**
     * @param v
     *        v
     * @return true if at least one entry of a vertex V is visible
     */
    protected boolean tryVertex(TaxonomyVertex v) {
        if (tryEntry(v.getPrimer())) {
            return true;
        }
        return v.synonyms().stream().anyMatch(p -> tryEntry(p));
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
        return found.stream().map(p -> fillArray(p)).collect(toList());
    }

    /**
     * @return get NULL-terminated 1D array of all required elements of the
     *         taxonomy
     */
    @PortedFrom(file = "Actor.h", name = "getElements1D")
    public List<ClassifiableEntry> getElements1D() {
        return found.stream().flatMap(p -> fillArray(p).stream())
                .collect(toList());
    }

    @Override
    public void removePastBoundaries(Collection<TaxonomyVertex> pastBoundary) {
        pastBoundary.forEach(t -> {
            found.remove(t.getPrimer());
            TaxonomyVertex t1 = t.getSynonymNode();
            while (t1 != null) {
                found.remove(t1.getPrimer());
                t1 = t1.getSynonymNode();
            }
        });
    }
}
