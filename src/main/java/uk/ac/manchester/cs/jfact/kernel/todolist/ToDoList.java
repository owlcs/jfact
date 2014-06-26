package uk.ac.manchester.cs.jfact.kernel.todolist;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.todolist.ToDoPriorMatrix.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.helpers.SaveStack;
import uk.ac.manchester.cs.jfact.kernel.ConceptWDep;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTree;
import uk.ac.manchester.cs.jfact.kernel.SaveStackRare;
import conformance.Original;
import conformance.PortedFrom;

/** todo list */
@PortedFrom(file = "ToDoList.h", name = "ToDoList")
public class ToDoList implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** waiting ops queue for IDs */
    @PortedFrom(file = "ToDoList.h", name = "queueID")
    private final ArrayQueue queueID = new ArrayQueue();
    /** waiting ops queue for lesser than or equal ops in nominal nodes */
    @PortedFrom(file = "ToDoList.h", name = "queueNN")
    private final QueueQueue queueNN;
    /** waiting ops queues */
    @PortedFrom(file = "ToDoList.h", name = "Wait")
    private final List<ArrayQueue> waitQueue = new ArrayList<>(nRegularOptions);
    /** stack of saved states */
    @PortedFrom(file = "ToDoList.h", name = "SaveStack")
    private final SaveStack<TODOListSaveState> saveStack = new SaveStack<>();
    /** priority matrix */
    @PortedFrom(file = "ToDoList.h", name = "Matrix")
    private final ToDoPriorMatrix matrix = new ToDoPriorMatrix();
    /** number of un-processed entries */
    @PortedFrom(file = "ToDoList.h", name = "noe")
    private int noe;

    /**
     * save current Todo table content to given saveState entry
     * 
     * @param tss
     *        tss
     */
    @PortedFrom(file = "ToDoList.h", name = "saveState")
    public void saveState(TODOListSaveState tss) {
        tss.backupID_sp = queueID.getsPointer();
        tss.backupID_ep = queueID.getWaitSize();
        queueNN.save(tss);
        tss.backup6key = waitQueue.get(6).getsPointer();
        tss.backup6value = waitQueue.get(6).getWaitSize();
        tss.backup5key = waitQueue.get(5).getsPointer();
        tss.backup5value = waitQueue.get(5).getWaitSize();
        tss.backup4key = waitQueue.get(4).getsPointer();
        tss.backup4value = waitQueue.get(4).getWaitSize();
        tss.backup3key = waitQueue.get(3).getsPointer();
        tss.backup3value = waitQueue.get(3).getWaitSize();
        tss.backup2key = waitQueue.get(2).getsPointer();
        tss.backup2value = waitQueue.get(2).getWaitSize();
        tss.backup1key = waitQueue.get(1).getsPointer();
        tss.backup1value = waitQueue.get(1).getWaitSize();
        tss.backup0key = waitQueue.get(0).getsPointer();
        tss.backup0value = waitQueue.get(0).getWaitSize();
        tss.noe = noe;
    }

    /**
     * restore Todo table content from given saveState entry
     * 
     * @param tss
     *        tss
     */
    @PortedFrom(file = "ToDoList.h", name = "restoreState")
    public void restoreState(TODOListSaveState tss) {
        queueID.restore(tss.backupID_sp, tss.backupID_ep);
        queueNN.restore(tss);
        waitQueue.get(0).restore(tss.backup0key, tss.backup0value);
        waitQueue.get(1).restore(tss.backup1key, tss.backup1value);
        waitQueue.get(2).restore(tss.backup2key, tss.backup2value);
        waitQueue.get(3).restore(tss.backup3key, tss.backup3value);
        waitQueue.get(4).restore(tss.backup4key, tss.backup4value);
        waitQueue.get(5).restore(tss.backup5key, tss.backup5value);
        waitQueue.get(6).restore(tss.backup6key, tss.backup6value);
        noe = tss.noe;
    }

    /**
     * default constructor
     * 
     * @param r
     *        rare stack
     */
    public ToDoList(SaveStackRare r) {
        queueNN = new QueueQueue(r);
        noe = 0;
        // Helper.resize(Wait, nRegularOps);
        for (int i = 0; i < nRegularOptions; i++) {
            waitQueue.add(new ArrayQueue());
        }
    }

    /**
     * init priorities via Options
     * 
     * @param Options
     *        Options
     */
    @Original
    public void initPriorities(String Options) {
        matrix.initPriorities(Options);
    }

    /** clear Todo table */
    @PortedFrom(file = "ToDoList.h", name = "clear")
    public void clear() {
        queueID.clear();
        queueNN.clear();
        for (int i = nRegularOptions - 1; i >= 0; --i) {
            waitQueue.get(i).clear();
        }
        saveStack.clear();
        noe = 0;
    }

    /** @return check if Todo table is empty */
    public boolean isEmpty() {
        return noe == 0;
    }

    // work with entries
    /**
     * add entry with given NODE and CONCEPT with given OFFSET to the Todo table
     * 
     * @param node
     *        node
     * @param type
     *        type
     * @param C
     *        C
     */
    @PortedFrom(file = "ToDoList.h", name = "addEntry")
    public void addEntry(DlCompletionTree node, DagTag type, ConceptWDep C) {
        int index = matrix.getIndex(type, C.getConcept() > 0,
                node.isNominalNode());
        switch (index) {
            case nRegularOptions: // unused entry
                return;
            case priorityIndexID: // ID
                queueID.add(node, C);
                break;
            case priorityIndexNominalNode: // NN
                queueNN.add(node, C);
                break;
            default: // regular queue
                waitQueue.get(index).add(node, C);
                break;
        }
        ++noe;
    }

    /** save current state using internal stack */
    @PortedFrom(file = "ToDoList.h", name = "save")
    public void save() {
        TODOListSaveState state = new TODOListSaveState();
        saveState(state);
        saveStack.push(state);
    }

    /**
     * restore state to the given level using internal stack
     * 
     * @param level
     *        level
     */
    @PortedFrom(file = "ToDoList.h", name = "restore")
    public void restore(int level) {
        restoreState(saveStack.pop(level));
    }

    /** @return next entry */
    @PortedFrom(file = "ToDoList.h", name = "getNextEntry")
    public ToDoEntry getNextEntry() {
        assert !isEmpty();
        // decrease amount of elements-to-process
        --noe;
        // check ID queue
        if (!queueID.isEmpty()) {
            return queueID.get();
        }
        // check NN queue
        if (!queueNN.isEmpty()) {
            return queueNN.get();
        }
        // check regular queues
        for (int i = 0; i < nRegularOptions; ++i) {
            ArrayQueue arrayQueue = waitQueue.get(i);
            if (!arrayQueue.isEmpty()) {
                return arrayQueue.get();
            }
        }
        // that's impossible, but still...
        return null;
    }

    @Override
    public String toString() {
        StringBuilder l = new StringBuilder("Todolist{");
        l.append('\n');
        l.append(queueID);
        l.append('\n');
        for (int i = 0; i < nRegularOptions; ++i) {
            l.append(waitQueue.get(i));
            l.append('\n');
        }
        l.append('\n');
        l.append('}');
        return l.toString();
    }
}
