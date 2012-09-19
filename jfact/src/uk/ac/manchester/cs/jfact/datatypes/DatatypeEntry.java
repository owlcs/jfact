package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;

import java.util.Collection;

import uk.ac.manchester.cs.jfact.kernel.NamedEntry;

public class DatatypeEntry extends NamedEntry {
    /** corresponding type (Type has null in the field) */
    private final Datatype<?> type;
    /** DAG index of the entry */
    private int pName;

    /** create data entry with given name */
    public DatatypeEntry(Datatype<?> type) {
        super(type.getDatatypeURI());
        this.type = type;
        pName = bpINVALID;
    }

    /** check if data entry represents basic data type */
    public boolean isBasicDataType() {
        return type != null && !type.isExpression();
    }

    public Datatype<?> getDatatype() {
        return type;
    }

    // facet part
    /** get RW access to constraints of the DE */
    public Collection<Facet> getFacet() {
        return type.getFacets();
    }

    /** get pointer to DAG entry corresponding to the data entry */
    @Override
    public int getIndex() {
        return pName;
    }

    /** set DAG index of the data entry */
    @Override
    public void setIndex(int p) {
        pName = p;
    }

    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + " " + pName + " " + type + " "
                + super.toString() + ")";
    }
}
