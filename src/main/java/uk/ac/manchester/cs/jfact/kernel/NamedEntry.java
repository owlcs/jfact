package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** named entry */
@PortedFrom(file = "tNamedEntry.h", name = "TNamedEntry")
public abstract class NamedEntry implements HasIRI, Serializable {

    /** name of the entry */
    @PortedFrom(file = "tNamedEntry.h", name = "extName") protected final IRI extName;
    /** entry identifier */
    @PortedFrom(file = "tNamedEntry.h", name = "extId") protected int extId;
    @PortedFrom(file = "tNamedEntry.h", name = "entity") protected NamedEntity entity = null;
    @Original private boolean system;
    @Original private boolean top = false;
    @Original private boolean bottom;

    /**
     * @param name
     *        entry IRI
     */
    public NamedEntry(IRI name) {
        assert name != null;
        extName = name;
        extId = 0;
        if (extName.isThing()) {
            top = true;
        }
        if (extName.isNothing()) {
            bottom = true;
        }
    }

    @Override
    @PortedFrom(file = "tNamedEntry.h", name = "getName")
    public IRI getIRI() {
        return extName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof NamedEntry) {
            NamedEntry e = (NamedEntry) obj;
            return extName.equals(e.extName) && extId == e.extId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return extName.hashCode();
    }

    /**
     * set internal ID
     * 
     * @param id
     *        id
     */
    @PortedFrom(file = "tNamedEntry.h", name = "setId")
    public void setId(int id) {
        extId = id;
    }

    /** @return internal ID */
    @PortedFrom(file = "tNamedEntry.h", name = "getId")
    public int getId() {
        return extId;
    }

    @Override
    public String toString() {
        return extName + " " + extId + ' ' + entity + ' ' + bottom + ' ' + system + ' ' + top;
    }

    /** @return System flag */
    @Original
    public boolean isSystem() {
        return system;
    }

    /** set as system entry */
    @Original
    public void setSystem() {
        system = true;
    }

    // hierarchy interface
    /** @return Top-of-the-hierarchy flag */
    @Original
    public boolean isTop() {
        return top;
    }

    /** set as top entity */
    @Original
    public void setTop() {
        top = true;
    }

    /** @return Bottom-of-the-hierarchy */
    @Original
    public boolean isBottom() {
        return bottom;
    }

    /** set as bottom entity */
    @Original
    public void setBottom() {
        bottom = true;
    }

    /** @return entity */
    @Nullable
    @PortedFrom(file = "tNamedEntry.h", name = "getEntity")
    public NamedEntity getEntity() {
        return entity;
    }

    /**
     * @param entity
     *        entity
     */
    @PortedFrom(file = "tNamedEntry.h", name = "setEntity")
    public void setEntity(NamedEntity entity) {
        this.entity = entity;
    }

    /**
     * @param i
     *        i
     */
    @PortedFrom(file = "taxNamEntry.h", name = "setIndex")
    public abstract void setIndex(int i);

    /** @return index */
    @PortedFrom(file = "taxNamEntry.h", name = "getIndex")
    public abstract int getIndex();
}
