package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.bpINVALID;

import java.util.Collection;

import uk.ac.manchester.cs.jfact.kernel.NamedEntry;

public class LiteralEntry extends NamedEntry {
    private Literal<?> literal;
    /** DAG index of the entry */
    private int pName;

    /** create data entry with given name */
    public LiteralEntry(final String name) {
        super(name);
        pName = bpINVALID;
    }

    /** set host data type for the data value */
    public void setLiteral(final Literal<?> l) {
        literal = l;
    }

    public Literal<?> getLiteral() {
        return literal;
    }

    /** get host type */
    public Datatype<?> getType() {
        return literal.getDatatypeExpression();
    }

    public Collection<Facet> getFacet() {
        return literal.getDatatypeExpression().getFacets();
    }

    /** get pointer to DAG entry corresponding to the data entry */
    @Override
    public int getIndex() {
        return pName;
    }

    /** set DAG index of the data entry */
    @Override
    public void setIndex(final int p) {
        pName = p;
    }

    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + " " + literal.toString()
                + super.toString() + ")";
    }
}
