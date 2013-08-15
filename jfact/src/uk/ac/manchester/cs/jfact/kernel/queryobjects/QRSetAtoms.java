package uk.ac.manchester.cs.jfact.kernel.queryobjects;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import conformance.Original;
import conformance.PortedFrom;

/** general QR conjunctions of atoms */
@PortedFrom(file = "QR.h", name = "QRSetAtoms")
public class QRSetAtoms implements Serializable {
    private static final long serialVersionUID = 11000L;
    @PortedFrom(file = "QR.h", name = "Base")
    private final List<QRAtom> Base = new ArrayList<QRAtom>();

    /**     */
    public QRSetAtoms() {
        // TODO Auto-generated constructor stub
    }

    /** @param q */
    public QRSetAtoms(QRSetAtoms q) {
        Base.addAll(q.Base);
    }

    /** replace an atom at a position P with NEWATOM;
     * 
     * @param i
     *            position of the element to replace
     * @param newAtom
     *            element to replace
     * @return a replaced atom */
    public QRAtom replaceAtom(int i, QRAtom newAtom) {
        return Base.set(i, newAtom);
    }

    /** add atom to a set
     * 
     * @param atom */
    @PortedFrom(file = "QR.h", name = "addAtom")
    public void addAtom(QRAtom atom) {
        Base.add(atom);
    }

    /** @return list of atoms */
    @Original
    public List<QRAtom> begin() {
        return Base;
    }
}
