package uk.ac.manchester.cs.jfact.kernel.queryobjects;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import conformance.PortedFrom;

/** inequality atom x!=y */
@PortedFrom(file = "QR.h", name = "QRNeqAtom")
public class QRNeqAtom extends QR2ArgAtom {

    private static final long serialVersionUID = 11000L;

    /**
     * @param A1
     *        A1
     * @param A2
     *        A2
     */
    public QRNeqAtom(QRiObject A1, QRiObject A2) {
        super(A1, A2);
    }
}
