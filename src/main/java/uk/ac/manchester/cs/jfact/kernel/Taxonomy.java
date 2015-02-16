package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.manchester.cs.jfact.kernel.actors.Actor;
import uk.ac.manchester.cs.jfact.kernel.actors.SupConceptActor;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import conformance.Original;
import conformance.PortedFrom;

/** taxonomy */
@PortedFrom(file = "Taxonomy.h", name = "Taxonomy")
public class Taxonomy implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** array of taxonomy verteces */
    @PortedFrom(file = "Taxonomy.h", name = "Graph")
    private final List<TaxonomyVertex> graph = new ArrayList<>();
    /** aux. vertex to be included to taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "Current")
    protected TaxonomyVertex current = new TaxonomyVertex();
    /** behaviour flag: if true, insert temporary vertex into taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "willInsertIntoTaxonomy")
    protected boolean willInsertIntoTaxonomy = true;
    /** vertex with parent Top and child Bot, represents the fresh entity */
    @PortedFrom(file = "Taxonomy.h", name = "FreshNode")
    protected final TaxonomyVertex FreshNode = new TaxonomyVertex();
    /** labeller for marking nodes as checked */
    @PortedFrom(file = "Taxonomy.h", name = "checkLabel")
    protected long visitedLabel = 1;
    @Original
    private final JFactReasonerConfiguration options;

    /** @return current */
    @PortedFrom(file = "Taxonomy.h", name = "getCurrent")
    public TaxonomyVertex getCurrent() {
        return current;
    }

    /**
     * set current to a given node
     * 
     * @param cur
     *        cur
     */
    @PortedFrom(file = "Taxonomy.h", name = "setCurrent")
    public void setCurrent(TaxonomyVertex cur) {
        current = cur;
    }

    /**
     * apply ACTOR to subgraph starting from NODE as defined by flags; this
     * version is intended to work only with SupConceptActor, which requires the
     * method to return as soon as the apply() method returns false
     * 
     * @param node
     *        node
     * @param actor
     *        actor
     * @param needCurrent
     *        needCurrent
     * @param onlyDirect
     *        onlyDirect
     * @param upDirection
     *        upDirection
     * @return false if actor does not apply
     */
    @PortedFrom(file = "Taxonomy.h", name = "getRelativesInfo")
    public boolean getRelativesInfo(TaxonomyVertex node, SupConceptActor actor,
            boolean needCurrent, boolean onlyDirect, boolean upDirection) {
        // XXX complexity here
        // if current node processed OK and there is no need to continue -- exit
        // this is the helper to the case like getDomain():
        // if there is a named concept that represent's a domain -- that's what
        // we need
        try {
            if (needCurrent) {
                if (!actor.apply(node)) {
                    return false;
                }
                if (onlyDirect) {
                    return true;
                }
            }
            Queue<Iterable<TaxonomyVertex>> queue = new LinkedList<>();
            queue.add(node.neigh(upDirection));
            while (queue.size() > 0) {
                Iterable<TaxonomyVertex> neigh = queue.remove();
                for (TaxonomyVertex _node : neigh) {
                    // recursive applicability checking
                    if (!isVisited(_node)) {
                        // label node as visited
                        setVisited(_node);
                        // if current node processed OK and there is no need to
                        // continue -- exit
                        // if node is NOT processed for some reasons -- go to
                        // another level
                        if (!actor.apply(_node) && onlyDirect) {
                            return false;
                        }
                        if (onlyDirect) {
                            continue;
                        }
                        // apply method to the proper neighbours with proper
                        // parameters
                        queue.add(_node.neigh(upDirection));
                    }
                }
            }
            return true;
        } finally {
            clearVisited();
        }
    }

    /**
     * apply ACTOR to subgraph starting from NODE as defined by flags;
     * 
     * @param node
     *        node
     * @param actor
     *        actor
     * @param needCurrent
     *        needCurrent
     * @param onlyDirect
     *        onlyDirect
     * @param upDirection
     *        upDirection
     */
    @PortedFrom(file = "Taxonomy.h", name = "getRelativesInfo")
    public void getRelativesInfo(TaxonomyVertex node, Actor actor,
            boolean needCurrent, boolean onlyDirect, boolean upDirection) {
        // XXX complexity here
        // if current node processed OK and there is no need to continue -- exit
        // this is the helper to the case like getDomain():
        // if there is a named concept that represent's a domain -- that's what
        // we need
        if (needCurrent && actor.apply(node) && onlyDirect) {
            return;
        }
        List<TaxonomyVertex> queue = new LinkedList<>();
        for (TaxonomyVertex v : node.neigh(upDirection)) {
            queue.add(v);
        }
        Set<TaxonomyVertex> pastBoundary = new HashSet<>();
        while (queue.size() > 0) {
            TaxonomyVertex _node = queue.remove(0);
            // recursive applicability checking
            if (!isVisited(_node)) {
                // label node as visited
                setVisited(_node);
                // if current node processed OK and there is no need to
                // continue -- exit
                // if node is NOT processed for some reasons -- go to
                // another level
                boolean applied = actor.apply(_node);
                if (applied && onlyDirect) {
                    for (TaxonomyVertex boundary : _node.neigh(upDirection)) {
                        setAllVisited(boundary, upDirection, pastBoundary);
                    }
                    continue;
                }
                // apply method to the proper neighbours with proper
                // parameters
                // only pick nodes that are policy applicable
                for (TaxonomyVertex v : _node.neigh(upDirection)) {
                    if (actor.applicable(v) || !onlyDirect) {
                        queue.add(v);
                    }
                }
            }
        }
        actor.removePastBoundaries(pastBoundary);
        clearVisited();
    }

    /**
     * set node NODE as checked within taxonomy
     * 
     * @param node
     *        node
     */
    @PortedFrom(file = "Taxonomy.h", name = "setVisited")
    public void setVisited(TaxonomyVertex node) {
        node.setChecked(visitedLabel);
    }

    /**
     * @param node
     *        node to visit
     * @param direction
     *        up or down
     * @param pastBoundary
     *        set of vertexes past the boundary
     */
    public void setAllVisited(TaxonomyVertex node, boolean direction,
            Set<TaxonomyVertex> pastBoundary) {
        pastBoundary.add(node);
        setVisited(node);
        for (TaxonomyVertex v : node.neigh(direction)) {
            setVisited(v);
            setAllVisited(v, direction, pastBoundary);
        }
    }

    /**
     * @param node
     *        node
     * @return check whether NODE is checked within taxonomy
     */
    @PortedFrom(file = "Taxonomy.h", name = "isVisited")
    public boolean isVisited(TaxonomyVertex node) {
        return node.isChecked(visitedLabel);
    }

    /** clear the CHECKED label from all the taxonomy vertex */
    @PortedFrom(file = "Taxonomy.h", name = "clearCheckedLabel")
    protected void clearVisited() {
        visitedLabel++;
    }

    /**
     * @param pTop
     *        pTop
     * @param pBottom
     *        pBottom
     * @param c
     *        c
     */
    public Taxonomy(ClassifiableEntry pTop, ClassifiableEntry pBottom,
            JFactReasonerConfiguration c) {
        options = c;
        graph.add(new TaxonomyVertex(pBottom));
        graph.add(new TaxonomyVertex(pTop));
        // set up fresh node
        FreshNode.addNeighbour(true, getTopVertex());
        FreshNode.addNeighbour(false, getBottomVertex());
    }

    /** @return reasoner configuration */
    public JFactReasonerConfiguration getOptions() {
        return options;
    }

    /** @return TOP of taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "getTopVertex")
    public TaxonomyVertex getTopVertex() {
        return graph.get(1);
    }

    /** @return BOTTOM of taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "getBottomVertex")
    public TaxonomyVertex getBottomVertex() {
        return graph.get(0);
    }

    /**
     * @param e
     *        e
     * @return node for fresh entity E
     */
    @PortedFrom(file = "Taxonomy.h", name = "getFreshVertex")
    public TaxonomyVertex getFreshVertex(ClassifiableEntry e) {
        FreshNode.setSample(e, false);
        return FreshNode;
    }

    @Override
    public String toString() {
        StringBuilder o = new StringBuilder();
        o.append("All entries are in format:\n\"entry\" {n: parent_1 ... parent_n} {m: child_1 child_m}\n\n");
        TreeSet<TaxonomyVertex> sorted = new TreeSet<>(
                new Comparator<TaxonomyVertex>() {

                    @Override
                    public int compare(TaxonomyVertex o1, TaxonomyVertex o2) {
                        return o1.getPrimer().getName()
                                .compareTo(o2.getPrimer().getName());
                    }
                });
        sorted.addAll(graph.subList(1, graph.size()));
        for (TaxonomyVertex p : sorted) {
            o.append(p);
        }
        o.append(getBottomVertex());
        return o.toString();
    }

    /**
     * remove node from the taxonomy; assume no references to the node
     * 
     * @param node
     *        node
     */
    @PortedFrom(file = "Taxonomy.h", name = "removeNode")
    public void removeNode(TaxonomyVertex node) {
        node.setInUse(false);
    }

    /**
     * @return true if taxonomy works in a query mode (no need to insert query
     *         vertex)
     */
    @PortedFrom(file = "Taxonomy.h", name = "queryMode")
    public boolean queryMode() {
        return !willInsertIntoTaxonomy;
    }

    // flags interface
    /** call this method after taxonomy is built */
    @PortedFrom(file = "Taxonomy.h", name = "finalise")
    public void finalise() {
        // create links from leaf concepts to bottom
        boolean upDirection = false;
        // TODO maybe useful to index Graph
        for (int i = 1; i < graph.size(); i++) {
            TaxonomyVertex p = graph.get(i);
            if (p.noNeighbours(upDirection)) {
                p.addNeighbour(upDirection, getBottomVertex());
                getBottomVertex().addNeighbour(!upDirection, p);
            }
        }
        willInsertIntoTaxonomy = false;
        // after finalisation one shouldn't add
        // new entries to taxonomy
    }

    /** unlink the bottom from the taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "deFinalise")
    public void deFinalise() {
        boolean upDirection = true;
        TaxonomyVertex bot = getBottomVertex();
        for (TaxonomyVertex p : bot.neigh(upDirection)) {
            p.removeLink(!upDirection, bot);
        }
        bot.clearLinks(upDirection);
        willInsertIntoTaxonomy = true;  // it's possible again to add entries
    }

    /**
     * @param syn
     *        syn
     */
    @PortedFrom(file = "Taxonomy.h", name = "addCurrentToSynonym")
    public void addCurrentToSynonym(TaxonomyVertex syn) {
        ClassifiableEntry currentEntry = current.getPrimer();
        if (queryMode()) {
            // no need to insert; just mark SYN as a host to curEntry
            syn.setVertexAsHost(currentEntry);
        } else {
            syn.addSynonym(currentEntry);
            options.getLog().print("\nTAX:set ", currentEntry.getName(),
                    " equal ", syn.getPrimer().getName());
        }
    }

    /** insert current node either directly or as a synonym */
    @PortedFrom(file = "Taxonomy.h", name = "finishCurrentNode")
    public void finishCurrentNode() {
        TaxonomyVertex syn = current.getSynonymNode();
        if (syn != null) {
            addCurrentToSynonym(syn);
        } else {
            // put curEntry as a representative of Current
            if (!queryMode()) {
                // insert node into taxonomy
                current.incorporate(options);
                graph.add(current);
                // we used the Current so need to create a new one
                current = new TaxonomyVertex();
            }
        }
    }

    // ------------------------------------------------------------------------------
    // -- classification support
    // ------------------------------------------------------------------------------
    /** @return true if current entry is a synonym of an already classified one */
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "processSynonym")
    protected boolean processSynonym() {
        ClassifiableEntry currentEntry = current.getPrimer();
        ClassifiableEntry syn = ClassifiableEntry.resolveSynonym(currentEntry);
        if (syn.equals(currentEntry)) {
            return false;
        }
        // assert willInsertIntoTaxonomy;
        if (syn.isClassified()) {
            addCurrentToSynonym(syn.getTaxVertex());
            return true;
        }
        return false;
    }

    /**
     * 
     */
    public void clearLabels() {
        visitedLabel++;
    }
}
