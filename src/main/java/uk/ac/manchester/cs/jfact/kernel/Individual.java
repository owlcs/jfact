package uk.ac.manchester.cs.jfact.kernel;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.verifyNotNull;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.*;

import org.semanticweb.owlapi.model.IRI;

import conformance.Original;
import conformance.PortedFrom;

/** individual */
@PortedFrom(file = "tIndividual.h", name = "TIndividual")
public class Individual extends Concept {

    /** pointer to nominal node (works for singletons only) */
    @PortedFrom(file = "tIndividual.h", name = "node") private DlCompletionTree node;
    /** index for axioms (this,C):R */
    @PortedFrom(file = "tIndividual.h", name = "RelatedIndex") private final List<Related> relatedIndex = new ArrayList<>();
    /** map for the related individuals: Map[R]={i:R(this,i)} */
    @PortedFrom(file = "tIndividual.h", name = "pRelatedMap") private final Map<Role, List<Individual>> pRelatedMap = new HashMap<>();

    /**
     * @param name
     *        name
     */
    public Individual(IRI name) {
        super(name);
        node = null;
        setSingleton(true);
    }

    /** init told subsumers of the individual by it's description */
    @Override
    @PortedFrom(file = "tIndividual.h", name = "initToldSubsumers")
    public void initToldSubsumers() {
        toldSubsumers = null;
        setHasSP(false);
        if (isRelated()) {
            updateToldFromRelated();
        }
        // normalise description if the only parent is TOP
        if (isPrimitive() && description != null && description.isTOP()) {
            removeDescription();
        }
        // not a completely defined if there are extra rules or related
        // individuals
        boolean cd = !hasExtraRules() && isPrimitive() && !isRelated();
        if (description != null || hasToldSubsumers()) {
            cd &= super.initToldSubsumers(description, new HashSet<Role>());
        }
        setCompletelyDefined(cd);
    }

    // related things
    /**
     * update told subsumers from the RELATED axioms in a given range
     * 
     * @param <T>
     *        type
     * @param begin
     *        begin
     * @param rolesProcessed
     *        RolesProcessed
     */
    @PortedFrom(file = "tIndividual.h", name = "updateTold")
    private <T extends Related> void updateTold(List<T> begin, Set<Role> rolesProcessed) {
        begin.forEach(t -> searchTSbyRoleAndSupers(t.getRole(), rolesProcessed));
    }

    /**
     * check if individual connected to something with RELATED statement
     * 
     * @return true if related
     */
    @PortedFrom(file = "tIndividual.h", name = "isRelated")
    private boolean isRelated() {
        return !relatedIndex.isEmpty();
    }

    /**
     * set individual related
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "tIndividual.h", name = "addRelated")
    public void addRelated(Related p) {
        relatedIndex.add(p);
    }

    /**
     * add all the related elements from the given P
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "tIndividual.h", name = "addRelated")
    public void addRelated(Individual p) {
        relatedIndex.addAll(p.relatedIndex);
    }

    // related map access
    /**
     * @param r
     *        R
     * @return true if has cache for related individuals via role R
     */
    @PortedFrom(file = "tIndividual.h", name = "hasRelatedCache")
    public boolean hasRelatedCache(Role r) {
        return pRelatedMap.containsKey(r);
    }

    /**
     * @param r
     *        R
     * @return set of individuals related to THIS via R
     */
    @PortedFrom(file = "tIndividual.h", name = "getRelatedCache")
    public List<Individual> getRelatedCache(Role r) {
        assert pRelatedMap.containsKey(r);
        return pRelatedMap.get(r);
    }

    /**
     * set the cache of individuals related to THIS via R
     * 
     * @param r
     *        R
     * @param v
     *        v
     */
    @PortedFrom(file = "tIndividual.h", name = "setRelatedCache")
    public void setRelatedCache(Role r, List<Individual> v) {
        assert !pRelatedMap.containsKey(r);
        pRelatedMap.put(r, v);
    }

    // TIndividual RELATED-dependent method' implementation
    @PortedFrom(file = "tIndividual.h", name = "updateToldFromRelated")
    private void updateToldFromRelated() {
        Set<Role> rolesProcessed = new HashSet<>();
        updateTold(relatedIndex, rolesProcessed);
    }

    /** @return completion tree node */
    @Original
    public DlCompletionTree getNode() {
        return verifyNotNull(node, "individual not initialized");
    }

    /** @return true if this individual is fresh and not initialized */
    @Original
    public boolean isFresh() {
        return node == null;
    }

    /**
     * @param node
     *        node
     */
    @Original
    public void setNode(DlCompletionTree node) {
        this.node = node;
    }

    /** @return list of related individuals */
    @Original
    public List<Related> getRelatedIndex() {
        return relatedIndex;
    }
}
