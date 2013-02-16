package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.Concept;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import conformance.PortedFrom;

/** this is to keep the track of new vars/axioms for C >< C0, C1, ..., Cn */
@PortedFrom(file = "tSplitVars.h", name = "TSplitVar")
public class TSplitVar {
    // types

    // name of split concept
    private ConceptName oldName;
    // split concept itself
    private Concept C;
    private List<SplitVarEntry> Entries = new ArrayList<SplitVarEntry>();

    void addEntry(ConceptName name, TSignature sig, Set<AxiomInterface> mod) {
        SplitVarEntry e = new SplitVarEntry();
        e.name = name;
        e.concept = null;
        e.sig = sig;
        e.Module = mod;
        Entries.add(e);
    }

    /** @return list of entries */
    public List<SplitVarEntry> getEntries() {
        return Entries;
    }

    /** @return old concept name */
    public ConceptName getOldName() {
        return oldName;
    }

    /** @param oldName */
    public void setOldName(ConceptName oldName) {
        this.oldName = oldName;
    }

    /** @return concept */
    public Concept getC() {
        return C;
    }

    /** @param c */
    public void setC(Concept c) {
        C = c;
    }
}
