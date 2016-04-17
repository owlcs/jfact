package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import org.semanticweb.owlapitools.decomposition.Signature;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;

/** taxonomy creator for DL */
@PortedFrom(file = "TaxonomyCreator.h", name = "TaxonomyCreator")
public class TaxonomyCreator implements Serializable {

    @PortedFrom(file = "TaxonomyCreator.h", name = "pTax") protected final Taxonomy pTax;
    @PortedFrom(file = "TaxonomyCreator.h", name = "Syns") protected final List<ClassifiableEntry> syns = new ArrayList<>();
    /** labeller for marking nodes with a label wrt classification */
    @PortedFrom(file = "TaxonomyCreator.h", name = "valueLabel") protected long valueLabel = 1;
    /** pointer to currently classified entry */
    @PortedFrom(file = "TaxonomyCreator.h", name = "curEntry") protected ClassifiableEntry curEntry = null;
    /** number of tested entryes */
    @PortedFrom(file = "TaxonomyCreator.h", name = "nEntries") protected int nEntries = 0;
    /** number of completely-defined entries */
    @PortedFrom(file = "TaxonomyCreator.h", name = "nCDEntries") protected long nCDEntries = 0;
    /**
     * optimisation flag: if entry is completely defined by it's told subsumers,
     * no other classification required
     */
    @PortedFrom(file = "TaxonomyCreator.h", name = "useCompletelyDefined") protected boolean useCompletelyDefined = false;
    /** session flag: shows the direction of the search */
    @PortedFrom(file = "TaxonomyCreator.h", name = "upDirection") protected boolean upDirection;
    /** stack for Taxonomy creation */
    @PortedFrom(file = "TaxonomyCreator.h", name = "waitStack") private final LinkedList<ClassifiableEntry> waitStack = new LinkedList<>();
    /** told subsumers corresponding to a given entry */
    @PortedFrom(file = "TaxonomyCreator.h", name = "ksStack") protected final LinkedList<KnownSubsumers> ksStack = new LinkedList<>();
    /** signature of a \bot-module corresponding to a given entry */
    @PortedFrom(file = "TaxonomyCreator.h", name = "sigStack") protected final LinkedList<Signature> sigStack = new LinkedList<>();

    /**
     * @param pTax2
     *        pTax2
     */
    public TaxonomyCreator(Taxonomy pTax2) {
        pTax = pTax2;
    }

    /**
     * initialise aux entry with given concept p
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "TaxonomyCreator.h", name = "setCurrentEntry")
    protected void setCurrentEntry(ClassifiableEntry p) {
        pTax.getCurrent().clear();
        pTax.getCurrent().setSample(p, true);
        pTax.getCurrent().add(true, pTax.getTopVertex());
        curEntry = p;
    }

    @PortedFrom(file = "TaxonomyCreator.h", name = "classifySynonym")
    protected boolean classifySynonym() {
        return pTax.processSynonym();
    }

    @PortedFrom(file = "TaxonomyCreator.cpp", name = "setToldSubsumers")
    private void setToldSubsumers() {
        Collection<ClassifiableEntry> top = ksStack.peek().sure();
        boolean needLogging = pTax.getOptions().isNeedLogging();
        LogAdapter log = pTax.getOptions().getLog();
        if (needLogging && !top.isEmpty()) {
            log.print("\nTAX: told subsumers");
        }
        top.stream().filter(p -> p.isClassified()).peek(p -> {
            if (needLogging) {
                log.printTemplate(Templates.TOLD_SUBSUMERS, p.getIRI());
            }
        }).forEach(p -> propagateTrueUp(p.getTaxVertex()));
        top = ksStack.peek().possible();
        if (!top.isEmpty() && needLogging) {
            log.print(" and possibly ");
            top.forEach(q -> log.print(Templates.TOLD_SUBSUMERS, q.getIRI()));
        }
    }

    @PortedFrom(file = "TaxonomyCreator.cpp", name = "setNonRedundantCandidates")
    private void setNonRedundantCandidates() {
        if (!curEntry.hasToldSubsumers() && pTax.getOptions().isNeedLogging()) {
            pTax.getOptions().getLog().print("\nTAX: TOP completely defines concept ").print(curEntry.getIRI());
        }
        // test if some "told subsumer" is not an immediate TS (ie, not a
        // border element)
        ksStack.peek().sure().forEach(p -> addPossibleParent(p.getTaxVertex()));
    }

    /**
     * check if no classification needed (synonym, orphan, unsatisfiable)
     * 
     * @return true if no classification
     */
    @PortedFrom(file = "TaxonomyCreator.h", name = "immediatelyClassified")
    protected boolean immediatelyClassified() {
        return classifySynonym();
    }

    @PortedFrom(file = "TaxonomyCreator.cpp", name = "setupTopDown")
    private void setupTopDown() {
        setToldSubsumers();
        if (!needTopDown()) {
            ++nCDEntries;
            setNonRedundantCandidates();
        }
    }

