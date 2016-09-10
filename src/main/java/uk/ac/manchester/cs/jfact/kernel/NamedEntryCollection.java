package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;

import conformance.Original;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/**
 * class for collect TNamedEntry'es together. Template parameter should be
 * inherited from TNamedEntry. Implemented as vector of T*, with Base[i].getId()
 * == i.
 * 
 * @param <T>
 *        type
 */
public class NamedEntryCollection<T extends NamedEntry> implements Serializable {

    /** nameset to hold the elements */
    private final NameSet<T, IRI> nameset;
    /** name of the type */
    private final String typeName;
    /** flag to lock the nameset (ie, prohibit to add new names there) */
    private boolean locked;
    @Original private final JFactReasonerConfiguration options;

    /**
     * c'tor: clear 0-th element
     * 
     * @param name
     *        name
     * @param options
     *        options
     */
    public NamedEntryCollection(String name, JFactReasonerConfiguration options) {
        typeName = name;
        locked = false;
        nameset = new NameSet<>();
        this.options = options;
    }

    /** @return check if collection is locked */
    public boolean isLocked() {
        return locked;
    }

    /**
     * set LOCKED value to a VAL;
     * 
     * @param val
     *        val
     * @return old value of LOCKED
     */
    public boolean setLocked(boolean val) {
        boolean old = locked;
        locked = val;
        return old;
    }

    // add/remove elements
    /**
     * @param name
     *        name
     * @return check if entry with a NAME is registered in given collection
     */
    public boolean isRegistered(IRI name) {
        return nameset.get(name) != null;
    }

    /**
     * @param name
     *        name
     * @param creator
     *        creator
     * @return get entry by NAME from the collection; register it if necessary
     */
    public T get(IRI name,  Function<IRI, T> creator) {
        T p = nameset.get(name);
        // check if name is already defined
        if (p != null) {
            return p;
        }
        // check if it is possible to insert name
        if (isLocked() && !options.isUseUndefinedNames()
            && options.getFreshEntityPolicy() == FreshEntityPolicy.DISALLOW) {
            throw new ReasonerFreshEntityException("Unable to register '" + name + "' as a " + typeName, name);
        }
        // create name in name set, and register it
        p = nameset.add(name, creator);
        // if fresh entity -- mark it System
        if (isLocked()) {
            p.setSystem();
            if (p instanceof ClassifiableEntry) {
                ((ClassifiableEntry) p).setNonClassifiable(true);
            }
        }
        return p;
    }

    /**
     * remove given entry from the collection;
     * 
     * @param p
     *        p
     * @return true iff it was NOT the last entry.
     */
    public boolean remove(T p) {
        if (!isRegistered(p.getIRI())) {
            // not in a name-set: just delete it
            return false;
        }
        nameset.remove(p.getIRI());
        return false;
    }

    /**
     * @return concept stream
     */
    public Stream<T> getConcepts() {
        return nameset.values().stream();
    }

    /**
     * @return first element
     */
    @Nullable
    public T first() {
        if (nameset.size() > 0) {
            return nameset.values().iterator().next();
        }
        return null;
    }

    /**
     * @return size
     */
    public int size() {
        return nameset.size();
    }
}
