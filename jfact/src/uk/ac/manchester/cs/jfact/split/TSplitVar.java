package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.Concept;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;

/**  this is to keep the track of new vars/axioms for C >< C0, C1, ..., Cn */
public class TSplitVar {
    // types
    public class Entry {
        public ConceptName name; // entry name
        public Concept concept; // internal name
        public TSignature sig;
        public Set<Axiom> Module;
    }

    // members
    public ConceptName oldName; // name of split concept
    public Concept C; // split concept itself
    private List<Entry> Entries = new ArrayList<Entry>();

    // methods
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
