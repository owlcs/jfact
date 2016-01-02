package uk.ac.manchester.cs.jfact.kernel.todolist;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import org.roaringbitmap.RoaringBitmap;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.kernel.ConceptWDep;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTree;

/** the entry of Todo table */
public class ToDoEntry implements Serializable {


    /** node to include concept */
    private final DlCompletionTree node;
    private final int concept;
    private final RoaringBitmap delegate;

    protected ToDoEntry(DlCompletionTree n, ConceptWDep off) {
        node = n;
        concept = off.getConcept();
        delegate = off.getDep().getDelegate();
    }

    /** @return node */
    public DlCompletionTree getNode() {
        return node;
    }

    /** @return concept offset */
    public int getOffsetConcept() {
        return concept;
    }

    /** @return offset dep set */
    public RoaringBitmap getOffsetDepSet() {
        return delegate;
    }

    @Override
    public String toString() {
        return "Node(" + node.getId() + "), offset("
            + new ConceptWDep(concept, DepSet.create(delegate)) + ')';
    }
}
