package uk.ac.manchester.cs.jfact.helpers;

import java.io.Serializable;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/**
 * Typed strong reference
 * 
 * @author ignazio
 * @param <E>
 *        type
 */
public class Reference<E> implements Serializable {

    private static final long serialVersionUID = 11000L;
    private E e;

    /** default constructor */
    public Reference() {}

    /**
     * @param e
     *        e
     */
    public Reference(E e) {
        setReference(e);
    }

    /**
     * @param e
     *        e
     */
    public void setReference(E e) {
        this.e = e;
    }

    /** @return object */
    public E getReference() {
        return e;
    }

    @Override
    public String toString() {
        if (e != null) {
            return "ref(" + e.toString() + ")";
        }
        return "ref(null)";
    }

    @Override
    public int hashCode() {
        return e != null ? e.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 == null) {
            return false;
        }
        if (this == arg0) {
            return true;
        }
        if (arg0 instanceof Reference) {
            return e != null ? e.equals(((Reference<?>) arg0).getReference())
                    : false;
        }
        return false;
    }
}
