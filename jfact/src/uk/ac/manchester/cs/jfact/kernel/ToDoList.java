package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.ToDoPriorMatrix.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.FastSetSimple;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.helpers.SaveStack;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;

public class ToDoList {
    static int limit = 500;
    protected TODOListSaveState[] states = new TODOListSaveState[limit];
    protected int nextState = 0;
    volatile boolean change = true;

    /** the entry of Todo table */
    public static class ToDoEntry {
        /** node to include concept */
        private DlCompletionTree node;
        private int concept;
        private FastSetSimple delegate;

        ToDoEntry(DlCompletionTree n, ConceptWDep off) {
            node = n;
            concept = off.getConcept();
            delegate = off.getDep().getDelegate();
        }

        protected DlCompletionTree getNode() {
            return node;
        }

        protected int getOffsetConcept() {
            return concept;
        }

        protected FastSetSimple getOffsetDepSet() {
            return delegate;
        }

        @Override
        public String toString() {
            return "Node(" + node.getId() + "), offset("
                    + new ConceptWDep(concept, DepSet.create(delegate)) + ")";
        }
    }

    /** class to represent single queue */
    static class ArrayQueue {
        /** waiting ops queue */
        List<ToDoEntry> Wait = new ArrayList<ToDoEntry>(50);
        /** start pointer; points to the 1st element in the queue */
        int sPointer = 0;

        /** add entry to a queue */
        public void add(DlCompletionTree node, ConceptWDep offset) {
            Wait.add(new ToDoEntry(node, offset));
        }

        /** clear queue */
        public void clear() {
            sPointer = 0;
            Wait.clear();
        }

        /** check if queue empty */
        public boolean isEmpty() {
            return sPointer == Wait.size();
        }

        /** get next entry from the queue; works for non-empty queues */
        public ToDoEntry get() {
            return Wait.get(sPointer++);
        }

        /** save queue content to the given entry */
        public void save(int[][] tss, int pos) {
            tss[pos][0] = sPointer;
            tss[pos][1] = Wait.size();
        }

        /** restore queue content from the given entry */
        public void restore(int[][] tss, int pos) {
            sPointer = tss[pos][0];
            Helper.resize(Wait, tss[pos][1]);
        }

        public void restore(int sp, int ep) {
            sPointer = sp;
            Helper.resize(Wait, ep);
        }

        @Override
        public String toString() {
            StringBuilder l = new StringBuilder();
            l.append("ArrayQueue{");
            l.append(sPointer);
            l.append(",");
            for (ToDoEntry t : Wait) {
                l.append(t);
                l.append(" ");
            }
            l.append("}");
            return l.toString();
        }
    }

    /** class for saving/restoring priority queue Todo */
    static class QueueQueueSaveState {
        /** save whole array */
        protected List<ToDoEntry> waitingQueue;
        /** save start point of queue of entries */
        protected int sp;
        /** save end point of queue of entries */
        protected int ep;
        /** save flag of queue's consistency */
        protected boolean queueBroken;
    }

    /** class to represent single priority queue */
    static class QueueQueue {
        /** waiting ops queue */
        private List<ToDoEntry> _Wait = new ArrayList<ToDoEntry>();
        /** start pointer; points to the 1st element in the queue */
        private int sPointer = 0;
        /** flag for checking whether queue was reordered */
        private boolean queueBroken = false;
        int size = 0;

        /** add entry to a queue */
        void add(DlCompletionTree Node, ConceptWDep offset) {
            // try {
            ToDoEntry e = new ToDoEntry(Node, offset);
            if (isEmpty() || // no problems with empty queue and if no priority
                             // clashes
                    _Wait.get(size - 1).getNode().getNominalLevel() <= Node
                            .getNominalLevel()) {
                _Wait.add(e);
                size++;
                return;
            }
            // here we need to put e on the proper place
            // int n = _Wait.size();
            int n = size;
            // _Wait.add(sPointer, e);
            // _Wait.add(e); // will be rewritten
            while (n > sPointer
                    && _Wait.get(n - 1).getNode().getNominalLevel() > Node
                            .getNominalLevel()) {
                // _Wait.set(n, _Wait.get(n - 1));
                --n;
            }
            _Wait.add(n, e);
            queueBroken = true;
            size++;
            // } finally {
            // size = _Wait.size();
            // }
        }

        /** clear queue */
        void clear() {
            sPointer = 0;
            queueBroken = false;
            _Wait.clear();
            size = 0;
        }

        /** check if queue empty */
        boolean isEmpty() {
            return sPointer == size;
        }

