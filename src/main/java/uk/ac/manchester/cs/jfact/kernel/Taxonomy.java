package uk.ac.manchester.cs.jfact.kernel;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.actors.Actor;
import uk.ac.manchester.cs.jfact.kernel.actors.SupConceptActor;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/** taxonomy */
@PortedFrom(file = "Taxonomy.h", name = "Taxonomy")
public class Taxonomy implements Serializable {

    /** array of taxonomy verteces */
    @PortedFrom(file = "Taxonomy.h", name = "Graph") private final List<TaxonomyVertex> graph = new ArrayList<>();
    /** aux. vertex to be included to taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "Current") protected TaxonomyVertex current = new TaxonomyVertex();
    /** behaviour flag: if true, insert temporary vertex into taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "willInsertIntoTaxonomy") protected boolean willInsertIntoTaxonomy = true;
    /** vertex with parent Top and child Bot, represents the fresh entity */
    @PortedFrom(file = "Taxonomy.h", name = "FreshNode") protected final TaxonomyVertex freshNode = new TaxonomyVertex();
    /** labeller for marking nodes as checked */
    @PortedFrom(file = "Taxonomy.h", name = "checkLabel") protected long visitedLabel = 1;
    @Original private final JFactReasonerConfiguration options;

    /**
     * @param pTop
     *        pTop
     * @param pBottom
     *        pBottom
     * @param c
     *        c
     */
    public Taxonomy(ClassifiableEntry pTop, ClassifiableEntry pBottom, JFactReasonerConfiguration c) {
        options = c;
        graph.add(new TaxonomyVertex(pBottom));
        graph.add(new TaxonomyVertex(pTop));
        // set up fresh node
        freshNode.addNeighbour(true, getTopVertex());
        freshNode.addNeighbour(false, getBottomVertex());
    }

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
    public boolean getRelativesInfo(TaxonomyVertex node, SupConceptActor actor, boolean needCurrent, boolean onlyDirect,
        boolean upDirection) {
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
            Queue<Stream<TaxonomyVertex>> queue = new LinkedList<>();
            queue.add(node.neigh(upDirection));
            while (!queue.isEmpty()) {
                for (TaxonomyVertex _node : asList(queue.remove())) {
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
    public void getRelativesInfo(TaxonomyVertex node, Actor actor, boolean needCurrent, boolean onlyDirect,
        boolean upDirection) {
        // XXX complexity here
        // if current node processed OK and there is no need to continue -- exit
        // this is the helper to the case like getDomain():
        // if there is a named concept that represent's a domain -- that's what
        // we need
        if (needCurrent && actor.apply(node) && onlyDirect) {
            return;
        }
        List<TaxonomyVertex> queue = new LinkedList<>();
        node.neigh(upDirection).forEach(queue::add);
        Set<TaxonomyVertex> pastBoundary = new HashSet<>();
        while (!queue.isEmpty()) {
            TaxonomyVertex nextNode = queue.remove(0);
            // recursive applicability checking
            if (!isVisited(nextNode)) {
                // label node as visited
                setVisited(nextNode);
                // if current node processed OK and there is no need to
                // continue -- exit
                // if node is NOT processed for some reasons -- go to
                // another level
                boolean applied = actor.apply(nextNode);
                if (applied && onlyDirect) {
                    nextNode.neigh(upDirection).forEach(boundary -> setAllVisited(boundary, upDirection, pastBoundary));
                    continue;
                }
                // apply method to the proper neighbours with proper
                // parameters
                // only pick nodes that are policy applicable
                nextNode.neigh(upDirection).filter(v -> actor.applicable(v) || !onlyDirect).forEach(queue::add);
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
    public void setAllVisited(TaxonomyVertex node, boolean direction, Set<TaxonomyVertex> pastBoundary) {
        pastBoundary.add(node);
        setVisited(node);
        node.neigh(direction).forEach(v -> {
            setVisited(v);
            setAllVisited(v, direction, pastBoundary);
        });
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
        freshNode.setSample(e, false);
        return freshNode;
    }

    @Override
    public String toString() {
        StringBuilder o = new StringBuilder();
        o.append("All entries are in format:\n\"entry\" {n: parent_1 ... parent_n} {m: child_1 child_m}\n\n");
        graph.stream().skip(1).sorted((o1, o2) -> o1.getPrimer().getIRI().compareTo(o2.getPrimer().getIRI())).forEach(
            o::append);
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
        graph.stream().skip(1).forEach(p -> {
            if (p.noNeighbours(false)) {
                p.addNeighbour(false, getBottomVertex());
                getBottomVertex().addNeighbour(true, p);
            }
        });
        willInsertIntoTaxonomy = false;
        // after finalisation one shouldn't add
        // new entries to taxonomy
    }

    /** unlink the bottom from the taxonomy */
    @PortedFrom(file = "Taxonomy.h", name = "deFinalise")
    public void deFinalise() {
        TaxonomyVertex bot = getBottomVertex();
        bot.neigh(true).forEach(p -> p.removeLink(false, bot));
        bot.clearLinks(true);
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
            options.getLog().print("\nTAX:set ", currentEntry.getIRI(), " equal ", syn.getPrimer().getIRI());
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
    /**
     * @return true if current entry is a synonym of an already classified one
     */
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "processSynonym")
    protected boolean processSynonym() {
        ClassifiableEntry currentEntry = current.getPrimer();
        ClassifiableEntry syn = ClassifiableEntry.resolveSynonym(currentEntry);
        if (syn.equals(currentEntry)) {
            return false;
        }
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
