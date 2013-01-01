package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.Concept;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import conformance.PortedFrom;

/** this is to keep the track of new vars/axioms for C >< C0, C1, ..., Cn */
@PortedFrom(file = "tSplitVars.h", name = "TSplitVar")
public class TSplitVar {
    // types
    public class Entry {
        // entry name
        public ConceptName name;
        // internal name
        public Concept concept;
        public TSignature sig;
        public Set<Axiom> Module;
    }

    // name of split concept
    public ConceptName oldName;
    // split concept itself
    public Concept C;
    private List<Entry> Entries = new ArrayList<Entry>();

    void addEntry(ConceptName name, TSignature sig, Set<Axiom> mod) {
        Entry e = new Entry();
        e.name = name;
        e.concept = null;
        e.sig = sig;
        e.Module = mod;
        Entries.add(e);
    }

    public List<Entry> getEntries() {
        return Entries;
    }
}
