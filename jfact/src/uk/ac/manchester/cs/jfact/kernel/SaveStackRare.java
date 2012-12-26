package uk.ac.manchester.cs.jfact.kernel;

import static uk.ac.manchester.cs.jfact.helpers.Helper.*;

import java.util.LinkedList;

import conformance.PortedFrom;

@PortedFrom(file = "tRareSaveStack.h", name = "TRareSaveStack")
class SaveStackRare {
    /** heap of saved objects */
    private LinkedList<Restorer> base = new LinkedList<Restorer>();
    /** current level */
    private int curLevel;

    public SaveStackRare() {
        curLevel = InitBranchingLevelValue;
    }

    /** inclrement current level */
    public void incLevel() {
        ++curLevel;
    }

    /** check that stack is empty */
    public boolean isEmpty() {
        return base.isEmpty();
    }

    /** add a new object to the stack */
    public void push(Restorer p) {
        p.setRaresavestackLevel(curLevel);
        base.addLast(p);
    }

    /** get all object from the top of the stack with levels >= LEVEL */
@PortedFrom(file="dlCompletionGraph.h",name="restore")
    public void restore(int level) {
        curLevel = level;
        while (base.size() > 0 && base.getLast().getRaresavestackLevel() > level) {
            // need to restore: restore last element, remove it from stack
            base.getLast().restore();
            base.removeLast();
        }
    }

    /** clear stack */
@PortedFrom(file="dlCompletionGraph.h",name="clear")
    public void clear() {
        base.clear();
        curLevel = InitBranchingLevelValue;
    }
}