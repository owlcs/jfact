package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;

/** ontology atom */
@PortedFrom(file = "tOntologyAtom.h", name = "TOntologyAtom")
public class TOntologyAtom implements Comparable<TOntologyAtom>, Serializable {

    /** set of axioms in the atom */
    @PortedFrom(file = "tOntologyAtom.h", name = "AtomAxioms") private final Set<AxiomInterface> atomAxioms = new HashSet<>();
    /** set of axioms in the module (Atom's ideal) */
    @PortedFrom(file = "tOntologyAtom.h", name = "ModuleAxioms") private Set<AxiomInterface> moduleAxioms = new HashSet<>();
    /** set of atoms current one depends on */
    @PortedFrom(file = "tOntologyAtom.h", name = "DepAtoms") private final Set<TOntologyAtom> depAtoms = new HashSet<>();
    /** set of all atoms current one depends on */
    @PortedFrom(file = "tOntologyAtom.h", name = "AllDepAtoms") private final Set<TOntologyAtom> allDepAtoms = new HashSet<>();
    /** unique atom's identifier */
    @PortedFrom(file = "tOntologyAtom.h", name = "Id") private int id = 0;

    @Override
    public int compareTo(@Nullable TOntologyAtom arg1) {
        return getId() - arg1.getId();
    }

    /** remove all atoms in AllDepAtoms from DepAtoms */
    @PortedFrom(file = "tOntologyAtom.h", name = "filterDep")
    public void filterDep() {
        allDepAtoms.forEach(depAtoms::remove);
    }

    /**
     * build all dep atoms; filter them from DepAtoms
     * 
     * @param checked
     *        checked
     */
    @PortedFrom(file = "tOntologyAtom.h", name = "buildAllDepAtoms")
    public void buildAllDepAtoms(Set<TOntologyAtom> checked) {
        // first gather all dep atoms from all known dep atoms
        depAtoms.forEach(p -> allDepAtoms.addAll(p.getAllDepAtoms(checked)));
        // now filter them out from known dep atoms
        filterDep();
        // add direct deps to all deps
        allDepAtoms.addAll(depAtoms);
        // now the atom is checked
        checked.add(this);
    }

    // fill in the sets
    /**
     * set the module axioms
     * 
     * @param module
     *        module
     */
    @PortedFrom(file = "tOntologyAtom.h", name = "setModule")
    public void setModule(Collection<AxiomInterface> module) {
        // XXX check if the defensive copy is needed
        moduleAxioms = new HashSet<>(module);
    }

    /**
     * add axiom AX to an atom
     * 
     * @param ax
     *        ax
     */
    @PortedFrom(file = "tOntologyAtom.h", name = "addAxiom")
    public void addAxiom(AxiomInterface ax) {
        atomAxioms.add(ax);
        ax.setAtom(this);
    }

    /**
     * add atom to the dependency set
     * 
     * @param atom
     *        atom
     */
    @PortedFrom(file = "tOntologyAtom.h", name = "addDepAtom")
    public void addDepAtom(@Nullable TOntologyAtom atom) {
        // XXX != should be !equals() ?
        if (atom != null && atom != this) {
            depAtoms.add(atom);
        }
    }

    /**
     * @param checked
     *        checked
     * @return all the atoms the current one depends on; build this set if
     *         necessary
     */
    @PortedFrom(file = "tOntologyAtom.h", name = "getAllDepAtoms")
    public Set<TOntologyAtom> getAllDepAtoms(Set<TOntologyAtom> checked) {
        if (checked.contains(this)) {
            buildAllDepAtoms(checked);
        }
        return allDepAtoms;
    }

    // access to axioms
    /** @return all the atom's axioms */
    @PortedFrom(file = "tOntologyAtom.h", name = "getAtomAxioms")
    public Set<AxiomInterface> getAtomAxioms() {
        return atomAxioms;
    }

    /** @return all the module axioms */
    @PortedFrom(file = "tOntologyAtom.h", name = "getModule")
    public Set<AxiomInterface> getModule() {
        return moduleAxioms;
    }

    /** @return atoms a given one depends on */
    @PortedFrom(file = "tOntologyAtom.h", name = "getDepAtoms")
    public Set<TOntologyAtom> getDepAtoms() {
        return depAtoms;
    }

    /** @return the value of the id */
    @PortedFrom(file = "tOntologyAtom.h", name = "getId")
    public int getId() {
        return id;
    }

    /**
     * set the value of the id to ID
     * 
     * @param id
     *        id
     */
    @PortedFrom(file = "tOntologyAtom.h", name = "setId")
    public void setId(int id) {
        this.id = id;
    }
}
