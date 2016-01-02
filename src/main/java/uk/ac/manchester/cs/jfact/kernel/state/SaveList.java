package uk.ac.manchester.cs.jfact.kernel.state;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.LinkedList;

import conformance.PortedFrom;

/** save list */
@PortedFrom(file = "tSaveList.h", name = "TSaveList")
public class SaveList extends LinkedList<DLCompletionTreeSaveState> {



    @Override
    @PortedFrom(file = "tSaveList.h", name = "pop")
    public DLCompletionTreeSaveState pop() {
        if (!isEmpty()) {
            return super.pop();
        }
        return null;
    }

    /**
     * @param level
     *        level
     * @return element from stack with given level
     */
    @PortedFrom(file = "tSaveList.h", name = "pop")
    public DLCompletionTreeSaveState pop(int level) {
        DLCompletionTreeSaveState p = isEmpty() ? null : peek();
        while (p != null && p.level() > level) {
            this.pop();
            p = peek();
        }
        // here p==head and either both == NULL or points to proper element
        if (p != null) {
            this.pop();
        }
        return p;
    }
}
