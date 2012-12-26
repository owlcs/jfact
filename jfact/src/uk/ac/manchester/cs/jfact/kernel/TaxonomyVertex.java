package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.*;

import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import conformance.PortedFrom;

@PortedFrom(file = "taxVertex.h", name = "TaxonomyVertex")
public class TaxonomyVertex {
    // TODO check if they need to be list
    /** immediate parents and children */
    private List<TaxonomyVertex> linksParent = new ArrayList<TaxonomyVertex>();
    private List<TaxonomyVertex> linksChild = new ArrayList<TaxonomyVertex>();
    /** entry corresponding to current tax vertex */
    private ClassifiableEntry sample = null;
    // TODO this can be a set, but there is no advantage
    /** synonyms of the sample entry */
    private Set<ClassifiableEntry> synonyms = new LinkedHashSet<ClassifiableEntry>();
    // labels for different purposes. all for 2 directions: top-down and
    // bottom-up search
    /** flag if given vertex was checked; connected with checkLab */
    private long checked;
    /** flag if given vertex has value; connected with valuedLab */
    private long isValued;
    /** number of common parents of a node */
    private int common;
    /** satisfiability value of a valued vertex */
    private boolean checkValue;

    /** mark vertex as the one corresponding to a given ENTRY */
@PortedFrom(file="taxVertex.h",name="setHostVertex")
    public void setHostVertex(ClassifiableEntry entry) {
        entry.setTaxVertex(this);
    }

    /** set sample to ENTRY */
@PortedFrom(file="taxVertex.h",name="setSample")
    public void setSample(ClassifiableEntry entry, boolean linkBack) {
        sample = entry;
        if (linkBack) {
            entry.setTaxVertex(this);
        }
    }

    /** indirect RW access to Links */
@PortedFrom(file="taxVertex.h",name="neigh")
    public List<TaxonomyVertex> neigh(boolean upDirection) {
        return upDirection ? linksParent : linksChild;
    }

    // checked part
@PortedFrom(file="taxVertex.h",name="isChecked")
    public boolean isChecked(long checkLab) {
        return checkLab == checked;
    }

@PortedFrom(file="taxVertex.h",name="setChecked")
    public void setChecked(long checkLab) {
        checked = checkLab;
    }

    // value part
@PortedFrom(file="taxVertex.h",name="isValued")
    public boolean isValued(long valueLab) {
        return valueLab == isValued;
    }

@PortedFrom(file="taxVertex.h",name="getValue")
    public boolean getValue() {
        return checkValue;
    }

@PortedFrom(file="taxVertex.h",name="setValued")
    public boolean setValued(boolean val, long valueLab) {
        isValued = valueLab;
        checkValue = val;
        return val;
    }

    // common part
@PortedFrom(file="taxVertex.h",name="isCommon")
    public boolean isCommon() {
        return common != 0;
    }

@PortedFrom(file="taxVertex.h",name="setCommon")
    public void setCommon() {
        ++common;
    }

@PortedFrom(file="taxVertex.h",name="clearCommon")
    public void clearCommon() {
        common = 0;
    }

    /** keep COMMON flag iff both flags are set; @return true if it is the case */
@PortedFrom(file="taxVertex.h",name="correctCommon")
    public boolean correctCommon(int n) {
        if (common == n) {
            return true;
        }
        common = 0;
        return false;
    }

    /** put initial values on the flags */
@PortedFrom(file="taxVertex.h",name="initFlags")
    private void initFlags() {
        checked = 0;
        isValued = 0;
        common = 0;
    }

    // get info about taxonomy structure
@PortedFrom(file="taxVertex.h",name="begin_syn")
    public Set<ClassifiableEntry> begin_syn() {
        return synonyms;
    }

    public TaxonomyVertex() {
        initFlags();
    }

    /** init c'tor; use it only for Top/Bot initialisations */
    public TaxonomyVertex(ClassifiableEntry p) {
        initFlags();
        setSample(p, true);
    }

    /** add P as a synonym to curent vertex */
@PortedFrom(file="taxVertex.h",name="addSynonym")
    public void addSynonym(ClassifiableEntry p) {
        synonyms.add(p);
        p.setTaxVertex(this);
    }

    /** clears the vertex */
@PortedFrom(file="taxVertex.h",name="clear")
    public void clear() {
        linksParent.clear();
        linksChild.clear();
        sample = null;
        initFlags();
    }

@PortedFrom(file="taxVertex.h",name="getPrimer")
    public ClassifiableEntry getPrimer() {
        return sample;
    }

    /** add link in given direction to vertex */
@PortedFrom(file="taxVertex.h",name="addNeighbour")
    public void addNeighbour(boolean upDirection, TaxonomyVertex p) {
        if (p == null) {
            throw new IllegalArgumentException("p cannot be null");
        }
        neigh(upDirection).add(p);
    }

    /** check if vertex has no neighbours in given direction */
@PortedFrom(file="taxVertex.h",name="noNeighbours")
    public boolean noNeighbours(boolean upDirection) {
        return neigh(upDirection).isEmpty();
    }

