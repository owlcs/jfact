package uk.ac.manchester.cs.jfact.kernel;

import static uk.ac.manchester.cs.jfact.helpers.Assertions.verifyNotNull;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import javax.annotation.Nullable;

import conformance.PortedFrom;

@PortedFrom(file = "tRole.cpp", name = "TRoleCompare")
final class RoleCompare implements Serializable {
    private RoleCompare() {
        // no instances
    }

    @PortedFrom(file = "tRole.cpp", name = "compare")
    public static int compare(@Nullable Role p, @Nullable Role q) {
        return Integer.compare(verifyNotNull(p).getId(), verifyNotNull(q).getId());
    }
}
