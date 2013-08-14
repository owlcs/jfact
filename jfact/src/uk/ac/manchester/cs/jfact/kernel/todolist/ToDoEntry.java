package uk.ac.manchester.cs.jfact.kernel.todolist;

import java.io.Serializable;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.FastSetSimple;
import uk.ac.manchester.cs.jfact.kernel.ConceptWDep;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTree;

/** the entry of Todo table */
public class ToDoEntry implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** node to include concept */
    private final DlCompletionTree node;
    private final int concept;
    private final FastSetSimple delegate;

    protected ToDoEntry(DlCompletionTree n, ConceptWDep off) {
        node = n;
        concept = off.getConcept();
        delegate = off.getDep().getDelegate();
    }

    public DlCompletionTree getNode() {
        return node;
    }

    public int getOffsetConcept() {
        return concept;
    }

    public FastSetSimple getOffsetDepSet() {
        return delegate;
    }

    @Override
    public String toString() {
        return "Node(" + node.getId() + "), offset("
                + new ConceptWDep(concept, DepSet.create(delegate)) + ")";
    }
}