    /** @return v if node represents a synonym (v=Up[i]==Down[j]); @return null
     *         otherwise */
@PortedFrom(file="taxVertex.h",name="getSynonymNode")
    public TaxonomyVertex getSynonymNode() {
        // try to find Vertex such that Vertex\in Up and Vertex\in Down
        for (TaxonomyVertex q : neigh(true)) {
            for (TaxonomyVertex r : neigh(false)) {
                if (q.equals(r)) {
                    return q;
                }
            }
        }
        return null;
    }

    /** remove latest link (usually to the BOTTOM node) */
@PortedFrom(file="taxVertex.h",name="removeLastLink")
    public void removeLastLink(boolean upDirection) {
        Helper.resize(neigh(upDirection), neigh(upDirection).size() - 1);
    }

    /** clear all links in a given direction */
@PortedFrom(file="taxVertex.h",name="clearLinks")
    public void clearLinks(boolean upDirection) {
        neigh(upDirection).clear();
    }

@PortedFrom(file="taxVertex.h",name="removeLink")
    public boolean removeLink(boolean upDirection, TaxonomyVertex p) {
        List<TaxonomyVertex> begin = neigh(upDirection);
        int index = begin.indexOf(p);
        if (index > -1) {
            begin.set(index, begin.get(begin.size() - 1));
            removeLastLink(upDirection);
            return true;
        }
        return false;
    }

    // TODO does not work with synonyms
@PortedFrom(file="taxVertex.h",name="incorporate")
    public void incorporate(JFactReasonerConfiguration c) {
        // setup links
        // TODO doublecheck
        List<TaxonomyVertex> falselist = new ArrayList<TaxonomyVertex>(neigh(false));
        List<TaxonomyVertex> truelist = new ArrayList<TaxonomyVertex>(neigh(true));
        for (TaxonomyVertex d : falselist) {
            for (TaxonomyVertex u : truelist) {
                if (d.removeLink(true, u)) {
                    u.removeLink(false, d);
                }
            }
            d.addNeighbour(true, this);
        }
        for (TaxonomyVertex u : truelist) {
            u.addNeighbour(false, this);
        }
        if (c.isLoggingActive()) {
            LogAdapter logAdapter = c.getLog();
            logAdapter.printTemplate(Templates.INCORPORATE, sample.getName());
            for (int i = 0; i < truelist.size(); i++) {
                if (i > 0) {
                    logAdapter.print(",");
                }
                logAdapter.print(truelist.get(i).sample.getName());
            }
            logAdapter.print("} and down = {");
            for (int i = 0; i < falselist.size(); i++) {
                if (i > 0) {
                    logAdapter.print(",");
                }
                logAdapter.print(falselist.get(i).sample.getName());
            }
            logAdapter.print("}");
        }
    }

    /** merge NODE which is independent to THIS */
@PortedFrom(file="taxVertex.h",name="mergeIndepNode")
    void mergeIndepNode(TaxonomyVertex node, Set<TaxonomyVertex> excludes,
            ClassifiableEntry curEntry) {
        // copy synonyms here
        if (!node.getPrimer().equals(curEntry)) {
            addSynonym(node.getPrimer());
        }
        for (ClassifiableEntry q : node.begin_syn()) {
            addSynonym(q);
        }
        boolean upDirection = true;
        for (TaxonomyVertex p : node.neigh(upDirection)) {
            if (!excludes.contains(p)) {
                addNeighbour(upDirection, p);
            }
            p.removeLink(!upDirection, node);
        }
        upDirection = false;
        for (TaxonomyVertex p : node.neigh(upDirection)) {
            if (!excludes.contains(p)) {
                addNeighbour(upDirection, p);
            }
            p.removeLink(!upDirection, node);
        }
    }

@PortedFrom(file="taxVertex.h",name="printSynonyms")
    public String printSynonyms() {
        assert sample != null;
        StringBuilder o = new StringBuilder();
        if (synonyms.isEmpty()) {
            o.append("\"");
            o.append(sample.getName());
            o.append("\"");
        } else {
            o.append("(\"");
            o.append(sample.getName());
            for (ClassifiableEntry q : begin_syn()) {
                o.append("\"=\"");
                o.append(q.getName());
            }
            o.append("\")");
        }
        return o.toString();
    }

@PortedFrom(file="taxVertex.h",name="printNeighbours")
    public String printNeighbours(boolean upDirection) {
        StringBuilder o = new StringBuilder();
        o.append(" {");
        o.append(neigh(upDirection).size());
        o.append(":");
        TreeSet<TaxonomyVertex> sorted = new TreeSet<TaxonomyVertex>(
                new Comparator<TaxonomyVertex>() {
                    @Override
                    public int compare(TaxonomyVertex o1, TaxonomyVertex o2) {
                        return o1.getPrimer().getName()
                                .compareTo(o2.getPrimer().getName());
                    }
                });
        sorted.addAll(neigh(upDirection));
        for (TaxonomyVertex p : sorted) {
            o.append(" \"");
            o.append(p.sample.getName());
            o.append("\"");
        }
        o.append("}");
        return o.toString();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(printSynonyms());
        b.append(printNeighbours(true));
        b.append(printNeighbours(false));
        b.append("\n");
        return b.toString();
    }
}
