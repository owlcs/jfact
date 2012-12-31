package uk.ac.manchester.cs.jfact.kernel;

import java.io.PrintStream;

import conformance.PortedFrom;

@PortedFrom(file = "dumpInterface.h", name = "dumpInterface")
class DumpInterface {
    /** output stream */
    @PortedFrom(file = "dumpInterface.h", name = "o")
    protected PrintStream o;
    /** indentation level */
    @PortedFrom(file = "dumpInterface.h", name = "indent")
    private int indent;
    /** print every axiom on a single line (need for sorting, for example) */
    @PortedFrom(file = "dumpInterface.h", name = "oneliner")
    private boolean oneliner;

    @PortedFrom(file = "dumpInterface.h", name = "o")
    public DumpInterface(PrintStream oo) {
        o = oo;
        indent = 0;
        oneliner = false;
    }

    // global prologue/epilogue
    @PortedFrom(file = "dumpInterface.h", name = "prologue")
    public void prologue() {}

    @PortedFrom(file = "dumpInterface.h", name = "epilogue")
    public void epilogue() {}

    // general concept expression
    @PortedFrom(file = "dumpInterface.h", name = "dumpTop")
    public void dumpTop() {}

    @PortedFrom(file = "dumpInterface.h", name = "dumpBottom")
    public void dumpBottom() {}

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "dumpNumber")
    public void dumpNumber(int n) {}

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "startOp")
    public void startOp(DIOp Op) {}

    /** start operation >=/<= with number */
    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "startOp")
    public void startOp(DIOp Op, int n) {}

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "contOp")
    public void contOp(DIOp Op) {}

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "finishOp")
    public void finishOp(DIOp Op) {}

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "startAx")
    public void startAx(DIOp Ax) {}

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "contAx")
    public void contAx(DIOp Ax) {}

    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "finishAx")
    public void finishAx(DIOp Ax) {}

    /** obtain name by the named entry */
    @PortedFrom(file = "dumpInterface.h", name = "dumpName")
    public void dumpName(NamedEntry p) {
        o.print(p.getName());
    }

    /** dump concept atom (as used in expression) */
    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "dumpConcept")
    public void dumpConcept(Concept p) {}

    /** dump role atom (as used in expression) */
    @SuppressWarnings("unused")
    @PortedFrom(file = "dumpInterface.h", name = "dumpRole")
    public void dumpRole(Role p) {}

    @PortedFrom(file = "dumpInterface.h", name = "skipIndent")
    public void skipIndent() {
        if (oneliner) {
            return;
        }
        o.print("\n");
        for (int i = indent - 1; i >= 0; --i) {
            o.print("  ");
        }
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
