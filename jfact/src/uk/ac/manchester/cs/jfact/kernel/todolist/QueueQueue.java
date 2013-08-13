package uk.ac.manchester.cs.jfact.kernel.todolist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.kernel.ConceptWDep;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTree;
import conformance.PortedFrom;

/** class to represent single priority queue */
public class QueueQueue implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** waiting ops queue */
    private List<ToDoEntry> _Wait = new ArrayList<ToDoEntry>();
    /** start pointer; points to the 1st element in the queue */
    private int sPointer = 0;
    /** flag for checking whether queue was reordered */
    private boolean queueBroken = false;
    private int size = 0;

    /** add entry to a queue */
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
        int n = size;
        while (n > sPointer
                && _Wait.get(n - 1).getNode().getNominalLevel() > Node
                        .getNominalLevel()) {
            --n;
        }
        _Wait.add(n, e);
        queueBroken = true;
        size++;
    }

    /** clear queue */
    @PortedFrom(file = "ToDoList.h", name = "clear")
    protected void clear() {
        sPointer = 0;
        queueBroken = false;
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
        tss.queueBroken = queueBroken;
        tss.sp = sPointer;
        if (queueBroken) {
            tss.waitingQueue = new ArrayList<ToDoEntry>(_Wait);
        } else {
            // save just end pointer
            tss.ep = size;
        }
        // clear flag for the next session
        queueBroken = false;
    }

    /** restore queue content from the given entry */
    @PortedFrom(file = "ToDoList.h", name = "restore")
    protected void restore(TODOListSaveState tss) {
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
