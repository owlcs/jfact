package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.*;

public class Individual extends Concept {
    /** pointer to nominal node (works for singletons only) */
    private DlCompletionTree node;
    /** index for axioms <this,C>:R */
    private List<Related> relatedIndex = new ArrayList<Related>();
    /** map for the related individuals: Map[R]={i:R(this,i)} */
    private Map<Role, List<Individual>> pRelatedMap;

    public Individual(String name) {
        super(name);
        node = null;
        setSingleton(true);
        pRelatedMap = new HashMap<Role, List<Individual>>();
    }

    /** init told subsumers of the individual by it's description */
    @Override
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
    private <T extends Related> void updateTold(List<T> begin, Set<Role> RolesProcessed) {
        for (int i = 0; i < begin.size(); i++) {
            searchTSbyRoleAndSupers(begin.get(i).getRole(), RolesProcessed);
        }
    }

    /** check if individual connected to something with RELATED statement */
    private boolean isRelated() {
        return !relatedIndex.isEmpty();
    }

    /** set individual related */
    public void addRelated(Related p) {
        relatedIndex.add(p);
    }

    /** add all the related elements from the given P */
    public void addRelated(Individual p) {
        relatedIndex.addAll(p.relatedIndex);
    }

    // related map access
    /** @return true if has cache for related individuals via role R */
    public boolean hasRelatedCache(Role R) {
        return pRelatedMap.containsKey(R);
    }

    /** get set of individuals related to THIS via R */
    public List<Individual> getRelatedCache(Role R) {
        assert pRelatedMap.containsKey(R);
        return pRelatedMap.get(R);
    }

    /** set the cache of individuals related to THIS via R */
    public void setRelatedCache(Role R, List<Individual> v) {
        assert !pRelatedMap.containsKey(R);
        pRelatedMap.put(R, v);
    }

    // TIndividual RELATED-dependent method' implementation
    private void updateToldFromRelated() {
        Set<Role> RolesProcessed = new HashSet<Role>();
        updateTold(relatedIndex, RolesProcessed);
    }

    public DlCompletionTree getNode() {
        return node;
    }

    public void setNode(DlCompletionTree node) {
        this.node = node;
    }

    public List<Related> getRelatedIndex() {
        return relatedIndex;
    }
}
