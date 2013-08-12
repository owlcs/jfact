package uk.ac.manchester.cs.jfact.kernel.queryobjects;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import conformance.Original;
import conformance.PortedFrom;

/** class for the queries */
@PortedFrom(file = "QR.h", name = "QRQuery")
public class QRQuery implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** query as a set of atoms */
    @PortedFrom(file = "QR.h", name = "Body")
    private QRSetAtoms Body = new QRSetAtoms();
    /** set of free variables */
    @PortedFrom(file = "QR.h", name = "FreeVars")
    private Set<QRVariable> FreeVars = new TreeSet<QRVariable>();

    public QRQuery() {
        // TODO Auto-generated constructor stub
    }

    public QRQuery(QRQuery q) {
        Body = new QRSetAtoms(q.Body);
        for (QRVariable v : q.getFreeVars()) {
            getFreeVars().add(v.clone());
        }
    }

    /** @param var
     * @return true if VAR is a free var */
    public boolean isFreeVar(QRVariable var) {
        return var != null && getFreeVars().contains(var);
    }

    /** add atom to a query body
     * 
     * @param atom */
    @PortedFrom(file = "QR.h", name = "addAtom")
    public void addAtom(QRAtom atom) {
        Body.addAtom(atom);
    }

    /** mark a variable as a free one
     * 
     * @param var */
    @PortedFrom(file = "QR.h", name = "setVarFree")
    public void setVarFree(QRVariable var) {
        getFreeVars().add(var);
    }

    @Original
    public QRSetAtoms getBody() {
        return Body;
    }

    public Set<QRVariable> getFreeVars() {
        return FreeVars;
    }

    public void setFreeVars(Set<QRVariable> freeVars) {
        FreeVars = freeVars;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("FreeVars = {");
        for (QRVariable v : FreeVars) {
            b.append('\n').append(v.getName());
        }
        b.append("}\nQuery = {");
        for (QRAtom p : Body.begin()) {
            b.append('\n').append(p);
        }
        b.append(" }\n");
        return b.toString();
    }
}
