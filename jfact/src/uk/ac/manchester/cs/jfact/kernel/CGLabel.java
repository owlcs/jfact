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

public final class CGLabel {
    private static int idcounter = 0;

    private static int getnewId() {
        return idcounter++;
    }

    /** all simple concepts, labelled a node */
    private final CWDArray scLabel;
    /** all complex concepts (ie, FORALL, GE), labelled a node */
    private final CWDArray ccLabel;
    private final int id;

    public CGLabel() {
        scLabel = new CWDArray();
        ccLabel = new CWDArray();
        id = getnewId();
    }

    public List<ConceptWDep> get_sc() {
        return scLabel.getBase();
    }

    public List<ConceptWDep> get_cc() {
        return ccLabel.getBase();
    }

    public ArrayIntMap get_sc_concepts() {
        return scLabel.getContainedConcepts();
    }

    public ArrayIntMap get_cc_concepts() {
        return ccLabel.getContainedConcepts();
    }

    /** get (RW) label associated with the concepts defined by TAG */
    public CWDArray getLabel(final DagTag tag) {
        return tag.isComplexConcept() ? ccLabel : scLabel;
    }

    public void add(final DagTag tag, final ConceptWDep p) {
        getLabel(tag).private_add(p);
        clearMyCache();
    }

    protected final void clearMyCache() {
        lesserEquals.clear();
    }

    protected final void clearOthersCache() {
        for (CGLabel c : lesserEquals) {
            c.lesserEquals.remove(this);
        }
    }

    /** check whether node is labelled by complex concept P */
    public boolean containsCC(final int p) {
        return ccLabel.contains(p);
    }

    @Override
    public int hashCode() {
        return id;
    }

    private final Set<CGLabel> lesserEquals = Collections
            .newSetFromMap(new IdentityHashMap<CGLabel, Boolean>());

    public boolean lesserequal(final CGLabel label) {
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
    public boolean equals(final Object obj) {
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
    public void save(final SaveState ss) {
        ss.setSc(scLabel.save());
        ss.setCc(ccLabel.save());
    }

    /** restore label to given LEVEL using given SS */
    public final void restore(final SaveState ss, final int level) {
        scLabel.restore(ss.getSc(), level);
        ccLabel.restore(ss.getCc(), level);
        //_clearCache();
        clearOthersCache();
    }

    @Override
    public String toString() {
        return scLabel.toString() + ccLabel.toString();
    }

    public final void init() {
        clearOthersCache();
        clearMyCache();
        scLabel.init();
        ccLabel.init();
    }

    public boolean contains(final int p) {
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

    public ConceptWDep getConceptWithBP(final int bp) {
        ConceptWDep toReturn = scLabel.getConceptWithBP(bp);
        if (toReturn != null) {
            return toReturn;
        }
        toReturn = ccLabel.getConceptWithBP(bp);
        return toReturn;
    }

    public int baseSize() {
        return ccLabel.size() + scLabel.size();
    }

    public int getId() {
        return id;
    }
}
