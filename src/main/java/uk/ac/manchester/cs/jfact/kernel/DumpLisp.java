package uk.ac.manchester.cs.jfact.kernel;

import conformance.PortedFrom;
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;

@PortedFrom(file = "dumpLisp.h", name = "dumpLisp")
class DumpLisp extends DumpInterface {

    public DumpLisp(LogAdapter oo) {
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
    public void startOp(DIOp op, int n) {
        this.startOp(op);
        dumpNumber(n);
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "contOp")
    public void contOp(DIOp op) {
        if (op == DIOp.AND || op == DIOp.OR) {
            skipIndent();
        } else {
            o.print(" ");
        }
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "finishOp")
    public void finishOp(DIOp op) {
        if (op == DIOp.AND || op == DIOp.OR) {
            decIndent();
        }
        o.print(")");
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "contAx")
    public void contAx(DIOp ax) {
        o.print(" ");
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "finishAx")
    public void finishAx(DIOp ax) {
        o.print(")\n");
    }

    /** obtain name by the named entry */
    @Override
    @PortedFrom(file = "dumpLisp.h", name = "dumpName")
    public void dumpName(NamedEntry p) {
        o.print("|" + p.getIRI() + '|');
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
    public void startOp(DIOp op) {
        if (op == DIOp.AND || op == DIOp.OR) {
            incIndent();
        }
        o.print("(");
        o.print(op.getString());
        if (op == DIOp.ENDOP) {
            throw new UnreachableSituationException();
        }
        contOp(op);
    }

    @Override
    @PortedFrom(file = "dumpLisp.h", name = "startAx")
    public void startAx(DIOp ax) {
        o.print("(");
        o.print(ax.getString());
        if (ax == DIOp.ENDOP) {
            throw new UnreachableSituationException();
        }
        contAx(ax);
    }
}
