package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.BP_INVALID;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;

/** datatype entry */
public class DatatypeEntry extends NamedEntry {

    /** corresponding type (Type has null in the field) */
    @Nonnull private final Datatype<?> type;
    /** DAG index of the entry */
    private int pName;

    /**
     * create data entry with given name
     * 
     * @param type
     *        type
     */
    public DatatypeEntry(Datatype<?> type) {
        super(type.getDatatypeIRI());
        this.type = type;
        pName = BP_INVALID;
    }

    /** @return check if data entry represents basic data type */
    public boolean isBasicDataType() {
        return !type.isExpression();
    }

    /** @return datatype */
    public Datatype<?> getDatatype() {
        return type;
    }

    // facet part
    /** @return constraints of the DE */
    public Collection<Facet> getFacet() {
        return type.getFacets();
    }

    /** @return pointer to DAG entry corresponding to the data entry */
    @Override
    @PortedFrom(file = "taxNamEntry.h", name = "getIndex")
    public int getIndex() {
        return pName;
    }

    /** set DAG index of the data entry */
    @Override
    @PortedFrom(file = "taxNamEntry.h", name = "setIndex")
    public void setIndex(int p) {
        pName = p;
    }

    @Override
    @Nonnull
    public String toString() {
        return '(' + this.getClass().getSimpleName() + ' ' + pName + ' ' + type + ' ' + super.toString() + ')';
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof DatatypeEntry)) {
            return false;
        }
        return type.equals(((DatatypeEntry) obj).type);
    }

    /**
     * @param p
     *        index to set
     * @return modified object
     */
    public DatatypeEntry withIndex(int p) {
        setIndex(p);
        return this;
    }
}
