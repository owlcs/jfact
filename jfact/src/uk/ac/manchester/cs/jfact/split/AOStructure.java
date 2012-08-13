package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// atomical ontology structure
public class AOStructure {
    // / vector of atoms as a type
    // typedef std::vector<TOntologyAtom*> AtomVec;
    // / all the atoms
    List<TOntologyAtom> Atoms = new ArrayList<TOntologyAtom>();

    // / create a new atom and get a pointer to it
    public TOntologyAtom newAtom() {
        TOntologyAtom ret = new TOntologyAtom();
        ret.setId(Atoms.size());
        Atoms.add(ret);
        return ret;
    }

    // / reduce graph of the atoms in the structure
    public void reduceGraph() {
        Set<TOntologyAtom> checked = new HashSet<TOntologyAtom>();
        for (TOntologyAtom p : Atoms) {
            p.getAllDepAtoms(checked);
        }
    }

    // / RW iterator begin
    public List<TOntologyAtom> begin() {
        return Atoms;
    }

    // / get RW atom by its index
    public TOntologyAtom get(int index) {
        return Atoms.get(index);
    }

    // / size of the structure
    public int size() {
        return Atoms.size();
    }
}
