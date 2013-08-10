package uk.ac.manchester.cs.jfact.kernel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.util.ProgressMonitor;

import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.split.TSignature;

/** Taxonomy of named DL concepts (and mapped individuals) */
public class IncrementalClassifier extends TaxonomyCreator { private static final long serialVersionUID=11000L;
    /** delegate reasoner */
    protected ReasoningKernel reasoner = null;
    /** session signature */
    protected TSignature sig = null;
    /** re-classified taxonomy node */
    protected TaxonomyVertex curNode = null;
    /** tested concept */
    protected ConceptName Current = null;
    /** set of possible parents */
    protected Set<TaxonomyVertex> candidates = new HashSet<TaxonomyVertex>();
    /** whether look into it */
    protected boolean useCandidates;
    // statistic counters
    protected long nConcepts = 0;
    protected long nTries = 0;
    protected long nPositives = 0;
    protected long nNegatives = 0;
    protected long nSearchCalls = 0;
    protected long nSubCalls = 0;
    protected long nNonTrivialSubCalls = 0;
    /** number of non-subsumptions because of module reasons */
    protected long nModuleNegative = 0;
    /** indicator of taxonomy creation progress */
    protected ProgressMonitor pTaxProgress = null;

    /** helper */
    protected static ConceptName getCName(ClassifiableEntry entry) {
        return (ConceptName) entry.getEntity();
    }

    // -----------------------------------------------------------------
    // -- General support for DL concept classification
    // -----------------------------------------------------------------
    /** test whether Current [= q */
    protected boolean testSub(Concept q) {
        assert q != null;
        if (q.isTop()) {
            return true;
        }
        if (q.isSingleton()       // singleton on the RHS is useless iff...
                && q.isPrimitive()    // it is primitive
                && !q.isNominal()) {
            return false;
        }
        pTax.getOptions().getLog().print("\nTAX: trying '").print(Current.getName())
                .print("' [= '").print(q.getName()).print("'... ");
        if (isNotInModule(q.getEntity())) {
            pTax.getOptions().getLog().print("NOT holds (module result)");
            ++nModuleNegative;
            return false;
        }
        boolean ret = testSubTBox(q);
        return ret;
    }

    /** test subsumption via TBox explicitely */
    protected boolean testSubTBox(Concept q) {
        boolean res = reasoner.isSubsumedBy(Current, getCName(q));
        // update statistic
        ++nTries;
        if (res) {
            ++nPositives;
        } else {
            ++nNegatives;
        }
        return res;
    }

    // interface from BAADER paper
    /** SEARCH procedure from Baader et al paper */
    protected void searchBaader(TaxonomyVertex cur) {
        // label 'visited'
        pTax.setVisited(cur);
        // std::cout << "visiting " << cur->getPrimer()->getName() << std::endl;
        ++nSearchCalls;
        boolean noPosSucc = true;
        // check if there are positive successors; use DFS on them.
        for (TaxonomyVertex p : cur.neigh(upDirection)) {
            if (enhancedSubs(p)) {
                if (!pTax.isVisited(p)) {
                    searchBaader(p);
                }
                noPosSucc = false;
            }
        }
        // in case current node is unchecked (no BOTTOM node) -- check it
        // explicitly
        if (!isValued(cur)) {
            setValue(cur, testSubsumption(cur));
        }
        // mark labelled leaf node as a parent
        if (noPosSucc && cur.getValue()) {
            curNode.addNeighbour(!upDirection, cur);
        }
    }

    /** ENHANCED_SUBS procedure from Baader et al paper */
    protected boolean enhancedSubs1(TaxonomyVertex cur) {
        ++nNonTrivialSubCalls;
        // need to be valued -- check all parents
        // propagate false
        for (TaxonomyVertex p : cur.neigh(!upDirection)) {
            if (!enhancedSubs(p)) {
                return false;
            }
        }
        // all subsumptions holds -- test current for subsumption
        return testSubsumption(cur);
    }

    /** short-cut from ENHANCED_SUBS */
    protected boolean enhancedSubs2(TaxonomyVertex cur) {
        if (useCandidates && candidates.contains(cur)) {
            return false;
        }
        return enhancedSubs1(cur);
    }

    // wrapper for the ENHANCED_SUBS
    protected boolean enhancedSubs(TaxonomyVertex cur) {
        ++nSubCalls;
        if (isValued(cur)) {
            return getValue(cur);
        } else {
            return setValue(cur, enhancedSubs2(cur));
        }
    }

    /** explicitly test appropriate subsumption relation */
    protected boolean testSubsumption(TaxonomyVertex cur) {
        return testSub((Concept) cur.getPrimer());
    }

