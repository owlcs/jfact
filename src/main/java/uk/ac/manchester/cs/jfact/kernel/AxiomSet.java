package uk.ac.manchester.cs.jfact.kernel;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;

/** set of axioms */
@PortedFrom(file = "tAxiomSet.h", name = "TAxiomSet")
public class AxiomSet implements Serializable {

    private static final String POSSIBILITIES = " possibilities";
    /** host TBox that holds all concepts/etc */
    @PortedFrom(file = "tAxiomSet.h", name = "Host") @Nonnull protected final TBox tboxHost;
    /** set of axioms that accumilates incoming (and newly created) axioms */
    @PortedFrom(file = "tAxiomSet.h", name = "Accum") private List<Axiom> accumulator = new ArrayList<>();
    private final LogAdapter absorptionLog;
    /** set of absorption action, in order */
    @PortedFrom(file = "tAxiomSet.h", name = "ActionVector") private final List<AbsorptionActions> actions = new ArrayList<>();
    @PortedFrom(file = "tAxiomSet.h", name = "curAxiom") private int curAxiom = 0;

    /**
     * @param host
     *        host
     */
    public AxiomSet(TBox host) {
        tboxHost = host;
        absorptionLog = tboxHost.getOptions().getAbsorptionLog();
        Axiom.setLogAdapter(absorptionLog);
    }

    /**
     * add already built GCI p
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "tAxiomSet.h", name = "insertGCI")
    private void insertGCI(Axiom p) {
        tboxHost.getOptions().getAbsorptionLog().print("\n new axiom (").print(accumulator.size()).print("):", p);
        accumulator.add(p);
    }

    /**
     * @param q
     *        axiom
     * @return true iff axiom q is a copy of an axiom in range [p,p_end)
     */
    @PortedFrom(file = "tAxiomSet.h", name = "copyOf")
        boolean copyOfExisting(Axiom q) {
        int i = accumulator.indexOf(q);
        if (i > -1) {
            absorptionLog.print(" same as (").print(i).print(")");
            return true;
        }
        return false;
    }

    /**
     * helper that inserts an axiom into Accum;
     * 
     * @param q
     *        q
     * @return bool if success
     */
    @PortedFrom(file = "tAxiomSet.h", name = "processNewAxiom")
    protected boolean processNewAxiom(@Nullable Axiom q) {
        if (q == null) {
            return false;
        }
        // if an axiom is a copy of already processed one -- fail to add (will
        // result in a cycle)
        if (q.isCyclic()) {
            return false;
        }
        // if an axiom is a copy of a new one -- succeed but didn't really add
        // anything
        if (copyOfExisting(q)) {
            return true;
        }
        // fresh axiom -- add it
        insertGCI(q);
        return true;
    }

    /**
     * add axiom for the GCI C [= D
     * 
     * @param c
     *        C
     * @param d
     *        D
     */
    @PortedFrom(file = "tAxiomSet.h", name = "addAxiom")
    public void addAxiom(DLTree c, DLTree d) {
        tboxHost.getStatistics().sAbsInput();
        Axiom p = new Axiom(null);
        p.add(c);
        p.add(DLTreeFactory.createSNFNot(d));
        insertGCI(p);
    }

    /**
     * get number of (not absorbed) GCIs
     * 
     * @return size
     */
    @PortedFrom(file = "tAxiomSet.h", name = "size")
    private int size() {
        return accumulator.size();
    }

    /**
     * @return true if non-concept aborption were executed
     */
    @PortedFrom(file = "tAxiomSet.h", name = "wasRoleAbsorptionApplied")
    public boolean wasRoleAbsorptionApplied() {
        return tboxHost.getStatistics().containsSAbsRApply();
    }

    /**
     * @return GCI of all non-absorbed axioms
     */
    @PortedFrom(file = "tAxiomSet.h", name = "getGCI")
    public DLTree getGCI() {
        List<DLTree> l = asList(accumulator.stream().map(p -> p.createAnAxiom(null)));
        return DLTreeFactory.createSNFAnd(l);
    }

    /**
     * split given axiom
     * 
     * @param p
     *        p
     * @param tbox
     *        tbox
     * @return true if any spit happens
     */
    @PortedFrom(file = "tAxiomSet.h", name = "split")
    protected boolean split(Axiom p, TBox tbox) {
        List<Axiom> splitted = p.split(tbox);
        if (splitted.isEmpty()) {
            // nothing to split
            return false;
        }
        List<Axiom> kept = new ArrayList<>();
        for (Axiom q : splitted) {
            if (q.isCyclic()) {
                // axiom is a copy of a processed one: fail to do split
                return false;
            }
            // axiom is not a copy of a new one: keep it
            if (!copyOfExisting(q)) {
                kept.add(q);
            }
        }
        // no failure: delete all the unneded axioms, add all kept ones
        // do the actual insertion if necessary
        kept.forEach(this::insertGCI);
        return true;
    }

