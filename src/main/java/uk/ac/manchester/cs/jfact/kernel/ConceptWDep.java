package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import javax.annotation.Nullable;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.dep.DepSet;

/** concept and dependency set */
@PortedFrom(file = "ConceptWithDep.h", name = "Concept")
public class ConceptWDep implements Serializable {

    /** "pointer" to a concept in DAG */
    @PortedFrom(file = "ConceptWithDep.h", name = "Concept") private final int concept;
    /** dep-set for a concept */
    @PortedFrom(file = "ConceptWithDep.h", name = "depSet") private final DepSet depSet;

    /**
     * c'tor with empty dep-set
     * 
     * @param p
     *        p
     */
    public ConceptWDep(int p) {
        concept = p;
        depSet = DepSet.create();
    }

    /**
     * @param p
     *        p
     * @param dep
     *        dep
     */
    public ConceptWDep(int p, DepSet dep) {
        concept = p;
        depSet = DepSet.create(dep);
    }

    /** @return concept */
    @PortedFrom(file = "ConceptWithDep.h", name = "getC")
    public int getConcept() {
        return concept;
    }

    /** @return dep-set */
    @PortedFrom(file = "ConceptWithDep.h", name = "getDep")
    public DepSet getDep() {
        return depSet;
    }

    /**
     * add dep-set to a CWD
     * 
     * @param d
     *        d
     */
    @PortedFrom(file = "ConceptWithDep.h", name = "addDep")
    public void addDep(DepSet d) {
        depSet.add(d);
    }

    /** @return print concept and a dep-set */
    @Override
    public String toString() {
        return concept + depSet.toString();
    }

    @Override
    public int hashCode() {
        return concept;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof ConceptWDep) {
            return concept == ((ConceptWDep) obj).concept;
        }
        return false;
    }
}