        /** get next entry from the queue; works for non-empty queues */
        ToDoEntry get() {
            return _Wait.get(sPointer++);
        }

        /** save queue content to the given entry */
        void save(QueueQueueSaveState tss) {
            tss.queueBroken = queueBroken;
            tss.sp = sPointer;
            if (queueBroken) {
                tss.waitingQueue = new ArrayList<ToDoEntry>(_Wait);
            } else {
                // save just end pointer
                tss.ep = size;
            }
            queueBroken = false; // clear flag for the next session
        }

        /** restore queue content from the given entry */
        void restore(QueueQueueSaveState tss) {
            queueBroken = tss.queueBroken;
            sPointer = tss.sp;
            if (queueBroken) {
                // the tss variable is discarded at the end of the restore, so
                // no need to copy
                _Wait = tss.waitingQueue;
                size = _Wait.size();
            } else {
                // save just end pointer
                Helper.resize(_Wait, tss.ep);
                size = tss.ep;
            }
        }

        @Override
        public String toString() {
            return "{" + (!isEmpty() ? _Wait.get(sPointer) : "empty") + " sPointer: "
                    + sPointer + " size: " + size + " Wait: " + _Wait + "}";
        }
    }

    /** class for saving/restoring array Todo table */
    static class TODOListSaveState {
        protected int backupID_sp;
        protected int backupID_ep;
        /** save state for queueNN */
        protected QueueQueueSaveState backupNN = new QueueQueueSaveState();
        /** save state of all regular queues */
        protected int[][] backup = new int[nRegularOptions][2];
        /** save number-of-entries to do */
        protected int noe;

        TODOListSaveState() {}

        @Override
        public String toString() {
            return "" + noe + " " + backupID_sp + "," + backupID_ep + " " + backupNN
                    + " " + Arrays.toString(backup);
        }
    }

    public TODOListSaveState getInstance() {
        // return new TODOListSaveState();
        if (nextState == limit) {
            nextState = 0;
        }
        TODOListSaveState toReturn = states[nextState];
        if (toReturn != null) {
            states[nextState++] = null;
            change = true;
            return toReturn;
        } else {
            // System.err.println("ToDoList.SaveState.getInstance() STILL waiting...");
            if (!isSaveStateGenerationStarted()) {
                startSaveStateGeneration();
            }
            return new TODOListSaveState();
        }
    }

    private boolean saveStateGenerationStarted = false;

    public boolean isSaveStateGenerationStarted() {
        return saveStateGenerationStarted;
    }

