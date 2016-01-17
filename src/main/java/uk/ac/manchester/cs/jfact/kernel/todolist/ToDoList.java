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

import javax.annotation.Nullable;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.SaveStack;
import uk.ac.manchester.cs.jfact.kernel.ConceptWDep;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTree;
import uk.ac.manchester.cs.jfact.kernel.SaveStackRare;

/** todo list */
@PortedFrom(file = "ToDoList.h", name = "ToDoList")
public class ToDoList implements Serializable {

    /** waiting ops queue for IDs */
    @PortedFrom(file = "ToDoList.h", name = "queueID") private final ArrayQueue queueID = new ArrayQueue();
    /** waiting ops queue for lesser than or equal ops in nominal nodes */
    @PortedFrom(file = "ToDoList.h", name = "queueNN") private final QueueQueue queueNN;
    /** waiting ops queues */
    @PortedFrom(file = "ToDoList.h", name = "Wait") private final List<ArrayQueue> waitQueue = new ArrayList<>(
        NREGULAROPTIONS);
    /** stack of saved states */
    @PortedFrom(file = "ToDoList.h", name = "SaveStack") private final SaveStack<TODOListSaveState> saveStack = new SaveStack<>();
    /** priority matrix */
    @PortedFrom(file = "ToDoList.h", name = "Matrix") private final ToDoPriorMatrix matrix = new ToDoPriorMatrix();
    /** number of un-processed entries */
    @PortedFrom(file = "ToDoList.h", name = "noe") private int noe;

    /**
     * Default constructor.
     * 
     * @param r
     *        rare stack
     */
    public ToDoList(SaveStackRare r) {
        queueNN = new QueueQueue(r);
        noe = 0;
        for (int i = 0; i < NREGULAROPTIONS; i++) {
            waitQueue.add(new ArrayQueue());
        }
    }

    /**
     * save current Todo table content to given saveState entry
     * 
     * @param tss
     *        tss
     */
    @PortedFrom(file = "ToDoList.h", name = "saveState")
    public void saveState(TODOListSaveState tss) {
        tss.backupIDsp = queueID.getsPointer();
        tss.backupIDep = queueID.getWaitSize();
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
        queueID.restore(tss.backupIDsp, tss.backupIDep);
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
     * init priorities via Options
     * 
     * @param options
     *        Options
     */
    @Original
    public void initPriorities(String options) {
        matrix.initPriorities(options);
    }

    /** clear Todo table */
    @PortedFrom(file = "ToDoList.h", name = "clear")
    public void clear() {
        queueID.clear();
        queueNN.clear();
        for (int i = NREGULAROPTIONS - 1; i >= 0; --i) {
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
     * @param c
     *        C
     */
    @PortedFrom(file = "ToDoList.h", name = "addEntry")
    public void addEntry(DlCompletionTree node, DagTag type, ConceptWDep c) {
        int index = matrix.getIndex(type, c.getConcept() > 0, node.isNominalNode());
        switch (index) {
            case NREGULAROPTIONS: // unused entry
                return;
            case PRIORITYINDEXID: // ID
                queueID.add(node, c);
                break;
            case PRIORITYINDEXNOMINALNODE: // NN
                queueNN.add(node, c);
                break;
            default: // regular queue
                waitQueue.get(index).add(node, c);
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
    @Nullable
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
        for (int i = 0; i < NREGULAROPTIONS; ++i) {
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
        for (int i = 0; i < NREGULAROPTIONS; ++i) {
            l.append(waitQueue.get(i));
            l.append('\n');
        }
        l.append('\n');
        l.append('}');
        return l.toString();
    }
}
