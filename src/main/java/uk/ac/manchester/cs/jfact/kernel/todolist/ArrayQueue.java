package uk.ac.manchester.cs.jfact.kernel.todolist;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.kernel.ConceptWDep;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTree;
import conformance.PortedFrom;

/** class to represent single queue */
public class ArrayQueue implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** waiting ops queue */
    private final List<ToDoEntry> Wait = new ArrayList<ToDoEntry>(50);
    /** start pointer; points to the 1st element in the queue */
    private int sPointer = 0;

    /**
     * add entry to a queue
     * 
     * @param node
     *        node
     * @param offset
     *        offset
     */
    public void add(DlCompletionTree node, ConceptWDep offset) {
        Wait.add(new ToDoEntry(node, offset));
    }

    /** clear queue */
    @PortedFrom(file = "ToDoList.h", name = "clear")
    public void clear() {
        setsPointer(0);
        Wait.clear();
    }

    /** @return check if queue empty */
    @PortedFrom(file = "ToDoList.h", name = "empty")
    public boolean isEmpty() {
        return sPointer == Wait.size();
    }

    /** @return next entry from the queue; works for non-empty queues */
    public ToDoEntry get() {
        return Wait.get(sPointer++);
    }

    /**
     * save queue content to the given entry
     * 
     * @param tss
     *        tss
     * @param pos
     *        pos
     */
    @PortedFrom(file = "ToDoList.h", name = "save")
    public void save(int[][] tss, int pos) {
        tss[pos][0] = sPointer;
        tss[pos][1] = Wait.size();
    }

    /**
     * restore queue content from the given entry
     * 
     * @param tss
     *        tss
     * @param pos
     *        pos
     */
    @PortedFrom(file = "ToDoList.h", name = "restore")
    public void restore(int[][] tss, int pos) {
        setsPointer(tss[pos][0]);
        Helper.resize(Wait, tss[pos][1]);
    }

    /**
     * @param sp
     *        sp
     * @param ep
     *        ep
     */
    @PortedFrom(file = "ToDoList.h", name = "restore")
    public void restore(int sp, int ep) {
        setsPointer(sp);
        Helper.resize(Wait, ep);
    }

    @Override
    public String toString() {
        StringBuilder l = new StringBuilder();
        l.append("ArrayQueue{");
        l.append(sPointer);
        l.append(',');
        for (ToDoEntry t : Wait) {
            l.append(t);
            l.append(' ');
        }
        l.append('}');
        return l.toString();
    }

    /** @return s pointer */
    public int getsPointer() {
        return sPointer;
    }

    /**
     * @param sPointer
     *        sPointer
     */
    public void setsPointer(int sPointer) {
        this.sPointer = sPointer;
    }

    /** @return wait size */
    public int getWaitSize() {
        return Wait.size();
    }
}
