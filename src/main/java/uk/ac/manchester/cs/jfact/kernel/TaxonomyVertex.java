package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static java.util.stream.Collectors.joining;
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static uk.ac.manchester.cs.jfact.helpers.Helper.elementFromIntersection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/** taxonomy vertex */
@PortedFrom(file = "taxVertex.h", name = "TaxonomyVertex")
public class TaxonomyVertex implements Serializable {

    /** immediate parents and children */
    @PortedFrom(file = "taxVertex.h", name = "Links") private LinkedHashSet<TaxonomyVertex> linksParent = new LinkedHashSet<>();
    @PortedFrom(file = "taxVertex.h", name = "Links") private LinkedHashSet<TaxonomyVertex> linksChild = new LinkedHashSet<>();
    /** entry corresponding to current tax vertex */
    @PortedFrom(file = "taxVertex.h", name = "sample") private ClassifiableEntry sample = null;
    /** synonyms of the sample entry */
    @PortedFrom(file = "taxVertex.h", name = "synonyms") private Set<ClassifiableEntry> synonyms = new LinkedHashSet<>();
    // labels for different purposes. all for 2 directions: top-down and
    // bottom-up search
    /** flag if given vertex was checked; connected with checkLab */
    @PortedFrom(file = "taxVertex.h", name = "checked") private long checked;
    /** flag if given vertex has value; connected with valuedLab */
    @Original private long isValued;
    /** number of common parents of a node */
    @PortedFrom(file = "taxVertex.h", name = "common") private int common;
    /** satisfiability value of a valued vertex */
    @PortedFrom(file = "taxVertex.h", name = "checkValue") private boolean checkValue;
    /** flag to check whether the vertex is in use */
    @PortedFrom(file = "taxVertex.h", name = "inUse") private boolean inUse = true;

    /** Default constructor. */
    public TaxonomyVertex() {
        initFlags();
    }

    /**
     * init c'tor; use it only for Top/Bot initialisations
     * 
     * @param p
     *        p
     */
    public TaxonomyVertex(ClassifiableEntry p) {
        initFlags();
        setSample(p, true);
    }

    /**
     * mark vertex as the one corresponding to a given ENTRY
     * 
     * @param entry
     *        entry
     */
    @PortedFrom(file = "taxVertex.h", name = "setVertexAsHost")
    public void setVertexAsHost(ClassifiableEntry entry) {
        entry.setTaxVertex(this);
    }

    /**
     * set sample to ENTRY
     * 
     * @param entry
     *        entry
     * @param linkBack
     *        linkBack
     */
    @PortedFrom(file = "taxVertex.h", name = "setSample")
    public void setSample(ClassifiableEntry entry, boolean linkBack) {
        sample = entry;
        if (linkBack) {
            setVertexAsHost(entry);
        }
    }

    /**
     * @param upDirection
     *        upDirection
     * @return Links
     */
    @PortedFrom(file = "taxVertex.h", name = "neigh")
    public Stream<TaxonomyVertex> neigh(boolean upDirection) {
        return upDirection ? linksParent.stream() : linksChild.stream();
    }

    // checked part
    /**
     * @param checkLab
     *        checkLab
     * @return true if checked
     */
    @PortedFrom(file = "taxVertex.h", name = "isChecked")
    public boolean isChecked(long checkLab) {
        return checkLab == checked;
    }

    /**
     * @param checkLab
     *        checkLab
     */
    @PortedFrom(file = "taxVertex.h", name = "setChecked")
    public void setChecked(long checkLab) {
        checked = checkLab;
    }

    // value part
    /**
     * @param valueLab
     *        valueLab
     * @return true if values
     */
    @PortedFrom(file = "taxVertex.h", name = "isValued")
    public boolean isValued(long valueLab) {
        return valueLab == isValued;
    }

    /** @return value */
    @PortedFrom(file = "taxVertex.h", name = "getValue")
    public boolean getValue() {
        return checkValue;
    }

    /**
     * @param val
     *        val
     * @param valueLab
     *        valueLab
     * @return val
     */
    @PortedFrom(file = "taxVertex.h", name = "setValued")
    public boolean setValued(boolean val, long valueLab) {
        isValued = valueLab;
        checkValue = val;
        return val;
    }

