package uk.ac.manchester.cs.jfact.kernel;

import java.io.PrintStream;

import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import conformance.PortedFrom;

@PortedFrom(file = "dumpLisp.h", name = "dumpLisp")
class DumpLisp extends DumpInterface {
    public DumpLisp(PrintStream oo) {
        super(oo);
    }

    // general concept expression
    @Override
    @PortedFrom(file = "dumpLisp.h", name = "dumpTop")
    public void dumpTop() {
        o.print("*TOP*");
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "dumpBottom")
    public void dumpBottom() {
        o.print("*BOTTOM*)");
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "dumpNumber")
    public void dumpNumber(int n) {
        o.print(n + " ");
    }

    /** start operation >=/<= with number */
    @Override
    @PortedFrom(file = "dumpLisp.h", name = "startOp")
    public void startOp(DIOp Op, int n) {
        this.startOp(Op);
        dumpNumber(n);
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "contOp")
    public void contOp(DIOp Op) {
        if (Op == DIOp.diAnd || Op == DIOp.diOr) {
            skipIndent();
        } else {
            o.print(" ");
        }
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "finishOp")
    public void finishOp(DIOp Op) {
        if (Op == DIOp.diAnd || Op == DIOp.diOr) {
            decIndent();
        }
        o.print(")");
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "contAx")
    public void contAx(DIOp Ax) {
        o.print(" ");
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "finishAx")
    public void finishAx(DIOp Ax) {
        o.print(")\n");
    }

    /** obtain name by the named entry */
    @Override
    @PortedFrom(file = "dumpLisp.h", name = "dumpName")
    public void dumpName(NamedEntry p) {
        o.print("|" + p.getName() + "|");
    }

    /** dump concept atom (as used in expression) */
    @Override
    @PortedFrom(file = "dumpLisp.h", name = "dumpConcept")
    public void dumpConcept(Concept p) {
        dumpName(p);
    }

    /** dump role atom (as used in expression) */
    @Override
    @PortedFrom(file = "dumpLisp.h", name = "dumpRole")
    public void dumpRole(Role p) {
        if (p.getId() < 0) // inverse
        {
            o.print("(inv ");
            dumpName(p.inverse());
            o.print(")");
        } else {
            dumpName(p);
        }
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "startOp")
    public void startOp(DIOp Op) {
        if (Op == DIOp.diAnd || Op == DIOp.diOr) {
            incIndent();
        }
        o.print("(");
        o.print(Op.getString());
        if (Op == DIOp.diEndOp) {
            throw new UnreachableSituationException();
        }
        contOp(Op);
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "startAx")
    public void startAx(DIOp Ax) {
        o.print("(");
        o.print(Ax.getString());
        if (Ax == DIOp.diEndOp) {
            throw new UnreachableSituationException();
        }
        contAx(Ax);
    }
}
