package uk.ac.manchester.cs.jfact.kernel.queryobjects;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import conformance.PortedFrom;

/** individual in a query */
@PortedFrom(file = "QR.h", name = "QRIndividual")
class QRIndividual extends QRiObject {
    /** original individual from Expression Manager */
    @PortedFrom(file = "QR.h", name = "Ind")
    IndividualName Ind;

    /** init c'tor */
    QRIndividual(IndividualName ind) {
        Ind = ind;
    }

    public QRIndividual(QRIndividual i) {
        this(i.Ind);
    }

    @Override
    public QRIndividual clone() {
        return new QRIndividual(this);
    }

    /** get the name */
    @PortedFrom(file = "QR.h", name = "getIndividual")
    IndividualName getIndividual() {
        return Ind;
    }
}
