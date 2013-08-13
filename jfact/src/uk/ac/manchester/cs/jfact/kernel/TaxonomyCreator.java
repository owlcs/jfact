package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.split.TSignature;
import conformance.PortedFrom;

/** taxonomy creator for DL */
@PortedFrom(file = "TaxonomyCreator.h", name = "TaxonomyCreator")
public class TaxonomyCreator implements Serializable {
    private static final long serialVersionUID = 11000L;
    @PortedFrom(file = "TaxonomyCreator.h", name = "pTax")
    protected final Taxonomy pTax;
    @PortedFrom(file = "TaxonomyCreator.h", name = "Syns")
    protected final List<ClassifiableEntry> Syns = new ArrayList<ClassifiableEntry>();
    /** labeller for marking nodes with a label wrt classification */
    @PortedFrom(file = "TaxonomyCreator.h", name = "valueLabel")
    protected long valueLabel = 1;
    /** pointer to currently classified entry */
    @PortedFrom(file = "TaxonomyCreator.h", name = "curEntry")
    protected ClassifiableEntry curEntry = null;
    /** number of tested entryes */
    @PortedFrom(file = "TaxonomyCreator.h", name = "nEntries")
    protected int nEntries = 0;
    /** number of completely-defined entries */
    @PortedFrom(file = "TaxonomyCreator.h", name = "nCDEntries")
    protected long nCDEntries = 0;
    /** optimisation flag: if entry is completely defined by it's told subsumers,
     * no other classification required */
    @PortedFrom(file = "TaxonomyCreator.h", name = "useCompletelyDefined")
    protected boolean useCompletelyDefined = false;
    /** session flag: shows the direction of the search */
    @PortedFrom(file = "TaxonomyCreator.h", name = "upDirection")
    protected boolean upDirection;
    /** stack for Taxonomy creation */
    @PortedFrom(file = "TaxonomyCreator.h", name = "waitStack")
    private final LinkedList<ClassifiableEntry> waitStack = new LinkedList<ClassifiableEntry>();
    /** told subsumers corresponding to a given entry */
    @PortedFrom(file = "TaxonomyCreator.h", name = "ksStack")
    protected final LinkedList<KnownSubsumers> ksStack = new LinkedList<KnownSubsumers>();
    /** signature of a \bot-module corresponding to a given entry */
    @PortedFrom(file = "TaxonomyCreator.h", name = "sigStack")
    protected final LinkedList<TSignature> sigStack = new LinkedList<TSignature>();

    /** @param pTax2 */
    public TaxonomyCreator(Taxonomy pTax2) {
        pTax = pTax2;
    }

    /** initialise aux entry with given concept p */
    @PortedFrom(file = "TaxonomyCreator.h", name = "setCurrentEntry")
    protected void setCurrentEntry(ClassifiableEntry p) {
        pTax.getCurrent().clear();
        pTax.getCurrent().setSample(p, true);
        curEntry = p;
    }

    @PortedFrom(file = "TaxonomyCreator.h", name = "classifySynonym")
    protected boolean classifySynonym() {
        return pTax.processSynonym();
    }

    @PortedFrom(file = "TaxonomyCreator.cpp", name = "setToldSubsumers")
    private void setToldSubsumers() {
        Collection<ClassifiableEntry> top = ksStack.peek().s_begin();
        if (needLogging() && !top.isEmpty()) {
            pTax.getOptions().getLog().print("\nTAX: told subsumers");
        }
        for (ClassifiableEntry p : top) {
            if (p.isClassified()) {
                if (needLogging()) {
                    pTax.getOptions().getLog()
                            .printTemplate(Templates.TOLD_SUBSUMERS, p.getName());
                }
                propagateTrueUp(p.getTaxVertex());
            }
        }
        // XXX this is misleading: in the C++ code the only implementation
        // available will always say that top is empty here even if it never
        // is.
        // if (!top.isEmpty() && needLogging()) {
        // LL.print(" and possibly ");
        // for (ClassifiableEntry q : top) {
        // LL.print(Templates.TOLD_SUBSUMERS, q.getName());
        // }
        // }
    }

    @PortedFrom(file = "TaxonomyCreator.cpp", name = "setNonRedundantCandidates")
    private void setNonRedundantCandidates() {
        if (!curEntry.hasToldSubsumers()) {
            pTax.getOptions().getLog().print("\nTAX: TOP");
        }
        pTax.getOptions().getLog().print(" completely defines concept ");
        pTax.getOptions().getLog().print(curEntry.getName());
        // test if some "told subsumer" is not an immediate TS (ie, not a
        // border
        // element)
        for (ClassifiableEntry p : ksStack.peek().s_begin()) {
            addPossibleParent(p.getTaxVertex());
        }
    }

    /** check if no classification needed (synonym, orphan, unsatisfiable) */
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

    /** check if it is possible to skip TD phase */
    @PortedFrom(file = "TaxonomyCreator.h", name = "needTopDown")
    protected boolean needTopDown() {
        return false;
    }

    /** explicitely run TD phase */
    @PortedFrom(file = "TaxonomyCreator.h", name = "runTopDown")
    protected void runTopDown() {}

    /** check if it is possible to skip BU phase */
    @PortedFrom(file = "TaxonomyCreator.h", name = "needBottomUp")
    protected boolean needBottomUp() {
        return false;
    }

    /** explicitely run BU phase */
    @PortedFrom(file = "TaxonomyCreator.h", name = "runBottomUp")
    protected void runBottomUp() {}