    /** @return true if non-subsumption is due to ENTITY is not in the \bot-module */
    protected boolean isNotInModule(NamedEntity entity) {
        return entity != null && sig != null && !sig.containsNamedEntity(entity);
    }

    /** fill candidates */
    protected void fillCandidates(TaxonomyVertex cur) {
        // std::cout << "fill candidates: " << cur->getPrimer()->getName() <<
        // std::endl;
        if (isValued(cur)) {
            if (getValue(cur)) {
                return;
            }
        } else {
            candidates.add(cur);
        }
        for (TaxonomyVertex p : cur.neigh(true)) {
            fillCandidates(p);
        }
    }

    /** check if concept is unsat; add it as a synonym of BOTTOM if necessary */
    protected boolean isUnsatisfiable() {
        if (reasoner.isSatisfiable(Current)) {
            return false;
        }
        pTax.addCurrentToSynonym(pTax.getBottomVertex());
        return true;
    }

    // -----------------------------------------------------------------
    // -- Tunable methods (depending on taxonomy type)
    // -----------------------------------------------------------------
    /** check if no BU classification is required as C=TOP */
    protected boolean isEqualToTop() {
        return false;
    }

    /** check if it is possible to skip TD phase */
    protected boolean needTopDown() {
        return !(useCompletelyDefined && curEntry.isCompletelyDefined());
    }

    /** explicitly run TD phase */
    protected void runTopDown() {
        searchBaader(pTax.getTopVertex());
    }

    /** check if it is possible to skip BU phase */
    protected boolean needBottomUp() {
        return false;
    }

    /** actions that to be done BEFORE entry will be classified */
    protected void preClassificationActions() {
        ++nConcepts;
        if (pTaxProgress != null) {
            pTaxProgress.setProgress(1);
        }
    }

    /** check if it is necessary to log taxonomy action */
    protected boolean needLogging() {
        return true;
    }

    public IncrementalClassifier(Taxonomy tax) {
        super(tax);
        upDirection = false;
    }

    public void reclassify(TaxonomyVertex node, ReasoningKernel r, TSignature s,
            boolean added, boolean removed) {
        reasoner = r;
        sig = s;
        curNode = node;
        Current = getCName(node.getPrimer());
        // FIXME!! check the unsatisfiability later
        assert added || removed;
        clearLabels();
        if (node.noNeighbours(true)) {
            node.addNeighbour(true, pTax.getTopVertex());
        }
        // we use candidates set if nothing was added (so no need to look
        // further from current subs)
        useCandidates = !added;
        candidates.clear();
        if (removed) {
            // re-check all parents
            List<TaxonomyVertex> pos = new ArrayList<TaxonomyVertex>();
            List<TaxonomyVertex> neg = new ArrayList<TaxonomyVertex>();
            for (TaxonomyVertex p : node.neigh(true)) {
                boolean sub = testSubsumption(p);
                setValue(p, sub);
                if (sub) {
                    pos.add(p);
                    propagateTrueUp(p);
                } else {
                    neg.add(p);
                }
            }
            node.removeLinks(true);
            for (TaxonomyVertex q : pos) {
                node.addNeighbour(true, q);
            }
            if (useCandidates) {
                for (TaxonomyVertex q : neg) {
                    fillCandidates(q);
                }
            }
        } else    // all parents are there
        {
            for (TaxonomyVertex p : node.neigh(true)) {
                setValue(p, true);
                propagateTrueUp(p);
            }
            node.removeLinks(true);
        }
        // FIXME!! for now. later check the equivalence etc
        setValue(node, false);
        // the landscape is prepared
        searchBaader(pTax.getTopVertex());
        curNode.incorporate(pTax.getOptions());
        clearLabels();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Totally ").append(nTries)
                .append(" subsumption tests was made\nAmong them ");
        long n = nTries > 0 ? nTries : 1;
        b.append(nPositives).append(" (").append(nPositives * 100 / n)
                .append("%) successful\n");
        if (nModuleNegative == 0) {
            b.append("Modular reasoning deals with ").append(nModuleNegative)
                    .append(" non-subsumptions\n");
        }
        b.append("There were made ").append(nSearchCalls)
                .append(" search calls\nThere were made ").append(nSubCalls)
                .append(" Sub calls, of which ").append(nNonTrivialSubCalls)
                .append(" non-trivial\n");
        b.append("Current efficiency (wrt Brute-force) is ")
                .append(nEntries * (nEntries - 1) / n).append("\n");
        b.append(super.toString());
        return b.toString();
    }
}
