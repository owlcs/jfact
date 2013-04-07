package uk.ac.manchester.cs.jfact.kernel.queryobjects;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.HashSet;
import java.util.Set;

import conformance.PortedFrom;

/** class for the queries */
@PortedFrom(file = "QR.h", name = "QRQuery")
class QRQuery {
    /** query as a set of atoms */
    @PortedFrom(file = "QR.h", name = "Body")
    QRSetAtoms Body = new QRSetAtoms();
    /** set of free variables */
    @PortedFrom(file = "QR.h", name = "FreeVars")
    Set<QRVariable> FreeVars = new HashSet<QRVariable>();

    public QRQuery() {
        // TODO Auto-generated constructor stub
    }

    public QRQuery(QRQuery q) {
        Body = new QRSetAtoms(q.Body);
        for (QRVariable v : q.FreeVars) {
            FreeVars.add(v.clone());
        }
    }

    /** @return true if VAR is a free var */
    boolean isFreeVar(QRVariable var) {
        return FreeVars.contains(var);
    }

    /** add atom to a query body */
    @PortedFrom(file = "QR.h", name = "addAtom")
    void addAtom(QRAtom atom) {
        Body.addAtom(atom);
    }

    /** mark a variable as a free one */
    @PortedFrom(file = "QR.h", name = "setVarFree")
    void setVarFree(QRVariable var) {
        FreeVars.add(var);
    }
}