    /**
     * check if it is possible to skip TD phase
     * 
     * @return true if top down can be skipped
     */
    @PortedFrom(file = "TaxonomyCreator.h", name = "needTopDown")
    protected boolean needTopDown() {
        return false;
    }

    /** explicitely run TD phase */
    @PortedFrom(file = "TaxonomyCreator.h", name = "runTopDown")
    protected void runTopDown() {
        // nothing as default
    }

    /**
     * check if it is possible to skip BU phase
     * 
     * @return true if bottom up necessary
     */
    @PortedFrom(file = "TaxonomyCreator.h", name = "needBottomUp")
    protected boolean needBottomUp() {
        return false;
    }

    /** explicitely run BU phase */
    @PortedFrom(file = "TaxonomyCreator.h", name = "runBottomUp")
    protected void runBottomUp() {
        // nothing as default
    }

    /** actions that to be done BEFORE entry will be classified */
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "preClassificationActions")
    protected void preClassificationActions() {
        // nothing as default
    }

    @PortedFrom(file = "TaxonomyCreator.cpp", name = "performClassification")
    private void performClassification() {
        // do something before classification (tunable)
        preClassificationActions();
        ++nEntries;
        if (pTax.getOptions().isNeedLogging()) {
            // this output is currently disabled in FaCT++
            pTax.getOptions().getLog().print("\nTAX: start classifying entry ");
            pTax.getOptions().getLog().print(curEntry.getIRI());
        }
        // if no classification needed -- nothing to do
        if (immediatelyClassified()) {
            return;
        }
        // perform main classification
        generalTwoPhaseClassification();
        // create new vertex
        pTax.finishCurrentNode();
        // clear all labels
        clearLabels();
    }

    @PortedFrom(file = "TaxonomyCreator.cpp", name = "generalTwoPhaseClassification")
    private void generalTwoPhaseClassification() {
        setupTopDown();
        if (needTopDown()) {
            setValue(pTax.getTopVertex(), true);
            setValue(pTax.getBottomVertex(), false);
            upDirection = false;
            runTopDown();
        }
        clearLabels();
        if (needBottomUp()) {
            setValue(pTax.getBottomVertex(), true);
            upDirection = true;
            runBottomUp();
        }
        clearLabels();
    }

    /**
     * @param v
     *        v
     * @return true if V is a direct parent of current wrt labels
     */
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "isDirectParent")
    public boolean isDirectParent(TaxonomyVertex v) {
        return v.neigh(false).parallel().noneMatch(q -> isValued(q) && getValue(q));
    }

    // -- DFS-based classification
    /**
     * add top entry together with its known subsumers
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "TaxonomyCreator.h", name = "addTop")
    private void addTop(ClassifiableEntry p) {
        waitStack.push(p);
        ksStack.push(new ToldSubsumers(p.getToldSubsumers()));
        sigStack.push(buildSignature(p));
    }

    /** remove top entry */
    @PortedFrom(file = "TaxonomyCreator.h", name = "removeTop")
    protected void removeTop() {
        waitStack.pop();
        ksStack.pop();
        sigStack.pop();
    }

    @PortedFrom(file = "TaxonomyCreator.cpp", name = "classifyTop")
    private void classifyTop() {
        assert !waitStack.isEmpty();
        // load last concept
        setCurrentEntry(waitStack.peek());
        if (pTax.getOptions().isPrintTaxonomyInfo()) {
            pTax.getOptions().getLog().print("\nTrying classify", curEntry.isCompletelyDefined() ? " CD " : " ",
                curEntry.getIRI(), "... ");
        }
        performClassification();
        if (pTax.getOptions().isPrintTaxonomyInfo()) {
            pTax.getOptions().getLog().print("done");
        }
        removeTop();
    }

    /**
     * propagate the TRUE value of the KS subsumption up the hierarchy
     * 
     * @param node
     *        node
     */
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "propagateTrueUp")
    protected void propagateTrueUp(TaxonomyVertex node) {
        // if taxonomy class already checked -- do nothing
        if (isValued(node)) {
            assert getValue(node);
            return;
        }
        // overwise -- value it...
        setValue(node, true);
        // ... and value all parents
        node.neigh(true).forEach(this::propagateTrueUp);
    }

    /**
     * propagate the FALSE value of the KS subsumption down the hierarchy
     * 
     * @param node
     *        node
     */
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "propagateFalseDown")
    protected void propagateFalseDown(TaxonomyVertex node) {
        // if taxonomy class already checked -- do nothing
        if (isValued(node)) {
            assert !getValue(node);
            return;
        }
        // overwise -- value it...
        setValue(node, false);
        // ... and value all children
        node.neigh(false).forEach(this::propagateFalseDown);
    }

    /**
     * propagate constant VALUE into an appropriate direction
     * 
     * @param node
     *        node
     * @param value
     *        value
     * @return value
     */
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "setAndPropagate")
    protected boolean setAndPropagate(TaxonomyVertex node, boolean value) {
        if (value) {
            propagateTrueUp(node);
        } else {
            propagateFalseDown(node);
        }
        return value;
    }

    /**
     * add PARENT as a parent if it exists and is direct parent
     * 
     * @param parent
     *        parent
     */
    @PortedFrom(file = "TaxonomyCreator.h", name = "addPossibleParent")
    public void addPossibleParent(@Nullable TaxonomyVertex parent) {
        if (parent != null && isDirectParent(parent)) {
            pTax.getCurrent().addNeighbour(true, parent);
        }
    }

    @PortedFrom(file = "TaxonomyCreator.h", name = "clearLabels")
    protected void clearLabels() {
        pTax.clearLabels();
        valueLabel++;
    }

    /**
     * set Completely Defined flag
     * 
     * @param use
     *        use
     */
    @PortedFrom(file = "TaxonomyCreator.h", name = "setCompletelyDefined")
    public void setCompletelyDefined(boolean use) {
        useCompletelyDefined = use;
    }

    /**
     * @param p
     *        p
     */
    @PortedFrom(file = "TaxonomyCreator.h", name = "classifyEntry")
    public void classifyEntry(ClassifiableEntry p) {
        assert waitStack.isEmpty();
        // don't classify artificial concepts
        if (p.isNonClassifiable()) {
            return;
        }
        prepareTS(p);
    }

    @Override
    public String toString() {
        StringBuilder o = new StringBuilder();
        o.append("Taxonomy consists of ");
        o.append(nEntries);
        o.append(" entries\n            of which ");
        o.append(nCDEntries);
        o.append(" are completely defined\n\n");
        return o.toString();
    }

    /**
     * ensure that all TS of the top entry are classified.
     * 
     * @param cur
     *        cur
     * @return the reason of cycle or NULL.
     */
    @Nullable
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "prepareTS")
    private ClassifiableEntry prepareTS(ClassifiableEntry cur) {
        // we just found that TS forms a cycle -- return stop-marker
        if (waitStack.contains(cur)) {
            return cur;
        }
        // starting from the topmost entry
        addTop(cur);
        // true iff CUR is a reason of the cycle
        boolean cycleFound = false;
        // for all the told subsumers...
        for (ClassifiableEntry p : ksStack.peek().sure()) {
            if (!p.isClassified()) {
                // need to classify it first
                if (p.isNonClassifiable()) {
                    continue;
                }
                // prepare TS for *p
                ClassifiableEntry v = prepareTS(p);
                // if NULL is returned -- just continue
                if (v == null) {
                    continue;
                }
                if (v.equals(cur)) {
                    // current cycle is finished, all saved in Syns
                    // after classification of CUR we need to mark all the
                    // Syns
                    // as synonyms
                    cycleFound = true;
                    // continue to prepare its classification
                    continue;
                } else {
                    // arbitrary vertex in a cycle: save in synonyms of a
                    // root
                    // cause
                    syns.add(cur);
                    // don't need to classify it
                    removeTop();
                    // return the cycle cause
                    return v;
                }
            }
        }
        // all TS are ready here -- let's classify!
        classifyTop();
        // now if CUR is the reason of cycle mark all SYNs as synonyms
        if (cycleFound) {
            TaxonomyVertex syn = cur.getTaxVertex();
            syns.forEach(syn::addSynonym);
            syns.clear();
        }
        // here the cycle is gone
        return null;
    }

    // -----------------------------------------------------------------
    // -- DFS-based classification
    // -----------------------------------------------------------------
    /**
     * @param node
     *        node
     * @return true if a NODE has been valued during current classification pass
     */
    @PortedFrom(file = "TaxonomyCreator.h", name = "isValued")
    public boolean isValued(TaxonomyVertex node) {
        return node.isValued(valueLabel);
    }

    /**
     * @param node
     *        node
     * @return the subsumption value of a NODE wrt currently classified one
     */
    @PortedFrom(file = "TaxonomyCreator.h", name = "getValue")
    public boolean getValue(TaxonomyVertex node) {
        return node.getValue();
    }

    /**
     * set the classification value of a NODE to VALUE
     * 
     * @param node
     *        node
     * @param value
     *        value
     * @return val
     */
    @PortedFrom(file = "TaxonomyCreator.h", name = "setValue")
    public boolean setValue(TaxonomyVertex node, boolean value) {
        return node.setValued(value, valueLabel);
    }

    /**
     * prepare signature for given entry
     * 
     * @param p
     *        p
     * @return signature
     */
    @Nullable
    @PortedFrom(file = "TaxonomyCreator.h", name = "buildSignature")
    protected Signature buildSignature(@SuppressWarnings("unused") ClassifiableEntry p) {
        return null;
    }
}
