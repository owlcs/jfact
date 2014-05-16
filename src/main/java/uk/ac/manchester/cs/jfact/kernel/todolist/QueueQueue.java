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
import uk.ac.manchester.cs.jfact.kernel.Restorer;
import uk.ac.manchester.cs.jfact.kernel.SaveStackRare;
import conformance.PortedFrom;

/** class to represent single priority queue */
public class QueueQueue implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** waiting ops queue */
    private List<ToDoEntry> _Wait = new ArrayList<ToDoEntry>();
    // / stack to save states for the overwritten queue
    SaveStackRare stack;
    /** start pointer; points to the 1st element in the queue */
    private int sPointer = 0;
    private int size = 0;
    // type for restore the whole queue
    class QueueRestorer extends Restorer {

        // copy of a queue
        List<ToDoEntry> Wait = new ArrayList<ToDoEntry>();
        // pointer to a queue to restore
        QueueQueue queue;
        // start pointer
        int sp;

        QueueRestorer(QueueQueue q) {
            Wait = q._Wait;
            queue = q;
            sp = q.sPointer;
        }

        // restore: copy the queue back, adjust pointers
        public void restore() {
            queue._Wait = Wait;
            queue.sPointer = sp;
        }
    }

    public QueueQueue(SaveStackRare rare) {
        stack = rare;
    }

    /**
     * add entry to a queue
     * 
     * @param Node
     *        Node
     * @param offset
     *        offset
     */
    protected void add(DlCompletionTree Node, ConceptWDep offset) {
        ToDoEntry e = new ToDoEntry(Node, offset);
        // no problems with empty queue and if no priority
        // clashes
        if (isEmpty()
                || _Wait.get(size - 1).getNode().getNominalLevel() <= Node
                        .getNominalLevel()) {
            _Wait.add(e);
            size++;
            return;
        }
        // here we need to put e on the proper place
        stack.push(new QueueRestorer(this));
        int n = size;
        while (n > sPointer
                && _Wait.get(n - 1).getNode().getNominalLevel() > Node.getNominalLevel()) {
            --n;
        }
        _Wait.add(n, e);
        size++;
    }

    /** clear queue */
    @PortedFrom(file = "ToDoList.h", name = "clear")
    protected void clear() {
        sPointer = 0;
        _Wait.clear();
        size = 0;
    }

    /** check if queue empty */
    protected boolean isEmpty() {
        return sPointer == size;
    }

    /** get next entry from the queue; works for non-empty queues */
    protected ToDoEntry get() {
        return _Wait.get(sPointer++);
    }

    /** save queue content to the given entry */
    @PortedFrom(file = "ToDoList.h", name = "save")
    protected void save(TODOListSaveState tss) {
        tss.sp = sPointer;
        tss.ep = size;
    }

    /** restore queue content from the given entry */
    @PortedFrom(file = "ToDoList.h", name = "restore")
    protected void restore(TODOListSaveState tss) {
        sPointer = tss.sp;
        Helper.resize(_Wait, tss.ep);
        size = tss.ep;
    }

    @Override
    public String toString() {
        return "{" + (!isEmpty() ? _Wait.get(sPointer) : "empty") + " sPointer: "
                + sPointer + " size: " + size + " Wait: " + _Wait + "}";
    }
}
