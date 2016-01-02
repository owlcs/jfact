package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** possible flags of re-checking ALL-like expressions in new nodes */
enum Redo {
    REDOFORALL(1), REDOFUNC(2), REDOATMOST(4), REDOIRR(8);

    private final int value;

    Redo(int i) {
        value = i;
    }

    public boolean match(int flags) {
        return (flags & value) > 0;
    }

    protected int getValue() {
        return value;
    }

    public static int redoForallFuncAtmostIrr() {
        return REDOFORALL.value | REDOFUNC.value | REDOATMOST.value
                | REDOIRR.value;
    }

    public static int redoForallAtmost() {
        return REDOFORALL.value | REDOATMOST.value;
    }

    public static int redoForallFunc() {
        return REDOFORALL.value | REDOFUNC.value;
    }

    public static int redoForallFuncAtMost() {
        return REDOFORALL.value | REDOFUNC.value | REDOATMOST.value;
    }

    public static int redoFuncAtMost() {
        return REDOFUNC.value | REDOATMOST.value;
    }
}
