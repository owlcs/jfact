package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.stream.IntStream;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;

@PortedFrom(file = "dumpInterface.h", name = "dumpInterface")
class DumpInterface implements Serializable {

    /** output stream */
    @PortedFrom(file = "dumpInterface.h", name = "o") protected final LogAdapter o;
    /** indentation level */
    @PortedFrom(file = "dumpInterface.h", name = "indent") private int indent;
    /** print every axiom on a single line (need for sorting, for example) */
    @PortedFrom(file = "dumpInterface.h", name = "oneliner") private final boolean oneliner;

    @PortedFrom(file = "dumpInterface.h", name = "o")
    public DumpInterface(LogAdapter oo) {
        o = oo;
        indent = 0;
        oneliner = false;
    }

    // global prologue/epilogue
    @PortedFrom(file = "dumpInterface.h", name = "prologue")
    public void prologue() {
        // default implementation empty
    }

    @PortedFrom(file = "dumpInterface.h", name = "epilogue")
    public void epilogue() {
        // default implementation empty
    }

    // general concept expression
    @PortedFrom(file = "dumpInterface.h", name = "dumpTop")
    public void dumpTop() {
        // default implementation empty
    }

    @PortedFrom(file = "dumpInterface.h", name = "dumpBottom")
    public void dumpBottom() {
        // default implementation empty
    }

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "dumpNumber")
    public void dumpNumber(int n) {
        // default implementation empty
    }

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "startOp")
    public void startOp(DIOp op) {
        // default implementation empty
    }

    /**
     * start operation >=/<= with number
     * 
     * @param op
     *        Op
     * @param n
     *        n
     */
    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "startOp")
    public void startOp(DIOp op, int n) {
        // default implementation empty
    }

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "contOp")
    public void contOp(DIOp op) {
        // default implementation empty
    }

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "finishOp")
    public void finishOp(DIOp op) {
        // default implementation empty
    }

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "startAx")
    public void startAx(DIOp ax) {
        // default implementation empty
    }

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "contAx")
    public void contAx(DIOp ax) {
        // default implementation empty
    }

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "finishAx")
    public void finishAx(DIOp ax) {
        // default implementation empty
    }

    /**
     * obtain name by the named entry
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "dumpInterface.h", name = "dumpName")
    public void dumpName(NamedEntry p) {
        o.print(p.getIRI());
    }

    /**
     * dump concept atom (as used in expression)
     * 
     * @param p
     *        p
     */
    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "dumpConcept")
    public void dumpConcept(Concept p) {
        // default implementation empty
    }

    /**
     * dump role atom (as used in expression)
     * 
     * @param p
     *        p
     */
    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "dumpRole")
    public void dumpRole(Role p) {
        // default implementation empty
    }

    @PortedFrom(file = "dumpInterface.h", name = "skipIndent")
    public void skipIndent() {
        if (oneliner) {
            return;
        }
        o.print("\n");
        IntStream.rangeClosed(1, indent).forEach(i -> o.print("  "));
    }

    @PortedFrom(file = "dumpInterface.h", name = "incIndent")
    public void incIndent() {
        skipIndent();
        ++indent; // operands of AND-like
    }

    @PortedFrom(file = "dumpInterface.h", name = "decIndent")
    public void decIndent() {
        --indent;
        skipIndent();
    }
}