    /**
     * @return new size
     */
    @PortedFrom(file = "tAxiomSet.h", name = "absorb")
    public int absorb() {
        // GCIs to process
        List<Axiom> gcis = new ArrayList<>();
        // we will change Accum (via split rule), so indexing and compare with
        // size
        for (curAxiom = 0; curAxiom < accumulator.size(); curAxiom++) {
            Axiom ax = accumulator.get(curAxiom);
            tboxHost.getOptions().getAbsorptionLog().print("\nProcessing (").print(curAxiom).print("):");
            if (!absorbGCI(ax)) {
                gcis.add(ax);
            }
        }
        // clear absorbed and remove them from Accum
        accumulator = gcis;
        if (tboxHost.getOptions().isAbsorptionLoggingActive()) {
            tboxHost.getOptions().getAbsorptionLog().print("\nAbsorption done with ").print(accumulator.size())
                .print(" GCIs left\n");
            printStatistics();
        }
        return size();
    }

    @PortedFrom(file = "tAxiomSet.h", name = "absorbGCI")
    private boolean absorbGCI(Axiom p) {
        tboxHost.getStatistics().sAbsAction();
        boolean absorbed = actions.stream().anyMatch(abs -> abs.execute(p, this));
        if (!absorbed) {
            tboxHost.getOptions().getAbsorptionLog().print(" keep as GCI");
        }
        return absorbed;
    }

    /**
     * @param flags
     *        flags
     * @return false if no absorptions
     */
    @PortedFrom(file = "tAxiomSet.h", name = "initAbsorptionFlags")
    public boolean initAbsorptionFlags(String flags) {
        actions.clear();
        for (char c : flags.toCharArray()) {
            actions.add(AbsorptionActions.get(c));
        }
        tboxHost.getOptions().getAbsorptionLog().print("Init absorption order as ").print(flags).print("\n");
        return false;
    }

    @PortedFrom(file = "tAxiomSet.h", name = "PrintStatistics")
    private void printStatistics() {
        if (!tboxHost.getStatistics().containsSAbsAction()) {
            return;
        }
        LogAdapter log = tboxHost.getOptions().getAbsorptionLog();
        log.print("\nAbsorption dealt with ").print(tboxHost.getStatistics().getSAbsInput())
            .print(" input axioms\nThere were made ").print(tboxHost.getStatistics().getSAbsAction())
            .print(" absorption actions, of which:");
        if (tboxHost.getStatistics().containsSAbsRepCN()) {
            log.print("\n\t").print(tboxHost.getStatistics().getSAbsRepCN()).print(" concept name replacements");
        }
        if (tboxHost.getStatistics().containsSAbsRepForall()) {
            log.print("\n\t").print(tboxHost.getStatistics().getSAbsRepForall()).print(" universals replacements");
        }
        if (tboxHost.getStatistics().containsSAbsSplit()) {
            log.print("\n\t").print(tboxHost.getStatistics().getSAbsSplit()).print(" conjunction splits");
        }
        if (tboxHost.getStatistics().containsSAbsBApply()) {
            log.print("\n\t").print(tboxHost.getStatistics().getSAbsBApply()).print(" BOTTOM absorptions");
        }
        if (tboxHost.getStatistics().containsSAbsTApply()) {
            log.print("\n\t").print(tboxHost.getStatistics().getSAbsTApply()).print(" TOP absorptions");
        }
        if (tboxHost.getStatistics().containsSAbsCApply()) {
            log.print("\n\t").print(tboxHost.getStatistics().getSAbsCApply()).print(" concept absorption with ")
                .print(tboxHost.getStatistics().getSAbsCAttempt()).print(POSSIBILITIES);
        }
        if (tboxHost.getStatistics().containsSAbsNApply()) {
            log.print("\n\t").print(tboxHost.getStatistics().getSAbsNApply()).print(" negated concept absorption with ")
                .print(tboxHost.getStatistics().getSAbsNAttempt()).print(POSSIBILITIES);
        }
        if (tboxHost.getStatistics().containsSAbsRApply()) {
            log.print("\n\t").print(tboxHost.getStatistics().getSAbsRApply()).print(" role domain absorption with ")
                .print(tboxHost.getStatistics().getSAbsRAttempt()).print(POSSIBILITIES);
        }
        if (!accumulator.isEmpty()) {
            log.print("\nThere are ").print(accumulator.size()).print(" GCIs left");
        }
    }
}
