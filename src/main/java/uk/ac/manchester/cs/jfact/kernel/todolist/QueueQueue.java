package uk.ac.manchester.cs.jfact.kernel.todolist;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.kernel.ConceptWDep;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTree;
import uk.ac.manchester.cs.jfact.kernel.Restorer;
import uk.ac.manchester.cs.jfact.kernel.SaveStackRare;

/** class to represent single priority queue */
public class QueueQueue implements Serializable {

    /** waiting ops queue */
    List<ToDoEntry> wait = new ArrayList<>();
    // / stack to save states for the overwritten queue
    SaveStackRare stack;
    /** start pointer; points to the 1st element in the queue */
    int sPointer = 0;
    private int size = 0;

    // type for restore the whole queue
    class QueueRestorer extends Restorer {

        // copy of a queue
        List<ToDoEntry> restorerWait = new ArrayList<>();
        // pointer to a queue to restore
        QueueQueue queue;
        // start pointer
        int sp;

        QueueRestorer(QueueQueue q) {
            restorerWait = q.wait;
            queue = q;
            sp = q.sPointer;
        }

        // restore: copy the queue back, adjust pointers
        @Override
        public void restore() {
            queue.wait = restorerWait;
            queue.sPointer = sp;
        }
    }

    /**
     * @param rare
     *        rare stack
     */
    public QueueQueue(SaveStackRare rare) {
        stack = rare;
    }

    /**
     * add entry to a queue
     * 
     * @param node
     *        Node
     * @param offset
     *        offset
     */
    protected void add(DlCompletionTree node, ConceptWDep offset) {
        ToDoEntry e = new ToDoEntry(node, offset);
        // no problems with empty queue and if no priority
        // clashes
        if (isEmpty() || wait.get(size - 1).getNode().getNominalLevel() <= node.getNominalLevel()) {
            wait.add(e);
            size++;
            return;
        }
        // here we need to put e on the proper place
        stack.push(new QueueRestorer(this));
        int n = size;
        while (n > sPointer && wait.get(n - 1).getNode().getNominalLevel() > node.getNominalLevel()) {
            --n;
        }
        wait.add(n, e);
        size++;
    }

    /** clear queue */
    @PortedFrom(file = "ToDoList.h", name = "clear")
    protected void clear() {
        sPointer = 0;
        wait.clear();
        size = 0;
    }

    /** @return true if queue empty */
    protected boolean isEmpty() {
        return sPointer == size;
    }

    /** @return get next entry from the queue; works for non-empty queues */
    protected ToDoEntry get() {
        return wait.get(sPointer++);
    }

    /**
     * save queue content to the given entry
     * 
     * @param tss
     *        tss
     */
    @PortedFrom(file = "ToDoList.h", name = "save")
    protected void save(TODOListSaveState tss) {
        tss.sp = sPointer;
        tss.ep = size;
    }

    /**
     * restore queue content from the given entry
     * 
     * @param tss
     *        tss
     */
    @PortedFrom(file = "ToDoList.h", name = "restore")
    protected void restore(TODOListSaveState tss) {
        sPointer = tss.sp;
        Helper.resize(wait, tss.ep, null);
        size = tss.ep;
    }

    @Override
    public String toString() {
        return "{" + (!isEmpty() ? wait.get(sPointer) : "empty") + " sPointer: " + sPointer + " size: " + size
            + " Wait: " + wait + '}';
    }
}
