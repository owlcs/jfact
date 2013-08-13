package uk.ac.manchester.cs.jfact.kernel.todolist;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import conformance.PortedFrom;

/** class for saving/restoring array Todo table */
public  class TODOListSaveState implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** save state of all regular queues */
    protected final int[][] backup;
    /** save number-of-entries to do */
    @PortedFrom(file = "ToDoList.h", name = "noe")
    protected int noe;
    protected int backupID_sp;
    protected int backupID_ep;
    /** save whole array */
    protected List<ToDoEntry> waitingQueue;
    /** save start point of queue of entries */
    protected int sp;
    /** save end point of queue of entries */
    protected int ep;
    /** save flag of queue's consistency */
    protected boolean queueBroken;

    public TODOListSaveState(int options) {
        backup = new int[options][2];
    }

    @Override
    public String toString() {
        return "" + noe + " " + backupID_sp + "," + backupID_ep + " " + waitingQueue
                + " " + sp + " " + ep + " " + queueBroken + " "
                + Arrays.toString(backup);
    }
}
