package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;

import conformance.Original;
import conformance.PortedFrom;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** flags */
@PortedFrom(file = "tKBFlags.h", name = "TKBFlags")
public class KBFlags implements Serializable {
    private static final long serialVersionUID = 11000L;
    @Original
    private boolean GCI;
    @Original
    private boolean RnD;
    @Original
    private boolean Reflexive;

    /** @return GCIs */
    @Original
    public boolean isGCI() {
        return GCI;
    }

    /** @param action
     *            action */
    @Original
    public void setGCI(boolean action) {
        GCI = action;
    }

    /** @return Range and Domain axioms */
    @Original
    public boolean isRnD() {
        return RnD;
    }

    /** @param b
     *            rnd value */
    @Original
    public void setRnD(boolean b) {
        RnD = b;
    }

    /** @return Reflexive roles */
    @Original
    public boolean isReflexive() {
        return Reflexive;
    }

    /** @param action
     *            action */
    @Original
    public void setReflexive(boolean action) {
        Reflexive = action;
    }
}
