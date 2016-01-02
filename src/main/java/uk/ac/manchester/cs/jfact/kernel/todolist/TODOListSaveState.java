package uk.ac.manchester.cs.jfact.kernel.todolist;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.List;

import conformance.PortedFrom;

/** class for saving/restoring array Todo table */
public final class TODOListSaveState implements Serializable {

    // save state of all regular queues
    /** key queue 0 */
    public int backup0key;
    /** key queue 0 */
    public int backup0value;
    /** key queue 1 */
    public int backup1key;
    /** key queue 1 */
    public int backup1value;
    /** key queue 2 */
    public int backup2key;
    /** key queue 2 */
    public int backup2value;
    /** key queue 3 */
    public int backup3key;
    /** key queue 3 */
    public int backup3value;
    /** key queue 4 */
    public int backup4key;
    /** key queue 4 */
    public int backup4value;
    /** key queue 5 */
    public int backup5key;
    /** key queue 5 */
    public int backup5value;
    /** key queue 6 */
    public int backup6key;
    /** value queue 6 */
    public int backup6value;
    /** save number-of-entries to do */
    @PortedFrom(file = "ToDoList.h", name = "noe") protected int noe;
    protected int backupIDsp;
    protected int backupIDep;
    /** save whole array */
    protected List<ToDoEntry> waitingQueue;
    /** save start point of queue of entries */
    protected int sp;
    /** save end point of queue of entries */
    protected int ep;
    /** save flag of queue's consistency */
    protected boolean queueBroken;

    @Override
    public String toString() {
        return noe + " " + backupIDsp + ',' + backupIDep + ' ' + waitingQueue + ' ' + sp + ' ' + ep + ' ' + queueBroken
            + ' ' + backup0key + ' ' + backup0value + ' ' + backup1key + ' ' + backup1value + ' ' + backup2key + ' '
            + backup2value + ' ' + backup3key + ' ' + backup3value + ' ' + backup4key + ' ' + backup4value + ' '
            + backup5key + ' ' + backup5value + ' ' + backup6key + ' ' + backup6value;
    }
}
