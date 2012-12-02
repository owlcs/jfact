package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;

public class TOntologyAtom {
    static Comparator<TOntologyAtom> comparator = new Comparator<TOntologyAtom>() {
        @Override
        public int compare(TOntologyAtom arg0, TOntologyAtom arg1) {
            return arg0.getId() - arg1.getId();
        }
    };
    /**  set of axioms in the atom */
    Set<Axiom> AtomAxioms = new HashSet<Axiom>();
    /**  set of axioms in the module (Atom's ideal) */
    Set<Axiom> ModuleAxioms = new HashSet<Axiom>();
    /**  set of atoms current one depends on */
    Set<TOntologyAtom> DepAtoms = new HashSet<TOntologyAtom>();
    /**  set of all atoms current one depends on */
    Set<TOntologyAtom> AllDepAtoms = new HashSet<TOntologyAtom>();
    /**  unique atom's identifier */
    int Id = 0;

    /**  remove all atoms in AllDepAtoms from DepAtoms */
    public void filterDep() {
        for (TOntologyAtom p : AllDepAtoms) {
            DepAtoms.remove(p);
        }
    }

    /**  build all dep atoms; filter them from DepAtoms */
    public void buildAllDepAtoms(Set<TOntologyAtom> checked) {
        // first gather all dep atoms from all known dep atoms
        for (TOntologyAtom p : DepAtoms) {
            Set<TOntologyAtom> Dep = p.getAllDepAtoms(checked);
            AllDepAtoms.addAll(Dep);
        }
        // now filter them out from known dep atoms
        filterDep();
        // add direct deps to all deps
        AllDepAtoms.addAll(DepAtoms);
        // now the atom is checked
        checked.add(this);
    }

    // fill in the sets
    /**  set the module axioms */
    public void setModule(Collection<Axiom> module) {
        ModuleAxioms = new HashSet<Axiom>(module);
    }

    /**  add axiom AX to an atom */
    public void addAxiom(Axiom ax) {
        AtomAxioms.add(ax);
        ax.setAtom(this);
    }

    /**  add atom to the dependency set */
    public void addDepAtom(TOntologyAtom atom) {
        if (atom != null && atom != this) {
            DepAtoms.add(atom);
        }
    }

    /**  get all the atoms the current one depends on; build this set if */
    // necessary
    public Set<TOntologyAtom> getAllDepAtoms(Set<TOntologyAtom> checked) {
        if (checked.contains(this)) {
            buildAllDepAtoms(checked);
        }
        return AllDepAtoms;
    }

    // access to axioms
    /**  get all the atom's axioms */
    public Set<Axiom> getAtomAxioms() {
        return AtomAxioms;
    }

    /**  get all the module axioms */
    public Set<Axiom> getModule() {
        return ModuleAxioms;
    }

    /**  get atoms a given one depends on */
    public Set<TOntologyAtom> getDepAtoms() {
        return DepAtoms;
    }

    /**  get the value of the id */
    public int getId() {
        return Id;
    }

    /**  set the value of the id to ID */
    public void setId(int id) {
        Id = id;
    }
}
