package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.*;

import conformance.Original;
import conformance.PortedFrom;

/** individual */
@PortedFrom(file = "tIndividual.h", name = "TIndividual")
public class Individual extends Concept { private static final long serialVersionUID=11000L;
    /** pointer to nominal node (works for singletons only) */
    @PortedFrom(file = "tIndividual.h", name = "node")
    private DlCompletionTree node;
    /** index for axioms <this,C>:R */
    @PortedFrom(file = "tIndividual.h", name = "RelatedIndex")
    private List<Related> relatedIndex = new ArrayList<Related>();
    /** map for the related individuals: Map[R]={i:R(this,i)} */
    @PortedFrom(file = "tIndividual.h", name = "pRelatedMap")
    private Map<Role, List<Individual>> pRelatedMap;

    /** @param name */
    public Individual(String name) {
        super(name);
        node = null;
        setSingleton(true);
        pRelatedMap = new HashMap<Role, List<Individual>>();
    }

    /** init told subsumers of the individual by it's description */
    @Override
    @PortedFrom(file = "tIndividual.h", name = "initToldSubsumers")
    public void initToldSubsumers() {
        toldSubsumers.clear();
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
        boolean CD = !hasExtraRules() && isPrimitive() && !isRelated();
        if (description != null || hasToldSubsumers()) {
            CD &= super.initToldSubsumers(description, new HashSet<Role>());
        }
        setCompletelyDefined(CD);
    }

    // related things
    /** update told subsumers from the RELATED axioms in a given range */
    @PortedFrom(file = "tIndividual.h", name = "updateTold")
    private <T extends Related> void updateTold(List<T> begin, Set<Role> RolesProcessed) {
        for (int i = 0; i < begin.size(); i++) {
            searchTSbyRoleAndSupers(begin.get(i).getRole(), RolesProcessed);
        }
    }

    /** check if individual connected to something with RELATED statement */
    @PortedFrom(file = "tIndividual.h", name = "isRelated")
    private boolean isRelated() {
        return !relatedIndex.isEmpty();
    }

    /** set individual related
     * 
     * @param p */
    @PortedFrom(file = "tIndividual.h", name = "addRelated")
    public void addRelated(Related p) {
        relatedIndex.add(p);
    }

    /** add all the related elements from the given P
     * 
     * @param p */
    @PortedFrom(file = "tIndividual.h", name = "addRelated")
    public void addRelated(Individual p) {
        relatedIndex.addAll(p.relatedIndex);
    }

    // related map access
    /** @param R
     * @return true if has cache for related individuals via role R */
    @PortedFrom(file = "tIndividual.h", name = "hasRelatedCache")
    public boolean hasRelatedCache(Role R) {
        return pRelatedMap.containsKey(R);
    }

    /** @param R
     * @return set of individuals related to THIS via R */
    @PortedFrom(file = "tIndividual.h", name = "getRelatedCache")
    public List<Individual> getRelatedCache(Role R) {
        assert pRelatedMap.containsKey(R);
        return pRelatedMap.get(R);
    }

    /** set the cache of individuals related to THIS via R
     * 
     * @param R
     * @param v */
    @PortedFrom(file = "tIndividual.h", name = "setRelatedCache")
    public void setRelatedCache(Role R, List<Individual> v) {
        assert !pRelatedMap.containsKey(R);
        pRelatedMap.put(R, v);
    }

    // TIndividual RELATED-dependent method' implementation
    @PortedFrom(file = "tIndividual.h", name = "updateToldFromRelated")
    private void updateToldFromRelated() {
        Set<Role> RolesProcessed = new HashSet<Role>();
        updateTold(relatedIndex, RolesProcessed);
    }

    /** @return completion tree node */
    @Original
    public DlCompletionTree getNode() {
        return node;
    }

    /** @param node */
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
