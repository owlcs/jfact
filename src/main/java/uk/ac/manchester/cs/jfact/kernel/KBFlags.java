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

    @Original private boolean gci;
    @Original private boolean rnD;
    @Original private boolean reflexive;

    /** @return GCIs */
    @Original
    public boolean isGCI() {
        return gci;
    }

    /**
     * @param action
     *        action
     */
    @Original
    public void setGCI(boolean action) {
        gci = action;
    }

    /** @return Range and Domain axioms */
    @Original
    public boolean isRnD() {
        return rnD;
    }

    /**
     * @param b
     *        rnd value
     */
    @Original
    public void setRnD(boolean b) {
        rnD = b;
    }

    /** @return Reflexive roles */
    @Original
    public boolean isReflexive() {
        return reflexive;
    }

    /**
     * @param action
     *        action
     */
    @Original
    public void setReflexive(boolean action) {
        reflexive = action;
    }
}
