package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import conformance.Original;
import conformance.PortedFrom;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import uk.ac.manchester.cs.jfact.helpers.ArrayIntMap;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.kernel.state.SaveState;

/** Completion graph label */
@PortedFrom(file = "CGLabel.h", name = "CGLabel")
public class CGLabel implements Serializable {

    @Original private static int idcounter = 0;
    /** all simple concepts, labelled a node */
    @PortedFrom(file = "CGLabel.h", name = "scLabel") private final CWDArray scLabel;
    /** all complex concepts (ie, FORALL, GE), labelled a node */
    @PortedFrom(file = "CGLabel.h", name = "ccLabel") private final CWDArray ccLabel;
    @Original private final int id;
    @Original private TIntSet lesserIndex = new TIntHashSet();
    @Original private final List<CGLabel> lesserEqualsList = new ArrayList<>();

    /**
     * Default constructor.
     * 
     * @param config
     *        config object
     */
    public CGLabel(JFactReasonerConfiguration config) {
        // init label with reasonable size
        // XXX size might need tuning
        scLabel = new CWDArray(config, 8);
        ccLabel = new CWDArray(config, 4);
        id = getnewId();
    }

    @Original
    private static int getnewId() {
        return idcounter++;
    }

    /** @return simple concepts list */
    @Original
    public List<ConceptWDep> getSimpleConcepts() {
        return scLabel.getBase();
    }

    /** @return complext concepts list */
    @Original
    public List<ConceptWDep> getComplexConcepts() {
        return ccLabel.getBase();
    }

    /** @return simple concepts map */
    @Original
    public ArrayIntMap getSimpleConceptsMap() {
        return scLabel.getContainedConcepts();
    }

    /** @return complex concepts map */
    @Original
    public ArrayIntMap getComplexConceptsMap() {
        return ccLabel.getContainedConcepts();
    }

    /**
     * @param complex
     *        true if complex concepts sought
     * @return label associated with the concepts defined by TAG
     */
    @PortedFrom(file = "CGLabel.h", name = "getLabel")
    public CWDArray getLabel(boolean complex) {
        return complex ? ccLabel : scLabel;
    }

    /**
     * index p by tag, clear caches
     * 
     * @param complex
     *        true if complex concepts sought
     * @param p
     *        p
     */
    @Original
    public void add(boolean complex, ConceptWDep p) {
        getLabel(complex).privateAdd(p);
        clearMyCache();
    }

    @Original
    protected void clearMyCache() {
        lesserEqualsList.clear();
        lesserIndex.clear();
    }

    @Original
    protected void clearOthersCache() {
        lesserEqualsList.forEach(c -> c.lesserEqualsList.remove(this));
        lesserEqualsList.forEach(c -> c.lesserIndex.remove(id));
    }

    /**
     * @param p
     *        p
     * @return true if node is labelled by complex concept P
     */
    @PortedFrom(file = "CGLabel.h", name = "containsCC")
    public boolean containsCC(int p) {
        return ccLabel.contains(p);
    }

    @Override
    public int hashCode() {
        return id;
    }

    /**
     * @param label
     *        label
     * @return true if this label is less or equal than label
     */
    @PortedFrom(file = "CGLabel.h", name = "<=")
    public boolean lesserequal(CGLabel label) {
        if (this == label) {
            return true;
        }
        if (lesserIndex.contains(label.id)) {
            return true;
        }
        boolean toReturn = scLabel.lesserequal(label.scLabel) && ccLabel.lesserequal(label.ccLabel);
        if (toReturn) {
            lesserEqualsList.add(label);
            lesserIndex.add(label.id);
        }
        return toReturn;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof CGLabel) {
            CGLabel obj2 = (CGLabel) obj;
            return scLabel.equals(obj2.scLabel) && ccLabel.equals(obj2.ccLabel);
        }
        return false;
    }

    /**
     * @param ss
     *        save label using given SS
     */
    @PortedFrom(file = "CGLabel.h", name = "save")
    public void save(SaveState ss) {
        ss.setSc(scLabel.save());
        ss.setCc(ccLabel.save());
    }

    /**
     * restore label to given LEVEL using given SS
     * 
     * @param ss
     *        ss
     * @param level
     *        level
     */
    @PortedFrom(file = "CGLabel.h", name = "restore")
    public void restore(SaveState ss, int level) {
        scLabel.restore(ss.getSc(), level);
        ccLabel.restore(ss.getCc(), level);
        clearOthersCache();
    }

    @Override
    public String toString() {
        return scLabel.toString() + ccLabel.toString();
    }

    /**
     * init the node - this is not in the constructor because objecs will be
     * reused
     */
    @PortedFrom(file = "CGLabel.h", name = "init")
    public void init() {
        clearOthersCache();
        clearMyCache();
        scLabel.init();
        ccLabel.init();
    }

    /**
     * @param p
     *        p
     * @return true if p index is contained in simple or complex concepts.
     */
    @PortedFrom(file = "CGLabel.h", name = "contains")
    public boolean contains(int p) {
        assert isCorrect(p);
        if (p == BP_TOP) {
            return true;
        }
        if (p == BP_BOTTOM) {
            return false;
        }
        return scLabel.contains(p) || ccLabel.contains(p);
    }

    /**
     * @param bp
     *        bp
     * @return concept with index bp
     */
    @PortedFrom(file = "CGLabel.h", name = "getConcept")
    public ConceptWDep getConceptWithBP(int bp) {
        ConceptWDep toReturn = getSCConceptWithBP(bp);
        if (toReturn != null) {
            return toReturn;
        }
        return getCCConceptWithBP(bp);
    }

    /**
     * @param bp
     *        bp
     * @return concept with index bp
     */
    @Nullable
    @PortedFrom(file = "CGLabel.h", name = "getConcept")
    public ConceptWDep getSCConceptWithBP(int bp) {
        return scLabel.getConceptWithBP(bp);
    }

    /**
     * @param bp
     *        bp
     * @return concept with index bp
     */
    @Nullable
    @PortedFrom(file = "CGLabel.h", name = "getConcept")
    public ConceptWDep getCCConceptWithBP(int bp) {
        return ccLabel.getConceptWithBP(bp);
    }
}
