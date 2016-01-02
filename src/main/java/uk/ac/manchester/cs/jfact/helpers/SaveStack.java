package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.LinkedList;

import conformance.Original;
import conformance.PortedFrom;

/**
 * save stack
 * 
 * @param <T>
 *        type
 */
@PortedFrom(file = "tSaveStack.h", name = "TSaveStack")
public class SaveStack<T> implements Serializable {


    @Original
    protected final LinkedList<T> list = new LinkedList<>();

    /**
     * @param depth
     *        depth
     * @return an object from a fixed depth
     */
    @PortedFrom(file = "tSaveStack.h", name = "pop")
    public T pop(int depth) {
        top(depth);
        return pop();
    }

    /**
     * @param depth
     *        depth
     * @return an object from a fixed depth
     */
    @PortedFrom(file = "tSaveStack.h", name = "top")
    public T top(int depth) {
        assert list.size() >= depth;
        while (list.size() > depth) {
            pop();
        }
        return list.peek();
    }

    /** @return pop stack */
    @PortedFrom(file = "tSaveStack.h", name = "pop")
    public T pop() {
        assert !list.isEmpty();
        return list.pop();
    }

    /**
     * @param e
     *        e
     */
    @PortedFrom(file = "tSaveStack.h", name = "push")
    public void push(T e) {
        list.push(e);
    }

    /** clear the stack */
    @Original
    public void clear() {
        list.clear();
    }

    /** @return true if is empty */
    @PortedFrom(file = "tSaveStack.h", name = "empty")
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
