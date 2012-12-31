package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import conformance.PortedFrom;

/** atomical ontology structure */
@PortedFrom(file = "AtomicDecomposer.h", name = "AOStructure")
public class AOStructure {
    /** vector of atoms as a type */
    // typedef std::vector<TOntologyAtom*> AtomVec;
    /** all the atoms */
    @PortedFrom(file = "AtomicDecomposer.h", name = "Atoms")
    List<TOntologyAtom> Atoms = new ArrayList<TOntologyAtom>();

    /** create a new atom and get a pointer to it */
    @PortedFrom(file = "AtomicDecomposer.h", name = "newAtom")
    public TOntologyAtom newAtom() {
        TOntologyAtom ret = new TOntologyAtom();
        ret.setId(Atoms.size());
        Atoms.add(ret);
        return ret;
    }

    /** reduce graph of the atoms in the structure */
    @PortedFrom(file = "AtomicDecomposer.h", name = "reduceGraph")
    public void reduceGraph() {
        Set<TOntologyAtom> checked = new HashSet<TOntologyAtom>();
        for (TOntologyAtom p : Atoms) {
            p.getAllDepAtoms(checked);
        }
    }

    /** RW iterator begin */
    @PortedFrom(file = "AtomicDecomposer.h", name = "begin")
    public List<TOntologyAtom> begin() {
        return Atoms;
    }

    /** get RW atom by its index */
    @PortedFrom(file = "AtomicDecomposer.h", name = "get")
    public TOntologyAtom get(int index) {
        return Atoms.get(index);
    }

    /** size of the structure */
    @PortedFrom(file = "AtomicDecomposer.h", name = "size")
    public int size() {
        return Atoms.size();
    }
}