    // common part
    /** @return true if common not 0 */
    @PortedFrom(file = "taxVertex.h", name = "isCommon")
    public boolean isCommon() {
        return common != 0;
    }

    /** increment common */
    @PortedFrom(file = "taxVertex.h", name = "setCommon")
    public void setCommon() {
        ++common;
    }

    /** zero common */
    @PortedFrom(file = "taxVertex.h", name = "clearCommon")
    public void clearCommon() {
        common = 0;
    }

    /**
     * keep COMMON flag iff both flags are set;
     * 
     * @param n
     *        n
     * @return true if it is the case
     */
    @PortedFrom(file = "taxVertex.h", name = "correctCommon")
    public boolean correctCommon(int n) {
        if (common == n) {
            return true;
        }
        common = 0;
        return false;
    }

    /** put initial values on the flags */
    @PortedFrom(file = "taxVertex.h", name = "initFlags")
    private void initFlags() {
        checked = 0;
        isValued = 0;
        common = 0;
    }

    // get info about taxonomy structure
    /** @return synonyms */
    @PortedFrom(file = "taxVertex.h", name = "begin_syn")
    public Stream<ClassifiableEntry> synonyms() {
        return synonyms.stream();
    }

    /**
     * add P as a synonym to curent vertex
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "taxVertex.h", name = "addSynonym")
    public void addSynonym(ClassifiableEntry p) {
        synonyms.add(p);
        p.setTaxVertex(this);
    }

    /** clears the vertex */
    @PortedFrom(file = "taxVertex.h", name = "clear")
    public void clear() {
        linksParent.clear();
        linksChild.clear();
        sample = null;
        initFlags();
    }

    /** @return primer */
    @PortedFrom(file = "taxVertex.h", name = "getPrimer")
    public ClassifiableEntry getPrimer() {
        return sample;
    }

    /**
     * add link in given direction to vertex
     * 
     * @param upDirection
     *        upDirection
     * @param p
     *        p
     */
    @PortedFrom(file = "taxVertex.h", name = "addNeighbour")
    public void addNeighbour(boolean upDirection, TaxonomyVertex p) {
        checkNotNull(p, "p cannot be null");
        add(upDirection, p);
    }

    /**
     * @param direction
     *        parent or child direction
     * @param t
     *        vertex to add
     */
    public void add(boolean direction, TaxonomyVertex t) {
        if (direction) {
            linksParent.add(t);
        } else {
            linksChild.add(t);
        }
    }

    /**
     * @param upDirection
     *        upDirection
     * @return check if vertex has no neighbours in given direction
     */
    @PortedFrom(file = "taxVertex.h", name = "noNeighbours")
    public boolean noNeighbours(boolean upDirection) {
        if (upDirection) {
            return linksParent.isEmpty();
        } else {
            return linksChild.isEmpty();
        }
    }

    /**
     * @return v if node represents a synonym (v=Up[i]==Down[j]); null otherwise
     */
    @Nullable
    @PortedFrom(file = "taxVertex.h", name = "getSynonymNode")
    public TaxonomyVertex getSynonymNode() {
        // try to find Vertex such that Vertex\in Up and Vertex\in Down
        return elementFromIntersection(linksParent, linksChild).orElse(null);
    }

    /**
     * clear all links in a given direction
     * 
     * @param upDirection
     *        upDirection
     */
    @PortedFrom(file = "taxVertex.h", name = "clearLinks")
    public void clearLinks(boolean upDirection) {
        if (upDirection) {
            linksParent.clear();
        } else {
            linksChild.clear();
        }
    }

    /**
     * @param upDirection
     *        upDirection
     * @param p
     *        p
     * @return true if link removed
     */
    @PortedFrom(file = "taxVertex.h", name = "removeLink")
    public boolean removeLink(boolean upDirection, TaxonomyVertex p) {
        if (upDirection) {
            return linksParent.remove(p);
        }
        return linksChild.remove(p);
    }