    public void startSaveStateGeneration() {
        saveStateGenerationStarted = true;
        Thread stateFiller = new Thread() {
            @Override
            public void run() {
                long last = System.currentTimeMillis();
                // timeout at one minute from last operation
                while (System.currentTimeMillis() - last < 60000) {
                    for (int wait = 0; wait < 10000; wait++) {
                        if (change) {
                            for (int i = 0; i < limit; i++) {
                                if (states[i] == null) {
                                    states[i] = new TODOListSaveState();
                                }
                            }
                            change = false;
                            last = System.currentTimeMillis();
                        }
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // after timeout elapses, reset the flag - new requests will
                // reactivate the thread
                saveStateGenerationStarted = false;
            }
        };
        stateFiller.setPriority(Thread.MIN_PRIORITY);
        stateFiller.setDaemon(true);
        stateFiller.start();
    }

    /** waiting ops queue for IDs */
    private ArrayQueue queueID = new ArrayQueue();
    /** waiting ops queue for <= ops in nominal nodes */
    private QueueQueue queueNN = new QueueQueue();
    /** waiting ops queues */
    private List<ArrayQueue> waitQueue = new ArrayList<ArrayQueue>(nRegularOptions);
    /** stack of saved states */
    private SaveStack<TODOListSaveState> saveStack = new SaveStack<TODOListSaveState>();
    /** priority matrix */
    private ToDoPriorMatrix matrix = new ToDoPriorMatrix();
    /** number of un-processed entries */
    private int noe;

    /** save current Todo table content to given saveState entry */
    public void saveState(TODOListSaveState tss) {
        tss.backupID_sp = queueID.sPointer;
        tss.backupID_ep = queueID.Wait.size();
        queueNN.save(tss.backupNN);
        for (int i = nRegularOptions - 1; i >= 0; --i) {
            waitQueue.get(i).save(tss.backup, i);
        }
        tss.noe = noe;
    }

    /** restore Todo table content from given saveState entry */
    public void restoreState(TODOListSaveState tss) {
        queueID.restore(tss.backupID_sp, tss.backupID_ep);
        queueNN.restore(tss.backupNN);
        for (int i = nRegularOptions - 1; i >= 0; --i) {
            waitQueue.get(i).restore(tss.backup[i][0], tss.backup[i][1]);
        }
        noe = tss.noe;
    }

    public ToDoList() {
        noe = 0;
        // Helper.resize(Wait, nRegularOps);
        for (int i = 0; i < nRegularOptions; i++) {
            waitQueue.add(new ArrayQueue());
        }
    }

    /** init priorities via Options */
    public void initPriorities(String Options) {
        matrix.initPriorities(Options, "IAOEFLG");
    }

    /** clear Todo table */
    public void clear() {
        queueID.clear();
        queueNN.clear();
        for (int i = nRegularOptions - 1; i >= 0; --i) {
            waitQueue.get(i).clear();
        }
        saveStack.clear();
        noe = 0;
    }

    /** check if Todo table is empty */
    public boolean isEmpty() {
        return noe == 0;
    }

    // work with entries
    /** add entry with given NODE and CONCEPT with given OFFSET to the Todo table */
    public void addEntry(DlCompletionTree node, DagTag type, ConceptWDep C) {
        int index = matrix.getIndex(type, C.getConcept() > 0, node.isNominalNode());
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
    public void save() {
        TODOListSaveState state = getInstance();
        saveState(state);
        saveStack.push(state);
    }

    /** restore state to the given level using internal stack */
    public void restore(int level) {
        restoreState(saveStack.pop(level));
    }

    public ToDoEntry getNextEntry() {
        assert !isEmpty(); // safety check
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
        l.append("\n");
        l.append(queueID);
        l.append("\n");
        for (int i = 0; i < nRegularOptions; ++i) {
            l.append(waitQueue.get(i));
            l.append("\n");
        }
        l.append("\n");
        l.append("}");
        return l.toString();
    }
}

class ToDoPriorMatrix {
    // regular operation indexes
    private int indexAnd;
    private int indexOr;
    private int indexExists;
    private int indexForall;
    private int indexLE;
    private int indexGE;

    public ToDoPriorMatrix() {}

    /** number of regular options (o- and NN-rules are not included) */
    protected static final int nRegularOptions = 7;
    /** priority index for o- and ID operations (note that these ops have the
     * highest priority) */
    protected static final int priorityIndexID = nRegularOptions + 1;
    /** priority index for <= operation in nominal node */
    protected static final int priorityIndexNominalNode = nRegularOptions + 2;

    /** Auxiliary class to get priorities on operations */
    public void initPriorities(String options, String optionName) {
        // check for correctness
        if (options.length() < 7) {
            throw new ReasonerInternalException(
                    "ToDo List option string should have length 7");
        }
        // init values by symbols loaded
        indexAnd = options.charAt(1) - '0';
        indexOr = options.charAt(2) - '0';
        indexExists = options.charAt(3) - '0';
        indexForall = options.charAt(4) - '0';
        indexLE = options.charAt(5) - '0';
        indexGE = options.charAt(6) - '0';
        // correctness checking
        if (indexAnd >= nRegularOptions || indexOr >= nRegularOptions
                || indexExists >= nRegularOptions || indexForall >= nRegularOptions
                || indexGE >= nRegularOptions || indexLE >= nRegularOptions) {
            throw new ReasonerInternalException("ToDo List option out of range");
        }
    }

    public int getIndex(DagTag Op, boolean Sign, boolean NominalNode) {
        switch (Op) {
            case dtAnd:
                return Sign ? indexAnd : indexOr;
            case dtSplitConcept:
                return indexAnd;
            case dtForall:
            case dtIrr: // process local (ir-)reflexivity as a FORALL
                return Sign ? indexForall : indexExists;
            case dtProj: // it should be the lowest priority but now just OR's
                         // one
            case dtChoose:
                return indexOr;
            case dtLE:
                return Sign ? NominalNode ? priorityIndexNominalNode : indexLE : indexGE;
            case dtDataType:
            case dtDataValue:
            case dtDataExpr:
            case dtNN:
            case dtTop: // no need to process these ops
                return nRegularOptions;
            case dtPSingleton:
            case dtPConcept: // no need to process neg of PC
                return Sign ? priorityIndexID : nRegularOptions;
            case dtNSingleton:
            case dtNConcept: // both NC and neg NC are processed
                return priorityIndexID;
            default: // safety check
                throw new UnreachableSituationException();
        }
    }
}