    /** actions that to be done BEFORE entry will be classified */
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "preClassificationActions")
    protected void preClassificationActions() {}

    @PortedFrom(file = "TaxonomyCreator.cpp", name = "performClassification")
    private void performClassification() {
        // do something before classification (tunable)
        preClassificationActions();
        ++nEntries;
        pTax.getOptions().getLog().print("\n\nTAX: start classifying entry ");
        pTax.getOptions().getLog().print(curEntry.getName());
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

    /** @return true if V is a direct parent of current wrt labels */
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "isDirectParent")
    public boolean isDirectParent(TaxonomyVertex v) {
        for (TaxonomyVertex q : v.neigh(false)) {
            if (isValued(q) && getValue(q)) {
                return false;
            }
        }
        return true;
    }

    // -- DFS-based classification
    /** add top entry together with its known subsumers */
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
        if (pTax.getOptions().isTMP_PRINT_TAXONOMY_INFO()) {
            pTax.getOptions()
                    .getLog()
                    .print("\nTrying classify",
                            curEntry.isCompletelyDefined() ? " CD " : " ",
                            curEntry.getName(), "... ");
        }
        performClassification();
        if (pTax.getOptions().isTMP_PRINT_TAXONOMY_INFO()) {
            pTax.getOptions().getLog().print("done");
        }
        removeTop();
    }

    /** propagate the TRUE value of the KS subsumption up the hierarchy */
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
        List<TaxonomyVertex> list = node.neigh(true);
        for (int i = 0; i < list.size(); i++) {
            propagateTrueUp(list.get(i));
        }
    }

    /** propagate the FALSE value of the KS subsumption down the hierarchy */
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "propagateFalseDown")
    protected void propagateFalseDown(TaxonomyVertex node) {
        // if taxonomy class already checked -- do nothing
        if (isValued(node)) {
            assert getValue(node) == false;
            return;
        }
        // overwise -- value it...
        setValue(node, false);
        // ... and value all children
        for (TaxonomyVertex p : node.neigh(false)) {
            propagateFalseDown(p);
        }
    }

    /** propagate constant VALUE into an appropriate direction */
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "setAndPropagate")
    protected boolean setAndPropagate(TaxonomyVertex node, boolean value) {
        if (value) {
            propagateTrueUp(node);
        } else {
            propagateFalseDown(node);
        }
        return value;
    }
    /** check if it is necessary to log taxonomy action */
    @PortedFrom(file = "TaxonomyCreator.h", name = "needLogging")
    protected boolean needLogging() {
        return true;
    }

    /** add PARENT as a parent if it exists and is direct parent
     * 
     * @param parent */
    @PortedFrom(file = "TaxonomyCreator.h", name = "addPossibleParent")
    public void addPossibleParent(TaxonomyVertex parent) {
        if (parent != null && isDirectParent(parent)) {
            pTax.getCurrent().addNeighbour(true, parent);
        }
    }

    @PortedFrom(file = "TaxonomyCreator.h", name = "clearLabels")
    protected void clearLabels() {
        pTax.clearLabels();
        valueLabel++;
    }

    /** set Completely Defined flag
     * 
     * @param use */
    @PortedFrom(file = "TaxonomyCreator.h", name = "setCompletelyDefined")
    public void setCompletelyDefined(boolean use) {
        useCompletelyDefined = use;
    }

    /** @param p */
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

    /** ensure that all TS of the top entry are classified.
     * 
     * @return the reason of cycle or NULL. */
    @PortedFrom(file = "TaxonomyCreator.cpp", name = "prepareTS")
    ClassifiableEntry prepareTS(ClassifiableEntry cur) {
        // we just found that TS forms a cycle -- return stop-marker
        if (waitStack.contains(cur)) {
            return cur;
        }
        // starting from the topmost entry
        addTop(cur);
        // true iff CUR is a reason of the cycle
        boolean cycleFound = false;
        // for all the told subsumers...
        for (ClassifiableEntry p : ksStack.peek().s_begin()) {
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
                if (v == cur) {
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
                    Syns.add(cur);
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
            for (ClassifiableEntry q : Syns) {
                syn.addSynonym(q);
            }
            Syns.clear();
        }
        // here the cycle is gone
        return null;
    }

    // -----------------------------------------------------------------
    // -- DFS-based classification
    // -----------------------------------------------------------------
    /** @param node
     * @return true if a NODE has been valued during current classification pass */
    @PortedFrom(file = "TaxonomyCreator.h", name = "isValued")
    public boolean isValued(TaxonomyVertex node) {
        return node.isValued(valueLabel);
    }

    /** @param node
     * @return the subsumption value of a NODE wrt currently classified one */
    @PortedFrom(file = "TaxonomyCreator.h", name = "getValue")
    public boolean getValue(TaxonomyVertex node) {
        return node.getValue();
    }

    /** set the classification value of a NODE to VALUE
     * 
     * @param node
     * @param value
     * @return val */
    @PortedFrom(file = "TaxonomyCreator.h", name = "setValue")
    public boolean setValue(TaxonomyVertex node, boolean value) {
        return node.setValued(value, valueLabel);
    }

    /** prepare signature for given entry
     * 
     * @param p */
    @PortedFrom(file = "TaxonomyCreator.h", name = "buildSignature")
    protected TSignature buildSignature(ClassifiableEntry p) {
        return null;
    }
}
