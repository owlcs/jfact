package uk.ac.manchester.cs.jfact.kernel.todolist;

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

    /** add entry to a queue
     * 
     * @param node
     * @param offset */
    public void add(DlCompletionTree node, ConceptWDep offset) {
        getWait().add(new ToDoEntry(node, offset));
    }

    /** clear queue */
    @PortedFrom(file = "ToDoList.h", name = "clear")
    public void clear() {
        setsPointer(0);
        getWait().clear();
    }

    /** @return check if queue empty */
    @PortedFrom(file = "ToDoList.h", name = "empty")
    public boolean isEmpty() {
        return sPointer == getWait().size();
    }

    /** @return next entry from the queue; works for non-empty queues */
    public ToDoEntry get() {
        return getWait().get(sPointer++);
    }

    /** save queue content to the given entry
     * 
     * @param tss
     * @param pos */
    @PortedFrom(file = "ToDoList.h", name = "save")
    public void save(int[][] tss, int pos) {
        tss[pos][0] = sPointer;
        tss[pos][1] = getWait().size();
    }

    /** restore queue content from the given entry
     * 
     * @param tss
     * @param pos */
    @PortedFrom(file = "ToDoList.h", name = "restore")
    public void restore(int[][] tss, int pos) {
        setsPointer(tss[pos][0]);
        Helper.resize(getWait(), tss[pos][1]);
    }

    /** @param sp
     * @param ep */
    @PortedFrom(file = "ToDoList.h", name = "restore")
    public void restore(int sp, int ep) {
        setsPointer(sp);
        Helper.resize(getWait(), ep);
    }

    @Override
    public String toString() {
        StringBuilder l = new StringBuilder();
        l.append("ArrayQueue{");
        l.append(sPointer);
        l.append(",");
        for (ToDoEntry t : getWait()) {
            l.append(t);
            l.append(" ");
        }
        l.append("}");
        return l.toString();
    }

    /** @return s pointer */
    public int getsPointer() {
        return sPointer;
    }

    public void setsPointer(int sPointer) {
        this.sPointer = sPointer;
    }

    public List<ToDoEntry> getWait() {
        return Wait;
    }
}
