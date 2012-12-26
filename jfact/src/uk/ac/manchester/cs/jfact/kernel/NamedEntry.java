package uk.ac.manchester.cs.jfact.kernel;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import conformance.PortedFrom;
/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/

@PortedFrom(file="tNamedEntry.h",name="TNamedEntry")
public abstract class NamedEntry {
    /** name of the entry */
    protected String extName;
    /** entry identifier */
    protected int extId;
    protected NamedEntity entity = null;

    // protected BitSet bits = new BitSet();
    public NamedEntry(String name) {
        assert name != null;
        extName = name;
        extId = 0; // sets local id
        if (extName.equals("TOP")) {
            top = true;
        }
        if (extName.equals("BOTTOM")) {
            bottom = true;
        }
    }

    /** gets name of given entry */
@PortedFrom(file="tNamedEntry.h",name="getName")
    public String getName() {
        return extName;
    }

    @Override
    public boolean equals(Object obj) {
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

    /** set internal ID */
@PortedFrom(file="tNamedEntry.h",name="setId")
    public void setId(int id) {
        extId = id;
    }

    /** get internal ID */
@PortedFrom(file="tNamedEntry.h",name="getId")
    public int getId() {
        return extId;
    }

    @Override
    public String toString() {
        return extName + " " + extId + " " + entity + " " + bottom + " " + system + " "
                + top;
    }

    private boolean system;

    /** a System flag */
    public boolean isSystem() {
        return system;
    }

    public void setSystem() {
        system = true;
    }

    private boolean top = false;

    // hierarchy interface
    /** a Top-of-the-hierarchy flag */
    public boolean isTop() {
        return top;
    }

    public void setTop() {
        top = true;
    }

    private boolean bottom;

    /** a Bottom-of-the-hierarchy flag */
    public boolean isBottom() {
        return bottom;
    }

    public void setBottom() {
        bottom = true;
    }

@PortedFrom(file="tNamedEntry.h",name="getEntity")
    public NamedEntity getEntity() {
        return entity;
    }

@PortedFrom(file="tNamedEntry.h",name="setEntity")
    public void setEntity(NamedEntity entity) {
        this.entity = entity;
    }

    public abstract void setIndex(int i);

    public abstract int getIndex();
}
