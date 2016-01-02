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

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.jfact.kernel.NamedEntry;

/** literal */
public class LiteralEntry extends NamedEntry {

    private Literal<?> literal;
    /** DAG index of the entry */
    private int pName;

    /**
     * create data entry with given name
     * 
     * @param name
     *        name
     */
    public LiteralEntry(IRI name) {
        super(name);
        pName = BP_INVALID;
    }

    /**
     * @param name
     *        name
     */
    public LiteralEntry(String name) {
        this(IRI.create(name));
    }

    /**
     * set host data type for the data value
     * 
     * @param l
     *        l
     */
    public void setLiteral(Literal<?> l) {
        literal = l;
    }

    /** @return literal */
    public Literal<?> getLiteral() {
        return literal;
    }

    /** @return host type */
    public Datatype<?> getType() {
        return literal.getDatatypeExpression();
    }

    /** @return set of facets */
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
    public void setIndex(int p) {
        pName = p;
    }

    @Override
    @Nonnull
    public String toString() {
        return '(' + this.getClass().getSimpleName() + ' ' + literal.toString() + super.toString() + ')';
    }

    @Override
    public int hashCode() {
        return literal.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof LiteralEntry)) {
            return false;
        }
        return literal.equals(((LiteralEntry) obj).literal);
    }

    /**
     * @param l
     *        literal
     * @return modified object
     */
    public LiteralEntry withLiteral(Literal<?> l) {
        setLiteral(l);
        return this;
    }

    /**
     * @param i
     *        index
     * @return modified object
     */
    public LiteralEntry withIndex(int i) {
        setIndex(i);
        return this;
    }
}