    /**
     * @param c
     *        c
     */
    @PortedFrom(file = "taxVertex.h", name = "incorporate")
    public void incorporate(JFactReasonerConfiguration c) {
        // setup links
        for (TaxonomyVertex d : linksChild) {
            // remove all down links
            for (TaxonomyVertex u : linksParent) {
                if (d.removeLink(true, u)) {
                    u.removeLink(false, d);
                }
            }
            // add new link between v and current
            // safe in general case, crucial for incremental
            d.removeLink(true, this);
            d.addNeighbour(true, this);
        }
        // add new link between v and current
        neigh(true).forEach(u -> u.addNeighbour(false, this));
        if (c.isLoggingActive()) {
            LogAdapter logAdapter = c.getLog();
            logAdapter.printTemplate(Templates.INCORPORATE, sample.getIRI()).print(names(linksParent))
                .print("} and down = {").print(names(linksChild)).print("}");
        }
    }

    Iterable<IRI> names(Collection<TaxonomyVertex> l) {
        return asList(l.stream().map(t -> t.sample.getIRI()));
    }

    /**
     * remove one half of a given node from a graph
     * 
     * @param upDirection
     *        upDirection
     */
    @PortedFrom(file = "taxVertex.h", name = "removeLinks")
    public void removeLinks(boolean upDirection) {
        neigh(upDirection).forEach(p -> p.removeLink(!upDirection, this));
        clearLinks(upDirection);
    }

    /** remove given node from a graph */
    @PortedFrom(file = "taxVertex.h", name = "remove")
    public void remove() {
        removeLinks(true);
        removeLinks(false);
        setInUse(false);
    }

    /** @return true iff the node is in use */
    @PortedFrom(file = "taxVertex.h", name = "isInUse")
    public boolean isInUse() {
        return inUse;
    }

    /**
     * set the inUse value of the node
     * 
     * @param value
     *        value
     */
    @PortedFrom(file = "taxVertex.h", name = "setInUse")
    public void setInUse(boolean value) {
        inUse = value;
    }

    /**
     * merge NODE which is independent to THIS
     * 
     * @param node
     *        node
     * @param excludes
     *        excludes
     * @param curEntry
     *        curEntry
     */
    @PortedFrom(file = "taxVertex.h", name = "mergeIndepNode")
    public void mergeIndepNode(TaxonomyVertex node, Set<TaxonomyVertex> excludes, ClassifiableEntry curEntry) {
        // copy synonyms here
        if (!node.getPrimer().equals(curEntry)) {
            addSynonym(node.getPrimer());
        }
        node.synonyms().forEach(this::addSynonym);
        for (TaxonomyVertex p : linksParent) {
            if (!excludes.contains(p)) {
                addNeighbour(true, p);
            }
            p.removeLink(false, node);
        }
        for (TaxonomyVertex p : linksChild) {
            if (!excludes.contains(p)) {
                addNeighbour(false, p);
            }
            p.removeLink(true, node);
        }
    }

    /** @return synonyms debug print */
    @PortedFrom(file = "taxVertex.h", name = "printSynonyms")
    public String printSynonyms() {
        assert sample != null;
        StringBuilder o = new StringBuilder();
        if (synonyms.isEmpty()) {
            o.append('"').append(sample.getIRI()).append('"');
        } else {
            o.append("(\"").append(sample.getIRI());
            o.append(synonyms().map(ClassifiableEntry::getIRI).collect(joining("\"=\"")));
            o.append("\")");
        }
        return o.toString();
    }

    /**
     * @param list
     *        vertexes to print
     * @return neighbours debug print
     */
    @PortedFrom(file = "taxVertex.h", name = "printNeighbours")
    private static String printNeighbours(Collection<TaxonomyVertex> list) {
        StringBuilder o = new StringBuilder();
        o.append(" {").append(list.size()).append(':');
        List<TaxonomyVertex> l = new ArrayList<>(list);
        l.sort((o1, o2) -> o1.getPrimer().getIRI().compareTo(o2.getPrimer().getIRI()));
        l.forEach(p -> o.append(" \"").append(p.sample.getIRI()).append('"'));
        o.append('}');
        return o.toString();
    }

    @Override
    public String toString() {
        return printSynonyms() + printNeighbours(linksParent) + printNeighbours(linksChild) + '\n';
    }
}
