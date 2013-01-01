package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.cs.jfact.helpers.ArrayIntMap;
import uk.ac.manchester.cs.jfact.kernel.state.SaveState;
import conformance.Original;
import conformance.PortedFrom;

@PortedFrom(file = "CGLabel.h", name = "CGLabel")
public class CGLabel {
    @Original
    private static int idcounter = 0;

    @Original
    private static int getnewId() {
        return idcounter++;
    }

    /** all simple concepts, labelled a node */
    @PortedFrom(file = "CGLabel.h", name = "scLabel")
    private CWDArray scLabel;
    /** all complex concepts (ie, FORALL, GE), labelled a node */
    @PortedFrom(file = "CGLabel.h", name = "ccLabel")
    private CWDArray ccLabel;
    @Original
    private int id;

    public CGLabel() {
        scLabel = new CWDArray();
        ccLabel = new CWDArray();
        id = getnewId();
    }

    @Original
    public List<ConceptWDep> get_sc() {
        return scLabel.getBase();
    }

    @Original
    public List<ConceptWDep> get_cc() {
        return ccLabel.getBase();
    }

    @Original
    public ArrayIntMap get_sc_concepts() {
        return scLabel.getContainedConcepts();
    }

    @Original
    public ArrayIntMap get_cc_concepts() {
        return ccLabel.getContainedConcepts();
    }

    /** get (RW) label associated with the concepts defined by TAG */
    @PortedFrom(file = "CGLabel.h", name = "getLabel")
    public CWDArray getLabel(DagTag tag) {
        return tag.isComplexConcept() ? ccLabel : scLabel;
    }

    @Original
    public void add(DagTag tag, ConceptWDep p) {
        getLabel(tag).private_add(p);
        clearMyCache();
    }

    @Original
    protected void clearMyCache() {
        lesserEquals.clear();
    }

    @Original
    protected void clearOthersCache() {
        for (CGLabel c : lesserEquals) {
            c.lesserEquals.remove(this);
        }
    }

    /** check whether node is labelled by complex concept P */
    @PortedFrom(file = "CGLabel.h", name = "containsCC")
    public boolean containsCC(int p) {
        return ccLabel.contains(p);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Original
    private Set<CGLabel> lesserEquals = Collections
            .newSetFromMap(new IdentityHashMap<CGLabel, Boolean>());

    @PortedFrom(file = "CGLabel.h", name = "<=")
    public boolean lesserequal(CGLabel label) {
        if (this == label) {
            return true;
        }
        if (lesserEquals.contains(label)) {
            return true;
        }
        boolean toReturn = scLabel.lesserequal(label.scLabel)
                && ccLabel.lesserequal(label.ccLabel);
        if (toReturn) {
            lesserEquals.add(label);
        }
        return toReturn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof CGLabel) {
            CGLabel obj2 = (CGLabel) obj;
            boolean toReturn = scLabel.equals(obj2.scLabel)
                    && ccLabel.equals(obj2.ccLabel);
            return toReturn;
        }
        return false;
    }

    /** save label using given SS */
    @PortedFrom(file = "CGLabel.h", name = "save")
    public void save(SaveState ss) {
        ss.setSc(scLabel.save());
        ss.setCc(ccLabel.save());
    }

    /** restore label to given LEVEL using given SS */
    @PortedFrom(file = "CGLabel.h", name = "restore")
    public void restore(SaveState ss, int level) {
        scLabel.restore(ss.getSc(), level);
        ccLabel.restore(ss.getCc(), level);
        // _clearCache();
        clearOthersCache();
    }

    @Override
    public String toString() {
        return scLabel.toString() + ccLabel.toString();
    }

    @PortedFrom(file = "CGLabel.h", name = "init")
    public void init() {
        clearOthersCache();
        clearMyCache();
        scLabel.init();
        ccLabel.init();
    }

    @PortedFrom(file = "CGLabel.h", name = "contains")
    public boolean contains(int p) {
        assert isCorrect(p);
        if (p == bpTOP) {
            return true;
        }
        if (p == bpBOTTOM) {
            return false;
        }
        boolean b = scLabel.contains(p) || ccLabel.contains(p);
        return b;
    }

    @PortedFrom(file = "CGLabel.h", name = "getConcept")
    public ConceptWDep getConceptWithBP(int bp) {
        ConceptWDep toReturn = scLabel.getConceptWithBP(bp);
        if (toReturn != null) {
            return toReturn;
        }
        toReturn = ccLabel.getConceptWithBP(bp);
        return toReturn;
    }

    @Original
    public int baseSize() {
        return ccLabel.size() + scLabel.size();
    }

    @Original
    public int getId() {
        return id;
    }
}
