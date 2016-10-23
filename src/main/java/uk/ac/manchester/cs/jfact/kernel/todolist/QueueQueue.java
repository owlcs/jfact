package uk.ac.manchester.cs.jfact.kernel.todolist;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.kernel.ConceptWDep;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTree;
import uk.ac.manchester.cs.jfact.kernel.SaveStackRare;

/** class to represent single priority queue */
public class QueueQueue extends ArrayQueue {

    // stack to save states for the overwritten queue
    SaveStackRare stack;
    /** start pointer; points to the 1st element in the queue */
    private int size = 0;

    /**
     * @param rare
     *        rare stack
     */
    public QueueQueue(SaveStackRare rare) {
        stack = rare;
    }

    @Override
    public void add(DlCompletionTree node, ConceptWDep offset) {
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

    @Override
    @PortedFrom(file = "ToDoList.h", name = "clear")
    public void clear() {
        super.clear();
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return sPointer == size;
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
