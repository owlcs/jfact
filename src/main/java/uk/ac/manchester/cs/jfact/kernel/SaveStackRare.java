package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.INITBRANCHINGLEVELVALUE;

import java.io.Serializable;
import java.util.LinkedList;

import conformance.PortedFrom;

/**
 * Rare stack
 */
@PortedFrom(file = "tRareSaveStack.h", name = "TRareSaveStack")
public class SaveStackRare implements Serializable {


    /** heap of saved objects */
    private final LinkedList<Restorer> base = new LinkedList<>();
    /** current level */
    private int curLevel;

    /** Default constructor. */
    public SaveStackRare() {
        curLevel = INITBRANCHINGLEVELVALUE;
    }

    /** inclrement current level */
    public void incLevel() {
        ++curLevel;
    }

    /**
     * add a new object to the stack
     * 
     * @param p
     *        p
     */
    public void push(Restorer p) {
        p.setRaresavestackLevel(curLevel);
        base.addLast(p);
    }

    /**
     * get all object from the top of the stack with levels greater or equal
     * LEVEL
     * 
     * @param level
     *        level
     */
    @PortedFrom(file = "dlCompletionGraph.h", name = "restore")
    public void restore(int level) {
        curLevel = level;
        while (!base.isEmpty()
                && base.getLast().getRaresavestackLevel() > level) {
            // need to restore: restore last element, remove it from stack
            base.getLast().restore();
            base.removeLast();
        }
    }

    /** clear stack */
    @PortedFrom(file = "dlCompletionGraph.h", name = "clear")
    public void clear() {
        base.clear();
        curLevel = INITBRANCHINGLEVELVALUE;
    }
}
