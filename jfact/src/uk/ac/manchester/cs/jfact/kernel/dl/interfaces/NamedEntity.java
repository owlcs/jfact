package uk.ac.manchester.cs.jfact.kernel.dl.interfaces;

import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import conformance.PortedFrom;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
@PortedFrom(file="tDLExpression.h",name="TNamedEntity")
public interface NamedEntity {
    /** get access to the name */
@PortedFrom(file="tDLExpression.h",name="getName")
    String getName();

    // NamedEntity getEntity();
@PortedFrom(file="tDLExpression.h",name="getEntry")
    NamedEntry getEntry();

@PortedFrom(file="tDLExpression.h",name="setEntry")
    void setEntry(NamedEntry e);
}
