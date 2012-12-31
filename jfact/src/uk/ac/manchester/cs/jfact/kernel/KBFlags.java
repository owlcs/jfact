package uk.ac.manchester.cs.jfact.kernel;

import conformance.Original;
import conformance.PortedFrom;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
@PortedFrom(file = "tKBFlags.h", name = "TKBFlags")
public class KBFlags {
    @Original
    private boolean GCI;
    @Original
    private boolean RnD;
    @Original
    private boolean Reflexive;

    /** flag for GCIs */
    @Original
    public boolean isGCI() {
        return GCI;
    }

    @Original
    public void setGCI(boolean action) {
        GCI = action;
    }

    /** flag for Range and Domain axioms */
    @Original
    public boolean isRnD() {
        return RnD;
    }

    @Original
    public void setRnD() {
        RnD = true;
    }

    /** flag for Reflexive roles */
    @Original
    public boolean isReflexive() {
        return Reflexive;
    }

    @Original
    public void setReflexive(boolean action) {
        Reflexive = action;
    }
}
