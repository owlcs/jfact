package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.Concept;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import conformance.PortedFrom;

/** this is to keep the track of new vars/axioms for C in C0, C1, ..., Cn */
@PortedFrom(file = "tSplitVars.h", name = "TSplitVar")
public class TSplitVar implements Serializable {

    private static final long serialVersionUID = 11000L;
    // types
    // name of split concept
    private ConceptName oldName;
    // split concept itself
    private Concept C;
    private final List<SplitVarEntry> Entries = new ArrayList<SplitVarEntry>();

    /**
     * @param name
     *        name
     * @param sig
     *        sig
     * @param mod
     *        mod
     */
    public void addEntry(ConceptName name, TSignature sig,
            Set<AxiomInterface> mod) {
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

    /**
     * @param oldName
     *        oldName
     */
    public void setOldName(ConceptName oldName) {
        this.oldName = oldName;
    }

    /** @return concept */
    public Concept getC() {
        return C;
    }

    /**
     * @param c
     *        c
     */
    public void setC(Concept c) {
        C = c;
    }
}
